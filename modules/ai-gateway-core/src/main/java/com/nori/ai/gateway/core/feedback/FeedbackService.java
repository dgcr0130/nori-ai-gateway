package com.nori.ai.gateway.core.feedback;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.llm.SentenceEvidenceCheck;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import java.text.Normalizer;
import java.util.*;
import java.util.function.*;

/** 위치 열람·마스킹·문장 대조·임베딩을 끝낸 뒤 저장소의 짧은 원자 확정을 호출합니다. */
public final class FeedbackService {
    private final FeedbackStore store;
    private final ContentSanitizer sanitizer;
    private final SentenceEvidenceCheck check;
    private final Function<String,float[]> embed;
    private final Consumer<VerifiedSystemScope> changed;
    public FeedbackService(FeedbackStore store,ContentSanitizer sanitizer,SearchLanguage language,Function<String,float[]> embed,Consumer<VerifiedSystemScope> changed) {
        this.store=store;this.sanitizer=sanitizer;this.check=new SentenceEvidenceCheck(language);this.embed=embed;this.changed=changed;
    }
    public FeedbackStore.Feedback evaluate(VerifiedSystemScope scope,String answerId,JsonNode input) {
        fields(input,Set.of("rating","comment"));String rating=text(input.get("rating"),1,4),comment=text(input.get("comment"),0,2000);
        if(!Set.of("UP","DOWN").contains(rating))throw FeedbackFailure.invalid();
        var result=store.evaluate(scope,answerId,rating,mask(comment));changed.accept(scope);return result;
    }
    public FeedbackStore.Feedback resolve(VerifiedSystemScope scope,UUID id,JsonNode input,Map<String,String> initialGlossary) {
        var detail=store.detail(scope,id);
        fields(input,Set.of("version","cause","action"));var version=input.get("version");
        if(!version.isIntegralNumber()||!version.canConvertToInt()||version.asInt()<1)throw FeedbackFailure.invalid();
        if(version.asInt()!=detail.feedback().version()||!detail.feedback().state().equals("OPEN"))throw FeedbackFailure.conflict();
        String cause=text(input.get("cause"),1,16);if(!Set.of("DOCUMENT","RETRIEVAL","ANSWER","REJECT").contains(cause))throw FeedbackFailure.invalid();
        JsonNode submitted=input.get("action");boolean liveOnly=!detail.evidence().isEmpty();
        for(var evidence:detail.evidence())if(evidence.path("sourceType").asString().equals("DOCUMENT"))liveOnly=false;
        var action=SchemaContracts.JSON.createObjectNode();FeedbackStore.Correction correction=null;
        if(liveOnly||cause.equals("REJECT")) {
            fields(submitted,Set.of("note"));action.put("note",mask(text(submitted.get("note"),1,2000)));
        } else {
            fields(submitted,switch(cause) {case "DOCUMENT"->Set.of("note","locations");case "RETRIEVAL"->Set.of("terms","correctAnswer","expectedFacts","locations");default->Set.of("correctAnswer","expectedFacts","locations");});
            var nodes=submitted.get("locations");if(!nodes.isArray()||nodes.size()>6||!cause.equals("DOCUMENT")&&nodes.isEmpty())throw FeedbackFailure.invalid();
            var locations=new ArrayList<DocumentLocation>();
            for(var node:nodes) {
                fields(node,Set.of("bucket","objectKey","revision","section","lineFrom","lineTo"));
                for(String key:List.of("bucket","objectKey","revision","section"))if(!node.get(key).isString())throw FeedbackFailure.invalid();
                for(String key:List.of("lineFrom","lineTo"))if(!node.get(key).isIntegralNumber()||!node.get(key).canConvertToInt())throw FeedbackFailure.invalid();
                try{var location=SchemaContracts.JSON.treeToValue(node,DocumentLocation.class);if(location.objectKey().isBlank())throw FeedbackFailure.invalid();locations.add(location);}catch(IllegalArgumentException invalid){throw FeedbackFailure.invalid();}
            }
            // 기존 질문 접수의 상한을 바꾸거나 질문을 자르지 않고, 새 문항 계약만 검사합니다.
            if(!locations.isEmpty())text(SchemaContracts.JSON.valueToTree(detail.question()),1,2000);
            var documents=locations.isEmpty()?null:store.documents(scope,locations);
            action.set("locations",SchemaContracts.JSON.valueToTree(locations));
            if(cause.equals("DOCUMENT"))action.put("note",mask(text(submitted.get("note"),1,2000)));
            else {
                // true 문항의 정답 위치는 §4.9가 읽는 자식 블록의 줄 범위를 그대로 보존합니다.
                for(int i=0;i<locations.size();i++)if(!documents.units().get(i).childLocations().contains(locations.get(i)))throw FeedbackFailure.invalid();
                var facts=submitted.get("expectedFacts");if(!facts.isArray()||facts.isEmpty()||facts.size()>20)throw FeedbackFailure.invalid();
                var maskedFacts=SchemaContracts.JSON.createArrayNode();
                for(var fact:facts) {
                    String value=mask(text(fact,1,256));
                    if(documents.units().stream().noneMatch(u->normalize(u.body()).contains(normalize(value))))throw FeedbackFailure.invalid();
                    maskedFacts.add(value);
                }
                action.set("expectedFacts",maskedFacts);
                var correct=submitted.get("correctAnswer");fields(correct,Set.of("sentences"));
                var sentences=correct.get("sentences");if(!sentences.isArray()||sentences.isEmpty()||sentences.size()>20)throw FeedbackFailure.invalid();
                var masked=SchemaContracts.JSON.createObjectNode();var rows=masked.putArray("sentences");
                for(var sentence:sentences) {
                    fields(sentence,Set.of("text","citations"));String value=text(sentence.get("text"),1,1000);
                    rows.addObject().put("text",mask(value)).set("citations",sentence.get("citations").deepCopy());
                }
                var checked=check.check(masked.toString(),documents.units());
                if(!checked.kind().equals("ANSWER")||checked.sentences().size()!=sentences.size())throw FeedbackFailure.invalid();
                action.set("correctAnswer",masked);
                if(cause.equals("RETRIEVAL")) {
                    var terms=submitted.get("terms");if(!terms.isObject()||terms.isEmpty()||terms.size()>20)throw FeedbackFailure.invalid();
                    var maskedTerms=SchemaContracts.JSON.createObjectNode();
                    terms.properties().stream().sorted(Map.Entry.comparingByKey()).forEach(entry->{
                        String term=mask(text(SchemaContracts.JSON.valueToTree(entry.getKey()),1,64));String value=mask(text(entry.getValue(),1,256));
                        if(maskedTerms.has(term))throw FeedbackFailure.invalid();maskedTerms.put(term,value);
                    });action.set("terms",maskedTerms);
                }
            }
            var permissions=new TreeSet<String>((a,b)->Arrays.compareUnsigned(a.getBytes(java.nio.charset.StandardCharsets.UTF_8),b.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            if(documents!=null)for(var unit:documents.units())permissions.addAll(unit.permissions());
            float[] vector=new float[0];
            if(!cause.equals("DOCUMENT")) {
                vector=embed.apply(detail.question());
                if(vector==null||vector.length!=documents.dimension())throw new FeedbackFailure(503,"KNOWLEDGE_UNAVAILABLE");
                for(float value:vector)if(!Float.isFinite(value))throw new FeedbackFailure(503,"KNOWLEDGE_UNAVAILABLE");
            }
            if(documents!=null)correction=new FeedbackStore.Correction(documents,locations,List.copyOf(permissions),vector);
        }
        var result=store.resolve(scope,id,version.asInt(),cause,action,correction,initialGlossary);changed.accept(scope);return result;
    }
    public static void fields(JsonNode value,Set<String> expected) {
        if(value==null||!value.isObject()||value.size()!=expected.size()||!expected.stream().allMatch(value::has))throw FeedbackFailure.invalid();
    }
    public static String text(JsonNode value,int min,int max) {
        if(value==null||!value.isString())throw FeedbackFailure.invalid();String text=value.asString();int size=text.codePointCount(0,text.length());
        if(size<min||size>max||min>0&&text.isBlank())throw FeedbackFailure.invalid();return text;
    }
    private String mask(String value){return sanitizer.sanitize(value).text();}
    private static String normalize(String value){return Normalizer.normalize(value,Normalizer.Form.NFC).replaceAll("\\s+"," ");}
}
