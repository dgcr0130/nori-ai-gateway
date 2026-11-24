package com.nori.ai.gateway.app.golden;

import com.nori.ai.gateway.app.config.*;
import com.nori.ai.gateway.app.job.*;
import com.nori.ai.gateway.app.observability.AuditRecorder;
import com.nori.ai.gateway.app.security.ScopeAssembler;
import com.nori.ai.gateway.app.web.ResultResponseAssembler;
import com.nori.ai.gateway.core.authz.*;
import com.nori.ai.gateway.core.blocks.*;
import com.nori.ai.gateway.core.cache.ToolResultCache;
import com.nori.ai.gateway.core.dispatch.ToolDispatcher;
import com.nori.ai.gateway.core.evidence.EvidenceAssembler;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.llm.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.seam.PlanInjectionSeam;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.integration.inference.*;
import com.nori.ai.gateway.integration.tokenizer.*;
import com.nori.ai.gateway.persistence.queue.*;
import com.nori.ai.gateway.persistence.repository.*;
import com.openai.client.OpenAIClient;
import java.io.IOException;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** DB 입출력과 계획 검증 다음의 실행만 대역으로 격리하며 분류·기간·계획 코드는 그대로 호출합니다. */
final class GoldenClassificationAdapter implements AutoCloseable {
    private final CoreBeansConfiguration core = new CoreBeansConfiguration();
    private final StandardEnvironment environment = new StandardEnvironment();
    private final GatewaySystemsProperties systems;
    private final CapabilityRegistry registry;
    private final HfTokenizerCounter tokenizer;
    private final PromptTokenCounter counter;
    private final OpenAIClient http;
    private final RecordingTransport transport;
    private final SpringAiOpenAiInferenceClient inference;
    private final LlmSettings settings;
    private final ExpiryCheckpoint expiry = new ExpiryCheckpoint();
    private JsonNode clarificationReason;

    GoldenClassificationAdapter(Path root) throws IOException {
        for (var source : new YamlPropertySourceLoader().load("golden-app-config",
                new FileSystemResource(root.resolve("config/ai-gateway.yaml")))) {
            environment.getPropertySources().addLast(source);
        }
        // Gradle 시험의 cwd는 앱 모듈이므로 운영의 저장소 루트 기준 상대 경로를 같은 파일의 절대 경로로 바꿉니다.
        var initial = LlmSettings.load(environment);
        Path tokenizerPath = Path.of(initial.tokenCounting().tokenizerFile());
        if (!tokenizerPath.isAbsolute()) environment.getPropertySources().addFirst(new MapPropertySource(
            "golden-tokenizer-location", Map.of("ai.gateway.llm.tokenCounting.tokenizerFile", root.resolve(tokenizerPath).toString())));
        settings = LlmSettings.load(environment);
        if (settings.requestTimeout().compareTo(Duration.ofSeconds(180)) > 0) throw new IllegalArgumentException("LLM timeout 상한은 180초입니다");
        systems = GatewaySystemsProperties.load(environment);
        registry = bean(core, "capabilityRegistry", systems, new ContractVersionPolicy());
        var endpoint = new InferenceEndpointConfig(settings);
        tokenizer = new HfTokenizerCounter(settings);
        OpenAIClient created = null;
        try {
            var models = new InferenceModelConfiguration();
            created = bean(models, "inferenceOpenAiClient", endpoint);
            var chat = GoldenClassificationAdapter.<OpenAiChatModel>bean(models, "openAiChatModel", created, endpoint, settings.policy());
            var embeddings = GoldenClassificationAdapter.<OpenAiEmbeddingModel>bean(models, "openAiEmbeddingModel", created, endpoint);
            var renderer = new ConfiguredChatTemplateRenderer(settings.tokenCounting().template());
            counter = new PromptTokenCounter(renderer, tokenizer);
            transport = new RecordingTransport(bean(models, "springAiChatTransport", chat, renderer));
            inference = new SpringAiOpenAiInferenceClient(transport, embeddings,
                new com.nori.ai.gateway.core.budget.PreCallBudgetAssert(counter, settings.policy(), tokenizer::countText),
                new com.nori.ai.gateway.core.budget.TruncationDetector(), new GenerationSemaphore(settings.generationConcurrency()), endpoint);
            http = created;
        } catch (RuntimeException | Error failure) {
            try { if (created != null) created.close(); }
            catch (RuntimeException cleanup) { failure.addSuppressed(cleanup); }
            try { tokenizer.close(); }
            catch (RuntimeException cleanup) { failure.addSuppressed(cleanup); }
            throw failure;
        }
    }

