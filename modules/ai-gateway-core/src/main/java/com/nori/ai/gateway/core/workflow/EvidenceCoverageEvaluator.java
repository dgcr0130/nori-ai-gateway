package com.nori.ai.gateway.core.workflow;
import com.nori.ai.gateway.core.registry.TaskType;
/** 충분성 실행은 phase 6 소유이며 이 단계에서는 task 규칙만 제공합니다. */
public final class EvidenceCoverageEvaluator {
    public boolean requiresDocuments(TaskType task){return task==TaskType.KNOWLEDGE_QUERY||task==TaskType.INCIDENT_ANALYSIS;}
}
