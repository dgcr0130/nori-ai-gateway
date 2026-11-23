package com.nori.ai.gateway.app.job;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.persistence.queue.JobTerminalCommitter;
import com.nori.ai.gateway.persistence.repository.JobRepository;

public final class JobCancellationService {
    private final JobRepository jobs;private final JobTerminalCommitter terminal;private final SlotLedger slots;
    public JobCancellationService(JobRepository jobs,JobTerminalCommitter terminal,SlotLedger slots){this.jobs=jobs;this.terminal=terminal;this.slots=slots;}
    public boolean cancel(VerifiedSystemScope scope,String jobId){
        event(System.getLogger(getClass().getName()),WARNING,"JOB_CANCELLATION_REQUESTED","jobId",jobId,"system",scope.boundSystem(),"actor",scope.actorRef());
        var job=jobs.find(scope,jobId);if(job.isEmpty())return false;
        if(job.get().status().equals("QUEUED")){
            if(terminal.commit(jobId,TerminalSpec.forCause(TerminalSpec.Cause.CANCELLED)))return true;
        }
        if(!jobs.requestCancel(scope,jobId,"CANCELLED"))return false;
        slots.signalIfActive(jobId,CancellationToken.Reason.CANCELLED);return true;
    }
}
