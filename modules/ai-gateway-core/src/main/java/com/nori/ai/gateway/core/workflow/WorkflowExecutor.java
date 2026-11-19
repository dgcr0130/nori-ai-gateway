package com.nori.ai.gateway.core.workflow;

import com.nori.ai.gateway.core.authz.*;
import com.nori.ai.gateway.core.cache.ToolResultCache;
import com.nori.ai.gateway.core.blocks.*;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.JobContext;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import java.time.*;
import java.util.*;

public final class WorkflowExecutor {
    /** record는 실제 Tool 호출, blocked는 호출 전에 막힌 단계 1회를 남긴다. trace는 dispatcher 사전 거절일 때만 있다. */
    public interface ExecutionRecorder {
        void record(JobContext job,ToolDispatcher.InvocationRecord invocation);
        void blocked(JobContext job,String capabilityId,ResultStatus status,Optional<ExecutionContext.DispatchTrace> trace);
    }
    private final CapabilityRegistry registry;private final PlanValidator validator;private final ToolDispatcher dispatcher;private final SourceAuthorizationStep authorization;
    private final ExpiryCheckpoint expiry;private final Clock clock;private final ToolResultCache cache;private final BlockBuilder blocks;private final ExecutionRecorder recorder;
    private final LogSummarizer summarizer;private final GeneralQueryHelpBuilder help;private final EvidenceCoverageEvaluator coverage;private final java.util.function.Supplier<ToolCallBudget> budgets;private final ReportSections reports=new ReportSections(new UiSchemaValidator());
    public WorkflowExecutor(CapabilityRegistry registry,PlanValidator validator,ToolDispatcher dispatcher,SourceAuthorizationStep authorization,ExpiryCheckpoint expiry,Clock clock,ToolResultCache cache,BlockBuilder blocks,ExecutionRecorder recorder,LogSummarizer summarizer,GeneralQueryHelpBuilder help) {
        this(registry,validator,dispatcher,authorization,expiry,clock,cache,blocks,recorder,summarizer,help,new EvidenceCoverageEvaluator());
    }
    public WorkflowExecutor(CapabilityRegistry registry,PlanValidator validator,ToolDispatcher dispatcher,SourceAuthorizationStep authorization,ExpiryCheckpoint expiry,Clock clock,ToolResultCache cache,BlockBuilder blocks,ExecutionRecorder recorder,LogSummarizer summarizer,GeneralQueryHelpBuilder help,EvidenceCoverageEvaluator coverage) {
        this(registry,validator,dispatcher,authorization,expiry,clock,cache,blocks,recorder,summarizer,help,coverage,ToolCallBudget::new);
    }
    /** {@code budgets} 는 Job 실행마다 새 Tool 호출 한도를 만든다(운영 = SPEC §28.8 기본 한도). */
    public WorkflowExecutor(CapabilityRegistry registry,PlanValidator validator,ToolDispatcher dispatcher,SourceAuthorizationStep authorization,ExpiryCheckpoint expiry,Clock clock,ToolResultCache cache,BlockBuilder blocks,ExecutionRecorder recorder,LogSummarizer summarizer,GeneralQueryHelpBuilder help,EvidenceCoverageEvaluator coverage,java.util.function.Supplier<ToolCallBudget> budgets) {
        this.budgets=Objects.requireNonNull(budgets);
        this.registry=registry;this.validator=validator;this.dispatcher=dispatcher;this.authorization=authorization;this.expiry=expiry;this.clock=clock;this.cache=cache;this.blocks=blocks;this.recorder=recorder;
        this.summarizer=summarizer;this.help=help;this.coverage=Objects.requireNonNull(coverage);
    }
    public WorkflowOutcome run(JobContext job,WorkflowExecutionPlan plan,WorkflowResourceResolver resolver) {
        return run(job,plan,resolver,budgets.get());
    }
    /** 규칙 문서 검색도 실제 도구 실행과 같은 Job 호출 예산을 소비합니다. */
    public WorkflowOutcome run(JobContext job,WorkflowExecutionPlan plan,WorkflowResourceResolver resolver,ToolCallBudget callBudget) {
        expiry.assertLive(job.cancellation());
        List<WorkflowOutcome.Trace> traces=new ArrayList<>();
        Set<String> selected=new HashSet<>();plan.steps().forEach(step->selected.add(step.definition().stepId()));
        for(var step:plan.definition().steps())if(!selected.contains(step.stepId()))traces.add(new WorkflowOutcome.Trace(step.stepId(),WorkflowOutcome.TraceOutcome.OPTIONAL_STEP_OMITTED));
        var preparation=validator.prepare(plan.toolPlan(),job.scope(),resolver);
        if(preparation instanceof PlanValidator.Rejected rejected)return new WorkflowOutcome(List.of(),List.of(),List.of(),emptyDeps(),traces,List.of(),Optional.of(rejected.outcome()));
        if(preparation instanceof PlanValidator.ReadRejected rejected) {
            var denied=new ToolResultEnvelope(null,"resource.lookup",null,null,job.scope().boundSystem(),rejected.status(),null,List.of(),null,null,List.of(),List.of(),null,null,null,null,List.of(),List.of(),List.of(),List.of(),List.of());
            return new WorkflowOutcome(List.of(new WorkflowOutcome.Stage("resource",denied,false,0)),List.of(),List.of(),emptyDeps(),traces,List.of(),Optional.empty());
        }
        var prepared=(PlanValidator.Prepared)preparation;
        var state=new RunState(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new HashSet<>(),new ResultSourceDepsRecorder(),new HashMap<>(),callBudget,traces);
        var rejected=runSteps(job,plan,prepared,plan.steps(),resolver,state);
        if(rejected.isPresent())return rejected.get();
        var ui=new ArrayList<>(state.ui());
        if(plan.definition().answerSource()==WorkflowDefinition.AnswerSource.REGISTRY_VIEW)ui.addAll(help.build(registry.view(job.scope()),job.scope().perm()));
        return new WorkflowOutcome(state.stages(),ui,state.evidence(),state.deps().snapshot(),traces,state.refs(),Optional.empty());
    }
    /** 한 Job 실행의 누적 상태. 1차 단계와 보완 round 단계가 같은 기록·의존성·바인딩·호출 한도를 공유한다. */
    private record RunState(List<WorkflowOutcome.Stage> stages,List<ResultBlock> ui,List<Evidence> evidence,List<String> refs,Set<String> actualIds,
            ResultSourceDepsRecorder deps,Map<String,StepBinding> bindings,ToolCallBudget calls,List<WorkflowOutcome.Trace> traces) {}
    /** 단계를 순서대로 실행한다. 입력 검증 실패면 그 시점까지의 결과와 거절을 담은 Outcome 을 돌려준다. */
    private Optional<WorkflowOutcome> runSteps(JobContext job,WorkflowExecutionPlan plan,PlanValidator.Prepared prepared,List<WorkflowExecutionPlan.ExecutionStep> executions,WorkflowResourceResolver resolver,RunState state) {
        var stages=state.stages();var ui=state.ui();var evidence=state.evidence();var refs=state.refs();var actualIds=state.actualIds();var deps=state.deps();var bindings=state.bindings();var calls=state.calls();var traces=state.traces();
        for(var execution:executions) {
            var definition=execution.definition();
            PlanStep step=execution.toolStep();
            var manifest=prepared.manifest(step.stepId());
            var invalid=new InputValidator().validate(manifest,step.arguments(),resolver.timezone(job.scope()));
            if(invalid!=null) {
                ErrorCode code=invalid.reason()==RejectReason.INPUT_BUDGET_EXCEEDED?ErrorCode.INPUT_BUDGET_EXCEEDED:ErrorCode.PLAN_INVALID;
                return Optional.of(new WorkflowOutcome(stages,ui,evidence,deps.snapshot(),traces,refs,Optional.of(new ValidationOutcome(false,List.of(),invalid.reason(),ResultStatus.FAILED,code,new Clarification(code,invalid.fields())))));
            }
            var inputs=new ToolResultCache.CacheInputs(Optional.empty(),Optional.empty(),manifest.sourceFreshnessPolicy());
            ToolResultEnvelope result=null;boolean hit=false;
            boolean cacheable=definition.cacheable()&&(manifest.toolKind()!=ToolKind.GIT||inputs.sourceRevision().isPresent());
            if(cacheable) {
                expiry.assertLive(job.cancellation());var attempt=prepared.newAttempt(step,job.scope(),StepAttempt.Purpose.CACHE_LOOKUP);
                var decision=authorization.authorizeStep(attempt,job.scope(),resolver.delegatedCall());
                if(decision instanceof SourceAuthorizationStep.StepGrant grant) {
                    var cached=cache.find(prepared,attempt,grant,job,inputs);if(cached.isPresent()){result=SchemaContracts.JSON.treeToValue(cached.get().value(),ToolResultEnvelope.class);deps.inherit(cached.get().sourceDeps());hit=true;}
                } else {result=failure(manifest,decision instanceof SourceAuthorizationStep.StepUnavailable?ResultStatus.SOURCE_UNAVAILABLE:ResultStatus.DENIED);recorder.blocked(job,manifest.capabilityId(),result.status(),Optional.empty());}
            }
            if(result==null) {
                // 실제 dispatcher 직전 Job Tool 호출 한도(SPEC §28.8: 전체 12·같은 Tool 3·Knowledge search 2).
                if(!calls.tryAcquire(manifest.capabilityId(),manifest.toolKind())){traces.add(new WorkflowOutcome.Trace(definition.stepId(),WorkflowOutcome.TraceOutcome.TOOL_LIMIT_REACHED));continue;}
                expiry.assertLive(job.cancellation());var attempt=prepared.newAttempt(step,job.scope(),StepAttempt.Purpose.DISPATCH);
                ToolDispatcher.InvocationOutcome invoked;var trace=new java.util.concurrent.atomic.AtomicReference<ExecutionContext.DispatchTrace>();
                try {
                    if(attempt.mode()==StepAttempt.Mode.BACKEND_READ_REQUIRED)invoked=dispatcher.dispatchBusiness(prepared,attempt,job.scope(),new ExecutionContext(resolver,trace::set));
                    else {
                        var decision=authorization.authorizeStep(attempt,job.scope(),resolver.delegatedCall());
                        if(!(decision instanceof SourceAuthorizationStep.StepGrant grant)){result=failure(manifest,decision instanceof SourceAuthorizationStep.StepUnavailable?ResultStatus.SOURCE_UNAVAILABLE:ResultStatus.DENIED);invoked=null;}
                        else {expiry.assertLive(job.cancellation());invoked=dispatcher.dispatchCurrent(prepared,attempt,job.scope(),grant,executionContext(job,plan,prepared,attempt,resolver,bindings,trace::set));}
                    }
                } catch(WorkflowResourceResolver.ReadFailure failure){result=failure(manifest,failure.status());invoked=null;}
                if(invoked==null)recorder.blocked(job,manifest.capabilityId(),result.status(),Optional.empty());
                if(invoked instanceof ToolDispatcher.Threw threw) {
                    try{recorder.record(job,threw.invocation());}catch(RuntimeException|Error storage){threw.suppress(storage);}threw.rethrow();
                }
                if(invoked instanceof ToolDispatcher.Returned returned) {
                    returned.invocation().ifPresentOrElse(record->{if(!actualIds.add(record.id()))throw new IllegalStateException("실행 식별자가 중복되었습니다");recorder.record(job,record);refs.add(record.id());},
                        ()->recorder.blocked(job,manifest.capabilityId(),returned.result().status(),Optional.ofNullable(trace.get())));
                    result=returned.result();
                }
            }
            if(result==null)throw new IllegalStateException("단계 결과가 없습니다");
            boolean observed=result.observedAt()!=null&&Set.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT).contains(result.status());
            int count=0;for(var block:result.blocks())if(block.content()!=null&&block.content().path("items").isArray())count+=block.content().get("items").size();
            stages.add(new WorkflowOutcome.Stage(step.stepId(),result,observed,count));
            ui.addAll(blocks.build(manifest,result));evidence.addAll(result.evidence());
            if(observed)deps.record(manifest.sourceAllowlistRef(),manifest.requiredPermission());
            var hits=com.nori.ai.gateway.core.evidence.EvidenceAssembler.projectKnowledge(manifest,result,job.scope());
            if(!hits.isEmpty())deps.inherit(new ResultSourceDeps(List.of(),hits.stream().map(com.nori.ai.gateway.core.evidence.EvidenceAssembler.KnowledgeHit::document).distinct().toList()));
            if(result.dataRef()!=null&&!result.blocks().isEmpty())bindings.put(step.stepId(),new StepBinding(job.scope().boundSystem(),result.dataRef(),result.blocks().getFirst().content()));
            if(cacheable&&!hit&&observed) {
                expiry.assertLive(job.cancellation());var attempt=prepared.newAttempt(step,job.scope(),StepAttempt.Purpose.CACHE_PUT);
                var decision=authorization.authorizeStep(attempt,job.scope(),resolver.delegatedCall());
                if(decision instanceof SourceAuthorizationStep.StepGrant grant)cache.put(prepared,attempt,grant,job,inputs,new ToolResultCache.CachedValue(manifest.capabilityId(),SchemaContracts.JSON.valueToTree(result),new ResultSourceDeps(List.of(new ResultSourceDeps.Source(manifest.sourceAllowlistRef(),Set.copyOf(manifest.requiredPermission()))),List.of()),clock.instant().plusSeconds(60)));
            }
        }
        return Optional.empty();
    }
    private ResultSourceDeps emptyDeps(){return new ResultSourceDeps(List.of(),List.of());}
    private ExecutionContext executionContext(JobContext job,WorkflowExecutionPlan plan,PlanValidator.Prepared prepared,StepAttempt attempt,WorkflowResourceResolver resolver,Map<String,StepBinding> bindings,java.util.function.Consumer<ExecutionContext.DispatchTrace> trace) {
        var kind=prepared.manifest(attempt.binding().stepId()).toolKind();
        if(kind!=ToolKind.GIT)return new ExecutionContext(resolver,trace);
        var step=prepared.plan().plan().stream().filter(value->value.stepId().equals(attempt.binding().stepId())).findFirst().orElseThrow();var ancestors=new HashSet<String>();
        collectToolAncestors(step,prepared.plan(),ancestors);var selected=new LinkedHashMap<String,StepBinding>();
        for(var candidate:prepared.plan().plan())if(ancestors.contains(candidate.stepId())&&prepared.manifest(candidate.stepId()).toolKind()==ToolKind.TRACE&&bindings.containsKey(candidate.stepId()))selected.put(candidate.stepId(),bindings.get(candidate.stepId()));
        return new ExecutionContext(resolver,trace,Optional.of(new ExecutionContext.BoundInputs(attempt,selected)));
    }
    private void collectToolAncestors(PlanStep step,PlanEnvelope plan,Set<String> selected) {
        for(String id:step.dependsOn())if(selected.add(id))collectToolAncestors(plan.plan().stream().filter(value->value.stepId().equals(id)).findFirst().orElseThrow(),plan,selected);
    }
    private ToolResultEnvelope failure(CapabilityManifest manifest,ResultStatus status){return new ToolResultEnvelope(null,manifest.capabilityId(),manifest.contractVersion(),manifest.schemaDigest(),manifest.ownerSystem(),status,null,List.of(),null,null,List.of(),List.of(),null,null,null,null,List.of(),List.of(),List.of(),List.of(),List.of());}
}
