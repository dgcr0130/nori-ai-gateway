package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;

class WorkflowGroundingTest {
    record Fixture(WorkflowGrounding grounding,JobContext job,WorkflowExecutionPlan plan) {}
    Fixture fixture(TaskType task,String question,DocumentSearch search,List<EvidenceUnit> units) {
        var scope=DomainFixtures.scope();var job=new JobContext(scope.requestId(),"conversation",scope,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,
            Map.of("taskType",task.name(),"toolCalls",List.of(),"targets",List.of(),"searchQuery",question,"retryQuery",question+" 재검색"),new JobRequestInput("message",1,question,Map.of()));
        var definition=new WorkflowDefinition(scope.boundSystem(),task,List.of(),WorkflowDefinition.AnswerSource.TOOL_RESULTS,null);
        var plan=new WorkflowExecutionPlan(definition,new PlanEnvelope(job.jobId(),"conversation",question,List.of(),task,List.of()),List.of(),new WorkflowExecutionPlan.DocumentSearch(task!=TaskType.LIVE_QUERY,question,question+" 재검색"));
        var inputs=new WorkflowGrounding.Inputs(){public WorkflowGrounding.SourceUnits collect(JobContext ignored,WorkflowOutcome outcome){return new WorkflowGrounding.SourceUnits(units,Set.of());}public Map<String,String> glossary(JobContext ignored){return Map.of();}};
        SearchLanguage language=text->Arrays.stream(text.split(" ")).map(t->new SearchLanguage.Term(t,t.matches("[가-힣]+")?"NNG":"SL")).toList();
        return new Fixture(new WorkflowGrounding(search,language,SearchSettings.defaults(),new EvidenceSelection(String::length,10000),inputs,DomainFixtures.CLOCK,new ExpiryCheckpoint()),job,plan);
    }
    WorkflowOutcome empty(){return new WorkflowOutcome(List.of(),List.of(),List.of(),new com.nori.ai.gateway.core.result.ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());}
    @Test void twoSearchRoundsShareToolBudgetAndUnionCanSatisfyCoverageWithoutClassifyingAgain() {
        var queries=new ArrayList<String>();var first=HybridEvidenceSelectionTest.hit(0,"설비 T3");var second=HybridEvidenceSelectionTest.hit(1,"상태 EQP-001");
        var fixture=fixture(TaskType.KNOWLEDGE_QUERY,"설비 상태 T3",(scope,query,targets,settings)->{queries.add(query);return HybridEvidenceSelectionTest.round(List.of(queries.size()==1?first:second),List.of(),List.of());},List.of());
        var budget=new ToolCallBudget();budget.tryAcquire("asset.read",ToolKind.BUSINESS);
        var result=fixture.grounding.prepare(fixture.job,fixture.plan,empty(),budget);
        assertThat(queries).containsExactly("설비 상태 T3","설비 상태 T3 재검색");assertThat(budget.total()).isEqualTo(3);
        assertThat(result.sufficient()).isTrue();assertThat(result.selected().units()).hasSize(2);assertThat(result.outcome().stages()).hasSize(2);
    }
    @Test void liveUsesOnlySelectedNonDocumentsAndNoSearchOrRetryEvenWhenInsufficient() {
        var count=new AtomicInteger();DocumentSearch search=(scope,q,t,s)->{count.incrementAndGet();throw new AssertionError("LIVE 문서 검색");};
        var good=fixture(TaskType.LIVE_QUERY,"설비 상태 T3",search,List.of(HybridEvidenceSelectionTest.other("설비 상태 T3")));
        assertThat(good.grounding.prepare(good.job,good.plan,empty(),new ToolCallBudget()).sufficient()).isTrue();
        var bad=fixture(TaskType.LIVE_QUERY,"설비 상태 T3",search,List.of(HybridEvidenceSelectionTest.other("설비 T3")));
        assertThat(bad.grounding.prepare(bad.job,bad.plan,empty(),new ToolCallBudget()).sufficient()).isFalse();assertThat(count).hasValue(0);
    }
    @Test void embeddingFailureRemainsSourceUnavailableAndBudgetRefusalDoesNotCallSearch() {
        var count=new AtomicInteger();var fixture=fixture(TaskType.INCIDENT_ANALYSIS,"설비 상태 T3",(scope,q,t,s)->{count.incrementAndGet();throw new InferenceClient.EmbeddingFailed(InferenceClient.FailureReason.TRANSPORT,null);},List.of());
        var result=fixture.grounding.prepare(fixture.job,fixture.plan,empty(),new ToolCallBudget());
        assertThat(count).hasValue(1);assertThat(result.sufficient()).isFalse();assertThat(result.outcome().stages()).singleElement().satisfies(s->{assertThat(s.result().status()).isEqualTo(com.nori.ai.gateway.core.result.ResultStatus.SOURCE_UNAVAILABLE);assertThat(s.observed()).isFalse();});
        var refused=fixture.grounding.prepare(fixture.job,fixture.plan,empty(),new ToolCallBudget(0,3,2,1));assertThat(count).hasValue(1);assertThat(refused.outcome().trace()).singleElement().satisfies(t->assertThat(t.outcome()).isEqualTo(WorkflowOutcome.TraceOutcome.TOOL_LIMIT_REACHED));
    }
    @Test void anApprovedCandidateCannotReplaceItsOwnMissingCoverageWithReviewerApproval() {
        var insufficient=HybridEvidenceSelectionTest.hit(0,"설비 T3");var sufficient=HybridEvidenceSelectionTest.hit(1,"설비 상태 T3");
        var fixture=fixture(TaskType.KNOWLEDGE_QUERY,"설비 상태 T3",(scope,q,t,s)->new DocumentSearch.Round(List.of(sufficient),List.of(),List.of(),List.of(new DocumentSearch.Verified(new UUID(2,1),List.of(insufficient)))),List.of());
        var result=fixture.grounding.prepare(fixture.job,fixture.plan,empty(),new ToolCallBudget());
        assertThat(result.sufficient()).isTrue();assertThat(result.selected().units()).singleElement().satisfies(unit->assertThat(unit.body()).isEqualTo("설비 상태 T3"));
    }
    @Test void verifiedOnlyParentIsObservedDataRatherThanANormalZeroHitSearch() {
        var hit=HybridEvidenceSelectionTest.hit(0,"설비 상태 T3");
        var fixture=fixture(TaskType.KNOWLEDGE_QUERY,"설비 상태 T3",(scope,q,t,s)->new DocumentSearch.Round(List.of(),List.of(),List.of(),List.of(new DocumentSearch.Verified(new UUID(2,1),List.of(hit)))),List.of());
        var result=fixture.grounding.prepare(fixture.job,fixture.plan,empty(),new ToolCallBudget());assertThat(result.sufficient()).isTrue();
        assertThat(result.outcome().stages()).singleElement().satisfies(stage->{assertThat(stage.result().status()).isEqualTo(com.nori.ai.gateway.core.result.ResultStatus.SUCCESS);assertThat(stage.count()).isEqualTo(1);});
    }
}
