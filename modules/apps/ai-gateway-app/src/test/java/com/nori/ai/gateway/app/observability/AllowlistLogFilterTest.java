package com.nori.ai.gateway.app.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** OTLP는 인자 없는 제품 고정 코드만 통과하며 상세 stdout과 별도의 계약을 유지합니다. */
class AllowlistLogFilterTest {
    private static final String READINESS_LOGGER = "com.nori.ai.gateway.integration.inference.InferenceReadinessService";
    private static final List<String> INFERENCE_ALERT_CODES = List.of("INFERENCE_ALERT_RAISED_MODEL_LOADING", "INFERENCE_ALERT_RAISED_INFERENCE_UNAVAILABLE",
        "INFERENCE_ALERT_CLEARED");
    private final LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
    private final AllowlistLogFilter filter = new AllowlistLogFilter();


    private LoggingEvent event(String logger, String message, Object... arguments) {
        return new LoggingEvent(AllowlistLogFilterTest.class.getName(), context.getLogger(logger), Level.WARN, message, null, arguments);
    }

    @Test void inferenceAlertCodesPassAndAreWrittenVerbatim() {
        for (String code : INFERENCE_ALERT_CODES) {
            var event = event(READINESS_LOGGER, code);
            assertThat(filter.decide(event)).as(code).isEqualTo(FilterReply.NEUTRAL);
        }
    }
    @Test void documentSynchronizationFailureExportsOnlyItsFixedCode() {
        var logger="com.nori.ai.gateway.app.job.recovery.ReadinessGate";
        var failure=event(logger,"DOCUMENT_SYNC_FAILED");
        assertThat(filter.decide(failure)).isEqualTo(FilterReply.NEUTRAL);
        assertThat(filter.decide(event(logger,"DOCUMENT_SYNC_FAILED","private-error"))).isEqualTo(FilterReply.DENY);
        assertThat(filter.decide(event("org.thirdparty.Client","DOCUMENT_SYNC_FAILED"))).isEqualTo(FilterReply.DENY);
    }

    @Test void unlistedOrArgumentCarryingMessagesStayMasked() {
        for (var event : List.of(event(READINESS_LOGGER, "INFERENCE_READINESS_MODEL_LOADING"), event(READINESS_LOGGER, "INFERENCE_ALERT_RAISED_READY"),
                event(READINESS_LOGGER, "INFERENCE_ALERT_RAISED_MODEL_DIGEST_MISMATCH {}", "sha256:" + "a".repeat(64)),
                event(READINESS_LOGGER, "INFERENCE_ALERT_CLEARED", "http://inference.internal:11434"), event("org.thirdparty.Inference", "INFERENCE_ALERT_CLEARED"))) {
            assertThat(filter.decide(event)).as(event.getMessage()).isEqualTo(FilterReply.DENY);
        }
    }
}
