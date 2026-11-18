package com.nori.ai.gateway.integration.system;

import com.nori.ai.gateway.core.job.CancellationToken;
import com.nori.ai.gateway.core.plan.InputValidator;
import com.nori.ai.gateway.core.port.SystemQueryPort;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.ResultStatus;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import java.io.ByteArrayOutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Flow;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

/** 한 호출의 deadline·수신 상한·취소를 같은 전송 수명 안에서 검사합니다. */
@Component
public final class HttpSystemQueryAdapter implements SystemQueryPort {
    public static final int MAX_REQUEST_BYTES = 64 * 1024, MAX_RESPONSE_BYTES = 1024 * 1024;
    public static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final tools.jackson.databind.json.JsonMapper STRICT_JSON = SchemaContracts.JSON.rebuild()
        .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
    private static final Set<String> ENVELOPE = Set.of("version", "systemId", "jobId", "toolId", "status", "observedAt",
        "data", "missingFields", "warnings", "retentionMeta", "errorCode");
    private static final Map<Integer,String> HTTP_ERRORS = Map.of(400,"INPUT_INVALID",401,"SERVICE_AUTH_INVALID",
        403,"DELEGATION_DENIED",404,"TOOL_NOT_FOUND",409,"DELEGATION_CHANGED",429,"SOURCE_BUSY",503,"SOURCE_UNAVAILABLE",504,"TIMEOUT");
    private static final Map<Integer,ResultStatus> HTTP_STATUSES = Map.of(400,ResultStatus.FAILED,401,ResultStatus.DENIED,
        403,ResultStatus.DENIED,404,ResultStatus.FAILED,409,ResultStatus.DENIED,429,ResultStatus.SOURCE_UNAVAILABLE,
        503,ResultStatus.SOURCE_UNAVAILABLE,504,ResultStatus.TIMEOUT);
    private final SystemQuerySettings settings;
    private final Clock clock;
    private final ContentSanitizer sanitizer;

    public HttpSystemQueryAdapter(SystemQuerySettings settings, Clock clock, ContentSanitizer sanitizer) {
        this.settings = Objects.requireNonNull(settings); this.clock = Objects.requireNonNull(clock); this.sanitizer = Objects.requireNonNull(sanitizer);
    }

