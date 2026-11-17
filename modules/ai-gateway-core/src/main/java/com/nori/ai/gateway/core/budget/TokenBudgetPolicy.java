package com.nori.ai.gateway.core.budget;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 호출 유형별 입력·답변·사고 상한을 한 곳에 둔다. 요청 {@code max_tokens} = 답변 상한 + 사고 상한이다.
 * 운영값은 설정에서 주입하며 current()는 초기 예산과 시험 fixture를 제공한다.
 */
public final class TokenBudgetPolicy {
    /** 초기 컨텍스트 크기. 운영 사전 검사는 설정의 contextTokens를 사용한다. */
    public static final int CONTEXT_TOKENS = 8192;

    public enum Violation { INPUT_LIMIT, CONTEXT_WINDOW }

    public record CallBudget(int inputLimit, int answerLimit, int thinkingLimit, int reserveTokens) {
        public CallBudget(int inputLimit, int answerLimit, int thinkingLimit) { this(inputLimit, answerLimit, thinkingLimit, 1); }
        public CallBudget {
            if (inputLimit < 1 || answerLimit < 1 || thinkingLimit < 0 || reserveTokens < 1
                    || (long) answerLimit + thinkingLimit > Integer.MAX_VALUE) throw new IllegalArgumentException("호출 예산 상한이 올바르지 않습니다");
        }
        public int requestMaxTokens() { return answerLimit + thinkingLimit; }
    }

    private final Map<CallType, CallBudget> budgets;
    private final int contextTokens;
    private final int evidenceTokens;

    public TokenBudgetPolicy(Map<CallType, CallBudget> budgets) {
        this(CONTEXT_TOKENS, Math.min(3072, budgets.getOrDefault(CallType.COMPOSER, new CallBudget(3072,1,0)).inputLimit()), budgets, false);
    }

    public TokenBudgetPolicy(int contextTokens, int evidenceTokens, Map<CallType, CallBudget> budgets) {
        this(contextTokens, evidenceTokens, budgets, true);
    }

    private TokenBudgetPolicy(int contextTokens, int evidenceTokens, Map<CallType, CallBudget> budgets, boolean validateWindow) {
        Objects.requireNonNull(budgets, "budgets");
        if (!budgets.keySet().equals(EnumSet.allOf(CallType.class))) throw new IllegalArgumentException("모든 호출 유형의 예산이 필요합니다");
        this.budgets = new EnumMap<>(budgets);
        if (contextTokens < 1 || evidenceTokens < 1 || evidenceTokens > budgets.get(CallType.COMPOSER).inputLimit())
            throw new IllegalArgumentException("컨텍스트·근거 예산이 올바르지 않습니다");
        if (validateWindow && budgets.values().stream().anyMatch(b -> (long)b.inputLimit() + b.answerLimit() + b.thinkingLimit() + b.reserveTokens() > contextTokens))
            throw new IllegalArgumentException("호출 예산이 컨텍스트를 초과합니다");
        this.contextTokens = contextTokens;
        this.evidenceTokens = evidenceTokens;
    }

    /** 006 초기 분류·작성 예산. PLAN은 제거 전까지 분류 예산을 공유한다. */
    public static TokenBudgetPolicy current() {
        return new TokenBudgetPolicy(Map.of(
            CallType.CLASSIFY, new CallBudget(5_120, 768, 1_280, 1_024),
            CallType.PLAN, new CallBudget(5_120, 768, 1_280, 1_024),
            CallType.COMPOSER, new CallBudget(4_608, 1_536, 1_536, 512)));
    }

    public CallBudget budget(CallType type) { return budgets.get(Objects.requireNonNull(type, "type")); }
    public int contextTokens() { return contextTokens; }
    public int evidenceTokens() { return evidenceTokens; }

    /** 호출 직전 두 조건(입력 ≤ 유형 상한, 입력 + max_tokens + reserve ≤ contextTokens) 중 어긴 것. 비어 있으면 호출할 수 있다. */
    public Set<Violation> violations(CallType type, int inputTokens, int maxTokens) {
        if (inputTokens < 0 || maxTokens < 1) throw new IllegalArgumentException("토큰 수가 올바르지 않습니다");
        EnumSet<Violation> violations = EnumSet.noneOf(Violation.class);
        if (inputTokens > budget(type).inputLimit()) violations.add(Violation.INPUT_LIMIT);
        if ((long) inputTokens + maxTokens + budget(type).reserveTokens() > contextTokens) violations.add(Violation.CONTEXT_WINDOW);
        return Set.copyOf(violations);
    }
}
