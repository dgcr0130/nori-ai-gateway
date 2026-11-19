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

/** 분류를 다시 부르지 않고 규칙 검색·재선택·작성 허용을 결정합니다. */
public final class WorkflowGrounding {
    public record SourceUnits(List<EvidenceUnit> units,Set<String> invalidSteps) {
        public SourceUnits {units=List.copyOf(units);invalidSteps=Set.copyOf(invalidSteps);}
    }
    public interface Inputs {
        SourceUnits collect(JobContext job,WorkflowOutcome outcome);
        Map<String,String> glossary(JobContext job);
    }
    public record Prepared(WorkflowOutcome outcome,EvidenceSelection.Selected selected,boolean sufficient) {}
    private final DocumentSearch search;private final SearchLanguage language;private final SearchSettings settings;
    private final EvidenceSelection selection;private final Inputs inputs;private final Clock clock;private final ExpiryCheckpoint expiry;
    private final EvidenceCoverageEvaluator coverage=new EvidenceCoverageEvaluator();
    public WorkflowGrounding(DocumentSearch search,SearchLanguage language,SearchSettings settings,EvidenceSelection selection,Inputs inputs,Clock clock,ExpiryCheckpoint expiry) {
        this.search=search;this.language=language;this.settings=settings;this.selection=selection;this.inputs=inputs;this.clock=clock;this.expiry=expiry;
    }
    public Prepared prepare(JobContext job,WorkflowExecutionPlan plan,WorkflowOutcome outcome,ToolCallBudget calls) {
        var source=inputs.collect(job,outcome);var stages=new ArrayList<>(outcome.stages());
        for(int i=0;i<stages.size();i++)if(source.invalidSteps().contains(stages.get(i).stepId()))stages.set(i,failure(stages.get(i),job));
        var targets=SchemaContracts.JSON.convertValue(job.classification().getOrDefault("targets",List.of()),new tools.jackson.core.type.TypeReference<List<ResourceRef>>(){}).stream().map(ResourceRef::ref).toList();
        var rounds=new ArrayList<DocumentSearch.Round>();var traces=new ArrayList<>(outcome.trace());
        var selected=selection.select(rounds,source.units(),settings);boolean sufficient=false;
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
                    var found=search.search(job.scope(),query,job.requestInput().maskedQuestion(),targets,settings);expiry.assertLive(job.cancellation());
                    var verified=found.verified().stream().filter(answer->{
                        var candidate=selection.select(List.of(new DocumentSearch.Round(List.of(),List.of(),List.of(),List.of(answer))),List.of(),settings);
                        return coverage.sufficient(job.requestInput().maskedQuestion(),targets,candidate.units(),language,settings.coreTermLimit(),inputs.glossary(job));
                    }).toList();
                    found=new DocumentSearch.Round(found.semantic(),found.keyword(),found.identifier(),verified);rounds.add(found);
                    var collected=new HashSet<UUID>();for(var hit:RankFusion.fuse(List.of(found),settings.rrfK()))collected.add(hit.hit().child().id());
                    for(var answer:verified)for(var hit:answer.parents())collected.add(hit.child().id());
                    int count=collected.size();stages.add(documentStage(job,round,count==0?ResultStatus.EMPTY:ResultStatus.SUCCESS,count));
                }catch(InferenceClient.EmbeddingFailed unavailable) {
                    stages.add(documentStage(job,round,ResultStatus.SOURCE_UNAVAILABLE,0));break;
                }
                selected=selection.select(rounds,source.units(),settings);
                sufficient=coverage.sufficient(job.requestInput().maskedQuestion(),targets,selected.units(),language,settings.coreTermLimit(),inputs.glossary(job));
                if(sufficient)break;
            }
        } else sufficient=coverage.sufficient(job.requestInput().maskedQuestion(),targets,selected.units(),language,settings.coreTermLimit(),inputs.glossary(job));
        // 실패 뒤에도 이전 회차의 선택과 자료 상태를 보존합니다.
        sufficient=coverage.sufficient(job.requestInput().maskedQuestion(),targets,selected.units(),language,settings.coreTermLimit(),inputs.glossary(job));
        var docs=new LinkedHashSet<>(outcome.sourceDeps().knowledgeDocuments());
        for(var unit:selected.units())if(unit.sourceType().equals("DOCUMENT"))docs.add(DocumentReferences.key(job.scope().boundSystem(),SchemaContracts.JSON.treeToValue(unit.location(),DocumentLocation.class)));
        var deps=new ResultSourceDeps(outcome.sourceDeps().sources(),List.copyOf(docs));
        var invalidRefs=new HashSet<com.nori.ai.gateway.core.scope.OwnedReference>();var invalidEvidence=new HashSet<String>();
        for(var stage:outcome.stages())if(source.invalidSteps().contains(stage.stepId())) {
            for(var block:stage.result().blocks())invalidRefs.add(block.dataRef());for(var value:stage.result().evidence())invalidEvidence.add(value.evidenceId());
        }
        var blocks=outcome.blocks().stream().filter(b->!invalidRefs.contains(b.dataRef())).toList();
        var evidence=outcome.evidence().stream().filter(e->!invalidEvidence.contains(e.evidenceId())).toList();
        return new Prepared(new WorkflowOutcome(stages,blocks,evidence,deps,traces,outcome.toolExecutionRefs(),outcome.rejection(),outcome.coverage()),selected,sufficient);
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
