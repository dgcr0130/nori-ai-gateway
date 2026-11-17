package com.nori.ai.gateway.app.hooks;

import com.nori.ai.gateway.integration.inference.InferenceReadinessService;
import com.nori.ai.gateway.integration.inference.OpenAiReadinessProbe;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

/** 공통 API 준비 상태의 시험 관측값을 주입합니다. OFF는 실제 최소 요청 검사를 다시 실행합니다. */
@RestController
@RequestMapping("/__test")
@Profile("verify")
@ConditionalOnProperty(name = "ai.gateway.test-hooks.enabled", havingValue = "true")
@ConditionalOnProperty(name = "ai.gateway.inference.mode", havingValue = "enabled")
public final class ReadinessStubHook {
    private static final Set<String> FIELDS = Set.of("kind");
    private final InferenceReadinessService readiness;

    public ReadinessStubHook(InferenceReadinessService readiness) { this.readiness = readiness; }

    @PostMapping("/readiness-stub")
    public ResponseEntity<Map<String,String>> stub(@RequestBody JsonNode body) {
        if (!body.isObject() || !FIELDS.equals(Set.copyOf(body.propertyNames())) || !body.get("kind").isString()) return ResponseEntity.badRequest().build();
        switch (body.get("kind").asString()) {
            case "MODEL_LOADING" -> readiness.overrideSource(() -> new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.MODEL_LOADING, OpenAiReadinessProbe.Reason.NONE, null));
            case "INFERENCE_UNAVAILABLE" -> readiness.overrideSource(() -> new OpenAiReadinessProbe.Observation(OpenAiReadinessProbe.State.INFERENCE_UNAVAILABLE, OpenAiReadinessProbe.Reason.NO_RESPONSE, null));
            case "OFF" -> readiness.overrideSource(null);
            default -> { return ResponseEntity.badRequest().build(); }
        }
        return ResponseEntity.ok(Map.of("state", readiness.refresh().state().name()));
    }
}
