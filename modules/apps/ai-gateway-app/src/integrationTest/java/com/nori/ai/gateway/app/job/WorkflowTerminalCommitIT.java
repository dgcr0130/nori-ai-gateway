package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.app.security.ScopeAssembler;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.JobRequestInputReader;
import com.nori.ai.gateway.core.registry.TaskType;
import com.nori.ai.gateway.persistence.queue.*;
import com.nori.ai.gateway.persistence.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 실제 Tool 생산 자료의 마지막 쓰기만 독립적으로 경합·실패시킵니다. */
class WorkflowTerminalCommitIT extends P09QueueSupport {
    static final class CallFault { final java.util.concurrent.atomic.AtomicReference<Throwable> next=new java.util.concurrent.atomic.AtomicReference<>(); }
    static final class ClockTrip { volatile Thread thread; int calls; void arm(){calls=0;thread=Thread.currentThread();} }
    @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods=false) static class ClockConfiguration {
        @org.springframework.context.annotation.Bean ClockTrip terminalClockTrip(){return new ClockTrip();}
        @org.springframework.context.annotation.Bean CallFault actualBusinessCallFault(){return new CallFault();}
        @org.springframework.context.annotation.Bean static org.springframework.beans.factory.config.BeanPostProcessor failAfterActualBusinessCall(org.springframework.beans.factory.ObjectProvider<CallFault> faults){return new org.springframework.beans.factory.config.BeanPostProcessor(){
            public Object postProcessAfterInitialization(Object bean,String name){if(!(bean instanceof com.nori.ai.gateway.integration.system.HttpSystemQueryAdapter original))return bean;var actual=spy(original);doAnswer(invocation->{var value=invocation.callRealMethod();var failure=faults.getObject().next.getAndSet(null);if(failure!=null)throw failure;return value;}).when(actual).query(org.mockito.ArgumentMatchers.any());return actual;}
        };}
        @org.springframework.context.annotation.Bean static org.springframework.beans.factory.config.BeanPostProcessor observeSharedClock(org.springframework.beans.factory.ObjectProvider<ClockTrip> trips){return new org.springframework.beans.factory.config.BeanPostProcessor(){
            public Object postProcessAfterInitialization(Object bean,String name){if(!(bean instanceof ControllableClock original))return bean;var actual=spy(original);doAnswer(invocation->{var trip=trips.getObject();if(trip.thread==Thread.currentThread()&&++trip.calls==2){trip.thread=null;actual.advance(1);}return invocation.callRealMethod();}).when(actual).instant();return actual;}
        };}
    }
    @Override protected Class<?>[] extraSources(){return new Class<?>[]{HarnessObservationsEndpoint.class,ClockConfiguration.class};}
    private record Produced(JobAdmissionService.Accepted accepted,TerminalSpec spec) {}
    private record Prepared(JobAdmissionService.Accepted accepted,JobContext job) {}
    private Prepared prepare(String key) {
        pauses().hold(PausePointRegistry.Point.DISPATCH_HOLD,"*");pauses().hold(PausePointRegistry.Point.SWEEPER_HOLD,"*");
        hooks().arm("actor",key,new TestHookRegistry.Scenario(TaskType.LIVE_QUERY,Set.of(),null,false,java.util.Optional.empty(),java.util.List.of(),java.util.Optional.empty(),com.nori.ai.gateway.app.job.StandardSystemConfiguration.calls(TaskType.LIVE_QUERY)));
        String delegation=registeredDlg("actor");
        var accepted=inRequestSpan(()->context.getBean(JobAdmissionService.class).accept(scope("actor"),new JobAdmissionService.AdmissionRequest("/v1/query",null,"실제 저장 자료",workflowFields(),key,delegation)));
        var queue=context.getBean(QueueJobRepository.class);context.getBean(TransactionTemplate.class).executeWithoutResult(tx->assertThat(queue.start(accepted.jobId(),clock().instant())).isTrue());
        var row=queue.nonterminal().stream().filter(value->value.jobId().equals(accepted.jobId())).findFirst().orElseThrow();var workerScope=context.getBean(ScopeAssembler.class).forWorker(row);
        var request=context.getBean(JobRequestInputReader.class).findRequestInput(workerScope,accepted.jobId()).orElseThrow();
        var job=new JobContext(accepted.jobId(),accepted.conversationId(),workerScope,delegation,new CancellationToken(),InferenceMode.DISABLED,Map.of("taskType",TaskType.LIVE_QUERY.name(),"toolCalls",StandardSystemConfiguration.calls(TaskType.LIVE_QUERY),"targets",request.selectedResourceRefs()),request);
        return new Prepared(accepted,job);
    }
    private Produced produce(String key) throws Exception {
        var prepared=prepare(key);var spec=context.getBean(com.nori.ai.gateway.core.job.WorkflowExecutor.class).execute(prepared.job());
        assertThat(spec.commitData()).isPresent();assertThat(spec.commitData().orElseThrow().evidence()).isNotEmpty();return new Produced(prepared.accepted(),spec);
    }
    private Produced withSnapshot(Produced produced) {
        var original=produced.spec();var payload=new LinkedHashMap<String,Object>(original.resultPayload());
        var answer=Map.of("kind","NO_EVIDENCE","text","근거 없음","citations",List.of(),"sentences",List.of());payload.put("answer",answer);payload.put("status","EMPTY");
        var data=original.commitData().orElseThrow();var snapshot=com.nori.ai.gateway.core.result.AnswerSnapshot.create("실제 저장 자료",com.nori.ai.gateway.core.registry.SchemaContracts.JSON.valueToTree(answer),List.of(),"a".repeat(64));
        var assistant=data.assistantMessage().map(message->new TerminalSpec.AssistantMessage(message.messageId(),"근거 없음",message.resourceRefs(),message.toolExecutionRefs(),message.responseRef()));
        var saved=new TerminalSpec.CommitData(data.scope(),data.jobId(),data.conversationId(),data.requestMessageId(),data.jobSeq(),data.evidence(),assistant,data.context(),data.summary(),data.sourceDeps(),Optional.of(snapshot));
        return new Produced(produced.accepted(),new TerminalSpec(original.jobStatus(),com.nori.ai.gateway.core.result.ResultStatus.EMPTY,original.errorCode(),original.eventType(),false,payload,original.resultSourceDeps(),original.terminalCause()).withCommitData(saved));
    }
    @Test void snapshotInsertFailureRollsBackEverythingAndSameHashRetryReusesWhileDifferentHashFails() throws Exception {
        var value=withSnapshot(produce("snapshot-failure"));String job=value.accepted().jobId();
        var events=new ArrayList<JobEventPublisher.Published>();context.addApplicationListener(event->{if(event instanceof org.springframework.context.PayloadApplicationEvent<?> e&&e.getPayload() instanceof JobEventPublisher.Published published)events.add(published);});
        jdbc().sql("ALTER TABLE kn.answer_snapshot ADD CONSTRAINT reject_phase7_fixture CHECK (job_id <> '"+job+"')").update();
        var committer=context.getBean(JobTerminalCommitter.class);
        assertThatThrownBy(()->committer.commit(job,value.spec())).isInstanceOf(org.springframework.dao.DataAccessException.class);noFinalData(value);assertThat(events).isEmpty();assertThat(state(job)).isEqualTo("RUNNING");
        jdbc().sql("ALTER TABLE kn.answer_snapshot DROP CONSTRAINT reject_phase7_fixture").update();
        assertThat(committer.commit(job,value.spec())).isTrue();String id=jdbc().sql("SELECT id::text FROM kn.answer_snapshot WHERE job_id=:job").param("job",job).query(String.class).single();
        assertThat(committer.commit(job,value.spec())).isFalse();assertThat(jdbc().sql("SELECT id::text FROM kn.answer_snapshot WHERE job_id=:job").param("job",job).query(String.class).single()).isEqualTo(id);assertThat(events).hasSize(1);
        var data=value.spec().commitData().orElseThrow();var old=data.snapshot().orElseThrow();var changed=new com.nori.ai.gateway.core.result.AnswerSnapshot("다른 질문",old.answer(),old.evidence(),old.configSha256());
        var mismatch=new TerminalSpec.CommitData(data.scope(),data.jobId(),data.conversationId(),data.requestMessageId(),data.jobSeq(),data.evidence(),data.assistantMessage(),data.context(),data.summary(),data.sourceDeps(),Optional.of(changed));
        assertThatThrownBy(()->committer.commit(job,value.spec().withCommitData(mismatch))).isInstanceOf(IllegalStateException.class).hasMessage("스냅샷 본문 해시가 다릅니다");assertThat(events).hasSize(1);
        assertThat(context.getBean(ConversationRepository.class).delete(data.scope(),data.conversationId())).isEqualTo(ConversationRepository.DeleteResult.DELETED);
        assertThat(jdbc().sql("SELECT id::text FROM kn.answer_snapshot WHERE job_id=:job").param("job",job).query(String.class).single()).isEqualTo(id);
    }
    private JobTerminalCommitter committer(List<TerminalCommitObserver> observers) {
        return new JobTerminalCommitter(jdbc(),context.getBean(PlatformTransactionManager.class),clock(),context.getBean(JobEventPublisher.class),observers,Duration.ofSeconds(180),context.getBean(SlotLedger.class)::terminated,
            context.getBean(ConversationRepository.class),context.getBean(MessageRepository.class),context.getBean(EvidenceRepository.class),context.getBean(ToolExecutionRepository.class));
    }
    private void noFinalData(Produced value) {
        String job=value.accepted().jobId(),conversation=value.accepted().conversationId();
        assertThat(jdbc().sql("SELECT count(*) FROM kn.answer_snapshot WHERE job_id=:job").param("job",job).query(Integer.class).single()).isZero();
        for(String table:List.of("result","evidence"))assertThat(jdbc().sql("SELECT count(*) FROM gw."+table+" WHERE job_id=:job").param("job",job).query(Integer.class).single()).as(table).isZero();
        // 실행 중 감사(접수·분류·Tool)는 남고 종결 감사는 없습니다.
        assertThat(assertJobAuditLineage(job,false)).doesNotContain("JOB_TERMINAL");
        assertThat(jdbc().sql("SELECT count(*) FROM gw.message WHERE conversation_id=:conversation AND role='assistant'").param("conversation",conversation).query(Integer.class).single()).isZero();
        var scope=value.spec().commitData().orElseThrow().scope();var conversations=context.getBean(ConversationRepository.class);assertThat(conversations.findContext(scope,conversation)).isEmpty();assertThat(conversations.findSummary(scope,conversation)).isEmpty();
        assertThat(jdbc().sql("SELECT count(*) FROM gw.job_event WHERE job_id=:job AND type IN ('job.completed','job.partial','job.failed','job.cancelled')").param("job",job).query(Integer.class).single()).isZero();
    }
    private TerminalSpec changed(Produced value,TerminalSpec.CommitData data){return value.spec().withCommitData(data);}
    private TerminalSpec.CommitData copy(TerminalSpec.CommitData data,String message,long seq,List<TerminalSpec.StoredEvidence> evidence,Optional<TerminalSpec.AssistantMessage> assistant){return new TerminalSpec.CommitData(data.scope(),data.jobId(),data.conversationId(),message,seq,evidence,assistant,data.context(),data.summary(),data.sourceDeps());}
    @Test void actualFinalBatchRollsBackOnTheExistingFaultInjectorAndAWinningRetryStoresItOnce() throws Exception {
        var value=withSnapshot(produce("final-rollback"));var fault=spy(context.getBean(FaultInjector.class));var original=new IllegalStateException("시험 종결 실패");
        doAnswer(invocation->{invocation.callRealMethod();throw original;}).when(fault).beforeCommit(value.accepted().jobId());
        var events=new ArrayList<JobEventPublisher.Published>();context.addApplicationListener(event->{if(event instanceof org.springframework.context.PayloadApplicationEvent<?> payload&&payload.getPayload() instanceof JobEventPublisher.Published published)events.add(published);});
        assertThatThrownBy(()->committer(List.of(fault)).commit(value.accepted().jobId(),value.spec())).isSameAs(original);noFinalData(value);assertThat(events).isEmpty();assertThat(state(value.accepted().jobId())).isEqualTo("RUNNING");
        assertThat(jdbc().sql("SELECT count(*) FROM gw.tool_execution WHERE job_id=:job").param("job",value.accepted().jobId()).query(Integer.class).single()).isOne();
        var actual=context.getBean(JobTerminalCommitter.class);assertThat(actual.commit(value.accepted().jobId(),value.spec())).isTrue();assertThat(actual.commit(value.accepted().jobId(),value.spec())).isFalse();
        assertThat(events).singleElement().satisfies(event->assertThat(event.type()).isEqualTo("job.completed"));assertThat(count("result")).isOne();assertThat(assertJobAuditLineage(value.accepted().jobId(),true)).last().isEqualTo("JOB_TERMINAL");assertThat(jdbc().sql("SELECT count(*) FROM gw.audit_event WHERE job_id IS DISTINCT FROM :job").param("job",value.accepted().jobId()).query(Integer.class).single()).isZero();assertThat(jdbc().sql("SELECT count(*) FROM gw.evidence WHERE job_id=:job").param("job",value.accepted().jobId()).query(Integer.class).single()).isEqualTo(value.spec().commitData().orElseThrow().evidence().size());
    }
    @Test void wrongMessageSequenceDuplicateEvidenceAndAnotherJobsActualToolReferenceAllRollBack() throws Exception {
        var value=produce("bindings");var other=produce("another-job");var data=value.spec().commitData().orElseThrow();var otherData=other.spec().commitData().orElseThrow();var assistant=data.assistantMessage().orElseThrow();
        var invalid=List.of(copy(data,otherData.requestMessageId(),data.jobSeq(),data.evidence(),data.assistantMessage()),copy(data,data.requestMessageId(),data.jobSeq()+1,data.evidence(),data.assistantMessage()),copy(data,data.requestMessageId(),data.jobSeq(),List.of(data.evidence().getFirst(),data.evidence().getFirst()),data.assistantMessage()),
            copy(data,data.requestMessageId(),data.jobSeq(),data.evidence(),Optional.of(new TerminalSpec.AssistantMessage(assistant.messageId(),assistant.contentMasked(),assistant.resourceRefs(),otherData.assistantMessage().orElseThrow().toolExecutionRefs(),assistant.responseRef()))));
        for(var forged:invalid){assertThatThrownBy(()->context.getBean(JobTerminalCommitter.class).commit(value.accepted().jobId(),changed(value,forged))).isInstanceOf(IllegalArgumentException.class);noFinalData(value);}
        var scope=data.scope();for(var wrongScope:List.of(new com.nori.ai.gateway.core.scope.VerifiedSystemScope(com.nori.ai.gateway.core.scope.OwnerSystem.WM,scope.authenticatedServiceRef(),scope.actorRef(),scope.sub(),scope.perm(),scope.pdg(),scope.issuedAt(),scope.expiresAt(),scope.requestId()),new com.nori.ai.gateway.core.scope.VerifiedSystemScope(scope.boundSystem(),scope.authenticatedServiceRef(),new com.nori.ai.gateway.core.scope.ActorRef("other"),"other",scope.perm(),scope.pdg(),scope.issuedAt(),scope.expiresAt(),scope.requestId()),otherData.scope())) {
            var forged=new TerminalSpec.CommitData(wrongScope,data.jobId(),data.conversationId(),data.requestMessageId(),data.jobSeq(),data.evidence(),data.assistantMessage(),data.context(),data.summary(),data.sourceDeps());assertThatThrownBy(()->context.getBean(JobTerminalCommitter.class).commit(value.accepted().jobId(),changed(value,forged))).isInstanceOf(IllegalArgumentException.class);noFinalData(value);
        }
        assertThatThrownBy(()->new JobTerminalCommitter(jdbc(),context.getBean(PlatformTransactionManager.class),clock(),context.getBean(JobEventPublisher.class),List.of(),Duration.ofSeconds(180),context.getBean(SlotLedger.class)::terminated).commit(value.accepted().jobId(),value.spec())).isInstanceOf(IllegalStateException.class);noFinalData(value);
        assertThat(context.getBean(JobTerminalCommitter.class).commit(other.accepted().jobId(),other.spec())).isTrue();var collision=copy(data,data.requestMessageId(),data.jobSeq(),List.of(otherData.evidence().getFirst()),data.assistantMessage());
        assertThatThrownBy(()->context.getBean(JobTerminalCommitter.class).commit(value.accepted().jobId(),changed(value,collision))).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);noFinalData(value);assertThat(state(value.accepted().jobId())).isEqualTo("RUNNING");assertThat(state(other.accepted().jobId())).isEqualTo("SUCCEEDED");
    }


    @Test void actualBusinessReadRuntimeAndErrorAreRecordedMinimallyAndTheSameOriginalObjectIsRethrown() throws Exception {
        int count=0;for(Throwable original:List.of(new IllegalStateException("SENTINEL:runtime-call"),new AssertionError("SENTINEL:error-call"))) {
            var prepared=prepare("actual-call-failure-"+count++);context.getBean(CallFault.class).next.set(original);
            assertThatThrownBy(()->context.getBean(com.nori.ai.gateway.core.job.WorkflowExecutor.class).execute(prepared.job())).isSameAs(original);
            String raw=jdbc().sql("SELECT payload::text FROM gw.tool_execution WHERE job_id=:job").param("job",prepared.accepted().jobId()).query(String.class).single();var record=json.readTree(raw);assertThat(record.properties().stream().map(Map.Entry::getKey)).containsExactlyInAnyOrder("id","capabilityId","status");assertThat(record.path("status").asString()).isEqualTo("FAILED");assertThat(record.path("id").asString()).isNotBlank();assertThat(record.path("capabilityId").asString()).isEqualTo("tc.eqp.getOperationalSnapshot");assertThat(raw).doesNotContain("SENTINEL","IllegalStateException","AssertionError");assertThat(state(prepared.accepted().jobId())).isEqualTo("RUNNING");
        }
        assertThat(calls().count("getEqpSnapshot")).isEqualTo(2);assertThat(count("result")).isZero();assertThat(jdbc().sql("SELECT count(*) FROM gw.evidence").query(Integer.class).single()).isZero();
    }
}
