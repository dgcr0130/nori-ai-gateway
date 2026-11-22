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
    @Bean @org.springframework.context.annotation.Lazy
    com.nori.ai.gateway.core.feedback.FeedbackService feedbackService(com.nori.ai.gateway.core.feedback.FeedbackStore store,com.nori.ai.gateway.core.sanitize.ContentSanitizer sanitizer,com.nori.ai.gateway.core.knowledge.SearchLanguage language,
            org.springframework.beans.factory.ObjectProvider<com.nori.ai.gateway.core.port.InferenceClient> inference,com.nori.ai.gateway.app.knowledge.FeedbackKnowledge knowledge,org.springframework.beans.factory.ObjectProvider<com.nori.ai.gateway.app.job.recovery.ReadinessGate> readiness) {
        return new com.nori.ai.gateway.core.feedback.FeedbackService(store,sanitizer,language,text->{if(!readiness.getObject().startupReady())throw new com.nori.ai.gateway.core.feedback.FeedbackFailure(503,"KNOWLEDGE_UNAVAILABLE");var client=inference.getIfAvailable();if(client==null)throw new com.nori.ai.gateway.core.feedback.FeedbackFailure(503,"KNOWLEDGE_UNAVAILABLE");var vectors=client.embed(List.of(text));if(vectors==null||vectors.size()!=1)throw new com.nori.ai.gateway.core.feedback.FeedbackFailure(503,"KNOWLEDGE_UNAVAILABLE");return vectors.getFirst();},knowledge::refresh);
    }
    @Bean com.nori.ai.gateway.core.knowledge.SearchSettings searchSettings(org.springframework.core.env.Environment env) {
        return new com.nori.ai.gateway.core.knowledge.SearchSettings(env.getProperty("ai.gateway.search.semanticCandidates",Integer.class,20),env.getProperty("ai.gateway.search.keywordCandidates",Integer.class,20),
            env.getProperty("ai.gateway.search.identifierCandidates",Integer.class,20),env.getProperty("ai.gateway.search.rrfK",Integer.class,60),env.getProperty("ai.gateway.search.evidenceLimit",Integer.class,6),
            env.getProperty("ai.gateway.search.verifiedCandidates",Integer.class,3),env.getProperty("ai.gateway.search.verifiedMinCosine",Double.class,.90),env.getProperty("ai.gateway.search.minSemanticCosine",Double.class,.35),
            env.getProperty("ai.gateway.search.coreTermLimit",Integer.class,4),env.getProperty("ai.gateway.search.retryCount",Integer.class,1));
    }
    @Bean @ConditionalOnProperty(name="ai.gateway.inference.mode",havingValue="enabled")
    com.nori.ai.gateway.core.workflow.EvidenceSelection evidenceSelection(com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter tokenizer,com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy) {
        return new com.nori.ai.gateway.core.workflow.EvidenceSelection(tokenizer::countText,policy.evidenceTokens());
    }
    @Bean @ConditionalOnProperty(name="ai.gateway.inference.mode",havingValue="enabled")
    com.nori.ai.gateway.core.workflow.WorkflowGrounding workflowGrounding(com.nori.ai.gateway.core.knowledge.DocumentSearch search,com.nori.ai.gateway.core.knowledge.SearchLanguage language,
            com.nori.ai.gateway.core.knowledge.SearchSettings settings,com.nori.ai.gateway.core.workflow.EvidenceSelection selection,com.nori.ai.gateway.app.job.GroundingInputs inputs,Clock clock,com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiry,com.nori.ai.gateway.core.llm.ComposerService composer) {
        return new com.nori.ai.gateway.core.workflow.WorkflowGrounding(search,language,settings,selection,inputs,clock,expiry,composer::fitsGrounded);
    }
    @Bean @ConditionalOnProperty(name="ai.gateway.inference.mode",havingValue="enabled")
    com.nori.ai.gateway.core.knowledge.DocumentSearch documentSearch(com.nori.ai.gateway.persistence.repository.DocumentIndexRepository store,
            com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex index,com.nori.ai.gateway.core.port.InferenceClient inference) {
        return new com.nori.ai.gateway.core.knowledge.DocumentSearch() {
          public Round search(com.nori.ai.gateway.core.scope.VerifiedSystemScope scope,String query,List<String> targets,com.nori.ai.gateway.core.knowledge.SearchSettings settings){return search(scope,query,query,targets,settings);}
          @Override public Round search(com.nori.ai.gateway.core.scope.VerifiedSystemScope scope,String query,String question,List<String> targets,com.nori.ai.gateway.core.knowledge.SearchSettings settings) {
            var state=store.searchState(scope);
            if(state.isEmpty())return new com.nori.ai.gateway.core.knowledge.DocumentSearch.Round(List.of(),List.of(),List.of(),List.of());
            var inputs=query.equals(question)?List.of(query):List.of(query,question);
            var vectors=inference.embed(inputs);
            if(vectors==null||vectors.size()!=inputs.size()||vectors.stream().anyMatch(vector->vector==null||vector.length!=state.get().dimension()))
                throw new com.nori.ai.gateway.core.port.InferenceClient.EmbeddingFailed(com.nori.ai.gateway.core.port.InferenceClient.FailureReason.EMPTY_RESPONSE,null);
            for(var vector:vectors)for(float value:vector)if(!Float.isFinite(value))throw new com.nori.ai.gateway.core.port.InferenceClient.EmbeddingFailed(com.nori.ai.gateway.core.port.InferenceClient.FailureReason.EMPTY_RESPONSE,null);
            return index.search(scope,query,question,targets,vectors.getFirst(),vectors.getLast(),settings,state.get());
          }
        };
    }
    @Bean @org.springframework.context.annotation.Lazy
    com.nori.ai.gateway.core.knowledge.DocumentSynchronization documentSynchronization(com.nori.ai.gateway.core.knowledge.DocumentObjectStore objects,com.nori.ai.gateway.core.knowledge.DocumentExtractor extractor,com.nori.ai.gateway.core.knowledge.DocumentIndexStore store,com.nori.ai.gateway.core.knowledge.DocumentKeywordIndex keywords,com.nori.ai.gateway.core.port.InferenceClient inference) {
        return new com.nori.ai.gateway.core.knowledge.DocumentSynchronization(objects,extractor,store,keywords,inference);
    }
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
    @Bean com.nori.ai.gateway.core.result.KnowledgeDocumentStateReader knowledgeDocumentStateReader(org.springframework.beans.factory.ObjectProvider<com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex> index,org.springframework.core.env.Environment env) {
        return "enabled".equals(env.getProperty("ai.gateway.inference.mode"))?(permissions,keys)->index.getObject().states(permissions,keys):new com.nori.ai.gateway.core.result.FailClosedKnowledgeDocumentStateReader();
    }
    @Bean com.nori.ai.gateway.core.result.EvidenceSourceReader evidenceSourceReader() { return new com.nori.ai.gateway.core.result.FailClosedEvidenceSourceReader(); }
    @Bean com.nori.ai.gateway.core.expiry.ExpiryCheckpoint expiryCheckpoint() { return new com.nori.ai.gateway.core.expiry.ExpiryCheckpoint(); }
    @Bean com.nori.ai.gateway.core.sanitize.ContentSanitizer contentSanitizer() { return new com.nori.ai.gateway.core.sanitize.ContentSanitizer(); }
    @Bean com.nori.ai.gateway.core.audit.AuditFieldAllowlist auditFieldAllowlist(com.nori.ai.gateway.core.sanitize.ContentSanitizer sanitizer, CapabilityRegistry registry) { return new com.nori.ai.gateway.core.audit.AuditFieldAllowlist(sanitizer, registry); }
    @Bean com.nori.ai.gateway.core.job.GenerationSemaphore generationSemaphore(CapacityProfile capacity,org.springframework.core.env.Environment environment) { var settings=com.nori.ai.gateway.integration.inference.LlmSettings.load(environment); if(capacity.generationConcurrency()!=settings.generationConcurrency())throw new IllegalStateException("생성 동시성 설정이 다릅니다"); return new com.nori.ai.gateway.core.job.GenerationSemaphore(settings.generationConcurrency()); }
    @Bean com.nori.ai.gateway.core.budget.TokenBudgetPolicy tokenBudgetPolicy(org.springframework.core.env.Environment environment) { return com.nori.ai.gateway.integration.inference.LlmSettings.load(environment).policy(); }
    @Bean com.nori.ai.gateway.core.budget.PromptReducer promptReducer(com.nori.ai.gateway.core.budget.PromptTokenCounting counter, com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy) { return new com.nori.ai.gateway.core.budget.PromptReducer(counter, policy); }
    @Bean com.nori.ai.gateway.core.budget.PreCallBudgetAssert preCallBudgetAssert(com.nori.ai.gateway.core.budget.PromptTokenCounting counter, com.nori.ai.gateway.core.budget.TokenBudgetPolicy policy,Optional<com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter> tokenizer) {
        // 비활성·부분 컨텍스트에 tokenizer를 강제하지 않으며 구조화 호출에서 없는 계수기는 기존 guard가 거절합니다.
        return new com.nori.ai.gateway.core.budget.PreCallBudgetAssert(counter,policy,
            tokenizer.map(value->(com.nori.ai.gateway.core.llm.AnswerTokenCounting)value::countText).orElse(null));
    }
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
