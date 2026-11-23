package com.nori.ai.gateway.app.observability;

import java.util.Map;
import org.slf4j.MDC;

/** 실행기·스트림 콜백은 등록 시점의 문맥을 복원하고 호출 스레드 문맥을 반드시 돌려놓습니다. */
public final class LogContext implements AutoCloseable {
    private final Map<String,String> previous=MDC.getCopyOfContextMap();
    private LogContext(Map<String,String> context) {if(context==null)MDC.clear();else MDC.setContextMap(context);}
    public static LogContext open(Map<String,String> context) {return new LogContext(context);}
    public static Runnable capture(Runnable action) {var context=MDC.getCopyOfContextMap();return ()->{try(var ignored=open(context)){action.run();}};}
    @Override public void close() {if(previous==null)MDC.clear();else MDC.setContextMap(previous);}
}
