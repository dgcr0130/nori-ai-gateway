package com.nori.ai.gateway.core.result;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import tools.jackson.databind.JsonNode;

/** V8의 기존 JSONB로 영구 재현 자료와 본문 해시를 보관합니다. 내부 ID는 이 자료에 없습니다. */
public record AnswerSnapshot(String questionMasked,JsonNode answer,JsonNode evidence,String configSha256) {
    public AnswerSnapshot {
        Objects.requireNonNull(questionMasked);
        if(answer==null||!answer.isObject()||evidence==null||!evidence.isArray()||configSha256==null||!configSha256.matches("[0-9a-f]{64}"))throw new IllegalArgumentException("답변 스냅샷 계약이 잘못되었습니다");
        for(int i=0;i<evidence.size();i++) {
            var unit=evidence.get(i);
            if(!unit.path("number").isIntegralNumber()||unit.path("number").asInt()!=i+1||!unit.path("bodyMasked").isString()||!sha(unit.path("bodyMasked").asString()).equals(unit.path("bodySha256").asString())
                    ||!unit.path("location").isObject()||!unit.path("permissions").isArray()||unit.path("permissions").isEmpty()||!Set.of("DOCUMENT","SYSTEM","LOG","TRACE","GIT").contains(unit.path("sourceType").asString()))throw new IllegalArgumentException("근거 스냅샷 결속이 다릅니다");
            for(var permission:unit.get("permissions"))if(!permission.isString()||permission.asString().isBlank())throw new IllegalArgumentException("근거 스냅샷 권한이 잘못되었습니다");
        }
        answer=answer.deepCopy();evidence=evidence.deepCopy();
    }
    public static AnswerSnapshot create(String question,JsonNode answer,List<EvidenceUnit> units,String config) {
        var evidence=SchemaContracts.JSON.createArrayNode();
        for(int i=0;i<units.size();i++) {
            var unit=units.get(i);var row=evidence.addObject().put("number",i+1).put("sourceType",unit.sourceType()).put("bodyMasked",unit.body()).put("bodySha256",sha(unit.body()));
            row.set("location",unit.location());row.set("permissions",SchemaContracts.JSON.valueToTree(unit.permissions()));
            if(!unit.sourceType().equals("DOCUMENT")) {
                row.put("status",unit.status()).put("observedAt",unit.observedAt());row.set("missingFields",SchemaContracts.JSON.valueToTree(unit.missingFields()));
                row.set("retentionMeta",unit.retentionMeta());row.set("warnings",SchemaContracts.JSON.valueToTree(unit.warnings()));
            }
        }
        return new AnswerSnapshot(question,answer,evidence,config);
    }
    public String contentSha256(){return sha(SchemaContracts.canonicalize(SchemaContracts.JSON.writeValueAsString(Map.of("questionMasked",questionMasked,"answer",answer,"evidence",evidence,"configSha256",configSha256))));}
    public static String sha(String text) {
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException impossible){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(AnswerSnapshot.class.getName()),"ANSWER_SNAPSHOT_FAILED",impossible);throw new IllegalStateException("본문 해시를 계산할 수 없습니다");}
    }
    @Override public String toString(){return "AnswerSnapshot[보호됨]";}
}
