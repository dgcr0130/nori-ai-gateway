package com.nori.ai.gateway.app.inference;

import com.nori.ai.gateway.app.StackContextSupport;
import com.nori.ai.gateway.app.inference.OllamaEndpointStub.Disconnect;
import com.nori.ai.gateway.app.inference.OllamaEndpointStub.Json;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.budget.PreCallBudgetAssert;
import com.nori.ai.gateway.core.budget.PromptTokenCounting;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.budget.TruncationDetector;
import com.nori.ai.gateway.core.job.GenerationSemaphore;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.port.InferenceClient.CallFailed;
import com.nori.ai.gateway.core.port.InferenceClient.Completed;
import com.nori.ai.gateway.core.port.InferenceClient.FailureReason;
import com.nori.ai.gateway.integration.inference.ChatTransport;
import com.nori.ai.gateway.integration.inference.InferenceReadinessService;
import com.nori.ai.gateway.integration.inference.OpenAiReadinessProbe;
import com.nori.ai.gateway.integration.inference.SpringAiOpenAiInferenceClient;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import static com.nori.ai.gateway.app.inference.OllamaEndpointStub.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 실제 앱 컨텍스트의 {@link SpringAiOpenAiInferenceClient}·Spring AI 모델·예산 guard·tokenizer 계수기를 JDK HttpServer Ollama stub 에 붙여
 * 호출 직전 예산 경로(전송 0), wire 계약, 재시도 0, sentinel 비노출, 기동 거부를 확인한다(guard·계수기 fake 0).
 */
@Tag("stage-F2") @Tag("label-LLM_STUB")
class InferenceRequestContractIT extends StackContextSupport {
    private static final String PROMPT_SENTINEL = "PSNTL9q4w";
    private static final String QUESTION_SENTINEL = "QSNTL7k2z";
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private OllamaEndpointStub stub;
    private final AtomicInteger usagePrompt = new AtomicInteger(40);
    private final List<byte[]> otlp = new CopyOnWriteArrayList<>();
    private HttpServer otlpServer;
    private String baseUrl;


    @BeforeEach void stubs() throws Exception {
        stub = new OllamaEndpointStub(request -> request.path().equals("/v1/chat/completions") && !isReadiness(request) ? new Json(200, completion("답변", usagePrompt.get())) : healthy(request));
        baseUrl = stub.baseUrl() + "/v1";
        otlpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        otlpServer.createContext("/", exchange -> { try (exchange) { otlp.add(exchange.getRequestBody().readAllBytes()); exchange.sendResponseHeaders(200, -1); } });
        otlpServer.start();
    }
    @AfterEach void stopStubs() { stub.close(); otlpServer.stop(0); }

    @Override protected Map<String,Object> extraApplicationProperties() {
        var properties = new HashMap<String,Object>(Map.of("ai.gateway.inference.mode", "enabled",
            "ai.gateway.otlp.endpoint", "http://127.0.0.1:" + otlpServer.getAddress().getPort(), "ai.gateway.otlp.traces.enabled", true, "ai.gateway.otlp.logs.enabled", true));
        if (baseUrl != null) properties.put("ai.gateway.llm.baseUrl", baseUrl);
        return properties;
    }

    private void awaitReadinessAndClear() {
        var readiness=context.getBean(InferenceReadinessService.class);
        readiness.stop();readiness.refresh();assertThat(readiness.ready()).isTrue();stub.clear();
    }

    private InferenceClient client() { return context.getBean(InferenceClient.class); }
    private static List<ChatMessage> input(int fillers) {
        return List.of(new ChatMessage(ChatMessage.Role.SYSTEM, "TC 질문을 분류한다 " + PROMPT_SENTINEL), new ChatMessage(ChatMessage.Role.USER, "질문 " + QUESTION_SENTINEL + ":" + " token".repeat(fillers)));
    }
    /** 실제 앱 계수기로 렌더 전체 계수가 정확히 target 인 메시지를 만든다. */
    private List<ChatMessage> messagesOf(int target) {
        var counter = context.getBean(PromptTokenCounting.class);
        var messages = input(target - counter.count(input(0)));
        assertThat(counter.count(messages)).as("입력 구성 계수").isEqualTo(target);
        return messages;
    }
    private static int max(CallType type) { return TokenBudgetPolicy.current().budget(type).requestMaxTokens(); }

