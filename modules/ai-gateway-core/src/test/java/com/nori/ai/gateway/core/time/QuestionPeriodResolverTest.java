package com.nori.ai.gateway.core.time;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionPeriodResolverTest {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final Instant REFERENCE = OffsetDateTime.parse("2026-11-24T19:30:00+09:00").toInstant();
    private final QuestionPeriodResolver resolver = new QuestionPeriodResolver();

    @Test
    void operationalTodayQuestionEndsAtReferenceInsteadOfEndOfDay() {
        Instant reference = OffsetDateTime.parse("2026-11-24T19:27:24+09:00").toInstant();
        String question = "설비는 모르겠지만 오늘 에러가 발생했던 설비 혹은 시스템 오류가 있다고 하더라고. 무슨일인지 정리해서 보고해줘.";

        assertResolved(resolver.resolve(question, reference, SEOUL),
            Instant.parse("2026-11-23T15:00:00Z"), Instant.parse("2026-11-24T10:27:24Z"), "오늘");
    }

    @Test
    void yesterdayUsesPreviousLocalMidnightThroughCurrentLocalMidnight() {
        assertResolved(resolver.resolve("어제 에러를 알려줘", REFERENCE, SEOUL),
            Instant.parse("2026-11-22T15:00:00Z"), Instant.parse("2026-11-23T15:00:00Z"), "어제");
    }

    @Test
    void todayAndYesterdayUseSuppliedZoneForCalendarBoundaries() {
        ZoneId utc = ZoneId.of("UTC");

        assertResolved(resolver.resolve("오늘 에러", REFERENCE, utc),
            Instant.parse("2026-11-24T00:00:00Z"), REFERENCE, "오늘");
        assertResolved(resolver.resolve("어제 에러", REFERENCE, utc),
            Instant.parse("2026-11-23T00:00:00Z"), Instant.parse("2026-11-24T00:00:00Z"), "어제");
    }

    @Test
    void relativeDurationsSupportBothPrefixesAndSpacingForEveryUnit() {
        for (String prefix : List.of("최근", "지난")) {
            for (String space : List.of(" ", "")) {
                assertRelative(prefix + space + "30분", Duration.ofMinutes(30));
                assertRelative(prefix + space + "2시간", Duration.ofHours(2));
                assertRelative(prefix + space + "3일", Duration.ofDays(3));
            }
        }
    }

    @Test
    void goldenG07RangeIsPreservedDespiteRepeatedFromAndToLiterals() {
        String expression = "2026-11-23T01:37:29Z부터 2026-11-24T01:37:29Z까지";
        String question = "설비 CLN_001의 통신 로그를 " + expression
            + " 조회해 송수신 기록을 알려 주세요. from=2026-11-23T01:37:29Z, to=2026-11-24T01:37:29Z입니다.";

        assertResolved(resolver.resolve(question, REFERENCE, SEOUL),
            Instant.parse("2026-11-23T01:37:29Z"), Instant.parse("2026-11-24T01:37:29Z"), expression);
    }

    @Test
    void absoluteTimesWithoutOffsetsUseSuppliedZone() {
        String expression = "2026-11-24 09:00부터 2026-11-24 10:00까지";

        assertResolved(resolver.resolve(expression + " 통신 로그를 알려줘", REFERENCE, SEOUL),
            Instant.parse("2026-11-24T00:00:00Z"), Instant.parse("2026-11-24T01:00:00Z"), expression);
        assertResolved(resolver.resolve(expression + " 통신 로그를 알려줘", REFERENCE, ZoneId.of("UTC")),
            Instant.parse("2026-11-24T09:00:00Z"), Instant.parse("2026-11-24T10:00:00Z"), expression);
    }

    @Test
    void absoluteOffsetsAndNanosecondsArePreserved() {
        String expression = "2026-11-24T09:00:00.123456789+09:00부터 2026-11-24T10:00:00.987654321+09:00까지";

        assertResolved(resolver.resolve(expression + " 에러를 알려줘", REFERENCE, SEOUL),
            Instant.parse("2026-11-24T00:00:00.123456789Z"),
            Instant.parse("2026-11-24T01:00:00.987654321Z"), expression);
    }

    @Test
    void futureAbsoluteEndIsClippedToReferenceAndKeepsOriginalExpression() {
        String expression = "2026-11-24T09:00:00+09:00부터 2026-11-24T23:59:59+09:00까지";

        assertResolved(resolver.resolve(expression + " 에러", REFERENCE, SEOUL),
            Instant.parse("2026-11-24T00:00:00Z"), REFERENCE, expression);
    }

    @Test
    void absoluteEndEqualToReferenceRemainsValid() {
        String expression = "2026-11-24T09:00:00+09:00부터 2026-11-24T19:30:00+09:00까지";

        assertResolved(resolver.resolve(expression + " 에러", REFERENCE, SEOUL),
            Instant.parse("2026-11-24T00:00:00Z"), REFERENCE, expression);
    }

    @Test
    void clippedRangeWithStartEqualToOrAfterEndIsUnresolvable() {
        for (String start : List.of("2026-11-24T19:30:00+09:00", "2026-11-24T20:00:00+09:00")) {
            String expression = start + "부터 2026-11-24T23:59:59+09:00까지";

            assertUnresolvable(resolver.resolve(expression + " 에러", REFERENCE, SEOUL), expression);
        }
    }

    @Test
    void equalOrReversedAbsoluteRangeIsUnresolvable() {
        for (String end : List.of("2026-11-24T09:00:00+09:00", "2026-11-24T08:00:00+09:00")) {
            String expression = "2026-11-24T09:00:00+09:00부터 " + end + "까지";

            assertUnresolvable(resolver.resolve(expression + " 에러", REFERENCE, SEOUL), expression);
        }
    }

    @Test
    void stateAndRegistrationQuestionsHaveNoPeriod() {
        for (String question : List.of("CLN_001 설비의 현재 상태를 알려줘", "PHOTO_001 호기의 등록 정보를 알고 싶어.")) {
            assertThat(resolver.resolve(question, REFERENCE, SEOUL))
                .as(question).isInstanceOf(QuestionPeriodResolver.NoPeriod.class);
        }
    }

    @Test
    void unsupportedAndLengthlessPeriodsAreUnresolvableWithEvidence() {
        assertUnresolvable(resolver.resolve("지난주 에러 알려줘", REFERENCE, SEOUL), "지난주");
        assertUnresolvable(resolver.resolve("이번 달 DLQ", REFERENCE, SEOUL), "이번 달");
        assertUnresolvable(resolver.resolve("그저께 통신 로그", REFERENCE, SEOUL), "그저께");
        assertUnresolvable(resolver.resolve("최근 에러 알려줘", REFERENCE, SEOUL), "최근");
        assertUnresolvable(resolver.resolve("요즘 에러 알려줘", REFERENCE, SEOUL), "요즘");
    }

    @Test
    void durationMustBeAPositiveInteger() {
        for (String expression : List.of("최근 0분", "지난 -1시간", "최근 1.5일")) {
            assertUnresolvable(resolver.resolve(expression + " 에러", REFERENCE, SEOUL), expression);
        }
    }

    @Test
    void missingReferenceNeverFallsBackToSystemClockForRelativePeriods() {
        for (String expression : List.of("오늘", "어제", "최근 30분", "지난 3일")) {
            assertUnresolvable(resolver.resolve(expression + " 에러", null, SEOUL), expression);
        }
    }

    @Test
    void explicitAbsoluteRangeDoesNotRequireReference() {
        String expression = "2026-11-23T01:37:29Z부터 2026-11-24T01:37:29Z까지";

        assertResolved(resolver.resolve(expression + " 통신 로그", null, SEOUL),
            Instant.parse("2026-11-23T01:37:29Z"), Instant.parse("2026-11-24T01:37:29Z"), expression);
    }

    @Test
    void absentPeriodRemainsAbsentWithoutReference() {
        assertThat(resolver.resolve("CLN_001 설비의 현재 상태를 알려줘", null, SEOUL))
            .isInstanceOf(QuestionPeriodResolver.NoPeriod.class);
    }

    private void assertRelative(String expression, Duration duration) {
        assertResolved(resolver.resolve(expression + " 에러를 알려줘", REFERENCE, SEOUL),
            REFERENCE.minus(duration), REFERENCE, expression);
    }

    private void assertResolved(QuestionPeriodResolver.Result result, Instant start, Instant end, String expression) {
        assertThat(result).as(expression).isInstanceOf(QuestionPeriodResolver.Resolved.class);
        var resolved = (QuestionPeriodResolver.Resolved) result;
        assertThat(resolved.window().startAt()).isEqualTo(start);
        assertThat(resolved.window().endAt()).isEqualTo(end);
        assertThat(resolved.expression()).isEqualTo(expression);
    }

    private void assertUnresolvable(QuestionPeriodResolver.Result result, String expression) {
        assertThat(result).as(expression).isInstanceOf(QuestionPeriodResolver.Unresolvable.class);
        assertThat(((QuestionPeriodResolver.Unresolvable) result).expression()).isEqualTo(expression);
    }
}
