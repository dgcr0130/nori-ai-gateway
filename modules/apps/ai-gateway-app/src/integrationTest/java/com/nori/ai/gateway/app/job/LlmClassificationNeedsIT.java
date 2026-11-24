package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.budget.CallType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class LlmClassificationNeedsIT extends LlmStubSupport {
 @Test void oneThroughFourIdenticalNeedsProduceOneReadWithoutRetry()throws Exception {
  for(int count=1;count<=4;count++){
   calls().reset();var node=SchemaContracts.JSON.createObjectNode();var needs=node.putArray("needs");for(int i=0;i<count;i++)needs.addObject().put("intent","status").put("subject","EQP-001");
   var job=admitScripted("needs-"+count,"EQP-001 상태",windowOnly(),List.of(classify(node.toString()),composeFacts(OBSERVED_TEXT,"e1")),Optional.empty(),null);
   assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(calls().count("tc.eqp.getOperationalSnapshot")).isOne();assertThat(tools(job.jobId())).isOne();
  }
 }
 @Test void unknownFieldsIntentsSubjectsAndZeroOrFiveNeedsStopWithoutDefaultBinding()throws Exception {
  var invalid=new ArrayList<String>(List.of(intentWire("unknown","EQP-001"),intentWire("status","INVENTED_001"),"{\"needs\":[]}"));
  var five=SchemaContracts.JSON.createObjectNode();var needs=five.putArray("needs");for(int i=0;i<5;i++)needs.addObject().put("intent","status").put("subject","EQP-001");invalid.add(five.toString());
  var extra=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(intentWire("status","EQP-001"));extra.put("systemId","WM");invalid.add(extra.toString());
  for(int i=0;i<invalid.size();i++){var job=admitScripted("invalid-need-"+i,"EQP-001 상태",windowOnly(),List.of(classify(invalid.get(i))),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(tools(job.jobId())).isZero();assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();assertThat(observations().workflows.stream().filter(value->value.jobId().equals(job.jobId()))).isEmpty();}
 }
 @Test void requestContextCannotSelectExtraTools()throws Exception {var fields=new HashMap<String,Object>(windowOnly());fields.put("toolCalls",List.of(Map.of("toolId","unknown","arguments",Map.of())));var job=admitScripted("injected-calls","EQP-001 상태",fields,List.of(classify(intentWire("status","EQP-001")),composeFacts(OBSERVED_TEXT,"e1")),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(tools(job.jobId())).isOne();assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:job").param("job",job.jobId()).query(String.class).list()).containsExactly("tc.eqp.getOperationalSnapshot");}
}
