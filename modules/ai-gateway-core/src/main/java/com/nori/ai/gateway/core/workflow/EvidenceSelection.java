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
    public Selected select(List<DocumentSearch.Round> rounds,List<EvidenceUnit> other,SearchSettings settings) {
        var ranked=RankFusion.fuse(rounds,settings.rrfK());var parents=new LinkedHashMap<String,EvidenceUnit>();
        var children=new HashMap<UUID,List<DocumentLocation>>();
        for(var score:ranked)children.computeIfAbsent(score.hit().parent().id(),key->new ArrayList<>()).add(score.hit().child().location());
        for(var round:rounds)for(var answer:round.verified())for(var hit:answer.parents()) {
            var locations=children.computeIfAbsent(hit.parent().id(),key->new ArrayList<>());var parent=hit.parent().location();
            for(var location:answer.locations())if(parent.bucket().equals(location.bucket())&&parent.objectKey().equals(location.objectKey())&&parent.revision().equals(location.revision())&&parent.section().equals(location.section())&&parent.lineFrom()<=location.lineFrom()&&parent.lineTo()>=location.lineTo()&&!locations.contains(location))locations.add(location);
        }
        for(var round:rounds)for(var answer:round.verified())for(var hit:answer.parents())parents.putIfAbsent(hit.parent().id().toString(),EvidenceUnit.document(hit,children.get(hit.parent().id())));
        for(var score:ranked)parents.putIfAbsent(score.hit().parent().id().toString(),EvidenceUnit.document(score.hit(),children.get(score.hit().parent().id())));
        var units=new ArrayList<EvidenceUnit>();int documents=0;
        for(var unit:parents.values())if(documents<settings.evidenceLimit()&&fits(units,unit)){units.add(unit);documents++;}
        boolean truncated=false;for(var unit:other)if(fits(units,unit))units.add(unit);else truncated=true;
        return new Selected(units,json(units),truncated);
    }
    private boolean fits(List<EvidenceUnit> selected,EvidenceUnit next) { var copy=new ArrayList<>(selected);copy.add(next);return tokens.count(json(copy))<=budget; }
    public static String json(List<EvidenceUnit> units) {
        var data=SchemaContracts.JSON.createObjectNode();var array=data.putArray("evidence");
        for(int i=0;i<units.size();i++) {
            var unit=units.get(i);var item=SchemaContracts.JSON.valueToTree(unit);
            ((tools.jackson.databind.node.ObjectNode)item).put("id","e"+(i+1)).put("number",i+1);array.add(item);
        }
        return com.nori.ai.gateway.core.evidence.EvidenceJson.write(data);
    }
}
