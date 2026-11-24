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
    @Test void actualSystemYamlLoadsAllNineIntentDeclarationsAndRegistrationContract()throws Exception {
        var environment=new StandardEnvironment();
        for(var source:new YamlPropertySourceLoader().load("intent-config",new FileSystemResource(Path.of(System.getProperty("aigw.rootDir")).resolve("config/ai-gateway.yaml"))))environment.getPropertySources().addLast(source);
        var tc=GatewaySystemsProperties.load(environment).systems().get("TC");
        assertThat(tc.intents()).extracting(i->i.id()).containsExactlyInAnyOrder("registration","status","model","dlq","communication","incident","document","help","unsupported");
        assertThat(tc.intents().stream().filter(i->i.answerTable()).map(i->i.id()).toList()).containsExactly("registration");
        assertThat(tc.intents().stream().filter(i->!i.id().equals("registration"))).allSatisfy(i->assertThat(i.answerTable()).isFalse());
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
