package com.nori.ai.gateway.app.config;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WidgetHelpPromptTest {
    @Test void helpExplainsAvailableFunctionsInTheAnswerBody() throws Exception {
        try (var stream = getClass().getResourceAsStream("/prompts/compose-v1.txt")) {
            assertThat(stream).isNotNull();
            var prompt = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(prompt).contains("답변 본문", "필요한 입력", "목록에 없는 기능이나 사실을 만들지 않고")
                .doesNotContain("답변 아래", "표가 표시");
        }
    }
}
