package com.nori.ai.gateway.app;

import com.nori.ai.gateway.app.inference.OllamaEndpointStub;
import com.nori.ai.gateway.app.job.recovery.ReadinessGate;
import com.nori.ai.gateway.app.safety.ProductionSafetyGuard;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import static org.assertj.core.api.Assertions.*;

/** 운영 프로필의 전체 앱을 격리 DB·S3·LLM·표준 업무 HTTP 대역으로 기동합니다. */
class ProductionProfileStartupIT extends StackContextSupport {
    private HttpServer s3;
    private OllamaEndpointStub llm;
    @Override protected boolean productionSourceClients() { return true; }
    @Override protected String[] activeProfiles() { return new String[]{"prod"}; }
    @Override protected WebApplicationType applicationType() { return WebApplicationType.SERVLET; }
    @Override protected List<JWK> trustedPublicKeys() {
        try {
            return List.of(new ECKeyGenerator(Curve.P_256).keyID("prod-local-fixture")
                .algorithm(JWSAlgorithm.ES256).generate().toPublicJWK());
        } catch (JOSEException failure) {
            throw new IllegalStateException("운영 프로필 시험 신뢰 키를 준비할 수 없습니다", failure);
        }
    }
    @BeforeEach void localServices() throws Exception {
        llm = new OllamaEndpointStub();
        s3 = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        s3.createContext("/", exchange -> {
            byte[] body = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><IsTruncated>false</IsTruncated></ListBucketResult>".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/xml");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
            finally { exchange.close(); }
        });
        s3.start();
    }
    @Override protected Map<String,Object> extraApplicationProperties() {
        try {
            var access = Files.writeString(securityDirectory.resolve("access-key"), "local-access");
            var secret = Files.writeString(securityDirectory.resolve("secret-key"), "local-secret");
            return Map.of("ai.gateway.inference.mode", "enabled", "ai.gateway.llm.baseUrl", llm.baseUrl() + "/v1",
                "ai.gateway.documents.s3Endpoint", "http://127.0.0.1:" + s3.getAddress().getPort(),
                "ai.gateway.documents.accessKeyFile", access.toString(), "ai.gateway.documents.secretKeyFile", secret.toString(),
                "test.document-prefix", "rag/");
        } catch (java.io.IOException failure) { throw new IllegalStateException("로컬 자격 준비 실패", failure); }
    }
    @Test void productionStartsReadyWithNoHookBeansOrSuccessfulTestRoutes() throws Exception {
        start();
        assertThat(context.getEnvironment().getActiveProfiles()).containsExactly("prod");
        assertThat(context.getBeansOfType(ProductionSafetyGuard.class)).hasSize(1);
        var hooks = Arrays.stream(context.getBeanDefinitionNames()).map(context::getType).filter(Objects::nonNull)
            .filter(type -> type.getPackageName().startsWith("com.nori.ai.gateway.app.hooks")).toList();
        assertThat(hooks).isEmpty();
        var client = HttpClient.newHttpClient();
        int port = ((WebServerApplicationContext) context).getWebServer().getPort();
        URI readiness = URI.create("http://127.0.0.1:" + port + "/actuator/health/readiness");
        long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
        HttpResponse<String> response;
        do {
            response = client.send(HttpRequest.newBuilder(readiness).timeout(Duration.ofSeconds(3)).GET().build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) break;
            Thread.sleep(20);
        } while (System.nanoTime() < deadline);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(context.getBean(ReadinessGate.class).ready()).isTrue();
        assertThat(context.getBeansOfType(com.nori.ai.gateway.integration.loki.LokiLogConnector.class)).hasSize(1);
        assertThat(context.getBeansOfType(com.nori.ai.gateway.integration.tempo.TempoTraceConnector.class)).hasSize(1);
        for (String path : List.of("/__test/clock", "/__test/egress-probe", "/__test/arm")) {
            for (String method : List.of("GET", "POST")) {
                var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).timeout(Duration.ofSeconds(3))
                    .method(method, HttpRequest.BodyPublishers.noBody()).build();
                assertThat(client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() / 100).isNotEqualTo(2);
            }
        }
    }
    @AfterEach @Override protected void close() {
        try { super.close(); }
        finally { if (s3 != null) s3.stop(0); if (llm != null) llm.close(); }
    }
}
