package com.nori.ai.gateway.integration;

import com.nori.ai.gateway.integration.inference.InferenceEndpointConfig;
import com.nori.ai.gateway.integration.inference.InferenceModelConfiguration;
import com.nori.ai.gateway.integration.inference.InferenceReadinessService;
import com.nori.ai.gateway.integration.inference.OpenAiReadinessProbe;
import com.nori.ai.gateway.integration.inference.SpringAiOpenAiInferenceClient;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import com.nori.ai.gateway.integration.tokenizer.PromptTokenCounter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({HfTokenizerCounter.class, PromptTokenCounter.class,
    InferenceEndpointConfig.class, InferenceModelConfiguration.class, OpenAiReadinessProbe.class, InferenceReadinessService.class,
    SpringAiOpenAiInferenceClient.class})
public class InferenceAdapterModuleConfiguration {
    @org.springframework.context.annotation.Bean
    com.nori.ai.gateway.integration.inference.LlmSettings llmSettings(org.springframework.core.env.Environment environment) {
        return com.nori.ai.gateway.integration.inference.LlmSettings.load(environment);
    }
    @org.springframework.context.annotation.Bean
    com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer configuredChatTemplateRenderer(com.nori.ai.gateway.integration.inference.LlmSettings settings) {
        return new com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer(settings.tokenCounting().template());
    }
}
