package com.nori.ai.gateway.app.golden;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.*;

/** 기대 계약은 골든셋에 고정하고 운영 내부 구조의 변환은 어댑터에 모읍니다. */
@Tag("llm-golden")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
@Isolated
class LlmGoldenClassificationTest {
    record GoldenCase(String id, String question, String referenceTime, String timezone,
            List<History> historyPairs, JsonNode expected) {}
    record History(String question, String answer, List<Map<String,String>> resourceRefs) {}

    private final Path root = Path.of(Objects.requireNonNull(System.getProperty("aigw.rootDir"),
        "시험 JVM에 저장소 루트 aigw.rootDir가 필요합니다")).toAbsolutePath().normalize();
    private final Path report = root.resolve("build/llm-golden/classification-report.json");
    private final List<Map<String,Object>> records = new ArrayList<>();
    private GoldenClassificationAdapter adapter;
    private Throwable setupFailure;
    private boolean initialized;

    @TestFactory
    Stream<DynamicTest> classifications() throws IOException {
        List<GoldenCase> cases;
        try (var input = getClass().getResourceAsStream("/golden/tc-classification.json")) {
            assertNotNull(input, "분류 골든셋이 필요합니다");
            var fixture = SchemaContracts.JSON.readTree(input);
            String reference = fixture.path("referenceTime").asString();
            String zone = fixture.path("timezone").asString();
            cases = new ArrayList<>();
            for (var value : fixture.path("cases")) {
                var history = SchemaContracts.JSON.convertValue(value.path("historyPairs"),
                    new tools.jackson.core.type.TypeReference<List<History>>() {});
                cases.add(new GoldenCase(value.path("id").asString(), value.path("question").asString(),
                    value.path("referenceTime").asString(reference), zone, history, value.get("expected")));
            }
        }
        assertEquals(17, cases.size(), "기존 15건과 코드 조회 2건을 모두 유지합니다");
        assertEquals(17, cases.stream().map(GoldenCase::id).distinct().count());
        writeReport();
        return cases.stream().map(value -> DynamicTest.dynamicTest(value.id() + " " + value.question(),
            () -> verifyCase(value)));
    }

    private void verifyCase(GoldenCase value) throws Exception {
        long started = System.nanoTime();
        var entry = new LinkedHashMap<String,Object>();
        entry.put("id", value.id());
        entry.put("question", value.question());
        entry.put("referenceTime", value.referenceTime());
        entry.put("timezone", value.timezone());
        entry.put("historyPairs", value.historyPairs());
        entry.put("expected", value.expected());
        JsonNode actual;
        try {
            if (!initialized) {
                initialized = true;
                try { adapter = new GoldenClassificationAdapter(root); }
                catch (Exception | LinkageError failure) { setupFailure = failure; }
            }
            actual = setupFailure == null ? adapter.classify(value) : GoldenClassificationAdapter.rejected(
                setupFailure.getClass().getName() + ": " + setupFailure.getMessage());
        } catch (Exception | LinkageError failure) {
            actual = GoldenClassificationAdapter.rejected(failure.getClass().getName() + ": " + failure.getMessage());
        }
        entry.put("actual", actual);
        entry.put("llmResponses", adapter == null ? List.of() : adapter.responses());
        entry.put("llmCalls", adapter == null ? 0 : adapter.calls());
        entry.put("clarificationReason", adapter == null ? null : adapter.clarificationReason());
        entry.put("elapsedMillis", Duration.ofNanos(System.nanoTime() - started).toMillis());
        records.add(entry);
        try {
            assertNotNull(adapter, "실제 LLM 설정·tokenizer·client 생성이 필요합니다");
            assertTrue(adapter.responses().stream().allMatch(response ->
                ((Number) response.get("timeoutMillis")).longValue() <= 180_000), "건당 timeout은 180초 이하입니다");
            assertEquals(comparable(value.expected()), comparable(actual), "결과 요약이 골든셋과 다릅니다");
            entry.put("passed", true);
        } catch (AssertionError | RuntimeException failure) {
            entry.put("passed", false);
            entry.put("comparisonFailure", failure.getMessage());
            throw failure;
        } finally {
            // 개별 불일치 뒤에도 다음 동적 시험을 실행하며 현재까지의 응답을 보존합니다.
            writeReport();
        }
    }

    private JsonNode comparable(JsonNode summary) {
        var normalized = (ObjectNode) summary.deepCopy();
        var calls = new ArrayList<JsonNode>();
        for (var call : normalized.path("toolCalls")) {
            var copy = (ObjectNode) call.deepCopy();
            var args = (ObjectNode) copy.get("args");
            for (String field : List.copyOf(args.propertyNames())) {
                var item = args.get(field);
                if (item.isNull() && adapter.nullable(copy.path("toolId").asString(), field)) {
                    args.remove(field);
                } else if (Set.of("from", "to").contains(field) && item.isString()) {
                    args.put(field, OffsetDateTime.parse(item.asString()).toInstant().toString());
                }
            }
            calls.add(copy);
        }
        calls.sort(Comparator.comparing(call -> SchemaContracts.canonicalize(call.toString())));
        normalized.set("toolCalls", SchemaContracts.JSON.valueToTree(calls));
        var fields = new ArrayList<String>();
        normalized.path("clarificationFields").forEach(field -> fields.add(field.asString()));
        Collections.sort(fields);
        normalized.set("clarificationFields", SchemaContracts.JSON.valueToTree(fields));
        return normalized;
    }

    private void writeReport() throws IOException {
        Files.createDirectories(report.getParent());
        Files.writeString(report, SchemaContracts.JSON.writerWithDefaultPrettyPrinter().writeValueAsString(
            Map.of("recordedAt", Instant.now().toString(), "cases", records)) + "\n");
    }

    @AfterAll
    void close() throws IOException {
        try { if (adapter != null) adapter.close(); }
        finally { writeReport(); }
    }
}
