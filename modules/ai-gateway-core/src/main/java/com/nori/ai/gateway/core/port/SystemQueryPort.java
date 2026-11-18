package com.nori.ai.gateway.core.port;

import com.nori.ai.gateway.core.job.CancellationToken;
import com.nori.ai.gateway.core.plan.ResourceRef;
import com.nori.ai.gateway.core.registry.CapabilityManifest;
import com.nori.ai.gateway.core.result.ResultStatus;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import tools.jackson.databind.JsonNode;

/** 업무 자료 해석은 소유 시스템에 두고 검증된 조회 외피만 전달합니다. */
public interface SystemQueryPort {
    Response query(Request request);

    record Request(VerifiedSystemScope scope, CapabilityManifest tool, JsonNode arguments,
            List<ResourceRef> targets, String delegationId, ZoneId timezone, CancellationToken cancellation) {
        public Request {
            Objects.requireNonNull(scope); Objects.requireNonNull(tool); Objects.requireNonNull(arguments);
            Objects.requireNonNull(cancellation); targets = List.copyOf(targets); arguments = arguments.deepCopy();
            if (!scope.boundSystem().equals(tool.ownerSystem()) || delegationId == null
                    || !delegationId.matches("[A-Za-z0-9_-]{22,128}")) throw new IllegalArgumentException("조회 위임 결속이 다릅니다");
        }
        @Override public JsonNode arguments() { return arguments.deepCopy(); }
        @Override public String toString() { return "SystemQueryRequest[보호됨]"; }
    }
    record Range(Instant from, Instant to) {
        public Range { if (from == null || to == null || !from.isBefore(to)) throw new IllegalArgumentException("조회 구간이 잘못되었습니다"); }
    }
    record Response(ResultStatus status, Instant observedAt, JsonNode data, List<String> missingFields,
            List<String> warnings, List<Range> missingRanges, List<Range> unverifiedRanges, String errorCode) {
        public Response {
            Objects.requireNonNull(status); Objects.requireNonNull(observedAt);
            data = data == null ? null : data.deepCopy(); missingFields = List.copyOf(missingFields);
            warnings = List.copyOf(warnings); missingRanges = List.copyOf(missingRanges); unverifiedRanges = List.copyOf(unverifiedRanges);
        }
        @Override public JsonNode data() { return data == null ? null : data.deepCopy(); }
        @Override public String toString() { return "SystemQueryResponse[status=" + status + "]"; }
    }
}
