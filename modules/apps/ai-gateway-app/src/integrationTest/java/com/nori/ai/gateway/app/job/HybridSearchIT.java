package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.integration.inference.*;
import com.nori.ai.gateway.app.inference.OllamaEndpointStub;
import com.nori.ai.gateway.persistence.repository.DocumentIndexRepository;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

/** 실제 앱·PG·메모리 색인·HTTP 임베딩 연결입니다. Docker 실행은 외부 세션이 수행합니다. */
class HybridSearchIT extends LlmStubSupport {
    private void seed() {
        var location=new DocumentLocation("fixture-bucket","rag/fixed.md","a".repeat(64),"설정",3,5);
        var child=new DocumentCorpus.Child(UUID.randomUUID(),0,location,"운영 > 설정","설비 상태 T3",List.of("t3"),new float[]{.5f,.5f,.5f});
        var parent=new DocumentCorpus.Parent(UUID.randomUUID(),location,"운영 > 설정","설비 상태 T3","b".repeat(64),List.of(child));
        var doc=new DocumentCorpus.Document(UUID.randomUUID(),"fixture-bucket","rag/fixed.md","a".repeat(64),"운영",Set.of("AI_DOC_READ"),List.of(),List.of(parent));
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"bge-m3",3,"c".repeat(64),List.of(doc),List.of());
        var store=context.getBean(DocumentIndexRepository.class);var index=context.getBean(MemoryDocumentKeywordIndex.class);var scope=scope("actor");store.saveBuilding(scope,bundle);
        try(var prepared=index.prepare(scope,store.load(scope,bundle.id()))){store.activate(scope,bundle);index.install(scope,prepared);}
    }
    private String classification(String task) {
        return "{\"taskType\":\""+task+"\",\"toolCalls\":[],\"targets\":[],\"timeWindow\":null,\"searchQuery\":\"설비 상태 T3\",\"retryQuery\":\"설비 T3 상태 설정\"}";
    }
    @Test void activeCorpusFlowsThroughTheActualJobAndProtectsStoredResultWithDocumentDependencies()throws Exception {
        seed();var job=admitScripted("hybrid-document","설비 상태 T3",Map.of(),List.of(classify(classification("KNOWLEDGE_QUERY")),composeSelection("C1")),Optional.empty(),null);
        assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(result(job.jobId())).containsEntry("status","SUCCESS");assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isOne();
        assertThat(stub.count("/v1/embeddings")).isEqualTo(1);assertThat(payload(job.jobId()).path("toolSummary")).hasSize(1);
        var deps=json.readTree(jdbc().sql("SELECT result_source_deps::text FROM gw.result WHERE job_id=:id").param("id",job.jobId()).query(String.class).single());assertThat(deps.path("knowledgeDocuments")).hasSize(1);assertThat(deps.path("knowledgeDocuments").get(0).path("revision").asString()).isEqualTo("a".repeat(64));
    }
    @Test void normalEmptyCorpusSearchesTwiceAndNeverCallsComposer()throws Exception {
        var job=admitScripted("hybrid-empty","설비 상태 T3",Map.of(),List.of(classify(classification("KNOWLEDGE_QUERY")),compose("사용되면 안 되는 답변")),Optional.empty(),null);
        assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(result(job.jobId())).containsEntry("status","EMPTY");assertThat(payload(job.jobId()).path("answer").path("kind").asString()).isEqualTo("NO_EVIDENCE");assertThat(payload(job.jobId()).path("answer").path("text").asString()).isEqualTo("근거 없음");assertThat(payload(job.jobId()).path("toolSummary")).hasSize(2);assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
    }
    @Test void questionEmbeddingHttpFailureKeepsSourceUnavailableAndIsNotRetriedAsZeroHits()throws Exception {
        seed();stub.respond(request->request.path().equals("/v1/embeddings")?new OllamaEndpointStub.Json(503,"{\"error\":{\"message\":\"private-source-error\"}}"):OllamaEndpointStub.healthy(request));
        var job=admitScripted("hybrid-error","설비 상태 T3",Map.of(),List.of(classify(classification("KNOWLEDGE_QUERY")),compose("사용 금지")),Optional.empty(),null);
        assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("status","SOURCE_UNAVAILABLE").containsEntry("error_code",null);assertThat(stub.count("/v1/embeddings")).isEqualTo(1);assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();assertThat(payload(job.jobId()).toString()).doesNotContain("private-source-error");assertThat(payload(job.jobId()).path("toolSummary").get(0).path("status").asString()).isEqualTo("SOURCE_UNAVAILABLE");
    }
    @Test void liveWithNoToolUnitsDoesNotReadAnAvailableDocumentCorpus()throws Exception {
        seed();var job=admitScripted("hybrid-live","설비 상태 T3",Map.of(),List.of(classify(classification("LIVE_QUERY")),compose("사용 금지")),Optional.empty(),null);
        assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(result(job.jobId())).containsEntry("status","EMPTY");assertThat(stub.count("/v1/embeddings")).isZero();assertThat(payload(job.jobId()).path("toolSummary")).isEmpty();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
    }
}
