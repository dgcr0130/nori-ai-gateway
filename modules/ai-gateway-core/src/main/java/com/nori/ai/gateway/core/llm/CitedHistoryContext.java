package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.util.*;
import java.util.regex.Pattern;
import tools.jackson.databind.node.ObjectNode;

/** 접근 확인된 이전 인용을 가리키는 요청 결속 내부 참조입니다. */
public final class CitedHistoryContext {
    private CitedHistoryContext() {}
    public static final int MAX_DOCUMENT_CITATIONS = 10;
    private static final Pattern REFERENTIAL = Pattern.compile("^(?:그렇게|그중|그런(?=\\s)|그것|그거|그(?=\\s)|해당(?=\\s)|앞서(?=\\s)|이전(?=\\s)).*", Pattern.DOTALL);
    public static final String INSTRUCTION = """
        추가 내부 필드 historyRef는 null 또는 historyReferences에 제공된 ref 하나다. 응답은 needs와 historyRef만 갖는다.
        현재 question을 다시 쓰지 않는다. 현재 질문이 앞선 대상을 가리키며 그 문맥이 꼭 필요한 문서 전용 질문일 때만 historyRef를 고른다. 독립 질문이면 null이다.
        historyReferences는 접근 가능한 대화 쌍의 인용 위치 참조이고 사실의 정답이 아니다. 이전 답변을 사실 근거로 쓰지 않으며 현재 문서가 실제 선택돼야 한다.
        subject는 subjectCandidates의 실제 식별자 또는 null만 쓴다. 화면·개념 이름을 새 대상으로 만들지 않는다.
        historyRef를 고르면 문서 전용 의도만 선택한다. 주어진 ref나 인용 위치를 만들거나 고치지 않는다.
        """;

    public record Pair(String ref, String sourceJobId, String question, String answer, List<DocumentLocation> citations) {
        public Pair {
            if (ref == null || !ref.matches("H[1-4]") || sourceJobId == null || sourceJobId.isBlank() || question == null || question.isBlank()
                || question.codePointCount(0, question.length()) > 2000 || answer == null || citations == null || citations.isEmpty() || citations.size() > MAX_DOCUMENT_CITATIONS) throw invalid();
            citations = List.copyOf(citations);
            if (new HashSet<>(citations).size() != citations.size()) throw invalid();
        }
        @Override public String toString() { return "CitedHistoryPair[보호됨]"; }
    }

    public static final class Bound {
        private final String question;
        private final Pair pair;
        private Bound(String question, Pair pair) { this.question = question; this.pair = pair; }
        public Pair pair() { return pair; }
        public String originalQuestion() { return question; }
        @Override public String toString() { return "BoundHistoryReference[보호됨]"; }
    }
    public record Decoded(String classificationJson, Optional<Bound> reference) {
        public Decoded { Objects.requireNonNull(reference); }
        @Override public String toString() { return "CitedHistoryDecoded[보호됨]"; }
    }

    public static boolean referential(String question) { return question != null && REFERENTIAL.matcher(question.strip()).matches(); }

    public static String classificationPrompt(String base) {
        return base.replace("응답은 needs 필수 필드만 가진 JSON 객체 한 개다.",
            "응답은 needs와 historyRef 두 필수 필드만 가진 JSON 객체 한 개다.") + INSTRUCTION;
    }

    /** H 번호는 해당 호출의 순번이므로 새 접근 검사에서는 원래 source Job·질문/답변·인용의 동일성을 확인합니다. */
    public static boolean accessible(Bound bound,List<Pair> available) {
        return bound!=null&&available.stream().anyMatch(pair->pair.sourceJobId().equals(bound.pair.sourceJobId())&&pair.question().equals(bound.pair.question())
            &&pair.answer().equals(bound.pair.answer())&&pair.citations().equals(bound.pair.citations()));
    }

    public static Optional<Bound> reference(com.nori.ai.gateway.core.job.JobContext job) {
        Object value=job.classification().get("historyReference");return value instanceof Bound bound&&bound.originalQuestion().equals(job.requestInput().maskedQuestion())?Optional.of(bound):Optional.empty();
    }

    /** 질의 원문은 자르지 않고 접근 가능한 실제 과거 질문·절 제목을 전체 문자열 단위로만 덧붙입니다. 충분성 oracle이 아닙니다. */
    public static String retrievalQuery(String query,Bound bound,List<Pair> available) {
        if(!accessible(bound,available))return query;
        var literals=new LinkedHashSet<String>();literals.add(bound.pair.question());bound.pair.citations().forEach(location->literals.add(location.section()));
        String result=query;for(String literal:literals)if(!literal.isBlank()&&!result.contains(literal)) {
            String candidate=result+"\n"+literal;if(candidate.codePointCount(0,candidate.length())<=512)result=candidate;
        }
        return result;
    }