    JsonNode classify(LlmGoldenClassificationTest.GoldenCase value) {
        transport.responses.clear();
        transport.calls = 0;
        clarificationReason = null;
        String jobId = "golden-" + value.id(), conversation = "conversation-" + value.id();
        Instant now = OffsetDateTime.parse(value.referenceTime()).toInstant();
        var clock = Clock.fixed(now, ZoneId.of(value.timezone()));
        var permissions = new HashSet<String>();
        registry.all().stream().filter(tool -> tool.ownerSystem().equals(OwnerSystem.TC))
            .forEach(tool -> permissions.addAll(tool.requiredPermission()));
        var queued = new JobScopeRecord(OwnerSystem.TC, "golden-user", permissions, "0".repeat(64), jobId, now, conversation, "RUNNING");
        var scopes = new ScopeAssembler();
        var scope = scopes.forWorker(queued);
        var input = new JobRequestInput("message-" + value.id(), value.historyPairs().size() + 1L, value.question(),
            Map.of("context", Map.of("timezone", value.timezone(), "referenceTime", value.referenceTime())));
        var jobs = mock(JobRepository.class);
        when(jobs.find(any(), eq(jobId))).thenReturn(Optional.of(job(jobId, input.jobSeq(), conversation, now, null)));
        when(jobs.findRequestInput(any(), eq(jobId))).thenReturn(Optional.of(input));
        var messages = mock(MessageRepository.class);
        when(messages.findPreviousResolvedTimeWindow(any(), anyString(), anyString())).thenReturn(Optional.empty());
        when(messages.updateResolvedTimeWindow(any(), anyString(), anyString(), anyMap())).thenReturn(true);
        var access = mock(ResultResponseAssembler.class);
        history(value, scope, conversation, now, messages, jobs, access);
        var inputs = new ClassificationInputs(systems, registry, messages, access,
            environment.getProperty("ai.gateway.classification.historyPairs", Integer.class, 2), jobs);
        var observed = new AtomicReference<JsonNode>();
        // package-private factory를 직접 호출하여 CLASSIFY_PROMPT 경로·자원 로딩과 실제 앱 배선을 함께 재사용합니다.
        ClassificationService classifier = spy(GoldenClassificationAdapter.<ClassificationService>bean(core,
            "classificationService", inference, settings.policy(), tokenizer,
            counter, expiry, Optional.empty(), (LlmCallAudit) attempt -> {}, inputs));
        doAnswer(invocation -> {
            try { return invocation.callRealMethod(); }
            catch (Exception failure) {
                observed.set(rejected("CLASSIFY: " + failure.getClass().getName() + ": " + failure.getMessage()));
                throw failure;
            }
        }).when(classifier).classify(any());

        var observedPlan = new AtomicReference<WorkflowExecutionPlan>();
        var preparation = new AtomicReference<PlanValidator.Preparation>();
        var builder = spy(new WorkflowPlanBuilder(registry));
        doAnswer(invocation -> {
            var plan = (WorkflowExecutionPlan) invocation.callRealMethod();
            observedPlan.set(plan);
            return plan;
        }).when(builder).build(any(), any(), any(), anyMap());
        PlanValidator validator = spy(GoldenClassificationAdapter.<PlanValidator>bean(core, "planValidator", registry, new PlanInjectionSeam.NoOp(), clock));
        doAnswer(invocation -> {
            preparation.set((PlanValidator.Preparation) invocation.callRealMethod());
            // 실제 executor가 실제 validator를 끝낸 바로 다음에서 멈춰 외부 도구·Composer 호출을 막습니다.
            throw new PlanObserved();
        }).when(validator).prepare(any(), any(), any());
        SourcePermissionMap sources = bean(core, "sourcePermissionMap", registry);
        var executor = new com.nori.ai.gateway.core.workflow.WorkflowExecutor(registry, validator,
            mock(ToolDispatcher.class), new SourceAuthorizationStep(sources), expiry, clock, mock(ToolResultCache.class),
            new BlockBuilder(new UiSchemaValidator()), mock(com.nori.ai.gateway.core.workflow.WorkflowExecutor.ExecutionRecorder.class),
            new LogSummarizer(), new GeneralQueryHelpBuilder());
        var adapter = new WorkflowExecutionAdapter(registry, builder, executor,
            new WorkflowResourceResolverFactory(sources, registry, expiry), new TimeWindowResolver(), messages, expiry,
            new ContentSanitizer(), new TerminalOutcomeAssembler(), new ConversationSummaryBuilder(), new EvidenceAssembler(),
            mock(AuditRecorder.class), Optional.empty(), new LogSummarizer(), Optional.empty(), Optional.empty());
        com.nori.ai.gateway.core.job.WorkflowExecutor workflow = context -> {
            try { return adapter.execute(context); }
            catch (PlanObserved boundary) {
                observed.set(planSummary(observedPlan.get(), preparation.get()));
                return TerminalSpec.outcome(JobStatus.SUCCEEDED, ResultStatus.SUCCESS, null, Map.of());
            } catch (Exception failure) {
                observed.set(rejected(failure.getClass().getName() + ": " + failure.getMessage()));
                throw failure;
            }
        };
        var terminal = mock(JobTerminalCommitter.class);
        when(terminal.commit(eq(jobId), any())).thenAnswer(invocation -> {
            if (observed.get() == null) observed.set(terminalSummary(invocation.getArgument(1)));
            return true;
        });
        var delegations = mock(JobDelegationReader.class);
        // 운영 DelegatedCall이 요구하는 22~128자의 불투명 식별자 형식을 지킵니다.
        when(delegations.forWorker(jobId)).thenReturn(Optional.of("golden-classification-delegation"));
        var slots = new SlotLedger();
        var token = slots.claim(jobId);
        token.deadline(Instant.now().plusSeconds(180), Clock.systemUTC());
        var worker = new JobWorker(delegations, scopes, systems, workflow, terminal, jobs,
            new InferenceModeProperties(InferenceMode.ENABLED), Optional.empty(), Optional.empty(), Optional.empty(),
            slots, jobs, Optional.of(classifier));
        try {
            worker.run(queued, token);
            return Objects.requireNonNull(observed.get(), "운영 경로가 결과를 남기지 않았습니다");
        } finally {
            slots.release(jobId);
        }
    }

