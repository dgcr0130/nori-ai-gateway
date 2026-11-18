package com.nori.ai.gateway.app.hooks;

import com.nori.ai.gateway.core.authz.SourcePermissionMap;
import com.nori.ai.gateway.core.registry.ManifestLoader;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import java.time.Clock;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class SourceProbeProfileTest {
    private ApplicationContextRunner runner(String... profiles){return new ApplicationContextRunner().withUserConfiguration(VerifyHooksModuleConfiguration.class).withInitializer(context->context.getEnvironment().setActiveProfiles(profiles))
        .withBean(TestHookRegistry.class,()->new TestHookRegistry(Clock.systemUTC(),new PausePointRegistry())).withBean(ManifestLoader.class,ManifestLoader::new)
.withBean(com.nori.ai.gateway.core.registry.CapabilityRegistry.class,()->new com.nori.ai.gateway.core.registry.CapabilityRegistry(List.of(),Set.of(),new com.nori.ai.gateway.core.registry.ContractVersionPolicy())).withBean(SourcePermissionMap.class,()->new SourcePermissionMap(Map.of())).withBean(com.nori.ai.gateway.app.config.GatewaySystemsProperties.class,()->new com.nori.ai.gateway.app.config.GatewaySystemsProperties(Map.of()));}
    @Test void onlyVerifyEnabledWithoutProdCreatesOneProbeAndController(){runner("verify").withPropertyValues("ai.gateway.test-hooks.enabled=true").run(context->{assertThat(context).hasNotFailed();assertThat(context.getBeansOfType(SourceRequestProbe.class)).hasSize(1);assertThat(context.getBeansOfType(SourceObservationController.class)).hasSize(1);});
        for(String[] profiles:new String[][]{{},{"prod"},{"verify","prod"}})runner(profiles).withPropertyValues("ai.gateway.test-hooks.enabled=true").run(context->{assertThat(context).hasNotFailed();assertThat(context.getBeansOfType(SourceRequestProbe.class)).isEmpty();assertThat(context.getBeansOfType(SourceObservationController.class)).isEmpty();});
        runner("verify").withPropertyValues("ai.gateway.test-hooks.enabled=false").run(context->{assertThat(context).hasNotFailed();assertThat(context.getBeansOfType(SourceRequestProbe.class)).isEmpty();});
    }
}
