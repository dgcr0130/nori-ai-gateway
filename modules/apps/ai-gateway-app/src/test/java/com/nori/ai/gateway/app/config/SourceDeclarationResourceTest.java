package com.nori.ai.gateway.app.config;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import static org.assertj.core.api.Assertions.*;
class SourceDeclarationResourceTest {
 @Test void containerAbsoluteTokenPathLoadsOnEveryHostAndRelativeOrTraversalPathsStillFail() {
  String declaration="{\"loki\":{},\"tempo\":{},\"gitlab\":{\"readTokenFile\":%s}}";
  var mapper=com.nori.ai.gateway.core.registry.SchemaContracts.JSON;
  var snapshot=SourceConnectorConfiguration.SourceSnapshot.parse(declaration.formatted(mapper.writeValueAsString("/app/config/gitlab-read-token")));
  assertThat(snapshot.gitlab().tokenPath()).contains(java.nio.file.Path.of("/app/config/gitlab-read-token"));
  for(String path:java.util.List.of("gitlab-read-token","config/gitlab-read-token","/app/../gitlab-read-token"))
   assertThatThrownBy(()->SourceConnectorConfiguration.SourceSnapshot.parse(declaration.formatted(mapper.writeValueAsString(path)))).as(path).isInstanceOf(IllegalStateException.class);
 }
 @Test void packagedSharedDeclarationLoadsConfirmedMonitoringAndApprovedGitAddresses()throws Exception {
  var resource=new ClassPathResource(SourceConnectorConfiguration.RESOURCE);assertThat(resource.exists()).isTrue();
  String text=resource.getContentAsString(java.nio.charset.StandardCharsets.UTF_8);var snapshot=SourceConnectorConfiguration.SourceSnapshot.parse(text);
  assertThat(snapshot.loki().serviceTargetVerified()).isTrue();assertThat(snapshot.tempo().serviceTargetVerified()).isTrue();assertThat(snapshot.loki().baseUrl()).contains(java.net.URI.create("http://loki.monitoring.svc:3100"));assertThat(snapshot.tempo().baseUrl()).contains(java.net.URI.create("http://tempo.monitoring.svc:3200"));assertThat(snapshot.gitlab().serviceTargetVerified()).isTrue();assertThat(snapshot.gitlab().baseUrl()).contains(java.net.URI.create("http://gitlab-webservice-default.gitlab-system.svc:8181"));assertThat(snapshot.gitlab().tokenPath()).contains(java.nio.file.Path.of("/app/config/gitlab-read-token"));assertThat(snapshot.repositories()).hasSize(5);assertThat(snapshot.deployLineage()).hasSize(6);assertThat(text).doesNotContain("namespace","ownerSystem");
 }
 @Test void actualRegistrarCreatesConfirmedSourceClientsWithoutCallingTheirEndpoints(){
  new org.springframework.boot.test.context.runner.ApplicationContextRunner().withUserConfiguration(SourceConnectorConfiguration.class)
   .withBean(org.springframework.jdbc.core.simple.JdbcClient.class,()->org.mockito.Mockito.mock(org.springframework.jdbc.core.simple.JdbcClient.class))
   .withBean(org.springframework.transaction.support.TransactionTemplate.class,()->org.mockito.Mockito.mock(org.springframework.transaction.support.TransactionTemplate.class))
   .withBean(GatewaySystemsProperties.class,()->com.nori.ai.gateway.app.SystemConfigFixtures.systems())
   .withBean(java.time.Clock.class,java.time.Clock::systemUTC)
   .withBean(com.nori.ai.gateway.core.authz.SourceAuthorizationStep.class,()->new com.nori.ai.gateway.core.authz.SourceAuthorizationStep(new com.nori.ai.gateway.core.authz.SourcePermissionMap(java.util.Map.of())))
   .withBean(io.micrometer.core.instrument.MeterRegistry.class,io.micrometer.core.instrument.simple.SimpleMeterRegistry::new)
   .withBean(io.opentelemetry.api.OpenTelemetry.class,io.opentelemetry.api.OpenTelemetry::noop)
   .run(context->{assertThat(context).hasNotFailed().hasSingleBean(SourceConnectorConfiguration.SourceSnapshot.class);assertThat(context.getBeansOfType(com.nori.ai.gateway.integration.source.SourceHttpClient.class)).hasSize(2);});
 }
 @org.junit.jupiter.api.io.TempDir java.nio.file.Path directory;
 @Test void readyGitFactoryAutowiresTheConfiguredScopesAndNeverAdoptsTransportRepositoryRanges()throws Exception {
  var token=directory.resolve("git-token");java.nio.file.Files.writeString(token,"local-test-token");
  String declaration="{\"loki\":{},\"tempo\":{},\"gitlab\":{\"baseUrl\":\"http://127.0.0.1:1\",\"serviceTargetVerified\":true,\"readTokenFile\":"+com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(token.toString())+",\"repositories\":[{\"projectId\":22,\"sourceRepo\":\"old-range\",\"refs\":[\"main\"],\"pathPrefixes\":[\"src\"],\"approvalRef\":\"fixture\"}]}}";
  var meters=new io.micrometer.core.instrument.simple.SimpleMeterRegistry();try(var context=new org.springframework.context.annotation.AnnotationConfigApplicationContext()){
   context.register(SourceConnectorConfiguration.FactoryConfiguration.class);
   context.registerBean(GatewaySystemsProperties.class,()->com.nori.ai.gateway.app.SystemConfigFixtures.systems());
   context.registerBean(java.time.Clock.class,java.time.Clock::systemUTC);
   context.registerBean(com.nori.ai.gateway.core.authz.SourceAuthorizationStep.class,()->new com.nori.ai.gateway.core.authz.SourceAuthorizationStep(new com.nori.ai.gateway.core.authz.SourcePermissionMap(java.util.Map.of())));
   context.registerBean(io.micrometer.core.instrument.MeterRegistry.class,()->meters);context.registerBean(io.opentelemetry.api.OpenTelemetry.class,io.opentelemetry.api.OpenTelemetry::noop);
   new SourceConnectorConfiguration.Registrar(declaration).registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata.introspect(SourceConnectorConfiguration.class),context);
   context.refresh();assertThat(context.getBean(com.nori.ai.gateway.integration.gitlab.GitLabCodeConnector.class)).isNotNull();assertThat(context.getBean(com.nori.ai.gateway.app.job.DirectSourceToolExecutor.GitExecutionScopes.class).repositories()).isEmpty();
  }finally{meters.close();}
 }
}
