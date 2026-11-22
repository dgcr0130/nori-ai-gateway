package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class EvidenceCandidateSelectionTest {
    @Test void runtimeKeepsValidRowsWhenIdsAreUnknownWrongTypedOrRepeatedButRejectsMalformedOuterEnvelope() {
        var selected=EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit("원문이다.")));
        var diagnostics=new ArrayList<LlmDiagnostic>();
        var checker=new SentenceEvidenceCheck(text->List.of());
        for(String raw:List.of("{\"selectedIds\":[\"C1\",\"unknown\"]}","{\"selectedIds\":[7,\"C1\"]}","{\"selectedIds\":[\"C1\",\"C1\"]}")) {
            var answer=checker.check(selected.reconstructRows(raw,diagnostics::add),List.of(EvidenceSpanSelectionTest.unit("원문이다.")));
            assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.text()).isEqualTo("원문이다.[1]");
        }
        String overLimit=SchemaContracts.JSON.writeValueAsString(Map.of("selectedIds",Collections.nCopies(21,"C1")));
        for(String raw:List.of(overLimit,"{\"selectedIds\":[\"C1\"],\"extra\":true}","{\"selectedIds\":[\"C1\"]} {}","{\"selectedIds\":[],\"selectedIds\":[\"C1\"]}"))
            assertThat(checker.check(selected.reconstructRows(raw,diagnostics::add),List.of(EvidenceSpanSelectionTest.unit("원문이다."))).kind()).isEqualTo("NO_EVIDENCE");
        assertThat(diagnostics).contains(LlmDiagnostic.COMPOSE_SELECTION,LlmDiagnostic.SENTENCE_STRUCTURE,LlmDiagnostic.SENTENCE_JSON);
        assertThat(selected.candidateMapSha()).matches("[0-9a-f]{64}");
        assertThatThrownBy(()->EvidenceCandidateSelection.prepare(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->EvidenceCandidateSelection.prepare(Arrays.asList((com.nori.ai.gateway.core.workflow.EvidenceUnit)null))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void lexicalHintUsesOnlyTheCurrentQuestionAndRetainsEveryCandidateBodyAndConstraint() {
        var units = List.of(EvidenceSpanSelectionTest.unit("일반 상태다. 조건 허용이다. 같은 조건이다."));
        var selected = EvidenceCandidateSelection.prepare(units);
        com.nori.ai.gateway.core.knowledge.SearchLanguage language = text -> Arrays.stream(text.replace('.', ' ').split("\\s+"))
            .filter(s -> !s.isEmpty()).map(s -> new com.nori.ai.gateway.core.knowledge.SearchLanguage.Term(s, "NNG")).toList();
        var original = SchemaContracts.JSON.readTree(selected.data());
        var hinted = SchemaContracts.JSON.readTree(selected.prioritizedData("조건 허용이다", units, language));
        assertThat(hinted.path("evidence")).isEqualTo(original.path("evidence"));
        assertThat(hinted.path("priorityIds").get(0).asString()).isEqualTo("C2");
        assertThat(selected.output().accepts("{\"selectedIds\":[\"C1\"]}")).isTrue();
        assertThat(selected.reconstruct("{\"selectedIds\":[\"C1\"]}")).contains("일반 상태다.");
        assertThatThrownBy(() -> selected.prioritizedData("조건", List.of(EvidenceSpanSelectionTest.unit("다른 본문")), language))
            .isInstanceOf(IllegalArgumentException.class).hasMessageNotContaining("다른 본문");
    }

    @Test void lexicalHintIsBoundedStableOnTiesAndDoesNotInferAnAnswerForZeroOverlap() {
        String body = String.join(" ", java.util.stream.IntStream.range(0, 30).mapToObj(i -> "문장" + i + ".").toList());
        var units = List.of(EvidenceSpanSelectionTest.unit(body)); var selected = EvidenceCandidateSelection.prepare(units);
        var hinted = SchemaContracts.JSON.readTree(selected.prioritizedData("무관한 질문", units, text -> List.of()));
        assertThat(hinted.path("priorityIds").size()).isEqualTo(20);
        assertThat(hinted.path("priorityIds").get(0).asString()).isEqualTo("C1");
        assertThat(selected.candidates()).hasSize(30);
        assertThat(selected.output().accepts("{\"selectedIds\":[\"C30\"]}")).isTrue();
        assertThat(selected.reconstruct("{\"selectedIds\":[]}")).isEqualTo("{\"sentences\":[]}");
    }
    @Test void viewPreservesEveryBodyByteAndUsesExactlyTheExistingSpansAndOwnCitations() {
        String body = "  - 조건이 없으면 전송하지 않는다. 다음 단계다.\n| 필드 | 값 1.5ms |\n{\"path\":\"/v1.2/file.json\"}\n";
        var units = List.of(EvidenceSpanSelectionTest.unit(body), EvidenceSpanSelectionTest.unit("다른 본문이다."));
        var selected = EvidenceCandidateSelection.prepare(units);
        assertThat(selected.candidates().stream().filter(c -> c.number() == 1).map(EvidenceCandidateSelection.Candidate::text))
            .containsExactlyElementsOf(EvidenceSpanSelection.spans(body, true));
        var evidence = SchemaContracts.JSON.readTree(selected.data()).get("evidence");
        assertThat(evidence.get(0).path("body").asString().replaceAll("\\[C[0-9]+@L[0-9]+]|\\[/]", "")).isEqualTo(body);
        assertThat(evidence.get(0).path("header").asString()).isEqualTo(units.getFirst().header());
        assertThat(evidence.get(0).get("location")).isEqualTo(units.getFirst().location());
        var last = selected.candidates().getLast();
        var result = SchemaContracts.JSON.readTree(selected.reconstruct("{\"selectedIds\":[\"" + last.id() + "\",\"C1\"]}"));
        assertThat(result.get("sentences").get(0).get("citations").get(0).asInt()).isEqualTo(2);
        assertThat(result.get("sentences").get(1).path("text").asString()).isEqualTo("조건이 없으면 전송하지 않는다.");
        assertThat(selected.candidates().stream().filter(c -> c.text().startsWith("|"))).allMatch(c -> c.bodyLine() == 2);
    }

    @Test void strictEnvelopeUnknownDuplicateWrongTypesTrailingAndOverLimitAreRejected() {
        var selected = EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit("원문이다.")));
        for (String response : List.of("{}", "null", "{\"selectedIds\":[1]}", "{\"selectedIds\":[\"C2\"]}",
                "{\"selectedIds\":[\"C1\",\"C1\"]}", "{\"selectedIds\":[],\"text\":\"추가\"}",
                "{\"selectedIds\":[]} {}", "{\"selectedIds\":[],\"selectedIds\":[\"C1\"]}"))
            assertThatThrownBy(() -> selected.reconstruct(response)).isInstanceOf(IllegalArgumentException.class);
        String many = String.join(" ", java.util.stream.IntStream.range(0, 21).mapToObj(i -> "문장" + i + ".").toList());
        var twentyOne = EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit(many)));
        var response = SchemaContracts.JSON.writeValueAsString(Map.of("selectedIds", twentyOne.candidates().stream().map(EvidenceCandidateSelection.Candidate::id).toList()));
        assertThatThrownBy(() -> twentyOne.reconstruct(response)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void emptyOverlongAndDuplicateTextPreserveTheOriginalSelectionRules() {
        for (var units : List.of(List.of(EvidenceSpanSelectionTest.unit("가".repeat(1001))), List.<com.nori.ai.gateway.core.workflow.EvidenceUnit>of())) {
            var selected = EvidenceCandidateSelection.prepare(units);
            assertThat(selected.reconstruct("{\"selectedIds\":[]}")).isEqualTo("{\"sentences\":[]}");
            assertThatThrownBy(() -> selected.reconstruct("{\"selectedIds\":[\"C1\"]}")).isInstanceOf(IllegalArgumentException.class);
        }
        var selected = EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit("같은 문장이다.\n같은 문장이다.")));
        assertThat(selected.candidates()).hasSize(1);
        assertThat(selected.candidates().getFirst().bodyLine()).isEqualTo(1);
        assertThatThrownBy(() -> EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit("[C1@L1]충돌[/]"))))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void overlappingLiteralsRepeatedLinesAndUnicodeKeepTheActualSpanOffsets() {
        String body = "  - 긴 문장이다. 문장이다.\r\n긴 문장이다.\u2028\t문장이다. 끝이다.\n😀 다음이다.";
        var selected = EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit(body)));
        for (var candidate : selected.candidates())
            assertThat(body.substring(candidate.from(), candidate.to())).isEqualTo(candidate.text());
        assertThat(selected.candidates().stream().filter(c -> c.text().equals("문장이다.")).findFirst().orElseThrow().from())
            .isEqualTo(body.indexOf("문장이다.", body.indexOf("긴 문장이다.") + "긴 문장이다.".length()));
        String view = SchemaContracts.JSON.readTree(selected.data()).path("evidence").get(0).path("body").asString();
        assertThat(view.replaceAll("\\[C[0-9]+@L[0-9]+]|\\[/]", "")).isEqualTo(body);
        var repeatedPrefix = EvidenceCandidateSelection.prepare(List.of(EvidenceSpanSelectionTest.unit("  1. 1.")));
        assertThat(repeatedPrefix.candidates().getFirst().from()).isEqualTo(5);
        assertThat(repeatedPrefix.candidates().getFirst().text()).isEqualTo("1.");
    }

    @Test void reconstructionStillPassesThroughUnchangedNumericNounCitationAndStatusChecks() {
        var unit = EvidenceSpanSelectionTest.unit("설정 1.5ms는 비활성이면 전송하지 않는다.");
        var selected = EvidenceCandidateSelection.prepare(List.of(unit));
        var raw = selected.reconstruct("{\"selectedIds\":[\"C1\"]}");
        assertThat(new SentenceEvidenceCheck(text -> List.of()).check(raw, List.of(unit)).kind()).isEqualTo("ANSWER");
        var checker = new SentenceEvidenceCheck(text -> List.of(new com.nori.ai.gateway.core.knowledge.SearchLanguage.Term("새이름", "NNP")));
        assertThat(checker.check(raw, List.of(EvidenceSpanSelectionTest.unit("다른 본문"))).kind()).isEqualTo("NO_EVIDENCE");
    }
}
