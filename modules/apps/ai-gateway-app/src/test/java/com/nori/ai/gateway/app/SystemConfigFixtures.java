package com.nori.ai.gateway.app;
import com.nori.ai.gateway.app.config.*;
import java.util.*;
public final class SystemConfigFixtures {
 private SystemConfigFixtures(){}
 public static java.util.List<com.nori.ai.gateway.core.llm.ClassificationIntent> intents(){
  return java.util.List.of(
   new com.nori.ai.gateway.core.llm.ClassificationIntent("status","설비 상태",java.util.List.of("tc.eqp.getOperationalSnapshot"),true,com.nori.ai.gateway.core.llm.ClassificationIntent.PeriodRequirement.NONE,com.nori.ai.gateway.core.registry.TaskType.LIVE_QUERY,false,false,null),
   new com.nori.ai.gateway.core.llm.ClassificationIntent("document","문서",java.util.List.of(),false,com.nori.ai.gateway.core.llm.ClassificationIntent.PeriodRequirement.NONE,com.nori.ai.gateway.core.registry.TaskType.KNOWLEDGE_QUERY,true,false,null),
   new com.nori.ai.gateway.core.llm.ClassificationIntent("help","도움말",java.util.List.of(),false,com.nori.ai.gateway.core.llm.ClassificationIntent.PeriodRequirement.NONE,com.nori.ai.gateway.core.registry.TaskType.GENERAL_QUERY,false,false,null));
 }
 public static GatewaySystemsProperties systems(){
  var tool=new GatewaySystemsProperties.Tool("tc.eqp.getOperationalSnapshot","설비 조회",Map.of("type","object","additionalProperties",false,"properties",Map.of("eqp",Map.of("type","string","minLength",1,"maxLength",64)),"required",List.of("eqp")),Map.of("type","object"),List.of("EQP_READ"),10000);
  return new GatewaySystemsProperties(Map.of("TC",new GatewaySystemsProperties.SystemConfig("tc-ui-backend","fixture-hash","fixture-jwks","http://127.0.0.1:1","fixture-token",List.of(tool),List.of(),List.of("tc-gateway","tc-business","tc-ui-backend","tc-ui-front").stream().map(app->new GatewaySystemsProperties.App(app,"tc",List.of("AI_LOG_READ"))).toList(),List.of(),List.of(new GatewaySystemsProperties.Trace("tc-gateway","tc",List.of("AI_TRACE_READ"))),"AI_REVIEW",Map.of(),intents())));
 }
}
