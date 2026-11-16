package com.nori.ai.gateway.eval;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

/** gate --out: 파생 보고서는 수용 결과 정본(acceptance/results) 밖에 새로만 쓰고, 생략하면 단계 결과 폴더의 acceptance-report.md 를 씁니다. */
class GateReportOutputTest {
    @TempDir Path directory;

    int gate(TestSupport t, ByteArrayOutputStream err, String... extra) {
        List<String> args = new ArrayList<>(List.of("gate", "--stage", "F0", "--parent-run-id", "R1")); args.addAll(List.of(extra));
        return new EvalCli(Map.of("AIGW_SPEC_FILE", t.spec.toString()), new PrintStream(new ByteArrayOutputStream()), new PrintStream(err, true, StandardCharsets.UTF_8), t.root).execute(args.toArray(String[]::new));
    }

    @Test void derivedReportIsWrittenOnlyOutsideResultsWithoutOverwriting() throws Exception {
        TestSupport t = new TestSupport(directory);
        Path canonical = t.root.resolve("acceptance/results/inference-diagnosis/acceptance-report.md");
        for (String invalid : List.of("acceptance/results/R1.md", "acceptance/results/inference-diagnosis/R1.md", "acceptance/results/inference-diagnosis/acceptance-report.md", "../outside/R1.md", "reports/R1.txt")) {
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            assertThat(gate(t, err, "--out", invalid)).as(invalid).isEqualTo(2);
            assertThat(err.toString(StandardCharsets.UTF_8).strip()).isEqualTo("GATE_REPORT_PATH_INVALID");
        }
        assertThat(t.root.resolve("acceptance/results")).doesNotExist();

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        assertThat(gate(t, err, "--out", "reports/R1.md")).isZero();
        Path report = t.root.resolve("reports/R1.md");
        assertThat(Files.readString(report, StandardCharsets.UTF_8)).contains("단계: F0", "부모 회차: R1");
        byte[] before = Files.readAllBytes(report);
        assertThat(gate(t, err, "--out", "reports/R1.md")).isEqualTo(2);
        assertThat(err.toString(StandardCharsets.UTF_8)).contains("OUTPUT_EXISTS");
        assertThat(Files.readAllBytes(report)).isEqualTo(before);
        assertThat(canonical).doesNotExist();

        // --out 생략 시 단계 결과 폴더의 정본 보고서(이 경로만 덮어쓰기)를 씁니다.
        assertThat(gate(t, new ByteArrayOutputStream())).isZero();
        assertThat(canonical).exists();
    }
}
