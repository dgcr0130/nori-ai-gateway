package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.SearchLanguage;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.workflow.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static org.assertj.core.api.Assertions.*;

class GroundedCompositionTest {
    @Test void explicitOriginalEnglishLiteralShapeRunsAfterSourceValidationWithOneComposeCall() {
        String question="영어 출력 문구는 무엇인가요?";
        var original=new JobContext("job-1","conversation",SCOPE,"delegation",new CancellationToken(),InferenceMode.ENABLED,Map.of("taskType","KNOWLEDGE_QUERY"),new JobRequestInput("m",1,question,Map.of()));
        for(String body:List.of("저장 성공 안내가 나타난다.","출력 문구는 \"READY\"이다.")) {
            var units=List.of(EvidenceSpanSelectionTest.unit(body));var input=new ComposerService.Input(List.of(),List.of(),"",List.of(),question,List.of(),Optional.of(EvidenceSelection.json(units)));
            var result=service(ids("C1"),CODEPOINTS).compose(original,input);
            assertThat(result.calls()).isOne();assertThat(result.answer().orElseThrow().kind()).isEqualTo(body.contains("READY")?"ANSWER":"NO_EVIDENCE");
        }
        assertThat(diagnostics).contains(LlmDiagnostic.COMPOSE_LITERAL_SHAPE);assertThat(constraints).hasSize(2);
    }
    private final List<StructuredOutput> constraints=new ArrayList<>();
    private final List<LlmDiagnostic> diagnostics=new ArrayList<>();
    private final List<LlmCallAudit.Attempt> audits=new ArrayList<>();
    private final LlmCallObserver observer=new LlmCallObserver(){
        public Scope open(String job,CallType type,List<ChatMessage> messages){return ()->{};}
        public void diagnostic(String job,CallType type,LlmDiagnostic reason){diagnostics.add(reason);}
    };
    private JobContext job(){return new JobContext("job-1","conversation",SCOPE,"delegation",new CancellationToken(),InferenceMode.ENABLED,Map.of("taskType","KNOWLEDGE_QUERY"),new JobRequestInput("m",1,"질문",Map.of()));}
    private ComposerService service(String reply,AnswerTokenCounting tokens){
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max){throw new AssertionError();}
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max,CancellationToken c,StructuredOutput schema){constraints.add(schema);
                String wire=reply;
                // 성공 fixture는 실제 요청의 모든 판정 필드를 명시하고 오류 원문은 그대로 둡니다.
                if(schema!=null)try {
                    var value=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.readTree(reply);
                    if(value.isObject()&&value.size()==1&&value.path("selectedIds").isArray()) {
                        var decisions=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.createObjectNode();
                        for(var key:com.nori.ai.gateway.core.registry.SchemaContracts.JSON.readTree(schema.schema()).path("required"))decisions.put(key.asString(),false);
                        for(var id:value.path("selectedIds"))decisions.put(id.asString(),true);
                        wire=decisions.toString();
                    }
                }catch(RuntimeException invalid){/* 잘못된 외피를 보정하지 않습니다. */}
                return LlmTestSupport.reply(wire);}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        SearchLanguage language=text->Arrays.stream(text.split("\\s+")).map(t->new SearchLanguage.Term(t,"NNG")).toList();
        var policy=TokenBudgetPolicy.current();
        return new ComposerService(client,new PromptReducer(m->100,policy),policy,tokens,new ExpiryCheckpoint(),observer,audits::add,"작성",language);
    }
    private static ComposerService.Input input(String text){return new ComposerService.Input(List.of(),List.of(),"",List.of(),"질문",List.of(),Optional.of(EvidenceSelection.json(List.of(EvidenceSpanSelectionTest.unit(text)))));}
    private static ComposerService.Input input(List<EvidenceUnit> units){return new ComposerService.Input(List.of(),List.of(),"",List.of(),"질문",List.of(),Optional.of(EvidenceSelection.json(units)));}
    private static String ids(String... ids){return com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(Map.of("selectedIds",List.of(ids)));}
    private static Map<String,Object> row(String text,int citation){return Map.of("text",text,"citations",List.of(citation));}
    private static String rows(Map<String,Object> first,Map<String,Object> second){return com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(Map.of("sentences",List.of(first,second)));}
    private static EvidenceUnit unit(String text,String status) {
        var unit=EvidenceSpanSelectionTest.unit(text);
        return new EvidenceUnit(unit.sourceType(),unit.parentKey(),unit.location(),unit.header(),unit.body(),unit.permissions(),status,unit.observedAt(),unit.missingFields(),unit.retentionMeta(),unit.warnings(),unit.childLocations(),unit.values());
    }

    @Test void emptyChoicesRejectShortExcerptFromAnOversizedSingleLineWithoutRetry() {
        String body="설비 "+"가".repeat(1001);
        var units=List.of(EvidenceSpanSelectionTest.unit(body));
        String output=EvidenceSpanSelectionTest.output("설비",1);
        SearchLanguage language=text->text.contains("설비")?List.of(new SearchLanguage.Term("설비","NNG")):List.of();
        // 기존 근거 검사는 통과하는 짧은 발췌라도 선택지가 비어 있으면 허용하지 않습니다.
        assertThat(new SentenceEvidenceCheck(language).check(output,units).kind()).isEqualTo("ANSWER");
        assertThat(EvidenceSpanSelection.spans(body,true)).isEmpty();
        var schema=EvidenceSpanSelection.create(units);
        var sentences=com.nori.ai.gateway.core.registry.SchemaContracts.parse(schema.schema()).path("properties").path("sentences");
        assertThat(sentences.path("items").isObject()).isTrue();assertThat(sentences.path("items").isEmpty()).isTrue();
        assertThat(sentences.path("maxItems").asInt()).isZero();
        var client=new ScriptedClient(reply(output));var policy=TokenBudgetPolicy.current();
        var composer=new ComposerService(client,new PromptReducer(m->100,policy),policy,CODEPOINTS,new ExpiryCheckpoint(),observer,audits::add,"작성",language);
        var result=composer.compose(job(),input(units));var answer=result.answer().orElseThrow();
        assertThat(result.outcome()).isEqualTo(ComposerOutcome.COMPOSED);assertThat(result.calls()).isZero();assertThat(client.sent).isEmpty();
        assertThat(answer.kind()).isEqualTo("NO_EVIDENCE");assertThat(answer.text()).isEqualTo("근거 없음");
        assertThat(answer.sentences()).isEmpty();assertThat(answer.citations()).isEmpty();
        assertThat(diagnostics).containsExactly(LlmDiagnostic.COMPOSE_SELECTION);
    }

    @Test void mixedLiteralAndOutOfEnumRowsKeepTheValidSentenceAsPartialWithoutRetry() {
        var result=service("{\"C1\":true,\"C2\":\"invalid\"}",CODEPOINTS).compose(job(),input(List.of(unit("설비는 없다.","SUCCESS"),unit("다른 설명이다.","SUCCESS"))));
        var answer=result.answer().orElseThrow();
        assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.text()).isEqualTo("설비는 없다.[1]");
        assertThat(answer.sentences()).singleElement().satisfies(value->{assertThat(value.text()).isEqualTo("설비는 없다.");assertThat(value.citations()).containsExactly(1);});
        assertThat(answer.citations()).singleElement().satisfies(value->assertThat(value.number()).isEqualTo(1));
        assertThat(result.calls()).isOne();assertThat(diagnostics).contains(LlmDiagnostic.COMPOSE_SELECTION);
    }

    @Test void malformedEnvelopeIsNeverRepairedEvenWhenItContainsAValidLiteralRow() {
        String valid=ids("C1");
        for(String raw:List.of(valid+" {}",valid.substring(0,valid.length()-1)+",\"extra\":true}",
                "{\"sentences\":[],\"sentences\":[{\"text\":\"설비는 없다.\",\"citations\":[1]}]}",
                "{\"sentences\":[{\"text\":\"설비는 없다.\",\"text\":\"설비는 없다.\",\"citations\":[1]}]}",
                "{\"sentences\":{}}","null")) {
            var result=service(raw,CODEPOINTS).compose(job(),input("설비는 없다."));
            assertThat(result.answer().orElseThrow().kind()).as(raw).isEqualTo("NO_EVIDENCE");assertThat(result.calls()).isOne();
        }
    }

    @Test void passingLiteralCitationsKeepPartialStaleNoticesAndAllRemovedKeepsNone() {
        var units=List.of(unit("설비는 없다.","PARTIAL"),unit("상태는 정상이다.","STALE"));
        var result=service(ids("C1","C2"),CODEPOINTS).compose(job(),input(units));
        var answer=result.answer().orElseThrow();
        assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.sentences()).hasSize(2);
        assertThat(answer.citations()).extracting(value->value.number()).containsExactly(1,2);
        assertThat(answer.text()).isEqualTo("설비는 없다.[1]\n상태는 정상이다.[2]\n일부 자료를 확인할 수 없습니다.\n최신 자료가 아닐 수 있습니다.");
        result=service(ids("unknown","C999"),CODEPOINTS).compose(job(),input(units));
        answer=result.answer().orElseThrow();
        assertThat(answer.kind()).isEqualTo("NO_EVIDENCE");assertThat(answer.text()).isEqualTo("근거 없음");
        assertThat(answer.citations()).isEmpty();assertThat(answer.sentences()).isEmpty();assertThat(result.calls()).isOne();
    }

    @Test void exactSelectionRunsTheExistingSentenceCheckWhileSchemaIgnoringParaphraseIsRefused() {
        var result=service(ids("C1"),CODEPOINTS).compose(job(),input("설비는 없다."));
        assertThat(result.answer().orElseThrow().kind()).isEqualTo("ANSWER");
        assertThat(result.calls()).isOne();assertThat(constraints).hasSize(1);assertThat(constraints.getFirst()).isNotNull();
        result=service(ids("unknown"),CODEPOINTS).compose(job(),input("설비는 없다."));
        assertThat(result.answer().orElseThrow().kind()).isEqualTo("NO_EVIDENCE");
        assertThat(diagnostics).contains(LlmDiagnostic.COMPOSE_SELECTION);
    }

    @Test void emptyAndMalformedAndWrongCitationKeepNoEvidenceWithoutNewRetry() {
        for(String value:List.of(ids(),"not-json",ids("C999"))) {
            var result=service(value,CODEPOINTS).compose(job(),input("설비는 없다."));
            assertThat(result.answer().orElseThrow().kind()).isEqualTo("NO_EVIDENCE");assertThat(result.calls()).isOne();
        }
        assertThat(diagnostics).contains(LlmDiagnostic.SENTENCE_EMPTY,LlmDiagnostic.SENTENCE_JSON,LlmDiagnostic.COMPOSE_SELECTION);
    }

    @Test void plainHelpDoesNotReceiveASelectionSchemaAndManualLegacyClientStillWorks() {
        var result=service("사용 가능한 기능을 안내합니다.",CODEPOINTS).compose(job(),new ComposerService.Input(List.of(),List.of(),"",List.of(),"도움말"));
        assertThat(result.narrative()).contains("사용 가능한 기능을 안내합니다.");
        assertThat(constraints).containsExactly((StructuredOutput)null);
        var legacy=new ScriptedClient(reply("{\"C1\":true}"));
        var policy=TokenBudgetPolicy.current();
        var old=new ComposerService(legacy,new PromptReducer(m->100,policy),policy,CODEPOINTS,new ExpiryCheckpoint(),observer,audits::add,"작성",text->List.of());
        assertThat(old.compose(job(),input("설비는 없다.")).answer().orElseThrow().kind()).isEqualTo("ANSWER");
        assertThat(legacy.sent).hasSize(1);
    }

    @Test void constraintAndEvidenceBudgetOverflowsStopBeforeTheCallAndNeverDropTheSchema() {
        var result=service("secret-sentinel",text->text.contains("\"boolean\"")?4000:10).compose(job(),input("설비는 없다."));
        assertThat(result.outcome()).isEqualTo(ComposerOutcome.INPUT_BUDGET_EXCEEDED);assertThat(result.calls()).isZero();assertThat(constraints).isEmpty();
        assertThat(diagnostics).contains(LlmDiagnostic.OUTPUT_CONSTRAINT_BUDGET);
        result=service("secret-sentinel",text->4000).compose(job(),input("설비는 없다."));
        assertThat(result.outcome()).isEqualTo(ComposerOutcome.INPUT_BUDGET_EXCEEDED);assertThat(constraints).isEmpty();
        assertThat(audits.toString()).doesNotContain("secret-sentinel","설비");
    }
}
