package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.integration.InferenceAdapterModuleConfiguration;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.*;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.job.GenerationSemaphore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class InferenceModuleWiringTest {
    @Test void disabledModeStillResolvesConfiguredTokenCountingWithoutCreatingNetworkClients() {
        try(var context=new AnnotationConfigApplicationContext()) {
            var values=new java.util.HashMap<>(InferenceFixtures.properties());values.put("ai.gateway.inference.mode","disabled");
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture",values));
            context.register(InferenceAdapterModuleConfiguration.class);context.refresh();
            assertThat(context.getBean(PromptTokenCounter.class).count(java.util.List.of(new ChatMessage(ChatMessage.Role.USER,"응답")))).isPositive();
            assertThat(context.getBeansOfType(InferenceEndpointConfig.class)).isEmpty();
            assertThat(context.getBeansOfType(SpringAiOpenAiInferenceClient.class)).isEmpty();
            assertThat(context.getBeansOfType(OpenAiReadinessProbe.class)).isEmpty();
        }
    }
    @Test void enabledModeResolvesTheActualModuleAndRunsOnlyTheConfiguredLocalReadinessRequests() {
        try(var server=new OllamaStubServer(request->new OllamaStubServer.Json(200,"{\"data\":[]}"));var context=new AnnotationConfigApplicationContext()) {
            var values=new java.util.HashMap<>(InferenceFixtures.properties());values.put("ai.gateway.inference.mode","enabled");values.put("ai.gateway.llm.baseUrl",server.baseUrl()+"/v1");
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture",values));
            context.registerBean(TokenBudgetPolicy.class,()->TokenBudgetPolicy.current());
            context.registerBean(PreCallBudgetAssert.class,()->new PreCallBudgetAssert(context.getBean(PromptTokenCounting.class),context.getBean(TokenBudgetPolicy.class)));
            context.registerBean(TruncationDetector.class,TruncationDetector::new);context.registerBean(GenerationSemaphore.class,()->new GenerationSemaphore(1));
            context.registerBean(java.time.Clock.class,java.time.Clock::systemUTC);
            context.registerBean(io.micrometer.core.instrument.MeterRegistry.class,SimpleMeterRegistry::new);
            context.register(InferenceAdapterModuleConfiguration.class);context.refresh();
            var readiness=context.getBean(InferenceReadinessService.class);readiness.stop();
            assertThat(readiness.refresh().state()).isEqualTo(OpenAiReadinessProbe.State.MODEL_LOADING);
            assertThat(context.getBean(SpringAiOpenAiInferenceClient.class)).isNotNull();
            assertThat(server.received()).isNotEmpty().allSatisfy(request->assertThat(request.path()).isEqualTo("/v1/models"));
        }
    }
}
