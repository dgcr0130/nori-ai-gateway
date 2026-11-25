package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.GroundedAnswer;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import tools.jackson.databind.JsonNode;
import java.util.*;
import java.util.regex.Pattern;

/** 인용한 단위의 본문과 그 헤더만 비교하며 의미 추론이나 수치 변환을 하지 않습니다. */
public final class SentenceEvidenceCheck {
    private static final Set<String> FUNCTION=Set.of("근거","확인","결과","정보","조회","문서","내용","값");
    private static final Pattern ENGLISH=Pattern.compile("(?<![a-z0-9_.-])[a-z_][a-z0-9_]*(?:[.-][a-z0-9_]+)*");
    private static final tools.jackson.databind.json.JsonMapper STRICT=SchemaContracts.JSON.rebuild().enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
    private final SearchLanguage language;
    public SentenceEvidenceCheck(SearchLanguage language){this.language=Objects.requireNonNull(language);}
    public GroundedAnswer check(String output,List<EvidenceUnit> units) { return check(output,units,reason->{}); }
    public GroundedAnswer check(String output,List<EvidenceUnit> units,java.util.function.Consumer<LlmDiagnostic> diagnostic) {
        return check(output,units,diagnostic,row->true);
    }
    /** LLM 소비자만 행 선택 제약을 더합니다. 외피를 복구하거나 기존 비LLM 검증에 제약을 강제하지 않습니다. */
    GroundedAnswer check(String output,List<EvidenceUnit> units,java.util.function.Consumer<LlmDiagnostic> diagnostic,
            java.util.function.Predicate<JsonNode> selectedSentence) {
        final JsonNode root;
        try { root=SchemaContracts.parse(output);STRICT.readTree(output); }catch(RuntimeException invalid){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(SentenceEvidenceCheck.class.getName()),"SENTENCE_EVIDENCE_CHECK_FAILED",invalid);diagnostic.accept(LlmDiagnostic.SENTENCE_JSON);return GroundedAnswer.noEvidence();}
        if(!root.isObject()||root.size()!=1||!root.path("sentences").isArray()||root.path("sentences").size()>20){diagnostic.accept(LlmDiagnostic.SENTENCE_STRUCTURE);return GroundedAnswer.noEvidence();}
        if(root.path("sentences").isEmpty()){diagnostic.accept(LlmDiagnostic.SENTENCE_EMPTY);return GroundedAnswer.noEvidence();}
        var kept=new ArrayList<GroundedAnswer.Sentence>();var cited=new TreeSet<Integer>();var states=new HashSet<String>();
        for(var row:root.get("sentences")) {
            if(!row.isObject()||row.size()!=2||!row.path("text").isString()||!row.path("citations").isArray()){diagnostic.accept(LlmDiagnostic.SENTENCE_STRUCTURE);continue;}
            String text=row.get("text").asString();int length=text.codePointCount(0,text.length());
            if(length<1||length>1000||text.codePoints().anyMatch(c->c=='\n'||c=='\r'||c==0x85||c==0x2028||c==0x2029)||row.get("citations").isEmpty()||row.get("citations").size()>6){diagnostic.accept(LlmDiagnostic.SENTENCE_STRUCTURE);continue;}
            var numbers=new ArrayList<Integer>();boolean valid=true;
            for(var number:row.get("citations")) {
                if(!number.isIntegralNumber()||!number.canConvertToInt()||number.asInt()<1||number.asInt()>units.size()){valid=false;break;}
                numbers.add(number.asInt());
            }
            if(!valid){diagnostic.accept(LlmDiagnostic.SENTENCE_CITATION);continue;}
            if(!selectedSentence.test(row)){diagnostic.accept(LlmDiagnostic.COMPOSE_SELECTION);continue;}
            String body=String.join("\n",numbers.stream().map(n->units.get(n-1).body()).toList());
            String headers=String.join("\n",numbers.stream().map(n->units.get(n-1).header()).toList());
            if(!facts(text,body)){diagnostic.accept(LlmDiagnostic.SENTENCE_FACT);continue;}
            if(!names(text,body+"\n"+headers)){diagnostic.accept(LlmDiagnostic.SENTENCE_NAME);continue;}
            kept.add(new GroundedAnswer.Sentence(text,numbers));cited.addAll(numbers);
            for(int number:numbers)states.add(units.get(number-1).status());
        }
        if(kept.isEmpty())return GroundedAnswer.noEvidence();
        var rendered=new ArrayList<String>();for(var sentence:kept){var line=new StringBuilder(sentence.text());for(int number:sentence.citations())line.append('[').append(number).append(']');rendered.add(line.toString());}
        var notices=Map.of("PARTIAL","일부 자료를 확인할 수 없습니다.","STALE","최신 자료가 아닐 수 있습니다.","CONFLICT","서로 맞지 않는 자료가 있어 하나로 확정할 수 없습니다.");
        boolean partial=kept.size()!=root.get("sentences").size();
        for(String status:List.of("PARTIAL","STALE","CONFLICT"))if(states.contains(status)){partial=true;rendered.add(notices.get(status));}
        var citations=cited.stream().map(n->GroundedAnswerAssembly.citation(n,units.get(n-1))).toList();
        return new GroundedAnswer(partial?"PARTIAL":"ANSWER",String.join("\n",rendered),citations,kept);
    }
    private boolean facts(String text,String body) {return FactTokens.extract(body,language).containsAll(FactTokens.extract(text,language));}
    private boolean names(String text,String evidence) {
        var available=new HashSet<String>();
        for(var term:language.analyze(evidence))if(Set.of("NNP","NNG").contains(term.tag()))available.add(SearchText.normalize(term.text()));
        for(var term:language.analyze(text))if(Set.of("NNP","NNG").contains(term.tag())&&!FUNCTION.contains(term.text())&&!available.contains(SearchText.normalize(term.text())))return false;
        var matcher=ENGLISH.matcher(SearchText.normalize(text));var availableEnglish=new HashSet<String>();
        var found=ENGLISH.matcher(SearchText.normalize(evidence));while(found.find())availableEnglish.add(found.group());
        while(matcher.find())if(!availableEnglish.contains(matcher.group()))return false;
        return true;
    }
}
