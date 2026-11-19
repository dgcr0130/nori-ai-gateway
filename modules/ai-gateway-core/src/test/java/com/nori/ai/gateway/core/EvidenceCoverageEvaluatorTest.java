package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.workflow.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class EvidenceCoverageEvaluatorTest {
    // 품사 공급자와 독립적으로 충분성 정책을 검사하며 실제 Nori 태그는 DB adapter 시험에서 대조합니다.
    private final SearchLanguage language=text->Arrays.stream(SearchText.normalize(text).split(" ")).map(t->new SearchLanguage.Term(t,t.matches("[가-힣]+")?"NNG":"SL")).toList();
    private final EvidenceCoverageEvaluator coverage=new EvidenceCoverageEvaluator();
    @Test void nounsKeepOrderAreDeduplicatedAndFunctionalTermsAndCoreTermLimitApply() {
        assertThat(coverage.coreTerms("질문 설비 답변 상태 설비 설명 방법 설정 무엇 알려줘",language,2)).containsExactly("설비","상태");
        assertThat(coverage.coreTerms("설비 상태 설정 연결",language,8)).containsExactly("설비","상태","설정","연결");
    }
    @Test void identifiersTargetsAndAtLeastTwoNounsMustExistInActualSelectedBodies() {
        var selected=List.of(HybridEvidenceSelectionTest.other("설비 상태 T3 EQP-001"));
        assertThat(coverage.sufficient("설비 상태 T3",List.of("EQP-001"),selected,language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("설비 상태 T3",List.of("EQP-002"),selected,language,4,Map.of())).isFalse();
        assertThat(coverage.sufficient("설비 설정 T3",List.of(),selected,language,4,Map.of())).isFalse();
        assertThat(coverage.sufficient("설비",List.of(),selected,language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("T3",List.of(),selected,language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("3",List.of(),selected,language,4,Map.of())).isFalse();
        assertThat(coverage.sufficient("!",List.of(),selected,language,4,Map.of())).isFalse();
        assertThat(coverage.sufficient("설비 상태 T3",List.of(),List.of(),language,4,Map.of())).isFalse();
    }
    @Test void glossaryEquivalenceIsOneHopAndNeverAddsNounsFromModelQueryOrUnselectedParent() {
        var selected=List.of(HybridEvidenceSelectionTest.other("장비 상태"));
        assertThat(coverage.sufficient("설비 상태",List.of(),selected,language,4,Map.of("설비","장비"))).isTrue();
        assertThat(coverage.sufficient("설비 상태",List.of(),selected,language,4,Map.of("설비","기계","기계","장비"))).isFalse();
        assertThat(coverage.sufficient("질문 답변 설명 방법",List.of(),selected,language,4,Map.of())).isFalse();
    }
}
