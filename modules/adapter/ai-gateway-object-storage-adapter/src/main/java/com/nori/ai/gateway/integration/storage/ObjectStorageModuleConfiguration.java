package com.nori.ai.gateway.integration.storage;

import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import org.springframework.context.annotation.*;

@Configuration(proxyBeanMethods = false)
public class ObjectStorageModuleConfiguration {
    @Bean MarkdownDocumentExtractor markdownDocumentExtractor(ContentSanitizer sanitizer) { return new MarkdownDocumentExtractor(sanitizer); }
}
