package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.registry.WorkflowDefinition;
import com.nori.ai.gateway.core.workflow.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static org.assertj.core.api.Assertions.*;

class GroundedTransmissionBudgetTest {
    private JobContext job(String question){return new JobContext("job","conversation",SCOPE,"delegation",new CancellationToken(),InferenceMode.ENABLED,Map.of(),new JobRequestInput("m",1,question,Map.of()));}
    private static ComposerService.Input input(String question,List<EvidenceUnit> units,Optional<ClassificationService.HistoryPair> history){return new ComposerService.Input(List.of(),List.of(),"",List.of(),question,List.of(),Optional.of(EvidenceSelection.json(units)),history);}
    private ComposerService composer(List<List<ChatMessage>> sent,PromptTokenCounting counter) {
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){sent.add(messages);return reply("{\"sentences\":[{\"text\":\"확인했습니다.\",\"factIds\":[\"e1\"]}]}");}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var policy=TokenBudgetPolicy.current();return new ComposerService(client,new PromptReducer(counter,policy),policy,CODEPOINTS,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"일반 도움말",text->List.of());
    }
    @Test void wholeUnitSelectionCountsActualFactsAndHistoryAndFinalTransmissionAgrees() {
        var sent=new ArrayList<List<ChatMessage>>();var composer=composer(sent,messages->100);var job=job("질문");
        var history=Optional.of(new ClassificationService.HistoryPair("이전 질문","문맥".repeat(500)));
        List<EvidenceUnit> boundary=null;
        for(int size=120;size<1400;size+=5) {
            var units=new ArrayList<EvidenceUnit>();for(int i=0;i<5;i++)units.add(FactCheckTest.unit("가".repeat(size/2)+". "+"나".repeat(size/2)+"."));
            if(CODEPOINTS.count(EvidenceSelection.json(units))<=3072&&!composer.fitsGrounded(job,units,history)){boundary=units;break;}
        }
        assertThat(boundary).as("원래 JSON은 들어가지만 실제 전송 메타데이터/문맥은 넘치는 경계").isNotNull();
        var selected=new EvidenceSelection(CODEPOINTS,3072).select(List.of(),boundary,SearchSettings.defaults(),units->composer.fitsGrounded(job,units,history));
        assertThat(selected.units()).isNotEmpty().hasSizeLessThan(boundary.size());
        var originalBodies=boundary.stream().map(EvidenceUnit::body).toList();
        assertThat(selected.units()).allSatisfy(unit->assertThat(originalBodies).contains(unit.body()));
        var result=composer.compose(job,input("질문",selected.units(),history));assertThat(result.calls()).isOne();
        var data=SchemaContracts.JSON.readTree(sent.getFirst().getLast().content());
        assertThat(CODEPOINTS.count(com.nori.ai.gateway.core.evidence.EvidenceJson.write(data))).isLessThanOrEqualTo(3072);
        assertThat(data.has("history")).isTrue();assertThat(data.path("facts")).hasSize(selected.units().size());assertThat(data.has("priorityIds")).isFalse();
        assertThat(selected.json()).doesNotContain("[C1@L");
    }

    @Test void irreducibleInputStopsBeforeInferenceButFormerCandidateMarkersAreOrdinaryData() {
        var sent=new ArrayList<List<ChatMessage>>();var tooLarge=composer(sent,messages->4609);var job=job("원래 질문");
        assertThat(tooLarge.compose(job,input("원래 질문",List.of(FactCheckTest.unit("현재 본문이다.")),Optional.empty())).outcome()).isEqualTo(ComposerOutcome.INPUT_BUDGET_EXCEEDED);
        assertThat(sent).isEmpty();
        var safe=composer(sent,messages->100);var answer=safe.compose(job,input("원래 질문",List.of(FactCheckTest.unit("[C1@L1]충돌[/]")),Optional.empty()));
        assertThat(answer.calls()).isOne();assertThat(answer.answer().orElseThrow().kind()).isEqualTo("ANSWER");assertThat(sent).hasSize(1);
    }
    @Test void selectionAndQuestionFitCountExactlyTheMessagesAndSchemaSentToInference() {
        var countedMessages=new ArrayList<List<ChatMessage>>();var countedTexts=new ArrayList<String>();var sent=new ArrayList<List<ChatMessage>>();var schemas=new ArrayList<String>();
        var policy=TokenBudgetPolicy.current();
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max){throw new AssertionError();}
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max,com.nori.ai.gateway.core.job.CancellationToken c,StructuredOutput output){sent.add(List.copyOf(m));schemas.add(output.schema());return reply("{\"sentences\":[]}");}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var composer=new ComposerService(client,new PromptReducer(m->{countedMessages.add(List.copyOf(m));return 100;},policy),policy,
            text->{countedTexts.add(text);return CODEPOINTS.count(text);},new ExpiryCheckpoint(),LlmCallObserver.NONE,a->{} ,"도움말",text->List.of());
        var job=job("원래 질문");var history=Optional.of(new ClassificationService.HistoryPair("이전 질문","이전 답변"));
        assertThat(composer.groundedQuestionFits(job)).isTrue();
        assertThat(SchemaContracts.JSON.readTree(countedMessages.getLast().getLast().content()).path("facts")).isEmpty();
        countedMessages.clear();countedTexts.clear();
        var units=List.of(FactCheckTest.unit("CLN_001 createdAt: 2026-04-28T05:02:28.248019Z"));
        assertThat(composer.fitsGrounded(job,units,history)).isTrue();
        var selectedMessages=List.copyOf(countedMessages);var selectedTexts=List.copyOf(countedTexts);
        countedMessages.clear();countedTexts.clear();composer.compose(job,input("원래 질문",units,history));
        assertThat(selectedMessages).contains(sent.getFirst());assertThat(countedMessages).contains(sent.getFirst());
        assertThat(selectedTexts).contains(sent.getFirst().getLast().content(),schemas.getFirst());
        assertThat(countedTexts).contains(sent.getFirst().getLast().content(),schemas.getFirst());
    }

    @Test void workflowSelectionCountsTheSameUserJsonAndSchemaAsFinalComposer() {
        var counted=new ArrayList<String>();var sent=new ArrayList<List<ChatMessage>>();var schemas=new ArrayList<String>();
        var policy=TokenBudgetPolicy.current();
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max){throw new AssertionError();}
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max,CancellationToken c,StructuredOutput output){sent.add(List.copyOf(m));schemas.add(output.schema());return reply("{\"sentences\":[]}");}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var composer=new ComposerService(client,new PromptReducer(m->100,policy),policy,CODEPOINTS,new ExpiryCheckpoint(),LlmCallObserver.NONE,a->{},"도움말",text->List.of());
        var units=List.of(FactCheckTest.unit("CLN_001 OFFLINE"));var job=job("CLN_001");
        var inputs=new WorkflowGrounding.Inputs(){
            public WorkflowGrounding.SourceUnits collect(JobContext j,WorkflowExecutionPlan p,WorkflowOutcome o){return new WorkflowGrounding.SourceUnits(units,Set.of(),List.of("CLN_001"));}
            public Map<String,String> glossary(JobContext j){return Map.of();}
        };
        DocumentSearch noSearch=(a,b,c,d)->{throw new AssertionError();};
        var grounding=new WorkflowGrounding(noSearch,text->List.of(),SearchSettings.defaults(),new EvidenceSelection(text->{counted.add(text);return CODEPOINTS.count(text);},policy.evidenceTokens()),inputs,java.time.Clock.systemUTC(),new ExpiryCheckpoint(),composer::fitsGrounded);
        var definition=new WorkflowDefinition(SCOPE.boundSystem(),com.nori.ai.gateway.core.registry.TaskType.LIVE_QUERY,List.of(),WorkflowDefinition.AnswerSource.TOOL_RESULTS,null);
        var plan=new WorkflowExecutionPlan(definition,new com.nori.ai.gateway.core.plan.PlanEnvelope("job","conversation","CLN_001",List.of(),com.nori.ai.gateway.core.registry.TaskType.LIVE_QUERY,List.of()),List.of());
        var outcome=new WorkflowOutcome(List.of(),List.of(),List.of(),new com.nori.ai.gateway.core.result.ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
        var prepared=grounding.prepare(job,plan,outcome,new ToolCallBudget());assertThat(prepared.selected().units()).hasSize(1);
        var selectionCounts=List.copyOf(counted);composer.compose(job,input("CLN_001",prepared.selected().units(),Optional.empty()));
        assertThat(selectionCounts).contains(sent.getFirst().getLast().content(),schemas.getFirst());
    }

}
