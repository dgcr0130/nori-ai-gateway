package com.nori.ai.gateway.integration.system;

import com.nori.ai.gateway.core.scope.OwnerSystem;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/** 기동 때 검증한 시스템별 접속 정보이며 자격 본문은 출력하지 않습니다. */
public record SystemQuerySettings(Map<OwnerSystem, Endpoint> endpoints) {
    public SystemQuerySettings { endpoints = Map.copyOf(endpoints); }
    public Optional<Endpoint> find(OwnerSystem system) { return Optional.ofNullable(endpoints.get(system)); }
    public record Endpoint(URI backendUrl, Path gatewayTokenFile) {
        public Endpoint {
            if (backendUrl == null || gatewayTokenFile == null || !java.util.Set.of("http", "https").contains(backendUrl.getScheme())
                    || backendUrl.getHost() == null || backendUrl.getRawUserInfo() != null || backendUrl.getRawQuery() != null
                    || backendUrl.getRawFragment() != null || !backendUrl.getRawPath().isEmpty())
                throw new IllegalArgumentException("시스템 조회 주소가 잘못되었습니다");
        }
        @Override public String toString() { return "SystemEndpoint[보호됨]"; }
    }
    @Override public String toString() { return "SystemQuerySettings[보호됨]"; }
}
