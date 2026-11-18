package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.budget.CallType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class LlmClassificationNeedsIT extends LlmStubSupport {
 @Test void acceptedExecutionListsBindZeroThroughFourCallsWithoutRetry()throws Exception {
  for(int count=0;count<=4;count++){calls().reset();var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("LQ","EQP-001",null,null,null));var selected=node.putArray("toolCalls");for(int i=0;i<count;i++)selected.addObject().put("toolId","tc.eqp.getOperationalSnapshot").set("arguments",SchemaContracts.parse("{\"eqp\":\"EQP-001\"}"));var job=admitScripted("calls-"+count,"EQP-001 상태",windowOnly(),List.of(classify(node.toString()),compose("관측했다[e1].")),Optional.empty(),null);terminal(job);var bound=observations().workflows.stream().filter(value->value.jobId().equals(job.jobId())).findFirst().orElseThrow();assertThat((List<?>)bound.classification().get("toolCalls")).hasSize(count);assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(calls().count("tc.eqp.getOperationalSnapshot")).isEqualTo(Math.min(3,count));if(count==4)assertThat(payload(job.jobId()).path("trace").toString()).contains("TOOL_LIMIT_REACHED");}
 }
 @Test void unknownFieldsAndToolsStopAfterOneClassificationWithoutToolsOrDefaultBinding()throws Exception {
  for(boolean unknownField:List.of(false,true)){var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("LQ","EQP-001",null,null,null));if(unknownField)node.put("systemId","WM");else ((tools.jackson.databind.node.ObjectNode)node.path("toolCalls").get(0)).put("toolId","unknown");var job=admitScripted("invalid-"+unknownField,"EQP-001 상태",windowOnly(),List.of(classify(node.toString())),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(tools(job.jobId())).isZero();assertThat(observations().workflows.stream().filter(value->value.jobId().equals(job.jobId()))).isEmpty();assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();}
 }
 @Test void requestContextCannotSelectExtraTools()throws Exception {var fields=new HashMap<String,Object>(windowOnly());fields.put("toolCalls",List.of(Map.of("toolId","unknown","arguments",Map.of())));var job=admitScripted("injected-calls","EQP-001 상태",fields,List.of(classify(wire("LQ","EQP-001",null,null,null)),compose("관측했다[e1].")),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(tools(job.jobId())).isOne();assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:job").param("job",job.jobId()).query(String.class).list()).containsExactly("tc.eqp.getOperationalSnapshot");}
}
