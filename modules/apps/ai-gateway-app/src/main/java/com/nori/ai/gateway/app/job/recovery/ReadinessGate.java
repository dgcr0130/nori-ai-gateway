package com.nori.ai.gateway.app.job.recovery;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import java.util.*;
import org.springframework.boot.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;

public final class ReadinessGate implements ApplicationRunner,Ordered {
    public record RecoveryCompleted(){}
    private final List<ReadinessPrecondition> preconditions;private final ApplicationEventPublisher publisher;
    private volatile boolean ready;
    private final Set<String> knowledgeFailures=java.util.concurrent.ConcurrentHashMap.newKeySet();
    public ReadinessGate(List<ReadinessPrecondition> preconditions,ApplicationEventPublisher publisher){
        this.preconditions=preconditions.stream().sorted(Comparator.comparingInt(ReadinessPrecondition::order)).toList();this.publisher=publisher;
        var routers=this.preconditions.stream().filter(p->p instanceof RecoveryModeRouter).toList();
        if(!routers.isEmpty()&&(routers.size()!=1||this.preconditions.getFirst()!=routers.getFirst()||this.preconditions.stream().filter(p->!(p instanceof RecoveryModeRouter)).anyMatch(p->p.order()==Integer.MIN_VALUE)))throw new IllegalStateException("복구 판정이 첫 번째 사전조건이어야 합니다");
    }
    @Override public int getOrder(){return Ordered.HIGHEST_PRECEDENCE+1;}
    @Override public void run(ApplicationArguments args){
        ready=false;
        try{for(var condition:preconditions){long started=System.nanoTime();event(System.getLogger(getClass().getName()),INFO,"STARTUP_PRECONDITION_STARTED","condition",condition.getClass().getSimpleName());try{condition.run();}catch(RuntimeException failure){failure(System.getLogger(getClass().getName()),ERROR,"STARTUP_PRECONDITION_FAILED",failure,"condition",condition.getClass().getSimpleName());throw failure;}event(System.getLogger(getClass().getName()),INFO,"STARTUP_PRECONDITION_COMPLETED","condition",condition.getClass().getSimpleName(),"durationMs",(System.nanoTime()-started)/1_000_000);}}
        catch(RecoveryModeRouter.RestorePathRequired pending){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ReadinessGate.class.getName()),"READINESS_GATE_FAILED",pending);System.getLogger(ReadinessGate.class.getName()).log(System.Logger.Level.WARNING,"RESTORE_PATH_REQUIRED");return;}
        catch(ReadinessPrecondition.PreparationFailed failed){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ReadinessGate.class.getName()),"READINESS_GATE_FAILED",failed);System.getLogger(ReadinessGate.class.getName()).log(System.Logger.Level.WARNING,"DOCUMENT_SYNC_FAILED");return;}
        ready=true;event(System.getLogger(getClass().getName()),INFO,"STARTUP_READY","preconditions",preconditions.size());publisher.publishEvent(new RecoveryCompleted());
    }
    public boolean ready(){return ready;}
    public boolean admissionReady(){return ready&&knowledgeFailures.isEmpty();}
    public boolean startupReady(){return ready;}
    public void knowledgeUnavailable(String system){knowledgeFailures.add(system);}
    public void knowledgeRestored(String system){knowledgeFailures.remove(system);}
    public void down(){ready=false;}
}
