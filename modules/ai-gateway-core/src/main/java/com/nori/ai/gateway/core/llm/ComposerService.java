package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.budget.PromptDraft;
import com.nori.ai.gateway.core.budget.PromptReducer;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.JobContext;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.ErrorCode;
import com.nori.ai.gateway.core.workflow.ComposerOutcome;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tools.jackson.databind.node.ObjectNode;

/**
 * Response Composer 서술(SPEC §14.5 설정된 작성 예산, §14.6 ②, §22.3). 입력은 Gateway가 선택한 마스킹 근거 전체 단위이고
 * Source 원문은 user 메시지의 자료 구획에만 둔다. 입력 조립 순서는 호출자 sanitizer 적용분 → {@link PromptReducer} → 관찰 → Client 내부 예산 assert 다.
 * 추론 실패는 {@link ComposerOutcome} 으로 돌려주며 검증 후 문장이 없으면 근거 확인 안내를 조립한다. 사고 표지가 든 content 는 결과에 싣지 않는다.
 */
public final class ComposerService {
    /** 재시도 요지(분류 재시도와 같은 형식, 직전 출력 원문은 다시 싣지 않는다). */
    static final String RETRY_PREFIX = "직전 출력이 무효다(오류: ", RETRY_SUFFIX = "). 지정된 출력 형식과 토큰 상한을 지켜 다시 답한다.";
    private static final Pattern CITATION = Pattern.compile("\\[e(\\d{1,9})]");
    private static final Pattern NUMBER = Pattern.compile("\\d+(?:\\.\\d+)?");

    /** 근거 하나의 결정론 요약. 호출자가 마스킹을 마친 값만 넣는다. 인용 표기 id 는 목록 순서의 {@code e1…eN} 이다. */
    public record EvidenceDigest(String sourceType, String eventTime, String observedAt) {
        public EvidenceDigest { Objects.requireNonNull(sourceType, "sourceType"); }
    }

    /**
     * 요구 근거 하나의 서버 수집 상태(SPEC §28.8, Plan §16.3 R4): 근거 코드·상태·근거 단계 ID·현재 READ 가 제공하지 않는 요청 항목. 원문·PII 는 없다.
     * 근거 계획이 없는 실행은 비어 있으며 그때 data 구획에 coverage 를 싣지 않는다.
     */
    public record CoverageDigest(String need, String status, List<String> sources, List<String> missingItems) {
        public CoverageDigest { Objects.requireNonNull(need); Objects.requireNonNull(status); sources = List.copyOf(sources); missingItems = List.copyOf(missingItems); }
    }

    /**
     * Composer 입력. 모든 문자열은 호출자가 ContentSanitizer 를 적용한 값이다.
     * @param toolResults 단계 결과(축소 4단계의 필수 필드 투영 포함)
     * @param logSummary 결정론 Log Summarizer 요약(없으면 빈 문자열)
     */
    public record Input(List<EvidenceDigest> evidence, List<PromptDraft.ToolResult> toolResults, String logSummary, List<String> rawLogLines, String question, List<CoverageDigest> coverage, Optional<String> selectedData, Optional<ClassificationService.HistoryPair> matchedHistory, List<String> businessTargets) {
        public Input(List<EvidenceDigest> evidence,List<PromptDraft.ToolResult> tools,String summary,List<String> lines,String question,List<CoverageDigest> coverage,Optional<String> selectedData,Optional<ClassificationService.HistoryPair> matchedHistory) {
            this(evidence,tools,summary,lines,question,coverage,selectedData,matchedHistory,List.of());
        }
        public Input(List<EvidenceDigest> evidence,List<PromptDraft.ToolResult> tools,String summary,List<String> lines,String question,List<CoverageDigest> coverage,Optional<String> selectedData) {
            this(evidence,tools,summary,lines,question,coverage,selectedData,Optional.empty());
        }
        public Input(List<EvidenceDigest> evidence,List<PromptDraft.ToolResult> tools,String summary,List<String> lines,String question,List<CoverageDigest> coverage) {
            this(evidence,tools,summary,lines,question,coverage,Optional.empty());
        }
        public Input(List<EvidenceDigest> evidence, List<PromptDraft.ToolResult> toolResults, String logSummary, List<String> rawLogLines, String question) {
            this(evidence, toolResults, logSummary, rawLogLines, question, List.of());
        }
        public Input {
            evidence = List.copyOf(evidence); toolResults = List.copyOf(toolResults); rawLogLines = List.copyOf(rawLogLines); coverage = List.copyOf(coverage);businessTargets=List.copyOf(businessTargets);Objects.requireNonNull(selectedData);Objects.requireNonNull(matchedHistory);
            Objects.requireNonNull(logSummary, "logSummary"); Objects.requireNonNull(question, "question");
        }
        @Override public String toString() { return "ComposerInput[보호됨]"; }
    }

