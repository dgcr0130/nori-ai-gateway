package com.nori.ai.gateway.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nori.ai.gateway.acceptance.loki.LokiDirectQuery;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.TokenBudgetPolicy;
import com.nori.ai.gateway.core.retention.TimeWindow;
import com.nori.ai.gateway.integration.loki.LokiProperties;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import org.opentest4j.TestAbortedException;
import static com.nori.ai.gateway.acceptance.F1SecurityChecks.*;

/**
 * F2 Job leaf(LLM_LIVE·LLM_STUB) 판정. 실제 Job API 응답·고정 SQL oracle·verify hook 관측(prompt-watch 호출 수, Mock 호출 수, Loki 송신 관측)만 대조하고,
 * 기대 결과를 제품에 주입하지 않는다. LLM_STUB 은 접수 전 {@code scenario.llmScript} 무장으로만 만들며 LIVE 결과를 대신하지 않는다.
 * 관측 수단이 배포본에 없으면 leaf 를 PASS 로 만들지 않고 고정 사유의 NOT_RUN(중단)으로 남긴다.
 */
public final class F2JobChecks {
    /** prompt-watch 의 질문 sentinel 위치. 같은 무장의 다른 sentinel 은 1부터다. */
    static final int QUESTION = 0;
    static final List<String> BUSINESS_READS = List.of("getEqpSnapshot", "getModel", "listDlq", "getCommLogs");
    static final String COMPOSER_OVERSIZE_SCENARIO = "composer-oversize";
    /** AC-35-EGRESS 양성 대조 대상 id(추론 endpoint·주 DB, SPEC §27.2). runconfig 목록에 ALLOWED 로 있어야 판정한다(test-hooks-v1). */
    static final Set<String> EGRESS_POSITIVE_CONTROLS = Set.of("inference-endpoint", "db-main");
    /** Tool 실패로 기록하는 저장 상태(ResultStatus 중 Source·실행 실패). */
    static final Set<String> TOOL_FAILURES = Set.of("FAILED", "TIMEOUT", "SOURCE_UNAVAILABLE", "DENIED");
    private static final Duration STUB_TIMEOUT = Duration.ofSeconds(200), LIVE_TIMEOUT = Duration.ofSeconds(600);
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 한 Job 의 접수 키·질문 sentinel·terminal 응답. */
    public record Job(String id, String requestKey, String sentinel, JsonNode response) {
        @Override public String toString() { return "F2Job[" + id + "]"; }
    }
    private final GatewayClient gateway; private final HookClient hooks; private final OracleQueries oracle;
    private final Optional<LokiDirectQuery> loki;
    /** pin tokenizer 텍스트 계수. 하네스에서 tokenizer 를 쓸 수 없으면 빈 값이다. */
    private final Function<String,Optional<Integer>> tokenCount;

    public F2JobChecks(GatewayClient gateway, HookClient hooks, OracleQueries oracle, Optional<LokiDirectQuery> loki, Function<String,Optional<Integer>> tokenCount) {
        this.gateway = gateway; this.hooks = hooks; this.oracle = oracle; this.loki = loki; this.tokenCount = tokenCount;
    }

    public void run(String leaf) {
        require(oracle.nonTerminalJobCount() == 0, "ISOLATION_NOT_IDLE"); hooks.reset();
        switch (leaf) {
            case "AC-22-LIVE" -> live();
            case "AC-29" -> foreignSystemLive();
            case "AC-38-F2" -> incidentLogsLive();
            case "AC-31" -> composerReductionLive();
            case "AC-22-STUB-TOOL", "AC-22-STUB-SYSTEM" -> planRejected(leaf);
            case "AC-22-GQ-HELP" -> generalHelp();
            case "AC-22-GQ-BYPASS-DATA", "AC-22-GQ-BYPASS-XSYS" -> generalBypass(leaf);
            case "AC-30-EQ", "AC-30-WM", "AC-30-ABSENT" -> resourceMissing(leaf);
            case "AC-32-SCHEMA", "AC-32-LENGTH" -> classificationFailsTwice(leaf);
            case "AC-32-RETRYOK" -> retryOk();
            case "AC-32-NEEDS" -> needs();
            case "AC-43-CLS" -> classificationOverBudget();
            case "AC-43-COMP" -> composerOverBudget();
            case "AC-35-EGRESS" -> egress();
            default -> throw new IllegalArgumentException("F2_JOB_LEAF_INVALID");
        }
    }

