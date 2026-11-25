package com.nori.ai.gateway.core.llm;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WidgetPresentationContractTest {
    @Test void fallbackPreservesInsufficiencyWithoutSendingUsersToRemovedPanels() {
        var answer = GroundedAnswerAssembly.fallback(List.of());
        assertThat(answer.kind()).isEqualTo("PARTIAL");
        assertThat(answer.text()).contains("정리하지 못했습니다", "다시 질문").doesNotContain("참고 자료", "근거 원문");
    }
    @Test void compositionDoesNotAssumeAnAuxiliaryEvidencePanel() throws Exception {
        try (var stream = getClass().getResourceAsStream("/prompts/compose-v2.txt")) {
            assertThat(stream).isNotNull();
            var prompt = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(prompt).doesNotContain("참고 자료로 함께 제공").contains("질문에 필요한");
        }
    }
}
