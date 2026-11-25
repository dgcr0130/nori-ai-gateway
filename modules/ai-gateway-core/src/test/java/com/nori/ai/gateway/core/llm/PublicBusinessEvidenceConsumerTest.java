package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** TC 공개 투영 계약의 같은 본문이 LLM·직접 조립에 전달되고 내부 ref는 업무 본문에 추가되지 않습니다. */
class PublicBusinessEvidenceConsumerTest {
    @Test void samePublicFactsFeedModelAndDirectAssemblyWithoutPromotingInternalReferences() {
        String tool = "tc.eqp.readRegistration";
        String body = "[설비 등록 정보]\n조회 조건 > 설비(eqp): ETC_001\n설비 ID(eqpId): ETC_001\n"
                + "라인 코드(lineCode): LINE_A\n라인 이름(lineName): A line\n모델 이름(modelName): ETC_MODEL\n"
                + "모델 버전(modelVersion): v77\n적용 파라미터 버전(appliedParamVersion): params-v9\n설비 포트(eqpPort): 6100";
        var values = SchemaContracts.JSON.valueToTree(Map.of("eqpId", "ETC_001", "lineCode", "LINE_A", "lineName", "A line",
                "modelName", "ETC_MODEL", "modelVersion", "v77", "appliedParamVersion", "params-v9", "eqpPort", 6100));
        var location = SchemaContracts.JSON.createObjectNode().put("toolId", tool).put("ref", "181:77:internal-reference");
        var unit = new EvidenceUnit("SYSTEM", null, location, "", body, List.of("EQP_READ"), "SUCCESS",
                "2026-11-25T00:00:00Z", List.of(), SchemaContracts.JSON.createObjectNode(), List.of(), List.of(), values);
        String llm = GroundedFacts.data("ETC_001 등록 정보", List.of(unit), Optional.empty());
        var direct = GroundedAnswerAssembly.assemble(new FactCheck.Checked(List.of(), false), List.of(unit), Map.of(), Set.of(tool));
        for (String text : List.of(llm, direct.text())) {
            assertThat(text).contains("ETC_001", "LINE_A", "A line", "ETC_MODEL", "v77", "params-v9", "6100")
                    .doesNotContain("eqpKey", "lineKey", "modelVersionKey", "181:77:internal-reference");
        }
        assertThat(direct.citations().getFirst().location()).isEqualTo(location);
        assertThat(unit.values()).isEqualTo(values);
    }
}
