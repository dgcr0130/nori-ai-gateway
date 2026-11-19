package com.nori.ai.gateway.persistence.repository;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;
import static com.nori.ai.gateway.core.knowledge.DocumentCorpus.*;

public final class DocumentIndexRepository implements DocumentIndexStore {
    private final JdbcClient jdbc;
    private final TransactionTemplate tx;
    private final Clock clock;
    public DocumentIndexRepository(JdbcClient jdbc, TransactionTemplate tx, Clock clock) { this.jdbc=jdbc;this.tx=tx;this.clock=clock; }
    private JdbcClient.StatementSpec sql(VerifiedSystemScope scope,String sql) { return jdbc.sql(sql).param("system",Objects.requireNonNull(scope).boundSystem().name()); }
    @Override public boolean hasActiveIndex(VerifiedSystemScope scope) { return sql(scope,"SELECT EXISTS(SELECT 1 FROM kn.index_set WHERE system_id=:system AND state='ACTIVE')").query(Boolean.class).single(); }
    /** 현재 활성 여부와 설정 지문을 매 질문에 확인하여 검토 취소·구 설정 답변을 배제합니다. */
    public Optional<Bundle> searchState(VerifiedSystemScope scope) {
        var current=sql(scope,"SELECT id,embedding_model,embedding_dimension,config_sha256 FROM kn.index_set WHERE system_id=:system AND state='ACTIVE'")
            .query((rs,n)->new Bundle(rs.getObject(1,UUID.class),rs.getString(2),rs.getInt(3),rs.getString(4),List.of(),List.of())).optional();
        return current.map(bundle->{
            var answers=sql(scope,"SELECT id,question_masked,locations::text,permissions::text,embedding::text FROM kn.verified_answer WHERE system_id=:system AND active AND embedding_model=:model AND embedding_dimension=:dimension AND config_sha256=:sha ORDER BY id")
                .param("model",bundle.embeddingModel()).param("dimension",bundle.dimension()).param("sha",bundle.configSha256())
                .query((rs,n)->new Answer(rs.getObject(1,UUID.class),rs.getString(2),locations(rs.getString(3)),strings(rs.getString(4)),vector(rs.getString(5)))).list();
            return new Bundle(bundle.id(),bundle.embeddingModel(),bundle.dimension(),bundle.configSha256(),List.of(),answers);
        });
    }
    @Override public void cleanNonActive(VerifiedSystemScope scope) { sql(scope,"DELETE FROM kn.index_set WHERE system_id=:system AND state IN ('INACTIVE','BUILDING')").update(); }
    @Override public List<Answer> activeAnswers(VerifiedSystemScope scope) {
        return sql(scope,"SELECT id,question_masked,locations::text,permissions::text,embedding::text FROM kn.verified_answer WHERE system_id=:system AND active ORDER BY id")
            .query((rs,n)->new Answer(rs.getObject(1,UUID.class),rs.getString(2),locations(rs.getString(3)),strings(rs.getString(4)),vector(rs.getString(5)))).list();
    }
    @Override public void saveBuilding(VerifiedSystemScope scope,Bundle bundle) {
        tx.executeWithoutResult(status->{
            sql(scope,"INSERT INTO kn.index_set(system_id,id,embedding_model,embedding_dimension,config_sha256,state,created_at) VALUES(:system,:id,:model,:dimension,:sha,'BUILDING',:now)")
                .param("id",bundle.id()).param("model",bundle.embeddingModel()).param("dimension",bundle.dimension()).param("sha",bundle.configSha256()).param("now",Timestamp.from(clock.instant())).update();
            for(var document:bundle.documents()) {
                sql(scope,"INSERT INTO kn.document(system_id,id,index_set_id,bucket,object_key,revision,title,permissions,extraction_issues) VALUES(:system,:id,:set,:bucket,:key,:revision,:title,CAST(:permissions AS jsonb),CAST(:issues AS jsonb))")
                    .param("id",document.id()).param("set",bundle.id()).param("bucket",document.bucket()).param("key",document.objectKey()).param("revision",document.revision()).param("title",document.title())
                    .param("permissions",json(document.permissions())).param("issues",json(document.issues())).update();
                for(var parent:document.parents()) {
                    sql(scope,"INSERT INTO kn.parent_chunk(system_id,id,document_id,location,header,body_masked,body_sha256) VALUES(:system,:id,:document,CAST(:location AS jsonb),:header,:body,:sha)")
                        .param("id",parent.id()).param("document",document.id()).param("location",json(parent.location())).param("header",parent.header()).param("body",parent.bodyMasked()).param("sha",parent.bodySha256()).update();
                    for(var child:parent.children()) {
                        validateVector(child.embedding(),bundle.dimension());
                        sql(scope,"INSERT INTO kn.child_chunk(system_id,id,parent_id,ordinal,location,header,body_masked,identifiers,embedding) VALUES(:system,:id,:parent,:ordinal,CAST(:location AS jsonb),:header,:body,CAST(:identifiers AS jsonb),CAST(:embedding AS vector))")
                            .param("id",child.id()).param("parent",parent.id()).param("ordinal",child.ordinal()).param("location",json(child.location())).param("header",child.header()).param("body",child.bodyMasked())
                            .param("identifiers",json(child.identifiers())).param("embedding",json(child.embedding())).update();
                    }
                }
            }
        });
    }
    @Override public Bundle load(VerifiedSystemScope scope,UUID bundleId) {
        var settings=sql(scope,"SELECT embedding_model,embedding_dimension,config_sha256 FROM kn.index_set WHERE system_id=:system AND id=:id")
            .param("id",bundleId).query((rs,n)->new Bundle(bundleId,rs.getString(1),rs.getInt(2),rs.getString(3),List.of(),List.of())).single();
        var documents=sql(scope,"SELECT id,bucket,object_key,revision,title,permissions::text,extraction_issues::text FROM kn.document WHERE system_id=:system AND index_set_id=:id ORDER BY bucket COLLATE \"C\",object_key COLLATE \"C\"")
            .param("id",bundleId).query((rs,n)->new DocumentCorpus.Document(rs.getObject(1,UUID.class),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),strings(rs.getString(6)),issues(rs.getString(7)),List.of())).list();
        var complete=new ArrayList<DocumentCorpus.Document>();
        for(var document:documents) {
            var parents=sql(scope,"SELECT id,location::text,header,body_masked,body_sha256 FROM kn.parent_chunk WHERE system_id=:system AND document_id=:id ORDER BY (location->>'lineFrom')::integer")
                .param("id",document.id()).query((rs,n)->new Parent(rs.getObject(1,UUID.class),location(rs.getString(2)),rs.getString(3),rs.getString(4),rs.getString(5),List.of())).list();
            var completeParents=new ArrayList<Parent>();
            for(var parent:parents) {
                var children=sql(scope,"SELECT id,ordinal,location::text,header,body_masked,identifiers::text,embedding::text FROM kn.child_chunk WHERE system_id=:system AND parent_id=:id ORDER BY ordinal")
                    .param("id",parent.id()).query((rs,n)->new Child(rs.getObject(1,UUID.class),rs.getInt(2),location(rs.getString(3)),rs.getString(4),rs.getString(5),new ArrayList<>(strings(rs.getString(6))),vector(rs.getString(7)))).list();
                completeParents.add(new Parent(parent.id(),parent.location(),parent.header(),parent.bodyMasked(),parent.bodySha256(),children));
            }
            complete.add(new DocumentCorpus.Document(document.id(),document.bucket(),document.objectKey(),document.revision(),document.title(),document.permissions(),document.issues(),completeParents));
        }
        return new Bundle(bundleId,settings.embeddingModel(),settings.dimension(),settings.configSha256(),complete,List.of());
    }
    @Override public void activate(VerifiedSystemScope scope,Bundle bundle) {
        tx.executeWithoutResult(status->{
            var state=sql(scope,"SELECT state FROM kn.index_set WHERE system_id=:system AND id=:id AND embedding_model=:model AND embedding_dimension=:dimension AND config_sha256=:sha FOR UPDATE")
                .param("id",bundle.id()).param("model",bundle.embeddingModel()).param("dimension",bundle.dimension()).param("sha",bundle.configSha256()).query(String.class).single();
            if(!state.equals("BUILDING")) throw new IllegalStateException("준비 중인 색인 묶음이 아닙니다");
            sql(scope,"UPDATE kn.index_set SET state='INACTIVE' WHERE system_id=:system AND state='ACTIVE'").update();
            sql(scope,"UPDATE kn.verified_answer SET active=false WHERE system_id=:system AND active").update();
            for(var answer:bundle.answers()) {
                validateVector(answer.embedding(),bundle.dimension());
                if(sql(scope,"UPDATE kn.verified_answer SET active=true,embedding_model=:model,embedding_dimension=:dimension,config_sha256=:sha,embedding=CAST(:embedding AS vector),permissions=CAST(:permissions AS jsonb) WHERE system_id=:system AND id=:id AND question_masked=:question")
                        .param("id",answer.id()).param("question",answer.questionMasked()).param("model",bundle.embeddingModel()).param("dimension",bundle.dimension()).param("sha",bundle.configSha256()).param("embedding",json(answer.embedding())).param("permissions",json(answer.permissions())).update()!=1)
                    throw new IllegalStateException("검증 답변이 변경되었습니다");
            }
            // DOCUMENT 문항은 정답 위치 대신 보완 대상 revision을 비교합니다. 영구 스냅샷은 손대지 않습니다.
            sql(scope,"""
                UPDATE kn.question_case q SET active=false WHERE q.system_id=:system AND q.active
                AND EXISTS (SELECT 1 FROM kn.review r WHERE r.system_id=q.system_id AND r.feedback_id=q.feedback_id
                  AND r.feedback_version=q.review_version AND r.cause='DOCUMENT')
                AND jsonb_typeof(q.payload->'documentTargets')='array'
                AND EXISTS (SELECT 1 FROM jsonb_array_elements(q.payload->'documentTargets') target
                  WHERE NOT EXISTS (SELECT 1 FROM kn.document d WHERE d.system_id=q.system_id AND d.index_set_id=:id
                    AND d.bucket=target->>'bucket' AND d.object_key=target->>'objectKey' AND d.revision=target->>'revision'))
                """).param("id",bundle.id()).update();
            if(sql(scope,"UPDATE kn.index_set SET state='ACTIVE' WHERE system_id=:system AND id=:id AND state='BUILDING'").param("id",bundle.id()).update()!=1) throw new IllegalStateException("색인 묶음을 활성화할 수 없습니다");
        });
    }
    @Override public Optional<String> currentRevision(VerifiedSystemScope scope,String bucket,String objectKey) {
        return sql(scope,"SELECT d.revision FROM kn.document d JOIN kn.index_set s ON s.system_id=d.system_id AND s.id=d.index_set_id WHERE d.system_id=:system AND s.state='ACTIVE' AND d.bucket=:bucket AND d.object_key=:key AND d.extraction_issues='[]'::jsonb")
            .param("bucket",bucket).param("key",objectKey).query(String.class).optional();
    }
    private static void validateVector(float[] vector,int dimension) { if(vector.length!=dimension)throw new IllegalArgumentException("벡터 차원이 다릅니다");for(float value:vector)if(!Float.isFinite(value))throw new IllegalArgumentException("벡터 값이 잘못되었습니다"); }
    private static String json(Object value) { return SchemaContracts.JSON.writeValueAsString(value); }
    private static float[] vector(String value) { return SchemaContracts.JSON.readValue(value,float[].class); }
    private static DocumentLocation location(String value) { return SchemaContracts.JSON.readValue(value,DocumentLocation.class); }
    private static List<DocumentLocation> locations(String value) { return SchemaContracts.JSON.readValue(value,new tools.jackson.core.type.TypeReference<List<DocumentLocation>>(){}); }
    private static Set<String> strings(String value) { return SchemaContracts.JSON.readValue(value,new tools.jackson.core.type.TypeReference<LinkedHashSet<String>>(){}); }
    private static List<Issue> issues(String value) { return SchemaContracts.JSON.readValue(value,new tools.jackson.core.type.TypeReference<List<Issue>>(){}); }
}
