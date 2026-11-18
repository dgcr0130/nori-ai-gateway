package com.nori.ai.gateway.integration.loki;

import java.util.*;

public final class LokiSourcePolicy {
    private LokiSourcePolicy() {}
    public static String retentionSourceId(String system,String namespace,String app){return "sources://"+system+"/logs.search/"+namespace+"/"+app;}
    private static String escapeLabel(String value){return value.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");}
    public static String selector(String app,String namespace, List<String> terms) {
        if(app==null||app.isBlank()||namespace==null||namespace.isBlank())throw new IllegalArgumentException("검색 위치가 잘못되었습니다");
        StringBuilder query = new StringBuilder("{app=\"").append(escapeLabel(app)).append("\", namespace=\"").append(escapeLabel(namespace)).append("\"}");
        for (String term : terms) {
            Objects.requireNonNull(term);
            query.append(" |= \"");
            for (int i = 0; i < term.length(); i++) {
                char value = term.charAt(i);
                switch (value) {
                    case '\\' -> query.append("\\\\");
                    case '"' -> query.append("\\\"");
                    case '\n' -> query.append("\\n");
                    case '\r' -> query.append("\\r");
                    case '\t' -> query.append("\\t");
                    default -> { if (value < 32 || value == 127) query.append(String.format(Locale.ROOT, "\\u%04x", (int) value)); else query.append(value); }
                }
            }
            query.append('"');
        }
        return query.toString();
    }
}
