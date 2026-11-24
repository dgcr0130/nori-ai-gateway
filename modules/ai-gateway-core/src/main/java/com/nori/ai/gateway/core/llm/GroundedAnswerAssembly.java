package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.result.GroundedAnswer;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;

/** 요약·표의 값은 설정과 선택된 근거에서만 가져옵니다. */
final class GroundedAnswerAssembly {
    static final String FALLBACK="확인된 조회 결과를 문장으로 정리하지 못했습니다. 참고 자료의 근거 원문을 확인해 주세요.";
    private static final Map<String,String> NOTICES=Map.of("PARTIAL","일부 자료를 확인할 수 없습니다.","STALE","최신 자료가 아닐 수 있습니다.","CONFLICT","서로 맞지 않는 자료가 있어 하나로 확정할 수 없습니다.");
    private GroundedAnswerAssembly() {}
    static boolean summary(EvidenceUnit unit) {
        String tool=unit.location().path("toolId").asString("");
        return unit.sourceType().equals("SYSTEM")&&!tool.isEmpty()&&unit.location().path("ref").asString("").equals(tool+":result-summary");
    }
    static GroundedAnswer assemble(FactCheck.Checked checked,List<EvidenceUnit> units,Map<String,String> descriptions,Set<String> tableTools) {
        var sentences=new ArrayList<GroundedAnswer.Sentence>();var cited=new TreeSet<Integer>();var tables=new ArrayList<String>();
        for(int i=0;i<units.size();i++) {
            var unit=units.get(i);int number=i+1;
            if(summary(unit)){sentences.add(new GroundedAnswer.Sentence(summaryText(unit,descriptions),List.of(number)));cited.add(number);}
            else if(tableTools.contains(unit.location().path("toolId").asString(""))) {
                var lines=unit.body().split("\n",-1);var table=new ArrayList<String>();table.add(lines[0]+" ["+number+"]");
                for(int j=1;j<lines.length;j++)if(!lines[j].startsWith("조회 조건 > "))table.add(GroundedFacts.kstLine(lines[j]));
                tables.add(String.join("\n",table));cited.add(number);
            }
        }
        sentences.addAll(checked.sentences());sentences.forEach(sentence->cited.addAll(sentence.citations()));
        if(sentences.isEmpty()&&tables.isEmpty())return fallback(units);
        var paragraphs=new ArrayList<String>();var prose=new ArrayList<String>();
        for(var sentence:sentences){var value=new StringBuilder(sentence.text());sentence.citations().forEach(n->value.append('[').append(n).append(']'));prose.add(value.toString());}
        if(!prose.isEmpty())paragraphs.add(String.join(" ",prose));if(!tables.isEmpty())paragraphs.add(String.join("\n\n",tables));
        var states=new HashSet<String>();cited.forEach(n->states.add(units.get(n-1).status()));var notices=new ArrayList<String>();
        for(String status:List.of("PARTIAL","STALE","CONFLICT"))if(states.contains(status))notices.add(NOTICES.get(status));
        if(!notices.isEmpty())paragraphs.add(String.join("\n",notices));
        return new GroundedAnswer(checked.removed()||!notices.isEmpty()?"PARTIAL":"ANSWER",String.join("\n\n",paragraphs),citations(units,cited),sentences);
    }
    static GroundedAnswer fallback(List<EvidenceUnit> units) {
        var all=new TreeSet<Integer>();for(int i=1;i<=units.size();i++)all.add(i);
        return new GroundedAnswer("PARTIAL",FALLBACK,citations(units,all),List.of());
    }
    private static List<GroundedAnswer.Citation> citations(List<EvidenceUnit> units,SortedSet<Integer> numbers) {
        return numbers.stream().map(n->new GroundedAnswer.Citation(n,"e"+n,units.get(n-1).sourceType(),units.get(n-1).location())).toList();
    }
    private static String summaryText(EvidenceUnit unit,Map<String,String> descriptions) {
        var values=unit.values();
        if(values==null||!values.path("arguments").isObject()||!values.path("count").isIntegralNumber()||values.path("count").asInt()!=0)throw new IllegalArgumentException("조회 요약 근거가 잘못되었습니다");
        String tool=unit.location().path("toolId").asString();String description=descriptions.get(tool);
        if(description==null||description.isBlank())throw new IllegalArgumentException("조회 도구 설명이 없습니다");
        var arguments=values.get("arguments");String subject=arguments.path("eqp").asString("");String period="";
        if(arguments.path("from").isString()&&arguments.path("to").isString())period=" (기간 "+GroundedFacts.kst(arguments.get("from").asString())+" ~ "+GroundedFacts.kst(arguments.get("to").asString())+")";
        return (subject.isEmpty()?"":subject+" ")+description+period+(unit.missingFields().contains("completeness")?" 결과는 조회된 보관 범위에서 0건이며, 전체 기간 완전성은 확인되지 않았습니다.":" 결과는 0건입니다.");
    }
}
