package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.app.hooks.PausePointRegistry;
import com.nori.ai.gateway.core.job.TerminalSpec;
import com.nori.ai.gateway.persistence.queue.JobTerminalCommitter;
import com.nori.ai.gateway.persistence.repository.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;
import static org.assertj.core.api.Assertions.*;

class ConversationApiIT extends ApiWebSupport {
    @Test void listLimitsCursorOwnershipAndActiveDeletionProtectEveryAccessPath() throws Exception {
        pauses().hold(PausePointRegistry.Point.DISPATCH_HOLD,"*");
        assertThat(json.readTree(request("GET","/v1/conversations","actor",null,null,false).body()).path("items").isEmpty()).isTrue();
        var job=accept("actor","active-delete","질문",null,dlg());
        assertThat(request("DELETE","/v1/conversations/"+job.conversationId(),"other",null,null,false).statusCode()).isEqualTo(404);
        var before=List.of(count("conversation"),count("message"),count("job"));var denied=request("DELETE","/v1/conversations/"+job.conversationId(),"actor",null,null,false);
        assertThat(denied.statusCode()).isEqualTo(409);assertThat(denied.body()).contains("CONVERSATION_ACTIVE");assertThat(List.of(count("conversation"),count("message"),count("job"))).isEqualTo(before);
        request("POST","/v1/conversations","actor",null,null,false);
        var page=json.readTree(request("GET","/v1/conversations?limit=1","actor",null,null,false).body());
        String cursor=page.path("nextAfter").asString();assertThat(cursor).isNotBlank();
        assertThat(json.readTree(request("GET","/v1/conversations?after="+cursor+"&limit=1","actor",null,null,false).body()).path("nextAfter").isNull()).isTrue();
        assertThat(request("GET","/v1/conversations?after="+cursor,"other",null,null,false).statusCode()).isEqualTo(400);
        for(String query:List.of("limit=0","limit=101","limit=bad","after=forged"))
            assertThat(request("GET","/v1/conversations?"+query,"actor",null,null,false).statusCode()).isEqualTo(400);
        context.getBean(JobTerminalCommitter.class).commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.CANCELLED));
        clock().advance(Duration.ofDays(31).toSeconds());
        assertThat(request("GET","/v1/conversations/"+job.conversationId(),"actor",null,null,false).statusCode()).isEqualTo(200);
        assertThat(request("DELETE","/v1/conversations/"+job.conversationId(),"actor",null,null,false).statusCode()).isEqualTo(204);
        for(String path:List.of("/v1/conversations/"+job.conversationId(),"/v1/analyses/"+job.jobId(),
                "/v1/analyses/"+job.jobId()+"/events","/v1/analyses/"+job.jobId()+"/evidence/none/source",
                "/v1/analyses/"+job.jobId()+"/blocks/b0/export","/v1/audit/jobs/"+job.jobId()))
            assertThat(request("GET",path,"actor",null,null,false).statusCode()).isEqualTo(404);
        assertThat(request("POST","/v1/conversations/"+job.conversationId()+"/messages","actor",Map.of("query","late"),"late",true).statusCode()).isEqualTo(404);
    }
    @Test void deletionCommittedBeforeAdmissionLockMakesAdmission404WithoutPartialMessageOrJob() throws Exception {
        pauses().hold(PausePointRegistry.Point.DISPATCH_HOLD,"*");
        String id=json.readTree(request("POST","/v1/conversations","actor",null,null,false).body()).path("id").asString();
        var locked=new CountDownLatch(1);var release=new CountDownLatch(1);
        try(var pool=Executors.newVirtualThreadPerTaskExecutor()) {
            var blocker=pool.submit(()->context.getBean(TransactionTemplate.class).executeWithoutResult(tx->{
                context.getBean(com.nori.ai.gateway.persistence.maintenance.QueueMaintenanceRepository.class).lockControl();locked.countDown();
                try{release.await();}catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new IllegalStateException(interrupted);}
            }));
            assertThat(locked.await(10,TimeUnit.SECONDS)).isTrue();
            pauses().hold(PausePointRegistry.Point.ADMISSION_FAST_MISS,"actor:race-delete");
            var admission=pool.submit(()->request("POST","/v1/conversations/"+id+"/messages","actor",Map.of("query","late"),"race-delete",true));
            try {
                assertThat(pauses().awaitReached(PausePointRegistry.Point.ADMISSION_FAST_MISS,"actor:race-delete",Duration.ofSeconds(10))).isTrue();
                assertThat(request("DELETE","/v1/conversations/"+id,"actor",null,null,false).statusCode()).isEqualTo(204);
            } finally { release.countDown();pauses().release(PausePointRegistry.Point.ADMISSION_FAST_MISS,"actor:race-delete"); }
            blocker.get();assertThat(admission.get().statusCode()).isEqualTo(404);
            assertThat(count("job")).isZero();assertThat(count("message")).isZero();
        } finally {release.countDown();}
    }
}
