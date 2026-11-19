package com.nori.ai.gateway.core.knowledge;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;

/** phase 6이 활성 자료에 대한 조회를 연결할 메모리 색인 수명 계약입니다. */
public interface DocumentKeywordIndex {
    interface Prepared extends AutoCloseable { @Override void close(); }
    Prepared prepare(VerifiedSystemScope scope, DocumentCorpus.Bundle bundle);
    void install(VerifiedSystemScope scope, Prepared prepared);
}
