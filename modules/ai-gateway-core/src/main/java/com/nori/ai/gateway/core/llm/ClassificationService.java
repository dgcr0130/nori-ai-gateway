package com.nori.ai.gateway.core.llm;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ErrorCode;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import java.time.ZoneId;
import java.util.*;
/** 현재 대화의 접근 가능한 쌍만 넣고 한 번 호출합니다. 무효 출력의 재시도는 없습니다. */
public final class ClassificationService {
    public record Request(VerifiedSystemScope scope,String conversationId,String jobId,CancellationToken cancellation,JobRequestInput input) {}
    public record HistoryPair(String question,String answer) {}
    public record Context(RegistryView tools,List<HistoryPair> history,Map<String,String> glossary,int historyPairs){
        public Context{history=List.copyOf(history);glossary=Map.copyOf(glossary);if(historyPairs<0||historyPairs>4)throw new IllegalArgumentException("대화 쌍 상한이 잘못되었습니다");}
    }
    @FunctionalInterface public interface ContextSource {Context load(Request request);}
    public sealed interface Result permits Classified,Rejected,Unavailable,BudgetExceeded{int calls();}
    public record Classified(Classification classification,int calls)implements Result{}
    public record Rejected(ErrorCode code,int calls,List<String> fields)implements Result{public Rejected{fields=List.copyOf(fields);}}
    public record Unavailable(int calls)implements Result{}
    public record BudgetExceeded(int calls)implements Result{}
    private final InferenceClient inference;private final TokenBudgetPolicy policy;private final AnswerTokenCounting answers;private final PromptTokenCounting counter;
    private final ExpiryCheckpoint expiry;private final LlmCallObserver observer;private final LlmCallAudit audit;private final String prompt;private final ContextSource source;
    public ClassificationService(InferenceClient inference,TokenBudgetPolicy policy,AnswerTokenCounting answers,PromptTokenCounting counter,ExpiryCheckpoint expiry,LlmCallObserver observer,LlmCallAudit audit,String prompt,ContextSource source){
        this.inference=inference;this.policy=policy;this.answers=answers;this.counter=counter;this.expiry=expiry;this.observer=observer;this.audit=audit;this.prompt=Objects.requireNonNull(prompt);this.source=source;
    }
    public Result classify(Request request){
        expiry.assertLive(request.cancellation());var context=source.load(request);if(!context.tools().ownerSystem().equals(request.scope().boundSystem()))throw new IllegalArgumentException("분류 시스템 결속이 다릅니다");
        var history=new ArrayList<>(context.history());while(history.size()>context.historyPairs())history.removeFirst();
        List<ChatMessage> messages;
        for(;;){messages=messages(request,context,history);int tokens=counter.count(messages);if(policy.violations(CallType.CLASSIFY,tokens,policy.budget(CallType.CLASSIFY).requestMaxTokens()).isEmpty())break;if(history.isEmpty()){try(var observed=observer.open(request.jobId(),CallType.CLASSIFY,messages)){audit.record(LlmCallAudit.attempt(request.scope(),request.conversationId(),request.jobId(),CallType.CLASSIFY,policy.budget(CallType.CLASSIFY).requestMaxTokens(),InferenceClient.ChatOutcome.budgetExceeded(tokens),ErrorCode.INPUT_BUDGET_EXCEEDED,List.of()));}return new BudgetExceeded(0);}history.removeFirst();}
        InferenceClient.ChatOutcome outcome;try(var observed=observer.open(request.jobId(),CallType.CLASSIFY,messages)){outcome=inference.chat(CallType.CLASSIFY,messages,policy.budget(CallType.CLASSIFY).requestMaxTokens(),request.cancellation());}
        Result result;
        if(outcome instanceof InferenceClient.BudgetExceeded)result=new BudgetExceeded(0);
        else if(outcome instanceof InferenceClient.CallFailed)result=new Unavailable(1);
        else{var completed=(InferenceClient.Completed)outcome;if(AnswerCheck.failure(completed,policy.budget(CallType.CLASSIFY),answers)!=null)result=new Rejected(ErrorCode.CONTRACT_INVALID,1,List.of());else try{result=decode(completed.content(),request,context,history);}catch(RuntimeException invalid){result=new Rejected(ErrorCode.CONTRACT_INVALID,1,List.of());}}
        audit.record(LlmCallAudit.attempt(request.scope(),request.conversationId(),request.jobId(),CallType.CLASSIFY,policy.budget(CallType.CLASSIFY).requestMaxTokens(),outcome,result instanceof Rejected rejected?rejected.code():null,List.of()));
        expiry.assertLive(request.cancellation());return result;
    }
    private List<ChatMessage> messages(Request request,Context context,List<HistoryPair> history){
        var user=SchemaContracts.JSON.createObjectNode().put("question",request.input().maskedQuestion());user.set("context",modelContext(request.input()));
        user.set("historyPairs",SchemaContracts.JSON.valueToTree(history));user.set("glossary",SchemaContracts.JSON.valueToTree(context.glossary()));
        var tools=user.putArray("tools");for(var tool:context.tools().capabilities()){var item=tools.addObject().put("toolId",tool.toolId()).put("description",tool.description());item.set("inputSchema",tool.inputSchema());}
        return List.of(new ChatMessage(ChatMessage.Role.SYSTEM,prompt),new ChatMessage(ChatMessage.Role.USER,SchemaContracts.JSON.writeValueAsString(user)));
    }
    static String canonical(JsonNode node) { return SchemaContracts.canonicalize(SchemaContracts.JSON.writeValueAsString(node)); }
    static JsonNode modelContext(JobRequestInput input){var node=SchemaContracts.JSON.createObjectNode();Object raw=input.requestFields().get("context");Map<?,?> values=raw instanceof Map<?,?> map?map:Map.of();for(String field:List.of("timezone","referenceTime","timeWindow"))node.set(field,SchemaContracts.JSON.valueToTree(values.get(field)));node.set("resourceRefs",SchemaContracts.JSON.valueToTree(input.selectedResourceRefs()));return node;}
    private Result decode(String json,Request request,Context context,List<HistoryPair> history){
        var mapper=SchemaContracts.JSON.rebuild().enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();JsonNode node=mapper.readTree(json);SchemaContracts.canonicalize(json);keys(node,Set.of("taskType","toolCalls","targets","timeWindow","searchQuery","retryQuery"));
        TaskType task=TaskType.valueOf(string(node.get("taskType"),64));var callsNode=node.get("toolCalls");var targetsNode=node.get("targets");if(!callsNode.isArray()||callsNode.size()>4||!targetsNode.isArray()||targetsNode.size()>16)throw invalid();
        if(Set.of(TaskType.GENERAL_QUERY,TaskType.UNSUPPORTED_REQUEST).contains(task)&&!callsNode.isEmpty())throw invalid();
        String literal=request.input().maskedQuestion();for(var pair:history)literal+="\n"+pair.question()+"\n"+pair.answer();
        var targets=new ArrayList<ResourceRef>();for(var target:targetsNode){keys(target,Set.of("type","ref"));var ref=new ResourceRef(string(target.get("type"),256),string(target.get("ref"),256));if(!request.input().selectedResourceRefs().contains(ref)&&!literal.contains(ref.ref()))throw invalid();targets.add(ref);}
        ZoneId zone=null;var raw=modelContext(request.input()).get("timezone");if(raw!=null&&!raw.isNull())zone=ZoneId.of(raw.asString());
        Map<String,String> window=Map.of();if(!node.get("timeWindow").isNull()){keys(node.get("timeWindow"),Set.of("from","to"));String from=string(node.get("timeWindow").get("from"),35),to=string(node.get("timeWindow").get("to"),35);if(!new InputValidator().parseTime(from,zone).isBefore(new InputValidator().parseTime(to,zone)))throw invalid();window=Map.of("from",from,"to",to);}
        String search=string(node.get("searchQuery"),512),retry=string(node.get("retryQuery"),512);
        for(var target:targets)if(!search.contains(target.ref()))throw invalid();
        for(String identifier:com.nori.ai.gateway.core.knowledge.SearchText.identifiers(request.input().maskedQuestion(),targets.stream().map(com.nori.ai.gateway.core.plan.ResourceRef::ref).toList()))
            if(!com.nori.ai.gateway.core.knowledge.SearchText.containsExact(search,identifier))throw invalid();
        if(search.equals(retry)){retry=context.glossary().isEmpty()?request.input().maskedQuestion():search+" "+String.join(" ",context.glossary().values());if(retry.codePointCount(0,retry.length())>512)throw invalid();}
        var calls=new ArrayList<Classification.ToolCall>();var missing=new LinkedHashSet<String>();
        for(var call:callsNode){keys(call,Set.of("toolId","arguments"));String id=string(call.get("toolId"),128);var tool=context.tools().find(id).orElseThrow(ClassificationService::invalid);JsonNode args=call.get("arguments");if(!args.isObject())throw invalid();
            var partial=(ObjectNode)tool.inputSchema().deepCopy();partial.remove("required");
            if(!SchemaContracts.matches(partial,args))throw invalid();
            for(var required:tool.inputSchema().path("required"))if(!args.hasNonNull(required.asString()))missing.add(required.asString());
            if(!missing.isEmpty())continue;
            if(new InputValidator().validate(tool,args,zone)!=null)throw invalid();
            if(args.hasNonNull("targetRef")&&targets.stream().noneMatch(value->value.ref().equals(args.get("targetRef").asString())))throw invalid();
            calls.add(new Classification.ToolCall(id,args));
        }
        if(!missing.isEmpty())return new Rejected(ErrorCode.PLAN_INVALID,1,List.copyOf(missing));
        return new Classified(new Classification(task,calls,targets,window,search,retry),1);
    }
    private static void keys(JsonNode node,Set<String> keys){if(node==null||!node.isObject()||!Set.copyOf(node.propertyNames()).equals(keys))throw invalid();}
    private static String string(JsonNode node,int max){if(node==null||!node.isString()||node.asString().isEmpty()||node.asString().codePointCount(0,node.asString().length())>max)throw invalid();return node.asString();}
    private static IllegalArgumentException invalid(){return new IllegalArgumentException("분류 출력 계약이 잘못되었습니다");}
}
