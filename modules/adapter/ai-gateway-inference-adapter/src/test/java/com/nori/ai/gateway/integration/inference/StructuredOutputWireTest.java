package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.llm.StructuredOutput;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 실제 Spring AI 2.0.2와 SDK의 직렬화·요청별 격리·실패 단일 전송을 확인합니다. */
class StructuredOutputWireTest {
    @Test void schemaReachesWireOnceAndDoesNotLeakIntoTheNextPlainRequestOrPromptTokens() throws Exception {
        var bodies=new java.util.concurrent.CopyOnWriteArrayList<String>();
        var status=new java.util.concurrent.atomic.AtomicInteger(200);
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/v1/chat/completions",exchange->{try(exchange){
            bodies.add(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8));
            String content=status.get()==200?"{\"id\":\"test\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"test\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{}\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":100,\"completion_tokens\":2,\"total_tokens\":102}}":"{\"error\":{\"message\":\"schema-error-secret-sentinel\"}}";
            byte[] response=content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type","application/json");exchange.sendResponseHeaders(status.get(),response.length);exchange.getResponseBody().write(response);
        }});
        server.start();
        try {
            var config=new InferenceModelConfiguration();var endpoint=new InferenceEndpointConfig(InferenceFixtures.settings("http://127.0.0.1:"+server.getAddress().getPort()+"/v1"));
            var sdk=config.inferenceOpenAiClient(endpoint);
            try {
                var policy=TokenBudgetPolicy.current();var model=config.openAiChatModel(sdk,endpoint,policy);
                var transport=new SpringAiChatTransport(model,InferenceFixtures.renderer());
                var client=new SpringAiOpenAiInferenceClient(transport,config.openAiEmbeddingModel(sdk,endpoint),new PreCallBudgetAssert(m->100,policy,String::length),new TruncationDetector(),new GenerationSemaphore(1));
                var messages=List.of(new ChatMessage(ChatMessage.Role.SYSTEM,"고정 지시"),new ChatMessage(ChatMessage.Role.USER,"질문"));
                var schema=new StructuredOutput("{\"type\":\"object\",\"properties\":{\"sentences\":{\"type\":\"array\",\"items\":{\"enum\":[{\"text\":\"조건이 없으면 전송하지 않는다.\",\"citations\":[1]}]}}},\"required\":[\"sentences\"],\"additionalProperties\":false}");
                var response=client.chat(CallType.COMPOSER,messages,3072,new CancellationToken(),schema);
                assertThat(response).isInstanceOfSatisfying(InferenceClient.Completed.class,completed->{assertThat(completed.inputTokens()).isEqualTo(100);assertThat(completed.promptTokens()).isEqualTo(100);});
                assertThat(client.chat(CallType.CLASSIFY,messages,2048)).isInstanceOf(InferenceClient.Completed.class);
                assertThat(bodies).hasSize(2);
                var structured=SchemaContracts.parse(bodies.getFirst());var plain=SchemaContracts.parse(bodies.getLast());
                assertThat(structured.path("response_format").path("type").asString()).isEqualTo("json_schema");
                assertThat(structured.path("response_format").path("json_schema").path("schema")).isEqualTo(SchemaContracts.parse(schema.schema()));
                assertThat(structured.path("response_format").path("json_schema").path("strict").asBoolean()).isTrue();
                assertThat(structured.path("messages")).isEqualTo(plain.path("messages"));
                assertThat(plain.has("response_format")).isFalse();
                assertThat(structured.path("max_tokens").asInt()).isEqualTo(3072);assertThat(plain.path("max_tokens").asInt()).isEqualTo(2048);
                assertThat(bodies.toString()).doesNotContain("num_ctx","reasoning_effort");
                status.set(400);
                var failed=client.chat(CallType.COMPOSER,messages,3072,new CancellationToken(),schema);
                assertThat(failed).isInstanceOf(InferenceClient.CallFailed.class);
                assertThat(failed.toString()).doesNotContain("schema-error-secret-sentinel");
                assertThat(bodies).as("지원 거부 뒤 schema 제거 재전송은 없습니다").hasSize(3);
            } finally {sdk.close();}
        } finally {server.stop(0);}
    }

    @Test void oversizedConstraintStopsBeforeTransportAndUnsupportedTransportDoesNotDiscardIt() {
        var count=new java.util.concurrent.atomic.AtomicInteger();
        ChatTransport old=(t,m,max)->{count.incrementAndGet();return new ChatTransport.Response("{}","stop",100,2);};
        var schema=new StructuredOutput("{\"type\":\"object\"}");
        var messages=List.of(new ChatMessage(ChatMessage.Role.USER,"질문"));
        var policy=TokenBudgetPolicy.current();
        assertThat(new PreCallBudgetAssert(m->100,policy,t->3073).check(CallType.COMPOSER,messages,3072,schema)).isInstanceOf(PreCallBudgetAssert.Exceeded.class);
        assertThatThrownBy(()->old.send(CallType.COMPOSER,messages,3072,java.time.Duration.ofSeconds(3),schema)).isInstanceOf(UnsupportedOperationException.class);
        assertThat(count).hasValue(0);
    }
}