    @Test void budgetGuardStopsBeforeHttpAndTheExactContextBoundarySendsOnce() {
        start();
        awaitReadinessAndClear();
        assertThat(context.getBean(InferenceClient.class)).isExactlyInstanceOf(SpringAiOpenAiInferenceClient.class);
        assertThat(client().chat(CallType.CLASSIFY, messagesOf(5_121), max(CallType.CLASSIFY))).isEqualTo(InferenceClient.ChatOutcome.budgetExceeded(5_121));
        assertThat(stub.count("/v1/chat/completions")).as("분류 상한 초과 → 수신 0").isZero();
        usagePrompt.set(5_120);
        assertThat(client().chat(CallType.CLASSIFY, messagesOf(5_120), max(CallType.CLASSIFY))).as("5,120 + 2,048 + reserve 1,024 = 8,192").isInstanceOf(Completed.class);
        assertThat(stub.count("/v1/chat/completions")).isOne();
        assertThat(client().chat(CallType.COMPOSER, messagesOf(4_609), max(CallType.COMPOSER))).as("4,609 + 3,072 + reserve 512 = 8,193").isEqualTo(InferenceClient.ChatOutcome.budgetExceeded(4_609));
        assertThat(stub.count("/v1/chat/completions")).isOne();
        // P18 시험 전용 정책(T_cmp=5,249·요청 5,377)을 실제 계수기·전송 빈과 함께 쓰는 Client 로 총합 검사를 확인한다(운영값 변경 0).
        var current = TokenBudgetPolicy.current();
        var fixture = new TokenBudgetPolicy(Map.of(CallType.CLASSIFY, current.budget(CallType.CLASSIFY), CallType.PLAN, current.budget(CallType.PLAN),
            CallType.COMPOSER, new TokenBudgetPolicy.CallBudget(2_816, 128, 5_249)));
        var fixtureClient = new SpringAiOpenAiInferenceClient(context.getBean(ChatTransport.class), context.getBean(OpenAiEmbeddingModel.class),
            new PreCallBudgetAssert(context.getBean(PromptTokenCounting.class), fixture), context.getBean(TruncationDetector.class), context.getBean(GenerationSemaphore.class));
        assertThat(fixtureClient.chat(CallType.COMPOSER, messagesOf(2_815), 5_377)).as("2,815 + 5,377 + reserve 1 = 8,193").isEqualTo(InferenceClient.ChatOutcome.budgetExceeded(2_815));
        assertThat(stub.count("/v1/chat/completions")).isOne();
        usagePrompt.set(2_814);
        assertThat(fixtureClient.chat(CallType.COMPOSER, messagesOf(2_814), 5_377)).as("2,814 + 5,377 + reserve 1 = 8,192").isInstanceOf(Completed.class);
        assertThat(stub.count("/v1/chat/completions")).isEqualTo(2);
        usagePrompt.set(39);
        assertThat(client().chat(CallType.CLASSIFY, messagesOf(40), max(CallType.CLASSIFY))).as("usage.prompt_tokens < 계수 → 그 호출 실패")
            .isInstanceOfSatisfying(CallFailed.class, failed -> assertThat(failed.reason()).isEqualTo(FailureReason.TRUNCATED));
        assertThat(stub.count("/v1/chat/completions")).isEqualTo(3);
    }

    @Test void wireUsesOnlyTheThreeExactPathsAllowedBodyKeysNoNumCtxAndNoAuthorization() {
        start();
        awaitReadinessAndClear();
        assertThat(client().chat(CallType.CLASSIFY, messagesOf(40), max(CallType.CLASSIFY))).isInstanceOf(Completed.class);
        assertThat(client().embed(List.of("probe"))).singleElement().satisfies(vector -> assertThat(vector).hasSize(3));

        context.getBean(InferenceReadinessService.class).refresh();
        assertThat(stub.received()).extracting(Received::path).containsOnly("/v1/models", "/v1/chat/completions", "/v1/embeddings").contains("/v1/models", "/v1/chat/completions", "/v1/embeddings");
        assertThat(stub.received()).allSatisfy(request -> assertThat(request.method()).isEqualTo(request.path().equals("/v1/models") ? "GET" : "POST"));
        var chat = stub.received().stream().filter(request -> request.path().equals("/v1/chat/completions")).findFirst().orElseThrow();
        JsonNode body = JSON.readTree(chat.body());
        assertThat(Set.copyOf(body.propertyNames())).isEqualTo(Set.of("model", "messages", "max_tokens", "temperature", "stream"));
        assertThat(body.get("model").asString()).isEqualTo("qwen3:4b");
        assertThat(body.get("max_tokens").asInt()).isEqualTo(max(CallType.CLASSIFY));
        assertThat(chat.body()).doesNotContain("num_ctx", "\"options\"");
        var embedding = stub.received().stream().filter(request -> request.path().equals("/v1/embeddings")).findFirst().orElseThrow();
        assertThat(Set.copyOf(JSON.readTree(embedding.body()).propertyNames())).isEqualTo(Set.of("model", "input", "encoding_format"));
        for (var request : stub.received()) if (!request.path().equals("/v1/models"))
            assertThat(request.header("Authorization")).as("자격 파일 생략 시 Authorization 없음").isNull();
    }

