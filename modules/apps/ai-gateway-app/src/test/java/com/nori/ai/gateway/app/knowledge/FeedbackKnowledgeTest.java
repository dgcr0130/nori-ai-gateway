package com.nori.ai.gateway.app.knowledge;

import com.nori.ai.gateway.app.SystemConfigFixtures;
import com.nori.ai.gateway.app.job.*;
import com.nori.ai.gateway.app.job.recovery.*;
import com.nori.ai.gateway.app.security.*;
import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.llm.ClassificationService;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.persistence.repository.*;
import org.springframework.beans.factory.ObjectProvider;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class FeedbackKnowledgeTest {
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"fixture",new ActorRef("reviewer"),"reviewer",Set.of(),"a".repeat(64),Instant.MIN,Instant.MAX,"request");
    final FeedbackRepository feedback=mock(FeedbackRepository.class);
    final DocumentIndexRepository indexes=mock(DocumentIndexRepository.class);
    final DocumentKeywordIndex keywords=mock(DocumentKeywordIndex.class);
    final ScopeAssembler scopes=mock(ScopeAssembler.class);
    final SecurityCredentialFiles credentials=mock(SecurityCredentialFiles.class);
    final ReadinessGate gate=new ReadinessGate(List.of(),event->{});
    FeedbackKnowledge projection() {
        gate.run(null);var provider=mock(ObjectProvider.class);when(provider.getObject()).thenReturn(gate);when(scopes.forDocumentStartup(OwnerSystem.TC,credentials)).thenReturn(scope);
        return new FeedbackKnowledge(feedback,indexes,keywords,SystemConfigFixtures.systems(),scopes,credentials,provider);
    }
    @Test void committedGlossaryOverridesConfigInBothClassifierAndCoverageWithoutChangingTheConfig() {
        when(feedback.glossary(scope)).thenReturn(Map.of("T3","DB 풀이","S1F2","DB 추가"));when(indexes.searchState(scope)).thenReturn(Optional.empty());
        var systems=SystemConfigFixtures.systems();var registry=new CapabilityRegistry(List.of(),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        try(var projection=projection()) {
            projection.run();assertThat(projection.current(scope,Map.of("T3","초기 풀이","S9F9","초기 유지"))).containsEntry("T3","DB 풀이").containsEntry("S9F9","초기 유지").containsEntry("S1F2","DB 추가");
            var request=new ClassificationService.Request(scope,"conversation","job",null,null);
            assertThat(new ClassificationInputs(systems,registry,null,null,0,null,projection).load(request).glossary()).containsEntry("T3","DB 풀이");
            var job=mock(com.nori.ai.gateway.core.job.JobContext.class);when(job.scope()).thenReturn(scope);
            assertThat(new GroundingInputs(systems,registry,new ContentSanitizer(),Map.of(),projection).glossary(job)).containsEntry("T3","DB 풀이");
            assertThat(systems.systems().get("TC").glossary()).doesNotContainEntry("T3","DB 풀이");
        }
    }
    @Test void memoryFailureClosesAdmissionUntilDbVersionRebuildSucceedsAndDoesNotOpenFailedStartup() {
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"model",2,"c".repeat(64),List.of(),List.of());
        when(indexes.searchState(scope)).thenReturn(Optional.of(bundle));when(indexes.load(scope,bundle.id())).thenReturn(bundle);when(feedback.glossary(scope)).thenReturn(Map.of("T3","DB 풀이"));
        when(keywords.prepare(eq(scope),any())).thenThrow(new IllegalStateException("고정 실패"));
        try(var projection=projection()) {
            assertThatCode(()->projection.refresh(scope)).doesNotThrowAnyException();assertThat(gate.ready()).isTrue();assertThat(gate.startupReady()).isTrue();assertThat(gate.admissionReady()).isFalse();
            when(keywords.prepare(eq(scope),any())).thenReturn(()->{});projection.refresh(scope);assertThat(gate.admissionReady()).isTrue();assertThat(projection.current(scope,Map.of())).containsEntry("T3","DB 풀이");
            gate.down();projection.refresh(scope);assertThat(gate.ready()).isFalse();assertThat(gate.startupReady()).isFalse();assertThat(gate.admissionReady()).isFalse();
        }
    }
    @Test void changedDbVersionAfterPreparingIsNotAFailureAndTheLatestCommitIsEventuallyProjected() {
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"model",2,"c".repeat(64),List.of(),List.of());when(indexes.searchState(scope)).thenReturn(Optional.of(bundle));when(indexes.load(scope,bundle.id())).thenReturn(bundle);when(feedback.glossary(scope)).thenReturn(Map.of());
        var version=new java.util.concurrent.atomic.AtomicLong(1);var drift=new java.util.concurrent.atomic.AtomicBoolean(true);when(feedback.projectionVersion(scope)).thenAnswer(call->version.get());when(keywords.prepare(eq(scope),any())).thenAnswer(call->{if(drift.getAndSet(false)){version.incrementAndGet();when(feedback.glossary(scope)).thenReturn(Map.of("T3","첫 확정","S1F2","다음 확정"));}return (DocumentKeywordIndex.Prepared)()->{};});
        try(var projection=projection()){
            assertThatCode(()->projection.refresh(scope)).doesNotThrowAnyException();assertThat(gate.ready()).isTrue();assertThat(gate.admissionReady()).isTrue();
            projection.refresh(scope);assertThat(projection.current(scope,Map.of())).containsEntry("T3","첫 확정").containsEntry("S1F2","다음 확정");verify(keywords,atLeastOnce()).install(eq(scope),any());
        }
    }
    @Test void successfulPreparationNeverClosesReadinessWhileItIsHeld() throws Exception {
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"model",2,"c".repeat(64),List.of(),List.of());when(indexes.searchState(scope)).thenReturn(Optional.of(bundle));when(indexes.load(scope,bundle.id())).thenReturn(bundle);when(feedback.glossary(scope)).thenReturn(Map.of());
        var entered=new CountDownLatch(1);var release=new CountDownLatch(1);
        when(keywords.prepare(eq(scope),any())).thenAnswer(call->{entered.countDown();release.await();return (DocumentKeywordIndex.Prepared)()->{};});
        try(var projection=projection();var executor=Executors.newVirtualThreadPerTaskExecutor()) {
            var refresh=executor.submit(()->projection.refresh(scope));
            try{assertThat(entered.await(10,TimeUnit.SECONDS)).isTrue();assertThat(gate.ready()).isTrue();assertThat(gate.admissionReady()).isTrue();}finally{release.countDown();}
            refresh.get();assertThat(gate.ready()).isTrue();
        }finally{release.countDown();}
    }
    @Test void installFailureClosesOnlyAdmissionAndRetainsTheCommittedResponse() {
        var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"model",2,"c".repeat(64),List.of(),List.of());when(indexes.searchState(scope)).thenReturn(Optional.of(bundle));when(indexes.load(scope,bundle.id())).thenReturn(bundle);when(feedback.glossary(scope)).thenReturn(Map.of("T3","DB 풀이"));when(keywords.prepare(eq(scope),any())).thenReturn(()->{});
        doThrow(new IllegalStateException("고정 설치 실패")).when(keywords).install(eq(scope),any());
        try(var projection=projection()) {
            assertThatCode(()->projection.refresh(scope)).doesNotThrowAnyException();assertThat(gate.ready()).isTrue();assertThat(gate.admissionReady()).isFalse();
            doNothing().when(keywords).install(eq(scope),any());projection.refresh(scope);assertThat(gate.ready()).isTrue();assertThat(gate.admissionReady()).isTrue();
        }
    }
}
