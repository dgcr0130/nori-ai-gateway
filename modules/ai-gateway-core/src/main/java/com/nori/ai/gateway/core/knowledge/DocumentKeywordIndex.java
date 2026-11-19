package com.nori.ai.gateway.core.knowledge;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;

/** 활성 문서 메모리 색인의 준비·교체 수명 계약입니다. 조회는 DocumentSearch가 담당합니다. */
public interface DocumentKeywordIndex {
    interface Prepared extends AutoCloseable { @Override void close(); }
    Prepared prepare(VerifiedSystemScope scope, DocumentCorpus.Bundle bundle);
    void install(VerifiedSystemScope scope, Prepared prepared);
}
