package com.nori.ai.gateway.core.expiry;

import com.nori.ai.gateway.core.job.CancellationToken;

/** 외부 호출과 캐시 쓰기 전후에 작업 취소를 확인합니다. */
public final class ExpiryCheckpoint {
    public void assertLive(CancellationToken token) {
        if (token.reason().isPresent()) throw new IllegalStateException("작업이 취소되었습니다");
    }
}
