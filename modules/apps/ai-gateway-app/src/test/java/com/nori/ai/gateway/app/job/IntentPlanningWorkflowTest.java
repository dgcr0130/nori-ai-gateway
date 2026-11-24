package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.observability.AuditRecorder;
import com.nori.ai.gateway.core.authz.SourcePermissionMap;
import com.nori.ai.gateway.core.evidence.EvidenceAssembler;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.llm.ClassificationIntent;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.persistence.repository.MessageRepository;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 기간 계산·확인 질문의 앱 연결을 DB와 도구 호출 없이 검증합니다. */
class IntentPlanningWorkflowTest {
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"svc",new ActorRef("actor"),"actor",Set.of("READ"),"a".repeat(64),Instant.parse("2026-11-24T10:00:00Z"),Instant.parse("2026-11-24T11:00:00Z"),"job");
    final MessageRepository messages=mock(MessageRepository.class);
    final com.nori.ai.gateway.core.workflow.WorkflowExecutor executor=mock(com.nori.ai.gateway.core.workflow.WorkflowExecutor.class);
    final List<ClassificationIntent> declarations=List.of(
        declaration("registration",List.of("tc.eqp.readRegistration"),true,"NONE",TaskType.LIVE_QUERY),
        declaration("status",List.of("tc.eqp.getOperationalSnapshot"),true,"NONE",TaskType.LIVE_QUERY),
        declaration("dlq",List.of("tc.dlq.list"),true,"OPTIONAL",TaskType.LIVE_QUERY),
        declaration("communication",List.of("tc.commLogs.search"),true,"REQUIRED",TaskType.LIVE_QUERY),
        declaration("incident",List.of("logs.search","trace.search"),false,"REQUIRED",TaskType.INCIDENT_ANALYSIS),
        declaration("unsupported",List.of(),false,"NONE",TaskType.UNSUPPORTED_REQUEST));
    ClassificationIntent declaration(String id,List<String> tools,boolean subject,String period,TaskType task){return new ClassificationIntent(id,id,tools,subject,ClassificationIntent.PeriodRequirement.valueOf(period),task,false,false);}
    CapabilityRegistry registry(){
        var manifests=new ArrayList<CapabilityManifest>();
        for(var intent:declarations)for(String id:intent.toolIds()){
            var props=new LinkedHashMap<String,Object>();var required=new ArrayList<String>();
            if(intent.subjectRequired()){props.put("eqp",Map.of("type","string","minLength",1,"maxLength",64));required.add("eqp");}
            if(intent.periodRequirement()!=ClassificationIntent.PeriodRequirement.NONE){for(String key:List.of("from","to"))props.put(key,Map.of("type","string","format","date-time"));if(intent.periodRequirement()==ClassificationIntent.PeriodRequirement.REQUIRED)required.addAll(List.of("from","to"));}
            manifests.add(ConfiguredTool.create(OwnerSystem.TC,id,id,id.equals("logs.search")?ToolKind.LOG:id.equals("trace.search")?ToolKind.TRACE:ToolKind.BUSINESS,SchemaContracts.JSON.valueToTree(Map.of("type","object","additionalProperties",false,"properties",props,"required",required)),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("READ"),10000));
        }
        return new CapabilityRegistry(manifests,Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
    }
    Map<String,Object> context(){return Map.of("timezone","Asia/Seoul","referenceTime","2026-11-24T19:27:24+09:00");}
    TerminalSpec run(String intent,String subject,String question,Map<String,Object> context)throws Exception {
        var need=new LinkedHashMap<String,Object>();need.put("intent",intent);need.put("subject",subject);
        var task=declarations.stream().filter(i->i.id().equals(intent)).findFirst().orElseThrow().taskType();
        var job=new JobContext("job","conversation",scope,"d".repeat(22),new CancellationToken(),InferenceMode.DISABLED,Map.of("needs",List.of(need),"taskType",task.name()),new JobRequestInput("message",1,question,Map.of("context",context)));
        var registry=registry();var expiry=new ExpiryCheckpoint();when(messages.updateResolvedTimeWindow(any(),anyString(),anyString(),anyMap())).thenReturn(true);
        when(executor.run(any(),any(),any(),any())).thenReturn(new WorkflowOutcome(List.of(),List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty()));
        var adapter=new WorkflowExecutionAdapter(registry,new WorkflowPlanBuilder(registry,declarations),executor,new WorkflowResourceResolverFactory(new SourcePermissionMap(Map.of()),registry,expiry),new TimeWindowResolver(),messages,expiry,new ContentSanitizer(),new TerminalOutcomeAssembler(),new ConversationSummaryBuilder(),new EvidenceAssembler(),mock(AuditRecorder.class),Optional.empty(),new LogSummarizer(),Optional.empty(),Optional.empty());
        return adapter.execute(job);
    }
    WorkflowExecutionPlan capturedPlan(){var plan=ArgumentCaptor.forClass(WorkflowExecutionPlan.class);verify(executor).run(any(),plan.capture(),any(),any());return plan.getValue();}
    void clarification(TerminalSpec result,String code,String field){assertThat(result.jobStatus()).isEqualTo(JobStatus.FAILED);var value=SchemaContracts.JSON.valueToTree(result.resultPayload());assertThat(value.path("clarification").path("reason").asString()).isEqualTo(code);assertThat(value.path("clarification").path("fields")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of(field)));verifyNoInteractions(executor);}
    @Test void unsupportedIntentReturnsExistingUnsupportedResultWithoutBuildingOrExecutingAPlan()throws Exception {
        var result=run("unsupported",null,"지원 범위 밖 요청",context());assertThat(result.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);assertThat(result.resultStatus()).isEqualTo(ResultStatus.UNSUPPORTED);assertThat(result.errorCode()).isNull();verifyNoInteractions(executor,messages);
    }
    @Test void dlqAt103819BuildsOnlyTheEqpReadWithoutRequiringOrInventingATimeWindow()throws Exception {
        run("dlq","CLN_001","CLN_001 DLQ 알려줘",Map.of("timezone","Asia/Seoul","referenceTime","2026-11-24T10:38:19+09:00"));
        var plan=capturedPlan();assertThat(plan.toolPlan().plan()).singleElement().satisfies(step->{assertThat(step.capabilityId()).isEqualTo("tc.dlq.list");assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001")));});assertThat(plan.documents().required()).isFalse();
    }
    @Test void incidentTodayStopsAt192724RatherThanFutureEndOfDayAndOverridesContext()throws Exception {
        var context=new HashMap<>(context());context.put("timeWindow",Map.of("from","2026-11-22T00:00:00Z","to","2026-11-22T01:00:00Z"));
        run("incident",null,"오늘 에러 원인 조사",context);var plan=capturedPlan();
        assertThat(plan.toolPlan().plan()).extracting(s->s.capabilityId()).containsExactly("logs.search","trace.search");
        assertThat(plan.toolPlan().plan()).allSatisfy(s->assertThat(s.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("from","2026-11-23T15:00:00Z","to","2026-11-24T10:27:24Z"))));assertThat(plan.documents().required()).isFalse();
    }
    @Test void resolvedOptionalDlqPeriodIsPassedInUtcWithoutAddingLogs()throws Exception {
        run("dlq","CLN_001","오늘 CLN_001 DLQ",context());var plan=capturedPlan();
        assertThat(plan.toolPlan().plan()).singleElement().satisfies(step->{assertThat(step.capabilityId()).isEqualTo("tc.dlq.list");assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001","from","2026-11-23T15:00:00Z","to","2026-11-24T10:27:24Z")));});
    }
    @Test void unknownRequiredPeriodClarifiesInsteadOfUsingContext()throws Exception {
        var context=new HashMap<>(context());context.put("timeWindow",Map.of("from","2026-11-22T00:00:00Z","to","2026-11-22T01:00:00Z"));
        clarification(run("incident",null,"지난주 에러",context),"TIME_WINDOW_REQUIRED","timeWindow");
    }
    @Test void unknownOptionalPeriodAlsoClarifiesInsteadOfDroppingThePeriod()throws Exception {clarification(run("dlq","CLN_001","이번 달 CLN_001 DLQ",context()),"TIME_WINDOW_REQUIRED","timeWindow");}
    @Test void missingPeriodRequiresClarificationAfterEmptyPreviousTurn()throws Exception {when(messages.findPreviousResolvedTimeWindow(any(),anyString(),anyString())).thenReturn(Optional.empty());clarification(run("communication","CLN_001","CLN_001 통신 로그",context()),"TIME_WINDOW_REQUIRED","timeWindow");}
    @Test void requiredSubjectNullClarifiesEqp()throws Exception {clarification(run("registration",null,"호기 등록 정보",context()),"PLAN_INVALID","eqp");}
    @Test void contextPeriodIsNormalizedToUtcForTc()throws Exception {
        var context=new HashMap<>(context());context.put("timeWindow",Map.of("from","2026-11-24T09:00:00+09:00","to","2026-11-24T10:00:00+09:00"));run("communication","CLN_001","CLN_001 통신 로그",context);
        assertThat(capturedPlan().toolPlan().plan().getFirst().arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001","from","2026-11-24T00:00:00Z","to","2026-11-24T01:00:00Z")));
    }
    @Test void onlyAbsentPeriodUsesPreviousTurnWindow()throws Exception {
        when(messages.findPreviousResolvedTimeWindow(any(),anyString(),anyString())).thenReturn(Optional.of(Map.of("from","2026-11-24T00:00:00Z","to","2026-11-24T01:00:00Z")));
        run("communication","CLN_001","CLN_001 통신 로그",context());assertThat(capturedPlan().toolPlan().plan().getFirst().arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp","CLN_001","from","2026-11-24T00:00:00Z","to","2026-11-24T01:00:00Z")));
    }
}
