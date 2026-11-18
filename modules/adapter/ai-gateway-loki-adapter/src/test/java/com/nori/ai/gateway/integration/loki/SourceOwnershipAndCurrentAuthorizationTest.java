package com.nori.ai.gateway.integration.loki;

import com.nori.ai.gateway.core.authz.DelegatedCall;
import static com.nori.ai.gateway.integration.ConnectorTestSupport.*;
import com.nori.ai.gateway.core.result.ResultStatus;
import com.nori.ai.gateway.core.retention.TimeWindow;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.integration.ConnectorTestSupport;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.integration.ConnectorTestSupport.*;

class SourceOwnershipAndCurrentAuthorizationTest {
    @Test void foreignSourceReferenceOwnerAndExpiredScopeAreDeniedBeforeAuthorizationOrSourceRead() throws Exception {
        try (var stub = new ConnectorTestSupport()) {
            AtomicInteger authorizationReads = new AtomicInteger();
            var access = access((call, required) -> { authorizationReads.incrementAndGet(); return new PermissionSnapshot(required, "b".repeat(64)); });
            var connector = new LokiLogConnector(stub.http("loki"), access, ignored -> Optional.empty(), new LokiProperties(), CLOCK);
            var query = new LokiLogConnector.LogQuery(new TimeWindow(NOW.minusSeconds(1), NOW), List.of(), 1000);
            for (var request : List.of(request("sources://WM/logs"), request("sources://TC/logs.search", OwnerSystem.WM, NOW.plusSeconds(60)), request("sources://TC/logs.search", OwnerSystem.TC, NOW)))
                assertThat(connector.search(request, query).status()).isEqualTo(ResultStatus.DENIED);
            assertThat(stub.requests).isEmpty(); assertThat(authorizationReads).hasValue(0);
        }
    }
    @Test void currentPermissionsAreRecheckedAndNeverReusedFromTheAdmissionScope() throws Exception {
        try (var stub = new ConnectorTestSupport()) {
            AtomicInteger authorizationReads = new AtomicInteger();
            var revoked=new java.util.concurrent.atomic.AtomicBoolean();
            var access = access((call, required) -> {authorizationReads.incrementAndGet();return revoked.get()?new Revoked():new PermissionSnapshot(required,"b".repeat(64));});
            stub.responder=request->{revoked.set(true);return new ConnectorTestSupport.Response(200,emptyLoki());};
            var connector = new LokiLogConnector(stub.http("loki"), access, ignored -> Optional.empty(), new LokiProperties(), CLOCK);
            var result = connector.search(request("sources://TC/logs.search"), new LokiLogConnector.LogQuery(new TimeWindow(NOW.minusSeconds(1), NOW), List.of(), 1000));
            assertThat(result.status()).isEqualTo(ResultStatus.DENIED); assertThat(stub.requests).hasSize(1); assertThat(result.items()).isEmpty();
        }
    }
    @Test void missingCurrentPermissionAndUnavailableBackendNeverStartSourceReads() throws Exception {
        for (Decision outcome : List.of(new PermissionSnapshot(Set.of(), "b".repeat(64)), new Revoked())) try (var stub = new ConnectorTestSupport()) {
            var connector = new LokiLogConnector(stub.http("loki"), access((call, required) -> outcome), ignored -> Optional.empty(), new LokiProperties(), CLOCK);
            assertThat(connector.search(request("sources://TC/logs.search"), new LokiLogConnector.LogQuery(new TimeWindow(NOW.minusSeconds(1), NOW), List.of(), 1000)).status())
                .isEqualTo(ResultStatus.DENIED);
            assertThat(stub.requests).isEmpty();
        }
    }
}
