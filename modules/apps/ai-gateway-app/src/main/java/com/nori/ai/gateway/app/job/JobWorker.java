package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.InferenceModeProperties;
import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.app.security.ScopeAssembler;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.persistence.queue.*;
import com.nori.ai.gateway.persistence.repository.JobRepository;
import java.time.*;
import java.util.*;

public final class JobWorker {
    private final JobDelegationReader delegations;
    private final ScopeAssembler scopes;
    private final GatewaySystemsProperties systems;
    private final WorkflowExecutor workflow;
    private final JobTerminalCommitter terminal;
    private final JobRepository jobs;
    private final InferenceModeProperties inference;
    private final Optional<StructuredClassificationHook> classification;
    private final Optional<PausePointRegistry> pauses;
    private final Optional<FaultInjector> faults;
    private final SlotLedger slots;
    private final com.nori.ai.gateway.core.port.JobRequestInputReader inputs;
    private final Optional<com.nori.ai.gateway.core.llm.ClassificationService> classifier;
    public JobWorker(JobDelegationReader delegations,ScopeAssembler scopes,
            GatewaySystemsProperties systems,WorkflowExecutor workflow,JobTerminalCommitter terminal,JobRepository jobs,
            InferenceModeProperties inference,Optional<StructuredClassificationHook> classification,Optional<PausePointRegistry> pauses,Optional<FaultInjector> faults,SlotLedger slots,com.nori.ai.gateway.core.port.JobRequestInputReader inputs,
            Optional<com.nori.ai.gateway.core.llm.ClassificationService> classifier){
        this.delegations=delegations;this.scopes=scopes;this.systems=systems;this.workflow=workflow;
        this.terminal=terminal;this.jobs=jobs;this.inference=inference;this.classification=classification;this.pauses=pauses;this.faults=faults;this.slots=slots;this.inputs=inputs;this.classifier=classifier;
    }
    public void run(JobScopeRecord job,CancellationToken token){
        try(var logContext=com.nori.ai.gateway.app.observability.LogContext.open(org.slf4j.MDC.getCopyOfContextMap())) {
        org.slf4j.MDC.put("jobId",job.jobId());org.slf4j.MDC.put("conversationId",job.conversationId());
        long logStarted=System.nanoTime();
        token.attachWorker(Thread.currentThread());TerminalSpec result;
        try{
            String delegation=delegations.forWorker(job.jobId()).orElse(null);

            var scope=scopes.forWorker(job);org.slf4j.MDC.put("ownerSystem",scope.boundSystem().name());
            com.nori.ai.gateway.core.logging.DiagnosticLog.event(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"JOB_STARTED","jobId",job.jobId(),"mode",inference.mode(),"system",scope.boundSystem());
            var persisted=jobs.find(scope,job.jobId());
            if(persisted.isEmpty())return;
            if(persisted.get().cancelRequestedAt()!=null)token.signal(CancellationToken.Reason.CANCELLED);
            if(token.reason().isPresent())result=cancelled(token);
            else{
                if(!systems.systems().containsKey(scope.boundSystem().name())||delegation==null)result=TerminalSpec.forCause(TerminalSpec.Cause.START_GATE_DENIED);
                else{
                    if(token.reason().isPresent()){result=cancelled(token);}
                    else{
                    faults.ifPresent(f->f.haltAt(job.jobId(),PausePointRegistry.Point.BEFORE_CLASSIFICATION));
                    if(pauses.isPresent())pauses.get().hit(PausePointRegistry.Point.BEFORE_CLASSIFICATION,job.jobId());

                    var latest=jobs.find(scope,job.jobId());
                    if(latest.isPresent()&&latest.get().cancelRequestedAt()!=null)token.signal(CancellationToken.Reason.CANCELLED);
                    if(token.reason().isPresent())throw new InterruptedException();
                    var input=inputs.findRequestInput(scope,job.jobId()).orElseThrow(()->{

                        return new IllegalStateException("작업 입력 연결을 확인할 수 없습니다");
                    });

                    if(token.reason().isPresent())throw new InterruptedException();
                    if(inference.mode()==InferenceMode.ENABLED){
                        // 분류는 JobContext 에 결속할 바로 그 접수 입력(maskedQuestion·요청 Context)만 받는다.
                        var classifiedOutcome=classifier.orElseThrow(()->new IllegalStateException("분류 구현이 없습니다")).classify(new com.nori.ai.gateway.core.llm.ClassificationService.Request(scope,job.conversationId(),job.jobId(),token,input));
                        com.nori.ai.gateway.core.logging.DiagnosticLog.body(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"JOB_CLASSIFIED",com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(classifiedOutcome),"jobId",job.jobId());
                        result=switch(classifiedOutcome){
                            case com.nori.ai.gateway.core.llm.ClassificationService.Classified classified->workflow.execute(new JobContext(job.jobId(),job.conversationId(),scope,delegation,token,inference.mode(),classified.classification().jobClassification(),input));
                            case com.nori.ai.gateway.core.llm.ClassificationService.Rejected rejected->TerminalSpec.outcome(JobStatus.FAILED,ResultStatus.FAILED,rejected.code(),Map.of("clarification",new Clarification(rejected.code(),rejected.fields())));
                            case com.nori.ai.gateway.core.llm.ClassificationService.Unavailable unavailable->TerminalSpec.outcome(JobStatus.FAILED,ResultStatus.FAILED,ErrorCode.LLM_UNAVAILABLE,Map.of());
                            case com.nori.ai.gateway.core.llm.ClassificationService.BudgetExceeded exceeded->TerminalSpec.outcome(JobStatus.FAILED,ResultStatus.FAILED,ErrorCode.INPUT_BUDGET_EXCEEDED,Map.of());
                        };
                    }else{
                        Map<String,Object> classified=classification.orElseThrow(()->new IllegalStateException("분류 구현이 없습니다")).classify(job.jobId(),input);
                        com.nori.ai.gateway.core.logging.DiagnosticLog.body(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"JOB_CLASSIFIED",com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(classified),"jobId",job.jobId());
                        result=workflow.execute(new JobContext(job.jobId(),job.conversationId(),scope,delegation,token,inference.mode(),classified,input));
                    }
                    }
                }
            }
        }catch(InterruptedException interrupted){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(JobWorker.class.getName()),"JOB_WORKER_FAILED",interrupted);
            if(token.reason().isEmpty())token.signal(CancellationToken.Reason.CANCELLED);
            result=cancelled(token);
        }catch(Exception failure){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(JobWorker.class.getName()),System.Logger.Level.ERROR,"JOB_WORKER_FAILED",failure);
            result=token.reason().isPresent()?cancelled(token):TerminalSpec.outcome(JobStatus.FAILED,ResultStatus.FAILED,ErrorCode.CONTRACT_INVALID,Map.of());
        }
        // interrupt 응답과 전송 계층 종료는 다르므로 실제 호출 종료를 끝까지 확인합니다.
        boolean interrupted=false;
        for(;;){try{Thread.interrupted();token.awaitTermination(Duration.ofSeconds(10));break;}catch(InterruptedException retry){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(JobWorker.class.getName()),"JOB_WORKER_FAILED",retry);interrupted=true;}}
        slots.markTerminated(job.jobId());
        // 종료 표지 뒤에는 외부 신호가 차단되므로 직전 신호의 interrupt를 종결 tx 전에 소비합니다.
        interrupted|=Thread.interrupted();
        if(token.reason().isPresent())result=cancelled(token);
        terminal.commit(job.jobId(),result);
        com.nori.ai.gateway.core.logging.DiagnosticLog.event(System.getLogger(getClass().getName()),token.reason().isPresent()?System.Logger.Level.WARNING:System.Logger.Level.INFO,"JOB_TERMINATED","jobId",job.jobId(),"jobStatus",result.jobStatus(),"resultStatus",result.resultStatus(),"errorCode",result.errorCode(),"durationMs",(System.nanoTime()-logStarted)/1_000_000);
        if(interrupted)Thread.currentThread().interrupt();
        }
    }
    private TerminalSpec cancelled(CancellationToken token){return TerminalSpec.forCause(switch(token.reason().orElse(CancellationToken.Reason.CANCELLED)){
        case TIMEOUT->TerminalSpec.Cause.JOB_TIMEOUT;case CANCELLED->TerminalSpec.Cause.CANCELLED;});}
}
