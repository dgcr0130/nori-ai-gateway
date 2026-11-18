package com.nori.ai.gateway.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import static org.assertj.core.api.Assertions.*;

/** AC-32-NEEDS 사례표 판정과 readiness stub leaf 판정을 loopback 대역으로 실행한다(대역 값은 시험 지정이며 제품 증거가 아님). */
class F2NeedsAndReadinessTest {
    /** 실행 목록 0~4와 동일 도구 3회 제한을 판정하는 HTTP 대역입니다. */
    private static F2LocalFixture.Outcome execution(Map.Entry<JsonNode,String> entry,boolean wrongCount){
        JsonNode wire;try{wire=CanonicalJson.MAPPER.readTree(entry.getKey().path("llmScript").get(0).path("content").asText());}catch(Exception invalid){throw new IllegalStateException(invalid);}
        if(wire.has("systemId"))return new F2LocalFixture.Outcome().failed("CONTRACT_INVALID").watch("CLASSIFY",0,1);
        int count=wire.path("toolCalls").size();var result=new F2LocalFixture.Outcome().watch("CLASSIFY",0,1);for(int i=0;i<Math.min(count,3)+(wrongCount?1:0);i++)result.tool("tc.eqp.getOperationalSnapshot","SUCCESS");
        if(count==4)result.payload=new LinkedHashMap<>(Map.of("trace",List.of(Map.of("stepId","call-4","outcome","TOOL_LIMIT_REACHED"))));return result;
    }
    @Test void executionListCasesCheckExactCountsSingleClassificationAndExplicitToolLimit()throws Exception {
        try(var fixture=new F2LocalFixture()){
            fixture.responder=entry->execution(entry,false);fixture.checks(Optional.empty()).run("AC-32-NEEDS");assertThat(fixture.jobs).isEqualTo(6);
            var scenarios=new ArrayList<>(fixture.armed.values());for(int count=0;count<=4;count++)assertThat(CanonicalJson.MAPPER.readTree(scenarios.get(count).path("llmScript").get(0).path("content").asText()).path("toolCalls")).hasSize(count);
            fixture.responder=entry->execution(entry,true);assertThatThrownBy(()->fixture.checks(Optional.empty()).run("AC-32-NEEDS")).hasMessage("F2_EXECUTION_LIST_COUNT");
            fixture.responder=entry->{var value=execution(entry,false);value.watch.clear();return value.watch("CLASSIFY",0,2);};assertThatThrownBy(()->fixture.checks(Optional.empty()).run("AC-32-NEEDS")).hasMessage("F2_CLASSIFY_SINGLE_CALL");
        }
    }

    private static F2ReadinessChecks readiness(F2LocalFixture fixture, F2ReadinessChecks.AlertLog alerts) { return new F2ReadinessChecks(fixture.gateway, fixture.hooks, fixture.oracle, alerts); }

    @Test void readinessLeavesUseOnlyTheStubHookObserve503AndAlwaysRestoreReady() throws Exception {
        try (var fixture = new F2LocalFixture()) {
            readiness(fixture, (code, since) -> 0).run("AC-44");
            readiness(fixture, (code, since) -> 0).run("AC-33-LOADING");
            fixture.retryAfter=false;
            assertThatThrownBy(() -> readiness(fixture,(code,since)->0).run("AC-44")).hasMessage("F2_ADMISSION_RETRY_AFTER_INFERENCE_UNAVAILABLE");
            assertThat(fixture.readiness).isEqualTo("READY");
            fixture.retryAfter=true;
            assertThatThrownBy(() -> readiness(fixture,F2ReadinessChecks.unavailableAlerts()).run("AC-33-PERSIST")).isInstanceOf(TestAbortedException.class).hasMessage("LOKI_READBACK_MISSING");
        }
    }

    @Test void persistentAlertAdvancesOnlyTheControlledClockAndFailsWhenClearedBeforeReady() throws Exception {
        try (var fixture = new F2LocalFixture()) {
            readiness(fixture, (code, since) -> code.equals("INFERENCE_ALERT_RAISED_MODEL_LOADING") ? 1 : 0).run("AC-33-PERSIST");
            assertThat(fixture.clockOps).hasSize(4);
            assertThat(fixture.clockOps.get(0)).contains("FREEZE_AT"); assertThat(fixture.clockOps.get(1)).contains("\"seconds\":601"); assertThat(fixture.clockOps.get(2)).contains("\"seconds\":3600"); assertThat(fixture.clockOps.get(3)).contains("RESUME_SYSTEM");
            assertThatThrownBy(() -> readiness(fixture, (code, since) -> code.endsWith("MODEL_LOADING") || code.endsWith("CLEARED") ? 1 : 0).run("AC-33-PERSIST")).hasMessage("F2_PERSIST_ALERT_CLEARED_BEFORE_READY");
            fixture.restoredState = "MODEL_LOADING";
            assertThatThrownBy(() -> readiness(fixture, (code, since) -> 0).run("AC-44")).hasMessage("F2_READINESS_NOT_READY_BEFORE");
        }
    }

    @Test void readinessRestorationFailureIsReportedOnlyWhenTheLeafItselfPassed() throws Exception {
        try (var fixture = new F2LocalFixture()) {
            var calls = new AtomicBoolean();
            var checks = readiness(fixture, (code, since) -> { if (code.equals("INFERENCE_ALERT_CLEARED")) return 0; fixture.restoredState = "INFERENCE_UNAVAILABLE"; return 1; });
            assertThatThrownBy(() -> checks.run("AC-33-PERSIST")).hasMessage("F2_READINESS_NOT_RESTORED");
        }
    }
}