    private void history(LlmGoldenClassificationTest.GoldenCase value, VerifiedSystemScope scope, String conversation,
            Instant now, MessageRepository messages, JobRepository jobs, ResultResponseAssembler access) {
        var stored = new ArrayList<MessageRepository.Message>();
        for (int i = 0; i < value.historyPairs().size(); i++) {
            var pair = value.historyPairs().get(i);
            String previousJob = "previous-" + value.id() + "-" + i, message = previousJob + "-question";
            String refs = SchemaContracts.JSON.writeValueAsString(pair.resourceRefs());
            stored.add(new MessageRepository.Message(message, "user", pair.question(), refs, "[]", null, null));
            stored.add(new MessageRepository.Message(previousJob + "-answer", "assistant", pair.answer(), refs, "[]", previousJob, null));
            var previousInput = new JobRequestInput(message, i + 1L, pair.question(), Map.of("resourceRefs", pair.resourceRefs()));
            when(jobs.findRequestInput(any(), eq(previousJob))).thenReturn(Optional.of(previousInput));
            // 등록 정보 답변은 문서 인용이 없으므로 CitedHistoryInputs가 문서 historyRef를 만들지 않습니다.
            var response = Map.<String,Object>of("jobId", previousJob, "result", Map.of("payload", Map.of("answer",
                Map.of("kind", "ANSWER", "text", pair.answer(), "citations", List.of()))));
            when(access.assemble(any(), eq(previousJob))).thenReturn(new ResultResponseAssembler.Access(scope,
                job(previousJob, i + 1L, conversation, now.minusSeconds(60), now.minusSeconds(30)),
                new ConversationRepository.Conversation(conversation, "TC", scope.sub(), now.minusSeconds(60), now, "ACTIVE"), response, true));
        }
        when(messages.findByConversation(any(), eq(conversation))).thenReturn(stored);
    }

    private static JobRepository.Job job(String id, long seq, String conversation, Instant created, Instant terminal) {
        return new JobRepository.Job(id, seq, conversation, "TC", "golden-user", terminal == null ? "RUNNING" : "SUCCEEDED",
            created, created, terminal, null, null, null, terminal == null ? null : id, id, "digest", "/golden");
    }

