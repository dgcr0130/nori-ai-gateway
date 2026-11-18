package com.nori.ai.gateway.integration.fixtures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** 만든 파일 하나만 소유하며 인입 보안 폴더의 다른 파일은 정리하지 않습니다. */
public final class GatewayTokenFixture implements AutoCloseable {
    private final Path file;
    public GatewayTokenFixture(Path directory, String token) throws IOException {
        file = directory == null ? null : Files.createTempFile(directory, "gateway-", ".token");
        if (file != null) {
            try { Files.writeString(file, token); }
            catch (IOException failure) { Files.deleteIfExists(file); throw failure; }
        }
    }
    public Path file() { return file; }
    @Override public void close() throws IOException { if (file != null) Files.deleteIfExists(file); }
}
