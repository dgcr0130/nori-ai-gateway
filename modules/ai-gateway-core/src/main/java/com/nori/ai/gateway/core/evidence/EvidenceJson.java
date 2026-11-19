package com.nori.ai.gateway.core.evidence;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import tools.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** 근거 JSON 키는 UTF-8 바이트순이며 배열 순서는 호출·선택 순서 그대로 보존합니다. */
public final class EvidenceJson {
    private EvidenceJson() {}
    public static String write(JsonNode value){return SchemaContracts.JSON.writeValueAsString(ordered(value));}
    private static JsonNode ordered(JsonNode value) {
        if(value.isObject()) {
            var result=SchemaContracts.JSON.createObjectNode();var keys=new ArrayList<>(value.propertyNames());
            keys.sort((a,b)->Arrays.compareUnsigned(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8)));
            for(String key:keys)result.set(key,ordered(value.get(key)));return result;
        }
        if(value.isArray()){var result=SchemaContracts.JSON.createArrayNode();for(var item:value)result.add(ordered(item));return result;}
        return value.deepCopy();
    }
}
