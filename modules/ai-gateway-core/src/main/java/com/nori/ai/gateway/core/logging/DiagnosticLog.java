package com.nori.ai.gateway.core.logging;

import java.util.*;
import java.util.regex.Pattern;

/** 제품 본문은 보존하고 자격 값만 제거한 뒤 한 줄·유한 크기로 기록합니다. */
public final class DiagnosticLog {
    public static final int PART_CHARS=30_000, MAX_PARTS=10;
    private static final String KEYS="api_key|apikey|api-key|password|passwd|secret|client_secret|token|access_token|refresh_token|id_token|private_key|authorization";
    private static final Pattern PEM=Pattern.compile("-----BEGIN [^-]*PRIVATE KEY-----.*?(?:-----END [^-]*PRIVATE KEY-----|\\z)",Pattern.DOTALL);
    private static final Pattern BEARER=Pattern.compile("(?i)\\bBearer\\s+[^\\s\\\"'<>;,}]+(?:={0,2})");
    private static final Pattern JWT=Pattern.compile("\\beyJ[A-Za-z0-9_-]*\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\b");
    private static final Pattern BASIC_AUTH=Pattern.compile("(?i)(?<![\\w-])([\"']?authorization[\"']?\\s*[:=]\\s*)Basic\\s+[^\\s,\"';}\\]]+");
    private static final Pattern PAIR=Pattern.compile("(?i)(?<![\\w-])([\\\"']?(?:"+KEYS+")[\\\"']?\\s*[:=]\\s*)(\\\"(?:\\\\.|[^\\\"\\\\])*\\\"|'(?:\\\\.|[^'\\\\])*'|[^\\s,;&}\\]]+)");
    private static final Pattern ESCAPED_KEY=Pattern.compile("(?i)((\\\\+)\"(?:"+KEYS+")(\\\\+)\"\\s*[:=]\\s*)");
    private DiagnosticLog() {}
    public static String mask(String value) {
        if(value==null)return "null";
        String safe=PEM.matcher(value).replaceAll("[MASKED]");
        safe=BEARER.matcher(safe).replaceAll("Bearer [MASKED]");
        safe=JWT.matcher(safe).replaceAll("[MASKED]");
        safe=BASIC_AUTH.matcher(safe).replaceAll(match->java.util.regex.Matcher.quoteReplacement(match.group(1)+"\"[MASKED]\""));
        safe=escapedPairs(safe);
        return PAIR.matcher(safe).replaceAll(match->java.util.regex.Matcher.quoteReplacement(match.group(1)+"\"[MASKED]\""));
    }
    /** role별 메시지의 JSON 문자열 안에 재인코딩된 자격 값도 원래 escape 깊이를 유지해 제거합니다. */
    private static String escapedPairs(String value) {
        var matcher=ESCAPED_KEY.matcher(value);var safe=new StringBuilder();int cursor=0;
        while(matcher.find()) {
            if(matcher.start()<cursor)continue;
            int slashes=matcher.group(2).length(),unit=slashes+1;
            if(slashes!=matcher.group(3).length()||(unit&(unit-1))!=0)continue;
            String delimiter="\\".repeat(slashes)+"\"";int start=matcher.end();if(!value.startsWith(delimiter,start))continue;
            int end=start+delimiter.length(),closing=value.length();boolean found=false;
            for(;end<value.length();end++)if(value.charAt(end)=='"') {
                int backslashes=0;for(int i=end-1;i>=0&&value.charAt(i)=='\\';i--)backslashes++;
                if(backslashes%(2L*unit)==slashes){closing=end+1;found=true;break;}
            }
            safe.append(value,cursor,start).append(delimiter).append("[MASKED]").append(delimiter);cursor=closing;
            if(!found)break;
        }
        return cursor==0?value:safe.append(value,cursor,value.length()).toString();
    }
    public static String quote(String value) {
        String safe=mask(value);var result=new StringBuilder(safe.length()+2).append('"');
        for(int i=0;i<safe.length();i++)switch(safe.charAt(i)) {
            case '\\'->result.append("\\\\");case '"'->result.append("\\\"");case '\r'->result.append("\\r");case '\n'->result.append("\\n");case '\t'->result.append("\\t");
            default->{char c=safe.charAt(i);if(c<32)result.append(String.format("\\u%04x",(int)c));else result.append(c);}
        }
        return result.append('"').toString();
    }
    public static Map<String,Object> headers(Map<String,? extends Object> headers) {
        var safe=new LinkedHashMap<String,Object>();headers.forEach((key,value)->safe.put(key,key.toLowerCase(Locale.ROOT).matches(".*(authorization|token|secret|key|assertion|cookie).*" )?"[MASKED]":mask(String.valueOf(value))));return safe;
    }
    public static String fields(Object... fields) {
        var result=new StringBuilder();
        for(int i=0;i+1<fields.length;i+=2)result.append(' ').append(fields[i]).append('=').append(quote(String.valueOf(fields[i]).toLowerCase(Locale.ROOT).matches(KEYS)?"[MASKED]":String.valueOf(fields[i+1])));
        return result.toString();
    }
    public static List<String> lines(String event,String head,String body) {
        String safe=mask(body);int total=safe.length(),cursor=0;var slices=new ArrayList<String>();var lines=new ArrayList<String>();
        do {
            int end=(int)Math.min(total,(long)cursor+PART_CHARS);
            if(end<total&&end>cursor&&Character.isHighSurrogate(safe.charAt(end-1))&&Character.isLowSurrogate(safe.charAt(end)))end--;
            slices.add(safe.substring(cursor,end));cursor=end;
        } while(cursor<total&&slices.size()<MAX_PARTS);
        int parts=slices.size();
        for(int i=0;i<parts;i++) {
            lines.add(event+head+(total>PART_CHARS?" part="+(i+1)+"/"+parts:"")+" body="+quote(slices.get(i))+(i==parts-1&&cursor<total?" truncated=true totalChars="+total:""));
        }
        return List.copyOf(lines);
    }
    public static void body(System.Logger logger,System.Logger.Level level,String event,String body,Object... fields) {
        if(logger.isLoggable(level))lines(event,fields(fields),body).forEach(line->logger.log(level,line));
    }
    /** Source 스트림은 앞부분만 보관하되 UTF-8 분할 문자와 전체 소비 문자 수는 끝까지 셉니다. */
    public static final class Utf8Capture {
        private final java.nio.charset.CharsetDecoder decoder=java.nio.charset.StandardCharsets.UTF_8.newDecoder().onMalformedInput(java.nio.charset.CodingErrorAction.REPLACE).onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPLACE);
        private final java.nio.ByteBuffer pending=java.nio.ByteBuffer.allocate(8192);
        private final java.nio.CharBuffer decoded=java.nio.CharBuffer.allocate(8192);
        private final StringBuilder text=new StringBuilder();private long totalChars;private boolean finished;
        public void accept(byte[] bytes,int offset,int length) {
            while(length>0){int count=Math.min(length,pending.remaining());pending.put(bytes,offset,count);offset+=count;length-=count;decode(false);}
        }
        private void decode(boolean end) {
            pending.flip();java.nio.charset.CoderResult result;
            do{result=decoder.decode(pending,decoded,end);decoded.flip();totalChars+=decoded.remaining();int kept=Math.min(decoded.remaining(),Math.max(0,PART_CHARS*MAX_PARTS+1-text.length()));for(int i=0;i<kept;i++)text.append(decoded.get());decoded.clear();}while(result.isOverflow());pending.compact();
        }
        private void finish(){if(!finished){decode(true);finished=true;}}
    }
    public static void capturedBody(System.Logger logger,System.Logger.Level level,String event,Utf8Capture captured,Object... fields) {
        captured.finish();String text=captured.text.toString();boolean limited=captured.totalChars>text.length();
        if(limited)text=text.replaceAll("\\beyJ[A-Za-z0-9_.-]*$","[MASKED]");
        var lines=new ArrayList<>(lines(event,fields(fields),text));
        if(limited){int last=lines.size()-1;lines.set(last,lines.get(last).replaceFirst(" truncated=true totalChars=[0-9]+$","")+" truncated=true totalChars="+captured.totalChars);}
        lines.forEach(line->logger.log(level,line));
    }
    public static void event(System.Logger logger,System.Logger.Level level,String event,Object... fields) {
        if(!logger.isLoggable(level))return;
        String text=fields(fields);
        if(text.length()<=PART_CHARS)logger.log(level,event+text);else {
            var identifiers=new ArrayList<Object>();for(int i=0;i+1<fields.length;i+=2)if(Set.of("jobId","requestId","conversationId","system","sourceId","stepId").contains(String.valueOf(fields[i]))){identifiers.add(fields[i]);identifiers.add(fields[i+1]);}
            body(logger,level,event,text,identifiers.toArray());
        }
    }
    /** 입력 형식 검증(날짜·JSON 읽기 포함)과 업무 거절은 WARN, 그 밖의 예상 밖 실패는 ERROR로 남깁니다. */
    public static void failure(System.Logger logger,String event,Throwable failure,Object... fields) {
        boolean expected=interrupted(failure)||failure instanceof IllegalArgumentException||failure instanceof java.time.DateTimeException
            ||failure instanceof tools.jackson.core.exc.StreamReadException||failure instanceof tools.jackson.databind.exc.MismatchedInputException
            ||failure.getClass().getSimpleName().matches(".*(Rejected|Blocked|Failure|NotFound|Invalid|Violation|Stopped).*" );
        var level=expected?System.Logger.Level.WARNING:System.Logger.Level.ERROR;
        failure(logger,level,event,failure,fields);
    }
    public static void failure(System.Logger logger,System.Logger.Level level,String event,Throwable failure,Object... fields) {
        body(logger,level,event,failure.getMessage(),fields);
        logger.log(level,event+fields(fields)+fields("exception",failure.getClass().getName()),safeThrowable(failure,new IdentityHashMap<>()));
    }
    public static void transportFailure(System.Logger logger,String event,Throwable failure,Object... fields) {failure(logger,interrupted(failure)?System.Logger.Level.WARNING:System.Logger.Level.ERROR,event,failure,fields);}
    private static boolean interrupted(Throwable failure) {
        var seen=Collections.newSetFromMap(new IdentityHashMap<Throwable,Boolean>());
        for(var value=failure;value!=null&&seen.add(value);value=value.getCause())if(value instanceof InterruptedException||value instanceof java.util.concurrent.CancellationException||value instanceof java.util.concurrent.TimeoutException||value.getClass().getSimpleName().endsWith("TimeoutException"))return true;
        return false;
    }
    /** 자격 파일 파서의 오류에는 원문 조각이 들어갈 수 있어 메시지 대신 타입·스택만 기록합니다. */
    public static void credentialFailure(System.Logger logger,String event,Throwable failure) {
        credentialFailure(logger,System.Logger.Level.ERROR,event,failure);
    }
    public static void credentialFailure(System.Logger logger,System.Logger.Level level,String event,Throwable failure) {
        var safe=new RuntimeException(failure.getClass().getName());safe.setStackTrace(failure.getStackTrace());logger.log(level,event+fields("exception",failure.getClass().getName()),safe);
    }
    private static Throwable safeThrowable(Throwable original,IdentityHashMap<Throwable,Throwable> seen) {
        if(original==null||seen.containsKey(original))return null;
        String message=mask(original.getClass().getName()+": "+original.getMessage());
        var safe=new RuntimeException(message.length()>PART_CHARS?message.substring(0,PART_CHARS)+" [truncated]":message);seen.put(original,safe);safe.setStackTrace(original.getStackTrace());
        Throwable cause=safeThrowable(original.getCause(),seen);if(cause!=null)safe.initCause(cause);
        for(var suppressed:original.getSuppressed()){var value=safeThrowable(suppressed,seen);if(value!=null)safe.addSuppressed(value);}return safe;
    }
}
