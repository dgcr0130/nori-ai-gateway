package com.nori.ai.gateway.acceptance;

import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HookClientBackendResetTest {
    @Test void resetsExternalCountersOnlyAfterGatewayAcceptsAndPreservesDelegations() throws Exception {
        var gateway = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        var resetStatus = new AtomicInteger(409);
        gateway.createContext("/__test/reset", exchange -> {
            try { exchange.sendResponseHeaders(resetStatus.get(), -1); }
            finally { exchange.close(); }
        });
        gateway.start();
        try (var backend = new StandardSystemStub(Clock.systemUTC()); var http = HttpClient.newHttpClient()) {
            var hooks = new HookClient(http, URI.create("http://127.0.0.1:" + gateway.getAddress().getPort()),
                "hook", backend.base(), backend.controlToken);
            hooks.registerDelegation("actor", "delegation");
            hooks.registerDelegation("actor", "revoked");
            backend.revoke(StandardSystemStub.hash("revoked"));
            backend.select("partial");
            assertEquals("PARTIAL", query(http, backend, "delegation"));
            assertEquals(1, backend.requests.size());
            assertEquals(409, assertThrows(HookClient.HookFailure.class, hooks::reset).status());
            assertEquals("partial", backend.selected());
            assertEquals(1, backend.requests.size());
            resetStatus.set(204);
            hooks.reset();
            assertEquals("default", backend.selected());
            assertEquals(0, backend.requests.size());
            assertEquals("SUCCESS", query(http, backend, "delegation"));
            assertEquals("DENIED", query(http, backend, "revoked"));
        } finally { gateway.stop(0); }
    }

    private static String query(HttpClient http, StandardSystemStub backend, String delegation) throws Exception {
        var body = Map.of("systemId", "TC", "jobId", "job", "toolId", "tc.eqp.getOperationalSnapshot",
            "subject", "actor", "arguments", Map.of("eqp", "EQP-1"));
        var request = HttpRequest.newBuilder(backend.base().resolve("/internal/ai/v1/query"))
            .header("Authorization", "Bearer " + backend.token).header("X-Request-Id", "job")
            .header("X-Delegation-Id", delegation)
            .POST(HttpRequest.BodyPublishers.ofByteArray(CanonicalJson.MAPPER.writeValueAsBytes(body))).build();
        return CanonicalJson.MAPPER.readTree(http.send(request, HttpResponse.BodyHandlers.ofByteArray()).body())
            .path("status").asText();
    }
}
