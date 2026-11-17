package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer.Template;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

/** 모델 이름과 예산·계수 자료를 하나의 기동 설정으로 검증합니다. */
public record LlmSettings(String baseUrl, String generationModel, String embeddingModel, Double temperature, Integer contextTokens,
        Duration requestTimeout, String apiKeyFile, TokenCounting tokenCounting, Budgets budgets, Integer evidenceTokens, Integer generationConcurrency) {
    public record TokenCounting(String mode, String tokenizerFile, String sha256, Template template) {
        public TokenCounting {
            if (!"hf".equals(mode) || tokenizerFile == null || tokenizerFile.isBlank() || sha256 == null || !sha256.matches("[0-9a-fA-F]{64}")) throw invalid();
            Objects.requireNonNull(template);
        }
    }
    public record Budget(Integer inputTokens, Integer answerTokens, Integer thinkingTokens, Integer reserveTokens) {
        public TokenBudgetPolicy.CallBudget value() {
            if (inputTokens == null || answerTokens == null || thinkingTokens == null || reserveTokens == null) throw invalid();
            return new TokenBudgetPolicy.CallBudget(inputTokens, answerTokens, thinkingTokens, reserveTokens);
        }
    }
    public record Budgets(Budget classify, Budget compose) {}
    public LlmSettings {
        if (baseUrl == null || generationModel == null || generationModel.isBlank() || embeddingModel == null || embeddingModel.isBlank()
                || temperature == null || !Double.isFinite(temperature) || temperature < 0 || temperature > 2 || contextTokens == null || contextTokens < 1
                || requestTimeout == null || requestTimeout.compareTo(Duration.ofSeconds(1)) < 0 || requestTimeout.compareTo(Duration.ofSeconds(60)) > 0
                || evidenceTokens == null || generationConcurrency == null || generationConcurrency != 1 || tokenCounting == null || budgets == null
                || budgets.classify() == null || budgets.compose() == null || apiKeyFile != null && apiKeyFile.isBlank()) throw invalid();
        policy(contextTokens, evidenceTokens, budgets);
    }
    public TokenBudgetPolicy policy() { return policy(contextTokens, evidenceTokens, budgets); }
    private static TokenBudgetPolicy policy(int context, int evidence, Budgets budgets) {
        return new TokenBudgetPolicy(context, evidence, Map.of(CallType.CLASSIFY, budgets.classify().value(), CallType.PLAN, budgets.classify().value(), CallType.COMPOSER, budgets.compose().value()));
    }
    public static LlmSettings load(Environment environment) {
        return Binder.get(environment).bind("ai.gateway.llm", Bindable.of(LlmSettings.class)).orElseThrow(LlmSettings::invalid);
    }
    private static IllegalArgumentException invalid() { return new IllegalArgumentException("LLM 설정이 잘못되었습니다"); }
    @Override public String toString() { return "LlmSettings[보호됨]"; }
}
