package com.nori.ai.gateway.core.result;

import java.util.*;

public record ResultSourceDeps(List<Source> sources, List<DocKey> knowledgeDocuments) {
    public ResultSourceDeps { sources=List.copyOf(sources); knowledgeDocuments=List.copyOf(knowledgeDocuments); }
    public record Source(String sourceId, Set<String> permCodes) {
        public Source { Objects.requireNonNull(sourceId); permCodes=Collections.unmodifiableSet(new LinkedHashSet<>(permCodes.stream().sorted().toList())); }
    }
    public record DocKey(String ownerSystem, String sourceRef, String revision) {
        public DocKey { Objects.requireNonNull(ownerSystem); Objects.requireNonNull(sourceRef); Objects.requireNonNull(revision); }
    }
}
