package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.plan.PlanValidator;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.seam.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.time.Clock;
import java.util.*;

/** Spring에 의존하지 않는 Core의 빈 등록 책임을 이 설정에 모읍니다. */
@Configuration(proxyBeanMethods = false)
public class CoreBeansConfiguration {
    @Bean com.nori.ai.gateway.core.workflow.WorkflowPlanBuilder workflowPlanBuilder(CapabilityRegistry registry){return new com.nori.ai.gateway.core.workflow.WorkflowPlanBuilder(registry);}
    @Bean com.nori.ai.gateway.core.workflow.EvidenceCoverageEvaluator evidenceCoverageEvaluator(){return new com.nori.ai.gateway.core.workflow.EvidenceCoverageEvaluator();}
    @Bean com.nori.ai.gateway.core.time.TimeWindowResolver timeWindowResolver(){return new com.nori.ai.gateway.core.time.TimeWindowResolver();}
    @Bean com.nori.ai.gateway.core.blocks.UiSchemaValidator uiSchemaValidator(){return new com.nori.ai.gateway.core.blocks.UiSchemaValidator();}
    @Bean com.nori.ai.gateway.core.blocks.BlockBuilder blockBuilder(com.nori.ai.gateway.core.blocks.UiSchemaValidator validator){return new com.nori.ai.gateway.core.blocks.BlockBuilder(validator);}
    @Bean com.nori.ai.gateway.core.workflow.TerminalOutcomeAssembler terminalOutcomeAssembler(){return new com.nori.ai.gateway.core.workflow.TerminalOutcomeAssembler();}
    @Bean com.nori.ai.gateway.core.workflow.ConversationSummaryBuilder conversationSummaryBuilder(){return new com.nori.ai.gateway.core.workflow.ConversationSummaryBuilder();}
    @Bean com.nori.ai.gateway.core.workflow.LogSummarizer logSummarizer(){return new com.nori.ai.gateway.core.workflow.LogSummarizer();}
    @Bean com.nori.ai.gateway.core.blocks.GeneralQueryHelpBuilder generalQueryHelpBuilder(){return new com.nori.ai.gateway.core.blocks.GeneralQueryHelpBuilder();}
    @Bean com.nori.ai.gateway.core.evidence.EvidenceAssembler evidenceAssembler(){return new com.nori.ai.gateway.core.evidence.EvidenceAssembler();}
    @Bean com.nori.ai.gateway.core.cache.ToolResultCache toolResultCache(com.nori.ai.gateway.core.cache.ToolResultCache.Store store,PlanValidator validator,com.nori.ai.gateway.core.result.ResultAccessEvaluator access,com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader documents,com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiry,Clock clock){return new com.nori.ai.gateway.core.cache.ToolResultCache(store,validator,access,documents,expiry,clock);}
    @Bean com.nori.ai.gateway.core.workflow.WorkflowExecutor coreWorkflowExecutor(CapabilityRegistry registry,PlanValidator validator,ToolDispatcher dispatcher,com.nori.ai.gateway.core.authz.SourceAuthorizationStep authorization,com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiry,Clock clock,com.nori.ai.gateway.core.cache.ToolResultCache cache,com.nori.ai.gateway.core.blocks.BlockBuilder blocks,com.nori.ai.gateway.core.workflow.WorkflowExecutor.ExecutionRecorder recorder,com.nori.ai.gateway.core.workflow.LogSummarizer summarizer,com.nori.ai.gateway.core.blocks.GeneralQueryHelpBuilder help,com.nori.ai.gateway.core.workflow.EvidenceCoverageEvaluator coverage){return new com.nori.ai.gateway.core.workflow.WorkflowExecutor(registry,validator,dispatcher,authorization,expiry,clock,cache,blocks,recorder,summarizer,help,coverage);}
    @Bean com.nori.ai.gateway.core.authz.SourcePermissionMap sourcePermissionMap(CapabilityRegistry registry){
        var mappings=new java.util.LinkedHashMap<String,com.nori.ai.gateway.core.authz.SourcePermissionMap.Entry>();
        for(var tool:registry.all())if(!tool.requiredPermission().isEmpty())mappings.put(tool.sourceAllowlistRef(),new com.nori.ai.gateway.core.authz.SourcePermissionMap.Entry(Set.copyOf(tool.requiredPermission()),false));
        return new com.nori.ai.gateway.core.authz.SourcePermissionMap(mappings);
    }
    @Bean com.nori.ai.gateway.core.authz.SourceAuthorizationStep sourceAuthorizationStep(com.nori.ai.gateway.core.authz.SourcePermissionMap permissions,Optional<com.nori.ai.gateway.app.hooks.SeamRegistry> seams){
        return new com.nori.ai.gateway.core.authz.SourceAuthorizationStep(permissions,seams.<com.nori.ai.gateway.core.authz.SourceAuthorizationStep.ScopedPermissionExclusion>map(seam->seam::permissionMappingExcluded).orElse((source,scope)->false));
    }
    @Bean com.nori.ai.gateway.core.retention.RetentionClassifier retentionClassifier() { return new com.nori.ai.gateway.core.retention.RetentionClassifier(); }
    @Bean com.nori.ai.gateway.core.retention.TrimCompletenessEvaluator trimCompletenessEvaluator() { return new com.nori.ai.gateway.core.retention.TrimCompletenessEvaluator(); }
    @Bean com.nori.ai.gateway.core.retention.DlqCompletenessEvaluator dlqCompletenessEvaluator() { return new com.nori.ai.gateway.core.retention.DlqCompletenessEvaluator(); }
    @Bean com.nori.ai.gateway.core.result.ResultAccessEvaluator resultAccessEvaluator() { return new com.nori.ai.gateway.core.result.ResultAccessEvaluator(); }
    @Bean com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader knowledgeDocumentStateReader() { return new com.nori.ai.gateway.core.result.FailClosedKnowledgeDocumentStateReader(); }
    @Bean com.nori.ai.gateway.core.result.EvidenceSourceReader evidenceSourceReader() { return new com.nori.ai.gateway.core.result.FailClosedEvidenceSourceReader(); }
    @Bean com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiryCheckpoint() { return new com.nori.ai.gateway.core.expiry.ExpiryCheckpoint(); }
    @Bean com.nori.ai.gateway.core.sanitize.ContentSanitizer contentSanitizer() { return new com.nori.ai.gateway.core.sanitize.ContentSanitizer(); }
    @Bean com.nori.ai.gateway.core.audit.AuditFieldAllowlist auditFieldAllowlist(com.nori.ai.gateway.core.sanitize.ContentSanitizer sanitizer, CapabilityRegistry registry) { return new com.nori.ai.gateway.core.audit.AuditFieldAllowlist(sanitizer, registry); }
    @Bean com.nori.ai.gateway.core.job.GenerationSemaphore generationSemaphore(CapacityProfile capacity,org.springframework.core.env.Environment environment) { var settings=com.nori.ai.gateway.integration.inference.LlmSettings.load(environment); if(capacity.generationConcurrency()!=settings.generationConcurrency())throw new IllegalStateException("생성 동시성 설정이 다릅니다"); return new com.nori.ai.gateway.core.job.GenerationSemaphore(settings.generationConcurrency()); }
    @Bean com.nori.ai.gateway.core.budget.TokenBudgetPolicy tokenBudgetPolicy(org.springframework.core.env.Environment environment) { return com.nori.ai.gateway.integration.inference.LlmSettings.load(environment).policy(); }
    @Bean com.nori.ai.gateway.core.budget.PromptReducer promptReducer(com.nori.ai.gateway.core.budget.PromptTokenCounting counter, com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy) { return new com.nori.ai.gateway.core.budget.PromptReducer(counter, policy); }
    @Bean com.nori.ai.gateway.core.budget.PreCallBudgetAssert preCallBudgetAssert(com.nori.ai.gateway.core.budget.PromptTokenCounting counter, com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy) { return new com.nori.ai.gateway.core.budget.PreCallBudgetAssert(counter, policy); }
    @Bean com.nori.ai.gateway.core.budget.TruncationDetector truncationDetector() { return new com.nori.ai.gateway.core.budget.TruncationDetector(); }
    @Bean ManifestLoader manifestLoader() { return new ManifestLoader(); }
    @Bean ContractVersionPolicy contractVersionPolicy() { return new ContractVersionPolicy(); }
    @Bean CapabilityRegistry capabilityRegistry(GatewaySystemsProperties systems,ContractVersionPolicy versions){return new CapabilityRegistry(SystemTools.manifests(systems),Set.of(ImplementationPhase.PHASE_1),versions);}
    @Bean PlanValidator planValidator(CapabilityRegistry registry,PlanInjectionSeam seam,Clock clock){return new PlanValidator(registry,seam,clock,12,12);}
    @Bean @ConditionalOnProperty(name="ai.gateway.inference.mode",havingValue="enabled")
    com.nori.ai.gateway.core.llm.ClassificationService classificationService(com.nori.ai.gateway.core.port.InferenceClient inference,com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy,
            com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter tokenizer,com.nori.ai.gateway.core.budget.PromptTokenCounting counter,com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiry,Optional<com.nori.ai.gateway.core.llm.LlmCallObserver> observer,
            com.nori.ai.gateway.core.llm.LlmCallAudit audit,com.nori.ai.gateway.app.job.ClassificationInputs inputs){
        return new com.nori.ai.gateway.core.llm.ClassificationService(inference,policy,tokenizer::countText,counter,expiry,observer.orElse(com.nori.ai.gateway.core.llm.LlmCallObserver.NONE),audit,JobConfiguration.resource(JobConfiguration.CLASSIFY_PROMPT),inputs);
    }
    @Bean ToolResultContractValidator toolResultContractValidator(ContractVersionPolicy versions) { return new ToolResultContractValidator(versions); }
    @Bean ToolDispatcher toolDispatcher(CapabilityRegistry registry, PlanValidator validator, ToolResultContractValidator results,
            DispatchInjectionSeam seam, List<ToolExecutor> executors, Clock clock) {
        return new ToolDispatcher(registry, validator, results, seam, executors, clock);
    }
    @Bean
    @ConditionalOnProperty(name = "ai.gateway.test-hooks.enabled", havingValue = "false", matchIfMissing = true)
    PlanInjectionSeam planInjectionSeam() { return new PlanInjectionSeam.NoOp(); }
    @Bean
    @ConditionalOnProperty(name = "ai.gateway.test-hooks.enabled", havingValue = "false", matchIfMissing = true)
    DispatchInjectionSeam dispatchInjectionSeam() { return new DispatchInjectionSeam.NoOp(); }
}
