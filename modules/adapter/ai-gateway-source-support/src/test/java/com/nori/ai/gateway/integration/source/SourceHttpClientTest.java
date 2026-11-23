package com.nori.ai.gateway.integration.source;

import com.nori.ai.gateway.integration.ConnectorTestSupport;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.integration.ConnectorTestSupport.*;

class SourceHttpClientTest {
    @Test void redirectCannotForwardSecretOrCreateASecondSourceRead() throws Exception {
        try (var source = new ConnectorTestSupport(); var destination = new ConnectorTestSupport()) {
            source.responder = req -> new Response(302, "비공개", Map.of("Location", destination.base() + "/redirected"));
            SourceHttpClient http = source.http("gitlab");
            assertThatThrownBy(() -> http.json(http.uri(List.of("api", "test"), Map.of()), "stub-read-token", http.deadline(Duration.ofSeconds(30))))
                .isInstanceOf(SourceHttpClient.Failure.class).hasMessage("Source 조회를 완료하지 못했습니다").hasNoCause();
            assertThat(source.requests).hasSize(1); assertThat(destination.requests).isEmpty();
        }
    }
    @Test void anAbsoluteUriFromAnotherAuthorityIsRejectedBeforeAnyRead() throws Exception {
        try (var source = new ConnectorTestSupport(); var destination = new ConnectorTestSupport()) {
            SourceHttpClient http = source.http("loki");
            assertThatThrownBy(() -> http.json(destination.base(), null, http.deadline(Duration.ofSeconds(30))))
                .isInstanceOf(SourceHttpClient.Failure.class);
            assertThat(source.requests).isEmpty(); assertThat(destination.requests).isEmpty();
        }
    }
    @Test void transportDefaultsMatchApprovedConnectAndReadTimeouts() {
        assertThat(SourceHttpClient.CONNECT_TIMEOUT).isEqualTo(Duration.ofSeconds(3));
        assertThat(SourceHttpClient.READ_TIMEOUT).isEqualTo(Duration.ofSeconds(10));
    }
    /** 진단 로그용 오류 본문 읽기가 끊겨도 404 판정과 5xx 1회 재시도는 상태 코드대로 유지됩니다. */
    @Test void aTruncatedErrorBodyKeepsTheStatusClassification() throws Exception {
        for (int status : List.of(404, 503)) {
            try (var server = new java.net.ServerSocket(0, 50, java.net.InetAddress.getLoopbackAddress())) {
                var connections = new java.util.concurrent.atomic.AtomicInteger();
                var responder = Thread.ofVirtual().start(() -> {
                    while (!server.isClosed()) {
                        try (var socket = server.accept()) {
                            connections.incrementAndGet();
                            var input = socket.getInputStream(); int matched = 0;
                            for (int value; matched < 4 && (value = input.read()) >= 0;) matched = value == "\r\n\r\n".charAt(matched) ? matched + 1 : (value == '\r' ? 1 : 0);
                            socket.getOutputStream().write(("HTTP/1.1 " + status + " Error\r\nContent-Type: application/json\r\nContent-Length: 1000\r\nConnection: close\r\n\r\n{\"partial\"")
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            socket.getOutputStream().flush();
                        } catch (java.io.IOException closed) { return; }
                    }
                });
                var http = new SourceHttpClient(java.net.URI.create("http://127.0.0.1:" + server.getLocalPort()), "gitlab", SourceObservability.noop());
                assertThatThrownBy(() -> http.json(http.uri(List.of("api", "test"), Map.of()), "stub-read-token", http.deadline(Duration.ofSeconds(30))))
                    .isInstanceOfSatisfying(SourceHttpClient.Failure.class, failure -> {
                        assertThat(failure.httpNotFound()).as("status %d", status).isEqualTo(status == 404);
                        assertThat(failure.deadline()).isFalse();
                    });
                assertThat(connections.get()).as("status %d", status).isEqualTo(status == 503 ? 2 : 1);
                server.close(); responder.join(Duration.ofSeconds(5));
            }
        }
    }
    @Test void aBlockedResponseCannotOverrunTheOverallStepDeadline() throws Exception {
        CountDownLatch release = new CountDownLatch(1);
        try (var source = new ConnectorTestSupport()) {
            source.responder = req -> {
                try { if (!release.await(3, TimeUnit.SECONDS)) throw new AssertionError("응답 해제 신호가 누락되었습니다"); }
                catch (InterruptedException failure) { Thread.currentThread().interrupt(); throw new AssertionError(failure); }
                return new Response(200, emptyLoki());
            };
            var http = source.http("loki");
            long start = System.nanoTime();
            try {
                assertThatThrownBy(() -> http.json(http.uri(List.of("test"), Map.of()), null, http.deadline(Duration.ofMillis(200))))
                    .isInstanceOfSatisfying(SourceHttpClient.Failure.class, failure -> assertThat(failure.deadline()).isTrue());
                assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(1));
                assertThat(source.requests).hasSize(1);
            } finally { release.countDown(); }
        }
    }
}
