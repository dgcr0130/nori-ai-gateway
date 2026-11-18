package com.nori.ai.gateway.core.workflow;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import java.util.*;
/** 도구 선택은 검증된 toolCalls 순서 하나로만 실행 목록을 만듭니다. */
public final class WorkflowPlanBuilder {
    public static final class Blocked extends RuntimeException{public Blocked(){super("요청 옵션으로 필수 조회가 차단되었습니다");}}
    public static final class Invalid extends RuntimeException{private final List<String> fields;public Invalid(List<String> fields){super("실행 입력을 확인해 주세요");this.fields=List.copyOf(fields);}public List<String> fields(){return fields;}}
    private final CapabilityRegistry registry;
    public WorkflowPlanBuilder(CapabilityRegistry registry){this.registry=registry;}
    public WorkflowDefinition definition(JobContext job){
        TaskType task=TaskType.valueOf((String)job.classification().get("taskType"));
        boolean window=calls(job).stream().anyMatch(call->Set.of("logs.search","trace.search","git.searchCode").contains(call.toolId())&&(!call.arguments().hasNonNull("from")||!call.arguments().hasNonNull("to")));
        return new WorkflowDefinition(job.scope().boundSystem(),task,List.of(),task==TaskType.GENERAL_QUERY?WorkflowDefinition.AnswerSource.REGISTRY_VIEW:WorkflowDefinition.AnswerSource.TOOL_RESULTS,window?java.time.Duration.ofHours(1):null);
    }
    private List<com.nori.ai.gateway.core.llm.Classification.ToolCall> calls(JobContext job){
        Object raw=job.classification().get("toolCalls");if(!(raw instanceof List<?> values)||values.size()>4)throw new Invalid(List.of("toolCalls"));
        return SchemaContracts.JSON.convertValue(raw,new tools.jackson.core.type.TypeReference<List<com.nori.ai.gateway.core.llm.Classification.ToolCall>>(){});
    }
    public void checkOptions(WorkflowDefinition definition,JobContext job){if(definition.taskType()==TaskType.KNOWLEDGE_QUERY&&job.requestInput().requestFields().get("options") instanceof Map<?,?> options&&Boolean.FALSE.equals(options.get("allowKnowledge")))throw new Blocked();if(!calls(job).isEmpty()&&job.requestInput().requestFields().get("options") instanceof Map<?,?> options&&Boolean.FALSE.equals(options.get("allowTools")))throw new Blocked();}
    public WorkflowExecutionPlan build(WorkflowDefinition definition,RegistryView view,JobContext job){return build(definition,view,job,Map.of());}
    public WorkflowExecutionPlan build(WorkflowDefinition definition,RegistryView view,JobContext job,Map<String,Object> window){
        if(!definition.ownerSystem().equals(job.scope().boundSystem())||!view.ownerSystem().equals(job.scope().boundSystem())||!job.jobId().equals(job.scope().requestId()))throw new IllegalArgumentException("실행 소유 범위가 다릅니다");
        checkOptions(definition,job);
        var selected=calls(job);if((definition.taskType()==TaskType.GENERAL_QUERY||definition.taskType()==TaskType.UNSUPPORTED_REQUEST)&&!selected.isEmpty())throw new Invalid(List.of("toolCalls"));
        var targets=SchemaContracts.JSON.convertValue(job.classification().getOrDefault("targets",List.of()),new tools.jackson.core.type.TypeReference<List<ResourceRef>>(){});
        var steps=new ArrayList<WorkflowExecutionPlan.ExecutionStep>();var definitions=new ArrayList<WorkflowDefinition.Step>();var tools=new ArrayList<PlanStep>();
        for(int i=0;i<selected.size();i++){
            var call=selected.get(i);var manifest=registry.find(job.scope(),call.toolId()).orElseThrow(()->new Invalid(List.of("toolId")));
            var args=(tools.jackson.databind.node.ObjectNode)call.arguments();var props=manifest.inputSchema().path("properties");
            for(String field:List.of("from","to"))if(props.has(field)&&!args.hasNonNull(field)&&window.get(field) instanceof String text)args.put(field,text);
            var missing=new ArrayList<String>();for(var required:manifest.inputSchema().path("required"))if(!args.hasNonNull(required.asString()))missing.add(required.asString());if(!missing.isEmpty())throw new Invalid(missing);
            String id="call-"+(i+1);List<String> depends=tools.isEmpty()?List.of():List.of(tools.getLast().stepId());
            var step=new PlanStep(id,call.toolId(),depends,targets,args);var declaration=new WorkflowDefinition.Step(id,call.toolId(),null,depends,false,List.of(),manifest.toolKind()!=ToolKind.BUSINESS&&manifest.toolKind()!=ToolKind.KNOWLEDGE);
            tools.add(step);definitions.add(declaration);steps.add(new WorkflowExecutionPlan.ExecutionStep(declaration,step));
        }
        var actual=new WorkflowDefinition(definition.ownerSystem(),definition.taskType(),definitions,definition.answerSource(),definition.defaultWindowLength());
        var intent=new WorkflowExecutionPlan.DocumentSearch(definition.taskType()!=TaskType.LIVE_QUERY&&definition.taskType()!=TaskType.GENERAL_QUERY&&definition.taskType()!=TaskType.UNSUPPORTED_REQUEST,(String)job.classification().getOrDefault("searchQuery",job.requestInput().maskedQuestion()),(String)job.classification().getOrDefault("retryQuery",job.requestInput().maskedQuestion()));
        return new WorkflowExecutionPlan(actual,new PlanEnvelope(job.jobId(),job.conversationId(),job.requestInput().maskedQuestion(),targets,definition.taskType(),tools),steps,intent);
    }
}
