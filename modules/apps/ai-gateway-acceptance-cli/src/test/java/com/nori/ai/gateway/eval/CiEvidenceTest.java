package com.nori.ai.gateway.eval;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nori.ai.gateway.eval.ci.BuildIdentityManifest;
import com.nori.ai.gateway.eval.ci.TarGz;
import com.nori.ai.gateway.eval.io.Json;
import com.nori.ai.gateway.eval.report.EvidenceRecord;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static com.nori.ai.gateway.eval.TestSupport.error;
import static org.assertj.core.api.Assertions.*;

class CiEvidenceTest {
    @TempDir Path directory;
    CiEvidenceFixture x;
    @BeforeEach void fixture() throws Exception { x = new CiEvidenceFixture(directory); }

    private void verify(Path bundle, String sha) { x.evidence().verify(bundle, sha, x.required, BuildIdentityManifest.load(x.identity)); }
    /** pack 을 우회해 정제 요약 하나를 바꾼 묶음을 만든다(manifest 의 파일 SHA 는 새 바이트로 맞춘다). */
    private Path rewrite(Path bundle, String name, byte[] bytes, boolean updateManifest) {
        var entries = new TreeMap<>(TarGz.read(bundle));
        entries.put(name, bytes);
        if (updateManifest) {
            ObjectNode manifest = (ObjectNode) Json.parse(entries.get("bundle.json"));
            ((ObjectNode) manifest.get("files")).put(name, Json.sha(bytes));
            entries.put("bundle.json", Json.canonical(manifest));
        }
        Path out = directory.resolve("rewritten-" + UUID.randomUUID() + ".tgz");
        TarGz.write(out, entries);
        return out;
    }
    private Path withSummary(Path bundle, Map<String,Object> changes) {
        ObjectNode summary = x.summary.deepCopy();
        changes.forEach((key, value) -> summary.set(key, Json.MAPPER.valueToTree(value)));
        EvidenceRecord.seal(summary);
        return rewrite(bundle, "sub/build/aigw-evidence/integrationTest/leaf-results.json", Json.canonical(Json.MAPPER.valueToTree(Map.of("summary", summary, "records", x.records))), true);
    }
    private static byte[] gunzip(Path file) throws IOException { try (var in = new GZIPInputStream(Files.newInputStream(file))) { return in.readAllBytes(); } }

    @Test void packedBundleHoldsOnlySanitizedSummariesAndVerifiesForSameCommit() throws Exception {
        Path bundle = x.pack();
        assertThat(TarGz.read(bundle).keySet()).containsExactly("bundle.json", "sub/build/aigw-evidence/integrationTest/leaf-results.json");
        String content = new String(gunzip(bundle), StandardCharsets.UTF_8);
        assertThat(content).doesNotContain("승인 fixture SPEC", "<?xml", "testcase", "stackTrace");
        var manifest = Json.parse(TarGz.read(bundle).get("bundle.json"));
        assertThat(manifest.path("gitSha").asText()).isEqualTo(CiEvidenceFixture.SHA);
        assertThat(manifest.path("worktreeClean").asBoolean()).isTrue();
        assertThat(manifest.path("dockerServerVersion").asText()).isEqualTo("29.9.2");
        assertThat(manifest.path("testcontainersVersion").asText()).isEqualTo("2.0.3");
        assertThat(manifest.has("specSha")).isFalse();
        verify(bundle, CiEvidenceFixture.SHA);
        // 미등록 모듈의 부재·카탈로그 leaf 부재는 실패가 아니다(부재 판정은 gate 몫).
        assertThat(x.f.cli("ci-evidence", "verify", "--sha", CiEvidenceFixture.SHA, "--bundle", "it-evidence.tgz", "--required", "acceptance/required-integration-tests.txt",
            "--expect-build-identity", "build/package/build-identity.json")).isZero();
    }

    @Test void packMakesNoBundleWhenPrecheckFails() throws Exception {
        x.summary.put("skippedTestCount", 1); x.save();
        error(x::pack, 4, "TESTS_SKIPPED");
        assertThat(directory.resolve("it-evidence.tgz")).doesNotExist();
        x.summary.put("skippedTestCount", 0); x.summary.put("executedTestCount", 0); x.save();
        error(x::pack, 4, "NO_TESTS_EXECUTED");
        assertThat(directory.resolve("it-evidence.tgz")).doesNotExist();
    }


