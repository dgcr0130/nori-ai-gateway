package com.nori.ai.gateway.app.web;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.persistence.queue.JobEventPublisher;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public final class SseHub implements AutoCloseable {
    private record Subscription(VerifiedSystemScope scope,String jobId,GuardedSseWriter writer,Map<String,String> logContext) {}
    private final Set<Subscription> subscriptions=ConcurrentHashMap.newKeySet();
    private final EventReplayService replay;
    private final ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().daemon().name("sse-heartbeat").factory());
    public SseHub(EventReplayService replay,Duration heartbeat) {
        if(heartbeat.isZero()||heartbeat.isNegative())throw new IllegalArgumentException("heartbeat 간격이 잘못되었습니다");
        this.replay=replay;
        scheduler.scheduleAtFixedRate(this::heartbeat,heartbeat.toMillis(),heartbeat.toMillis(),TimeUnit.MILLISECONDS);
    }
    public SseEmitter connect(VerifiedSystemScope scope,String jobId,String lastEventId) {
        try(var ignored=com.nori.ai.gateway.app.observability.LogContext.open(org.slf4j.MDC.getCopyOfContextMap())) {
        org.slf4j.MDC.put("jobId",jobId);org.slf4j.MDC.put("ownerSystem",scope.boundSystem().name());
        event(System.getLogger(getClass().getName()),INFO,"SSE_CONNECTION_OPENED","jobId",jobId,"lastEventId",lastEventId);
        long cursor=replay.cursor(scope,jobId,lastEventId);
        var emitter=new SseEmitter(0L);var writer=new GuardedSseWriter(emitter,cursor);var subscription=new Subscription(scope,jobId,writer,org.slf4j.MDC.getCopyOfContextMap());
        Runnable remove=com.nori.ai.gateway.app.observability.LogContext.capture(()->{event(System.getLogger(getClass().getName()),INFO,"SSE_CONNECTION_REMOVED","jobId",jobId);subscriptions.remove(subscription);});
        emitter.onCompletion(remove);emitter.onTimeout(()->{writer.complete();remove.run();});emitter.onError(failure->{try(var context=com.nori.ai.gateway.app.observability.LogContext.open(subscription.logContext())){failure(System.getLogger(getClass().getName()),"SSE_CONNECTION_DISCONNECTED",failure,"jobId",jobId);writer.complete();remove.run();}});
        subscriptions.add(subscription);
        // 재생할 새 이벤트가 없어도 연결 응답을 즉시 시작합니다.
        if(writer.connected())drain(subscription);else subscriptions.remove(subscription);return emitter;
        }
    }
    @EventListener public void published(JobEventPublisher.Published event) { subscriptions.stream().filter(s->s.jobId().equals(event.jobId())).forEach(this::drain); }
    private void drain(Subscription subscription) {
        try(var context=com.nori.ai.gateway.app.observability.LogContext.open(subscription.logContext())) {
        synchronized(subscription.writer()) {
            try {for(var event:replay.after(subscription.scope(),subscription.jobId(),subscription.writer().cursor()))if(!subscription.writer().send(subscription.jobId(),event))break;}
            catch(RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(SseHub.class.getName()),"SSE_HUB_FAILED",failure);subscription.writer().complete();}
            if(subscription.writer().closed())subscriptions.remove(subscription);
        }
        }
    }
    public void heartbeat() { for(var subscription:subscriptions)if(!subscription.writer().heartbeat())subscriptions.remove(subscription); }
    @Override public void close() {scheduler.shutdownNow();subscriptions.forEach(s->s.writer().complete());subscriptions.clear();}
}
