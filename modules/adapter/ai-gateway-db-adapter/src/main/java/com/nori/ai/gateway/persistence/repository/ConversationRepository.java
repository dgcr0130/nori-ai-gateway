package com.nori.ai.gateway.persistence.repository;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

public final class ConversationRepository extends ScopedJdbc {
    public ConversationRepository(JdbcClient jdbc, TransactionTemplate transactions, Clock clock) { super(jdbc, transactions, clock); }

    public record Conversation(String id, String ownerSystem, String ownerActorRef, Instant createdAt, Instant updatedAt, String status) {}
    public boolean insert(VerifiedSystemScope scope, String id) {
        Instant now = clock.instant();
        return transaction(() -> scoped(scope, """
            INSERT INTO gw.conversation(id, owner_system, owner_actor_ref, created_at, updated_at, status)
            VALUES (:id, :bound, :actor, :now, :now, 'ACTIVE')
            """).param("id", id).param("now", Timestamp.from(now)).update() == 1);
    }
    public Optional<Conversation> find(VerifiedSystemScope scope, String id) {
        return transaction(() -> scoped(scope, """
            SELECT id, owner_system, owner_actor_ref, created_at, updated_at, status FROM gw.conversation
            WHERE id = :id AND owner_system = :bound AND owner_actor_ref = :actor
            """).param("id", id).query((rs, row) -> new Conversation(rs.getString("id"), rs.getString("owner_system"),
                rs.getString("owner_actor_ref"), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant(),
                rs.getString("status"))).optional());
    }
    public boolean lock(VerifiedSystemScope scope, String id) {
        if (!org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive())
            throw new IllegalStateException("대화 잠금 트랜잭션이 필요합니다");
        return scoped(scope, "SELECT id FROM gw.conversation WHERE id=:id AND owner_system=:bound AND owner_actor_ref=:actor FOR UPDATE")
            .param("id", id).query(String.class).optional().isPresent();
    }
    public enum DeleteResult { DELETED, NOT_FOUND, ACTIVE }
    public DeleteResult delete(VerifiedSystemScope scope, String id) {
        return transaction(() -> {
            if (!lock(scope,id)) return DeleteResult.NOT_FOUND;
            boolean active = scoped(scope, "SELECT EXISTS(SELECT 1 FROM gw.job WHERE conversation_id=:id AND owner_system=:bound AND status IN ('QUEUED','RUNNING'))")
                .param("id",id).query(Boolean.class).single();
            if (active) return DeleteResult.ACTIVE;
            if (scoped(scope,"DELETE FROM gw.conversation WHERE id=:id AND owner_system=:bound AND owner_actor_ref=:actor").param("id",id).update() != 1)
                throw new IllegalStateException("대화를 삭제할 수 없습니다");
            return DeleteResult.DELETED;
        });
    }
    public record ListItem(String id, String title, Instant createdAt, Instant updatedAt, String status) {}
    public List<ListItem> list(VerifiedSystemScope scope, Instant afterTime, String afterId, int limit) {
        if (limit < 1 || limit > 101 || (afterTime == null) != (afterId == null)) throw new IllegalArgumentException("대화 목록 입력이 잘못되었습니다");
        return transaction(() -> scoped(scope, """
            SELECT c.id,c.created_at,c.updated_at,c.status,
                (SELECT left(m.content_masked,100) FROM gw.message m WHERE m.conversation_id=c.id AND m.owner_system=c.owner_system
                    AND m.role='user' ORDER BY m.created_at,m.id LIMIT 1) AS title
            FROM gw.conversation c WHERE c.owner_system=:bound AND c.owner_actor_ref=:actor
                AND (:first OR (c.updated_at,c.id)<(:afterTime,:afterId))
            ORDER BY c.updated_at DESC,c.id DESC LIMIT :limit
            """).param("first",afterTime==null).param("afterTime",Timestamp.from(afterTime==null?Instant.EPOCH:afterTime))
            .param("afterId",afterId==null?"":afterId).param("limit",limit)
            .query((rs,n)->new ListItem(rs.getString("id"),rs.getString("title"),rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant(),rs.getString("status"))).list());
    }
    public boolean touch(VerifiedSystemScope scope, String id) {
        return transaction(() -> scoped(scope, """
            UPDATE gw.conversation SET updated_at = :now WHERE id = :id AND owner_system = :bound
            AND owner_actor_ref = :actor
            """).param("id", id).update() == 1);
    }
    public boolean touchAtMessage(VerifiedSystemScope scope, String id, String messageId) {
        return transaction(() -> scoped(scope,"""
            UPDATE gw.conversation c SET updated_at=m.created_at FROM gw.message m
            WHERE c.id=:id AND c.owner_system=:bound AND c.owner_actor_ref=:actor
                AND m.id=:message AND m.conversation_id=c.id AND m.owner_system=c.owner_system AND m.role='user'
            """).param("id",id).param("message",messageId).update()==1);
    }
    public boolean saveContext(VerifiedSystemScope scope, String conversationId, String payload) {
        return save(scope, "context", conversationId, payload);
    }
    public Optional<String> findContext(VerifiedSystemScope scope, String conversationId) {
        return findPayload(scope, "context", conversationId);
    }
    public boolean saveSummary(VerifiedSystemScope scope, String conversationId, String payload) {
        return save(scope, "conversation_summary", conversationId, payload);
    }
    public Optional<String> findSummary(VerifiedSystemScope scope, String conversationId) {
        return findPayload(scope, "conversation_summary", conversationId);
    }
    private boolean save(VerifiedSystemScope scope, String table, String conversationId, String payload) {
        return transaction(() -> scoped(scope, "INSERT INTO gw." + table + " (conversation_id, owner_system, payload, updated_at) "
            + "SELECT :conversation, :bound, CAST(:payload AS jsonb), :now WHERE EXISTS (" + CONVERSATION + ") "
            + "ON CONFLICT (conversation_id) DO UPDATE SET payload = EXCLUDED.payload, updated_at = EXCLUDED.updated_at "
            + "WHERE gw." + table + ".owner_system = :bound")
            .param("conversation", conversationId).param("payload", payload).update() == 1);
    }
    private Optional<String> findPayload(VerifiedSystemScope scope, String table, String conversationId) {
        return transaction(() -> scoped(scope, "SELECT p.payload::text FROM gw." + table + " p WHERE p.conversation_id = :conversation "
            + "AND p.owner_system = :bound AND EXISTS (" + CONVERSATION + ")")
            .param("conversation", conversationId).query(String.class).optional());
    }

}
