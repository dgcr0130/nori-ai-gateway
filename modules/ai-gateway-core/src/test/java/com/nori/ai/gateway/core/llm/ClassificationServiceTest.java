package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ErrorCode;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.node.ObjectNode;
import java.util.*;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static com.nori.ai.gateway.core.llm.IntentTestFixtures.*;
import static org.assertj.core.api.Assertions.*;

class ClassificationServiceTest {
    static RegistryView view(){return registry().view(SCOPE);}
    static ObjectNode wire(){return (ObjectNode)SchemaContracts.parse(IntentTestFixtures.wire("document",null));}
    static ClassificationService.Request request(String question){return request(question,Map.of("context",Map.of("timezone","UTC","referenceTime",NOW.toString())));}
    static ClassificationService.Request request(String question,Map<String,Object> fields){return new ClassificationService.Request(SCOPE,"conversation","job-1",new CancellationToken(),new JobRequestInput("message",1,question,fields));}
    static ClassificationService service(ScriptedClient client){return service(client,context(view(),List.of(),2),m->100,a->{});}
    static ClassificationService service(ScriptedClient client,ClassificationService.Context context,PromptTokenCounting counter,LlmCallAudit audit){return new ClassificationService(client,TokenBudgetPolicy.current(),CODEPOINTS,counter,new ExpiryCheckpoint(),LlmCallObserver.NONE,audit,"분류 지시",r->context);}

