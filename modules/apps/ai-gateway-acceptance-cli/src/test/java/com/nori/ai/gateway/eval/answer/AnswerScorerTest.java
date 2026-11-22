package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.eval.answer.AnswerFixtures.*;

class AnswerScorerTest {
    final AnswerScorer scorer=new AnswerScorer();
    boolean grade(String fact,String text,EvalMarkdown.Location... locations){return scorer.score(bound(question("q",true,fact)),response(text,locations)).correct();}
    @Test void acElevenFiveFixedSentences() {
        assertThat(grade("100","코드는 100입니다",a())).isTrue();
        assertThat(grade("100","코드는 100이 아닙니다",a())).isFalse();
        assertThat(grade("100","코드는 100입니다",b())).isFalse();
        assertThat(grade("재시도 없음","재시도 없음으로 설정됩니다",a())).isTrue();
        assertThat(grade("100","코드는 100이 안 됩니다",a())).isFalse();
    }
    @Test void excludesEveryOccurrenceAndOnlyOverlappingTokensFromNegation() {
        assertThat(grade("재시도 없음","재시도 없음, 재시도 없음으로 설정됩니다",a())).isTrue();
        assertThat(grade("재시도 없음","재시도 없음이 아닙니다",a())).isFalse();
        assertThat(grade("100","재시도 없음이며 코드는 100입니다",a())).isFalse();
        for(String negative:List.of("아닙니다","안 됩니다","못 합니다","하지 못합니다","하지 않습니다","없습니다","하지 마세요"))
            assertThat(grade("100","코드는 100이고 "+negative,a())).as(negative).isFalse();
    }
    @Test void differentBucketRevisionSectionAndInsufficientLineCoverageFail() {
        for(var location:List.of(new EvalMarkdown.Location("bucket-b",a().objectKey(),a().revision(),a().section(),3,3),
                new EvalMarkdown.Location(a().bucket(),a().objectKey(),"f".repeat(64),a().section(),3,3),
                new EvalMarkdown.Location(a().bucket(),a().objectKey(),a().revision(),"절 B",3,3),
                new EvalMarkdown.Location(a().bucket(),a().objectKey(),a().revision(),a().section(),4,4)))
            assertThat(grade("100","코드는 100입니다",location)).isFalse();
        assertThat(grade("100","코드는 100입니다",new EvalMarkdown.Location(a().bucket(),a().objectKey(),a().revision(),a().section(),2,13))).isTrue();
        assertThat(grade("100","코드는 101입니다",a())).isFalse();
    }
    @Test void everyFactMustUseItsOwnLocationInThatSameSentenceAndAllLocationsMustBeCited() {
        var q=question("q",true,"100");q.set("expectedFacts",Json.MAPPER.valueToTree(List.of("100","T3는 1.5s")));
        var second=document().parents().getFirst().children().get(1).location();q.set("evidenceLocations",Json.MAPPER.valueToTree(List.of(a(),second)));
        var bound=bound(q);var answer=response("코드는 100입니다",a(),second);
        var sentences=(com.fasterxml.jackson.databind.node.ArrayNode)answer.path("result").path("payload").path("answer").path("sentences");
        ((ObjectNode)sentences.get(0)).putArray("citations").add(2);sentences.addObject().put("text","T3는 1.5s입니다").putArray("citations").add(1);
        assertThat(scorer.score(bound,answer).missingFacts()).containsExactly("100","T3는 1.5s");
        assertThat(scorer.score(bound,answer).missingLocations()).isEmpty();
        ((ObjectNode)sentences.get(0)).putArray("citations").add(1);((ObjectNode)sentences.get(1)).putArray("citations").add(2);
        assertThat(scorer.score(bound,answer).correct()).isTrue();
        // 같은 사실이 두 절에 있지만 요구 위치를 하나라도 빼면 실패합니다.
        q=question("r",true,"100");q.set("evidenceLocations",Json.MAPPER.valueToTree(List.of(a(),b())));
        assertThat(scorer.score(bound(q),response("코드는 100입니다",a())).missingLocations()).containsExactly(b());
    }
    @Test void refusalRequiresAllThreeExactConditionsAndSuccessfulResponse() {
        var q=bound(question("no",false,""));assertThat(scorer.score(q,refusal()).correct()).isTrue();
        for(String field:List.of("kind","text","citations")) {
            var r=refusal();var answer=(ObjectNode)r.path("result").path("payload").path("answer");
            if(field.equals("citations"))answer.putArray(field).addObject().put("number",1);else answer.put(field,"다른 값");
            assertThat(scorer.score(q,r).correct()).isFalse();
        }
        var r=refusal();r.put("jobStatus","FAILED");assertThat(scorer.score(q,r).correct()).isFalse();
        r=refusal();r.withObject("result").put("errorCode","LLM_UNAVAILABLE");assertThat(scorer.score(q,r).correct()).isFalse();
        r=refusal();r.put("resultAccess","DENIED");assertThat(scorer.score(q,r).correct()).isFalse();
    }
    @Test void normalizesNfcAndWhitespaceButNeverCaseNumbersOrUnits() {
        var q=question("q",true,"T3는 1.5s");var loc=document().parents().getFirst().children().get(1).location();q.set("evidenceLocations",Json.MAPPER.valueToTree(List.of(loc)));
        var bound=bound(q);assertThat(scorer.score(bound,response("T3는  1.5s입니다",loc)).correct()).isTrue();
        for(String text:List.of("t3는 1.5s입니다","T3는 1.5S입니다","T3는 1500ms입니다"))assertThat(scorer.score(bound,response(text,loc)).correct()).isFalse();
        String nfd=java.text.Normalizer.normalize("재시도 없음으로 설정됩니다",java.text.Normalizer.Form.NFD);
        assertThat(grade("재시도 없음",nfd,a())).isTrue();
    }
}
