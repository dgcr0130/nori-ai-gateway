package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class InferenceReadinessServiceTest {
    static final class MutableClock extends Clock {
        Instant now=Instant.parse("2026-11-18T00:00:00Z");
        public ZoneId getZone(){return ZoneOffset.UTC;} public Clock withZone(ZoneId zone){return this;} public Instant instant(){return now;}
    }
    @Test void checksThirtySecondCadenceAndSixHundredSecondAlertBoundaryUntilReady() {
        var clock=new MutableClock();var meters=new SimpleMeterRegistry();
        InferenceClient unused=new InferenceClient(){
            public ChatOutcome chat(com.nori.ai.gateway.core.budget.CallType type,List<com.nori.ai.gateway.core.budget.ChatMessage> messages,int max){throw new AssertionError();}
            public List<float[]> embed(List<String> inputs){throw new AssertionError();}
        };
        try(var tokenizer=new HfTokenizerCounter();var probe=new OpenAiReadinessProbe(new InferenceEndpointConfig(InferenceFixtures.settings("http://127.0.0.1:1/v1")),unused,tokenizer)) {
            var service=new InferenceReadinessService(probe,clock,meters);
            assertThat(InferenceReadinessService.PROBE_INTERVAL).isEqualTo(Duration.ofSeconds(30));
            assertThat(service.ready()).isFalse();
            var next=new AtomicReference<>(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.MODEL_LOADING,OpenAiReadinessProbe.Reason.NONE,null));
            service.overrideSource(next::get);
            assertThat(service.refresh().alerts()).isEmpty();
            clock.now=clock.now.plusSeconds(600);assertThat(service.refresh().alerts()).isEmpty();
            clock.now=clock.now.plusSeconds(1);assertThat(service.refresh().alerts()).containsExactly(OpenAiReadinessProbe.State.MODEL_LOADING);
            next.set(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE,OpenAiReadinessProbe.Reason.ENDPOINT_ERROR,null));
            service.refresh();clock.now=clock.now.plusSeconds(601);
            assertThat(service.refresh().alerts()).containsExactlyInAnyOrder(OpenAiReadinessProbe.State.MODEL_LOADING,OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE);
            next.set(new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.READY,OpenAiReadinessProbe.Reason.NONE,3));
            assertThat(service.refresh().alerts()).isEmpty();assertThat(service.ready()).isTrue();assertThat(service.admissionBlock()).isEmpty();
            assertThat(service.snapshot().embeddingDimension()).isEqualTo(3);
            service.stop();
        }
    }
}
