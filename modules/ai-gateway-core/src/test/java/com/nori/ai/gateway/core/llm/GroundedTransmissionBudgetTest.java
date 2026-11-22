package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.SchemaContracts;
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
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){sent.add(messages);return reply("{\"selectedIds\":[\"C1\"]}");}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var policy=TokenBudgetPolicy.current();return new ComposerService(client,new PromptReducer(counter,policy),policy,CODEPOINTS,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"일반 도움말",text->List.of());
    }
    @Test void wholeUnitSelectionCountsActualMarkersPriorityAndHistoryAndFinalTransmissionAgrees() {
        var sent=new ArrayList<List<ChatMessage>>();var composer=composer(sent,messages->100);var job=job("질문");
        var history=Optional.of(new ClassificationService.HistoryPair("이전 질문","문맥".repeat(80)));
        List<EvidenceUnit> boundary=null;
        for(int size=120;size<450;size+=5) {
            var units=new ArrayList<EvidenceUnit>();for(int i=0;i<5;i++)units.add(EvidenceSpanSelectionTest.unit("가".repeat(size/2)+". "+"나".repeat(size/2)+"."));
            if(CODEPOINTS.count(EvidenceSelection.json(units))<=3072&&!composer.fitsGrounded(job,units,history)){boundary=units;break;}
        }
        assertThat(boundary).as("원래 JSON은 들어가지만 실제 전송 메타데이터/문맥은 넘치는 경계").isNotNull();
        var selected=new EvidenceSelection(CODEPOINTS,3072).select(List.of(),boundary,SearchSettings.defaults(),units->composer.fitsGrounded(job,units,history));
        assertThat(selected.units()).isNotEmpty().hasSizeLessThan(boundary.size());
        var originalBodies=boundary.stream().map(EvidenceUnit::body).toList();
        assertThat(selected.units()).allSatisfy(unit->assertThat(originalBodies).contains(unit.body()));
        var result=composer.compose(job,input("질문",selected.units(),history));assertThat(result.calls()).isOne();
        var data=SchemaContracts.JSON.readTree(sent.getFirst().getLast().content()).get("data");
        assertThat(CODEPOINTS.count(com.nori.ai.gateway.core.evidence.EvidenceJson.write(data))).isLessThanOrEqualTo(3072);
        assertThat(data.has("history")).isTrue();assertThat(data.has("priorityIds")).isTrue();
        assertThat(selected.json()).doesNotContain("[C1@L");
    }

    @Test void irreducibleInputAndMarkerCollisionAreZeroCallSafeFailures() {
        var sent=new ArrayList<List<ChatMessage>>();var tooLarge=composer(sent,messages->4609);var job=job("원래 질문");
        assertThat(tooLarge.compose(job,input("원래 질문",List.of(EvidenceSpanSelectionTest.unit("현재 본문이다.")),Optional.empty())).outcome()).isEqualTo(ComposerOutcome.INPUT_BUDGET_EXCEEDED);
        assertThat(sent).isEmpty();
        var safe=composer(sent,messages->100);var answer=safe.compose(job,input("원래 질문",List.of(EvidenceSpanSelectionTest.unit("[C1@L1]충돌[/]")),Optional.empty()));
        assertThat(answer.calls()).isZero();assertThat(answer.answer().orElseThrow().kind()).isEqualTo("NO_EVIDENCE");assertThat(sent).isEmpty();
    }
}
