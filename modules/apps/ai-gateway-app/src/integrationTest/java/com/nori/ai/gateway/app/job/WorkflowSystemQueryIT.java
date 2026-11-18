package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.hooks.*;
import com.nori.ai.gateway.core.job.JobContext;
import com.nori.ai.gateway.core.registry.TaskType;
import com.nori.ai.gateway.persistence.repository.*;
import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.net.http.*;
import static org.assertj.core.api.Assertions.*;

class WorkflowSystemQueryIT extends P09QueueSupport {
    @Override protected Class<?>[] extraSources(){return new Class<?>[]{HarnessObservationsEndpoint.class,WorkflowSourceFixture.class};}
    @Override protected com.nori.ai.gateway.core.scope.VerifiedSystemScope scope(String actor) {
        var original=super.scope(actor);return new com.nori.ai.gateway.core.scope.VerifiedSystemScope(original.boundSystem(),original.authenticatedServiceRef(),original.actorRef(),original.sub(),Set.of("EQP_READ","COMM_LOG_READ","MODEL_READ","DLQ_READ","AI_LOG_READ","AI_TRACE_READ","AI_CODE_READ","AI_DOC_READ"),original.pdg(),original.issuedAt(),original.expiresAt(),original.requestId());
    }
    @Override protected Map<String,Object> claims(String actor,String delegation){var values=new HashMap<>(super.claims(actor,delegation));values.put("perm",scope(actor).perm().stream().sorted().toList());return values;}
    protected JobAdmissionService.Accepted admit(TaskType task,String key,String conversation,Map<String,Object> fields) {
        hooks().arm("actor",key,new TestHookRegistry.Scenario(task,Set.of(),null,false,java.util.Optional.empty(),java.util.List.of(),java.util.Optional.empty(),com.nori.ai.gateway.app.job.StandardSystemConfiguration.calls(task)));var scope=scope("actor");String delegation=registeredDlg("actor");
        return inRequestSpan(()->context.getBean(JobAdmissionService.class).accept(scope,new JobAdmissionService.AdmissionRequest("/v1/query",conversation,"요청 "+key,fields,key,delegation)));
    }
    protected Map<String,Object> fields(String eqp,String from,String to) {
        return Map.of("context",Map.of("timezone","UTC","referenceTime",NOW.toString(),"timeWindow",Map.of("from",from,"to",to)),"resourceRefs",List.of(Map.of("type","EQP","ref",eqp)));
    }
    protected tools.jackson.databind.JsonNode payload(String job){return json.readTree(jdbc().sql("SELECT payload::text FROM gw.result WHERE job_id=:id").param("id",job).query(String.class).single());}
    @Test void validPhoneSubjectIsPreservedAcrossTheActualBusinessToolAndAllStoredIdentities() throws Exception {
        String actor="01012345678",key="phone-subject";hooks().arm(actor,key,new TestHookRegistry.Scenario(TaskType.LIVE_QUERY,Set.of(),null,false,java.util.Optional.empty(),java.util.List.of(),java.util.Optional.empty(),com.nori.ai.gateway.app.job.StandardSystemConfiguration.calls(TaskType.LIVE_QUERY)));
        var accepted=inRequestSpan(()->context.getBean(JobAdmissionService.class).accept(scope(actor),new JobAdmissionService.AdmissionRequest("/v1/query",null,"연락 token=local-private user@example.com SENTINEL:input",workflowFields(),key,registeredDlg(actor))));await(()->state(accepted.jobId()).equals("SUCCEEDED"));
        var result=payload(accepted.jobId());assertThat(result.path("evidence").get(0).path("actorAccessScope").path("value").asString()).isEqualTo(actor);
        String raw=jdbc().sql("SELECT payload::text FROM gw.tool_execution WHERE job_id=:job").param("job",accepted.jobId()).query(String.class).single();assertThat(json.readTree(raw).path("result").path("evidence").get(0).path("actorAccessScope").path("value").asString()).isEqualTo(actor);
        String stored=jdbc().sql("SELECT payload::text FROM gw.evidence WHERE job_id=:job").param("job",accepted.jobId()).query(String.class).single();assertThat(json.readTree(stored).path("actorAccessScope").path("value").asString()).isEqualTo(actor);assertThat(result.toString()).doesNotContain("local-private","user@example.com","SENTINEL:input");
    }
    @Test void actualLogAndTraceCacheHitsUseCurrentPdgAndRebindEvidenceToTheSecondJob() throws Exception {
        var server=context.getBean(WorkflowSourceFixture.Server.class);
        var first=admit(TaskType.INCIDENT_ANALYSIS,"cache-first",null,workflowFields());await(()->state(first.jobId()).equals("SUCCEEDED"));
        String saved=payload(first.jobId()).toString();var firstIds=jdbc().sql("SELECT id FROM gw.evidence WHERE job_id=:job ORDER BY id").param("job",first.jobId()).query(String.class).list();
        assertThat(server.logs).hasValue(4);assertThat(server.traces).hasValue(3);
        assertThat(jdbc().sql("SELECT count(*) FROM gw.cache_entry WHERE conversation_id=:conversation").param("conversation",first.conversationId()).query(Integer.class).single()).isEqualTo(2);
        var pdgs=jdbc().sql("SELECT DISTINCT pdg FROM gw.cache_entry WHERE conversation_id=:conversation").param("conversation",first.conversationId()).query(String.class).list();assertThat(pdgs).containsExactly(P09TestPorts.PDG).allSatisfy(value->assertThat(value).matches("[0-9a-f]{64}"));
        calls().reset();var second=admit(TaskType.INCIDENT_ANALYSIS,"cache-second",first.conversationId(),workflowFields());await(()->state(second.jobId()).equals("SUCCEEDED"));
        assertThat(server.logs).hasValue(4);assertThat(server.traces).hasValue(3);
        assertThat(jdbc().sql("SELECT capability_id FROM gw.tool_execution WHERE job_id=:job").param("job",second.jobId()).query(String.class).list()).containsExactly("tc.eqp.getOperationalSnapshot");
        var secondIds=jdbc().sql("SELECT id FROM gw.evidence WHERE job_id=:job ORDER BY id").param("job",second.jobId()).query(String.class).list();assertThat(secondIds).hasSameSizeAs(firstIds).doesNotContainAnyElementsOf(firstIds);
        assertThat(payload(first.jobId()).toString()).isEqualTo(saved);assertThat(payload(second.jobId()).path("evidence")).hasSize(secondIds.size());
        for(var accepted:List.of(first,second)) {
            var expected=payload(accepted.jobId());
            var request=HttpRequest.newBuilder(uri("/v1/analyses/"+accepted.jobId())).header("Authorization","Bearer "+serviceToken).header("X-Actor-Assertion",signer.sign(claims("actor",null),clock().instant())).GET().build();
            var analysis=HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());assertThat(analysis.statusCode()).isEqualTo(200);assertThat(json.readTree(analysis.body()).path("result").path("payload")).isEqualTo(expected);
            for(var block:expected.path("blocks")) {
                var export=HttpRequest.newBuilder(uri("/v1/analyses/"+accepted.jobId()+"/blocks/"+block.path("id").asString()+"/export")).header("Authorization","Bearer "+serviceToken).header("X-Actor-Assertion",signer.sign(claims("actor",null),clock().instant())).GET().build();
                var exported=HttpClient.newHttpClient().send(export,HttpResponse.BodyHandlers.ofString());assertThat(exported.statusCode()).isEqualTo(200);assertThat(json.readTree(exported.body())).isEqualTo(block);
            }
        }
    }
    @Test void oneDlqQueryPreservesTheSelectedTargetAndNanosecondWindowThroughStorage()throws Exception {
        var fields=workflowFields();String key="dlq-exact";hooks().arm("actor",key,new TestHookRegistry.Scenario(TaskType.LIVE_QUERY,Set.of(),null,false,Optional.empty(),List.of(),Optional.empty(),List.of(Map.of("toolId","tc.dlq.list","arguments",Map.of("eqp","EQP-001")))));
        var base=scope("actor");var scope=new com.nori.ai.gateway.core.scope.VerifiedSystemScope(base.boundSystem(),base.authenticatedServiceRef(),base.actorRef(),base.sub(),Set.of("DLQ_READ"),base.pdg(),base.issuedAt(),base.expiresAt(),base.requestId());
        var accepted=inRequestSpan(()->context.getBean(JobAdmissionService.class).accept(scope,new JobAdmissionService.AdmissionRequest("/v1/query",null,"DLQ 조회",fields,key,registeredDlg("actor"))));await(()->state(accepted.jobId()).equals("SUCCEEDED"));
        assertThat(calls().requests).hasSize(1);var sent=calls().requests.getFirst().body();assertThat(sent.path("toolId").asString()).isEqualTo("tc.dlq.list");assertThat(sent.path("arguments").path("eqp").asString()).isEqualTo("EQP-001");assertThat(sent.path("arguments").path("from").asString()).isEqualTo("2026-11-05T23:00:00.123456789Z");assertThat(sent.path("arguments").path("to").asString()).isEqualTo("2026-11-05T23:30:00.987654321Z");assertThat(jdbc().sql("SELECT count(*) FROM gw.tool_execution WHERE job_id=:job").param("job",accepted.jobId()).query(Integer.class).single()).isOne();assertThat(payload(accepted.jobId()).path("blocks")).hasSize(1);
    }
    @Test void actualBusinessProducerRecordsToolEvidenceFinalIdsContextAndAssistantInOneTerminalPath() throws Exception {
        var accepted=admit(TaskType.LIVE_QUERY,"actual-live",null,fields("EQP-001","2026-11-05T23:00:00.123456789Z","2026-11-05T23:30:00.987654321Z"));await(()->state(accepted.jobId()).equals("SUCCEEDED"));
        assertThat(calls().count("getEqpSnapshot")).isOne();
        var result=payload(accepted.jobId());assertThat(result.path("blocks").get(0).path("id").asString()).isEqualTo("b0");assertThat(result.has("inputQuestion")).isFalse();
        String tool=jdbc().sql("SELECT id FROM gw.tool_execution WHERE job_id=:job").param("job",accepted.jobId()).query(String.class).single();assertThat(tool).isNotBlank();
        var scope=observations().workflows.getLast().scope();var evidence=context.getBean(EvidenceRepository.class);String id=jdbc().sql("SELECT id FROM gw.evidence WHERE job_id=:job").param("job",accepted.jobId()).query(String.class).single();assertThat(evidence.find(scope,accepted.jobId(),id)).isPresent();
        var stored=context.getBean(ConversationRepository.class);assertThat(json.readTree(stored.findContext(scope,accepted.conversationId()).orElseThrow()).path("requestMessageId").asString()).isEqualTo(accepted.messageId());
        assertThat(json.readTree(stored.findSummary(scope,accepted.conversationId()).orElseThrow()).path("jobSeq").asLong()).isEqualTo(observations().workflows.getLast().requestInput().jobSeq());
        var history=context.getBean(MessageRepository.class).findByConversation(scope,accepted.conversationId());assertThat(history.stream().filter(m->"assistant".equals(m.role()))).singleElement().satisfies(message->{assertThat(message.contentMasked()).isEmpty();assertThat(message.responseRef()).isEqualTo(accepted.jobId());assertThat(message.toolExecutionRefs()).contains(tool);});
        var builder=HttpRequest.newBuilder(uri("/v1/analyses/"+accepted.jobId()+"/blocks/b0/export")).header("Authorization","Bearer "+serviceToken).header("X-Actor-Assertion",signer.sign(claims("actor",null),clock().instant())).GET().build();
        var exported=HttpClient.newHttpClient().send(builder,HttpResponse.BodyHandlers.ofString());assertThat(exported.statusCode()).isEqualTo(200);assertThat(json.readTree(exported.body())).isEqualTo(result.path("blocks").get(0));
        var historyRequest=HttpRequest.newBuilder(uri("/v1/conversations/"+accepted.conversationId())).header("Authorization","Bearer "+serviceToken).header("X-Actor-Assertion",signer.sign(claims("actor",null),clock().instant())).GET().build();
        var historyResponse=HttpClient.newHttpClient().send(historyRequest,HttpResponse.BodyHandlers.ofString());assertThat(historyResponse.statusCode()).isEqualTo(200);var historyBody=json.readTree(historyResponse.body());
        assertThat(java.util.stream.StreamSupport.stream(historyBody.path("messages").spliterator(),false).filter(message->message.path("role").asString().equals("assistant"))).singleElement().satisfies(message->assertThat(message.path("content").path("result").path("payload")).isEqualTo(result));
    }
    @Test void falseMandatoryToolStopsBeforeTimeResourceAuthorizationOrCacheWhileFalseGeneralHelpSucceeds() throws Exception {
        for(TaskType task:List.of(TaskType.LIVE_QUERY,TaskType.INCIDENT_ANALYSIS)) {
            calls().reset();var accepted=admit(task,"blocked-"+task,null,Map.of("options",Map.of("allowTools",false,"allowKnowledge",true)));await(()->state(accepted.jobId()).equals("FAILED"));
            var result=payload(accepted.jobId());assertThat(result.properties().stream().map(Map.Entry::getKey)).containsExactly("clarification");assertThat(result.path("clarification").path("reason").asString()).isEqualTo("PLAN_INVALID");
            assertThat(calls().count("resolveResource")).isZero();assertThat(calls().count("getEqpSnapshot")).isZero();
            assertThat(jdbc().sql("SELECT count(*) FROM gw.tool_execution WHERE job_id=:id").param("id",accepted.jobId()).query(Integer.class).single()).isZero();
        }
        calls().reset();var help=admit(TaskType.GENERAL_QUERY,"help-false",null,Map.of("options",Map.of("allowTools",false,"allowKnowledge",false)));await(()->state(help.jobId()).equals("SUCCEEDED"));assertThat(payload(help.jobId()).path("blocks").get(0).path("type").asString()).isEqualTo("table");assertThat(count("cache_entry")).isZero();
    }
    @Test void exactRequestWindowsStayOnOwnMessagesAndImmediateNullPreviousDoesNotUseOlderWindow() throws Exception {
        var a=admit(TaskType.INCIDENT_ANALYSIS,"window-a",null,fields("EQP-001","2026-11-05T22:00:00.123456789Z","2026-11-05T22:30:00.987654321Z"));await(()->state(a.jobId()).equals("SUCCEEDED"));
        var b=admit(TaskType.GENERAL_QUERY,"window-b",a.conversationId(),Map.of());await(()->state(b.jobId()).equals("SUCCEEDED"));calls().reset();
        var c=admit(TaskType.INCIDENT_ANALYSIS,"window-c",a.conversationId(),Map.of("context",Map.of("timezone","UTC","referenceTime",NOW.toString()),"resourceRefs",List.of(Map.of("type","EQP","ref","EQP-001"))));await(()->state(c.jobId()).equals("FAILED"));
        assertThat(payload(c.jobId()).path("clarification").path("reason").asString()).isEqualTo("TIME_WINDOW_REQUIRED");
        var scope=observations().workflows.getLast().scope();var messages=context.getBean(MessageRepository.class).findByConversation(scope,a.conversationId());assertThat(messages.stream().filter(m->m.id().equals(a.messageId())).findFirst().orElseThrow().resolvedTimeWindow()).contains("123456789");assertThat(messages.stream().filter(m->m.id().equals(b.messageId())||m.id().equals(c.messageId())).map(MessageRepository.Message::resolvedTimeWindow)).containsOnlyNulls();
        var d=admit(TaskType.INCIDENT_ANALYSIS,"window-d",a.conversationId(),fields("EQP-001","2026-11-05T23:00:00.000000001Z","2026-11-05T23:30:00.999999999Z"));await(()->state(d.jobId()).equals("SUCCEEDED"));
        var inputs=context.getBean(JobRepository.class);assertThat(inputs.findRequestInput(scope,a.jobId()).orElseThrow().messageId()).isEqualTo(a.messageId());assertThat(inputs.findRequestInput(scope,d.jobId()).orElseThrow().jobSeq()).isGreaterThan(inputs.findRequestInput(scope,a.jobId()).orElseThrow().jobSeq());
    }
    @Test void precedingExactConfirmedWindowIsUsedWithoutAConversationLatestContextInput() throws Exception {
        var a=admit(TaskType.INCIDENT_ANALYSIS,"previous-a",null,fields("EQP-001","2026-11-05T22:00:00.123456789Z","2026-11-05T22:30:00.987654321Z"));await(()->state(a.jobId()).equals("SUCCEEDED"));
        context.getBean(ConversationRepository.class).saveContext(observations().workflows.getLast().scope(),a.conversationId(),"{\"timeWindow\":{\"from\":\"1900-01-01T00:00Z\",\"to\":\"1900-01-01T01:00Z\"}}");
        var b=admit(TaskType.INCIDENT_ANALYSIS,"previous-b",a.conversationId(),Map.of("context",Map.of("timezone","UTC","referenceTime",NOW.toString()),"resourceRefs",List.of(Map.of("type","EQP","ref","EQP-001"))));await(()->state(b.jobId()).equals("SUCCEEDED"));
        String window=jdbc().sql("SELECT resolved_time_window::text FROM gw.message WHERE id=:message").param("message",b.messageId()).query(String.class).single();assertThat(window).contains("2026-11-05T22:00:00.123456789Z").doesNotContain("1900");
    }

}
