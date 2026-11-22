package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class SentenceEvidenceCheckTest {
    final SearchLanguage language=text->List.of("설비","상태","서울","부산","근거","확인","결과","정보","조회","문서","내용","값").stream().filter(text::contains).map(t->new SearchLanguage.Term(t,t.equals("서울")||t.equals("부산")?"NNP":"NNG")).toList();
    final SentenceEvidenceCheck check=new SentenceEvidenceCheck(language);
    EvidenceUnit unit(String body,String header,String status){return new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("toolId","read").put("ref","r").put("observedAt","2026-11-19T00:00:00Z"),header,body,List.of("READ"),status,"2026-11-19T00:00:00Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());}
    String output(Object... rows){return SchemaContracts.JSON.writeValueAsString(Map.of("sentences",List.of(rows)));}
    Map<String,Object> row(String text,Integer... citations){return Map.of("text",text,"citations",List.of(citations));}
    @Test void anAllowedEnumRowStillNeedsFactsAndNounsAndNonLlmCallKeepsItsOriginalContract() {
        var units=List.of(unit("설비 10","","SUCCESS"));
        var itemSchema=SchemaContracts.JSON.valueToTree(Map.of("enum",List.of(row("설비",1),row("99",1),row("부산",1))));
        var reasons=new ArrayList<LlmDiagnostic>();
        var result=check.check(output(row("설비",1),row("99",1),row("부산",1)),units,reasons::add,value->SchemaContracts.matches(itemSchema,value));
        assertThat(result.kind()).isEqualTo("PARTIAL");assertThat(result.text()).isEqualTo("설비[1]");
        assertThat(result.sentences()).hasSize(1);assertThat(result.citations()).singleElement().satisfies(value->assertThat(value.number()).isEqualTo(1));
        assertThat(reasons).containsExactly(LlmDiagnostic.SENTENCE_FACT,LlmDiagnostic.SENTENCE_NAME);
        assertThat(check.check(output(row("설비 상태",1)),List.of(unit("설비 상태 T3","","SUCCESS"))).kind()).isEqualTo("ANSWER");
    }
    @Test void onlyCitedBodySuppliesNumbersAndIdentifiersWhileNamesMayUseItsOwnHeader() {
        var units=List.of(unit("설비 T3 S1F2 EQP-001 linger.ms 1.5ms -2 20%","서울 Alpha","SUCCESS"),unit("부산 Beta 77 T30 EQP-0010","","SUCCESS"));
        var result=check.check(output(row("설비 T3 S1F2 EQP-001 linger.ms 1.5ms -2 20%",1),row("서울 Alpha 확인",1),row("부산 77",1),row("Beta",1),row("EQP-00",1),row("T30",1)),units);
        assertThat(result.kind()).isEqualTo("PARTIAL");assertThat(result.sentences()).extracting(s->s.text()).containsExactly("설비 T3 S1F2 EQP-001 linger.ms 1.5ms -2 20%","서울 Alpha 확인");
        assertThat(result.citations()).extracting(c->c.number()).containsExactly(1);
        assertThat(check.check(output(row("77",1)),List.of(unit("설비","77","SUCCESS"))).kind()).isEqualTo("NO_EVIDENCE");
        assertThat(check.check(output(row("linger.ms",1)),List.of(unit("설비","linger.ms","SUCCESS"))).kind()).isEqualTo("NO_EVIDENCE");
    }
    @Test void decimalSignsUnitsAndExponentStringsRequireLiteralIdentityWithoutConversion() {
        var units=List.of(unit(".5 -0.5 +2 1e3 1.5ms 10%","","SUCCESS"));
        for(String valid:List.of(".5","-0.5","+2","1e3","1.5ms","10%"))assertThat(check.check(output(row(valid,1)),units).kind()).as(valid).isEqualTo("ANSWER");
        for(String invalid:List.of("0.5","0.0015s","1000","2","-2"))assertThat(check.check(output(row(invalid,1)),units).kind()).as(invalid).isEqualTo("NO_EVIDENCE");
    }
    @Test void formattedNumbersAndDigitLeadingIdentifiersCannotBeBorrowedAsSmallerTokens() {
        var units=List.of(unit("1,000 1.2.3 −2","","SUCCESS"));
        for(String valid:List.of("1,000","1.2.3","−2"))assertThat(check.check(output(row(valid,1)),units).kind()).as(valid).isEqualTo("ANSWER");
        for(String invalid:List.of("1","000","1.2","3","2"))assertThat(check.check(output(row(invalid,1)),units).kind()).as(invalid).isEqualTo("NO_EVIDENCE");
    }
    @Test void fixedFunctionWordsAreTheOnlyNounExceptionsAndPartialSubstringNamesDoNotPass() {
        assertThat(check.check(output(row("근거 확인 결과 정보 조회 문서 내용 값",1)),List.of(unit("","","SUCCESS"))).kind()).isEqualTo("ANSWER");
        assertThat(check.check(output(row("부산",1)),List.of(unit("서울","","SUCCESS"))).kind()).isEqualTo("NO_EVIDENCE");
        assertThat(check.check(output(row("Alpha",1)),List.of(unit("Alphabet","","SUCCESS"))).kind()).isEqualTo("NO_EVIDENCE");
    }
    @Test void limitsInvalidNumbersMissingCitationsAndLineBreaksRemoveSentencesWithoutRetry() {
        var units=List.of(unit("x "+"가".repeat(1001),"","SUCCESS"));
        for(Object row:List.of(row("x"),row("x",0),row("x",2),row("x",1,1,1,1,1,1,1),row("",1),row("가".repeat(1001),1),row("x\nx",1),row("x\rx",1),Map.of("text","x","citations",List.of(1.0)),Map.of("text","x","citations",List.of("1")),Map.of("text","x","citations",List.of(1),"extra",true)))assertThat(check.check(output(row),units).kind()).as(row.toString()).isEqualTo("NO_EVIDENCE");
        assertThat(check.check(output(row("가".repeat(1000),1)),units).sentences()).hasSize(1);
        assertThat(check.check(output(Collections.nCopies(20,row("x",1)).toArray()),units).sentences()).hasSize(20);
        for(String raw:List.of("invalid","{}","{\"sentences\":[{\"text\":\"x\",\"citations\":[1]}]} {}",output(),output(Collections.nCopies(21,row("x",1)).toArray())))assertThat(check.check(raw,units).kind()).isEqualTo("NO_EVIDENCE");
    }
    @Test void noticesUseOnlyPassingCitationsInFixedOrderExactlyOnceOutsideSentences() {
        var units=List.of(unit("x","","CONFLICT"),unit("x","","PARTIAL"),unit("x","","STALE"),unit("x","","PARTIAL"));
        var result=check.check(output(row("x",1,2,3,4),row("x",2)),units);
        assertThat(result.text()).isEqualTo("x[1][2][3][4]\nx[2]\n일부 자료를 확인할 수 없습니다.\n최신 자료가 아닐 수 있습니다.\n서로 맞지 않는 자료가 있어 하나로 확정할 수 없습니다.");
        assertThat(result.kind()).isEqualTo("PARTIAL");assertThat(result.sentences()).hasSize(2);
        var other=check.check(output(row("x",2),row("77",1)),units);assertThat(other.text()).isEqualTo("x[2]\n일부 자료를 확인할 수 없습니다.");
        var empty=check.check(output(row("77",1)),units);assertThat(empty.text()).isEqualTo("근거 없음");assertThat(empty.citations()).isEmpty();assertThat(empty.sentences()).isEmpty();
    }
}
