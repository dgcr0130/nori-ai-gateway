package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.io.Json;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.time.Clock;
import java.util.*;

/** Gateway에는 서명기를 추가하지 않습니다. JCA ES256으로 매 HTTP 요청을 새로 서명합니다. */
public final class JwkAssertionSigner implements GatewayClient.Assertions {
    private final PrivateKey key;private final String kid,issuer,system,subject;private final Set<String> granted;private final Clock clock;
    public JwkAssertionSigner(JsonNode jwk,String issuer,String system,String subject,List<String> granted,Clock clock) {
        try {
            if(!jwk.path("kty").asText().equals("EC")||!jwk.path("crv").asText().equals("P-256")
                ||jwk.has("alg")&&!jwk.path("alg").asText().equals("ES256")||jwk.has("use")&&!jwk.path("use").asText().equals("sig"))throw new IllegalArgumentException();
            this.kid=QuestionSet.text(jwk,"kid");
            var params=AlgorithmParameters.getInstance("EC");params.init(new ECGenParameterSpec("secp256r1"));var spec=params.getParameterSpec(ECParameterSpec.class);
            var factory=KeyFactory.getInstance("EC");this.key=factory.generatePrivate(new ECPrivateKeySpec(number(jwk,"d"),spec));
            var publicKey=factory.generatePublic(new ECPublicKeySpec(new ECPoint(number(jwk,"x"),number(jwk,"y")),spec));
            byte[] probe="평가 서명 키 대조".getBytes(StandardCharsets.UTF_8);byte[] signature=signature(probe);
            var verifier=Signature.getInstance("SHA256withECDSAinP1363Format");verifier.initVerify(publicKey);verifier.update(probe);
            if(!verifier.verify(signature)||issuer.isBlank()||issuer.length()>128||!system.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,63}")
                    ||!subject.matches("[A-Za-z0-9_-]{1,64}"))throw new IllegalArgumentException();
            for(String permission:granted)if(!permission.matches("[A-Z0-9_]{1,64}"))throw new IllegalArgumentException();
            this.issuer=issuer;this.system=system;this.subject=subject;this.granted=Set.copyOf(granted);this.clock=clock;
        }catch(Exception invalid){throw GatewayClient.environment();}
    }
    @Override public String sign(List<String> permissions,boolean job) {
        if(!granted.containsAll(permissions))throw GatewayClient.environment();
        var sorted=permissions.stream().distinct().sorted().toList();
        var claims=Json.MAPPER.createObjectNode().put("iss",issuer).put("aud","ai-gateway").put("sys",system).put("sub",subject).put("scope","READ");
        claims.set("perm",Json.MAPPER.valueToTree(sorted));claims.put("pdg",Json.sha(String.join("\n",sorted).getBytes(StandardCharsets.UTF_8)));
        long now=clock.instant().getEpochSecond();claims.put("iat",now).put("exp",now+60);
        if(job)claims.put("dlg",UUID.randomUUID().toString().replace("-",""));
        String input=encode(Json.canonical(Json.MAPPER.valueToTree(Map.of("alg","ES256","kid",kid))))+"."+encode(Json.canonical(claims));
        try{return input+"."+encode(signature(input.getBytes(StandardCharsets.US_ASCII)));}catch(GeneralSecurityException failure){throw GatewayClient.environment();}
    }
    private byte[] signature(byte[] input) throws GeneralSecurityException {
        var signer=Signature.getInstance("SHA256withECDSAinP1363Format");signer.initSign(key);signer.update(input);return signer.sign();
    }
    private static BigInteger number(JsonNode jwk,String key) {
        String encoded=QuestionSet.text(jwk,key);byte[] bytes=Base64.getUrlDecoder().decode(encoded);
        if(bytes.length!=32||!encode(bytes).equals(encoded))throw new IllegalArgumentException();return new BigInteger(1,bytes);
    }
    private static String encode(byte[] bytes){return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
    @Override public String toString(){return "평가 서명기[보호됨]";}
}
