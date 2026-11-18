package com.nori.ai.gateway.core.registry;

import com.nori.ai.gateway.core.scope.OwnerSystem;
import java.util.*;
import tools.jackson.databind.JsonNode;

/** 설정의 한 건 출력 schema를 raw 실행 결과의 items 외피와 구별합니다. */
public final class ConfiguredTool {
    private ConfiguredTool() {}
    public static CapabilityManifest create(OwnerSystem system,String id,String description,ToolKind kind,JsonNode input,
            JsonNode output,List<String> permissions,long timeoutMs) {
        return new CapabilityManifest(id,id,"1.0",system,kind,"sources://"+system.name()+"/"+id,Availability.IMPLEMENTED,
            ImplementationPhase.PHASE_1,description,List.of(),RiskLevel.READ,"READ",SideEffects.NONE,"AUTHENTICATED_ACTOR",
            Set.of("TARGET"),permissions,"READ",false,false,true,true,new RetryPolicy(1,0),
            "schema://"+system.name()+"/"+id+"/input/1.0","schema://"+system.name()+"/"+id+"/output/1.0",
            input,output,SchemaContracts.digest(input,output),timeoutMs,16,Map.of(),"METADATA_ONLY","system-query-1","LIVE","RESULT",1);
    }
}
