package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.job.JobContext;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.workflow.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.util.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/** 결과 항목의 경계를 유지하여 선택과 대조가 서로 다른 본문을 보지 않게 합니다. */
public final class GroundingInputs implements WorkflowGrounding.Inputs {
    private final com.nori.ai.gateway.core.llm.ClassificationService.ContextSource history;
    private final com.nori.ai.gateway.core.feedback.ReviewedGlossary glossary;
    private final GatewaySystemsProperties systems;private final CapabilityRegistry registry;private final ContentSanitizer sanitizer;private final Map<String,String> gitProjects;
    public GroundingInputs(GatewaySystemsProperties systems,CapabilityRegistry registry,ContentSanitizer sanitizer){this(systems,registry,sanitizer,Map.of());}
    public GroundingInputs(GatewaySystemsProperties systems,CapabilityRegistry registry,ContentSanitizer sanitizer,Map<String,String> gitProjects){this(systems,registry,sanitizer,gitProjects,(scope,initial)->initial);}
    public GroundingInputs(GatewaySystemsProperties systems,CapabilityRegistry registry,ContentSanitizer sanitizer,Map<String,String> gitProjects,com.nori.ai.gateway.core.feedback.ReviewedGlossary glossary){this(systems,registry,sanitizer,gitProjects,glossary,null);}
    public GroundingInputs(GatewaySystemsProperties systems,CapabilityRegistry registry,ContentSanitizer sanitizer,Map<String,String> gitProjects,com.nori.ai.gateway.core.feedback.ReviewedGlossary glossary,com.nori.ai.gateway.core.llm.ClassificationService.ContextSource history){this.systems=systems;this.registry=registry;this.sanitizer=sanitizer;this.gitProjects=Map.copyOf(gitProjects);this.glossary=glossary;this.history=history;}
    @Override public List<com.nori.ai.gateway.core.llm.CitedHistoryContext.Pair> history(JobContext job) {
        if(history==null||com.nori.ai.gateway.core.llm.CitedHistoryContext.reference(job).isEmpty())return List.of();
        return history.load(new com.nori.ai.gateway.core.llm.ClassificationService.Request(job.scope(),job.conversationId(),job.jobId(),job.cancellation(),job.requestInput())).references();
    }
    @Override public Map<String,String> glossary(JobContext job){return glossary.current(job.scope(),systems.systems().get(job.scope().boundSystem().name()).glossary());}
    @Override public List<String> permissions(JobContext job,String capabilityId) {
        var manifest=registry.find(job.scope(),capabilityId).orElse(null);if(manifest==null)return List.of();
        var system=systems.systems().get(job.scope().boundSystem().name());var result=new LinkedHashSet<String>();
        switch(manifest.toolKind()) {
            case LOG->system.apps().forEach(value->result.addAll(value.requiredPermissions()));
            case TRACE->system.traces().forEach(value->result.addAll(value.requiredPermissions()));
            case GIT->system.repositories().forEach(value->result.addAll(value.requiredPermissions()));
            default->result.addAll(manifest.requiredPermission());
        }
        return List.copyOf(result);
    }
    @Override public WorkflowGrounding.SourceUnits collect(JobContext job,WorkflowOutcome outcome) {
        var units=new ArrayList<EvidenceUnit>();var invalid=new HashSet<String>();var system=systems.systems().get(job.scope().boundSystem().name());
        for(var stage:outcome.stages()) {
            var manifest=registry.find(job.scope(),stage.result().capabilityId()).orElse(null);
            if(manifest==null||manifest.toolKind()==ToolKind.KNOWLEDGE||!stage.observed())continue;
            var local=new ArrayList<EvidenceUnit>();
            try {
                if(!Set.of("SUCCESS","EMPTY","PARTIAL","STALE","CONFLICT").contains(stage.result().status().name())||stage.result().observedAt()==null)throw invalid();
                var rows=new ArrayList<JsonNode>();for(var block:stage.result().blocks()) {
                    if(block.content()==null||!block.content().path("items").isArray())throw invalid();
                    for(var item:block.content().get("items"))rows.add(item);
                }
                for(var row:rows) {
                    if(manifest.toolKind()==ToolKind.TRACE) {
                        String trace=string(row,"traceId",false);if(!row.path("spans").isArray())throw invalid();
                        for(var span:row.get("spans")) {
                            String service=string(span,"service",false),namespace=namespace(span,system,service,true);
                            var location=SchemaContracts.JSON.createObjectNode().put("namespace",namespace).put("traceId",trace);
                            var body=SchemaContracts.JSON.createObjectNode();
                            for(String field:List.of("service","spanId","name","startTimeUnixNano","endTimeUnixNano","serviceVersion")) {
                                String value=string(span,field,field.equals("serviceVersion"));body.put(field,field.equals("name")?WorkflowContent.text(value,sanitizer):value);
                                if(!field.equals("name"))location.put(field,value);
                            }
                            if(!body.path("startTimeUnixNano").asString().matches("[0-9]+")||!body.path("endTimeUnixNano").asString().matches("[0-9]+"))throw invalid();
                            var permissions=system.traces().stream().filter(t->t.service().equals(service)&&t.namespace().equals(namespace)).findFirst().orElseThrow(GroundingInputs::invalid).requiredPermissions();
                            local.add(unit(stage,"TRACE",location,canonical(body),permissions));
                        }
                    } else if(manifest.toolKind()==ToolKind.LOG) {
                        String app=string(row,"app",false),namespace=namespace(row,system,app,false),at=string(row,"occurredAt",false);if(!at.endsWith("Z"))throw invalid();Instant.parse(at);
                        var location=SchemaContracts.JSON.createObjectNode().put("app",app).put("namespace",namespace).put("occurredAt",at);
                        var permissions=system.apps().stream().filter(a->a.name().equals(app)&&a.namespace().equals(namespace)).findFirst().orElseThrow(GroundingInputs::invalid).requiredPermissions();
                        local.add(unit(stage,"LOG",location,string(row,"text",true),permissions));
                    } else if(manifest.toolKind()==ToolKind.GIT) {
                        var location=SchemaContracts.JSON.createObjectNode();for(String field:List.of("sourceRepo","path","ref"))location.put(field,string(row,field,false));
                        var body=SchemaContracts.JSON.createObjectNode().put("text",WorkflowContent.text(string(row,"text",true),sanitizer));
                        for(String field:List.of("codeRefKind","deployedSourceCommit","missingEvidence","traceId","spanId","service","serviceVersion"))if(row.has(field)) {
                            var value=row.get(field);
                            if(field.equals("missingEvidence")){if(!value.isArray())throw invalid();for(var item:value)if(!item.isString())throw invalid();}
                            else {string(row,field,field.equals("serviceVersion"));if(field.equals("codeRefKind")&&!Set.of("DEPLOYED_SOURCE_COMMIT","CURRENT_APPROVED_REF").contains(value.asString()))throw invalid();}
                            body.set(field,value);
                        }
                        String project=gitProjects.getOrDefault(location.path("sourceRepo").asString(),location.path("sourceRepo").asString());
                        var permissions=new LinkedHashSet<String>();
                        for(var repo:system.repositories())if(repo.projectId().equals(project)&&location.path("path").asString().startsWith(repo.pathPrefix())
                                &&(repo.ref().equals(location.path("ref").asString())||row.path("codeRefKind").asString("").equals("DEPLOYED_SOURCE_COMMIT")))permissions.addAll(repo.requiredPermissions());
                        if(permissions.isEmpty())throw invalid();
                        local.add(unit(stage,"GIT",location,canonical(body),List.copyOf(permissions)));
                    } else if(manifest.toolKind()==ToolKind.BUSINESS) {
                        var location=SchemaContracts.JSON.createObjectNode().put("toolId",manifest.capabilityId()).put("ref",string(row,"ref",false)).put("observedAt",stage.result().observedAt().toString());
                        if(!row.path("values").isObject())throw invalid();
                        local.add(unit(stage,"SYSTEM",location,string(row,"text",true),manifest.requiredPermission()).withValues(WorkflowContent.mask(row.get("values"),sanitizer)));
                    }
                }
                if(!local.stream().allMatch(u->job.scope().perm().containsAll(u.permissions())))throw invalid();
                local.sort(GroundingInputs::compare);units.addAll(local);
            }catch(IllegalArgumentException|java.time.DateTimeException malformed){invalid.add(stage.stepId());}
        }
        return new WorkflowGrounding.SourceUnits(units,invalid);
    }
    private EvidenceUnit unit(WorkflowOutcome.Stage stage,String type,JsonNode location,String body,List<String> permissions) {
        var result=stage.result();var retention=SchemaContracts.JSON.createObjectNode();
        retention.set("missingRanges",SchemaContracts.JSON.valueToTree(result.metadata()==null?List.of():result.metadata().missingRanges()));
        retention.set("unverifiedRanges",SchemaContracts.JSON.valueToTree(result.metadata()==null?List.of():result.metadata().unverifiedRanges()));
        return new EvidenceUnit(type,null,location,canonical(location),Set.of("TRACE","GIT").contains(type)?body:WorkflowContent.text(body,sanitizer),permissions,result.status().name(),result.observedAt().toString(),result.missingFields(),retention,result.warnings().stream().map(Enum::name).toList(),List.of());
    }
    private static String namespace(JsonNode row,GatewaySystemsProperties.SystemConfig system,String name,boolean trace) {
        var configured=trace?system.traces().stream().filter(t->t.service().equals(name)).map(GatewaySystemsProperties.Trace::namespace).distinct().toList():system.apps().stream().filter(a->a.name().equals(name)).map(GatewaySystemsProperties.App::namespace).distinct().toList();
        String actual=row.path("namespace").asString("");
        if(!actual.isEmpty()){if(!configured.contains(actual))throw invalid();return actual;}
        if(configured.size()!=1)throw invalid();return configured.getFirst();
    }
    private static int compare(EvidenceUnit a,EvidenceUnit b) {
        int c;
        if(a.sourceType().equals("LOG")) {
            c=Instant.parse(b.location().path("occurredAt").asString()).compareTo(Instant.parse(a.location().path("occurredAt").asString()));if(c!=0)return c;
            for(String key:List.of("app","namespace")){c=bytes(a.location().path(key).asString(),b.location().path(key).asString());if(c!=0)return c;}return bytes(a.body(),b.body());
        }
        if(a.sourceType().equals("TRACE")) {
            for(String key:List.of("traceId","service","spanId")){c=field(a,b,key);if(c!=0)return c;}
            c=new BigInteger(a.location().path("startTimeUnixNano").asString()).compareTo(new BigInteger(b.location().path("startTimeUnixNano").asString()));if(c!=0)return c;
            for(String key:List.of("namespace","endTimeUnixNano")){c=field(a,b,key);if(c!=0)return c;}
            c=SchemaContracts.JSON.readTree(a.body()).path("name").asString().compareTo(SchemaContracts.JSON.readTree(b.body()).path("name").asString());
            return c!=0?c:field(a,b,"serviceVersion");
        }
        if(a.sourceType().equals("GIT")){for(String key:List.of("sourceRepo","path","ref")){c=bytes(a.location().path(key).asString(),b.location().path(key).asString());if(c!=0)return c;}return bytes(SchemaContracts.JSON.readTree(a.body()).path("text").asString(),SchemaContracts.JSON.readTree(b.body()).path("text").asString());}
        return 0;
    }
    private static int field(EvidenceUnit a,EvidenceUnit b,String field){return a.location().path(field).asString().compareTo(b.location().path(field).asString());}
    private static int bytes(String a,String b){return Arrays.compareUnsigned(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8));}
    private static String string(JsonNode node,String field,boolean empty){var value=node.get(field);if(value==null||!value.isString()||!empty&&value.asString().isEmpty())throw invalid();return value.asString();}
    private static String canonical(JsonNode node){return com.nori.ai.gateway.core.evidence.EvidenceJson.write(node);}
    private static IllegalArgumentException invalid(){return new IllegalArgumentException("근거 항목 계약이 잘못되었습니다");}
}
