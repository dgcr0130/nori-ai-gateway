package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class EvidenceSpanSelectionTest {
    static EvidenceUnit unit(String text){return new EvidenceUnit("DOCUMENT","parent",SchemaContracts.JSON.createObjectNode(),"헤더",text,List.of("READ"),"SUCCESS",null,List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());}
    static String output(String text,int citation){return SchemaContracts.JSON.writeValueAsString(Map.of("sentences",List.of(Map.of("text",text,"citations",List.of(citation)))));}

    @Test void onlyExactSourceTextAndItsOwnCitationAreSelectableAndEmptyIsAllowed() {
        var schema=EvidenceSpanSelection.create(List.of(unit("첫 문장이다. 두 번째는 없다."),unit("번호 1.5ms EQP-001이다.")));
        assertThat(schema.accepts(output("첫 문장이다.",1))).isTrue();
        assertThat(schema.accepts(output("두 번째는 없다.",1))).isTrue();
        assertThat(schema.accepts(output("두 번째는 있다.",1))).isFalse();
        assertThat(schema.accepts(output("첫 문장이다.",2))).isFalse();
        assertThat(schema.accepts(output("번호 15ms EQP-001이다.",2))).isFalse();
        assertThat(schema.accepts("{\"sentences\":[]}")).isTrue();
        assertThat(schema.accepts("{\"sentences\":[]} {} ")).isFalse();
    }

    @Test void tableCodeConditionsListsDecimalVersionsAndPathsKeepTheirLiteralBoundaries() {
        String table="설정=1.5ms | 버전=v1.2.3 | 조건=비활성이면 전송하지 않는다. 재시도는 없다.";
        String code="{\"path\":\"/api/v1.2/file.json\",\"message\":\"do not. retry\"}";
        var spans=EvidenceSpanSelection.spans("1. 조건이 없으면 전송하지 않는다.\n"+table+"\n"+code+"\n- EQP-001의 v1.2.3 경로 /a/b.json을 연다.",true);
        assertThat(spans).containsExactly("조건이 없으면 전송하지 않는다.",table,code,"EQP-001의 v1.2.3 경로 /a/b.json을 연다.");
        assertThat(EvidenceSpanSelection.spans("counter=1.5. do not. retry",false)).containsExactly("counter=1.5. do not. retry");
    }

    @Test void longLinesAreNotCutIntoInventedFragmentsAndNoChoicesAllowsOnlyEmpty() {
        var schema=EvidenceSpanSelection.create(List.of(unit("가".repeat(1001))));
        assertThat(schema.accepts("{\"sentences\":[]}")).isTrue();
        assertThat(schema.accepts(output("가".repeat(1000),1))).isFalse();
        for(var empty:List.of(schema,EvidenceSpanSelection.create(List.of()))) {
            var sentences=SchemaContracts.parse(empty.schema()).path("properties").path("sentences");
            assertThat(sentences.path("items").isObject()).isTrue();assertThat(sentences.path("items").isEmpty()).isTrue();
            assertThat(sentences.path("maxItems").asInt()).isZero();
            assertThat(empty.accepts("{\"sentences\":[]}")).isTrue();
            for(String item:List.of("{}","null","1","\"arbitrary\"","[]","{\"text\":\"원문\",\"citations\":[1]}"))
                assertThat(empty.accepts("{\"sentences\":["+item+"]}")).isFalse();
        }
    }
}
