package com.nori.ai.gateway.app.security;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import static org.assertj.core.api.Assertions.*;

class SystemConfigurationTest {
    private MockEnvironment environment() {
        return new MockEnvironment().withProperty("ai.gateway.systems[New-7].issuer","new-backend")
            .withProperty("ai.gateway.systems[New-7].service-token-sha256-file","digest")
            .withProperty("ai.gateway.systems[New-7].actor-jwks-file","jwks")
            .withProperty("ai.gateway.systems[New-7].review-permission","AI_REVIEW");
    }
    private GatewaySystemsProperties bind(MockEnvironment env) { return new IngressSecurityConfiguration().gatewaySystemsProperties(env); }
    @Test void emptySystemsAndDocumentOnlySystemAreValidAndTcDefaultsDoNotLeak() {
        assertThat(new SecurityCredentialFiles(bind(new MockEnvironment())).authenticate("unknown")).isEmpty();
        var config=bind(environment());
        assertThat(config.systems()).containsOnlyKeys("New-7");
        var item=config.systems().get("New-7");
        assertThat(item.tools()).isEmpty(); assertThat(item.apps()).isEmpty(); assertThat(item.documents()).isEmpty();
        assertThat(item.backendUrl()).isNull();
    }
    @Test void unknownNestedFieldsInvalidIdsAndMissingRequiredValuesFailWithoutInputLeakage() {
        for(String field:List.of("typo","tools[0].url","documents[0].wrong")) {
            var env=environment().withProperty("ai.gateway.systems[New-7]."+field,"secret-sentinel");
            assertThatThrownBy(()->bind(env)).hasMessage("시스템 설정이 잘못되었습니다").hasNoCause();
        }
        assertThatThrownBy(()->bind(new MockEnvironment().withProperty("ai.gateway.systems[_invalid].issuer","issuer"))).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->bind(environment().withProperty("ai.gateway.systems[New-7].issuer",""))).isInstanceOf(IllegalStateException.class);
    }
    @Test void prefixConflictsIncludeEmptyPrefixButDifferentBucketsAreIndependent() {
        var env=environment();
        for(int i=0;i<2;i++) {
            env.withProperty("ai.gateway.systems[New-7].documents["+i+"].bucket","bucket")
                .withProperty("ai.gateway.systems[New-7].documents["+i+"].prefix",i==0?"rag/":"rag/sub/")
                .withProperty("ai.gateway.systems[New-7].documents["+i+"].required-permissions[0]","AI_DOC_READ");
        }
        assertThatThrownBy(()->bind(env)).isInstanceOf(IllegalStateException.class);
        env.withProperty("ai.gateway.systems[New-7].documents[0].prefix","");
        assertThatThrownBy(()->bind(env)).isInstanceOf(IllegalStateException.class);
        env.withProperty("ai.gateway.systems[New-7].documents[1].bucket","other");
        assertThat(bind(env).systems().get("New-7").documents()).hasSize(2);
    }
    @Test void invalidUrlsToolTimeoutPermissionsAndGlossaryCyclesAreRejected() {
        var schema=Map.<String,Object>of("type","object","additionalProperties",false);
        for(int timeout:new int[]{0,10001}) assertThatThrownBy(()->new GatewaySystemsProperties.Tool("read","조회",schema,schema,List.of("READ"),timeout)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new GatewaySystemsProperties.Tool("read","조회",schema,schema,List.of(),null).timeoutMs()).isEqualTo(10000);
        for(String url:List.of("http://user@host","http://host/path","http://host?x","file:///path","http://host#f"))
            assertThatThrownBy(()->bind(environment().withProperty("ai.gateway.systems[New-7].backend-url",url))).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->bind(environment().withProperty("ai.gateway.systems[New-7].glossary.A","B")
            .withProperty("ai.gateway.systems[New-7].glossary.B","A"))).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->bind(environment().withProperty("ai.gateway.systems[New-7].review-permission","bad"))).isInstanceOf(IllegalStateException.class);
    }
    @Test void actualInitialYamlBindsSchemasAndDuplicateYamlKeysAreRejected() throws Exception {
        var env=new MockEnvironment().withProperty("AI_GATEWAY_TC_SERVICE_TOKEN_SHA256_FILE","digest")
            .withProperty("AI_GATEWAY_TC_ACTOR_JWKS_FILE","jwks").withProperty("AI_GATEWAY_TC_GATEWAY_TOKEN_FILE","outbound");
        var loader=new org.springframework.boot.env.YamlPropertySourceLoader();
        var source=new org.springframework.core.io.FileSystemResource(java.nio.file.Path.of(System.getProperty("aigw.rootDir"),"config/ai-gateway.yaml"));
        for(var properties:loader.load("initial",source)) env.getPropertySources().addLast(properties);
        var tc=bind(env).systems().get("TC");
        assertThat(tc.reviewPermission()).isEqualTo("AI_REVIEW");
        assertThat(tc.tools()).hasSize(4);assertThat(tc.documents()).hasSize(2).allSatisfy(d->assertThat(d.bucket()).isEqualTo("nori-tc"));assertThat(tc.documents()).extracting(GatewaySystemsProperties.Document::prefix).containsExactly("00.rag/user/","00.rag/admin/");assertThat(tc.documents().get(0).requiredPermissions()).containsExactly("AI_DOC_READ");assertThat(tc.documents().get(1).requiredPermissions()).containsExactly("AI_DOC_READ","AI_ADMIN_DOC_READ");
        assertThat(tc.tools().getFirst().inputSchema()).containsKey("properties");
        String duplicate="ai:\n  gateway:\n    systems:\n      New: {}\n      New: {}\n";
        assertThatThrownBy(()->loader.load("duplicate",new org.springframework.core.io.ByteArrayResource(duplicate.getBytes(java.nio.charset.StandardCharsets.UTF_8)))).isInstanceOf(RuntimeException.class);
    }

    @Test void yamlKeepsSchemaPropertyNamesAndArrayTypesAndRejectsUnknownSystemFields() throws Exception {
        var schema=Map.<String,Object>of("type","object","properties",Map.of("a.b",Map.of("type","string","enum",List.of("x","y"))),
            "required",List.of("a.b"),"additionalProperties",false,"default",Map.of("0","value"));
        var tool=new GatewaySystemsProperties.Tool("read","조회",schema,Map.of("type","object"),List.of("READ"),1);
        var system=new LinkedHashMap<String,Object>(Map.of("issuer","issuer","serviceTokenSha256File","digest","actorJwksFile","jwks",
            "reviewPermission","AI_REVIEW","backendUrl","http://host","gatewayTokenFile","outbound","tools",List.of(tool)));
        for(String invalid:List.of("none","unknown","type")) {
            if(invalid.equals("unknown")) system.put("typo","invalid");
            if(invalid.equals("type")){system.remove("typo");system.put("issuer",123);}
            String data=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(Map.of("ai",Map.of("gateway",Map.of("systems",Map.of("New-7",system)))));
            var resource=new org.springframework.core.io.ByteArrayResource(data.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
                @Override public String getFilename(){return "ai-gateway.yaml";}
            };
            var env=new MockEnvironment();for(var source:new org.springframework.boot.env.YamlPropertySourceLoader().load("fixture",resource))env.getPropertySources().addLast(source);
            if(!invalid.equals("none"))assertThatThrownBy(()->bind(env)).as(invalid).isInstanceOf(IllegalStateException.class).hasNoCause();
            else assertThat(bind(env).systems().get("New-7").tools().getFirst().inputSchema()).isEqualTo(schema);
        }
    }

    @Test void deploymentCredentialPathsUseContainerDefaultsAndRespectEnvironmentOverrides() throws Exception {
        var loader = new org.springframework.boot.env.YamlPropertySourceLoader();
        var source = new org.springframework.core.io.FileSystemResource(java.nio.file.Path.of(System.getProperty("aigw.rootDir"), "config/ai-gateway.yaml"));
        for (boolean override : List.of(false, true)) {
            var env = new MockEnvironment();
            var variables = Map.<String,Object>of("AI_GATEWAY_TC_SERVICE_TOKEN_SHA256_FILE", "/local/service",
                "AI_GATEWAY_TC_ACTOR_JWKS_FILE", "/local/jwks", "AI_GATEWAY_TC_GATEWAY_TOKEN_FILE", "/local/gateway");
            env.getPropertySources().addFirst(new org.springframework.core.env.SystemEnvironmentPropertySource("fixtureEnvironment", override ? variables : Map.of()));
            for (var properties : loader.load("deployment", source)) env.getPropertySources().addLast(properties);
            var tc = bind(env).systems().get("TC");
            assertThat(tc.serviceTokenSha256File()).isEqualTo(override ? "/local/service" : "/app/config/tc-service-token-sha256");
            assertThat(tc.actorJwksFile()).isEqualTo(override ? "/local/jwks" : "/app/config/tc-actor-jwks.json");
            assertThat(tc.gatewayTokenFile()).isEqualTo(override ? "/local/gateway" : "/app/config/tc-gateway-token");
        }
    }

}
