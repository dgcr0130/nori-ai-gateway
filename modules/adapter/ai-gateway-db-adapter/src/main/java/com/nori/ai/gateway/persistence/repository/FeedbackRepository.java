package com.nori.ai.gateway.persistence.repository;

import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import com.nori.ai.gateway.persistence.audit.AuditWriter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.JsonNode;
import java.sql.*;
import java.time.*;
import java.util.*;
import static com.nori.ai.gateway.core.feedback.FeedbackStore.*;

/** 영구 스냅샷과 평가 이력을 다루며 일반 사용자 경로만 생존 대화의 소유를 요구합니다. */
public final class FeedbackRepository extends ScopedJdbc implements FeedbackStore {
    private final DocumentIndexRepository indexes;
    private final AuditWriter audit;
    public FeedbackRepository(JdbcClient jdbc,TransactionTemplate tx,Clock clock,DocumentIndexRepository indexes,AuditWriter audit) {super(jdbc,tx,clock);this.indexes=indexes;this.audit=audit;}
    private static final String VISIBLE="""
        NOT EXISTS (SELECT 1 FROM jsonb_array_elements(s.evidence) e
          WHERE jsonb_typeof(e->'permissions') IS DISTINCT FROM 'array' OR e->'permissions'='[]'::jsonb)
        AND NOT EXISTS (SELECT 1 FROM jsonb_array_elements(s.evidence) e,
          LATERAL jsonb_array_elements_text(CASE WHEN jsonb_typeof(e->'permissions')='array' THEN e->'permissions' ELSE '[]'::jsonb END) AS p(permission)
          WHERE NOT jsonb_exists(CAST(:permissions AS jsonb),p.permission))
        """;
    private static final String SELECT="""
        SELECT f.id,f.version,f.state,f.rating,f.comment_masked,f.created_at,f.updated_at
        FROM kn.feedback f JOIN kn.answer_snapshot s ON s.system_id=f.system_id AND s.id=f.answer_id
        WHERE f.system_id=:bound AND
        """+VISIBLE;
    private JdbcClient.StatementSpec visible(VerifiedSystemScope scope,String sql) {
        if(!scope.validAt(clock.instant()))throw FeedbackFailure.missing();
        return scoped(scope,sql).param("permissions",json(new TreeSet<>(scope.perm())));
    }
    private void lock(VerifiedSystemScope scope) {
        // 활성 묶음 전환과 검토/재개방이 같은 순서로 시스템별 짧은 확정 잠금을 잡습니다.
        scoped(scope,"SELECT pg_advisory_xact_lock(hashtextextended(:bound,806008))").query((rs,n)->true).single();
    }
    @Override public Feedback evaluate(VerifiedSystemScope scope,String answerId,String rating,String comment) {
        return transaction(()->{
            if(!scope.validAt(clock.instant()))throw FeedbackFailure.missing();
            var owner=scoped(scope,"""
                SELECT c.id FROM gw.job j JOIN gw.conversation c ON c.id=j.conversation_id AND c.owner_system=j.owner_system
                WHERE j.owner_system=:bound AND j.actor_ref=:actor AND c.owner_actor_ref=:actor AND j.id=:job FOR UPDATE OF c
                """).param("job",answerId).query(String.class).optional();
            if(owner.isEmpty())throw FeedbackFailure.missing();
            lock(scope);
            var snapshot=visible(scope,"SELECT s.id FROM kn.answer_snapshot s WHERE s.system_id=:bound AND s.actor_ref=:actor AND s.job_id=:job AND "+VISIBLE)
                .param("job",answerId).query(UUID.class).optional();
            if(snapshot.isEmpty()) {
                boolean exists=scoped(scope,"SELECT EXISTS(SELECT 1 FROM kn.answer_snapshot WHERE system_id=:bound AND job_id=:job)").param("job",answerId).query(Boolean.class).single();
                if(exists)throw FeedbackFailure.missing();
                throw new FeedbackFailure(409,"ANSWER_NOT_READY");
            }
            var evidence=scoped(scope,"SELECT evidence::text FROM kn.answer_snapshot WHERE system_id=:bound AND id=:id").param("id",snapshot.get()).query(String.class).single();
            for(var unit:SchemaContracts.JSON.readTree(evidence))if(unit.path("sourceType").asString().equals("DOCUMENT")) {
                try{documents(scope,List.of(SchemaContracts.JSON.treeToValue(unit.get("location"),DocumentLocation.class)));}
                catch(FeedbackFailure unavailable){throw FeedbackFailure.missing();}
            }
            var previous=scoped(scope,"SELECT id,version,state,rating,comment_masked,created_at,updated_at FROM kn.feedback WHERE system_id=:bound AND answer_id=:answer AND actor_ref=:actor FOR UPDATE")
                .param("answer",snapshot.get()).query(FeedbackRepository::feedback).optional();
            if(previous.isPresent()&&previous.get().rating().equals(rating)&&previous.get().comment().equals(comment))return previous.get();
            String state=rating.equals("UP")&&comment.isEmpty()?"RESOLVED":"OPEN";
            UUID id=previous.map(Feedback::feedbackId).orElseGet(UUID::randomUUID);
            if(previous.isPresent()) {
                if(!previous.get().state().equals("OPEN"))state="OPEN";
                deactivate(scope,id);
                scoped(scope,"UPDATE kn.feedback SET rating=:rating,comment_masked=:comment,state=:state,version=version+1,updated_at=:now WHERE system_id=:bound AND id=:id")
                    .param("id",id).param("rating",rating).param("comment",comment).param("state",state).update();
            } else scoped(scope,"""
                INSERT INTO kn.feedback(system_id,id,answer_id,actor_ref,rating,comment_masked,state,version,created_at,updated_at)
                VALUES(:bound,:id,:answer,:actor,:rating,:comment,:state,1,:now,:now)
                """).param("id",id).param("answer",snapshot.get()).param("rating",rating).param("comment",comment).param("state",state).update();
            return find(scope,id,false);
        });
    }
    @Override public List<Feedback> list(VerifiedSystemScope scope,String state,Instant afterTime,UUID afterId,int limit) {
        return visible(scope,SELECT+" AND f.state=:state"+(afterTime==null?"":" AND (f.created_at,f.id)>(:time,:id)")+" ORDER BY f.created_at,f.id LIMIT :limit")
            .param("state",state).params(afterTime==null?Map.of():Map.of("time",Timestamp.from(afterTime),"id",afterId)).param("limit",limit).query(FeedbackRepository::feedback).list();
    }
    private Feedback find(VerifiedSystemScope scope,UUID id,boolean locked) {
        return visible(scope,SELECT+" AND f.id=:id"+(locked?" FOR UPDATE OF f":"")).param("id",id).query(FeedbackRepository::feedback).optional().orElseThrow(FeedbackFailure::missing);
    }
    @Override public Detail detail(VerifiedSystemScope scope,UUID id) {
        return transaction(()->{
            var feedback=find(scope,id,true);
            var snapshot=visible(scope,"""
                SELECT s.job_id,s.question_masked,s.answer::text,s.evidence::text FROM kn.feedback f
                JOIN kn.answer_snapshot s ON s.system_id=f.system_id AND s.id=f.answer_id
                WHERE f.system_id=:bound AND f.id=:id AND
                """+VISIBLE).param("id",id).query((rs,n)->List.of(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4))).single();
            var reviews=scoped(scope,"SELECT feedback_version,reviewer_ref,cause,action::text,created_at FROM kn.review WHERE system_id=:bound AND feedback_id=:id ORDER BY feedback_version")
                .param("id",id).query((rs,n)->(JsonNode)SchemaContracts.JSON.createObjectNode().put("version",rs.getInt(1)).put("reviewerRef",rs.getString(2)).put("cause",rs.getString(3)).put("createdAt",rs.getTimestamp(5).toInstant().toString()).set("action",SchemaContracts.JSON.readTree(rs.getString(4)))).list();
            var questionCase=scoped(scope,"SELECT payload::text FROM kn.question_case WHERE system_id=:bound AND feedback_id=:id AND active ORDER BY review_version DESC LIMIT 1")
                .param("id",id).query(String.class).optional().map(SchemaContracts.JSON::readTree).orElse(null);
            return new Detail(feedback,snapshot.get(0),snapshot.get(1),SchemaContracts.JSON.readTree(snapshot.get(2)),SchemaContracts.JSON.readTree(snapshot.get(3)),reviews,questionCase);
        });
    }
    @Override public Documents documents(VerifiedSystemScope scope,List<DocumentLocation> locations) {
        var state=indexes.searchState(scope).orElseThrow(FeedbackFailure::conflict);var bundle=indexes.load(scope,state.id());var units=new ArrayList<EvidenceUnit>();
        for(var location:locations) {
            var document=bundle.documents().stream().filter(d->d.issues().isEmpty()&&d.bucket().equals(location.bucket())&&d.objectKey().equals(location.objectKey())&&d.revision().equals(location.revision())).findFirst().orElseThrow(FeedbackFailure::conflict);
            if(!scope.perm().containsAll(document.permissions()))throw FeedbackFailure.missing();
            EvidenceUnit selected=null;
            for(var parent:document.parents()) {
                if(parent.location().equals(location)&&!parent.children().isEmpty())selected=EvidenceUnit.document(new DocumentSearch.Hit(parent,parent.children().getFirst(),document.permissions()),parent.children().stream().map(DocumentCorpus.Child::location).toList());
                for(var child:parent.children())if(child.location().equals(location))selected=new EvidenceUnit("DOCUMENT",parent.id().toString(),SchemaContracts.JSON.valueToTree(location),child.header(),child.bodyMasked(),ordered(document.permissions()),"SUCCESS",null,List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of(location));
            }
            if(selected==null)throw FeedbackFailure.invalid();units.add(selected);
        }
        return new Documents(state.id(),state.embeddingModel(),state.dimension(),state.configSha256(),units);
    }
    @Override public Feedback resolve(VerifiedSystemScope scope,UUID id,int version,String cause,JsonNode action,Correction correction,Map<String,String> initialGlossary) {
        return transaction(()->{
            lock(scope);var feedback=find(scope,id,true);
            if(feedback.version()!=version||!feedback.state().equals("OPEN"))throw FeedbackFailure.conflict();
            var detail=detail(scope,id);
            if(correction!=null) {
                var current=documents(scope,correction.locations());
                if(!current.bundleId().equals(correction.documents().bundleId())||!current.model().equals(correction.documents().model())||current.dimension()!=correction.documents().dimension()||!current.configSha256().equals(correction.documents().configSha256()))throw FeedbackFailure.conflict();
            }
            if(action.has("terms")) {
                var merged=new TreeMap<>(initialGlossary);merged.putAll(glossary(scope));action.get("terms").properties().forEach(e->merged.put(e.getKey(),e.getValue().asString()));
                for(String term:merged.keySet())cycle(term,merged,new HashSet<>());
            }
            UUID review=UUID.randomUUID();
            scoped(scope,"INSERT INTO kn.review(system_id,id,feedback_id,feedback_version,reviewer_ref,cause,action,created_at) VALUES(:bound,:id,:feedback,:version,:actor,:cause,CAST(:action AS jsonb),:now)")
                .param("id",review).param("feedback",id).param("version",version).param("cause",cause).param("action",action.toString()).update();
            deactivate(scope,id);
            if(correction!=null&&action.has("correctAnswer")) {
                var settings=correction.documents();
                scoped(scope,"""
                    INSERT INTO kn.verified_answer(system_id,id,review_id,question_masked,answer_masked,embedding_model,embedding_dimension,config_sha256,locations,permissions,embedding,active)
                    VALUES(:bound,:id,:review,:question,CAST(:answer AS jsonb),:model,:dimension,:sha,CAST(:locations AS jsonb),CAST(:permissions AS jsonb),CAST(:embedding AS vector),true)
                    """).param("id",UUID.randomUUID()).param("review",review).param("question",detail.question()).param("answer",action.get("correctAnswer").toString())
                    .param("model",settings.model()).param("dimension",settings.dimension()).param("sha",settings.configSha256()).param("locations",json(correction.locations())).param("permissions",json(correction.permissions())).param("embedding",json(correction.embedding())).update();
            }
            if(action.has("terms"))for(var entry:action.get("terms").properties())scoped(scope,"""
                INSERT INTO kn.glossary(system_id,term,expansion,review_id,version) VALUES(:bound,:term,:expansion,:review,1)
                ON CONFLICT(system_id,term) DO UPDATE SET expansion=EXCLUDED.expansion,review_id=EXCLUDED.review_id,version=kn.glossary.version+1
                """).param("term",entry.getKey()).param("expansion",entry.getValue().asString()).param("review",review).update();
            if(correction!=null) {
                UUID caseId=UUID.randomUUID();boolean answerable=action.has("correctAnswer");
                var payload=SchemaContracts.JSON.createObjectNode().put("id",caseId.toString()).put("systemId",scope.boundSystem().name()).put("question",detail.question()).put("answerable",answerable).put("origin","FEEDBACK").put("feedbackId",id.toString()).put("reviewVersion",version);
                payload.putNull("context");payload.putNull("predecessorId");payload.set("permissions",SchemaContracts.JSON.valueToTree(correction.permissions()));
                payload.set("expectedFacts",answerable?action.get("expectedFacts"):SchemaContracts.JSON.createArrayNode());
                payload.set("evidenceLocations",answerable?action.get("locations"):SchemaContracts.JSON.createArrayNode());
                var targets=payload.putArray("documentTargets");
                if(!answerable)for(var location:correction.locations())targets.addObject().put("bucket",location.bucket()).put("objectKey",location.objectKey()).put("revision",location.revision());
                scoped(scope,"INSERT INTO kn.question_case(system_id,id,feedback_id,review_version,payload,active,created_at) VALUES(:bound,:id,:feedback,:version,CAST(:payload AS jsonb),true,:now)")
                    .param("id",caseId).param("feedback",id).param("version",version).param("payload",payload.toString()).update();
            }
            scoped(scope,"UPDATE kn.feedback SET state=:state,version=version+1,updated_at=:now WHERE system_id=:bound AND id=:id")
                .param("id",id).param("state",cause.equals("REJECT")?"REJECTED":"RESOLVED").update();
            audit.write(new com.nori.ai.gateway.core.audit.AuditEvent(scope,com.nori.ai.gateway.core.audit.AuditEventType.REVIEW_RESOLVED,null,null,null,null,null,null,null,null,null,null));
            return find(scope,id,false);
        });
    }
    private void deactivate(VerifiedSystemScope scope,UUID id) {
        scoped(scope,"UPDATE kn.verified_answer a SET active=false FROM kn.review r WHERE a.system_id=:bound AND r.system_id=a.system_id AND a.review_id=r.id AND r.feedback_id=:id").param("id",id).update();
        scoped(scope,"UPDATE kn.question_case SET active=false WHERE system_id=:bound AND feedback_id=:id AND active").param("id",id).update();
    }
    @Override public Map<String,String> glossary(VerifiedSystemScope scope) {
        var result=new LinkedHashMap<String,String>();scoped(scope,"SELECT term,expansion FROM kn.glossary WHERE system_id=:bound ORDER BY term COLLATE \"C\"").query((rs,n)->Map.entry(rs.getString(1),rs.getString(2))).list().forEach(e->result.put(e.getKey(),e.getValue()));return Collections.unmodifiableMap(result);
    }
    public void initializeGlossary(VerifiedSystemScope scope,Map<String,String> initial) {
        transaction(()->{
            lock(scope);
            // config 초기값만 교체합니다. 검토로 확정한 행과 그 version은 보존합니다.
            scoped(scope,"DELETE FROM kn.glossary WHERE system_id=:bound AND review_id IS NULL").update();
            for(var entry:new TreeMap<>(initial).entrySet())scoped(scope,"INSERT INTO kn.glossary(system_id,term,expansion,review_id,version) VALUES(:bound,:term,:expansion,NULL,1) ON CONFLICT(system_id,term) DO NOTHING")
                .param("term",entry.getKey()).param("expansion",entry.getValue()).update();
            return true;
        });
    }
    public long projectionVersion(VerifiedSystemScope scope) {
        return scoped(scope,"SELECT COALESCE((SELECT sum(version) FROM kn.feedback WHERE system_id=:bound),0)+COALESCE((SELECT sum(version) FROM kn.glossary WHERE system_id=:bound),0)").query(Long.class).single();
    }
    private static void cycle(String term,Map<String,String> values,Set<String> path) {
        if(!path.add(term))throw FeedbackFailure.invalid();for(String next:values.keySet())if(values.get(term).contains(next))cycle(next,values,path);path.remove(term);
    }
    private static Feedback feedback(ResultSet rs,int n)throws SQLException {return new Feedback(rs.getObject(1,UUID.class),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getTimestamp(6).toInstant(),rs.getTimestamp(7).toInstant());}
    private static String json(Object value){return SchemaContracts.JSON.writeValueAsString(value);}
    private static List<String> ordered(Set<String> values){return values.stream().sorted((a,b)->Arrays.compareUnsigned(a.getBytes(java.nio.charset.StandardCharsets.UTF_8),b.getBytes(java.nio.charset.StandardCharsets.UTF_8))).toList();}
}
