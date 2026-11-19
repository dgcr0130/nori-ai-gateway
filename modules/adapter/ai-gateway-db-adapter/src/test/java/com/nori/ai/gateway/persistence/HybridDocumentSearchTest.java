package com.nori.ai.gateway.persistence;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class HybridDocumentSearchTest {
    static final String REV="a".repeat(64),SHA="b".repeat(64);
    static VerifiedSystemScope scope(String system,Set<String> permissions) {
        return new VerifiedSystemScope(OwnerSystem.valueOf(system),"service",new ActorRef("actor"),"actor",permissions,"pdg",Instant.EPOCH,Instant.parse("2099-01-01T00:00:00Z"),"job");
    }
    static DocumentCorpus.Document doc(String bucket,String key,String body,Set<String> permissions,float... vector) {
        var location=new DocumentLocation(bucket,key,REV,"설정",3,5);UUID parent=UUID.nameUUIDFromBytes((bucket+key).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var child=new DocumentCorpus.Child(UUID.nameUUIDFromBytes((bucket+key+"child").getBytes(java.nio.charset.StandardCharsets.UTF_8)),0,location,"운영 > 설정",body,SearchText.identifiers(body,List.of()),vector);
        return new DocumentCorpus.Document(UUID.randomUUID(),bucket,key,REV,"운영",permissions,List.of(),List.of(new DocumentCorpus.Parent(parent,location,"운영 > 설정",body,SHA,List.of(child))));
    }
    static DocumentCorpus.Bundle bundle(List<DocumentCorpus.Document> docs,List<DocumentCorpus.Answer> answers){return new DocumentCorpus.Bundle(UUID.randomUUID(),"fixture",2,SHA,docs,answers);}
    static void install(MemoryDocumentKeywordIndex index,VerifiedSystemScope scope,DocumentCorpus.Bundle bundle){try(var prepared=index.prepare(scope,bundle)){index.install(scope,prepared);}}
    @Test void observesActualPinnedNoriTagsAndIdentifierTokens() {
        try(var index=new MemoryDocumentKeywordIndex()) {
            var terms=index.analyze("설비 상태 확인 질문 답변 설명 방법 무엇 알려줘 T3 S1F2 EQP-001 linger.ms");
            assertThat(terms).contains(new SearchLanguage.Term("설비","NNG"),new SearchLanguage.Term("상태","NNG"));
            assertThat(terms.stream().filter(t->List.of("t","3","s","1","f","2","eqp","001","linger","ms").contains(t.text())).toList())
                .containsExactly(new SearchLanguage.Term("t","SL"),new SearchLanguage.Term("3","SN"),new SearchLanguage.Term("s","SL"),new SearchLanguage.Term("1","SN"),new SearchLanguage.Term("f","SL"),new SearchLanguage.Term("2","SN"),new SearchLanguage.Term("eqp","SL"),new SearchLanguage.Term("001","SN"),new SearchLanguage.Term("linger","SL"),new SearchLanguage.Term("ms","SL"));
            assertThat(index.analyze("서울 삼성전자")).containsExactly(new SearchLanguage.Term("서울","NNP"),new SearchLanguage.Term("삼성","NNG"),new SearchLanguage.Term("전자","NNG"));
            var coverage=new com.nori.ai.gateway.core.workflow.EvidenceCoverageEvaluator();
            assertThat(coverage.coreTerms("질문 답변 설명 방법 서울 설비 상태 설비",index,4)).containsExactly("서울","설비","상태");
            assertThat(coverage.coreTerms("질문 답변 설명 방법 서울 설비 상태 설비",index,2)).containsExactly("서울","설비");
        }
    }
    @Test void threePathsHaveIndependentHitsAndIdentifierIsACompleteNormalizedToken() {
        var scope=scope("TC",Set.of("READ"));
        var semantic=doc("bucket","a.md","온도 경고 기준",Set.of("READ"),1,0);
        var keyword=doc("bucket","b.md","설비 상태 확인",Set.of("READ"),-1,0);
        var identifier=doc("bucket","c.md","장비 ID=EQP-001 | 프로토콜=S1F2 | 대기=T3 | 값=42 | 설정=linger.ms",Set.of("READ"),-1,0);
        var bundle=bundle(List.of(semantic,keyword,identifier),List.of());
        try(var index=new MemoryDocumentKeywordIndex()) {
            install(index,scope,bundle);
            var found=index.search(scope,"설비",List.of("eqp-001"),new float[]{1,0},SearchSettings.defaults(),bundle);
            assertThat(found.semantic()).extracting(h->h.child().location().objectKey()).containsExactly("a.md");
            assertThat(found.keyword()).extracting(h->h.child().location().objectKey()).containsExactly("b.md");
            assertThat(found.identifier()).extracting(h->h.child().location().objectKey()).containsExactly("c.md");
            assertThat(index.search(scope,"42",List.of(),new float[]{0,1},SearchSettings.defaults(),bundle).identifier()).hasSize(1);
            assertThat(index.search(scope,"설정",List.of("linger.ms"),new float[]{0,1},SearchSettings.defaults(),bundle).identifier()).hasSize(1);
            for(String absent:List.of("2","S1F","EQP-00","T30","linger.m"))assertThat(index.search(scope,absent,List.of(absent),new float[]{0,1},SearchSettings.defaults(),bundle).identifier()).isEmpty();
            assertThat(index.search(scope,"Ｔ３",List.of(),new float[]{0,1},SearchSettings.defaults(),bundle).identifier()).isEmpty();
            assertThat(index.search(scope,"\"*:*\" OR password:*",List.of(),new float[]{0,1},SearchSettings.defaults(),bundle).keyword()).isEmpty();
        }
    }
    @Test void configuredCandidateWidthsAreAppliedSeparatelyBeforeFusion() {
        var scope=scope("TC",Set.of("READ"));var docs=new ArrayList<DocumentCorpus.Document>();for(int i=0;i<130;i++)docs.add(doc("bucket",String.format("%03d.md",i),"설비 상태 T3",Set.of("READ"),1,0));
        var bundle=bundle(docs,List.of());
        try(var index=new MemoryDocumentKeywordIndex()) {
            install(index,scope,bundle);
            for(int width:List.of(1,20,100)) {
                var settings=new SearchSettings(width,width,width,60,6,3,.9,.35,4,1);var found=index.search(scope,"설비 상태 T3",List.of(),new float[]{1,0},settings,bundle);
                for(var list:List.of(found.semantic(),found.keyword(),found.identifier()))assertThat(list).hasSize(width);
            }
        }
    }
    @Test void filtersBeforeEveryTopKAndKeepsBucketsSystemsAndInstalledRevisionSeparate() {
        var current=scope("TC",Set.of("READ"));var denied=doc("a","0.md","설비 T3",Set.of("ADMIN"),1,0);
        var first=doc("a","same.md","설비 T3",Set.of("READ"),1,0);var second=doc("b","same.md","설비 T3",Set.of("READ"),1,0);
        var stale=doc("a","old.md","설비 T3",Set.of("READ"),1,0);var p=stale.parents().getFirst();var c=p.children().getFirst();
        var bad=new DocumentCorpus.Child(c.id(),c.ordinal(),new DocumentLocation("a","old.md","c".repeat(64),"설정",3,5),c.header(),c.bodyMasked(),c.identifiers(),c.embedding());
        stale=new DocumentCorpus.Document(stale.id(),stale.bucket(),stale.objectKey(),REV,stale.title(),stale.permissions(),List.of(),List.of(new DocumentCorpus.Parent(p.id(),p.location(),p.header(),p.bodyMasked(),SHA,List.of(bad))));
        var bundle=bundle(List.of(denied,stale,second,first),List.of());
        var one=new SearchSettings(1,1,1,60,6,3,.9,.35,4,1);
        try(var index=new MemoryDocumentKeywordIndex()) {
            install(index,current,bundle);
            var found=index.search(current,"설비 T3",List.of(),new float[]{1,0},one,bundle);
            for(var list:List.of(found.semantic(),found.keyword(),found.identifier()))assertThat(list).singleElement().satisfies(h->{assertThat(h.child().location().bucket()).isEqualTo("a");assertThat(h.child().location().objectKey()).isEqualTo("same.md");});
            var all=index.search(current,"설비 T3",List.of(),new float[]{1,0},SearchSettings.defaults(),bundle);assertThat(all.semantic()).hasSize(2);
            assertThat(index.states(Set.of("READ"),Set.of(DocumentReferences.key(current.boundSystem(),denied.parents().getFirst().location()))).values()).containsExactly(com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader.State.UNAVAILABLE);
            var foreign=scope("WM",Set.of("READ"));install(index,foreign,bundle(List.of(doc("a","foreign.md","설비 T3",Set.of("READ"),1,0)),List.of()));
            assertThat(index.search(current,"설비 T3",List.of(),new float[]{1,0},one,bundle).semantic()).extracting(h->h.child().location().objectKey()).containsExactly("same.md");
            assertThatThrownBy(()->index.search(current,"설비",List.of(),new float[]{1},one,bundle)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(()->index.search(current,"설비",List.of(),new float[]{1,0},one,bundle(List.of(first),List.of()))).isInstanceOf(IllegalStateException.class);
        }
    }
    @Test void verifiedAnswersRequireCurrentLocationsPermissionsAndExactQuestionPrecedesCosine() {
        var scope=scope("TC",Set.of("READ"));var document=doc("bucket","a.md","설비 T3 상태",Set.of("READ"),1,0);var location=document.parents().getFirst().location();
        var exact=new DocumentCorpus.Answer(new UUID(0,2),"설비 T3",List.of(location),Set.of("READ"),new float[]{.9f,.4f});
        var similar=new DocumentCorpus.Answer(new UUID(0,1),"설비 상태",List.of(location),Set.of("READ"),new float[]{1,0});
        var denied=new DocumentCorpus.Answer(new UUID(0,3),"설비 T3",List.of(location),Set.of("ADMIN"),new float[]{1,0});
        var old=new DocumentCorpus.Answer(new UUID(0,4),"설비 T3",List.of(new DocumentLocation("bucket","a.md","d".repeat(64),"설정",3,5)),Set.of("READ"),new float[]{1,0});
        var belowThreshold=new DocumentCorpus.Answer(new UUID(0,5),"설비 T3",List.of(location),Set.of("READ"),new float[]{0,1});
        var bundle=bundle(List.of(document),List.of(old,denied,similar,exact,belowThreshold));
        try(var index=new MemoryDocumentKeywordIndex()) {
            install(index,scope,bundle);assertThat(index.search(scope,"설비 T3",List.of(),new float[]{1,0},SearchSettings.defaults(),bundle).verified()).extracting(DocumentSearch.Verified::id).containsExactly(exact.id(),similar.id());
            assertThat(index.search(scope,"다른 검색문","설비 T3",List.of(),new float[]{1,0},new float[]{1,0},SearchSettings.defaults(),bundle).verified()).extracting(DocumentSearch.Verified::id).containsExactly(exact.id(),similar.id());
        }
    }
    @Test void verifiedCosineUsesTheCurrentQuestionVectorWhileSemanticUsesTheSearchVector() {
        var scope=scope("TC",Set.of("READ"));var document=doc("bucket","a.md","설비 T3 상태",Set.of("READ"),1,0);var location=document.parents().getFirst().location();
        var exact=new DocumentCorpus.Answer(new UUID(0,1),"설비 상태 T3",List.of(location),Set.of("READ"),new float[]{0,1});
        var queryOnly=new DocumentCorpus.Answer(new UUID(0,2),"다른 질문",List.of(location),Set.of("READ"),new float[]{1,0});
        var bundle=bundle(List.of(document),List.of(queryOnly,exact));
        try(var index=new MemoryDocumentKeywordIndex()) {
            install(index,scope,bundle);
            // 단위 벡터에서 질문·정답 cosine=1, 질문·다른 답변 cosine=0이며 검색 질의는 그 반대입니다.
            var found=index.search(scope,"설비 T3","설비 상태 T3",List.of(),new float[]{1,0},new float[]{0,1},SearchSettings.defaults(),bundle);
            assertThat(found.semantic()).extracting(h->h.child().id()).containsExactly(document.parents().getFirst().children().getFirst().id());
            assertThat(found.verified()).extracting(DocumentSearch.Verified::id).containsExactly(exact.id());
        }
    }
}
