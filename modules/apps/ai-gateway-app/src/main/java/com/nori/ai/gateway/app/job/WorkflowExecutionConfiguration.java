package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.core.authz.SourcePermissionMap;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.port.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.integration.loki.*;
import com.nori.ai.gateway.integration.tempo.TempoTraceConnector;
import com.nori.ai.gateway.integration.gitlab.*;
import com.nori.ai.gateway.persistence.repository.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import java.time.Clock;

@Configuration(proxyBeanMethods=false)
@EnableConfigurationProperties(LokiProperties.class)
public class WorkflowExecutionConfiguration {
    @Bean @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="ai.gateway.inference.mode",havingValue="enabled")
    GroundingInputs groundingInputs(com.nori.ai.gateway.app.config.GatewaySystemsProperties systems,CapabilityRegistry registry,ContentSanitizer sanitizer,ObjectProvider<DirectSourceToolExecutor.GitExecutionScopes> scopes,com.nori.ai.gateway.core.feedback.ReviewedGlossary glossary,ClassificationInputs history){
        var projects=new java.util.HashMap<String,String>();var configured=scopes.getIfAvailable();if(configured!=null)for(var repo:configured.repositories())projects.put(repo.sourceRepo(),repo.projectId());
        return new GroundingInputs(systems,registry,sanitizer,projects,glossary,history);
    }
    @Bean WorkflowResourceResolverFactory workflowResourceResolverFactory(SourcePermissionMap permissions,CapabilityRegistry registry,ExpiryCheckpoint expiry){return new WorkflowResourceResolverFactory(permissions,registry,expiry);}
    @Bean WorkflowCacheStore workflowCacheStore(CacheRepository cache,ContentSanitizer sanitizer,CapabilityRegistry registry){return new WorkflowCacheStore(cache,sanitizer,registry);}
    @Bean WorkflowExecutionRecords workflowExecutionRecords(ToolExecutionRepository executions,ContentSanitizer sanitizer,ExpiryCheckpoint expiry,CapabilityRegistry registry,com.nori.ai.gateway.app.observability.AuditRecorder audit){return new WorkflowExecutionRecords(executions,sanitizer,expiry,registry,audit);}
    @Bean BusinessToolExecutor businessToolExecutor(SystemQueryPort business,Clock clock){return new BusinessToolExecutor(business,clock);}
    @Bean DirectSourceToolExecutor logToolExecutor(ObjectProvider<LokiLogConnector> logs,ObjectProvider<TempoTraceConnector> traces,ObjectProvider<GitLabCodeConnector> code,ObjectProvider<DirectSourceToolExecutor.GitExecutionScopes> scopes,Clock clock,LokiProperties properties,DeploymentEvidenceResolver deployments,ObjectProvider<com.nori.ai.gateway.app.hooks.SourceRequestProbe> probe,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems){return new DirectSourceToolExecutor(ToolKind.LOG,logs,traces,code,scopes,clock,properties,deployments,java.util.Optional.ofNullable(probe.getIfAvailable()),systems);}
    @Bean DirectSourceToolExecutor traceToolExecutor(ObjectProvider<LokiLogConnector> logs,ObjectProvider<TempoTraceConnector> traces,ObjectProvider<GitLabCodeConnector> code,ObjectProvider<DirectSourceToolExecutor.GitExecutionScopes> scopes,Clock clock,LokiProperties properties,DeploymentEvidenceResolver deployments,ObjectProvider<com.nori.ai.gateway.app.hooks.SourceRequestProbe> probe,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems){return new DirectSourceToolExecutor(ToolKind.TRACE,logs,traces,code,scopes,clock,properties,deployments,java.util.Optional.ofNullable(probe.getIfAvailable()),systems);}
    @Bean DirectSourceToolExecutor gitToolExecutor(ObjectProvider<LokiLogConnector> logs,ObjectProvider<TempoTraceConnector> traces,ObjectProvider<GitLabCodeConnector> code,ObjectProvider<DirectSourceToolExecutor.GitExecutionScopes> scopes,Clock clock,LokiProperties properties,DeploymentEvidenceResolver deployments,ObjectProvider<com.nori.ai.gateway.app.hooks.SourceRequestProbe> probe,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems){return new DirectSourceToolExecutor(ToolKind.GIT,logs,traces,code,scopes,clock,properties,deployments,java.util.Optional.ofNullable(probe.getIfAvailable()),systems);}
    @Bean DeploymentEvidenceResolver deploymentEvidenceResolver(ObjectProvider<GitLabPipelineLineageReader> reader){return new DeploymentEvidenceResolver(reader);}
    @Bean SourceRetentionAttributesProvider sourceRetentionAttributesProvider(ObjectProvider<SourceRetentionReader> reader){return new SourceRetentionAttributesProvider(reader);}
    @Bean WorkflowExecutionAdapter workflowExecutionAdapter(CapabilityRegistry registry,WorkflowPlanBuilder builder,WorkflowExecutor executor,WorkflowResourceResolverFactory resolvers,TimeWindowResolver windows,MessageRepository messages,ExpiryCheckpoint expiry,ContentSanitizer sanitizer,TerminalOutcomeAssembler assembler,ConversationSummaryBuilder summaries,com.nori.ai.gateway.core.evidence.EvidenceAssembler evidence,com.nori.ai.gateway.app.observability.AuditRecorder audit,
            java.util.Optional<com.nori.ai.gateway.core.llm.ComposerService> composer,LogSummarizer logSummarizer,java.util.Optional<com.nori.ai.gateway.app.hooks.PausePointRegistry> pauses,java.util.Optional<WorkflowGrounding> grounding){return new WorkflowExecutionAdapter(registry,builder,executor,resolvers,windows,messages,expiry,sanitizer,assembler,summaries,evidence,audit,composer,logSummarizer,pauses,grounding);}
    /**
     * Response Composer(Plan P20). 추론 비활성에서는 빈 자체가 없다(N7). 프롬프트는 compose-v1 이고 답변 토큰은 pin tokenizer 로 센다.
     * 추론 활성 조건과 프롬프트 자원을 이 Job 실행 설정이 함께 가진다(Plan §16.4 WorkflowExecutionConfiguration 의 Composer 배선).
     */
    @Bean @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="ai.gateway.inference.mode",havingValue="enabled")
    com.nori.ai.gateway.core.llm.ComposerService composerService(InferenceClient inference,com.nori.ai.gateway.core.budget.PromptReducer reducer,com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy,
            com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter tokenizer,ExpiryCheckpoint expiry,java.util.Optional<com.nori.ai.gateway.core.llm.LlmCallObserver> observer,
            com.nori.ai.gateway.core.llm.LlmCallAudit audit,com.nori.ai.gateway.core.knowledge.SearchLanguage language,org.springframework.core.env.Environment environment,com.nori.ai.gateway.core.knowledge.SearchSettings search,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems){
        if(!prompt(COMPOSE_SELECT_PROMPT).equals(com.nori.ai.gateway.core.llm.EvidenceCandidateSelection.systemPrompt(false)))throw new IllegalStateException("선택 프롬프트 자원과 전송 계약이 다릅니다");
        if(!prompt(COMPOSE_SELECT_SUFFIX_PROMPT).equals(com.nori.ai.gateway.core.llm.EvidenceCandidateSelection.CONDITION_INSTRUCTION))throw new IllegalStateException("선택 보완 지시 자원과 전송 계약이 다릅니다");
        return new com.nori.ai.gateway.core.llm.ComposerService(inference,reducer,policy,tokenizer::countText,expiry,observer.orElse(com.nori.ai.gateway.core.llm.LlmCallObserver.NONE),audit,prompt(COMPOSE_PROMPT),language,configurationFingerprint(environment,search,systems));
    }
    private static String configurationFingerprint(org.springframework.core.env.Environment environment,com.nori.ai.gateway.core.knowledge.SearchSettings search,com.nori.ai.gateway.app.config.GatewaySystemsProperties systems) {
        var settings=com.nori.ai.gateway.integration.inference.LlmSettings.load(environment);
        // 자격 값과 자격 파일 내용은 영구 지문의 입력에 포함하지 않습니다.
        var values=new java.util.TreeMap<String,Object>();values.put("baseUrl",settings.baseUrl());values.put("generationModel",settings.generationModel());values.put("embeddingModel",settings.embeddingModel());
        values.put("temperature",settings.temperature());values.put("contextTokens",settings.contextTokens());values.put("tokenCounting",settings.tokenCounting());values.put("budgets",settings.budgets());
        values.put("evidenceTokens",settings.evidenceTokens());values.put("requestTimeout",settings.requestTimeout().toString());values.put("prompt",prompt(COMPOSE_PROMPT));values.put("mask","mask-v1");
        values.put("groundedPrompt",prompt(COMPOSE_SELECT_PROMPT));values.put("groundedHistoryPrompt",com.nori.ai.gateway.core.llm.EvidenceCandidateSelection.systemPrompt(true));values.put("historyClassification",com.nori.ai.gateway.core.llm.CitedHistoryContext.INSTRUCTION);
        values.put("groundedPromptVersion",com.nori.ai.gateway.core.llm.EvidenceCandidateSelection.PROMPT_VERSION);values.put("groundedSuffixAfterOwner",prompt(COMPOSE_SELECT_SUFFIX_PROMPT));values.put("groundedAnswerShape",com.nori.ai.gateway.core.llm.QuestionAnswerShape.contract());
        values.put("search",search);var sources=new java.util.TreeMap<String,Object>();systems.systems().forEach((id,system)->sources.put(id,java.util.Map.of("tools",system.tools(),"documents",system.documents(),"apps",system.apps(),"traces",system.traces(),"repositories",system.repositories(),"glossary",system.glossary())));values.put("systems",sources);
        return com.nori.ai.gateway.core.result.AnswerSnapshot.sha(SchemaContracts.canonicalize(SchemaContracts.JSON.writeValueAsString(values)));
    }
    public static final String COMPOSE_PROMPT="prompts/compose-v1.txt";
    public static final String COMPOSE_SELECT_PROMPT="prompts/compose-select-v1.txt";
    public static final String COMPOSE_SELECT_SUFFIX_PROMPT="prompts/compose-select-suffix-v1.txt";
    private static String prompt(String path){
        try(var input=WorkflowExecutionConfiguration.class.getClassLoader().getResourceAsStream(path)){
            if(input==null)throw new IllegalStateException("프롬프트 자원이 없습니다: "+path);
            return new String(input.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);
        }catch(java.io.IOException failure){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(WorkflowExecutionConfiguration.class.getName()),"WORKFLOW_EXECUTION_CONFIGURATION_FAILED",failure);throw new IllegalStateException("프롬프트 자원을 읽을 수 없습니다: "+path,failure);}
    }
}
