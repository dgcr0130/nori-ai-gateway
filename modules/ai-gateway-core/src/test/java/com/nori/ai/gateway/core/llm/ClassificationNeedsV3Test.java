package com.nori.ai.gateway.core.llm;
import org.junit.jupiter.api.Test;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.workflow.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
class ClassificationNeedsV3Test {
 @Test void documentSearchIntentDependsOnTaskAndSharesTheJobBudget(){
  var rules=new EvidenceCoverageEvaluator();for(var task:TaskType.values())assertThat(rules.requiresDocuments(task)).isEqualTo(task==TaskType.KNOWLEDGE_QUERY);
  var budget=new ToolCallBudget();assertThat(budget.tryAcquireDocumentSearch()).isTrue();assertThat(budget.tryAcquireDocumentSearch()).isTrue();assertThat(budget.tryAcquireDocumentSearch()).isFalse();assertThat(budget.total()).isEqualTo(2);assertThat(budget.tryStartSupplementRound()).isTrue();assertThat(budget.tryStartSupplementRound()).isFalse();
  for(int tool=0;tool<3;tool++)for(int call=0;call<3;call++)assertThat(budget.tryAcquire("tool"+tool,ToolKind.BUSINESS)).isTrue();assertThat(budget.tryAcquire("tool0",ToolKind.BUSINESS)).isFalse();assertThat(budget.tryAcquire("last",ToolKind.BUSINESS)).isTrue();assertThat(budget.total()).isEqualTo(12);assertThat(budget.tryAcquire("over",ToolKind.BUSINESS)).isFalse();
 }
 @Test void generalUnsupportedAndDocumentOnlyQuestionsHaveNoTools(){for(String intent:List.of("help","unsupported","document")){var node=IntentTestFixtures.wire(intent,null);var client=new ScriptedClient(reply(node));var result=(ClassificationService.Classified)ClassificationServiceTest.service(client).classify(ClassificationServiceTest.request("질문"));assertThat(result.classification().toolCalls()).isEmpty();assertThat(client.sent).hasSize(1);}}
 @Test void explicitLimitsBoundTheSameCountersAndRejectNegativeValues(){var budget=new ToolCallBudget(1,1,1,0);assertThat(budget.tryAcquire("logs.search",ToolKind.LOG)).isTrue();assertThat(budget.tryAcquireDocumentSearch()).isFalse();assertThat(budget.tryStartSupplementRound()).isFalse();assertThatThrownBy(()->new ToolCallBudget(-1,3,2,1)).isInstanceOf(IllegalArgumentException.class);}
}