    public static StructuredOutput output(StructuredOutput needsOutput, String question, List<Pair> available) {
        validatePairs(available);
        var root = (ObjectNode) SchemaContracts.JSON.readTree(needsOutput.schema());
        if (!root.path("properties").isObject() || root.path("properties").size() != 1 || !root.path("properties").has("needs") || root.path("properties").has("historyRef")) throw invalid();
        var refs = new ArrayList<Object>(); refs.add(null);
        if (referential(question)) available.forEach(pair -> refs.add(pair.ref()));
        ((ObjectNode) root.get("properties")).set("historyRef", SchemaContracts.JSON.valueToTree(Map.of("enum", refs)));
        ((tools.jackson.databind.node.ArrayNode) root.get("required")).add("historyRef");
        return new StructuredOutput(SchemaContracts.JSON.writeValueAsString(root));
    }

    /** needs를 복구·수정하지 않고 내부 참조만 검증·분리합니다. 의도의 문서 전용 여부는 분류기가 선언과 대조합니다. */
    public static Decoded decode(StructuredOutput needsOutput, String question, List<Pair> available, Set<String> verifiedCurrentRefs, String response) {
        if (!output(needsOutput, question, available).accepts(response)) throw invalid();
        var value = (ObjectNode) SchemaContracts.JSON.readTree(response);
        var ref = value.remove("historyRef");
        Optional<Bound> selected = Optional.empty();
        if (!ref.isNull()) {
            var pair = available.stream().filter(p -> p.ref().equals(ref.asString())).findFirst().orElseThrow(CitedHistoryContext::invalid);
            // 대상 후보는 실제 보존 이력만 받는 분류 decoder가 검사합니다.
            selected = Optional.of(new Bound(question, pair));
        }
        String projected = SchemaContracts.JSON.writeValueAsString(value);
        if (!needsOutput.accepts(projected)) throw invalid();
        return new Decoded(projected, selected);
    }

    /** 이전 답변은 대조 본문이 아닙니다. 현재 선택된 같은 문서 부모·현재 권한·원래 식별자를 모두 요구합니다. */
    public static boolean sufficient(Bound bound, String originalQuestion, List<String> targets, List<Pair> currentlyAccessible,
            List<EvidenceUnit> selected, Set<String> currentPermissions) {
        if (bound == null || !bound.question.equals(originalQuestion) || !referential(originalQuestion) || !accessible(bound,currentlyAccessible)) return false;
        var matched = selected.stream().filter(unit -> unit.sourceType().equals("DOCUMENT") && !unit.permissions().isEmpty()
            && currentPermissions.containsAll(unit.permissions()) && bound.pair.citations().stream().anyMatch(location -> contains(unit, location))).toList();
        if (matched.isEmpty()) return false;
        return SearchText.identifiers(originalQuestion, targets).stream().allMatch(id -> matched.stream().anyMatch(unit -> SearchText.containsExact(unit.body(), id)));
    }

    public static List<Map<String, Object>> modelReferences(List<Pair> pairs) {
        validatePairs(pairs);
        return pairs.stream().map(pair -> Map.<String, Object>of("ref", pair.ref(), "citations", pair.citations())).toList();
    }
    public static Optional<ClassificationService.HistoryPair> composerHistory(Bound bound, String originalQuestion, List<String> targets,
            List<Pair> currentlyAccessible, List<EvidenceUnit> selected, Set<String> currentPermissions) {
        if (!sufficient(bound, originalQuestion, targets, currentlyAccessible, selected, currentPermissions)) return Optional.empty();
        return Optional.of(new ClassificationService.HistoryPair(bound.pair.question(), bound.pair.answer()));
    }
    private static boolean contains(EvidenceUnit unit, DocumentLocation reference) {
        try {
            var current = SchemaContracts.JSON.treeToValue(unit.location(), DocumentLocation.class);
            return current.bucket().equals(reference.bucket()) && current.objectKey().equals(reference.objectKey()) && current.revision().equals(reference.revision())
                && current.section().equals(reference.section()) && current.lineFrom() <= reference.lineFrom() && current.lineTo() >= reference.lineTo();
        } catch (RuntimeException invalid) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(CitedHistoryContext.class.getName()),"CITED_HISTORY_CONTEXT_FAILED",invalid); return false; }
    }
    private static void validatePairs(List<Pair> pairs) {
        if (pairs == null || pairs.size() > 4 || pairs.stream().map(Pair::ref).distinct().count() != pairs.size()) throw invalid();
    }
    private static IllegalArgumentException invalid() { return new IllegalArgumentException("이전 인용 참조 형식이 잘못되었습니다"); }
}
