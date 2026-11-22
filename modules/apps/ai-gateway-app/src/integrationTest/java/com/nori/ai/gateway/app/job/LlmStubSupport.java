package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.hooks.LlmScriptRegistry;
import com.nori.ai.gateway.app.hooks.TestHookRegistry;
import com.nori.ai.gateway.app.inference.OllamaEndpointStub;
import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.registry.TaskType;
import com.nori.ai.gateway.integration.inference.InferenceReadinessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

/**
 * F2 LLM_STUB 통합 시험 공통(Plan P20). 추론 모드 enabled·verify hook 활성에서 실제 ClassificationService·ComposerService·SpringAiOpenAiInferenceClient
 * (예산 assert·잘림 감지) 경로를 쓰고, 무장 Job 은 ScriptedChatTransport 고정 응답, 결속 없는 Job 은 로컬 Ollama endpoint 대역으로 간다.
 * 실제 추론 서버·공유 서버 호출은 0이다.
 */
@Tag("stage-F2") @Tag("label-LLM_STUB")
abstract class LlmStubSupport extends P09QueueSupport {
    static final Set<String> PERMS = Set.of("EQP_READ","COMM_LOG_READ","MODEL_READ","DLQ_READ","AI_LOG_READ","AI_TRACE_READ","AI_CODE_READ","AI_DOC_READ");
    protected static final String OBSERVED_TEXT="EQP-001 설비 상태 장애 원인 로그 모델 공정 적용 조건 정의 관측 자료";
    protected final OllamaEndpointStub stub = new OllamaEndpointStub();
    protected Set<String> perms = PERMS;

    @Override protected Map<String,Object> extraApplicationProperties() {
        var properties = new HashMap<String,Object>(super.extraApplicationProperties());
        properties.putAll(Map.of("ai.gateway.inference.mode", "enabled", "ai.gateway.llm.baseUrl", stub.baseUrl() + "/v1"));
        return properties;
    }
    @Override protected Class<?>[] extraSources() { return new Class<?>[]{HarnessObservationsEndpoint.class, WorkflowSourceFixture.class}; }
    @Override protected com.nori.ai.gateway.core.scope.VerifiedSystemScope scope(String actor) {
        var original = super.scope(actor);
        return new com.nori.ai.gateway.core.scope.VerifiedSystemScope(original.boundSystem(), original.authenticatedServiceRef(), original.actorRef(), original.sub(), perms,
            original.pdg(), original.issuedAt(), original.expiresAt(), original.requestId());
    }
    @Override protected Map<String,Object> claims(String actor, String delegation) { var values = new HashMap<>(super.claims(actor, delegation)); values.put("perm", perms.stream().sorted().toList()); return values; }
    @BeforeEach void inferenceReady() throws Exception {
        await(() -> context.getBean(InferenceReadinessService.class).ready());context.getBean(InferenceReadinessService.class).stop();stub.clear();
        // 작성 시험은 실제 HTTP 본문이 충분한 고정 자료를 사용합니다. 위치(ref)를 본문으로 대신하지 않습니다.
        context.getBean(com.nori.ai.gateway.integration.fixtures.StandardSystemStub.class).evidenceText=OBSERVED_TEXT;
    }
    @AfterEach void closeStub() { stub.close(); }

