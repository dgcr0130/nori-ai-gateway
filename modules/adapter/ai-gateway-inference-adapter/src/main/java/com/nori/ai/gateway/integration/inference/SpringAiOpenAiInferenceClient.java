package com.nori.ai.gateway.integration.inference;

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
 * 예외는 클래스명만 남긴다(메시지·응답 본문을 결과·로그에 싣지 않음).
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
        requestTimeout = endpoint.settings().requestTimeout();
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
        if (checked instanceof PreCallBudgetAssert.Exceeded exceeded) return ChatOutcome.budgetExceeded(exceeded.count());
        long started = System.nanoTime();
        try {
            Duration left = cancellation.remaining(requestTimeout);
            if (left.isZero() || !generation.acquire(left)) return failed(FailureReason.INTERRUPTED, null, started, checked.count());
        }
        catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); return failed(FailureReason.INTERRUPTED, null, started, checked.count()); }
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
            response = transport.send(type, List.copyOf(messages), maxTokens, left, output);
            if (cancellation.remaining(requestTimeout).isZero() || Thread.currentThread().isInterrupted()) return failed(FailureReason.INTERRUPTED, null, started, checked.count());
        }
        catch (RuntimeException failure) { return failed(FailureReason.TRANSPORT, failure.getClass().getName(), started, checked.count()); }
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
        final List<float[]> vectors;
        try { vectors = embeddings.embed(copy); }
        catch (RuntimeException failure) { throw new EmbeddingFailed(FailureReason.TRANSPORT, failure.getClass().getName()); }
        if (vectors == null || vectors.size() != copy.size() || vectors.stream().anyMatch(vector -> vector == null || vector.length == 0))
            throw new EmbeddingFailed(FailureReason.EMPTY_RESPONSE, null);
        int dimension = vectors.getFirst().length;
        if (vectors.stream().anyMatch(vector -> vector.length != dimension || !finite(vector))
                || embeddingDimension != null && embeddingDimension != dimension) throw new EmbeddingFailed(FailureReason.EMPTY_RESPONSE, null);
        embeddingDimension = dimension;
        return List.copyOf(vectors);
    }

    private static boolean finite(float[] vector) { for (float value : vector) if (!Float.isFinite(value)) return false; return true; }

    private static CallFailed failed(FailureReason reason, String errorClass, long started, int inputTokens) {
        return new CallFailed(reason, errorClass, Duration.ofNanos(System.nanoTime() - started), inputTokens);
    }
}
