package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class ClassificationSchemaLimitsIT extends LlmStubSupport {
 @Test void serverSearchQueryPreservesTheWholeMaskedUnicodeQuestion()throws Exception {
  String question="대상"+new String(Character.toChars(0x1F600)).repeat(60)+" 설명";
  var job=admitScripted("literal-query",question,windowOnly(),List.of(classify(intentWire("document",null))),Optional.empty(),null);terminal(job);
  var classification=observations().workflows.stream().filter(value->value.jobId().equals(job.jobId())).findFirst().orElseThrow().classification();assertThat(classification.get("searchQuery")).isEqualTo(question);assertThat(calls().requests).isEmpty();
 }
 @Test void llmCannotSupplyArgumentsTargetsOrTimeWindowsBeforeAnySourceOrComposer()throws Exception {
  for(String field:List.of("toolCalls","targets","timeWindow","searchQuery","retryQuery","taskType")){
   var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(intentWire("incident",null));node.put(field,"invented");
   var job=admitScripted("forbidden-"+field,"EQP-001 조회",windowOnly(),List.of(classify(node.toString())),Optional.empty(),null);
   assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(tools(job.jobId())).isZero();assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();
  }
 }
 @Test void nullRequiredSubjectClarifiesOnlyEqpWithoutABackendRequest()throws Exception {
  var job=admitScripted("missing-eqp","상태 조회",windowOnly(),List.of(classify(intentWire("status",null))),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertClarification(job.jobId(),"PLAN_INVALID",List.of("eqp"));assertThat(tools(job.jobId())).isZero();assertThat(calls().requests).isEmpty();
 }
 @Test void outsideCandidateSubjectIsAContractFailureWithNoRetry()throws Exception {
  var job=admitScripted("invented-subject","EQP-001 조회",windowOnly(),List.of(classify(intentWire("status","INVENTED_001"))),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(tools(job.jobId())).isZero();
 }
}
