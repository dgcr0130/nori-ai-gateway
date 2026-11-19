package com.nori.ai.gateway.persistence.knowledge;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import java.io.IOException;
import java.util.*;

/** DB 정본에서 준비한 메모리 색인을 접수 전에 교체하며 디스크 파일을 만들지 않습니다. */
public final class MemoryDocumentKeywordIndex implements DocumentKeywordIndex, AutoCloseable {
    private final Map<String,MemorySet> active=new HashMap<>();
    private static final class MemorySet implements Prepared {
        final String system; final UUID bundleId; final ByteBuffersDirectory directory;
        final DirectoryReader reader; final KoreanAnalyzer analyzer; boolean installed;
        MemorySet(String system,UUID id,ByteBuffersDirectory directory,DirectoryReader reader,KoreanAnalyzer analyzer) { this.system=system;this.bundleId=id;this.directory=directory;this.reader=reader;this.analyzer=analyzer; }
        @Override public void close() { if(!installed)dispose(); }
        void dispose() { try { reader.close();directory.close();analyzer.close(); } catch(IOException failure) { throw new IllegalStateException("메모리 색인을 정리할 수 없습니다"); } }
    }
    @Override public Prepared prepare(VerifiedSystemScope scope,DocumentCorpus.Bundle bundle) {
        var directory=new ByteBuffersDirectory();var analyzer=new KoreanAnalyzer();
        try {
            try(var writer=new IndexWriter(directory,new IndexWriterConfig(analyzer))) {
                for(var document:bundle.documents()) for(var parent:document.parents()) for(var child:parent.children()) {
                    var row=row(scope,bundle.id(),child.id(),document.permissions(),child.header()+"\n"+child.bodyMasked(),"DOCUMENT");
                    row.add(new StringField("bucket",document.bucket(),Field.Store.YES));row.add(new StringField("objectKey",document.objectKey(),Field.Store.YES));
                    row.add(new StringField("revision",document.revision(),Field.Store.YES));writer.addDocument(row);
                }
                for(var answer:bundle.answers())writer.addDocument(row(scope,bundle.id(),answer.id(),answer.permissions(),answer.questionMasked(),"VERIFIED_ANSWER"));
            }
            return new MemorySet(scope.boundSystem().name(),bundle.id(),directory,DirectoryReader.open(directory),analyzer);
        } catch(IOException|RuntimeException failure) {
            try { directory.close(); } catch(IOException cleanup) { throw new IllegalStateException("문서 색인 정리에 실패했습니다"); }
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
    /** 조회 구현은 phase 6이 동일 monitor 안에서 현재 묶음·권한 필터를 선행 적용하도록 연결합니다. */
    public synchronized Optional<UUID> activeBundle(VerifiedSystemScope scope) { return Optional.ofNullable(active.get(scope.boundSystem().name())).map(value->value.bundleId); }
    @Override public synchronized void close() { for(var value:active.values())value.dispose();active.clear(); }
}
