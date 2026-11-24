package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.job.JobRequestInput;
import com.nori.ai.gateway.core.knowledge.SearchText;
import java.util.*;
import java.util.regex.Pattern;

/** 식별자 비교는 공통 검색 규칙을 재사용하고 모델에는 원래 대소문자를 보존합니다. */
final class ClassificationSubjects {
    private static final Pattern TOKEN = Pattern.compile("[A-Za-z0-9_.-]+");
    private static final Pattern CAMEL_CASE = Pattern.compile("[a-z][A-Z]");
    private ClassificationSubjects() {}

    static List<String> candidates(JobRequestInput input, List<ClassificationService.HistoryPair> history, Collection<String> toolIds) {
        return candidates(input,history,toolIds,false);
    }

    static List<String> candidates(JobRequestInput input,List<ClassificationService.HistoryPair> history,Collection<String> toolIds,boolean codeTerms) {
        var result = new LinkedHashSet<String>();
        collect(input.maskedQuestion(), result);
        for (var pair : history) { collect(pair.question(), result); collect(pair.answer(), result); }
        input.selectedResourceRefs().forEach(ref -> { if(ref.ref().codePointCount(0,ref.ref().length())<=64)result.add(ref.ref()); });
        // 코드 전용 후보는 현재 질문에서만 더하며 이력·선택 설비의 기존 순서를 유지합니다.
        if(codeTerms)collectCodeTerms(input.maskedQuestion(),result);
        var excluded=new HashSet<String>();toolIds.forEach(id->excluded.add(SearchText.normalize(id)));
        result.removeIf(candidate->excluded.contains(SearchText.normalize(candidate)));
        return List.copyOf(result);
    }

    private static void collectCodeTerms(String text,Set<String> result) {
        var tokens=TOKEN.matcher(text);
        while(tokens.find()) {
            String token=tokens.group().replaceAll("^[.-]+|[.-]+$","");
            if(token.length()<3||token.length()>64||!token.substring(0,1).matches("[A-Za-z]"))continue;
            if(token.chars().anyMatch(c->c>='0'&&c<='9')||token.contains("_")||token.contains(".")||CAMEL_CASE.matcher(token).find())result.add(token);
        }
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
