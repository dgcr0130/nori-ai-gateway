package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.authz.*;
import com.nori.ai.gateway.core.dispatch.ExecutionContext;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.workflow.StepBinding;
import com.nori.ai.gateway.integration.gitlab.*;
import com.nori.ai.gateway.integration.loki.*;
import com.nori.ai.gateway.integration.source.SourceReadAccess;
import com.nori.ai.gateway.integration.tempo.TempoTraceConnector;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MultiRepositoryDeploymentTest {
    static final Instant NOW=Instant.parse("2026-11-25T00:00:00Z");
    static final String GATEWAY="a".repeat(40),COMMON="b".repeat(40);
    final GitLabPipelineLineageReader reader=mock(GitLabPipelineLineageReader.class);
    final GitLabCodeConnector connector=mock(GitLabCodeConnector.class);
    final List<String> reads=new ArrayList<>(),resolutions=new ArrayList<>();
    final List<GitLabSourcePolicy.LineageEntry> entries=List.of(
        new GitLabSourcePolicy.LineageEntry("tc-gateway",27,"package","nori-tc-gateway",28,"SIBLING_CLONE_LOG","approved"),
        new GitLabSourcePolicy.LineageEntry("tc-gateway",27,"package","nori-tc-common",26,"SIBLING_CLONE_LOG","approved"));
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("AI_CODE_READ","AI_TRACE_READ"),"pdg",NOW.minusSeconds(1),NOW.plusSeconds(60),"job");
    static <T> ObjectProvider<T> provider(Class<T> type,T bean) {var factory=new DefaultListableBeanFactory();if(bean!=null)factory.registerSingleton("fixture",bean);return factory.getBeanProvider(type);}
    GitLabPipelineLineageReader.Lineage lineage(String repo,String commit,GitLabPipelineLineageReader.Reason reason) {
        var value=mock(GitLabPipelineLineageReader.Lineage.class);when(value.service()).thenReturn("tc-gateway");when(value.execPipelineId()).thenReturn(764L);when(value.sourceRepo()).thenReturn(repo);
        when(value.commitSha()).thenReturn(commit);when(value.reason()).thenReturn(reason);when(value.status()).thenReturn(reason==GitLabPipelineLineageReader.Reason.VERIFIED?GitLabPipelineLineageReader.Status.VERIFIED:GitLabPipelineLineageReader.Status.UNKNOWN);return value;
    }
    ToolResultEnvelope execute(GitLabPipelineLineageReader.Reason commonReason,boolean single) {
        var active=single?entries.subList(0,1):entries;
        when(reader.siblingCommits(any(),any(),eq(764L))).thenAnswer(invocation->{
            GitLabSourcePolicy.LineageEntry entry=invocation.getArgument(1);resolutions.add(entry.sourceRepo());
            return entry.sourceRepo().equals("nori-tc-gateway")?lineage(entry.sourceRepo(),GATEWAY,GitLabPipelineLineageReader.Reason.VERIFIED):lineage(entry.sourceRepo(),commonReason==GitLabPipelineLineageReader.Reason.VERIFIED?COMMON:null,commonReason);
        });
        when(connector.searchCode(any(),eq("EquipmentContextRegistry"),any(),anyString(),any())).thenAnswer(invocation->{
            GitLabCodeConnector.CodeScope range=invocation.getArgument(2);String ref=invocation.getArgument(3);reads.add(range.sourceRepo()+"@"+ref);
            Optional<GitLabPipelineLineageReader.Lineage> proof=invocation.getArgument(4);
            assertThat(proof.isPresent()).isEqualTo(!ref.equals("main"));if(proof.isPresent())assertThat(proof.get().sourceRepo()).isEqualTo(range.sourceRepo());
            return new GitLabCodeConnector.CodeResult(ResultStatus.SUCCESS,List.of(new GitLabCodeConnector.Snippet(range.sourceRepo(),range.pathPrefix()+"/Use.java",ref,"class EquipmentContextRegistry {}")));
        });
        var ranges=single?List.of(new GatewaySystemsProperties.Repository("28","main","gateway",List.of("AI_CODE_READ"))):List.of(new GatewaySystemsProperties.Repository("28","main","gateway",List.of("AI_CODE_READ")),new GatewaySystemsProperties.Repository("26","main","common",List.of("AI_CODE_READ")));
        var config=new GatewaySystemsProperties.SystemConfig("fixture","digest","jwks",null,null,List.of(),List.of(),List.of(),ranges,List.of(new GatewaySystemsProperties.Trace("tc-gateway","tc",List.of("AI_TRACE_READ"))),"REVIEW",Map.of());
        var repositories=active.stream().map(e->new GitLabSourcePolicy.Repository(e.siblingProjectId(),e.sourceRepo(),Set.of("main"),Set.of(e.siblingProjectId()==28?"gateway":"common"),"approved")).toList();
        var executor=new DirectSourceToolExecutor(ToolKind.GIT,provider(LokiLogConnector.class,null),provider(TempoTraceConnector.class,null),provider(GitLabCodeConnector.class,connector),
            provider(DirectSourceToolExecutor.GitExecutionScopes.class,new DirectSourceToolExecutor.GitExecutionScopes(repositories,active)),Clock.fixed(NOW,ZoneOffset.UTC),new LokiProperties(),new DeploymentEvidenceResolver(provider(GitLabPipelineLineageReader.class,reader)),Optional.empty(),new GatewaySystemsProperties(Map.of("TC",config)));
        var manifest=ConfiguredTool.create(OwnerSystem.TC,"git.searchCode","코드 조회",ToolKind.GIT,SchemaContracts.parse("{\"type\":\"object\"}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("AI_CODE_READ"),10000);
        var resolver=mock(WorkflowResourceResolver.class);when(resolver.delegatedCall()).thenReturn(new DelegatedCall("actor","d".repeat(22)));
        var trace=SchemaContracts.JSON.valueToTree(Map.of("items",List.of(Map.of("traceId","trace","spans",List.of(Map.of("service","tc-gateway","serviceVersion","764","spanId","span"))))));
        var binding=new StepBinding(OwnerSystem.TC,new OwnedReference(OwnerSystem.TC,"data://TC/traces"),trace);
        var bound=new ExecutionContext.BoundInputs(mock(StepAttempt.class),Map.of("trace",binding));
        return executor.execute(new PlanStep("code","git.searchCode",List.of(),List.of(),SchemaContracts.JSON.createObjectNode().put("q","EquipmentContextRegistry")),manifest,scope,new ExecutionContext(resolver,t->{},Optional.of(bound)));
    }
    List<String> deployments(ToolResultEnvelope result){return result.evidence().stream().filter(e->e.deploymentRef()!=null).map(e->e.deploymentRef().ref()).toList();}
    @Test void everyServiceEntryIsReadInConfigurationOrderAndEachRepositoryUsesItsOwnCommit() {
        var result=execute(GitLabPipelineLineageReader.Reason.VERIFIED,false);
        assertThat(resolutions).containsExactly("nori-tc-gateway","nori-tc-common");assertThat(result.status()).isEqualTo(ResultStatus.SUCCESS);
        assertThat(reads).containsExactly("nori-tc-gateway@"+GATEWAY,"nori-tc-common@"+COMMON);
        assertThat(deployments(result)).containsExactly("deployment://TC/pipelines/764/repos/nori-tc-gateway/commits/"+GATEWAY,"deployment://TC/pipelines/764/repos/nori-tc-common/commits/"+COMMON);
        assertThat(result.blocks().getFirst().content().path("items")).allSatisfy(row->{assertThat(row.path("codeRefKind").asString()).isEqualTo("DEPLOYED_SOURCE_COMMIT");assertThat(row.path("missingEvidence")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("plugin JAR digest","effectiveConfig")));});
    }
    @Test void unknownCommonLineageFallsBackOnlyThatRepositoryToApprovedMain() {
        var result=execute(GitLabPipelineLineageReader.Reason.SIBLING_COMMIT_AMBIGUOUS,false);
        assertThat(resolutions).containsExactly("nori-tc-gateway","nori-tc-common");assertThat(reads).containsExactly("nori-tc-gateway@"+GATEWAY,"nori-tc-common@main");
        assertThat(deployments(result)).containsExactly("deployment://TC/pipelines/764/repos/nori-tc-gateway/commits/"+GATEWAY);
        var rows=result.blocks().getFirst().content().path("items");assertThat(rows.get(0).path("codeRefKind").asString()).isEqualTo("DEPLOYED_SOURCE_COMMIT");
        assertThat(rows.get(1).path("codeRefKind").asString()).isEqualTo("CURRENT_APPROVED_REF");assertThat(rows.get(1).path("deployedSourceCommit").asString()).isEqualTo("UNKNOWN");
        assertThat(rows.get(1).path("missingEvidence")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("deployed source commit","plugin JAR digest","effectiveConfig")));
    }
    @Test void deniedLineageDoesNotBecomeUnknownOrStartApprovedRefFallback() {var result=execute(GitLabPipelineLineageReader.Reason.DENIED,false);assertThat(result.status()).isEqualTo(ResultStatus.DENIED);assertThat(reads).isEmpty();assertThat(deployments(result)).isEmpty();}
    @Test void unavailableLineageDoesNotBecomeUnknownOrStartApprovedRefFallback() {var result=execute(GitLabPipelineLineageReader.Reason.SOURCE_UNAVAILABLE,false);assertThat(result.status()).isEqualTo(ResultStatus.SOURCE_UNAVAILABLE);assertThat(reads).isEmpty();assertThat(deployments(result)).isEmpty();}
    @Test void singleEntryBehaviorKeepsItsDeployedCommitAndEvidence() {var result=execute(GitLabPipelineLineageReader.Reason.VERIFIED,true);assertThat(result.status()).isEqualTo(ResultStatus.SUCCESS);assertThat(reads).containsExactly("nori-tc-gateway@"+GATEWAY);assertThat(deployments(result)).hasSize(1);}
}