    @Test void verifyRejectsEachBundleDefect() throws Exception {
        Path bundle = x.pack();
        error(() -> verify(directory.resolve("absent.tgz"), CiEvidenceFixture.SHA), 4, "CI_EVIDENCE_BUNDLE_MISSING");
        Files.write(directory.resolve("broken.tgz"), new byte[]{1, 2, 3});
        error(() -> verify(directory.resolve("broken.tgz"), CiEvidenceFixture.SHA), 4, "CI_EVIDENCE_BUNDLE_INVALID");
        error(() -> verify(bundle, "f".repeat(40)), 4, "CI_EVIDENCE_SHA_MISMATCH");
        error(() -> verify(withSummary(bundle, Map.of("skipped", 1, "skippedTestCount", 1)), CiEvidenceFixture.SHA), 4, "TESTS_SKIPPED");
        error(() -> verify(withSummary(bundle, Map.of("failed", 1)), CiEvidenceFixture.SHA), 4, "TESTS_FAILED");
        error(() -> verify(withSummary(bundle, Map.of("executedTestCount", 0)), CiEvidenceFixture.SHA), 4, "NO_TESTS_EXECUTED");
        error(() -> verify(withSummary(bundle, Map.of("buildIdentity", Map.of("gitSha", CiEvidenceFixture.SHA, "runtimeClasspathSha256", "9".repeat(64)))), CiEvidenceFixture.SHA), 4, "BUILD_IDENTITY_MISMATCH");
        // 요약 바이트를 바꾸고 contentSha256 을 다시 봉인하지 않으면 거부한다.
        byte[] tampered = new String(TarGz.read(bundle).get("sub/build/aigw-evidence/integrationTest/leaf-results.json"), StandardCharsets.UTF_8)
            .replace("\"executedTestCount\":1", "\"executedTestCount\":2").getBytes(StandardCharsets.UTF_8);
        error(() -> verify(rewrite(bundle, "sub/build/aigw-evidence/integrationTest/leaf-results.json", tampered, true), CiEvidenceFixture.SHA), 4, "CI_EVIDENCE_CONTENT_MISMATCH");
        error(() -> verify(rewrite(bundle, "sub/build/aigw-evidence/integrationTest/leaf-results.json", tampered, false), CiEvidenceFixture.SHA), 4, "CI_EVIDENCE_CONTENT_MISMATCH");
        error(() -> verify(rewrite(bundle, "sub/build/test-results/integrationTest/TEST-probe.RealIT.xml", "<testcase/>".getBytes(StandardCharsets.UTF_8), true), CiEvidenceFixture.SHA), 4, "CI_EVIDENCE_UNEXPECTED_ENTRY");
        Files.writeString(x.required, ":sub probe.RealIT,probe.OtherIT\n");
        error(() -> verify(bundle, CiEvidenceFixture.SHA), 4, "REQUIRED_CLASS_NOT_EXECUTED");
        // 등록 모듈의 요약이 없으면(NO-SOURCE·미실행) 거부한다.
        Files.writeString(x.required, ":sub probe.RealIT\n:other probe.MissingIT\n");
        error(() -> verify(bundle, CiEvidenceFixture.SHA), 4, "REQUIRED_TASK_SUMMARY_MISSING");
        Files.writeString(x.required, ":sub probe.RealIT\n");
        x.status = " M modules/apps/ai-gateway-app/src/main/java/Changed.java\n";
        Path dirty = x.pack();
        error(() -> verify(dirty, CiEvidenceFixture.SHA), 4, "CI_EVIDENCE_DIRTY");
    }

    // settings 의 projectDir 매핑(:sub → modules/sub)에서 runner 판정·pack 은 기록된 projectDir 의 파일을 읽고, 묶음 항목 이름은 task 경로 기준으로 둔다.
    @Test void runnerAndPackReadTaskEvidenceThroughRecordedProjectDir() throws Exception {
        Files.createDirectories(directory.resolve("modules")); Files.move(directory.resolve("sub"), directory.resolve("modules/sub"));
        Json.write(directory.resolve("build/aigw-fingerprint/_sub_integrationTest.json"), Map.of("inputFingerprint", "a".repeat(64), "projectDir", "modules/sub"));
        assertThat(x.f.cli("test-evidence", "verify", "--tasks", ":sub:integrationTest", "--require-docker", "--expect-build-identity", "build/package/build-identity.json")).isZero();
        Path bundle = x.pack();
        assertThat(TarGz.read(bundle).keySet()).containsExactly("bundle.json", "sub/build/aigw-evidence/integrationTest/leaf-results.json");
        verify(bundle, CiEvidenceFixture.SHA);
    }


}
