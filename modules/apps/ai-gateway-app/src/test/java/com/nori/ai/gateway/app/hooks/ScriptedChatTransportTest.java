package com.nori.ai.gateway.app.hooks;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.registry.TaskType;
import com.nori.ai.gateway.integration.inference.ChatTransport;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

class ScriptedChatTransportTest {
    @Test void schemaIgnoringMixedIdsAreNotSilentlyRepairedByTheOperationalTransport() {
        String content="{\"selectedIds\":[\"C1\",\"UNKNOWN\"]}";
        bind("mixed-ids",List.of(new TestHookRegistry.LlmReply(CallType.COMPOSER,content,"stop",null,20)),Optional.empty());
        var schema=new com.nori.ai.gateway.core.llm.StructuredOutput("{\"type\":\"object\",\"properties\":{\"selectedIds\":{\"type\":\"array\",\"items\":{\"enum\":[\"C1\"]}}},\"required\":[\"selectedIds\"],\"additionalProperties\":false}");
        try(var observed=scripts.open("mixed-ids",CallType.COMPOSER,MESSAGES)) {
            assertThat(transport.send(CallType.COMPOSER,MESSAGES,3072,java.time.Duration.ofSeconds(3),schema).content()).isEqualTo(content);
        }
    }
    static final class Movable extends Clock {
        Instant now = Instant.parse("2026-11-06T00:00:00Z");
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }
    private final Movable clock = new Movable();
    private final TestHookRegistry hooks = new TestHookRegistry(clock, new PausePointRegistry());
    private final LlmScriptRegistry scripts = new LlmScriptRegistry(hooks, clock);
    private final List<CallType> live = new ArrayList<>();
    private final ChatTransport liveTransport = (type, messages, maxTokens) -> { live.add(type); return new ChatTransport.Response("live", "stop", 1, 1); };
    private final ScriptedChatTransport transport = new ScriptedChatTransport(liveTransport, scripts, messages -> 777);
    private static final List<ChatMessage> MESSAGES = List.of(new ChatMessage(ChatMessage.Role.SYSTEM, "지시 SENTINEL-A"), new ChatMessage(ChatMessage.Role.USER, "질문"));

    private void bind(String job, List<TestHookRegistry.LlmReply> script, Optional<TestHookRegistry.PromptWatch> watch) {
        hooks.arm("actor", job, new TestHookRegistry.Scenario(TaskType.GENERAL_QUERY, Set.of(), null, false, Optional.empty(), script, watch));
        TransactionSynchronizationManager.initSynchronization(); TransactionSynchronizationManager.setActualTransactionActive(true);
        try { hooks.bindOnAdmission("actor", job, job); for (var sync : TransactionSynchronizationManager.getSynchronizations()) sync.afterCompletion(TransactionSynchronization.STATUS_COMMITTED); }
        finally { TransactionSynchronizationManager.clearSynchronization(); TransactionSynchronizationManager.setActualTransactionActive(false); }
    }
    private ChatTransport.Response call(String job, CallType type) {
        try (var scope = scripts.open(job, type, MESSAGES)) { return transport.send(type, MESSAGES, 5_120); }
    }

