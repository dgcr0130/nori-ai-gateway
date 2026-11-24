package com.nori.ai.gateway.app.web;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.persistence.repository.JobEventRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.json.JsonMapper;

public final class GuardedSseWriter {
    private static final Set<String> FIELDS=Set.of("type","jobId","seq","queuePosition","toolId","status","jobStatus","errorCode");
    private final SseEmitter emitter;
    private final Map<String,String> logContext=org.slf4j.MDC.getCopyOfContextMap();
    private final JsonMapper json=JsonMapper.builder().build();private boolean closed;private long cursor;
    public GuardedSseWriter(SseEmitter emitter,long cursor) { this.emitter=emitter;this.cursor=cursor; }
    public synchronized boolean send(String jobId,JobEventRepository.Event event) {
        if(event.externalSuppressed()||event.id()<=cursor)return !closed;
        var payload=json.createObjectNode();var saved=json.readTree(event.payload());
        if(saved.isObject())for(var field:saved.properties())if(FIELDS.contains(field.getKey())&&(field.getValue().isString()||field.getValue().isNumber()||field.getValue().isNull()))payload.set(field.getKey(),field.getValue());
        payload.put("type",event.type());payload.put("jobId",jobId);
        if(!write(SseEmitter.event().id(Long.toString(event.id())).name(event.type()).data(payload)))return false;
        try(var ignored=com.nori.ai.gateway.app.observability.LogContext.open(logContext)){body(System.getLogger(getClass().getName()),INFO,"SSE_EVENT_SENT",json.writeValueAsString(payload),"jobId",jobId,"eventId",event.id(),"type",event.type());}
        cursor=event.id();return true;
    }
    public synchronized boolean heartbeat() { return write(SseEmitter.event().comment("heartbeat")); }
    public synchronized boolean connected() { return write(SseEmitter.event().comment("connected")); }
    private boolean write(SseEmitter.SseEventBuilder event) {
        if(closed)return false;
        try(var ignored=com.nori.ai.gateway.app.observability.LogContext.open(logContext)) { emitter.send(event);return true; }
        catch(IOException|IllegalStateException failure) {SseFailureLog.record(System.getLogger(GuardedSseWriter.class.getName()),"GUARDED_SSE_WRITER_FAILED",failure); complete();return false; }
    }
    public synchronized long cursor() { return cursor; }
    public synchronized boolean closed() { return closed; }
    public synchronized void complete() { if(!closed){closed=true;try(var ignored=com.nori.ai.gateway.app.observability.LogContext.open(logContext)){event(System.getLogger(getClass().getName()),INFO,"SSE_CONNECTION_CLOSED","reason","completed");emitter.complete();}} }
}
