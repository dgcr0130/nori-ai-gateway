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
import static org.assertj.core.api.Assertions.*;

class ClassificationStructuredOutputTest {
    private final List<LlmDiagnostic> diagnostics = new ArrayList<>();
    private final List<StructuredOutput> constraints = new ArrayList<>();
    private final LlmCallObserver observer = new LlmCallObserver() {
        public Scope open(String job,CallType type,List<ChatMessage> messages){return ()->{};}
        public void diagnostic(String job,CallType type,LlmDiagnostic reason){diagnostics.add(reason);}
    };

    private ClassificationService.Result classify(String output,String question,Map<String,Object> fields,RegistryView registry) {
        var client = new InferenceClient() {
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){throw new AssertionError("제약 진입점이어야 합니다");}
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max,CancellationToken token,StructuredOutput constraint){constraints.add(constraint);return reply(output);}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var service = new ClassificationService(client,TokenBudgetPolicy.current(),CODEPOINTS,m->100,new ExpiryCheckpoint(),observer,a-> {},"분류",r->new ClassificationService.Context(registry,List.of(),Map.of(),2));
        return service.classify(new ClassificationService.Request(SCOPE,"conversation","job-1",new CancellationToken(),new JobRequestInput("m",1,question,fields)));
    }
    private static RegistryView empty(){return new RegistryView(SCOPE.boundSystem(),List.of());}

    @Test void zeroToolsSchemaStillAllowsKnowledgeAndLiveButForbidsInventedCallsAndExtraKeys() {
        var node=ClassificationServiceTest.wire();node.put("taskType","KNOWLEDGE_QUERY");
        assertThat(classify(node.toString(),"질문",Map.of(),empty())).isInstanceOf(ClassificationService.Classified.class);
        var schema=constraints.getFirst();assertThat(schema.accepts(node.toString())).isTrue();
        var toolCalls=SchemaContracts.parse(schema.schema()).path("properties").path("toolCalls");
        assertThat(toolCalls.path("items").isObject()).isTrue();assertThat(toolCalls.path("items").isEmpty()).isTrue();
        assertThat(toolCalls.path("maxItems").asInt()).isZero();
        for(String item:List.of("{}","null","1","\"arbitrary\"","[]")) {
            var invalid=ClassificationServiceTest.wire();invalid.withArray("toolCalls").add(SchemaContracts.parse(item));
            assertThat(schema.accepts(invalid.toString())).isFalse();
        }
        node.put("taskType","LIVE_QUERY");assertThat(schema.accepts(node.toString())).isTrue();
        node.withArray("toolCalls").addObject().put("toolId","invented").set("arguments",SchemaContracts.parse("{}"));
        assertThat(schema.accepts(node.toString())).isFalse();
        assertThat(classify(node.toString(),"질문",Map.of(),empty())).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_TOOL);
        node=ClassificationServiceTest.wire();node.put("secret","sentinel");assertThat(schema.accepts(node.toString())).isFalse();
        assertThat(schema.toString()).doesNotContain("properties","sentinel");
    }

    @Test void registeredSchemaKeepsLiteralTargetsAndMissingRequiredInputReachesPlanInvalid() {
        var node=ClassificationServiceTest.wire();node.withArray("toolCalls").addObject().put("toolId","lookup").set("arguments",SchemaContracts.parse("{}"));
        assertThat(classify(node.toString(),"질문",Map.of(),ClassificationServiceTest.view()))
            .isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("targetRef")));
        assertThat(constraints.getFirst().accepts(node.toString())).isTrue();
        ((tools.jackson.databind.node.ObjectNode)node.path("toolCalls").get(0).path("arguments")).put("extra","invented");
        assertThat(constraints.getFirst().accepts(node.toString())).isFalse();
        node=ClassificationServiceTest.wire();node.withArray("targets").addObject().put("type","MODEL").put("ref","MODEL-X");node.put("searchQuery","MODEL-X 질문");
        var refs=Map.<String,Object>of("resourceRefs",List.of(Map.of("type","MODEL","ref","MODEL-X")));
        assertThat(classify(node.toString(),"질문",refs,empty())).isInstanceOf(ClassificationService.Classified.class);
        assertThat(classify(node.toString(),"질문",Map.of(),empty())).isInstanceOf(ClassificationService.Rejected.class);
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_TARGET);
        node=ClassificationServiceTest.wire();
        assertThat(classify(node.toString(),"EQP-001 질문",Map.of(),empty())).isInstanceOf(ClassificationService.Rejected.class);
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_SEARCH);
    }

    @Test void embeddedToolSchemasKeepLocalReferencesBoundToTheirOriginalTool() {
        var tool=ConfiguredTool.create(SCOPE.boundSystem(),"reference","참조",ToolKind.BUSINESS,
            SchemaContracts.parse("{\"type\":\"object\",\"properties\":{\"value\":{\"$ref\":\"#/$defs/value\"}},\"$defs\":{\"value\":{\"type\":\"string\",\"enum\":[\"literal\"]}},\"required\":[\"value\"],\"additionalProperties\":false}"),
            SchemaContracts.parse("{\"type\":\"object\"}"),List.of("READ"),10000);
        var schema=ClassificationOutputSchema.create(new RegistryView(SCOPE.boundSystem(),List.of(tool)));
        var node=ClassificationServiceTest.wire();node.withArray("toolCalls").addObject().put("toolId","reference").set("arguments",SchemaContracts.parse("{\"value\":\"literal\"}"));
        assertThat(schema.accepts(node.toString())).isTrue();
        ((tools.jackson.databind.node.ObjectNode)node.path("toolCalls").get(0).path("arguments")).put("value","invented");
        assertThat(schema.accepts(node.toString())).isFalse();
    }

    @Test void relativeWindowWithoutReferenceCannotInventDatesButExplicitAbsoluteDatesRemainValid() {
        var node=ClassificationServiceTest.wire();node.set("timeWindow",SchemaContracts.parse("{\"from\":\"2024-04-01T00:00Z\",\"to\":\"2024-05-01T00:00Z\"}"));
        assertThat(classify(node.toString(),"이번 달 조회",Map.of(),empty()))
            .isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("timeWindow")));
        assertThat(constraints.getFirst().accepts(node.toString())).isFalse();
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_TIME_SOURCE);
        String question="2024-04-01T00:00Z 부터 2024-05-01T00:00Z 조회";
        node.put("searchQuery",question);
        assertThat(classify(node.toString(),question,Map.of(),empty())).isInstanceOf(ClassificationService.Classified.class);
        assertThat(constraints.getLast().accepts(node.toString())).isTrue();
        var context=Map.<String,Object>of("context",Map.of("timezone","UTC","referenceTime","2024-06-01T00:00Z"));
        node.put("searchQuery","조회");
        assertThat(classify(node.toString(),"이번 달 조회",context,empty())).isInstanceOf(ClassificationService.Classified.class);
        ((tools.jackson.databind.node.ObjectNode)node.get("timeWindow")).put("from","2024-05-01T00:00Z");
        assertThat(classify(node.toString(),"조회",context,empty())).isInstanceOf(ClassificationService.Rejected.class);
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_TIME);
    }

    @Test void noWindowRemainsValidWithoutClockAndNaiveAbsoluteTimeStillRequiresTimezone() {
        var node=ClassificationServiceTest.wire();
        assertThat(classify(node.toString(),"이번 달 조회",Map.of(),empty())).isInstanceOf(ClassificationService.Classified.class);
        node.set("timeWindow",SchemaContracts.parse("{\"from\":\"2024-04-01T00:00\",\"to\":\"2024-05-01T00:00\"}"));
        node.put("searchQuery","2024-04-01T00:00 2024-05-01T00:00");
        assertThat(classify(node.toString(),"2024-04-01T00:00 2024-05-01T00:00",Map.of(),empty())).isInstanceOf(ClassificationService.Rejected.class);
    }

    @Test void toolTimeArgumentsAlsoNeedInputProvenanceAndMissingInputIsNotInvented() {
        var tool=ConfiguredTool.create(SCOPE.boundSystem(),"times","기간 조회",ToolKind.BUSINESS,
            SchemaContracts.parse("{\"type\":\"object\",\"properties\":{\"from\":{\"type\":\"string\",\"maxLength\":35},\"to\":{\"type\":\"string\",\"maxLength\":35}},\"required\":[\"from\",\"to\"],\"additionalProperties\":false}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("READ"),10000);
        var registry=new RegistryView(SCOPE.boundSystem(),List.of(tool));
        var node=ClassificationServiceTest.wire();node.withArray("toolCalls").addObject().put("toolId","times").set("arguments",SchemaContracts.parse("{\"from\":\"2024-04-01T00:00Z\",\"to\":\"2024-05-01T00:00Z\"}"));
        assertThat(classify(node.toString(),"이번 달 조회",Map.of(),registry)).isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("from")));
        ((tools.jackson.databind.node.ObjectNode)node.path("toolCalls").get(0)).set("arguments",SchemaContracts.parse("{}"));
        assertThat(classify(node.toString(),"이번 달 조회",Map.of(),registry)).isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("from","to")));
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_MISSING_INPUT);
    }

    @Test void oversizedConstraintIsNotSentAndLeavesOnlyTheExistingTextFreeAudit() {
        var client=new ScriptedClient();var records=new ArrayList<LlmCallAudit.Attempt>();
        var service=new ClassificationService(client,TokenBudgetPolicy.current(),text->6000,m->100,new ExpiryCheckpoint(),observer,records::add,"분류",r->new ClassificationService.Context(empty(),List.of(),Map.of(),0));
        assertThat(service.classify(ClassificationServiceTest.request("질문"))).isEqualTo(new ClassificationService.BudgetExceeded(0));
        assertThat(client.sent).isEmpty();assertThat(records).singleElement().satisfies(row->{assertThat(row.sent()).isFalse();assertThat(row.inputTokens()).isEqualTo(100);});
        assertThat(diagnostics).contains(LlmDiagnostic.OUTPUT_CONSTRAINT_BUDGET);
        assertThat(records.toString()).doesNotContain("질문","schema");
    }

    @Test void schemaCannotReplaceSemanticValidationAndJsonAnswerAndShapeDiagnosticsStayDistinct() {
        var valid=ClassificationServiceTest.wire();
        assertThat(classify("not-json secret-sentinel","질문",Map.of(),empty())).isInstanceOf(ClassificationService.Rejected.class);
        valid.remove("targets");assertThat(classify(valid.toString(),"질문",Map.of(),empty())).isInstanceOf(ClassificationService.Rejected.class);
        assertThat(classify("<think>secret-sentinel</think>","질문",Map.of(),empty())).isInstanceOf(ClassificationService.Rejected.class);
        assertThat(diagnostics).contains(LlmDiagnostic.CLASSIFY_JSON,LlmDiagnostic.CLASSIFY_SHAPE,LlmDiagnostic.CLASSIFY_ANSWER_CHECK);
        assertThat(diagnostics.toString()).doesNotContain("secret-sentinel","질문");
    }
}
