package com.nori.ai.gateway.core.result;

import java.util.*;
import tools.jackson.databind.JsonNode;

/** 문장 검증을 통과한 공개 답변입니다. 고정 문구는 문장 배열과 분리합니다. */
public record GroundedAnswer(String kind,String text,List<Citation> citations,List<Sentence> sentences) {
    public record Sentence(String text,List<Integer> citations) {
        public Sentence { citations=List.copyOf(citations); }
    }
    public record Citation(int number,String evidenceId,String sourceType,JsonNode location) {}
    public GroundedAnswer { citations=List.copyOf(citations);sentences=List.copyOf(sentences); }
    public static GroundedAnswer noEvidence(){return new GroundedAnswer("NO_EVIDENCE","근거 없음",List.of(),List.of());}
    @Override public String toString(){return "GroundedAnswer[보호됨]";}
}
