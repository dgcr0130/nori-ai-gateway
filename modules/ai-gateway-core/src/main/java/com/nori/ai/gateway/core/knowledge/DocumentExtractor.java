package com.nori.ai.gateway.core.knowledge;

public interface DocumentExtractor {
    DocumentCorpus.Document extract(DocumentCorpus.Source source, String key, DocumentObjectStore.Content content, DocumentCorpus.Limits limits);
}
