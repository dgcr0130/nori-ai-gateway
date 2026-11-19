package com.nori.ai.gateway.core.knowledge;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.*;

/** 각 목록은 필터 뒤 top-k이며 빈 경로의 순위를 만들어 넣지 않습니다. */
public interface DocumentSearch {
    record Hit(DocumentCorpus.Parent parent,DocumentCorpus.Child child,Set<String> permissions) {
        public Hit { permissions=Set.copyOf(permissions); }
    }
    record Verified(UUID id,List<Hit> parents,List<DocumentLocation> locations) {
        public Verified(UUID id,List<Hit> parents){this(id,parents,parents.stream().map(h->h.child().location()).toList());}
        public Verified { parents=List.copyOf(parents);locations=List.copyOf(locations); }
    }
    record Round(List<Hit> semantic,List<Hit> keyword,List<Hit> identifier,List<Verified> verified) {
        public Round { semantic=List.copyOf(semantic);keyword=List.copyOf(keyword);identifier=List.copyOf(identifier);verified=List.copyOf(verified); }
    }
    Round search(VerifiedSystemScope scope,String query,List<String> targets,SearchSettings settings);
    default Round search(VerifiedSystemScope scope,String query,String question,List<String> targets,SearchSettings settings) {
        return search(scope,query,targets,settings);
    }
}
