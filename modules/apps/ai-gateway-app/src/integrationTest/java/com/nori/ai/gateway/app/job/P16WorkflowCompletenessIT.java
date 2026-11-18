package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.core.registry.TaskType;
import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 실제 앱·PG·Mock·loopback Source를 묶어 새 READ와 단일 거부 경계가 terminal까지 보존되는지 검사합니다. */
class P16WorkflowCompletenessIT extends P09QueueSupport {
    @Override protected Class<?>[] extraSources(){return new Class<?>[]{HarnessObservationsEndpoint.class,WorkflowSourceFixture.class};}
    @Override protected Map<String,Object> claims(String actor,String dlg){var values=new HashMap<>(super.claims(actor,dlg));values.put("perm",List.of("EQP_READ","COMM_LOG_READ","DLQ_READ","AI_LOG_READ","AI_TRACE_READ","AI_CODE_READ"));return values;}
    @Override protected com.nori.ai.gateway.core.scope.VerifiedSystemScope scope(String actor){var old=super.scope(actor);return new com.nori.ai.gateway.core.scope.VerifiedSystemScope(old.boundSystem(),old.authenticatedServiceRef(),old.actorRef(),old.sub(),Set.of("EQP_READ","COMM_LOG_READ","DLQ_READ","AI_LOG_READ","AI_TRACE_READ","AI_CODE_READ"),old.pdg(),old.issuedAt(),old.expiresAt(),old.requestId());}
    private String run(String key,Map<String,Object> seam)throws Exception {
        var scenario=new HashMap<String,Object>();scenario.put("taskType","INCIDENT_ANALYSIS");var window=json.valueToTree(workflowFields()).path("context").path("timeWindow");scenario.put("toolCalls",List.of(Map.of("toolId","tc.eqp.getOperationalSnapshot","arguments",Map.of("eqp","EQP-001")),Map.of("toolId","tc.commLogs.search","arguments",Map.of("eqp","EQP-001","from",window.path("from").asString(),"to",window.path("to").asString())),Map.of("toolId","logs.search","arguments",Map.of("targetRef","EQP-001")),Map.of("toolId","trace.search","arguments",Map.of())));if(!seam.isEmpty())scenario.put("seam",seam);
        assertThat(hook("/__test/arm","POST",json.writeValueAsString(Map.of("actorSub","actor","requestKey",key,"scenario",scenario)),hookToken).statusCode()).isEqualTo(204);
        var accepted=inRequestSpan(()->context.getBean(JobAdmissionService.class).accept(scope("actor"),new JobAdmissionService.AdmissionRequest("/v1/query",null,"설비 장애 통신 로그 조회",workflowFields(),key,registeredDlg("actor"))));
        await(()->!Set.of("QUEUED","RUNNING").contains(state(accepted.jobId())));return accepted.jobId();
    }
    private tools.jackson.databind.JsonNode payload(String job){return json.readTree(jdbc().sql("SELECT payload::text FROM gw.result WHERE job_id=:id").param("id",job).query(String.class).single());}
    @Test void oneStandardReadPreservesWarningsNanosecondRangeAndRemainsUncached()throws Exception {
        context.getBean(StandardSystemStub.class).select("comm-trim");String job=run("comm-trim",Map.of());
        assertThat(calls().count("getEqpSnapshot")).isOne();assertThat(calls().count("getCommLogs")).isOne();
        var stored=json.readTree(jdbc().sql("SELECT payload::text FROM gw.tool_execution WHERE job_id=:id AND capability_id='tc.commLogs.search'").param("id",job).query(String.class).single()).path("result");
        assertThat(stored.path("warnings").toString()).contains("RETENTION_UNVERIFIED");
        var ranges=stored.path("metadata").path("unverifiedRanges");assertThat(ranges).hasSize(1);
        var requested=json.valueToTree(workflowFields()).path("context").path("timeWindow");
        assertThat(ranges.get(0).path("from").asString()).isEqualTo(requested.path("from").asString());
        assertThat(ranges.get(0).path("to").asString()).isEqualTo(NOW.minusSeconds(600).toString());
        assertThat(stored.path("metadata").path("missingRanges")).isEmpty();
        assertThat(payload(job).toString()).contains("RETENTION_UNVERIFIED");
        assertThat(jdbc().sql("SELECT count(*) FROM gw.cache_entry WHERE capability_id='tc.commLogs.search'").query(Long.class).single()).isZero();
    }
    @Test void unsupportedAvailabilityReachesValidatorSixAndExecutesNoBusinessTool()throws Exception {
        for(String kind:List.of("PLANNED","UNSUPPORTED","DISABLED","PHASE_INACTIVE")) {
            String job=run("reject-"+kind,Map.of("kind",kind,"capabilityId","tc.eqp.getOperationalSnapshot"));var result=payload(job);
            assertThat(result.path("status").asString()).isEqualTo("UNSUPPORTED");
            var trace=result.path("validationTrace");assertThat(java.util.stream.StreamSupport.stream(trace.spliterator(),false).filter(row->row.path("step").asInt()==6)).singleElement().satisfies(row->{assertThat(row.path("passed").asBoolean()).isFalse();assertThat(row.path("reason").asString()).isEqualTo(kind);});
            assertThat(calls().count("getEqpSnapshot")).isZero();assertThat(calls().count("getCommLogs")).isZero();
        }
    }
    @Test void dispatchOnlySecondBusinessOwnerMismatchKeepsFirstReadAndRejectsBeforeCommCall()throws Exception {
        String job=run("dispatch-second",Map.of("kind","DISPATCH_OWNER_OVERRIDE","capabilityId","tc.commLogs.search","ownerSystem","WM"));
        assertThat(calls().count("getEqpSnapshot")).isOne();assertThat(calls().count("getCommLogs")).isZero();
        assertThat(jdbc().sql("SELECT count(*) FROM gw.audit_event WHERE job_id=:id AND event_type='TOOL_BLOCKED' AND decision='OWNER_MISMATCH'").param("id",job).query(Long.class).single()).isEqualTo(1);
        assertThat(jdbc().sql("SELECT count(*) FROM gw.audit_event WHERE job_id=:id AND event_type='TOOL_BLOCKED' AND decision='OWNER_MISMATCH' AND tool_id='tc.commLogs.search' AND status='FAILED' AND error_code='SYSTEM_SCOPE_MISMATCH'").param("id",job).query(Long.class).single()).isEqualTo(1);
        assertThat(jdbc().sql("SELECT count(*) FROM gw.tool_execution WHERE job_id=:id AND capability_id='tc.commLogs.search'").param("id",job).query(Long.class).single()).isZero();
    }
    @Test void actualSourceServerRequestsMatchAuthenticatedJobCaptureAndUnboundJobsCannotReadIt()throws Exception {
        String job=run("source-capture",Map.of());var server=context.getBean(WorkflowSourceFixture.Server.class);
        assertThat(server.logs).hasValue(4);var response=hook("/__test/source-observations/"+job,"GET",null,hookToken);assertThat(response.statusCode()).isEqualTo(200);var body=json.readTree(response.body());
        var logs=java.util.stream.StreamSupport.stream(body.path("observations").spliterator(),false).filter(value->value.path("capabilityId").asString().equals("logs.search")).findFirst().orElseThrow();
        assertThat(logs.path("status").asString()).isEqualTo("COMPLETE");assertThat(logs.path("count").asInt()).isEqualTo(4);
        assertThat(java.util.stream.StreamSupport.stream(logs.path("attempts").spliterator(),false).map(value->value.path("digest").asString()).toList()).containsExactlyInAnyOrderElementsOf(server.requestDigests);
        assertThat(response.body()).doesNotContain("EQP-001","query_range","127.0.0.1","2026-");
        assertThat(hook("/__test/source-observations/"+job,"GET",null,null).statusCode()).isEqualTo(404);
        assertThat(hook("/__test/source-observations/never-admitted","GET",null,hookToken).statusCode()).isEqualTo(404);
    }
}
