package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.CapacityProfile;
import com.nori.ai.gateway.app.hooks.PausePointRegistry;
import com.nori.ai.gateway.app.job.recovery.ReadinessGate;
import com.nori.ai.gateway.core.job.InferenceReadiness;
import com.nori.ai.gateway.persistence.maintenance.QueueMaintenanceRepository;
import com.nori.ai.gateway.persistence.queue.*;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.support.TransactionTemplate;

public final class JobDispatcher implements AutoCloseable {
    private final ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().name("job-dispatch-",0).factory());
    private final ExecutorService workers=Executors.newVirtualThreadPerTaskExecutor();
    private final TransactionTemplate transactions;
    private final QueueMaintenanceRepository control;
    private final QueueJobRepository queue;
    private final JobWorker worker;
    private final InferenceReadiness inference;
    private final ReadinessGate gate;
    private final CapacityProfile capacity;
    private final Clock clock;
    private final SlotLedger slots;
    private final Optional<PausePointRegistry> pauses;
    private final JobEventPublisher publisher;
    private volatile boolean started;
    private volatile Throwable failure;
    public JobDispatcher(TransactionTemplate transactions,QueueMaintenanceRepository control,QueueJobRepository queue,JobWorker worker,
            InferenceReadiness inference,ReadinessGate gate,CapacityProfile capacity,Clock clock,SlotLedger slots,Optional<PausePointRegistry> pauses,JobEventPublisher publisher){
        this.transactions=transactions;this.control=control;this.queue=queue;this.worker=worker;this.inference=inference;this.gate=gate;
        this.capacity=capacity;this.clock=clock;this.slots=slots;this.pauses=pauses;this.publisher=publisher;
    }
    @EventListener public void start(ReadinessGate.RecoveryCompleted event){if(!started){started=true;scheduler.scheduleWithFixedDelay(this::dispatchSafely,0,250,TimeUnit.MILLISECONDS);}}
    @EventListener public void wake(JobAdmissionService.AdmissionCommitted event){if(started)scheduler.execute(com.nori.ai.gateway.app.observability.LogContext.capture(()->{publisher.publishQueued(event.jobId());dispatchSafely();}));}
    @EventListener public void finished(JobEventPublisher.TerminalCommitted event){slots.release(event.jobId());if(started)scheduler.execute(com.nori.ai.gateway.app.observability.LogContext.capture(this::dispatchSafely));}
    private void dispatchSafely(){try{dispatch();}catch(RuntimeException failed){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(JobDispatcher.class.getName()),System.Logger.Level.ERROR,"JOB_DISPATCHER_FAILED",failed);failure=failed;gate.down();}}
    private void dispatch(){
        if(!gate.ready()||!inference.ready()||pauses.map(p->p.held(PausePointRegistry.Point.DISPATCH_HOLD)).orElse(false))return;
        var selected=transactions.execute(tx->{
            control.lockControl();
            if(!gate.ready()||queue.count("RUNNING")>=capacity.maxRunningJobs()||!inference.ready())return Optional.<JobScopeRecord>empty();
            var job=queue.nextQueued();
            if(job.isPresent()&&clock.instant().isAfter(job.get().createdAt().plus(capacity.queueWaitLimit())))return Optional.<JobScopeRecord>empty();
            if(job.isPresent()&&!queue.start(job.get().jobId(),clock.instant()))return Optional.<JobScopeRecord>empty();return job;
        });
        if(selected!=null)selected.ifPresent(job->{
            try(var logContext=com.nori.ai.gateway.app.observability.LogContext.open(org.slf4j.MDC.getCopyOfContextMap())) {
            if(!job.jobId().equals(org.slf4j.MDC.get("jobId"))){org.slf4j.MDC.remove("requestId");org.slf4j.MDC.remove("traceId");}
            org.slf4j.MDC.put("jobId",job.jobId());org.slf4j.MDC.put("ownerSystem",job.ownerSystem().name());org.slf4j.MDC.put("conversationId",job.conversationId());
            com.nori.ai.gateway.core.logging.DiagnosticLog.event(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"JOB_DISPATCHED","jobId",job.jobId(),"waitMs",java.time.Duration.between(job.createdAt(),clock.instant()).toMillis(),"running",slots.activeCount()+1);
            publisher.publishQueued(job.jobId());publisher.publishStarted(job.jobId());
            var token=slots.claim(job.jobId());token.deadline(job.createdAt().plus(capacity.jobLifetimeLimit()),clock);workers.execute(com.nori.ai.gateway.app.observability.LogContext.capture(()->worker.run(job,token)));
            }
        });
    }
    public boolean started(){return started;}
    public Optional<Throwable> failure(){return Optional.ofNullable(failure);}
    @Override public void close(){started=false;scheduler.shutdownNow();workers.shutdownNow();}
}
