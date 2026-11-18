package com.nori.ai.gateway.app.hooks;

import com.nori.ai.gateway.core.authz.SourcePermissionMap;
import com.nori.ai.gateway.core.registry.CapabilityRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

@Configuration(proxyBeanMethods = false)
@Profile("verify")
@ConditionalOnProperty(name = "ai.gateway.test-hooks.enabled", havingValue = "true")
@Import({SourceObservationController.class, LlmScriptHooksConfiguration.class})
public class VerifyHooksModuleConfiguration {
    @Bean @Profile("!prod") SourceRequestProbe sourceRequestProbe(TestHookRegistry hooks,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems){return new SourceRequestProbe(hooks,systems);}
    @Bean SeamRegistry seamRegistry(TestHookRegistry hooks, CapabilityRegistry registry, SourcePermissionMap permissions) {
        return new SeamRegistry(hooks, registry, permissions);
    }
}
