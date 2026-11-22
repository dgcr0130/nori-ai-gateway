package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.function.LongSupplier;

/** HTTP 응답 본문·헤더·원인 예외를 오류나 결과 JSON으로 전달하지 않습니다. */
public final class GatewayClient implements AutoCloseable {
    @FunctionalInterface public interface Assertions { String sign(List<String> permissions,boolean job); }
    @FunctionalInterface public interface Pause { void sleep(Duration duration) throws InterruptedException; }
    public record Step(JsonNode response,String failure,String conversationId) {}
    private static final Duration STEP_LIMIT=Duration.ofSeconds(180);
    private final URI base;private final String token;private final Assertions assertions;private final HttpClient http;
    private final LongSupplier nanos;private final Pause pause;
    public GatewayClient(URI base,String token,Assertions assertions) {this(base,token,assertions,System::nanoTime,d->Thread.sleep(d.toMillis()));}
    public GatewayClient(URI base,String token,Assertions assertions,LongSupplier nanos,Pause pause) {
        if(!Set.of("http","https").contains(base.getScheme())||base.getHost()==null||base.getRawUserInfo()!=null||base.getRawQuery()!=null
                ||base.getRawFragment()!=null||!(base.getPath().isEmpty()||base.getPath().equals("/")))throw environment();
        this.base=base;this.token=token;this.assertions=assertions;this.nanos=nanos;this.pause=pause;
        this.http=HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).connectTimeout(STEP_LIMIT).build();
    }
    public List<QuestionSet.Question> activeQuestions(List<String> permissions,QuestionSet schema) {
        var result=new ArrayList<QuestionSet.Question>();var seenIds=new HashSet<String>();
        for(String state:List.of("OPEN","RESOLVED","REJECTED")) {
            String after="";var cursors=new HashSet<String>();
            do {
                if(!cursors.add(after))throw environment();
                var page=review("/v1/reviews?state="+state+"&limit=100&after="+encode(after),permissions);
                if(!page.path("items").isArray()||!page.has("nextAfter")||!(page.path("nextAfter").isNull()||page.path("nextAfter").isTextual()))throw environment();
                if(page.path("nextAfter").isTextual()&&page.path("nextAfter").textValue().isBlank())throw environment();
                for(var item:page.path("items")) {
                    String id=QuestionSet.text(item,"feedbackId");try{UUID.fromString(id);}catch(IllegalArgumentException invalid){throw environment();}
                    if(!seenIds.add(id))continue;
                    var detail=review("/v1/reviews/"+id,permissions);
                    if(!detail.has("questionCase")||!detail.path("feedback").path("feedbackId").asText().equals(id))throw environment();
                    if(!detail.path("questionCase").isNull()) {
                        var question=schema.parse(detail.path("questionCase"));
                        if(!question.origin().equals("FEEDBACK")||!id.equals(question.feedbackId()))throw environment();result.add(question);
                    }
                }
                after=page.path("nextAfter").isNull()?"":page.path("nextAfter").textValue();
            }while(!after.isEmpty());
        }
        return List.copyOf(result);
    }
    private JsonNode review(String path,List<String> permissions) {
        try{return request("GET",path,null,permissions,false,nanos.getAsLong()+STEP_LIMIT.toNanos());}
        catch(StageFailure failure){throw environment();}
    }
    public Step execute(QuestionSet.Question question,String conversation) {
        long deadline=nanos.getAsLong()+STEP_LIMIT.toNanos();
        try {
            if(conversation==null) {
                var created=request("POST","/v1/conversations",Json.MAPPER.createObjectNode(),question.permissions(),false,deadline);
                conversation=identifier(created,"id");
            }
            var body=Json.MAPPER.createObjectNode().put("query",question.question());body.set("context",question.context());
            var accepted=request("POST","/v1/conversations/"+encode(conversation)+"/messages",body,question.permissions(),true,deadline);
            String job=identifier(accepted,"jobId");
            if(!accepted.path("conversationId").asText().equals(conversation))throw new StageFailure("응답 형식 불일치");
            while(true) {
                var response=request("GET","/v1/analyses/"+encode(job),null,question.permissions(),false,deadline);
                if(!response.path("jobId").asText().equals(job))throw new StageFailure("응답 형식 불일치");
                String state=response.path("jobStatus").asText();
                if(!Set.of("QUEUED","RUNNING").contains(state))return new Step(response,null,conversation);
                remaining(deadline);pause.sleep(Duration.ofNanos(Math.min(Duration.ofMillis(100).toNanos(),remaining(deadline).toNanos())));
            }
        }catch(StageFailure failure){return new Step(null,failure.getMessage(),conversation);}
        catch(InterruptedException interrupted){Thread.currentThread().interrupt();return new Step(null,"평가 중단",conversation);}
    }
    private JsonNode request(String method,String path,JsonNode body,List<String> permissions,boolean job,long deadline) {
        try {
            var request=HttpRequest.newBuilder(base.resolve(path)).timeout(remaining(deadline))
                .header("Authorization","Bearer "+token).header("X-Actor-Assertion",assertions.sign(permissions,job)).header("Accept","application/json");
            if(body==null)request.method(method,HttpRequest.BodyPublishers.noBody());
            else request.header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofByteArray(Json.canonical(body)));
            var response=http.send(request.build(),HttpResponse.BodyHandlers.ofByteArray());remaining(deadline);
            int expected=method.equals("POST")?(path.equals("/v1/conversations")?201:202):200;
            if(response.statusCode()!=expected)throw new StageFailure("HTTP 응답 실패 "+response.statusCode());
            try {var json=Json.parse(response.body());if(!json.isObject())throw new StageFailure("응답 형식 불일치");return json;}
            catch(EvalException malformed){throw new StageFailure("응답 JSON 불일치");}
        }catch(HttpTimeoutException timeout){throw new StageFailure("단계 시간 초과");}
        catch(IOException failure){throw new StageFailure("HTTP 연결 실패");}
        catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new StageFailure("평가 중단");}
    }
    private Duration remaining(long deadline){long remaining=deadline-nanos.getAsLong();if(remaining<=0)throw new StageFailure("단계 시간 초과");return Duration.ofNanos(remaining);}
    private static String identifier(JsonNode node,String name){String value=node.path(name).asText();if(!value.matches("[A-Za-z0-9_-]{1,128}"))throw new StageFailure("응답 식별자 불일치");return value;}
    private static String encode(String text){return URLEncoder.encode(text,java.nio.charset.StandardCharsets.UTF_8);}
    static EvalException environment(){return new EvalException(2,"평가 환경·검토 권한을 확인할 수 없습니다");}
    private static final class StageFailure extends RuntimeException {StageFailure(String reason){super(reason,null,false,false);}}
    @Override public void close(){http.close();}
}
