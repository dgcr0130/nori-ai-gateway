package com.nori.ai.gateway.app.job.recovery;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.app.config.CapacityProfile;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.job.TerminalSpec;
import com.nori.ai.gateway.persistence.queue.*;
import com.nori.ai.gateway.persistence.queue.recovery.CacheInvalidationRepository;
import java.time.Clock;

public final class StartupRecoveryRunner {
    private final QueueJobRepository jobs;private final JobTerminalCommitter terminal;private final JobEventPublisher events;
    private final CacheInvalidationRepository caches;private final Clock clock;private final GatewaySystemsProperties systems;private final CapacityProfile capacity;
    public StartupRecoveryRunner(QueueJobRepository jobs,JobTerminalCommitter terminal,JobEventPublisher events,CacheInvalidationRepository caches,Clock clock,GatewaySystemsProperties systems,CapacityProfile capacity){
        this.jobs=jobs;this.terminal=terminal;this.events=events;this.caches=caches;this.clock=clock;this.systems=systems;this.capacity=capacity;
    }
    public void run(){
        long logStarted=System.nanoTime();int count=0;
        for(var job:jobs.nonterminal()){count++;event(System.getLogger(getClass().getName()),WARNING,"STARTUP_JOB_RECOVERY","jobId",job.jobId(),"status",job.status());
            if(job.status().equals("RUNNING"))terminal.commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.WORKER_INTERRUPTED));
            else if(!systems.systems().containsKey(job.ownerSystem().name()))terminal.commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.START_GATE_DENIED));
            else if(clock.instant().isAfter(job.createdAt().plus(capacity.queueWaitLimit())))terminal.commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.QUEUE_TIMEOUT));
        }
        events.republishTerminal();caches.deleteAllCacheEntries();event(System.getLogger(getClass().getName()),INFO,"STARTUP_RECOVERY_COMPLETED","jobs",count,"durationMs",(System.nanoTime()-logStarted)/1_000_000);
    }
}
