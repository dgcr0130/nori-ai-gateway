package com.nori.ai.gateway.integration.testing;

import com.nori.ai.gateway.integration.inference.LlmSettings;
import com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer;
import java.time.Duration;

/** 초기 설정을 사용하는 오프라인 회귀용 자료이며 운영 기본값을 제공하지 않습니다. */
public final class InferenceFixtures {
    private InferenceFixtures() {}
    public static final String TEMPLATE_SHA256 = "2d54db2b9bb29ce7db54fea63a891f5859603813c555b1f88b5e0994652897f9";
    public static ConfiguredChatTemplateRenderer.Template template() {
        return new ConfiguredChatTemplateRenderer.Template("<|im_start|>system\n", "<|im_start|>user\n", "<|im_start|>assistant\n",
            "<|im_end|>\n", "\n\n", true, "<|im_start|>assistant\n<think>\n", "\n\n", "");
    }
    public static ConfiguredChatTemplateRenderer renderer() { return new ConfiguredChatTemplateRenderer(template()); }
    public static LlmSettings settings(String baseUrl) {
        return new LlmSettings(baseUrl, "qwen3:4b", "bge-m3", 0.0, 8192, Duration.ofSeconds(60), null,
            new LlmSettings.TokenCounting("hf", System.getProperty("aigw.rootDir", ".") + "/modules/adapter/ai-gateway-inference-adapter/src/main/resources/tokenizer/qwen3-4b-thinking-2507.tokenizer.json",
                "aeb13307a71acd8fe81861d94ad54ab689df773318809eed3cbe794b4492dae4", template()),
            new LlmSettings.Budgets(new LlmSettings.Budget(5120,768,1280,1024), new LlmSettings.Budget(4608,1536,1536,512)),3072,1);
    }
    public static java.util.Map<String,Object> properties() {
        var values=new java.util.LinkedHashMap<String,Object>();
        values.put("ai.gateway.llm.baseUrl","http://127.0.0.1:1/v1");
        values.put("ai.gateway.llm.generationModel","qwen3:4b");values.put("ai.gateway.llm.embeddingModel","bge-m3");
        values.put("ai.gateway.llm.temperature",0.0);values.put("ai.gateway.llm.contextTokens",8192);
        values.put("ai.gateway.llm.requestTimeout","60s");values.put("ai.gateway.llm.generationConcurrency",1);values.put("ai.gateway.llm.evidenceTokens",3072);
        var settings=settings("http://127.0.0.1:1/v1");
        values.put("ai.gateway.llm.tokenCounting.mode","hf");values.put("ai.gateway.llm.tokenCounting.tokenizerFile",settings.tokenCounting().tokenizerFile());
        values.put("ai.gateway.llm.tokenCounting.sha256",settings.tokenCounting().sha256());
        var t=template();
        values.put("ai.gateway.llm.tokenCounting.template.systemPrefix",t.systemPrefix());values.put("ai.gateway.llm.tokenCounting.template.userPrefix",t.userPrefix());
        values.put("ai.gateway.llm.tokenCounting.template.assistantPrefix",t.assistantPrefix());values.put("ai.gateway.llm.tokenCounting.template.messageSuffix",t.messageSuffix());
        values.put("ai.gateway.llm.tokenCounting.template.sameRoleSeparator",t.sameRoleSeparator());values.put("ai.gateway.llm.tokenCounting.template.mergeSameRole",t.mergeSameRole());
        values.put("ai.gateway.llm.tokenCounting.template.generationPrefix",t.generationPrefix());values.put("ai.gateway.llm.tokenCounting.template.systemContentSuffix",t.systemContentSuffix());
        values.put("ai.gateway.llm.tokenCounting.template.contentSuffix",t.contentSuffix());
        for(var entry:java.util.Map.of("classify",settings.budgets().classify(),"compose",settings.budgets().compose()).entrySet()) {
            String prefix="ai.gateway.llm.budgets."+entry.getKey()+".";var b=entry.getValue();
            values.put(prefix+"inputTokens",b.inputTokens());values.put(prefix+"answerTokens",b.answerTokens());values.put(prefix+"thinkingTokens",b.thinkingTokens());values.put(prefix+"reserveTokens",b.reserveTokens());
        }
        return values;
    }
}
