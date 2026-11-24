package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.budget.CallType;
import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class AnswerSnapshotAccessIT extends LlmStubSupport {
    private java.net.http.HttpResponse<String> request(String method,String path,String actor)throws Exception {
        var request=java.net.http.HttpRequest.newBuilder(uri(path)).timeout(java.time.Duration.ofSeconds(15)).header("Authorization","Bearer "+serviceToken).header("X-Actor-Assertion",signer.sign(claims(actor,null),clock().instant())).method(method,java.net.http.HttpRequest.BodyPublishers.noBody()).build();
        try(var client=java.net.http.HttpClient.newHttpClient()){return client.send(request,java.net.http.HttpResponse.BodyHandlers.ofString());}
    }
    @Test void actualValidatedSnapshotAndCitationsSurviveDeletionWhileEveryOrdinaryPathBecomes404() throws Exception {
        var job=admitScripted("snapshot-access","EQP-001 상태",windowOnly(),List.of(classify(wire("LQ","EQP-001",null,null,null)),composeFacts(OBSERVED_TEXT,"e1")),Optional.empty(),null);
        assertThat(terminal(job)).isEqualTo("SUCCEEDED");assertThat(scripts().scriptedCalls(job.jobId(),CallType.COMPOSER)).isOne();
        assertThat(jdbc().sql("SELECT content_masked FROM gw.message WHERE conversation_id=:conversation AND role='assistant'").param("conversation",job.conversationId()).query(String.class).single()).isEqualTo(OBSERVED_TEXT+"[1]");
        var answer=payload(job.jobId()).path("answer");assertThat(answer.path("kind").asString()).isEqualTo("ANSWER");assertThat(answer.path("text").asString()).isEqualTo(OBSERVED_TEXT+"[1]");
        assertThat(answer.path("sentences")).isEqualTo(json.valueToTree(List.of(Map.of("text",OBSERVED_TEXT,"citations",List.of(1)))));String evidence=answer.path("citations").get(0).path("evidenceId").asString();
        var stored=jdbc().sql("SELECT id::text,answer::text,evidence::text,question_masked,config_sha256 FROM kn.answer_snapshot WHERE job_id=:job").param("job",job.jobId()).query().singleRow();String internal=(String)stored.get("id");
        assertThat(json.readTree((String)stored.get("answer"))).isEqualTo(answer);assertThat(stored.get("question_masked")).isEqualTo("EQP-001 상태");assertThat((String)stored.get("config_sha256")).matches("[0-9a-f]{64}");
        var units=json.readTree((String)stored.get("evidence"));assertThat(units).hasSize(1);String body=context.getBean(StandardSystemStub.class).evidenceText;
        assertThat(units.get(0).path("bodyMasked").asString()).isEqualTo(body);assertThat(units.get(0).path("bodySha256").asString()).isEqualTo(java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(body.getBytes(java.nio.charset.StandardCharsets.UTF_8))));
        assertThat(units.get(0).path("location").path("ref").asString()).isEqualTo("EQP-001");assertThat(units.get(0).path("permissions").toString()).isEqualTo("[\"EQP_READ\"]");
        String source="/v1/analyses/"+job.jobId()+"/evidence/"+evidence+"/source";
        var response=request("GET",source,"actor");assertThat(response.statusCode()).isEqualTo(200);assertThat(json.readTree(response.body()).path("maskedText").asString()).isEqualTo(body);
        for(String path:List.of("/v1/analyses/"+job.jobId(),"/v1/audit/jobs/"+job.jobId())){var publicResponse=request("GET",path,"actor");assertThat(publicResponse.statusCode()).isEqualTo(200);assertThat(publicResponse.body()).doesNotContain(internal);}
        assertThat(request("GET",source,"other").statusCode()).isEqualTo(404);
        perms=Set.of("AI_DOC_READ");var denied=request("GET",source,"actor");assertThat(denied.body()).contains("DENIED").doesNotContain(body,internal,evidence);perms=PERMS;
        String block=payload(job.jobId()).path("blocks").get(0).path("id").asString();
        assertThat(request("DELETE","/v1/conversations/"+job.conversationId(),"actor").statusCode()).isEqualTo(204);
        for(String path:List.of("/v1/conversations/"+job.conversationId(),"/v1/analyses/"+job.jobId(),"/v1/analyses/"+job.jobId()+"/events",source,"/v1/analyses/"+job.jobId()+"/blocks/"+block+"/export","/v1/audit/jobs/"+job.jobId())){var gone=request("GET",path,"actor");assertThat(gone.statusCode()).as(path).isEqualTo(404);assertThat(gone.body()).isEmpty();}
        assertThat(jdbc().sql("SELECT id::text FROM kn.answer_snapshot WHERE job_id=:job").param("job",job.jobId()).query(String.class).single()).isEqualTo(internal);
    }
}
