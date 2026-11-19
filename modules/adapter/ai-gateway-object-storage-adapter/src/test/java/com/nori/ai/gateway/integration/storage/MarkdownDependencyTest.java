package com.nori.ai.gateway.integration.storage;

import org.commonmark.parser.Parser;
import org.commonmark.parser.IncludeSourceSpans;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Heading;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class MarkdownDependencyTest {
    @Test void java21LoadsFixedParserWithOriginalLineSpans() {
        assertThat(Runtime.version().feature()).isEqualTo(21);
        var root = Parser.builder().extensions(List.of(TablesExtension.create()))
            .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES).build().parse("# 제목\n\n## 절\n\n본문\n");
        assertThat(root.getFirstChild()).isInstanceOf(Heading.class);
        assertThat(root.getFirstChild().getSourceSpans().getFirst().getLineIndex()).isZero();
        assertThat(root.getFirstChild().getNext().getSourceSpans().getFirst().getLineIndex()).isEqualTo(2);
    }
}
