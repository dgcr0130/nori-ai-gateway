package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.integration.system.*;
import java.time.Clock;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.*;

class SystemToolsTest {
    private GatewaySystemsProperties.SystemConfig system(boolean sources) {
        return new GatewaySystemsProperties.SystemConfig("issuer","service.sha","actors.jwks","http://localhost:8080","token.txt",
            List.of(new GatewaySystemsProperties.Tool("data.read","자료 조회",Map.of("type","object","additionalProperties",false),Map.of("type","object"),List.of("DATA_READ"),10000)),
            List.of(),sources?List.of(new GatewaySystemsProperties.App("first","team-a",List.of("AI_LOG_READ")),new GatewaySystemsProperties.App("second","team-b",List.of("AI_LOG_READ"))):List.of(),
            sources?List.of(new GatewaySystemsProperties.Repository("42","main","src/",List.of("AI_CODE_READ"))):List.of(),
            sources?List.of(new GatewaySystemsProperties.Trace("first","team-a",List.of("AI_TRACE_READ"))):List.of(),"REVIEW",Map.of());
    }
    @Test void builtinIdsInputsPermissionsAndDescriptionComeOnlyFromTheOwningSystem() {
        var definitions=SystemTools.manifests(new GatewaySystemsProperties(Map.of("NEW",system(true))));
        assertThat(definitions).extracting(CapabilityManifest::toolId).containsExactly("data.read","logs.search","trace.search","git.searchCode");
        var logs=definitions.get(1);assertThat(logs.ownerSystem()).isEqualTo(OwnerSystem.valueOf("NEW"));assertThat(logs.requiredPermission()).containsExactly("AI_LOG_READ");
        assertThat(logs.timeoutMs()).isEqualTo(10000);assertThat(logs.description()).isEqualTo("이 시스템이 남긴 로그에서 대상·기간·검색어로 로그 줄을 찾는다. 설비 수신·발신 시각과 데이터, 이벤트, 오류 기록을 찾을 때 사용한다. 검색 위치:team-a/first,team-b/second");
        assertThat(SchemaContracts.matches(logs.inputSchema(),SchemaContracts.parse("{}"))).isTrue();
        assertThat(SchemaContracts.matches(logs.inputSchema(),SchemaContracts.parse("{\"targetRef\":null,\"q\":null}"))).isTrue();
        assertThat(SchemaContracts.matches(logs.inputSchema(),SchemaContracts.parse("{\"app\":\"third\"}"))).isFalse();
        var trace=definitions.get(2);assertThat(trace.requiredPermission()).containsExactly("AI_TRACE_READ");
        assertThat(SchemaContracts.matches(trace.inputSchema(),SchemaContracts.parse("{\"from\":null,\"to\":null}"))).isTrue();
        for(String input:List.of("{\"targetRef\":\"eqp\"}","{\"q\":\"query\"}","{\"service\":\"first\"}"))assertThat(SchemaContracts.matches(trace.inputSchema(),SchemaContracts.parse(input))).isFalse();
        assertThat(definitions.get(3).requiredPermission()).containsExactly("AI_CODE_READ");
        assertThat(SystemTools.manifests(new GatewaySystemsProperties(Map.of("NEW",system(false))))).extracting(CapabilityManifest::toolId).containsExactly("data.read");
        assertThat(SystemTools.manifests(new GatewaySystemsProperties(Map.of()))).isEmpty();
    }
    @Test void explicitModuleImportCreatesTheSingleHttpPortAndSettingsWithoutMakingARequest() {
        new ApplicationContextRunner().withUserConfiguration(SystemQueryConfiguration.class)
            .withBean(GatewaySystemsProperties.class,()->new GatewaySystemsProperties(Map.of("NEW",system(false))))
            .withBean(Clock.class,Clock::systemUTC).withBean(com.nori.ai.gateway.core.sanitize.ContentSanitizer.class,com.nori.ai.gateway.core.sanitize.ContentSanitizer::new)
            .run(context->{assertThat(context).hasNotFailed().hasSingleBean(com.nori.ai.gateway.core.port.SystemQueryPort.class);
                assertThat(context.getBean(SystemQuerySettings.class).find(OwnerSystem.valueOf("NEW"))).isPresent();});
    }
}
