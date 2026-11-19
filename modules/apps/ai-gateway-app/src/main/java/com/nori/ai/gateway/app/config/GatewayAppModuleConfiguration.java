package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.persistence.PersistenceModuleConfiguration;
import com.nori.ai.gateway.app.security.IngressSecurityConfiguration;
import com.nori.ai.gateway.integration.InferenceAdapterModuleConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({CoreBeansConfiguration.class, NormalDatabaseConfiguration.class, PersistenceModuleConfiguration.class,
    InferenceAdapterModuleConfiguration.class, IngressSecurityConfiguration.class,
    JobConfiguration.class, com.nori.ai.gateway.app.job.WorkflowExecutionConfiguration.class, TestHooksConfiguration.class, com.nori.ai.gateway.app.web.WebConfiguration.class,
    com.nori.ai.gateway.app.hooks.VerifyHooksModuleConfiguration.class,
    SourceConnectorConfiguration.class, SystemQueryConfiguration.class, DocumentConfiguration.class,
    com.nori.ai.gateway.integration.storage.ObjectStorageModuleConfiguration.class, com.nori.ai.gateway.app.observability.ObservabilityConfiguration.class, com.nori.ai.gateway.app.ops.OpsConfiguration.class,
    com.nori.ai.gateway.app.safety.ProductionSafetyGuard.class, com.nori.ai.gateway.app.hooks.EgressProbeHook.class,
    com.nori.ai.gateway.app.hooks.EmbeddingProbeHook.class, com.nori.ai.gateway.app.hooks.ReadinessStubHook.class})
public class GatewayAppModuleConfiguration {}
