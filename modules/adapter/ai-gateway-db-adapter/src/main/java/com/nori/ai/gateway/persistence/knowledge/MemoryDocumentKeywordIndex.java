package com.nori.ai.gateway.persistence.knowledge;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.ko.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.analysis.ko.dict.UserDictionary;
import org.apache.lucene.store.ByteBuffersDirectory;
import java.io.IOException;
import java.util.*;

/** DB 정본에서 준비한 메모리 색인을 접수 전에 교체하며 디스크 파일을 만들지 않습니다. */
public final class MemoryDocumentKeywordIndex implements DocumentKeywordIndex, SearchLanguage, AutoCloseable {
    private final Map<String,MemorySet> active=new HashMap<>();
    private static final class MemorySet implements Prepared {
        final String system; final UUID bundleId; final ByteBuffersDirectory directory;
        final DirectoryReader reader; final KoreanAnalyzer analyzer; boolean installed;
        final DocumentCorpus.Bundle bundle;
        final Map<UUID,DocumentSearch.Hit> hits=new HashMap<>();
        MemorySet(String system,DocumentCorpus.Bundle bundle,ByteBuffersDirectory directory,DirectoryReader reader,KoreanAnalyzer analyzer) {
            this.system=system;this.bundleId=bundle.id();this.bundle=bundle;this.directory=directory;this.reader=reader;this.analyzer=analyzer;
            for(var doc:bundle.documents())if(doc.issues().isEmpty())for(var parent:doc.parents())for(var child:parent.children())
                if(child.location().bucket().equals(doc.bucket())&&child.location().objectKey().equals(doc.objectKey())&&child.location().revision().equals(doc.revision()))
                    hits.put(child.id(),new DocumentSearch.Hit(parent,child,doc.permissions()));
        }
        @Override public void close() { if(!installed)dispose(); }
        void dispose() { try { reader.close();directory.close();analyzer.close(); } catch(IOException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(MemoryDocumentKeywordIndex.class.getName()),"MEMORY_DOCUMENT_KEYWORD_INDEX_FAILED",failure); throw new IllegalStateException("메모리 색인을 정리할 수 없습니다"); } }
    }
    @Override public Prepared prepare(VerifiedSystemScope scope,DocumentCorpus.Bundle bundle) {
        var directory=new ByteBuffersDirectory();var analyzer=new KoreanAnalyzer();
        try {
            try(var writer=new IndexWriter(directory,new IndexWriterConfig(analyzer))) {
                for(var document:bundle.documents()) if(document.issues().isEmpty()) for(var parent:document.parents()) for(var child:parent.children()) {
                    var row=row(scope,bundle.id(),child.id(),document.permissions(),child.header()+"\n"+child.bodyMasked(),"DOCUMENT");
                    row.add(new StringField("bucket",document.bucket(),Field.Store.YES));row.add(new StringField("objectKey",document.objectKey(),Field.Store.YES));
                    row.add(new StringField("revision",document.revision(),Field.Store.YES));writer.addDocument(row);
                }
                for(var answer:bundle.answers())writer.addDocument(row(scope,bundle.id(),answer.id(),answer.permissions(),answer.questionMasked(),"VERIFIED_ANSWER"));
            }
            return new MemorySet(scope.boundSystem().name(),bundle,directory,DirectoryReader.open(directory),analyzer);
        } catch(IOException|RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(MemoryDocumentKeywordIndex.class.getName()),"MEMORY_DOCUMENT_KEYWORD_INDEX_FAILED",failure);
            try { directory.close(); } catch(IOException cleanup) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(MemoryDocumentKeywordIndex.class.getName()),"MEMORY_DOCUMENT_KEYWORD_INDEX_FAILED",cleanup); throw new IllegalStateException("문서 색인 정리에 실패했습니다"); }
            finally { analyzer.close(); }
            throw new IllegalStateException("문서 색인 구성에 실패했습니다");
        }
    }
    private static Document row(VerifiedSystemScope scope,UUID bundle,UUID id,Set<String> permissions,String body,String kind) {
        var row=new Document();row.add(new StringField("system",scope.boundSystem().name(),Field.Store.YES));row.add(new StringField("bundle",bundle.toString(),Field.Store.YES));
        row.add(new StringField("id",id.toString(),Field.Store.YES));row.add(new StringField("kind",kind,Field.Store.YES));
        for(var permission:permissions)row.add(new StringField("permission",permission,Field.Store.YES));row.add(new TextField("body",body,Field.Store.NO));return row;
    }
    @Override public synchronized void install(VerifiedSystemScope scope,Prepared prepared) {
        if(!(prepared instanceof MemorySet memory)||!memory.system.equals(scope.boundSystem().name())||memory.installed)throw new IllegalArgumentException("색인 시스템이 일치하지 않습니다");
        var previous=active.put(memory.system,memory);memory.installed=true;if(previous!=null)previous.dispose();
    }
    /** DB의 현재 활성 묶음과 메모리 묶음이 다르면 오래된 자료로 검색하지 않습니다. */
    public synchronized DocumentSearch.Round search(VerifiedSystemScope scope,String query,List<String> targets,float[] vector,SearchSettings settings,DocumentCorpus.Bundle current) {
        return search(scope,query,query,targets,vector,vector,settings,current);
    }
    public synchronized DocumentSearch.Round search(VerifiedSystemScope scope,String query,String question,List<String> targets,float[] vector,float[] questionVector,SearchSettings settings,DocumentCorpus.Bundle current) {
        var memory=active.get(scope.boundSystem().name());
        if(memory==null||!memory.bundleId.equals(current.id())||!memory.bundle.embeddingModel().equals(current.embeddingModel())
                ||memory.bundle.dimension()!=current.dimension()||!memory.bundle.configSha256().equals(current.configSha256()))
            throw new IllegalStateException("활성 검색 묶음이 일치하지 않습니다");
        validateVector(vector,current.dimension());
        validateVector(questionVector,current.dimension());
        var allowed=memory.hits.values().stream().filter(h->scope.perm().containsAll(h.permissions())).sorted(RankFusion.ORDER).toList();
        var cosine=new HashMap<UUID,Double>();for(var hit:allowed)cosine.put(hit.child().id(),cosine(vector,hit.child().embedding()));
        var semantic=allowed.stream().filter(h->cosine.get(h.child().id())>=settings.minSemanticCosine())
            .sorted(Comparator.<DocumentSearch.Hit>comparingDouble(h->cosine.get(h.child().id())).reversed().thenComparing(RankFusion.ORDER)).limit(settings.semanticCandidates()).toList();
        var identifiers=SearchText.identifiers(query,targets);
        var identifier=allowed.stream().filter(h->identifiers.stream().anyMatch(id->SearchText.containsExact(h.child().header()+"\n"+h.child().bodyMasked(),id)))
            .limit(settings.identifierCandidates()).toList();
        var keyword=new ArrayList<DocumentSearch.Hit>();
        try {
            var words=new LinkedHashSet<String>();
            try(var stream=memory.analyzer.tokenStream("body",SearchText.normalize(query))) {
                var term=stream.addAttribute(CharTermAttribute.class);stream.reset();while(stream.incrementToken())words.add(term.toString());stream.end();
            }
            if(!words.isEmpty()&&!allowed.isEmpty()) {
                // 사용자 검색 문법은 파싱하지 않습니다. 분석한 토큰만 SHOULD 절로 사용합니다.
                var terms=new BooleanQuery.Builder();for(String word:words)terms.add(new TermQuery(new org.apache.lucene.index.Term("body",word)),BooleanClause.Occur.SHOULD);
                terms.setMinimumNumberShouldMatch(1);
                var filter=new TermInSetQuery("id",allowed.stream().map(h->new org.apache.lucene.util.BytesRef(h.child().id().toString())).toList());
                var search=new BooleanQuery.Builder().add(terms.build(),BooleanClause.Occur.MUST).add(filter,BooleanClause.Occur.FILTER).build();
                var scores=new HashMap<UUID,Float>();var searcher=new IndexSearcher(memory.reader);
                for(var hit:searcher.search(search,allowed.size()).scoreDocs) {
                    UUID id=UUID.fromString(searcher.storedFields().document(hit.doc).get("id"));scores.put(id,hit.score);
                }
                allowed.stream().filter(h->scores.containsKey(h.child().id()))
                    .sorted(Comparator.<DocumentSearch.Hit>comparingDouble(h->scores.get(h.child().id())).reversed().thenComparing(RankFusion.ORDER))
                    .limit(settings.keywordCandidates()).forEach(keyword::add);
            }
        } catch(IOException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(MemoryDocumentKeywordIndex.class.getName()),"MEMORY_DOCUMENT_KEYWORD_INDEX_FAILED",failure); throw new IllegalStateException("문서 키워드 검색에 실패했습니다"); }
        record Candidate(DocumentCorpus.Answer answer,boolean exact,double cosine,List<DocumentSearch.Hit> parents) {}
        var verified=new ArrayList<Candidate>();
        for(var answer:current.answers()) {
            if(!scope.perm().containsAll(answer.permissions())||answer.locations().isEmpty())continue;
            var parents=new LinkedHashMap<UUID,DocumentSearch.Hit>();boolean valid=true;
            for(var location:answer.locations()) {
                var found=allowed.stream().filter(h->contains(h.parent().location(),location)).findFirst();
                if(found.isEmpty()){valid=false;break;}parents.putIfAbsent(found.get().parent().id(),found.get());
            }
            if(!valid)continue;
            double score=cosine(questionVector,answer.embedding());boolean exact=SearchText.normalize(question).equals(SearchText.normalize(answer.questionMasked()));
            if(score>=settings.verifiedMinCosine())verified.add(new Candidate(answer,exact,score,List.copyOf(parents.values())));
        }
        var answers=verified.stream().sorted(Comparator.comparing(Candidate::exact).reversed().thenComparing(Comparator.comparingDouble(Candidate::cosine).reversed()).thenComparing(c->c.answer().id().toString()))
            .limit(settings.verifiedCandidates()).map(c->new DocumentSearch.Verified(c.answer().id(),c.parents(),c.answer().locations())).toList();
        return new DocumentSearch.Round(semantic,keyword,identifier,answers);
    }
    private static boolean contains(DocumentLocation parent,DocumentLocation child) {
        return parent.bucket().equals(child.bucket())&&parent.objectKey().equals(child.objectKey())&&parent.revision().equals(child.revision())
            &&parent.section().equals(child.section())&&parent.lineFrom()<=child.lineFrom()&&parent.lineTo()>=child.lineTo();
    }
    private static void validateVector(float[] vector,int dimension) {
        if(vector==null||vector.length!=dimension)throw new IllegalArgumentException("질문 벡터 차원이 다릅니다");
        for(float value:vector)if(!Float.isFinite(value))throw new IllegalArgumentException("질문 벡터가 유효하지 않습니다");
    }
    private static double cosine(float[] a,float[] b) {
        validateVector(b,a.length);double dot=0,aa=0,bb=0;for(int i=0;i<a.length;i++){dot+=(double)a[i]*b[i];aa+=(double)a[i]*a[i];bb+=(double)b[i]*b[i];}
        return aa==0||bb==0?0:Math.max(-1,Math.min(1,dot/Math.sqrt(aa*bb)));
    }
    @Override public List<SearchLanguage.Term> analyze(String text) {
        var terms=new ArrayList<SearchLanguage.Term>();
        try(var tokenizer=new KoreanTokenizer(org.apache.lucene.util.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,(UserDictionary)null,KoreanTokenizer.DecompoundMode.DISCARD,false)) {
            tokenizer.setReader(new java.io.StringReader(SearchText.normalize(text)));
            var term=tokenizer.addAttribute(CharTermAttribute.class);var pos=tokenizer.addAttribute(PartOfSpeechAttribute.class);
            tokenizer.reset();while(tokenizer.incrementToken())terms.add(new SearchLanguage.Term(term.toString(),pos.getLeftPOS().name()));tokenizer.end();
        }catch(IOException failure){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(MemoryDocumentKeywordIndex.class.getName()),"MEMORY_DOCUMENT_KEYWORD_INDEX_FAILED",failure);throw new IllegalStateException("질문 형태소 분석에 실패했습니다");}
        return List.copyOf(terms);
    }
    public synchronized Optional<UUID> activeBundle(VerifiedSystemScope scope) { return Optional.ofNullable(active.get(scope.boundSystem().name())).map(value->value.bundleId); }
    public synchronized Map<com.nori.ai.gateway.core.result.ResultSourceDeps.DocKey,com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader.State> states(Set<String> permissions,Set<com.nori.ai.gateway.core.result.ResultSourceDeps.DocKey> keys) {
        var states=new HashMap<com.nori.ai.gateway.core.result.ResultSourceDeps.DocKey,com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader.State>();
        for(var key:keys) {
            var memory=active.get(key.ownerSystem());var state=com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader.State.UNAVAILABLE;
            if(memory==null)state=com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader.State.UNKNOWN;
            else for(var hit:memory.hits.values())if(permissions.containsAll(hit.permissions())&&DocumentReferences.key(com.nori.ai.gateway.core.scope.OwnerSystem.valueOf(key.ownerSystem()),hit.parent().location()).equals(key)) {
                state=com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader.State.AVAILABLE;break;
            }
            states.put(key,state);
        }
        return states;
    }
    @Override public synchronized void close() { for(var value:active.values())value.dispose();active.clear(); }
}
