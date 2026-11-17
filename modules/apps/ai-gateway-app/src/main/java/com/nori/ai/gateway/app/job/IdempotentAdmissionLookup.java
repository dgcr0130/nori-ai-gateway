package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.persistence.queue.AdmissionLookupRepository;
import java.util.Optional;

public final class IdempotentAdmissionLookup {
    private final AdmissionLookupRepository repository;
    public IdempotentAdmissionLookup(AdmissionLookupRepository repository){this.repository=repository;}
    public Optional<JobAdmissionService.Accepted> find(VerifiedSystemScope scope,String key,String digest){
        if(key==null)return Optional.empty();
        return repository.find(scope,key).map(existing->{
            if(!existing.requestDigest().equals(digest))throw new JobAdmissionService.Rejected(409,"IDEMPOTENCY_KEY_CONFLICT",0);
            return new JobAdmissionService.Accepted(existing.jobId(),existing.conversationId(),existing.requestMessageId(),0);
        });
    }
}
