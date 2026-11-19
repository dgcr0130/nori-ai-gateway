package com.nori.ai.gateway.core.knowledge;

import java.util.Objects;

public record DocumentLocation(String bucket, String objectKey, String revision, String section, int lineFrom, int lineTo) {
    public DocumentLocation {
        if (bucket == null || bucket.isBlank() || objectKey == null || revision == null || !revision.matches("[0-9a-f]{64}")
                || section == null || lineFrom < 1 || lineTo < lineFrom) throw new IllegalArgumentException("문서 위치가 잘못되었습니다");
    }
}
