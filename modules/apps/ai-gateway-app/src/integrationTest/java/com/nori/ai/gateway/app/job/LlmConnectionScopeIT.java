package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import java.util.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 로컬 PostgreSQL·HTTP 대역에서 실제 분류/계획/표준 결과/근거/종결을 연결합니다. 실제 LLM 정확도 시험은 아닙니다. */
@Tag("connection-scope")
class LlmConnectionScopeIT extends LlmStubSupport {
    static final String CONNECTION = "tc.eqp.getConnectionState";
    static final String SNAPSHOT = "tc.eqp.getOperationalSnapshot";
    static final String OBSERVED = "ETC_001 설비의 관측 연결 상태는 OFFLINE입니다.";

    void source(String connectionStatus, String snapshotStatus) {
        calls().responder = request -> reply(request, connectionStatus, snapshotStatus);
    }

    StandardSystemStub.Reply reply(StandardSystemStub.Request request, String connectionStatus, String snapshotStatus) {
            String tool = request.body().path("toolId").asString();
            String eqp = request.body().path("arguments").path("eqp").asString();
            String status = tool.equals(SNAPSHOT) ? snapshotStatus : connectionStatus;
            String text = OBSERVED.replace("ETC_001", eqp);
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("eqpId", eqp);
            values.put("connectionState", "OFFLINE");
            if (tool.equals(SNAPSHOT)) { values.put("lotStatus", "UNKNOWN"); values.put("currentLot", null); }
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("version", "1");
            for (String key : List.of("systemId", "jobId", "toolId")) body.put(key, request.body().path(key).asString());
            body.put("status", status);
            body.put("observedAt", NOW.toString());
            body.put("data", Set.of("SUCCESS", "PARTIAL", "CONFLICT").contains(status)
                    ? Map.of("items", List.of(Map.of("ref", eqp, "text", text, "values", values)))
                    : status.equals("EMPTY") ? Map.of("items", List.of()) : null);
            body.put("missingFields", status.equals("PARTIAL") ? List.of(tool.equals(SNAPSHOT) ? "currentLot" : "connectionState") : List.of());
            body.put("warnings", List.of());
            body.put("retentionMeta", Map.of("missingRanges", List.of(), "unverifiedRanges", List.of()));
            int http = switch (status) { case "DENIED" -> 403; case "SOURCE_UNAVAILABLE" -> 503; case "TIMEOUT" -> 504; default -> 200; };
            body.put("errorCode", http == 200 ? null : status.equals("DENIED") ? "DELEGATION_DENIED" : status);
            return new StandardSystemStub.Reply(http, body);
    }

    @Test void previousTurnEtcBindingRunsOnlyConnectionAndCommitsCompleteStandardEvidence() throws Exception {
        source("SUCCESS", "PARTIAL");
        var first = admitScripted("connection-previous", "ETC_001 연결 상태", windowOnly(),
                List.of(classify(intentWire("connection", "ETC_001")), composeFacts(OBSERVED, "e1")), Optional.empty(), null);
        assertThat(terminal(first)).isEqualTo("SUCCEEDED");
        calls().requests.clear();
        var next = admitScripted("connection-followup", "그럼 해당 설비의 현재 연결 상태가 어때", windowOnly(),
                List.of(classify(intentWire("connection", "ETC_001")), composeFacts(OBSERVED, "e1")), Optional.empty(), first.conversationId());
        assertThat(terminal(next)).as("%s", payload(next.jobId())).isEqualTo("SUCCEEDED");
        assertThat(calls().requests).singleElement().satisfies(request -> {
            assertThat(request.body().path("toolId").asString()).isEqualTo(CONNECTION);
            assertThat(request.body().path("arguments").path("eqp").asString()).isEqualTo("ETC_001");
        });
        assertThat(result(next.jobId())).containsEntry("status", "SUCCESS");
        var payload = payload(next.jobId());
        assertThat(payload.path("answer").path("kind").asString()).isEqualTo("ANSWER");
        assertThat(payload.path("missingFields")).isEmpty();
        assertThat(payload.path("toolSummary").get(0).path("status").asString()).isEqualTo("SUCCESS");
        assertThat(payload.path("evidence")).isNotEmpty();
        assertThat(scripts().scriptedCalls(next.jobId(), CallType.CLASSIFY)).isOne();
        assertThat(scripts().scriptedCalls(next.jobId(), CallType.COMPOSER)).isOne();
    }

