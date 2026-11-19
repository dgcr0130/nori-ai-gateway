package com.nori.ai.gateway.persistence;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.persistence.repository.DocumentIndexRepository;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.core.knowledge.DocumentCorpus.*;

class DocumentIndexIT extends PgIntegrationSupport {
    private VerifiedSystemScope scope(String system) { return new VerifiedSystemScope(OwnerSystem.valueOf(system),"fixture",new ActorRef("actor"),"actor",Set.of(),"pdg",Instant.MIN,Instant.MAX,"request"); }
    private Document document(String bucket,String revision) {
        var location=new DocumentLocation(bucket,"rag/a.md",revision,"절",3,5);
        var child=new Child(UUID.randomUUID(),0,new DocumentLocation(bucket,"rag/a.md",revision,"절",5,5),"제목 > 절","마스킹 본문",List.of(),new float[]{1,2,3});
        var parent=new Parent(UUID.randomUUID(),location,"제목 > 절","마스킹 본문",bodySha(),List.of(child));
        return new Document(UUID.randomUUID(),bucket,"rag/a.md",revision,"제목",Set.of("READ"),List.of(),List.of(parent));
    }
    private String bodySha() {
        try { return HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest("마스킹 본문".getBytes(java.nio.charset.StandardCharsets.UTF_8))); }
        catch(java.security.NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }
    private Bundle bundle(List<Document> documents) { return new Bundle(UUID.randomUUID(),"old-model",3,"a".repeat(64),documents,List.of()); }
    @Test void v8StoresSameKeyAndRevisionInDifferentBucketsAndActivationReembedsThenDropsStaleAnswersAndDocumentCases() {
        try(var database=rawDatabase()) {
            database.bootstrap();database.migrate();
            try(var context=application(database)) {
                var store=context.getBean(DocumentIndexRepository.class);var keywords=context.getBean(MemoryDocumentKeywordIndex.class);var jdbc=context.getBean(JdbcClient.class);var scope=scope("Doc");
                var first=bundle(List.of(document("A","b".repeat(64)),document("B","b".repeat(64))));
                store.saveBuilding(scope,first);try(var prepared=keywords.prepare(scope,store.load(scope,first.id()))){store.activate(scope,first);keywords.install(scope,prepared);}
                assertThat(store.currentRevision(scope,"A","rag/a.md")).contains("b".repeat(64));assertThat(store.currentRevision(scope,"B","rag/a.md")).contains("b".repeat(64));assertThat(store.currentRevision(scope("Other"),"A","rag/a.md")).isEmpty();
                seedPermanent(jdbc,first.documents().getFirst().parents().getFirst().children().getFirst().location());
                assertThat(store.activeAnswers(scope)).hasSize(1);
                var objects=new DocumentObjectStore(){
                    public List<ObjectKey> list(Source source){return List.of(new ObjectKey("rag/a.md",1));}
                    public Content download(Source source,ObjectKey key,int max){assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();return new Content("b".repeat(64),new byte[0],false);}
                };
                var inputs=new ArrayList<String>();
                var inference=new InferenceClient(){
                    public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){throw new AssertionError();}
                    public List<float[]> embed(List<String> values){assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();inputs.addAll(values);return values.stream().map(v->new float[]{4,5}).toList();}
                };
                var sources=List.of(new Source("A","rag/",Set.of("READ","ADMIN")),new Source("B","rag/",Set.of("READ")));
                var sync=new DocumentSynchronization(objects,(source,key,content,limits)->{
                    var parsed=document(source.bucket(),content.revision());return new Document(parsed.id(),parsed.bucket(),parsed.objectKey(),parsed.revision(),parsed.title(),source.permissions(),parsed.issues(),parsed.parents());
                },store,keywords,inference);
                var second=sync.synchronize(scope,sources,new Limits(1048576,200,450,60),"new-model","d".repeat(64),2);
                assertThat(inputs).containsExactly("제목 > 절\n마스킹 본문","제목 > 절\n마스킹 본문","검증 질문");
                assertThat(store.activeAnswers(scope)).singleElement().satisfies(answer->{assertThat(answer.embedding()).containsExactly(4,5);assertThat(answer.permissions()).containsExactlyInAnyOrder("READ","ADMIN");});
                assertThat(jdbc.sql("SELECT embedding_model||':'||embedding_dimension||':'||config_sha256 FROM kn.verified_answer WHERE active").query(String.class).single()).isEqualTo("new-model:2:"+"d".repeat(64));
                assertThat(jdbc.sql("SELECT active FROM kn.question_case").query(Boolean.class).single()).isTrue();
                var changed=new DocumentSynchronization(objects,(source,key,content,limits)->document(source.bucket(),"e".repeat(64)),store,keywords,inference);
                changed.synchronize(scope,List.of(sources.getLast()),new Limits(1048576,200,450,60),"new-model","f".repeat(64),2);
                assertThat(store.currentRevision(scope,"A","rag/a.md")).isEmpty();assertThat(store.currentRevision(scope,"B","rag/a.md")).contains("e".repeat(64));assertThat(store.activeAnswers(scope)).isEmpty();
                assertThat(jdbc.sql("SELECT active FROM kn.question_case").query(Boolean.class).single()).isFalse();
                assertThat(jdbc.sql("SELECT count(*) FROM kn.index_set WHERE id=:id").param("id",first.id()).query(Integer.class).single()).isZero();
                assertThat(jdbc.sql("SELECT state FROM kn.index_set WHERE id=:id").param("id",second.id()).query(String.class).single()).isEqualTo("INACTIVE");
                store.cleanNonActive(scope);
                assertThat(jdbc.sql("SELECT count(*) FROM kn.parent_chunk").query(Integer.class).single()).isOne();
                assertThat(jdbc.sql("SELECT count(*) FROM kn.answer_snapshot").query(Integer.class).single()).isOne();
                assertThat(jdbc.sql("SELECT count(*) FROM kn.feedback").query(Integer.class).single()).isEqualTo(2);assertThat(jdbc.sql("SELECT count(*) FROM kn.review").query(Integer.class).single()).isEqualTo(2);
            }
        }
    }
    @Test void activationFailureRollsBackOldStateAnswerConfigurationAndQuestionCasesTogether() {
        try(var database=rawDatabase()) {
            database.bootstrap();database.migrate();
            try(var context=application(database)) {
                var store=context.getBean(DocumentIndexRepository.class);var jdbc=context.getBean(JdbcClient.class);var scope=scope("Doc");
                var first=bundle(List.of(document("A","b".repeat(64))));store.saveBuilding(scope,first);store.activate(scope,first);seedPermanent(jdbc,first.documents().getFirst().parents().getFirst().children().getFirst().location());
                var next=bundle(List.of());store.saveBuilding(scope,next);
                jdbc.sql("CREATE FUNCTION kn.fail_activation() RETURNS trigger LANGUAGE plpgsql AS 'BEGIN IF NEW.state=''ACTIVE'' THEN RAISE EXCEPTION ''fixture activation failure''; END IF; RETURN NEW; END;'").update();
                jdbc.sql("CREATE TRIGGER activation_failure BEFORE UPDATE ON kn.index_set FOR EACH ROW EXECUTE FUNCTION kn.fail_activation()").update();
                assertThatThrownBy(()->store.activate(scope,next)).isInstanceOf(org.springframework.dao.DataAccessException.class);
                assertThat(store.currentRevision(scope,"A","rag/a.md")).contains("b".repeat(64));assertThat(store.activeAnswers(scope)).hasSize(1);
                assertThat(jdbc.sql("SELECT active FROM kn.question_case").query(Boolean.class).single()).isTrue();
                assertThat(jdbc.sql("SELECT state FROM kn.index_set WHERE id=:id").param("id",next.id()).query(String.class).single()).isEqualTo("BUILDING");
                jdbc.sql("DROP TRIGGER activation_failure ON kn.index_set").update();jdbc.sql("DROP FUNCTION kn.fail_activation()").update();
                assertThatThrownBy(()->store.saveBuilding(scope,new Bundle(UUID.randomUUID(),"model",2,"a".repeat(64),List.of(document("B","b".repeat(64))),List.of()))).isInstanceOf(IllegalArgumentException.class);
                assertThat(jdbc.sql("SELECT count(*) FROM kn.index_set").query(Integer.class).single()).isEqualTo(2);
            }
        }
    }
    @Test void startupCleanupRemovesBuildingAndInactiveTreesButPreservesActiveAndPermanentRows() {
        try(var database=rawDatabase()) {
            database.bootstrap();database.migrate();
            try(var context=application(database)) {
                var store=context.getBean(DocumentIndexRepository.class);var jdbc=context.getBean(JdbcClient.class);var scope=scope("Doc");
                var inactive=bundle(List.of(document("inactive","b".repeat(64))));store.saveBuilding(scope,inactive);store.activate(scope,inactive);
                var active=bundle(List.of(document("active","c".repeat(64))));store.saveBuilding(scope,active);store.activate(scope,active);
                var building=bundle(List.of(document("building","d".repeat(64))));store.saveBuilding(scope,building);
                var other=bundle(List.of(document("other","e".repeat(64))));store.saveBuilding(scope("Other"),other);
                seedPermanent(jdbc,active.documents().getFirst().parents().getFirst().children().getFirst().location());
                jdbc.sql("INSERT INTO kn.glossary(system_id,term,expansion,review_id,version) VALUES('Doc','용어','풀이',null,1)").update();
                var permanent=new LinkedHashMap<String,List<String>>();
                for(String table:List.of("answer_snapshot","feedback","review","question_case","verified_answer","glossary"))
                    permanent.put(table,jdbc.sql("SELECT to_jsonb(t)::text FROM kn."+table+" t WHERE system_id='Doc' ORDER BY to_jsonb(t)::text").query(String.class).list());
                String activeBefore=SchemaContracts.JSON.writeValueAsString(store.load(scope,active.id()));
                assertThat(jdbc.sql("SELECT state FROM kn.index_set WHERE system_id='Doc' ORDER BY state").query(String.class).list()).containsExactly("ACTIVE","BUILDING","INACTIVE");

                store.cleanNonActive(scope);

                assertThat(jdbc.sql("SELECT id FROM kn.index_set WHERE system_id='Doc'").query(UUID.class).list()).containsExactly(active.id());
                assertThat(jdbc.sql("SELECT state FROM kn.index_set WHERE system_id='Doc'").query(String.class).single()).isEqualTo("ACTIVE");
                assertThat(SchemaContracts.JSON.writeValueAsString(store.load(scope,active.id()))).isEqualTo(activeBefore);
                for(String table:List.of("document","parent_chunk","child_chunk"))
                    assertThat(jdbc.sql("SELECT count(*) FROM kn."+table+" WHERE system_id='Doc'").query(Integer.class).single()).as(table).isOne();
                permanent.forEach((table,rows)->assertThat(jdbc.sql("SELECT to_jsonb(t)::text FROM kn."+table+" t WHERE system_id='Doc' ORDER BY to_jsonb(t)::text").query(String.class).list()).as(table).containsExactlyElementsOf(rows));
                assertThat(jdbc.sql("SELECT state FROM kn.index_set WHERE system_id='Other' AND id=:id").param("id",other.id()).query(String.class).single()).isEqualTo("BUILDING");
                assertThat(store.load(scope("Other"),other.id()).documents()).hasSize(1);
            }
        }
    }
    private void seedPermanent(JdbcClient jdbc,DocumentLocation location) {
        var snapshot=UUID.randomUUID();var feedback=UUID.randomUUID();var review=UUID.randomUUID();
        jdbc.sql("INSERT INTO kn.answer_snapshot VALUES('Doc',:id,'historical-job','actor','검증 질문','{}','[]',repeat('a',64),now())").param("id",snapshot).update();
        jdbc.sql("INSERT INTO kn.feedback VALUES('Doc',:id,:answer,'actor','DOWN','지적','RESOLVED',2,now(),now())").param("id",feedback).param("answer",snapshot).update();
        jdbc.sql("INSERT INTO kn.review VALUES('Doc',:id,:feedback,1,'reviewer','ANSWER','{}',now())").param("id",review).param("feedback",feedback).update();
        jdbc.sql("INSERT INTO kn.verified_answer VALUES('Doc',:id,:review,'검증 질문','{\"sentences\":[{\"text\":\"마스킹 본문\",\"citations\":[1]}]}','old-model',3,repeat('a',64),CAST(:locations AS jsonb),'[\"READ\"]','[1,2,3]',true)")
            .param("id",UUID.randomUUID()).param("review",review).param("locations",SchemaContracts.JSON.writeValueAsString(List.of(location))).update();
        var documentFeedback=UUID.randomUUID();
        jdbc.sql("INSERT INTO kn.feedback VALUES('Doc',:id,:answer,'case-actor','DOWN','보완','RESOLVED',2,now(),now())").param("id",documentFeedback).param("answer",snapshot).update();
        jdbc.sql("INSERT INTO kn.review VALUES('Doc',:id,:feedback,1,'reviewer','DOCUMENT','{}',now())").param("id",UUID.randomUUID()).param("feedback",documentFeedback).update();
        jdbc.sql("INSERT INTO kn.question_case VALUES('Doc',:id,:feedback,1,CAST(:payload AS jsonb),true,now())").param("id",UUID.randomUUID()).param("feedback",documentFeedback)
            .param("payload",SchemaContracts.JSON.writeValueAsString(Map.of("answerable",false,"documentTargets",List.of(Map.of("bucket",location.bucket(),"objectKey",location.objectKey(),"revision",location.revision()))))).update();
    }
}
