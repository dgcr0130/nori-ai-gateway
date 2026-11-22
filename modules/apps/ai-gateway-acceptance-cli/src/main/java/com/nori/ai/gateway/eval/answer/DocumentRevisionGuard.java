package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.EvalException;
import java.util.*;

/** 채점 점수와 별개로 병합 세트가 요구하는 문서의 실행 중 revision을 대조합니다. */
final class DocumentRevisionGuard {
    private record Key(String bucket,String objectKey) {}
    private final Map<Key,String> required=new HashMap<>();
    DocumentRevisionGuard(List<QuestionSet.Bound> questions) {
        for(var bound:questions) {
            for(var location:bound.question().evidenceLocations())add(location.bucket(),location.objectKey(),location.revision());
            for(var target:bound.question().documentTargets())add(target.bucket(),target.objectKey(),target.revision());
        }
    }
    private void add(String bucket,String key,String revision) {
        String previous=required.putIfAbsent(new Key(bucket,key),revision);QuestionSet.require(previous==null||previous.equals(revision));
    }
    void check(JsonNode response){walk(response.path("result").path("payload"));}
    private void walk(JsonNode node) {
        if(node.isObject()) {
            if(node.path("bucket").isTextual()&&node.path("objectKey").isTextual()&&node.has("revision")) {
                String expected=required.get(new Key(node.path("bucket").textValue(),node.path("objectKey").textValue()));
                if(expected!=null&&(!node.path("revision").isTextual()||!expected.equals(node.path("revision").textValue())))
                    throw new EvalException(2,"답변 인용의 문서 revision이 평가 세트와 다릅니다");
            }
            // 표준 업무 조회의 values는 Gateway 문서 위치가 아닌 업무 자료입니다.
            boolean businessItem=node.size()==3&&node.path("ref").isTextual()&&node.path("text").isTextual()&&node.path("values").isObject();
            node.fields().forEachRemaining(field->{if(!businessItem||!field.getKey().equals("values"))walk(field.getValue());});
        }else if(node.isArray())node.forEach(this::walk);
    }
}
