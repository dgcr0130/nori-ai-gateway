package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.util.Objects;

/** 요청 하나에 결속한 제약입니다. 원문은 로그와 예외의 문자열 표현에 싣지 않습니다. */
public record StructuredOutput(String schema) {
    public StructuredOutput {
        Objects.requireNonNull(schema);
        schema = SchemaContracts.canonicalize(schema);
        SchemaContracts.validateSchema(SchemaContracts.parse(schema));
    }

    public boolean accepts(String content) {
        try {
            var strict = SchemaContracts.JSON.rebuild().enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
            return SchemaContracts.matches(SchemaContracts.parse(schema), strict.readTree(content));
        } catch (RuntimeException invalid) { return false; }
    }

    @Override public String toString() { return "StructuredOutput[보호됨]"; }
}
