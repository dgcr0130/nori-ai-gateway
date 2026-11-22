package com.nori.ai.gateway.eval.answer;

import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class NoriObservationTest {
    @Test void recordsPinnedDictionarySurfaceTagsOffsetsAndInflections() throws Exception {
        assertThat(org.apache.lucene.util.Version.LATEST.toString()).isEqualTo("9.12.3");
        var analyzer=new NegationAnalyzer();
        var observed=Json.read(java.nio.file.Path.of(getClass().getResource("/answer/nori-9.12.3.json").toURI()));
        assertThat(observed.path("outputUnknownUnigrams").booleanValue()).isFalse();
        assertThat(observed.path("discardPunctuation").booleanValue()).isTrue();
        for(var row:observed.path("observations")) {
            String text=row.path("text").textValue();
            assertThat((com.fasterxml.jackson.databind.JsonNode)Json.MAPPER.valueToTree(analyzer.analyze(text))).isEqualTo(row.path("tokens"));
        }
    }
}
