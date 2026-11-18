package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.core.registry.TaskType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class EvidenceWorkflowContractIT extends P09QueueSupport {
 @Test void concurrentJobsOfOneConversationUseOnlyTheirOwnExecutionLists()throws Exception {
  pauses().hold(PausePointRegistry.Point.DISPATCH_HOLD,"*");
  hooks().arm("actor","first",StandardSystemConfiguration.scenario(TaskType.LIVE_QUERY,Set.of()));var first=accept("actor","first","첫 상태",null,registeredDlg("actor"));
  hooks().arm("actor","second",StandardSystemConfiguration.scenario(TaskType.GENERAL_QUERY,Set.of()));var second=accept("actor","second","도움말",first.conversationId(),registeredDlg("actor"));pauses().release(PausePointRegistry.Point.DISPATCH_HOLD,"*");await(()->state(first.jobId()).equals("SUCCEEDED"));await(()->state(second.jobId()).equals("SUCCEEDED"));
  assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:job").param("job",first.jobId()).query(String.class).list()).containsExactly("tc.eqp.getOperationalSnapshot");assertThat(jdbc().sql("SELECT count(*) FROM gw.tool_execution WHERE job_id=:job").param("job",second.jobId()).query(Integer.class).single()).isZero();assertThat(calls().requests).hasSize(1);assertThat(calls().requests.getFirst().body().path("jobId").asString()).isEqualTo(first.jobId());
 }
}
