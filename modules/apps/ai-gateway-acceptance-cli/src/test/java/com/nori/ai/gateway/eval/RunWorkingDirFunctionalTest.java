package com.nori.ai.gateway.eval;

import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class RunWorkingDirFunctionalTest {
    @TempDir Path directory;
    @Test void resolvesSubprojectRunPathsAgainstRepositoryRootWithoutSpecInputs() throws Exception {
        TestKitSupport fixture=new TestKitSupport(directory);fixture.passingTest();
        assertThat(directory.resolve("spec.md")).doesNotExist();
        assertThat(directory.resolve("acceptance/spec-baseline.md")).doesNotExist();
        assertThat(fixture.env.keySet()).noneMatch(k -> k.startsWith("AIGW_SPEC") || k.equals("AIGW_APPROVED_SPEC_SHA"));
        fixture.run(false,":sub:test");
        // 현재 fingerprint는 정식 CLI task가 준비한다. fixture의 JavaExec는 같은 증거를 루트 경로로 읽어야 한다.
        fixture.run(false,":apps:ai-gateway-acceptance-cli:run",fixture.verifyArgs(":sub:test"));
        fixture.run(false,":sub:run",fixture.verifyArgs(":sub:test"));
        assertThat(directory.resolve("build/aigw-fingerprint/_sub_test.json")).exists();
        assertThat(directory.resolve("sub/build/aigw-fingerprint/_sub_test.json")).doesNotExist();
        assertThat(directory.resolve("sub/build/aigw-evidence/test/leaf-results.json")).exists();
    }
    @Test void sanitizedEnvironmentSupportsCiWithoutSpecPackage() throws Exception {
        TestKitSupport fixture=new TestKitSupport(directory);fixture.passingTest();
        assertThat(fixture.env.keySet()).noneMatch(k->k.equals("CI")||k.startsWith("CI_")||k.startsWith("GITLAB_"));
        fixture.env.put("CI","true");
        fixture.run(false,":sub:test");
        fixture.run(false,":apps:ai-gateway-acceptance-cli:run",fixture.verifyArgs(":sub:test"));
        assertThat(Json.read(directory.resolve("sub/build/aigw-evidence/test/leaf-results.json")).path("summary").has("specSha")).isFalse();
    }
}
