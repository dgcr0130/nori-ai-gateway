package com.nori.ai.gateway.eval.answer;

import java.util.List;
import java.util.regex.Pattern;

/** 공용 mask-v1의 고정 규칙입니다. Gateway 모듈을 의존하지 않고 공용 oracle로 결속합니다. */
final class EvalMasking {
    private static final List<Pattern> RULES=List.of(
        Pattern.compile("(?i)(?:bearer\\s+)[A-Za-z0-9._~+/-]+=*"),
        Pattern.compile("(?i)(?:api[_-]?key|password|passwd|secret|token)\\s*[:=]\\s*[\\\"']?[^\\s\\\"',;}]+"),
        Pattern.compile("(?i)[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9-]+(?:\\.[A-Z0-9-]+)+"),
        Pattern.compile("(?<!\\d)(?:\\+82[- .]?)?0?1[016789][- .]?\\d{3,4}[- .]?\\d{4}(?!\\d)"),
        Pattern.compile("(?:표시\\s*이름|이름|성명|display[_ -]?name)\\s*[:=]\\s*[^\\s,;]+",Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(?:AIGW[_-])?SENTINEL[_:-][A-Za-z0-9_@.:-]+"));
    static String mask(String value){for(var rule:RULES)value=rule.matcher(value).replaceAll("[마스킹]");return value;}
}
