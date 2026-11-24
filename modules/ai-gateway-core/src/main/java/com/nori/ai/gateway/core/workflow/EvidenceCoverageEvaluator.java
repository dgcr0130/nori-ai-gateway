package com.nori.ai.gateway.core.workflow;
import com.nori.ai.gateway.core.registry.TaskType;
import com.nori.ai.gateway.core.knowledge.*;
import java.util.*;
/** 검색 후보가 아닌 실제 예산 안에 담긴 본문만 충분성 근거로 사용합니다. */
public final class EvidenceCoverageEvaluator {
    public boolean requiresDocuments(TaskType task){return task==TaskType.KNOWLEDGE_QUERY;}
    private static final Set<String> FUNCTIONS=Set.of("질문","답변","설명","방법","무엇","알려줘");
    public List<String> coreTerms(String question,SearchLanguage language,int limit) {
        return language.analyze(question).stream().filter(t->Set.of("NNG","NNP").contains(t.tag()))
            .map(t->SearchText.normalize(t.text())).filter(t->!FUNCTIONS.contains(t)).distinct().limit(limit).toList();
    }
    public boolean sufficient(String question,List<String> targets,List<EvidenceUnit> selected,SearchLanguage language,int limit,Map<String,String> glossary) {
        var identifiers=SearchText.identifiers(question,targets);var nouns=coreTerms(question,language,limit);
        if(selected.isEmpty()||identifiers.isEmpty()&&nouns.isEmpty())return false;
        if(!identifiers.stream().allMatch(id->selected.stream().anyMatch(u->SearchText.containsExact(u.body(),id))))return false;
        var bodyTerms=new HashSet<String>();for(var unit:selected)for(var term:language.analyze(unit.body()))bodyTerms.add(SearchText.normalize(term.text()));
        long count=nouns.stream().filter(noun->{
            if(bodyTerms.contains(noun))return true;
            for(var entry:glossary.entrySet()) {
                String key=SearchText.normalize(entry.getKey()),value=SearchText.normalize(entry.getValue());
                if((noun.equals(key)||noun.equals(value))&&selected.stream().anyMatch(u->SearchText.containsExact(u.body(),key)||SearchText.containsExact(u.body(),value)))return true;
            }
            return false;
        }).count();
        return count>=Math.min(2,nouns.size());
    }
}
