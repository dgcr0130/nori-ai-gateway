package com.nori.ai.gateway.integration.inference;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;

/** API 접두 경로를 포함한 주소를 그대로 사용하며 자격 원문은 출력하지 않습니다. */
@ConditionalOnProperty(name = "ai.gateway.inference.mode", havingValue = "enabled")
public final class InferenceEndpointConfig {
    public static final String BASE_URL_PROPERTY = "ai.gateway.llm.baseUrl";
    private final URI baseUrl;
    private final LlmSettings settings;
    private final String apiKey;
    @Autowired public InferenceEndpointConfig(Environment environment) { this(LlmSettings.load(environment)); }
    public InferenceEndpointConfig(LlmSettings settings) {
        this.settings = settings;
        try {
            URI uri = new URI(settings.baseUrl());
            if (!Set.of("http", "https").contains(uri.getScheme()) || uri.getHost() == null || uri.getPort() < 1 || uri.getPort() > 65535
                || uri.getRawUserInfo() != null || uri.getRawQuery() != null || uri.getRawFragment() != null
                || uri.getRawPath().isEmpty() || uri.getRawPath().endsWith("/") || uri.getRawPath().contains("..")) throw invalid();
            baseUrl = uri;
            apiKey = settings.apiKeyFile() == null ? null : Files.readString(Path.of(settings.apiKeyFile())).strip();
            if (apiKey != null && (apiKey.isEmpty() || apiKey.codePoints().anyMatch(c -> c <= 32 || c == 127))) throw invalid();
        } catch (Exception failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.credentialFailure(System.getLogger(InferenceEndpointConfig.class.getName()),"INFERENCE_ENDPOINT_CONFIG_FAILED",failure); throw invalid(); }
    }
    private static IllegalStateException invalid() { return new IllegalStateException("추론 endpoint 설정이 잘못되었습니다"); }
    public URI baseUrl() { return baseUrl; }
    public String sdkBaseUrl() { return baseUrl.toString(); }
    public URI readinessUri() { return URI.create(baseUrl + "/models"); }
    public LlmSettings settings() { return settings; }
    String apiKey() { return apiKey; }
    @Override public String toString() { return "InferenceEndpointConfig[보호됨]"; }
}
