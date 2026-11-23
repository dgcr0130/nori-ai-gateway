package com.nori.ai.gateway.core.logging;

import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DiagnosticLogTest {
    @Test void masksOnlyCredentialsAndPreservesQuestionAndPersonalData() {
        String input="질문 홍길동 user@example.com 010-1234-5678 Bearer opaque-value {\"API_KEY\":\"abc\",\"password\":\"a b\"} token=def client_secret: ghi eyJhbGci.eyJzdWI.signature";
        assertThat(DiagnosticLog.mask(input)).contains("질문 홍길동 user@example.com 010-1234-5678","[MASKED]").doesNotContain("opaque-value","abc","a b","def","ghi","eyJhbGci");
        for(String key:"api_key apikey api-key password passwd secret client_secret token access_token refresh_token id_token private_key authorization".split(" ")) {
            assertThat(DiagnosticLog.mask("{\""+key.toUpperCase()+"\":\"credential-body\"}")).doesNotContain("credential-body");
            assertThat(DiagnosticLog.mask(key+"='credential-body'")).doesNotContain("credential-body");
        }
        assertThat(DiagnosticLog.mask("-----BEGIN RSA PRIVATE KEY-----\nsecret bytes\n-----END RSA PRIVATE KEY-----")).isEqualTo("[MASKED]");
        assertThat(DiagnosticLog.mask("my_token=keep tokenCount=keep api_key_file=/app/key")).isEqualTo("my_token=keep tokenCount=keep api_key_file=/app/key");
        assertThat(DiagnosticLog.mask("authorization: Basic dXNlcjpwYXNz")).doesNotContain("dXNlcjpwYXNz");
    }
    @Test void masksHeadersCaseInsensitively() {
        assertThat(DiagnosticLog.headers(Map.of("Authorization","opaque","X-Actor-Assertion","signed","Cookie","session","X-API-Key","key-body","Accept","application/json")))
            .containsEntry("Authorization","[MASKED]").containsEntry("X-Actor-Assertion","[MASKED]").containsEntry("Cookie","[MASKED]").containsEntry("X-API-Key","[MASKED]").containsEntry("Accept","application/json");
    }
    @Test void escapesWithoutInterpretingBracesOrApostrophes() {
        assertThat(DiagnosticLog.quote("a\\b\"c\r\n\t{'question'}")).isEqualTo("\"a\\\\b\\\"c\\r\\n\\t{'question'}\"");
    }
    @Test void exactSinglePartBoundary() {
        var lines=DiagnosticLog.lines("EVENT"," jobId=\"job\"","x".repeat(30_000));assertThat(lines).hasSize(1);assertThat(lines.getFirst()).doesNotContain(" part=","truncated=true");
    }
    @Test void firstMultiplePartBoundary() {
        var lines=DiagnosticLog.lines("EVENT"," jobId=\"job\"","x".repeat(30_001));assertThat(lines).hasSize(2);assertThat(lines.get(0)).contains("part=1/2");assertThat(lines.get(1)).contains("part=2/2 body=\"x\"");
    }
    @Test void maximumBoundaryAndTruncation() {
        assertThat(DiagnosticLog.lines("EVENT","","x".repeat(300_000))).hasSize(10).noneMatch(line->line.contains("truncated=true"));
        var lines=DiagnosticLog.lines("EVENT"," jobId=\"job\"","x".repeat(300_001));assertThat(lines).hasSize(10).allMatch(line->line.startsWith("EVENT jobId=\"job\""));assertThat(lines.getLast()).endsWith("truncated=true totalChars=300001");
    }
    @Test void masksBeforeSplittingAcrossBoundary() {
        var lines=DiagnosticLog.lines("EVENT","","x".repeat(29_993)+" password=boundary-secret "+"x".repeat(30_000));assertThat(String.join("",lines)).doesNotContain("boundary-secret");
    }
    @Test void inputFormatFailuresAreWarningsAndUnexpectedFailuresAreErrors() {
        var levels=new java.util.ArrayList<System.Logger.Level>();
        var logger=new System.Logger(){
            @Override public String getName(){return "test";}
            @Override public boolean isLoggable(Level level){return true;}
            @Override public void log(Level level,java.util.ResourceBundle bundle,String message,Throwable thrown){levels.add(level);}
            @Override public void log(Level level,java.util.ResourceBundle bundle,String format,Object... params){levels.add(level);}
        };
        var json=tools.jackson.databind.json.JsonMapper.builder().build();
        Throwable malformed=catchThrowable(()->json.readTree("{\"broken\""));
        Throwable mismatched=catchThrowable(()->json.readValue("\"text\"",Integer.class));
        assertThat(malformed).isInstanceOf(tools.jackson.core.exc.StreamReadException.class);
        assertThat(mismatched).isInstanceOf(tools.jackson.databind.exc.MismatchedInputException.class);
        for(Throwable expected:java.util.List.of(new java.time.DateTimeException("날짜 아님"),malformed,mismatched,new NumberFormatException("숫자 아님"))) {
            levels.clear();DiagnosticLog.failure(logger,"EVENT",expected);
            assertThat(levels).as(expected.getClass().getName()).isNotEmpty().containsOnly(System.Logger.Level.WARNING);
        }
        levels.clear();DiagnosticLog.failure(logger,"EVENT",new IllegalStateException("예상 밖"));
        assertThat(levels).isNotEmpty().containsOnly(System.Logger.Level.ERROR);
    }
    @Test void masksCredentialsInsideEncodedLlmMessagesWithoutMaskingOtherFields() {
        String json="{\"token\":\"nested-secret\\\"tail-secret\\\\\",\"question\":\"홍길동 user@example.com\"}";
        for(int depth=0;depth<4;depth++) {
            assertThat(DiagnosticLog.mask(json)).doesNotContain("nested-secret","tail-secret").contains("홍길동 user@example.com");
            json=json.replace("\\","\\\\").replace("\"","\\\"");
        }
    }
}
