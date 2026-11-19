package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
/** 선택한 실행 목록만 실행하고 로그 내용이 후속 도구의 인자를 바꾸지 않는지 확인합니다. */
class LlmIncOptionalStepIT extends LlmStubSupport {
 @Override protected Class<?>[] extraSources(){return new Class<?>[]{HarnessObservationsEndpoint.class,WorkflowSourceFixture.class,WorkflowSourceFixture.GitConfiguration.class};}
 @Override protected Map<String,Object> extraApplicationProperties(){var values=new HashMap<>(super.extraApplicationProperties());values.put("test.git-enabled",true);return values;}
 @Test void incidentExecutesOnlySelectedToolsAndUsesClassifiedLiteralCodeQuery()throws Exception {
  var server=context.getBean(WorkflowSourceFixture.Server.class);server.logText="EQP-001 장애 원인 EQUIPMENT_ERROR";
  var wire=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("INC","EQP-001",null,null,null));var calls=wire.putArray("toolCalls");
  calls.addObject().put("toolId","logs.search").set("arguments",SchemaContracts.JSON.valueToTree(Map.of("targetRef","EQP-001")));
  calls.addObject().put("toolId","git.searchCode").set("arguments",SchemaContracts.JSON.valueToTree(Map.of("q","QUOTEDLITERAL")));
  var job=admitScripted("inc-selected","EQP-001 QUOTEDLITERAL 장애 원인 찾아줘",workflowFields(),List.of(classify(wire.toString()),compose("로그와 코드 자료를 확인했다[e1].")),Optional.empty(),null);
  assertThat(terminal(job)).isEqualTo("SUCCEEDED");
  assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:id ORDER BY created_at,id").param("id",job.jobId()).query(String.class).list()).containsExactlyInAnyOrder("logs.search","git.searchCode");
  assertThat(server.codeSearches).singleElement().isEqualTo("QUOTEDLITERAL");assertThat(server.traces).hasValue(0);assertThat(calls().requests).isEmpty();
  assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isOne();
 }
}
