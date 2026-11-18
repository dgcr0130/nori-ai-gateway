package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.integration.system.SystemAdapterModuleConfiguration;
import com.nori.ai.gateway.integration.system.SystemQuerySettings;
import java.net.URI;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(SystemAdapterModuleConfiguration.class)
public class SystemQueryConfiguration {
    @Bean SystemQuerySettings systemQuerySettings(GatewaySystemsProperties properties) {
        var endpoints = new LinkedHashMap<OwnerSystem,SystemQuerySettings.Endpoint>();
        properties.systems().forEach((id,system) -> {
            if (!system.tools().isEmpty()) endpoints.put(OwnerSystem.valueOf(id),
                new SystemQuerySettings.Endpoint(URI.create(system.backendUrl()),Path.of(system.gatewayTokenFile())));
        });
        return new SystemQuerySettings(endpoints);
    }
}
