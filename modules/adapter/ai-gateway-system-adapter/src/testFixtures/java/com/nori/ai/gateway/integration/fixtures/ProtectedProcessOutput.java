package com.nori.ai.gateway.integration.fixtures;

import java.util.ArrayDeque;
import java.util.List;
import java.util.regex.Pattern;

/** 출력 원문 대신 예외 타입·빈 이름·고정 하네스 코드만 최대 20줄 보존합니다. */
public final class ProtectedProcessOutput {
    private static final Pattern TYPE = Pattern.compile("(?:[a-zA-Z_$][\\w$]*\\.)+[A-Z_$][\\w$]*(?:Exception|Error)");
    private final ArrayDeque<String> lines = new ArrayDeque<>();
    public synchronized void record(String line) {
        var safe = new StringBuilder();
        var types = TYPE.matcher(line);
        while (types.find()) {
            if (!safe.isEmpty()) safe.append(' ');
            safe.append(types.group());
        }
        if (line.startsWith("HARNESS_STARTUP_BEAN=") && line.substring(21).matches("[A-Za-z][A-Za-z0-9_.]{0,100}")) safe.append(line);
        if (line.equals("HARNESS_CLASSPATH_MISMATCH")) safe.append(line);
        if (safe.isEmpty()) return;
        if (lines.size() == 20) lines.removeFirst();
        lines.addLast(safe.toString());
    }
    public synchronized List<String> tail() { return List.copyOf(lines); }
}
