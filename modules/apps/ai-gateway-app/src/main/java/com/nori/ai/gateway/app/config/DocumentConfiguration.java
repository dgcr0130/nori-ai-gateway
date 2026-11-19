package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.integration.storage.S3DocumentObjectStore;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import java.net.URI;
import java.nio.file.Path;

@Configuration(proxyBeanMethods=false)
public class DocumentConfiguration {
    @Bean @Lazy S3DocumentObjectStore documentObjectStore(GatewaySystemsProperties systems,Environment environment) {
        var sources=systems.systems().values().stream().flatMap(system->system.documents().stream())
            .map(d->new DocumentCorpus.Source(d.bucket(),d.prefix(),java.util.Set.copyOf(d.requiredPermissions()))).toList();
        try {
            return new S3DocumentObjectStore(sources.isEmpty()?null:URI.create(environment.getRequiredProperty("ai.gateway.documents.s3Endpoint")),
                path(environment,"accessKeyFile"),path(environment,"secretKeyFile"),sources);
        } catch(RuntimeException invalid) { throw new IllegalStateException("DOCUMENT_CONFIGURATION_INVALID"); }
    }
    private static Path path(Environment environment,String name) { String value=environment.getProperty("ai.gateway.documents."+name);return value==null?null:Path.of(value); }
    @Bean com.nori.ai.gateway.app.knowledge.DocumentStartupRunner documentStartupRunner(GatewaySystemsProperties systems,
            com.nori.ai.gateway.persistence.repository.DocumentIndexRepository store,
            org.springframework.beans.factory.ObjectProvider<DocumentSynchronization> synchronization,
            java.util.Optional<com.nori.ai.gateway.integration.inference.InferenceReadinessService> readiness,
            com.nori.ai.gateway.app.security.ScopeAssembler scopes,com.nori.ai.gateway.app.security.SecurityCredentialFiles credentials,
            com.nori.ai.gateway.integration.inference.LlmSettings settings,Environment environment,S3DocumentObjectStore objects) {
        // S3 자격과 제한은 빈 조립 때 검사하여 기존 Job 복구보다 앞서 확정합니다. 네트워크 요청은 하지 않습니다.
        var limits=new DocumentCorpus.Limits(environment.getProperty("ai.gateway.documents.maxFileBytes",Integer.class,1048576),
            environment.getProperty("ai.gateway.documents.childMaxChars",Integer.class,200),environment.getProperty("ai.gateway.documents.parentMaxChars",Integer.class,450),
            environment.getProperty("ai.gateway.documents.headingMaxChars",Integer.class,60));
        return new com.nori.ai.gateway.app.knowledge.DocumentStartupRunner(systems,store,synchronization::getObject,readiness,scopes,credentials,settings,environment,limits);
    }
}
