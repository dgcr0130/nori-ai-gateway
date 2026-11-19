package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;

class FeedbackServiceTest {
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"fixture",new ActorRef("actor"),"actor",Set.of("READ"),"a".repeat(64),Instant.MIN,Instant.MAX,"request");
    final UUID id=UUID.randomUUID();
    final DocumentLocation location=new DocumentLocation("bucket","rag/a.md","a".repeat(64),"설비",5,5);
    final DocumentLocation other=new DocumentLocation("bucket","rag/b.md","b".repeat(64),"설비",7,7);
    final AtomicInteger embeds=new AtomicInteger(),changes=new AtomicInteger();
    final Store store=new Store();
    final FeedbackService service=new FeedbackService(store,new ContentSanitizer(),text->List.of(),text->{embeds.incrementAndGet();return new float[]{1,0};},s->changes.incrementAndGet());
    class Store implements FeedbackStore {
        String question="설비 T3 코드";
        boolean parentOnly;
        String firstBody="설비 T3 S1F2 코드는 100이고 linger.ms는 1.5초입니다.";
        JsonNode action;Correction correction;String rating,comment;int saves;
        JsonNode evidence=SchemaContracts.parse("[{\"sourceType\":\"DOCUMENT\"}]");
        public Feedback evaluate(VerifiedSystemScope s,String job,String rating,String comment){this.rating=rating;this.comment=comment;return row();}
        public List<Feedback> list(VerifiedSystemScope s,String state,Instant time,UUID after,int limit){throw new AssertionError();}
        public Detail detail(VerifiedSystemScope s,UUID id){return new Detail(row(),"job",question,SchemaContracts.parse("{}"),evidence,List.of(),null);}
        Feedback row(){return new Feedback(id,1,"OPEN","DOWN","지적",Instant.EPOCH,Instant.EPOCH);}
        public Documents documents(VerifiedSystemScope s,List<DocumentLocation> locations){
            return new Documents(id,"model",2,"c".repeat(64),locations.stream().map(l->new EvidenceUnit("DOCUMENT","parent",SchemaContracts.JSON.valueToTree(l),"설비 > 코드",l.equals(location)?firstBody:"설비 S9F9 코드는 200입니다.",List.of("READ"),"SUCCESS",null,List.of(),SchemaContracts.parse("{}"),List.of(),parentOnly?List.of(other):List.of(l))).toList());
        }
        public Feedback resolve(VerifiedSystemScope s,UUID id,int version,String cause,JsonNode action,Correction correction,Map<String,String> initial){this.action=action;this.correction=correction;saves++;return row();}
        public Map<String,String> glossary(VerifiedSystemScope s){return Map.of();}
    }
    ObjectNode request(String cause,Map<String,Object> action){return (ObjectNode)SchemaContracts.JSON.valueToTree(Map.of("version",1,"cause",cause,"action",action));}
    Map<String,Object> correction(List<DocumentLocation> locations,String fact,String text,List<Integer> citations){return Map.of("locations",locations,"expectedFacts",List.of(fact),"correctAnswer",Map.of("sentences",List.of(Map.of("text",text,"citations",citations))));}
    void invalid(JsonNode input){assertThatThrownBy(()->service.resolve(scope,id,input,Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.status()).isEqualTo(400));assertThat(store.saves).isZero();assertThat(embeds).hasValue(0);}

    @Test void evaluationMasksAndCountsUnicodeCodepointsBeforeSaving() {
        service.evaluate(scope,"job",SchemaContracts.JSON.valueToTree(Map.of("rating","DOWN","comment","😀".repeat(2000))));assertThat(store.comment.codePointCount(0,store.comment.length())).isEqualTo(2000);
        assertThatThrownBy(()->service.evaluate(scope,"job",SchemaContracts.JSON.valueToTree(Map.of("rating","DOWN","comment","😀".repeat(2001))))).isInstanceOf(FeedbackFailure.class);
        service.evaluate(scope,"job",SchemaContracts.JSON.valueToTree(Map.of("rating","UP","comment","")));assertThat(store.comment).isEmpty();
        service.evaluate(scope,"job",SchemaContracts.JSON.valueToTree(Map.of("rating","DOWN","comment","password=private")));assertThat(store.comment).isEqualTo("[마스킹]");
    }
    @Test void unknownFieldsAndRatingsAreNotAccepted() {
        for(var input:List.of(Map.of("rating","OTHER","comment",""),Map.of("rating","DOWN","comment","","actor","other"),Map.of("rating","DOWN")))assertThatThrownBy(()->service.evaluate(scope,"job",SchemaContracts.JSON.valueToTree(input))).isInstanceOf(FeedbackFailure.class);
    }
    @Test void correctAnswerKeepsSentenceLocationNumbersAndMaskedFacts() {
        var input=request("ANSWER",correction(List.of(location,other),"S9F9","설비 S9F9 코드는 200입니다.",List.of(2)));
        service.resolve(scope,id,input,Map.of());assertThat(store.action.path("correctAnswer")).isEqualTo(input.path("action").path("correctAnswer"));assertThat(store.correction.locations()).containsExactly(location,other);assertThat(store.correction.permissions()).containsExactly("READ");assertThat(embeds).hasValue(1);assertThat(changes).hasValue(1);
    }
    @Test void wrongCitationCannotBorrowTheOtherLocationsNumberOrIdentifier(){invalid(request("ANSWER",correction(List.of(location,other),"S9F9","설비 S9F9 코드는 200입니다.",List.of(1))));}
    @Test void expectedFactMustBeInsideTheSubmittedLocationsBody(){invalid(request("ANSWER",correction(List.of(location),"S9F9","설비 T3 코드는 100입니다.",List.of(1))));}
    @Test void aSingleRemovedSentenceRejectsTheWholeReviewBeforeEmbedding() {
        var action=new LinkedHashMap<>(correction(List.of(location),"100","설비 T3 코드는 100입니다.",List.of(1)));
        action.put("correctAnswer",Map.of("sentences",List.of(Map.of("text","설비 T3 코드는 100입니다.","citations",List.of(1)),Map.of("text","설비 T3 코드는 999입니다.","citations",List.of(1)))));invalid(request("ANSWER",action));
    }
    @Test void structuredSentenceAndCitationBoundariesUseTheExistingChecker() {
        for(var row:List.of(Map.of("text","설비 T3 코드는 100입니다.","citations",List.of(0)),Map.of("text","설비 T3 코드는 100입니다.","citations",List.of(2)),Map.of("text","설비\nT3","citations",List.of(1)),Map.of("text","설비","citations",List.of()),Map.of("text","설비","citations",Collections.nCopies(7,1)),Map.of("text","😀".repeat(1001),"citations",List.of(1)))) {
            var action=new LinkedHashMap<>(correction(List.of(location),"100","설비",List.of(1)));action.put("correctAnswer",Map.of("sentences",List.of(row)));invalid(request("ANSWER",action));
        }
        var action=new LinkedHashMap<>(correction(List.of(location),"100","설비",List.of(1)));action.put("correctAnswer",Map.of("sentences",Collections.nCopies(21,Map.of("text","설비","citations",List.of(1)))));invalid(request("ANSWER",action));
    }
    @Test void thousandSupplementaryCodepointsAreAcceptedWithoutUtf16Counting() {
        service.resolve(scope,id,request("ANSWER",correction(List.of(location),"100","😀".repeat(1000),List.of(1))),Map.of());assertThat(store.saves).isOne();
    }
    @Test void expectedFactsHaveOneToTwentyValuesAnd256Codepoints() {
        var action=new LinkedHashMap<>(correction(List.of(location),"100","설비",List.of(1)));
        for(var facts:List.of(List.of(),Collections.nCopies(21,"100"),List.of("😀".repeat(257)))){action.put("expectedFacts",facts);invalid(request("ANSWER",action));}
    }
    @Test void documentZeroLocationsAndRejectDoNotEmbedOrCreateCorrection() {
        service.resolve(scope,id,request("DOCUMENT",Map.of("locations",List.of(),"note","보완 필요")),Map.of());assertThat(store.correction).isNull();
        service.resolve(scope,id,request("REJECT",Map.of("note","지적 기각")),Map.of());assertThat(store.correction).isNull();assertThat(embeds).hasValue(0);
    }
    @Test void documentLocationsRemainTargetsWithoutCorrectAnswer() {
        service.resolve(scope,id,request("DOCUMENT",Map.of("locations",List.of(location),"note","보완 필요")),Map.of());assertThat(store.action.has("correctAnswer")).isFalse();assertThat(store.correction.locations()).containsExactly(location);assertThat(store.correction.embedding()).isEmpty();
    }
    @Test void retrievalTermsAndNoteBoundariesAreValidatedBeforeStorage() {
        var action=new LinkedHashMap<>(correction(List.of(location),"100","설비 T3 코드는 100입니다.",List.of(1)));action.put("terms",Map.of("T3","설비 코드"));
        service.resolve(scope,id,request("RETRIEVAL",action),Map.of());assertThat(store.action.path("terms").path("T3").asString()).isEqualTo("설비 코드");
        for(String note:List.of("","😀".repeat(2001)))assertThatThrownBy(()->service.resolve(scope,id,request("REJECT",Map.of("note",note)),Map.of())).isInstanceOf(FeedbackFailure.class);
        service.resolve(scope,id,request("REJECT",Map.of("note","😀".repeat(2000))),Map.of());
    }
    @Test void liveOnlyActionsAreNotesForEveryCauseAndNeverUseDocumentsOrEmbedding() {
        store.evidence=SchemaContracts.parse("[{\"sourceType\":\"LOG\"},{\"sourceType\":\"TRACE\"}]");
        for(String cause:List.of("DOCUMENT","RETRIEVAL","ANSWER","REJECT")){service.resolve(scope,id,request(cause,Map.of("note","조회 처리기 확인")),Map.of());assertThat(store.correction).isNull();assertThat(store.action.size()).isOne();}assertThat(embeds).hasValue(0);
        assertThatThrownBy(()->service.resolve(scope,id,request("ANSWER",correction(List.of(location),"100","설비",List.of(1))),Map.of())).isInstanceOf(FeedbackFailure.class);
    }
    @Test void wrongVersionNeverPreparesOrSavesAndEmbeddingFailureDoesNotConfirm() {
        var input=request("ANSWER",correction(List.of(location),"100","설비",List.of(1)));input.put("version",2);
        assertThatThrownBy(()->service.resolve(scope,id,input,Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.code()).isEqualTo("REVIEW_CONFLICT"));assertThat(embeds).hasValue(0);
        var failed=new FeedbackService(store,new ContentSanitizer(),text->List.of(),text->new float[]{Float.NaN,0},s->changes.incrementAndGet());input.put("version",1);
        assertThatThrownBy(()->failed.resolve(scope,id,input,Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.status()).isEqualTo(503));assertThat(store.saves).isZero();
    }
    @Test void exactFact256Term64AndExpansion256UseCodepointsAndNotUtf16() {
        store.firstBody="😀".repeat(256);service.resolve(scope,id,request("ANSWER",correction(List.of(location),store.firstBody,"😀".repeat(1000),List.of(1))),Map.of());
        store.firstBody="설비 T3 코드는 100입니다.";var action=new LinkedHashMap<>(correction(List.of(location),"100","설비 T3 코드는 100입니다.",List.of(1)));
        for(var terms:List.of(Map.of("😀".repeat(65),"설비"),Map.of("T3","🧭".repeat(257)),Map.of())){action.put("terms",terms);assertThatThrownBy(()->service.resolve(scope,id,request("RETRIEVAL",action),Map.of())).isInstanceOfSatisfying(FeedbackFailure.class,e->assertThat(e.status()).isEqualTo(400));}
        action.put("terms",Map.of("😀".repeat(64),"🧭".repeat(256)));service.resolve(scope,id,request("RETRIEVAL",action),Map.of());assertThat(store.action.path("terms").size()).isOne();
    }
    @Test void twentySentencesSixCitationsAndSixLocationsAreAcceptedAndSevenLocationsAreRejected() {
        var action=new LinkedHashMap<>(correction(Collections.nCopies(7,location),"100","설비",List.of(1)));invalid(request("ANSWER",action));
        action.put("locations",Collections.nCopies(6,location));action.put("correctAnswer",Map.of("sentences",Collections.nCopies(20,Map.of("text","설비 T3 코드는 100입니다.","citations",List.of(1,2,3,4,5,6)))));
        action.put("expectedFacts",Collections.nCopies(20,"100"));service.resolve(scope,id,request("ANSWER",action),Map.of());assertThat(store.action.path("correctAnswer").path("sentences")).hasSize(20);assertThat(store.correction.locations()).hasSize(6);
    }
    @Test void questionCaseLengthDoesNotTruncateTheSnapshotOrLimitNoteOnlyReviews() {
        store.question="😀".repeat(2001);invalid(request("ANSWER",correction(List.of(location),"100","설비",List.of(1))));
        service.resolve(scope,id,request("DOCUMENT",Map.of("note","보완","locations",List.of())),Map.of());assertThat(store.question.codePointCount(0,store.question.length())).isEqualTo(2001);
        store.question="😀".repeat(2000);service.resolve(scope,id,request("ANSWER",correction(List.of(location),"100","설비",List.of(1))),Map.of());assertThat(store.correction).isNotNull();
    }
    @Test void answerCasesRequireChildBlockLocationsWhileDocumentTargetsCanUseAParent() {
        store.parentOnly=true;invalid(request("ANSWER",correction(List.of(location),"100","설비 T3 코드는 100입니다.",List.of(1))));
        service.resolve(scope,id,request("DOCUMENT",Map.of("note","보완","locations",List.of(location))),Map.of());assertThat(store.correction.locations()).containsExactly(location);assertThat(embeds).hasValue(0);
    }
}
