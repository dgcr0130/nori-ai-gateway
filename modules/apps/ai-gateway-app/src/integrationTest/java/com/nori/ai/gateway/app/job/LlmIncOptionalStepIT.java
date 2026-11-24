package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.budget.CallType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
/** 장애 의도는 서버가 권한에 보이는 로그와 추적만 계획합니다. */
class LlmIncOptionalStepIT extends LlmStubSupport {
 @Override protected Class<?>[] extraSources(){return new Class<?>[]{HarnessObservationsEndpoint.class,WorkflowSourceFixture.class,WorkflowSourceFixture.GitConfiguration.class};}
 @Override protected Map<String,Object> extraApplicationProperties(){var values=new HashMap<>(super.extraApplicationProperties());values.put("test.git-enabled",true);return values;}
 @Test void incidentPlansLogsAndTracesWithoutAddingCodeSearchFromQuestionOrLogText()throws Exception {
  var server=context.getBean(WorkflowSourceFixture.Server.class);server.logText="EQP-001 장애 원인 EQUIPMENT_ERROR";
  var job=admitScripted("inc-server-plan","EQP-001 QUOTEDLITERAL 장애 원인 찾아줘",workflowFields(),List.of(classify(intentWire("incident",null)),composeFacts(OBSERVED_TEXT,"e1")),Optional.empty(),null);
  terminal(job);
  assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:id ORDER BY created_at,id").param("id",job.jobId()).query(String.class).list()).containsExactlyInAnyOrder("logs.search","trace.search");
  assertThat(server.codeSearches).isEmpty();assertThat(server.traces.get()).isPositive();assertThat(server.logs.get()).isPositive();assertThat(calls().requests).isEmpty();
  assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();
 }
}
