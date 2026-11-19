package com.nori.ai.gateway.core.knowledge;

import java.util.List;

public interface DocumentObjectStore extends AutoCloseable {
    record ObjectKey(String key, long size) {}
    record Content(String revision, byte[] bytes, boolean oversized) {
        public Content { bytes = bytes.clone(); }
        @Override public byte[] bytes() { return bytes.clone(); }
    }
    List<ObjectKey> list(DocumentCorpus.Source source);
    Content download(DocumentCorpus.Source source, ObjectKey key, int maxBytes);
    @Override default void close() {}
}
