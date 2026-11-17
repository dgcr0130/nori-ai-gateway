package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.core.scope.*;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ConversationCursorTest {
    private VerifiedSystemScope scope(String system,String actor) {
        return new VerifiedSystemScope(OwnerSystem.valueOf(system),"fixture",new ActorRef(actor),actor,Set.of(),"a".repeat(64),Instant.EPOCH,Instant.EPOCH.plusSeconds(60),"request");
    }
    @Test void exactPositionIsAuthenticatedForItsOwnerOnly() {
        var cursor=new ConversationCursor(); var scope=scope("New-7","actor"); var time=Instant.parse("2026-11-17T00:00:00.123456Z");
        String encoded=cursor.encode(scope,time,"id");
        assertThat(cursor.decode(scope,encoded)).isEqualTo(new ConversationCursor.Position(time,"id"));
        assertThat(cursor.decode(scope,"")).isNull();
        for(var foreign:new VerifiedSystemScope[]{scope("New-7","other"),scope("Other","actor")})
            assertThatThrownBy(()->cursor.decode(foreign,encoded)).isInstanceOf(IllegalArgumentException.class);
        for(String malformed:new String[]{"a",encoded+"x","."+encoded,"x".repeat(1025)})
            assertThatThrownBy(()->cursor.decode(scope,malformed)).isInstanceOf(IllegalArgumentException.class);
    }
}
