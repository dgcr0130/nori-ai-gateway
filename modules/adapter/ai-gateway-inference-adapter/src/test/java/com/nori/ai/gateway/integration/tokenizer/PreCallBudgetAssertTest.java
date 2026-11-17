package com.nori.ai.gateway.integration.tokenizer;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.budget.ChatMessage.Role;
import com.nori.ai.gateway.core.budget.PreCallBudgetAssert;
import com.nori.ai.gateway.core.budget.PreCallBudgetAssert.Allowed;
import com.nori.ai.gateway.core.budget.PreCallBudgetAssert.Exceeded;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy.CallBudget;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy.Violation;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 실제 chat template 렌더 + pin tokenizer 계수로 만든 정확한 토큰 수의 입력에서 호출 직전 assert 경계를 확인한다(계수기 fake 0). */
class PreCallBudgetAssertTest {
    private static HfTokenizerCounter tokenizer;
    private static PromptTokenCounter counter;
    @BeforeAll static void load() { tokenizer = new HfTokenizerCounter(); counter = new PromptTokenCounter(com.nori.ai.gateway.integration.testing.InferenceFixtures.renderer(), tokenizer); }
    @AfterAll static void close() { tokenizer.close(); }

    /** 렌더 전체 계수가 정확히 {@code target} 인 system + user 메시지. " token" 하나가 한 토큰이라 개수로 맞춘 뒤 실제 계수로 다시 확인한다. */
    private static List<ChatMessage> messagesOf(int target) {
        int base = counter.count(input(0));
        List<ChatMessage> messages = input(target - base);
        assertThat(counter.count(messages)).as("입력 구성 계수").isEqualTo(target);
        return messages;
    }
    private static List<ChatMessage> input(int fillers) {
        return List.of(new ChatMessage(Role.SYSTEM, "TC 질문을 분류한다."), new ChatMessage(Role.USER, "질문:" + " token".repeat(fillers)));
    }
    private static TokenBudgetPolicy composerContextFixture() {
        var current = TokenBudgetPolicy.current();
        return new TokenBudgetPolicy(Map.of(CallType.CLASSIFY, current.budget(CallType.CLASSIFY), CallType.PLAN, current.budget(CallType.PLAN),
            CallType.COMPOSER, new CallBudget(4_608, 1_536, 1_537, 512)));
    }

    @Test void classifyAndPlanAllowInputLimitPlusMaxTokensExactlyAtTheContextWindow() {
        var guard = new PreCallBudgetAssert(counter, TokenBudgetPolicy.current());
        for (CallType type : new CallType[]{CallType.CLASSIFY, CallType.PLAN}) {
            assertThat(guard.check(type, messagesOf(5_120), 2_048)).isEqualTo(new Allowed(5_120));
            assertThat(guard.check(type, messagesOf(5_121), 2_048)).isEqualTo(new Exceeded(5_121, java.util.Set.of(Violation.INPUT_LIMIT, Violation.CONTEXT_WINDOW)));
        }
    }

    @Test void composerInputAndReserveRejectTheSuccessor4609() {
        var guard = new PreCallBudgetAssert(counter, TokenBudgetPolicy.current());
        assertThat(guard.check(CallType.COMPOSER, messagesOf(4_608), 3_072)).isEqualTo(new Allowed(4_608));
        // 4,609 + 3,072 + reserve 512 = 8,193: 입력과 컨텍스트 상한을 함께 위반한다.
        assertThat(guard.check(CallType.COMPOSER, messagesOf(4_609), 3_072)).isEqualTo(new Exceeded(4_609, java.util.Set.of(Violation.INPUT_LIMIT, Violation.CONTEXT_WINDOW)));
    }

    @Test void contextSumIsCheckedIndependentlyWithTheTestOnlyComposerFixture() {
        var guard = new PreCallBudgetAssert(counter, composerContextFixture());
        assertThat(guard.check(CallType.COMPOSER, messagesOf(4_607), 3_073)).isEqualTo(new Allowed(4_607));
        // 두 입력 모두 입력 상한 4,608 이하이며 4,608 + 3,073 + reserve 512만 컨텍스트를 넘는다.
        assertThat(guard.check(CallType.COMPOSER, messagesOf(4_608), 3_073)).isEqualTo(new Exceeded(4_608, java.util.Set.of(Violation.CONTEXT_WINDOW)));
    }

    @Test void requestMaxTokensOtherThanThePolicyValueIsRejected() {
        var guard = new PreCallBudgetAssert(counter, TokenBudgetPolicy.current());
        var messages = input(1);
        assertThatThrownBy(() -> guard.check(CallType.CLASSIFY, messages, 5_121)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> guard.check(CallType.COMPOSER, messages, 5_120)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> guard.check(CallType.CLASSIFY, List.of(new ChatMessage(Role.USER, "U"), new ChatMessage(Role.ASSISTANT, "A")), 2_048))
            .hasMessage("CHAT_TEMPLATE_INPUT_UNSUPPORTED");
    }
}
