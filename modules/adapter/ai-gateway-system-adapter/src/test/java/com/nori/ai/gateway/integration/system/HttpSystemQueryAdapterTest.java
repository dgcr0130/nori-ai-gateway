package com.nori.ai.gateway.integration.system;

import com.nori.ai.gateway.core.job.CancellationToken;
import com.nori.ai.gateway.core.plan.ResourceRef;
import com.nori.ai.gateway.core.port.SystemQueryPort;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ResultStatus;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

class HttpSystemQueryAdapterTest {
    private static final List<Path> TEMP_PATHS = new ArrayList<>();
    @TempDir Path temp;
    HttpServer server;
    final AtomicInteger calls = new AtomicInteger();
    final AtomicReference<String> received = new AtomicReference<>();
    final AtomicReference<com.sun.net.httpserver.Headers> headers = new AtomicReference<>();
    byte[] response;
    int http = 200;
    static final Instant NOW = Instant.parse("2026-11-18T00:00:00Z");
    static final Clock CLOCK = Clock.fixed(NOW,ZoneOffset.UTC);
    @BeforeEach void start() throws Exception {
        TEMP_PATHS.add(temp);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(),0),0);
        server.createContext("/internal/ai/v1/query",exchange -> {
            calls.incrementAndGet(); received.set(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8)); headers.set(exchange.getRequestHeaders());
            exchange.getResponseHeaders().set("Content-Type","application/json"); exchange.sendResponseHeaders(http,response.length);
            try(var output=exchange.getResponseBody()) { output.write(response); } finally { exchange.close(); }
        }); server.start(); response = envelope("SUCCESS",null,"{\"items\":[{\"ref\":\"row-1\",\"text\":\"관측 사실\",\"values\":{\"value\":1}}]}").getBytes(StandardCharsets.UTF_8);
    }
    @AfterEach void stop() { if(server!=null)server.stop(0); }
    @AfterAll static void temporaryDirectoriesAreGoneAfterEachJUnitScope() {
        TEMP_PATHS.forEach(path -> assertThat(path).doesNotExist());
    }
    HttpSystemQueryAdapter adapter() throws Exception {
        Path token=temp.resolve("token"); Files.writeString(token,"local-gateway-token");
        URI base = new URI("http://localhost:"+server.getAddress().getPort());
        return new HttpSystemQueryAdapter(new SystemQuerySettings(Map.of(OwnerSystem.valueOf("NEW"),new SystemQuerySettings.Endpoint(base,token))),CLOCK,new ContentSanitizer());
    }
    SystemQueryPort.Request request(String arguments,String schema,long timeout,CancellationToken token) {
        var scope=new VerifiedSystemScope(OwnerSystem.valueOf("NEW"),"service",new ActorRef("actor"),"actor",Set.of("READ_DATA"),"a".repeat(64),NOW.minusSeconds(30),NOW.plusSeconds(30),"job");
        var tool=ConfiguredTool.create(scope.boundSystem(),"data.read","자료 조회",ToolKind.BUSINESS,SchemaContracts.parse(schema),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("READ_DATA"),timeout);
        return new SystemQueryPort.Request(scope,tool,SchemaContracts.parse(arguments),List.of(new ResourceRef("TARGET","row-1")),"delegation-local-123456789",ZoneId.of("Asia/Seoul"),token);
    }
    SystemQueryPort.Request request() { return request("{}","{\"type\":\"object\",\"additionalProperties\":false}",10000,new CancellationToken()); }
    static String envelope(String status,String code,String data) {
        return "{\"version\":\"1\",\"systemId\":\"NEW\",\"jobId\":\"job\",\"toolId\":\"data.read\",\"status\":\""+status+"\",\"observedAt\":\"2026-11-18T00:00:00Z\",\"data\":"+data+",\"missingFields\":[],\"warnings\":[],\"retentionMeta\":{\"missingRanges\":[],\"unverifiedRanges\":[]},\"errorCode\":"+(code==null?"null":"\""+code+"\"")+"}";
    }
    @Test void sendsVerifiedIdentityDelegationAndUtcOnceForANewSystem() throws Exception {
        var request=request("{\"from\":\"2026-11-18T09:00:00\",\"to\":\"2026-11-18T10:00:00\"}","{\"type\":\"object\",\"properties\":{\"from\":{\"type\":\"string\"},\"to\":{\"type\":\"string\"}},\"additionalProperties\":false}",10000,new CancellationToken());
        assertThat(adapter().query(request).status()).isEqualTo(ResultStatus.SUCCESS);
        var body=SchemaContracts.parse(received.get()); assertThat(body.path("arguments").path("from").asString()).isEqualTo("2026-11-18T00:00:00Z");
        assertThat(body.path("deadline").asString()).isEqualTo("2026-11-18T00:00:10Z");
        assertThat(body.path("subject").asString()).isEqualTo(headers.get().getFirst("X-Actor-Subject"));
        assertThat(body.path("delegationId").asString()).isEqualTo(headers.get().getFirst("X-Delegation-Id"));
        assertThat(headers.get().getFirst("X-Request-Id")).isEqualTo("job"); assertThat(headers.get().getFirst("Authorization")).isEqualTo("Bearer local-gateway-token");
        assertThat(body.path("permissionDigest").asString()).isEqualTo("a".repeat(64)); assertThat(calls).hasValue(1);
    }
    @Test void rejectsSchemaTimeAndExpiredDeadlineBeforeSending() throws Exception {
        var adapter=adapter(); assertThat(adapter.query(request("{\"extra\":1}","{\"type\":\"object\",\"additionalProperties\":false}",10000,new CancellationToken())).errorCode()).isEqualTo("PLAN_INVALID");
        assertThat(adapter.query(request("{\"from\":\"invalid\"}","{\"type\":\"object\"}",10000,new CancellationToken())).errorCode()).isEqualTo("PLAN_INVALID");
        var expired=new CancellationToken();expired.deadline(NOW,CLOCK);assertThat(adapter.query(request("{}","{\"type\":\"object\"}",10000,expired)).status()).isEqualTo(ResultStatus.TIMEOUT);
        assertThat(calls).hasValue(0); assertThat(HttpSystemQueryAdapter.CONNECT_TIMEOUT).isEqualTo(Duration.ofSeconds(2));
    }
    @Test void validatesEveryHttpMappingWithoutRetryAndRefusesUnexpectedHttpOrJson() throws Exception {
        var adapter=adapter(); var codes=Map.of(400,"INPUT_INVALID",401,"SERVICE_AUTH_INVALID",403,"DELEGATION_DENIED",404,"TOOL_NOT_FOUND",409,"DELEGATION_CHANGED",429,"SOURCE_BUSY",503,"SOURCE_UNAVAILABLE",504,"TIMEOUT");
        for(var entry:codes.entrySet()) {
            http=entry.getKey();String status=switch(http){case 400,404->"FAILED";case 401,403,409->"DENIED";case 504->"TIMEOUT";default->"SOURCE_UNAVAILABLE";};
            response=envelope(status,entry.getValue(),"null").getBytes(StandardCharsets.UTF_8);
            var result=adapter.query(request());assertThat(result.status().name()).isEqualTo(status);assertThat(result.errorCode()).isEqualTo(entry.getValue());
        }
        assertThat(calls).hasValue(8);
        for(String json:List.of("<html>error</html>","{}",envelope("SUCCESS",null,"null"),envelope("EMPTY",null,"{\"items\":[]}").replace("\"job\"","\"other\""))) {
            http=200;response=json.getBytes(StandardCharsets.UTF_8); assertThat(adapter.query(request()).errorCode()).isEqualTo("CONTRACT_INVALID");
        }
        http=500;response=envelope("FAILED","CONTRACT_INVALID","null").getBytes(StandardCharsets.UTF_8);assertThat(adapter.query(request()).errorCode()).isEqualTo("CONTRACT_INVALID");
        assertThat(calls).hasValue(13);
    }
    @Test void preservesPartialTruncationAndMasksTextValuesAndLocationTogether() throws Exception {
        response=envelope("PARTIAL",null,"{\"items\":[{\"ref\":\"a@example.com\",\"text\":\"a@example.com\",\"values\":{\"value\":\"a@example.com\"}}]}")
            .replace("\"missingFields\":[]","\"missingFields\":[\"truncatedItems\"]")
            .replace("\"missingRanges\":[]","\"missingRanges\":[{\"from\":\"2026-11-17T00:00:00Z\",\"to\":\"2026-11-18T00:00:00Z\"}]").getBytes(StandardCharsets.UTF_8);
        var result=adapter().query(request());assertThat(result.status()).isEqualTo(ResultStatus.PARTIAL);assertThat(result.missingFields()).containsExactly("truncatedItems");assertThat(result.missingRanges()).hasSize(1);
        assertThat(result.data().toString()).doesNotContain("a@example.com"); assertThat(result.data().path("items").get(0).path("ref").asString()).isEqualTo("[마스킹]");
    }
    @Test void boundsResponseAtExactlyOneMibAndRejectsAnExtraByte() throws Exception {
        String base=envelope("EMPTY",null,"{\"items\":[]}");var adapter=adapter();
        response=(base+" ".repeat(HttpSystemQueryAdapter.MAX_RESPONSE_BYTES-base.getBytes(StandardCharsets.UTF_8).length)).getBytes(StandardCharsets.UTF_8);
        assertThat(adapter.query(request()).status()).isEqualTo(ResultStatus.EMPTY);
        response=Arrays.copyOf(response,response.length+1);response[response.length-1]=' ';
        assertThat(adapter.query(request()).errorCode()).isEqualTo("CONTRACT_INVALID");assertThat(calls).hasValue(2);
    }
    @Test void boundsUtf8RequestAndDoesNotSendOversizedInput() throws Exception {
        var adapter=adapter();String schema="{\"type\":\"object\",\"properties\":{\"q\":{\"type\":\"string\"}},\"additionalProperties\":false}";
        adapter.query(request("{\"q\":\"a\"}",schema,10000,new CancellationToken()));
        int fixed=received.get().getBytes(StandardCharsets.UTF_8).length-1;
        int payloadBytes=HttpSystemQueryAdapter.MAX_REQUEST_BYTES-fixed;
        String unicode="한".repeat(payloadBytes/3)+"a".repeat(payloadBytes%3);
        assertThat(adapter.query(request("{\"q\":\""+unicode+"\"}",schema,10000,new CancellationToken())).status()).isEqualTo(ResultStatus.SUCCESS);
        assertThat(adapter.query(request("{\"q\":\""+unicode+"a\"}",schema,10000,new CancellationToken())).errorCode()).isEqualTo("CONTRACT_INVALID");
        assertThat(calls).hasValue(2);
    }
    @Test void classifiesConnectionFailureAsUnavailableAndKeepsSecretsOutOfResults() throws Exception {
        var adapter=adapter();server.stop(0);var result=adapter.query(request());assertThat(result.status()).isEqualTo(ResultStatus.SOURCE_UNAVAILABLE);
        assertThat(result.toString()).doesNotContain("local-gateway-token","delegation-local"); assertThat(calls).hasValue(0);
    }
    @Test void rejectsDuplicateTrailingMalformedUtf8AndEveryEnvelopeInvariant() throws Exception {
        var adapter=adapter();String valid=envelope("EMPTY",null,"{\"items\":[]}");
        var invalid=new ArrayList<String>(List.of(valid+" {}",valid.replace("\"version\":\"1\"","\"version\":\"1\",\"version\":\"1\""),
            valid.replace("\"observedAt\":\"2026-11-18T00:00:00Z\"","\"observedAt\":\"2026-11-18T09:00:00+09:00\""),
            valid.replace("\"warnings\":[]","\"warnings\":[\"UNKNOWN\"]"),valid.replace("\"warnings\":[]","\"warnings\":[\"RETENTION_UNVERIFIED\",\"RETENTION_UNVERIFIED\"]"),
            valid.replace("\"items\":[]","\"items\":[{\"ref\":\"row\",\"text\":\"text\",\"values\":{}}]"),
            envelope("PARTIAL",null,"{\"items\":[]}"),envelope("SUCCESS",null,"{\"items\":[]}"),
            valid.replace("\"missingRanges\":[]","\"missingRanges\":[{\"from\":\"2026-11-18T00:00:00Z\",\"to\":\"2026-11-18T00:00:00Z\"}]"),
            valid.replace("\"status\":\"EMPTY\"","\"status\":\"ACCEPTED_ASYNC\"")));
        for(String key:List.of("version","systemId","jobId","toolId","status","observedAt","data","missingFields","warnings","retentionMeta","errorCode")) {
            var node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(valid);node.remove(key);invalid.add(node.toString());
            node=(tools.jackson.databind.node.ObjectNode)SchemaContracts.parse(valid);node.put("extra",true);invalid.add(node.toString());
        }
        for(String json:invalid) { response=json.getBytes(StandardCharsets.UTF_8); assertThat(adapter.query(request()).errorCode()).as(json).isEqualTo("CONTRACT_INVALID"); }
        response=new byte[]{(byte)0xc3,0x28};assertThat(adapter.query(request()).errorCode()).isEqualTo("CONTRACT_INVALID");
        assertThat(calls).hasValue(invalid.size()+1);
    }
    @Test void keepsEveryDataStatusAndSingleItemSchemaAndTruncationWithoutInventingItems() throws Exception {
        var adapter=adapter();
        for(String status:List.of("SUCCESS","STALE","CONFLICT")) {
            response=envelope(status,null,"{\"items\":[{\"ref\":\"row\",\"text\":\"사실\",\"values\":{\"number\":1}}]}").getBytes(StandardCharsets.UTF_8);
            assertThat(adapter.query(request()).status().name()).isEqualTo(status);
            var original=request();var typed=ConfiguredTool.create(original.scope().boundSystem(),"data.read","조회",ToolKind.BUSINESS,original.tool().inputSchema(),SchemaContracts.parse("{\"type\":\"object\",\"required\":[\"missing\"]}"),List.of("READ_DATA"),10000);
            assertThat(adapter.query(new SystemQueryPort.Request(original.scope(),typed,original.arguments(),original.targets(),original.delegationId(),original.timezone(),original.cancellation())).errorCode()).isEqualTo("CONTRACT_INVALID");
        }
        response=envelope("PARTIAL",null,"{\"items\":[]}").replace("\"missingFields\":[]","\"missingFields\":[\"truncatedItems\"]").getBytes(StandardCharsets.UTF_8);
        var partial=adapter.query(request());assertThat(partial.status()).isEqualTo(ResultStatus.PARTIAL);assertThat(partial.data().path("items")).isEmpty();assertThat(partial.missingRanges()).isEmpty();
        for(String status:List.of("EMPTY","OUT_OF_RETENTION","UNSUPPORTED")) {
            response=envelope(status,null,status.equals("EMPTY")?"{\"items\":[]}":"null").getBytes(StandardCharsets.UTF_8);
            assertThat(adapter.query(request()).status().name()).isEqualTo(status);
        }
    }
    @Test void enforcesTheWholeCallDeadlineWithTheActualClient() throws Exception {
        server.removeContext("/internal/ai/v1/query");
        var entered=new java.util.concurrent.CountDownLatch(1);var release=new java.util.concurrent.CountDownLatch(1);
        server.createContext("/internal/ai/v1/query",exchange->{calls.incrementAndGet();exchange.getRequestBody().readAllBytes();entered.countDown();
            try{release.await();}catch(InterruptedException interrupted){Thread.currentThread().interrupt();}finally{exchange.close();}});
        try {
            var result=adapter().query(request("{}","{\"type\":\"object\"}",150,new CancellationToken()));
            assertThat(result.status()).isEqualTo(ResultStatus.TIMEOUT);assertThat(entered.getCount()).isZero();assertThat(calls).hasValue(1);
        } finally {release.countDown();}
    }
    @Test void appliesTheTwoSecondConnectionLimitDuringAnActualTlsHandshake() throws Exception {
        try(var listener=new ServerSocket(0,1,InetAddress.getLoopbackAddress());var workers=java.util.concurrent.Executors.newSingleThreadExecutor()) {
            var accepted=new java.util.concurrent.CountDownLatch(1);var release=new java.util.concurrent.CountDownLatch(1);
            var peer=workers.submit(()->{try(var socket=listener.accept()){accepted.countDown();release.await();}catch(Exception failure){throw new RuntimeException(failure);}});
            Path token=temp.resolve("tls-token");Files.writeString(token,"local-gateway-token");
            var adapter=new HttpSystemQueryAdapter(new SystemQuerySettings(Map.of(OwnerSystem.valueOf("NEW"),new SystemQuerySettings.Endpoint(URI.create("https://localhost:"+listener.getLocalPort()),token))),CLOCK,new ContentSanitizer());
            long start=System.nanoTime();
            try {
                var result=adapter.query(request());long millis=java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start);
                assertThat(accepted.getCount()).isZero();assertThat(result.status()).isEqualTo(ResultStatus.TIMEOUT);
                assertThat(millis).isBetween(1500L,5000L);assertThat(calls).hasValue(0);
            } finally {release.countDown();peer.get(5,java.util.concurrent.TimeUnit.SECONDS);}
        }
    }
}
