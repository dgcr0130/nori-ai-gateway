package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

/**
 * 운영 chat 전송: 비스트리밍 {@link OpenAiChatModel#call(Prompt)} 1회. 요청 옵션은 모델 기본값 전체에 요청별 max_tokens 만 바꾼다
 * (Spring AI 2.0.2 는 Prompt 옵션이 있으면 기본값과 병합하지 않는다). num_ctx 는 싣지 않는다(서버 기본값 사용, SPEC §26.1).
 */
public final class SpringAiChatTransport implements ChatTransport {
    private final OpenAiChatModel model;
    private final com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer renderer;

    public SpringAiChatTransport(OpenAiChatModel model, com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer renderer) { this.model = Objects.requireNonNull(model, "model"); this.renderer = renderer; }

    @Override public Response send(CallType type, List<ChatMessage> messages, int maxTokens) {
        return send(type, messages, maxTokens, model.getOptions().getTimeout());
    }

    @Override public Response send(CallType type, List<ChatMessage> messages, int maxTokens, java.time.Duration timeout) {
        return send(type, messages, maxTokens, timeout, null);
    }

    @Override public Response send(CallType type, List<ChatMessage> messages, int maxTokens, java.time.Duration timeout,
            com.nori.ai.gateway.core.llm.StructuredOutput output) {
        var builder = model.getOptions().mutate().maxTokens(maxTokens).timeout(timeout);
        if (output != null) builder.responseFormat(OpenAiChatModel.ResponseFormat.builder()
            .type(OpenAiChatModel.ResponseFormat.Type.JSON_SCHEMA).jsonSchema(output.schema()).strict(true).build());
        OpenAiChatOptions options = builder.build();
        var response = model.call(new Prompt(renderer.normalize(messages).stream().map(SpringAiChatTransport::message).toList(), options));
        var result = response == null ? null : response.getResult();
        var usage = response == null || response.getMetadata() == null ? null : response.getMetadata().getUsage();
        Integer promptTokens = usage == null ? null : usage.getPromptTokens(), completionTokens = usage == null ? null : usage.getCompletionTokens();
        if (result == null || result.getOutput() == null) return new Response(null, null, promptTokens, completionTokens);
        String finish = result.getMetadata() == null ? null : result.getMetadata().getFinishReason();
        // 사고문은 별도 reasoning 필드로 오며 metadata 에만 있다. 답변은 message.content 만 쓴다(SPEC §14.5 방법 (b)).
        return new Response(result.getOutput().getText(), finish == null ? null : finish.toLowerCase(Locale.ROOT), promptTokens, completionTokens);
    }

    private static Message message(ChatMessage message) {
        return switch (message.role()) {
            case SYSTEM -> new SystemMessage(message.content());
            case USER -> new UserMessage(message.content());
            case ASSISTANT -> new AssistantMessage(message.content());
        };
    }
}
