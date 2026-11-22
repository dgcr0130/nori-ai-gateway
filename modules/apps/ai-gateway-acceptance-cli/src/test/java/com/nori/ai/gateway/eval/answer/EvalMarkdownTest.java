package com.nori.ai.gateway.eval.answer;

import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;

class EvalMarkdownTest {
    @TestFactory Stream<DynamicTest> matchesEveryPhaseFiveOracleWithoutGatewayModules() throws Exception {
        var root=Path.of(getClass().getResource("/markdown/expected.json").toURI()).getParent();var expected=Json.read(root.resolve("expected.json"));
        return java.util.stream.StreamSupport.stream(expected.spliterator(),false).map(row->DynamicTest.dynamicTest(row.path("file").asText(),()->{
            byte[] bytes=Files.readAllBytes(root.resolve(row.path("file").asText()));var doc=new EvalMarkdown().extract("fixture-bucket",row.path("key").asText(),bytes);
            assertThat(doc.revision()).isEqualTo(row.path("revision").asText());
            if(row.has("reason")) {
                assertThat(doc.parents()).isEmpty();assertThat(doc.issues()).containsExactly(new EvalMarkdown.Issue(row.path("line").intValue(),row.path("reason").asText()));return;
            }
            assertThat(doc.issues()).isEmpty();assertThat(doc.parents()).hasSize(row.path("parents").size());
            for(int i=0;i<doc.parents().size();i++) {
                var actual=doc.parents().get(i);var parent=row.path("parents").get(i);
                assertThat(actual.location()).isEqualTo(new EvalMarkdown.Location("fixture-bucket",row.path("key").asText(),doc.revision(),parent.path("section").asText(),parent.path("from").intValue(),parent.path("to").intValue()));
                assertThat(actual.body()).isEqualTo(parent.path("body").asText());assertThat(QuestionSet.size(actual.body())).isEqualTo(parent.path("chars").intValue());
                assertThat(actual.children()).hasSize(parent.path("children").size());
                for(int j=0;j<actual.children().size();j++) {
                    var child=actual.children().get(j);var oracle=parent.path("children").get(j);
                    assertThat(child.ordinal()).isEqualTo(j);assertThat(child.body()).isEqualTo(oracle.path("body").asText());
                    assertThat(child.location().lineFrom()).isEqualTo(oracle.path("from").intValue());assertThat(child.location().lineTo()).isEqualTo(oracle.path("to").intValue());
                    assertThat(QuestionSet.size(child.body())).isEqualTo(oracle.path("chars").intValue());assertThat(child.header()).isEqualTo(actual.header());
                }
            }
        }));
    }
}
