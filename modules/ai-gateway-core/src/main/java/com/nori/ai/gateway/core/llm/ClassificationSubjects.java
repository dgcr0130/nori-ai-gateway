package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.job.JobRequestInput;
import com.nori.ai.gateway.core.knowledge.SearchText;
import java.util.*;
import java.util.regex.Pattern;

/** 식별자 비교는 공통 검색 규칙을 재사용하고 모델에는 원래 대소문자를 보존합니다. */
final class ClassificationSubjects {
    private static final Pattern TOKEN = Pattern.compile("[A-Za-z0-9_.-]+");
    private ClassificationSubjects() {}

    static List<String> candidates(JobRequestInput input, List<ClassificationService.HistoryPair> history, Collection<String> toolIds) {
        var result = new LinkedHashSet<String>();
        collect(input.maskedQuestion(), result);
        for (var pair : history) { collect(pair.question(), result); collect(pair.answer(), result); }
        input.selectedResourceRefs().forEach(ref -> { if(ref.ref().codePointCount(0,ref.ref().length())<=64)result.add(ref.ref()); });
        var excluded=new HashSet<String>();toolIds.forEach(id->excluded.add(SearchText.normalize(id)));
        result.removeIf(candidate->excluded.contains(SearchText.normalize(candidate)));
        return List.copyOf(result);
    }

    private static void collect(String text, Set<String> result) {
        var identifiers = new HashSet<>(SearchText.identifiers(text, List.of()));
        var tokens = TOKEN.matcher(text);
        while (tokens.find()) {
            String literal = tokens.group();
            String token = literal.replaceAll("^\\.+|\\.+$", "");
            // 점으로 구분한 도구 ID와 숫자·날짜는 설비 식별자로 승격하지 않습니다.
            if (!token.isEmpty() && token.length() <= 64 && Character.isLetter(token.charAt(0)) && !token.contains(".")
                    && identifiers.contains(SearchText.normalize(literal))) result.add(token);
        }
    }
}
