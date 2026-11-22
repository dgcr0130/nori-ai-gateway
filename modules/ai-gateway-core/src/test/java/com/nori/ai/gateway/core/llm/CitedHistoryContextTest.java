package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.DocumentLocation;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CitedHistoryContextTest {
    @Test void documentCitationTotalAllowsTenParentsAndRejectsEleven() {
        var locations=java.util.stream.IntStream.rangeClosed(1,11).mapToObj(i->new DocumentLocation("bucket","doc-"+i+".md","a".repeat(64),"절",1,5)).toList();
        for(int count=1;count<=10;count++)assertThat(new CitedHistoryContext.Pair("H1","previous","질문","답변",locations.subList(0,count)).citations()).hasSize(count);
        assertThatThrownBy(()->new CitedHistoryContext.Pair("H1","previous","질문","답변",locations)).isInstanceOf(IllegalArgumentException.class);
    }
    static final String QUESTION = "그 설정에서 CODE-7 조건은 무엇인가요?";
    static final DocumentLocation LOCATION = new DocumentLocation("bucket", "guide.md", "a".repeat(64), "설정", 10, 20);
    static final CitedHistoryContext.Pair PAIR = new CitedHistoryContext.Pair("H1", "previous", "설정을 설명해 주세요", "이전 답변은 사실 근거가 아니다", List.of(LOCATION));
    static StructuredOutput base() { return ClassificationOutputSchema.create(new RegistryView(OwnerSystem.TC, List.of()), false); }
    static String raw(String ref) { var node = SchemaContracts.JSON.valueToTree(Map.of("taskType", "KNOWLEDGE_QUERY", "toolCalls", List.of(), "targets", List.of(), "searchQuery", "CODE-7 설정", "retryQuery", "CODE-7 조건", "historyRef", ref));
        ((tools.jackson.databind.node.ObjectNode) node).putNull("timeWindow"); return SchemaContracts.JSON.writeValueAsString(node); }
    static EvidenceUnit unit(DocumentLocation location, List<String> permissions, String body) { return new EvidenceUnit("DOCUMENT", "parent", SchemaContracts.JSON.valueToTree(location), "설정", body, permissions, "SUCCESS", null, List.of(), SchemaContracts.JSON.createObjectNode(), List.of(), List.of()); }
    static CitedHistoryContext.Bound bound() { return CitedHistoryContext.decode(base(), QUESTION, List.of(PAIR), Set.of(), raw("H1")).reference().orElseThrow(); }

    @Test void onlyCurrentSelectedCitedParentCanProvideReferentialCoverage() {
        assertThat(CitedHistoryContext.sufficient(bound(), QUESTION, List.of(), List.of(PAIR), List.of(unit(LOCATION, List.of("READ"), "CODE-7은 현재 본문이다")), Set.of("READ"))).isTrue();
        assertThat(CitedHistoryContext.sufficient(bound(), QUESTION, List.of(), List.of(PAIR), List.of(), Set.of("READ"))).isFalse();
        var wrong = new DocumentLocation("bucket", "other.md", "a".repeat(64), "설정", 10, 20);
        assertThat(CitedHistoryContext.sufficient(bound(), QUESTION, List.of(), List.of(PAIR), List.of(unit(wrong, List.of("READ"), "CODE-7 설정 조건")), Set.of("READ"))).isFalse();
    }

    @Test void revisionPermissionsHistoryAccessAndOriginalIdentifiersCannotBeBorrowed() {
        var changed = new DocumentLocation("bucket", "guide.md", "b".repeat(64), "설정", 10, 20);
        for (var units : List.of(List.of(unit(changed, List.of("READ"), "CODE-7")), List.of(unit(LOCATION, List.of("ADMIN"), "CODE-7")), List.of(unit(LOCATION, List.of("READ"), "CODE-8"))))
            assertThat(CitedHistoryContext.sufficient(bound(), QUESTION, List.of(), List.of(PAIR), units, Set.of("READ"))).isFalse();
        assertThat(CitedHistoryContext.sufficient(bound(), QUESTION, List.of(), List.of(), List.of(unit(LOCATION, List.of("READ"), "CODE-7")), Set.of("READ"))).isFalse();
        assertThat(CitedHistoryContext.sufficient(bound(), "그 설정에서 CODE-8 조건은 무엇인가요?", List.of(), List.of(PAIR), List.of(unit(LOCATION, List.of("READ"), "CODE-8")), Set.of("READ"))).isFalse();
    }

    @Test void inventedReferencesSubjectsAndIndependentQuestionsCannotInheritHistory() {
        assertThatThrownBy(() -> CitedHistoryContext.decode(base(), QUESTION, List.of(PAIR), Set.of(), raw("H2"))).isInstanceOf(IllegalArgumentException.class);
        var inventedSubject = (tools.jackson.databind.node.ObjectNode) SchemaContracts.JSON.readTree(raw("H1")); inventedSubject.put("subject", "새 대상");
        assertThatThrownBy(() -> CitedHistoryContext.decode(base(), QUESTION, List.of(PAIR), Set.of(), inventedSubject.toString())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CitedHistoryContext.decode(base(), "새 질문의 CODE-7 상태는 무엇인가요?", List.of(PAIR), Set.of(), raw("H1"))).isInstanceOf(IllegalArgumentException.class);
        var independent = (tools.jackson.databind.node.ObjectNode) SchemaContracts.JSON.readTree(raw("H1")); independent.putNull("historyRef");
        assertThat(CitedHistoryContext.decode(base(), "새 질문의 CODE-7 상태는 무엇인가요?", List.of(PAIR), Set.of(), independent.toString()).reference()).isEmpty();
    }

    @Test void projectionPreservesTheSixFieldsAndOriginalQuestionForTheirExistingDecoder() {
        var decoded = CitedHistoryContext.decode(base(), QUESTION, List.of(PAIR), Set.of(), raw("H1"));
        assertThat(SchemaContracts.JSON.readTree(decoded.classificationJson()).size()).isEqualTo(6);
        assertThat(decoded.reference().orElseThrow().originalQuestion()).isEqualTo(QUESTION);
        assertThat(decoded.reference().orElseThrow().pair().answer()).isEqualTo(PAIR.answer());
    }
}
