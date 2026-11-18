package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.app.SystemConfigFixtures;
import com.nori.ai.gateway.app.web.ResultResponseAssembler;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.llm.ClassificationService;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.repository.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class ClassificationInputsTest {
 final MessageRepository messages=mock(MessageRepository.class);final JobRepository jobs=mock(JobRepository.class);final ResultResponseAssembler access=mock(ResultResponseAssembler.class);
 final Instant now=Instant.parse("2026-11-06T00:00:00Z");final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of(),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),"current");
 final ClassificationService.Request request=new ClassificationService.Request(scope,"conversation","current",new CancellationToken(),new JobRequestInput("u3",3,"현재 질문",Map.of()));
 MessageRepository.Message user(String id,String text){return new MessageRepository.Message(id,"user",text,"[]","[]",null,null);}
 MessageRepository.Message answer(String id,String job,String text){return new MessageRepository.Message(id,"assistant",text,"[]","[]",job,null);}
 void stored(String id,long seq,String message,boolean allowed,String conversation,Instant terminal){
  var job=new JobRepository.Job(id,seq,conversation,"TC","actor","SUCCEEDED",now,now,terminal,null,null,null,id,"key","digest","/v1/query");
  when(access.assemble(scope,id)).thenReturn(new ResultResponseAssembler.Access(scope,job,null,Map.of(),allowed));when(jobs.findRequestInput(scope,id)).thenReturn(Optional.of(new JobRequestInput(message,seq,"저장 질문",Map.of())));
 }
 ClassificationService.Context load(){return new ClassificationInputs(SystemConfigFixtures.systems(),new CapabilityRegistry(List.of(),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy()),messages,access,2,jobs).load(request);}
 @Test void interleavedAdmissionsAndLaterAnswersPairByTheOriginalJobsMessageAndKeepSequence(){
  when(messages.findByConversation(scope,"conversation")).thenReturn(List.of(user("u1","첫 질문"),user("u2","둘째 질문"),user("u3","현재 질문"),answer("a2","j2","둘째 답변"),answer("a1","j1","첫 답변"),user("u4","미래 질문"),answer("a4","j4","미래 답변")));
  stored("j1",1,"u1",true,"conversation",now);stored("j2",2,"u2",true,"conversation",now);stored("j4",4,"u4",true,"conversation",now);
  assertThat(load().history()).containsExactly(new ClassificationService.HistoryPair("첫 질문","첫 답변"),new ClassificationService.HistoryPair("둘째 질문","둘째 답변"));
 }
 @Test void deniedRemovedForeignRunningAndUnboundAnswersNeverBecomeHistory(){
  when(messages.findByConversation(scope,"conversation")).thenReturn(List.of(user("u1","비공개 질문"),answer("a1","denied","비공개 답변"),answer("a2","foreign","타 대화 답변"),answer("a3","running","실행 중"),answer("a4","unbound","손상 결속"),answer("a5","removed","삭제 답변")));
  stored("denied",1,"u1",false,"conversation",now);stored("foreign",1,"u1",true,"other",now);stored("running",1,"u1",true,"conversation",null);stored("unbound",1,"absent",true,"conversation",now);
  when(access.assemble(scope,"removed")).thenThrow(new com.nori.ai.gateway.app.web.UniformNotFound());assertThat(load().history()).isEmpty();
 }
}