    @Test @ExtendWith(OutputCaptureExtension.class)
    void serverErrorDisconnectAndEmptyChoicesAreOneRequestPerAttemptAndLeakNoSentinel(CapturedOutput output) {
        start();
        awaitReadinessAndClear();
        var messages = messagesOf(60);
        var outcomes = new ArrayList<InferenceClient.ChatOutcome>();
        inRequestSpan(() -> {
            stub.respond(request -> request.path().equals("/v1/chat/completions") ? new Json(500, "{\"error\":{\"message\":\"" + PROMPT_SENTINEL + "\"}}") : healthy(request));
            outcomes.add(client().chat(CallType.CLASSIFY, messages, max(CallType.CLASSIFY)));
            assertThat(stub.count("/v1/chat/completions")).as("5xx").isOne();
            stub.respond(request -> request.path().equals("/v1/chat/completions") ? new Disconnect() : healthy(request));
            outcomes.add(client().chat(CallType.CLASSIFY, messages, max(CallType.CLASSIFY)));
            assertThat(stub.count("/v1/chat/completions")).as("연결 끊김").isEqualTo(2);
            stub.respond(request -> request.path().equals("/v1/chat/completions") ? new Json(200, EMPTY_CHOICES) : healthy(request));
            outcomes.add(client().chat(CallType.CLASSIFY, messages, max(CallType.CLASSIFY)));
            assertThat(stub.count("/v1/chat/completions")).as("빈 choices").isEqualTo(3);
            return null;
        });
        assertThat(outcomes).extracting(outcome -> ((CallFailed) outcome).reason()).containsExactly(FailureReason.TRANSPORT, FailureReason.TRANSPORT, FailureReason.EMPTY_RESPONSE);
        assertThat(stub.received()).filteredOn(request -> request.path().equals("/v1/chat/completions"))
            .allSatisfy(request -> assertThat(request.body()).contains(PROMPT_SENTINEL, QUESTION_SENTINEL));
        assertThat(outcomes.toString()).doesNotContain(PROMPT_SENTINEL, QUESTION_SENTINEL);
        context.close(); context = null;
        assertThat(otlp).as("OTLP 수신 경로가 실제로 동작").isNotEmpty();
        String exported = otlp.stream().map(bytes -> new String(bytes, StandardCharsets.UTF_8)).reduce("", String::concat);
        assertThat(exported).doesNotContain(PROMPT_SENTINEL, QUESTION_SENTINEL);
        assertThat(output.getAll()).doesNotContain(PROMPT_SENTINEL, QUESTION_SENTINEL);
    }

    @Test void readinessMapsUnauthorizedErrorsAndNonJsonToEndpointErrorNeverModelLoading() {
        start();
        awaitReadinessAndClear();
        var readiness = context.getBean(InferenceReadinessService.class);
        for (var reply : List.of(new Json(401, "{}"), new Json(403, "{}"), new Json(500, "{}"), new Json(200, "<html>not json</html>"))) {
            stub.respond(request -> reply);
            var snapshot = readiness.refresh();
            assertThat(snapshot.state()).as(reply.toString()).isEqualTo(OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE);
            assertThat(snapshot.reason()).as(reply.toString()).isEqualTo(OpenAiReadinessProbe.Reason.ENDPOINT_ERROR);
        }
        assertThat(stub.received()).extracting(Received::path).containsOnly("/v1/models");
    }

    @Test void startupRejectsMissingPrefixPortAndTrailingSlashWithoutContactingTheEndpoint() throws Exception {
        database = new com.nori.ai.gateway.persistence.testing.PgTestDatabase(System.getProperty("aigw.pgTestImage"), java.nio.file.Path.of(System.getProperty("aigw.rootDir"))).start();
        database.bootstrap(); database.migrate();
        String stubUrl = stub.baseUrl() + "/v1";
        for (String invalid : Arrays.asList(stub.baseUrl(), "http://127.0.0.1", stubUrl + "/")) {
            baseUrl = invalid;
            assertThatThrownBy(() -> startApplication()).as(String.valueOf(invalid)).hasRootCauseMessage("추론 endpoint 설정이 잘못되었습니다");
        }
        baseUrl = stubUrl;
        assertThat(stub.received()).as("기동 거부 경로의 추론 endpoint 수신 0").isEmpty();
        startApplication();
        long deadline = System.nanoTime() + java.time.Duration.ofSeconds(10).toNanos();
        while (!context.getBean(InferenceReadinessService.class).ready() && System.nanoTime() < deadline) Thread.sleep(20);
        assertThat(context.getBean(InferenceReadinessService.class).ready()).as("정상 값이면 공통 최소 요청으로 READY").isTrue();
    }
}