    @Test void fullOperationalAndConnectionPlusLotQuestionsKeepSnapshotPartial() throws Exception {
        source("SUCCESS", "PARTIAL");
        int index = 0;
        for (String question : List.of("ETC_001 전체 운영 상태", "ETC_001 연결 상태와 현재 LOT를 함께 알려줘")) {
            calls().requests.clear();
            var job = admitScripted("broad-" + index++, question, windowOnly(),
                    List.of(classify(intentWire("status", "ETC_001")), composeFacts(OBSERVED, "e1")), Optional.empty(), null);
            assertThat(terminal(job)).isEqualTo("PARTIAL");
            assertThat(calls().requests).singleElement().satisfies(request -> assertThat(request.body().path("toolId").asString()).isEqualTo(SNAPSHOT));
            assertThat(payload(job.jobId()).path("toolSummary").get(0).path("missingFields").toString()).contains("currentLot");
            assertThat(payload(job.jobId()).path("toolSummary").get(0).path("status").asString()).isEqualTo("PARTIAL");
        }
    }

    @Test void sentenceRejectionCanCoexistWithSuccessfulToolAndJob() throws Exception {
        source("SUCCESS", "PARTIAL");
        var job = admitScripted("connection-sentence-reject", "ETC_001 연결 상태", windowOnly(),
                List.of(classify(intentWire("connection", "ETC_001")), composeSentences(List.of(
                        Map.of("text", OBSERVED, "factIds", List.of("e1")),
                        Map.of("text", "ETC_001 설비의 온도는 987654321도입니다.", "factIds", List.of("e1"))))), Optional.empty(), null);
        assertThat(terminal(job)).isEqualTo("SUCCEEDED");
        assertThat(result(job.jobId())).containsEntry("status", "SUCCESS");
        assertThat(payload(job.jobId()).path("answer").path("kind").asString()).isEqualTo("PARTIAL");
        assertThat(payload(job.jobId()).path("answer").path("text").asString()).doesNotContain("987654321");
    }

    @Test void multipleEquipmentKeepsEachReadAndItsSourceFailure() throws Exception {
        source("SUCCESS", "PARTIAL");
        var original = calls().responder;
        calls().responder = request -> {
            if (!request.body().path("arguments").path("eqp").asString().equals("PHOTO_001")) return original.apply(request);
            return reply(request, "SOURCE_UNAVAILABLE", "PARTIAL");
        };
        String wire = "{\"needs\":[{\"intent\":\"connection\",\"subject\":\"ETC_001\"},{\"intent\":\"connection\",\"subject\":\"PHOTO_001\"}]}";
        var job = admitScripted("connection-multi", "ETC_001와 PHOTO_001 연결 상태 비교", windowOnly(),
                List.of(classify(wire), composeFacts(OBSERVED, "e1")), Optional.empty(), null);
        assertThat(terminal(job)).isEqualTo("PARTIAL");
        assertThat(calls().requests).extracting(request -> request.body().path("arguments").path("eqp").asString())
                .containsExactly("ETC_001", "PHOTO_001");
        assertThat(payload(job.jobId()).path("toolSummary").toString()).contains("SOURCE_UNAVAILABLE");
    }

    @Test void missingPermissionRejectsConnectionBeforeStandardBackendCall() throws Exception {
        perms = Set.of("MODEL_READ");
        var job = admitScripted("connection-denied", "ETC_001 연결 상태", windowOnly(),
                List.of(classify(intentWire("connection", "ETC_001"))), Optional.empty(), null);
        assertThat(terminal(job)).isEqualTo("FAILED");
        assertThat(calls().requests).isEmpty();
        assertThat(tools(job.jobId())).isZero();
    }

    @Test void standardEmptyDeniedUnavailableAndTimeoutRemainDistinctAtToolBoundary() throws Exception {
        for (String status : List.of("EMPTY", "DENIED", "SOURCE_UNAVAILABLE", "TIMEOUT")) {
            source(status, "PARTIAL");
            calls().requests.clear();
            var job = admitScripted("connection-source-" + status.toLowerCase(Locale.ROOT), "ETC_001 연결 상태", windowOnly(),
                    List.of(classify(intentWire("connection", "ETC_001"))), Optional.empty(), null);
            assertThat(terminal(job)).isEqualTo(status.equals("EMPTY") ? "SUCCEEDED" : "FAILED");
            assertThat(payload(job.jobId()).path("toolSummary").get(0).path("status").asString()).isEqualTo(status);
            assertThat(calls().requests).hasSize(1);
            assertThat(scripts().scriptedCalls(job.jobId(), CallType.COMPOSER)).isZero();
        }
    }
}
