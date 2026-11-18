package com.nori.ai.gateway.core;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.workflow.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static com.nori.ai.gateway.core.DomainFixtures.*;
import static org.assertj.core.api.Assertions.*;
class WorkflowPlanBuilderTest {
 final CapabilityRegistry registry=registry(manifest(),new ContractVersionPolicy());final WorkflowPlanBuilder builder=new WorkflowPlanBuilder(registry);
 JobContext job(int calls,Map<String,Object> fields){var list=new ArrayList<Map<String,Object>>();for(int i=0;i<calls;i++)list.add(Map.of("toolId",ID,"arguments",Map.of("eqp","EQP_001","q","질문")));return new JobContext("request","conversation",scope(),"d".repeat(22),new CancellationToken(),InferenceMode.DISABLED,Map.of("taskType","INCIDENT_ANALYSIS","toolCalls",list,"targets",List.of(Map.of("type","EQP","ref","EQP_001")),"searchQuery","질문","retryQuery","다른 질문"),new JobRequestInput("message",1,"질문",fields));}
 @Test void executionListsHaveZeroThroughFourStepsAndKeepOrderArgumentsAndDependencies(){for(int count=0;count<=4;count++){var job=job(count,Map.of());var plan=builder.build(builder.definition(job),registry.view(job.scope()),job);assertThat(plan.steps()).hasSize(count);assertThat(plan.documents()).isEqualTo(new WorkflowExecutionPlan.DocumentSearch(true,"질문","다른 질문"));for(int i=0;i<count;i++){var step=plan.toolPlan().plan().get(i);assertThat(step.stepId()).isEqualTo("call-"+(i+1));assertThat(step.dependsOn()).containsExactlyElementsOf(i==0?List.of():List.of("call-"+i));assertThat(step.arguments().path("eqp").asString()).isEqualTo("EQP_001");}}assertThatThrownBy(()->builder.definition(job(5,Map.of()))).isInstanceOf(WorkflowPlanBuilder.Invalid.class);}
 @Test void falseAllowToolsBlocksOnlyNonemptyExecutionLists(){var noTools=job(0,Map.of("options",Map.of("allowTools",false)));assertThatCode(()->builder.checkOptions(builder.definition(noTools),noTools)).doesNotThrowAnyException();var tools=job(1,Map.of("options",Map.of("allowTools",false)));assertThatThrownBy(()->builder.checkOptions(builder.definition(tools),tools)).isInstanceOf(WorkflowPlanBuilder.Blocked.class);}
}
