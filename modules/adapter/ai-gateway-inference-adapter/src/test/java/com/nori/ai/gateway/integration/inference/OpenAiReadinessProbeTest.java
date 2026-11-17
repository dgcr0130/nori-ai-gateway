package com.nori.ai.gateway.integration.inference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.job.GenerationSemaphore;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.*;
import com.openai.client.OpenAIClient;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class OpenAiReadinessProbeTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    static String models() { return "{\"object\":\"list\",\"data\":[{\"id\":\"qwen3:4b\"},{\"id\":\"bge-m3\"}]}"; }
    @Test void modelsHttpResponseErrorsRemainEndpointErrorsAndDisconnectionIsNoResponse() {
        var unused = new com.nori.ai.gateway.core.port.InferenceClient() {
            @Override public ChatOutcome chat(CallType type, List<ChatMessage> messages, int maxTokens) { throw new AssertionError("잘못된 모델 목록 뒤 생성 요청은 금지합니다"); }
            @Override public List<float[]> embed(List<String> inputs) { throw new AssertionError("잘못된 모델 목록 뒤 임베딩 요청은 금지합니다"); }
        };
        try (var tokenizer = new HfTokenizerCounter();
                var server = new OllamaStubServer(request -> new OllamaStubServer.Json(200, "<html>not json</html>"));
                var probe = new OpenAiReadinessProbe(new InferenceEndpointConfig(InferenceFixtures.settings(server.baseUrl() + "/v1")), unused, tokenizer)) {
            var expected = new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE, OpenAiReadinessProbe.Reason.ENDPOINT_ERROR, null);
            for (var reply : List.of(new OllamaStubServer.Json(200, "<html>not json</html>"),
                    new OllamaStubServer.Json(200, "{"), new OllamaStubServer.Json(200, ""),
                    new OllamaStubServer.Json(200, "x".repeat(1024 * 1024 + 1)),
                    new OllamaStubServer.Json(200, "[]"), new OllamaStubServer.Json(200, "{}"),
                    new OllamaStubServer.Json(200, "{\"data\":[{\"id\":1}]}"),
                    new OllamaStubServer.Json(401, "{}"), new OllamaStubServer.Json(403, "{}"), new OllamaStubServer.Json(500, "{}"))) {
                server.respond(request -> reply);
                assertThat(probe.probe()).as("status=%s, bodyBytes=%s", reply.status(), reply.body().length()).isEqualTo(expected);
            }
            server.respond(request -> new OllamaStubServer.Json(200, "{\"data\":[]}"));
            assertThat(probe.probe()).isEqualTo(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.MODEL_LOADING, OpenAiReadinessProbe.Reason.NONE, null));
            server.respond(request -> new OllamaStubServer.Disconnect());
            assertThat(probe.probe()).isEqualTo(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE, OpenAiReadinessProbe.Reason.NO_RESPONSE, null));
            assertThat(server.received()).extracting(OllamaStubServer.Received::path).containsOnly("/v1/models");
        }
    }

    @Test void minimumRequestsUseActualUserOnlyTokensAndRejectMismatchUsageLengthAndDimensionChanges() throws Exception {
        try (var tokenizer = new HfTokenizerCounter()) {
            var counter = new PromptTokenCounter(InferenceFixtures.renderer(), tokenizer);
            var next = new AtomicReference<String>("ready");
            try (var server = new OllamaStubServer(request -> {
                if(request.path().equals("/v1/models")) return new OllamaStubServer.Json(200, next.get().equals("loading") ? "{\"data\":[]}" : models());
                if(request.path().equals("/v1/embeddings")) {
                    if(next.get().equals("embeddingError")) return new OllamaStubServer.Json(503, "{}");
                    if(next.get().equals("embeddingMalformed")) return new OllamaStubServer.Json(200, "<html>not json</html>");
                    return new OllamaStubServer.Json(200, next.get().equals("dimension") ? InferenceTransportContractTest.EMBEDDING.replace("0.25,0.5,0.75","0.25,0.5") : InferenceTransportContractTest.EMBEDDING);
                }
                if(next.get().equals("chatError")) return new OllamaStubServer.Json(503, "{}");
                if(next.get().equals("chatMalformed")) return new OllamaStubServer.Json(200, "<html>not json</html>");
                try {
                    var node=JSON.readTree(request.body());
                    assertThat(node.path("messages")).hasSize(1);
                    assertThat(node.path("messages").get(0).path("role").asText()).isEqualTo("user");
                    assertThat(node.path("messages").get(0).path("content").asText()).isEqualTo("응답");
                    assertThat(node.path("max_tokens").asInt()).isEqualTo(2048);
                    String body=InferenceTransportContractTest.completion("답변", counter.count(List.of(new ChatMessage(ChatMessage.Role.USER,"응답"))));
                    body=body.replace("length","stop");
                    if(next.get().equals("mismatch")) body=body.replace("\"prompt_tokens\":7", "\"prompt_tokens\":999");
                    if(next.get().equals("length")) body=body.replace("stop","length");
                    if(next.get().equals("usage")) body=body.replace("\"completion_tokens\":5,", "");
                    if(next.get().equals("mismatch")) body=body.replace("\"prompt_tokens\":"+counter.count(List.of(new ChatMessage(ChatMessage.Role.USER,"응답"))),"\"prompt_tokens\":999");
                    return new OllamaStubServer.Json(200,body);
                } catch(Exception failure){throw new IllegalStateException(failure);}
            })) {
                var endpoint=new InferenceEndpointConfig(InferenceFixtures.settings(server.baseUrl()+"/v1"));
                var config=new InferenceModelConfiguration();
                OpenAIClient sdk=config.inferenceOpenAiClient(endpoint);
                try {
                    var client=new SpringAiOpenAiInferenceClient(config.springAiChatTransport(config.openAiChatModel(sdk,endpoint,endpoint.settings().policy()),InferenceFixtures.renderer()),
                        config.openAiEmbeddingModel(sdk,endpoint),new PreCallBudgetAssert(counter,endpoint.settings().policy()),new TruncationDetector(),new GenerationSemaphore(1));
                    try(var probe=new OpenAiReadinessProbe(endpoint,client,tokenizer)) {
                        assertThat(probe.probe()).isEqualTo(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.READY,OpenAiReadinessProbe.Reason.NONE,3));
                        for(String kind:List.of("mismatch","usage","length","dimension","chatError","chatMalformed","embeddingError","embeddingMalformed")) {
                            next.set(kind); assertThat(probe.probe()).as(kind).isEqualTo(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE,OpenAiReadinessProbe.Reason.ENDPOINT_ERROR,null));
                        }
                        next.set("loading"); assertThat(probe.probe().state()).isEqualTo(OpenAiReadinessProbe.State.MODEL_LOADING);
                    }
                } finally { sdk.close(); }
                assertThat(server.received()).allSatisfy(request -> assertThat(request.header("Authorization")).isNull());
                assertThat(server.received()).extracting(OllamaStubServer.Received::path).containsOnly("/v1/models","/v1/chat/completions","/v1/embeddings");
            }
        }
    }
}
