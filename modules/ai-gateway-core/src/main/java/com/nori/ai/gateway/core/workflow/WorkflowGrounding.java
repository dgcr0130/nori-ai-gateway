package com.nori.ai.gateway.core.workflow;

import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.plan.ResourceRef;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import java.time.Clock;
import java.util.*;
import com.nori.ai.gateway.core.llm.*;

/** 분류를 다시 부르지 않고 규칙 검색·재선택·작성 허용을 결정합니다. */
public final class WorkflowGrounding {
    public record SourceUnits(List<EvidenceUnit> units,Set<String> invalidSteps) {
        public SourceUnits {units=List.copyOf(units);invalidSteps=Set.copyOf(invalidSteps);}
    }
    public interface Inputs {
        SourceUnits collect(JobContext job,WorkflowOutcome outcome);
        Map<String,String> glossary(JobContext job);
        default List<String> permissions(JobContext job,String capabilityId){return List.of();}
        default List<CitedHistoryContext.Pair> history(JobContext job){return List.of();}
    }
    public record Prepared(WorkflowOutcome outcome,EvidenceSelection.Selected selected,boolean sufficient,Optional<ClassificationService.HistoryPair> history) {
        public Prepared(WorkflowOutcome outcome,EvidenceSelection.Selected selected,boolean sufficient){this(outcome,selected,sufficient,Optional.empty());}
    }
    @FunctionalInterface public interface TransmissionBudget {boolean fits(JobContext job,List<EvidenceUnit> units,Optional<ClassificationService.HistoryPair> history);}
    private final TransmissionBudget transmission;
    private final DocumentSearch search;private final SearchLanguage language;private final SearchSettings settings;
    private final EvidenceSelection selection;private final Inputs inputs;private final Clock clock;private final ExpiryCheckpoint expiry;
    private final EvidenceCoverageEvaluator coverage=new EvidenceCoverageEvaluator();
    public WorkflowGrounding(DocumentSearch search,SearchLanguage language,SearchSettings settings,EvidenceSelection selection,Inputs inputs,Clock clock,ExpiryCheckpoint expiry) {
        this(search,language,settings,selection,inputs,clock,expiry,(job,units,history)->true);
    }
    public WorkflowGrounding(DocumentSearch search,SearchLanguage language,SearchSettings settings,EvidenceSelection selection,Inputs inputs,Clock clock,ExpiryCheckpoint expiry,TransmissionBudget transmission) {
        this.search=search;this.language=language;this.settings=settings;this.selection=selection;this.inputs=inputs;this.clock=clock;this.expiry=expiry;
        this.transmission=transmission;
    }
    public List<String> permissions(JobContext job,String capabilityId){return inputs.permissions(job,capabilityId);}
    public Prepared prepare(JobContext job,WorkflowExecutionPlan plan,WorkflowOutcome outcome,ToolCallBudget calls) {
        var source=inputs.collect(job,outcome);var stages=new ArrayList<>(outcome.stages());
        for(int i=0;i<stages.size();i++)if(source.invalidSteps().contains(stages.get(i).stepId()))stages.set(i,failure(stages.get(i),job));
        var targets=SchemaContracts.JSON.convertValue(job.classification().getOrDefault("targets",List.of()),new tools.jackson.core.type.TypeReference<List<ResourceRef>>(){}).stream().map(ResourceRef::ref).toList();
        var rounds=new ArrayList<DocumentSearch.Round>();var traces=new ArrayList<>(outcome.trace());
        var history=currentHistory(job);
        var selected=select(job,rounds,source.units(),history);boolean sufficient=false;
        if(plan.documents().required()) {
            for(int round=0;round<=settings.retryCount();round++) {
                if(round>0&&!calls.tryStartSupplementRound())break;
                if(!calls.tryAcquireDocumentSearch()){traces.add(new WorkflowOutcome.Trace("documents.search",WorkflowOutcome.TraceOutcome.TOOL_LIMIT_REACHED));break;}
                expiry.assertLive(job.cancellation());
                try {
                    String query=round==0?plan.documents().searchQuery():plan.documents().retryQuery();
                    if(round>0&&query.equals(plan.documents().searchQuery())) {
                        var glossary=inputs.glossary(job);query=glossary.isEmpty()?job.requestInput().maskedQuestion():query+" "+String.join(" ",new TreeMap<>(glossary).values());
                    }
                    var bound=CitedHistoryContext.reference(job);if(bound.isPresent())query=CitedHistoryContext.retrievalQuery(query,bound.get(),history);
                    var debug=System.getLogger(WorkflowGrounding.class.getName());
                    if(debug.isLoggable(System.Logger.Level.DEBUG))debug.log(System.Logger.Level.DEBUG,
                        "GROUNDING_SEARCH job={0} round={1} querySha={2}",job.jobId(),round,AnswerSnapshot.sha(query));
                    var found=search.search(job.scope(),query,job.requestInput().maskedQuestion(),targets,settings);expiry.assertLive(job.cancellation());
                    var verifiedHistory=currentHistory(job);
                    var verified=found.verified().stream().filter(answer->{
                        var candidate=select(job,List.of(new DocumentSearch.Round(List.of(),List.of(),List.of(),List.of(answer))),List.of(),verifiedHistory);
                        return sufficient(job,targets,candidate.units(),verifiedHistory);
                    }).toList();
                    found=new DocumentSearch.Round(found.semantic(),found.keyword(),found.identifier(),verified);rounds.add(found);
                    var collected=new HashSet<UUID>();for(var hit:RankFusion.fuse(List.of(found),settings.rrfK()))collected.add(hit.hit().child().id());
                    for(var answer:verified)for(var hit:answer.parents())collected.add(hit.child().id());
                    int count=collected.size();stages.add(documentStage(job,round,count==0?ResultStatus.EMPTY:ResultStatus.SUCCESS,count));
                }catch(InferenceClient.EmbeddingFailed unavailable) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(WorkflowGrounding.class.getName()),"WORKFLOW_GROUNDING_FAILED",unavailable);
                    stages.add(documentStage(job,round,ResultStatus.SOURCE_UNAVAILABLE,0));break;
                }
                history=currentHistory(job);selected=select(job,rounds,source.units(),history);
                sufficient=sufficient(job,targets,selected.units(),history);
                if(sufficient)break;
            }
        } else sufficient=sufficient(job,targets,selected.units(),history);
        // 실패 뒤에도 이전 회차의 선택과 자료 상태를 보존합니다.
        history=currentHistory(job);selected=select(job,rounds,source.units(),history);sufficient=sufficient(job,targets,selected.units(),history);
        // 스냅샷의 실제 선택 본문과 결속할 수 있는 해시/개수만 남기며 검색식·대상·근거 원문은 로깅하지 않습니다.
        var diagnostic=System.getLogger(WorkflowGrounding.class.getName());
        if(diagnostic.isLoggable(System.Logger.Level.DEBUG))diagnostic.log(System.Logger.Level.DEBUG,
            "GROUNDING_DIAGNOSTIC job={0} planQuerySha={1} planRetrySha={2} targetCount={3} targetsSha={4} selectedBodyShas={5} rounds={6} sufficient={7}",
            job.jobId(),AnswerSnapshot.sha(plan.documents().searchQuery()),AnswerSnapshot.sha(plan.documents().retryQuery()),targets.size(),
            AnswerSnapshot.sha(SchemaContracts.JSON.writeValueAsString(targets)),selected.units().stream().map(u->AnswerSnapshot.sha(u.body())).toList(),rounds.size(),sufficient);
        var docs=new LinkedHashSet<>(outcome.sourceDeps().knowledgeDocuments());
        for(var unit:selected.units())if(unit.sourceType().equals("DOCUMENT"))docs.add(DocumentReferences.key(job.scope().boundSystem(),SchemaContracts.JSON.treeToValue(unit.location(),DocumentLocation.class)));
        var sources=new ArrayList<>(outcome.sourceDeps().sources());
        // 기존 Source 안의 ANY 계약을 바꾸지 않고 각 선택 항목의 필수 권한을 모두 요구합니다.
        for(int i=0;i<selected.units().size();i++)for(String permission:selected.units().get(i).permissions())
            sources.add(new ResultSourceDeps.Source("answer:"+(i+1)+":"+permission,Set.of(permission)));
        var deps=new ResultSourceDeps(sources,List.copyOf(docs));
        var invalidRefs=new HashSet<com.nori.ai.gateway.core.scope.OwnedReference>();var invalidEvidence=new HashSet<String>();
        for(var stage:outcome.stages())if(source.invalidSteps().contains(stage.stepId())) {
            for(var block:stage.result().blocks())invalidRefs.add(block.dataRef());for(var value:stage.result().evidence())invalidEvidence.add(value.evidenceId());
        }
        var blocks=outcome.blocks().stream().filter(b->!invalidRefs.contains(b.dataRef())).toList();
        var evidence=outcome.evidence().stream().filter(e->!invalidEvidence.contains(e.evidenceId())).toList();
        return new Prepared(new WorkflowOutcome(stages,blocks,evidence,deps,traces,outcome.toolExecutionRefs(),outcome.rejection(),outcome.coverage()),selected,sufficient,matchedHistory(job,targets,selected.units(),history));
    }
    private List<CitedHistoryContext.Pair> currentHistory(JobContext job){return CitedHistoryContext.reference(job).isEmpty()?List.of():inputs.history(job);}
    private List<String> targets(JobContext job){return SchemaContracts.JSON.convertValue(job.classification().getOrDefault("targets",List.of()),new tools.jackson.core.type.TypeReference<List<ResourceRef>>(){}).stream().map(ResourceRef::ref).toList();}
    private Optional<ClassificationService.HistoryPair> matchedHistory(JobContext job,List<String> targets,List<EvidenceUnit> units,List<CitedHistoryContext.Pair> available) {
        return CitedHistoryContext.reference(job).flatMap(bound->CitedHistoryContext.composerHistory(bound,job.requestInput().maskedQuestion(),targets,available,units,job.scope().perm()));
    }
    private boolean sufficient(JobContext job,List<String> targets,List<EvidenceUnit> units,List<CitedHistoryContext.Pair> available) {
        return coverage.sufficient(job.requestInput().maskedQuestion(),targets,units,language,settings.coreTermLimit(),inputs.glossary(job))||matchedHistory(job,targets,units,available).isPresent();
    }
    private boolean fits(JobContext job,List<EvidenceUnit> units,List<CitedHistoryContext.Pair> available) {
        try {
            var history=matchedHistory(job,targets(job),units,available);var selector=EvidenceCandidateSelection.prepare(units);
            return selection.fitsTransmission(selector.transmittedData(job.requestInput().maskedQuestion(),units,language,history),selector.output().schema())&&transmission.fits(job,units,history);
        }catch(IllegalArgumentException invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(WorkflowGrounding.class.getName()),"WORKFLOW_GROUNDING_FAILED",invalid);return false;}
    }
    private EvidenceSelection.Selected select(JobContext job,List<DocumentSearch.Round> rounds,List<EvidenceUnit> sources,List<CitedHistoryContext.Pair> available) {
        return selection.select(rounds,sources,settings,units->fits(job,units,available));
    }
    /** 검색 뒤 작성 직전의 현재 접근을 다시 확인하며 과거 답변을 근거로 사용하지 않습니다. */
    public Prepared refresh(JobContext job,Prepared prepared) {
        var available=currentHistory(job);var targets=targets(job);var history=matchedHistory(job,targets,prepared.selected().units(),available);
        boolean sufficient=sufficient(job,targets,prepared.selected().units(),available)&&fits(job,prepared.selected().units(),available);
        var debug=System.getLogger(WorkflowGrounding.class.getName());
        if(debug.isLoggable(System.Logger.Level.DEBUG))debug.log(System.Logger.Level.DEBUG,"GROUNDING_HISTORY job={0} sourceJob={1} citationSha={2} matched={3} sufficient={4}",job.jobId(),
            CitedHistoryContext.reference(job).map(ref->ref.pair().sourceJobId()).orElse("NONE"),CitedHistoryContext.reference(job).map(ref->AnswerSnapshot.sha(SchemaContracts.JSON.writeValueAsString(ref.pair().citations()))).orElse("NONE"),history.isPresent(),sufficient);
        return new Prepared(prepared.outcome(),prepared.selected(),sufficient,history);
    }
    private WorkflowOutcome.Stage documentStage(JobContext job,int round,ResultStatus status,int count) {
        boolean observed=status==ResultStatus.SUCCESS||status==ResultStatus.EMPTY;
        var result=new ToolResultEnvelope(null,"documents.search",null,null,job.scope().boundSystem(),status,null,List.of(),observed?clock.instant():null,null,List.of(),List.of(),null,null,null,null,List.of(),List.of(),List.of(),List.of(),List.of());
        return new WorkflowOutcome.Stage("documents-"+(round+1),result,observed,count);
    }
    private WorkflowOutcome.Stage failure(WorkflowOutcome.Stage stage,JobContext job) {
        var r=stage.result();var result=new ToolResultEnvelope(r.toolExecutionId(),r.capabilityId(),r.contractVersion(),r.schemaDigest(),job.scope().boundSystem(),ResultStatus.FAILED,null,List.of(),null,null,r.missingFields(),r.warnings(),ErrorCode.CONTRACT_INVALID,r.traceId(),r.metadata(),null,List.of(),List.of(),List.of(),List.of(),List.of());
        return new WorkflowOutcome.Stage(stage.stepId(),result,false,0);
    }
}
