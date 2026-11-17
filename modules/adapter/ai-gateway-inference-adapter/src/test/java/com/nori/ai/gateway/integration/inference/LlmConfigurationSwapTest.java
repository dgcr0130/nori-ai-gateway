package com.nori.ai.gateway.integration.inference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.job.GenerationSemaphore;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

class LlmConfigurationSwapTest {
    @TempDir Path directory;
    @Test void twoLocalEndpointsTemplatesTokenizersAndOptionalKeyNeedOnlyConfigurationChanges() throws Exception {
        Path other=directory.resolve("tokenizer.json");
        Files.writeString(other,"""
            {"version":"1.0","truncation":null,"padding":null,"added_tokens":[],"normalizer":null,"pre_tokenizer":{"type":"Whitespace"},"post_processor":null,"decoder":null,"model":{"type":"WordLevel","vocab":{"[UNK]":0,"응답":1,"답변":2,"질문":3},"unk_token":"[UNK]"}}
            """);
        String sha=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(other)));
        Path key=directory.resolve("api.key");Files.writeString(key,"fixture-bearer-key\n");
        for(int variant=0;variant<2;variant++) {
            int version=variant;String prefix=version==0?"/compat-a":"/compat-b";
            var initial=InferenceFixtures.settings("http://127.0.0.1:1/v1");
            var template=version==0?InferenceFixtures.template():new ConfiguredChatTemplateRenderer.Template("S\n","U\n","A\n","\nEND\n","\nSEP\n",true,"A\n","\n","");
            String file=version==0?initial.tokenCounting().tokenizerFile():other.toString();
            String hash=version==0?initial.tokenCounting().sha256():sha;
            var tokenCount=new java.util.concurrent.atomic.AtomicReference<PromptTokenCounter>();
            var json=new ObjectMapper();
            try(var stub=new OllamaStubServer(request -> {
                if(request.path().endsWith("/models"))return new OllamaStubServer.Json(200,"{\"data\":[{\"id\":\"chat-"+version+"\"},{\"id\":\"embed-"+version+"\"}]}");
                if(request.path().endsWith("/embeddings"))return new OllamaStubServer.Json(200,InferenceTransportContractTest.EMBEDDING);
                try {
                    var body=json.readTree(request.body());var messages=new ArrayList<ChatMessage>();
                    for(var item:body.path("messages"))messages.add(new ChatMessage(ChatMessage.Role.valueOf(item.path("role").asText().toUpperCase(Locale.ROOT)),item.path("content").asText()));
                    return new OllamaStubServer.Json(200,InferenceTransportContractTest.completion("답변",tokenCount.get().count(messages)).replace("length","stop"));
                }catch(Exception failure){throw new IllegalStateException(failure);}
            })) {
                var settings=new LlmSettings(stub.baseUrl()+prefix,"chat-"+version,"embed-"+version,0.25,8192,Duration.ofSeconds(2),version==0?null:key.toString(),
                    new LlmSettings.TokenCounting("hf",file,hash,template),initial.budgets(),3072,1);
                var endpoint=new InferenceEndpointConfig(settings);var config=new InferenceModelConfiguration();
                try(var tokenizer=new HfTokenizerCounter(endpoint)) {
                    var renderer=new ConfiguredChatTemplateRenderer(template);var counter=new PromptTokenCounter(renderer,tokenizer);tokenCount.set(counter);
                    var sdk=config.inferenceOpenAiClient(endpoint);
                    try {
                        var client=new SpringAiOpenAiInferenceClient(config.springAiChatTransport(config.openAiChatModel(sdk,endpoint,settings.policy()),renderer),config.openAiEmbeddingModel(sdk,endpoint),
                            new PreCallBudgetAssert(counter,settings.policy()),new TruncationDetector(),new GenerationSemaphore(1),endpoint);
                        try(var probe=new OpenAiReadinessProbe(endpoint,client,tokenizer)) {assertThat(probe.probe().state()).isEqualTo(OpenAiReadinessProbe.State.READY);}
                        var messages=List.of(new ChatMessage(ChatMessage.Role.SYSTEM,"지시"),new ChatMessage(ChatMessage.Role.USER,"질문"),new ChatMessage(ChatMessage.Role.USER,"추가"));
                        assertThat(client.chat(CallType.CLASSIFY,messages,2048)).isInstanceOf(com.nori.ai.gateway.core.port.InferenceClient.Completed.class);
                        assertThat(counter.count(messages)).isEqualTo(tokenizer.countText(renderer.render(messages)));
                        var request=stub.received().getLast();var body=json.readTree(request.body());
                        assertThat(body.path("messages")).hasSize(2);assertThat(body.path("messages").get(1).path("content").asText()).isEqualTo("질문"+template.sameRoleSeparator()+"추가");
                        assertThat(body.path("model").asText()).isEqualTo("chat-"+version);assertThat(body.path("temperature").asDouble()).isEqualTo(0.25);
                        assertThat(body.path("stream").asBoolean()).isFalse();assertThat(body.has("stream")).isTrue();
                        for(var received:stub.received())assertThat(received.header("Authorization")).isEqualTo(version==0?null:"Bearer fixture-bearer-key");
                        assertThat(stub.received()).extracting(OllamaStubServer.Received::path).containsExactly(prefix+"/models",prefix+"/chat/completions",prefix+"/embeddings",prefix+"/chat/completions");
                    }finally{sdk.close();}
                }
            }
        }
    }
}
