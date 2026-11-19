package com.nori.ai.gateway.persistence;

import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.index.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.*;

class LuceneDependencyTest {
    @Test void java21LoadsNoriAndSearchesMemoryIndex() throws Exception {
        assertThat(Runtime.version().feature()).isEqualTo(21);
        try (var analyzer = new KoreanAnalyzer(); var directory = new ByteBuffersDirectory()) {
            var terms = new ArrayList<String>();
            try (var stream = analyzer.tokenStream("body", "설비 상태 확인")) {
                var term = stream.addAttribute(CharTermAttribute.class); stream.reset();
                while (stream.incrementToken()) terms.add(term.toString()); stream.end();
            }
            assertThat(terms).contains("설비", "상태", "확인");
            try (var writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
                var document = new Document(); document.add(new TextField("body", "설비 상태 확인", Field.Store.NO));
                writer.addDocument(document);
            }
            try (var reader = DirectoryReader.open(directory)) {
                assertThat(new IndexSearcher(reader).count(new TermQuery(new Term("body", "설비")))).isEqualTo(1);
            }
        }
    }
}
