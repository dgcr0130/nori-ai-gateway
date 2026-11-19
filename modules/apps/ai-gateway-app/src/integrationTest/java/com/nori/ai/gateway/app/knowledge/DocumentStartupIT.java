package com.nori.ai.gateway.app.knowledge;

import com.nori.ai.gateway.app.StackContextSupport;
import com.nori.ai.gateway.app.job.recovery.ReadinessGate;
import com.nori.ai.gateway.app.security.*;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.persistence.repository.DocumentIndexRepository;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.*;

class DocumentStartupIT extends StackContextSupport {
    HttpServer server;HfTokenizerCounter tokenizer;
    volatile String raw="# 제목\n\n## 절\n\npassword=private-value\n";
    volatile boolean empty,broken;
    final List<String> embeddings=new CopyOnWriteArrayList<>();
    @BeforeEach void localServices() throws Exception {
        tokenizer=new HfTokenizerCounter(InferenceFixtures.settings("http://127.0.0.1:1/v1"));
        int prompt=tokenizer.countText(InferenceFixtures.renderer().render(List.of(new ChatMessage(ChatMessage.Role.USER,"응답"))));int completion=tokenizer.countText("예");
        server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/",exchange->{
            String path=exchange.getRequestURI().getPath();String input=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);String body;
            if(path.equals("/v1/models"))body="{\"data\":[{\"id\":\"qwen3:4b\"},{\"id\":\"bge-m3\"}]}";
            else if(path.equals("/v1/chat/completions"))body="{\"id\":\"fixture\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"qwen3:4b\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"예\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":"+prompt+",\"completion_tokens\":"+completion+",\"total_tokens\":"+(prompt+completion)+"}}";
            else if(path.equals("/v1/embeddings")) {embeddings.add(input);body="{\"object\":\"list\",\"model\":\"bge-m3\",\"data\":[{\"object\":\"embedding\",\"index\":0,\"embedding\":[1,2,3]}],\"usage\":{\"prompt_tokens\":1,\"total_tokens\":1}}";}
            else if(exchange.getRequestURI().getRawQuery()!=null)body=broken?"broken-private-response":"<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><IsTruncated>false</IsTruncated>"+(empty?"":"<Contents><Key>rag/a.md</Key><Size>7</Size></Contents>")+"</ListBucketResult>";
            else body=raw;
            byte[] bytes=body.getBytes(StandardCharsets.UTF_8);exchange.getResponseHeaders().add("Content-Type",path.startsWith("/v1")?"application/json":"application/xml");exchange.sendResponseHeaders(200,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();
        });server.start();
    }
    @Override protected Map<String,Object> extraApplicationProperties() {
        try {
            var access=securityDirectory.resolve("document-access");var secret=securityDirectory.resolve("document-secret");Files.writeString(access,"fixture-access");Files.writeString(secret,"fixture-secret");
            return Map.of("ai.gateway.inference.mode","enabled","ai.gateway.llm.baseUrl",base()+"/v1","ai.gateway.documents.s3Endpoint",base(),
                "ai.gateway.documents.accessKeyFile",access.toString(),"ai.gateway.documents.secretKeyFile",secret.toString(),"test.document-prefix","rag/");
        } catch(java.io.IOException failure) {throw new IllegalStateException("시험 자격 파일을 만들 수 없습니다");}
    }
    private String base() {return "http://127.0.0.1:"+server.getAddress().getPort();}
    private JdbcClient jdbc() {return context.getBean(JdbcClient.class);}
    private com.nori.ai.gateway.core.scope.VerifiedSystemScope scope() {return context.getBean(ScopeAssembler.class).forDocumentStartup(OwnerSystem.TC,context.getBean(SecurityCredentialFiles.class));}
    @Test void actualStartupMasksStoresAndBuildsLuceneThenRestartChangesRevisionAndCleansInactiveWithoutDeletingSnapshot() throws Exception {
        start();assertThat(context.getBean(ReadinessGate.class).ready()).isTrue();
        var first=context.getBean(DocumentIndexRepository.class).currentRevision(scope(),"fixture-bucket","rag/a.md").orElseThrow();
        assertThat(jdbc().sql("SELECT body_masked FROM kn.parent_chunk").query(String.class).single()).isEqualTo("[마스킹]");
        assertThat(context.getBean(MemoryDocumentKeywordIndex.class).activeBundle(scope())).isPresent();
        assertThat(jdbc().sql("SELECT count(*) FROM kn.verified_answer").query(Integer.class).single()).isZero();
        jdbc().sql("INSERT INTO kn.answer_snapshot VALUES('TC',:id,'permanent-job','actor','질문','{}','[]',repeat('a',64),now())").param("id",UUID.randomUUID()).update();
        raw="# 제목\n\n## 절\n\n수정된 본문\n";
        assertThat(context.getBean(DocumentIndexRepository.class).currentRevision(scope(),"fixture-bucket","rag/a.md")).contains(first);
        context.close();context=null;startApplication();
        var second=context.getBean(DocumentIndexRepository.class).currentRevision(scope(),"fixture-bucket","rag/a.md").orElseThrow();assertThat(second).isNotEqualTo(first);
        assertThat(jdbc().sql("SELECT count(*) FROM kn.index_set WHERE state='INACTIVE'").query(Integer.class).single()).isOne();
        context.close();context=null;empty=true;startApplication();
        assertThat(context.getBean(ReadinessGate.class).ready()).isTrue();assertThat(context.getBean(DocumentIndexRepository.class).currentRevision(scope(),"fixture-bucket","rag/a.md")).isEmpty();
        assertThat(jdbc().sql("SELECT count(*) FROM kn.document WHERE revision=:old").param("old",first).query(Integer.class).single()).isZero();
        assertThat(jdbc().sql("SELECT count(*) FROM kn.answer_snapshot").query(Integer.class).single()).isOne();
        assertThat(embeddings).allSatisfy(input->assertThat(input).doesNotContain("private-value","password="));
    }
    @Test void synchronizationFailureKeepsOldActiveBundleAndClosesReadinessWithoutStartingDispatcher() {
        start();var previous=context.getBean(MemoryDocumentKeywordIndex.class).activeBundle(scope()).orElseThrow();context.close();context=null;broken=true;startApplication();
        assertThat(context.getBean(ReadinessGate.class).ready()).isFalse();assertThat(context.getBean(com.nori.ai.gateway.app.job.JobDispatcher.class).started()).isFalse();
        assertThat(jdbc().sql("SELECT id FROM kn.index_set WHERE state='ACTIVE'").query(UUID.class).single()).isEqualTo(previous);
        assertThat(context.getBean(MemoryDocumentKeywordIndex.class).activeBundle(scope())).isEmpty();
    }
    @Test void zeroObjectsAndRuleExcludedFilesAreSuccessfulDistinctZeroChunkBundles() {
        empty=true;start();assertThat(context.getBean(ReadinessGate.class).ready()).isTrue();assertThat(jdbc().sql("SELECT count(*) FROM kn.document").query(Integer.class).single()).isZero();
        context.close();context=null;empty=false;raw="# 제목\n## 절\n### 금지\n";startApplication();assertThat(context.getBean(ReadinessGate.class).ready()).isTrue();
        assertThat(jdbc().sql("SELECT count(*) FROM kn.child_chunk").query(Integer.class).single()).isZero();
        assertThat(jdbc().sql("SELECT extraction_issues::text FROM kn.document").query(String.class).single()).contains("FORBIDDEN_HEADING");
    }
    @AfterEach void closeLocalServices() {
        // HTTP·추론 클라이언트가 먼저 닫힌 뒤 대역과 자격 파일을 같은 시험 수명에서 정리합니다.
        if(context!=null){context.close();context=null;}if(server!=null)server.stop(0);if(tokenizer!=null)tokenizer.close();
        try {Files.deleteIfExists(securityDirectory.resolve("document-access"));Files.deleteIfExists(securityDirectory.resolve("document-secret"));}
        catch(java.io.IOException failure){throw new IllegalStateException("시험 문서 자격 파일을 정리할 수 없습니다");}
    }
}
