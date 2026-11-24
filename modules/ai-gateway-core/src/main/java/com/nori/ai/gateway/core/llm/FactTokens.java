package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.SearchLanguage;
import com.nori.ai.gateway.core.knowledge.SearchText;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/** 작성·피드백 양쪽에서 숫자·식별자의 경계와 단위 규칙을 동일하게 유지합니다. */
final class FactTokens {
    private static final Pattern FACT=Pattern.compile("(?<![a-z0-9_.])(?:[+−-]?[0-9]+(?:,[0-9]{3})+(?:\\.[0-9]+)?(?:[a-z][a-z0-9]*)?|[+−-]?\\.[0-9]+(?:[a-z][a-z0-9]*)?|[+−-]?[a-z0-9_]+(?:[.-][a-z0-9_]+)*)(?:%)?(?![a-z0-9_]|[.-][a-z0-9_])");
    private FactTokens() {}
    static Set<String> extract(String text,SearchLanguage language) {
        var tokens=new HashSet<String>();var matcher=FACT.matcher(SearchText.normalize(text));
        while(matcher.find()) {
            String token=matcher.group();if(!token.codePoints().anyMatch(Character::isDigit)&&token.chars().noneMatch(c->c=='.'||c=='_'||c=='-'))continue;
            // 수치와 식별자의 최대 경계를 먼저 잡아 1,000·1.2.3·부호를 작은 숫자로 빌리지 않습니다.
            // 한글 단위 뒤 조사는 같은 Nori의 실제 품사로 구분하며 수치를 변환하지 않습니다.
            if(token.matches("[+−-]?(?:[0-9]+(?:,[0-9]{3})*(?:\\.[0-9]+)?|\\.[0-9]+)")) {
                String suffix=SearchText.normalize(text).substring(matcher.end());var letters=Pattern.compile("^[가-힣]+").matcher(suffix);
                if(letters.find()) {
                    String attached=letters.group();var unit=new StringBuilder();int offset=0;
                    for(var term:language.analyze(attached)) {
                        if(!term.tag().startsWith("N")||!attached.startsWith(term.text(),offset))break;
                        unit.append(term.text());offset+=term.text().length();
                    }
                    token+=unit;
                }
            }
            tokens.add(token);
        }
        return tokens;
    }
}