    // ── LLM_LIVE ─────────────────────────────────────────────────────────────────────────────────
    private void live() {
        for (var fixture : F2Fixture.fixtures("AC-22-LIVE")) {
            var job = submit(fixture.fixtureId(), fixture.question(), window(Duration.ofMinutes(30), true), null, LIVE_TIMEOUT);
            var rows = oracle.toolRows(job.id());
            long unregistered = rows.stream().filter(row -> !F2Fixture.IMPLEMENTED.contains(row.capabilityId())).count();
            long foreign = rows.stream().filter(row -> !row.capabilityId().startsWith("tc.")&&!F2Fixture.IMPLEMENTED.contains(row.capabilityId())).count();
            sample("AC-22-LIVE", fixture.fixtureId(), job, rows, Map.of("unregisteredToolCount", unregistered, "foreignSystemToolCount", foreign));
            require(oracle.jobOwner(job.id()).orElseThrow(() -> new AssertionError("F2_JOB_OWNER_MISSING")).equals("TC"), "F2_LIVE_SYSTEM_SWITCHED");
            require(unregistered == 0 && foreign == 0, "F2_LIVE_TOOL_OUTSIDE_REGISTRY");
            byte[] narrative = F1JobChecks.narrative(job.response());
            require(!F2Fixture.forbiddenHit(fixture, new String(narrative, StandardCharsets.UTF_8)), "F2_LIVE_FORBIDDEN_CLAIM");
            F2RubricTargets.add("AC-22-LIVE", fixture.fixtureId(), job.id(), narrative);
        }
    }
    private void foreignSystemLive() {
        var job = submit("ac-29", "WM 에서 LOT-001 재고·납기 알려줘", window(Duration.ofMinutes(30), false), null, LIVE_TIMEOUT);
        var rows = oracle.toolRows(job.id());
        sample("AC-29", "ac-29", job, rows, Map.of());
        require(status(job).equals("UNSUPPORTED"), "F2_FOREIGN_NOT_UNSUPPORTED");
        require(rows.isEmpty(), "F2_FOREIGN_TOOL_EXECUTED");
        F1SourceChecks.assertReferences(payload(job));
        require(payload(job).path("evidence").isEmpty(), "F2_FOREIGN_DATA_EXPOSED");
    }
    private void incidentLogsLive() {
        hooks.clock(Map.of("op", "RESUME_SYSTEM"));
        Instant to = hooks.clockNow().truncatedTo(ChronoUnit.SECONDS), from = to.minusSeconds(3600);
        var direct = loki.orElseThrow(() -> new TestAbortedException("LOKI_POSITIVE_CONTROL_UNAVAILABLE"));
        int count = 0; for (String app : F1Fixture.APPS) count += direct.measure(app, new TimeWindow(from, to)).count();
        if (count == 0) throw new TestAbortedException("LOKI_POSITIVE_CONTROL_EMPTY");
        var body = new LinkedHashMap<String,Object>(context(to, from, to)); body.put("resourceRefs", List.of(Map.of("type", "EQP", "ref", "EQP-001")));
        var job = submit("ac-38-f2", "EQP-001 에서 발생한 오류 로그를 보고 장애 원인을 분석해 줘", body, null, LIVE_TIMEOUT);
        var rows = oracle.toolRows(job.id());
        sample("AC-38-F2", "ac-38-f2", job, rows, Map.of());
        require(oracle.classifiedTaskTypes(job.id()).equals(List.of("INCIDENT_ANALYSIS")), "F2_INCIDENT_NOT_CLASSIFIED");
        require(rows.stream().anyMatch(row -> row.capabilityId().equals("logs.search")), "F2_INCIDENT_LOG_TOOL_MISSING");
        require(rows.stream().allMatch(row -> F2Fixture.IMPLEMENTED.contains(row.capabilityId())), "F2_INCIDENT_FOREIGN_TOOL");
        require(!status(job).equals("UNSUPPORTED"), "F2_INCIDENT_UNSUPPORTED");
        var boundaries = new LinkedHashMap<String,Instant>();
        for (String app : F1Fixture.APPS) boundaries.put(app, oracle.retentionProbe(app).flatMap(OracleQueries.RetentionProbe::verifiedOldestAt).orElse(from));
        JsonNode observation;
        try { observation = hooks.get("/__test/source-observations/" + job.id()); } catch (HookClient.HookFailure unavailable) { throw new TestAbortedException("SOURCE_REQUEST_OBSERVATION_UNAVAILABLE"); }
        F1SourceChecks.verify(observation, job.id(), F1SourceChecks.expected(boundaries, from, to, new LokiProperties().splitInterval()), false);
    }
    /**
     * SPEC §27.2 AC-31: 실제 Composer 호출 1건이 호출 직전 assert(입력 ≤ 2,816, 입력 + 요청 max_tokens ≤ 8192)를 통과하고, 적용한 축소 단계와 단계별 전후 입력 토큰 수가
     * 그 호출의 감사(LLM_CALL)에 남고, 축소 금지 항목인 현재 질문이 축소 뒤 Prompt 에 남고(prompt-watch), usage.prompt_tokens 가 호출 직전 계수와 같다.
     * 감사는 고정 SELECT 로만 읽는다. 축소가 일어나지 않았거나 축소로도 상한 안에 들지 않은 실행은 시험 전제 미충족이라 NOT_RUN 이다.
     */
    private void composerReductionLive() {
        String sentinel = "PW" + letters(10);
        // llmScript 없는 무장은 운영 전송으로 가며(test-hooks-v1 P20) prompt-watch 관찰만 붙는다. 추론 사용 시 무장 taskType 은 분류에 쓰이지 않는다.
        var scenario = CanonicalJson.MAPPER.createObjectNode().put("taskType", "GENERAL_QUERY");
        scenario.putObject("promptWatch").putArray("sentinels").add(sentinel);
        var job = submit("ac-31", sentinel + " EQP-001 에서 발생한 오류 로그를 모두 보고 장애 원인을 분석해 줘", window(Duration.ofHours(1), true), scenario, LIVE_TIMEOUT, sentinel);
        sample("AC-31", "ac-31", job, oracle.toolRows(job.id()), Map.of());
        var calls = oracle.llmCalls(job.id());
        require(calls.stream().filter(OracleQueries.LlmCall::sent).allMatch(F2JobChecks::withinBudget), "F2_AC31_OVER_BUDGET_SENT");
        var composer = calls.stream().filter(call -> call.callType().equals("COMPOSER")).toList();
        if (composer.isEmpty()) throw new TestAbortedException("AC31_COMPOSER_NOT_REACHED");
        var call = composer.getFirst();
        if (call.reduction().isEmpty()) throw new TestAbortedException("AC31_REDUCTION_NOT_EXERCISED");
        if (!call.sent()) throw new TestAbortedException("AC31_REDUCTION_INSUFFICIENT");
        require(composer.size() == 1, "F2_AC31_COMPOSER_CALLS");
        require(call.maxTokens() == 3_072 && call.inputTokens() <= 4_608 && call.inputTokens() + call.maxTokens() + 512 <= TokenBudgetPolicy.CONTEXT_TOKENS, "F2_AC31_PRE_CALL_ASSERT");
        require(reductionRecorded(call.reduction(), 4_608, call.inputTokens()), "F2_AC31_REDUCTION_RECORD");
        require(watch(job, "COMPOSER", QUESTION) == 1, "F2_AC31_QUESTION_REDUCED");
        int prompt = call.promptTokens().orElseThrow(() -> new TestAbortedException("AC31_USAGE_MISSING"));
        require(prompt == call.inputTokens(), "F2_AC31_SERVER_TRUNCATION");
    }
    /** 보낸 호출의 유형별 입력 상한·num_ctx 총합(SPEC §14.5). */
    static boolean withinBudget(OracleQueries.LlmCall call) {
        return TokenBudgetPolicy.current().violations(CallType.valueOf(call.callType()), call.inputTokens(), call.maxTokens()).isEmpty();
    }
    /** 단계 번호는 §14.5 순서(1~4)로 증가하고 단계 사이 계수가 이어지며, 첫 단계 전은 상한 초과·마지막 단계 뒤는 호출 직전 계수다. */
    static boolean reductionRecorded(List<OracleQueries.ReductionStep> steps, int inputLimit, int inputTokens) {
        int previousStage = 0, previousAfter = steps.getFirst().beforeTokens();
        for (var step : steps) {
            if (step.stage() <= previousStage || step.stage() > 4 || step.beforeTokens() != previousAfter || step.afterTokens() > step.beforeTokens()) return false;
            previousStage = step.stage(); previousAfter = step.afterTokens();
        }
        return steps.getFirst().beforeTokens() > inputLimit && previousAfter == inputTokens;
    }
    private void sample(String leaf, String fixture, Job job, List<OracleQueries.ToolRow> rows, Map<String,Object> extra) {
        var values = new LinkedHashMap<String,Object>();
        values.put("resultStatus", status(job)); values.put("errorCode", errorCode(job).orElse(null));
        values.put("toolCount", rows.size()); values.put("toolFailedCount", rows.stream().filter(row -> TOOL_FAILURES.contains(row.status())).count());
        values.putAll(extra);
        F2LiveSamples.add(leaf, fixture, job.id(), values);
    }

