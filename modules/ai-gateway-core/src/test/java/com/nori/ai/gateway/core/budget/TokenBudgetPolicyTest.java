package com.nori.ai.gateway.core.budget;

import com.nori.ai.gateway.core.budget.TokenBudgetPolicy.CallBudget;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy.Violation;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class TokenBudgetPolicyTest {
    /** 작성 사고 상한을 1,537로 둔 시험 전용 fixture로 컨텍스트 합계를 독립 확인한다. */
    static TokenBudgetPolicy composerContextFixture() {
        var current = TokenBudgetPolicy.current();
        return new TokenBudgetPolicy(Map.of(CallType.CLASSIFY, current.budget(CallType.CLASSIFY), CallType.PLAN, current.budget(CallType.PLAN),
            CallType.COMPOSER, new CallBudget(4_608, 1_536, 1_537, 512)));
    }

    @Test void currentProfileIsSpec145WithRequestMaxTokensAsAnswerPlusThinking() {
        var policy = TokenBudgetPolicy.current();
        assertThat(TokenBudgetPolicy.CONTEXT_TOKENS).isEqualTo(8_192);
        assertThat(policy.budget(CallType.CLASSIFY)).as("SPEC §28.3 분류 wire v3").isEqualTo(new CallBudget(5_120, 768, 1_280, 1_024));
        assertThat(policy.budget(CallType.PLAN)).isEqualTo(new CallBudget(5_120, 768, 1_280, 1_024));
        assertThat(policy.budget(CallType.COMPOSER)).isEqualTo(new CallBudget(4_608, 1_536, 1_536, 512));
        assertThat(policy.budget(CallType.CLASSIFY).requestMaxTokens()).isEqualTo(2_048);
        assertThat(policy.budget(CallType.PLAN).requestMaxTokens()).isEqualTo(2_048);
        assertThat(policy.budget(CallType.COMPOSER).requestMaxTokens()).isEqualTo(3_072);
    }

    @Test void currentProfileSeparatesInputLimitFromContextWindowAtExactBoundaries() {
        var policy = TokenBudgetPolicy.current();
        for (CallType type : new CallType[]{CallType.CLASSIFY, CallType.PLAN}) {
            assertThat(policy.violations(type, 5_120, 2_048)).isEmpty();
            assertThat(policy.violations(type, 5_121, 2_048)).containsExactlyInAnyOrder(Violation.INPUT_LIMIT, Violation.CONTEXT_WINDOW);
        }
        assertThat(policy.violations(CallType.COMPOSER, 4_608, 3_072)).isEmpty();
        // 4,609 + 3,072 + reserve 512 = 8,193이므로 두 상한을 함께 어긴다.
        assertThat(policy.violations(CallType.COMPOSER, 4_609, 3_072)).containsExactlyInAnyOrder(Violation.INPUT_LIMIT, Violation.CONTEXT_WINDOW);
    }

    @Test void fixtureChecksTheContextSumIndependentlyOfTheInputLimit() {
        var fixture = composerContextFixture();
        assertThat(fixture.budget(CallType.COMPOSER).requestMaxTokens()).isEqualTo(3_073);
        assertThat(fixture.violations(CallType.COMPOSER, 4_607, 3_073)).isEmpty();
        assertThat(fixture.violations(CallType.COMPOSER, 4_608, 3_073)).containsExactly(Violation.CONTEXT_WINDOW);
        assertThat(TokenBudgetPolicy.current().budget(CallType.COMPOSER).thinkingLimit()).isEqualTo(1_536);
    }

    @Test void rejectsIncompleteOrInvalidBudgets() {
        var current = TokenBudgetPolicy.current();
        assertThatThrownBy(() -> new TokenBudgetPolicy(Map.of(CallType.CLASSIFY, current.budget(CallType.CLASSIFY)))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallBudget(0, 192, 4_928)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallBudget(5_120, 0, 4_928)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallBudget(5_120, 192, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> current.violations(CallType.CLASSIFY, -1, 5_120)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> current.violations(CallType.CLASSIFY, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThat(Set.copyOf(current.violations(CallType.PLAN, 0, 5_120))).isEmpty();
    }
}
