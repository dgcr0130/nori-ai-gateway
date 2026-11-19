package com.nori.ai.gateway.core.workflow;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import tools.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** 선택·충분성·작성 소비자가 공유하는 전체 단위입니다. 영구 저장은 후속 phase가 담당합니다. */
public record EvidenceUnit(String sourceType,String parentKey,JsonNode location,String header,String body,
        List<String> permissions,String status,String observedAt,List<String> missingFields,JsonNode retentionMeta,
        List<String> warnings,List<DocumentLocation> childLocations,JsonNode values) {
    public EvidenceUnit(String sourceType,String parentKey,JsonNode location,String header,String body,List<String> permissions,String status,String observedAt,List<String> missingFields,JsonNode retentionMeta,List<String> warnings,List<DocumentLocation> childLocations) {
        this(sourceType,parentKey,location,header,body,permissions,status,observedAt,missingFields,retentionMeta,warnings,childLocations,null);
    }
    public EvidenceUnit { permissions=List.copyOf(permissions);missingFields=List.copyOf(missingFields);warnings=List.copyOf(warnings);childLocations=List.copyOf(childLocations); }
    public static EvidenceUnit document(DocumentSearch.Hit hit,List<DocumentLocation> children) {
        return new EvidenceUnit("DOCUMENT",hit.parent().id().toString(),SchemaContracts.JSON.valueToTree(hit.parent().location()),hit.parent().header(),hit.parent().bodyMasked(),
            hit.permissions().stream().sorted((a,b)->Arrays.compareUnsigned(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8))).toList(),"SUCCESS",null,List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),children);
    }
    public EvidenceUnit withValues(JsonNode value){return new EvidenceUnit(sourceType,parentKey,location,header,body,permissions,status,observedAt,missingFields,retentionMeta,warnings,childLocations,value);}
    @Override public String toString(){return "EvidenceUnit[보호됨]";}
}
