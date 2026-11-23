package com.nori.ai.gateway.app.observability;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.nori.ai.gateway.app.security.ServiceTokenAuthFilter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;
import org.springframework.mock.web.*;
import org.springframework.web.util.ContentCachingResponseWrapper;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpDiagnosticFilterTest {
    private final ch.qos.logback.classic.Logger logger=(ch.qos.logback.classic.Logger)LoggerFactory.getLogger(HttpDiagnosticFilter.class);
    private final ListAppender<ILoggingEvent> captured=new ListAppender<>(){@Override protected void append(ILoggingEvent event){event.prepareForDeferredProcessing();super.append(event);}};
    private boolean installedBridge;
    @BeforeEach void setup(){installedBridge=!org.slf4j.bridge.SLF4JBridgeHandler.isInstalled();if(installedBridge)org.slf4j.bridge.SLF4JBridgeHandler.install();captured.start();logger.addAppender(captured);}
    @AfterEach void cleanup(){logger.detachAppender(captured);captured.stop();MDC.clear();if(installedBridge)org.slf4j.bridge.SLF4JBridgeHandler.uninstall();}
    private String logs(){return captured.list.stream().map(ILoggingEvent::getFormattedMessage).reduce("",String::concat);}
    @Test void bodiesCredentialsResponseAndRequestIdAreCorrect() throws Exception {
        var request=new MockHttpServletRequest("POST","/v1/query");request.setCharacterEncoding("UTF-8");request.setContentType("application/json");request.setContent("{\"question\":\"질문 원문\",\"password\":\"body-secret\"}".getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization","Bearer service-secret");request.addHeader("X-Actor-Assertion","opaque-assertion");var response=new MockHttpServletResponse();
        new HttpDiagnosticFilter().doFilter(request,response,(input,output)->{
            assertThat(MDC.get("requestId")).isEqualTo(input.getAttribute(HttpDiagnosticFilter.REQUEST_ID_ATTRIBUTE).toString());
            input.getInputStream().readAllBytes();output.setCharacterEncoding("UTF-8");output.getWriter().write("답변 본문");
        });
        assertThat(response.getContentAsString(StandardCharsets.UTF_8)).isEqualTo("답변 본문");assertThat(MDC.get("requestId")).isNull();
        assertThat(logs()).contains("HTTP_REQUEST_RECEIVED","HTTP_REQUEST_BODY","질문 원문","HTTP_RESPONSE_BODY","답변 본문","HTTP_RESPONSE_SENT","[MASKED]").doesNotContain("body-secret","service-secret","opaque-assertion");
    }
    @Test void sseAndAlreadyAsyncRequestsUseOriginalResponse() throws Exception {
        for(boolean async:List.of(false,true)) {
            var request=new MockHttpServletRequest("GET",async?"/v1/async":"/v1/analyses/job/events");request.setAsyncSupported(true);request.setAsyncStarted(async);var response=new MockHttpServletResponse();
            new HttpDiagnosticFilter().doFilter(request,response,(input,output)->assertThat(output).isSameAs(response).isNotInstanceOf(ContentCachingResponseWrapper.class));
        }
    }
    @Test void asyncTransitionUsesOriginalResponseAndDoesNotHoldBytes() throws Exception {
        var request=new MockHttpServletRequest("GET","/v1/async");request.setAsyncSupported(true);var response=new MockHttpServletResponse();
        new HttpDiagnosticFilter().doFilter(request,response,(input,output)->{var writer=output.getWriter();writer.write("prefix-");var context=input.startAsync();assertThat(context.getResponse()).isSameAs(response);assertThat(response.getHeader("Content-Length")).isNull();writer.write("stream");});
        assertThat(response.getContentAsString()).isEqualTo("prefix-stream");assertThat(MDC.get("requestId")).isNull();assertThat(logs()).doesNotContain("HTTP_RESPONSE_BODY");
    }
    @Test void authenticationErrorUsesTheSameRequestIdAsHttpLogs() throws Exception {
        var credentials=mock(com.nori.ai.gateway.app.security.SecurityCredentialFiles.class);var verifier=mock(com.nori.ai.gateway.app.security.ActorAssertionVerifier.class);
        var auth=new ServiceTokenAuthFilter(credentials,verifier,mock(com.nori.ai.gateway.app.security.ScopeAssembler.class),new com.nori.ai.gateway.app.security.BodySystemFieldGuard(),mock(com.nori.ai.gateway.app.security.AuthRejectionAuditor.class),mock(AuditRecorder.class),io.opentelemetry.api.OpenTelemetry.noop());
        var request=new MockHttpServletRequest("POST","/v1/query");var response=new MockHttpServletResponse();
        new HttpDiagnosticFilter().doFilter(request,response,(input,output)->auth.doFilter(input,output,(ignored,unused)->fail("인증 거절 뒤 호출되면 안 됩니다")));
        String id=request.getAttribute(HttpDiagnosticFilter.REQUEST_ID_ATTRIBUTE).toString();assertThat(response.getStatus()).isEqualTo(401);assertThat(response.getContentAsString()).contains(id);assertThat(logs()).contains(id);assertThat(MDC.get("requestId")).isNull();
    }
    @Test void actuatorProducesNoDiagnosticLog() throws Exception {new HttpDiagnosticFilter().doFilter(new MockHttpServletRequest("GET","/actuator/health"),new MockHttpServletResponse(),(input,output)->{});assertThat(captured.list).isEmpty();}
}
