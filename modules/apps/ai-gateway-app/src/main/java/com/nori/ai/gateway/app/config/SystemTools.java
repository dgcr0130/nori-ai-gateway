package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import java.util.*;
import tools.jackson.databind.JsonNode;

/** 업무·내장 도구의 유일한 등록 입력은 검증된 시스템 설정입니다. */
public final class SystemTools {
    private SystemTools() {}
    public static List<CapabilityManifest> manifests(GatewaySystemsProperties properties) {
        var result = new ArrayList<CapabilityManifest>();
        properties.systems().forEach((id,system) -> {
            var owner = OwnerSystem.valueOf(id);
            for (var tool : system.tools()) result.add(ConfiguredTool.create(owner,tool.id(),tool.description(),ToolKind.BUSINESS,
                SchemaContracts.JSON.valueToTree(tool.inputSchema()),SchemaContracts.JSON.valueToTree(tool.outputSchema()),tool.requiredPermissions(),tool.timeoutMs()));
            if (!system.apps().isEmpty()) result.add(builtin(owner,"logs.search",
                "이 시스템이 남긴 로그에서 대상·기간·검색어로 로그 줄을 찾는다. 설비 수신·발신 시각과 데이터, 이벤트, 오류 기록을 찾을 때 사용한다. 검색 위치:"
                    + String.join(",",system.apps().stream().map(app -> app.namespace()+"/"+app.name()).toList()),ToolKind.LOG,"AI_LOG_READ",true));
            if (!system.traces().isEmpty()) result.add(builtin(owner,"trace.search","이 시스템의 설정된 서비스·namespace 전체에서 기간으로 trace를 찾는다.",ToolKind.TRACE,"AI_TRACE_READ",false));
            if (!system.repositories().isEmpty()) result.add(builtin(owner,"git.searchCode","이 시스템에 등록된 저장소·ref·경로 안에서 코드를 찾는다.",ToolKind.GIT,"AI_CODE_READ",true));
        });
        return List.copyOf(result);
    }
    private static CapabilityManifest builtin(OwnerSystem owner,String id,String description,ToolKind kind,String permission,boolean search) {
        var input=SchemaContracts.JSON.createObjectNode().put("type","object").put("additionalProperties",false);
        var fields=input.putObject("properties");
        for (String key : search ? List.of("targetRef","from","to","q") : List.of("from","to")) {
            var field=fields.putObject(key); field.putArray("type").add("string").add("null");
            field.put("minLength",1).put("maxLength",switch(key){case "targetRef"->64;case "q"->256;default->35;});
        }
        JsonNode output=SchemaContracts.parse("{\"type\":\"object\",\"properties\":{\"items\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}}},\"required\":[\"items\"],\"additionalProperties\":false}");
        return ConfiguredTool.create(owner,id,description,kind,input,output,List.of(permission),10000);
    }
}
