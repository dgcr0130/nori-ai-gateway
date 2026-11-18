package com.nori.ai.gateway.core.authz;


import com.nori.ai.gateway.core.scope.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;

class SourceAuthorizationStepTest {
    private final DelegatedCall call = new DelegatedCall("actor", "a".repeat(22));
    private final VerifiedSystemScope scope = new VerifiedSystemScope(OwnerSystem.TC, "service", new ActorRef("actor"),
        "actor", Set.of(), "a".repeat(64), Instant.EPOCH, Instant.parse("2100-01-01T00:00:00Z"), "req");
    private final SourcePermissionMap permissions = new SourcePermissionMap(Map.of("sources://TC/test", new SourcePermissionMap.Entry(Set.of("A", "B"), true)));
    @Test void allRequiredPermissionsAreCheckedInTheVerifiedSystemScope() {
        var step=new SourceAuthorizationStep(permissions);
        for(var perms:List.of(Set.<String>of(),Set.of("A"),Set.of("B"),Set.of("OTHER")))assertThat(step.authorizeBefore("sources://TC/test",withPermissions(perms),call)).isInstanceOf(SourceAuthorizationStep.Denied.class);
        assertThat(step.authorizeBefore("sources://TC/test",withPermissions(Set.of("A","B")),call)).isEqualTo(new SourceAuthorizationStep.Granted(Set.of("A","B"),scope.pdg()));
    }
    private VerifiedSystemScope withPermissions(Set<String> values){return new VerifiedSystemScope(scope.boundSystem(),scope.authenticatedServiceRef(),scope.actorRef(),scope.sub(),values,scope.pdg(),scope.issuedAt(),scope.expiresAt(),scope.requestId());}
    @Test void missingMappingForeignActorAndForeignOwnerRemainDenied() {
        var step=new SourceAuthorizationStep(permissions);var current=withPermissions(Set.of("A","B"));
        for(String source:List.of("absent","sources://WM/test","sources://TC/../test","sources://TC/test?other=true","sources://TC/test#other"))assertThat(step.authorizeBefore(source,current,call)).isInstanceOf(SourceAuthorizationStep.Denied.class);
        assertThat(step.authorizeBefore("sources://TC/test",current,new DelegatedCall("other",call.dlg()))).isInstanceOf(SourceAuthorizationStep.Denied.class);
    }
    @Test void mappingParserValidatesExactFieldsAndPermissionFormat() {
        assertThat(SourcePermissionMap.fromJson("{\"source\":{\"requiredPermCodes\":[\"A\"],\"mockOnly\":true}}").find("source")).isPresent();
        for (String json : List.of("[]", "{\"s\":{\"requiredPermCodes\":[],\"mockOnly\":true}}", "{\"s\":{\"requiredPermCodes\":[\"a\"],\"mockOnly\":true}}",
                "{\"s\":{\"requiredPermCodes\":[\"A\",\"A\"],\"mockOnly\":true}}", "{\"s\":{\"requiredPermCodes\":[\"A\"],\"mockOnly\":\"true\"}}"))
            assertThatThrownBy(() -> SourcePermissionMap.fromJson(json)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void scopedExclusionCannotLeakToAnotherJobAndDoesNotChangeTheMapping() {
        var current=withPermissions(Set.of("A","B"));var step=new SourceAuthorizationStep(permissions,(source,actual)->actual.requestId().equals("req"));assertThat(step.authorizeBefore("sources://TC/test",current,call)).isInstanceOf(SourceAuthorizationStep.Denied.class);
        var next=new VerifiedSystemScope(current.boundSystem(),current.authenticatedServiceRef(),current.actorRef(),current.sub(),current.perm(),current.pdg(),current.issuedAt(),current.expiresAt(),"next");assertThat(step.authorizeBefore("sources://TC/test",next,call)).isInstanceOf(SourceAuthorizationStep.Granted.class);assertThat(permissions.find("sources://TC/test")).isPresent();
    }
}
