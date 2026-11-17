package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.integration.inference.LlmSettings;
import com.nori.ai.gateway.core.budget.CallType;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HexFormat;
import static org.assertj.core.api.Assertions.*;

class LlmInitialSettingsTest {
    @Test void yamlInitialValuesAndTokenizerBytesMatchTheConfiguredContract() throws Exception {
        Path root=Path.of(System.getProperty("aigw.rootDir", "../../.."));
        var yaml=new YamlPropertiesFactoryBean();yaml.setResources(new FileSystemResource(root.resolve("config/ai-gateway.yaml")));
        var env=new StandardEnvironment();env.getPropertySources().addFirst(new PropertiesPropertySource("fixture",yaml.getObject()));
        var settings=LlmSettings.load(env);
        assertThat(settings.baseUrl()).isEqualTo("http://192.168.0.160:11434/v1");
        assertThat(settings.generationModel()).isEqualTo("qwen3:4b");assertThat(settings.embeddingModel()).isEqualTo("bge-m3");
        assertThat(settings.temperature()).isZero();assertThat(settings.contextTokens()).isEqualTo(8192);
        assertThat(settings.requestTimeout()).isEqualTo(java.time.Duration.ofSeconds(60));assertThat(settings.apiKeyFile()).isNull();
        assertThat(settings.evidenceTokens()).isEqualTo(3072);assertThat(settings.generationConcurrency()).isOne();
        assertThat(settings.policy().budget(CallType.CLASSIFY)).isEqualTo(new com.nori.ai.gateway.core.budget.TokenBudgetPolicy.CallBudget(5120,768,1280,1024));
        assertThat(settings.policy().budget(CallType.COMPOSER)).isEqualTo(new com.nori.ai.gateway.core.budget.TokenBudgetPolicy.CallBudget(4608,1536,1536,512));
        assertThat(settings.tokenCounting().template()).isEqualTo(com.nori.ai.gateway.integration.testing.InferenceFixtures.template());
        assertThat(settings.tokenCounting().mode()).isEqualTo("hf");
        Path file=Path.of(settings.tokenCounting().tokenizerFile());if(!file.isAbsolute())file=root.resolve(file);
        assertThat(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file)))).isEqualTo(settings.tokenCounting().sha256());
    }
}