    /**
     * {@code narrative} 는 {@link ComposerOutcome#COMPOSED} 일 때만 있다. 사실 답변의 answer는 결정적 문장 대조를 통과한 구조이며 일반 도움말은 기존 narrative를 유지한다.
     * {@code reductionSteps} 는 적용한 축소 단계와 전후 토큰 수(원문 없음)다.
     */
    public record Result(ComposerOutcome outcome, Optional<String> narrative, int calls, int citationViolations, int numberViolations, List<PromptReducer.Step> reductionSteps,Optional<com.nori.ai.gateway.core.result.GroundedAnswer> answer) {
        public Result(ComposerOutcome outcome,Optional<String> narrative,int calls,int citations,int numbers,List<PromptReducer.Step> steps){this(outcome,narrative,calls,citations,numbers,steps,Optional.empty());}
        public Result {
            Objects.requireNonNull(outcome, "outcome"); Objects.requireNonNull(narrative, "narrative"); reductionSteps = List.copyOf(reductionSteps);
            if (outcome == ComposerOutcome.NOT_INVOKED || narrative.isPresent() != (outcome == ComposerOutcome.COMPOSED)) throw new IllegalArgumentException("서술 결과 조합이 잘못되었습니다");
        }
        @Override public String toString() { return "ComposerResult[" + outcome + "]"; }
    }

    private final InferenceClient inference;
    private final PromptReducer reducer;
    private final TokenBudgetPolicy.CallBudget budget;
    private final int evidenceTokens;
    private final AnswerTokenCounting answers;
    private final ExpiryCheckpoint expiry;
    private final LlmCallObserver observer;
    private final LlmCallAudit audit;
    private final String systemPrompt;
    private final FactCheck factCheck;
    private final java.util.Map<com.nori.ai.gateway.core.scope.OwnerSystem,Presentation> presentations;
    private final Presentation defaultPresentation;
    private final String configurationSha256;
    public String configurationSha256(){return configurationSha256;}

