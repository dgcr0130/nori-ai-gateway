package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.SearchLanguage;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.GroundedAnswer;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 근거 작성 전용 검사이며 피드백 문서의 SentenceEvidenceCheck 계약은 변경하지 않습니다. */
class FactCheckTest {
    static final String FALLBACK="확인된 조회 결과를 문장으로 정리하지 못했습니다. 참고 자료의 근거 원문을 확인해 주세요.";
    final SearchLanguage language=text->List.of("건","명","연결","설비","상태","생성","일시").stream().filter(text::contains)
        .map(term->new SearchLanguage.Term(term,"NNG")).toList();
    final FactCheck check=new FactCheck(language);
    static EvidenceUnit unit(String body) {
        return new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("toolId","asset.snapshot").put("ref","CLN_001"),"",body,
            List.of("READ"),"SUCCESS","2026-11-24T01:37:32Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());
    }
    static EvidenceUnit git(String path,String text) {
        var body=SchemaContracts.JSON.createObjectNode().put("codeRefKind","CURRENT_APPROVED_REF").put("deployedSourceCommit","UNKNOWN").put("text",text);
        return new EvidenceUnit("GIT",null,SchemaContracts.JSON.createObjectNode().put("path",path).put("ref","main").put("sourceRepo","nori-tc-gateway"),"",
            SchemaContracts.JSON.writeValueAsString(body),List.of("AI_CODE_READ"),"SUCCESS","2026-11-24T01:37:32Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());
    }
    static Map<String,Object> row(String text,String... ids){return Map.of("text",text,"factIds",List.of(ids));}
    static String rows(Map<String,Object>... rows){return SchemaContracts.JSON.writeValueAsString(Map.of("sentences",List.of(rows)));}
    GroundedAnswer run(String text,List<EvidenceUnit> units,List<String> targets){return check.check(text,units,targets);}

    @Test void unknownLabelsIdDropsOnlyThatSentenceEvenAlongsideARealId() {
        var units=List.of(unit("CLN_001 OFFLINE DOWN"));
        var answer=run(rows(row("CLN_001의 연결 상태는 연결되지 않은 상태입니다.","e1","labels"),row("CLN_001의 설비 상태는 DOWN입니다.","e1")),units,List.of("CLN_001"));
        assertThat(answer.kind()).isEqualTo("PARTIAL");
        assertThat(answer.sentences()).extracting(GroundedAnswer.Sentence::text).containsExactly("CLN_001의 설비 상태는 DOWN입니다.");
    }
    @Test void valuesAndCitedBodiesSupplyExactNumbersVersionsIdentifiersAndKoreanUnitsOnly() {
        var first=unit("CLN_001 EQP-001 T3 S1F2 1.5ms 1,000 1.2.3 -2 10건").withValues(SchemaContracts.parse("{\"port\":6100}"));
        var units=List.of(first,unit("CLN_001 77 T30 EQP-0010"));
        for(String valid:List.of("6100","EQP-001","T3","S1F2","1.5ms","1,000","1.2.3","-2","10건입니다"))
            assertThat(run(rows(row(valid,"e1")),units,List.of()).sentences()).as(valid).hasSize(1);
        for(String invalid:List.of("77","T30","EQP-00","1","000","1.2","3","2","10명","0.0015s"))
            assertThat(run(rows(row(invalid,"e1")),units,List.of()).sentences()).as(invalid).isEmpty();
    }
    @Test void originalUtcAndConvertedKstAreSameValueButAnInventedHourIsRemoved() {
        var units=List.of(unit("CLN_001 createdAt: 2026-04-28T05:02:28.248019Z"));
        for(String valid:List.of("2026-04-28T05:02:28.248019Z","2026-04-28 14:02","2026-04-28 14:02:28 (KST)"))
            assertThat(run(rows(row("생성 일시는 "+valid+"입니다.","e1")),units,List.of()).sentences()).as(valid).hasSize(1);
        assertThat(run(rows(row("생성 일시는 2026-04-28 15:02입니다.","e1")),units,List.of()).sentences()).isEmpty();
    }
    @Test void eachCitedSystemBodyMustMatchAnExactBusinessTargetAndDocumentsAreExempt() {
        var units=List.of(unit("CLN_001 6100"),unit("PHOTO_0010 6100"),unit("PHOTO_001 6100"));
        assertThat(run(rows(row("6100","e1","e3")),units,List.of("PHOTO_001")).sentences()).isEmpty();
        assertThat(run(rows(row("6100","e2")),units,List.of("PHOTO_001")).sentences()).isEmpty();
        assertThat(run(rows(row("6100","e3")),units,List.of("PHOTO_001")).sentences()).hasSize(1);
        assertThat(run(rows(row("6100","e1")),units,List.of()).sentences()).hasSize(1);
        var doc=new EvidenceUnit("DOCUMENT","parent",SchemaContracts.JSON.createObjectNode(),"안내","등록 정보 조회 안내",List.of("READ"),"SUCCESS",null,List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());
        assertThat(run(rows(row("등록 정보를 안내합니다.","e1")),List.of(doc),List.of("PHOTO_001")).sentences()).hasSize(1);
    }
    @Test void independentSession12VerbatimCannotTransferClnRegistrationToPhoto() {
        String text="PHOTO_001 호기의 등록 정보는 CLN_001 설비로, 설비 키는 25이며, 라인 키는 1입니다. 통신 인터페이스는 SOCKET이며, 통신 모드는 PASSIVE입니다. 이 설비는 개발용으로 사용 중이며, 경로 파티션은 0입니다. IP 주소는 0.0.0.0이고, 포트 번호는 6100입니다. 모델 버전 키는 77이며, 적용된 매개변수 버전은 26.04.28.0000입니다. 게이트웨이 JAR 키와 비즈니스 JAR 키는 없습니다. 설비는 활성화되어 있으며, 생성 일시는 2026-04-28T05:02:28.248019Z이고, 최종 수정 일시는 2026-07-08T23:32:51.263184Z입니다. 생성자와 수정자는 각각 SYSTEM과 admin입니다.";
        // 숫자 대조가 아니라 업무 대상 대조가 이 반례를 거절하도록 모든 토큰을 근거에 제공합니다.
        var answer=run(rows(row(text,"e1")),List.of(unit(text.replace("PHOTO_001", "CLN_001")).withValues(SchemaContracts.JSON.createObjectNode().put("questionTarget","PHOTO_001"))),List.of("PHOTO_001"));
        assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.text()).isEqualTo(FALLBACK);
        assertThat(answer.citations()).extracting(GroundedAnswer.Citation::number).containsExactly(1);
        assertThat(answer.sentences()).isEmpty();
        // 같은 원문과 토큰으로 대상 제약을 비우면 통과하므로 대상 검사가 반례의 거절 원인입니다.
        assertThat(run(rows(row(text,"e1")),List.of(unit(text.replace("PHOTO_001","CLN_001")).withValues(SchemaContracts.JSON.createObjectNode().put("questionTarget","PHOTO_001"))),List.of()).sentences()).hasSize(1);
    }
    @Test void conversationalKoreanNounsAreNotRequiredToAppearInMixedLanguageEvidence() {
        var answer=run(rows(row("CLN_001의 연결 상태는 OFFLINE이며 설비 상태는 DOWN입니다.","e1")),List.of(unit("{\"eqpId\":\"CLN_001\",\"connectionState\":\"OFFLINE\",\"eqpState\":\"DOWN\"}")),List.of("CLN_001"));
        assertThat(answer.kind()).isEqualTo("ANSWER");assertThat(answer.sentences()).hasSize(1);
    }
    @Test void codeSentencesNameOnlyIdentifiersFoundInCitedSnippetsOrTheirRepositoryPaths() {
        var units=List.of(git("gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java",
                "import com.nori.tc.gateway.core.context.service.EquipmentContextRegistry;\n    private final EquipmentContextRegistry contextRegistry;\n"),
            git("gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java",
                "    GatewayUiRuntimeControlSupport(\n            final EquipmentContextRegistry contextRegistry,\n"));
        // 경로의 파일 이름·경로 조각과 조각 안의 전체 이름이 근거입니다. 이름의 앞부분만 쓰는 것은 기존처럼 인정하지 않습니다.
        for(String valid:List.of("EquipmentContextRegistry는 GatewayEquipmentService에서 필드로 사용됩니다.",
                "GatewayEquipmentService.java와 kafka 어댑터의 GatewayUiRuntimeControlSupport가 EquipmentContextRegistry를 받습니다.",
                "GatewayEquipmentService.java는 com.nori.tc.gateway.core.context.service.EquipmentContextRegistry를 가져옵니다."))
            assertThat(run(rows(row(valid,"e1","e2")),units,List.of()).sentences()).as(valid).hasSize(1);
        // 인용한 근거 어디에도 없는 클래스 이름이 있으면 문장을 제거합니다.
        var diagnostics=new ArrayList<LlmDiagnostic>();
        var checked=check.evaluate(rows(row("EquipmentContextRegistry는 EquipmentStateManager에서 사용됩니다.","e1","e2")),units,List.of(),diagnostics::add);
        assertThat(checked.sentences()).isEmpty();assertThat(diagnostics).containsExactly(LlmDiagnostic.SENTENCE_NAME);
        // 인용하지 않은 근거의 파일 이름은 근거가 아닙니다.
        assertThat(run(rows(row("EquipmentContextRegistry는 GatewayUiRuntimeControlSupport에서 사용됩니다.","e1")),units,List.of()).sentences()).isEmpty();
    }
    @Test void allRemovedOrEmptyOutputIsPartialWithAllFactReferencesAndFixedFallback() {
        var units=List.of(unit("CLN_001"),unit("PHOTO_001"));
        for(String output:List.of(rows(row("999","e1")),rows(),"not-json")) {
            var answer=run(output,units,List.of());assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.text()).isEqualTo(FALLBACK);
            assertThat(answer.citations()).extracting(GroundedAnswer.Citation::number).containsExactly(1,2);assertThat(answer.sentences()).isEmpty();
        }
    }
}
