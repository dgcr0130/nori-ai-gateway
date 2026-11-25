package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.nio.file.Path;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import static org.assertj.core.api.Assertions.*;

class GatewayIntentConfigurationTest {
    @Test void connectionIntentUsesDedicatedEqpReadWithoutLotScope() throws Exception {
        var environment = new StandardEnvironment();
        for (var source : new YamlPropertySourceLoader().load("connection-config", new FileSystemResource(
                Path.of(System.getProperty("aigw.rootDir")).resolve("config/ai-gateway.yaml")))) {
            environment.getPropertySources().addLast(source);
        }
        var tc = GatewaySystemsProperties.load(environment).systems().get("TC");
        var connection = tc.intents().stream().filter(i -> i.id().equals("connection")).findFirst();
        assertThat(connection).as("연결만 묻는 질문에는 LOT 조회가 필요 없는 의도가 등록되어야 합니다").isPresent();
        assertThat(connection.orElseThrow().toolIds()).containsExactly("tc.eqp.getConnectionState");
        assertThat(connection.orElseThrow().subjectRequired()).isTrue();
        assertThat(connection.orElseThrow().periodRequirement().name()).isEqualTo("NONE");
        var tool = tc.tools().stream().filter(t -> t.id().equals("tc.eqp.getConnectionState")).findFirst().orElseThrow();
        assertThat(tool.requiredPermissions()).containsExactly("EQP_READ");
        var schema = SchemaContracts.JSON.valueToTree(tool.inputSchema());
        assertThat(schema.path("required")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("eqp")));
        assertThat(schema.path("properties").propertyNames()).containsExactly("eqp");
        assertThat(schema.path("additionalProperties").asBoolean()).isFalse();
        var status = tc.intents().stream().filter(i -> i.id().equals("status")).findFirst().orElseThrow();
        assertThat(status.toolIds()).containsExactly("tc.eqp.getOperationalSnapshot");
        assertThat(status.description()).contains("LOT");
    }
    @Test void actualSystemYamlLoadsIntentDeclarationsAndRegistrationContract()throws Exception {
        var environment=new StandardEnvironment();
        for(var source:new YamlPropertySourceLoader().load("intent-config",new FileSystemResource(Path.of(System.getProperty("aigw.rootDir")).resolve("config/ai-gateway.yaml"))))environment.getPropertySources().addLast(source);
        var tc=GatewaySystemsProperties.load(environment).systems().get("TC");
        assertThat(tc.intents()).extracting(i->i.id()).containsExactlyInAnyOrder("registration","connection","status","model","dlq","communication","incident","document","help","unsupported","code");
        // 본문은 대화체 문장만 두고 조회 원문은 참고 자료로 보이므로 TC는 서버 표를 붙이는 의도가 없습니다.
        assertThat(tc.intents()).allSatisfy(i->assertThat(i.answerTable()).as(i.id()).isFalse());
        assertThat(tc.intents()).allSatisfy(intent->assertThat(intent.description()).isNotBlank());
        assertThat(tc.intents().stream().filter(i->i.id().equals("registration")).findFirst().orElseThrow().toolIds()).containsExactly("tc.eqp.readRegistration");
        assertThat(tc.intents().stream().filter(i->i.id().equals("incident")).findFirst().orElseThrow().toolIds()).containsExactly("logs.search","trace.search");
        var registration=tc.tools().stream().filter(t->t.id().equals("tc.eqp.readRegistration")).findFirst().orElseThrow();
        assertThat(registration.requiredPermissions()).containsExactly("EQP_READ");var schema=SchemaContracts.JSON.valueToTree(registration.inputSchema());
        assertThat(schema.path("required")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("eqp")));assertThat(schema.path("properties").propertyNames()).containsExactly("eqp");
        assertThat(schema.path("properties").path("eqp").path("type").asString()).isEqualTo("string");
        assertThat(schema.path("properties").path("eqp").path("minLength").asInt()).isOne();assertThat(schema.path("properties").path("eqp").path("maxLength").asInt()).isEqualTo(64);
    }
    @Test void loaderBindsACustomIntentDescriptionAndPlanValuesWithoutTcSpecificCode(){
        var environment=new org.springframework.mock.env.MockEnvironment()
            .withProperty("ai.gateway.systems.TC.issuer","fixture").withProperty("ai.gateway.systems.TC.serviceTokenSha256File","fixture.sha")
            .withProperty("ai.gateway.systems.TC.actorJwksFile","fixture.jwks").withProperty("ai.gateway.systems.TC.reviewPermission","REVIEW")
            .withProperty("ai.gateway.systems.TC.intents[0].id","custom-doc").withProperty("ai.gateway.systems.TC.intents[0].description","등록된 사용자 설명")
            .withProperty("ai.gateway.systems.TC.intents[0].subjectRequired","false").withProperty("ai.gateway.systems.TC.intents[0].periodRequirement","NONE")
            .withProperty("ai.gateway.systems.TC.intents[0].taskType","KNOWLEDGE_QUERY").withProperty("ai.gateway.systems.TC.intents[0].documentSearch","true");
        var intent=GatewaySystemsProperties.load(environment).systems().get("TC").intents().getFirst();
        assertThat(intent.id()).isEqualTo("custom-doc");assertThat(intent.description()).isEqualTo("등록된 사용자 설명");assertThat(intent.toolIds()).isEmpty();assertThat(intent.subjectRequired()).isFalse();assertThat(intent.periodRequirement().name()).isEqualTo("NONE");assertThat(intent.taskType().name()).isEqualTo("KNOWLEDGE_QUERY");assertThat(intent.documentSearch()).isTrue();assertThat(intent.answerTable()).isFalse();
        environment.withProperty("ai.gateway.systems.TC.intents[0].answerTable","true");
        assertThat(GatewaySystemsProperties.load(environment).systems().get("TC").intents().getFirst().answerTable()).isTrue();
    }
}
