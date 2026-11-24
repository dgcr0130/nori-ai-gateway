package com.nori.ai.gateway.core.workflow;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.llm.AnswerTokenCounting;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.util.*;

public final class EvidenceSelection {
    public record Selected(List<EvidenceUnit> units,String json,boolean truncatedItems) {
        public Selected { units=List.copyOf(units); }
        @Override public String toString(){return "SelectedEvidence[보호됨]";}
    }
    private final AnswerTokenCounting tokens;private final int budget;
    public EvidenceSelection(AnswerTokenCounting tokens,int budget){this.tokens=tokens;if(budget<1)throw new IllegalArgumentException("근거 예산이 잘못되었습니다");this.budget=budget;}
    public boolean fitsTransmission(String data,String schema){return tokens.count(data)<=budget&&tokens.count(schema)<=budget;}
    public Selected select(List<DocumentSearch.Round> rounds,List<EvidenceUnit> other,SearchSettings settings) {
        return select(rounds, other, settings, units -> tokens.count(json(units)) <= budget);
    }
    /** 본문·순서는 그대로 두고 요청별 실제 직렬화/문맥/출력 문법 예산으로 전체 단위를 선택합니다. */
    public Selected select(List<DocumentSearch.Round> rounds,List<EvidenceUnit> other,SearchSettings settings,java.util.function.Predicate<List<EvidenceUnit>> fits) {
        var ranked=RankFusion.fuse(rounds,settings.rrfK());var parents=new LinkedHashMap<String,EvidenceUnit>();
        var children=new HashMap<UUID,List<DocumentLocation>>();
        for(var score:ranked)com.nori.ai.gateway.core.logging.DiagnosticLog.event(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"DOCUMENT_SEARCH_HIT","objectKey",score.hit().child().location().objectKey(),"revision",score.hit().child().location().revision(),"block",score.hit().parent().id(),"chunk",score.hit().child().id(),"score",score.score(),"title",score.hit().parent().header());
        for(var score:ranked)children.computeIfAbsent(score.hit().parent().id(),key->new ArrayList<>()).add(score.hit().child().location());
        for(var round:rounds)for(var answer:round.verified())for(var hit:answer.parents()) {
            var locations=children.computeIfAbsent(hit.parent().id(),key->new ArrayList<>());var parent=hit.parent().location();
            for(var location:answer.locations())if(parent.bucket().equals(location.bucket())&&parent.objectKey().equals(location.objectKey())&&parent.revision().equals(location.revision())&&parent.section().equals(location.section())&&parent.lineFrom()<=location.lineFrom()&&parent.lineTo()>=location.lineTo()&&!locations.contains(location))locations.add(location);
        }
        for(var round:rounds)for(var answer:round.verified())for(var hit:answer.parents())parents.putIfAbsent(hit.parent().id().toString(),EvidenceUnit.document(hit,children.get(hit.parent().id())));
        for(var score:ranked)parents.putIfAbsent(score.hit().parent().id().toString(),EvidenceUnit.document(score.hit(),children.get(score.hit().parent().id())));
        var units=new ArrayList<EvidenceUnit>();int documents=0;
        boolean truncated=false;for(var unit:other)if(fits(units,unit,fits))units.add(unit);else truncated=true;
        for(var unit:parents.values())if(documents<settings.evidenceLimit()&&fits(units,unit,fits)){units.add(unit);documents++;}
        return new Selected(units,json(units),truncated);
    }
    private boolean fits(List<EvidenceUnit> selected,EvidenceUnit next,java.util.function.Predicate<List<EvidenceUnit>> fits) { var copy=new ArrayList<>(selected);copy.add(next);return fits.test(List.copyOf(copy)); }
    public static String json(List<EvidenceUnit> units) {
        var data=SchemaContracts.JSON.createObjectNode();var array=data.putArray("evidence");
        for(int i=0;i<units.size();i++) {
            var unit=units.get(i);var item=SchemaContracts.JSON.valueToTree(unit);
            ((tools.jackson.databind.node.ObjectNode)item).put("id","e"+(i+1)).put("number",i+1);array.add(item);
        }
        return com.nori.ai.gateway.core.evidence.EvidenceJson.write(data);
    }
}