    // ── LLM_STUB ─────────────────────────────────────────────────────────────────────────────────
    private void planRejected(String leaf) {
        var output=(ObjectNode)parse(wire("LQ","EQP-001",List.of(),"B"));
        if(leaf.equals("AC-22-STUB-TOOL"))((ObjectNode)output.path("toolCalls").get(0)).put("toolId","tc.unknown.tool");
        else output.put("systemId","WM");
        String reply=output.toString();
        var job = scripted(leaf, "EQP-001 상태 알려줘", window(Duration.ofMinutes(30), false), List.of(classify(reply), classify(reply)), List.of());
        failed(job, "CONTRACT_INVALID");
        require(oracle.jobOwner(job.id()).orElseThrow(() -> new AssertionError("F2_JOB_OWNER_MISSING")).equals("TC"), "F2_BOUND_SYSTEM_CHANGED");
        require(oracle.toolRows(job.id()).isEmpty(), "F2_REJECTED_PLAN_EXECUTED_TOOL");
        require(watch(job, "CLASSIFY", QUESTION) >= 1 && watch(job, "COMPOSER", QUESTION) == 0, "F2_REJECTED_PLAN_CALLS");
    }
    private void generalHelp() {
        var job = scripted("AC-22-GQ-HELP", "여기서 뭘 물어볼 수 있어?", Map.of(), List.of(classify(wire("GQ", null, List.of(), "B")), compose("물어볼 수 있는 기능 범위를 안내한다.")),
            List.of(F2Fixture.GQ_MANIFEST_SNIPPET, F2Fixture.ABSENT_DOCUMENT_TOOL));
        generalCommon(job);
        var rows = new ArrayList<String>(); payload(job).path("blocks").path(0).path("content").path("rows").forEach(row -> rows.add(row.path("capabilityId").asText()));
        require(rows.equals(F2Fixture.GQ_HELP_ROWS) && rows.stream().noneMatch(F2Fixture.PLANNED::contains), "F2_GQ_HELP_ROWS");
        require(watch(job, "COMPOSER", 1) == 1 && watch(job, "COMPOSER", 2) == 0, "F2_GQ_HELP_COMPOSER_INPUT");
    }
    private void generalBypass(String leaf) {
        for (var fixture : F2Fixture.fixtures(leaf)) {
            var job = scripted(fixture.fixtureId(), fixture.question(), Map.of(), List.of(classify(wire("GQ", null, List.of(), "B")), compose(fixture.composerScript().orElseThrow())), List.of());
            generalCommon(job);
            byte[] narrative = F1JobChecks.narrative(job.response());
            require(narrative.length > 0 && !F2Fixture.forbiddenHit(fixture, new String(narrative, StandardCharsets.UTF_8)), "F2_GQ_FORBIDDEN_CLAIM");
            F2RubricTargets.add(leaf, fixture.fixtureId(), job.id(), narrative);
        }
    }
    private void generalCommon(Job job) {
        require(job.response().path("jobStatus").asText().equals("SUCCEEDED") && status(job).equals("SUCCESS"), "F2_GQ_STATUS");
        require(oracle.toolRows(job.id()).isEmpty() && businessReads() == 0, "F2_GQ_BUSINESS_READ");
        require(!payload(job).has("claims") && payload(job).path("evidence").isEmpty(), "F2_GQ_BUSINESS_CLAIM");
        require(oracle.classifiedTaskTypes(job.id()).equals(List.of("GENERAL_QUERY")), "F2_GQ_AUDIT_TASK_TYPE");
        require(watch(job, "CLASSIFY", QUESTION) == 1, "F2_GQ_CLASSIFY_CALLS");
    }
    private void resourceMissing(String leaf) {
        String eqp = switch (leaf) { case "AC-30-EQ" -> "EQ"; case "AC-30-WM" -> "WM"; default -> "EQP-NOT-FOUND"; };
        var job = scripted(leaf, "설비 상태 알려줘", window(Duration.ofMinutes(30), false), List.of(classify(wire("LQ", eqp, List.of("S"), "B"))), List.of());
        failed(job, "CONTRACT_INVALID");
        clarification(job, "CONTRACT_INVALID", List.of());
        require(calls("getEqpSnapshot") == 0, "F2_RESOURCE_VALIDATION_NOT_RUN");
        require(oracle.toolRows(job.id()).isEmpty() && calls("getEqpSnapshot") == 0, "F2_RESOURCE_TOOL_EXECUTED");
        require(watch(job, "CLASSIFY", QUESTION) == 1 && watch(job, "COMPOSER", QUESTION) == 0, "F2_RESOURCE_CALLS");
    }
    private void classificationFailsTwice(String leaf) {
        var reply = leaf.equals("AC-32-SCHEMA") ? classify("{\"t\":\"LQ\",\"eqp\":\"EQP-001\",\"from\":null,\"to\":null,\"n\":[\"S\"],\"a\":\"B\"}")
            : reply("CLASSIFY", "{\"t\":\"LQ\",\"eqp\":\"EQP-0", "length", 2_048);
        var job = scripted(leaf, "EQP-001 상태 알려줘", window(Duration.ofMinutes(30), false), List.of(reply, reply), List.of());
        failed(job, "CONTRACT_INVALID");
        clarification(job, "CONTRACT_INVALID", List.of());
        require(watch(job, "CLASSIFY", QUESTION) == 1, "F2_CLASSIFY_RETRY_COUNT");
        require(oracle.toolRows(job.id()).isEmpty() && watch(job, "COMPOSER", QUESTION) == 0, "F2_FAILED_CLASSIFY_PROCEEDED");
    }
    private void retryOk() {
        var job=scripted("no-retry","EQP-001 상태",window(Duration.ofMinutes(30),false),List.of(classify("{}"),classify(wire("LQ","EQP-001",List.of(),"B"))),List.of());failed(job,"CONTRACT_INVALID");require(watch(job,"CLASSIFY",QUESTION)==1&&oracle.toolRows(job.id()).isEmpty()&&watch(job,"COMPOSER",QUESTION)==0,"F2_NO_CLASSIFICATION_RETRY");
    }
    private void classificationOverBudget() {
        // 분류 고정 응답을 두지 않는다. 분류가 한 번이라도 전송되면 유형 불일치 전송 실패(LLM_UNAVAILABLE)가 되어 예산 초과와 구별된다.
        String question = "EQP-001 의 최근 통신 오류와 로그를 모두 자세히 분석해 줘. ".repeat(400);
        var job = scripted("AC-43-CLS", question, window(Duration.ofMinutes(30), false), List.of(compose("서술")), List.of());
        failed(job, "INPUT_BUDGET_EXCEEDED");
        require(!payload(job).has("clarification"), "F2_CLS_BUDGET_CLARIFICATION");
        require(watch(job, "CLASSIFY", QUESTION) == 1 && watch(job, "COMPOSER", QUESTION) == 0, "F2_CLS_BUDGET_CALLS");
        require(oracle.toolRows(job.id()).isEmpty() && businessReads() == 0, "F2_CLS_BUDGET_TOOL");
        // 호출 직전 assert 로 막힌 시도도 감사에 남는다(sent=false). 보낸 분류 호출은 0 이다.
        var calls = oracle.llmCalls(job.id());
        require(calls.size() == 1 && calls.getFirst().callType().equals("CLASSIFY") && !calls.getFirst().sent() && calls.getFirst().inputTokens() > 5_120
            && calls.getFirst().errorCode().equals(Optional.of("INPUT_BUDGET_EXCEEDED")), "F2_CLS_BUDGET_AUDIT");
    }
    private void composerOverBudget() {
        try { hooks.post("/__fixture/scenario", Map.of("id", COMPOSER_OVERSIZE_SCENARIO), 204); }
        catch (HookClient.HookFailure failure) { if (failure.status() == 400) throw new TestAbortedException("COMPOSER_OVERSIZE_FIXTURE_UNAVAILABLE"); throw failure; }
        String fallback = "FALLBACK" + letters(8);
        // 표준 조회 fixture의 대량 values를 축소 뒤에도 보존하는 실행 목록으로 분류한다.
        var job = scripted("AC-43-COMP", "EQP-001 상태와 적용 모델 정의 알려줘", window(Duration.ofMinutes(30), false),
            List.of(classify(wire("LQ", "EQP-001", List.of("S", "N"), "B")), compose("대체 서술 " + fallback)), List.of());
        failed(job, "INPUT_BUDGET_EXCEEDED");
        require(watch(job, "CLASSIFY", QUESTION) == 1 && watch(job, "COMPOSER", QUESTION) == 0, "F2_COMP_BUDGET_CALLS");
        require(!payload(job).has("answer") && !job.response().toString().contains(fallback), "F2_COMP_BUDGET_FALLBACK_TEXT");
        require(!payload(job).path("blocks").isEmpty(), "F2_COMP_BUDGET_BLOCKS_DROPPED");
        // 축소 1~4 뒤에도 초과면 Composer 호출 시도 자체가 없다(감사 0). 같은 Job 의 분류는 상한 안에서 보냈다.
        var calls = oracle.llmCalls(job.id());
        require(calls.stream().noneMatch(call -> call.callType().equals("COMPOSER")), "F2_COMP_BUDGET_COMPOSER_AUDITED");
        require(!calls.isEmpty() && calls.stream().allMatch(call -> call.callType().equals("CLASSIFY") && call.sent() && withinBudget(call)), "F2_COMP_BUDGET_CLASSIFY_AUDIT");
    }

