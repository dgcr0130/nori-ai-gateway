package com.nori.ai.gateway.core.workflow;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.llm.Classification;
import com.nori.ai.gateway.core.llm.ClassificationIntent;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import java.time.Duration;
import java.time.ZoneId;
import java.util.function.Supplier;
import java.util.*;
/** 공개 의도 선언으로 계획을 만들며 모델이 도구·인자·기간을 지정하지 않습니다. */
public final class WorkflowPlanBuilder {
    public static final class Blocked extends RuntimeException{public Blocked(){super("요청 옵션으로 필수 조회가 차단되었습니다");}}
    public static final class Invalid extends RuntimeException{private final List<String> fields;public Invalid(List<String> fields){super("실행 입력을 확인해 주세요");this.fields=List.copyOf(fields);}public List<String> fields(){return fields;}}
    private final CapabilityRegistry registry;
    private final Map<OwnerSystem,List<ClassificationIntent>> intents;
    private final List<ClassificationIntent> commonIntents;
    public WorkflowPlanBuilder(CapabilityRegistry registry){this(registry,Map.of());}
    public WorkflowPlanBuilder(CapabilityRegistry registry,List<ClassificationIntent> declarations){
        this.registry=registry;this.intents=Map.of();this.commonIntents=ClassificationIntent.checked(declarations);
    }
    public WorkflowPlanBuilder(CapabilityRegistry registry,Map<OwnerSystem,List<ClassificationIntent>> declarations){
        this.registry=registry;this.commonIntents=List.of();var copy=new LinkedHashMap<OwnerSystem,List<ClassificationIntent>>();declarations.forEach((owner,values)->copy.put(owner,ClassificationIntent.checked(values)));this.intents=Map.copyOf(copy);
    }
    public boolean usesIntents(JobContext job){return job.classification().containsKey("needs");}
    private List<Classification.Need> needs(JobContext job){
        var node=SchemaContracts.JSON.valueToTree(job.classification().get("needs"));
        if(!node.isArray()||node.isEmpty()||node.size()>4)throw new Invalid(List.of("needs"));
        var result=new ArrayList<Classification.Need>();
        for(var value:node){
            if(!value.isObject()||!Set.copyOf(value.propertyNames()).equals(Set.of("intent","subject"))||!value.path("intent").isString()||(!value.get("subject").isNull()&&!value.get("subject").isString()))throw new Invalid(List.of("needs"));
            result.add(new Classification.Need(value.get("intent").asString(),value.get("subject").isNull()?null:value.get("subject").asString()));
        }
        return List.copyOf(result);
    }
    private ClassificationIntent declaration(JobContext job,Classification.Need need){
        return intents.getOrDefault(job.scope().boundSystem(),commonIntents).stream().filter(intent->intent.id().equals(need.intent())).findFirst().orElseThrow(()->new Invalid(List.of("intent")));
    }
    private List<ClassificationIntent> selected(JobContext job){return needs(job).stream().map(need->declaration(job,need)).toList();}
    public TimeWindowResolver.Resolved resolveWindow(JobContext job,Supplier<Optional<Map<String,Object>>> previous){
        var definition=definition(job);var view=registry.view(job.scope());
        boolean accepts=selected(job).stream().anyMatch(intent->intent.periodRequirement()!=ClassificationIntent.PeriodRequirement.NONE&&intent.toolIds().stream().anyMatch(id->view.find(id).isPresent()));
        var context=job.requestInput().requestFields().get("context") instanceof Map<?,?> raw?SchemaContracts.JSON.convertValue(raw,new tools.jackson.core.type.TypeReference<Map<String,Object>>(){}):Map.<String,Object>of();
        return new TimeWindowResolver().resolveQuestion(job.requestInput().maskedQuestion(),accepts,context,previous,definition);
    }
    public WorkflowDefinition definition(JobContext job){
        if(usesIntents(job)){
            var selected=selected(job);var task=ClassificationIntent.taskType(selected);
            var view=registry.view(job.scope());boolean required=selected.stream().anyMatch(intent->intent.periodRequirement()==ClassificationIntent.PeriodRequirement.REQUIRED&&intent.toolIds().stream().anyMatch(id->view.find(id).isPresent()));
            return new WorkflowDefinition(job.scope().boundSystem(),task,List.of(),task==TaskType.GENERAL_QUERY?WorkflowDefinition.AnswerSource.REGISTRY_VIEW:WorkflowDefinition.AnswerSource.TOOL_RESULTS,required?Duration.ofHours(1):null);
        }
        TaskType task=TaskType.valueOf((String)job.classification().get("taskType"));
        boolean window=calls(job).stream().anyMatch(call->Set.of("logs.search","trace.search","git.searchCode").contains(call.toolId())&&(!call.arguments().hasNonNull("from")||!call.arguments().hasNonNull("to")));
        return new WorkflowDefinition(job.scope().boundSystem(),task,List.of(),task==TaskType.GENERAL_QUERY?WorkflowDefinition.AnswerSource.REGISTRY_VIEW:WorkflowDefinition.AnswerSource.TOOL_RESULTS,window?java.time.Duration.ofHours(1):null);
    }
    private List<com.nori.ai.gateway.core.llm.Classification.ToolCall> calls(JobContext job){
        Object raw=job.classification().get("toolCalls");if(!(raw instanceof List<?> values)||values.size()>4)throw new Invalid(List.of("toolCalls"));
        return SchemaContracts.JSON.convertValue(raw,new tools.jackson.core.type.TypeReference<List<com.nori.ai.gateway.core.llm.Classification.ToolCall>>(){});
    }
    public void checkOptions(WorkflowDefinition definition,JobContext job){
        var options=job.requestInput().requestFields().get("options") instanceof Map<?,?> values?values:Map.of();
        boolean documents=usesIntents(job)?selected(job).stream().anyMatch(ClassificationIntent::documentSearch):definition.taskType()==TaskType.KNOWLEDGE_QUERY;
        boolean tools=usesIntents(job)?selected(job).stream().anyMatch(intent->!intent.toolIds().isEmpty()):!calls(job).isEmpty();
        if(documents&&Boolean.FALSE.equals(options.get("allowKnowledge"))||tools&&Boolean.FALSE.equals(options.get("allowTools")))throw new Blocked();
    }
    public WorkflowExecutionPlan build(WorkflowDefinition definition,RegistryView view,JobContext job){return build(definition,view,job,Map.of());}
    public WorkflowExecutionPlan build(WorkflowDefinition definition,RegistryView view,JobContext job,Map<String,Object> window){
        if(!definition.ownerSystem().equals(job.scope().boundSystem())||!view.ownerSystem().equals(job.scope().boundSystem())||!job.jobId().equals(job.scope().requestId()))throw new IllegalArgumentException("실행 소유 범위가 다릅니다");
        checkOptions(definition,job);
        if(usesIntents(job))return buildIntents(definition,view,job,window);
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
    private WorkflowExecutionPlan buildIntents(WorkflowDefinition definition,RegistryView view,JobContext job,Map<String,Object> window){
        var needs=needs(job);var selected=needs.stream().map(need->declaration(job,need)).toList();
        if(definition.taskType()!=ClassificationIntent.taskType(selected))throw new Invalid(List.of("taskType"));
        for(int i=0;i<needs.size();i++){
            var intent=selected.get(i);if(!intent.visible(view))throw new Invalid(List.of("intent"));
            if(intent.subjectRequired()&&needs.get(i).subject()==null)throw new Invalid(List.of("eqp"));
        }
        // 앱에서 전달한 창은 동일 요청의 확인된 이전 턴 후보이며 질문·context가 항상 우선합니다.
        var resolved=resolveWindow(job,()->window.isEmpty()?Optional.empty():Optional.of(window));
        var context=job.requestInput().requestFields().get("context") instanceof Map<?,?> values?values:Map.of();
        ZoneId zone=context.get("timezone") instanceof String text?ZoneId.of(text):null;
        var targets=new LinkedHashSet<ResourceRef>();
        var steps=new ArrayList<WorkflowExecutionPlan.ExecutionStep>();var declarations=new ArrayList<WorkflowDefinition.Step>();var tools=new ArrayList<PlanStep>();var unique=new HashSet<String>();
        for(int i=0;i<needs.size();i++){
            var need=needs.get(i);var intent=selected.get(i);boolean targeted=need.subject()!=null&&intent.subjectRequired();if(targeted)targets.add(new ResourceRef("EQP",need.subject()));
            for(String id:intent.toolIds()){
                var manifest=view.find(id).orElse(null);if(manifest==null)continue;
                var args=SchemaContracts.JSON.createObjectNode();var properties=manifest.inputSchema().path("properties");
                if(intent.subjectRequired()&&need.subject()!=null&&properties.has("eqp"))args.put("eqp",need.subject());
                if(intent.periodRequirement()!=ClassificationIntent.PeriodRequirement.NONE)for(String bound:List.of("from","to"))if(properties.has(bound)&&resolved.values().get(bound) instanceof String time)args.put(bound,new InputValidator().parseTime(time,zone).toString());
                var missing=new ArrayList<String>();for(var field:manifest.inputSchema().path("required"))if(!args.hasNonNull(field.asString()))missing.add(field.asString());if(!missing.isEmpty())throw new Invalid(missing);
                String key=id+"\n"+SchemaContracts.canonicalize(args.toString());if(!unique.add(key))continue;
                String stepId="call-"+(tools.size()+1);var depends=tools.isEmpty()?List.<String>of():List.of(tools.getLast().stepId());
                var step=new PlanStep(stepId,id,depends,targeted?List.of(new ResourceRef("EQP",need.subject())):List.of(),args);var declaration=new WorkflowDefinition.Step(stepId,id,null,depends,false,List.of(),manifest.toolKind()!=ToolKind.BUSINESS&&manifest.toolKind()!=ToolKind.KNOWLEDGE);
                tools.add(step);declarations.add(declaration);steps.add(new WorkflowExecutionPlan.ExecutionStep(declaration,step));
            }
        }
        String search=(String)job.classification().getOrDefault("searchQuery",job.requestInput().maskedQuestion());
        for(var need:needs)if(need.subject()!=null&&!search.contains(need.subject()))search+=" "+need.subject();
        String retry=(String)job.classification().getOrDefault("retryQuery",search);
        var documents=new WorkflowExecutionPlan.DocumentSearch(selected.stream().anyMatch(ClassificationIntent::documentSearch),search,retry);
        var actual=new WorkflowDefinition(definition.ownerSystem(),definition.taskType(),declarations,definition.answerSource(),definition.defaultWindowLength());
        return new WorkflowExecutionPlan(actual,new PlanEnvelope(job.jobId(),job.conversationId(),job.requestInput().maskedQuestion(),List.copyOf(targets),actual.taskType(),tools),steps,documents);
    }

}
