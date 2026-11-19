package com.nori.ai.gateway.app.security;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.UUID;

public final class ScopeAssembler {
    public VerifiedSystemScope forDocumentStartup(com.nori.ai.gateway.core.scope.OwnerSystem system,SecurityCredentialFiles credentials) {
        if(credentials.issuer(system)==null)throw new IllegalArgumentException("등록된 시스템 자격이 필요합니다");
        // 사용자 권한을 만들어 내지 않습니다. 검증된 설정 시스템의 기동 저장 작업만 이 범위를 사용합니다.
        return new VerifiedSystemScope(system,"document-startup",new com.nori.ai.gateway.core.scope.ActorRef("document-sync"),"document-sync",java.util.Set.of(),"document-startup",java.time.Instant.MIN,java.time.Instant.MAX,"document-startup");
    }
    public VerifiedSystemScope forWorker(com.nori.ai.gateway.persistence.queue.JobScopeRecord job) {
        // Job의 180초 정각 허용을 배타적 만료 상한으로 표현하며 요청 단언의 만료는 바꾸지 않습니다.
        return new VerifiedSystemScope(job.ownerSystem(), "job-worker", new com.nori.ai.gateway.core.scope.ActorRef(job.actorRef()),
            job.actorRef(), job.requestPerm(), job.requestPdg(), job.createdAt(), job.createdAt().plusSeconds(180).plusNanos(1), job.jobId());
    }
    VerifiedSystemScope forRequest(VerifiedServiceIdentity service, ActorAssertionVerifier.VerifiedAssertion assertion, UUID requestId) {
        if (!java.util.Objects.equals(service.ownerSystem(),assertion.owner())) throw new IllegalArgumentException("검증된 시스템이 일치하지 않습니다");
        var scope = new VerifiedSystemScope(service.ownerSystem(),service.authenticatedServiceRef(),assertion.actor(),assertion.actor().value(),
            assertion.perm(),assertion.pdg(),assertion.issuedAt(),assertion.expiresAt(),requestId.toString());
        if (!scope.validAt(assertion.verifiedAt())) throw new IllegalArgumentException("검증된 유효 기간이 잘못되었습니다");
        return scope;
    }
}
