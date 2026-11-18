package com.nori.ai.gateway.core.registry;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.*;

public final class CapabilityRegistry {
    private final Map<String, CapabilityManifest> manifests;
    private final Set<ImplementationPhase> activePhases;
    public CapabilityRegistry(List<CapabilityManifest> candidates,
            Set<ImplementationPhase> activePhases, ContractVersionPolicy versions) {
        this.activePhases = Set.copyOf(activePhases);
        Map<String, CapabilityManifest> registered = new LinkedHashMap<>();
        Set<String> ids = new HashSet<>();
        for (CapabilityManifest manifest : candidates) {
            String key = manifest.ownerSystem().name() + "/" + manifest.capabilityId();
            if (!ids.add(key)) throw new IllegalArgumentException("Capability가 중복되었습니다");
            SchemaContracts.validateSchema(manifest.inputSchema()); SchemaContracts.validateSchema(manifest.outputSchema());
            if (!SchemaContracts.digest(manifest.inputSchema(), manifest.outputSchema()).equals(manifest.schemaDigest()))
                throw new IllegalArgumentException("Schema digest가 일치하지 않습니다");
            if (!versions.supports(manifest.contractVersion())) continue;
            registered.put(key, manifest); versions.register(manifest);
        }
        this.manifests = Collections.unmodifiableMap(registered);
    }
    public Optional<CapabilityManifest> find(String id) {
        var found = manifests.values().stream().filter(value -> value.capabilityId().equals(id)).toList();
        return found.size() == 1 ? Optional.of(found.getFirst()) : Optional.empty();
    }
    public Optional<CapabilityManifest> find(VerifiedSystemScope scope,String id) {
        return find(scope.boundSystem(),id);
    }
    public Optional<CapabilityManifest> find(com.nori.ai.gateway.core.scope.OwnerSystem owner,String id) {
        return Optional.ofNullable(manifests.get(owner.name()+"/"+id));
    }
    public List<CapabilityManifest> all(){return List.copyOf(manifests.values());}
    public boolean hasToolId(String id) { return manifests.values().stream().anyMatch(value -> value.toolId().equals(id)); }
    public boolean phaseActive(ImplementationPhase phase) { return activePhases.contains(phase); }
    public RegistryView view(VerifiedSystemScope scope) {
        return new RegistryView(scope.boundSystem(), manifests.values().stream().filter(m -> java.util.Objects.equals(m.ownerSystem(),scope.boundSystem())
            && m.availability() == Availability.IMPLEMENTED && phaseActive(m.implementationPhase())
            && !m.requiredPermission().isEmpty() && scope.perm().containsAll(m.requiredPermission())
            && m.requiredActorScope().equals("READ")).toList());
    }
}
