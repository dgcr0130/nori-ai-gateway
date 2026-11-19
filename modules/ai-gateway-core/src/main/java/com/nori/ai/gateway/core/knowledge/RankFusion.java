package com.nori.ai.gateway.core.knowledge;

import java.util.*;

public final class RankFusion {
    private RankFusion() {}
    public record Scored(DocumentSearch.Hit hit,double score) {}
    public static final Comparator<DocumentSearch.Hit> ORDER=Comparator
        .comparing((DocumentSearch.Hit h)->h.child().location().bucket())
        .thenComparing(h->h.child().location().objectKey()).thenComparingInt(h->h.child().location().lineFrom())
        .thenComparing(h->h.child().id().toString());
    public static List<Scored> fuse(List<DocumentSearch.Round> rounds,int k) {
        var ranks=new HashMap<UUID,List<Integer>>();var hits=new HashMap<UUID,DocumentSearch.Hit>();
        for(var round:rounds)for(var list:List.of(round.semantic(),round.keyword(),round.identifier())) {
            var seen=new HashSet<UUID>();
            for(int i=0;i<list.size();i++) {
                var hit=list.get(i);if(!seen.add(hit.child().id()))throw new IllegalArgumentException("검색 경로의 자식이 중복되었습니다");
                hits.put(hit.child().id(),hit);ranks.computeIfAbsent(hit.child().id(),id->new ArrayList<>()).add(i+1);
            }
        }
        return hits.values().stream().map(h->new Scored(h,ranks.get(h.child().id()).stream().sorted().mapToDouble(rank->1.0/(k+rank)).sum()))
            .sorted(Comparator.comparingDouble(Scored::score).reversed().thenComparing(Scored::hit,ORDER)).toList();
    }
}
