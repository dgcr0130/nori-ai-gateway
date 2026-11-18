package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.dispatch.ToolResultContractValidator;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.core.DomainFixtures.*;

class ConfiguredToolTest {
    private CapabilityManifest tool(OwnerSystem owner,List<String> permissions) {
        return ConfiguredTool.create(owner,"read.data","자료 조회",ToolKind.BUSINESS,
            SchemaContracts.parse("{\"type\":\"object\",\"additionalProperties\":false}"),
            SchemaContracts.parse("{\"type\":\"object\",\"properties\":{\"value\":{\"type\":\"integer\"}},\"required\":[\"value\"],\"additionalProperties\":false}"),permissions,10000);
    }
    @Test void sameToolIdsAreScopedBySystemAndAllPermissionsAreRequired() {
        var first=OwnerSystem.valueOf("FIRST"); var second=OwnerSystem.valueOf("SECOND");
        var registry=new CapabilityRegistry(List.of(tool(first,List.of("READ_A","READ_B")),tool(second,List.of("READ_A"))),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        assertThat(registry.find("read.data")).isEmpty();
        assertThat(registry.find(scope(first,Set.of("READ_A","READ_B")),"read.data").orElseThrow().ownerSystem()).isEqualTo(first);
        assertThat(registry.find(scope(second,Set.of("READ_A")),"read.data").orElseThrow().ownerSystem()).isEqualTo(second);
        assertThat(registry.view(scope(first,Set.of("READ_A"))).capabilities()).isEmpty();
        assertThat(registry.view(scope(first,Set.of("READ_A","READ_B"))).capabilities()).hasSize(1);
        assertThat(registry.hasToolId("read.data")).isTrue(); assertThat(registry.hasToolId("unknown")).isFalse();
        var audit=new com.nori.ai.gateway.core.audit.AuditFieldAllowlist(new com.nori.ai.gateway.core.sanitize.ContentSanitizer(),registry);
        for(var owner:List.of(first,second)) {
            var scope=scope(owner,Set.of("READ_A","READ_B"));
            audit.requireGeneral(com.nori.ai.gateway.core.audit.AuditEvent.toolCall(scope,"11111111-1111-4111-8111-111111111111","22222222-2222-4222-8222-222222222222",
                "read.data","sources://"+owner.name()+"/read.data",com.nori.ai.gateway.core.result.ResultStatus.SUCCESS,null,0));
        }
        assertThatThrownBy(()->new CapabilityRegistry(List.of(tool(first,List.of("READ_A")),tool(first,List.of("READ_B"))),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy())).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void standardBusinessOutputChecksEveryValuesObjectWithoutTreatingTheEnvelopeAsOneItem() {
        var tool=tool(OwnerSystem.valueOf("FIRST"),List.of("READ_A"));
        assertThat(ToolResultContractValidator.outputMatches(tool,SchemaContracts.parse("{\"items\":[{\"ref\":\"row\",\"text\":\"사실\",\"values\":{\"value\":1}}]}"))).isTrue();
        for(String invalid:List.of("{\"value\":1}","{\"items\":[{\"ref\":\"row\",\"text\":\"사실\",\"values\":{\"value\":\"1\"}}]}","{\"items\":[{\"ref\":\"row\",\"text\":\"사실\",\"values\":{\"value\":1},\"extra\":1}]}"))
            assertThat(ToolResultContractValidator.outputMatches(tool,SchemaContracts.parse(invalid))).isFalse();
    }
}
