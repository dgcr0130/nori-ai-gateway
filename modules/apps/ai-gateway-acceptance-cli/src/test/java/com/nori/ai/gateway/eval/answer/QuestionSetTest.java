package com.nori.ai.gateway.eval.answer;

import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.eval.answer.AnswerFixtures.*;

class QuestionSetTest {
    @TempDir Path root;
    final QuestionSet schema=new QuestionSet();
    void invalid(Runnable task){assertThatThrownBy(task::run).isInstanceOfSatisfying(EvalException.class,e->assertThat(e.exitCode()).isEqualTo(2));}
    @Test void missingIdSelfReferenceCycleAndZeroDenominatorsAreInputErrors() {
        var a=question("a",true,"100");var b=question("b",true,"100");var no=schema.parse(question("no",false,""));
        a.put("predecessorId","missing");invalid(()->schema.merge(List.of(schema.parse(a),no),List.of(),"TC"));
        a.put("predecessorId","a");invalid(()->schema.merge(List.of(schema.parse(a),no),List.of(),"TC"));
        a.put("predecessorId","b");b.put("predecessorId","a");invalid(()->schema.merge(List.of(schema.parse(a),schema.parse(b),no),List.of(),"TC"));
        invalid(()->schema.merge(List.of(no),List.of(),"TC"));invalid(()->schema.merge(List.of(schema.parse(question("a",true,"100"))),List.of(),"TC"));
    }
    @Test void activeCaseIdDeduplicatesIdenticalPayloadButRejectsConflictOrInitialCollision() {
        var q=question("feedback",true,"100");q.put("origin","FEEDBACK").put("feedbackId",UUID.randomUUID().toString()).put("reviewVersion",1);
        var initial=List.of(schema.parse(question("a",true,"100")),schema.parse(question("no",false,"")));var feedback=schema.parse(q);
        assertThat(schema.merge(initial,List.of(feedback,feedback),"TC")).hasSize(3);
        q.put("question","바뀐 질문");invalid(()->schema.merge(initial,List.of(feedback,schema.parse(q)),"TC"));
        q.put("id","a");invalid(()->schema.merge(initial,List.of(schema.parse(q)),"TC"));
    }
    @Test void rejectsSchemaRevisionAndFactMismatchBeforeAnyModelRequest() {
        var mismatch=question("a",true,"not-in-document");invalid(()->bound(mismatch));
        var q=question("a",true,"100");q.withArray("evidenceLocations").get(0).withObject("").put("revision","a".repeat(64));
        var revision=q;invalid(()->bound(revision));
        var missing=question("a",true,"100");missing.remove("documentTargets");invalid(()->schema.parse(missing));
        var typed=question("a",true,"100");typed.put("answerable","true");invalid(()->schema.parse(typed));
        var extra=question("a",true,"100");extra.put("category","임의 필드");invalid(()->schema.parse(extra));
        var falseFacts=question("a",false,"");falseFacts.withArray("expectedFacts").add("100");invalid(()->schema.parse(falseFacts));
        var blank=question("a",true,"\u0085");invalid(()->schema.parse(blank));
        invalid(()->new NegationAnalyzer().hasOutsideNegation("본문","\u0085"));
    }
    @Test void bindsOriginalExactChildBodyRatherThanHeadingMetadataOrWholeFile() {
        var heading=question("a",true,"설비 문서");invalid(()->bound(heading));
        var q=question("a",true,"100");q.withArray("evidenceLocations").get(0).withObject("").put("lineFrom",2).put("lineTo",13);
        var parent=q;invalid(()->bound(parent));
        var table=document().parents().getFirst().children().get(2);var row=question("a",true,"항목=linger.ms | 값=10ms");row.set("evidenceLocations",Json.MAPPER.valueToTree(List.of(table.location())));
        assertThat(bound(row).factLocations()).containsKey("항목=linger.ms | 값=10ms");
    }
    @Test void localDocumentIdentityIncludesBucketKeyAndRawByteRevisionAndGuardsPath() throws Exception {
        for(String bucket:List.of("bucket-a","bucket-b","문서자료")) {
            Path path=root.resolve(bucket).resolve("00.rag/user/guide.md");Files.createDirectories(path.getParent());Files.write(path,MARKDOWN);
        }
        var documents=new LocalDocuments(root);assertThat(documents.read("bucket-a","00.rag/user/guide.md").revision()).isEqualTo(Json.sha(MARKDOWN));
        documents.read("bucket-b","00.rag/user/guide.md");assertThat(documents.revisions()).hasSize(2);
        assertThat(documents.read("문서자료","00.rag/user/guide.md").bucket()).isEqualTo("문서자료");assertThat(documents.revisions()).hasSize(3);
        invalid(()->documents.read("bucket-a","../../outside.md"));invalid(()->documents.read("bucket-a","C:/outside.md"));
        invalid(()->documents.read("bucket-a","../bucket-b/00.rag/user/guide.md"));
    }
    @Test void preservesPermittedArrayDuplicatesAndRequiresDocumentTargetsForFeedbackRefusal() {
        var yes=question("a",true,"100");yes.withArray("permissions").add("AI_DOC_READ");yes.withArray("evidenceLocations").add(Json.MAPPER.valueToTree(a()));
        assertThat(bound(yes).question().evidenceLocations()).hasSize(2);
        var no=question("document-case",false,"");no.put("origin","FEEDBACK").put("feedbackId",UUID.randomUUID().toString()).put("reviewVersion",1);
        invalid(()->schema.parse(no));
        var target=Map.of("bucket",a().bucket(),"objectKey",a().objectKey(),"revision",a().revision());
        no.set("documentTargets",Json.MAPPER.valueToTree(List.of(target,target)));
        assertThat(bound(no).question().documentTargets()).hasSize(2);
    }
}
