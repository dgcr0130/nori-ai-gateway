package com.nori.ai.gateway.core.authz;

import com.nori.ai.gateway.core.scope.*;
import java.util.*;

public final class SourceAuthorizationStep {
    @FunctionalInterface public interface ScopedPermissionExclusion { boolean excludes(String sourceId, VerifiedSystemScope scope); }
    public sealed interface Decision permits Granted, Denied, SourceUnavailable {}
    public record Granted(Set<String> allowed, String pdg) implements Decision {
        public Granted { allowed = Set.copyOf(allowed); }
    }
    public record Denied() implements Decision {}
    public record SourceUnavailable() implements Decision {}
    public sealed interface StepDecision permits StepGrant, StepDenied, StepUnavailable {}
    public record StepDenied() implements StepDecision {}
    public record StepUnavailable() implements StepDecision {}
    public static final class StepGrant implements StepDecision {
        private final StepAttempt attempt;
        private final Set<String> requested, allowed;
        private final String currentPdg;
        private final java.util.concurrent.atomic.AtomicBoolean consumed=new java.util.concurrent.atomic.AtomicBoolean();
        private StepGrant(StepAttempt attempt, Set<String> requested, Granted granted) {
            this.attempt=attempt; this.requested=Set.copyOf(requested); this.allowed=granted.allowed(); this.currentPdg=granted.pdg();
        }
        public Set<String> requested() { return requested; }
        public Set<String> allowed() { return allowed; }
        public String currentPdg() { return currentPdg; }
        public boolean matches(StepAttempt expected) { return attempt==expected && !consumed.get() && attempt.evaluated(); }
        public void consume(StepAttempt expected, StepAttempt.Purpose purpose) {
            if (!matches(expected) || expected.purpose()!=purpose || !consumed.compareAndSet(false,true)) throw new IllegalStateException("인가 결과를 재사용할 수 없습니다");
            attempt.consume(purpose);
        }
        @Override public String toString() { return "StepGrant[보호됨]"; }
    }
    private final SourcePermissionMap permissions;
    private final ScopedPermissionExclusion exclusion;
    public SourceAuthorizationStep(SourcePermissionMap permissions) { this(permissions,(source,scope)->false); }
    public SourceAuthorizationStep(SourcePermissionMap permissions,ScopedPermissionExclusion exclusion) {
        this.permissions=Objects.requireNonNull(permissions);this.exclusion=Objects.requireNonNull(exclusion);
    }
    public Decision authorizeBefore(String stepSource, VerifiedSystemScope scope, DelegatedCall call) {
        Objects.requireNonNull(scope); Objects.requireNonNull(call);
        if (!new OwnedReference(scope.boundSystem(), stepSource).belongsTo(scope.boundSystem()) || !scope.sub().equals(call.sub())) return new Denied();
        var mapping = permissions.find(stepSource);
        if (mapping.isEmpty()) return new Denied();
        if (exclusion.excludes(stepSource, scope)) return new Denied();
        if (!scope.perm().containsAll(mapping.get().requiredPermCodes())) return new Denied();
        return new Granted(mapping.get().requiredPermCodes(),scope.pdg());
    }
    public StepDecision authorizeStep(StepAttempt attempt, VerifiedSystemScope scope, DelegatedCall call) {
        Objects.requireNonNull(attempt); Objects.requireNonNull(scope); Objects.requireNonNull(call);
        var binding=attempt.binding();
        if (attempt.mode()!=StepAttempt.Mode.DIRECT_SOURCE) throw new IllegalArgumentException("직결 인가 대상이 아닙니다");
        attempt.claimEvaluation();
        if (!java.util.Objects.equals(binding.ownerSystem(),scope.boundSystem()) || !binding.actorRef().equals(scope.actorRef()) || !binding.sub().equals(scope.sub())
                || !binding.requestId().equals(scope.requestId()) || !binding.jobId().equals(scope.requestId()) || !call.sub().equals(scope.sub())
                || binding.manifestRequiredPermissions().isEmpty()) { attempt.close(); return new StepDenied(); }
        var mapping=permissions.find(binding.sourceRef());
        if (mapping.isEmpty() || !mapping.get().requiredPermCodes().containsAll(binding.manifestRequiredPermissions())) { attempt.close(); return new StepDenied(); }
        Decision decision=authorizeBefore(binding.sourceRef(),scope,call);
        if (decision instanceof Granted granted) {
            if (!granted.allowed().containsAll(binding.manifestRequiredPermissions())) { attempt.close(); return new StepDenied(); }
            return new StepGrant(attempt,mapping.get().requiredPermCodes(),granted);
        }
        attempt.close();
        return decision instanceof SourceUnavailable ? new StepUnavailable() : new StepDenied();
    }
}
