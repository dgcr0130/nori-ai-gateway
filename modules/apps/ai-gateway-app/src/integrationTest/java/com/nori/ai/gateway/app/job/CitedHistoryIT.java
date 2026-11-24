package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.app.inference.OllamaEndpointStub;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.llm.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.AnswerSnapshot;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import com.nori.ai.gateway.persistence.repository.*;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

/** 실제 저장된 접근 가능한 이전 인용과 현재 검색/선택/작성 연결을 격리 PG·로컬 HTTP 대역에서 확인합니다. */
class CitedHistoryIT extends LlmStubSupport {
    private static final String FIRST="로그인 화면 아이디 저장은 이 브라우저에 아이디를 남긴다.";
    private static final String SECOND="저장한 아이디를 지우려면 아이디 저장을 끈다.";
    private static final String BODY=FIRST+"\n"+SECOND;
    private static final String FOLLOW="그 설정에서 기억한 값을 지우려면 어떻게 하나요?";

    @BeforeEach void embeddingBatchMatchesTheActualSearchAndQuestionInputs() {
        // 검색식과 원래 질문이 다르면 실제 검색은 두 벡터를 요구합니다. 기본 단건 대역을 이 fixture에만 확장합니다.
        stub.respond(request->{
            if(!request.path().equals("/v1/embeddings"))return OllamaEndpointStub.healthy(request);
            var input=json.readTree(request.body()).path("input");int count=input.isArray()?input.size():1;
            var response=(tools.jackson.databind.node.ObjectNode)json.readTree(OllamaEndpointStub.embeddingResponse(3));
            var rows=response.withArray("data");var template=rows.get(0).deepCopy();rows.removeAll();
            for(int i=0;i<count;i++){var row=(tools.jackson.databind.node.ObjectNode)template.deepCopy();row.put("index",i);rows.add(row);}
            return new OllamaEndpointStub.Json(200,response.toString());
        });
    }

