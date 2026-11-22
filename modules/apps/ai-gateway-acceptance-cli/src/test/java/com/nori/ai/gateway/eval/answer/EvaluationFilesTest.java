package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nori.ai.gateway.eval.EvalCli;
import com.nori.ai.gateway.eval.io.Json;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.eval.answer.AnswerFixtures.*;

class EvaluationFilesTest {
    @TempDir Path directory;
    static final List<String> FIELDS=List.of("privateJwk","issuer","subject","permissions","codeSha256","generationModelSha256","embeddingModelSha256","configSha256");
    static final List<String> GRANTED=List.of("REPO_READ","TRACE_READ","AI_DOC_READ","AI_REVIEW","LOG_READ","TOOL_READ","AI_DOC_READ");
    static final String CONFIG="""
        ai:
          gateway:
            systems:
              TC:
                issuer: tc-ui-backend
                reviewPermission: AI_REVIEW
                actorJwksFile: ${IGNORED_CREDENTIAL_PATH}
                tools: [{requiredPermissions: [TOOL_READ]}]
                documents: [{requiredPermissions: [AI_DOC_READ]}]
                apps: [{requiredPermissions: [LOG_READ]}]
                traces: [{requiredPermissions: [TRACE_READ]}]
                repositories: [{requiredPermissions: [REPO_READ]}]
        """;
    final class Fixture implements AutoCloseable {
        final Path root;final HttpServer server;final KeyPair pair;final ObjectNode signer;final String privateValue;
        final ByteArrayOutputStream output=new ByteArrayOutputStream(),errors=new ByteArrayOutputStream();
        final AtomicInteger count=new AtomicInteger(),jobs=new AtomicInteger(),conversations=new AtomicInteger();
        final Map<String,String> queries=new HashMap<>();final List<JsonNode> claims=new ArrayList<>();final AtomicReference<Throwable> failure=new AtomicReference<>();
        Consumer<ObjectNode> mutate=response->{};boolean activeDocumentCase;byte[] signerBytes;
        Fixture() throws Exception {
            root=Files.createTempDirectory(directory,"files-");
            var generator=KeyPairGenerator.getInstance("EC");generator.initialize(new ECGenParameterSpec("secp256r1"));pair=generator.generateKeyPair();
            var publicKey=(ECPublicKey)pair.getPublic();var privateKey=(ECPrivateKey)pair.getPrivate();
            var jwk=Json.MAPPER.createObjectNode().put("kty","EC").put("crv","P-256").put("kid","test-files")
                .put("x",JwkAssertionSignerTest.coordinate(publicKey.getW().getAffineX())).put("y",JwkAssertionSignerTest.coordinate(publicKey.getW().getAffineY()))
                .put("d",JwkAssertionSignerTest.coordinate(privateKey.getS()));privateValue=jwk.path("d").asText();
            signer=Json.MAPPER.createObjectNode().put("issuer","tc-ui-backend").put("subject","eval-user");signer.set("privateJwk",jwk);signer.set("permissions",Json.MAPPER.valueToTree(GRANTED));
            for(String name:List.of("codeSha256","generationModelSha256","embeddingModelSha256","configSha256"))signer.put(name,Json.sha(("fixture-"+name).getBytes(StandardCharsets.UTF_8)));
            write("config/ai-gateway.yaml",CONFIG);write("token.txt"," \r\nfixture-service-token\r\n ");
            Path doc=root.resolve("rag-documents/bucket-a/00.rag/user/guide.md");Files.createDirectories(doc.getParent());Files.write(doc,MARKDOWN);Files.write(doc.resolveSibling("other.md"),MARKDOWN);
            var no=question("no",false,"");no.put("question","거절 질문");write("questions.jsonl",Json.canonicalString(question("yes",true,"100"))+"\n"+Json.canonicalString(no)+"\n");
            server=HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(),0),0);
            server.createContext("/",exchange->{try {
                count.incrementAndGet();assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer fixture-service-token");
                String[] signed=exchange.getRequestHeaders().getFirst("X-Actor-Assertion").split("\\.");
                var verifier=Signature.getInstance("SHA256withECDSAinP1363Format");verifier.initVerify(pair.getPublic());verifier.update((signed[0]+"."+signed[1]).getBytes(StandardCharsets.US_ASCII));
                assertThat(verifier.verify(Base64.getUrlDecoder().decode(signed[2]))).isTrue();var claim=Json.parse(Base64.getUrlDecoder().decode(signed[1]));claims.add(claim);
                String path=exchange.getRequestURI().toString();assertThat(claim.has("dlg")).isEqualTo(path.endsWith("/messages"));
                byte[] input=exchange.getRequestBody().readAllBytes();JsonNode body=input.length==0?Json.MAPPER.nullNode():Json.parse(input);JsonNode response;int status=200;
                if(path.startsWith("/v1/reviews?")) {
                    var page=Json.MAPPER.createObjectNode();var items=page.putArray("items");page.putNull("nextAfter");
                    if(activeDocumentCase&&path.contains("state=RESOLVED"))items.addObject().put("feedbackId","00000000-0000-0000-0000-000000000001");response=page;
                }else if(path.startsWith("/v1/reviews/"))response=detail();
                else if(path.equals("/v1/conversations")){response=Json.MAPPER.valueToTree(Map.of("id","conversation-"+conversations.incrementAndGet()));status=201;}
                else if(path.endsWith("/messages")) {
                    String job="job-"+jobs.incrementAndGet();queries.put(job,body.path("query").asText());
                    response=Json.MAPPER.valueToTree(Map.of("jobId",job,"conversationId",path.split("/")[3],"messageId","message-1","queuePosition",0));status=202;
                }else {
                    String job=path.substring(path.lastIndexOf('/')+1);var result=queries.get(job).equals("거절 질문")?refusal():response("코드는 100입니다",a());result.put("jobId",job);mutate.accept(result);response=result;
                }
                byte[] bytes=Json.canonical(response);exchange.sendResponseHeaders(status,bytes.length);exchange.getResponseBody().write(bytes);
            }catch(Throwable invalid){failure.set(invalid);exchange.sendResponseHeaders(500,-1);}finally{exchange.close();}});server.start();
        }
        ObjectNode detail() {
            String id="00000000-0000-0000-0000-000000000001";var q=question("feedback",false,"");q.put("question","거절 질문").put("origin","FEEDBACK").put("feedbackId",id).put("reviewVersion",1);
            q.withArray("documentTargets").addObject().put("bucket","bucket-a").put("objectKey","00.rag/user/other.md").put("revision",Json.sha(MARKDOWN));
            var detail=Json.MAPPER.createObjectNode().put("answerId","job-previous").put("question","거절 질문");detail.set("feedback",Json.MAPPER.valueToTree(Map.of("feedbackId",id,"version",2,"state","RESOLVED","rating","DOWN","comment","지적","createdAt","2026-11-19T00:00:00Z","updatedAt","2026-11-19T00:00:00Z")));
            detail.set("answer",refusal().path("result").path("payload").path("answer"));detail.putArray("evidence");detail.putArray("reviews");detail.set("questionCase",q);return detail;
        }
        void write(String path,String text) throws IOException {var target=root.resolve(path);Files.createDirectories(target.getParent());Files.writeString(target,text,StandardCharsets.UTF_8);}
        int run() throws IOException {
            if(signerBytes==null)write("signer.json",Json.canonicalString(signer));else Files.write(root.resolve("signer.json"),signerBytes);
            int exit=new EvalCli(Map.of("AI_GATEWAY_EVAL_SERVICE_TOKEN_FILE","token.txt","AI_GATEWAY_EVAL_SIGNER_FILE",root.resolve("signer.json").toString()),
                new PrintStream(output,true,StandardCharsets.UTF_8),new PrintStream(errors,true,StandardCharsets.UTF_8),root)
                .execute("answer-evaluate","--system","TC","--questions","questions.jsonl","--gateway","http://localhost:"+server.getAddress().getPort());
            assertThat(failure.get()).isNull();assertThat(output.toString(StandardCharsets.UTF_8)+errors.toString(StandardCharsets.UTF_8)).doesNotContain(privateValue,"fixture-service-token");return exit;
        }
        void noHttpError() throws IOException {assertThat(run()).isEqualTo(2);assertThat(count.get()).isZero();assertThat(output.size()).isZero();}
        @Override public void close(){server.stop(0);}
    }
    @Test void filesExecuteRealHttpWithEveryConfigSourcePermissionAndFourDeclaredIdentities() throws Exception {
        try(var f=new Fixture()) {
            assertThat(f.run()).isZero();var report=Json.parse(f.output.toByteArray());assertThat(report.path("items")).hasSize(2);
            for(String field:List.of("codeSha256","generationModelSha256","embeddingModelSha256","configSha256"))assertThat(report.path("hashes").path(field).asText()).isEqualTo(Json.sha(("fixture-"+field).getBytes(StandardCharsets.UTF_8)));
            assertThat(report.path("hashes").has("modelSha256")).isFalse();
            assertThat(f.claims).anySatisfy(c->{assertThat(c.path("perm")).hasSize(6);assertThat(c.path("pdg").asText()).isEqualTo(Json.sha("AI_DOC_READ\nAI_REVIEW\nLOG_READ\nREPO_READ\nTOOL_READ\nTRACE_READ".getBytes(StandardCharsets.UTF_8)));});
            assertThat(f.claims).anySatisfy(c->{assertThat(c.path("perm")).hasSize(1);assertThat(c.path("pdg").asText()).isEqualTo(Json.sha("AI_DOC_READ".getBytes(StandardCharsets.UTF_8)));});
        }
    }
    @TestFactory Stream<DynamicTest> eachMissingSignerFieldFailsBeforeHttp() {
        return FIELDS.stream().map(field->DynamicTest.dynamicTest("누락 "+field,()->{try(var f=new Fixture()){f.signer.remove(field);f.noHttpError();}}));
    }
    @TestFactory Stream<DynamicTest> eachWrongSignerFieldTypeFailsBeforeHttp() {
        return FIELDS.stream().map(field->DynamicTest.dynamicTest("자료형 "+field,()->{try(var f=new Fixture()){f.signer.put(field,42);f.noHttpError();}}));
    }
    @TestFactory Stream<DynamicTest> eachBadIdentityFormatFailsBeforeHttp() {
        return Stream.of("codeSha256","generationModelSha256","embeddingModelSha256","configSha256").flatMap(field->Stream.of("A".repeat(64),"a".repeat(63),"z".repeat(64)).map(value->DynamicTest.dynamicTest("지문 "+field+" "+value.length()+value.charAt(0),()->{try(var f=new Fixture()){f.signer.put(field,value);f.noHttpError();}})));
    }
    @Test void unknownFieldIssuerSubjectPermissionsAndPrivateKeyErrorsDoNotExposeSecrets() throws Exception {
        List<Consumer<ObjectNode>> changes=List.of(n->n.put("unknown","숨김"),n->n.put("issuer","another-issuer"),n->n.put("subject","bad subject"),
            n->n.putArray("permissions").add("bad-permission"),n->n.putArray("permissions").add(1),n->n.withObject("privateJwk").put("d","PRIVATE_KEY_MUST_NOT_ESCAPE"),n->n.withObject("privateJwk").put("crv","P-384"));
        for(var change:changes)try(var f=new Fixture()){change.accept(f.signer);f.noHttpError();assertThat(f.errors.toString(StandardCharsets.UTF_8)).doesNotContain("PRIVATE_KEY_MUST_NOT_ESCAPE");}
    }
    @TestFactory Stream<DynamicTest> everyReviewAndConfiguredPermissionIsRequiredBeforeHttp() {
        return GRANTED.stream().distinct().map(missing->DynamicTest.dynamicTest("권한 "+missing,()->{try(var f=new Fixture()){
            f.signer.set("permissions",Json.MAPPER.valueToTree(GRANTED.stream().filter(p->!p.equals(missing)).toList()));f.noHttpError();
        }}));
    }
    @TestFactory Stream<DynamicTest> configMustNameSystemAndUseLiteralStringsAndArrays() {
        var invalid=List.of(CONFIG.replace("TC:","OTHER:"),CONFIG.replace("issuer: tc-ui-backend","issuer: ${ISSUER}"),CONFIG.replace("reviewPermission: AI_REVIEW","reviewPermission: 7"),
            CONFIG.replace("tools: [{requiredPermissions: [TOOL_READ]}]","tools: ${TOOLS}"),CONFIG.replace("[LOG_READ]","${APP_PERMISSIONS}"),
            CONFIG.replace("[REPO_READ]","[3]"),CONFIG.replace("[TRACE_READ]","[${TRACE_PERMISSION}]"),CONFIG.replace("issuer: tc-ui-backend","issuer: [tc-ui-backend]"),
            CONFIG.replace("reviewPermission: AI_REVIEW","reviewPermission: ${REVIEW}"),CONFIG.replace("[TOOL_READ]","TOOL_READ"));
        return java.util.stream.IntStream.range(0,invalid.size()).mapToObj(i->DynamicTest.dynamicTest("config "+i,()->{try(var f=new Fixture()){f.write("config/ai-gateway.yaml",invalid.get(i));f.noHttpError();}}));
    }
    @Test void invalidOrUnreadableTokenFailsBeforeHttp() throws Exception {
        for(String token:List.of("","a b","a\nb","\ufefftoken"))try(var f=new Fixture()){f.write("token.txt",token);f.noHttpError();}
        try(var f=new Fixture()){Files.write(f.root.resolve("token.txt"),new byte[]{(byte)0xff});f.noHttpError();}
    }
    @Test void signerFileMustBeOneUtf8ObjectWithUniqueFields() throws Exception {
        for(String malformed:List.of("","{","[]","{} {}"))try(var f=new Fixture()){f.signerBytes=malformed.getBytes(StandardCharsets.UTF_8);f.noHttpError();}
        try(var f=new Fixture()){f.signerBytes=Json.canonicalString(f.signer).replace("\"issuer\":","\"issuer\":\"duplicate\",\"issuer\":").getBytes(StandardCharsets.UTF_8);f.noHttpError();}
        try(var f=new Fixture()){f.signerBytes=(Json.canonicalString(f.signer)+" {}").getBytes(StandardCharsets.UTF_8);f.noHttpError();}
        try(var f=new Fixture()){f.signerBytes=new byte[]{(byte)0xff};f.noHttpError();}
        try(var f=new Fixture()){f.signerBytes=Json.canonicalString(f.signer).getBytes(StandardCharsets.UTF_16LE);f.noHttpError();}
    }
    @Test void optionalEmptySourceListsAreValidAndUnneededPlaceholdersAreNotExpanded() throws Exception {
        try(var f=new Fixture()) {
            f.write("config/ai-gateway.yaml","ai:\n  gateway:\n    systems:\n      TC:\n        issuer: tc-ui-backend\n        reviewPermission: AI_REVIEW\n        documents: []\n        actorJwksFile: ${UNREAD_CREDENTIAL}\n");
            assertThat(f.run()).isZero();
        }
    }
    static ObjectNode location(JsonNode response){return (ObjectNode)response.path("result").path("payload").path("answer").path("citations").get(0).path("location");}
    @Test void requiredDocumentRevisionMismatchAbortsWithoutPublishingScores() throws Exception {
        try(var f=new Fixture()){f.mutate=r->{if(!r.path("result").path("payload").path("answer").path("citations").isEmpty())location(r).put("revision","f".repeat(64));};assertThat(f.run()).isEqualTo(2);assertThat(f.output.size()).isZero();assertThat(f.jobs.get()).isEqualTo(1);}
    }
    @Test void unrelatedDocumentOrBucketRevisionDoesNotChangeGrading() throws Exception {
        for(String change:List.of("objectKey","bucket"))try(var f=new Fixture()) {
            f.mutate=r->{var citations=r.path("result").path("payload").path("answer").path("citations");if(!citations.isEmpty()) {
                var extra=((com.fasterxml.jackson.databind.node.ArrayNode)citations).addObject().put("number",2).put("evidenceId","extra").put("sourceType","DOCUMENT");
                var loc=Json.MAPPER.valueToTree(a());extra.set("location",loc);((ObjectNode)loc).put(change,change.equals("bucket")?"bucket-b":"unrequired.md").put("revision","f".repeat(64));
            }};assertThat(f.run()).isZero();
        }
    }
    @Test void documentTargetsOfAnotherMergedCaseAndEveryEvidenceLocationAreChecked() throws Exception {
        for(String path:List.of("citation","location","childLocations"))try(var f=new Fixture()) {
            f.activeDocumentCase=true;f.mutate=r->{var payload=(ObjectNode)r.path("result").path("payload");var loc=Json.MAPPER.createObjectNode().put("bucket","bucket-a").put("objectKey","00.rag/user/other.md").put("revision","f".repeat(64)).put("section","절 A").put("lineFrom",3).put("lineTo",3);
                if(path.equals("citation")){var extra=payload.withObject("answer").withArray("citations").addObject().put("number",2).put("evidenceId","extra").put("sourceType","DOCUMENT");extra.set("location",loc);}
                else {var evidence=payload.putArray("evidence").addObject().put("sourceType","DOCUMENT");if(path.equals("location"))evidence.set("location",loc);else evidence.putArray("childLocations").add(loc);}
            };assertThat(f.run()).isEqualTo(2);assertThat(f.output.size()).isZero();assertThat(f.jobs.get()).isEqualTo(1);
        }
    }
}
