package com.nori.ai.gateway.core.llm;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.plan.ResourceRef;
import tools.jackson.databind.JsonNode;
import java.util.*;
/** 한 번 분류한 질문 계획이며 시스템·권한·조회 주소를 모델 출력에서 받지 않습니다. */
public record Classification(TaskType taskType,List<ToolCall> toolCalls,List<ResourceRef> targets,Map<String,String> timeWindow,String searchQuery,String retryQuery,Optional<CitedHistoryContext.Bound> historyReference) {
    public Classification(TaskType task,List<ToolCall> calls,List<ResourceRef> targets,Map<String,String> window,String search,String retry){this(task,calls,targets,window,search,retry,Optional.empty());}
    public record ToolCall(String toolId,JsonNode arguments){public ToolCall{Objects.requireNonNull(toolId);arguments=arguments.deepCopy();}@Override public JsonNode arguments(){return arguments.deepCopy();}}
    public Classification{toolCalls=List.copyOf(toolCalls);targets=List.copyOf(targets);timeWindow=timeWindow==null?Map.of():Map.copyOf(timeWindow);Objects.requireNonNull(historyReference);}
    public Map<String,Object> jobClassification(){var result=new LinkedHashMap<String,Object>();result.put("taskType",taskType.name());result.put("toolCalls",SchemaContracts.JSON.convertValue(toolCalls,List.class));result.put("targets",SchemaContracts.JSON.convertValue(targets,List.class));result.put("timeWindow",timeWindow);result.put("searchQuery",searchQuery);result.put("retryQuery",retryQuery);historyReference.ifPresent(value->result.put("historyReference",value));return Collections.unmodifiableMap(result);}
    @Override public String toString(){return "Classification[taskType="+taskType+"]";}
}