    private void assertSucceeded(JobAdmissionService.Accepted job)throws Exception {
        String status=terminal(job);
        var audits=jdbc().sql("SELECT event_type,status,error_code,token_usage::text AS tokens FROM gw.audit_event WHERE job_id=:job AND event_type='LLM_CALL' ORDER BY id").param("job",job.jobId()).query().listOfRows();
        var batches=stub.received().stream().filter(request->request.path().equals("/v1/embeddings")).map(request->{var input=json.readTree(request.body()).path("input");return input.isArray()?input.size():1;}).toList();
        assertThat(status).as("합성 fixture result=%s payload=%s audit=%s classifyCalls=%s composeCalls=%s embeddingInputCounts=%s",result(job.jobId()),payload(job.jobId()),audits,scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY),scripts().scriptedCalls(job.jobId(),CallType.COMPOSER),batches).isEqualTo("SUCCEEDED");
    }

    private void seed(String revision,Set<String> permissions) {
        var location=new DocumentLocation("fixture-bucket","rag/context.md",revision,"로그인 화면",3,5);
        var child=new DocumentCorpus.Child(UUID.randomUUID(),0,location,"운영 > 로그인 화면",BODY,List.of(),new float[]{.5f,.5f,.5f});
        var parent=new DocumentCorpus.Parent(UUID.randomUUID(),location,"운영 > 로그인 화면",BODY,AnswerSnapshot.sha(BODY),List.of(child));
        var document=new DocumentCorpus.Document(UUID.randomUUID(),location.bucket(),location.objectKey(),revision,"운영",permissions,List.of(),List.of(parent));
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"bge-m3",3,"c".repeat(64),List.of(document),List.of());
        var store=context.getBean(DocumentIndexRepository.class);var index=context.getBean(MemoryDocumentKeywordIndex.class);var scope=scope("actor");
        store.saveBuilding(scope,bundle);try(var prepared=index.prepare(scope,store.load(scope,bundle.id()))){store.activate(scope,bundle);index.install(scope,prepared);}
    }
    private String wire(boolean reference,String ref) {
        var value=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(intentWire("document",null));
        if(reference)value.put("historyRef",ref);return value.toString();
    }
    private JobAdmissionService.Accepted first(String key,String question,String conversation)throws Exception {
        var job=admitScripted(key,question,Map.of(),List.of(classify(wire(false,null)),composeFacts(FIRST,"e1")),Optional.empty(),conversation);
        assertSucceeded(job);assertThat(payload(job.jobId()).path("answer").path("text").asString()).isEqualTo(FIRST+"[1]");return job;
    }
    private JobAdmissionService.Accepted follow(String key,String conversation,String ref,Optional<TestHookRegistry.PromptWatch> watch) {
        return admitScripted(key,FOLLOW,Map.of(),List.of(classify(wire(true,ref)),composeFacts(SECOND,"e1")),watch,conversation);
    }

    @Test void actualAccessibleCitationSuppliesRetrievalContextAndCurrentSourceOnlyAnswerWithOriginalQuestion()throws Exception {
        seed("a".repeat(64),Set.of("AI_DOC_READ"));var prior=first("history-first","로그인 화면 아이디 저장은 무엇인가요?",null);
        var job=follow("history-follow",prior.conversationId(),"H1",Optional.empty());assertSucceeded(job);
        assertThat(payload(job.jobId()).path("answer").path("text").asString()).isEqualTo(SECOND+"[1]");
        assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isOne();
        var snapshot=jdbc().sql("SELECT question_masked,evidence::text FROM kn.answer_snapshot WHERE job_id=:job").param("job",job.jobId()).query().singleRow();
        assertThat(snapshot.get("question_masked")).isEqualTo(FOLLOW);assertThat(snapshot.get("evidence").toString()).contains(FIRST,SECOND).doesNotContain("@L","priorityIds","historyRef");
        var old=scope("actor");var other=new VerifiedSystemScope(old.boundSystem(),old.authenticatedServiceRef(),new ActorRef("other"),"other",old.perm(),old.pdg(),old.issuedAt(),old.expiresAt(),old.requestId());
        var input=context.getBean(JobRepository.class).findRequestInput(scope("actor"),job.jobId()).orElseThrow();
        assertThat(context.getBean(ClassificationInputs.class).load(new ClassificationService.Request(other,job.conversationId(),"next",new CancellationToken(),new JobRequestInput("next-message",input.jobSeq()+1,FOLLOW,Map.of()))).references()).isEmpty();
    }

    @ParameterizedTest @ValueSource(strings={"revision","permission"})
    void historyAccessIsRecheckedAfterTheBeforeComposerPause(String mutation)throws Exception {
        seed("a".repeat(64),Set.of("AI_DOC_READ"));var prior=first("fresh-first","로그인 화면 아이디 저장은 무엇인가요?",null);
        pauses().hold(PausePointRegistry.Point.BEFORE_COMPOSER,"*");var job=follow("fresh-follow",prior.conversationId(),"H1",Optional.empty());
        assertThat(pauses().awaitReached(PausePointRegistry.Point.BEFORE_COMPOSER,"*",Duration.ofSeconds(15))).isTrue();
        try {seed(mutation.equals("revision")?"b".repeat(64):"a".repeat(64),mutation.equals("permission")?Set.of("AI_ADMIN_DOC_READ"):Set.of("AI_DOC_READ"));}
        finally {pauses().release(PausePointRegistry.Point.BEFORE_COMPOSER,"*");}
        assertSucceeded(job);assertThat(payload(job.jobId()).path("answer").path("kind").asString()).isEqualTo("NO_EVIDENCE");
        assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
    }

    @Test void independentInterjectionDoesNotRequestAHistoryReference()throws Exception {
        seed("a".repeat(64),Set.of("AI_DOC_READ"));var prior=first("independent-first","로그인 화면 아이디 저장은 무엇인가요?",null);
        var independent=(tools.jackson.databind.node.ObjectNode)SchemaContracts.JSON.readTree(wire(false,null));
        var job=admitScripted("independent","그런데 다른 독립 질문: 새 권한 코드는 무엇인가요?",Map.of(),List.of(classify(independent.toString()),composeFacts(FIRST,"e1")),Optional.empty(),prior.conversationId());
        assertSucceeded(job);assertThat(payload(job.jobId()).path("answer").path("kind").asString()).isEqualTo("NO_EVIDENCE");
        assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
    }

    @Test void longStoredHistoryDropsOldestReferenceAndKeepsOnlyTheMatchedPairInComposer()throws Exception {
        // v4의 간결한 분류 입력에서도 두 쌍은 넘치고 최신 한 쌍은 남도록 fixture 길이를 확보합니다.
        seed("a".repeat(64),Set.of("AI_DOC_READ"));String padding="뺡".repeat(1100);
        String firstQuestion="로그인 화면 아이디 저장 FIRSTHISTORYQZ "+padding,secondQuestion="로그인 화면 아이디 저장 SECONDHISTORYQZ "+padding;
        var first=first("long-first",firstQuestion,null);var second=first("long-second",secondQuestion,first.conversationId());
        var previous=context.getBean(JobRepository.class).findRequestInput(scope("actor"),second.jobId()).orElseThrow();
        var probe=new ClassificationService.Request(scope("actor"),second.conversationId(),"next-job",new CancellationToken(),new JobRequestInput("next-message",previous.jobSeq()+1,FOLLOW,Map.of()));
        var loaded=context.getBean(ClassificationInputs.class).load(probe);
        var assembler=ClassificationService.class.getDeclaredMethod("messages",ClassificationService.Request.class,ClassificationService.Context.class,List.class);assembler.setAccessible(true);
        @SuppressWarnings("unchecked")var messages=(List<com.nori.ai.gateway.core.budget.ChatMessage>)assembler.invoke(context.getBean(ClassificationService.class),probe,loaded,loaded.history());
        var counter=context.getBean(com.nori.ai.gateway.core.budget.PromptTokenCounting.class);
        var policy=context.getBean(com.nori.ai.gateway.core.budget.TokenBudgetPolicy.class);var budget=policy.budget(CallType.CLASSIFY);
        assertThat(loaded.history()).hasSize(2);assertThat(loaded.references()).extracting(CitedHistoryContext.Pair::ref).containsExactly("H1","H2");
        int fullTokens=counter.count(messages);
        assertThat(fullTokens).as("두 대화 쌍을 포함한 CLASSIFY 입력").isGreaterThan(budget.inputLimit());
        assertThat(policy.violations(CallType.CLASSIFY,fullTokens,budget.requestMaxTokens())).contains(com.nori.ai.gateway.core.budget.TokenBudgetPolicy.Violation.INPUT_LIMIT);
        @SuppressWarnings("unchecked")var retainedMessages=(List<com.nori.ai.gateway.core.budget.ChatMessage>)assembler.invoke(context.getBean(ClassificationService.class),probe,loaded,List.of(loaded.history().getLast()));
        int retainedTokens=counter.count(retainedMessages);
        assertThat(retainedTokens).as("최신 대화 한 쌍을 포함한 CLASSIFY 입력").isLessThanOrEqualTo(budget.inputLimit());
        assertThat(policy.violations(CallType.CLASSIFY,retainedTokens,budget.requestMaxTokens())).isEmpty();
        var watch=Optional.of(new TestHookRegistry.PromptWatch(List.of("FIRSTHISTORYQZ","SECONDHISTORYQZ")));
        var job=follow("long-follow",second.conversationId(),"H2",watch);assertSucceeded(job);
        assertThat(payload(job.jobId()).path("answer").path("text").asString()).isEqualTo(SECOND+"[1]");
        assertThat(scripts().promptWatch(job.jobId()).orElseThrow()).contains(new LlmScriptRegistry.WatchRow(CallType.CLASSIFY,0,0),new LlmScriptRegistry.WatchRow(CallType.CLASSIFY,1,1),new LlmScriptRegistry.WatchRow(CallType.COMPOSER,0,0),new LlmScriptRegistry.WatchRow(CallType.COMPOSER,1,1));
    }
}
