package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.RegistryView;
import com.nori.ai.gateway.core.registry.TaskType;
import java.util.*;

/** 의도 설명과 실행 정책은 소유 시스템의 설정이며 모델이 변경할 수 없습니다. */
public record ClassificationIntent(String id, String description, List<String> toolIds, boolean subjectRequired,
        PeriodRequirement periodRequirement, TaskType taskType, boolean documentSearch, Boolean answerTable, SubjectKind subjectKind) {
    public enum PeriodRequirement { NONE, OPTIONAL, REQUIRED }
    public enum SubjectKind { EQP, CODE_TERM }

    public ClassificationIntent {
        // 생략 가능한 설정은 nullable 입력을 받은 뒤 기본화하여 다른 필수 기본형의 누락 검사를 유지합니다.
        answerTable=Boolean.TRUE.equals(answerTable);
        subjectKind=subjectKind==null?SubjectKind.EQP:subjectKind;
        if (id == null || !id.matches("[A-Za-z0-9_.-]{1,128}") || description == null || description.isBlank()
                || description.codePointCount(0, description.length()) > 1024) throw invalid();
        toolIds = toolIds == null ? List.of() : List.copyOf(toolIds);
        if (new HashSet<>(toolIds).size() != toolIds.size()) throw invalid();
        for (String tool : toolIds) if (!tool.matches("[A-Za-z0-9_.-]{1,128}")) throw invalid();
        if(subjectKind==SubjectKind.CODE_TERM&&(!subjectRequired||toolIds.isEmpty()))throw invalid();
        Objects.requireNonNull(periodRequirement); Objects.requireNonNull(taskType);
        if ((taskType == TaskType.GENERAL_QUERY || taskType == TaskType.UNSUPPORTED_REQUEST)
                && (!toolIds.isEmpty() || documentSearch || subjectRequired || periodRequirement != PeriodRequirement.NONE)) throw invalid();
    }

    public String subjectField() {return subjectKind==SubjectKind.CODE_TERM?"q":"eqp";}

    public boolean visible(RegistryView view) {
        return toolIds.isEmpty() || toolIds.stream().anyMatch(id -> view.find(id).isPresent());
    }

    public static List<ClassificationIntent> visible(List<ClassificationIntent> declarations, RegistryView view) {
        return declarations.stream().filter(intent -> intent.visible(view)).toList();
    }

    public static List<ClassificationIntent> checked(List<ClassificationIntent> declarations) {
        var result = declarations == null ? List.<ClassificationIntent>of() : List.copyOf(declarations);
        if (result.stream().map(ClassificationIntent::id).distinct().count() != result.size()) throw invalid();
        return result;
    }

    /** 복합 요청은 실제 조회를 대표하는 유형을 기록하고 문서 검색 여부는 각 선언으로 별도 결정합니다. */
    public static TaskType taskType(List<ClassificationIntent> selected) {
        for (var type : List.of(TaskType.INCIDENT_ANALYSIS, TaskType.LIVE_QUERY, TaskType.KNOWLEDGE_QUERY, TaskType.GENERAL_QUERY, TaskType.UNSUPPORTED_REQUEST))
            if (selected.stream().anyMatch(intent -> intent.taskType() == type)) return type;
        throw invalid();
    }

    private static IllegalArgumentException invalid() { return new IllegalArgumentException("의도 선언이 잘못되었습니다"); }
}
