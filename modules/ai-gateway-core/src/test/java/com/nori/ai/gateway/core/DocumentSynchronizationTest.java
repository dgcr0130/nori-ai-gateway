package com.nori.ai.gateway.core;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.budget.*;
import java.util.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static com.nori.ai.gateway.core.knowledge.DocumentCorpus.*;

class DocumentSynchronizationTest {
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.valueOf("Doc"),"fixture",new ActorRef("actor"),"actor",Set.of(),"pdg",Instant.MIN,Instant.MAX,"request");
    static class Store implements DocumentIndexStore {
        Bundle saved,active; final List<String> calls=new ArrayList<>();
        public boolean hasActiveIndex(VerifiedSystemScope scope){return active!=null;}
        public void cleanNonActive(VerifiedSystemScope scope){calls.add("clean");}
        public List<Answer> activeAnswers(VerifiedSystemScope scope){return List.of();}
        public void saveBuilding(VerifiedSystemScope scope,Bundle bundle){calls.add("save");saved=bundle;}
        public Bundle load(VerifiedSystemScope scope,UUID id){calls.add("load");return saved;}
        public void activate(VerifiedSystemScope scope,Bundle bundle){calls.add("activate");active=bundle;}
        public Optional<String> currentRevision(VerifiedSystemScope scope,String bucket,String key){return Optional.empty();}
    }
    @Test void allPrefixesAreListedBeforeAnyDownloadAndUtf8OrderingIsAcrossSources() {
        var store=new Store();var calls=new ArrayList<String>();var prepared=new ArrayList<String>();
        DocumentObjectStore objects=new DocumentObjectStore(){
            public List<ObjectKey> list(Source source){calls.add("list:"+source.bucket());return source.bucket().equals("B")?List.of(new ObjectKey("한.md",0),new ObjectKey("a.md",0)):List.of(new ObjectKey("z.md",0));}
            public Content download(Source source,ObjectKey key,int max){calls.add(source.bucket()+":"+key.key());return new Content("a".repeat(64),new byte[0],false);}
        };
        DocumentExtractor extractor=(source,key,content,limits)->new Document(UUID.randomUUID(),source.bucket(),key,content.revision(),"제목",source.permissions(),List.of(),List.of());
        var index=new DocumentKeywordIndex(){
            public Prepared prepare(VerifiedSystemScope scope,Bundle bundle){assertThat(store.calls).containsExactly("clean","save","load");prepared.add("prepare");return ()->prepared.add("close");}
            public void install(VerifiedSystemScope scope,Prepared value){assertThat(store.active).isNotNull();prepared.add("install");}
        };
        var inference=new InferenceClient(){
            public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){throw new AssertionError("생성 호출 금지");}
            public List<float[]> embed(List<String> values){throw new AssertionError("정상 0자식·0정답에서 임베딩 호출 금지");}
        };
        new DocumentSynchronization(objects,extractor,store,index,inference).synchronize(scope,List.of(new Source("B","",Set.of("READ")),new Source("A","",Set.of("READ"))),new Limits(1048576,200,450,60),"model","b".repeat(64),3);
        assertThat(calls).containsExactly("list:B","list:A","B:a.md","A:z.md","B:한.md");assertThat(prepared).containsExactly("prepare","install","close");assertThat(store.active.answers()).isEmpty();
    }
    @Test void keywordPreparationFailureNeverActivatesTheBuildingSet() {
        var store=new Store();var objects=new DocumentObjectStore(){public List<ObjectKey> list(Source source){return List.of();}public Content download(Source source,ObjectKey key,int max){throw new AssertionError();}};
        var keywords=new DocumentKeywordIndex(){public Prepared prepare(VerifiedSystemScope scope,Bundle bundle){throw new IllegalStateException("색인 실패");}public void install(VerifiedSystemScope scope,Prepared prepared){throw new AssertionError();}};
        var inference=new InferenceClient(){public ChatOutcome chat(CallType type,List<ChatMessage> values,int max){throw new AssertionError();}public List<float[]> embed(List<String> values){throw new AssertionError();}};
        assertThatThrownBy(()->new DocumentSynchronization(objects,(a,b,c,d)->{throw new AssertionError();},store,keywords,inference).synchronize(scope,List.of(),new Limits(1048576,200,450,60),"model","a".repeat(64),3)).hasMessage("색인 실패");
        assertThat(store.saved).isNotNull();assertThat(store.active).isNull();assertThat(store.calls).containsExactly("clean","save","load");
    }
}
