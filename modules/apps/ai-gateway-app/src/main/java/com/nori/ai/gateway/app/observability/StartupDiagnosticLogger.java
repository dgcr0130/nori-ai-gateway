package com.nori.ai.gateway.app.observability;

import com.nori.ai.gateway.app.config.CapacityProfile;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.logging.DiagnosticLog;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.util.*;
import org.springframework.boot.*;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/** 설정은 허용한 경로·주소·한도만 읽고 파일 내용이나 임의 환경변수는 열거하지 않습니다. */
public final class StartupDiagnosticLogger implements ApplicationRunner,Ordered {
    private final GatewaySystemsProperties systems;private final CapacityProfile capacity;private final Environment environment;private final com.nori.ai.gateway.app.config.SourceConnectorConfiguration.SourceSnapshot sources;
    public StartupDiagnosticLogger(GatewaySystemsProperties systems,CapacityProfile capacity,Environment environment,com.nori.ai.gateway.app.config.SourceConnectorConfiguration.SourceSnapshot sources){this.systems=systems;this.capacity=capacity;this.environment=environment;this.sources=sources;}
    @Override public int getOrder(){return Ordered.HIGHEST_PRECEDENCE;}
    @Override public void run(ApplicationArguments arguments) {
        var values=new TreeMap<String,Object>();var registered=new TreeMap<String,Object>();
        systems.systems().forEach((id,system)->{
            var entry=new LinkedHashMap<String,Object>();entry.put("backendUrl",system.backendUrl());entry.put("apps",system.apps());entry.put("documents",system.documents());entry.put("traces",system.traces());entry.put("repositories",system.repositories());
            entry.put("serviceTokenSha256File",system.serviceTokenSha256File());entry.put("actorJwksFile",system.actorJwksFile());entry.put("gatewayTokenFile",system.gatewayTokenFile());registered.put(id,entry);
        });
        values.put("systems",registered);values.put("capacity",capacity);
        for(String key:List.of("llm.baseUrl","llm.generationModel","llm.embeddingModel","llm.apiKeyFile","llm.tokenCounting.tokenizerFile","llm.requestTimeout","documents.s3Endpoint","documents.accessKeyFile","documents.secretKeyFile","documents.maxFileBytes","llm.contextTokens","llm.generationConcurrency"))values.put(key,environment.getProperty("ai.gateway."+key));
        for(var source:List.of(sources.loki(),sources.tempo(),sources.gitlab()))values.put("source."+source.sourceId(),Map.of("baseUrl",source.baseUrl().map(Object::toString).orElse("absent"),"tokenPath",source.tokenPath().map(Object::toString).orElse("absent"),"serviceTargetVerified",source.serviceTargetVerified()));
        values.put("retention.auditDays",90);values.put("retention.conversationDays",30);
        DiagnosticLog.body(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"STARTUP_CONFIG",SchemaContracts.JSON.writeValueAsString(values));
    }
}
