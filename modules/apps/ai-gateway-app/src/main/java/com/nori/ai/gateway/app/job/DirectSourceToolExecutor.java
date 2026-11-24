package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.integration.source.*;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.retention.TimeWindow;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.integration.loki.*;
import com.nori.ai.gateway.integration.tempo.TempoTraceConnector;
import com.nori.ai.gateway.integration.gitlab.*;
import org.springframework.beans.factory.ObjectProvider;
import java.time.Clock;
import java.util.*;

/** 생산 Source는 소유 phase가 등록하며 부재를 성공 자료로 바꾸지 않습니다. */
public final class DirectSourceToolExecutor implements ToolExecutor {
    public record GitExecutionScopes(List<GitLabSourcePolicy.Repository> repositories,List<GitLabSourcePolicy.LineageEntry> deployLineage) {
        public GitExecutionScopes {repositories=List.copyOf(repositories);deployLineage=List.copyOf(deployLineage);}
    }
    private final com.nori.ai.gateway.app.config.GatewaySystemsProperties systems;
    private final ToolKind kind;private final ObjectProvider<LokiLogConnector> logs;private final ObjectProvider<TempoTraceConnector> traces;
    private final ObjectProvider<GitLabCodeConnector> code;private final ObjectProvider<GitExecutionScopes> scopes;private final Clock clock;private final LokiProperties properties;
    private final DeploymentEvidenceResolver deployments;
    private final Optional<com.nori.ai.gateway.app.hooks.SourceRequestProbe> probe;
    public DirectSourceToolExecutor(ToolKind kind,ObjectProvider<LokiLogConnector> logs,ObjectProvider<TempoTraceConnector> traces,ObjectProvider<GitLabCodeConnector> code,ObjectProvider<GitExecutionScopes> scopes,Clock clock,LokiProperties properties,DeploymentEvidenceResolver deployments) {
        this(kind,logs,traces,code,scopes,clock,properties,deployments,Optional.empty(),new com.nori.ai.gateway.app.config.GatewaySystemsProperties(Map.of()));
    }
    public DirectSourceToolExecutor(ToolKind kind,ObjectProvider<LokiLogConnector> logs,ObjectProvider<TempoTraceConnector> traces,ObjectProvider<GitLabCodeConnector> code,ObjectProvider<GitExecutionScopes> scopes,Clock clock,LokiProperties properties,DeploymentEvidenceResolver deployments,Optional<com.nori.ai.gateway.app.hooks.SourceRequestProbe> probe,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems) {
        if(!Set.of(ToolKind.LOG,ToolKind.TRACE,ToolKind.GIT).contains(kind))throw new IllegalArgumentException("직결 Source 종류가 잘못되었습니다");
        this.kind=kind;this.logs=logs;this.traces=traces;this.code=code;this.scopes=scopes;this.clock=clock;this.properties=properties;
        this.deployments=deployments;
        this.probe=Objects.requireNonNull(probe);this.systems=Objects.requireNonNull(systems);
    }
    @Override public ToolKind toolKind(){return kind;}
    @Override public ToolResultEnvelope execute(PlanStep step,CapabilityManifest manifest,VerifiedSystemScope scope,ExecutionContext context) {
        try(var capture=probe.map(value->value.open(scope.requestId(),manifest.capabilityId(),scope.boundSystem())).orElse(()->{})){return executeRead(step,manifest,scope,context);}
    }
    private ToolResultEnvelope executeRead(PlanStep step,CapabilityManifest manifest,VerifiedSystemScope scope,ExecutionContext context) {
        if(!(context.resolver() instanceof WorkflowResourceResolver resolver))return unavailable(manifest,scope);
        resolver.beforeRead();var system=systems.systems().get(scope.boundSystem().name());
        if(system==null)return unavailable(manifest,scope);
        var locations=new SystemSourceScope(system.apps().stream().map(value->new SystemSourceScope.App(value.name(),value.namespace(),value.requiredPermissions())).toList(),system.traces().stream().map(value->new SystemSourceScope.Trace(value.service(),value.namespace(),value.requiredPermissions())).toList(),system.repositories().stream().map(value->new SystemSourceScope.Repository(value.projectId(),value.ref(),value.pathPrefix(),value.requiredPermissions())).toList());
        var request=new SourceReadAccess.Request(scope,resolver.delegatedCall(),manifest.sourceAllowlistRef(),resolver::beforeRead,locations);
        return switch(kind) {
            case LOG -> logs(step,manifest,scope,resolver,request);
            case TRACE -> traces(step,manifest,scope,resolver,request);
            case GIT -> code(step,manifest,scope,resolver,request,context);
            default -> throw new IllegalStateException("직결 Source 종류가 잘못되었습니다");
        };
    }
    private TimeWindow window(PlanStep step,WorkflowResourceResolver resolver,VerifiedSystemScope scope) {
        var parser=new InputValidator();return new TimeWindow(parser.parseTime(step.arguments().path("from").asString(),resolver.timezone(scope)),parser.parseTime(step.arguments().path("to").asString(),resolver.timezone(scope)));
    }
    private ToolResultEnvelope logs(PlanStep step,CapabilityManifest manifest,VerifiedSystemScope scope,WorkflowResourceResolver resolver,SourceReadAccess.Request request) {
        var connector=logs.getIfAvailable();if(connector==null)return unavailable(manifest,scope);
        var args=step.arguments();List<String> terms=new ArrayList<>();if(args.path("targetRef").isString())terms.add(args.path("targetRef").asString());if(args.path("q").isString())terms.add(args.get("q").asString());
        var result=connector.search(request,new LokiLogConnector.LogQuery(window(step,resolver,scope),terms,properties.maxLinesTotal()));
        return ToolResults.result(manifest,scope,result.status(),result.items(),clock.instant(),List.of(),result.warnings(),result.missingRanges().stream().map(value->new ToolResultMetadata.MissingRange(value.startAt(),value.endAt())).toList(),null);
    }
    private ToolResultEnvelope traces(PlanStep step,CapabilityManifest manifest,VerifiedSystemScope scope,WorkflowResourceResolver resolver,SourceReadAccess.Request request) {
        var connector=traces.getIfAvailable();if(connector==null)return unavailable(manifest,scope);
        List<TempoTraceConnector.Trace> values=new ArrayList<>();Set<String> ids=new HashSet<>();List<String> missing=new ArrayList<>();Set<Warning> warnings=new HashSet<>();int observed=0;
        for(var location:request.locations().traces()) {
            String service=location.service();
            resolver.beforeRead();var result=connector.search(request,service,location.namespace(),window(step,resolver,scope));
            if(result.status()==ResultStatus.DENIED)return ToolResults.result(manifest,scope,ResultStatus.DENIED,List.of(),clock.instant(),List.of(),Set.of(),List.of(),null);
            if(result.status()==ResultStatus.SOURCE_UNAVAILABLE){missing.add(service);continue;}
            observed++;warnings.addAll(result.warnings());for(var trace:result.traces())if(ids.add(trace.traceId()))values.add(trace);
        }
        var status=observed==0?ResultStatus.SOURCE_UNAVAILABLE:!missing.isEmpty()?ResultStatus.PARTIAL:values.isEmpty()?ResultStatus.EMPTY:ResultStatus.SUCCESS;
        return ToolResults.result(manifest,scope,status,values,clock.instant(),missing,warnings,List.of(),null);
    }
    private record Provenance(String traceId,String spanId,String service,String serviceVersion) {}
    private ToolResultEnvelope code(PlanStep step,CapabilityManifest manifest,VerifiedSystemScope scope,WorkflowResourceResolver resolver,SourceReadAccess.Request request,ExecutionContext context) {
        var connector=code.getIfAvailable();var approved=scopes.getIfAvailable();if(connector==null||approved==null||approved.repositories().isEmpty())return unavailable(manifest,scope);
        var bound=context.boundInputs().orElseThrow(()->new IllegalArgumentException("배포 근거의 단계 결속이 없습니다"));
        List<Optional<Provenance>> provenance=new ArrayList<>();
        for(var binding:bound.bindings().values()) {
            var local=new ArrayList<Provenance>();for(var trace:binding.value().path("items"))for(var span:trace.path("spans"))local.add(new Provenance(text(trace,"traceId"),text(span,"spanId"),text(span,"service"),text(span,"serviceVersion")));
            local.sort(Comparator.comparing((Provenance value)->Objects.toString(value.service(),"")).thenComparing(value->Objects.toString(value.serviceVersion(),"")).thenComparing(value->Objects.toString(value.traceId(),"")).thenComparing(value->Objects.toString(value.spanId(),"")));
            local.forEach(value->provenance.add(Optional.of(value)));
        }
        if(provenance.isEmpty())provenance.add(Optional.empty());
        String query=step.arguments().path("q").isString()?step.arguments().path("q").asString():step.arguments().path("targetRef").asString("");List<Map<String,Object>> items=new ArrayList<>();List<String> missing=new ArrayList<>();List<Evidence> deploymentEvidence=new ArrayList<>();int observed=0;
        Map<String,List<DeploymentEvidenceResolver.Resolution>> resolved=new HashMap<>();
        for(var origin:provenance) {
            List<DeploymentEvidenceResolver.Resolution> resolutions;
            if(origin.isPresent()&&origin.get().service()!=null&&request.locations().traces().stream().anyMatch(value->value.service().equals(origin.get().service()))) {
                var value=origin.get();String key=value.service()+"\u0000"+value.serviceVersion();
                resolver.beforeRead();resolutions=resolved.computeIfAbsent(key,ignored->deployments.resolve(request,value.service(),value.serviceVersion(),approved.deployLineage()));
            } else resolutions=List.of();
            var lineages=resolutions.stream().flatMap(value->value.lineage().stream()).toList();
            if(lineages.stream().anyMatch(value->value.reason()==GitLabPipelineLineageReader.Reason.DENIED))return ToolResults.result(manifest,scope,ResultStatus.DENIED,List.of(),clock.instant(),List.of(),Set.of(),List.of(),null);
            if(lineages.stream().anyMatch(value->value.reason()==GitLabPipelineLineageReader.Reason.SOURCE_UNAVAILABLE))return unavailable(manifest,scope);
            for(var location:request.locations().repositories()) {
                if(!scope.perm().containsAll(location.requiredPermissions()))return ToolResults.result(manifest,scope,ResultStatus.DENIED,List.of(),clock.instant(),List.of(),Set.of(),List.of(),null);
                var approvedRepository=approved.repositories().stream().filter(value->value.projectId().equals(location.projectId())).findFirst();
                if(approvedRepository.isEmpty())return ToolResults.result(manifest,scope,ResultStatus.DENIED,List.of(),clock.instant(),List.of(),Set.of(),List.of(),null);
                var repository=new GitLabSourcePolicy.Repository(location.projectId(),approvedRepository.get().sourceRepo(),Set.of(location.ref()),Set.of(location.pathPrefix()),"SYSTEM_CONFIG");
                var lineage=resolutions.stream().filter(value->value.codeRefKind().equals("DEPLOYED_SOURCE_COMMIT"))
                    .flatMap(value->value.lineage().stream()).filter(value->value.status()==GitLabPipelineLineageReader.Status.VERIFIED&&value.sourceRepo().equals(repository.sourceRepo())).findFirst();
                List<String> refs=lineage.isPresent()?List.of(lineage.get().commitSha()):repository.refs().stream().sorted().toList();
                for(String ref:refs)for(String prefix:repository.pathPrefixes().stream().sorted().toList()) {
            resolver.beforeRead();var result=connector.searchCode(request,query,new GitLabCodeConnector.CodeScope(repository.sourceRepo(),prefix),ref,lineage);
            if(result.status()==ResultStatus.DENIED)return ToolResults.result(manifest,scope,ResultStatus.DENIED,List.of(),clock.instant(),List.of(),Set.of(),List.of(),null);
            if(result.status()==ResultStatus.SOURCE_UNAVAILABLE){missing.add(repository.sourceRepo());continue;}
            observed++;for(var snippet:result.snippets()){Map<String,Object> item=SchemaContracts.JSON.convertValue(snippet,new tools.jackson.core.type.TypeReference<Map<String,Object>>(){});var marked=new LinkedHashMap<>(item);marked.put("codeRefKind",lineage.isPresent()?"DEPLOYED_SOURCE_COMMIT":"CURRENT_APPROVED_REF");marked.put("deployedSourceCommit",lineage.map(GitLabPipelineLineageReader.Lineage::commitSha).orElse("UNKNOWN"));marked.put("missingEvidence",lineage.isPresent()?List.of("plugin JAR digest","effectiveConfig"):List.of("deployed source commit","plugin JAR digest","effectiveConfig"));origin.ifPresent(value->{putKnown(marked,"traceId",value.traceId());putKnown(marked,"spanId",value.spanId());putKnown(marked,"service",value.service());putKnown(marked,"serviceVersion",value.serviceVersion());});items.add(marked);}
            if(lineage.isPresent()&&result.status()!=ResultStatus.SOURCE_UNAVAILABLE) {
                var actual=lineage.get();var deployment=deploymentRef(scope,actual);
                deploymentEvidence.add(new Evidence(UUID.randomUUID().toString(),scope.boundSystem(),ToolKind.GIT.name(),new com.nori.ai.gateway.core.scope.OwnedReference(scope.boundSystem(),manifest.sourceAllowlistRef()),null,clock.instant(),origin.map(Provenance::traceId).orElse(null),deployment,"OBSERVED",scope.actorRef()));
            }
                }
            }
        }
        var status=observed==0?ResultStatus.SOURCE_UNAVAILABLE:!missing.isEmpty()?ResultStatus.PARTIAL:items.isEmpty()?ResultStatus.EMPTY:ResultStatus.SUCCESS;
        var result=ToolResults.result(manifest,scope,status,items,clock.instant(),missing,Set.of(),List.of(),null);if(deploymentEvidence.isEmpty())return result;
        var evidence=new ArrayList<>(result.evidence());evidence.addAll(deploymentEvidence);
        return new ToolResultEnvelope(result.toolExecutionId(),result.capabilityId(),result.contractVersion(),result.schemaDigest(),result.ownerSystem(),result.status(),result.dataRef(),result.evidenceRefs(),result.observedAt(),result.sourceRevision(),result.missingFields(),result.warnings(),result.errorCode(),result.traceId(),result.metadata(),result.clarification(),result.blocks(),result.claims(),evidence,result.citations(),result.composerInputs());
    }
    private String text(tools.jackson.databind.JsonNode value,String field){return value.path(field).isString()?value.get(field).asString():null;}
    private void putKnown(Map<String,Object> value,String key,String text){if(text!=null)value.put(key,text);}
    private com.nori.ai.gateway.core.scope.OwnedReference deploymentRef(VerifiedSystemScope scope,GitLabPipelineLineageReader.Lineage lineage) {
        String repo=java.net.URLEncoder.encode(lineage.sourceRepo(),java.nio.charset.StandardCharsets.UTF_8).replace("+","%20");
        var ref=new com.nori.ai.gateway.core.scope.OwnedReference(scope.boundSystem(),"deployment://"+scope.boundSystem().name()+"/pipelines/"+lineage.execPipelineId()+"/repos/"+repo+"/commits/"+lineage.commitSha());
        if(!ref.belongsTo(scope.boundSystem()))throw new IllegalArgumentException("배포 근거 소유 범위가 다릅니다");return ref;
    }
    private ToolResultEnvelope unavailable(CapabilityManifest manifest,VerifiedSystemScope scope){return ToolResults.result(manifest,scope,ResultStatus.SOURCE_UNAVAILABLE,List.of(),clock.instant(),List.of(),Set.of(),List.of(),null);}
}
