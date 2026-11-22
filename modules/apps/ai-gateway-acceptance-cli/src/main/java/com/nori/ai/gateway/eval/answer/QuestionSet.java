package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import static com.nori.ai.gateway.eval.answer.NegationAnalyzer.normalize;

/** 사전 검사에 통과한 문항만 실행기에 넘깁니다. 원본 세트는 쓰지 않습니다. */
public final class QuestionSet {
    public record Question(String id,String systemId,String question,boolean answerable,List<String> expectedFacts,
            List<EvalMarkdown.Location> evidenceLocations,JsonNode context,List<String> permissions,String origin,
            List<Target> documentTargets,String predecessorId,String feedbackId,Integer reviewVersion) {}
    public record Target(String bucket,String objectKey,String revision) {}
    public record Bound(Question question,Map<String,List<EvalMarkdown.Location>> factLocations) {}
    @FunctionalInterface public interface Documents { EvalMarkdown.Document read(String bucket,String objectKey); }
    private static final Set<String> FIELDS=Set.of("id","systemId","question","answerable","expectedFacts","evidenceLocations",
        "context","permissions","origin","documentTargets","predecessorId","feedbackId","reviewVersion");
    public List<Question> load(Path file) {
        String text;try{text=java.nio.charset.StandardCharsets.UTF_8.newDecoder().onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
            .decode(java.nio.ByteBuffer.wrap(Json.bytes(file))).toString();}catch(java.nio.charset.CharacterCodingException invalid){throw invalid();}
        var result=new ArrayList<Question>();
        for(String line:text.split("\n",-1))if(!line.isBlank())result.add(parse(Json.parse(line.getBytes(java.nio.charset.StandardCharsets.UTF_8))));
        return List.copyOf(result);
    }
    public Question parse(JsonNode node) {
        require(node.isObject());node.fieldNames().forEachRemaining(k->require(FIELDS.contains(k)));
        for(String field:FIELDS)if(!Set.of("feedbackId","reviewVersion").contains(field))require(node.has(field));
        String id=text(node,"id"),system=text(node,"systemId"),query=text(node,"question"),origin=text(node,"origin");
        require(system.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,63}")&&size(query)<=2000&&node.path("answerable").isBoolean());
        boolean answerable=node.path("answerable").booleanValue();
        var facts=strings(node.get("expectedFacts"),false);facts.forEach(f->require(!normalize(f).isEmpty()));require(answerable?facts.size()>=1&&facts.size()<=20:facts.isEmpty());
        var locations=new ArrayList<EvalMarkdown.Location>();require(node.path("evidenceLocations").isArray());
        for(var location:node.path("evidenceLocations"))locations.add(location(location));
        require(answerable?locations.size()>=1&&locations.size()<=6:locations.isEmpty());
        var permissions=strings(node.get("permissions"),false);permissions.forEach(p->require(p.matches("[A-Z0-9_]{1,64}")));
        var targets=new ArrayList<Target>();require(node.path("documentTargets").isArray()&&node.path("documentTargets").size()<=6);
        for(var target:node.path("documentTargets")) {
            exact(target,Set.of("bucket","objectKey","revision"));targets.add(new Target(text(target,"bucket"),text(target,"objectKey"),revision(target)));
        }
        require(!answerable||targets.isEmpty());
        var context=node.get("context");context(context);
        String predecessor=null;require(node.path("predecessorId").isNull()||node.path("predecessorId").isTextual());
        if(!node.path("predecessorId").isNull())predecessor=text(node,"predecessorId");
        String feedback=null;Integer version=null;
        require(Set.of("INITIAL","FEEDBACK").contains(origin));
        if(origin.equals("FEEDBACK")) {
            feedback=text(node,"feedbackId");try{UUID.fromString(feedback);}catch(IllegalArgumentException invalid){throw invalid();}
            require(node.path("reviewVersion").isIntegralNumber()&&node.path("reviewVersion").canConvertToInt()&&node.path("reviewVersion").intValue()>0);
            version=node.path("reviewVersion").intValue();require(predecessor==null);
            if(!answerable)require(!targets.isEmpty());
        }else require(!node.has("feedbackId")&&!node.has("reviewVersion")&&targets.isEmpty());
        return new Question(id,system,query,answerable,List.copyOf(facts),List.copyOf(locations),context.deepCopy(),
            List.copyOf(permissions),origin,List.copyOf(targets),predecessor,feedback,version);
    }
    public List<Question> merge(List<Question> initial,List<Question> feedback,String system) {
        var all=new LinkedHashMap<String,Question>();
        for(var question:initial){require(question.origin().equals("INITIAL")&&!all.containsKey(question.id()));add(all,question,system);}
        for(var question:feedback){require(question.origin().equals("FEEDBACK"));add(all,question,system);}
        require(!all.isEmpty());
        for(var question:all.values()) {
            var visited=new HashSet<String>();var current=question;
            while(current!=null) {
                require(visited.add(current.id()));String predecessor=current.predecessorId();
                if(predecessor==null)break;require(all.containsKey(predecessor));current=all.get(predecessor);
            }
        }
        long yes=all.values().stream().filter(Question::answerable).count();require(yes>0&&yes<all.size());
        return List.copyOf(all.values());
    }
    private void add(Map<String,Question> all,Question question,String system) {
        require(question.systemId().equals(system));var previous=all.putIfAbsent(question.id(),question);
        require(previous==null||previous.equals(question));
    }
    public Bound bind(Question question,Documents documents) {
        var bodies=new LinkedHashMap<EvalMarkdown.Location,String>();
        for(var location:question.evidenceLocations()) {
            var document=documents.read(location.bucket(),location.objectKey());
            require(document.issues().isEmpty()&&document.revision().equals(location.revision()));
            String body=null;
            for(var parent:document.parents())for(var child:parent.children())if(child.location().equals(location))body=child.rawBody();
            require(body!=null);bodies.put(location,normalize(body));
        }
        for(var target:question.documentTargets())require(documents.read(target.bucket(),target.objectKey()).revision().equals(target.revision()));
        var byFact=new LinkedHashMap<String,List<EvalMarkdown.Location>>();
        for(String fact:question.expectedFacts()) {
            var found=bodies.entrySet().stream().filter(e->e.getValue().contains(normalize(fact))).map(Map.Entry::getKey).toList();
            require(!found.isEmpty());byFact.put(fact,found);
        }
        for(var body:bodies.entrySet())require(question.expectedFacts().stream().anyMatch(f->body.getValue().contains(normalize(f))));
        return new Bound(question,Collections.unmodifiableMap(byFact));
    }
    public static EvalMarkdown.Location location(JsonNode node) {
        exact(node,Set.of("bucket","objectKey","revision","section","lineFrom","lineTo"));
        require(node.path("lineFrom").isIntegralNumber()&&node.path("lineFrom").canConvertToInt()&&node.path("lineTo").isIntegralNumber()&&node.path("lineTo").canConvertToInt());
        int from=node.path("lineFrom").intValue(),to=node.path("lineTo").intValue();require(from>=1&&to>=from);
        return new EvalMarkdown.Location(text(node,"bucket"),text(node,"objectKey"),revision(node),text(node,"section"),from,to);
    }
    public static void context(JsonNode context) {
        require(context!=null&&(context.isObject()||context.isNull()));if(context.isNull())return;
        require(context.path("timezone").isTextual()&&context.path("referenceTime").isTextual());
        try {
            ZoneId zone=ZoneId.of(text(context,"timezone"));parseTime(text(context,"referenceTime"),zone);
            if(context.hasNonNull("timeWindow")) {
                var window=context.path("timeWindow");require(window.isObject());
                require(parseTime(text(window,"from"),zone).isBefore(parseTime(text(window,"to"),zone)));
            }
        }catch(DateTimeException invalid){throw invalid();}
    }
    private static Instant parseTime(String value,ZoneId zone) {
        try{return OffsetDateTime.parse(value).toInstant();}catch(DateTimeException offset){return LocalDateTime.parse(value).atZone(zone).toInstant();}
    }
    static List<String> strings(JsonNode node,boolean unique) {
        require(node!=null&&node.isArray());var result=new ArrayList<String>();
        for(var item:node){require(item.isTextual()&&!item.textValue().isBlank());result.add(item.textValue());}
        if(unique)require(new HashSet<>(result).size()==result.size());return result;
    }
    static String text(JsonNode node,String name){require(node.path(name).isTextual()&&!node.path(name).textValue().isBlank());return node.path(name).textValue();}
    static String revision(JsonNode node){String value=text(node,"revision");require(value.matches("[0-9a-f]{64}"));return value;}
    static void exact(JsonNode node,Set<String> keys){require(node.isObject()&&node.size()==keys.size());node.fieldNames().forEachRemaining(k->require(keys.contains(k)));}
    static int size(String text){return text.codePointCount(0,text.length());}
    static void require(boolean condition){if(!condition)throw invalid();}
    static EvalException invalid(){return new EvalException(2,"평가 문항·문서 입력이 올바르지 않습니다");}
}
