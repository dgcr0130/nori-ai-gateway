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
    volatile String embeddingModel="bge-m3",embeddingVector="[1,2,3]";
    volatile boolean failQuestionEmbedding;
    final List<String> embeddings=new CopyOnWriteArrayList<>();
    @BeforeEach void localServices() throws Exception {
        tokenizer=new HfTokenizerCounter(InferenceFixtures.settings("http://127.0.0.1:1/v1"));
        int prompt=tokenizer.countText(InferenceFixtures.renderer().render(List.of(new ChatMessage(ChatMessage.Role.USER,"응답"))));int completion=tokenizer.countText("예");
        server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/",exchange->{
            String path=exchange.getRequestURI().getPath();String input=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);String body;int status=200;
            if(path.equals("/v1/models"))body="{\"data\":[{\"id\":\"qwen3:4b\"},{\"id\":\""+embeddingModel+"\"}]}";
            else if(path.equals("/v1/chat/completions"))body="{\"id\":\"fixture\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"qwen3:4b\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"예\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":"+prompt+",\"completion_tokens\":"+completion+",\"total_tokens\":"+(prompt+completion)+"}}";
            else if(path.equals("/v1/embeddings")) {
                embeddings.add(input);
                if(failQuestionEmbedding&&com.nori.ai.gateway.core.registry.SchemaContracts.JSON.readTree(input).path("input").get(0).asString().equals("설비 T3 코드")){status=503;body="{\"error\":{\"message\":\"고정 실패\"}}";}
                else body="{\"object\":\"list\",\"model\":\""+embeddingModel+"\",\"data\":[{\"object\":\"embedding\",\"index\":0,\"embedding\":"+embeddingVector+"}],\"usage\":{\"prompt_tokens\":1,\"total_tokens\":1}}";
            }
            else if(exchange.getRequestURI().getRawQuery()!=null)body=broken?"broken-private-response":"<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><IsTruncated>false</IsTruncated>"+(empty?"":"<Contents><Key>rag/a.md</Key><Size>7</Size></Contents>")+"</ListBucketResult>";
            else body=raw;
            byte[] bytes=body.getBytes(StandardCharsets.UTF_8);exchange.getResponseHeaders().add("Content-Type",path.startsWith("/v1")?"application/json":"application/xml");exchange.sendResponseHeaders(status,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();
        });server.start();
    }
    @Override protected Map<String,Object> extraApplicationProperties() {
        try {
            var access=securityDirectory.resolve("document-access");var secret=securityDirectory.resolve("document-secret");Files.writeString(access,"fixture-access");Files.writeString(secret,"fixture-secret");
            return Map.of("ai.gateway.inference.mode","enabled","ai.gateway.llm.baseUrl",base()+"/v1","ai.gateway.documents.s3Endpoint",base(),
                "ai.gateway.documents.accessKeyFile",access.toString(),"ai.gateway.documents.secretKeyFile",secret.toString(),"test.document-prefix","rag/","ai.gateway.llm.embeddingModel",embeddingModel);
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
    private com.nori.ai.gateway.core.scope.VerifiedSystemScope reviewer() {
        return new com.nori.ai.gateway.core.scope.VerifiedSystemScope(OwnerSystem.TC,"fixture",new com.nori.ai.gateway.core.scope.ActorRef("reviewer"),"reviewer",Set.of("AI_REVIEW","AI_DOC_READ"),"a".repeat(64),java.time.Instant.MIN,java.time.Instant.MAX,UUID.randomUUID().toString());
    }
    private UUID reviewed(String cause) {
        var index=context.getBean(DocumentIndexRepository.class);String revision=index.currentRevision(scope(),"fixture-bucket","rag/a.md").orElseThrow();
        var location=new com.nori.ai.gateway.core.knowledge.DocumentLocation("fixture-bucket","rag/a.md",revision,"설정",5,5);
        var snapshot=UUID.randomUUID();var feedback=UUID.randomUUID();
        jdbc().sql("INSERT INTO kn.answer_snapshot VALUES('TC',:id,:job,'actor','설비 T3 코드','{}','[]',repeat('c',64),now())").param("id",snapshot).param("job",UUID.randomUUID().toString()).update();
        jdbc().sql("INSERT INTO kn.feedback VALUES('TC',:id,:answer,'actor','DOWN','지적','OPEN',1,now(),now())").param("id",feedback).param("answer",snapshot).update();
        Object action=cause.equals("DOCUMENT")?Map.of("note","문서 보완","locations",List.of(location)):Map.of("terms",Map.of("T3","설비 코드"),"correctAnswer",Map.of("sentences",List.of(Map.of("text","설비 T3 코드는 100입니다.","citations",List.of(1)))),"expectedFacts",List.of("100"),"locations",List.of(location));
        context.getBean(com.nori.ai.gateway.core.feedback.FeedbackService.class).resolve(reviewer(),feedback,com.nori.ai.gateway.core.registry.SchemaContracts.JSON.valueToTree(Map.of("version",1,"cause",cause,"action",action)),Map.of("T3","초기 풀이"));return feedback;
    }
    @Test void reviewedAnswersAndGlossarySurviveRestartWhileEmbeddingReplacementRevisionAndReopenExcludeOldMaterial() {
        raw="# 운영\n\n## 설정\n\n설비 T3 코드는 100입니다.\n";start();var answer=reviewed("RETRIEVAL");var document=reviewed("DOCUMENT");
        var query=context.getBean(com.nori.ai.gateway.core.knowledge.DocumentSearch.class);
        assertThat(query.search(reviewer(),"설비 T3 코드",List.of(),com.nori.ai.gateway.core.knowledge.SearchSettings.defaults()).verified()).hasSize(1);
        assertThat(query.search(scope(),"설비 T3 코드",List.of(),com.nori.ai.gateway.core.knowledge.SearchSettings.defaults()).verified()).isEmpty();
        embeddingModel="replacement-model";embeddingVector="[4,5]";context.close();context=null;startApplication();
        assertThat(context.getBean(ReadinessGate.class).ready()).isTrue();var indexes=context.getBean(DocumentIndexRepository.class);var state=indexes.searchState(reviewer()).orElseThrow();
        assertThat(state.dimension()).isEqualTo(2);assertThat(state.answers()).singleElement().satisfies(a->assertThat(a.embedding()).containsExactly(4,5));
        assertThat(jdbc().sql("SELECT embedding_model||':'||embedding_dimension||':'||config_sha256 FROM kn.verified_answer WHERE active").query(String.class).single()).isEqualTo("replacement-model:2:"+state.configSha256());
        assertThat(context.getBean(com.nori.ai.gateway.core.feedback.ReviewedGlossary.class).current(reviewer(),Map.of("T3","초기 풀이"))).containsEntry("T3","설비 코드");
        assertThat(context.getBean(com.nori.ai.gateway.core.knowledge.DocumentSearch.class).search(reviewer(),"설비 T3 코드",List.of(),com.nori.ai.gateway.core.knowledge.SearchSettings.defaults()).verified()).hasSize(1);
        jdbc().sql("UPDATE kn.feedback SET state='OPEN',version=version+1 WHERE id=:id").param("id",answer).update();jdbc().sql("UPDATE kn.verified_answer SET active=false").update();jdbc().sql("UPDATE kn.question_case SET active=false WHERE feedback_id=:id").param("id",answer).update();
        context.close();context=null;startApplication();assertThat(context.getBean(DocumentIndexRepository.class).activeAnswers(scope())).isEmpty();
        raw="# 운영\n\n## 설정\n\n설비 T3 코드는 200입니다.\n";context.close();context=null;startApplication();
        assertThat(jdbc().sql("SELECT active FROM kn.question_case WHERE feedback_id=:id").param("id",document).query(Boolean.class).single()).isFalse();
        assertThat(jdbc().sql("SELECT count(*) FROM kn.review").query(Integer.class).single()).isEqualTo(2);assertThat(jdbc().sql("SELECT count(*) FROM kn.answer_snapshot").query(Integer.class).single()).isEqualTo(2);
    }
    @Test void activeReviewedAnswerEmbeddingFailureOnRestartKeepsOldBundleAndClosesReadiness() {
        raw="# 운영\n\n## 설정\n\n설비 T3 코드는 100입니다.\n";start();reviewed("RETRIEVAL");UUID active=context.getBean(DocumentIndexRepository.class).searchState(reviewer()).orElseThrow().id();
        context.close();context=null;failQuestionEmbedding=true;startApplication();assertThat(context.getBean(ReadinessGate.class).ready()).isFalse();assertThat(context.getBean(com.nori.ai.gateway.app.job.JobDispatcher.class).started()).isFalse();
        assertThat(jdbc().sql("SELECT id FROM kn.index_set WHERE state='ACTIVE'").query(UUID.class).single()).isEqualTo(active);assertThat(context.getBean(MemoryDocumentKeywordIndex.class).activeBundle(scope())).isEmpty();
    }
    @AfterEach void closeLocalServices() {
        // HTTP·추론 클라이언트가 먼저 닫힌 뒤 대역과 자격 파일을 같은 시험 수명에서 정리합니다.
        if(context!=null){context.close();context=null;}if(server!=null)server.stop(0);if(tokenizer!=null)tokenizer.close();
        try {Files.deleteIfExists(securityDirectory.resolve("document-access"));Files.deleteIfExists(securityDirectory.resolve("document-secret"));}
        catch(java.io.IOException failure){throw new IllegalStateException("시험 문서 자격 파일을 정리할 수 없습니다");}
    }
}
