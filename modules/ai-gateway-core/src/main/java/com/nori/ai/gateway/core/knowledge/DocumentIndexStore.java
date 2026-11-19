package com.nori.ai.gateway.core.knowledge;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.*;

public interface DocumentIndexStore {
    boolean hasActiveIndex(VerifiedSystemScope scope);
    /** 단일 인스턴스 기동에서 이전 INACTIVE·BUILDING 묶음만 정리합니다. */
    void cleanNonActive(VerifiedSystemScope scope);
    List<DocumentCorpus.Answer> activeAnswers(VerifiedSystemScope scope);
    void saveBuilding(VerifiedSystemScope scope, DocumentCorpus.Bundle bundle);
    DocumentCorpus.Bundle load(VerifiedSystemScope scope, UUID bundleId);
    void activate(VerifiedSystemScope scope, DocumentCorpus.Bundle bundle);
    Optional<String> currentRevision(VerifiedSystemScope scope, String bucket, String objectKey);
}
