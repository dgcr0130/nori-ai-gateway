package com.nori.ai.gateway.core.llm;

import java.util.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;

/** 모델은 공개된 의도와 서버 대상 후보만 고르며 인자나 시간을 작성하지 않습니다. */
final class ClassificationOutputSchema {
    private ClassificationOutputSchema() {}

    static StructuredOutput create(List<ClassificationIntent> visible, List<String> candidates) {
        var subjects = new ArrayList<Object>(candidates); subjects.add(null);
        var intentSchema = visible.isEmpty() ? Map.<String,Object>of("not", Map.of())
            : Map.<String,Object>of("type", "string", "enum", visible.stream().map(ClassificationIntent::id).toList());
        var need = object(Map.of("intent", intentSchema, "subject", Map.of("enum", subjects)));
        return new StructuredOutput(SchemaContracts.JSON.writeValueAsString(object(Map.of("needs",
            Map.of("type", "array", "minItems", 1, "maxItems", 4, "items", need)))));
    }

    private static Map<String,Object> object(Map<String,?> properties) {
        return Map.of("type", "object", "properties", properties, "required", properties.keySet().stream().sorted().toList(), "additionalProperties", false);
    }
}
