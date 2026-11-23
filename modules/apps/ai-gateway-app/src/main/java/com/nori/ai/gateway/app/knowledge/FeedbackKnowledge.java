package com.nori.ai.gateway.app.knowledge;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.app.job.recovery.*;
import com.nori.ai.gateway.app.security.*;
import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.repository.*;
import org.springframework.beans.factory.ObjectProvider;
import java.util.*;
import java.util.concurrent.*;

/** DB 확정 자료만 투영하며 실패한 시스템은 재구성될 때까지 접수를 닫습니다. */
public final class FeedbackKnowledge implements ReadinessPrecondition,ReviewedGlossary,AutoCloseable {
    private final FeedbackRepository feedback;
    private final DocumentIndexRepository indexes;
    private final DocumentKeywordIndex keywords;
    private final GatewaySystemsProperties systems;
    private final ScopeAssembler scopes;
    private final SecurityCredentialFiles credentials;
    private final ObjectProvider<ReadinessGate> readiness;
    private final Map<String,Map<String,String>> glossary=new ConcurrentHashMap<>();
    private final Set<String> pending=ConcurrentHashMap.newKeySet();
    private ScheduledExecutorService retry;
    private final Map<String,Map<String,String>> pendingContexts=new ConcurrentHashMap<>();
    public FeedbackKnowledge(FeedbackRepository feedback,DocumentIndexRepository indexes,DocumentKeywordIndex keywords,GatewaySystemsProperties systems,ScopeAssembler scopes,SecurityCredentialFiles credentials,ObjectProvider<ReadinessGate> readiness) {
        this.feedback=feedback;this.indexes=indexes;this.keywords=keywords;this.systems=systems;this.scopes=scopes;this.credentials=credentials;this.readiness=readiness;
    }
    @Override public int order(){return 1;}
    @Override public void run(){for(String system:new TreeSet<>(systems.systems().keySet())){var scope=scope(system);feedback.initializeGlossary(scope,systems.systems().get(system).glossary());glossary.put(system,feedback.glossary(scope));event(System.getLogger(getClass().getName()),INFO,"FEEDBACK_GLOSSARY_INITIALIZED","system",system,"entries",glossary.get(system).size());}}
    @Override public Map<String,String> current(VerifiedSystemScope scope,Map<String,String> initial) {
        var result=new TreeMap<>(initial);result.putAll(glossary.getOrDefault(scope.boundSystem().name(),Map.of()));return Collections.unmodifiableMap(result);
    }
    public synchronized void refresh(VerifiedSystemScope scope) {
        String system=scope.boundSystem().name();var logContext=org.slf4j.MDC.getCopyOfContextMap();pendingContexts.put(system,logContext==null?Map.of():logContext);pending.add(system);project(system);
        if(!pending.isEmpty()&&retry==null) {
            retry=Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().daemon().name("feedback-rebuild").factory());
            retry.scheduleWithFixedDelay(this::retryPending,1,1,TimeUnit.SECONDS);
        }
    }
    private synchronized void retryPending() {
        for(String system:List.copyOf(pending))project(system);
    }
    private void project(String system) {
        try(var ignored=com.nori.ai.gateway.app.observability.LogContext.open(pendingContexts.get(system))) {
        long started=System.nanoTime();event(System.getLogger(getClass().getName()),INFO,"FEEDBACK_KNOWLEDGE_STARTED","system",system);
        try {
            if(rebuild(system)){pending.remove(system);pendingContexts.remove(system);readiness.getObject().knowledgeRestored(system);event(System.getLogger(getClass().getName()),INFO,"FEEDBACK_KNOWLEDGE_COMPLETED","system",system,"glossaryEntries",glossary.getOrDefault(system,Map.of()).size(),"durationMs",(System.nanoTime()-started)/1_000_000);}
        } catch(RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(FeedbackKnowledge.class.getName()),"FEEDBACK_KNOWLEDGE_FAILED",failure);
            // DB 확정 응답은 보존하고 실제 투영 실패에만 새 질문 접수를 닫습니다.
            readiness.getObject().knowledgeUnavailable(system);
        }
        }
    }
    private boolean rebuild(String system) {
        var scope=scope(system);
        // 겹친 커밋은 실패가 아닙니다. 최신 DB 상태의 다음 투영을 남깁니다.
        long before=feedback.projectionVersion(scope);var values=feedback.glossary(scope);var state=indexes.searchState(scope);
        if(state.isPresent()) {
            var settings=state.get();var documents=indexes.load(scope,settings.id());
            var bundle=new DocumentCorpus.Bundle(settings.id(),settings.embeddingModel(),settings.dimension(),settings.configSha256(),documents.documents(),settings.answers());
            try(var prepared=keywords.prepare(scope,bundle)) {
                if(before!=feedback.projectionVersion(scope))return false;
                keywords.install(scope,prepared);
            }
        }
        if(before!=feedback.projectionVersion(scope)||!state.map(DocumentCorpus.Bundle::id).equals(indexes.searchState(scope).map(DocumentCorpus.Bundle::id)))return false;
        glossary.put(system,values);return true;
    }
    private VerifiedSystemScope scope(String system){return scopes.forDocumentStartup(OwnerSystem.valueOf(system),credentials);}
    @Override public void close(){ScheduledExecutorService executor;synchronized(this){executor=retry;retry=null;}if(executor!=null){executor.shutdownNow();try{if(!executor.awaitTermination(5,TimeUnit.SECONDS))throw new IllegalStateException("검토 재구성이 종료되지 않았습니다");}catch(InterruptedException interrupted){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(FeedbackKnowledge.class.getName()),"FEEDBACK_KNOWLEDGE_FAILED",interrupted);Thread.currentThread().interrupt();throw new IllegalStateException("검토 재구성 종료가 중단되었습니다");}}}
}
