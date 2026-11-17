package com.nori.ai.gateway.integration.tokenizer;

import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.budget.ChatMessage.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 서버와 대조할 렌더 문자열은 모델 분기 없이 설정 자료만으로 조립합니다. */
public final class ConfiguredChatTemplateRenderer {
    public record Template(String systemPrefix, String userPrefix, String assistantPrefix, String messageSuffix,
            String sameRoleSeparator, Boolean mergeSameRole, String generationPrefix, String systemContentSuffix, String contentSuffix) {
        public Template {
            Objects.requireNonNull(systemPrefix); Objects.requireNonNull(userPrefix); Objects.requireNonNull(assistantPrefix);
            Objects.requireNonNull(messageSuffix); Objects.requireNonNull(sameRoleSeparator); Objects.requireNonNull(mergeSameRole);
            Objects.requireNonNull(generationPrefix); Objects.requireNonNull(systemContentSuffix); Objects.requireNonNull(contentSuffix);
        }
    }
    private final Template template;
    public ConfiguredChatTemplateRenderer(Template template) { this.template = Objects.requireNonNull(template); }

    /** 준비 요청의 user 한 개도 같은 목록 그대로 렌더합니다. */
    public String render(List<ChatMessage> messages) {
        List<ChatMessage> merged = normalize(messages);
        StringBuilder text = new StringBuilder();
        for (ChatMessage message : merged) {
            if (message.role() == Role.SYSTEM && message.content().isEmpty()) continue;
            text.append(switch (message.role()) {
                case SYSTEM -> template.systemPrefix(); case USER -> template.userPrefix(); case ASSISTANT -> template.assistantPrefix();
            }).append(message.content()).append(message.role() == Role.SYSTEM ? template.systemContentSuffix() : template.contentSuffix())
                .append(template.messageSuffix());
        }
        return text.append(template.generationPrefix()).toString();
    }

    /** 전송과 계수는 동일한 역할 병합을 사용하여 서버의 목록 해석 차이를 없앱니다. */
    public List<ChatMessage> normalize(List<ChatMessage> messages) {
        if (messages.isEmpty() || messages.getLast().role() != Role.USER) throw unsupported();
        if (messages.getFirst().role() != Role.SYSTEM && !(messages.size() == 1 && messages.getFirst().role() == Role.USER)) throw unsupported();
        if (messages.size() > 1 && messages.get(1).role() != Role.USER) throw unsupported();
        List<ChatMessage> result = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage message = messages.get(i);
            if (i > 0 && message.role() == Role.SYSTEM) throw unsupported();
            if (!result.isEmpty() && result.getLast().role() == message.role() && template.mergeSameRole()) {
                ChatMessage last = result.removeLast();
                result.add(new ChatMessage(last.role(), last.content() + template.sameRoleSeparator() + message.content()));
            } else result.add(message);
        }
        return List.copyOf(result);
    }
    private static IllegalArgumentException unsupported() { return new IllegalArgumentException("CHAT_TEMPLATE_INPUT_UNSUPPORTED"); }
}
