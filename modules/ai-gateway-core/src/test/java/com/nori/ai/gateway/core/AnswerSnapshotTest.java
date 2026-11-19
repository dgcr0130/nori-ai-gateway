package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.result.AnswerSnapshot;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class AnswerSnapshotTest {
    @Test void bodyHashIsUtf8AndContentHashIgnoresObjectKeyOrderButKeepsEvidenceOrder() {
        var location=SchemaContracts.JSON.readTree("{\"ref\":\"EQP-001\",\"toolId\":\"read\",\"observedAt\":\"2026-11-19T00:00:00Z\"}");
        var unit=new EvidenceUnit("SYSTEM",null,location,location.toString(),"abc",List.of("B","A"),"PARTIAL","2026-11-19T00:00:00Z",List.of("gap"),SchemaContracts.JSON.readTree("{\"missingRanges\":[],\"unverifiedRanges\":[]}"),List.of("warning"),List.of());
        var answer=SchemaContracts.JSON.readTree("{\"kind\":\"NO_EVIDENCE\",\"text\":\"근거 없음\",\"citations\":[],\"sentences\":[]}");
        var value=AnswerSnapshot.create("질문",answer,List.of(unit),"a".repeat(64));var stored=value.evidence().get(0);
        assertThat(stored.path("bodySha256").asString()).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        assertThat(stored.path("number").asInt()).isOne();assertThat(stored.path("location")).isEqualTo(location);assertThat(stored.path("permissions").toString()).isEqualTo("[\"B\",\"A\"]");
        assertThat(stored.path("status").asString()).isEqualTo("PARTIAL");assertThat(stored.path("observedAt").asString()).isEqualTo("2026-11-19T00:00:00Z");
        var reordered=SchemaContracts.JSON.readTree("{\"sentences\":[],\"citations\":[],\"text\":\"근거 없음\",\"kind\":\"NO_EVIDENCE\"}");
        assertThat(new AnswerSnapshot("질문",reordered,value.evidence(),"a".repeat(64)).contentSha256()).isEqualTo(value.contentSha256());
        assertThat(new AnswerSnapshot("다른 질문",reordered,value.evidence(),"a".repeat(64)).contentSha256()).isNotEqualTo(value.contentSha256());
    }
}
