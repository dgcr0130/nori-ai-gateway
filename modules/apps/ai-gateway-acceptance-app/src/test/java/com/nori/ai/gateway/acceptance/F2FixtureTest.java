package com.nori.ai.gateway.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** F2 고정 기대값이 배포본 Manifest(앱 classpath 정본)와 같은지, 사전등록 rubric 이 형식·금지 표현 규칙을 지키는지 고정한다. */
class F2FixtureTest {
    private static JsonNode manifest(Path directory, String entry) throws Exception { return CanonicalJson.MAPPER.readTree(Files.readString(directory.resolve(entry.substring(entry.lastIndexOf('/') + 1)))); }

    @Test void pinnedCapabilityListsAndManifestSnippetMatchTheApplicationManifests() throws Exception {
        Path config=Path.of(System.getProperty("aigw.rootDir"),"config/ai-gateway.yaml");var implemented=new ArrayList<String>();String snippetSource=null;
        for(String line:Files.readAllLines(config)){String text=line.strip();if(text.startsWith("- {\"id\":")){var value=CanonicalJson.MAPPER.readTree(text.substring(2));implemented.add(value.path("id").asText());if(snippetSource==null)snippetSource=value.path("description").asText();}}
        implemented.add("logs.search");implemented.add("trace.search");assertThat(F2Fixture.IMPLEMENTED).containsExactlyElementsOf(implemented);assertThat(F2Fixture.GQ_HELP_ROWS).containsExactlyElementsOf(implemented);assertThat(F2Fixture.PLANNED).isEmpty();assertThat(snippetSource).startsWith(F2Fixture.GQ_MANIFEST_SNIPPET);
        assertThat(F2Fixture.GQ_MANIFEST_SNIPPET.codePointCount(0, F2Fixture.GQ_MANIFEST_SNIPPET.length())).isBetween(1, 64);
    }

    @Test void preregisteredRubricBindsFixturesToLeavesAndScriptsOnlyTheGeneralQueryComposer() {
        for (var fixture : F2Fixture.fixtures("AC-22-GQ-BYPASS-DATA")) {
            assertThat(F2Fixture.forbiddenHit(fixture, fixture.composerScript().orElseThrow())).as("사전등록 고정 서술은 자기 금지 표현에 걸리지 않는다").isFalse();
            assertThat(F2Fixture.forbiddenHit(fixture, "EQP_001 은 CONNECTED 상태입니다")).isTrue();
        }
        for (var fixture : F2Fixture.fixtures("AC-22-GQ-BYPASS-XSYS")) {
            assertThat(F2Fixture.forbiddenHit(fixture, fixture.composerScript().orElseThrow())).isFalse();
            assertThat(F2Fixture.forbiddenHit(fixture, "LOT-001 재고는 120 개입니다")).isTrue();
        }
        for (var fixture : F2Fixture.fixtures("AC-22-LIVE")) assertThat(F2Fixture.forbiddenHit(fixture, "wm.inventory.get 으로 WM 에서 조회했다")).isTrue();
        var target = new RubricViewer.Target("AC-22-LIVE", "f2-live-incident-comm", "job-1", "a".repeat(64));
        assertThat(F2Fixture.claimsFor(target)).hasValueSatisfying(claims -> assertThat(claims).containsEntry("sub", F2Fixture.SUB).containsEntry("perm", F1Fixture.PERMISSIONS).doesNotContainKey("dlg"));
        assertThat(F2Fixture.claimsFor(new RubricViewer.Target("AC-22-GQ-BYPASS-DATA", "f2-live-incident-comm", "job-1", "a".repeat(64)))).as("다른 leaf 로 재결속 0").isEmpty();
        assertThat(F1Fixture.claimsFor(target)).as("F1 fixture 와 섞이지 않는다").isEmpty();
        assertThat(F2Fixture.rubricVersion()).isEqualTo("f2-v1");
    }
}
