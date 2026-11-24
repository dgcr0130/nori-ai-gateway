package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.workflow.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CodeGroundingContractTest {
    static final Instant NOW=Instant.parse("2026-11-25T00:00:00Z");
    static final String TERM="EquipmentContextRegistry",QUESTION=TERM+" 클래스 사용 위치";
    record Fixture(JobContext job,WorkflowExecutionPlan plan,WorkflowOutcome outcome,GroundingInputs inputs,WorkflowGrounding grounding) {}
    Fixture fixture(String query,String text,ResultStatus status,boolean observed) {
        var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("AI_CODE_READ"),"pdg",NOW.minusSeconds(1),NOW.plusSeconds(60),"job");
        var job=new JobContext("job","conversation",scope,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,Map.of("taskType","LIVE_QUERY","targets",List.of()),new JobRequestInput("message",1,QUESTION,Map.of()));
        var manifest=ConfiguredTool.create(OwnerSystem.TC,"git.searchCode","코드 조회",ToolKind.GIT,SchemaContracts.parse("{\"type\":\"object\",\"properties\":{\"q\":{\"type\":\"string\"}}}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("AI_CODE_READ"),10000);
        var registry=new CapabilityRegistry(List.of(manifest),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        var config=new GatewaySystemsProperties.SystemConfig("fixture","digest","jwks",null,null,List.of(),List.of(),List.of(),List.of(new GatewaySystemsProperties.Repository("28","main","gateway",List.of("AI_CODE_READ"))),List.of(),"REVIEW",Map.of());
        var inputs=new GroundingInputs(new GatewaySystemsProperties(Map.of("TC",config)),registry,new ContentSanitizer(),Map.of("nori-tc-gateway","28"));
        var declaration=new WorkflowDefinition.Step("code","git.searchCode",null,List.of(),false,List.of());
        var args=SchemaContracts.JSON.createObjectNode();if(query!=null)args.put("q",query);
        var step=new PlanStep("code","git.searchCode",List.of(),List.of(),args);
        var definition=new WorkflowDefinition(OwnerSystem.TC,TaskType.LIVE_QUERY,List.of(declaration),WorkflowDefinition.AnswerSource.TOOL_RESULTS,null);
        var plan=new WorkflowExecutionPlan(definition,new PlanEnvelope("job","conversation",QUESTION,List.of(),TaskType.LIVE_QUERY,List.of(step)),List.of(new WorkflowExecutionPlan.ExecutionStep(declaration,step)));
        var rows=text==null?List.of():List.of(Map.of("sourceRepo","nori-tc-gateway","path","gateway/Use.java","ref","main","text",text,"codeRefKind","CURRENT_APPROVED_REF"));
        var result=ToolResults.result(manifest,scope,status,rows,NOW,List.of(),Set.of(),List.of(),null);
        var outcome=new WorkflowOutcome(List.of(new WorkflowOutcome.Stage("code",result,observed,rows.size())),List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
        SearchLanguage language=value->Arrays.stream(SearchText.normalize(value).split("[^가-힣a-z0-9_.-]+"))
            .map(t->new SearchLanguage.Term(t,t.matches("[가-힣]+")?"NNG":"SL")).toList();
        DocumentSearch search=(s,q,t,p)->{throw new AssertionError("코드 LIVE_QUERY는 문서 검색을 하지 않습니다");};
        var grounding=new WorkflowGrounding(search,language,SearchSettings.defaults(),new EvidenceSelection(String::length,1000000),inputs,Clock.fixed(NOW,ZoneOffset.UTC),new ExpiryCheckpoint(),(j,u,h)->true);
        return new Fixture(job,plan,outcome,inputs,grounding);
    }
    WorkflowGrounding.Prepared prepare(Fixture f){return f.grounding().prepare(f.job(),f.plan(),f.outcome(),new ToolCallBudget());}
    @Test void observedGitQueryFlowsThroughSourceUnitsPreparedAndRefreshWithoutKoreanNouns() {
        for(var status:List.of(ResultStatus.SUCCESS,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT)) {
            var f=fixture(TERM,"class "+TERM+" {}",status,true);
            var collected=f.inputs().collect(f.job(),f.plan(),f.outcome());assertThat(collected.invalidSteps()).isEmpty();assertThat(collected.businessTargets()).isEmpty();assertThat(collected.codeTargets()).containsExactly(TERM);
            assertThat(collected.units()).singleElement().satisfies(u->{assertThat(u.sourceType()).isEqualTo("GIT");assertThat(SchemaContracts.parse(u.body()).path("text").asString()).isEqualTo("class "+TERM+" {}");});
            var prepared=prepare(f);assertThat(prepared.sufficient()).as(status.name()).isTrue();assertThat(prepared.codeTargets()).containsExactly(TERM);
            var refreshed=f.grounding().refresh(f.job(),prepared);assertThat(refreshed.sufficient()).isTrue();assertThat(refreshed.codeTargets()).containsExactly(TERM);
        }
    }
    @Test void absentQueryUsesLegacyRulesAndNeitherUnobservedNorEmptyGitInventsSummaryEvidence() {
        assertThat(prepare(fixture(null,"class "+TERM+" {}",ResultStatus.SUCCESS,true)).sufficient()).isFalse();
        assertThat(prepare(fixture(null,TERM+" 클래스 사용 위치",ResultStatus.SUCCESS,true)).sufficient()).isTrue();
        for(var f:List.of(fixture(TERM,"class AnotherRegistry {}",ResultStatus.SUCCESS,true),fixture(TERM,"class "+TERM+"Factory {}",ResultStatus.SUCCESS,true),fixture(TERM,null,ResultStatus.EMPTY,true),fixture(TERM,"class "+TERM+" {}",ResultStatus.SUCCESS,false))) {
            assertThat(prepare(f).sufficient()).isFalse();assertThat(f.grounding().refresh(f.job(),prepare(f)).sufficient()).isFalse();
        }
        var empty=fixture(TERM,null,ResultStatus.EMPTY,true);assertThat(empty.inputs().collect(empty.job(),empty.plan(),empty.outcome()).units()).isEmpty();
        var unobserved=fixture(TERM,TERM,ResultStatus.SUCCESS,false);assertThat(unobserved.inputs().collect(unobserved.job(),unobserved.plan(),unobserved.outcome()).codeTargets()).isEmpty();
    }
    @Test void plannedButUnobservedQueriesAreExcludedAndEveryObservedQueryMustBeCovered() {
        var f=fixture(TERM,"class "+TERM+" {}",ResultStatus.SUCCESS,true);
        var declaration=new WorkflowDefinition.Step("second","git.searchCode",null,List.of(),false,List.of());
        var step=new PlanStep("second","git.searchCode",List.of(),List.of(),SchemaContracts.JSON.createObjectNode().put("q","readRegistration"));
        var definition=new WorkflowDefinition(OwnerSystem.TC,TaskType.LIVE_QUERY,List.of(f.plan().definition().steps().getFirst(),declaration),WorkflowDefinition.AnswerSource.TOOL_RESULTS,null);
        var plan=new WorkflowExecutionPlan(definition,new PlanEnvelope("job","conversation",QUESTION,List.of(),TaskType.LIVE_QUERY,List.of(f.plan().toolPlan().plan().getFirst(),step)),List.of(f.plan().steps().getFirst(),new WorkflowExecutionPlan.ExecutionStep(declaration,step)));
        assertThat(f.grounding().prepare(f.job(),plan,f.outcome(),new ToolCallBudget()).sufficient()).isTrue();
        var stages=new ArrayList<>(f.outcome().stages());stages.add(new WorkflowOutcome.Stage("second",stages.getFirst().result(),true,1));
        var outcome=new WorkflowOutcome(stages,List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
        assertThat(f.inputs().collect(f.job(),plan,outcome).codeTargets()).containsExactly(TERM,"readRegistration");
        var prepared=f.grounding().prepare(f.job(),plan,outcome,new ToolCallBudget());assertThat(prepared.sufficient()).isFalse();assertThat(f.grounding().refresh(f.job(),prepared).sufficient()).isFalse();
    }
}
