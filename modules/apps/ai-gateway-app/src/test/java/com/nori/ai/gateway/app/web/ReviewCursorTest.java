package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.core.feedback.FeedbackFailure;
import com.nori.ai.gateway.core.scope.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class ReviewCursorTest {
    VerifiedSystemScope scope(String system){return new VerifiedSystemScope(OwnerSystem.valueOf(system),"fixture",new ActorRef("reviewer"),"reviewer",Set.of(),"a".repeat(64),Instant.MIN,Instant.MAX,"request");}
    @Test void signedCursorPreservesTimeIdAndBindsSystemStateAndServerInstance() {
        var cursor=new ReviewCursor();var time=Instant.parse("2026-11-19T01:02:03.123456Z");var id=UUID.randomUUID();String value=cursor.encode(scope("TC"),"OPEN",time,id);
        assertThat(cursor.decode(scope("TC"),"OPEN",value)).isEqualTo(new ReviewCursor.Position(time,id));assertThat(cursor.decode(scope("TC"),"OPEN","")).isNull();
        for(String invalid:List.of("bad",value+"x","x".repeat(1025)))assertThatThrownBy(()->cursor.decode(scope("TC"),"OPEN",invalid)).isInstanceOf(FeedbackFailure.class);
        assertThatThrownBy(()->cursor.decode(scope("Other"),"OPEN",value)).isInstanceOf(FeedbackFailure.class);
        assertThatThrownBy(()->cursor.decode(scope("TC"),"RESOLVED",value)).isInstanceOf(FeedbackFailure.class);
        assertThatThrownBy(()->new ReviewCursor().decode(scope("TC"),"OPEN",value)).isInstanceOf(FeedbackFailure.class);
    }
}
