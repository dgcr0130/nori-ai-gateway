package com.nori.ai.gateway.integration.inference;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.budget.PreCallBudgetAssert;
import com.nori.ai.gateway.core.budget.TruncationDetector;
import com.nori.ai.gateway.core.job.GenerationSemaphore;
import com.nori.ai.gateway.core.port.InferenceClient;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * {@link InferenceClient} 의 유일한 구현. chat 은 매 호출 직전 {@link PreCallBudgetAssert} 를 거쳐 초과면 전송 0 으로 돌려주고,
 * 생성 동시성 슬롯 안에서 {@link ChatTransport} 를 1회 호출한 뒤 {@link TruncationDetector} 로 서버 잘림을 확인한다.
 * 공개 실패는 기존 클래스명 계약을 유지하고, 진단 로그에는 자격 값을 가린 메시지·본문·스택을 남깁니다.
 */
@ConditionalOnProperty(name = "ai.gateway.inference.mode", havingValue = "enabled")
public final class SpringAiOpenAiInferenceClient implements InferenceClient {
    private final ChatTransport transport;
    private final OpenAiEmbeddingModel embeddings;
    private final PreCallBudgetAssert budget;
    private final TruncationDetector truncation;
    private final GenerationSemaphore generation;
    private Duration requestTimeout = Duration.ofSeconds(60);
    private Integer embeddingDimension;
    private InferenceEndpointConfig endpoint;
    private static final System.Logger LOG=System.getLogger(SpringAiOpenAiInferenceClient.class.getName());

    public SpringAiOpenAiInferenceClient(ChatTransport transport, OpenAiEmbeddingModel embeddings, PreCallBudgetAssert budget,
            TruncationDetector truncation, GenerationSemaphore generation) {
        this.transport = Objects.requireNonNull(transport, "transport");
        this.embeddings = Objects.requireNonNull(embeddings, "embeddings");
        this.budget = Objects.requireNonNull(budget, "budget");
        this.truncation = Objects.requireNonNull(truncation, "truncation");
        this.generation = Objects.requireNonNull(generation, "generation");
    }

    @org.springframework.beans.factory.annotation.Autowired
    public SpringAiOpenAiInferenceClient(ChatTransport transport, OpenAiEmbeddingModel embeddings, PreCallBudgetAssert budget,
            TruncationDetector truncation, GenerationSemaphore generation, InferenceEndpointConfig endpoint) {
        this(transport, embeddings, budget, truncation, generation);
        requestTimeout = endpoint.settings().requestTimeout();this.endpoint=endpoint;
    }

    @Override public ChatOutcome chat(CallType type, List<ChatMessage> messages, int maxTokens) {
        return chat(type, messages, maxTokens, new com.nori.ai.gateway.core.job.CancellationToken());
    }

    @Override public ChatOutcome chat(CallType type, List<ChatMessage> messages, int maxTokens, com.nori.ai.gateway.core.job.CancellationToken cancellation) {
        return chat(type, messages, maxTokens, cancellation, null);
    }

