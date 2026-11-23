package com.nori.ai.gateway.app.job;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.app.web.ResultResponseAssembler;
import com.nori.ai.gateway.core.knowledge.DocumentLocation;
import com.nori.ai.gateway.core.llm.ClassificationService;
import com.nori.ai.gateway.core.llm.CitedHistoryContext;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.util.*;

/** 기존 결과 접근 검사를 통과한 Access.response의 실제 답변 인용만 내부 문맥으로 연결합니다. */
public final class CitedHistoryInputs {
    private CitedHistoryInputs() {}

    public static Optional<CitedHistoryContext.Pair> fromAccess(String ref, ClassificationService.Request request,
            ClassificationService.HistoryPair history, com.nori.ai.gateway.core.job.JobRequestInput previousInput, ResultResponseAssembler.Access access) {
        try {
            if (!access.allowed() || access.job().terminalAt() == null || access.job().seq() >= request.input().jobSeq()
                || !access.job().conversationId().equals(request.conversationId()) || access.job().id().equals(request.jobId())
                || !access.scope().boundSystem().equals(request.scope().boundSystem()) || !access.scope().actorRef().equals(request.scope().actorRef())
                || !access.scope().perm().equals(request.scope().perm()) || !access.conversation().id().equals(request.conversationId())
                || !access.job().ownerSystem().equals(request.scope().boundSystem().name()) || !access.job().actorRef().equals(request.scope().actorRef().value())
                || previousInput.jobSeq() != access.job().seq() || !previousInput.maskedQuestion().equals(history.question())
                || previousInput.messageId().equals(request.input().messageId())
                || !access.conversation().ownerActorRef().equals(request.scope().actorRef().value())
                || !access.conversation().ownerSystem().equals(request.scope().boundSystem().name()) || !access.conversation().status().equals("ACTIVE")) return Optional.empty();
            var response = SchemaContracts.JSON.valueToTree(access.response());
            if (!response.path("jobId").asString().equals(access.job().id())) return Optional.empty();
            var answer = response.path("result").path("payload").path("answer");
            if (!Set.of("ANSWER", "PARTIAL").contains(answer.path("kind").asString()) || !answer.path("citations").isArray()
                || !answer.path("text").asString().equals(history.answer())) return Optional.empty();
            var locations = new LinkedHashSet<DocumentLocation>();
            for (var citation : answer.get("citations")) if (citation.path("sourceType").asString().equals("DOCUMENT")) {
                var location = citation.get("location");
                if (location == null || !Set.copyOf(location.propertyNames()).equals(Set.of("bucket", "objectKey", "revision", "section", "lineFrom", "lineTo"))) return Optional.empty();
                locations.add(SchemaContracts.JSON.treeToValue(location, DocumentLocation.class));
            }
            if (locations.isEmpty() || locations.size() > CitedHistoryContext.MAX_DOCUMENT_CITATIONS) return Optional.empty();
            body(System.getLogger(CitedHistoryInputs.class.getName()),INFO,"CITED_HISTORY_INPUT",SchemaContracts.JSON.writeValueAsString(Map.of("question",history.question(),"answer",history.answer(),"locations",locations)),"jobId",request.jobId(),"reference",ref);
            return Optional.of(new CitedHistoryContext.Pair(ref, access.job().id(), history.question(), history.answer(), List.copyOf(locations)));
        } catch (RuntimeException invalid) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(CitedHistoryInputs.class.getName()),"CITED_HISTORY_INPUTS_FAILED",invalid);
            // 공개 응답으로 실패 원문을 보내거나 500으로 바꾸지 않고 참조 문맥을 제공하지 않습니다.
            return Optional.empty();
        }
    }
}
