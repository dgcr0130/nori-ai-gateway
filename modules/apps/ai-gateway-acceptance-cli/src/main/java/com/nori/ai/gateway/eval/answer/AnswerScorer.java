package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import static com.nori.ai.gateway.eval.answer.NegationAnalyzer.normalize;

/** 사실은 같은 문장의 인용과 결속하고 전체 요구 위치도 별도로 검사합니다. */
public final class AnswerScorer {
    public record Grade(boolean correct,String reason,List<String> missingFacts,List<EvalMarkdown.Location> missingLocations) {}
    private final NegationAnalyzer language=new NegationAnalyzer();
    public Grade failure(QuestionSet.Bound bound,String reason) {
        return new Grade(false,reason,bound.question().expectedFacts(),bound.question().evidenceLocations());
    }
    public Grade score(QuestionSet.Bound bound,JsonNode response) {
        var question=bound.question();
        String job=response.path("jobStatus").asText();var result=response.path("result");
        if(!Set.of("SUCCEEDED","PARTIAL").contains(job)||response.has("resultAccess")||!result.isObject()
                ||result.hasNonNull("errorCode")||!Set.of("SUCCESS","EMPTY","PARTIAL","STALE","CONFLICT").contains(result.path("status").asText()))return failure(bound,"응답 실패 또는 접근 거절");
        var answer=result.path("payload").path("answer");
        if(!answer.isObject()||!answer.path("citations").isArray()||!answer.path("sentences").isArray())return failure(bound,"답변 형식 불일치");
        if(!question.answerable()) {
            boolean correct=answer.path("kind").asText().equals("NO_EVIDENCE")&&answer.path("text").asText().equals("근거 없음")&&answer.path("citations").isEmpty();
            return new Grade(correct,correct?"정답":"거절 형식 불일치",List.of(),List.of());
        }
        if(!Set.of("ANSWER","PARTIAL").contains(answer.path("kind").asText())||!answer.path("text").isTextual()
                ||answer.path("sentences").isEmpty()||answer.path("sentences").size()>20)return failure(bound,"답변 형식 불일치");
        var citations=new LinkedHashMap<Integer,EvalMarkdown.Location>();var numbers=new HashSet<Integer>();
        for(var citation:answer.path("citations")) {
            var number=citation.path("number");
            if(!number.isIntegralNumber()||!number.canConvertToInt()||number.intValue()<1||!numbers.add(number.intValue()))return failure(bound,"인용 형식 불일치");
            if(citation.path("sourceType").asText().equals("DOCUMENT")) {
                try{citations.put(number.intValue(),QuestionSet.location(citation.path("location")));}
                catch(com.nori.ai.gateway.eval.EvalException invalid){return failure(bound,"인용 형식 불일치");}
            }
        }
        var found=new HashSet<String>();
        for(var sentence:answer.path("sentences")) {
            var text=sentence.path("text");var refs=sentence.path("citations");
            if(!text.isTextual()||text.textValue().isBlank()||QuestionSet.size(text.textValue())>1000
                    ||text.textValue().contains("\n")||text.textValue().contains("\r")||!refs.isArray()||refs.isEmpty()||refs.size()>6)return failure(bound,"문장 형식 불일치");
            var sentenceLocations=new ArrayList<EvalMarkdown.Location>();
            for(var ref:refs) {
                if(!ref.isIntegralNumber()||!ref.canConvertToInt()||!numbers.contains(ref.intValue()))return failure(bound,"문장 인용 불일치");
                if(citations.containsKey(ref.intValue()))sentenceLocations.add(citations.get(ref.intValue()));
            }
            for(String fact:question.expectedFacts())if(normalize(text.textValue()).contains(normalize(fact))&&!language.hasOutsideNegation(text.textValue(),fact)
                    &&bound.factLocations().get(fact).stream().anyMatch(required->sentenceLocations.stream().anyMatch(actual->covers(actual,required))))found.add(fact);
        }
        var missingFacts=question.expectedFacts().stream().filter(f->!found.contains(f)).toList();
        var missingLocations=question.evidenceLocations().stream().filter(required->citations.values().stream().noneMatch(actual->covers(actual,required))).toList();
        boolean correct=missingFacts.isEmpty()&&missingLocations.isEmpty();
        return new Grade(correct,correct?"정답":"사실 또는 인용 위치 불일치",missingFacts,missingLocations);
    }
    static boolean covers(EvalMarkdown.Location actual,EvalMarkdown.Location required) {
        return actual.bucket().equals(required.bucket())&&actual.objectKey().equals(required.objectKey())&&actual.revision().equals(required.revision())
            &&actual.section().equals(required.section())&&actual.lineFrom()<=required.lineFrom()&&actual.lineTo()>=required.lineTo();
    }
}
