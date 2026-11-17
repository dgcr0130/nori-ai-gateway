package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.port.InferenceClient;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AnswerCheckTest {
    private static InferenceClient.Completed result(String content,String finish,int completion){return new InferenceClient.Completed(content,finish,10,completion,Duration.ZERO,10);}
    @Test void exactPublicAndThinkingBoundsPassAndEachSuccessorFailsWithoutChangingTheText() {
        var budget=new TokenBudgetPolicy.CallBudget(10,3,2,1);
        assertThat(AnswerCheck.failure(result("abc","stop",5),budget,String::length)).isNull();
        assertThat(AnswerCheck.failure(result("abcd","stop",5),budget,String::length)).isEqualTo("ANSWER_LIMIT_EXCEEDED");
        assertThat(AnswerCheck.failure(result("ab","stop",5),budget,String::length)).isEqualTo("USAGE_EXCEEDS_LIMIT");
        assertThat(AnswerCheck.failure(result("abc","stop",2),budget,String::length)).isEqualTo("USAGE_EXCEEDS_LIMIT");
        assertThat(AnswerCheck.failure(result("abc","length",5),budget,String::length)).isEqualTo("FINISH_LENGTH");
        assertThat(AnswerCheck.failure(result("<think>abc","stop",5),budget,String::length)).isEqualTo("ANSWER_TOKENS_UNCOUNTABLE");
        assertThat(AnswerCheck.failure(result("abc","stop",5),budget,text->{throw new IllegalArgumentException();})).isEqualTo("ANSWER_TOKENS_UNCOUNTABLE");
        var noThinking=new TokenBudgetPolicy.CallBudget(10,3,0,1);
        assertThat(AnswerCheck.failure(result("abc","stop",3),noThinking,String::length)).isNull();
        assertThat(AnswerCheck.failure(result("ab","stop",3),noThinking,String::length)).isEqualTo("USAGE_EXCEEDS_LIMIT");
    }
}
