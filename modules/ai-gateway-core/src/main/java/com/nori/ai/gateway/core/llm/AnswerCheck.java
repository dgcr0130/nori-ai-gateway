package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.port.InferenceClient;

/**
 * 분류·Composer 공통 답변 판정(SPEC §14.5 검사 ③④ "답변 상한 초과 = 실패", 열린 항목 ⓓ 2026-11-10 결정).
 * 오류 코드는 F0 하네스 ClassificationJudge 의 고정 코드와 같다(재시도 요지는 답변 판정 두 코드를 USAGE_EXCEEDS_LIMIT 로 보낸다).
 */
public final class AnswerCheck {
    static final String ANSWER_LIMIT_EXCEEDED = "ANSWER_LIMIT_EXCEEDED", ANSWER_TOKENS_UNCOUNTABLE = "ANSWER_TOKENS_UNCOUNTABLE";
    private AnswerCheck() {}

    /** 실패면 고정 코드, 통과면 null. content 는 판정에만 쓰며 고치지 않는다. */
    public static String failure(InferenceClient.Completed completed, TokenBudgetPolicy.CallBudget budget, AnswerTokenCounting answers) {
        if (completed.finishReason().equals("length")) return "FINISH_LENGTH";
        if (!completed.finishReason().equals("stop")) return "FINISH_REASON_INVALID";
        if (completed.completionTokens() < 0 || completed.completionTokens() > budget.requestMaxTokens()) return "USAGE_EXCEEDS_LIMIT";
        // 사고 표지가 든 content 는 답변 토큰을 셀 수 없는 것과 같은 실패다(표지 앞뒤를 잘라 고치지 않는다).
        if (completed.content().contains("<think>") || completed.content().contains("</think>")) return ANSWER_TOKENS_UNCOUNTABLE;
        final int answer;
        try { answer = answers.count(completed.content()); }
        catch (IllegalArgumentException uncountable) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(AnswerCheck.class.getName()),"ANSWER_CHECK_FAILED",uncountable); return ANSWER_TOKENS_UNCOUNTABLE; }
        if (answer < 0) return ANSWER_TOKENS_UNCOUNTABLE;
        if (answer > budget.answerLimit()) return ANSWER_LIMIT_EXCEEDED;
        // 공개 본문보다 작은 총 사용량은 모순이며, 분리 가능한 나머지만 사고 사용량으로 판정합니다.
        if (completed.completionTokens() < answer) return "USAGE_EXCEEDS_LIMIT";
        return completed.completionTokens() - answer > budget.thinkingLimit() ? "USAGE_EXCEEDS_LIMIT" : null;
    }

    static String retryCode(String failure) {
        return failure.equals(ANSWER_LIMIT_EXCEEDED) || failure.equals(ANSWER_TOKENS_UNCOUNTABLE) ? "USAGE_EXCEEDS_LIMIT" : failure;
    }
}
