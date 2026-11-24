package com.nori.ai.gateway.app.job;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.nori.ai.gateway.app.SystemConfigFixtures;
import com.nori.ai.gateway.app.config.*;
import com.nori.ai.gateway.app.job.recovery.ReadinessGate;
import com.nori.ai.gateway.app.observability.AuditRecorder;
import com.nori.ai.gateway.app.security.ScopeAssembler;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.llm.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.integration.inference.*;
import com.nori.ai.gateway.persistence.maintenance.QueueMaintenanceRepository;
import com.nori.ai.gateway.persistence.queue.*;
import com.nori.ai.gateway.persistence.repository.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.slf4j.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DetailedQuestionLoggingTest {
    /** 실제 접수의 동기화·afterCommit 경계를 유지하되 DB I/O만 대역으로 바꿉니다. */
    private static final class Transactions extends AbstractPlatformTransactionManager {
        @Override protected Object doGetTransaction(){return new Object();}
        @Override protected void doBegin(Object transaction,TransactionDefinition definition){}
        @Override protected void doCommit(DefaultTransactionStatus status){}
        @Override protected void doRollback(DefaultTransactionStatus status){}
    }
    @Test void admissionClassificationLlmAndTerminalLogsKeepBodiesAndJobContext() throws Exception {
        var logger=(ch.qos.logback.classic.Logger)LoggerFactory.getLogger("com.nori.ai.gateway");
        var captured=new ListAppender<ILoggingEvent>(){@Override protected void append(ILoggingEvent event){event.prepareForDeferredProcessing();super.append(event);}};
        boolean bridge=!org.slf4j.bridge.SLF4JBridgeHandler.isInstalled();if(bridge)org.slf4j.bridge.SLF4JBridgeHandler.install();captured.start();logger.addAppender(captured);
        try {
            var now=Instant.now();var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of(),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),UUID.randomUUID().toString());
            var conversations=mock(ConversationRepository.class);var messages=mock(MessageRepository.class);var jobs=mock(JobRepository.class);var events=mock(JobEventRepository.class);
            when(conversations.insert(any(),anyString())).thenReturn(true);when(conversations.touchAtMessage(any(),anyString(),anyString())).thenReturn(true);
            when(messages.insert(any(),anyString(),any())).thenReturn(true);when(jobs.insert(any(),any(),anyString())).thenReturn(true);when(events.insert(any(),anyString(),anyString(),anyString(),anyBoolean())).thenReturn(true);
            var gate=mock(ReadinessGate.class);when(gate.admissionReady()).thenReturn(true);
            var admission=new JobAdmissionService(new Transactions(),mock(QueueMaintenanceRepository.class),mock(IdempotentAdmissionLookup.class),()->true,Optional.empty(),new CapacityProfile(1,1,4,Duration.ofSeconds(60),Duration.ofSeconds(180)),mock(QueueJobRepository.class),conversations,messages,jobs,events,new ContentSanitizer(),Optional.empty(),Optional.empty(),event->{},gate,mock(AuditRecorder.class));
            String question="설비 상태 질문 원문";var accepted=admission.accept(scope,new JobAdmissionService.AdmissionRequest("/v1/query",null,question,Map.of(),"logging-test","d".repeat(22)));
            String answer="{\"needs\":[{\"intent\":\"help\",\"subject\":null}]}";
            ChatTransport transport=new ChatTransport(){@Override public Response send(CallType type,List<ChatMessage> input,int max){return new Response(answer,"stop",10,10);}@Override public Response send(CallType type,List<ChatMessage> input,int max,Duration timeout,StructuredOutput output){return send(type,input,max);}};
            var policy=TokenBudgetPolicy.current();var inference=new SpringAiOpenAiInferenceClient(transport,mock(org.springframework.ai.openai.OpenAiEmbeddingModel.class),new PreCallBudgetAssert(input->10,policy,text->10),new TruncationDetector(),new GenerationSemaphore(1));
            var registry=new CapabilityRegistry(List.of(),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
            var classification=new ClassificationService(inference,policy,text->10,input->10,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"분류 프롬프트 본문",request->new ClassificationService.Context(registry.view(request.scope()),List.of(),Map.of(),0,List.of(),com.nori.ai.gateway.app.SystemConfigFixtures.intents()));
            var row=new JobScopeRecord(OwnerSystem.TC,"actor",Set.of(),"a".repeat(64),accepted.jobId(),now,accepted.conversationId(),"RUNNING");
            when(jobs.find(any(),eq(accepted.jobId()))).thenReturn(Optional.of(new JobRepository.Job(accepted.jobId(),1,accepted.conversationId(),"TC","actor","RUNNING",now,now,null,null,null,null,accepted.jobId(),"logging-test","digest","/v1/query")));
            var input=mock(com.nori.ai.gateway.core.port.JobRequestInputReader.class);when(input.findRequestInput(any(),eq(accepted.jobId()))).thenReturn(Optional.of(new JobRequestInput(accepted.messageId(),1,question,Map.of())));
            var delegations=mock(JobDelegationReader.class);when(delegations.forWorker(accepted.jobId())).thenReturn(Optional.of("d".repeat(22)));
            var terminal=mock(JobTerminalCommitter.class);var slots=new SlotLedger();var cancellation=slots.claim(accepted.jobId());
            new JobWorker(delegations,new ScopeAssembler(),SystemConfigFixtures.systems(),job->TerminalSpec.outcome(JobStatus.SUCCEEDED,ResultStatus.SUCCESS,null,Map.of("answer","답변")),terminal,jobs,new InferenceModeProperties(InferenceMode.ENABLED),Optional.empty(),Optional.empty(),Optional.empty(),slots,input,Optional.of(classification)).run(row,cancellation);
            verify(terminal).commit(eq(accepted.jobId()),argThat(result->result.jobStatus()==JobStatus.SUCCEEDED));
            String logs=captured.list.stream().map(ILoggingEvent::getFormattedMessage).reduce("",String::concat);
            assertThat(logs).contains("JOB_ADMITTED","JOB_CLASSIFIED","LLM_CHAT_REQUEST","LLM_CHAT_RESPONSE","JOB_TERMINATED",question,"분류 프롬프트 본문","GENERAL_QUERY",accepted.jobId());
            assertThat(captured.list.stream().filter(event->event.getMessage().startsWith("LLM_CHAT_")).toList()).isNotEmpty().allSatisfy(event->assertThat(event.getMDCPropertyMap()).containsEntry("jobId",accepted.jobId()));
            assertThat(MDC.get("jobId")).isNull();assertThat(MDC.get("conversationId")).isNull();assertThat(MDC.get("ownerSystem")).isNull();
        } finally {logger.detachAppender(captured);captured.stop();MDC.clear();if(bridge)org.slf4j.bridge.SLF4JBridgeHandler.uninstall();}
    }
}
