package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.GroundedAnswer;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import java.util.function.Consumer;

/** 작성 문장의 값·실제 업무 대상과 코드 근거를 인용한 문장의 영문 이름을 대조하며 질문 명사 일치는 요구하지 않습니다. */
public final class FactCheck {
    private static final tools.jackson.databind.json.JsonMapper STRICT=SchemaContracts.JSON.rebuild().enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
    private final SearchLanguage language;
    public FactCheck(SearchLanguage language){this.language=Objects.requireNonNull(language);}
    record Checked(List<GroundedAnswer.Sentence> sentences,boolean removed) {
        Checked {sentences=List.copyOf(sentences);}
    }
    public GroundedAnswer check(String output,List<EvidenceUnit> units,List<String> businessTargets) {
        return GroundedAnswerAssembly.assemble(evaluate(output,units,businessTargets,reason->{}),units,Map.of(),Set.of());
    }
    Checked evaluate(String output,List<EvidenceUnit> units,List<String> businessTargets,Consumer<LlmDiagnostic> diagnostic) {
        final tools.jackson.databind.JsonNode root;
        try {root=SchemaContracts.parse(output);STRICT.readTree(output);}
        catch(RuntimeException invalid){diagnostic.accept(LlmDiagnostic.SENTENCE_JSON);return new Checked(List.of(),true);}
        if(!root.isObject()||root.size()!=1||!root.path("sentences").isArray()||root.path("sentences").size()>20){diagnostic.accept(LlmDiagnostic.SENTENCE_STRUCTURE);return new Checked(List.of(),true);}
        var kept=new ArrayList<GroundedAnswer.Sentence>();
        for(var row:root.get("sentences")) {
            if(!row.isObject()||row.size()!=2||!row.path("text").isString()||!row.path("factIds").isArray()){diagnostic.accept(LlmDiagnostic.SENTENCE_STRUCTURE);continue;}
            String text=row.path("text").asString();int length=text.codePointCount(0,text.length());var ids=row.get("factIds");
            if(length<1||length>1000||text.codePoints().anyMatch(c->c=='\n'||c=='\r'||c==0x85||c==0x2028||c==0x2029)||ids.isEmpty()||ids.size()>6){diagnostic.accept(LlmDiagnostic.SENTENCE_STRUCTURE);continue;}
            var numbers=new TreeSet<Integer>();boolean valid=true;
            for(var id:ids) {
                if(!id.isString()||!id.asString().matches("e[1-9][0-9]{0,8}")){valid=false;break;}
                int number=Integer.parseInt(id.asString().substring(1));if(number>units.size()){valid=false;break;}numbers.add(number);
            }
            if(!valid){diagnostic.accept(LlmDiagnostic.SENTENCE_CITATION);continue;}
            var available=new HashSet<String>();
            for(int number:numbers) {
                var unit=units.get(number-1);available.addAll(FactTokens.extract(unit.body(),language));available.addAll(FactTokens.extract(GroundedFacts.kstText(unit.body()),language));
                if(unit.values()!=null)available.addAll(FactTokens.extract(unit.values().toString(),language));
                for(String source:codeSources(unit))available.addAll(FactTokens.extract(source,language));
            }
            if(!available.containsAll(FactTokens.extract(text,language))){diagnostic.accept(LlmDiagnostic.SENTENCE_FACT);continue;}
            if(numbers.stream().anyMatch(n->units.get(n-1).sourceType().equals("GIT"))&&!citedNames(numbers,units).containsAll(names(text))){diagnostic.accept(LlmDiagnostic.SENTENCE_NAME);continue;}
            if(!businessTargets.isEmpty()&&numbers.stream().map(n->units.get(n-1)).anyMatch(unit->unit.sourceType().equals("SYSTEM")&&businessTargets.stream().noneMatch(target->SearchText.containsExact(unit.body(),target)))) {
                diagnostic.accept(LlmDiagnostic.COMPOSE_SELECTION);continue;
            }
            kept.add(new GroundedAnswer.Sentence(text,List.copyOf(numbers)));
        }
        return new Checked(kept,kept.size()!=root.get("sentences").size());
    }
    /** 영문으로 시작하는 이름 토큰(클래스·메서드·패키지·파일 이름)이며 숫자·밑줄만 다른 값은 FactTokens가 따로 대조합니다. */
    private static final java.util.regex.Pattern NAME=java.util.regex.Pattern.compile("(?<![a-z0-9_.-])[a-z_][a-z0-9_]*(?:[.-][a-z0-9_]+)*");
    static Set<String> names(String text) {
        var names=new HashSet<String>();var matcher=NAME.matcher(SearchText.normalize(text));
        while(matcher.find())names.add(matcher.group());
        return names;
    }
    /** 인용 근거의 본문·헤더와 코드 근거 출처에서 이름을 모으며, 경로 조각(kafka 등)도 이름으로 봅니다. */
    private static Set<String> citedNames(Set<Integer> numbers,List<EvidenceUnit> units) {
        var sources=new ArrayList<String>();
        for(int number:numbers) {
            var unit=units.get(number-1);sources.add(unit.body());
            if(unit.header()!=null)sources.add(unit.header());
            sources.addAll(codeSources(unit));
        }
        var names=new HashSet<String>();
        for(String source:sources)for(String name:names(source)){names.add(name);names.addAll(List.of(name.split("[.-]")));}
        return names;
    }
    /**
     * 코드 근거 본문은 JSON 문자열이라 줄 첫머리 이름 앞에 이스케이프가 붙으므로 디코딩한 문자열 값을 따로 대조하고,
     * 작성 입력에 함께 보내는 저장소·파일 경로도 근거로 인정합니다. 다른 근거 종류는 빈 목록입니다.
     */
    private static List<String> codeSources(EvidenceUnit unit) {
        if(!unit.sourceType().equals("GIT"))return List.of();
        var sources=new ArrayList<String>();strings(SchemaContracts.JSON.readTree(unit.body()),sources);
        sources.add(unit.location().path("sourceRepo").asString(""));sources.add(unit.location().path("path").asString(""));
        return sources;
    }
    private static void strings(tools.jackson.databind.JsonNode node,List<String> into) {
        if(node.isString())into.add(node.asString());
        else for(var child:node)strings(child,into);
    }
}
