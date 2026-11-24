package com.nori.ai.gateway.app.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import static org.assertj.core.api.Assertions.*;

class SseFailureLogTest {
    private final List<ch.qos.logback.classic.Logger> loggers=List.of((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(SseHub.class),(ch.qos.logback.classic.Logger)LoggerFactory.getLogger(GuardedSseWriter.class));
    private final ListAppender<ILoggingEvent> captured=new ListAppender<>(){@Override protected void append(ILoggingEvent event){event.prepareForDeferredProcessing();super.append(event);}};
    private boolean installedBridge;
    @BeforeEach void setup(){installedBridge=!org.slf4j.bridge.SLF4JBridgeHandler.isInstalled();if(installedBridge)org.slf4j.bridge.SLF4JBridgeHandler.install();captured.start();loggers.forEach(logger->logger.addAppender(captured));}
    @AfterEach void cleanup(){loggers.forEach(logger->logger.detachAppender(captured));captured.stop();if(installedBridge)org.slf4j.bridge.SLF4JBridgeHandler.uninstall();}

    /** 운영에서 위젯이 종료 이벤트 뒤 연결을 끊을 때 실제로 받은 두 예외와 끊김 문구는 ERROR가 아니라 INFO 한 줄이다. */
    @Test void clientDisconnectsAreLoggedAsInfo() {
        for(Throwable failure:List.<Throwable>of(
                new AsyncRequestNotUsableException("ServletResponse failed to flushBuffer: java.io.IOException: Connection reset by peer",new IOException("Connection reset by peer")),
                new AsyncRequestNotUsableException("Servlet container error notification for disconnected client"),
                new IOException("Broken pipe"))) {
            captured.list.clear();
            SseFailureLog.record(System.getLogger(SseHub.class.getName()),"SSE_CONNECTION_DISCONNECTED",failure,"jobId","job-1");
            assertThat(captured.list).as(failure.getMessage()).singleElement().satisfies(event->{
                assertThat(event.getLevel()).isEqualTo(Level.INFO);
                assertThat(event.getFormattedMessage()).startsWith("SSE_CLIENT_DISCONNECTED").contains("jobId=\"job-1\"").contains("exception=\""+failure.getClass().getName()+"\"");
            });
        }
    }

    @Test void otherFailuresKeepErrorLevel() {
        SseFailureLog.record(System.getLogger(SseHub.class.getName()),"SSE_CONNECTION_DISCONNECTED",new IOException("No space left on device"),"jobId","job-1");
        assertThat(captured.list).isNotEmpty().allSatisfy(event->assertThat(event.getLevel()).isEqualTo(Level.ERROR));
        assertThat(captured.list.getFirst().getFormattedMessage()).startsWith("SSE_CONNECTION_DISCONNECTED");
    }

    @Test void writerClosesWithoutErrorWhenClientHasGone() {
        var emitter=new SseEmitter(0L){@Override public void send(SseEventBuilder event) throws IOException {throw new AsyncRequestNotUsableException("ServletResponse failed to flushBuffer: java.io.IOException: Connection reset by peer");}};
        var writer=new GuardedSseWriter(emitter,0L);
        assertThat(writer.heartbeat()).isFalse();
        assertThat(writer.closed()).isTrue();
        assertThat(captured.list).isNotEmpty().allSatisfy(event->assertThat(event.getLevel()).isEqualTo(Level.INFO));
        assertThat(captured.list).extracting(ILoggingEvent::getFormattedMessage).anySatisfy(message->assertThat(message).startsWith("SSE_CLIENT_DISCONNECTED"));
    }
}
