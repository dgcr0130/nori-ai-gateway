package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.eval.answer.AnswerFixtures.*;

class GatewayClientTest {
    record Request(String method,String path,JsonNode body,List<String> permissions,boolean delegation) {}
    record Reply(int status,byte[] bytes) {Reply(int status,JsonNode body){this(status,Json.canonical(body));}}
    static final class Fixture implements AutoCloseable {
        final HttpServer server;final List<Request> requests=Collections.synchronizedList(new ArrayList<>());
        final AtomicInteger conversations=new AtomicInteger(),jobs=new AtomicInteger();final Map<String,String> queries=new java.util.concurrent.ConcurrentHashMap<>();
        Function<Request,Reply> handler=this::normal;final AtomicLong now=new AtomicLong();
        Fixture() throws Exception {
            server=HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(),0),0);
            server.createContext("/",exchange->{try {
                String assertion=exchange.getRequestHeaders().getFirst("X-Actor-Assertion");var claims=Json.parse(Base64.getUrlDecoder().decode(assertion));
                byte[] body=exchange.getRequestBody().readAllBytes();var request=new Request(exchange.getRequestMethod(),exchange.getRequestURI().toString(),body.length==0?Json.MAPPER.nullNode():Json.parse(body),QuestionSet.strings(claims.path("perm"),true),claims.path("job").booleanValue());
                requests.add(request);var reply=handler.apply(request);exchange.sendResponseHeaders(reply.status(),reply.bytes().length);exchange.getResponseBody().write(reply.bytes());
            }finally{exchange.close();}});server.start();
        }
        Reply normal(Request request) {
            String path=request.path();
            if(path.startsWith("/v1/reviews?"))return new Reply(200,Json.MAPPER.valueToTree(new LinkedHashMap<>(){{put("items",List.of());put("nextAfter",null);}}));
            if(path.equals("/v1/conversations"))return new Reply(201,Json.MAPPER.valueToTree(Map.of("id","conversation-"+conversations.incrementAndGet())));
            if(path.endsWith("/messages")) {
                String id="job-"+jobs.incrementAndGet();queries.put(id,request.body().path("query").asText());
                return new Reply(202,Json.MAPPER.valueToTree(Map.of("jobId",id,"conversationId",path.split("/")[3],"messageId","message-1","queuePosition",0)));
            }
            if(path.startsWith("/v1/analyses/")) {
                String job=path.substring(path.lastIndexOf('/')+1);var response=queries.get(job).equals("거절 질문")?refusal():response("코드는 100입니다",a());response.put("jobId",job);return new Reply(200,response);
            }
            return new Reply(404,Json.MAPPER.createObjectNode());
        }
        URI uri(){return URI.create("http://localhost:"+server.getAddress().getPort());}
        GatewayClient.Assertions assertions(){return (permissions,job)->Base64.getUrlEncoder().withoutPadding().encodeToString(Json.canonical(Json.MAPPER.valueToTree(Map.of("perm",permissions,"job",job))));}
        GatewayClient client(){return new GatewayClient(uri(),"TOKEN_BODY_MUST_NOT_ESCAPE",assertions(),now::get,d->now.addAndGet(d.toNanos()));}
        @Override public void close(){server.stop(0);}
    }
    List<QuestionSet.Bound> questions() {
        var a=question("a",true,"100");a.put("question","첫 질문");var b=question("b",true,"100");b.put("question","이어서 질문").put("predecessorId","a");
        var c=question("c",true,"100");c.put("question","다른 후속 질문").put("predecessorId","a");var no=question("no",false,"");no.put("question","거절 질문");
        var schema=new QuestionSet();return schema.merge(List.of(schema.parse(a),schema.parse(b),schema.parse(c),schema.parse(no)),List.of(),"TC").stream().map(q->schema.bind(q,(bucket,key)->document())).toList();
    }
    @Test void realHttpConversationsPredecessorsPermissionsAndDenominatorsMatchWireContract() throws Exception {
        try(var f=new Fixture();var client=f.client()) {
            var result=new AnswerEvaluation().run(questions(),client,Json.MAPPER.createObjectNode());assertThat(result.exitCode()).isZero();
            assertThat(result.report().items()).hasSize(4);assertThat(result.report().systems().get("TC")).isEqualTo(new AnswerEvaluation.Aggregate(3,3,1,1,1,1,true));
            assertThat(f.conversations.get()).isEqualTo(4);assertThat(f.jobs.get()).isEqualTo(6);
            var messages=f.requests.stream().filter(r->r.path().endsWith("/messages")).toList();
            assertThat(messages.stream().map(r->r.body().path("query").asText()).toList()).containsExactly("첫 질문","첫 질문","이어서 질문","첫 질문","다른 후속 질문","거절 질문");
            assertThat(messages.get(1).path()).isEqualTo(messages.get(2).path());assertThat(messages.get(3).path()).isEqualTo(messages.get(4).path());assertThat(messages.get(1).path()).isNotEqualTo(messages.get(3).path());
            assertThat(f.requests).allSatisfy(r->{assertThat(r.permissions()).containsExactly("AI_DOC_READ");assertThat(r.delegation()).isEqualTo(r.path().endsWith("/messages"));});
        }
    }
    @Test void preparationFailureDoesNotRewritePredecessorsOwnIndependentGradeOrRemoveDenominators() throws Exception {
        try(var f=new Fixture();var client=f.client()) {
            f.handler=r->{if(r.path().startsWith("/v1/analyses/")&&f.jobs.get()==2)return new Reply(200,response("코드는 101입니다",a()).put("jobId","job-2"));return f.normal(r);};
            var result=new AnswerEvaluation().run(questions(),client,Json.MAPPER.createObjectNode());assertThat(result.exitCode()).isEqualTo(1);
            assertThat(result.report().items().get(0).correct()).isTrue();assertThat(result.report().items().get(1).failedPredecessorId()).isEqualTo("a");
            assertThat(result.report().items().get(2).correct()).isTrue();assertThat(result.report().systems().get("TC").answerableTotal()).isEqualTo(3);
            assertThat(result.report().systems().get("TC").answerableCorrect()).isEqualTo(2);
        }
    }
    @Test void stageLimitIsOneHundredEightySecondsIncludingCreateSubmitAndPollWithoutWaiting() throws Exception {
        try(var f=new Fixture();var client=f.client()) {
            f.handler=r->{if(r.path().startsWith("/v1/analyses/"))f.now.addAndGet(Duration.ofSeconds(181).toNanos());return f.normal(r);};
            var step=client.execute(questions().getFirst().question(),null);assertThat(step.failure()).isEqualTo("단계 시간 초과");
        }
        try(var f=new Fixture();var client=f.client()) {
            f.handler=r->{if(r.path().startsWith("/v1/analyses/"))return new Reply(200,Json.MAPPER.valueToTree(Map.of("jobId","job-1","jobStatus","RUNNING","result",Map.of())));return f.normal(r);};
            var step=client.execute(questions().getFirst().question(),null);assertThat(step.failure()).isEqualTo("단계 시간 초과");assertThat(f.now.get()).isEqualTo(Duration.ofSeconds(180).toNanos());
        }
    }
    @Test void actualHttpFourAndFiveHundredsMalformedJsonAndConnectionRefusedAreWrongAnswers() throws Exception {
        for(int status:List.of(400,401,403,404,429,500,503,302))try(var f=new Fixture();var client=f.client()) {
            f.handler=r->new Reply(status,"TOKEN_BODY_MUST_NOT_ESCAPE PRIVATE_KEY_BODY_MUST_NOT_ESCAPE".getBytes());
            assertThat(client.execute(questions().getFirst().question(),null).failure()).isEqualTo("HTTP 응답 실패 "+status);
        }
        try(var f=new Fixture();var client=f.client()) {
            f.handler=r->new Reply(201,"{wrong-json".getBytes());assertThat(client.execute(questions().getFirst().question(),null).failure()).isEqualTo("응답 JSON 불일치");
        }
        URI closed;GatewayClient.Assertions signer;
        try(var f=new Fixture()){closed=f.uri();signer=f.assertions();}
        try(var client=new GatewayClient(closed,"TOKEN_BODY_MUST_NOT_ESCAPE",signer)) {
            assertThat(client.execute(questions().getFirst().question(),null).failure()).isEqualTo("HTTP 연결 실패");
        }
    }
    @Test void actualHttpTimeoutExceptionIsAStageFailureUsingInjectedRemainingTime() throws Exception {
        try(var f=new Fixture()) {
            var calls=new AtomicInteger();long almostExpired=Duration.ofSeconds(180).toNanos()-1;
            try(var client=new GatewayClient(f.uri(),"fixture-token",f.assertions(),()->calls.getAndIncrement()==0?0:almostExpired,d->{})) {
                assertThat(client.execute(questions().getFirst().question(),null).failure()).isEqualTo("단계 시간 초과");
            }
        }
    }
    @Test void listsAllStatesAndEveryCursorAndReadsActiveCaseFromActualDetailShape() throws Exception {
        try(var f=new Fixture();var client=f.client()) {
            String id="00000000-0000-0000-0000-000000000001",id2="00000000-0000-0000-0000-000000000002";
            var feedback=question("feedback",true,"100");feedback.put("origin","FEEDBACK").put("feedbackId",id2).put("reviewVersion",3);
            f.handler=r->{
                if(r.path().equals("/v1/reviews/"+id)||r.path().equals("/v1/reviews/"+id2)) {
                    boolean active=r.path().endsWith(id2);var detail=Json.MAPPER.createObjectNode();detail.set("feedback",Json.MAPPER.valueToTree(Map.of("feedbackId",active?id2:id,"version",4,"state","RESOLVED","rating","DOWN","comment","지적","createdAt","2026-11-19T00:00:00Z","updatedAt","2026-11-19T00:00:00Z")));
                    detail.put("answerId","job-1").put("question","질문");detail.set("answer",refusal().path("result").path("payload").path("answer"));detail.putArray("evidence");detail.putArray("reviews");detail.set("questionCase",active?feedback:Json.MAPPER.nullNode());return new Reply(200,detail);
                }
                if(r.path().contains("state=RESOLVED")) {
                    boolean second=r.path().endsWith("after=next%2Bcursor");var page=Json.MAPPER.createObjectNode();page.putArray("items").addObject().put("feedbackId",second?id2:id);if(second)page.putNull("nextAfter");else page.put("nextAfter","next+cursor");return new Reply(200,page);
                }
                return f.normal(r);
            };
            assertThat(client.activeQuestions(List.of("AI_DOC_READ","AI_REVIEW"),new QuestionSet())).containsExactly(new QuestionSet().parse(feedback));
            assertThat(f.requests.stream().filter(r->r.path().startsWith("/v1/reviews?")).count()).isEqualTo(4);
            assertThat(f.requests.stream().map(Request::path).toList()).anyMatch(p->p.contains("state=OPEN")).anyMatch(p->p.contains("state=REJECTED"));
        }
    }
    @Test void reviewPermissionFailureOrMalformedPaginationIsEnvironmentExitTwo() throws Exception {
        for(int status:List.of(401,403,500))try(var f=new Fixture();var client=f.client()) {
            f.handler=r->new Reply(status,"secret must never appear".getBytes());
            assertThatThrownBy(()->client.activeQuestions(List.of(),new QuestionSet())).isInstanceOfSatisfying(EvalException.class,e->{assertThat(e.exitCode()).isEqualTo(2);assertThat(e.getMessage()).doesNotContain("secret");});
        }
        try(var f=new Fixture();var client=f.client()) {
            f.handler=r->new Reply(200,Json.MAPPER.valueToTree(Map.of("items",List.of(),"nextAfter","same")));
            assertThatThrownBy(()->client.activeQuestions(List.of(),new QuestionSet())).isInstanceOf(EvalException.class);
        }
    }
    @Test void exactEightyPercentPassesButAnyWrongRefusalFails() throws Exception {
        var cases=new ArrayList<QuestionSet.Bound>();
        for(int i=0;i<5;i++){var q=question("yes-"+i,true,"100");q.put("question",i==4?"틀린 답변":"정답");cases.add(bound(q));}
        var no=question("no",false,"");no.put("question","거절 질문");cases.add(bound(no));
        for(boolean correctRefusal:List.of(true,false))try(var f=new Fixture();var client=f.client()) {
            f.handler=r->{
                if(r.path().startsWith("/v1/analyses/")) {
                    String job=r.path().substring(r.path().lastIndexOf('/')+1),query=f.queries.get(job);
                    if(query.equals("틀린 답변"))return new Reply(200,response("코드는 101입니다",a()).put("jobId",job));
                    if(query.equals("거절 질문")&&!correctRefusal)return new Reply(200,response("코드는 100입니다",a()).put("jobId",job));
                }
                return f.normal(r);
            };
            var result=new AnswerEvaluation().run(cases,client,Json.MAPPER.createObjectNode());
            assertThat(result.report().systems().get("TC").answerableRate()).isEqualTo(0.80);
            assertThat(result.exitCode()).isEqualTo(correctRefusal?0:1);
        }
    }
}
