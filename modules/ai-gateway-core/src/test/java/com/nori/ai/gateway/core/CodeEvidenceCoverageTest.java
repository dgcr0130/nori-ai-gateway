package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CodeEvidenceCoverageTest {
    final EvidenceCoverageEvaluator coverage=new EvidenceCoverageEvaluator();
    final SearchLanguage language=text->Arrays.stream(SearchText.normalize(text).split("[^가-힣a-z0-9_.-]+"))
        .map(t->new SearchLanguage.Term(t,t.matches("[가-힣]+")?"NNG":"SL")).toList();
    EvidenceUnit unit(String type,String status,String text) {
        return new EvidenceUnit(type,null,SchemaContracts.parse("{\"sourceRepo\":\"nori-tc-gateway\",\"path\":\"gateway/Use.java\",\"ref\":\"main\"}"),"",
            SchemaContracts.JSON.writeValueAsString(Map.of("text",text,"codeRefKind","CURRENT_APPROVED_REF")),List.of("AI_CODE_READ"),status,"2026-11-25T00:00:00Z",List.of(),SchemaContracts.parse("{}"),List.of(),List.of());
    }
    boolean sufficient(List<String> queries,List<EvidenceUnit> units) {
        // 신규 overload는 업무 대상과 코드 대상을 분리하여 기존 호출 계약을 유지합니다.
        return coverage.sufficient("EquipmentContextRegistry 클래스 사용 위치",List.of(),List.of(),queries,units,language,4,Map.of());
    }
    @Test void eachExecutedQueryNeedsAnExactBodyMatchInAnAcceptedGitUnit() {
        for(String type:List.of("GIT","SYSTEM","LOG","TRACE","DOCUMENT"))for(String status:List.of("SUCCESS","PARTIAL","STALE","CONFLICT","EMPTY","FAILED","DENIED","SOURCE_UNAVAILABLE")) {
            boolean expected=type.equals("GIT")&&Set.of("SUCCESS","PARTIAL","STALE","CONFLICT").contains(status);
            assertThat(sufficient(List.of("EquipmentContextRegistry"),List.of(unit(type,status,"class EquipmentContextRegistry {}")))).as("%s/%s",type,status).isEqualTo(expected);
        }
        var first=unit("GIT","SUCCESS","class EquipmentContextRegistry {}");
        assertThat(sufficient(List.of(),List.of(first))).isFalse();
        assertThat(sufficient(List.of("EquipmentContextRegistry","readRegistration"),List.of(first))).isFalse();
        assertThat(sufficient(List.of("EquipmentContextRegistry","readRegistration"),List.of(first,unit("GIT","PARTIAL","void readRegistration() {}")))).isTrue();
        assertThat(sufficient(List.of("EquipmentContextRegistry"),List.of(unit("GIT","SUCCESS","class EquipmentContextRegistryFactory {}")))).isFalse();
        assertThat(sufficient(List.of("equipmentcontextregistry"),List.of(first))).isTrue();
        assertThat(sufficient(List.of("EquipmentContextRegistry"),List.of())).isFalse();
    }
    @Test void codePathIsAnAlternativeAndLegacyBusinessAndIdentifierNounPathsStillWork() {
        var git=unit("GIT","SUCCESS","설비 상태 PHOTO_001");
        assertThat(coverage.sufficient("설비 상태 PHOTO_001",List.of(),List.of(),List.of(),List.of(git),language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("설비 설정 PHOTO_001",List.of(),List.of(),List.of(),List.of(git),language,4,Map.of())).isFalse();
        assertThat(coverage.sufficient("설비 상태 PHOTO_001",List.of(),List.of(),List.of("absentCodeTerm"),List.of(git),language,4,Map.of())).isTrue();
        assertThat(coverage.sufficient("EquipmentContextRegistry 클래스 사용 위치",List.of(),List.of("PHOTO_001"),List.of("absentCodeTerm"),List.of(unit("SYSTEM","SUCCESS","PHOTO_001")),language,4,Map.of())).isTrue();
    }
}