    protected LlmScriptRegistry scripts() { return context.getBean(LlmScriptRegistry.class); }
    protected static TestHookRegistry.LlmReply classify(String wire) { return new TestHookRegistry.LlmReply(CallType.CLASSIFY, wire, "stop", null, Math.max(40, answerTokens(wire))); }
    protected static TestHookRegistry.LlmReply compose(String text) {
        String content=composeWire(text);
        return new TestHookRegistry.LlmReply(CallType.COMPOSER,content,"stop",null,Math.max(30,answerTokens(content)));
    }
    /** 성공 fixture가 실제 제공된 후보 ID를 명시합니다. 무효 출력의 자동 보정에는 사용하지 않습니다. */
    protected static TestHookRegistry.LlmReply composeSelection(String... ids) {
        return composeSelection(Arrays.stream(ids).mapToInt(id->Integer.parseInt(id.substring(1))).max().orElse(0),ids);
    }
    protected static TestHookRegistry.LlmReply composeSelection(int count,String... ids) {
        var decisions=SchemaContracts.JSON.createObjectNode();for(int i=1;i<=count;i++)decisions.put("C"+i,false);
        for(String id:ids)decisions.put(id,true);
        String content=decisions.toString();
        return new TestHookRegistry.LlmReply(CallType.COMPOSER,content,"stop",null,Math.max(30,answerTokens(content)));
    }
    protected static String composeWire(String text) {
        var matcher=java.util.regex.Pattern.compile("\\[e([0-9]+)]").matcher(text);var citations=new ArrayList<Integer>();
        while(matcher.find())citations.add(Integer.parseInt(matcher.group(1)));
        if(citations.isEmpty()||text.contains("<think>")||text.contains("</think>"))return text;
        return SchemaContracts.JSON.writeValueAsString(Map.of("sentences",List.of(Map.of("text",text.replaceAll("\\[e[0-9]+]",""),"citations",citations))));
    }
    /**
     * 분류 wire v3 고정 fixture(SPEC §28.3): {@code n} 은 기존 고정 workflow 경로를 그대로 쓰는 근거 — KQ=[N], LQ=[S], INC=[S,H,I](선택 git 단계 포함),
     * GQ·UNS=[] — 이고 {@code a}=B 다. 다른 근거·표현 길이는 {@link #wire(String,String,String,String,String,java.util.List,String)} 로 명시한다.
     */
    protected static String wire(String t, String eqp, String from, String to, String qRef) {
        var needs = switch (t) { case "KQ" -> List.of("N"); case "LQ" -> List.of("S"); case "INC" -> List.of("S", "H", "I"); default -> List.<String>of(); };
        return wire(t, eqp, from, to, qRef, needs, "B");
    }
    protected static String wire(String t, String eqp, String from, String to, String qRef, List<String> n, String a) {
        var task=switch(t){case "KQ"->TaskType.KNOWLEDGE_QUERY;case "LQ"->TaskType.LIVE_QUERY;case "INC"->TaskType.INCIDENT_ANALYSIS;case "GQ"->TaskType.GENERAL_QUERY;case "UNS"->TaskType.UNSUPPORTED_REQUEST;default->throw new IllegalArgumentException();};
        var node=SchemaContracts.JSON.createObjectNode().put("taskType",task.name()).put("searchQuery",eqp==null?"질문":eqp).put("retryQuery",eqp==null?"질문 보완":eqp+" 보완");
        var calls=StandardSystemConfiguration.calls(task);node.set("toolCalls",SchemaContracts.JSON.valueToTree(calls));var targets=node.putArray("targets");if(eqp!=null)targets.addObject().put("type","EQP").put("ref",eqp);
        if(from!=null&&to!=null)node.set("timeWindow",SchemaContracts.JSON.valueToTree(Map.of("from",from,"to",to)));else node.putNull("timeWindow");
        return node.toString();
    }
    /** 질문 시간창(Context) 과 화면 선택 리소스 없는 요청 필드. 설비는 분류 출력으로만 들어온다. */
    protected Map<String,Object> windowOnly() {
        return Map.of("context", Map.of("timezone", "UTC", "referenceTime", NOW.toString(), "timeWindow", Map.of("from", "2026-11-05T23:00:00Z", "to", "2026-11-05T23:30:00Z")));
    }
    protected JobAdmissionService.Accepted admitScripted(String key, String question, Map<String,Object> fields, List<TestHookRegistry.LlmReply> script,
            Optional<TestHookRegistry.PromptWatch> watch, String conversation) {
        var boundScript=new ArrayList<TestHookRegistry.LlmReply>();
        for(var reply:script){String content=reply.content();if(reply.callType()==CallType.CLASSIFY&&content!=null)try{var node=SchemaContracts.parse(content);if(node.isObject()&&node.has("searchQuery")&&node.path("searchQuery").asString().equals("질문")&&question.codePointCount(0,question.length())<=512){var copy=(tools.jackson.databind.node.ObjectNode)node;copy.put("searchQuery",question);content=copy.toString();}}catch(RuntimeException invalid){/* 무효 출력은 그대로 제품 분류기에 전달합니다. */}int completion=reply.completionTokens();if(!Objects.equals(content,reply.content())&&completion>=answerTokens(reply.content()))completion=Math.max(completion,answerTokens(content));boundScript.add(new TestHookRegistry.LlmReply(reply.callType(),content,reply.finishReason(),reply.promptTokens(),completion));}
        hooks().arm("actor", key, new TestHookRegistry.Scenario(TaskType.GENERAL_QUERY,Set.of(),null,false,Optional.empty(),boundScript,watch,com.nori.ai.gateway.app.job.StandardSystemConfiguration.calls(TaskType.GENERAL_QUERY)));
        return admitUnarmed(key, question, fields, conversation);
    }
    protected JobAdmissionService.Accepted admitUnarmed(String key, String question, Map<String,Object> fields, String conversation) {
        String delegation = registeredDlg("actor");
        return inRequestSpan(() -> context.getBean(JobAdmissionService.class).accept(scope("actor"), new JobAdmissionService.AdmissionRequest("/v1/query", conversation, question, fields, key, delegation)));
    }
    protected String terminal(JobAdmissionService.Accepted job) throws Exception {
        await(() -> !Set.of("QUEUED", "RUNNING").contains(state(job.jobId())));
        return state(job.jobId());
    }
    protected tools.jackson.databind.JsonNode payload(String job) { return json.readTree(jdbc().sql("SELECT payload::text FROM gw.result WHERE job_id=:id").param("id", job).query(String.class).single()); }
    protected Map<String,Object> result(String job) { return jdbc().sql("SELECT status,error_code FROM gw.result WHERE job_id=:id").param("id", job).query().singleRow(); }
    protected int tools(String job) { return jdbc().sql("SELECT count(*) FROM gw.tool_execution WHERE job_id=:id").param("id", job).query(Integer.class).single(); }
    protected void assertClarification(String job, String reason, List<String> fields) {
        var value = payload(job);
        assertThat(value.properties().stream().map(Map.Entry::getKey)).containsExactly("clarification");
        assertThat(value.path("clarification").path("reason").asString()).isEqualTo(reason);
        var actual = new ArrayList<String>(); value.path("clarification").path("fields").forEach(field -> actual.add(field.asString()));
        assertThat(actual).containsExactlyElementsOf(fields);
    }
    protected static int answerTokens(String content) {
        try(var tokenizer=new com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter()) { return tokenizer.countText(content); }
    }
    /** 로컬 대역의 prompt 사용량은 실제 전송 목록의 계수와 같아야 합니다. 공개 본문보다 작은 completion을 만들지 않습니다. */
    protected static OllamaEndpointStub.Json chat(String content, int promptTokens) {
        var message = SchemaContracts.JSON.createObjectNode().put("role", "assistant").put("content", content);
        var body = SchemaContracts.JSON.createObjectNode().put("id", "chatcmpl-stub").put("object", "chat.completion").put("created", 1).put("model", "qwen3:4b");
        body.putArray("choices").addObject().put("index", 0).put("finish_reason", "stop").set("message", message);
        int completion=Math.max(20,answerTokens(content));
        body.putObject("usage").put("prompt_tokens", promptTokens).put("completion_tokens", completion).put("total_tokens", promptTokens + completion);
        return new OllamaEndpointStub.Json(200, body.toString());
    }
}
