package com.nori.ai.gateway.persistence;

import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.result.AnswerSnapshot;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.repository.*;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;

class FeedbackTransactionIT extends PgIntegrationSupport {
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"fixture",new ActorRef("reviewer"),"reviewer",Set.of("READ","ADMIN"),"a".repeat(64),Instant.MIN,Instant.MAX,UUID.randomUUID().toString());
    final DocumentLocation location=new DocumentLocation("bucket","rag/fixed.md","a".repeat(64),"설정",5,5);
    final UUID id=UUID.randomUUID();
    void seed(JdbcClient jdbc,DocumentIndexRepository indexes) {
        var child=new DocumentCorpus.Child(UUID.randomUUID(),0,location,"운영 > 설정","설비 T3 코드는 100입니다.",List.of(),new float[]{1,0});
        var parent=new DocumentCorpus.Parent(UUID.randomUUID(),location,"운영 > 설정",child.bodyMasked(),AnswerSnapshot.sha(child.bodyMasked()),List.of(child));
        var document=new DocumentCorpus.Document(UUID.randomUUID(),location.bucket(),location.objectKey(),location.revision(),"운영",Set.of("READ","ADMIN"),List.of(),List.of(parent));
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"model",2,"c".repeat(64),List.of(document),List.of());indexes.saveBuilding(scope,bundle);indexes.activate(scope,bundle);
        var snapshot=UUID.randomUUID();jdbc.sql("INSERT INTO kn.answer_snapshot VALUES('TC',:id,'removed-job','actor','설비 T3 코드','{}','[]',repeat('c',64),now())").param("id",snapshot).update();
        jdbc.sql("INSERT INTO kn.feedback VALUES('TC',:id,:answer,'actor','DOWN','지적','OPEN',1,now(),now())").param("id",id).param("answer",snapshot).update();
    }
    tools.jackson.databind.JsonNode action(){return SchemaContracts.JSON.valueToTree(Map.of("terms",Map.of("T3","설비 코드"),"locations",List.of(location),"expectedFacts",List.of("100"),"correctAnswer",Map.of("sentences",List.of(Map.of("text","설비 T3 코드는 100입니다.","citations",List.of(1))))));}
    tools.jackson.databind.JsonNode request(){return SchemaContracts.JSON.valueToTree(Map.of("version",1,"cause","RETRIEVAL","action",action()));}
    FeedbackService service(org.springframework.context.ApplicationContext context,AtomicInteger changed) {
        return new FeedbackService(context.getBean(FeedbackRepository.class),new ContentSanitizer(),context.getBean(MemoryDocumentKeywordIndex.class),text->{assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();return new float[]{1,0};},s->changed.incrementAndGet());
    }
    @Test void middleWriteAndFinalAuditFailuresRollBackEveryMaterialAndVersion() {
        try(var database=rawDatabase()){database.bootstrap();database.migrate();try(var context=application(database)) {
            var jdbc=context.getBean(JdbcClient.class);seed(jdbc,context.getBean(DocumentIndexRepository.class));var changed=new AtomicInteger();var service=service(context,changed);
            for(String table:List.of("kn.glossary","gw.audit_event")) {
                jdbc.sql("CREATE FUNCTION kn.fail_review_write() RETURNS trigger LANGUAGE plpgsql AS 'BEGIN RAISE EXCEPTION ''fixture review failure''; END;'").update();
                jdbc.sql("CREATE TRIGGER fail_review_write BEFORE INSERT ON "+table+" FOR EACH ROW EXECUTE FUNCTION kn.fail_review_write()").update();
                assertThatThrownBy(()->service.resolve(scope,id,request(),Map.of("T3","초기 코드"))).isInstanceOf(org.springframework.dao.DataAccessException.class);
                for(String name:List.of("review","verified_answer","glossary","question_case"))assertThat(jdbc.sql("SELECT count(*) FROM kn."+name).query(Integer.class).single()).as(name).isZero();
                assertThat(jdbc.sql("SELECT version FROM kn.feedback").query(Integer.class).single()).isOne();assertThat(jdbc.sql("SELECT state FROM kn.feedback").query(String.class).single()).isEqualTo("OPEN");assertThat(changed).hasValue(0);
                jdbc.sql("DROP TRIGGER fail_review_write ON "+table).update();jdbc.sql("DROP FUNCTION kn.fail_review_write()").update();
            }
            var result=service.resolve(scope,id,request(),Map.of("T3","초기 코드"));assertThat(result.version()).isEqualTo(2);assertThat(changed).hasValue(1);
            assertThat(jdbc.sql("SELECT count(*) FROM gw.audit_event WHERE event_type='REVIEW_RESOLVED'").query(Integer.class).single()).isOne();
            assertThat(jdbc.sql("SELECT permissions::text FROM kn.verified_answer").query(String.class).single()).isEqualTo("[\"ADMIN\", \"READ\"]");
            assertThat(jdbc.sql("SELECT answer_masked::text FROM kn.verified_answer").query(String.class).single()).contains("citations", "100");
            assertThat(jdbc.sql("SELECT version FROM kn.glossary WHERE term='T3'").query(Integer.class).single()).isOne();
            jdbc.sql("UPDATE kn.feedback SET version=3,state='OPEN' WHERE id=:id").param("id",id).update();
            var next=(tools.jackson.databind.node.ObjectNode)request();next.put("version",3);service.resolve(scope,id,next,Map.of("T3","초기 풀이"));
            assertThat(jdbc.sql("SELECT version FROM kn.glossary WHERE term='T3'").query(Integer.class).single()).isEqualTo(2);
            context.getBean(FeedbackRepository.class).initializeGlossary(scope,Map.of("T3","다른 초기 풀이","S1F2","초기 코드"));
            assertThat(context.getBean(FeedbackRepository.class).glossary(scope)).containsEntry("T3","설비 코드").containsEntry("S1F2","초기 코드");
            assertThat(jdbc.sql("SELECT version FROM kn.glossary WHERE term='T3'").query(Integer.class).single()).isEqualTo(2);
        }}
    }
    @Test void preparedRevisionAndVersionAreRecheckedInsideTheSavingTransaction() {
        try(var database=rawDatabase()){database.bootstrap();database.migrate();try(var context=application(database)) {
            var jdbc=context.getBean(JdbcClient.class);var store=context.getBean(FeedbackRepository.class);seed(jdbc,context.getBean(DocumentIndexRepository.class));var documents=store.documents(scope,List.of(location));
            var correction=new FeedbackStore.Correction(documents,List.of(location),List.of("ADMIN","READ"),new float[]{1,0});
            jdbc.sql("UPDATE kn.document SET revision=repeat('b',64)").update();
            assertThatThrownBy(()->store.resolve(scope,id,1,"RETRIEVAL",action(),correction,Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.code()).isEqualTo("REVIEW_CONFLICT"));
            jdbc.sql("UPDATE kn.document SET revision=repeat('a',64)").update();jdbc.sql("UPDATE kn.feedback SET version=2").update();
            assertThatThrownBy(()->store.resolve(scope,id,1,"RETRIEVAL",action(),correction,Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.code()).isEqualTo("REVIEW_CONFLICT"));
            assertThat(jdbc.sql("SELECT count(*) FROM kn.review").query(Integer.class).single()).isZero();
        }}
    }
    @Test void glossaryCyclesAreRejectedAgainstBothInitialAndCommittedTerms() {
        try(var database=rawDatabase()){database.bootstrap();database.migrate();try(var context=application(database)) {
            var jdbc=context.getBean(JdbcClient.class);seed(jdbc,context.getBean(DocumentIndexRepository.class));var changed=new AtomicInteger();
            assertThatThrownBy(()->service(context,changed).resolve(scope,id,request(),Map.of("설비 코드","T3"))).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.status()).isEqualTo(400));
            assertThat(jdbc.sql("SELECT count(*) FROM kn.review").query(Integer.class).single()).isZero();assertThat(changed).hasValue(0);
            var store=context.getBean(FeedbackRepository.class);store.initializeGlossary(scope,Map.of("T3","초기 코드"));service(context,changed).resolve(scope,id,request(),Map.of("T3","초기 코드"));
            assertThat(jdbc.sql("SELECT version FROM kn.glossary WHERE term='T3'").query(Integer.class).single()).isEqualTo(2);
            jdbc.sql("UPDATE kn.feedback SET state='OPEN',version=3 WHERE id=:id").param("id",id).update();
            var cyclic=(tools.jackson.databind.node.ObjectNode)request();cyclic.put("version",3);((tools.jackson.databind.node.ObjectNode)cyclic.get("action")).set("terms",SchemaContracts.JSON.valueToTree(Map.of("설비 코드","T3")));
            assertThatThrownBy(()->service(context,changed).resolve(scope,id,cyclic,Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.status()).isEqualTo(400));
            assertThat(jdbc.sql("SELECT count(*) FROM kn.review").query(Integer.class).single()).isOne();assertThat(changed).hasValue(1);
        }}
    }
}
