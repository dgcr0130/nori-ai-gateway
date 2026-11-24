package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.io.IOException;
import java.util.*;
import tools.jackson.databind.JsonNode;

/** 지정 운영 로그에서 행의 ref·text·values와 조회 상태만 보존한 자료입니다. */
final class BusinessEvidenceFixtures {
    static final String SNAPSHOT_QUESTION="설비 CLN_001의 현재 운영 스냅샷을 조회해 연결 상태, 제어 상태, 설비 상태를 알려 주세요.";
    static final String MODEL_QUESTION="tc.model.read 도구를 사용하여 eqp=CLN_001에 연결된 모델과 메시지 정의를 읽어 주세요.";
    static final String MIXED_QUESTION="설비 CLN_001에 연결된 모델 정의와 모델 파라미터, SECS 또는 SOCKET 메시지 정의를 조회해 알려 주세요. 운영 상태 대신 모델 구성을 읽어 주세요.";
    static JsonNode load(String name) {
        try(var in=BusinessEvidenceFixtures.class.getResourceAsStream("/fixtures/business-grounding/"+name+".json")) {
            return SchemaContracts.JSON.readTree(Objects.requireNonNull(in));
        } catch(IOException failure){throw new AssertionError(failure);}
    }
    static List<EvidenceUnit> units(String name,String tool) {
        var data=load(name);var units=new ArrayList<EvidenceUnit>();
        for(var row:data.path("items"))units.add(new EvidenceUnit("SYSTEM",null,
            SchemaContracts.JSON.createObjectNode().put("toolId",tool).put("ref",row.path("ref").asString()).put("observedAt","2026-11-24T05:33:08Z"),
            "",row.path("text").asString(),List.of("READ"),data.path("status").asString(),"2026-11-24T05:33:08Z",
            SchemaContracts.JSON.convertValue(data.path("missingFields"),new tools.jackson.core.type.TypeReference<List<String>>(){}),
            SchemaContracts.JSON.createObjectNode(),List.of(),List.of(),row.path("values")));
        return List.copyOf(units);
    }
    static SearchLanguage language(String question,List<String> nouns) {
        // 코어 시험은 형태소 분석기를 대역으로 고정해 대상 판정과 명사 판정을 분리합니다.
        return text->text.equals(question)?nouns.stream().map(t->new SearchLanguage.Term(t,"NNG")).toList():
            Arrays.stream(SearchText.normalize(text).split("[^가-힣a-z0-9_.-]+")).map(t->new SearchLanguage.Term(t,"NNG")).toList();
    }
}