    @Test void needsAreDecodedOnceAndTaskTypeIsDerivedForEveryDeclaredIntent(){
        for(var intent:INTENTS){
            var client=new ScriptedClient(reply(IntentTestFixtures.wire(intent.id(),intent.subjectRequired()?"CLN_001":null)));
            var result=service(client).classify(request("CLN_001 오늘 조회",Map.of("context",Map.of("timezone","Asia/Seoul","referenceTime","2026-11-24T19:27:24+09:00"))));
            assertThat(result).as(intent.id()).isInstanceOf(ClassificationService.Classified.class);
            var classified=((ClassificationService.Classified)result).classification();
            assertThat(classified.taskType()).isEqualTo(intent.taskType());assertThat(classified.jobClassification()).containsEntry("taskType",intent.taskType().name());
            assertThat(client.sent).singleElement().satisfies(sent->assertThat(sent.maxTokens()).isEqualTo(2048));
        }
    }
    @Test void configuredIntentIdAndDescriptionAreUsedWithoutAHardcodedTcIntentList(){
        var local=intent("owner-local","소유 시스템의 등록 설명",List.of("tc.eqp.readRegistration"),true,"NONE",TaskType.LIVE_QUERY,false);
        var context=new ClassificationService.Context(view(),List.of(),Map.of(),0,List.of(),List.of(local));
        var client=new ScriptedClient(reply(IntentTestFixtures.wire("owner-local","PHOTO_001")));
        assertThat(service(client,context,m->100,a->{}).classify(request("PHOTO_001 등록 정보"))).isInstanceOfSatisfying(ClassificationService.Classified.class,r->assertThat(r.classification().taskType()).isEqualTo(TaskType.LIVE_QUERY));
        assertThat(client.sent.getFirst().messages().getLast().content()).contains("owner-local","소유 시스템의 등록 설명");
    }
    @Test void malformedUnknownIntentOutsideSubjectAndZeroOrFiveNeedsFailWithoutRetry(){
        var invalid=new ArrayList<String>(List.of("not json",wire()+" {}","{}","{\"needs\":[]}",IntentTestFixtures.wire("not-declared",null),IntentTestFixtures.wire("status","INVENTED_001")));
        for(String value:List.of("{\"subject\":null}","{\"intent\":\"document\"}","{\"intent\":7,\"subject\":null}","{\"intent\":\"document\",\"subject\":{}}"))invalid.add("{\"needs\":["+value+"]}");
        invalid.add(SchemaContracts.JSON.writeValueAsString(Map.of("needs",Collections.nCopies(5,need("document",null)))));
        for(String key:List.of("systemId","taskType","toolCalls","targets","timeWindow","searchQuery","retryQuery")){var copy=wire();copy.put(key,"forbidden");invalid.add(copy.toString());}
        for(String text:invalid){var client=new ScriptedClient(reply(text));assertThat(service(client).classify(request("질문"))).as(text).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));assertThat(client.sent).hasSize(1);}
    }
    @Test void candidatesIncludeQuestionRetainedHistoryAndContextRefsButNeverToolIds(){
        var cases=List.of(request("tc.model.read 도구를 사용하여 eqp=CLN_001에 연결된 모델"),request("그 호기의 등록 정보를 알려줘"),
            request("선택한 호기",Map.of("context",Map.of("timezone","UTC","referenceTime",NOW.toString(),"resourceRefs",List.of(Map.of("type","EQP","ref","PHOTO_001"))))));
        for(int i=0;i<cases.size();i++){
            var history=i==1?List.of(new ClassificationService.HistoryPair("PHOTO_001 호기의 등록 정보를 알고 싶어.","등록 정보")):List.<ClassificationService.HistoryPair>of();
            String candidate=i==0?"CLN_001":"PHOTO_001";var client=new ScriptedClient(reply(IntentTestFixtures.wire("registration",candidate)));
            assertThat(service(client,context(view(),history,2),m->100,a->{}).classify(cases.get(i))).isInstanceOf(ClassificationService.Classified.class);
            var input=SchemaContracts.parse(client.sent.getFirst().messages().getLast().content());
            assertThat(input.path("subjectCandidates")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of(candidate)));
        }
    }
    @Test void retainedPhotoSubjectFlowsFromLlmSelectionIntoServerRegistrationPlan(){
        var registry=registry();var context=context(registry.view(SCOPE),List.of(new ClassificationService.HistoryPair("PHOTO_001 호기의 등록 정보를 알고 싶어.","등록 정보")),2);
        var request=request("그 호기의 등록 정보를 알려줘");var client=new ScriptedClient(reply(IntentTestFixtures.wire("registration","PHOTO_001")));
        var result=service(client,context,m->100,a->{}).classify(request);assertThat(result).isInstanceOf(ClassificationService.Classified.class);
        var job=new JobContext("job-1","conversation",SCOPE,"d".repeat(22),request.cancellation(),InferenceMode.ENABLED,((ClassificationService.Classified)result).classification().jobClassification(),request.input());
        var builder=new com.nori.ai.gateway.core.workflow.WorkflowPlanBuilder(registry,INTENTS);var plan=builder.build(builder.definition(job),registry.view(SCOPE),job);
        assertThat(plan.toolPlan().plan()).singleElement().satisfies(step->{assertThat(step.capabilityId()).isEqualTo("tc.eqp.readRegistration");assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","PHOTO_001")));});assertThat(client.sent).hasSize(1);
    }
    @Test void exactLiteralSubjectsUseOnlyHistoryActuallySent(){
        var history=List.of(new ClassificationService.HistoryPair("OLD_001 질문","답"),new ClassificationService.HistoryPair("NEW_001 질문","답"));var client=new ScriptedClient(reply(IntentTestFixtures.wire("status","OLD_001")));
        assertThat(service(client,context(view(),history,1),m->100,a->{}).classify(request("질문"))).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));
        assertThat(client.sent.getFirst().messages().getLast().content()).doesNotContain("OLD_001").contains("NEW_001");
    }
    @Test void searchQueriesAreBuiltByServerAndAppendSelectedSubjectOnlyWhenAbsent(){
        var history=List.of(new ClassificationService.HistoryPair("PHOTO_001 등록 정보","답"));
        for(String question:List.of("그 호기의 DLQ 문서","PHOTO_001 DLQ 문서")){
            var client=new ScriptedClient(reply(IntentTestFixtures.wire("document","PHOTO_001")));
            var result=(ClassificationService.Classified)service(client,context(view(),history,2),m->100,a->{}).classify(request(question));
            String expected=question.contains("PHOTO_001")?question:question+" PHOTO_001";
            assertThat(result.classification().searchQuery()).isEqualTo(expected);assertThat(result.classification().retryQuery()).isEqualTo(expected+" Dead Letter Queue");
        }
    }
    @Test void tokenOverflowDropsOldestPairsThenStopsBeforeSendingIfBaseIsTooLarge(){
        var context=context(view(),List.of(new ClassificationService.HistoryPair("OLD_001","답"),new ClassificationService.HistoryPair("NEW_001","답")),2);var client=new ScriptedClient(reply(wire().toString()));
        assertThat(service(client,context,m->m.toString().contains("OLD_001")?6000:100,a->{}).classify(request("질문"))).isInstanceOf(ClassificationService.Classified.class);
        assertThat(client.sent.getFirst().messages().toString()).contains("NEW_001").doesNotContain("OLD_001");var absent=new ScriptedClient();
        assertThat(service(absent,context,m->6000,a->{}).classify(request("질문"))).isEqualTo(new ClassificationService.BudgetExceeded(0));assertThat(absent.sent).isEmpty();
    }
    @Test void transportBudgetAndMalformedRepliesLeaveExactlyOneTextFreeAudit(){for(var reply:List.of(reply("secret malformed"),new InferenceClient.CallFailed(InferenceClient.FailureReason.TRANSPORT,"java.io.IOException",java.time.Duration.ofMillis(7),120),InferenceClient.ChatOutcome.budgetExceeded(6000))){var records=new ArrayList<LlmCallAudit.Attempt>();var client=new ScriptedClient(reply);service(client,context(view(),List.of(),2),m->100,records::add).classify(request("질문"));assertThat(records).hasSize(1);assertThat(records.toString()).doesNotContain("secret malformed","질문");}}
    @Test void cancellationBeforeAssemblyMakesNoCall(){var client=new ScriptedClient();var request=request("질문");request.cancellation().signal(CancellationToken.Reason.CANCELLED);assertThatThrownBy(()->service(client).classify(request)).isInstanceOf(IllegalStateException.class);assertThat(client.sent).isEmpty();}
}
