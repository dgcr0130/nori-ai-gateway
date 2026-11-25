package com.nori.ai.gateway.core.blocks;

import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ResultBlock;
import java.util.*;

/** 설정 도구는 예시 질문을 갖지 않으므로(ConfiguredTool) 기능 목록은 기능 ID와 설명만 보입니다. */
public final class GeneralQueryHelpBuilder {
    public List<ResultBlock> build(RegistryView view,Set<String> requestPerm) {
        var value=SchemaContracts.JSON.createObjectNode();value.putArray("columns").add("capabilityId").add("description");var rows=value.putArray("rows");
        for(var manifest:view.capabilities()) {
            if(manifest.availability()!=Availability.IMPLEMENTED||manifest.requiredPermission().stream().noneMatch(requestPerm::contains))continue;
            rows.addObject().put("capabilityId",manifest.capabilityId()).put("description",manifest.description());
        }
        var block=new ResultBlock("table",null,value);new UiSchemaValidator().validate(block,view.ownerSystem());return List.of(block);
    }
}
