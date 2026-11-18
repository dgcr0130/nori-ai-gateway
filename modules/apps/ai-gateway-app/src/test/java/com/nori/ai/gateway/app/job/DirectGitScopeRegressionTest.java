package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.SystemConfigFixtures;
import com.nori.ai.gateway.app.config.*;
import com.nori.ai.gateway.core.authz.*;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.integration.gitlab.*;
import com.nori.ai.gateway.integration.loki.LokiProperties;
import com.nori.ai.gateway.integration.source.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DirectGitScopeRegressionTest {
    @TempDir Path directory;
    @Test void projectIdBindsApprovedAliasAndCommitWithoutLosingConfigBoundaries() throws Exception {
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);var received=new ArrayList<String>();
        server.createContext("/",exchange->{received.add(exchange.getRequestURI().toString());String path=exchange.getRequestURI().getPath();String body=path.contains("/pipelines/")?"[{\"id\":1,\"name\":\"package\",\"status\":\"success\"}]":path.endsWith("/trace")?"sibling nori-tc-gateway @ aaaaaaa\n":path.contains("/commits/")?"{\"id\":\""+"a".repeat(40)+"\"}":"[{\"path\":\"src/main/A.java\",\"data\":\"자료\"}]";byte[] bytes=body.getBytes(StandardCharsets.UTF_8);try{exchange.sendResponseHeaders(200,bytes.length);exchange.getResponseBody().write(bytes);}finally{exchange.close();}});server.start();
        try {
            var clock=Clock.systemUTC();var now=clock.instant();var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("AI_CODE_READ"),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),"job");
            var base=SystemConfigFixtures.systems().systems().get("TC");
            var system=new GatewaySystemsProperties.SystemConfig(base.issuer(),base.serviceTokenSha256File(),base.actorJwksFile(),base.backendUrl(),base.gatewayTokenFile(),base.tools(),base.documents(),base.apps(),List.of(new GatewaySystemsProperties.Repository("22","main","src/main",List.of("AI_CODE_READ"))),base.traces(),base.reviewPermission(),base.glossary());
            var systems=new GatewaySystemsProperties(Map.of("TC",system));var manifest=SystemTools.manifests(systems).stream().filter(value->value.toolKind()==ToolKind.GIT).findFirst().orElseThrow();
            var repo=new GitLabSourcePolicy.Repository(22,"nori-tc-gateway",Set.of("main"),Set.of("src/main"),"fixture");var entry=new GitLabSourcePolicy.LineageEntry("tc-gateway",11,"package","nori-tc-gateway",22,"SIBLING_CLONE_LOG","fixture");var policy=new GitLabSourcePolicy(List.of(repo),List.of(entry));
            var token=directory.resolve("read-token");Files.writeString(token,"fixture");
            var authorization=mock(SourceAuthorizationStep.class);when(authorization.authorizeBefore(anyString(),any(),any())).thenReturn(new SourceAuthorizationStep.Granted(scope.perm(),scope.pdg()));
            var connector=new GitLabCodeConnector(new SourceHttpClient(URI.create("http://127.0.0.1:"+server.getAddress().getPort()),"gitlab",SourceObservability.noop()),new SourceReadAccess(authorization,clock),policy,new GitLabReadTokenFile(token));
            var reader=new GitLabPipelineLineageReader(new SourceHttpClient(URI.create("http://127.0.0.1:"+server.getAddress().getPort()),"gitlab",SourceObservability.noop()),new SourceReadAccess(authorization,clock),policy,new GitLabReadTokenFile(token));
            var beans=new StaticListableBeanFactory(Map.of("git",connector,"lineage",reader,"scopes",new DirectSourceToolExecutor.GitExecutionScopes(List.of(repo),List.of(entry))));
            var deployments=new DeploymentEvidenceResolver(beans.getBeanProvider(GitLabPipelineLineageReader.class));
            var executor=new DirectSourceToolExecutor(ToolKind.GIT,beans.getBeanProvider(com.nori.ai.gateway.integration.loki.LokiLogConnector.class),beans.getBeanProvider(com.nori.ai.gateway.integration.tempo.TempoTraceConnector.class),beans.getBeanProvider(GitLabCodeConnector.class),beans.getBeanProvider(DirectSourceToolExecutor.GitExecutionScopes.class),clock,new LokiProperties(),deployments,Optional.empty(),systems);
            var resolver=mock(WorkflowResourceResolver.class);when(resolver.delegatedCall()).thenReturn(new DelegatedCall(scope.sub(),"d".repeat(22)));
            var step=new PlanStep("call-2",manifest.capabilityId(),List.of(),List.of(),SchemaContracts.parse("{\"q\":\"literal\"}"));
            var plain=executor.execute(step,manifest,scope,new ExecutionContext(resolver,ignored->{},Optional.of(new ExecutionContext.BoundInputs(mock(StepAttempt.class),Map.of()))));assertThat(plain.status()).isEqualTo(ResultStatus.SUCCESS);assertThat(received).singleElement().asString().contains("/projects/22/search","ref=main");
            received.clear();String sha="a".repeat(40);
            var binding=new com.nori.ai.gateway.core.workflow.StepBinding(OwnerSystem.TC,new OwnedReference(OwnerSystem.TC,"data://TC/trace"),SchemaContracts.parse("{\"items\":[{\"traceId\":\"trace\",\"spans\":[{\"service\":\"tc-gateway\",\"serviceVersion\":\"123\",\"spanId\":\"span\"}]}]}"));
            var context=new ExecutionContext(resolver,ignored->{},Optional.of(new ExecutionContext.BoundInputs(mock(StepAttempt.class),Map.of("call-1",binding))));
            var deployed=executor.execute(step,manifest,scope,context);assertThat(deployed.status()).isEqualTo(ResultStatus.SUCCESS);assertThat(received).hasSize(4);assertThat(received.getLast()).contains("ref="+sha);assertThat(deployed.evidence()).anySatisfy(value->assertThat(value.deploymentRef()).isNotNull());
            received.clear();var missing=new StaticListableBeanFactory(Map.of("git",connector,"scopes",new DirectSourceToolExecutor.GitExecutionScopes(List.of(new GitLabSourcePolicy.Repository(99,"other",Set.of("main"),Set.of("src/main"),"fixture")),List.of())));
            var blocked=new DirectSourceToolExecutor(ToolKind.GIT,missing.getBeanProvider(com.nori.ai.gateway.integration.loki.LokiLogConnector.class),missing.getBeanProvider(com.nori.ai.gateway.integration.tempo.TempoTraceConnector.class),missing.getBeanProvider(GitLabCodeConnector.class),missing.getBeanProvider(DirectSourceToolExecutor.GitExecutionScopes.class),clock,new LokiProperties(),deployments,Optional.empty(),systems);
            assertThat(blocked.execute(step,manifest,scope,new ExecutionContext(resolver,ignored->{},Optional.of(new ExecutionContext.BoundInputs(mock(StepAttempt.class),Map.of())))).status()).isEqualTo(ResultStatus.DENIED);assertThat(received).isEmpty();
        } finally {server.stop(0);}
    }
}
