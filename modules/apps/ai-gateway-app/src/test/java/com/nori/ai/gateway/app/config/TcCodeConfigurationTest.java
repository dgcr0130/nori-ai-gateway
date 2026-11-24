package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.llm.ClassificationIntent;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.scope.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.env.MockEnvironment;
import static org.assertj.core.api.Assertions.*;

class TcCodeConfigurationTest {
    static GatewaySystemsProperties actual()throws Exception {
        var env=new StandardEnvironment();
        for(var source:new YamlPropertySourceLoader().load("tc-code",new FileSystemResource(Path.of(System.getProperty("aigw.rootDir"),"config/ai-gateway.yaml"))))env.getPropertySources().addLast(source);
        return GatewaySystemsProperties.load(env);
    }
    static SourceConnectorConfiguration.SourceSnapshot snapshot()throws Exception {
        return SourceConnectorConfiguration.SourceSnapshot.parse(Files.readString(Path.of(System.getProperty("aigw.rootDir"),"modules/apps/ai-gateway-app/src/main/resources/config/sources/shared-v1.json")));
    }
    static final Map<String,Set<String>> PREFIXES=Map.of("26",Set.of("common","db","messaging"),"28",Set.of("gateway"),"24",Set.of("business"),"31",Set.of("ui"),"32",Set.of("src"));
    static final Map<String,String> NAMES=Map.of("26","nori-tc-common","28","nori-tc-gateway","24","nori-tc-business","31","nori-tc-ui-backend","32","nori-tc-ui-front");
    @Test void productionNetworkTargetMatchesTheApprovedGitLabService()throws Exception {
        var yaml=new org.yaml.snakeyaml.Yaml().load(Files.readString(Path.of(System.getProperty("aigw.rootDir"),"operations/deploy/helm/ai-gateway/values-prod.yaml")));
        var target=SchemaContracts.JSON.valueToTree(yaml).path("netpol").path("targets").path("gitlab");
        assertThat(target.path("namespace").asString()).isEqualTo("gitlab-system");assertThat(target.path("port").asInt()).isEqualTo(8181);
        assertThat(target.path("podSelector")).isEqualTo(SchemaContracts.JSON.valueToTree(Map.of("app","webservice","release","gitlab","gitlab.com/webservice-name","default")));
    }
    @Test void actualTcCodeIntentAndSevenRepositoryRangesArePermissionScoped()throws Exception {
        var systems=actual();var tc=systems.systems().get("TC");
        assertThat(tc.repositories()).hasSize(7);
        for(var entry:PREFIXES.entrySet())assertThat(tc.repositories().stream().filter(r->r.projectId().equals(entry.getKey())).map(GatewaySystemsProperties.Repository::pathPrefix).toList()).containsExactlyInAnyOrderElementsOf(entry.getValue());
        assertThat(tc.repositories()).allSatisfy(r->{assertThat(r.ref()).isEqualTo("main");assertThat(r.requiredPermissions()).containsExactly("AI_CODE_READ");assertThat(r.pathPrefix()).doesNotEndWith("/");});
        var code=tc.intents().stream().filter(i->i.id().equals("code")).findFirst().orElseThrow();
        assertThat(code.toolIds()).containsExactly("git.searchCode");assertThat(code.subjectRequired()).isTrue();
        assertThat(SchemaContracts.JSON.valueToTree(code).path("subjectKind").asString()).isEqualTo("CODE_TERM");
        assertThat(code.periodRequirement()).isEqualTo(ClassificationIntent.PeriodRequirement.NONE);assertThat(code.taskType()).isEqualTo(TaskType.LIVE_QUERY);assertThat(code.documentSearch()).isFalse();
        var registry=new CapabilityRegistry(SystemTools.manifests(systems),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        var ordinary=tc.tools().stream().flatMap(t->t.requiredPermissions().stream()).collect(java.util.stream.Collectors.toSet());
        for(boolean allowed:List.of(false,true)) {
            var permissions=new HashSet<>(ordinary);if(allowed)permissions.add("AI_CODE_READ");
            var scope=new VerifiedSystemScope(OwnerSystem.TC,"svc",new ActorRef("actor"),"actor",permissions,"pdg",Instant.EPOCH,Instant.EPOCH.plusSeconds(60),"job");
            var visible=ClassificationIntent.visible(tc.intents(),registry.view(scope));
            assertThat(visible.stream().anyMatch(i->i.id().equals("code"))).isEqualTo(allowed);
        }
    }
    @Test void actualSourceSnapshotAndExecutionScopesRetainFiveRepositoriesAndAllSixLineages()throws Exception {
        var snapshot=snapshot();
        assertThat(snapshot.gitlab().baseUrl()).contains(java.net.URI.create("http://gitlab-webservice-default.gitlab-system.svc:8181"));
        assertThat(snapshot.gitlab().serviceTargetVerified()).isTrue();assertThat(snapshot.gitlab().tokenPath()).contains(Path.of("/app/config/gitlab-read-token"));
        assertRepositories(snapshot.repositories());
        assertThat(snapshot.repositories()).allSatisfy(r->assertThat(r.approvalRef()).isEqualTo("tc-code-read-approved-2026-11-24"));
        var expected=List.of("tc-gateway/nori-tc-gateway/28","tc-gateway/nori-tc-common/26","tc-business/nori-tc-business/24","tc-business/nori-tc-common/26","tc-ui-backend/nori-tc-ui-backend/31","tc-ui-backend/nori-tc-common/26");
        assertThat(snapshot.deployLineage()).extracting(e->e.service()+"/"+e.sourceRepo()+"/"+e.siblingProjectId()).containsExactlyElementsOf(expected);
        assertThat(snapshot.deployLineage()).allSatisfy(e->{assertThat(e.method()).isEqualTo("SIBLING_CLONE_LOG");assertThat(e.packageJobName()).isEqualTo("package");assertThat(e.execProjectId()).isEqualTo(27);assertThat(e.approvalRef()).isEqualTo("tc-code-read-approved-2026-11-24");});
        var scopes=new SourceConnectorConfiguration.FactoryConfiguration().gitExecutionScopes(actual(),snapshot);
        assertRepositories(scopes.repositories());assertThat(scopes.deployLineage()).containsExactlyElementsOf(snapshot.deployLineage());
    }
    void assertRepositories(List<com.nori.ai.gateway.integration.gitlab.GitLabSourcePolicy.Repository> repositories) {
        assertThat(repositories).hasSize(5);assertThat(repositories).extracting(r->r.projectId()).containsExactlyInAnyOrderElementsOf(PREFIXES.keySet());
        assertThat(repositories).allSatisfy(r->{assertThat(r.sourceRepo()).isEqualTo(NAMES.get(r.projectId()));assertThat(r.refs()).containsExactly("main");assertThat(r.pathPrefixes()).containsExactlyInAnyOrderElementsOf(PREFIXES.get(r.projectId()));});
    }
    MockEnvironment declaration(boolean repositories) {
        var env=new MockEnvironment().withProperty("ai.gateway.systems.TC.issuer","fixture").withProperty("ai.gateway.systems.TC.serviceTokenSha256File","fixture.sha")
            .withProperty("ai.gateway.systems.TC.actorJwksFile","fixture.jwks").withProperty("ai.gateway.systems.TC.reviewPermission","REVIEW")
            .withProperty("ai.gateway.systems.TC.intents[0].id","code").withProperty("ai.gateway.systems.TC.intents[0].description","코드 식별자 조회")
            .withProperty("ai.gateway.systems.TC.intents[0].toolIds[0]","git.searchCode").withProperty("ai.gateway.systems.TC.intents[0].subjectKind","CODE_TERM")
            .withProperty("ai.gateway.systems.TC.intents[0].subjectRequired","true").withProperty("ai.gateway.systems.TC.intents[0].periodRequirement","NONE")
            .withProperty("ai.gateway.systems.TC.intents[0].taskType","LIVE_QUERY").withProperty("ai.gateway.systems.TC.intents[0].documentSearch","false");
        if(repositories)env.withProperty("ai.gateway.systems.TC.repositories[0].projectId","28").withProperty("ai.gateway.systems.TC.repositories[0].ref","main")
            .withProperty("ai.gateway.systems.TC.repositories[0].pathPrefix","gateway").withProperty("ai.gateway.systems.TC.repositories[0].requiredPermissions[0]","AI_CODE_READ");
        return env;
    }
    @Test void builtinQSchemaAcceptsCodeDeclarationButMissingRepositoryFailsLoading() {
        assertThatCode(()->GatewaySystemsProperties.load(declaration(true))).doesNotThrowAnyException();
        assertThatThrownBy(()->GatewaySystemsProperties.load(declaration(false))).isInstanceOf(RuntimeException.class);
    }
    @Test void codeTermRequiresSubject() {
        assertThatThrownBy(()->GatewaySystemsProperties.load(declaration(true).withProperty("ai.gateway.systems.TC.intents[0].subjectRequired","false"))).isInstanceOf(RuntimeException.class);
    }
    @Test void codeTermRequiresAtLeastOneTool() {
        var env=declaration(true);env.getPropertySources().forEach(source->{if(source instanceof org.springframework.core.env.MapPropertySource map)map.getSource().remove("ai.gateway.systems.TC.intents[0].toolIds[0]");});
        assertThatThrownBy(()->GatewaySystemsProperties.load(env)).isInstanceOf(RuntimeException.class);
    }
    StandardEnvironment customTool(boolean q,boolean multiple)throws Exception {
        var tool=Map.of("id","custom.read","description","사용자 도구","inputSchema",Map.of("type","object","additionalProperties",false,"properties",Map.of(q?"q":"eqp",Map.of("type","string"))),
            "outputSchema",Map.of("type","object"),"requiredPermissions",List.of("READ"),"timeoutMs",10000);
        var intent=Map.of("id","code","description","코드 식별자 조회","toolIds",multiple?List.of("custom.read","git.searchCode"):List.of("custom.read"),"subjectKind","CODE_TERM",
            "subjectRequired",true,"periodRequirement","NONE","taskType","LIVE_QUERY","documentSearch",false);
        var tc=Map.of("issuer","fixture","serviceTokenSha256File","fixture.sha","actorJwksFile","fixture.jwks","reviewPermission","REVIEW","backendUrl","http://127.0.0.1:1","gatewayTokenFile","fixture-token",
            "tools",List.of(tool),"repositories",List.of(Map.of("projectId","28","ref","main","pathPrefix","gateway","requiredPermissions",List.of("AI_CODE_READ"))),"intents",List.of(intent));
        // 일반 도구의 기존 additionalProperties=false 계약을 실제 YAML 자료형으로 제공합니다.
        var resource=new org.springframework.core.io.ByteArrayResource(SchemaContracts.JSON.writeValueAsBytes(Map.of("ai",Map.of("gateway",Map.of("systems",Map.of("TC",tc)))))) {
            @Override public String getFilename(){return "ai-gateway.yaml";}
        };
        var env=new StandardEnvironment();for(var source:new YamlPropertySourceLoader().load("code-tool-fixture",resource))env.getPropertySources().addLast(source);return env;
    }
    @Test void everyDeclaredToolMustActuallyHaveQInItsInputSchema() {
        assertThatCode(()->GatewaySystemsProperties.load(customTool(true,false))).doesNotThrowAnyException();
        assertThatThrownBy(()->GatewaySystemsProperties.load(customTool(false,false))).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(()->GatewaySystemsProperties.load(customTool(false,true))).isInstanceOf(RuntimeException.class);
    }
    @Test void omittedAndNullSubjectKindAndCanonicalConstructorNullUseEqp() {
        for(String suffix:List.of("",",\"subjectKind\":null")) {
            var intent=SchemaContracts.JSON.readValue("{\"id\":\"registration\",\"description\":\"등록 정보\",\"toolIds\":[\"tc.eqp.readRegistration\"],\"subjectRequired\":true,\"periodRequirement\":\"NONE\",\"taskType\":\"LIVE_QUERY\",\"documentSearch\":false"+suffix+"}",ClassificationIntent.class);
            assertThat(SchemaContracts.JSON.valueToTree(intent).path("subjectKind").asString()).isEqualTo("EQP");
        }
        var explicitNull=new ClassificationIntent("registration","등록 정보",List.of("tc.eqp.readRegistration"),true,ClassificationIntent.PeriodRequirement.NONE,TaskType.LIVE_QUERY,false,false,null);
        assertThat(SchemaContracts.JSON.valueToTree(explicitNull).path("subjectKind").asString()).isEqualTo("EQP");
    }
}