    @Override public ChatOutcome chat(CallType type, List<ChatMessage> messages, int maxTokens, com.nori.ai.gateway.core.job.CancellationToken cancellation,
            com.nori.ai.gateway.core.llm.StructuredOutput output) {
        var checked = budget.check(type, messages, maxTokens, output);
        if(checked instanceof PreCallBudgetAssert.Exceeded)event(LOG,WARNING,"LLM_CHAT_BUDGET_EXCEEDED","purpose",type,"inputTokens",checked.count(),"maxTokens",maxTokens);
        if (checked instanceof PreCallBudgetAssert.Exceeded exceeded) return ChatOutcome.budgetExceeded(exceeded.count());
        long started = System.nanoTime();
        try {
            Duration left = cancellation.remaining(requestTimeout);
            if (left.isZero() || !generation.acquire(left)) return failed(FailureReason.INTERRUPTED, null, started, checked.count());
        }
        catch (InterruptedException interrupted) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(SpringAiOpenAiInferenceClient.class.getName()),"SPRING_AI_OPEN_AI_INFERENCE_CLIENT_FAILED",interrupted); Thread.currentThread().interrupt(); return failed(FailureReason.INTERRUPTED, null, started, checked.count()); }
        final ChatTransport.Response response;
        Thread calling = Thread.currentThread();
        var terminated = new java.util.concurrent.CountDownLatch(1);
        var inFlight = new com.nori.ai.gateway.core.job.CancellationToken.InFlightCall() {
            @Override public void abort() { calling.interrupt(); }
            @Override public boolean awaitTermination(Duration timeout) throws InterruptedException { return terminated.await(timeout.toNanos(), java.util.concurrent.TimeUnit.NANOSECONDS); }
        };
        cancellation.register(inFlight);
        try {
            Duration left = cancellation.remaining(requestTimeout);
            if (left.isZero() || Thread.currentThread().isInterrupted()) return failed(FailureReason.INTERRUPTED, null, started, checked.count());
            body(LOG,INFO,"LLM_CHAT_REQUEST",com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(messages),"purpose",type,"model",endpoint==null?null:endpoint.settings().generationModel(),"endpoint",endpoint==null?null:endpoint.baseUrl()+"/chat/completions","temperature",endpoint==null?null:endpoint.settings().temperature(),"maxTokens",maxTokens,"schema",output==null?null:type.name()+"_SCHEMA");
            response = transport.send(type, List.copyOf(messages), maxTokens, left, output);
            body(LOG,INFO,"LLM_CHAT_RESPONSE",response==null?null:response.content(),"purpose",type,"durationMs",(System.nanoTime()-started)/1_000_000,"finishReason",response==null?null:response.finishReason(),"promptTokens",response==null?null:response.promptTokens(),"completionTokens",response==null?null:response.completionTokens(),"streaming",false);
            if (cancellation.remaining(requestTimeout).isZero() || Thread.currentThread().isInterrupted()) return failed(FailureReason.INTERRUPTED, null, started, checked.count());
        }
        catch (RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.transportFailure(System.getLogger(SpringAiOpenAiInferenceClient.class.getName()),"SPRING_AI_OPEN_AI_INFERENCE_CLIENT_FAILED",failure); return failed(FailureReason.TRANSPORT, failure.getClass().getName(), started, checked.count()); }
        finally { terminated.countDown(); cancellation.completed(inFlight); generation.release(); }
        Duration latency = Duration.ofNanos(System.nanoTime() - started);
        if (response == null || response.content() == null || response.finishReason() == null) return new CallFailed(FailureReason.EMPTY_RESPONSE, null, latency, checked.count());
        // usage 가 없으면 잘림을 배제할 수 없으므로 잘림과 같이 실패로 본다.
        if (response.promptTokens() == null || response.completionTokens() == null || response.completionTokens() < 0
            || truncation.check(checked.count(), response.promptTokens()) == TruncationDetector.Result.TRUNCATED)
            return new CallFailed(FailureReason.TRUNCATED, null, latency, checked.count());
        return new Completed(response.content(), response.finishReason(), response.promptTokens(), response.completionTokens(), latency, checked.count());
    }

    @Override public synchronized List<float[]> embed(List<String> inputs) {
        var copy = List.copyOf(inputs);
        if (copy.isEmpty()) return List.of();
        long logStarted=System.nanoTime();
        body(LOG,INFO,"LLM_EMBEDDING_REQUEST",com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(copy),"model",endpoint==null?null:endpoint.settings().embeddingModel(),"endpoint",endpoint==null?null:endpoint.baseUrl()+"/embeddings","inputCount",copy.size());
        final List<float[]> vectors;
        try { vectors = embeddings.embed(copy); }
        catch (RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.transportFailure(System.getLogger(SpringAiOpenAiInferenceClient.class.getName()),"SPRING_AI_OPEN_AI_INFERENCE_CLIENT_FAILED",failure); throw new EmbeddingFailed(FailureReason.TRANSPORT, failure.getClass().getName()); }
        if (vectors == null || vectors.size() != copy.size() || vectors.stream().anyMatch(vector -> vector == null || vector.length == 0))
            {event(LOG,WARNING,"LLM_EMBEDDING_INVALID_RESPONSE","reason","EMPTY_OR_INCONSISTENT_VECTOR","inputCount",copy.size());throw new EmbeddingFailed(FailureReason.EMPTY_RESPONSE, null);}
        int dimension = vectors.getFirst().length;
        if (vectors.stream().anyMatch(vector -> vector.length != dimension || !finite(vector))
                || embeddingDimension != null && embeddingDimension != dimension) {event(LOG,WARNING,"LLM_EMBEDDING_INVALID_RESPONSE","reason","EMPTY_OR_INCONSISTENT_VECTOR","inputCount",copy.size());throw new EmbeddingFailed(FailureReason.EMPTY_RESPONSE, null);}
        event(LOG,INFO,"LLM_EMBEDDING_RESPONSE","dimension",dimension,"vectorCount",vectors.size(),"durationMs",(System.nanoTime()-logStarted)/1_000_000);
        embeddingDimension = dimension;
        return List.copyOf(vectors);
    }

    private static boolean finite(float[] vector) { for (float value : vector) if (!Float.isFinite(value)) return false; return true; }

    private static CallFailed failed(FailureReason reason, String errorClass, long started, int inputTokens) {
        return new CallFailed(reason, errorClass, Duration.ofNanos(System.nanoTime() - started), inputTokens);
    }
}
