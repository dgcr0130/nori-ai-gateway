package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ErrorCode;
import com.nori.ai.gateway.core.workflow.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.Test;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static org.assertj.core.api.Assertions.*;

class CodeTermClassificationTest {
    static ClassificationIntent codeIntent() {
        return SchemaContracts.JSON.readValue("""
            {"id":"code","description":"코드 식별자의 정의와 사용 위치 조회","toolIds":["git.searchCode"],
             "subjectRequired":true,"subjectKind":"CODE_TERM","periodRequirement":"NONE",
             "taskType":"LIVE_QUERY","documentSearch":false}
            """,ClassificationIntent.class);
    }
    static CapabilityManifest git() {
        return ConfiguredTool.create(SCOPE.boundSystem(),"git.searchCode","코드 조회",ToolKind.GIT,
            SchemaContracts.parse("{\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"q\":{\"type\":\"string\"}}}"),
            SchemaContracts.parse("{\"type\":\"object\"}"),List.of("AI_CODE_READ"),10000);
    }
    static List<ClassificationIntent> intents() {var values=new ArrayList<>(IntentTestFixtures.INTENTS);values.add(codeIntent());return values;}
    static RegistryView view(boolean codeVisible) {
        var values=new ArrayList<>(ClassificationServiceTest.view().capabilities());if(codeVisible)values.add(git());return new RegistryView(SCOPE.boundSystem(),values);
    }
    static final class Capture implements InferenceClient {
        List<ChatMessage> messages;StructuredOutput output;final String reply;
        Capture(String intent,String subject){reply=IntentTestFixtures.wire(intent,subject);}
        @Override public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){throw new AssertionError("출력 스키마가 필요합니다");}
        @Override public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max,CancellationToken cancellation,StructuredOutput output){this.messages=List.copyOf(messages);this.output=output;return LlmTestSupport.reply(reply);}
        @Override public List<float[]> embed(List<String> inputs){throw new AssertionError("문서 검색은 하지 않습니다");}
    }
    ClassificationService.Result classify(Capture client,String question,boolean visible,List<ClassificationIntent> declarations,List<ClassificationService.HistoryPair> history) {
        var context=new ClassificationService.Context(view(visible),history,Map.of(),2,List.of(),declarations);
        var service=new ClassificationService(client,TokenBudgetPolicy.current(),text->1,messages->100,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"분류 지시",request->context);
        return service.classify(ClassificationServiceTest.request(question));
    }
    @Test void candidatesAreEqpFirstThenOnlyCurrentQuestionCodeTermsWithStableDeduplication() {
        String question="EquipmentContextRegistry EQP_PORT_STATUS_CHG nori.ai.integration.enabled readRegistration PHOTO_001 DLQ TC 한국어 AB git.searchCode GIT.SEARCHCODE --readRegistration.. aB A_1 123abc";
        var client=new Capture("code","EquipmentContextRegistry");
        var result=classify(client,question,true,intents(),List.of(new ClassificationService.HistoryPair("OldHistoryClass OLD_001","답")));
        assertThat(result).isInstanceOf(ClassificationService.Classified.class);
        var expected=List.of("PHOTO_001","A_1","OLD_001","EquipmentContextRegistry","EQP_PORT_STATUS_CHG","nori.ai.integration.enabled","readRegistration");
        assertThat(SchemaContracts.parse(client.messages.getLast().content()).path("subjectCandidates")).isEqualTo(SchemaContracts.JSON.valueToTree(expected));
        var subjects=new ArrayList<Object>(expected);subjects.add(null);
        assertThat(SchemaContracts.parse(client.output.schema()).path("properties").path("needs").path("items").path("properties").path("subject").path("enum"))
            .isEqualTo(SchemaContracts.JSON.valueToTree(subjects));
    }
    @Test void codeTokenLengthTrimmingAndShapeBoundaries() {
        for(String term:List.of("EquipmentContextRegistry","EQP_PORT_STATUS_CHG","nori.ai.integration.enabled","readRegistration","PHOTO_001","A_1","a".repeat(62)+"B1")) {
            var client=new Capture("code",term);assertThat(classify(client,"..--"+term+"--..",true,intents(),List.of())).as(term).isInstanceOf(ClassificationService.Classified.class);
        }
        for(String term:List.of("DLQ","TC","한국어","AB","aB","git.searchCode","GIT.SEARCHCODE","123abc","a".repeat(63)+"B1")) {
            var client=new Capture("code",term);assertThat(classify(client,term,true,intents(),List.of())).as(term).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));
        }
    }
    @Test void hiddenCodeIntentLeavesMessagesAndSchemaByteIdenticalToFrozenPreStepSevenInput() throws Exception {
        String question="PHOTO_001 EquipmentContextRegistry EQP_PORT_STATUS_CHG nori.ai.integration.enabled git.searchCode";
        // 고정 자료는 현재 메시지 생성 순서와 canonical 출력 스키마에서 작성했습니다.
        var legacy=new Capture("registration","PHOTO_001");classify(legacy,question,false,IntentTestFixtures.INTENTS,List.of());
        var hidden=new Capture("registration","PHOTO_001");classify(hidden,question,false,intents(),List.of());
        for(var client:List.of(legacy,hidden)) {
            assertThat(client.messages.getFirst().content()).isEqualTo("분류 지시");
            assertThat(client.messages.getLast().content().getBytes(StandardCharsets.UTF_8)).isEqualTo(resource("hidden-code-user.json"));
            assertThat(client.output.schema().getBytes(StandardCharsets.UTF_8)).isEqualTo(resource("hidden-code-schema.json"));
        }
        assertThat(hidden.messages).isEqualTo(legacy.messages);assertThat(hidden.output.schema()).isEqualTo(legacy.output.schema());
    }
    byte[] resource(String name)throws Exception {try(var in=getClass().getResourceAsStream("/fixtures/code-query/"+name)){return Objects.requireNonNull(in).readAllBytes();}}
    @Test void codeOnlySubjectIsAcceptedWithoutEqpTargetsButRegistrationRejectsIt() {
        var client=new Capture("code","EquipmentContextRegistry");
        assertThat(classify(client,"EquipmentContextRegistry 클래스는 어디서 쓰여?",true,intents(),List.of()))
            .isInstanceOfSatisfying(ClassificationService.Classified.class,r->{assertThat(r.classification().targets()).isEmpty();assertThat(r.classification().needs()).containsExactly(new Classification.Need("code","EquipmentContextRegistry"));});
        var rejected=new Capture("registration","EquipmentContextRegistry");
        var diagnostics=new ArrayList<LlmDiagnostic>();
        var observer=new LlmCallObserver(){@Override public Scope open(String job,CallType type,List<ChatMessage> messages){return ()->{};}@Override public void diagnostic(String job,CallType type,LlmDiagnostic reason){diagnostics.add(reason);}};
        var context=new ClassificationService.Context(view(true),List.of(),Map.of(),0,List.of(),intents());
        var service=new ClassificationService(rejected,TokenBudgetPolicy.current(),text->1,messages->100,new ExpiryCheckpoint(),observer,attempt->{},"분류 지시",r->context);
        assertThat(service.classify(ClassificationServiceTest.request("EquipmentContextRegistry 등록 정보"))).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_TARGET);
    }
    @Test void missingCodeSubjectRequestsQAndEqpIntentStillRequestsEqp() {
        assertThat(classify(new Capture("code",null),"클래스 조회",true,intents(),List.of())).isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("q")));
        assertThat(classify(new Capture("registration",null),"등록 조회",true,intents(),List.of())).isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("eqp")));
    }
    @Test void codePlanBindsOnlyQWithoutResourcesOrTargetsAndRegistrationKeepsEqp() {
        var manifests=new ArrayList<>(ClassificationServiceTest.view().capabilities());manifests.add(git());
        var registry=new CapabilityRegistry(manifests,Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        var builder=new WorkflowPlanBuilder(registry,intents());
        for(String id:List.of("code","registration")) {
            String subject=id.equals("code")?"EquipmentContextRegistry":"PHOTO_001";
            var job=new JobContext("job-1","conversation",SCOPE,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,Map.of("needs",List.of(IntentTestFixtures.need(id,subject))),ClassificationServiceTest.request(subject).input());
            var plan=builder.build(builder.definition(job),view(true),job);
            assertThat(plan.toolPlan().plan()).singleElement().satisfies(step->{
                assertThat(step.capabilityId()).isEqualTo(id.equals("code")?"git.searchCode":"tc.eqp.readRegistration");
                assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of(id.equals("code")?"q":"eqp",subject)));
                if(id.equals("code"))assertThat(step.resources()).isEmpty();else assertThat(step.resources()).hasSize(1);
            });
            if(id.equals("code"))assertThat(plan.toolPlan().targetResources()).isEmpty();else assertThat(plan.toolPlan().targetResources()).hasSize(1);
            assertThat(plan.documents().required()).isFalse();
        }
        var missing=new JobContext("job-1","conversation",SCOPE,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,Map.of("needs",List.of(IntentTestFixtures.need("code",null))),ClassificationServiceTest.request("코드 조회").input());
        assertThatThrownBy(()->builder.build(builder.definition(missing),view(true),missing)).isInstanceOfSatisfying(WorkflowPlanBuilder.Invalid.class,e->assertThat(e.fields()).containsExactly("q"));
    }
}
