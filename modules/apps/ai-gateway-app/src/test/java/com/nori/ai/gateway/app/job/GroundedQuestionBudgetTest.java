package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.observability.AuditRecorder;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.evidence.EvidenceAssembler;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.llm.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import com.nori.ai.gateway.persistence.repository.MessageRepository;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 분류가 이미 허용한 요청에서 실제 grounded 계수와 종결 계약을 확인하며 모델·HTTP를 호출하지 않습니다. */
class GroundedQuestionBudgetTest {
    private enum Case { QUESTION_OVERFLOW, NO_EVIDENCE, PARENT_OVERFLOW }
    private static final String QUESTION="설정 조건은 무엇인가요?";

    @Test void irreducibleQuestionAndMandatoryInstructionsBecomeTerminalBudgetErrorBeforeInsufficientSkip() throws Exception {
        var terminal=execute(Case.QUESTION_OVERFLOW);
        assertThat(terminal.jobStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.FAILED);
        assertThat(terminal.errorCode()).isEqualTo(ErrorCode.INPUT_BUDGET_EXCEEDED);
        assertThat(terminal.eventType()).isEqualTo("job.failed");
    }

    @Test void ordinaryMissingEvidenceRemainsSuccessfulNoEvidence() throws Exception { assertNoEvidence(execute(Case.NO_EVIDENCE)); }

    @Test void wholeParentTooLargeIsSkippedWithoutBecomingAQuestionBudgetError() throws Exception { assertNoEvidence(execute(Case.PARENT_OVERFLOW)); }

    private void assertNoEvidence(TerminalSpec terminal) {
        assertThat(terminal.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);
        assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.EMPTY);assertThat(terminal.errorCode()).isNull();
        assertThat(SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer")).path("kind").asString()).isEqualTo("NO_EVIDENCE");
        assertThat(terminal.commitData().orElseThrow().snapshot().orElseThrow().evidence()).isEmpty();
    }

    private TerminalSpec execute(Case scenario) throws Exception {
        try(var tokens=new HfTokenizerCounter(InferenceFixtures.settings("http://127.0.0.1:1/v1"));var language=new MemoryDocumentKeywordIndex()) {
            var now=Instant.now();var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("READ"),"pdg",now,now.plusSeconds(60),"job");
            var classification=new Classification(TaskType.KNOWLEDGE_QUERY,List.of(),List.of(),Map.of(),"설정 조건","설정 범위");
            var job=new JobContext("job","conversation",scope,"delegation",new CancellationToken(),InferenceMode.ENABLED,classification.jobClassification(),new JobRequestInput("message",1,QUESTION,Map.of()));
            PromptTokenCounting counter=messages->tokens.countText(InferenceFixtures.renderer().render(messages));
            var countedMessages=new ArrayList<List<ChatMessage>>();var initialPolicy=TokenBudgetPolicy.current();
            var probe=new ComposerService(mock(InferenceClient.class),new PromptReducer(messages->{countedMessages.add(List.copyOf(messages));return counter.count(messages);},initialPolicy),initialPolicy,tokens::countText,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"일반 도움말",language);
            probe.groundedQuestionFits(job);
            var mandatoryMessages=countedMessages.getLast();
            assertThat(SchemaContracts.JSON.readTree(mandatoryMessages.getLast().content()).path("facts").isArray()).isTrue();
            int mandatory=counter.count(mandatoryMessages);
            var current=TokenBudgetPolicy.current();var budgets=new EnumMap<CallType,TokenBudgetPolicy.CallBudget>(CallType.class);
            for(var type:CallType.values())budgets.put(type,current.budget(type));
            int limit=scenario==Case.QUESTION_OVERFLOW?mandatory-1:scenario==Case.PARENT_OVERFLOW?mandatory+1:current.budget(CallType.COMPOSER).inputLimit();
            var original=budgets.get(CallType.COMPOSER);budgets.put(CallType.COMPOSER,new TokenBudgetPolicy.CallBudget(limit,original.answerLimit(),original.thinkingLimit(),original.reserveTokens()));
            var policy=new TokenBudgetPolicy(8192,Math.min(3072,limit),budgets);
            var inference=mock(InferenceClient.class);var expiry=new ExpiryCheckpoint();
            var composer=new ComposerService(inference,new PromptReducer(counter,policy),policy,tokens::countText,expiry,LlmCallObserver.NONE,attempt->{},"일반 도움말",language);
            assertThat(composer.groundedQuestionFits(job)).isEqualTo(scenario!=Case.QUESTION_OVERFLOW);
            var location=new DocumentLocation("bucket","guide.md","a".repeat(64),"설정",1,5);
            String body="설정 조건은 현재 값을 유지한다. ".repeat(15);
            var child=new DocumentCorpus.Child(UUID.randomUUID(),0,location,"설정","설정 조건은 현재 값을 유지한다.",List.of(),new float[]{1});
            var parent=new DocumentCorpus.Parent(UUID.randomUUID(),location,"설정",body,AnswerSnapshot.sha(body),List.of(child));
            var hit=new DocumentSearch.Hit(parent,child,Set.of("READ"));
            DocumentSearch search=(bound,query,targets,settings)->new DocumentSearch.Round(scenario==Case.PARENT_OVERFLOW?List.of(hit):List.of(),List.of(),List.of(),List.of());
            var inputs=new WorkflowGrounding.Inputs() {
                public WorkflowGrounding.SourceUnits collect(JobContext ignored,WorkflowExecutionPlan executedPlan,WorkflowOutcome outcome){return new WorkflowGrounding.SourceUnits(List.of(),Set.of());}
                public Map<String,String> glossary(JobContext ignored){return Map.of();}
            };
            var grounding=new WorkflowGrounding(search,language,SearchSettings.defaults(),new EvidenceSelection(tokens::countText,policy.evidenceTokens()),inputs,Clock.systemUTC(),expiry,composer::fitsGrounded);
            var registry=new CapabilityRegistry(List.of(),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
            var outcome=new WorkflowOutcome(List.of(),List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
            var executor=mock(com.nori.ai.gateway.core.workflow.WorkflowExecutor.class);when(executor.run(any(),any(),any(),any())).thenReturn(outcome);
            var adapter=new WorkflowExecutionAdapter(registry,new WorkflowPlanBuilder(registry),executor,mock(WorkflowResourceResolverFactory.class),new TimeWindowResolver(),mock(MessageRepository.class),expiry,new ContentSanitizer(),new TerminalOutcomeAssembler(),new ConversationSummaryBuilder(),new EvidenceAssembler(),mock(AuditRecorder.class),Optional.of(composer),new LogSummarizer(),Optional.empty(),Optional.of(grounding));
            var terminal=adapter.execute(job);verifyNoInteractions(inference);return terminal;
        }
    }
}
