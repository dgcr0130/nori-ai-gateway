package com.nori.ai.gateway.core.scope;

import java.net.URI;
public record OwnedReference(OwnerSystem ownerSystem, String ref) {
    public boolean belongsTo(OwnerSystem expected) {
        if (expected == null || !java.util.Objects.equals(ownerSystem,expected) || ref == null) return false;
        try {
            URI uri = URI.create(ref);
            return uri.getScheme() != null && expected.name().equals(uri.getRawAuthority())
                && uri.getRawQuery() == null && uri.getRawFragment() == null && uri.getRawPath() != null
                && uri.getRawPath().startsWith("/") && !uri.getRawPath().contains("..");
        } catch (IllegalArgumentException e) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(OwnedReference.class.getName()),"OWNED_REFERENCE_FAILED",e); return false; }
    }
}
