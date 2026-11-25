package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.workflow.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static com.nori.ai.gateway.core.llm.IntentTestFixtures.*;

/** 고정 분류 응답을 사용하는 계약 회귀이며 실제 모델의 분류 정확도 측정은 아닙니다. */
class ConnectionIntentFlowTest {
    private static List<ClassificationIntent> connectionIntents() {
        var intents = new ArrayList<>(INTENTS);
        intents.add(intent("connection", "관측 연결 상태만 조회. 연결과 LOT를 함께 묻는 질문은 status",
                List.of("tc.eqp.getConnectionState"), true, "NONE", TaskType.LIVE_QUERY, false));
        return List.copyOf(intents);
    }

    private static CapabilityRegistry connectionRegistry() {
        // 고정된 기존 분류 fixture는 보존하고 이 시험에만 새 catalog 항목을 공급합니다.
        var manifests = new ArrayList<>(registry().all());
        manifests.add(ConfiguredTool.create(SCOPE.boundSystem(), "tc.eqp.getConnectionState", "관측 연결 상태", ToolKind.BUSINESS,
                SchemaContracts.JSON.valueToTree(Map.of("type", "object", "additionalProperties", false,
                        "properties", Map.of("eqp", Map.of("type", "string", "minLength", 1, "maxLength", 64)),
                        "required", List.of("eqp"))), SchemaContracts.parse("{\"type\":\"object\"}"), List.of("EQP_READ"), 10000));
        return new CapabilityRegistry(manifests, Set.of(ImplementationPhase.PHASE_1), new ContractVersionPolicy());
    }

    private static ClassificationService.Context connectionContext(RegistryView view, List<ClassificationService.HistoryPair> history) {
        return new ClassificationService.Context(view, history, Map.of("DLQ", "Dead Letter Queue"), 2, List.of(), connectionIntents());
    }

    @Test void previousEtcSubjectUsesDedicatedReadWhileBroadAndMixedQuestionsKeepSnapshot() {
        for (var entry : Map.of("그럼 해당 설비의 현재 연결 상태가 어때", "connection",
                "그 설비 전체 운영 상태를 알려줘", "status", "연결 상태와 현재 LOT도 알려줘", "status").entrySet()) {
            var registry = connectionRegistry();
            var history = List.of(new ClassificationService.HistoryPair("ETC_001 등록 정보", "등록 모델 v77"));
            var client = new ScriptedClient(reply(wire(entry.getValue(), "ETC_001")));
            var result = ClassificationServiceTest.service(client, connectionContext(registry.view(SCOPE), history), m -> 100, a -> {})
                    .classify(ClassificationServiceTest.request(entry.getKey()));
            assertThat(result).isInstanceOf(ClassificationService.Classified.class);
            var classification = ((ClassificationService.Classified) result).classification();
            assertThat(classification.needs()).containsExactly(new Classification.Need(entry.getValue(), "ETC_001"));
            var request = ClassificationServiceTest.request(entry.getKey());
            var job = new JobContext("job-1", "conversation", SCOPE, "d".repeat(22), request.cancellation(),
                    InferenceMode.ENABLED, classification.jobClassification(), request.input());
            var builder = new WorkflowPlanBuilder(registry, connectionIntents());
            var plan = builder.build(builder.definition(job), registry.view(SCOPE), job);
            assertThat(plan.toolPlan().plan()).singleElement().satisfies(step -> {
                assertThat(step.capabilityId()).isEqualTo(entry.getValue().equals("connection")
                        ? "tc.eqp.getConnectionState" : "tc.eqp.getOperationalSnapshot");
                assertThat(step.arguments()).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("eqp", "ETC_001")));
                assertThat(step.resources()).containsExactly(new com.nori.ai.gateway.core.plan.ResourceRef("EQP", "ETC_001"));
            });
            var input = SchemaContracts.parse(client.sent.getFirst().messages().getLast().content());
            // 기존 추출 계약은 이력의 영숫자 업무 버전도 후보에 보존합니다. 실제 선택과 계획은 ETC_001이어야 합니다.
            assertThat(input.path("subjectCandidates")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("ETC_001", "v77")));
            assertThat(input.path("historyPairs").get(0).path("answer").asString()).isEqualTo("등록 모델 v77");
            assertThat(client.sent).hasSize(1);
        }
    }

    @Test void ambiguousPreviousEquipmentDoesNotPermitAnInventedFirstSubject() {
        var client = new ScriptedClient(reply(wire("connection", null)));
        var history = List.of(new ClassificationService.HistoryPair("ETC_001와 PHOTO_001 비교", "비교"));
        var result = ClassificationServiceTest.service(client, connectionContext(connectionRegistry().view(SCOPE), history), m -> 100, a -> {})
                .classify(ClassificationServiceTest.request("해당 설비 연결 상태"));
        assertThat(result).isInstanceOf(ClassificationService.Rejected.class);
    }
}