    /** SPEC §27.2 AC-32-NEEDS ⓐ~ⓞ. 실패는 즉시 FAIL, 배포 관측이 없는 사례가 하나라도 있으면 끝에 NOT_RUN 이다. */
    private void needs() {
        for(int count=0;count<=4;count++){
            var node=(ObjectNode)parse(wire("LQ","EQP-001",List.of(),"B"));var selected=node.putArray("toolCalls");for(int i=0;i<count;i++)selected.addObject().put("toolId","tc.eqp.getOperationalSnapshot").set("arguments",CanonicalJson.MAPPER.valueToTree(Map.of("eqp","EQP-001")));
            var job=scripted("calls-"+count,"EQP-001 상태",window(Duration.ofMinutes(30),false),List.of(classify(node.toString()),compose("자료를 확인했다.")),List.of());
            require(watch(job,"CLASSIFY",QUESTION)==1,"F2_CLASSIFY_SINGLE_CALL");require(oracle.toolRows(job.id()).size()==Math.min(count,3),"F2_EXECUTION_LIST_COUNT");if(count==4)require(payload(job).path("trace").toString().contains("TOOL_LIMIT_REACHED"),"F2_TOOL_LIMIT_TRACE");
        }
        var invalid=(ObjectNode)parse(wire("LQ","EQP-001",List.of(),"B"));invalid.put("systemId","WM");var job=scripted("calls-invalid","EQP-001 상태",window(Duration.ofMinutes(30),false),List.of(classify(invalid.toString())),List.of());failed(job,"CONTRACT_INVALID");require(watch(job,"CLASSIFY",QUESTION)==1&&oracle.toolRows(job.id()).isEmpty(),"F2_EXECUTION_LIST_REJECTED");
    }

