package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.budget.CallType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class LlmClassificationNeedsIT extends LlmStubSupport {
 @Override protected Class<?>[] extraSources(){return new Class<?>[]{HarnessObservationsEndpoint.class,WorkflowSourceFixture.class,WorkflowSourceFixture.GitConfiguration.class};}
 @Override protected Map<String,Object> extraApplicationProperties(){var values=new HashMap<>(super.extraApplicationProperties());values.put("test.git-enabled",true);return values;}
 @Test void codeQuestionSearchesTheSelectedIdentifierAndAnswersWithAGitCitation()throws Exception {
  var server=context.getBean(WorkflowSourceFixture.Server.class);
  server.codeText="EquipmentContextRegistry registry = new EquipmentContextRegistry();";
  String answer="EquipmentContextRegistry를 생성해 사용하고 있어요.";
  var job=admitScripted("code-identifier","EquipmentContextRegistry 클래스는 어디서 쓰여?",windowOnly(),List.of(classify(intentWire("code","EquipmentContextRegistry")),composeFacts(answer,"e1")),Optional.empty(),null);
  assertThat(terminal(job)).isEqualTo("SUCCEEDED");
  assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:job").param("job",job.jobId()).query(String.class).list()).containsExactly("git.searchCode");
  // HTTP 대역이 해독한 실제 GitLab search 파라미터를 대조합니다.
  assertThat(server.codeSearches).containsExactly("EquipmentContextRegistry");assertThat(server.codeRefs).containsExactly("main");
  var value=payload(job.jobId());assertThat(value.path("answer").path("kind").asString()).isEqualTo("ANSWER").isNotEqualTo("NO_EVIDENCE");
  assertThat(value.path("answer").path("text").asString()).contains(answer);
  assertThat(value.path("answer").path("citations")).isNotEmpty().anySatisfy(citation->{assertThat(citation.path("sourceType").asString()).isEqualTo("GIT");assertThat(citation.path("location").path("sourceRepo").asString()).isEqualTo("nori-tc-gateway");});
  assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isOne();
  assertThat(calls().requests).isEmpty();
 }
 @Test void codeIntentCannotBeSelectedWithoutCodePermissionAndNeverReadsGit()throws Exception {
  perms=PERMS.stream().filter(p->!p.equals("AI_CODE_READ")).collect(java.util.stream.Collectors.toSet());
  var job=admitScripted("code-hidden","EquipmentContextRegistry 클래스는 어디서 쓰여?",windowOnly(),List.of(classify(intentWire("code","EquipmentContextRegistry"))),Optional.empty(),null);
  assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(tools(job.jobId())).isZero();
  assertThat(context.getBean(WorkflowSourceFixture.Server.class).codeSearches).isEmpty();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
 }
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
