package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ErrorCode;
import java.util.*;
import org.junit.jupiter.api.Test;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static com.nori.ai.gateway.core.llm.IntentTestFixtures.*;
import static org.assertj.core.api.Assertions.*;

class ClassificationStructuredOutputTest {
    final List<StructuredOutput> constraints=new ArrayList<>();
    final List<LlmDiagnostic> diagnostics=new ArrayList<>();
    final LlmCallObserver observer=new LlmCallObserver(){public Scope open(String job,CallType type,List<ChatMessage> messages){return ()->{};}public void diagnostic(String job,CallType type,LlmDiagnostic reason){diagnostics.add(reason);}};
    ClassificationService.Result classify(String output,RegistryView view){
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){throw new AssertionError("제약 진입점이어야 합니다");}
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max,CancellationToken token,StructuredOutput constraint){constraints.add(constraint);return reply(output);}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var service=new ClassificationService(client,TokenBudgetPolicy.current(),CODEPOINTS,m->100,new ExpiryCheckpoint(),observer,a->{},"분류",r->context(view,List.of(),2));
        return service.classify(ClassificationServiceTest.request("CLN_001 조회"));
    }
    @Test void schemaHasOnlyNeedsAndClosedIntentSubjectEnumsWithOneThroughFourItems(){
        classify(wire("status","CLN_001"),registry().view(SCOPE));var output=constraints.getFirst();var root=SchemaContracts.parse(output.schema());
        assertThat(root.path("properties").propertyNames()).containsExactly("needs");assertThat(root.path("additionalProperties").asBoolean()).isFalse();
        assertThat(root.path("required")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("needs")));
        var needs=root.path("properties").path("needs");assertThat(needs.path("minItems").asInt()).isOne();assertThat(needs.path("maxItems").asInt()).isEqualTo(4);
        var item=needs.path("items");assertThat(item.path("properties").propertyNames()).containsExactlyInAnyOrder("intent","subject");assertThat(item.path("required")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("intent","subject")));
        assertThat(item.path("additionalProperties").asBoolean()).isFalse();
        assertThat(output.accepts(wire("status","CLN_001"))).isTrue();assertThat(output.accepts(wire("status",null))).isTrue();
        assertThat(output.accepts(wire("unknown",null))).isFalse();assertThat(output.accepts(wire("status","OTHER_001"))).isFalse();
        for(int count:List.of(0,1,4,5))assertThat(output.accepts(SchemaContracts.JSON.writeValueAsString(Map.of("needs",Collections.nCopies(count,need("status","CLN_001")))))).as("needs=%s",count).isEqualTo(count>=1&&count<=4);
        for(String field:List.of("taskType","toolCalls","targets","timeWindow","searchQuery","retryQuery")){var extra=ClassificationServiceTest.wire();extra.put(field,"forbidden");assertThat(output.accepts(extra.toString())).as(field).isFalse();}
    }
    @Test void visibilityUsesAnyAuthorizedToolAndToollessIntentsAreAlwaysVisible(){
        var all=registry().view(SCOPE);var trace=all.capabilities().stream().filter(t->t.toolId().equals("trace.search")).toList();
        for(var view:List.of(new RegistryView(SCOPE.boundSystem(),List.of()),new RegistryView(SCOPE.boundSystem(),trace))){
            classify(wire("document",null),view);var output=constraints.getLast();
            for(String intent:List.of("document","help","unsupported"))assertThat(output.accepts(wire(intent,null))).isTrue();
            assertThat(output.accepts(wire("incident",null))).isEqualTo(!view.capabilities().isEmpty());
            for(String intent:List.of("registration","status","model","dlq","communication"))assertThat(output.accepts(wire(intent,null))).isFalse();
            assertThat(classify(wire("status","CLN_001"),view)).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));
        }
    }
    @Test void schemaCannotReplaceSemanticValidationAndJsonAnswerShapeDiagnosticsStayDistinct(){
        var empty=new RegistryView(SCOPE.boundSystem(),List.of());
        for(String text:List.of("not-json secret-sentinel","{}","<think>secret-sentinel</think>"))assertThat(classify(text,empty)).isInstanceOf(ClassificationService.Rejected.class);
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_JSON,LlmDiagnostic.CLASSIFY_SHAPE,LlmDiagnostic.CLASSIFY_ANSWER_CHECK);assertThat(diagnostics.toString()).doesNotContain("secret-sentinel","질문");
    }
    @Test void oversizedConstraintIsNotSentAndLeavesTextFreeAudit(){
        var client=new ScriptedClient();var records=new ArrayList<LlmCallAudit.Attempt>();
        var service=new ClassificationService(client,TokenBudgetPolicy.current(),text->6000,m->100,new ExpiryCheckpoint(),observer,records::add,"분류",r->context(new RegistryView(SCOPE.boundSystem(),List.of()),List.of(),0));
        assertThat(service.classify(ClassificationServiceTest.request("질문"))).isEqualTo(new ClassificationService.BudgetExceeded(0));assertThat(client.sent).isEmpty();
        assertThat(records).singleElement().satisfies(row->{assertThat(row.sent()).isFalse();assertThat(row.inputTokens()).isEqualTo(100);});assertThat(diagnostics).contains(LlmDiagnostic.OUTPUT_CONSTRAINT_BUDGET);assertThat(records.toString()).doesNotContain("질문","schema");
    }
}
