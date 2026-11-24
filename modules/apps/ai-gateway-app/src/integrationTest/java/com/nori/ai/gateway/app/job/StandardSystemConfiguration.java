package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import java.nio.file.*;
import java.util.*;
import org.springframework.core.env.Environment;
/** 운영 도구 설정을 그대로 사용하고 주소와 자격만 로컬 시험 것으로 바꿉니다. */
public final class StandardSystemConfiguration {
 private StandardSystemConfiguration(){}
 public static GatewaySystemsProperties systems(Environment env,String url,String token)throws java.io.IOException {
  Object data=new org.yaml.snakeyaml.Yaml().load(Files.readString(Path.of(System.getProperty("aigw.rootDir"),"config/ai-gateway.yaml")));
  var tc=SchemaContracts.JSON.valueToTree(data).path("ai").path("gateway").path("systems").path("TC");
  var tools=SchemaContracts.JSON.convertValue(tc.path("tools"),new tools.jackson.core.type.TypeReference<List<GatewaySystemsProperties.Tool>>(){});
  var apps=SchemaContracts.JSON.convertValue(tc.path("apps"),new tools.jackson.core.type.TypeReference<List<GatewaySystemsProperties.App>>(){});
  var traces=SchemaContracts.JSON.convertValue(tc.path("traces"),new tools.jackson.core.type.TypeReference<List<GatewaySystemsProperties.Trace>>(){});
  var intents=SchemaContracts.JSON.convertValue(tc.path("intents"),new tools.jackson.core.type.TypeReference<List<com.nori.ai.gateway.core.llm.ClassificationIntent>>(){});
  return new GatewaySystemsProperties(Map.of("TC",new GatewaySystemsProperties.SystemConfig("tc-ui-backend",env.getProperty("ai.gateway.systems[TC].service-token-sha256-file"),env.getProperty("ai.gateway.systems[TC].actor-jwks-file"),url,token,tools,env.containsProperty("test.document-prefix")?List.of(new GatewaySystemsProperties.Document("fixture-bucket",env.getProperty("test.document-prefix"),List.of("AI_DOC_READ"))):List.of(),apps,env.getProperty("test.git-enabled",Boolean.class,false)?List.of(new GatewaySystemsProperties.Repository("22","main","src/main",List.of("AI_CODE_READ"))):List.of(),traces,"AI_REVIEW",Map.of(),intents)));
 }
 public static List<Map<String,Object>> calls(com.nori.ai.gateway.core.registry.TaskType task){return switch(task){case GENERAL_QUERY,UNSUPPORTED_REQUEST,KNOWLEDGE_QUERY->List.of();case LIVE_QUERY->List.of(Map.of("toolId","tc.eqp.getOperationalSnapshot","arguments",Map.of("eqp","EQP-001")));case INCIDENT_ANALYSIS->List.of(Map.of("toolId","tc.eqp.getOperationalSnapshot","arguments",Map.of("eqp","EQP-001")),Map.of("toolId","logs.search","arguments",Map.of("targetRef","EQP-001")),Map.of("toolId","trace.search","arguments",Map.of()));};}
 public static com.nori.ai.gateway.app.hooks.TestHookRegistry.Scenario scenario(com.nori.ai.gateway.core.registry.TaskType task,Set<com.nori.ai.gateway.app.hooks.PausePointRegistry.Point> points){return new com.nori.ai.gateway.app.hooks.TestHookRegistry.Scenario(task,points,null,false,Optional.empty(),List.of(),Optional.empty(),calls(task));}
}
