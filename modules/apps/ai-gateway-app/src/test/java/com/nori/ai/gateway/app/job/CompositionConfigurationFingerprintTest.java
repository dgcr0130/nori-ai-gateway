package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.*;
import com.nori.ai.gateway.core.knowledge.SearchSettings;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.AnswerSnapshot;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.JsonNode;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 지문의 hex 모양 대신 실제 해시 입력에 새 작성 계약이 들어가는지 확인합니다. */
class CompositionConfigurationFingerprintTest {
    @Test void applicationFingerprintBindsTheV2PromptAndSentenceFactIdSchema() throws Exception {
        var environment=new StandardEnvironment();
        for(var source:new YamlPropertySourceLoader().load("composition-config",new FileSystemResource(Path.of(System.getProperty("aigw.rootDir")).resolve("config/ai-gateway.yaml"))))environment.getPropertySources().addLast(source);
        var systems=GatewaySystemsProperties.load(environment);var inputs=new ArrayList<String>();
        try(var hashes=mockStatic(AnswerSnapshot.class,CALLS_REAL_METHODS)) {
            hashes.when(()->AnswerSnapshot.sha(anyString())).thenAnswer(call->{inputs.add(call.getArgument(0));return call.callRealMethod();});
            String sha=ReflectionTestUtils.invokeMethod(WorkflowExecutionConfiguration.class,"configurationFingerprint",environment,SearchSettings.defaults(),systems);
            assertThat(sha).matches("[0-9a-f]{64}");
        }
        byte[] bytes;
        try(var input=getClass().getResourceAsStream("/prompts/compose-v2.txt")){assertThat(input).isNotNull();bytes=input.readAllBytes();}
        String prompt=new String(bytes,StandardCharsets.UTF_8);
        var configuration=SchemaContracts.JSON.readTree(inputs.getLast());var strings=new ArrayList<String>();collectStrings(configuration,strings);
        assertThat(strings).contains(prompt);
        assertThat(containsCompositionSchema(configuration)).as("새 factIds 구조화 스키마가 지문 입력에 포함되어야 합니다").isTrue();
    }
    private void collectStrings(JsonNode value,List<String> strings) {
        if(value.isString())strings.add(value.asString());else if((value.isObject()||value.isArray()))value.forEach(child->collectStrings(child,strings));
    }
    private boolean containsCompositionSchema(JsonNode value) {
        if(value.isString()) {
            String text=value.asString().strip();
            if(text.startsWith("{"))try{return containsCompositionSchema(SchemaContracts.JSON.readTree(text));}catch(IllegalArgumentException invalid){return false;}
            return false;
        }
        if(value.isObject()&&value.path("properties").path("sentences").path("items").path("properties").has("factIds"))return true;
        if((value.isObject()||value.isArray()))for(var child:value)if(containsCompositionSchema(child))return true;
        return false;
    }
}
