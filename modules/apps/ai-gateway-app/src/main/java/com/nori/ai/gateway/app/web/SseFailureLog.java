package com.nori.ai.gateway.app.web;

import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.core.logging.DiagnosticLog;
import java.util.Arrays;
import org.springframework.web.util.DisconnectedClientHelper;

/** 클라이언트가 SSE 연결을 끊은 경우는 정상 종료로 INFO에 남기고, 그 밖의 쓰기·연결 실패만 기존 실패 수준으로 남깁니다. */
final class SseFailureLog {
    private SseFailureLog() {}
    static void record(System.Logger logger,String failureEvent,Throwable failure,Object... fields) {
        if(!DisconnectedClientHelper.isClientDisconnectedException(failure)) {DiagnosticLog.failure(logger,failureEvent,failure,fields);return;}
        var withException=Arrays.copyOf(fields,fields.length+2);withException[fields.length]="exception";withException[fields.length+1]=failure.getClass().getName();
        DiagnosticLog.event(logger,INFO,"SSE_CLIENT_DISCONNECTED",withException);
    }
}
