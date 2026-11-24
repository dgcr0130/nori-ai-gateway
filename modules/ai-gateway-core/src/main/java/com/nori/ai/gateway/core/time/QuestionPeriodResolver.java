package com.nori.ai.gateway.core.time;

import com.nori.ai.gateway.core.logging.DiagnosticLog;
import com.nori.ai.gateway.core.plan.InputValidator;
import com.nori.ai.gateway.core.retention.TimeWindow;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 질문에 명시한 기간만 해석하며 기준 시각이나 기간 길이를 추측하지 않습니다. */
public final class QuestionPeriodResolver {
    public sealed interface Result permits Resolved, NoPeriod, Unresolvable {}
    public record Resolved(TimeWindow window, String expression) implements Result {
        public Resolved {
            Objects.requireNonNull(window);
            Objects.requireNonNull(expression);
        }
    }
    public record NoPeriod() implements Result {}
    public record Unresolvable(String expression) implements Result {
        public Unresolvable { Objects.requireNonNull(expression); }
    }

    // 분류의 시각 출처 검사와 질문 기간 해석이 같은 절대 시각 문법을 사용합니다.
    public static final Pattern ABSOLUTE = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}[T ][0-9]{2}:[0-9]{2}(?::[0-9]{2}(?:\\.[0-9]{1,9})?)?(?:Z|[+-][0-9]{2}:[0-9]{2})?");
    private static final Pattern PERIOD = Pattern.compile(
        "(?<range>(?<from>" + ABSOLUTE.pattern() + ")\\s*부터\\s*(?<to>" + ABSOLUTE.pattern() + ")\\s*까지)"
            + "|(?<unsupported>그저께|그제|내일|모레|요즘|(?:지난|이번|다음)\\s*(?:주|달|월|분기|해|년))"
            + "|(?<relative>(?:최근|지난)(?:\\s*(?<amount>[+-]?[0-9]+(?:\\.[0-9]+)?)\\s*(?<unit>분|시간|일|초|주|개월|달|년))?)"
            + "|(?<calendar>오늘|어제)"
            + "|(?<incomplete>" + ABSOLUTE.pattern() + "|[0-9]{4}-[0-9]{2}-[0-9]{2})");
    private static final Pattern INTEGER = Pattern.compile("[0-9]+");
    private static final System.Logger LOGGER = System.getLogger(QuestionPeriodResolver.class.getName());
    private final InputValidator parser = new InputValidator();

    public Result resolve(String question, Instant referenceTime, ZoneId zone) {
        Objects.requireNonNull(question, "질문은 필수입니다");
        Matcher match = PERIOD.matcher(question);
        if (!match.find()) return new NoPeriod();
        String expression = match.group();
        if (match.group("range") != null) return absolute(match, referenceTime, zone);
        if (match.group("unsupported") != null || match.group("incomplete") != null)
            return new Unresolvable(expression);
        if (referenceTime == null) return new Unresolvable(expression);
        if (match.group("relative") != null) return relative(match, referenceTime);
        return calendar(expression, referenceTime, zone);
    }

    private Result absolute(Matcher match, Instant referenceTime, ZoneId zone) {
        String expression = match.group();
        try {
            Instant start = parser.parseTime(match.group("from").replace(' ', 'T'), zone);
            Instant end = parser.parseTime(match.group("to").replace(' ', 'T'), zone);
            if (referenceTime != null && end.isAfter(referenceTime)) end = referenceTime;
            return resolved(start, end, expression);
        } catch (DateTimeException | IllegalArgumentException invalid) {
            return invalid(expression, invalid);
        }
    }

    private Result relative(Matcher match, Instant referenceTime) {
        String expression = match.group();
        String amount = match.group("amount");
        if (amount == null || !INTEGER.matcher(amount).matches()) return new Unresolvable(expression);
        try {
            long count = Long.parseLong(amount);
            if (count == 0) return new Unresolvable(expression);
            // '일'은 달력 날짜 이동이 아닌 기준 시각에서의 24시간 길이입니다.
            Duration duration = switch (match.group("unit")) {
                case "분" -> Duration.ofMinutes(count);
                case "시간" -> Duration.ofHours(count);
                case "일" -> Duration.ofDays(count);
                default -> throw new IllegalArgumentException("지원하지 않는 기간 단위입니다");
            };
            return resolved(referenceTime.minus(duration), referenceTime, expression);
        } catch (DateTimeException | IllegalArgumentException | ArithmeticException invalid) {
            return invalid(expression, invalid);
        }
    }

    private Result calendar(String expression, Instant referenceTime, ZoneId zone) {
        if (zone == null) return new Unresolvable(expression);
        try {
            var date = referenceTime.atZone(zone).toLocalDate();
            Instant midnight = date.atStartOfDay(zone).toInstant();
            return expression.equals("오늘")
                ? resolved(midnight, referenceTime, expression)
                : resolved(date.minusDays(1).atStartOfDay(zone).toInstant(), midnight, expression);
        } catch (DateTimeException | ArithmeticException invalid) {
            return invalid(expression, invalid);
        }
    }

    private Result resolved(Instant start, Instant end, String expression) {
        if (!start.isBefore(end)) return new Unresolvable(expression);
        return new Resolved(new TimeWindow(start, end), expression);
    }

    private Result invalid(String expression, RuntimeException failure) {
        DiagnosticLog.failure(LOGGER, "QUESTION_PERIOD_RESOLVER_FAILED", failure);
        return new Unresolvable(expression);
    }
}
