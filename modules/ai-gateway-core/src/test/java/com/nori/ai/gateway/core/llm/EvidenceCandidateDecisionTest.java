package com.nori.ai.gateway.core.llm;

import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class EvidenceCandidateDecisionTest {
    @Test void everyCandidateRequiresItsOwnBooleanAndReconstructionKeepsEachOriginalSource() {
        var units=List.of(EvidenceSpanSelectionTest.unit("절차 설명이다. 조건 없이도 저장한다."),EvidenceSpanSelectionTest.unit("같은 조건 없이도 저장한다."));
        var selector=EvidenceCandidateSelection.prepare(units);
        assertThat(selector.decisionOutput().accepts("{\"C1\":false,\"C2\":true,\"C3\":true}")).isTrue();
        for(String invalid:List.of("{\"C2\":true}","{\"C1\":false,\"C2\":1,\"C3\":true}","{\"C1\":false,\"C2\":true,\"C3\":true,\"extra\":false}"))
            assertThat(selector.decisionOutput().accepts(invalid)).isFalse();
        var answer=new SentenceEvidenceCheck(text->List.of()).check(selector.reconstructDecisionRows("{\"C1\":false,\"C2\":true,\"C3\":true}",reason->{}),units);
        assertThat(answer.sentences()).extracting(s->s.text()).containsExactly("조건 없이도 저장한다.","같은 조건 없이도 저장한다.");
        assertThat(answer.sentences()).extracting(s->s.citations()).containsExactly(List.of(1),List.of(2));
    }

    @Test void malformedOrIncompleteEnvelopeCannotBecomeAnAnswerAndInvalidIndividualDecisionIsPartial() {
        var units=List.of(EvidenceSpanSelectionTest.unit("첫 원문이다. 두 번째다."));var selector=EvidenceCandidateSelection.prepare(units);
        var checker=new SentenceEvidenceCheck(text->List.of());var diagnostics=new ArrayList<LlmDiagnostic>();
        for(String raw:List.of("{\"C1\":true}","{\"C1\":true,\"C2\":false,\"extra\":false}","{\"C1\":true,\"C2\":false} {}","{\"C1\":true,\"C1\":false,\"C2\":false}","{\"selectedIds\":[\"C1\"]}","null"))
            assertThat(checker.check(selector.reconstructDecisionRows(raw,diagnostics::add),units).kind()).isEqualTo("NO_EVIDENCE");
        var partial=checker.check(selector.reconstructDecisionRows("{\"C1\":true,\"C2\":\"yes\"}",diagnostics::add),units);
        assertThat(partial.kind()).isEqualTo("PARTIAL");assertThat(partial.text()).isEqualTo("첫 원문이다.[1]");
        assertThat(checker.check(selector.reconstructDecisionRows("{\"C1\":false,\"C2\":false}",diagnostics::add),units).kind()).isEqualTo("NO_EVIDENCE");
        assertThat(diagnostics).contains(LlmDiagnostic.SENTENCE_STRUCTURE,LlmDiagnostic.SENTENCE_JSON,LlmDiagnostic.COMPOSE_SELECTION);
    }

    @Test void outputCapIsPreservedWithoutSilentlyTruncatingSelectedFacts() {
        var unit=EvidenceSpanSelectionTest.unit(String.join(" ",java.util.stream.IntStream.rangeClosed(1,21).mapToObj(i->"원문"+i+"다.").toList()));
        var selector=EvidenceCandidateSelection.prepare(List.of(unit));var decisions=com.nori.ai.gateway.core.registry.SchemaContracts.JSON.createObjectNode();
        selector.candidates().forEach(c->decisions.put(c.id(),true));
        var answer=new SentenceEvidenceCheck(text->List.of()).check(selector.reconstructDecisionRows(decisions.toString(),reason->{}),List.of(unit));
        assertThat(answer.kind()).isEqualTo("NO_EVIDENCE");assertThat(selector.validDecisionIds(decisions.toString())).isEmpty();
    }
}
