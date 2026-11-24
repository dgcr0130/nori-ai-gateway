package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.plan.InputValidator;
import com.nori.ai.gateway.core.time.QuestionPeriodResolver;
import tools.jackson.databind.JsonNode;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

/** 기준 시각 없는 상대 기간을 달력의 임의 날짜로 바꾸지 않습니다. 시스템 시계는 사용하지 않습니다. */
final class ClassificationTimeSource {
    private static final Pattern ABSOLUTE = QuestionPeriodResolver.ABSOLUTE;
    private ClassificationTimeSource() {}

    static boolean present(JsonNode context,String literal) {
        return context.hasNonNull("referenceTime") || context.path("timeWindow").hasNonNull("from")
            || context.path("timeWindow").hasNonNull("to") || ABSOLUTE.matcher(literal).find();
    }

    static boolean supported(String value, JsonNode context, String literal, ZoneId zone) {
        var parser = new InputValidator();
        Instant time = parser.parseTime(value, zone);
        if (context.hasNonNull("referenceTime")) {
            return !time.isAfter(parser.parseTime(context.get("referenceTime").asString(), zone));
        }
        var supplied = context.path("timeWindow");
        for (String bound : List.of("from", "to")) {
            if (supplied.hasNonNull(bound) && time.equals(parser.parseTime(supplied.get(bound).asString(), zone))) return true;
        }
        var matcher = ABSOLUTE.matcher(literal);
        while (matcher.find()) {
            try { if (time.equals(parser.parseTime(matcher.group().replace(' ', 'T'), zone))) return true; }
            catch (DateTimeException | IllegalArgumentException invalid) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(ClassificationTimeSource.class.getName()),"CLASSIFICATION_TIME_SOURCE_FAILED",invalid); /* 잘못된 입력 날짜를 출처로 인정하지 않습니다. */ }
        }
        return false;
    }
}
