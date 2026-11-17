package com.nori.ai.gateway.app.job.recovery;

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
        for(var job:jobs.nonterminal()){
            if(job.status().equals("RUNNING"))terminal.commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.WORKER_INTERRUPTED));
            else if(!systems.systems().containsKey(job.ownerSystem().name()))terminal.commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.START_GATE_DENIED));
            else if(clock.instant().isAfter(job.createdAt().plus(capacity.queueWaitLimit())))terminal.commit(job.jobId(),TerminalSpec.forCause(TerminalSpec.Cause.QUEUE_TIMEOUT));
        }
        events.republishTerminal();caches.deleteAllCacheEntries();
    }
}
