package com.nori.ai.gateway.app.ops;

import com.nori.ai.gateway.app.hooks.PausePointRegistry;
import com.nori.ai.gateway.app.job.recovery.ReadinessGate;
import com.nori.ai.gateway.app.observability.MetricsCatalog;
import com.nori.ai.gateway.persistence.maintenance.RetentionMaintenanceRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.context.event.EventListener;

/** 감사 90일과 캐시 자체 만료를 복구 완료 후 1시간 주기로 정리합니다. */
public final class RetentionPurgeJob implements AutoCloseable {
    static final Duration PERIOD = Duration.ofHours(1);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().name("retention-purge-", 0).factory());
    private final RetentionMaintenanceRepository retention;
    private final Clock clock;
    private final Optional<PausePointRegistry> pauses;
    private final Counter cache, audit, failures;
    private volatile boolean started;
    private Instant nextRunAt;

    public RetentionPurgeJob(RetentionMaintenanceRepository retention, Clock clock, Optional<PausePointRegistry> pauses, MeterRegistry meters) {
        this.retention = Objects.requireNonNull(retention); this.clock = Objects.requireNonNull(clock); this.pauses = Objects.requireNonNull(pauses);
        cache = purgedRows(meters, "cache"); audit = purgedRows(meters, "audit");
        failures = Counter.builder(MetricsCatalog.RETENTION_PURGE_FAILURES).register(meters);
    }
    @EventListener public void start(ReadinessGate.RecoveryCompleted event) {
        if (started) return;
        started = true;
        // 주기를 실시간 대신 Gateway Clock으로 판정하여 제어 시계 시험과 운영이 같은 경로를 쓰게 합니다.
        scheduler.scheduleWithFixedDelay(this::tick, 0, 1, TimeUnit.SECONDS);
    }
    private synchronized void tick() {
        Instant now = clock.instant();
        if (nextRunAt != null && now.isBefore(nextRunAt)) return;
        try {
            if (purgeNow(now)) nextRunAt = now.plus(PERIOD);
        } catch (RuntimeException failure) {
            // 실패 batch만 rollback되고 이전 커밋은 남습니다. 다음 주기에 다시 시도하며 경보는 실패 지표로 판정합니다.
            nextRunAt = now.plus(PERIOD);
            signalFailure();
        }
    }
    /** 보류 지점이 걸려 있으면 그 주기를 건너뜁니다. */
    private boolean purgeNow(Instant now) {
        if (pauses.map(registry -> registry.held(PausePointRegistry.Point.PURGE_HOLD)).orElse(false)) return false;
        for (int deleted; (deleted = retention.purgeExpiredCacheBatch(RetentionMaintenanceRepository.DEFAULT_PURGE_BATCH_SIZE)) > 0;) cache.increment(deleted);
        for (int deleted; (deleted = retention.purgeExpiredAuditBatch(RetentionMaintenanceRepository.DEFAULT_PURGE_BATCH_SIZE)) > 0;) audit.increment(deleted);
        return true;
    }
    private void signalFailure() {
        failures.increment();
        System.getLogger(RetentionPurgeJob.class.getName()).log(System.Logger.Level.WARNING, "RETENTION_PURGE_FAILED");
    }
    private static Counter purgedRows(MeterRegistry meters, String kind) { return Counter.builder(MetricsCatalog.RETENTION_PURGED_ROWS).tag("kind", kind).register(meters); }
    public boolean started() { return started; }
    @Override public void close() { started = false; scheduler.shutdownNow(); }
}
