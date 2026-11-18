package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.authz.DelegatedCall;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.job.CancellationToken;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.port.SystemQueryPort;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.scope.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class CommLogToolExecutorTest {
 final Instant now=Instant.parse("2026-11-06T00:00:00Z");
 final CapabilityManifest manifest=ConfiguredTool.create(OwnerSystem.TC,"tc.commLogs.search","업무 통신 로그",ToolKind.BUSINESS,SchemaContracts.parse("{\"type\":\"object\",\"additionalProperties\":false}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("COMM_LOG_READ"),10000);
 @Test void oneStandardQueryPreservesPartialRangesAndNeverCombinesBusinessRows(){
  var calls=new ArrayList<SystemQueryPort.Request>();var range=new SystemQueryPort.Range(now.minusSeconds(1).plusNanos(1),now.minusNanos(1));
  SystemQueryPort port=request->{calls.add(request);return new SystemQueryPort.Response(ResultStatus.PARTIAL,now,SchemaContracts.parse("{\"items\":[{\"ref\":\"EQP-001\",\"text\":\"업무 원문\",\"values\":{\"direction\":\"IN\"}}]}"),List.of("business"),List.of("RETENTION_UNVERIFIED"),List.of(),List.of(range),null);};
  var resolver=mock(WorkflowResourceResolver.class);when(resolver.delegatedCall()).thenReturn(new DelegatedCall("actor","d".repeat(22)));when(resolver.timezone(any())).thenReturn(ZoneOffset.UTC);when(resolver.cancellation()).thenReturn(new CancellationToken());
  var scope=new VerifiedSystemScope(OwnerSystem.TC,"test",new ActorRef("actor"),"actor",Set.of("COMM_LOG_READ"),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),"job");
  var step=new PlanStep("call-1",manifest.capabilityId(),List.of(),List.of(new ResourceRef("EQP","EQP-001")),SchemaContracts.parse("{}"));var result=new BusinessToolExecutor(port,Clock.fixed(now,ZoneOffset.UTC)).execute(step,manifest,scope,new ExecutionContext(resolver,t->{}));
  assertThat(calls).hasSize(1);assertThat(calls.getFirst().scope()).isSameAs(scope);assertThat(calls.getFirst().targets()).isEqualTo(step.resources());assertThat(result.status()).isEqualTo(ResultStatus.PARTIAL);assertThat(result.missingFields()).containsExactly("business");assertThat(result.metadata().missingRanges()).isEmpty();assertThat(result.metadata().unverifiedRanges()).containsExactly(new ToolResultMetadata.MissingRange(range.from(),range.to()));assertThat(result.blocks().getFirst().content().path("items")).hasSize(1);assertThat(result.blocks().getFirst().content().path("items").get(0).path("values").path("direction").asString()).isEqualTo("IN");
 }
}
