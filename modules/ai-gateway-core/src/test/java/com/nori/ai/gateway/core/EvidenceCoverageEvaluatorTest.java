package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.workflow.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class EvidenceCoverageEvaluatorTest {
    @Test void englishSnapshotUsesTheBusinessTargetWithoutQuestionNounsOrTimeAndCountTokens() {
        var units=BusinessEvidenceFixtures.units("snapshot","tc.eqp.getOperationalSnapshot");
        String question=BusinessEvidenceFixtures.SNAPSHOT_QUESTION+" 10:37 기준 1건";
        var terms=BusinessEvidenceFixtures.language(question,List.of("설비","현재","운영","스냅샷"));
        assertThat(units).hasSize(1);
        assertThat(coverage.sufficient(BusinessEvidenceFixtures.SNAPSHOT_QUESTION,List.of("CLN_001"),units,
            BusinessEvidenceFixtures.language(BusinessEvidenceFixtures.SNAPSHOT_QUESTION,List.of("설비","현재","운영","스냅샷")),4,Map.of())).isTrue();
        assertThat(coverage.sufficient(question,List.of("CLN_001"),units,terms,4,Map.of())).isTrue();
    }
    @Test void twentyFiveModelRowsNeedOnlyTheExecutedBusinessTarget() {
        var units=BusinessEvidenceFixtures.units("model","tc.model.read");
        var terms=BusinessEvidenceFixtures.language(BusinessEvidenceFixtures.MODEL_QUESTION,List.of("도구","사용","연결","모델"));
        assertThat(units).hasSize(25);
        var bodyTerms=units.stream().flatMap(u->terms.analyze(u.body()).stream()).map(SearchLanguage.Term::text).collect(java.util.stream.Collectors.toSet());
        assertThat(coverage.coreTerms(BusinessEvidenceFixtures.MODEL_QUESTION,terms,4).stream().filter(bodyTerms::contains).toList()).containsExactly("모델");
        assertThat(coverage.sufficient(BusinessEvidenceFixtures.MODEL_QUESTION,List.of("CLN_001"),units,terms,4,Map.of())).isTrue();
    }
    @Test void documentOnlyEvidenceKeepsIdentifierAndNounRequirements() {
        var document=EvidenceUnit.document(HybridEvidenceSelectionTest.hit(0,"설비 상태 CLN_001"),List.of());
        assertThat(coverage.sufficient("설비 상태 CLN_001",List.of("CLN_001"),List.of(document),language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("설비 설정 CLN_001",List.of("CLN_001"),List.of(document),language,4,Map.of())).isFalse();
        assertThat(coverage.sufficient("설비 상태 CLN_001 T3",List.of("CLN_001"),List.of(document),language,4,Map.of())).isFalse();
    }
    @Test void businessTargetsDoNotReplaceClassificationTargetsForTheLegacyPath() {
        var document=EvidenceUnit.document(HybridEvidenceSelectionTest.hit(0,"설비 상태 CLASS_001"),List.of());
        assertThat(coverage.sufficient("설비 상태",List.of("CLASS_001"),List.of("EXEC_001"),List.of(document),language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("설비 상태",List.of("EXEC_001"),List.of("CLASS_001"),List.of(document),language,4,Map.of())).isFalse();
        var system=List.of(HybridEvidenceSelectionTest.other("EXEC_001"));
        assertThat(coverage.sufficient("설비 상태 CLASS_001",List.of("CLASS_001"),List.of("EXEC_001"),system,language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("설비 상태 CLASS_001",List.of("CLASS_001"),List.of(),system,language,4,Map.of())).isFalse();
    }
    @Test void differentTargetAndOnlyOneOfTwoTargetsCannotCoverBusinessRequest() {
        var units=BusinessEvidenceFixtures.units("snapshot","tc.eqp.getOperationalSnapshot");
        String question="PHOTO_001 CLN_001 상태 비교";
        var terms=BusinessEvidenceFixtures.language(question,List.of("상태","비교"));
        assertThat(coverage.sufficient(question,List.of("PHOTO_001"),units,terms,4,Map.of())).isFalse();
        assertThat(coverage.sufficient(question,List.of("PHOTO_001","CLN_001"),units,terms,4,Map.of())).isFalse();
        var both=new ArrayList<>(units);both.add(HybridEvidenceSelectionTest.other("PHOTO_001"));
        assertThat(coverage.sufficient(question,List.of("PHOTO_001","CLN_001"),both,terms,4,Map.of())).isTrue();
        assertThat(coverage.sufficient(question,List.of("CLN_00"),units,terms,4,Map.of())).isFalse();
    }
    @Test void onlySystemUnitsWithAcceptedQueryStatusesMayUseTheBusinessPath() {
        var source=BusinessEvidenceFixtures.units("snapshot","tc.eqp.getOperationalSnapshot").getFirst();
        var terms=BusinessEvidenceFixtures.language(BusinessEvidenceFixtures.SNAPSHOT_QUESTION,List.of("설비","현재","운영","스냅샷"));
        for(String type:List.of("DOCUMENT","LOG","TRACE","GIT","SYSTEM"))for(String status:List.of("SUCCESS","EMPTY","PARTIAL","STALE","CONFLICT","FAILED","DENIED","TIMEOUT","SOURCE_UNAVAILABLE")) {
            var unit=new EvidenceUnit(type,source.parentKey(),source.location(),source.header(),source.body(),source.permissions(),status,source.observedAt(),source.missingFields(),source.retentionMeta(),source.warnings(),source.childLocations(),source.values());
            boolean accepted=type.equals("SYSTEM")&&Set.of("SUCCESS","EMPTY","PARTIAL","STALE","CONFLICT").contains(status);
            assertThat(coverage.sufficient(BusinessEvidenceFixtures.SNAPSHOT_QUESTION,List.of("CLN_001"),List.of(unit),terms,4,Map.of())).as("%s/%s",type,status).isEqualTo(accepted);
        }
        assertThat(coverage.sufficient(BusinessEvidenceFixtures.SNAPSHOT_QUESTION,List.of(),List.of(source),terms,4,Map.of())).isFalse();
    }
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
