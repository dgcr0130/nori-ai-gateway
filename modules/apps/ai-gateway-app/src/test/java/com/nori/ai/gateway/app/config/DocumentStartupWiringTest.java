package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.app.knowledge.DocumentStartupRunner;

import com.nori.ai.gateway.app.config.*;
import com.nori.ai.gateway.app.job.recovery.*;
import com.nori.ai.gateway.app.security.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.integration.InferenceAdapterModuleConfiguration;
import com.nori.ai.gateway.integration.inference.*;
import com.nori.ai.gateway.integration.storage.*;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import com.nori.ai.gateway.persistence.repository.DocumentIndexRepository;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentStartupWiringTest {
    @TempDir Path temporary;
    @TestFactory Stream<DynamicTest> actualModuleWiringUsesOnlyLocalHttpAndFailsClosed() {
        return Stream.of("success","zero","embedding-error","dimension-error","list-error","no-sources","verified-answer")
            .map(mode->DynamicTest.dynamicTest(mode,()->run(mode)));
    }
    private void run(String mode) throws Exception {
        var directory=Files.createDirectory(temporary.resolve(mode));var sent=new CopyOnWriteArrayList<String>();var order=new ArrayList<String>();
        String raw="# 제목\n\n## 절\n\npassword=private-value\n";
        String revision=HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));
        var settings=InferenceFixtures.settings("http://127.0.0.1:1/v1");
        try(var tokenizer=new HfTokenizerCounter(settings)) {
            int prompt=tokenizer.countText(InferenceFixtures.renderer().render(List.of(new ChatMessage(ChatMessage.Role.USER,"응답"))));
            int completion=tokenizer.countText("예");
            var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
            server.createContext("/",exchange->{
                String path=exchange.getRequestURI().getPath();String request=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);sent.add(path+" "+request);
                String body;int status=200;
                if(path.equals("/v1/models"))body="{\"data\":[{\"id\":\"qwen3:4b\"},{\"id\":\"bge-m3\"}]}";
                else if(path.equals("/v1/chat/completions"))body="{\"id\":\"fixture\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"qwen3:4b\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"예\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":"+prompt+",\"completion_tokens\":"+completion+",\"total_tokens\":"+(prompt+completion)+"}}";
                else if(path.equals("/v1/embeddings")) {
                    boolean document=!request.contains("준비 확인");
                    if(document&&mode.equals("embedding-error")) {status=503;body="{\"error\":{\"message\":\"private-error\"}}";}
                    else body="{\"object\":\"list\",\"model\":\"bge-m3\",\"data\":[{\"object\":\"embedding\",\"index\":0,\"embedding\":"+(document&&mode.equals("dimension-error")?"[1,2]":"[1,2,3]")+"}],\"usage\":{\"prompt_tokens\":1,\"total_tokens\":1}}";
                } else if(exchange.getRequestURI().getRawQuery()!=null) {
                    if(mode.equals("list-error"))body="broken-private-response";
                    else body="<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><IsTruncated>false</IsTruncated>"+(mode.equals("zero")?"":"<Contents><Key>rag/a.md</Key><Size>7</Size></Contents>")+"</ListBucketResult>";
                } else body=raw;
                byte[] bytes=body.getBytes(StandardCharsets.UTF_8);exchange.getResponseHeaders().add("Content-Type",path.startsWith("/v1")?"application/json":"application/xml");exchange.sendResponseHeaders(status,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();
            });server.start();
            try {
            var saved=new AtomicReference<DocumentCorpus.Bundle>();var active=new AtomicReference<DocumentCorpus.Bundle>();
            var repository=mock(DocumentIndexRepository.class);
            when(repository.activeAnswers(any())).thenReturn(mode.equals("verified-answer")?List.of(new DocumentCorpus.Answer(UUID.randomUUID(),"검증 질문",List.of(new DocumentLocation("bucket","rag/a.md",revision,"절",5,5)),Set.of("OLD"),new float[]{9})):List.of());
            doAnswer(call->{order.add("save");saved.set(call.getArgument(1));return null;}).when(repository).saveBuilding(any(),any());
            when(repository.load(any(),any())).thenAnswer(call->saved.get());
            doAnswer(call->{order.add("activate");active.set(call.getArgument(1));return null;}).when(repository).activate(any(),any());
            var credential=directory.resolve("access");Files.writeString(credential,"fixture-key");
            var digest=directory.resolve("digest");Files.writeString(digest,"a".repeat(64));
            var signer=com.nori.ai.gateway.integration.fixtures.testing.StubAssertionSigner.ephemeral("test-eph-document");
            var jwks=directory.resolve("jwks");Files.writeString(jwks,new com.nimbusds.jose.jwk.JWKSet(signer.publicJwk()).toString());
            var documents=mode.equals("no-sources")?List.<GatewaySystemsProperties.Document>of():List.of(new GatewaySystemsProperties.Document("bucket","rag/",List.of("READ")));
            var systems=new GatewaySystemsProperties(Map.of("Doc",new GatewaySystemsProperties.SystemConfig("fixture",digest.toString(),jwks.toString(),null,null,List.of(),documents,List.of(),List.of(),List.of(),"REVIEW",Map.of())));
            try(var context=new AnnotationConfigApplicationContext()) {
                var values=new HashMap<>(InferenceFixtures.properties());values.put("ai.gateway.llm.baseUrl","http://127.0.0.1:"+server.getAddress().getPort()+"/v1");
                values.put("ai.gateway.inference.mode",mode.equals("no-sources")?"disabled":"enabled");values.put("ai.gateway.documents.s3Endpoint","http://127.0.0.1:"+server.getAddress().getPort());
                values.put("ai.gateway.documents.accessKeyFile",credential.toString());values.put("ai.gateway.documents.secretKeyFile",credential.toString());
                context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture",values));
                context.registerBean(GatewaySystemsProperties.class,()->systems);context.registerBean(SecurityCredentialFiles.class,()->new SecurityCredentialFiles(systems));context.registerBean(ScopeAssembler.class,ScopeAssembler::new);
                context.registerBean(Clock.class,Clock::systemUTC);context.registerBean(io.micrometer.core.instrument.MeterRegistry.class,io.micrometer.core.instrument.simple.SimpleMeterRegistry::new);
                context.registerBean(ContentSanitizer.class,ContentSanitizer::new);context.registerBean(DocumentIndexRepository.class,()->repository);context.registerBean(MemoryDocumentKeywordIndex.class,MemoryDocumentKeywordIndex::new);
                context.registerBean(TokenBudgetPolicy.class,TokenBudgetPolicy::current);context.registerBean(PreCallBudgetAssert.class,()->new PreCallBudgetAssert(context.getBean(PromptTokenCounting.class),context.getBean(TokenBudgetPolicy.class)));
                context.registerBean(TruncationDetector.class,TruncationDetector::new);context.registerBean(com.nori.ai.gateway.core.job.GenerationSemaphore.class,()->new com.nori.ai.gateway.core.job.GenerationSemaphore(1));
                // Core의 운영 factory와 adapter ModuleConfiguration을 그대로 연결합니다.
                context.registerBean(DocumentSynchronization.class,()->new CoreBeansConfiguration().documentSynchronization(context.getBean(S3DocumentObjectStore.class),context.getBean(DocumentExtractor.class),repository,context.getBean(MemoryDocumentKeywordIndex.class),context.getBean(InferenceClient.class)),definition->definition.setLazyInit(true));
                context.register(InferenceAdapterModuleConfiguration.class,ObjectStorageModuleConfiguration.class,DocumentConfiguration.class);context.refresh();
                context.getBeansOfType(InferenceReadinessService.class).values().forEach(InferenceReadinessService::stop);
                var events=new ArrayList<Object>();
                var recovery=new ReadinessPrecondition(){public int order(){return -1;}public void run(){order.add("recovery");}};
                var gate=new ReadinessGate(List.of(context.getBean(DocumentStartupRunner.class),recovery),events::add);gate.run(null);
                boolean failure=mode.endsWith("error");assertThat(gate.ready()).isEqualTo(!failure);
                assertThat(order.getFirst()).isEqualTo("recovery");
                if(failure) {assertThat(active.get()).isNull();assertThat(events).isEmpty();verify(repository,never()).activate(any(),any());}
                else if(!mode.equals("no-sources")) {
                    assertThat(order).containsExactly("recovery","save","activate");assertThat(active.get()).isNotNull();
                    assertThat(active.get().documents()).hasSize(mode.equals("zero")?0:1);assertThat(active.get().toString()).doesNotContain("private-value");
                    var scope=context.getBean(ScopeAssembler.class).forDocumentStartup(com.nori.ai.gateway.core.scope.OwnerSystem.valueOf("Doc"),context.getBean(SecurityCredentialFiles.class));
                    assertThat(context.getBean(MemoryDocumentKeywordIndex.class).activeBundle(scope)).contains(active.get().id());
                    assertThat(active.get().answers()).hasSize(mode.equals("verified-answer")?1:0);
                    if(mode.equals("verified-answer")) {assertThat(active.get().answers().getFirst().embedding()).containsExactly(1,2,3);assertThat(active.get().answers().getFirst().permissions()).containsExactly("READ");}
                } else {assertThat(sent).isEmpty();assertThat(saved.get()).isNull();}
                assertThat(sent.stream().filter(s->s.startsWith("/v1/embeddings")).toList()).allSatisfy(s->assertThat(s).doesNotContain("private-value","password="));
            }
            } finally {server.stop(0);}
        }
    }
}
