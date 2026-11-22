package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.inference.OllamaEndpointStub;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.AnswerSnapshot;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import com.nori.ai.gateway.persistence.repository.DocumentIndexRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 문서 충분성·실제 작성·원문 대조 뒤에만 영문 리터럴의 최소 형식을 확인합니다. */
class EnglishLiteralAnswerIT extends LlmStubSupport {
    private static final String QUESTION="저장 검증 성공 시 나오는 정확한 영어 출력 문구는 무엇인가요?";
    @BeforeEach void matchingEmbeddingBatch() {
        stub.respond(request->{
            if(!request.path().equals("/v1/embeddings"))return OllamaEndpointStub.healthy(request);
            var input=json.readTree(request.body()).path("input");int count=input.isArray()?input.size():1;
            var response=(tools.jackson.databind.node.ObjectNode)json.readTree(OllamaEndpointStub.embeddingResponse(3));
            var array=response.withArray("data");var template=array.get(0).deepCopy();array.removeAll();
            for(int i=0;i<count;i++){var row=(tools.jackson.databind.node.ObjectNode)template.deepCopy();row.put("index",i);array.add(row);}
            return new OllamaEndpointStub.Json(200,response.toString());
        });
    }
    private void seed(String body) {
        var location=new DocumentLocation("fixture-bucket","rag/literal.md","a".repeat(64),"저장 검증",3,5);
        var child=new DocumentCorpus.Child(UUID.randomUUID(),0,location,"운영 > 저장 검증",body,List.of(),new float[]{.5f,.5f,.5f});
        var parent=new DocumentCorpus.Parent(UUID.randomUUID(),location,"운영 > 저장 검증",body,AnswerSnapshot.sha(body),List.of(child));
        var document=new DocumentCorpus.Document(UUID.randomUUID(),location.bucket(),location.objectKey(),location.revision(),"운영",Set.of("AI_DOC_READ"),List.of(),List.of(parent));
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"bge-m3",3,"c".repeat(64),List.of(document),List.of());
        var store=context.getBean(DocumentIndexRepository.class);var index=context.getBean(MemoryDocumentKeywordIndex.class);var scope=scope("actor");store.saveBuilding(scope,bundle);
        try(var prepared=index.prepare(scope,store.load(scope,bundle.id()))){store.activate(scope,bundle);index.install(scope,prepared);}
    }
    private JobAdmissionService.Accepted run(String key,String... ids) throws Exception {
        var value=SchemaContracts.JSON.createObjectNode().put("taskType","KNOWLEDGE_QUERY").put("searchQuery","저장 검증 성공").put("retryQuery","저장 성공 안내");
        value.putArray("toolCalls");value.putArray("targets");value.putNull("timeWindow");
        var job=admitScripted(key,QUESTION,Map.of(),List.of(classify(value.toString()),composeSelection(ids)),Optional.empty(),null);
        assertThat(terminal(job)).as("합성 fixture result=%s payload=%s",result(job.jobId()),payload(job.jobId())).isEqualTo("SUCCEEDED");
        assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isOne();
        assertThat(stub.received().stream().filter(request->request.path().equals("/v1/embeddings")).map(request->json.readTree(request.body()).path("input").size()).toList()).containsExactly(2);
        return job;
    }
    @Test void sourceValidatedUnrelatedKoreanSaveSuccessCannotSatisfyExplicitEnglishOutput() throws Exception {
        seed("저장 검증 성공 안내가 나타난다.");var job=run("literal-missing","C1");
        var answer=payload(job.jobId()).path("answer");assertThat(result(job.jobId())).containsEntry("status","EMPTY").containsEntry("error_code",null);
        assertThat(answer.path("kind").asString()).isEqualTo("NO_EVIDENCE");assertThat(answer.path("text").asString()).isEqualTo("근거 없음");assertThat(answer.path("sentences")).isEmpty();assertThat(answer.path("citations")).isEmpty();
    }
    @Test void actualEnglishLiteralAndSupportingKoreanContextRemainSourceValidatedAnswer() throws Exception {
        String contextText="저장 검증 성공 안내를 확인한다.",literal="영어 출력 문구는 \"SAVED OK\"이다.";
        seed(contextText+"\n"+literal);var job=run("literal-present","C1","C2");var answer=payload(job.jobId()).path("answer");
        assertThat(answer.path("kind").asString()).isEqualTo("ANSWER");assertThat(answer.path("text").asString()).isEqualTo(contextText+"[1]\n"+literal+"[1]");assertThat(answer.path("sentences")).hasSize(2);assertThat(answer.path("citations")).hasSize(1);
    }
}
