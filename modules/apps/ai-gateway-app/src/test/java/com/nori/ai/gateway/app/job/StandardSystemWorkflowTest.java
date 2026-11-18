package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.authz.DelegatedCall;
import com.nori.ai.gateway.core.blocks.*;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.job.CancellationToken;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import com.nori.ai.gateway.integration.system.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class StandardSystemWorkflowTest {
 @TempDir Path directory;
 @Test void realHttpBusinessResultsPassTheCoreContractAndUiProjectionWithoutTcDependencies()throws Exception {
  var now=Instant.parse("2026-11-06T00:00:00Z");var clock=Clock.fixed(now,ZoneOffset.UTC);var owner=OwnerSystem.valueOf("HRM");var scope=new VerifiedSystemScope(owner,"service",new ActorRef("actor"),"actor",Set.of("ASSET_READ"),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),"job-1");
  try(var backend=new StandardSystemStub(clock)){var token=directory.resolve("gateway-token");Files.writeString(token,backend.token);String delegation="d".repeat(22);backend.register(scope.sub(),delegation);
   var settings=new SystemQuerySettings(Map.of(owner,new SystemQuerySettings.Endpoint(backend.base(),token)));var adapter=new HttpSystemQueryAdapter(settings,clock,new ContentSanitizer());var executor=new BusinessToolExecutor(adapter,clock);var manifest=ConfiguredTool.create(owner,"asset.read","자산 조회",ToolKind.BUSINESS,SchemaContracts.parse("{\"type\":\"object\",\"additionalProperties\":false}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("ASSET_READ"),10000);var versions=new ContractVersionPolicy();new CapabilityRegistry(List.of(manifest),Set.of(ImplementationPhase.PHASE_1),versions);
   var resolver=mock(WorkflowResourceResolver.class);when(resolver.delegatedCall()).thenReturn(new DelegatedCall(scope.sub(),delegation));when(resolver.timezone(scope)).thenReturn(ZoneOffset.UTC);when(resolver.cancellation()).thenReturn(new CancellationToken());var step=new PlanStep("call-1",manifest.capabilityId(),List.of(),List.of(),SchemaContracts.parse("{}"));
   for(String scenario:List.of("default","empty","denied","unavailable","timeout")){backend.select(scenario);var result=executor.execute(step,manifest,scope,new ExecutionContext(resolver,ignored->{}));assertThat(new ToolResultContractValidator(versions).validate(result,manifest)).isSameAs(result);if(scenario.equals("default")){assertThat(result.status()).isEqualTo(ResultStatus.SUCCESS);var blocks=new BlockBuilder(new UiSchemaValidator()).build(manifest,result);assertThat(blocks).singleElement().satisfies(block->assertThat(block.content().path("rows")).hasSize(1));}else if(scenario.equals("empty"))assertThat(result.status()).isEqualTo(ResultStatus.EMPTY);else assertThat(result.blocks()).isEmpty();}
   assertThat(backend.requests).hasSize(5);assertThat(backend.requests).allSatisfy(request->{assertThat(request.body().path("systemId").asString()).isEqualTo("HRM");assertThat(request.body().path("jobId").asString()).isEqualTo(scope.requestId());});
  }
 }
}
