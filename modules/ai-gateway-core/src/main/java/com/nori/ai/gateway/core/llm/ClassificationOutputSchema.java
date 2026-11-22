package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.RegistryView;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.registry.TaskType;
import java.util.*;
import tools.jackson.databind.node.ObjectNode;

/** 등록 도구만 생성할 수 있게 하되 부족한 필수 인자는 기존 clarification 검사에 남깁니다. */
final class ClassificationOutputSchema {
    private ClassificationOutputSchema() {}

    static StructuredOutput create(RegistryView registry) {
        return create(registry,true);
    }

    static StructuredOutput create(RegistryView registry,boolean timeSourcePresent) {
        var calls = new ArrayList<Object>();
        for (var tool : registry.capabilities()) {
            var arguments = (ObjectNode) tool.inputSchema().deepCopy();
            arguments.remove("required");
            if(!timeSourcePresent&&arguments.path("properties").isObject())for(String bound:List.of("from","to")) {
                var properties=(ObjectNode)arguments.get("properties");
                if(properties.has(bound))properties.set(bound,SchemaContracts.JSON.valueToTree(Map.of("allOf",List.of(properties.get(bound),Map.of("type","null")))));
            }
            relocate(arguments,"/properties/toolCalls/items/anyOf/"+calls.size()+"/properties/arguments","tool"+calls.size()+"_");
            calls.add(object(Map.of("toolId", Map.of("enum", List.of(tool.toolId())), "arguments", arguments)));
        }
        var properties = new LinkedHashMap<String,Object>();
        properties.put("taskType", Map.of("enum", Arrays.stream(TaskType.values()).map(Enum::name).toList()));
        properties.put("toolCalls", Map.of("type", "array", "maxItems", calls.isEmpty() ? 0 : 4,
            "items", calls.isEmpty() ? Map.of() : Map.of("anyOf", calls)));
        properties.put("targets", Map.of("type", "array", "maxItems", 16, "items", object(Map.of("type", string(256), "ref", string(256)))));
        properties.put("timeWindow", timeSourcePresent?Map.of("anyOf", List.of(Map.of("type", "null"), object(Map.of("from", string(35), "to", string(35))))):Map.of("type","null"));
        properties.put("searchQuery", string(512));
        properties.put("retryQuery", string(512));
        return new StructuredOutput(SchemaContracts.JSON.writeValueAsString(object(properties)));
    }

    private static Map<String,Object> string(int max) { return Map.of("type", "string", "minLength", 1, "maxLength", max); }
    /** 원래 도구 schema 루트를 가리키던 로컬 참조는 감싼 위치에서도 같은 입력을 가리켜야 합니다. */
    private static void relocate(tools.jackson.databind.JsonNode node,String path,String anchorPrefix) {
        if(node.isObject()) {
            for(String key:List.of("$ref","$dynamicRef"))if(node.has(key)) {
                String ref=node.get(key).asString();
                ((ObjectNode)node).put(key,ref.equals("#")?"#"+path:ref.startsWith("#/")?"#"+path+ref.substring(1):"#"+anchorPrefix+ref.substring(1));
            }
            for(String key:List.of("$anchor","$dynamicAnchor"))if(node.has(key))((ObjectNode)node).put(key,anchorPrefix+node.get(key).asString());
            for(String key:List.of("$defs","definitions","properties","patternProperties","dependentSchemas"))
                if(node.path(key).isObject())for(var field:node.get(key).properties())relocate(field.getValue(),path,anchorPrefix);
            for(String key:List.of("allOf","anyOf","oneOf","prefixItems"))
                if(node.path(key).isArray())for(var child:node.get(key))relocate(child,path,anchorPrefix);
            for(String key:List.of("items","additionalProperties","unevaluatedProperties","unevaluatedItems","contains","propertyNames","not","if","then","else"))
                if(node.has(key))relocate(node.get(key),path,anchorPrefix);
        }
    }
    private static Map<String,Object> object(Map<String,?> properties) {
        return Map.of("type", "object", "properties", properties, "required", properties.keySet().stream().sorted().toList(), "additionalProperties", false);
    }
}
