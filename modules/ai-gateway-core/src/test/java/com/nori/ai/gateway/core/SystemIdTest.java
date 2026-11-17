package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SystemIdTest {
    @Test void valuesRoundTripAsStringsAndReferencesBindByValueAndCase() {
        var owner=OwnerSystem.valueOf("New_System-7");
        assertThat(SchemaContracts.JSON.writeValueAsString(owner)).isEqualTo("\"New_System-7\"");
        assertThat(SchemaContracts.JSON.readValue("\"New_System-7\"",OwnerSystem.class)).isEqualTo(owner);
        assertThat(new OwnedReference(owner,"result://New_System-7/a").belongsTo(OwnerSystem.valueOf("New_System-7"))).isTrue();
        assertThat(new OwnedReference(owner,"result://new_System-7/a").belongsTo(owner)).isFalse();
        assertThat(OwnerSystem.valueOf("TC")).isEqualTo(OwnerSystem.TC).isNotSameAs(OwnerSystem.TC);
    }
    @Test void invalidIdsAndNonStringJsonAreRejected() {
        for(String id:new String[]{"","_x","a b","a/b","가","a".repeat(65)})
            assertThatThrownBy(()->OwnerSystem.valueOf(id)).isInstanceOf(IllegalArgumentException.class);
        assertThat(OwnerSystem.valueOf("a".repeat(64))).isNotNull();
        for(String json:new String[]{"{}","123","true"}) assertThatThrownBy(()->SchemaContracts.JSON.readValue(json,OwnerSystem.class)).isInstanceOf(RuntimeException.class);
    }
}
