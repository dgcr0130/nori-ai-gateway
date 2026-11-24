package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.core.scope.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import java.util.*;
import static com.nori.ai.gateway.core.llm.IntentTestFixtures.*;
import static org.assertj.core.api.Assertions.*;

class WorkflowPlanBuilderTest {
    final CapabilityRegistry registry=registry();
    final WorkflowPlanBuilder builder=new WorkflowPlanBuilder(registry,INTENTS);
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"svc",new ActorRef("actor"),"actor",Set.of("EQP_READ"),"a".repeat(64),Instant.parse("2026-11-24T10:00:00Z"),Instant.parse("2026-11-24T11:00:00Z"),"job");
    JobContext job(String question,List<Map<String,Object>> needs,Map<String,Object> fields){return new JobContext("job","conversation",scope,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,Map.of("needs",needs),new JobRequestInput("message",1,question,fields));}
    Map<String,Object> fields(){return Map.of("context",Map.of("timezone","Asia/Seoul","referenceTime","2026-11-24T19:27:24+09:00"));}
    WorkflowExecutionPlan plan(String intent,String subject,String question){var job=job(question,List.of(need(intent,subject)),fields());return builder.build(builder.definition(job),registry.view(scope),job);}
    @Test void fiveTcIntentsMapToExactlyTheirDeclaredToolAndNoDocuments(){
        var cases=Map.of("registration","tc.eqp.readRegistration","status","tc.eqp.getOperationalSnapshot","model","tc.model.read","dlq","tc.dlq.list","communication","tc.commLogs.search");
        cases.forEach((intent,id)->{var plan=plan(intent,"CLN_001",intent.equals("communication")?"CLN_001 오늘 통신 로그":"CLN_001 조회");
            assertThat(plan.definition().taskType()).isEqualTo(TaskType.LIVE_QUERY);assertThat(plan.documents().required()).isFalse();
            assertThat(plan.toolPlan().plan()).singleElement().satisfies(step->{assertThat(step.capabilityId()).isEqualTo(id);var args=step.arguments();assertThat(args.path("eqp").asString()).isEqualTo("CLN_001");
                if(intent.equals("communication")){assertThat(args.path("from").asString()).isEqualTo("2026-11-23T15:00:00Z");assertThat(args.path("to").asString()).isEqualTo("2026-11-24T10:27:24Z");}
                else assertThat(args).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001")));});});
    }
    @Test void dlqAt103819HasOnlyEqpAndNeverRequiresOrAddsLogs(){
        var job=job("CLN_001 DLQ 알려줘",List.of(need("dlq","CLN_001")),Map.of("context",Map.of("timezone","Asia/Seoul","referenceTime","2026-11-24T10:38:19+09:00")));
        var plan=builder.build(builder.definition(job),registry.view(scope),job);assertThat(plan.toolPlan().plan()).singleElement().satisfies(step->{assertThat(step.capabilityId()).isEqualTo("tc.dlq.list");assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001")));});
    }
    @Test void incidentTodayEndsAt192724AndHasOnlyVisibleLogAndTraceWithNoDocuments(){
        var job=job("오늘 에러 조사",List.of(need("incident",null)),fields());
        for(String omitted:List.of("none","logs.search","trace.search")){
            var view=new RegistryView(scope.boundSystem(),registry.view(scope).capabilities().stream().filter(t->!t.toolId().equals(omitted)).toList());
            var plan=builder.build(builder.definition(job),view,job);assertThat(plan.definition().taskType()).isEqualTo(TaskType.INCIDENT_ANALYSIS);assertThat(plan.documents().required()).isFalse();
            var expected=new ArrayList<>(List.of("logs.search","trace.search"));expected.remove(omitted);
            assertThat(plan.toolPlan().plan()).extracting(s->s.capabilityId()).containsExactlyElementsOf(expected);
            for(var step:plan.toolPlan().plan())assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("from","2026-11-23T15:00:00Z","to","2026-11-24T10:27:24Z")));
        }
    }
    @Test void documentAndHelpUseTheirExistingPaths(){
        var document=plan("document",null,"설비 문서");assertThat(document.definition().taskType()).isEqualTo(TaskType.KNOWLEDGE_QUERY);assertThat(document.documents().required()).isTrue();assertThat(document.documents().searchQuery()).isEqualTo("설비 문서");assertThat(document.steps()).isEmpty();
        var help=plan("help",null,"도움말");assertThat(help.definition().taskType()).isEqualTo(TaskType.GENERAL_QUERY);assertThat(help.definition().answerSource()).isEqualTo(WorkflowDefinition.AnswerSource.REGISTRY_VIEW);assertThat(help.documents().required()).isFalse();assertThat(help.steps()).isEmpty();
    }
    @Test void requiredNullSubjectHasOnlyEqpClarificationAndSelectedHistorySubjectBecomesRegistrationArgument(){
        assertThatThrownBy(()->plan("registration",null,"등록 정보 알려줘")).isInstanceOfSatisfying(WorkflowPlanBuilder.Invalid.class,e->assertThat(e.fields()).containsExactly("eqp"));
        var plan=plan("registration","PHOTO_001","그 호기의 등록 정보를 알려줘");assertThat(plan.toolPlan().plan()).singleElement().satisfies(s->assertThat(s.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","PHOTO_001"))));
    }
    @Test void comparisonHasTwoStepsAndIdenticalToolArgumentsAreExecutedOnce(){
        for(boolean duplicate:List.of(false,true)){
            var needs=new ArrayList<Map<String,Object>>(List.of(need("status","PHOTO_001"),need("status","CLN_001")));if(duplicate)needs.add(need("status","PHOTO_001"));
            var job=job("PHOTO_001이랑 CLN_001 상태 비교해줘",needs,fields());var plan=builder.build(builder.definition(job),registry.view(scope),job);
            assertThat(plan.toolPlan().plan()).extracting(step->step.capabilityId()).containsExactly("tc.eqp.getOperationalSnapshot","tc.eqp.getOperationalSnapshot");
            assertThat(plan.toolPlan().plan()).extracting(step->step.arguments().path("eqp").asString()).containsExactly("PHOTO_001","CLN_001");
        }
    }
    @Test void noPeriodToolsIgnoreEvenAnUnresolvablePeriodAndNeverReceiveTimeArguments(){
        for(String intent:List.of("registration","status","model")){var plan=plan(intent,"CLN_001","지난주 CLN_001 조회");assertThat(plan.toolPlan().plan().getFirst().arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001")));}
    }
    @Test void falseAllowToolsBlocksOnlyPlansWithTools(){
        for(String intent:List.of("help","status")){var fields=new HashMap<>(fields());fields.put("options",Map.of("allowTools",false));var job=job("CLN_001 조회",List.of(need(intent,intent.equals("status")?"CLN_001":null)),fields);
            if(intent.equals("status"))assertThatThrownBy(()->builder.checkOptions(builder.definition(job),job)).isInstanceOf(WorkflowPlanBuilder.Blocked.class);else assertThatCode(()->builder.checkOptions(builder.definition(job),job)).doesNotThrowAnyException();}
    }
}
