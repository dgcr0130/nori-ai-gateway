package com.nori.ai.gateway.app.job;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SystemRemovalRecoveryIT extends WorkerRestartProcessSupport {
    @Test void removedSystemKeepsHistoryRecoversRunningAndDeniesQueuedWithoutExecution() throws Exception {
        haltWithRunningAndQueued();
        var restart=properties(NOW.plusSeconds(1));
        restart.keySet().removeIf(key->key.startsWith("ai.gateway.systems"));
        harness.launch(state,restart,null); harness.awaitReady(state);
        assertThat(jdbc.sql("SELECT status FROM gw.job ORDER BY seq").query(String.class).list()).containsExactly("FAILED","FAILED");
        assertThat(jdbc.sql("SELECT decision FROM gw.audit_event WHERE event_type='JOB_TERMINAL' ORDER BY job_id").query(String.class).list())
            .containsExactlyInAnyOrder("WORKER_INTERRUPTED","START_GATE_DENIED");
        assertThat(jdbc.sql("SELECT status FROM gw.result ORDER BY job_id").query(String.class).list()).containsExactlyInAnyOrder("FAILED","DENIED");
        assertThat(jdbc.sql("SELECT count(*) FROM gw.conversation").query(Integer.class).single()).isEqualTo(2);
        assertThat(jdbc.sql("SELECT count(*) FROM gw.cache_entry").query(Integer.class).single()).isZero();
        assertThat(jdbc.sql("SELECT count(*) FROM gw.job_event WHERE type='job.failed' AND NOT external_suppressed").query(Integer.class).single()).isEqualTo(2);
        for(String id:jdbc.sql("SELECT id FROM gw.job").query(String.class).list()) JobAuditLineage.assertLineage(jdbc,id,true);
        assertThat(json.readTree(send("/__test/harness-observations","GET",null,null,null,false).body()).get("backendQueries").asInt()).isZero();
    }
}