    @Override public Response query(Request request) {
        // JVM wire 로그가 켜져 있으면 위임·서비스 토큰의 전송 자체를 막습니다.
        if (!System.getProperty("jdk.httpclient.HttpClient.log","").isBlank()
                || !System.getProperty("jdk.internal.httpclient.debug","").isBlank()
                || !System.getProperty("javax.net.debug","").isBlank()
                || java.util.logging.Logger.getLogger("jdk.internal.httpclient.debug").isLoggable(java.util.logging.Level.FINE)
                || java.util.logging.Logger.getLogger("jdk.internal.httpclient.hpack.debug").isLoggable(java.util.logging.Level.FINE))
            return failure(ResultStatus.FAILED,"CONTRACT_INVALID");
        Instant now = clock.instant();
        Duration remaining = request.cancellation().remaining(Duration.ofMillis(request.tool().timeoutMs()));
        if (remaining.isZero() || remaining.isNegative()) return failure(ResultStatus.TIMEOUT, "TIMEOUT");
        if (!request.scope().perm().containsAll(request.tool().requiredPermission())) return failure(ResultStatus.DENIED, "DELEGATION_DENIED");
        var endpoint = settings.find(request.scope().boundSystem()).orElse(null);
        if (endpoint == null) return failure(ResultStatus.DENIED, "DELEGATION_DENIED");
        final ObjectNode arguments;
        try {
            if (new InputValidator().validate(request.tool(), request.arguments(), request.timezone()) != null) return failure(ResultStatus.FAILED,"PLAN_INVALID");
            arguments = (ObjectNode) request.arguments();
            for (String key : List.of("from", "to")) if (arguments.hasNonNull(key))
                arguments.put(key, new InputValidator().parseTime(arguments.get(key).asString(), request.timezone()).toString());
            if (request.targets().size() > 16 || request.targets().stream().anyMatch(value -> !text(value.type(),256) || !text(value.ref(),256)))
                return failure(ResultStatus.FAILED,"PLAN_INVALID");
        } catch (RuntimeException invalid) { return failure(ResultStatus.FAILED,"PLAN_INVALID"); }
        Instant deadline = now.plus(remaining);
        var body = SchemaContracts.JSON.createObjectNode().put("version","1").put("systemId",request.scope().boundSystem().name())
            .put("jobId",request.scope().requestId()).put("toolId",request.tool().toolId()).put("subject",request.scope().sub())
            .put("delegationId",request.delegationId()).put("permissionDigest",request.scope().pdg()).put("deadline",deadline.toString());
        body.set("arguments",arguments); body.set("targets",SchemaContracts.JSON.valueToTree(request.targets()));
        byte[] bytes = SchemaContracts.JSON.writeValueAsBytes(body);
        if (bytes.length > MAX_REQUEST_BYTES) return failure(ResultStatus.FAILED,"CONTRACT_INVALID");
        if (!request.scope().pdg().matches("[0-9a-f]{64}")) return failure(ResultStatus.FAILED,"CONTRACT_INVALID");
        final String token;
        try {
            token = Files.readString(endpoint.gatewayTokenFile(),StandardCharsets.UTF_8).strip();
            if (token.isEmpty() || !token.matches("[A-Za-z0-9._~+/-]+=*")) return failure(ResultStatus.FAILED,"CONTRACT_INVALID");
        } catch (java.io.IOException unavailable) { return failure(ResultStatus.SOURCE_UNAVAILABLE,"SOURCE_UNAVAILABLE"); }
        Duration left = Duration.between(clock.instant(),deadline);
        if (left.isNegative() || left.isZero() || request.cancellation().reason().isPresent()) return failure(ResultStatus.TIMEOUT,"TIMEOUT");
        try (var client = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).followRedirects(HttpClient.Redirect.NEVER)
                .version(HttpClient.Version.HTTP_1_1).build()) {
            var httpRequest = HttpRequest.newBuilder(endpoint.backendUrl().resolve("/internal/ai/v1/query")).timeout(left)
                .header("Content-Type","application/json").header("Authorization","Bearer " + token)
                .header("X-Request-Id",request.scope().requestId()).header("X-Actor-Subject",request.scope().sub())
                .header("X-Delegation-Id",request.delegationId()).POST(HttpRequest.BodyPublishers.ofByteArray(bytes)).build();
            var future = client.sendAsync(httpRequest, info -> new BoundedBody());
            var inFlight = new CancellationToken.InFlightCall() {
                @Override public void abort() { future.cancel(true); client.shutdownNow(); }
                @Override public boolean awaitTermination(Duration timeout) throws InterruptedException { return client.awaitTermination(timeout); }
            };
            request.cancellation().register(inFlight);
            try {
                var response = future.get(left.toNanos(),TimeUnit.NANOSECONDS);
                if (request.cancellation().reason().isPresent() || !clock.instant().isBefore(deadline)) return failure(ResultStatus.TIMEOUT,"TIMEOUT");
                try {
                    String json = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(response.body())).toString();
                    JsonNode parsed = STRICT_JSON.readTree(json);
                    // JSON 문자열의 Unicode 불변식도 공용 계약과 같게 검사합니다.
                    return decode(request,response.statusCode(),SchemaContracts.parse(SchemaContracts.JSON.writeValueAsString(parsed)));
                } catch (RuntimeException | java.nio.charset.CharacterCodingException invalid) { return failure(ResultStatus.FAILED,"CONTRACT_INVALID"); }
            } catch (TimeoutException timeout) {
                inFlight.abort(); return failure(ResultStatus.TIMEOUT,"TIMEOUT");
            } catch (ExecutionException failed) {
                Throwable cause = failed.getCause();
                if (cause instanceof BodyLimit) return failure(ResultStatus.FAILED,"CONTRACT_INVALID");
                if (cause instanceof HttpTimeoutException) return failure(ResultStatus.TIMEOUT,"TIMEOUT");
                return failure(ResultStatus.SOURCE_UNAVAILABLE,"SOURCE_UNAVAILABLE");
            } catch (InterruptedException interrupted) {
                inFlight.abort(); Thread.currentThread().interrupt(); return failure(ResultStatus.TIMEOUT,"TIMEOUT");
            } catch (CancellationException cancelled) { return failure(ResultStatus.TIMEOUT,"TIMEOUT"); }
            finally {
                client.shutdownNow();
                // 실제 transport 종료를 확인할 때까지 token의 in-flight 등록을 보존합니다.
                boolean interrupted = Thread.interrupted();
                for (;;) try { if (client.awaitTermination(Duration.ofSeconds(1))) break; }
                    catch (InterruptedException again) { interrupted = true; }
                request.cancellation().completed(inFlight);
                if (interrupted) Thread.currentThread().interrupt();
            }
        } catch (RuntimeException invalid) { return failure(ResultStatus.SOURCE_UNAVAILABLE,"SOURCE_UNAVAILABLE"); }
    }

    private Response decode(Request request, int http, JsonNode node) {
        keys(node,ENVELOPE);
        equal(node,"version","1"); equal(node,"systemId",request.scope().boundSystem().name());
        equal(node,"jobId",request.scope().requestId()); equal(node,"toolId",request.tool().toolId());
        if (!node.get("status").isString()) throw invalid();
        ResultStatus status = ResultStatus.valueOf(node.get("status").asString());
        if (status == ResultStatus.ACCEPTED_ASYNC) throw invalid();
        String error = null;
        if (http == 200) {
            if (!node.get("errorCode").isNull() || Set.of(ResultStatus.DENIED,ResultStatus.SOURCE_UNAVAILABLE,ResultStatus.TIMEOUT,ResultStatus.FAILED).contains(status)) throw invalid();
        } else {
            if (!HTTP_ERRORS.containsKey(http) || status != HTTP_STATUSES.get(http)) throw invalid();
            equal(node,"errorCode",HTTP_ERRORS.get(http)); error = HTTP_ERRORS.get(http);
        }
        Instant observed = utc(node.get("observedAt"));
        List<String> missing = strings(node.get("missingFields"),100,128);
        List<String> warnings = strings(node.get("warnings"),1,128);
        if (new HashSet<>(warnings).size() != warnings.size() || warnings.stream().anyMatch(value -> !value.equals("RETENTION_UNVERIFIED"))) throw invalid();
        JsonNode retention = node.get("retentionMeta"); keys(retention,Set.of("missingRanges","unverifiedRanges"));
        List<Range> missingRanges = ranges(retention.get("missingRanges")), unverified = ranges(retention.get("unverifiedRanges"));
        JsonNode data = node.get("data");
        if (Set.of(ResultStatus.SUCCESS,ResultStatus.EMPTY,ResultStatus.PARTIAL,ResultStatus.STALE,ResultStatus.CONFLICT).contains(status)) {
            keys(data,Set.of("items")); JsonNode items = data.get("items");
            if (!items.isArray() || items.size() > 100 || status == ResultStatus.SUCCESS && items.isEmpty() || status == ResultStatus.EMPTY && !items.isEmpty()) throw invalid();
            for (JsonNode item : items) {
                keys(item,Set.of("ref","text","values"));
                if (!item.get("ref").isString() || !text(item.get("ref").asString(),256) || !item.get("text").isString()
                        || !text(item.get("text").asString(),4000) || !item.get("values").isObject()
                        || !SchemaContracts.matches(request.tool().outputSchema(),item.get("values"))) throw invalid();
            }
            if (status == ResultStatus.SUCCESS && (!missing.isEmpty() || !missingRanges.isEmpty() || !unverified.isEmpty())) throw invalid();
            if (status == ResultStatus.PARTIAL && missing.isEmpty() && missingRanges.isEmpty()) throw invalid();
            data = mask(data);
            for (JsonNode item : data.get("items")) if (!SchemaContracts.matches(request.tool().outputSchema(),item.get("values"))) throw invalid();
        } else if (!data.isNull()) throw invalid();
        return new Response(status,observed,data.isNull()?null:data,missing.stream().map(value -> sanitizer.sanitize(value).text()).toList(),
            warnings,missingRanges,unverified,error);
    }
    private JsonNode mask(JsonNode node) {
        if (node.isString()) return SchemaContracts.JSON.valueToTree(sanitizer.sanitize(node.asString()).text());
        if (node.isObject()) {
            var result = SchemaContracts.JSON.createObjectNode();
            for (var entry : node.properties()) {
                String key = sanitizer.sanitize(entry.getKey()).text();
                if (result.has(key)) throw invalid();
                result.set(key,mask(entry.getValue()));
            }
            return result;
        }
        if (node.isArray()) { var result = SchemaContracts.JSON.createArrayNode(); node.forEach(value -> result.add(mask(value))); return result; }
        return node.deepCopy();
    }
    private static List<Range> ranges(JsonNode node) {
        if (!node.isArray() || node.size()>100) throw invalid(); var result = new ArrayList<Range>();
        for (JsonNode value : node) { keys(value,Set.of("from","to")); result.add(new Range(utc(value.get("from")),utc(value.get("to")))); }
        return List.copyOf(result);
    }
    private static Instant utc(JsonNode node) {
        if (node == null || !node.isString() || !node.asString().matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(?:\\.[0-9]{1,9})?Z")) throw invalid();
        return Instant.parse(node.asString());
    }
    private static List<String> strings(JsonNode node,int max,int length) {
        if (!node.isArray() || node.size()>max) throw invalid(); var result = new ArrayList<String>();
        for (JsonNode value : node) { if (!value.isString() || !text(value.asString(),length)) throw invalid(); result.add(value.asString()); } return List.copyOf(result);
    }
    private static boolean text(String value,int max) { return value != null && value.codePointCount(0,value.length())>=1 && value.codePointCount(0,value.length())<=max; }
    private static void keys(JsonNode node,Set<String> keys) { if (node == null || !node.isObject() || !Set.copyOf(node.propertyNames()).equals(keys)) throw invalid(); }
    private static void equal(JsonNode node,String key,String expected) { if (!node.get(key).isString() || !node.get(key).asString().equals(expected)) throw invalid(); }
    private static IllegalArgumentException invalid() { return new IllegalArgumentException("시스템 조회 응답 계약이 잘못되었습니다"); }
    private Response failure(ResultStatus status,String code) { return new Response(status,clock.instant(),null,List.of(),List.of(),List.of(),List.of(),code); }
    private static final class BodyLimit extends RuntimeException {}
    private static final class BoundedBody implements HttpResponse.BodySubscriber<byte[]> {
        private final CompletableFuture<byte[]> body = new CompletableFuture<>();
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private Flow.Subscription subscription;
        @Override public CompletionStage<byte[]> getBody() { return body; }
        @Override public void onSubscribe(Flow.Subscription value) { subscription=value; value.request(1); }
        @Override public void onNext(List<ByteBuffer> buffers) {
            for (var buffer : buffers) {
                if (buffer.remaining() > MAX_RESPONSE_BYTES - bytes.size()) { subscription.cancel(); body.completeExceptionally(new BodyLimit()); return; }
                byte[] part = new byte[buffer.remaining()]; buffer.get(part); bytes.writeBytes(part);
            }
            subscription.request(1);
        }
        @Override public void onError(Throwable failure) { body.completeExceptionally(failure); }
        @Override public void onComplete() { body.complete(bytes.toByteArray()); }
    }
}
