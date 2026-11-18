package com.nori.ai.gateway.core.plan;

import com.nori.ai.gateway.core.authz.DelegatedCall;
import com.nori.ai.gateway.core.result.ResultStatus;
import com.nori.ai.gateway.core.registry.CapabilityManifest;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.Objects;

/** 현재 READ 실패를 리소스 부재로 바꾸지 않는 Job 로컬 경계입니다. */
public interface WorkflowResourceResolver extends ResourceResolver {
    sealed interface Lookup permits Found, Absent, Denied, Unavailable {}
    record Found(ResourceState value) implements Lookup {
        public Found { Objects.requireNonNull(value); if (!value.exists() || value.ownerSystem() == null || value.type() == null) throw new IllegalArgumentException("리소스 결과가 잘못되었습니다"); }
    }
    record Absent() implements Lookup {}
    record Denied() implements Lookup {}
    record Unavailable() implements Lookup {}
    final class ReadFailure extends RuntimeException {
        private final ResultStatus status;
        public ReadFailure(ResultStatus status) {
            super("리소스 조회를 완료할 수 없습니다");
            if (status != ResultStatus.DENIED && status != ResultStatus.SOURCE_UNAVAILABLE) throw new IllegalArgumentException("조회 상태가 잘못되었습니다");
            this.status = status;
        }
        public ResultStatus status() { return status; }
    }
    Lookup lookup(ResourceRef resource, VerifiedSystemScope scope);
    DelegatedCall delegatedCall();
    void beforeRead();
    default com.nori.ai.gateway.core.job.CancellationToken cancellation(){throw new IllegalStateException("작업 취소 결속이 없습니다");}
    @Override default ResourceState resolve(ResourceRef ref, VerifiedSystemScope scope) {
        return switch (lookup(ref, scope)) {
            case Found found -> found.value();
            case Absent ignored -> new ResourceState(scope.boundSystem(), ref.type(), false);
            case Denied ignored -> throw new ReadFailure(ResultStatus.DENIED);
            case Unavailable ignored -> throw new ReadFailure(ResultStatus.SOURCE_UNAVAILABLE);
        };
    }
    @Override default CurrentAuthorization authorize(CapabilityManifest manifest, PlanStep step, VerifiedSystemScope scope) {
        throw new IllegalStateException("현재 단계 인가 경로가 필요합니다");
    }
}
