package com.nori.ai.gateway.persistence.repository;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

public final class EvidenceRepository extends ScopedJdbc {
    public EvidenceRepository(JdbcClient jdbc, TransactionTemplate transactions, Clock clock) { super(jdbc, transactions, clock); }

    public record Entry(String id, String sourceRef, String chunkId, String revision, String payload) {}
    public boolean insert(VerifiedSystemScope scope, String jobId, Entry value) {
        return transaction(() -> scoped(scope, "INSERT INTO gw.evidence(id, job_id, owner_system, source_ref, chunk_id, revision, payload, created_at) "
            + "SELECT :id, :job, :bound, :source, :chunk, :revision, CAST(:payload AS jsonb), :now WHERE EXISTS (" + JOB + ")")
            .param("job", jobId).param("id", value.id()).param("source", value.sourceRef()).param("chunk", value.chunkId()).param("revision", value.revision()).param("payload", value.payload()).update() == 1);
    }
    public Optional<Entry> find(VerifiedSystemScope scope, String jobId, String id) {
        return transaction(() -> scoped(scope, "SELECT e.id, source_ref, chunk_id, revision, e.payload::text FROM gw.evidence e "
            + "WHERE e.id = :id AND e.job_id = :job AND e.owner_system = :bound AND EXISTS (" + JOB + ")")
            .param("job", jobId).param("id", id).query((rs, row) -> new Entry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5))).optional());
    }

    /** 일반 열람은 살아 있는 소유 Job 및 현재 권한 검사를 거친 controller에서만 접근합니다. */
    public Optional<String> findAnswerBody(VerifiedSystemScope scope,String jobId,String evidenceId) {
        return transaction(()->scoped(scope,"""
            SELECT unit->>'bodyMasked' FROM kn.answer_snapshot s,
            LATERAL jsonb_array_elements(s.answer->'citations') citation,
            LATERAL jsonb_array_elements(s.evidence) unit
            WHERE s.system_id=:bound AND s.job_id=:job AND s.actor_ref=:actor
            AND citation->>'evidenceId'=:id AND citation->>'number'=unit->>'number'
            AND EXISTS ("""+JOB+")").param("job",jobId).param("id",evidenceId).query(String.class).optional());
    }

}
