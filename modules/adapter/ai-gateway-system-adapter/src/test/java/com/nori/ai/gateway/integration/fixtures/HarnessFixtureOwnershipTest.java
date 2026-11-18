package com.nori.ai.gateway.integration.fixtures;

import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class HarnessFixtureOwnershipTest {
    @TempDir Path directory;
    @Test void tokenOwnerDeletesOnlyItsFileAndCanCloseTwice() throws Exception {
        var ingress = directory.resolve("service-tokens.json"); Files.writeString(ingress, "ingress");
        Path token;
        try (var fixture = new GatewayTokenFixture(directory, "backend")) {
            token = fixture.file(); assertEquals("backend", Files.readString(token));
        }
        assertFalse(Files.exists(token)); assertEquals("ingress", Files.readString(ingress));
        try (var empty = new GatewayTokenFixture(null, "unused")) { assertNull(empty.file()); }
    }
    @Test void diagnosticsKeepBoundedTypesAndBeanNamesWithoutRawValues() {
        var output = new ProtectedProcessOutput();
        output.record("password=PRIVATE-SENTINEL token=ARBITRARY-SECRET");
        output.record("Caused by: java.lang.NullPointerException: PRIVATE-SENTINEL");
        output.record("HARNESS_STARTUP_BEAN=fixtureSystems");
        assertTrue(output.tail().contains("HARNESS_STARTUP_BEAN=fixtureSystems"));
        assertFalse(output.tail().toString().contains("PRIVATE-SENTINEL"));
        assertFalse(output.tail().toString().contains("ARBITRARY-SECRET"));
        for (int i=0;i<25;i++) output.record("java.lang.IllegalStateException: private-"+i);
        assertEquals(20, output.tail().size());
    }
}
