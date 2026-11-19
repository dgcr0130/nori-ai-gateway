package com.nori.ai.gateway.core.feedback;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import tools.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.*;

/** 스냅샷의 내부 UUID는 저장소 안에서만 해석합니다. */
public interface FeedbackStore {
    record Feedback(UUID feedbackId,int version,String state,String rating,String comment,Instant createdAt,Instant updatedAt) {
        @Override public String toString(){return "Feedback[보호됨]";}
    }
    record Detail(Feedback feedback,String answerId,String question,JsonNode answer,JsonNode evidence,List<JsonNode> reviews,JsonNode questionCase) {
        public Detail { reviews=List.copyOf(reviews); }
        @Override public String toString(){return "Detail[보호됨]";}
    }
    record Documents(UUID bundleId,String model,int dimension,String configSha256,List<EvidenceUnit> units) {
        public Documents { units=List.copyOf(units); }
    }
    record Correction(Documents documents,List<DocumentLocation> locations,List<String> permissions,float[] embedding) {
        public Correction { locations=List.copyOf(locations);permissions=List.copyOf(permissions);embedding=embedding.clone(); }
        @Override public float[] embedding(){return embedding.clone();}
        @Override public String toString(){return "Correction[보호됨]";}
    }
    Feedback evaluate(VerifiedSystemScope scope,String answerId,String rating,String comment);
    List<Feedback> list(VerifiedSystemScope scope,String state,Instant afterTime,UUID afterId,int limit);
    Detail detail(VerifiedSystemScope scope,UUID feedbackId);
    Documents documents(VerifiedSystemScope scope,List<DocumentLocation> locations);
    Feedback resolve(VerifiedSystemScope scope,UUID feedbackId,int version,String cause,JsonNode action,Correction correction,Map<String,String> initialGlossary);
    Map<String,String> glossary(VerifiedSystemScope scope);
}
