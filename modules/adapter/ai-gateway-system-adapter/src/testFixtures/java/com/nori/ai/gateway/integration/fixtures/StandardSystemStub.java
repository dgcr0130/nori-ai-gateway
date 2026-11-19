package com.nori.ai.gateway.integration.fixtures;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.sun.net.httpserver.HttpServer;
import tools.jackson.databind.JsonNode;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
/** 테스트에서만 소유하는 표준 HTTP backend입니다. 제품 port 구현을 대체하지 않습니다. */
public final class StandardSystemStub implements AutoCloseable {
 public record Request(JsonNode body,Map<String,List<String>> headers){}
 public record Reply(int httpStatus,Object body){}
 private final HttpServer server;private final ExecutorService executor=Executors.newVirtualThreadPerTaskExecutor();private final Clock clock;
 public final List<Request> requests=new CopyOnWriteArrayList<>();private final Map<String,String> delegations=new ConcurrentHashMap<>();private final Set<String> revoked=ConcurrentHashMap.newKeySet();
 public final String token=UUID.randomUUID().toString();public final String controlToken;private volatile String scenario="default";public volatile Function<Request,Reply> responder;
 public volatile String evidenceText="관측 자료";
 public StandardSystemStub(Clock clock)throws java.io.IOException {this(clock,UUID.randomUUID().toString());}
 public StandardSystemStub(Clock clock,String controlToken)throws java.io.IOException {this.clock=clock;this.controlToken=controlToken;server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);server.setExecutor(executor);server.createContext("/internal/ai/v1/query",exchange->{
  byte[] bytes;int status;
  try {var request=new Request(SchemaContracts.parse(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8)),Map.copyOf(exchange.getRequestHeaders()));requests.add(request);
   if(!"POST".equals(exchange.getRequestMethod())||!Objects.equals(exchange.getRequestHeaders().getFirst("Authorization"),"Bearer "+token)||!Objects.equals(exchange.getRequestHeaders().getFirst("X-Request-Id"),request.body().path("jobId").asString()))throw new IllegalArgumentException("표준 요청 결속 불일치");
   var reply=responder==null?reply(request):responder.apply(request);status=reply.httpStatus();bytes=(reply.body() instanceof String text?text:SchemaContracts.JSON.writeValueAsString(reply.body())).getBytes(StandardCharsets.UTF_8);
  }catch(RuntimeException invalid){status=400;bytes="{}".getBytes(StandardCharsets.UTF_8);}
  try{exchange.getResponseHeaders().set("Content-Type","application/json");exchange.sendResponseHeaders(status,bytes.length);exchange.getResponseBody().write(bytes);}finally{exchange.close();}
 });
 server.createContext("/__fixture/",exchange->{
  int status=200;Object result=Map.of();try{if(!Objects.equals(exchange.getRequestHeaders().getFirst("Authorization"),"Bearer "+controlToken))status=401;
   else {String path=exchange.getRequestURI().getPath();String method=exchange.getRequestMethod();var body=method.equals("POST")?SchemaContracts.parse(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8)):null;
    switch(path){case "/__fixture/reset"->{if(method.equals("POST")){reset();select("default");status=204;}else status=405;}
     case "/__fixture/scenario"->{if(method.equals("POST")){select(body.path("id").asString());status=204;}else result=Map.of("id",selected());}
     case "/__fixture/delegations"->{register(body.path("sub").asString(),body.path("dlg").asString());status=204;}
     case "/__fixture/calls"->{var counts=new LinkedHashMap<String,Long>();for(String id:List.of("getEqpSnapshot","getModel","listDlq","getCommLogs"))counts.put(id,count(id));result=counts;}
     default->status=404;}
   }
  }catch(RuntimeException invalid){status=400;}
  byte[] bytes=SchemaContracts.JSON.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);try{exchange.sendResponseHeaders(status,status==204?-1:bytes.length);if(status!=204)exchange.getResponseBody().write(bytes);}finally{exchange.close();}
 });server.start();}
 public URI base(){return URI.create("http://127.0.0.1:"+server.getAddress().getPort());}
 public void register(String actor,String delegation){delegations.put(hash(delegation),actor);}
 public void revoke(String digest){revoked.add(digest);}
 public void select(String value){scenario=value;}
 public String selected(){return scenario;}
 public long count(String tool){String id=switch(tool){case "getEqpSnapshot"->"tc.eqp.getOperationalSnapshot";case "getModel"->"tc.model.read";case "listDlq"->"tc.dlq.list";case "getCommLogs"->"tc.commLogs.search";default->tool;};return requests.stream().filter(request->request.body().path("toolId").asString().equals(id)).count();}
 public void reset(){requests.clear();}
 public static String hash(String value){try{return HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(java.security.NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}}
 private Reply reply(Request request){String status=switch(scenario){case "logout","denied"->"DENIED";case "authz-down","unavailable"->"SOURCE_UNAVAILABLE";case "timeout"->"TIMEOUT";case "empty","no-data"->"EMPTY";case "partial"->"PARTIAL";default->"SUCCESS";};
  String delegation=request.headers().entrySet().stream().filter(entry->entry.getKey().equalsIgnoreCase("X-Delegation-Id")).flatMap(entry->entry.getValue().stream()).findFirst().orElse("");if(revoked.contains(hash(delegation))||!Objects.equals(delegations.get(hash(delegation)),request.body().path("subject").asString()))status="DENIED";
  if(scenario.equals("comm-trim")&&request.body().path("toolId").asString().equals("tc.commLogs.search"))status="PARTIAL";
  var body=new LinkedHashMap<String,Object>();body.put("version",scenario.equals("contract-v2")?"2":"1");for(String key:List.of("systemId","jobId","toolId"))body.put(key,request.body().path(key).asString());body.put("status",status);body.put("observedAt",clock.instant().toString());
  String ref=request.body().path("arguments").path("eqp").asString("record-1");body.put("data",Set.of("SUCCESS","PARTIAL").contains(status)?Map.of("items",List.of(Map.of("ref",ref,"text",evidenceText,"values",Map.of()))):"EMPTY".equals(status)?Map.of("items",List.of()):null);if(scenario.equals("composer-oversize")&&Set.of("SUCCESS","PARTIAL").contains(status)){var steps=new ArrayList<Map<String,Object>>();for(int i=1;i<=320;i++)steps.add(Map.of("step","STEP-"+i,"description","장비 공정 단계의 적용 조건과 검증 절차 "+i));body.put("data",Map.of("items",List.of(Map.of("ref",ref,"text","모델 공정 자료","values",Map.of("steps",steps)))));}
  body.put("missingFields",status.equals("PARTIAL")?List.of("fixture-missing"):List.of());boolean comm=request.body().path("toolId").asString().equals("tc.commLogs.search");
  body.put("warnings",comm?List.of("RETENTION_UNVERIFIED"):List.of());
  var ranges=new ArrayList<Map<String,String>>();if(comm&&scenario.equals("comm-trim")){var args=request.body().path("arguments");ranges.add(Map.of("from",args.path("from").asString(),"to",clock.instant().minusSeconds(600).toString()));}
  body.put("retentionMeta",Map.of("missingRanges",List.of(),"unverifiedRanges",ranges));int http=switch(status){case "DENIED"->403;case "SOURCE_UNAVAILABLE"->503;case "TIMEOUT"->504;default->200;};body.put("errorCode",switch(http){case 403->"DELEGATION_DENIED";case 503->"SOURCE_UNAVAILABLE";case 504->"TIMEOUT";default->null;});return new Reply(http,body);
 }
 @Override public void close(){server.stop(0);executor.close();}
}
