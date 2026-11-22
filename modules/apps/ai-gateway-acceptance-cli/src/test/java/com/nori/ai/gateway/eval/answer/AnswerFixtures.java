package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nori.ai.gateway.eval.io.Json;
import java.util.*;

final class AnswerFixtures {
    // 다른 절에 같은 수치, 표·목록·코드·단위·식별자를 함께 둡니다.
    static final byte[] MARKDOWN=("# 설비 문서\n## 절 A\n코드는 100입니다. 재시도 없음으로 설정됩니다.\n\n"
        +"- T3는 1.5s이며 S1F2에서 EQP-001을 확인한다.\n\n| 항목 | 값 |\n| --- | --- |\n| linger.ms | 10ms |\n\n```text\nPLC UINT16\n```\n"
        +"## 절 B\n코드는 100입니다.\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);
    static EvalMarkdown.Document document(){return new EvalMarkdown().extract("bucket-a","00.rag/user/guide.md",MARKDOWN);}
    static EvalMarkdown.Location a(){return document().parents().getFirst().children().getFirst().location();}
    static EvalMarkdown.Location b(){return document().parents().getLast().children().getFirst().location();}
    static ObjectNode question(String id,boolean answerable,String fact) {
        var q=Json.MAPPER.createObjectNode().put("id",id).put("systemId","TC").put("question","코드 값은 무엇인가요?").put("answerable",answerable).put("origin","INITIAL");
        q.set("expectedFacts",Json.MAPPER.valueToTree(answerable?List.of(fact):List.of()));q.set("evidenceLocations",Json.MAPPER.valueToTree(answerable?List.of(a()):List.of()));
        q.putNull("context");q.putArray("permissions").add("AI_DOC_READ");q.putArray("documentTargets");q.putNull("predecessorId");return q;
    }
    static QuestionSet.Bound bound(ObjectNode q){return new QuestionSet().bind(new QuestionSet().parse(q),(bucket,key)->document());}
    static ObjectNode response(String text,EvalMarkdown.Location... locations) {
        var citations=Json.MAPPER.createArrayNode();var refs=Json.MAPPER.createArrayNode();
        for(int i=0;i<locations.length;i++) {
            var citation=citations.addObject().put("number",i+1).put("evidenceId","evidence-"+i).put("sourceType","DOCUMENT");citation.set("location",Json.MAPPER.valueToTree(locations[i]));refs.add(i+1);
        }
        var answer=Json.MAPPER.createObjectNode().put("kind","ANSWER").put("text",text+"[1]");answer.set("citations",citations);
        var sentence=answer.putArray("sentences").addObject().put("text",text);sentence.set("citations",refs);
        return envelope(answer);
    }
    static ObjectNode refusal() {
        var answer=Json.MAPPER.createObjectNode().put("kind","NO_EVIDENCE").put("text","근거 없음");answer.putArray("citations");answer.putArray("sentences");return envelope(answer);
    }
    static ObjectNode envelope(ObjectNode answer) {
        var root=Json.MAPPER.createObjectNode().put("jobId","job-1").put("jobStatus","SUCCEEDED").put("createdAt","2026-11-19T00:00:00Z").put("terminalAt","2026-11-19T00:00:01Z");
        var result=root.putObject("result").put("status",answer.path("kind").asText().equals("NO_EVIDENCE")?"EMPTY":"SUCCESS");result.putNull("errorCode");result.putNull("clarification");result.putObject("payload").set("answer",answer);return root;
    }
}
