package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.DocumentLocation;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import java.util.*;
import org.junit.jupiter.api.Test;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static org.assertj.core.api.Assertions.*;

class CitedHistoryRuntimeTest {
    @Test void priorLiteralTargetPassesOnlyWhenItIsInActuallyRetainedAccessibleHistory() {
        var location=new DocumentLocation("bucket","guide.md","a".repeat(64),"설정",1,5);
        var dropped=new CitedHistoryContext.Pair("H1","first","OLD_SENTINEL MODEL-8 설정","이전 답변",List.of(location));
        var retained=new CitedHistoryContext.Pair("H2","second","MODEL-7 설정","최근 답변",List.of(location));
        var context=new ClassificationService.Context(new RegistryView(OwnerSystem.TC,List.of()),List.of(new ClassificationService.HistoryPair(dropped.question(),dropped.answer()),new ClassificationService.HistoryPair(retained.question(),retained.answer())),Map.of(),2,List.of(dropped,retained));
        var request=new ClassificationService.Request(SCOPE,"conversation","job",new CancellationToken(),new JobRequestInput("m",3,"그 모델 설정은 무엇인가요?",Map.of()));
        for(String target:List.of("MODEL-7","INVENTED-9","MODEL-8")) {
            var wire=(tools.jackson.databind.node.ObjectNode)SchemaContracts.JSON.readTree(CitedHistoryContextTest.raw("H2"));
            wire.put("searchQuery",target+" 설정").put("retryQuery",target+" 설정 조건");wire.set("targets",SchemaContracts.JSON.valueToTree(List.of(Map.of("type","model","ref",target))));
            var client=new ScriptedClient(reply(wire.toString()));
            var service=new ClassificationService(client,TokenBudgetPolicy.current(),CODEPOINTS,messages->messages.getLast().content().contains("OLD_SENTINEL")?6000:100,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"분류",ignored->context);
            var result=service.classify(request);
            if(target.equals("MODEL-7"))assertThat(result).isInstanceOfSatisfying(ClassificationService.Classified.class,value->{
                assertThat(value.classification().targets()).extracting(com.nori.ai.gateway.core.plan.ResourceRef::ref).containsExactly(target);
                assertThat(value.classification().historyReference()).isPresent();
            });
            else assertThat(result).isInstanceOf(ClassificationService.Rejected.class);
            assertThat(client.sent).hasSize(1);assertThat(client.sent.getFirst().messages().getLast().content()).doesNotContain("OLD_SENTINEL");
        }
    }
    @Test void tokenReductionKeepsOnlyTheActuallySentReferenceAndIndependentInterjectionKeepsSixFields() {
        var location=new DocumentLocation("bucket","guide.md","a".repeat(64),"현재 설정",1,5);
        var first=new CitedHistoryContext.Pair("H1","first","OLD_SENTINEL 질문","이전 답변",List.of(location));
        var second=new CitedHistoryContext.Pair("H2","second","현재 설정 질문","최근 답변",List.of(location));
        var context=new ClassificationService.Context(new RegistryView(OwnerSystem.TC,List.of()),List.of(new ClassificationService.HistoryPair(first.question(),first.answer()),new ClassificationService.HistoryPair(second.question(),second.answer())),Map.of(),2,List.of(first,second));
        var captured=new ArrayList<List<ChatMessage>>();var outputs=new ArrayList<StructuredOutput>();
        String response=CitedHistoryContextTest.raw("H2");
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){throw new AssertionError();}
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max,CancellationToken cancellation,StructuredOutput output){captured.add(messages);outputs.add(output);return reply(response);}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var service=new ClassificationService(client,TokenBudgetPolicy.current(),CODEPOINTS,messages->messages.getLast().content().contains("OLD_SENTINEL")?6000:100,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"분류",request->context);
        var request=new ClassificationService.Request(SCOPE,"conversation","job",new CancellationToken(),new JobRequestInput("m",3,CitedHistoryContextTest.QUESTION,Map.of()));
        var result=(ClassificationService.Classified)service.classify(request);
        assertThat(result.classification().historyReference().orElseThrow().pair().sourceJobId()).isEqualTo("second");
        var user=SchemaContracts.JSON.readTree(captured.getFirst().getLast().content());
        assertThat(user.path("historyPairs")).hasSize(1);assertThat(user.path("historyReferences")).hasSize(1);
        assertThat(user.path("historyReferences").get(0).path("ref").asString()).isEqualTo("H2");
        assertThat(outputs.getFirst().accepts(CitedHistoryContextTest.raw("H1"))).isFalse();
        assertThat(result.classification().jobClassification().get("historyReference")).isInstanceOf(CitedHistoryContext.Bound.class);
        assertThat(CitedHistoryContext.referential("그런데 다른 독립 질문: CODE-7 상태는?" )).isFalse();
        assertThat(ClassificationService.retainedReferences(context,"그런데 다른 독립 질문: CODE-7 상태는?",context.history())).isEmpty();
    }

    @Test void retrievalContextUsesOnlyWholeAccessibleLiteralsAndNeverChangesOrOverflowsTheOriginalQuery() {
        var bound=CitedHistoryContextTest.bound();String query="CODE-7 현재 질의";
        String contextual=CitedHistoryContext.retrievalQuery(query,bound,List.of(CitedHistoryContextTest.PAIR));
        assertThat(contextual).startsWith(query+"\n").contains(CitedHistoryContextTest.PAIR.question()).doesNotContain(CitedHistoryContextTest.PAIR.answer());
        assertThat(CitedHistoryContext.retrievalQuery(query,bound,List.of())).isEqualTo(query);
        String full="질".repeat(512);assertThat(CitedHistoryContext.retrievalQuery(full,bound,List.of(CitedHistoryContextTest.PAIR))).isEqualTo(full);
    }
}
