package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 서버 발급 커서는 사용자·시스템에 결속하며 재기동 후에는 다시 첫 쪽에서 시작합니다. */
public final class ConversationCursor {
    private final byte[] key = new byte[32];
    public ConversationCursor() { new SecureRandom().nextBytes(key); }
    public record Position(Instant updatedAt, String id) {}
    public String encode(VerifiedSystemScope scope, Instant updatedAt, String id) {
        String payload = updatedAt + "\n" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8)) + "."
            + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(scope,payload));
    }
    public Position decode(VerifiedSystemScope scope, String cursor) {
        if (cursor == null || cursor.isEmpty()) return null;
        try {
            if (cursor.length() > 1024) throw new IllegalArgumentException();
            var parts = cursor.split("\\.", -1);
            if (parts.length != 2) throw new IllegalArgumentException();
            String payload = new String(Base64.getUrlDecoder().decode(parts[0]),StandardCharsets.UTF_8);
            if (!MessageDigest.isEqual(sign(scope,payload),Base64.getUrlDecoder().decode(parts[1]))) throw new IllegalArgumentException();
            var fields = payload.split("\n", -1);
            if (fields.length != 2 || fields[1].isBlank()) throw new IllegalArgumentException();
            return new Position(Instant.parse(fields[0]),fields[1]);
        } catch (RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ConversationCursor.class.getName()),"CONVERSATION_CURSOR_FAILED",failure); throw new IllegalArgumentException("대화 커서가 잘못되었습니다"); }
    }
    private byte[] sign(VerifiedSystemScope scope, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(key,"HmacSHA256"));
            return mac.doFinal((scope.boundSystem().name() + "\n" + scope.actorRef().value() + "\n" + payload).getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ConversationCursor.class.getName()),"CONVERSATION_CURSOR_FAILED",failure); throw new IllegalStateException("대화 커서를 처리할 수 없습니다"); }
    }
}
