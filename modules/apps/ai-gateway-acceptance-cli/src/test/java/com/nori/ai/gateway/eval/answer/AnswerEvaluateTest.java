package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.*;
import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.eval.answer.AnswerFixtures.*;

class AnswerEvaluateTest {
    @TempDir Path root;
    final ByteArrayOutputStream output=new ByteArrayOutputStream(),errors=new ByteArrayOutputStream();
    final String[] command={"answer-evaluate","--system","TC","--questions","questions.jsonl","--gateway","http://localhost:8080"};
    void set(boolean allCorrect) throws Exception {
        var yes=question("yes",true,allCorrect?"100":"재시도 없음");var no=question("no",false,"");no.put("question","거절 질문");
        Files.writeString(root.resolve("questions.jsonl"),Json.canonicalString(yes)+"\n"+Json.canonicalString(no)+"\n");
    }
    AnswerEvaluate.Inputs inputs(GatewayClientTest.Fixture f,List<String> granted,List<String> required,String review) {
        var identity=Json.MAPPER.createObjectNode().put("codeSha256",Json.sha("fixture-code".getBytes(StandardCharsets.UTF_8)))
            .put("generationModelSha256",Json.sha("fixture-generation".getBytes(StandardCharsets.UTF_8))).put("embeddingModelSha256",Json.sha("fixture-embedding".getBytes(StandardCharsets.UTF_8))).put("configSha256",Json.sha("fixture-config".getBytes(StandardCharsets.UTF_8)))
            .put("privateValue","PRIVATE_KEY_BODY_MUST_NOT_ESCAPE");
        return new AnswerEvaluate.Inputs((bucket,key)->document(),granted,required,review,f.client(),identity);
    }
    EvalCli cli(AnswerEvaluate.InputResolver resolver) {
        return new EvalCli(Map.of("AI_GATEWAY_EVAL_SERVICE_TOKEN_FILE","token-file","AI_GATEWAY_EVAL_SIGNER_FILE","signer-file"),
            new PrintStream(output,true,StandardCharsets.UTF_8),new PrintStream(errors,true,StandardCharsets.UTF_8),root,resolver);
    }
    @Test void operationalDispatcherAndRealHttpUseExitZeroOneAndTwoAndNoCredentialsInEitherStream() throws Exception {
        for(boolean correct:List.of(true,false))try(var f=new GatewayClientTest.Fixture()) {
            set(correct);output.reset();errors.reset();
            assertThat(cli((root,env,system,uri)->inputs(f,List.of("AI_DOC_READ","AI_REVIEW"),List.of("AI_DOC_READ"),"AI_REVIEW")).execute(command)).isEqualTo(correct?0:1);
            JsonNode report=Json.parse(output.toByteArray());assertThat(report.path("items")).hasSize(2);
            assertThat(report.path("systems").path("TC").path("passed").booleanValue()).isEqualTo(correct);
            assertThat(output.toString(StandardCharsets.UTF_8)+errors.toString(StandardCharsets.UTF_8)).doesNotContain("TOKEN_BODY_MUST_NOT_ESCAPE","PRIVATE_KEY_BODY_MUST_NOT_ESCAPE");
        }
        output.reset();errors.reset();set(true);
        assertThat(new EvalCli(Map.of(),new PrintStream(output),new PrintStream(errors),root).execute(command)).isEqualTo(2);
        assertThat(output.size()).isZero();assertThat(errors.toString(StandardCharsets.UTF_8)).contains("평가 환경");
    }
    @Test void everyConfiguredPermissionAndReviewPermissionIsRequiredBeforeReviewRequest() throws Exception {
        set(true);
        for(String missing:List.of("AI_DOC_READ","MODEL_READ","AI_REVIEW"))try(var f=new GatewayClientTest.Fixture()) {
            var all=List.of("AI_DOC_READ","MODEL_READ","AI_REVIEW");var granted=all.stream().filter(p->!p.equals(missing)).toList();
            assertThat(cli((root,env,system,uri)->inputs(f,granted,List.of("AI_DOC_READ","MODEL_READ"),"AI_REVIEW")).execute(command)).isEqualTo(2);
            assertThat(f.requests).isEmpty();
        }
    }
    @Test void hashesBindActualInitialBytesMergedQuestionsDocumentIdentitiesAndSuppliedMeasuredIdentities() throws Exception {
        set(true);JsonNode first;
        try(var f=new GatewayClientTest.Fixture()) {
            assertThat(cli((root,env,system,uri)->inputs(f,List.of("AI_DOC_READ","AI_REVIEW"),List.of(),"AI_REVIEW")).execute(command)).isZero();
            first=Json.parse(output.toByteArray()).path("hashes");
            assertThat(first.path("initialSetSha256").asText()).isEqualTo(Json.sha(Files.readAllBytes(root.resolve("questions.jsonl"))));
            assertThat(first.path("codeSha256").asText()).isEqualTo(Json.sha("fixture-code".getBytes(StandardCharsets.UTF_8)));
            var schema=new QuestionSet();var merged=schema.merge(schema.load(root.resolve("questions.jsonl")),List.of(),"TC");
            assertThat(first.path("mergedSetSha256").asText()).isEqualTo(Json.sha(Json.canonical(Json.MAPPER.valueToTree(merged))));
            var revisions=List.of(Map.of("bucket","bucket-a","objectKey","00.rag/user/guide.md","revision",Json.sha(MARKDOWN)));
            assertThat(first.path("documentRevisions")).isEqualTo(Json.MAPPER.valueToTree(revisions));
            assertThat(first.path("documentRevisionsSha256").asText()).isEqualTo(Json.sha(Json.canonical(Json.MAPPER.valueToTree(revisions))));
        }
        output.reset();errors.reset();Files.writeString(root.resolve("questions.jsonl"),Files.readString(root.resolve("questions.jsonl"))+"\n");
        try(var f=new GatewayClientTest.Fixture()) {
            assertThat(cli((root,env,system,uri)->inputs(f,List.of("AI_DOC_READ","AI_REVIEW"),List.of(),"AI_REVIEW")).execute(command)).isZero();
            var second=Json.parse(output.toByteArray()).path("hashes");assertThat(second.path("initialSetSha256")).isNotEqualTo(first.path("initialSetSha256"));
            assertThat(second.path("mergedSetSha256")).isEqualTo(first.path("mergedSetSha256"));assertThat(second.path("documentRevisionsSha256")).isEqualTo(first.path("documentRevisionsSha256"));
        }
    }
}
