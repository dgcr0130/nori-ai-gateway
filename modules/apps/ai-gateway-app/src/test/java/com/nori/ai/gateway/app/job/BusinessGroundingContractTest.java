package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.app.observability.AuditRecorder;
import com.nori.ai.gateway.core.evidence.EvidenceAssembler;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.llm.ComposerService;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.persistence.repository.MessageRepository;
import java.io.IOException;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 실제 업무 근거 수집부터 충분성·재확인·종결 상태까지 연결하며 작성기만 대역을 씁니다. */
class BusinessGroundingContractTest {
    static final Instant NOW=Instant.parse("2026-11-24T05:33:12Z");
    static final String DLQ_QUESTION="설비 CLN_001의 DLQ 데드레터 큐 목록을 조회해 실패 메시지와 오류 내역을 알려 주세요.";
    static final String MODEL_QUESTION="tc.model.read 도구를 사용하여 eqp=CLN_001에 연결된 모델과 메시지 정의를 읽어 주세요.";
    record Fixture(JobContext job,WorkflowExecutionPlan plan,WorkflowOutcome outcome,GroundingInputs inputs,WorkflowGrounding grounding,CapabilityRegistry registry) {}
    JsonNode load(String name) {
        try(var in=getClass().getResourceAsStream("/fixtures/business-grounding/"+name+".json")) {
            return SchemaContracts.JSON.readTree(Objects.requireNonNull(in));
        }catch(IOException failure){throw new AssertionError(failure);}
    }
    Fixture fixture(String name,ResultStatus status,boolean observed,List<String> targets,Map<String,Object> arguments,int maxUnits) {
        var data=load(name);String tool=name.equals("dlq")?"tc.dlq.list":name.equals("snapshot")?"tc.eqp.getOperationalSnapshot":"tc.model.read";
        String question=name.equals("dlq")?DLQ_QUESTION:name.equals("snapshot")?"설비 CLN_001의 현재 운영 스냅샷을 조회해 연결 상태, 제어 상태, 설비 상태를 알려 주세요.":MODEL_QUESTION;
        var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("READ","DLQ_READ"),"pdg",NOW.minusSeconds(1),NOW.plusSeconds(60),"job");
        // 분류의 오답 대상을 일부러 남겨 서버 실행 계획의 resources만 사용해야 통과하게 합니다.
        var job=new JobContext("job","conversation",scope,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,
            Map.of("taskType","LIVE_QUERY","toolCalls",List.of(),"targets",List.of(new ResourceRef("EQP","LLM_WRONG_999"))),new JobRequestInput("message",1,question,Map.of()));
        var manifest=ConfiguredTool.create(OwnerSystem.TC,tool,"업무 조회",ToolKind.BUSINESS,SchemaContracts.parse("{\"type\":\"object\"}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of(name.equals("dlq")?"DLQ_READ":"READ"),10000);
        var registry=new CapabilityRegistry(List.of(manifest),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        var definitionStep=new WorkflowDefinition.Step("business",tool,null,List.of(),false,List.of());
        var step=new PlanStep("business",tool,List.of(),targets.stream().map(ref->new ResourceRef("EQP",ref)).toList(),SchemaContracts.JSON.valueToTree(arguments));
        var definition=new WorkflowDefinition(OwnerSystem.TC,TaskType.LIVE_QUERY,List.of(definitionStep),WorkflowDefinition.AnswerSource.TOOL_RESULTS,null);
        var plan=new WorkflowExecutionPlan(definition,new PlanEnvelope("job","conversation",question,List.of(),TaskType.LIVE_QUERY,List.of(step)),List.of(new WorkflowExecutionPlan.ExecutionStep(definitionStep,step)));
        var items=SchemaContracts.JSON.convertValue(data.path("items"),new tools.jackson.core.type.TypeReference<List<Map<String,Object>>>(){});
        var missing=SchemaContracts.JSON.convertValue(data.path("missingFields"),new tools.jackson.core.type.TypeReference<List<String>>(){});
        var result=ToolResults.result(manifest,scope,status,items,NOW,missing,List.of(Warning.RETENTION_UNVERIFIED),List.of(new ToolResultMetadata.MissingRange(NOW.minusSeconds(30),NOW)),null);
        var blocks=new com.nori.ai.gateway.core.blocks.BlockBuilder(new com.nori.ai.gateway.core.blocks.UiSchemaValidator()).build(manifest,result);
        var outcome=new WorkflowOutcome(List.of(new WorkflowOutcome.Stage("business",result,observed,items.size())),blocks,result.evidence(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
        var config=new GatewaySystemsProperties.SystemConfig("fixture","digest","jwks",null,null,List.of(),List.of(),List.of(),List.of(),List.of(),"REVIEW",Map.of());
        var inputs=new GroundingInputs(new GatewaySystemsProperties(Map.of("TC",config)),registry,new ContentSanitizer());
        var nouns=name.equals("dlq")?List.of("설비","데드레터","큐","목록"):name.equals("snapshot")?List.of("설비","현재","운영","스냅샷"):List.of("도구","사용","연결","모델");
        SearchLanguage language=text->text.equals(question)?nouns.stream().map(t->new SearchLanguage.Term(t,"NNG")).toList():Arrays.stream(SearchText.normalize(text).split("[^가-힣a-z0-9_.-]+")).map(t->new SearchLanguage.Term(t,"NNG")).toList();
        DocumentSearch search=(s,q,t,p)->{throw new AssertionError("LIVE 업무 조회는 문서를 검색하지 않습니다");};
        var grounding=new WorkflowGrounding(search,language,SearchSettings.defaults(),new EvidenceSelection(String::length,1000000),inputs,Clock.fixed(NOW,ZoneOffset.UTC),new ExpiryCheckpoint(),(j,u,h)->u.size()<=maxUnits);
        return new Fixture(job,plan,outcome,inputs,grounding,registry);
    }
    Fixture dlq(ResultStatus status,boolean observed,Map<String,Object> arguments) {return fixture("dlq",status,observed,List.of("CLN_001"),arguments,25);}
    WorkflowGrounding.Prepared prepare(Fixture f) {return f.grounding().prepare(f.job(),f.plan(),f.outcome(),new ToolCallBudget());}

    @Test void partialZeroDlqRowsProduceOneExactSummaryWithLocationValuesPermissionsAndMetadata() {
        var f=dlq(ResultStatus.PARTIAL,true,Map.of("eqp","CLN_001"));
        var collected=f.inputs().collect(f.job(),f.plan(),f.outcome());assertThat(collected.invalidSteps()).isEmpty();
        assertThat(collected.units()).singleElement().satisfies(unit->{
            assertThat(unit.sourceType()).isEqualTo("SYSTEM");
            assertThat(unit.body()).isEqualTo("[조회 결과 요약]\n도구(toolId): tc.dlq.list\n조회 조건 > eqp: CLN_001\n조회 상태(status): PARTIAL\n결과 건수(count): 0\n누락 항목(missingFields): completeness");
            assertThat(unit.location()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("toolId","tc.dlq.list","ref","tc.dlq.list:result-summary","observedAt",NOW.toString())));
            assertThat(unit.values()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("toolId","tc.dlq.list","arguments",Map.of("eqp","CLN_001"),"status","PARTIAL","count",0,"missingFields",List.of("completeness"))));
            assertThat(unit.permissions()).containsExactly("DLQ_READ");assertThat(unit.status()).isEqualTo("PARTIAL");assertThat(unit.observedAt()).isEqualTo(NOW.toString());
            assertThat(unit.missingFields()).containsExactly("completeness");assertThat(unit.warnings()).containsExactly("RETENTION_UNVERIFIED");
            assertThat(unit.retentionMeta().path("missingRanges")).isEqualTo(SchemaContracts.JSON.valueToTree(f.outcome().stages().getFirst().result().metadata().missingRanges()));
            assertThat(unit.retentionMeta().path("unverifiedRanges")).isEmpty();
        });
    }
    @Test void summaryArgumentsAreSortedAndMaskedThroughTheExistingContentPath() {
        var arguments=new LinkedHashMap<String,Object>();arguments.put("zeta","operator@example.com");arguments.put("eqp","CLN_001");arguments.put("alpha","first");
        var f=dlq(ResultStatus.PARTIAL,true,arguments);var unit=f.inputs().collect(f.job(),f.plan(),f.outcome()).units().getFirst();
        assertThat(unit.body()).contains("조회 조건 > alpha: first\n조회 조건 > eqp: CLN_001\n조회 조건 > zeta:");
        assertThat(unit.body()).doesNotContain("operator@example.com");assertThat(unit.values().toString()).doesNotContain("operator@example.com");
    }
    @Test void everyAcceptedObservedZeroRowStatusGetsASummaryAndEmptyMissingFieldsSayNone() {
        for(var status:List.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT)) {
            var f=dlq(status,true,Map.of("eqp","CLN_001"));
            assertThat(f.inputs().collect(f.job(),f.plan(),f.outcome()).units()).as(status.name()).singleElement().satisfies(u->assertThat(u.status()).isEqualTo(status.name()));
        }
        var f=fixture("model",ResultStatus.SUCCESS,true,List.of("CLN_001"),Map.of("eqp","CLN_001"),25);
        var stage=f.outcome().stages().getFirst();var result=ToolResults.result(f.registry().find(f.job().scope(),stage.result().capabilityId()).orElseThrow(),f.job().scope(),ResultStatus.EMPTY,List.of(),NOW,List.of(),Set.of(),List.of(),null);
        var empty=new WorkflowOutcome(List.of(new WorkflowOutcome.Stage("business",result,true,0)),List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
        assertThat(f.inputs().collect(f.job(),f.plan(),empty).units()).singleElement().satisfies(u->{assertThat(u.body()).endsWith("누락 항목(missingFields): 없음");assertThat(u.values().path("missingFields")).isEmpty();});
    }
    @Test void rowsProduceOnlyRowUnitsAndRejectedOrUnobservedStatusesNeverProduceSummaries() {
        var f=fixture("model",ResultStatus.SUCCESS,true,List.of("CLN_001"),Map.of("eqp","CLN_001"),25);
        assertThat(f.inputs().collect(f.job(),f.plan(),f.outcome()).units()).hasSize(25).noneSatisfy(u->assertThat(u.location().path("ref").asString()).endsWith(":result-summary"));
        for(var status:ResultStatus.values())if(!Set.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT).contains(status)) {
            var rejected=dlq(status,true,Map.of("eqp","CLN_001"));assertThat(rejected.inputs().collect(rejected.job(),rejected.plan(),rejected.outcome()).units()).as(status.name()).isEmpty();
        }
        for(var status:List.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT)) {
            var unobserved=dlq(status,false,Map.of("eqp","CLN_001"));assertThat(unobserved.inputs().collect(unobserved.job(),unobserved.plan(),unobserved.outcome()).units()).isEmpty();
        }
    }
    @Test void zeroDlqSummaryAndEnglishSnapshotRemainSufficientThroughRefresh() {
        for(var f:List.of(dlq(ResultStatus.PARTIAL,true,Map.of("eqp","CLN_001")),fixture("snapshot",ResultStatus.PARTIAL,true,List.of("CLN_001"),Map.of("eqp","CLN_001"),1))) {
            var prepared=prepare(f);assertThat(prepared.sufficient()).isTrue();assertThat(prepared.selected().truncatedItems()).isFalse();
            assertThat(f.grounding().refresh(f.job(),prepared).sufficient()).isTrue();
        }
    }
    @Test void businessTargetsComeFromExecutedResourcesAndEveryTargetNeedsSelectedSystemBody() {
        var wrong=fixture("snapshot",ResultStatus.PARTIAL,true,List.of("PHOTO_001"),Map.of("eqp","PHOTO_001"),2);assertThat(prepare(wrong).sufficient()).isFalse();
        var missing=fixture("snapshot",ResultStatus.PARTIAL,true,List.of("PHOTO_001","CLN_001"),Map.of("eqp","CLN_001"),2);assertThat(prepare(missing).sufficient()).isFalse();
        var noResources=fixture("snapshot",ResultStatus.PARTIAL,true,List.of(),Map.of("eqp","CLN_001"),2);assertThat(prepare(noResources).sufficient()).isFalse();
        var unobserved=fixture("snapshot",ResultStatus.PARTIAL,false,List.of("CLN_001"),Map.of("eqp","CLN_001"),2);assertThat(prepare(unobserved).sufficient()).isFalse();
    }
    @Test void aPlannedButUnexecutedBusinessTargetDoesNotInvalidateAnObservedQuery() {
        var f=fixture("snapshot",ResultStatus.PARTIAL,true,List.of("CLN_001"),Map.of("eqp","CLN_001"),2);
        var planned=new WorkflowDefinition.Step("not-executed","tc.eqp.getOperationalSnapshot",null,List.of(),false,List.of());
        var omitted=new PlanStep(planned.stepId(),planned.capabilityId(),List.of(),List.of(new ResourceRef("EQP","PHOTO_001")),SchemaContracts.JSON.createObjectNode().put("eqp","PHOTO_001"));
        var definition=new WorkflowDefinition(OwnerSystem.TC,TaskType.LIVE_QUERY,List.of(f.plan().definition().steps().getFirst(),planned),WorkflowDefinition.AnswerSource.TOOL_RESULTS,null);
        var plan=new WorkflowExecutionPlan(definition,new PlanEnvelope("job","conversation",f.job().requestInput().maskedQuestion(),List.of(),TaskType.LIVE_QUERY,List.of(f.plan().toolPlan().plan().getFirst(),omitted)),List.of(f.plan().steps().getFirst(),new WorkflowExecutionPlan.ExecutionStep(planned,omitted)));
        assertThat(f.grounding().prepare(f.job(),plan,f.outcome(),new ToolCallBudget()).sufficient()).isTrue();
    }
    @Test void modelOverflowStaysSufficientAndTerminalIsPartialWithTruncatedItemsAndAnAnswer() throws Exception {
        var f=fixture("model",ResultStatus.SUCCESS,true,List.of("CLN_001"),Map.of("eqp","CLN_001"),2);
        var prepared=prepare(f);assertThat(prepared.sufficient()).isTrue();assertThat(prepared.selected().units()).hasSize(2);assertThat(prepared.selected().truncatedItems()).isTrue();
        assertThat(f.grounding().refresh(f.job(),prepared).sufficient()).isTrue();
        var composer=mock(ComposerService.class);when(composer.groundedQuestionFits(any())).thenReturn(true);
        when(composer.configurationSha256()).thenReturn("a".repeat(64));
        var unit=prepared.selected().units().getFirst();var answer=new GroundedAnswer("ANSWER","[모델 정의][1]",List.of(new GroundedAnswer.Citation(1,null,"SYSTEM",unit.location())),List.of(new GroundedAnswer.Sentence("[모델 정의]",List.of(1))));
        when(composer.compose(any(),any())).thenReturn(new ComposerService.Result(ComposerOutcome.COMPOSED,Optional.of(answer.text()),1,0,0,List.of(),Optional.of(answer)));
        var builder=mock(WorkflowPlanBuilder.class);when(builder.definition(f.job())).thenReturn(f.plan().definition());when(builder.build(any(),any(),any(),any())).thenReturn(f.plan());
        var executor=mock(com.nori.ai.gateway.core.workflow.WorkflowExecutor.class);when(executor.run(any(),any(),any(),any())).thenReturn(f.outcome());
        var adapter=new WorkflowExecutionAdapter(f.registry(),builder,executor,mock(WorkflowResourceResolverFactory.class),new TimeWindowResolver(),mock(MessageRepository.class),new ExpiryCheckpoint(),new ContentSanitizer(),new TerminalOutcomeAssembler(),new ConversationSummaryBuilder(),new EvidenceAssembler(),mock(AuditRecorder.class),Optional.of(composer),new LogSummarizer(),Optional.empty(),Optional.of(f.grounding()));
        var terminal=adapter.execute(f.job());assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.PARTIAL);assertThat(terminal.jobStatus()).isEqualTo(JobStatus.PARTIAL);
        assertThat(terminal.resultPayload().get("missingFields")).isEqualTo(List.of("truncatedItems"));
        assertThat(SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer")).path("kind").asString()).isEqualTo("ANSWER");verify(composer).compose(any(),any());
    }
}
