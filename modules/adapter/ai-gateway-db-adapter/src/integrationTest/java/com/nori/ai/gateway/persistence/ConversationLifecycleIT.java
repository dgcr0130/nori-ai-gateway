package com.nori.ai.gateway.persistence;

import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.repository.*;
import com.nori.ai.gateway.persistence.maintenance.RetentionMaintenanceRepository;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.*;

class ConversationLifecycleIT extends PgIntegrationSupport {
    static final Instant NOW=Instant.parse("2026-11-17T00:00:00Z");
    static class TestClock { @Bean Clock testClock(){return Clock.fixed(NOW,ZoneOffset.UTC);} }
    private VerifiedSystemScope scope(String system,String actor) {
        return new VerifiedSystemScope(OwnerSystem.valueOf(system),"fixture",new ActorRef(actor),actor,Set.of(),"a".repeat(64),NOW,NOW.plusSeconds(60),"request");
    }
    @Test void permanentHistoryListsByOwnerWithStableTiesUnicodeTitleAndExplicitDeletion() {
        try(var database=rawDatabase()) {
            database.bootstrap();database.migrate();
            try(var context=application(database,TestClock.class)) {
                var conversations=context.getBean(ConversationRepository.class);var messages=context.getBean(MessageRepository.class);
                var jdbc=context.getBean(org.springframework.jdbc.core.simple.JdbcClient.class);var owner=scope("New-7","actor");
                assertThat(conversations.list(owner,null,null,21)).isEmpty();
                for(String id:List.of("a","b","c")) assertThat(conversations.insert(owner,id)).isTrue();
                conversations.insert(scope("Other","actor"),"foreign-system");conversations.insert(scope("New-7","other"),"foreign-user");
                String title="😀".repeat(101);
                messages.insert(owner,"b",new MessageRepository.Message("question","user",title,"[]","[]",null,null));
                jdbc.sql("UPDATE gw.conversation SET created_at=:old,updated_at=:old WHERE id='a'").param("old",java.sql.Timestamp.from(NOW.minus(Duration.ofDays(31)))).update();
                var first=conversations.list(owner,null,null,2);
                assertThat(first).extracting(ConversationRepository.ListItem::id).containsExactly("c","b");
                assertThat(first.getFirst().title()).isNull();assertThat(first.getLast().title()).isEqualTo("😀".repeat(100));
                assertThat(conversations.list(owner,first.getLast().updatedAt(),first.getLast().id(),2)).extracting(ConversationRepository.ListItem::id).containsExactly("a");
                assertThat(conversations.find(owner,"a")).isPresent();
                assertThat(context.getBean(RetentionMaintenanceRepository.class).purgeExpiredCacheBatch(100)).isZero();
                assertThat(conversations.find(owner,"a")).isPresent();
                assertThat(conversations.delete(scope("New-7","other"),"b")).isEqualTo(ConversationRepository.DeleteResult.NOT_FOUND);
                assertThat(conversations.delete(owner,"b")).isEqualTo(ConversationRepository.DeleteResult.DELETED);
                assertThat(messages.findByConversation(owner,"b")).isEmpty();
                assertThat(conversations.delete(owner,"b")).isEqualTo(ConversationRepository.DeleteResult.NOT_FOUND);
                assertThat(conversations.touch(owner,"b")).isFalse();
                assertThat(messages.insert(owner,"b",new MessageRepository.Message("late","user","late","[]","[]",null,null))).isFalse();
            }
        }
    }
}
