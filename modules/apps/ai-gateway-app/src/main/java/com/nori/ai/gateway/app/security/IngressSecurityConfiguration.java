package com.nori.ai.gateway.app.security;

import com.nori.ai.gateway.persistence.audit.auth.AuthRejectionAppendRepository;
import jakarta.servlet.DispatcherType;
import java.nio.file.Path;
import java.time.Clock;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
public class IngressSecurityConfiguration {
    @Bean com.nori.ai.gateway.app.config.GatewaySystemsProperties gatewaySystemsProperties(Environment environment) {
        try {
            return com.nori.ai.gateway.app.config.GatewaySystemsProperties.load(environment);
        } catch (RuntimeException failure) { throw new IllegalStateException("시스템 설정이 잘못되었습니다"); }
    }
    @Bean SecurityCredentialFiles securityCredentialFiles(com.nori.ai.gateway.app.config.GatewaySystemsProperties properties) {
        return new SecurityCredentialFiles(properties);
    }
    @Bean RouteClassifier routeClassifier() { return new RouteClassifier(); }
    @Bean ActorAssertionVerifier actorAssertionVerifier(SecurityCredentialFiles credentials, Clock clock, RouteClassifier routes) { return new ActorAssertionVerifier(credentials,clock,routes); }
    @Bean ScopeAssembler scopeAssembler() { return new ScopeAssembler(); }
    @Bean BodySystemFieldGuard bodySystemFieldGuard() { return new BodySystemFieldGuard(); }
    @Bean AuthRejectionAuditor authRejectionAuditor(AuthRejectionAppendRepository repository) { return new AuthRejectionAuditor(repository); }
    @Bean FilterRegistrationBean<ServiceTokenAuthFilter> serviceTokenAuthFilterRegistration(SecurityCredentialFiles credentials, ActorAssertionVerifier verifier,
            ScopeAssembler scopes, BodySystemFieldGuard bodies, AuthRejectionAuditor auditor, com.nori.ai.gateway.app.observability.AuditRecorder audit,
            io.opentelemetry.api.OpenTelemetry openTelemetry) {
        var registration = new FilterRegistrationBean<>(new ServiceTokenAuthFilter(credentials,verifier,scopes,bodies,auditor,audit,openTelemetry));
        registration.setName("serviceTokenAuthFilter"); registration.addUrlPatterns("/v1/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST); registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
