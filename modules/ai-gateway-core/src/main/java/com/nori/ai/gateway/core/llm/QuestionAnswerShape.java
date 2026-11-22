package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.result.GroundedAnswer;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/** 명시적으로 요구한 영문 리터럴이 전혀 없는 답변만 거절합니다. 주제·의미의 정답 판정은 하지 않습니다. */
public final class QuestionAnswerShape {
    private QuestionAnswerShape() {}
    public static final String VERSION="english-literal-shape-v1";
    private static final Pattern KOREAN=Pattern.compile("(?:영어|영문)(?:로|으로)?\\s*(?:출력(?:된|되는)?\\s*)?(?:문구|문자열|리터럴)(?:\\s*(?:은|는|이|가)?\\s*(?:무엇|뭐|어떤)|\\s*(?:을|를)?\\s*(?:정확히|그대로)?\\s*(?:알려|보여|제시|적어|써)|\\s*(?:은|는)?\\s*\\?$)");
    private static final Pattern ENGLISH=Pattern.compile("\\b(?:what (?:is|are)|give|show|provide|return|tell me)\\b[^\\n?]*\\b(?:exact|literal|verbatim)\\s+English\\s+(?:output\\s+)?(?:phrase|string|literal|message)\\b",Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPLANATION=Pattern.compile("뜻|의미|번역|해석|설명|\\b(?:mean|meaning|translate|translation|explain|explanation)\\b",Pattern.CASE_INSENSITIVE);
    private static final Pattern NEGATION=Pattern.compile("필요\\s*없|원하지|요구하지|쓰지|넣지|주지|하지\\s*말|말고|아니라|아닌|제외|없이|않아도|않은|안\\s*(?:써|쓰|넣|알려|줘)|\\b(?:not|no|without|don't)\\b",Pattern.CASE_INSENSITIVE);
    private static final Pattern LATIN=Pattern.compile("[\\p{IsLatin}&&\\p{L}]");

    /** 설명·번역·부정이 섞여 요구가 모호하면 새 형식 제약을 만들지 않습니다. */
    public static boolean requiresEnglishLiteral(String question) {
        Objects.requireNonNull(question);
        return (KOREAN.matcher(question).find()||ENGLISH.matcher(question).find())
            &&!EXPLANATION.matcher(question).find()&&!NEGATION.matcher(question).find();
    }
    public static GroundedAnswer apply(String question,GroundedAnswer validated,Consumer<LlmDiagnostic> diagnostic) {
        Objects.requireNonNull(validated);Objects.requireNonNull(diagnostic);
        if(!requiresEnglishLiteral(question)||validated.kind().equals("NO_EVIDENCE")
            ||validated.sentences().stream().anyMatch(sentence->LATIN.matcher(sentence.text()).find()))return validated;
        // 위치/파일명/상태 문구의 영문을 답변 리터럴로 빌리지 않습니다. 이미 검증한 문장만 확인합니다.
        diagnostic.accept(LlmDiagnostic.COMPOSE_LITERAL_SHAPE);return GroundedAnswer.noEvidence();
    }
    public static String contract(){return VERSION+"\n"+KOREAN.pattern()+"\n"+ENGLISH.pattern()+"\n"+EXPLANATION.pattern()+"\n"+NEGATION.pattern()+"\n"+LATIN.pattern();}
}