    public ComposerService(InferenceClient inference, PromptReducer reducer, TokenBudgetPolicy policy, AnswerTokenCounting answers, ExpiryCheckpoint expiry,
            LlmCallObserver observer, LlmCallAudit audit, String systemPrompt) {
        this(inference,reducer,policy,answers,expiry,observer,audit,systemPrompt,text->{throw new IllegalStateException("문장 분석기가 없습니다");});
    }
    /** 표시 규칙은 소유 시스템별로 결속하여 같은 도구 ID의 다른 시스템 설정과 섞이지 않게 합니다. */
    public record Presentation(java.util.Map<String,String> descriptions,java.util.Set<String> tableTools) {
        public Presentation {descriptions=java.util.Map.copyOf(descriptions);tableTools=java.util.Set.copyOf(tableTools);}
    }
    public ComposerService(InferenceClient inference, PromptReducer reducer, TokenBudgetPolicy policy, AnswerTokenCounting answers, ExpiryCheckpoint expiry,
            LlmCallObserver observer,LlmCallAudit audit,String systemPrompt,com.nori.ai.gateway.core.knowledge.SearchLanguage language) {
        this(inference,reducer,policy,answers,expiry,observer,audit,systemPrompt,language,java.util.Map.of(),java.util.Set.of());
    }
    public ComposerService(InferenceClient inference,PromptReducer reducer,TokenBudgetPolicy policy,AnswerTokenCounting answers,ExpiryCheckpoint expiry,LlmCallObserver observer,LlmCallAudit audit,String systemPrompt,com.nori.ai.gateway.core.knowledge.SearchLanguage language,java.util.Map<String,String> descriptions,java.util.Set<String> tableTools) {
        this(inference,reducer,policy,answers,expiry,observer,audit,systemPrompt,language,fingerprint(systemPrompt,policy,descriptions,tableTools),java.util.Map.of(),new Presentation(descriptions,tableTools));
    }
    public ComposerService(InferenceClient inference,PromptReducer reducer,TokenBudgetPolicy policy,AnswerTokenCounting answers,ExpiryCheckpoint expiry,LlmCallObserver observer,LlmCallAudit audit,String systemPrompt,com.nori.ai.gateway.core.knowledge.SearchLanguage language,String configurationSha256) {
        this(inference,reducer,policy,answers,expiry,observer,audit,systemPrompt,language,configurationSha256,java.util.Map.of());
    }
    public ComposerService(InferenceClient inference,PromptReducer reducer,TokenBudgetPolicy policy,AnswerTokenCounting answers,ExpiryCheckpoint expiry,LlmCallObserver observer,LlmCallAudit audit,String systemPrompt,com.nori.ai.gateway.core.knowledge.SearchLanguage language,String configurationSha256,java.util.Map<com.nori.ai.gateway.core.scope.OwnerSystem,Presentation> presentations) {
        this(inference,reducer,policy,answers,expiry,observer,audit,systemPrompt,language,configurationSha256,presentations,new Presentation(java.util.Map.of(),java.util.Set.of()));
    }
    private ComposerService(InferenceClient inference,PromptReducer reducer,TokenBudgetPolicy policy,AnswerTokenCounting answers,ExpiryCheckpoint expiry,LlmCallObserver observer,LlmCallAudit audit,String systemPrompt,com.nori.ai.gateway.core.knowledge.SearchLanguage language,String configurationSha256,java.util.Map<com.nori.ai.gateway.core.scope.OwnerSystem,Presentation> presentations,Presentation defaultPresentation) {
        this.configurationSha256=Objects.requireNonNull(configurationSha256);
        this.factCheck=new FactCheck(language);this.presentations=java.util.Map.copyOf(presentations);this.defaultPresentation=defaultPresentation;
        this.inference = Objects.requireNonNull(inference, "inference");
        this.reducer = Objects.requireNonNull(reducer, "reducer");
        this.budget = Objects.requireNonNull(policy, "policy").budget(CallType.COMPOSER);
        this.evidenceTokens = policy.evidenceTokens();
        this.answers = Objects.requireNonNull(answers, "answers");
        this.expiry = Objects.requireNonNull(expiry, "expiry");
        this.observer = Objects.requireNonNull(observer, "observer");
        this.audit = Objects.requireNonNull(audit, "audit");
        this.systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt");
        if (systemPrompt.isBlank() || systemPrompt.indexOf('\r') >= 0) throw new IllegalArgumentException("서술 프롬프트 형식이 잘못되었습니다");
    }