    // ── AC-35-EGRESS ─────────────────────────────────────────────────────────────────────────────
    /**
     * SPEC §27.2 AC-35-EGRESS: 대상 목록은 Gateway hook(GET /__test/egress-probe, id·expect 만)에서 읽는다. BLOCKED 가 없거나 양성 대조(추론 endpoint·주 DB)가
     * ALLOWED 로 없으면 판정 근거가 없어 NOT_RUN, ALLOWED 가 연결되지 않으면 NOT_RUN, BLOCKED 가 하나라도 연결되면 FAIL 이다.
     */
    private void egress() {
        var targets = hooks.find("/__test/egress-probe").orElseThrow(() -> new TestAbortedException("EGRESS_TARGETS_UNAVAILABLE"));
        require(targets.isArray(), "F2_EGRESS_TARGETS_INVALID");
        var allowed = new ArrayList<String>(); var blocked = new ArrayList<String>();
        for (var target : targets) {
            String id = target.path("id").asText(), expect = target.path("expect").asText();
            require(target.size() == 2 && id.matches("[A-Za-z0-9_.-]{1,64}") && Set.of("ALLOWED", "BLOCKED").contains(expect), "F2_EGRESS_TARGETS_INVALID");
            (expect.equals("ALLOWED") ? allowed : blocked).add(id);
        }
        if (blocked.isEmpty() || !allowed.containsAll(EGRESS_POSITIVE_CONTROLS)) throw new TestAbortedException("EGRESS_TARGETS_INCOMPLETE");
        var probe = new EgressProbeClient(hooks);
        for (String id : allowed) { var outcome = probe.probe(id); require(outcome.targetId().equals(id), "F2_EGRESS_TARGET_MISMATCH"); if (!outcome.connected()) throw new TestAbortedException("EGRESS_POSITIVE_CONTROL_FAILED"); }
        for (String id : blocked) { var outcome = probe.probe(id); require(outcome.targetId().equals(id), "F2_EGRESS_TARGET_MISMATCH"); require(!outcome.connected(), "F2_EGRESS_BLOCKED_CONNECTED"); }
    }

