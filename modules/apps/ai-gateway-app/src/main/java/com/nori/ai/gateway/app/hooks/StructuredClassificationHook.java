package com.nori.ai.gateway.app.hooks;

import java.util.Map;

public final class StructuredClassificationHook {
    private final TestHookRegistry registry;
    public StructuredClassificationHook(TestHookRegistry registry){this.registry=registry;}
    public Map<String,Object> classify(String jobId,com.nori.ai.gateway.core.job.JobRequestInput input){
        var scenario=registry.binding(jobId).orElseThrow(()->new IllegalStateException("구조화 분류 무장이 없습니다"));
        return Map.of("taskType",scenario.taskType().name(),"toolCalls",scenario.toolCalls(),"targets",input.selectedResourceRefs().stream().map(value->Map.of("type",value.type(),"ref",value.ref())).toList(),"searchQuery","구조 시험","retryQuery","구조 시험 보완");
    }
}
