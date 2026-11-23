package com.nori.ai.gateway.app.observability;

import com.nori.ai.gateway.core.logging.DiagnosticLog;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/** 인증 단계의 본문 읽기도 캐시하고 비동기 전환 시에는 원 응답으로 즉시 넘깁니다. */
public final class HttpDiagnosticFilter implements Filter {
    public static final String REQUEST_ID_ATTRIBUTE=HttpDiagnosticFilter.class.getName()+".requestId";
    public static final String CONTEXT_ATTRIBUTE=HttpDiagnosticFilter.class.getName()+".context";
    private static final System.Logger LOG=System.getLogger(HttpDiagnosticFilter.class.getName());
    @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException,ServletException {
        var http=(HttpServletRequest)request;var output=(HttpServletResponse)response;
        if(http.getRequestURI().equals("/actuator")||http.getRequestURI().startsWith("/actuator/")){chain.doFilter(request,response);return;}
        try(var logContext=LogContext.open(MDC.getCopyOfContextMap())) {
        UUID requestId=UUID.randomUUID();http.setAttribute(REQUEST_ID_ATTRIBUTE,requestId);MDC.put("requestId",requestId.toString());long started=System.nanoTime();
        var input=new ContentCachingRequestWrapper(http,Integer.MAX_VALUE);
        boolean stream=http.isAsyncStarted()||http.getRequestURI().matches(".*/analyses/[^/]+/events/?")||Optional.ofNullable(http.getHeader("Accept")).orElse("").contains("text/event-stream")||Optional.ofNullable(http.getContentType()).orElse("").contains("text/event-stream");
        var cached=stream?null:new AsyncResponse(output);
        var headers=new LinkedHashMap<String,Object>();Collections.list(http.getHeaderNames()).forEach(name->headers.put(name,name.equalsIgnoreCase("X-Delegation-Id")?List.of("[MASKED]"):Collections.list(http.getHeaders(name))));
        DiagnosticLog.event(LOG,System.Logger.Level.INFO,"HTTP_REQUEST_RECEIVED","requestId",requestId,"method",http.getMethod(),"path",http.getRequestURI(),"query",http.getQueryString(),"remoteAddress",http.getRemoteAddr(),"contentType",http.getContentType(),"contentLength",http.getContentLengthLong(),"headers",DiagnosticLog.headers(headers));
        var forwarding=new HttpServletRequestWrapper(input) {
            private void release() {if(cached!=null)try{cached.release();}catch(IOException failure){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(HttpDiagnosticFilter.class.getName()),"HTTP_DIAGNOSTIC_FILTER_FAILED",failure);throw new IllegalStateException("비동기 응답을 시작할 수 없습니다",failure);}}
            @Override public AsyncContext startAsync() {release();return super.startAsync(this,output);}
            @Override public AsyncContext startAsync(ServletRequest value,ServletResponse target) {release();return super.startAsync(value,target==cached?output:target);}
        };
        boolean failed=false;
        try {chain.doFilter(forwarding,cached==null?output:cached);}
        catch(IOException|ServletException|RuntimeException failure){failed=true;DiagnosticLog.failure(LOG,System.Logger.Level.ERROR,"HTTP_REQUEST_FAILED",failure,"requestId",requestId,"path",http.getRequestURI());throw failure;}
        finally {
            @SuppressWarnings("unchecked") var completedContext=(Map<String,String>)http.getAttribute(CONTEXT_ATTRIBUTE);
            try(var completed=LogContext.open(completedContext==null?MDC.getCopyOfContextMap():completedContext)) {
                DiagnosticLog.body(LOG,System.Logger.Level.INFO,"HTTP_REQUEST_BODY",assertionSafe(text(input.getContentAsByteArray(),input.getCharacterEncoding())),"requestId",requestId,"method",http.getMethod(),"path",http.getRequestURI());
                if(cached!=null&&!http.isAsyncStarted())DiagnosticLog.body(LOG,System.Logger.Level.INFO,"HTTP_RESPONSE_BODY",text(cached.getContentAsByteArray(),cached.getCharacterEncoding()),"requestId",requestId,"status",cached.getStatus());
                int status=failed?500:output.getStatus();
                DiagnosticLog.event(LOG,status>=500?System.Logger.Level.WARNING:System.Logger.Level.INFO,"HTTP_RESPONSE_SENT","requestId",requestId,"method",http.getMethod(),"path",http.getRequestURI(),"status",status,"durationMs",(System.nanoTime()-started)/1_000_000,"async",http.isAsyncStarted(),"completed",!failed);
            } finally {try{if(cached!=null&&!http.isAsyncStarted())cached.copyBodyToResponse();}finally{MDC.remove("requestId");}}
        }
        }
    }
    private static String text(byte[] bytes,String charset) {
        try{return new String(bytes,charset==null?StandardCharsets.UTF_8:Charset.forName(charset));}
        catch(IllegalArgumentException invalid){DiagnosticLog.failure(LOG,System.Logger.Level.WARNING,"HTTP_LOG_CHARSET_INVALID",invalid);return new String(bytes,StandardCharsets.UTF_8);}
    }
    private static final class AsyncResponse extends ContentCachingResponseWrapper {
        private volatile boolean released;private Map<String,String> logContext=MDC.getCopyOfContextMap();private java.io.PrintWriter cachedWriter,forwardingWriter;private ServletOutputStream cachedStream,forwardingStream;
        AsyncResponse(HttpServletResponse response){super(response);}
        @Override public java.io.PrintWriter getWriter() throws IOException {
            if(forwardingWriter==null){cachedWriter=super.getWriter();forwardingWriter=new java.io.PrintWriter(new java.io.Writer(){
                private java.io.PrintWriter target() throws IOException{return released?getResponse().getWriter():cachedWriter;}
                @Override public void write(char[] value,int offset,int length) throws IOException{target().write(value,offset,length);}
                @Override public void flush() throws IOException{target().flush();}
                @Override public void close() throws IOException{target().close();}
            });}return forwardingWriter;
        }
        @Override public ServletOutputStream getOutputStream() throws IOException {
            if(forwardingStream==null){cachedStream=super.getOutputStream();forwardingStream=new ServletOutputStream(){
                private ServletOutputStream target() throws IOException{return released?getResponse().getOutputStream():cachedStream;}
                @Override public void write(int value) throws IOException{target().write(value);}
                @Override public void write(byte[] value,int offset,int length) throws IOException{target().write(value,offset,length);}
                @Override public void flush() throws IOException{target().flush();}
                @Override public void close() throws IOException{target().close();}
                @Override public boolean isReady(){try{return target().isReady();}catch(IOException failure){failed(failure);throw new IllegalStateException(failure);}}
                @Override public void setWriteListener(WriteListener listener){try{target().setWriteListener(listener);}catch(IOException failure){failed(failure);throw new IllegalStateException(failure);}}
            };}return forwardingStream;
        }
        // 비동기 전환은 최종 길이 확정·flush 없이 이미 쓴 바이트만 넘겨 이후 스트림을 보존합니다.
        void release() throws IOException {
            if(released)return;
            logContext=MDC.getCopyOfContextMap();
            if(cachedWriter!=null){cachedWriter.flush();byte[] bytes=getContentAsByteArray();resetBuffer();getResponse().getWriter().write(new String(bytes,Charset.forName(getCharacterEncoding())));}
            else copyBodyToResponse(false);
            released=true;
        }
        private void failed(IOException failure){try(var ignored=LogContext.open(logContext)){DiagnosticLog.failure(LOG,"HTTP_ASYNC_STREAM_FAILED",failure);}}
    }
    // contextRef는 본문으로 전달되는 인증 단언이므로 JWT 형식이 잘못된 값도 기록하지 않습니다.
    private static String assertionSafe(String text) {return text.replaceAll("(\"contextRef\"\\s*:\\s*)\"(?:\\\\.|[^\"\\\\])*\"","$1\"[MASKED]\"");}
}
