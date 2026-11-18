package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.workflow.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.core.DomainFixtures.*;

class TerminalOutcomeAssemblerTest {
    @Test void allOrderedPairsFollowTheSpecifiedSourcePriorityAndObservedAtIsRequired() {
        var data=Set.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT);
        var failures=List.of(ResultStatus.FAILED,ResultStatus.SOURCE_UNAVAILABLE,ResultStatus.DENIED);
        var statuses=List.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT,
            ResultStatus.OUT_OF_RETENTION,ResultStatus.DENIED,ResultStatus.SOURCE_UNAVAILABLE,ResultStatus.FAILED);
        for(var first:statuses)for(var second:statuses) {
            var pair=List.of(first,second);
            boolean observed=pair.stream().anyMatch(data::contains);
            ResultStatus expected;
            var failure=failures.stream().filter(pair::contains).findFirst();
            if(failure.isPresent())expected=observed?ResultStatus.PARTIAL:failure.get();
            else if(pair.contains(ResultStatus.PARTIAL)||observed&&pair.contains(ResultStatus.OUT_OF_RETENTION))expected=ResultStatus.PARTIAL;
            else if(!observed)expected=ResultStatus.OUT_OF_RETENTION;
            else expected=List.of(ResultStatus.CONFLICT,ResultStatus.STALE,ResultStatus.SUCCESS,ResultStatus.EMPTY).stream().filter(pair::contains).findFirst().orElseThrow();
            var result=assembler.assemble(outcome(List.of(stage(first,data.contains(first)),stage(second,data.contains(second)))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
            assertThat(result.resultStatus()).as(pair.toString()).isEqualTo(expected);
            assertThat(result.errorCode()).as(pair.toString()).isEqualTo(expected==ResultStatus.FAILED?ErrorCode.CONTRACT_INVALID:null);
            JobStatus job=expected==ResultStatus.PARTIAL?JobStatus.PARTIAL:failures.contains(expected)?JobStatus.FAILED:JobStatus.SUCCEEDED;
            assertThat(result.jobStatus()).isEqualTo(job);assertThat(result.eventType()).isEqualTo(job==JobStatus.PARTIAL?"job.partial":job==JobStatus.FAILED?"job.failed":"job.completed");
        }
        for(var status:data) {
            var withoutTime=new WorkflowOutcome.Stage("missing-time",changeResult(stage(status,true).result(),"observedAt",null),true,0);
            if(status==ResultStatus.PARTIAL)continue;
            assertThatThrownBy(()->assembler.assemble(outcome(List.of(withoutTime)),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED)).isInstanceOf(IllegalArgumentException.class);
        }
    }
    @Test void timeoutAndUnsupportedRemainInToolSummaryAndAggregateAsUnavailable() {
        record Row(List<ResultStatus> input,ResultStatus expected) {}
        for(var row:List.of(new Row(List.of(ResultStatus.TIMEOUT),ResultStatus.SOURCE_UNAVAILABLE),new Row(List.of(ResultStatus.UNSUPPORTED),ResultStatus.SOURCE_UNAVAILABLE),
                new Row(List.of(ResultStatus.TIMEOUT,ResultStatus.EMPTY),ResultStatus.PARTIAL),new Row(List.of(ResultStatus.FAILED,ResultStatus.TIMEOUT),ResultStatus.FAILED),
                new Row(List.of(ResultStatus.DENIED,ResultStatus.UNSUPPORTED),ResultStatus.SOURCE_UNAVAILABLE))) {
            var result=assembler.assemble(outcome(row.input().stream().map(status->stage(status,status==ResultStatus.EMPTY)).toList()),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
            assertThat(result.resultStatus()).isEqualTo(row.expected());assertThat(result.jobStatus()).isEqualTo(row.expected()==ResultStatus.PARTIAL?JobStatus.PARTIAL:JobStatus.FAILED);
            assertThat(result.errorCode()).isEqualTo(row.expected()==ResultStatus.FAILED?ErrorCode.CONTRACT_INVALID:null);
            assertThat(result.eventType()).isEqualTo(row.expected()==ResultStatus.PARTIAL?"job.partial":"job.failed");
            var summary=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.valueToTree(result.resultPayload()).path("toolSummary");
            for(int i=0;i<row.input().size();i++)assertThat(summary.get(i).path("status").asString()).isEqualTo(row.input().get(i).name());
        }
    }
    @Test void canonicalStageMatrixPreservesStatusJobEventAndErrorForEverySpecifiedCombination() {
        record Row(List<ResultStatus> stages,ResultStatus status,JobStatus job,ErrorCode error,String event) {}
        var rows=List.of(new Row(List.of(ResultStatus.SUCCESS),ResultStatus.SUCCESS,JobStatus.SUCCEEDED,null,"job.completed"),new Row(List.of(ResultStatus.SUCCESS,ResultStatus.EMPTY),ResultStatus.SUCCESS,JobStatus.SUCCEEDED,null,"job.completed"),new Row(List.of(ResultStatus.EMPTY),ResultStatus.EMPTY,JobStatus.SUCCEEDED,null,"job.completed"),new Row(List.of(ResultStatus.OUT_OF_RETENTION),ResultStatus.OUT_OF_RETENTION,JobStatus.SUCCEEDED,null,"job.completed"),new Row(List.of(ResultStatus.PARTIAL),ResultStatus.PARTIAL,JobStatus.PARTIAL,null,"job.partial"),new Row(List.of(ResultStatus.SUCCESS,ResultStatus.OUT_OF_RETENTION),ResultStatus.PARTIAL,JobStatus.PARTIAL,null,"job.partial"),new Row(List.of(ResultStatus.SUCCESS,ResultStatus.SOURCE_UNAVAILABLE),ResultStatus.PARTIAL,JobStatus.PARTIAL,null,"job.partial"),new Row(List.of(ResultStatus.EMPTY,ResultStatus.SOURCE_UNAVAILABLE),ResultStatus.PARTIAL,JobStatus.PARTIAL,null,"job.partial"),new Row(List.of(ResultStatus.SUCCESS,ResultStatus.DENIED),ResultStatus.PARTIAL,JobStatus.PARTIAL,null,"job.partial"),new Row(List.of(ResultStatus.DENIED),ResultStatus.DENIED,JobStatus.FAILED,null,"job.failed"),new Row(List.of(ResultStatus.DENIED,ResultStatus.OUT_OF_RETENTION),ResultStatus.DENIED,JobStatus.FAILED,null,"job.failed"),new Row(List.of(ResultStatus.SOURCE_UNAVAILABLE,ResultStatus.DENIED),ResultStatus.SOURCE_UNAVAILABLE,JobStatus.FAILED,null,"job.failed"),new Row(List.of(ResultStatus.FAILED),ResultStatus.FAILED,JobStatus.FAILED,ErrorCode.CONTRACT_INVALID,"job.failed"),new Row(List.of(),ResultStatus.SUCCESS,JobStatus.SUCCEEDED,null,"job.completed"));
        for(var row:rows) {
            var stages=row.stages().stream().map(status->stage(status,Set.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL).contains(status))).toList();var base=outcome(stages);
            var withSkips=new WorkflowOutcome(base.stages(),base.blocks(),base.evidence(),base.sourceDeps(),List.of(new WorkflowOutcome.Trace("optional",WorkflowOutcome.TraceOutcome.OPTIONAL_STEP_OMITTED),new WorkflowOutcome.Trace("empty",WorkflowOutcome.TraceOutcome.SKIPPED_NO_INPUT)),List.of(),Optional.empty());
            for(var actual:List.of(base,withSkips)) {var spec=assembler.assemble(actual,ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);assertThat(spec.resultStatus()).as(row.toString()).isEqualTo(row.status());assertThat(spec.jobStatus()).isEqualTo(row.job());assertThat(spec.errorCode()).isEqualTo(row.error());assertThat(spec.eventType()).isEqualTo(row.event());assertThat(spec.resultPayload()).doesNotContainKey("missingFields");assertThat(spec.resultPayload().toString()).doesNotContain("LLM_UNAVAILABLE","composer");}
        }
        assertThat(assembler.assemble(outcome(List.of(stage(ResultStatus.UNSUPPORTED,false))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED).resultStatus()).isEqualTo(ResultStatus.SOURCE_UNAVAILABLE);
    }
    @Test void composerFailureAndBudgetAndMissingRangeDoNotInventAnswerOrChangeSourceStatuses() {
        var empty=outcome(List.of());assertThat(assembler.assemble(empty,ComposerOutcome.LLM_UNAVAILABLE,InferenceMode.ENABLED).jobStatus()).isEqualTo(JobStatus.FAILED);
        var base=outcome(List.of(stage(ResultStatus.SUCCESS,true)));var data=new WorkflowOutcome(base.stages(),result(manifest()).blocks(),result(manifest()).evidence(),base.sourceDeps(),List.of(),List.of(),Optional.empty());
        var partial=assembler.assemble(data,ComposerOutcome.LLM_UNAVAILABLE,InferenceMode.ENABLED);assertThat(partial.resultStatus()).isEqualTo(ResultStatus.PARTIAL);assertThat(partial.errorCode()).isEqualTo(ErrorCode.LLM_UNAVAILABLE);assertThat(partial.resultPayload()).containsEntry("missingFields",List.of("answer"));
        var budget=assembler.assemble(data,ComposerOutcome.INPUT_BUDGET_EXCEEDED,InferenceMode.ENABLED);assertThat(budget.jobStatus()).isEqualTo(JobStatus.FAILED);assertThat(budget.errorCode()).isEqualTo(ErrorCode.INPUT_BUDGET_EXCEEDED);
        var outside=stage(ResultStatus.OUT_OF_RETENTION,false);var metadata=new ToolResultMetadata(result(manifest()).metadata().sourceRef(),"LIVE",false,List.of(new ToolResultMetadata.MissingRange(NOW.minusSeconds(60),NOW)));
        outside=new WorkflowOutcome.Stage("outside",changeResult(outside.result(),"metadata",metadata),false,0);var spec=assembler.assemble(outcome(List.of(stage(ResultStatus.SUCCESS,true),outside)),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
        var tree=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.valueToTree(spec.resultPayload());assertThat(tree.path("toolSummary").get(1).path("status").asString()).isEqualTo("OUT_OF_RETENTION");assertThat(tree.path("toolSummary").get(1).path("missingRanges")).hasSize(1);
    }
    @Test void backendRetentionWarningsAndUnverifiedRangesSurviveTheTerminalProjection(){
        var partial=stage(ResultStatus.PARTIAL,true);var ranges=List.of(new ToolResultMetadata.MissingRange(NOW.minusSeconds(60).plusNanos(1),NOW.minusNanos(1)));
        var result=changeResult(partial.result(),"warnings",List.of(Warning.RETENTION_UNVERIFIED));result=changeResult(result,"metadata",new ToolResultMetadata(partial.result().metadata().sourceRef(),"LIVE",false,List.of(),ranges));
        var spec=assembler.assemble(outcome(List.of(new WorkflowOutcome.Stage("call-1",result,true,1))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
        var row=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.valueToTree(spec.resultPayload()).path("toolSummary").get(0);
        assertThat(row.path("warnings").toString()).isEqualTo("[\"RETENTION_UNVERIFIED\"]");assertThat(row.path("unverifiedRanges").get(0).path("from").asString()).isEqualTo(ranges.getFirst().from().toString());assertThat(row.path("unverifiedRanges").get(0).path("to").asString()).isEqualTo(ranges.getFirst().to().toString());
    }
    private final TerminalOutcomeAssembler assembler=new TerminalOutcomeAssembler();
    private WorkflowOutcome outcome(List<WorkflowOutcome.Stage> stages){return new WorkflowOutcome(stages,List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());}
    private WorkflowOutcome.Stage stage(ResultStatus status,boolean observed) {
        var result=changeResult(result(manifest()),"status",status);
        if(status==ResultStatus.FAILED)result=changeResult(result,"errorCode",ErrorCode.CONTRACT_INVALID);
        return new WorkflowOutcome.Stage(status.name(),result,observed,0);
    }
    @Test void actualZeroCountObservationPlusSourceFailureIsPartialAndNoObservationFailureIsFailed() {
        var result=assembler.assemble(outcome(List.of(stage(ResultStatus.EMPTY,true),stage(ResultStatus.SOURCE_UNAVAILABLE,false))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
        assertThat(result.resultStatus()).isEqualTo(ResultStatus.PARTIAL);assertThat(result.jobStatus()).isEqualTo(JobStatus.PARTIAL);assertThat(result.errorCode()).isNull();assertThat(result.resultPayload()).doesNotContainKey("missingFields");
        result=assembler.assemble(outcome(List.of(stage(ResultStatus.OUT_OF_RETENTION,false),stage(ResultStatus.SOURCE_UNAVAILABLE,false))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
        assertThat(result.resultStatus()).isEqualTo(ResultStatus.SOURCE_UNAVAILABLE);assertThat(result.jobStatus()).isEqualTo(JobStatus.FAILED);
    }
    @Test void retentionCrossingAndClosedStageOutcomesFollowCanonicalAggregation() {
        assertThat(assembler.assemble(outcome(List.of(stage(ResultStatus.EMPTY,true),stage(ResultStatus.OUT_OF_RETENTION,false))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED).resultStatus()).isEqualTo(ResultStatus.PARTIAL);
        assertThat(assembler.assemble(outcome(List.of(stage(ResultStatus.CONFLICT,true),stage(ResultStatus.STALE,true))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED).resultStatus()).isEqualTo(ResultStatus.CONFLICT);
        assertThat(assembler.assemble(outcome(List.of(stage(ResultStatus.OUT_OF_RETENTION,false))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED).jobStatus()).isEqualTo(JobStatus.SUCCEEDED);
        assertThat(assembler.assemble(outcome(List.of(stage(ResultStatus.TIMEOUT,false))),ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED).resultStatus()).isEqualTo(ResultStatus.SOURCE_UNAVAILABLE);
        assertThatThrownBy(()->assembler.assemble(outcome(List.of()),ComposerOutcome.LLM_UNAVAILABLE,InferenceMode.DISABLED)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void clarificationContainsExactlyClarificationAndNoEarlierDataPayload() {
        for(var code:List.of(ErrorCode.PLAN_INVALID,ErrorCode.RESOURCE_NOT_FOUND,ErrorCode.INPUT_BUDGET_EXCEEDED,ErrorCode.TIME_WINDOW_REQUIRED)) {
            var rejection=new ValidationOutcome(false,List.of(),RejectReason.PLAN_INVALID,ResultStatus.FAILED,code,new Clarification(code,List.of("field")));
            var outcome=new WorkflowOutcome(List.of(stage(ResultStatus.SUCCESS,true)),result(manifest()).blocks(),result(manifest()).evidence(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of("execution"),Optional.of(rejection));
            var spec=assembler.assemble(outcome,ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);assertThat(spec.resultPayload()).containsOnlyKeys("clarification");assertThat(spec.errorCode()).isEqualTo(code);
        }
    }
    @Test void unsupportedRejectionPreservesOnlyFixedValidatorTrace() {
        var rejection=new ValidationOutcome(false,List.of(new ValidationOutcome.Trace(6,false,RejectReason.UNSUPPORTED)),RejectReason.UNSUPPORTED,ResultStatus.UNSUPPORTED,null,null);
        var source=outcome(List.of());var rejected=new WorkflowOutcome(source.stages(),source.blocks(),source.evidence(),source.sourceDeps(),source.trace(),source.toolExecutionRefs(),Optional.of(rejection));
        var result=assembler.assemble(rejected,ComposerOutcome.NOT_INVOKED,InferenceMode.DISABLED);
        assertThat(result.resultPayload().get("validationTrace")).isEqualTo(rejection.trace());assertThat(result.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);
    }
}
