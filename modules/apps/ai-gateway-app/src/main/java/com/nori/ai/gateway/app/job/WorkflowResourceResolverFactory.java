package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.authz.SourcePermissionMap;
import com.nori.ai.gateway.core.job.JobContext;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.port.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ResultStatus;
import com.nori.ai.gateway.core.scope.*;
import java.time.ZoneId;
import java.util.*;

public final class WorkflowResourceResolverFactory {
    private final SourcePermissionMap permissions;private final CapabilityRegistry registry;private final com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiry;
    public WorkflowResourceResolverFactory(SourcePermissionMap permissions,CapabilityRegistry registry,com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiry){this.permissions=permissions;this.registry=registry;this.expiry=expiry;}
    public WorkflowResourceResolver forJob(JobContext job,ZoneId zone) {
        var call=new com.nori.ai.gateway.core.authz.DelegatedCall(job.scope().sub(),job.delegation());
        return new WorkflowResourceResolver() {
            private void verify(VerifiedSystemScope supplied){if(supplied!=job.scope()||!job.jobId().equals(supplied.requestId()))throw new IllegalArgumentException("조회 작업 결속이 다릅니다");if(job.cancellation().reason().isPresent())throw new IllegalStateException("작업이 취소되었습니다");}
            @Override public Lookup lookup(ResourceRef ref,VerifiedSystemScope scope) {
                verify(scope);beforeRead();
                // 대상은 분류의 리터럴 검증을 마친 참고값입니다. 최종 데이터 범위는 표준 조회 내부에서 검사합니다.
                boolean literal=job.requestInput().selectedResourceRefs().contains(ref);
                Object targets=job.classification().get("targets");
                if(targets instanceof List<?> values)literal|=values.stream().anyMatch(value->value instanceof Map<?,?> map&&Objects.equals(map.get("type"),ref.type())&&Objects.equals(map.get("ref"),ref.ref()));
                return literal?new Found(new ResourceState(scope.boundSystem(),ref.type(),true)):new Absent();
            }
            @Override public SourceBinding source(CapabilityManifest manifest,VerifiedSystemScope scope) {
                verify(scope);var canonical=registry.find(scope,manifest.capabilityId()).orElse(null);
                boolean allowed=canonical!=null&&java.util.Objects.equals(canonical.ownerSystem(),scope.boundSystem())&&canonical.sourceAllowlistRef().equals(manifest.sourceAllowlistRef())
                    &&new OwnedReference(scope.boundSystem(),manifest.sourceAllowlistRef()).belongsTo(scope.boundSystem())&&permissions.find(manifest.sourceAllowlistRef()).isPresent();
                return new SourceBinding(manifest.ownerSystem(),manifest.sourceAllowlistRef(),allowed);
            }
            @Override public ZoneId timezone(VerifiedSystemScope scope){verify(scope);return zone;}
            @Override public com.nori.ai.gateway.core.authz.DelegatedCall delegatedCall(){return call;}
            @Override public com.nori.ai.gateway.core.job.CancellationToken cancellation(){return job.cancellation();}
            @Override public void beforeRead(){expiry.assertLive(job.cancellation());}
        };
    }
}