    private static JsonNode planSummary(WorkflowExecutionPlan plan, PlanValidator.Preparation preparation) {
        if (preparation instanceof PlanValidator.Rejected failure) return rejected("PLAN: " + SchemaContracts.JSON.writeValueAsString(failure.outcome()));
        if (preparation instanceof PlanValidator.ReadRejected failure) return rejected("PLAN_READ: " + failure.status());
        if (!(preparation instanceof PlanValidator.Prepared)) return rejected("계획 검증 결과가 없습니다");
        var summary = summary(plan.definition().answerSource() == WorkflowDefinition.AnswerSource.REGISTRY_VIEW ? "HELP" : "PLAN");
        summary.put("documentSearchRequired", plan.documents().required());
        var calls = summary.withArray("toolCalls");
        for (var step : plan.toolPlan().plan()) {
            var call = calls.addObject().put("toolId", step.capabilityId());
            call.set("args", step.arguments().deepCopy());
        }
        return summary;
    }

    private JsonNode terminalSummary(TerminalSpec terminal) {
        var clarification = SchemaContracts.JSON.valueToTree(terminal.resultPayload()).path("clarification");
        if (clarification.isObject()) {
            clarificationReason = clarification.get("reason");
            var summary = summary("CLARIFICATION");
            summary.set("clarificationFields", clarification.path("fields").deepCopy());
            return summary;
        }
        if (terminal.resultStatus() == ResultStatus.UNSUPPORTED) return summary("UNSUPPORTED");
        return rejected("JOB: " + terminal.jobStatus() + "/" + terminal.resultStatus() + "/" + terminal.errorCode()
            + " " + SchemaContracts.JSON.writeValueAsString(terminal.resultPayload()));
    }

    static ObjectNode rejected(String reason) { return summary("REJECTED").put("reason", reason); }
    private static ObjectNode summary(String kind) {
        var summary = SchemaContracts.JSON.createObjectNode().put("kind", kind).put("documentSearchRequired", false);
        summary.putArray("toolCalls");
        summary.putArray("clarificationFields");
        return summary;
    }

    boolean nullable(String toolId, String field) {
        return registry.find(OwnerSystem.TC, toolId).map(tool -> tool.inputSchema().path("properties").get(field))
            .filter(Objects::nonNull).map(schema -> SchemaContracts.matches(schema, SchemaContracts.parse("null"))).orElse(false);
    }
    int calls() { return transport.calls; }
    List<Map<String,Object>> responses() { return List.copyOf(transport.responses); }
    JsonNode clarificationReason() { return clarificationReason; }

    // 테스트 전용 패키지 안에서만 접근을 열어 제품 상수·factory의 가시성과 파일을 바꾸지 않습니다.
    private static <T> T bean(Object configuration, String method, Object... arguments) {
        return Objects.requireNonNull(ReflectionTestUtils.invokeMethod(configuration, method, arguments), method);
    }
    @Override public void close() {
        try { http.close(); }
        finally { tokenizer.close(); }
    }
    private static final class PlanObserved extends RuntimeException {}

    private static final class RecordingTransport implements ChatTransport {
        private final ChatTransport delegate;
        private final List<Map<String,Object>> responses = new ArrayList<>();
        private int calls;
        RecordingTransport(ChatTransport delegate) { this.delegate = delegate; }
        @Override public Response send(com.nori.ai.gateway.core.budget.CallType type,
                List<com.nori.ai.gateway.core.budget.ChatMessage> messages, int maxTokens) {
            throw new IllegalStateException("운영의 timeout·구조화 출력 진입점이 필요합니다");
        }
        @Override public Response send(com.nori.ai.gateway.core.budget.CallType type,
                List<com.nori.ai.gateway.core.budget.ChatMessage> messages, int maxTokens, Duration timeout, StructuredOutput output) {
            calls++;
            long started = System.nanoTime();
            var record = new LinkedHashMap<String,Object>();
            record.put("call", calls);
            record.put("timeoutMillis", timeout.toMillis());
            record.put("rawResponse", null);
            try {
                var response = delegate.send(type, messages, maxTokens, timeout, output);
                record.put("rawResponse", response == null ? null : response.content());
                record.put("finishReason", response == null ? null : response.finishReason());
                record.put("promptTokens", response == null ? null : response.promptTokens());
                record.put("completionTokens", response == null ? null : response.completionTokens());
                return response;
            } catch (RuntimeException failure) {
                record.put("failureClass", failure.getClass().getName());
                throw failure;
            } finally {
                record.put("elapsedMillis", Duration.ofNanos(System.nanoTime() - started).toMillis());
                responses.add(record);
            }
        }
    }
}
