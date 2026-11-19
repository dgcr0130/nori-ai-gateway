package com.nori.ai.gateway.core.knowledge;

import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.*;
import static com.nori.ai.gateway.core.knowledge.DocumentCorpus.*;

/** 외부 호출 중에는 저장소 트랜잭션을 열지 않고, 완성된 묶음만 마지막에 활성화합니다. */
public final class DocumentSynchronization {
    private final DocumentObjectStore objects;
    private final DocumentExtractor extractor;
    private final DocumentIndexStore store;
    private final DocumentKeywordIndex keywords;
    private final InferenceClient inference;
    public DocumentSynchronization(DocumentObjectStore objects, DocumentExtractor extractor, DocumentIndexStore store, DocumentKeywordIndex keywords, InferenceClient inference) {
        this.objects = objects; this.extractor = extractor; this.store = store; this.keywords = keywords; this.inference = inference;
    }
    public Bundle synchronize(VerifiedSystemScope scope, List<Source> sources, Limits limits, String model, String configSha, int dimension) {
        store.cleanNonActive(scope);
        var documents = new ArrayList<Document>();
        record Located(Source source,DocumentObjectStore.ObjectKey key) {}
        var listed = new ArrayList<Located>();
        for (var source : sources) {
            for(var key:objects.list(source))listed.add(new Located(source,key));
        }
        listed.sort((a,b)->{
            int key=Arrays.compareUnsigned(a.key().key().getBytes(java.nio.charset.StandardCharsets.UTF_8),b.key().key().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return key!=0?key:Arrays.compareUnsigned(a.source().bucket().getBytes(java.nio.charset.StandardCharsets.UTF_8),b.source().bucket().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        });
        for (var located : listed) {
                var source=located.source();var key=located.key();
                var parsed = extractor.extract(source, key.key(), objects.download(source, key, limits.maxFileBytes()), limits);
                var parents = new ArrayList<Parent>();
                for (var parent : parsed.parents()) {
                    var children = new ArrayList<Child>();
                    for (var child : parent.children()) children.add(new Child(child.id(), child.ordinal(), child.location(), child.header(), child.bodyMasked(), child.identifiers(), embed(child.header()+"\n"+child.bodyMasked(), dimension)));
                    parents.add(new Parent(parent.id(), parent.location(), parent.header(), parent.bodyMasked(), parent.bodySha256(), children));
                }
                documents.add(new Document(parsed.id(), parsed.bucket(), parsed.objectKey(), parsed.revision(), parsed.title(), parsed.permissions(), parsed.issues(), parents));
        }
        var answers = new ArrayList<Answer>();
        var originalAnswers=store.activeAnswers(scope);
        for (var answer : originalAnswers) {
            boolean current = answer.locations().stream().allMatch(location -> documents.stream().anyMatch(d -> d.issues().isEmpty() && d.bucket().equals(location.bucket())
                && d.objectKey().equals(location.objectKey()) && d.revision().equals(location.revision()) && d.parents().stream().anyMatch(p -> p.location().section().equals(location.section())
                && p.location().lineFrom() <= location.lineFrom() && p.location().lineTo() >= location.lineTo())));
            if (current && !answer.locations().isEmpty()) {
                var permissions=new HashSet<String>();
                for(var location:answer.locations())for(var document:documents)if(document.bucket().equals(location.bucket()) && document.objectKey().equals(location.objectKey()))permissions.addAll(document.permissions());
                answers.add(new Answer(answer.id(), answer.questionMasked(), answer.locations(), permissions, embed(answer.questionMasked(), dimension)));
            }
        }
        var bundle = new Bundle(UUID.randomUUID(), model, dimension, configSha, documents, answers);
        store.saveBuilding(scope, bundle);
        // Lucene도 DB에 실제 저장된 청크로 구성합니다. 준비 실패 시 이전 ACTIVE를 건드리지 않습니다.
        var stored = store.load(scope, bundle.id());
        var preparedBundle = new Bundle(stored.id(), stored.embeddingModel(), stored.dimension(), stored.configSha256(), stored.documents(), answers);
        try (var prepared = keywords.prepare(scope, preparedBundle)) {
            store.activate(scope, bundle,originalAnswers.stream().map(Answer::id).collect(java.util.stream.Collectors.toSet()));
            keywords.install(scope, prepared);
        }
        return bundle;
    }
    private float[] embed(String text, int dimension) {
        var values = inference.embed(List.of(text));
        if (values == null || values.size() != 1 || values.getFirst() == null || values.getFirst().length != dimension || dimension < 1) throw invalidEmbedding();
        for (float value : values.getFirst()) if (!Float.isFinite(value)) throw invalidEmbedding();
        return values.getFirst();
    }
    private static InferenceClient.EmbeddingFailed invalidEmbedding() { return new InferenceClient.EmbeddingFailed(InferenceClient.FailureReason.EMPTY_RESPONSE, null); }
}
