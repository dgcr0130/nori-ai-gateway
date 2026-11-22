package com.nori.ai.gateway.eval.answer;

import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class JwkAssertionSignerTest {
    static String coordinate(BigInteger value){byte[] raw=value.toByteArray(),bytes=new byte[32];int n=Math.min(raw.length,32);System.arraycopy(raw,raw.length-n,bytes,32-n,n);return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
    @Test void signsEs256P256WithExactClaimsFreshDelegationAndNoNonJobDelegation() throws Exception {
        var generator=KeyPairGenerator.getInstance("EC");generator.initialize(new ECGenParameterSpec("secp256r1"));var pair=generator.generateKeyPair();
        var publicKey=(ECPublicKey)pair.getPublic();var privateKey=(ECPrivateKey)pair.getPrivate();
        var key=Json.MAPPER.createObjectNode().put("kty","EC").put("crv","P-256").put("kid","test-evaluation").put("alg","ES256")
            .put("x",coordinate(publicKey.getW().getAffineX())).put("y",coordinate(publicKey.getW().getAffineY())).put("d",coordinate(privateKey.getS()));
        var signer=new JwkAssertionSigner(key,"tc-ui-backend","TC","eval-user",List.of("AI_REVIEW","AI_DOC_READ"),Clock.fixed(Instant.parse("2026-11-19T00:00:00Z"),ZoneOffset.UTC));
        var assertions=List.of(signer.sign(List.of("AI_REVIEW","AI_DOC_READ"),true),signer.sign(List.of("AI_REVIEW","AI_DOC_READ"),true),signer.sign(List.of("AI_DOC_READ"),false));
        String previous=null;
        for(int i=0;i<assertions.size();i++) {
            String[] parts=assertions.get(i).split("\\.");var header=Json.parse(Base64.getUrlDecoder().decode(parts[0]));var claims=Json.parse(Base64.getUrlDecoder().decode(parts[1]));
            assertThat(header.path("alg").asText()).isEqualTo("ES256");assertThat(header.path("kid").asText()).isEqualTo("test-evaluation");
            var verifier=Signature.getInstance("SHA256withECDSAinP1363Format");verifier.initVerify(publicKey);verifier.update((parts[0]+"."+parts[1]).getBytes(StandardCharsets.US_ASCII));
            assertThat(verifier.verify(Base64.getUrlDecoder().decode(parts[2]))).isTrue();
            assertThat(claims.path("iss").asText()).isEqualTo("tc-ui-backend");assertThat(claims.path("aud").asText()).isEqualTo("ai-gateway");
            assertThat(claims.path("sys").asText()).isEqualTo("TC");assertThat(claims.path("sub").asText()).isEqualTo("eval-user");assertThat(claims.path("scope").asText()).isEqualTo("READ");
            assertThat(claims.path("iat").longValue()).isEqualTo(1795046400L);assertThat(claims.path("exp").longValue()).isEqualTo(1795046460L);
            String material=i<2?"AI_DOC_READ\nAI_REVIEW":"AI_DOC_READ";
            assertThat(claims.path("pdg").asText()).isEqualTo(Json.sha(material.getBytes(StandardCharsets.UTF_8)));
            if(i<2) {String dlg=claims.path("dlg").asText();assertThat(dlg).matches("[A-Za-z0-9_-]{22,128}").isNotEqualTo(previous);previous=dlg;}
            else assertThat(claims.has("dlg")).isFalse();
        }
        assertThatThrownBy(()->signer.sign(List.of("MODEL_READ"),false)).isInstanceOf(EvalException.class);
        key.put("d","PRIVATE_KEY_BODY_MUST_NOT_ESCAPE");
        assertThatThrownBy(()->new JwkAssertionSigner(key,"issuer","TC","eval-user",List.of(),Clock.systemUTC())).isInstanceOf(EvalException.class)
            .hasMessageNotContaining("PRIVATE_KEY_BODY_MUST_NOT_ESCAPE");
    }
    @Test void actualHttpClientSendsVerifiedJwsAndAddsDelegationOnlyToMessageCreation() throws Exception {
        var generator=KeyPairGenerator.getInstance("EC");generator.initialize(new ECGenParameterSpec("secp256r1"));var pair=generator.generateKeyPair();
        var publicKey=(ECPublicKey)pair.getPublic();var privateKey=(ECPrivateKey)pair.getPrivate();
        var key=Json.MAPPER.createObjectNode().put("kty","EC").put("crv","P-256").put("kid","test-http")
            .put("x",coordinate(publicKey.getW().getAffineX())).put("y",coordinate(publicKey.getW().getAffineY())).put("d",coordinate(privateKey.getS()));
        var signer=new JwkAssertionSigner(key,"tc-ui-backend","TC","eval-user",List.of("AI_DOC_READ"),Clock.systemUTC());
        var server=com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(java.net.InetAddress.getLoopbackAddress(),0),0);
        var failure=new java.util.concurrent.atomic.AtomicReference<Throwable>();var requests=new java.util.concurrent.atomic.AtomicInteger();
        server.createContext("/",exchange->{try {
            assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer fixture-service-token");
            String[] parts=exchange.getRequestHeaders().getFirst("X-Actor-Assertion").split("\\.");
            var verifier=Signature.getInstance("SHA256withECDSAinP1363Format");verifier.initVerify(publicKey);verifier.update((parts[0]+"."+parts[1]).getBytes(StandardCharsets.US_ASCII));
            assertThat(verifier.verify(Base64.getUrlDecoder().decode(parts[2]))).isTrue();
            var claims=Json.parse(Base64.getUrlDecoder().decode(parts[1]));String path=exchange.getRequestURI().getPath();
            assertThat(claims.has("dlg")).isEqualTo(path.endsWith("/messages"));requests.incrementAndGet();
            com.fasterxml.jackson.databind.JsonNode body;int status;
            if(path.equals("/v1/conversations")){body=Json.MAPPER.valueToTree(Map.of("id","conversation-1"));status=201;}
            else if(path.endsWith("/messages")){body=Json.MAPPER.valueToTree(Map.of("jobId","job-1","conversationId","conversation-1","messageId","message-1","queuePosition",0));status=202;}
            else {body=AnswerFixtures.response("코드는 100입니다",AnswerFixtures.a());status=200;}
            byte[] bytes=Json.canonical(body);exchange.sendResponseHeaders(status,bytes.length);exchange.getResponseBody().write(bytes);
        }catch(Throwable invalid){failure.set(invalid);exchange.sendResponseHeaders(500,-1);}finally{exchange.close();}});
        server.start();
        try(var gateway=new GatewayClient(java.net.URI.create("http://localhost:"+server.getAddress().getPort()),"fixture-service-token",signer)) {
            var q=AnswerFixtures.bound(AnswerFixtures.question("q",true,"100"));var step=gateway.execute(q.question(),null);
            assertThat(failure.get()).isNull();assertThat(step.failure()).isNull();assertThat(new AnswerScorer().score(q,step.response()).correct()).isTrue();assertThat(requests.get()).isEqualTo(3);
        }finally{server.stop(0);}
    }
}