    public Result compose(JobContext job, Input input) {
        // 취소면 Prompt 조립도 하지 않는다.
        expiry.assertLive(job.cancellation());
        if(input.selectedData().isPresent())return composeGrounded(job,input);
        var draft = new PromptDraft(systemPrompt, "boundSystem: " + job.scope().boundSystem().name(), List.of(), List.of(), input.logSummary(),
            input.rawLogLines(), input.toolResults(), input.question());
        var reduction = reducer.reduce(CallType.COMPOSER, draft, value -> messages(value,input.evidence(),input.coverage()));
        if (!reduction.withinBudget()) return failed(ComposerOutcome.INPUT_BUDGET_EXCEEDED, 0, reduction.steps());
        String data = reduction.messages().getLast().content();
        // 이후 검색 단계가 선택한 근거도 설정한 상한을 넘겨 전송할 수 없습니다. 질문은 줄이지 않습니다.
        try {
            String evidence = input.selectedData().orElseGet(()->ClassificationService.canonical(SchemaContracts.JSON.readTree(data).path("data")));
            if (answers.count(evidence) > evidenceTokens) return failed(ComposerOutcome.INPUT_BUDGET_EXCEEDED, 0, reduction.steps());
        } catch (IllegalArgumentException uncountable) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ComposerService.class.getName()),"COMPOSER_SERVICE_FAILED",uncountable); return failed(ComposerOutcome.LLM_UNAVAILABLE, 0, reduction.steps()); }
        List<ChatMessage> messages = reduction.messages();
        StructuredOutput output=null;
        int calls = 0;
        for (int attempt = 0; attempt < 2; attempt++) {
            expiry.assertLive(job.cancellation());
            InferenceClient.ChatOutcome outcome;
            try (var scope = observer.open(job.jobId(), CallType.COMPOSER, messages)) {
                outcome = inference.chat(CallType.COMPOSER, messages, budget.requestMaxTokens(), job.cancellation(), output);
            }
            String failure = outcome instanceof InferenceClient.Completed answered ? AnswerCheck.failure(answered, budget, answers) : null;
            // 시도마다 같은 축소 단계(첫 조립 기준)를 함께 남긴다. 재시도 입력 계수는 Client 의 호출 직전 계수다.
            audit.record(LlmCallAudit.attempt(job.scope(), job.conversationId(), job.jobId(), CallType.COMPOSER, budget.requestMaxTokens(), outcome,
                failure == null ? null : ErrorCode.LLM_UNAVAILABLE, reduction.steps()));
            expiry.assertLive(job.cancellation());
            if (outcome instanceof InferenceClient.BudgetExceeded) return failed(ComposerOutcome.INPUT_BUDGET_EXCEEDED, calls, reduction.steps());
            calls++;
            if (outcome instanceof InferenceClient.CallFailed) return failed(ComposerOutcome.LLM_UNAVAILABLE, calls, reduction.steps());
            var completed = (InferenceClient.Completed) outcome;
            if (failure == null) {
                return composed(completed.content(), input.evidence().size(), data, calls, reduction.steps());
            }
            List<ChatMessage> retry = new ArrayList<>(reduction.messages());
            retry.add(new ChatMessage(ChatMessage.Role.USER, RETRY_PREFIX + AnswerCheck.retryCode(failure) + RETRY_SUFFIX));
            messages = List.copyOf(retry);
        }
        return failed(ComposerOutcome.LLM_UNAVAILABLE, calls, reduction.steps());
    }

    private static String fingerprint(String systemPrompt,TokenBudgetPolicy policy,java.util.Map<String,String> descriptions,java.util.Set<String> tableTools) {
        var values=java.util.Map.of("helpPrompt",systemPrompt,"budget",policy.budget(CallType.COMPOSER),"evidenceTokens",policy.evidenceTokens(),
            "groundedPrompt",GroundedFacts.prompt(),"groundedSchema",SchemaContracts.parse(GroundedFacts.output(1).schema()),
            "answerShape",QuestionAnswerShape.contract(),"descriptions",descriptions,"tableTools",tableTools.stream().sorted().toList());
        return com.nori.ai.gateway.core.result.AnswerSnapshot.sha(SchemaContracts.canonicalize(SchemaContracts.JSON.writeValueAsString(values)));
    }
    private record Grounded(List<com.nori.ai.gateway.core.workflow.EvidenceUnit> units,String data,StructuredOutput output,PromptReducer.Reduction reduction) {}

    /** 근거 부족으로 생략해도 질문·필수 지시의 예산 초과는 자료 부족으로 바꾸지 않습니다. */
    public boolean groundedQuestionFits(JobContext job) {
        expiry.assertLive(job.cancellation());
        return fits(grounded(job,job.requestInput().maskedQuestion(),List.of(),Optional.empty()));
    }
    public boolean fitsGrounded(JobContext job,List<com.nori.ai.gateway.core.workflow.EvidenceUnit> units,Optional<ClassificationService.HistoryPair> history) {
        try {return fits(grounded(job,job.requestInput().maskedQuestion(),units,history));}
        catch(IllegalArgumentException invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ComposerService.class.getName()),"COMPOSER_SERVICE_FAILED",invalid);return false;}
    }
    private boolean fits(Grounded value) {return value.reduction().withinBudget()&&answers.count(value.data())<=evidenceTokens&&answers.count(value.output().schema())<=evidenceTokens;}
    private Grounded grounded(JobContext job,String question,List<com.nori.ai.gateway.core.workflow.EvidenceUnit> units,Optional<ClassificationService.HistoryPair> history) {
        String data=GroundedFacts.data(question,units,history);var output=GroundedFacts.output(units.size());
        var draft=new PromptDraft(GroundedFacts.prompt(),"boundSystem: "+job.scope().boundSystem().name(),List.of(),List.of(),"",List.of(),List.of(),question);
        var reduction=reducer.reduce(CallType.COMPOSER,draft,value->GroundedFacts.messages(job.scope().boundSystem().name(),data));
        return new Grounded(List.copyOf(units),data,output,reduction);
    }
    private Result composeGrounded(JobContext job,Input input) {
        final Grounded prepared;
        try {
            String original=input.selectedData().orElseThrow();SchemaContracts.canonicalize(original);
            var root=SchemaContracts.JSON.readTree(original);if(!root.isObject()||root.size()!=1||!root.path("evidence").isArray())throw new IllegalArgumentException();
            var values=root.get("evidence").deepCopy();for(int i=0;i<values.size();i++) {
                var value=values.get(i);if(!value.isObject()||value.path("number").asInt()!=i+1||!value.path("id").asString().equals("e"+(i+1)))throw new IllegalArgumentException();
                ((ObjectNode)value).remove("id");((ObjectNode)value).remove("number");
            }
            var units=SchemaContracts.JSON.convertValue(values,new tools.jackson.core.type.TypeReference<List<com.nori.ai.gateway.core.workflow.EvidenceUnit>>(){});
            if(!com.nori.ai.gateway.core.evidence.EvidenceJson.write(root).equals(com.nori.ai.gateway.core.workflow.EvidenceSelection.json(units)))throw new IllegalArgumentException();
            prepared=grounded(job,input.question(),units,input.matchedHistory());
        }catch(IllegalArgumentException invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ComposerService.class.getName()),"COMPOSER_SERVICE_FAILED",invalid);observer.diagnostic(job.jobId(),CallType.COMPOSER,LlmDiagnostic.COMPOSE_SELECTION);return groundedResult(job,GroundedAnswerAssembly.fallback(List.of()),List.of(),0,List.of());}
        if(prepared.units().isEmpty()||prepared.units().stream().allMatch(GroundedAnswerAssembly::summary))
            return assembleGrounded(job,prepared,new FactCheck.Checked(List.of(),false),0);
        try {
            if(answers.count(prepared.output().schema())>evidenceTokens){observer.diagnostic(job.jobId(),CallType.COMPOSER,LlmDiagnostic.OUTPUT_CONSTRAINT_BUDGET);return failed(ComposerOutcome.INPUT_BUDGET_EXCEEDED,0,prepared.reduction().steps());}
            if(!fits(prepared))return failed(ComposerOutcome.INPUT_BUDGET_EXCEEDED,0,prepared.reduction().steps());
        }catch(IllegalArgumentException uncountable){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ComposerService.class.getName()),"COMPOSER_SERVICE_FAILED",uncountable);return failed(ComposerOutcome.LLM_UNAVAILABLE,0,prepared.reduction().steps());}
        expiry.assertLive(job.cancellation());InferenceClient.ChatOutcome outcome;
        try(var observed=observer.open(job.jobId(),CallType.COMPOSER,prepared.reduction().messages())) {
            outcome=inference.chat(CallType.COMPOSER,prepared.reduction().messages(),budget.requestMaxTokens(),job.cancellation(),prepared.output());
        }
        String failure=outcome instanceof InferenceClient.Completed completed?AnswerCheck.failure(completed,budget,answers):null;
        audit.record(LlmCallAudit.attempt(job.scope(),job.conversationId(),job.jobId(),CallType.COMPOSER,budget.requestMaxTokens(),outcome,failure==null?null:ErrorCode.LLM_UNAVAILABLE,prepared.reduction().steps()));
        expiry.assertLive(job.cancellation());
        if(outcome instanceof InferenceClient.BudgetExceeded)return failed(ComposerOutcome.INPUT_BUDGET_EXCEEDED,0,prepared.reduction().steps());
        if(!(outcome instanceof InferenceClient.Completed completed)||failure!=null)return failed(ComposerOutcome.LLM_UNAVAILABLE,1,prepared.reduction().steps());
        var checked=factCheck.evaluate(completed.content(),prepared.units(),input.businessTargets(),reason->observer.diagnostic(job.jobId(),CallType.COMPOSER,reason));
        return assembleGrounded(job,prepared,checked,1);
    }
    private Result assembleGrounded(JobContext job,Grounded prepared,FactCheck.Checked checked,int calls) {
        var presentation=presentations.getOrDefault(job.scope().boundSystem(),defaultPresentation);
        var answer=GroundedAnswerAssembly.assemble(checked,prepared.units(),presentation.descriptions(),presentation.tableTools());
        return groundedResult(job,answer,prepared.units(),calls,prepared.reduction().steps());
    }
    private Result groundedResult(JobContext job,com.nori.ai.gateway.core.result.GroundedAnswer answer,List<com.nori.ai.gateway.core.workflow.EvidenceUnit> units,int calls,List<PromptReducer.Step> steps) {
        answer=QuestionAnswerShape.apply(job.requestInput().maskedQuestion(),answer,reason->observer.diagnostic(job.jobId(),CallType.COMPOSER,reason));
        // 최종 형식 검사가 거절해도 작성된 근거 경로는 PARTIAL과 원문 참조를 유지합니다.
        if(answer.kind().equals("NO_EVIDENCE"))answer=GroundedAnswerAssembly.fallback(units);
        return new Result(ComposerOutcome.COMPOSED,Optional.of(answer.text()),calls,0,0,steps,Optional.of(answer));
    }

    /** system = 고정 지시 + boundSystem 제약, user = 질문과 data 구획(canonical JSON). 같은 draft 는 같은 메시지를 만든다. */
    static List<ChatMessage> messages(PromptDraft draft, List<EvidenceDigest> evidence) { return messages(draft, evidence, List.of()); }

    static List<ChatMessage> messages(PromptDraft draft, List<EvidenceDigest> evidence, List<CoverageDigest> coverage) {
        ObjectNode user = SchemaContracts.JSON.createObjectNode();
        user.put("question", draft.currentQuestion());
        ObjectNode data = user.putObject("data");
        var digests = data.putArray("evidence");
        for (int i = 0; i < evidence.size(); i++) {
            var item = digests.addObject().put("id", "e" + (i + 1)).put("sourceType", evidence.get(i).sourceType());
            item.put("eventTime", evidence.get(i).eventTime()); item.put("observedAt", evidence.get(i).observedAt());
        }
        var history = data.putArray("history");
        for (var turn : draft.history()) history.addObject().put("user", turn.user()).put("assistant", turn.assistant());
        var chunks = data.putArray("ragChunks");
        for (var chunk : draft.ragChunks()) chunks.addObject().put("id", chunk.id()).put("text", chunk.text());
        data.put("logSummary", draft.logSummary());
        var lines = data.putArray("rawLogLines"); draft.rawLogLines().forEach(lines::add);
        var tools = data.putArray("toolResults");
        for (var tool : draft.toolResults()) tools.addObject().put("ref", tool.ref()).put("text", tool.text());
        if (!coverage.isEmpty()) {
            var needs = data.putArray("coverage");
            for (var value : coverage) {
                var item = needs.addObject().put("need", value.need()).put("status", value.status());
                var sources = item.putArray("sources"); value.sources().forEach(sources::add);
                var missing = item.putArray("missingItems"); value.missingItems().forEach(missing::add);
            }
        }
        return List.of(new ChatMessage(ChatMessage.Role.SYSTEM, draft.systemPrompt() + "\n" + draft.boundSystemConstraint()),
            new ChatMessage(ChatMessage.Role.USER, ClassificationService.canonical(user)));
    }

    private static Result composed(String content, int evidenceCount, String data, int calls, List<PromptReducer.Step> steps) {
        StringBuilder cleaned = new StringBuilder();
        int citationViolations = 0;
        Matcher citation = CITATION.matcher(content);
        while (citation.find()) {
            long index = Long.parseLong(citation.group(1));
            boolean valid = index >= 1 && index <= evidenceCount;
            if (!valid) citationViolations++;
            citation.appendReplacement(cleaned, valid ? Matcher.quoteReplacement(citation.group()) : "");
        }
        citation.appendTail(cleaned);
        String narrative = cleaned.toString();
        int numberViolations = 0;
        Matcher number = NUMBER.matcher(CITATION.matcher(narrative).replaceAll(""));
        while (number.find()) if (!data.contains(number.group())) numberViolations++;
        return new Result(ComposerOutcome.COMPOSED, Optional.of(narrative), calls, citationViolations, numberViolations, steps);
    }

    private static Result failed(ComposerOutcome outcome, int calls, List<PromptReducer.Step> steps) {
        return new Result(outcome, Optional.empty(), calls, 0, 0, steps);
    }
}
