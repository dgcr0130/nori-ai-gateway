package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.budget.CallType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

/** 의도 선택 이후 기존 clarification·종결·감사·이전 턴 창 연결을 실제 앱에서 확인합니다. */
class IntentPeriodPlanningIT extends LlmStubSupport {
    Map<String,Object> clockOnly(){return Map.of("context",Map.of("timezone","Asia/Seoul","referenceTime","2026-11-24T19:27:24+09:00"));}
    @Test void unresolvableRequiredAndOptionalPeriodsClarifyEvenWithAnExplicitFallbackWindow()throws Exception {
        for(String intent:List.of("incident","communication","dlq")){
            String question=intent.equals("dlq")?"이번 달 EQP-001 DLQ":"지난주 EQP-001 에러";
            var job=admitScripted("unresolvable-"+intent,question,windowOnly(),List.of(classify(intentWire(intent,intent.equals("incident")?null:"EQP-001"))),Optional.empty(),null);
            assertThat(terminal(job)).isEqualTo("FAILED");assertClarification(job.jobId(),"TIME_WINDOW_REQUIRED",List.of("timeWindow"));
            assertThat(tools(job.jobId())).isZero();assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
        }
    }
    @Test void missingRequiredPeriodClarifiesAndOptionalDlqDoesNotRequireOne()throws Exception {
        var required=admitScripted("missing-period","EQP-001 통신 로그",clockOnly(),List.of(classify(intentWire("communication","EQP-001"))),Optional.empty(),null);
        assertThat(terminal(required)).isEqualTo("FAILED");assertClarification(required.jobId(),"TIME_WINDOW_REQUIRED",List.of("timeWindow"));assertThat(tools(required.jobId())).isZero();
        calls().reset();var optional=admitScripted("optional-dlq","EQP-001 DLQ 조회",clockOnly(),List.of(classify(intentWire("dlq","EQP-001")),composeSelection("C1")),Optional.empty(),null);terminal(optional);
        assertThat(result(optional.jobId()).get("error_code")).isNull();assertThat(calls().requests).singleElement().satisfies(r->{assertThat(r.body().path("toolId").asString()).isEqualTo("tc.dlq.list");assertThat(r.body().path("arguments")).isEqualTo(json.valueToTree(Map.of("eqp","EQP-001")));});
        assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:job").param("job",optional.jobId()).query(String.class).list()).containsExactly("tc.dlq.list");
    }
    @Test void noQuestionPeriodUsesContextThenImmediatelyPrecedingResolvedWindowInUtc()throws Exception {
        var first=admitScripted("context-period","EQP-001 통신 로그",windowOnly(),List.of(classify(intentWire("communication","EQP-001")),composeSelection("C1")),Optional.empty(),null);terminal(first);
        assertThat(calls().requests).singleElement().satisfies(r->{assertThat(r.body().path("arguments").path("from").asString()).isEqualTo("2026-11-05T23:00:00Z");assertThat(r.body().path("arguments").path("to").asString()).isEqualTo("2026-11-05T23:30:00Z");});
        calls().reset();var second=admitScripted("previous-period","EQP-001 통신 로그",clockOnly(),List.of(classify(intentWire("communication","EQP-001")),composeSelection("C1")),Optional.empty(),first.conversationId());terminal(second);
        assertThat(calls().requests).singleElement().satisfies(r->{assertThat(r.body().path("arguments").path("from").asString()).isEqualTo("2026-11-05T23:00:00Z");assertThat(r.body().path("arguments").path("to").asString()).isEqualTo("2026-11-05T23:30:00Z");});
    }
    @Test void unsupportedUsesExistingTerminalResultAndDerivedAuditType()throws Exception {
        var job=admitScripted("unsupported-intent","지원 범위 밖 요청",Map.of(),List.of(classify(intentWire("unsupported",null))),Optional.empty(),null);
        assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(result(job.jobId())).containsEntry("status","UNSUPPORTED");assertThat(tools(job.jobId())).isZero();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
        assertThat(jdbc().sql("SELECT task_type FROM gw.audit_event WHERE job_id=:job AND event_type='JOB_CLASSIFIED'").param("job",job.jobId()).query(String.class).single()).isEqualTo("UNSUPPORTED_REQUEST");
    }
}
