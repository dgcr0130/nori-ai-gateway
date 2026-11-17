package com.nori.ai.gateway.app.ops;

import com.nori.ai.gateway.app.hooks.PausePointRegistry.Point;
import com.nori.ai.gateway.app.job.P09QueueSupport;
import com.nori.ai.gateway.app.job.SlotLedger;
import com.nori.ai.gateway.persistence.maintenance.RetentionMaintenanceRepository;
import com.nori.ai.gateway.persistence.repository.AuditRepository;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** 보존 정리·만료 경계 Job의 복구 완료 결속, 보류 지점, Gateway Clock 주기를 실제 앱 경로로 확인한다. */
@Tag("stage-F1") @Tag("label-MOCK_API")
class RetentionPurgeIT extends P09QueueSupport {
    private static final Duration CONVERSATION_TTL = Duration.ofDays(30);







    @Test @Tag("AC-24-AUDIT-EXPIRY")
    void auditIsKeptJustBeforeNinetyDaysHiddenAtTheBoundaryAndPurgedWithinTheNextDay() throws Exception {
        await(context.getBean(RetentionPurgeJob.class)::started);
        Instant recorded = clock().instant();
        String target = UUID.randomUUID().toString();
        insertAudit(target, recorded);
        insertAudit("older", recorded.minusNanos(1000));
        clock().freezeAt(recorded.plus(Duration.ofDays(90)).minusNanos(1000), true);
        await(() -> auditCount("older") == 0);
        assertThat(auditCount(target)).as("90일 직전").isEqualTo(1);
        clock().freezeAt(recorded.plus(Duration.ofDays(90)), true);
        assertThat(context.getBean(AuditRepository.class).findByRequest(scope("actor"), target)).as("정각부터 조회 차단").isEmpty();
        clock().freezeAt(recorded.plus(Duration.ofDays(90)).plus(Duration.ofHours(24)), true);
        await(() -> auditCount(target) == 0);
    }

    private void insertAudit(String request, Instant recordedAt) {
        jdbc().sql("INSERT INTO gw.audit_event(recorded_at, owner_system, actor_ref, request_id, event_type, status) VALUES (:at, 'TC', 'actor', :request, 'JOB_TERMINAL', 'SUCCEEDED')")
            .param("at", Timestamp.from(recordedAt)).param("request", request).update();
    }
    private int auditCount(String request) {
        return jdbc().sql("SELECT count(*) FROM gw.audit_event WHERE request_id=:request").param("request", request).query(Integer.class).single();
    }
}
