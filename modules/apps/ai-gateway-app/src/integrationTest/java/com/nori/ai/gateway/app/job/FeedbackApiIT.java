package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.AnswerSnapshot;
import com.nori.ai.gateway.core.job.TerminalSpec;
import com.nori.ai.gateway.app.hooks.PausePointRegistry;
import com.nori.ai.gateway.persistence.queue.JobTerminalCommitter;
import com.nori.ai.gateway.persistence.repository.DocumentIndexRepository;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

/** 운영 앱 HTTP·인증·PG 트랜잭션·로컬 임베딩을 함께 검사하며 외부 서버를 사용하지 않습니다. */
class FeedbackApiIT extends LlmStubSupport {
    static final class ProjectionFault implements DocumentKeywordIndex {
        final MemoryDocumentKeywordIndex delegate;volatile boolean fail;
        final java.util.concurrent.atomic.AtomicReference<PreparationPause> nextPause=new java.util.concurrent.atomic.AtomicReference<>();
        volatile DocumentCorpus.Bundle installed;
        record PreparationPause(CountDownLatch entered,CountDownLatch release){}
        ProjectionFault(MemoryDocumentKeywordIndex delegate){this.delegate=delegate;}
        PreparationPause holdPreparation(){var pause=new PreparationPause(new CountDownLatch(1),new CountDownLatch(1));if(!nextPause.compareAndSet(null,pause))throw new IllegalStateException("이미 보류한 투영이 있습니다");return pause;}
        public Prepared prepare(com.nori.ai.gateway.core.scope.VerifiedSystemScope scope,DocumentCorpus.Bundle bundle){
            if(fail)throw new IllegalStateException("고정 투영 실패");var pause=nextPause.getAndSet(null);
            if(pause!=null){pause.entered().countDown();try{pause.release().await();}catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new IllegalStateException("시험 투영 대기가 중단되었습니다");}}
            var prepared=delegate.prepare(scope,bundle);return new Projection(prepared,bundle);
        }
        record Projection(Prepared delegate,DocumentCorpus.Bundle bundle) implements Prepared {public void close(){delegate.close();}}
        public void install(com.nori.ai.gateway.core.scope.VerifiedSystemScope scope,Prepared prepared){var projection=(Projection)prepared;delegate.install(scope,projection.delegate());installed=projection.bundle();}
    }
    @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods=false)
    static class ProjectionFixture {
        @org.springframework.context.annotation.Bean @org.springframework.context.annotation.Primary
        ProjectionFault projectionFault(MemoryDocumentKeywordIndex index){return new ProjectionFault(index);}
    }
    @Override protected Class<?>[] extraSources(){var sources=new ArrayList<Class<?>>(List.of(super.extraSources()));sources.add(ProjectionFixture.class);return sources.toArray(Class<?>[]::new);}
    final DocumentLocation location=new DocumentLocation("fixture-bucket","rag/fixed.md","a".repeat(64),"설정",5,5);
    final DocumentLocation other=new DocumentLocation("fixture-bucket","rag/other.md","b".repeat(64),"설정",7,7);
    Set<String> reviewer(){var values=new HashSet<>(PERMS);values.add("AI_REVIEW");return Set.copyOf(values);}
    HttpResponse<String> http(String method,String path,String actor,Object input)throws Exception{return raw(method,path,actor,input==null?"":json.writeValueAsString(input),false);}
    HttpResponse<String> raw(String method,String path,String actor,String input,boolean dlg)throws Exception {
        var request=HttpRequest.newBuilder(uri(path)).timeout(Duration.ofSeconds(20)).header("Authorization","Bearer "+serviceToken).header("X-Actor-Assertion",signer.sign(claims(actor,dlg?dlg():null),clock().instant())).header("Content-Type","application/json").method(method,input.isEmpty()?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(input,StandardCharsets.UTF_8)).build();
        try(var client=HttpClient.newHttpClient()){return client.send(request,HttpResponse.BodyHandlers.ofString());}
    }
    void error(HttpResponse<String> response,int status,String code){assertThat(response.statusCode()).isEqualTo(status);var body=json.readTree(response.body());assertThat(body.propertyNames()).containsExactlyInAnyOrder("code","message","requestId");assertThat(body.path("code").asString()).isEqualTo(code);assertThat(body.path("requestId").asString()).matches("[0-9a-f-]{36}");}
    JsonNode evidence(String type,List<String> permissions) {
        String body=type.equals("TRACE")?"{\"endTimeUnixNano\":\"2\",\"name\":\"설비 상태\",\"service\":\"tc-service\",\"serviceVersion\":\"v1\",\"spanId\":\"span-1\",\"startTimeUnixNano\":\"1\"}":"설비 T3 코드 100";
        Object place=switch(type){case "LOG"->Map.of("app","tc-app","namespace","prod","occurredAt",NOW.toString());case "TRACE"->Map.of("namespace","prod","traceId","trace-1","service","tc-service","spanId","span-1","startTimeUnixNano","1","endTimeUnixNano","2","serviceVersion","v1");default->location;};
        var value=SchemaContracts.JSON.createObjectNode().put("number",1).put("sourceType",type).put("bodyMasked",body).put("bodySha256",AnswerSnapshot.sha(body));
        value.set("permissions",SchemaContracts.JSON.valueToTree(permissions));value.set("location",SchemaContracts.JSON.valueToTree(place));
        if(!type.equals("DOCUMENT")){value.put("status","SUCCESS").put("observedAt",NOW.toString());value.putArray("missingFields");value.putArray("warnings");value.set("retentionMeta",SchemaContracts.JSON.valueToTree(Map.of("missingRanges",List.of(),"unverifiedRanges",List.of())));}return value;
    }
    JobAdmissionService.Accepted answer(List<JsonNode> units)throws Exception {
        return answer("actor","설비 T3 코드",units);
    }
    JobAdmissionService.Accepted answer(String actor,String question,List<JsonNode> units)throws Exception {
        pauses().hold(PausePointRegistry.Point.DISPATCH_HOLD,"*");var job=accept(actor,UUID.randomUUID().toString(),question,null,dlg());
        context.getBean(JobTerminalCommitter.class).commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.CANCELLED));
        jdbc().sql("INSERT INTO kn.answer_snapshot VALUES('TC',:id,:job,:actor,:question,CAST(:answer AS jsonb),CAST(:evidence AS jsonb),repeat('c',64),:now)")
            .param("id",UUID.randomUUID()).param("job",job.jobId()).param("actor",actor).param("question",question).param("answer","{\"kind\":\"NO_EVIDENCE\",\"text\":\"근거 없음\",\"citations\":[],\"sentences\":[]}").param("evidence",json.writeValueAsString(units)).param("now",java.sql.Timestamp.from(clock().instant())).update();return job;
    }
    UUID feedback(String job,String rating,String comment)throws Exception {var response=http("PUT","/v1/answers/"+job+"/feedback","actor",Map.of("rating",rating,"comment",comment));assertThat(response.statusCode()).isEqualTo(200);return UUID.fromString(json.readTree(response.body()).path("feedbackId").asString());}
    Object resolution(int version,String cause,Object action){return Map.of("version",version,"cause",cause,"action",action);}
    Map<String,Object> correct(List<DocumentLocation> locations,String fact,String text,int citation){return Map.of("locations",locations,"expectedFacts",List.of(fact),"correctAnswer",Map.of("sentences",List.of(Map.of("text",text,"citations",List.of(citation)))));}
    void corpus() {
        var docs=new ArrayList<DocumentCorpus.Document>();for(var place:List.of(location,other)) {
            String body=place.equals(location)?"설비 T3 S1F2 코드는 100이고 linger.ms는 1.5초입니다.":"설비 S9F9 코드는 200입니다.";
            var child=new DocumentCorpus.Child(UUID.randomUUID(),0,place,"운영 > 설정",body,List.of(),new float[]{.5f,.5f,.5f});
            var parent=new DocumentCorpus.Parent(UUID.randomUUID(),place,"운영 > 설정",body,AnswerSnapshot.sha(body),List.of(child));
            docs.add(new DocumentCorpus.Document(UUID.randomUUID(),place.bucket(),place.objectKey(),place.revision(),"운영",Set.of("AI_DOC_READ"),List.of(),List.of(parent)));
        }
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"bge-m3",3,"c".repeat(64),docs,List.of());var store=context.getBean(DocumentIndexRepository.class);var index=context.getBean(MemoryDocumentKeywordIndex.class);store.saveBuilding(scope("actor"),bundle);
        try(var prepared=index.prepare(scope("actor"),store.load(scope("actor"),bundle.id()))){store.activate(scope("actor"),bundle);index.install(scope("actor"),prepared);}
    }
    @Test void ownershipNotReadyDeletedConversationAndPermanentReviewUsePublicJobId()throws Exception {
        corpus();
        var job=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));String path="/v1/answers/"+job.jobId()+"/feedback";
        error(http("PUT",path,"other",Map.of("rating","DOWN","comment","지적")),404,"NOT_FOUND");
        error(http("PUT","/v1/answers/missing/feedback","actor",Map.of("rating","DOWN","comment","지적")),404,"NOT_FOUND");
        var pending=accept("actor","no-snapshot","질문",null,dlg());error(http("PUT","/v1/answers/"+pending.jobId()+"/feedback","actor",Map.of("rating","DOWN","comment","")),409,"ANSWER_NOT_READY");
        UUID id=feedback(job.jobId(),"DOWN","지적");perms=reviewer();var detail=http("GET","/v1/reviews/"+id,"reviewer",null);assertThat(detail.statusCode()).isEqualTo(200);
        String internal=jdbc().sql("SELECT id::text FROM kn.answer_snapshot WHERE job_id=:job").param("job",job.jobId()).query(String.class).single();assertThat(detail.body()).contains(job.jobId()).doesNotContain(internal);
        assertThat(json.readTree(detail.body()).path("questionCase").isNull()).isTrue();assertThat(http("DELETE","/v1/conversations/"+job.conversationId(),"actor",null).statusCode()).isEqualTo(204);
        error(http("PUT",path,"actor",Map.of("rating","DOWN","comment","수정")),404,"NOT_FOUND");assertThat(http("GET","/v1/reviews/"+id,"reviewer",null).statusCode()).isEqualTo(200);
        assertThat(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(1,"REJECT",Map.of("note","기각"))).statusCode()).isEqualTo(200);
    }
    @Test void identicalAndConcurrentEvaluationsKeepOneRowAndTheRightVersionAndOpenRules()throws Exception {
        var job=answer(List.of());UUID id=feedback(job.jobId(),"UP","");assertThat(jdbc().sql("SELECT state FROM kn.feedback WHERE id=:id").param("id",id).query(String.class).single()).isEqualTo("RESOLVED");
        assertThat(feedback(job.jobId(),"UP","")).isEqualTo(id);assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isOne();
        try(var pool=Executors.newVirtualThreadPerTaskExecutor()){var start=new CountDownLatch(1);var futures=new ArrayList<Future<HttpResponse<String>>>();for(int i=0;i<6;i++)futures.add(pool.submit(()->{start.await();return http("PUT","/v1/answers/"+job.jobId()+"/feedback","actor",Map.of("rating","DOWN","comment","수정"));}));start.countDown();for(var future:futures)assertThat(future.get().statusCode()).isEqualTo(200);}
        assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isEqualTo(2);assertThat(jdbc().sql("SELECT state FROM kn.feedback WHERE id=:id").param("id",id).query(String.class).single()).isEqualTo("OPEN");assertThat(jdbc().sql("SELECT count(*) FROM kn.feedback").query(Integer.class).single()).isOne();
        var another=answer(List.of());UUID commented=feedback(another.jobId(),"UP","지적");assertThat(jdbc().sql("SELECT state FROM kn.feedback WHERE id=:id").param("id",commented).query(String.class).single()).isEqualTo("OPEN");
    }
    @Test void reviewPermissionAndEvidenceAllPermissionsAreAppliedBeforePagination()throws Exception {
        corpus();var ownerPermissions=new HashSet<>(PERMS);ownerPermissions.add("ADMIN_DOC");perms=Set.copyOf(ownerPermissions);
        var hidden1=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ","ADMIN_DOC"))));var hidden2=answer(List.of(evidence("LOG",List.of("AI_LOG_READ"))));var hidden3=answer(List.of(evidence("TRACE",List.of("AI_TRACE_READ"))));
        var visible1=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));var visible2=answer(List.of());
        var ids=List.of(feedback(hidden1.jobId(),"DOWN","관리 문서"),feedback(hidden2.jobId(),"DOWN","로그"),feedback(hidden3.jobId(),"DOWN","추적"),feedback(visible1.jobId(),"DOWN","문서"),feedback(visible2.jobId(),"DOWN","빈 근거"));
        for(int i=0;i<ids.size();i++)jdbc().sql("UPDATE kn.feedback SET created_at=:time WHERE id=:id").param("id",ids.get(i)).param("time",java.sql.Timestamp.from(NOW.plusSeconds(i))).update();
        error(http("GET","/v1/reviews","reviewer",null),403,"REVIEW_FORBIDDEN");
        perms=Set.of("AI_REVIEW","AI_DOC_READ");var first=json.readTree(http("GET","/v1/reviews?limit=1","reviewer",null).body());assertThat(first.path("items")).hasSize(1);assertThat(first.path("items").get(0).path("feedbackId").asString()).isEqualTo(ids.get(3).toString());String cursor=first.path("nextAfter").asString();assertThat(cursor).isNotBlank();
        var last=json.readTree(http("GET","/v1/reviews?limit=1&after="+cursor,"reviewer",null).body());assertThat(last.path("items")).hasSize(1);assertThat(last.path("items").get(0).path("feedbackId").asString()).isEqualTo(ids.get(4).toString());assertThat(last.path("nextAfter").isNull()).isTrue();
        for(UUID hidden:ids.subList(0,3)){error(http("GET","/v1/reviews/"+hidden,"reviewer",null),404,"NOT_FOUND");error(http("POST","/v1/reviews/"+hidden+"/resolve","reviewer",resolution(1,"REJECT",Map.of("note","기각"))),404,"NOT_FOUND");}
        error(http("GET","/v1/reviews?state=RESOLVED&after="+cursor,"reviewer",null),400,"INVALID_INPUT");
        for(String query:List.of("limit=0","limit=101","limit=bad","state=bad","after=forged"))error(http("GET","/v1/reviews?"+query,"reviewer",null),400,"INVALID_INPUT");
        assertThat(http("GET","/v1/reviews?limit=100","reviewer",null).statusCode()).isEqualTo(200);var empty=json.readTree(http("GET","/v1/reviews?state=REJECTED","reviewer",null).body());assertThat(empty.path("items")).isEmpty();assertThat(empty.path("nextAfter").isNull()).isTrue();error(http("GET","/v1/reviews/"+UUID.randomUUID(),"reviewer",null),404,"NOT_FOUND");
    }
    @Test void actualHttpBodyLimitAndUnicodeBoundariesAndDelegationAndInputEnvelope()throws Exception {
        var job=answer(List.of());String path="/v1/answers/"+job.jobId()+"/feedback";
        assertThat(http("PUT",path,"actor",Map.of("rating","DOWN","comment","😀".repeat(2000))).statusCode()).isEqualTo(200);error(http("PUT",path,"actor",Map.of("rating","DOWN","comment","😀".repeat(2001))),400,"INVALID_INPUT");
        String minimal="{\"rating\":\"DOWN\",\"comment\":\"\"}";String exact=minimal+" ".repeat(65536-minimal.getBytes(StandardCharsets.UTF_8).length);
        assertThat(raw("PUT",path,"actor",exact,false).statusCode()).isEqualTo(200);error(raw("PUT",path,"actor",exact+" ",false),400,"INVALID_INPUT");error(raw("PUT",path,"actor","{",false),400,"INVALID_INPUT");
        error(raw("PUT",path,"actor",minimal,true),401,"AUTHENTICATION_FAILED");
        UUID id=feedback(job.jobId(),"DOWN","지적");perms=reviewer();error(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(3,"REJECT",Map.of("note","😀".repeat(2001)))),400,"INVALID_INPUT");
        assertThat(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(3,"REJECT",Map.of("note","😀".repeat(2000)))).statusCode()).isEqualTo(200);
    }
    @Test void allCausesStoreOnlyTheirOwnMaterialAndSentenceMismatchIs400()throws Exception {
        corpus();var document=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID zero=feedback(document.jobId(),"DOWN","문서 보완");perms=reviewer();
        assertThat(http("POST","/v1/reviews/"+zero+"/resolve","reviewer",resolution(1,"DOCUMENT",Map.of("note","보완","locations",List.of()))).statusCode()).isEqualTo(200);assertThat(jdbc().sql("SELECT count(*) FROM kn.question_case").query(Integer.class).single()).isZero();
        var target=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID targetId=feedback(target.jobId(),"DOWN","문서 보완");assertThat(http("POST","/v1/reviews/"+targetId+"/resolve","reviewer",resolution(1,"DOCUMENT",Map.of("note","보완","locations",List.of(location)))).statusCode()).isEqualTo(200);
        var targetCase=json.readTree(http("GET","/v1/reviews/"+targetId,"reviewer",null).body()).path("questionCase");assertThat(targetCase.path("answerable").asBoolean()).isFalse();assertThat(targetCase.path("expectedFacts")).isEmpty();assertThat(targetCase.path("evidenceLocations")).isEmpty();assertThat(targetCase.path("documentTargets")).hasSize(1);assertThat(targetCase.path("documentTargets").get(0).path("revision").asString()).isEqualTo(location.revision());assertThat(targetCase.path("predecessorId").isNull()).isTrue();
        for(String cause:List.of("ANSWER","RETRIEVAL")) {
            var job=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID id=feedback(job.jobId(),"DOWN",cause);var action=new LinkedHashMap<>(correct(List.of(location,other),"S9F9","설비 S9F9 코드는 200입니다.",1));if(cause.equals("RETRIEVAL"))action.put("terms",Map.of("T3","설비 코드"));
            error(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(1,cause,action)),400,"INVALID_INPUT");action.putAll(correct(List.of(location,other),"S9F9","설비 S9F9 코드는 200입니다.",2));assertThat(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(1,cause,action)).statusCode()).isEqualTo(200);
            var item=json.readTree(http("GET","/v1/reviews/"+id,"reviewer",null).body()).path("questionCase");assertThat(item.path("answerable").asBoolean()).isTrue();assertThat(item.path("expectedFacts").toString()).isEqualTo("[\"S9F9\"]");assertThat(item.path("evidenceLocations")).hasSize(2);assertThat(item.path("documentTargets")).isEmpty();assertThat(item.path("origin").asString()).isEqualTo("FEEDBACK");
        }
        assertThat(jdbc().sql("SELECT count(*) FROM kn.verified_answer WHERE active").query(Integer.class).single()).isEqualTo(2);assertThat(jdbc().sql("SELECT expansion FROM kn.glossary WHERE term='T3'").query(String.class).single()).isEqualTo("설비 코드");
        var live=answer(List.of(evidence("LOG",List.of("AI_LOG_READ"))));UUID liveId=feedback(live.jobId(),"DOWN","조회 문제");assertThat(http("POST","/v1/reviews/"+liveId+"/resolve","reviewer",resolution(1,"ANSWER",Map.of("note","처리기 확인"))).statusCode()).isEqualTo(200);assertThat(jdbc().sql("SELECT count(*) FROM kn.question_case WHERE feedback_id=:id").param("id",liveId).query(Integer.class).single()).isZero();
    }
    @Test void concurrentSameVersionOnlyOneReviewWinsAndReevaluationPreservesHistoryAndDeactivatesMaterial()throws Exception {
        corpus();var job=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID id=feedback(job.jobId(),"DOWN","지적");perms=reviewer();var input=resolution(1,"ANSWER",correct(List.of(location),"100","설비 T3 코드는 100입니다.",1));
        try(var pool=Executors.newVirtualThreadPerTaskExecutor()){var start=new CountDownLatch(1);var tasks=new ArrayList<Future<HttpResponse<String>>>();for(int i=0;i<4;i++)tasks.add(pool.submit(()->{start.await();return http("POST","/v1/reviews/"+id+"/resolve","reviewer",input);}));start.countDown();var codes=new ArrayList<Integer>();for(var task:tasks){var response=task.get();codes.add(response.statusCode());if(response.statusCode()!=200)error(response,409,"REVIEW_CONFLICT");}assertThat(codes.stream().filter(c->c==200).count()).isOne();}
        assertThat(jdbc().sql("SELECT count(*) FROM kn.review WHERE feedback_id=:id").param("id",id).query(Integer.class).single()).isOne();assertThat(jdbc().sql("SELECT count(*) FROM kn.verified_answer WHERE active").query(Integer.class).single()).isOne();
        assertThat(feedback(job.jobId(),"DOWN","지적")).isEqualTo(id);assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isEqualTo(2);
        feedback(job.jobId(),"UP","수정");var detail=json.readTree(http("GET","/v1/reviews/"+id,"reviewer",null).body());assertThat(detail.path("feedback").path("version").asInt()).isEqualTo(3);assertThat(detail.path("feedback").path("state").asString()).isEqualTo("OPEN");assertThat(detail.path("reviews")).hasSize(1);assertThat(detail.path("questionCase").isNull()).isTrue();assertThat(jdbc().sql("SELECT count(*) FROM kn.verified_answer WHERE active").query(Integer.class).single()).isZero();
        assertThat(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(3,"REJECT",Map.of("note","기각"))).statusCode()).isEqualTo(200);assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isEqualTo(4);assertThat(jdbc().sql("SELECT count(*) FROM kn.review WHERE feedback_id=:id").param("id",id).query(Integer.class).single()).isEqualTo(2);
    }
    @Test void committedReviewWithMemoryFailureReturns200AndOnlyAdmissionClosesUntilDbReconstruction()throws Exception {
        corpus();var job=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID id=feedback(job.jobId(),"DOWN","지적");perms=reviewer();
        var queued=admitScripted("projection-already-queued","지원 범위",Map.of(),List.of(classify(wire("UNS",null,null,null,null))),Optional.empty(),null);
        var fault=context.getBean(ProjectionFault.class);fault.fail=true;
        var resolved=http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(1,"ANSWER",correct(List.of(location),"100","설비 T3 코드는 100입니다.",1)));assertThat(resolved.statusCode()).isEqualTo(200);
        assertThat(json.readTree(resolved.body())).isEqualTo(json.valueToTree(Map.of("feedbackId",id.toString(),"version",2,"state","RESOLVED")));
        assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isEqualTo(2);assertThat(jdbc().sql("SELECT count(*) FROM kn.review WHERE feedback_id=:id").param("id",id).query(Integer.class).single()).isOne();
        int before=count("job");var blocked=postQuery("actor","projection-blocked","설비 상태");assertThat(blocked.statusCode()).isEqualTo(503);assertThat(json.readTree(blocked.body()).path("error").asString()).isEqualTo("INFERENCE_UNAVAILABLE");assertThat(count("job")).isEqualTo(before);
        assertReadinessUp();pauses().release(PausePointRegistry.Point.DISPATCH_HOLD,"*");terminal(queued);
        assertThat(jdbc().sql("SELECT count(*) FROM gw.job_event WHERE job_id=:id AND type='job.started'").param("id",queued.jobId()).query(Integer.class).single()).isOne();
        assertThat(scripts().scriptedCalls(queued.jobId(),com.nori.ai.gateway.core.budget.CallType.CLASSIFY)).isOne();assertReadinessUp();
        fault.fail=false;await(()->context.getBean(com.nori.ai.gateway.app.job.recovery.ReadinessGate.class).admissionReady());
        assertThat(postQuery("actor","projection-restored","설비 상태").statusCode()).isEqualTo(202);
        assertThat(context.getBean(DocumentSearch.class).search(scope("actor"),"설비 T3 코드",List.of(),SearchSettings.defaults()).verified()).hasSize(1);
    }
    private void assertReadinessUp()throws Exception {var health=http("GET","/actuator/health/readiness","reviewer",null);assertThat(health.statusCode()).isEqualTo(200);assertThat(json.readTree(health.body()).path("status").asString()).isEqualTo("UP");}
    @Test void successfulAndOverlappingProjectionsKeepAdmissionOpenAndRetainBothUsersCommits()throws Exception {
        corpus();var first=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));var second=answer("other","설비 S9F9 코드",List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID a=feedback(first.jobId(),"DOWN","첫 지적");
        var secondFeedback=http("PUT","/v1/answers/"+second.jobId()+"/feedback","other",Map.of("rating","DOWN","comment","다른 지적"));assertThat(secondFeedback.statusCode()).isEqualTo(200);UUID b=UUID.fromString(json.readTree(secondFeedback.body()).path("feedbackId").asString());perms=reviewer();
        var actionA=new LinkedHashMap<>(correct(List.of(location),"100","설비 T3 코드는 100입니다.",1));actionA.put("terms",Map.of("T3","설비 코드"));var actionB=new LinkedHashMap<>(correct(List.of(other),"200","설비 S9F9 코드는 200입니다.",1));actionB.put("terms",Map.of("S9F9","별도 코드"));
        var fault=context.getBean(ProjectionFault.class);var pause=fault.holdPreparation();
        try(var executor=Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                var requestA=executor.submit(()->http("POST","/v1/reviews/"+a+"/resolve","reviewerA",resolution(1,"RETRIEVAL",actionA)));
                assertThat(pause.entered().await(10,TimeUnit.SECONDS)).isTrue();assertThat(postQuery("actor","projection-success-open","설비 상태").statusCode()).isEqualTo(202);assertReadinessUp();
                var requestB=executor.submit(()->http("POST","/v1/reviews/"+b+"/resolve","reviewerB",resolution(1,"RETRIEVAL",actionB)));
                await(()->jdbc().sql("SELECT count(*) FROM kn.review").query(Integer.class).single()==2);pause.release().countDown();
                for(var response:List.of(requestA.get(),requestB.get())){assertThat(response.statusCode()).isEqualTo(200);assertThat(json.readTree(response.body()).path("version").asInt()).isEqualTo(2);assertThat(json.readTree(response.body()).path("state").asString()).isEqualTo("RESOLVED");}
            }finally{pause.release().countDown();}
        }
        assertThat(fault.installed.answers()).extracting(DocumentCorpus.Answer::questionMasked).containsExactlyInAnyOrder("설비 T3 코드","설비 S9F9 코드");
        assertThat(context.getBean(ReviewedGlossary.class).current(scope("actor"),Map.of())).containsEntry("T3","설비 코드").containsEntry("S9F9","별도 코드");assertReadinessUp();
        var replay=fault.holdPreparation();
        try(var executor=Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                var same=executor.submit(()->http("PUT","/v1/answers/"+first.jobId()+"/feedback","actor",Map.of("rating","DOWN","comment","첫 지적")));
                assertThat(replay.entered().await(10,TimeUnit.SECONDS)).isTrue();assertThat(postQuery("actor","projection-replay-open","설비 상태").statusCode()).isEqualTo(202);replay.release().countDown();
                var response=same.get();assertThat(response.statusCode()).isEqualTo(200);assertThat(json.readTree(response.body()).path("version").asInt()).isEqualTo(2);
            }finally{replay.release().countDown();}
        }
    }
    @Test void actualEmbeddingHttpFailureDoesNotConfirmAnyReviewMaterial()throws Exception {
        corpus();var job=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID id=feedback(job.jobId(),"DOWN","지적");perms=reviewer();
        stub.respond(request->request.path().equals("/v1/embeddings")?new com.nori.ai.gateway.app.inference.OllamaEndpointStub.Json(503,"{\"error\":{\"message\":\"고정 실패\"}}"):com.nori.ai.gateway.app.inference.OllamaEndpointStub.healthy(request));
        error(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(1,"ANSWER",correct(List.of(location),"100","설비 T3 코드는 100입니다.",1))),503,"KNOWLEDGE_UNAVAILABLE");
        assertThat(stub.count("/v1/embeddings")).isOne();assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isOne();
        for(String table:List.of("review","verified_answer","question_case"))assertThat(jdbc().sql("SELECT count(*) FROM kn."+table).query(Integer.class).single()).isZero();
    }
    @Test void unpreparedIndexCannotLabelNewEmbeddingWithThePreviousBundleConfiguration()throws Exception {
        corpus();var job=answer(List.of(evidence("DOCUMENT",List.of("AI_DOC_READ"))));UUID id=feedback(job.jobId(),"DOWN","지적");perms=reviewer();context.getBean(com.nori.ai.gateway.app.job.recovery.ReadinessGate.class).down();
        error(http("POST","/v1/reviews/"+id+"/resolve","reviewer",resolution(1,"ANSWER",correct(List.of(location),"100","설비 T3 코드는 100입니다.",1))),503,"KNOWLEDGE_UNAVAILABLE");
        assertThat(stub.count("/v1/embeddings")).isZero();assertThat(jdbc().sql("SELECT version FROM kn.feedback WHERE id=:id").param("id",id).query(Integer.class).single()).isOne();assertThat(jdbc().sql("SELECT count(*) FROM kn.review").query(Integer.class).single()).isZero();
    }
}
