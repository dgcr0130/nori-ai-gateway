package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.*;

class HybridEvidenceSelectionTest {
    static DocumentSearch.Hit hit(int id,String body) {
        var location=new DocumentLocation(id%2==0?"a":"b",String.format("%04d.md",id),"a".repeat(64),"설비 설정",3,5);
        var child=new DocumentCorpus.Child(new UUID(0,id+1),0,location,"설비 > 설정",body,List.of(),new float[]{1,0});
        var parent=new DocumentCorpus.Parent(new UUID(1,id+1),location,"설비 > 설정",body,"b".repeat(64),List.of(child));
        return new DocumentSearch.Hit(parent,child,Set.of("READ"));
    }
    static DocumentSearch.Round round(List<DocumentSearch.Hit> a,List<DocumentSearch.Hit> b,List<DocumentSearch.Hit> c){return new DocumentSearch.Round(a,b,c,List.of());}
    static EvidenceUnit other(String body){return new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("ref","EQP-001"),"",body,List.of("READ"),"SUCCESS","2026-11-19T00:00:00Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());}
    @Test void permissionArraysHaveExactDocumentOrderAndPreserveNonDocumentConfigOrder() {
        var hit=hit(0,"설비 상태");var document=EvidenceUnit.document(new DocumentSearch.Hit(hit.parent(),hit.child(),Set.of("Z_READ","A_READ")),List.of());
        var source=new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("ref","EQP-001"),"","비문서",List.of("Z_READ","A_READ"),"SUCCESS","2026-11-19T00:00:00Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());
        String expected="{\"evidence\":[{\"body\":\"설비 상태\",\"childLocations\":[],\"header\":\"설비 > 설정\",\"id\":\"e1\",\"location\":{\"bucket\":\"a\",\"lineFrom\":3,\"lineTo\":5,\"objectKey\":\"0000.md\",\"revision\":\""+"a".repeat(64)+"\",\"section\":\"설비 설정\"},\"missingFields\":[],\"number\":1,\"observedAt\":null,\"parentKey\":\"00000000-0000-0001-0000-000000000001\",\"permissions\":[\"A_READ\",\"Z_READ\"],\"retentionMeta\":{},\"sourceType\":\"DOCUMENT\",\"status\":\"SUCCESS\",\"values\":null,\"warnings\":[]},{\"body\":\"비문서\",\"childLocations\":[],\"header\":\"\",\"id\":\"e2\",\"location\":{\"ref\":\"EQP-001\"},\"missingFields\":[],\"number\":2,\"observedAt\":\"2026-11-19T00:00:00Z\",\"parentKey\":null,\"permissions\":[\"Z_READ\",\"A_READ\"],\"retentionMeta\":{},\"sourceType\":\"SYSTEM\",\"status\":\"SUCCESS\",\"values\":null,\"warnings\":[]}]}";
        assertThat(EvidenceSelection.json(List.of(document,source))).isEqualTo(expected);
        var reversed=new LinkedHashSet<>(List.of("A_READ","Z_READ"));
        assertThat(EvidenceSelection.json(List.of(EvidenceUnit.document(new DocumentSearch.Hit(hit.parent(),hit.child(),reversed),List.of()),source))).isEqualTo(expected);
    }
    @Test void rrfUsesEachPathRankAndNoContributionForEmptyPathsThenRecomputesTheUnion() {
        var a=hit(0,"설비 상태");var b=hit(1,"대기 T3");var c=hit(2,"프로토콜 S1F2");
        var first=round(List.of(a,b),List.of(b,c),List.of());
        var one=RankFusion.fuse(List.of(first),60);
        assertThat(one).extracting(s->s.hit().child().id()).containsExactly(b.child().id(),a.child().id(),c.child().id());
        assertThat(one.get(0).score()).isCloseTo(1.0/62+1.0/61,within(1e-15));assertThat(one.get(1).score()).isEqualTo(1.0/61);assertThat(one.get(2).score()).isEqualTo(1.0/62);
        var second=round(List.of(c),List.of(),List.of(c,a));var two=RankFusion.fuse(List.of(first,second),60);
        assertThat(two).extracting(s->s.hit().child().id()).containsExactly(c.child().id(),a.child().id(),b.child().id());
        assertThat(two.get(0).score()).isCloseTo(1.0/62+2.0/61,within(1e-15));assertThat(two.get(1).score()).isCloseTo(1.0/61+1.0/62,within(1e-15));
    }
    @Test void tiesUseBucketKeyLineThenChildIdAndCandidateBoundsAreSixtyOneTwentySixHundred() {
        var ordered=IntStream.range(0,600).mapToObj(i->hit(i,"설비 상태 T3")).toList();
        for(int width:List.of(20,100)) {
            var first=round(ordered.subList(0,width),ordered.subList(width,2*width),ordered.subList(2*width,3*width));
            var second=round(ordered.subList(3*width,4*width),ordered.subList(4*width,5*width),ordered.subList(5*width,6*width));
            assertThat(RankFusion.fuse(List.of(first),60)).hasSize(3*width);
            assertThat(RankFusion.fuse(List.of(first,second),60)).hasSize(6*width);
        }
        var b=hit(1,"둘");var a=hit(0,"하나");
        assertThat(RankFusion.fuse(List.of(round(List.of(b),List.of(a),List.of())),60)).extracting(s->s.hit().child().location().bucket()).containsExactly("a","b");
    }
    @Test void wholeParentsAreDeduplicatedVerifiedParentsComeFirstAndLimitsDoNotCountNonDocuments() {
        var hits=IntStream.range(0,12).mapToObj(i->hit(i,"부모 전체 설비 상태 T3 | 행="+i)).toList();var first=hits.getFirst();
        var child=new DocumentCorpus.Child(new UUID(0,999),1,new DocumentLocation("a","0000.md","a".repeat(64),"설비 설정",6,6),first.child().header(),"자식 일부",List.of(),new float[]{1,0});
        var sibling=new DocumentSearch.Hit(first.parent(),child,first.permissions());var semantic=new ArrayList<>(hits);semantic.add(sibling);
        var round=new DocumentSearch.Round(semantic,List.of(),List.of(),List.of(new DocumentSearch.Verified(new UUID(2,1),List.of(hits.get(11)))));
        for(int limit:List.of(1,6,10)) {
            var settings=new SearchSettings(20,20,20,60,limit,3,.9,.35,4,1);
            var result=new EvidenceSelection(String::length,100000).select(List.of(round),List.of(other("비문서 1"),other("비문서 2")),settings);
            assertThat(result.units()).hasSize(limit+2);assertThat(result.units().getFirst().body()).isEqualTo(hits.get(11).parent().bodyMasked());
            assertThat(result.units().stream().filter(u->u.sourceType().equals("DOCUMENT")).map(EvidenceUnit::parentKey)).doesNotHaveDuplicates();
            if(limit>1){var parent=result.units().stream().filter(u->first.parent().id().toString().equals(u.parentKey())).findFirst().orElseThrow();assertThat(parent.body()).isEqualTo(first.parent().bodyMasked());assertThat(parent.childLocations()).contains(first.child().location(),child.location());}
        }
    }
    @Test void oversizedParentIsSkippedWithoutClippingAndEverySelectedWireFieldCountsTowardBudget() {
        var big=hit(0,"큰 본문".repeat(1000));var small=hit(1,"설비 상태 T3");var unit=EvidenceUnit.document(small,List.of(small.child().location()));
        int exact=EvidenceSelection.json(List.of(unit)).length();
        var result=new EvidenceSelection(String::length,exact).select(List.of(round(List.of(big,small),List.of(),List.of())),List.of(),SearchSettings.defaults());
        assertThat(result.units()).containsExactly(unit);assertThat(result.json().length()).isEqualTo(exact);
        assertThat(new EvidenceSelection(String::length,exact-1).select(List.of(round(List.of(small),List.of(),List.of())),List.of(),SearchSettings.defaults()).units()).isEmpty();
        var truncated=new EvidenceSelection(String::length,exact).select(List.of(round(List.of(small),List.of(),List.of())),List.of(other("비문서")),SearchSettings.defaults());assertThat(truncated.truncatedItems()).isTrue();
    }
    @Test void equalRrfScoresUseEveryDeclaredTieField() {
        var hits=new ArrayList<DocumentSearch.Hit>();
        for(int id:List.of(5,3,1,4,2,0)) {
            String bucket=id==0?"a":"b",key=id<2?"a.md":"b.md";int line=id<4?3:4;
            var location=new DocumentLocation(bucket,key,"a".repeat(64),"설정",line,line);
            var child=new DocumentCorpus.Child(new UUID(0,id+1),0,location,"제목 > 설정","설비 상태",List.of(),new float[]{1,0});
            hits.add(new DocumentSearch.Hit(new DocumentCorpus.Parent(new UUID(1,id+1),location,"제목 > 설정","설비 상태","b".repeat(64),List.of(child)),child,Set.of("READ")));
        }
        var first=round(List.of(hits.get(0)),List.of(hits.get(1)),List.of(hits.get(2)));var second=round(List.of(hits.get(3)),List.of(hits.get(4)),List.of(hits.get(5)));
        assertThat(RankFusion.fuse(List.of(first,second),60)).extracting(s->s.hit().child().id().getLeastSignificantBits()).containsExactly(1L,2L,3L,4L,5L,6L);
    }
}
