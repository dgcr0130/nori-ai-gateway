package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class ClassificationSchemaLimitsIT extends LlmStubSupport {
 @Test void searchQueryPreservesUnicodeAndTargetsComeOnlyFromQuestionOrSelectedContext()throws Exception {
  String literal="대상"+new String(Character.toChars(0x1F600)).repeat(60);var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("LQ",null,null,null,null));node.putArray("toolCalls");node.put("searchQuery",literal);node.withArray("targets").addObject().put("type","TARGET").put("ref",literal);
  var job=admitScripted("literal-target",literal+" 설명",windowOnly(),List.of(classify(node.toString()),compose("질문을 확인했다.")),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("SUCCEEDED");var classification=observations().workflows.stream().filter(value->value.jobId().equals(job.jobId())).findFirst().orElseThrow().classification();assertThat(classification.get("searchQuery")).isEqualTo(literal);assertThat((List<?>)classification.get("targets")).hasSize(1);assertThat(tools(job.jobId())).isZero();
 }
 @Test void traceRejectsExtraTargetOrQueryFieldsBeforeAnySourceOrComposer()throws Exception {
  for(String field:List.of("targetRef","q")){var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("INC","EQP-001",null,null,null));node.putArray("toolCalls").addObject().put("toolId","trace.search").set("arguments",SchemaContracts.JSON.createObjectNode().put(field,"EQP-001"));var job=admitScripted("trace-extra-"+field,"EQP-001 조회",windowOnly(),List.of(classify(node.toString())),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(tools(job.jobId())).isZero();assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isZero();}
 }
 @Test void absentRequiredInputReturnsOnlyTheFieldNamesAndMakesNoBackendRequest()throws Exception {var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("LQ",null,null,null,null));node.putArray("toolCalls").addObject().put("toolId","tc.eqp.getOperationalSnapshot").set("arguments",SchemaContracts.parse("{}"));var job=admitScripted("missing-eqp","상태 조회",windowOnly(),List.of(classify(node.toString())),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertClarification(job.jobId(),"PLAN_INVALID",List.of("eqp"));assertThat(tools(job.jobId())).isZero();assertThat(calls().requests).isEmpty();}
 @Test void impossibleDatesAndInventedTargetsAreContractFailuresWithNoRetry()throws Exception {for(boolean date:List.of(false,true)){var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(wire("LQ","EQP-001",null,null,null));if(date)node.set("timeWindow",SchemaContracts.JSON.valueToTree(Map.of("from","2026-02-30T00:00Z","to","2026-03-01T00:00Z")));else node.withArray("targets").addObject().put("type","TARGET").put("ref","INVENTED");var job=admitScripted("invalid-date-"+date,"EQP-001 조회",windowOnly(),List.of(classify(node.toString())),Optional.empty(),null);assertThat(terminal(job)).isEqualTo("FAILED");assertThat(result(job.jobId())).containsEntry("error_code","CONTRACT_INVALID");assertThat(scripts().scriptedCalls(job.jobId(),CallType.CLASSIFY)).isOne();assertThat(tools(job.jobId())).isZero();}}
}
