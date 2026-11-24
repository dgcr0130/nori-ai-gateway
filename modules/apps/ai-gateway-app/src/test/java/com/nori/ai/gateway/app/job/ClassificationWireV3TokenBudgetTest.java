package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.CancellationToken;
import com.nori.ai.gateway.core.job.JobRequestInput;
import com.nori.ai.gateway.core.llm.ClassificationRegistryText;
import com.nori.ai.gateway.core.llm.ClassificationService;
import com.nori.ai.gateway.core.llm.LlmCallObserver;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.ManifestLoader;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nori.ai.gateway.core.workflow.EvidenceNeed;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import com.nori.ai.gateway.integration.tokenizer.PromptTokenCounter;
import com.nori.ai.gateway.integration.tokenizer.ConfiguredChatTemplateRenderer;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

/** 최대 길이 대상과 4개 needs를 실제 tokenizer로 계수합니다. */
class ClassificationWireV3TokenBudgetTest {
    private static final Instant NOW = Instant.parse("2026-11-05T00:00:00Z");

    private static String resource(String path) throws Exception {
        try (var input = ClassificationWireV3TokenBudgetTest.class.getClassLoader().getResourceAsStream(path)) { assertThat(input).as(path).isNotNull(); return new String(input.readAllBytes(), StandardCharsets.UTF_8); }
    }
    @Test void maximumNeedsWireFitsTheAnswerLimitAndTheAssembledInputFitsTheContextWithTheRequestMaxTokens() throws Exception {
        String eqp = "설비\"경계\\" + "가".repeat(54) + "EQP0";
        assertThat(eqp.codePointCount(0, eqp.length())).isEqualTo(64);
        var wire=SchemaContracts.JSON.createObjectNode();var needs=wire.putArray("needs");
        for(int i=0;i<4;i++)needs.addObject().put("intent","document").put("subject",eqp);
        String content = wire.toString();
        assertThat(content).contains("\\\"", "\\\\");
        var policy = TokenBudgetPolicy.current();
        var budget = policy.budget(CallType.CLASSIFY);
        String question=eqp+" 원인과 정상 절차 비교";
        List<List<ChatMessage>> sent = new ArrayList<>();
        try (var tokenizer = new HfTokenizerCounter()) {
            int answer = tokenizer.countText(content);
            assertThat(answer).as("ⓞ 답변 토큰").isLessThanOrEqualTo(budget.answerLimit());
            assertThat(budget.answerLimit()).isEqualTo(768);
            assertThat(budget.requestMaxTokens()).as("ⓞ 요청 max_tokens").isEqualTo(2_048);
            InferenceClient client = new InferenceClient() {
                @Override public ChatOutcome chat(CallType type, List<ChatMessage> messages, int maxTokens) {
                    sent.add(messages); assertThat(maxTokens).isEqualTo(2_048);
                    return new Completed(content, "stop", 100, answer, Duration.ofMillis(1), 100);
                }
                @Override public List<float[]> embed(List<String> inputs) { throw new UnsupportedOperationException(); }
            };
            var service = new ClassificationService(client, policy, tokenizer::countText, new PromptTokenCounter(com.nori.ai.gateway.integration.testing.InferenceFixtures.renderer(),tokenizer)::count, new ExpiryCheckpoint(),
                LlmCallObserver.NONE, attempt -> {}, resource("prompts/classify-v4.txt"), ignored->new ClassificationService.Context(new com.nori.ai.gateway.core.registry.RegistryView(OwnerSystem.TC,List.of()),List.of(),Map.of(),2,List.of(),com.nori.ai.gateway.app.SystemConfigFixtures.intents()));
            var context = Map.<String,Object>of("context", Map.of("timezone", "+09:00", "referenceTime", "2026-10-20T10:00:00+09:00"),"resourceRefs",List.of(Map.of("type","EQP","ref",eqp)));
            var result = service.classify(new ClassificationService.Request(new com.nori.ai.gateway.core.scope.VerifiedSystemScope(OwnerSystem.TC, "svc", new com.nori.ai.gateway.core.scope.ActorRef("actor"), "actor",
                java.util.Set.of("EQP_READ"), "a".repeat(64), NOW.minusSeconds(1), NOW.plusSeconds(60), "job-1"), "conversation", "job-1", new CancellationToken(), new JobRequestInput("message", 1, question, context)));
            assertThat(result).as("최대 wire 는 절삭 없이 통과").isInstanceOf(ClassificationService.Classified.class);
            var classified = ((ClassificationService.Classified) result).classification();
            assertThat(classified.targets()).extracting(com.nori.ai.gateway.core.plan.ResourceRef::ref).containsExactly(eqp);
            assertThat(classified.toolCalls()).isEmpty();
            int input = new PromptTokenCounter(com.nori.ai.gateway.integration.testing.InferenceFixtures.renderer(), tokenizer).count(sent.getFirst());
            assertThat(input).as("분류 입력 상한").isLessThanOrEqualTo(budget.inputLimit());
            assertThat(input + budget.requestMaxTokens()).as("ⓞ 입력 + max_tokens").isLessThanOrEqualTo(TokenBudgetPolicy.CONTEXT_TOKENS);
        }
    }
}
