package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.core.feedback.FeedbackFailure;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 목록의 마지막 공개 피드백 ID·시각을 시스템과 state에 서명 결속합니다. */
public final class ReviewCursor {
    private final byte[] key=new byte[32];
    public ReviewCursor(){new SecureRandom().nextBytes(key);}
    public record Position(Instant time,UUID id){}
    public String encode(VerifiedSystemScope scope,String state,Instant time,UUID id){String payload=time+"\n"+id;return encode(payload.getBytes(StandardCharsets.UTF_8))+"."+encode(sign(scope,state,payload));}
    public Position decode(VerifiedSystemScope scope,String state,String cursor) {
        if(cursor==null||cursor.isEmpty())return null;
        try {
            if(cursor.length()>1024)throw FeedbackFailure.invalid();var parts=cursor.split("\\.",-1);if(parts.length!=2)throw FeedbackFailure.invalid();
            String payload=new String(Base64.getUrlDecoder().decode(parts[0]),StandardCharsets.UTF_8);
            if(!MessageDigest.isEqual(sign(scope,state,payload),Base64.getUrlDecoder().decode(parts[1])))throw FeedbackFailure.invalid();
            var values=payload.split("\n",-1);if(values.length!=2)throw FeedbackFailure.invalid();return new Position(Instant.parse(values[0]),UUID.fromString(values[1]));
        }catch(RuntimeException invalid){throw FeedbackFailure.invalid();}
    }
    private static String encode(byte[] bytes){return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
    private byte[] sign(VerifiedSystemScope scope,String state,String payload) {
        try{var mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(key,"HmacSHA256"));return mac.doFinal((scope.boundSystem().name()+"\n"+state+"\n"+payload).getBytes(StandardCharsets.UTF_8));}
        catch(GeneralSecurityException impossible){throw new IllegalStateException("검토 커서를 처리할 수 없습니다");}
    }
}
