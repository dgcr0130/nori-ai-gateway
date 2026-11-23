package com.nori.ai.gateway.app.knowledge;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.app.job.recovery.ReadinessPrecondition;
import com.nori.ai.gateway.app.security.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.integration.inference.*;
import org.springframework.core.env.Environment;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.function.Supplier;

public final class DocumentStartupRunner implements ReadinessPrecondition {
    private final GatewaySystemsProperties systems;private final DocumentIndexStore store;private final Supplier<DocumentSynchronization> synchronization;
    private final Optional<InferenceReadinessService> readiness;private final ScopeAssembler scopes;private final SecurityCredentialFiles credentials;
    private final LlmSettings settings;private final Environment environment;
    private final DocumentCorpus.Limits limits;
    public DocumentStartupRunner(GatewaySystemsProperties systems,DocumentIndexStore store,Supplier<DocumentSynchronization> synchronization,
            Optional<InferenceReadinessService> readiness,ScopeAssembler scopes,SecurityCredentialFiles credentials,LlmSettings settings,Environment environment,DocumentCorpus.Limits limits) {
        this.systems=systems;this.store=store;this.synchronization=synchronization;this.readiness=readiness;this.scopes=scopes;this.credentials=credentials;this.settings=settings;this.environment=environment;
        this.limits=limits;
    }
    @Override public int order() { return 0; }
    @Override public void run() {
        try {
            for(var system:new TreeMap<>(systems.systems()).entrySet()) {
                var scope=scopes.forDocumentStartup(OwnerSystem.valueOf(system.getKey()),credentials);
                var sources=system.getValue().documents().stream().map(d->new DocumentCorpus.Source(d.bucket(),d.prefix(),Set.copyOf(d.requiredPermissions()))).toList();
                // 문서가 없는 독립 도구 시스템에는 S3·임베딩 자격과 요청을 요구하지 않습니다.
                // 이전 자료가 있으면 빈 새 묶음으로 전환하여 제거한 원천을 재사용하지 못하게 합니다.
                if(sources.isEmpty() && !store.hasActiveIndex(scope) && store.activeAnswers(scope).isEmpty())continue;
                var probe=readiness.orElseThrow().refresh();
                if(probe.state()!=OpenAiReadinessProbe.State.READY || probe.embeddingDimension()==null)throw new PreparationFailed();
                String fingerprint=fingerprint(sources,limits);
                synchronization.get().synchronize(scope,sources,limits,settings.embeddingModel(),fingerprint,probe.embeddingDimension());
            }
        } catch(RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(DocumentStartupRunner.class.getName()),"DOCUMENT_STARTUP_RUNNER_FAILED",failure); throw new PreparationFailed(); }
    }
    private String fingerprint(List<DocumentCorpus.Source> sources,DocumentCorpus.Limits limits) {
        // 자격 본문은 지문 입력에도 넣지 않습니다. 모델·주소·차원은 묶음 필드와 함께 결속합니다.
        var llm=new TreeMap<String,Object>();llm.put("baseUrl",settings.baseUrl());llm.put("embeddingModel",settings.embeddingModel());
        llm.put("generationModel",settings.generationModel());llm.put("temperature",settings.temperature());llm.put("contextTokens",settings.contextTokens());
        llm.put("requestTimeout",settings.requestTimeout().toString());llm.put("tokenCounting",settings.tokenCounting());llm.put("budgets",settings.budgets());
        llm.put("evidenceTokens",settings.evidenceTokens());llm.put("generationConcurrency",settings.generationConcurrency());
        var normalized=sources.stream().map(s->Map.of("bucket",s.bucket(),"prefix",s.prefix(),"permissions",new TreeSet<>(s.permissions()))).toList();
        String input=SchemaContracts.JSON.writeValueAsString(Map.of("llm",llm,"sources",normalized,"limits",limits,"mask","mask-v1","s3Endpoint",environment.getProperty("ai.gateway.documents.s3Endpoint","")));
        String text=SchemaContracts.JSON.writeValueAsString(ordered(SchemaContracts.JSON.readValue(input,Object.class)));
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8))); }
        catch(NoSuchAlgorithmException impossible) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(DocumentStartupRunner.class.getName()),"DOCUMENT_STARTUP_RUNNER_FAILED",impossible); throw new IllegalStateException("설정 지문을 계산할 수 없습니다"); }
    }
    private static Object ordered(Object value) {
        if(value instanceof Map<?,?> map) { var sorted=new TreeMap<String,Object>();map.forEach((key,item)->sorted.put(key.toString(),ordered(item)));return sorted; }
        if(value instanceof List<?> list)return list.stream().map(DocumentStartupRunner::ordered).toList();
        return value;
    }
}
