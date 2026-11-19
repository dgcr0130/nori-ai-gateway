package com.nori.ai.gateway.integration.storage;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;

class MarkdownDocumentExtractorTest {
    final MarkdownDocumentExtractor extractor=new MarkdownDocumentExtractor(new ContentSanitizer());
    final DocumentCorpus.Source source=new DocumentCorpus.Source("fixture-bucket","",Set.of("READ"));
    final DocumentCorpus.Limits limits=new DocumentCorpus.Limits(1048576,200,450,60);
    @TestFactory Stream<DynamicTest> sharedRawFixturesMatchSerializationCodepointsAndEveryOriginalLine() throws Exception {
        var directory=Path.of(getClass().getResource("/markdown/expected.json").toURI()).getParent();
        var expected=SchemaContracts.JSON.readTree(Files.readString(directory.resolve("expected.json")));
        return java.util.stream.StreamSupport.stream(expected.spliterator(),false).map(row->DynamicTest.dynamicTest(row.path("file").asString(),()->{
            byte[] raw=Files.readAllBytes(directory.resolve(row.path("file").asString()));
            assertThat(MarkdownDocumentExtractor.sha(raw)).isEqualTo(row.path("revision").asString());
            var result=extractor.extract(source,row.path("key").asString(),new DocumentObjectStore.Content(row.path("revision").asString(),raw,false),limits);
            if(row.has("reason")) {
                assertThat(result.parents()).isEmpty();assertThat(result.issues()).singleElement().satisfies(issue->{assertThat(issue.reason()).isEqualTo(row.path("reason").asString());assertThat(issue.line()).isEqualTo(row.path("line").asInt());});return;
            }
            assertThat(result.issues()).isEmpty();assertThat(result.parents()).hasSize(row.path("parents").size());
            for(int i=0;i<result.parents().size();i++) {
                var parent=result.parents().get(i);var oracle=row.path("parents").get(i);
                assertThat(parent.bodyMasked()).isEqualTo(oracle.path("body").asString());
                assertThat(parent.bodyMasked().codePointCount(0,parent.bodyMasked().length())).isEqualTo(oracle.path("chars").asInt());
                assertThat(parent.location().bucket()).isEqualTo("fixture-bucket");assertThat(parent.location().section()).isEqualTo(oracle.path("section").asString());
                assertThat(parent.location().lineFrom()).isEqualTo(oracle.path("from").asInt());assertThat(parent.location().lineTo()).isEqualTo(oracle.path("to").asInt());
                assertThat(parent.children()).hasSize(oracle.path("children").size());
                for(int j=0;j<parent.children().size();j++) {
                    var child=parent.children().get(j);var childOracle=oracle.path("children").get(j);
                    assertThat(child.ordinal()).isEqualTo(j);assertThat(child.bodyMasked()).isEqualTo(childOracle.path("body").asString());
                    assertThat(child.bodyMasked().codePointCount(0,child.bodyMasked().length())).isEqualTo(childOracle.path("chars").asInt());
                    assertThat(child.location().lineFrom()).isEqualTo(childOracle.path("from").asInt());assertThat(child.location().lineTo()).isEqualTo(childOracle.path("to").asInt());
                    assertThat(child.header()).isEqualTo(parent.header());
                }
            }
        }));
    }
    @Test void oversizedAndPartialValidFilesNeverPublishAnyChunk() {
        var bytes="# 제목\n## 정상\n정상\n## 위반\n### 금지\n".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(extractor.extract(source,"a.md",new DocumentObjectStore.Content("a".repeat(64),bytes,false),limits).parents()).isEmpty();
        assertThat(extractor.extract(source,"a.md",new DocumentObjectStore.Content("a".repeat(64),new byte[0],true),limits).issues()).singleElement().extracting(DocumentCorpus.Issue::reason).isEqualTo("FILE_TOO_LARGE");
    }
}
