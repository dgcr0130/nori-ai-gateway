package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.web.ResultResponseAssembler;
import com.nori.ai.gateway.core.job.JobRequestInput;
import com.nori.ai.gateway.core.llm.ClassificationService;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.persistence.repository.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CitedHistoryInputsTest {
    static final Instant NOW = Instant.parse("2026-11-21T00:00:00Z");
    static VerifiedSystemScope scope(String actor, Set<String> permissions) { return new VerifiedSystemScope(OwnerSystem.TC, "service", new ActorRef(actor), actor, permissions, "pdg", NOW, NOW.plusSeconds(60), "request"); }
    static final VerifiedSystemScope SCOPE = scope("owner", Set.of("READ"));
    static final ClassificationService.HistoryPair HISTORY = new ClassificationService.HistoryPair("이전 질문", "이전 답변");
    static final JobRequestInput PREVIOUS = new JobRequestInput("old-message", 1, "이전 질문", Map.of());
    static final ClassificationService.Request REQUEST = new ClassificationService.Request(SCOPE, "conversation", "current", null, new JobRequestInput("current-message", 2, "그 값은 무엇인가요?", Map.of()));
    static ResultResponseAssembler.Access access(boolean allowed, VerifiedSystemScope scope, String conversation) {
        return access(allowed,scope,conversation,1);
    }
    static ResultResponseAssembler.Access access(boolean allowed, VerifiedSystemScope scope, String conversation,int count) {
        var citations=java.util.stream.IntStream.rangeClosed(1,count).mapToObj(i->{
            var location=Map.of("bucket","bucket","objectKey",i==1?"doc.md":"doc-"+i+".md","revision","a".repeat(64),"section","절","lineFrom",1,"lineTo",5);
            return Map.of("number",i,"sourceType","DOCUMENT","location",location);
        }).toList();
        var answer = Map.of("kind", "ANSWER", "text", "이전 답변", "citations", citations);
        var response = Map.<String, Object>of("jobId", "previous", "result", Map.of("payload", SchemaContracts.JSON.valueToTree(Map.of("answer", answer))));
        var job = new JobRepository.Job("previous", 1, conversation, "TC", "owner", "SUCCEEDED", NOW, NOW, NOW, null, null, null, "previous", "key", "digest", "/path");
        var value = new ConversationRepository.Conversation(conversation, "TC", "owner", NOW, NOW, "ACTIVE");
        return new ResultResponseAssembler.Access(scope, job, value, response, allowed);
    }

    @Test void accessPreservesOneThroughTenDocumentParentsAndRejectsEleven() {
        for(int count=1;count<=10;count++)assertThat(CitedHistoryInputs.fromAccess("H1",REQUEST,HISTORY,PREVIOUS,access(true,SCOPE,"conversation",count)).orElseThrow().citations()).hasSize(count);
        assertThat(CitedHistoryInputs.fromAccess("H1",REQUEST,HISTORY,PREVIOUS,access(true,SCOPE,"conversation",11))).isEmpty();
    }

    @Test void actualAccessResponsePayloadCitationShapeIsReadWithoutTrustingTheOldAnswerAsFact() {
        var pair = CitedHistoryInputs.fromAccess("H1", REQUEST, HISTORY, PREVIOUS, access(true, SCOPE, "conversation")).orElseThrow();
        assertThat(pair.citations()).hasSize(1); assertThat(pair.citations().getFirst().objectKey()).isEqualTo("doc.md");
        assertThat(pair.sourceJobId()).isEqualTo("previous");
    }

    @Test void deniedLostPermissionWrongOwnerWrongConversationAndInventedQuestionGiveNoContext() {
        for (var access : List.of(access(false, SCOPE, "conversation"), access(true, scope("owner", Set.of()), "conversation"),
                access(true, scope("other", Set.of("READ")), "conversation"), access(true, SCOPE, "other")))
            assertThat(CitedHistoryInputs.fromAccess("H1", REQUEST, HISTORY, PREVIOUS, access)).isEmpty();
        assertThat(CitedHistoryInputs.fromAccess("H1", REQUEST, new ClassificationService.HistoryPair("만든 주제", "이전 답변"), PREVIOUS, access(true, SCOPE, "conversation"))).isEmpty();
    }

    @Test void malformedOrMismatchedAccessResponseIsSafelyUnavailable() {
        var original = access(true, SCOPE, "conversation");
        var malformed = new ResultResponseAssembler.Access(SCOPE, original.job(), original.conversation(), Map.of("jobId", "previous", "result", Map.of("payload", Map.of("answer", "원문"))), true);
        assertThat(CitedHistoryInputs.fromAccess("H1", REQUEST, HISTORY, PREVIOUS, malformed)).isEmpty();
        assertThat(CitedHistoryInputs.fromAccess("H9", REQUEST, HISTORY, PREVIOUS, original)).isEmpty();
        assertThat(CitedHistoryInputs.fromAccess("H1", REQUEST, new ClassificationService.HistoryPair("이전 질문", "변조한 답변"), PREVIOUS, original)).isEmpty();
    }
}
