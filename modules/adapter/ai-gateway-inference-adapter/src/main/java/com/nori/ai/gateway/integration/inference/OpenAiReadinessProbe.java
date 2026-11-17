package com.nori.ai.gateway.integration.inference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.port.InferenceClient;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** 모델 목록만으로 준비를 선언하지 않고 실제 생성·임베딩 계약을 함께 검사합니다. */
@ConditionalOnProperty(name = "ai.gateway.inference.mode", havingValue = "enabled")
public final class OpenAiReadinessProbe implements AutoCloseable {
    public enum State { READY, MODEL_LOADING, INFERENCE_UNAVAILABLE }
    public enum Reason { NONE, NO_RESPONSE, ENDPOINT_ERROR }
    public record Observation(State state, Reason reason, Integer embeddingDimension) {
        public Observation { Objects.requireNonNull(state); Objects.requireNonNull(reason); }
    }
    private final InferenceEndpointConfig endpoint;
    private final InferenceClient inference;
    private final HttpClient transport;
    private final RestClient http;
    private final com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter tokenizer;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAiReadinessProbe(InferenceEndpointConfig endpoint, InferenceClient inference, com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter tokenizer) {
        this.endpoint = endpoint; this.inference = inference; this.tokenizer = tokenizer;
        transport = HttpClient.newBuilder().connectTimeout(InferenceModelConfiguration.CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NEVER).version(HttpClient.Version.HTTP_1_1).build();
        var factory = new JdkClientHttpRequestFactory(transport);
        factory.setReadTimeout(endpoint.settings().requestTimeout());
        var builder = RestClient.builder().requestFactory(factory);
        if (endpoint.apiKey() != null) builder.defaultHeader("Authorization", "Bearer " + endpoint.apiKey());
        http = builder.build();
    }

    public Observation probe() {
        try {
            var observation = http.get().uri(endpoint.readinessUri()).exchange((request, response) -> {
                if (!response.getStatusCode().is2xxSuccessful()) return error();
                byte[] bytes = response.getBody().readNBytes(1024 * 1024 + 1);
                if (bytes.length > 1024 * 1024) return error();
                // 응답 본문의 파싱 실패가 전송 예외로 감싸져 무응답으로 분류되지 않게 합니다.
                try { return models(mapper.readTree(bytes)); }
                catch (com.fasterxml.jackson.core.JsonProcessingException invalid) { return error(); }
            });
            if (observation.state() != State.READY) return observation;
            var outcome = inference.chat(CallType.CLASSIFY, List.of(new ChatMessage(ChatMessage.Role.USER, "응답")),
                endpoint.settings().policy().budget(CallType.CLASSIFY).requestMaxTokens());
            if (!(outcome instanceof InferenceClient.Completed completed) || com.nori.ai.gateway.core.llm.AnswerCheck.failure(completed, endpoint.settings().policy().budget(CallType.CLASSIFY), tokenizer::countText) != null) return error();
            var vectors = inference.embed(List.of("준비 확인"));
            return new Observation(State.READY, Reason.NONE, vectors.getFirst().length);
        } catch (org.springframework.web.client.RestClientException unavailable) {
            return new Observation(State.INFERENCE_UNAVAILABLE, Reason.NO_RESPONSE, null);
        } catch (Exception invalid) { return error(); }
    }

    private Observation models(JsonNode root) {
        if (root == null || !root.isObject() || !root.path("data").isArray()) return error();
        boolean generation = false, embedding = false;
        for (JsonNode model : root.get("data")) {
            if (!model.isObject() || !model.path("id").isTextual()) return error();
            generation |= endpoint.settings().generationModel().equals(model.get("id").asText());
            embedding |= endpoint.settings().embeddingModel().equals(model.get("id").asText());
        }
        return new Observation(generation && embedding ? State.READY : State.MODEL_LOADING, Reason.NONE, null);
    }
    private static Observation error() { return new Observation(State.INFERENCE_UNAVAILABLE, Reason.ENDPOINT_ERROR, null); }
    @Override public void close() { transport.close(); }
}
