package com.nori.ai.gateway.core.budget;

/**
 * 서버 잘림 2차 방어(SPEC §14.5): 응답 {@code usage.prompt_tokens} 가 Gateway 계수와 다르면 그 호출은 실패다.
 * G-USAGE 진단(Dev report §12.32)에서 warm·공유 접두부의 usage 와 계수가 같았으므로 규칙을 그대로 적용한다.
 * 크게 보고된 경우도 서버와 template가 일치하지 않는 계수 실패다.
 */
public final class TruncationDetector {
    public enum Result { OK, TRUNCATED }

    public Result check(int count, int usagePromptTokens) {
        if (count < 1 || usagePromptTokens < 0) throw new IllegalArgumentException("토큰 수가 올바르지 않습니다");
        return usagePromptTokens != count ? Result.TRUNCATED : Result.OK;
    }
}
