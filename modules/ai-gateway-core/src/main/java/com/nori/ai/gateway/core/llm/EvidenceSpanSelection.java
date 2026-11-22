package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import java.util.regex.Pattern;

/** 질문이나 정답 없이 실제 선택 본문에서만 문장·표 행과 인용 번호의 조합을 만듭니다. */
final class EvidenceSpanSelection {
    private static final Pattern LIST = Pattern.compile("^(?:[-*+] |[0-9]+[.)] )");
    private static final Pattern BOUNDARY = Pattern.compile("(?<=[.!?])\\s+(?=\\S)");
    private EvidenceSpanSelection() {}

    static StructuredOutput create(List<EvidenceUnit> units) {
        var choices = SchemaContracts.JSON.createArrayNode();
        for (int i = 0; i < units.size(); i++) {
            var unit = units.get(i);
            for (String span : spans(unit.body(), unit.sourceType().equals("DOCUMENT"))) {
                var choice = choices.addObject().put("text", span);
                choice.putArray("citations").add(i + 1);
            }
        }
        // 빈 근거에서도 []만 가능한 유효 schema를 사용합니다. 제약을 버리는 fallback은 없습니다.
        Object items = choices.isEmpty() ? Map.of() : Map.of("enum", choices);
        var rows = Map.of("type", "array", "minItems", 0, "maxItems", choices.isEmpty() ? 0 : 20, "items", items);
        var root = Map.of("type", "object", "properties", Map.of("sentences", rows), "required", List.of("sentences"), "additionalProperties", false);
        return new StructuredOutput(SchemaContracts.JSON.writeValueAsString(root));
    }

    static List<String> spans(String body, boolean document) {
        return locatedSpans(body, document).stream().map(LocatedSpan::text).toList();
    }

    record LocatedSpan(String text, int bodyLine, int from, int to) {}

    /** 표시 위치만 함께 보존하며 기존 분할·공백 제거·중복 제거·길이 규칙은 그대로 사용합니다. */
    static List<LocatedSpan> locatedSpans(String body, boolean document) {
        var result = new LinkedHashMap<String, LocatedSpan>();
        var separators = Pattern.compile("\\R").matcher(body);
        int lineFrom = 0, bodyLine = 0;
        for (String line : body.split("\\R")) {
            bodyLine++;
            String value = line.strip();
            int valueFrom = lineFrom + line.indexOf(value), cursor = 0;
            if (document) {
                var prefix = LIST.matcher(value);
                if (prefix.find()) { valueFrom += prefix.end(); value = value.substring(prefix.end()); }
            }
            // 표의 열/값 결속과 코드·JSON의 문자열 경계를 문장 부호로 자르지 않습니다.
            boolean row = !document || value.contains("|") || value.contains("`") || value.contains("{") || value.contains("}")
                || value.startsWith("//") || value.startsWith("#") || value.startsWith("$");
            for (String text : row ? List.of(value) : Arrays.asList(BOUNDARY.split(value))) {
                int textFrom = value.indexOf(text, cursor);
                cursor = textFrom + text.length();
                String span = text.strip();
                int size = span.codePointCount(0, span.length());
                if (size > 0 && size <= 1000 && span.codePoints().noneMatch(c -> c == '\n' || c == '\r' || c == 0x85 || c == 0x2028 || c == 0x2029)) {
                    int from = valueFrom + textFrom + text.indexOf(span);
                    result.putIfAbsent(span, new LocatedSpan(span, bodyLine, from, from + span.length()));
                }
            }
            if (separators.find()) lineFrom = separators.end();
        }
        return List.copyOf(result.values());
    }
}