    // ── 공통 ─────────────────────────────────────────────────────────────────────────────────────
    private Job scripted(String fixture, String question, Map<String,Object> fields, List<ObjectNode> script, List<String> extraSentinels) {
        String sentinel = "PW" + letters(10);
        var scenario = CanonicalJson.MAPPER.createObjectNode().put("taskType", "GENERAL_QUERY");
        ArrayNode replies = scenario.putArray("llmScript"); script.forEach(replies::add);
        var sentinels = scenario.putObject("promptWatch").putArray("sentinels"); sentinels.add(sentinel); extraSentinels.forEach(sentinels::add);
        return submit(fixture, sentinel + " " + question, fields, scenario, STUB_TIMEOUT, sentinel);
    }
    private Job submit(String fixture, String question, Map<String,Object> fields, ObjectNode scenario, Duration timeout) { return submit(fixture, question, fields, scenario, timeout, null); }
    private Job submit(String fixture, String question, Map<String,Object> fields, ObjectNode scenario, Duration timeout, String sentinel) {
        String request = fixture + "-" + UUID.randomUUID(); String key = GatewayClient.idempotencyKey(request);
        if (scenario != null) hooks.arm(F2Fixture.SUB, key, scenario);
        var body = new LinkedHashMap<String,Object>(); body.put("query", question); body.putAll(fields);
        var admitted = gateway.submitObserved("/v1/query", request, F2Fixture.claims(), gateway.newDlg(), json(body)).response();
        require(admitted.statusCode() == 202, "F2_JOB_NOT_ACCEPTED");
        String id = parse(admitted.body()).path("jobId").asText(); require(id.matches("[A-Za-z0-9_:-]{1,160}"), "F2_JOB_ID_INVALID");
        long deadline = System.nanoTime() + timeout.toNanos();
        do {
            var response = gateway.get("/v1/analyses/" + id, F2Fixture.claims()); require(response.statusCode() == 200, "F2_JOB_NOT_READABLE");
            var result = parse(response.body());
            if (!Set.of("QUEUED", "RUNNING").contains(result.path("jobStatus").asText())) {
                require(result.path("jobId").asText().equals(id) && result.hasNonNull("result"), "F2_RESULT_MISSING"); require(oracle.resultRowCount(id) == 1, "F2_RESULT_NOT_UNIQUE");
                return new Job(id, key, sentinel, result);
            }
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException("F2_WAIT_INTERRUPTED"); }
        } while (System.nanoTime() < deadline);
        throw new AssertionError("F2_JOB_TIMEOUT");
    }
    /** 무장 Job 의 호출 유형별 sentinel 포함 호출 수. 결속 Job 의 관찰이 아직 없으면(404) 0 이다. */
    int watch(Job job, String callType, int sentinelIndex) {
        if (job.sentinel() == null) throw new IllegalStateException("F2_WATCH_NOT_ARMED");
        var rows = hooks.find("/__test/prompt-watch/" + job.requestKey());
        if (rows.isEmpty()) return 0;
        int found = 0;
        for (var row : rows.get()) if (row.path("callType").asText().equals(callType) && row.path("sentinelIndex").asInt() == sentinelIndex) found = row.path("matchCount").asInt();
        return found;
    }
    private long calls(String operation) { return hooks.get("/__fixture/calls").path(operation).asLong(); }
    private long businessReads() { var calls = hooks.get("/__fixture/calls"); return BUSINESS_READS.stream().mapToLong(operation -> calls.path(operation).asLong()).sum(); }
    private Map<String,Object> window(Duration length, boolean resource) {
        Instant now = hooks.clockNow().truncatedTo(ChronoUnit.SECONDS);
        var body = new LinkedHashMap<String,Object>(context(now, now.minus(length), now));
        if (resource) body.put("resourceRefs", List.of(Map.of("type", "EQP", "ref", "EQP-001")));
        return body;
    }
    private static Map<String,Object> context(Instant reference, Instant from, Instant to) {
        return Map.of("context", Map.of("timezone", "UTC", "referenceTime", reference.toString(), "timeWindow", Map.of("from", from.toString(), "to", to.toString())));
    }
    private static JsonNode payload(Job job) { return job.response().path("result").path("payload"); }
    private static String status(Job job) { return job.response().path("result").path("status").asText(); }
    private static Optional<String> errorCode(Job job) { var value = job.response().path("result").path("errorCode"); return value.isTextual() ? Optional.of(value.asText()) : Optional.empty(); }
    private static void failed(Job job, String code) {
        require(job.response().path("jobStatus").asText().equals("FAILED") && status(job).equals("FAILED") && errorCode(job).equals(Optional.of(code)), "F2_EXPECTED_FAILED_" + code);
    }
    private static void clarification(Job job, String reason, List<String> fields) {
        var value = payload(job).path("clarification"); var actual = new ArrayList<String>(); value.path("fields").forEach(field -> actual.add(field.asText()));
        require(value.path("reason").asText().equals(reason) && actual.equals(fields), "F2_CLARIFICATION_" + reason);
    }
    static String wire(String t, String eqp, List<String> needs, String answerMode) {
        String task=switch(t){case "LQ"->"LIVE_QUERY";case "INC"->"INCIDENT_ANALYSIS";case "KQ"->"KNOWLEDGE_QUERY";case "GQ"->"GENERAL_QUERY";case "UNS"->"UNSUPPORTED_REQUEST";default->t;};
        var node=CanonicalJson.MAPPER.createObjectNode().put("taskType",task).put("searchQuery",eqp==null?"질문":eqp).put("retryQuery",eqp==null?"질문 보완":eqp+" 보완");node.putNull("timeWindow");var calls=node.putArray("toolCalls");var targets=node.putArray("targets");
        if(eqp!=null){targets.addObject().put("type","EQP").put("ref",eqp);if(t.equals("LQ")||t.equals("INC"))calls.addObject().put("toolId","tc.eqp.getOperationalSnapshot").set("arguments",CanonicalJson.MAPPER.valueToTree(Map.of("eqp",eqp)));}
        return node.toString();
    }
    static ObjectNode classify(String content) { return reply("CLASSIFY", content, "stop", 40); }
    static ObjectNode compose(String text) { return reply("COMPOSER", text, "stop", 30); }
    static ObjectNode reply(String callType, String content, String finishReason, int completionTokens) {
        var node = CanonicalJson.MAPPER.createObjectNode().put("callType", callType).put("content", content).put("finishReason", finishReason);
        node.putObject("usage").put("completionTokens", completionTokens); return node;
    }
    static String letters(int count) { var value = new StringBuilder(); for (int i = 0; i < count; i++) value.append((char) ('A' + RANDOM.nextInt(26))); return value.toString(); }
    @SuppressWarnings("unchecked") private static Map<String,Object> castMap(Object value) { return (Map<String,Object>) value; }
}
