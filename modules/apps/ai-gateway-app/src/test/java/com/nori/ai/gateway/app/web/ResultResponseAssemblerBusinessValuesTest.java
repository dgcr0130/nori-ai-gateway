package com.nori.ai.gateway.app.web;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.repository.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class ResultResponseAssemblerBusinessValuesTest {
 final Instant now=Instant.parse("2026-11-06T00:00:00Z");final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.valueOf("HRM"),"service",new ActorRef("actor"),"actor",Set.of(),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),"request");
 final JobRepository jobs=mock(JobRepository.class);final ConversationRepository conversations=mock(ConversationRepository.class);final ResultRepository results=mock(ResultRepository.class);
 ResultResponseAssembler reader(String payload){
  when(jobs.find(scope,"job")).thenReturn(Optional.of(new JobRepository.Job("job",1,"conversation","HRM","actor","SUCCEEDED",now,now,now,null,null,null,"job","key","digest","/v1/query")));
  when(conversations.find(scope,"conversation")).thenReturn(Optional.of(new ConversationRepository.Conversation("conversation","HRM","actor",now,now,"ACTIVE")));
  when(results.find(scope,"job")).thenReturn(Optional.of(new ResultRepository.Result("SUCCESS",null,payload,null,"{\"sources\":[],\"knowledgeDocuments\":[]}")));
  return new ResultResponseAssembler(jobs,conversations,results,new ResultAccessEvaluator(),mock(KnowledgeDocumentStateReader.class));
 }
 String payload(){return """
 {"blocks":[{"type":"table","dataRef":{"ownerSystem":"HRM","ref":"data://HRM/tool/record"},"content":{"columns":["ref","text","values"],"rows":[{"ref":"record-1","text":"관측 자료","values":{"ownerSystem":"업무 분류","systemId":17,"nested":{"ownerSystem":"다른 업무 값"}}}]}}]}
 """;}
 @Test void businessValuesNamedLikeScopeMetadataRemainOrdinaryValues(){var result=reader(payload()).assemble(scope,"job");assertThat(result.allowed()).isTrue();var tree=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.valueToTree(result.response());assertThat(tree.path("result").path("payload").path("blocks").get(0).path("content").path("rows").get(0).path("values").path("systemId").asInt()).isEqualTo(17);}
 @Test void outerScopeMetadataAndForeignReferencesRemainDenied(){
  var json=com.nori.ai.gateway.core.registry.SchemaContracts.JSON;var owner=(tools.jackson.databind.node.ObjectNode)json.readTree(payload());((tools.jackson.databind.node.ObjectNode)owner.path("blocks").get(0).path("dataRef")).put("ownerSystem","WM");
  var ref=(tools.jackson.databind.node.ObjectNode)json.readTree(payload());((tools.jackson.databind.node.ObjectNode)ref.path("blocks").get(0).path("dataRef")).put("ref","system://WM/private");
  var root=(tools.jackson.databind.node.ObjectNode)json.readTree(payload());root.put("systemId","WM");
  for(String invalid:List.of(owner.toString(),ref.toString(),root.toString())){var result=reader(invalid).assemble(scope,"job");assertThat(result.allowed()).isFalse();assertThat(result.response()).doesNotContainKey("result");}
 }
}
