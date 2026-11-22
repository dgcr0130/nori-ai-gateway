package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.result.GroundedAnswer;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class QuestionAnswerShapeTest {
    private GroundedAnswer answer(String kind,String... sentences) {
        return new GroundedAnswer(kind,String.join("\n",sentences)+"\n최신 자료가 아닐 수 있습니다.",
            List.of(new GroundedAnswer.Citation(1,"e1","DOCUMENT",SchemaContracts.JSON.valueToTree(Map.of("objectKey","english-file.md")))),
            Arrays.stream(sentences).map(text->new GroundedAnswer.Sentence(text,List.of(1))).toList());
    }
    @Test void explicitEnglishLiteralWithoutLatinInValidatedSentencesBecomesEmptyNoEvidence() {
        var reasons=new ArrayList<LlmDiagnostic>();var original=answer("ANSWER","저장 성공 안내가 나타난다.");
        for(String q:List.of("성공 시 나오는 정확한 영어 출력 문구는 무엇인가요?","영문 문자열을 알려 주세요.","What is the exact English output string?")) {
            var checked=QuestionAnswerShape.apply(q,original,reasons::add);
            assertThat(checked).isEqualTo(GroundedAnswer.noEvidence());assertThat(checked.sentences()).isEmpty();assertThat(checked.citations()).isEmpty();
        }
        assertThat(reasons).containsOnly(LlmDiagnostic.COMPOSE_LITERAL_SHAPE);
    }
    @Test void quotedEnglishAndSupportingKoreanRowsAndExistingPartialStatusStayByteEqual() {
        var mixed=answer("PARTIAL","검증이 끝나면 다음 문구가 표시된다.","출력 문구는 \"READY\"이다.");
        assertThat(QuestionAnswerShape.apply("영어 출력 문구를 알려 주세요.",mixed,reason->{throw new AssertionError();})).isSameAs(mixed);
        assertThat(QuestionAnswerShape.apply("영문 문자열을 알려 주세요.",answer("ANSWER","A"),reason->{throw new AssertionError();}).kind()).isEqualTo("ANSWER");
    }
    @Test void fullWidthLatinLiteralIsRetainedWithoutRewritingAndMetadataCannotSupplyLatin() {
        String question="영어 출력 문구를 알려 주세요.";
        var fullWidth=answer("ANSWER","출력 문구는 ＲＥＡＤＹ이다.");
        var checked=QuestionAnswerShape.apply(question,fullWidth,reason->{throw new AssertionError();});
        assertThat(checked).isSameAs(fullWidth);
        assertThat(checked.text().getBytes(java.nio.charset.StandardCharsets.UTF_8)).containsExactly(fullWidth.text().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var koreanOnly=answer("ANSWER","저장 성공 안내가 나타난다.");
        assertThat(QuestionAnswerShape.apply(question,koreanOnly,reason->{})).isEqualTo(GroundedAnswer.noEvidence());
    }
    @Test void ordinaryQuestionsMeaningTranslationExplanationAndNegatedRequirementsDoNotGainAConstraint() {
        var original=answer("ANSWER","현재 상태는 정상이다.");
        for(String q:List.of("What is MODEL-7?","Please show the status.","저장 상태는 무엇인가요?","영어 출력 문구의 뜻은 무엇인가요?","영문 문자열을 한국어로 번역해 주세요.","영어 문구를 설명해 주세요.","영어 출력 문구가 아니라 한국어로 알려 주세요.","영문 문자열은 필요 없어요.","Do not give the exact English output string.","영어 출력 문구 없이 한국어 결과만 알려 주세요.","이 문구가 영어인가요?","영어 출력 문구가 바뀌었나요?","Does the exact English output string change?")) {
            assertThat(QuestionAnswerShape.apply(q,original,reason->{throw new AssertionError();})).as(q).isSameAs(original);
        }
        var none=GroundedAnswer.noEvidence();assertThat(QuestionAnswerShape.apply("영어 출력 문구는?",none,reason->{throw new AssertionError();})).isSameAs(none);
    }
}
