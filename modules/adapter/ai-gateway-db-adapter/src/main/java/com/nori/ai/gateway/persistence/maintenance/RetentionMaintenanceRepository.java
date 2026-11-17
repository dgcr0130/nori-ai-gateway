package com.nori.ai.gateway.persistence.maintenance;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

public final class RetentionMaintenanceRepository {
    /** 한 트랜잭션의 후보 행 수를 제한하는 기술 초기값이다. 관측·성능을 보장한 수치가 아니다. */
    public static final int DEFAULT_PURGE_BATCH_SIZE = 100;
    private final JdbcClient jdbc; private final TransactionTemplate transactions; private final Clock clock;
    public RetentionMaintenanceRepository(JdbcClient jdbc, TransactionTemplate transactions, Clock clock) {
        this.jdbc = jdbc; this.transactions = transactions; this.clock = clock;
    }
    public int purgeExpiredAudit() {
        int total = 0;
        for (int deleted; (deleted = purgeExpiredAuditBatch(DEFAULT_PURGE_BATCH_SIZE)) > 0;) total += deleted;
        return total;
    }
    public int purgeExpiredCacheBatch(int batchSize) {
        requirePositive(batchSize);
        return transactions.execute(status -> jdbc.sql("""
            WITH candidate AS (SELECT key_digest,owner_system,actor_ref,pdg FROM gw.cache_entry WHERE expires_at<=:now
                ORDER BY expires_at,key_digest LIMIT :batch FOR UPDATE SKIP LOCKED)
            DELETE FROM gw.cache_entry c USING candidate d WHERE c.key_digest=d.key_digest AND c.owner_system=d.owner_system
                AND c.actor_ref=d.actor_ref AND c.pdg=d.pdg AND c.expires_at<=:now
            """).param("now",Timestamp.from(clock.instant().truncatedTo(ChronoUnit.MICROS))).param("batch",batchSize).update());
    }
    public int purgeExpiredAuditBatch(int batchSize) {
        requirePositive(batchSize);
        return transactions.execute(status -> jdbc.sql("WITH candidate AS (SELECT a.id FROM gw.audit_event a WHERE a.recorded_at <= :cutoff"
                + " ORDER BY a.recorded_at, a.id LIMIT :batch FOR UPDATE SKIP LOCKED) "
                + "DELETE FROM gw.audit_event a USING candidate d WHERE a.id = d.id AND a.recorded_at <= :cutoff")
            .param("cutoff", Timestamp.from(clock.instant().minus(java.time.Duration.ofDays(90)).truncatedTo(ChronoUnit.MICROS))).param("batch", batchSize).update());
    }
    private static void requirePositive(int batchSize) { if (batchSize <= 0) throw new IllegalArgumentException("정리 한도는 양수여야 합니다"); }
}
