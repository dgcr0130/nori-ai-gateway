package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

class InferenceEndpointConfigTest {
    @TempDir Path directory;
    @Test void configuredPrefixIsUsedAsIsAndExplicitPortIsRequired() {
        for (String url : new String[]{"http://127.0.0.1:1234/v1", "https://localhost:443/openai"}) {
            var endpoint = new InferenceEndpointConfig(InferenceFixtures.settings(url));
            assertThat(endpoint.sdkBaseUrl()).isEqualTo(url);
            assertThat(endpoint.readinessUri().toString()).isEqualTo(url + "/models");
            assertThat(endpoint.apiKey()).isNull();
        }
        for (String url : new String[]{"http://localhost/v1", "http://localhost:1234", "http://localhost:1234/v1/", "http://x:y@localhost:1234/v1", "file:///tmp/v1", "http://localhost:1/v1?q=x", "http://localhost:1/v1#x"})
            assertThatThrownBy(() -> new InferenceEndpointConfig(InferenceFixtures.settings(url))).hasMessage("추론 endpoint 설정이 잘못되었습니다");
    }
    @Test void keyFileIsOptionalAndInvalidKeyNeverAppearsInFailure() throws Exception {
        var original = InferenceFixtures.settings("http://127.0.0.1:1/v1");
        Path key = directory.resolve("api.key"); Files.writeString(key, "fixture-key\n");
        var settings = copy(original, key.toString(), original.requestTimeout(), original.contextTokens());
        assertThat(new InferenceEndpointConfig(settings).apiKey()).isEqualTo("fixture-key");
        Files.writeString(key, "invalid secret key");
        assertThatThrownBy(() -> new InferenceEndpointConfig(settings)).hasMessage("추론 endpoint 설정이 잘못되었습니다");
        assertThat(settings.toString()).doesNotContain("api.key");
    }
    static LlmSettings copy(LlmSettings s, String key, Duration timeout, int context) {
        return new LlmSettings(s.baseUrl(),s.generationModel(),s.embeddingModel(),s.temperature(),context,timeout,key,s.tokenCounting(),s.budgets(),s.evidenceTokens(),s.generationConcurrency());
    }
    @Test void rejectsWindowOverflowAndTimeoutOutsideOneToSixtySeconds() {
        var s=InferenceFixtures.settings("http://127.0.0.1:1/v1");
        assertThatThrownBy(() -> copy(s,null,Duration.ofSeconds(60),8191)).isInstanceOf(IllegalArgumentException.class);
        for (Duration timeout : new Duration[]{Duration.ZERO,Duration.ofMillis(999),Duration.ofSeconds(61)})
            assertThatThrownBy(() -> copy(s,null,timeout,8192)).isInstanceOf(IllegalArgumentException.class);
    }
}
