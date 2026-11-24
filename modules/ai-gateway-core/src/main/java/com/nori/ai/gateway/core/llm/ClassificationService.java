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
import java.util.*;
/** 현재 대화의 접근 가능한 쌍만 넣고 한 번 호출합니다. 무효 출력의 재시도는 없습니다. */
public final class ClassificationService {
    public record Request(VerifiedSystemScope scope,String conversationId,String jobId,CancellationToken cancellation,JobRequestInput input) {}
    public record HistoryPair(String question,String answer) {}
    public record Context(RegistryView tools,List<HistoryPair> history,Map<String,String> glossary,int historyPairs,List<CitedHistoryContext.Pair> references,List<ClassificationIntent> intents){
        public Context(RegistryView tools,List<HistoryPair> history,Map<String,String> glossary,int historyPairs){this(tools,history,glossary,historyPairs,List.of(),List.of());}
        public Context(RegistryView tools,List<HistoryPair> history,Map<String,String> glossary,int historyPairs,List<CitedHistoryContext.Pair> references){this(tools,history,glossary,historyPairs,references,List.of());}
        public Context{intents=ClassificationIntent.checked(intents);history=List.copyOf(history);glossary=Map.copyOf(glossary);references=List.copyOf(references);if(historyPairs<0||historyPairs>4||references.size()>4)throw new IllegalArgumentException("대화 쌍 상한이 잘못되었습니다");}
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
        var visible=ClassificationIntent.visible(context.intents(),context.tools());
        var baseOutput=ClassificationOutputSchema.create(visible,candidates(request,context,history));
        var references=retainedReferences(context,request.input().maskedQuestion(),history);
        var output=references.isEmpty()?baseOutput:CitedHistoryContext.output(baseOutput,request.input().maskedQuestion(),references);
        if(answers.count(output.schema())>policy.budget(CallType.CLASSIFY).inputLimit()) {
            observer.diagnostic(request.jobId(),CallType.CLASSIFY,LlmDiagnostic.OUTPUT_CONSTRAINT_BUDGET);
            try(var observed=observer.open(request.jobId(),CallType.CLASSIFY,messages)) {
                audit.record(LlmCallAudit.attempt(request.scope(),request.conversationId(),request.jobId(),CallType.CLASSIFY,policy.budget(CallType.CLASSIFY).requestMaxTokens(),InferenceClient.ChatOutcome.budgetExceeded(counter.count(messages)),ErrorCode.INPUT_BUDGET_EXCEEDED,List.of()));
            }
            return new BudgetExceeded(0);
        }
        InferenceClient.ChatOutcome outcome;try(var observed=observer.open(request.jobId(),CallType.CLASSIFY,messages)){outcome=inference.chat(CallType.CLASSIFY,messages,policy.budget(CallType.CLASSIFY).requestMaxTokens(),request.cancellation(),output);}
        Result result;
        if(outcome instanceof InferenceClient.BudgetExceeded)result=new BudgetExceeded(0);
        else if(outcome instanceof InferenceClient.CallFailed)result=new Unavailable(1);
        else{
            var completed=(InferenceClient.Completed)outcome;
            if(AnswerCheck.failure(completed,policy.budget(CallType.CLASSIFY),answers)!=null){
                observer.diagnostic(request.jobId(),CallType.CLASSIFY,LlmDiagnostic.CLASSIFY_ANSWER_CHECK);
                result=new Rejected(ErrorCode.CONTRACT_INVALID,1,List.of());
            }else try{
                if(references.isEmpty())result=decode(completed.content(),request,context,history);
                else {
                    var projected=CitedHistoryContext.decode(baseOutput,request.input().maskedQuestion(),references,
                        request.input().selectedResourceRefs().stream().map(ResourceRef::ref).collect(java.util.stream.Collectors.toSet()),completed.content());
                    result=decode(projected.classificationJson(),request,context,history);
                    if(result instanceof Classified classified) {
                        var value=classified.classification();
                        if(projected.reference().isPresent()&&value.needs().stream().anyMatch(need->context.intents().stream().noneMatch(intent->intent.id().equals(need.intent())&&intent.documentSearch()&&intent.toolIds().isEmpty()&&intent.taskType()==TaskType.KNOWLEDGE_QUERY)))throw invalid();
                        result=new Classified(new Classification(value.taskType(),value.toolCalls(),value.targets(),value.timeWindow(),value.searchQuery(),value.retryQuery(),projected.reference(),value.needs()),1);
                    }
                }
            }
            catch(InvalidOutput invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ClassificationService.class.getName()),"CLASSIFICATION_SERVICE_FAILED",invalid);observer.diagnostic(request.jobId(),CallType.CLASSIFY,invalid.reason);result=new Rejected(ErrorCode.CONTRACT_INVALID,1,List.of());}
            catch(RuntimeException invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ClassificationService.class.getName()),"CLASSIFICATION_SERVICE_FAILED",invalid);observer.diagnostic(request.jobId(),CallType.CLASSIFY,LlmDiagnostic.CLASSIFY_SHAPE);result=new Rejected(ErrorCode.CONTRACT_INVALID,1,List.of());}
        }
        var debug=System.getLogger(ClassificationService.class.getName());
        if(debug.isLoggable(System.Logger.Level.DEBUG))debug.log(System.Logger.Level.DEBUG,"CLASSIFICATION_CONTEXT job={0} retainedPairs={1} refsSha={2} historySourceJob={3}",
            request.jobId(),history.size(),com.nori.ai.gateway.core.result.AnswerSnapshot.sha(SchemaContracts.JSON.writeValueAsString(CitedHistoryContext.modelReferences(references))),
            result instanceof Classified classified?classified.classification().historyReference().map(ref->ref.pair().sourceJobId()).orElse("NONE"):"NONE");
        audit.record(LlmCallAudit.attempt(request.scope(),request.conversationId(),request.jobId(),CallType.CLASSIFY,policy.budget(CallType.CLASSIFY).requestMaxTokens(),outcome,result instanceof Rejected rejected?rejected.code():null,List.of()));
        expiry.assertLive(request.cancellation());return result;
    }
    private List<ChatMessage> messages(Request request,Context context,List<HistoryPair> history){
        var user=SchemaContracts.JSON.createObjectNode().put("question",request.input().maskedQuestion());user.set("context",modelContext(request.input()));
        user.set("historyPairs",SchemaContracts.JSON.valueToTree(history));user.set("glossary",SchemaContracts.JSON.valueToTree(context.glossary()));
        var intents=user.putArray("intents");for(var intent:ClassificationIntent.visible(context.intents(),context.tools()))intents.addObject().put("id",intent.id()).put("description",intent.description());
        user.set("subjectCandidates",SchemaContracts.JSON.valueToTree(candidates(request,context,history)));
        var references=retainedReferences(context,request.input().maskedQuestion(),history);
        if(!references.isEmpty())user.set("historyReferences",SchemaContracts.JSON.valueToTree(CitedHistoryContext.modelReferences(references)));
        return List.of(new ChatMessage(ChatMessage.Role.SYSTEM,references.isEmpty()?prompt:CitedHistoryContext.classificationPrompt(prompt)),new ChatMessage(ChatMessage.Role.USER,SchemaContracts.JSON.writeValueAsString(user)));
    }
    private static List<String> candidates(Request request,Context context,List<HistoryPair> history){
        var tools=new LinkedHashSet<String>();context.tools().capabilities().forEach(tool->{tools.add(tool.toolId());tools.add(tool.capabilityId());});
        context.intents().forEach(intent->tools.addAll(intent.toolIds()));
        return ClassificationSubjects.candidates(request.input(),history,tools);
    }
    static List<CitedHistoryContext.Pair> retainedReferences(Context context,String question,List<HistoryPair> retained) {
        if(!CitedHistoryContext.referential(question))return List.of();
        int dropped=context.history().size()-retained.size();
        return context.references().stream().filter(ref->{int index=ref.ref().charAt(1)-'1';return index>=dropped&&index<context.history().size()
            &&retained.get(index-dropped).equals(new HistoryPair(ref.question(),ref.answer()));}).toList();
    }
    static String canonical(JsonNode node) { return SchemaContracts.canonicalize(SchemaContracts.JSON.writeValueAsString(node)); }
    static JsonNode modelContext(JobRequestInput input){var node=SchemaContracts.JSON.createObjectNode();Object raw=input.requestFields().get("context");Map<?,?> values=raw instanceof Map<?,?> map?map:Map.of();for(String field:List.of("timezone","referenceTime","timeWindow"))node.set(field,SchemaContracts.JSON.valueToTree(values.get(field)));node.set("resourceRefs",SchemaContracts.JSON.valueToTree(input.selectedResourceRefs()));return node;}
    private Result decode(String json,Request request,Context context,List<HistoryPair> history){
        var mapper=SchemaContracts.JSON.rebuild().enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
        final JsonNode node;
        try{node=mapper.readTree(json);SchemaContracts.canonicalize(json);}catch(RuntimeException invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ClassificationService.class.getName()),"CLASSIFICATION_SERVICE_FAILED",invalid);throw invalid(LlmDiagnostic.CLASSIFY_JSON);}
        keys(node,Set.of("needs"));
        var values=node.get("needs");if(!values.isArray()||values.isEmpty()||values.size()>4)throw invalid();
        var candidates=candidates(request,context,history);
        var visible=ClassificationIntent.visible(context.intents(),context.tools());
        var needs=new ArrayList<Classification.Need>();var selected=new ArrayList<ClassificationIntent>();
        var targets=new LinkedHashSet<ResourceRef>();var missing=new LinkedHashSet<String>();
        for(var value:values){
            keys(value,Set.of("intent","subject"));String id=string(value.get("intent"),128);
            var intent=visible.stream().filter(i->i.id().equals(id)).findFirst().orElseThrow(()->invalid(LlmDiagnostic.CLASSIFY_TOOL));
            String subject=value.get("subject").isNull()?null:string(value.get("subject"),256);
            if(subject!=null&&!candidates.contains(subject))throw invalid(LlmDiagnostic.CLASSIFY_TARGET);
            if(intent.subjectRequired()&&subject==null)missing.add("eqp");
            if(subject!=null&&(intent.subjectRequired()||intent.documentSearch()))targets.add(new ResourceRef("EQP",subject));
            needs.add(new Classification.Need(id,subject));selected.add(intent);
        }
        if(!missing.isEmpty()){observer.diagnostic(request.jobId(),CallType.CLASSIFY,LlmDiagnostic.CLASSIFY_MISSING_INPUT);return new Rejected(ErrorCode.PLAN_INVALID,1,List.copyOf(missing));}
        String search=request.input().maskedQuestion();for(var need:needs)if(need.subject()!=null&&!search.contains(need.subject()))search+=" "+need.subject();
        String retry=context.glossary().isEmpty()?search:search+" "+String.join(" ",new TreeMap<>(context.glossary()).values());
        return new Classified(new Classification(ClassificationIntent.taskType(selected),List.of(),List.copyOf(targets),Map.of(),search,retry,Optional.empty(),needs),1);
    }
    private static void keys(JsonNode node,Set<String> keys){if(node==null||!node.isObject()||!Set.copyOf(node.propertyNames()).equals(keys))throw invalid();}
    private static String string(JsonNode node,int max){if(node==null||!node.isString()||node.asString().isEmpty()||node.asString().codePointCount(0,node.asString().length())>max)throw invalid();return node.asString();}
    private static InvalidOutput invalid(){return invalid(LlmDiagnostic.CLASSIFY_SHAPE);}
    private static InvalidOutput invalid(LlmDiagnostic reason){return new InvalidOutput(reason);}
    private static final class InvalidOutput extends RuntimeException {
        final LlmDiagnostic reason;
        InvalidOutput(LlmDiagnostic reason){super("분류 출력 계약이 잘못되었습니다",null,false,false);this.reason=reason;}
    }
}
