package com.nori.ai.gateway.core.knowledge;

import java.util.*;

/** 저장 경계를 넘는 본문과 제목은 모두 mask-v1을 적용한 값입니다. */
public final class DocumentCorpus {
    private DocumentCorpus() {}
    public record Source(String bucket, String prefix, Set<String> permissions) {
        public Source {
            if (bucket == null || bucket.isBlank() || prefix == null || permissions == null || permissions.isEmpty()) throw new IllegalArgumentException("문서 원천이 잘못되었습니다");
            permissions = Set.copyOf(permissions);
        }
    }
    public record Limits(int maxFileBytes, int childMaxChars, int parentMaxChars, int headingMaxChars) {
        public Limits {
            if (maxFileBytes < 1 || maxFileBytes > 1048576 || childMaxChars < 1 || childMaxChars > 200
                    || parentMaxChars < 1 || parentMaxChars > 450 || headingMaxChars < 1 || headingMaxChars > 60) throw new IllegalArgumentException("문서 제한이 잘못되었습니다");
        }
    }
    public record Issue(int line, String reason) {
        public Issue { if (line < 1 || reason == null || !reason.matches("[A-Z_]+")) throw new IllegalArgumentException("제외 위치가 잘못되었습니다"); }
    }
    public record Child(UUID id, int ordinal, DocumentLocation location, String header, String bodyMasked, List<String> identifiers, float[] embedding) {
        public Child { identifiers = List.copyOf(identifiers); embedding = embedding.clone(); }
        @Override public float[] embedding() { return embedding.clone(); }
    }
    public record Parent(UUID id, DocumentLocation location, String header, String bodyMasked, String bodySha256, List<Child> children) {
        public Parent { children = List.copyOf(children); }
    }
    public record Document(UUID id, String bucket, String objectKey, String revision, String title, Set<String> permissions, List<Issue> issues, List<Parent> parents) {
        public Document { permissions = Set.copyOf(permissions); issues = List.copyOf(issues); parents = List.copyOf(parents); }
    }
    public record Answer(UUID id, String questionMasked, List<DocumentLocation> locations, Set<String> permissions, float[] embedding) {
        public Answer { locations = List.copyOf(locations); permissions = Set.copyOf(permissions); embedding = embedding.clone(); }
        @Override public float[] embedding() { return embedding.clone(); }
    }
    public record Bundle(UUID id, String embeddingModel, int dimension, String configSha256, List<Document> documents, List<Answer> answers) {
        public Bundle {
            if (dimension < 1 || embeddingModel == null || embeddingModel.isBlank() || configSha256 == null || !configSha256.matches("[0-9a-f]{64}")) throw new IllegalArgumentException("색인 설정이 잘못되었습니다");
            documents = List.copyOf(documents); answers = List.copyOf(answers);
        }
    }
}