    @Test void structuredRequestsKeepScriptReplayAndForwardConstraintsUnchangedToLive() {
        var schema=new com.nori.ai.gateway.core.llm.StructuredOutput("{\"type\":\"object\"}");
        bind("structured",List.of(new TestHookRegistry.LlmReply(CallType.CLASSIFY,"{}","stop",null,1)),Optional.empty());
        try(var scope=scripts.open("structured",CallType.CLASSIFY,MESSAGES)) {
            assertThat(transport.send(CallType.CLASSIFY,MESSAGES,2048,java.time.Duration.ofSeconds(3),schema).content()).isEqualTo("{}");
        }
        var forwarded=new ArrayList<com.nori.ai.gateway.core.llm.StructuredOutput>();
        var live=new ChatTransport(){
            public Response send(CallType type,List<ChatMessage> messages,int max){throw new AssertionError();}
            public Response send(CallType type,List<ChatMessage> messages,int max,java.time.Duration timeout,com.nori.ai.gateway.core.llm.StructuredOutput output){forwarded.add(output);return new Response("{}","stop",777,1);}
        };
        var compatible=new ScriptedChatTransport(live,scripts,m->777);
        assertThat(compatible.send(CallType.CLASSIFY,MESSAGES,2048,java.time.Duration.ofSeconds(3),schema).content()).isEqualTo("{}");
        assertThat(forwarded).containsExactly(schema);
        assertThatThrownBy(()->liveTransport.send(CallType.CLASSIFY,MESSAGES,2048,java.time.Duration.ofSeconds(3),schema)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test void boundJobGetsItsScriptInOrderWhileUnboundAndUnarmedJobsGoToTheLiveTransport() {
        bind("scripted", List.of(new TestHookRegistry.LlmReply(CallType.CLASSIFY, "{\"t\":\"GQ\"}", "stop", null, 9),
            new TestHookRegistry.LlmReply(CallType.COMPOSER, "서술", "length", 12, 128)), Optional.empty());
        bind("armed-without-script", List.of(), Optional.empty());
        var classify = call("scripted", CallType.CLASSIFY);
        assertThat(classify).isEqualTo(new ChatTransport.Response("{\"t\":\"GQ\"}", "stop", 777, 9));
        assertThat(call("scripted", CallType.COMPOSER)).isEqualTo(new ChatTransport.Response("서술", "length", 12, 128));
        assertThat(call("unbound", CallType.CLASSIFY).content()).isEqualTo("live");
        assertThat(call("armed-without-script", CallType.COMPOSER).content()).isEqualTo("live");
        assertThat(transport.send(CallType.CLASSIFY, MESSAGES, 5_120).content()).as("결속 범위 밖 호출").isEqualTo("live");
        assertThat(live).containsExactly(CallType.CLASSIFY, CallType.COMPOSER, CallType.CLASSIFY);
        assertThat(scripts.scriptedCalls("scripted", CallType.CLASSIFY)).isOne();
        assertThat(scripts.scriptedCalls("scripted", CallType.COMPOSER)).isOne();
    }

    @Test void exhaustedOrWrongTypeScriptFailsTheCallInsteadOfReachingTheLiveModel() {
        bind("short", List.of(new TestHookRegistry.LlmReply(CallType.CLASSIFY, "x", "stop", null, 1)), Optional.empty());
        assertThatThrownBy(() -> call("short", CallType.COMPOSER)).isInstanceOf(IllegalStateException.class);
        call("short", CallType.CLASSIFY);
        assertThatThrownBy(() -> call("short", CallType.CLASSIFY)).isInstanceOf(IllegalStateException.class);
        assertThat(live).isEmpty();
        assertThatThrownBy(() -> new ScriptedChatTransport(transport, scripts, messages -> 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void promptWatchCountsOnlySentinelHitsPerCallTypeAndForgetsSixtySecondsAfterTerminal() {
        bind("watched", List.of(), Optional.of(new TestHookRegistry.PromptWatch(List.of("SENTINEL-A", "SENTINEL-B"))));
        call("watched", CallType.CLASSIFY); call("watched", CallType.CLASSIFY); call("watched", CallType.COMPOSER);
        var controller = new PromptWatchHook(hooks, scripts);
        var response = controller.watch("watched");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactlyInAnyOrder(
            java.util.Map.of("callType", "CLASSIFY", "sentinelIndex", 0, "matchCount", 2), java.util.Map.of("callType", "CLASSIFY", "sentinelIndex", 1, "matchCount", 0),
            java.util.Map.of("callType", "COMPOSER", "sentinelIndex", 0, "matchCount", 1), java.util.Map.of("callType", "COMPOSER", "sentinelIndex", 1, "matchCount", 0));
        assertThat(response.getBody().toString()).doesNotContain("지시", "질문");
        assertThat(controller.watch("unknown").getStatusCode().value()).isEqualTo(404);
        scripts.afterCommit("watched");
        clock.now = clock.now.plusSeconds(59);
        assertThat(controller.watch("watched").getStatusCode().value()).isEqualTo(200);
        clock.now = clock.now.plusSeconds(1);
        assertThat(controller.watch("watched").getStatusCode().value()).isEqualTo(404);
    }

    @Test void watchInputIsBoundedAndScriptUsageMustBeNonNegative() {
        assertThatThrownBy(() -> new TestHookRegistry.PromptWatch(List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TestHookRegistry.PromptWatch(java.util.Collections.nCopies(17, "s"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TestHookRegistry.PromptWatch(List.of("가".repeat(65)))).isInstanceOf(IllegalArgumentException.class);
        assertThat(new TestHookRegistry.PromptWatch(List.of("가".repeat(64))).sentinels()).hasSize(1);
        assertThatThrownBy(() -> new TestHookRegistry.LlmReply(CallType.CLASSIFY, "x", "stop", -1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new TestHookRegistry.LlmReply(CallType.CLASSIFY, "원문-SENTINEL", "stop", null, 0).toString()).doesNotContain("원문-SENTINEL");
    }
    @Test void fourOccurrencesInMultipleMessagesCountAsOneContainingComposerCall() {
        bind("four-units",List.of(),Optional.of(new TestHookRegistry.PromptWatch(List.of("SENTINEL-A","SENTINEL-B"))));
        var messages=List.of(new ChatMessage(ChatMessage.Role.SYSTEM,"지시"),new ChatMessage(ChatMessage.Role.USER,"SENTINEL-A\nSENTINEL-A"),new ChatMessage(ChatMessage.Role.USER,"SENTINEL-A\nSENTINEL-A"));
        try(var observed=scripts.open("four-units",CallType.COMPOSER,messages)){transport.send(CallType.COMPOSER,messages,5120);}
        assertThat(scripts.promptWatch("four-units").orElseThrow()).containsExactly(new LlmScriptRegistry.WatchRow(CallType.COMPOSER,0,1),new LlmScriptRegistry.WatchRow(CallType.COMPOSER,1,0));
    }
}
