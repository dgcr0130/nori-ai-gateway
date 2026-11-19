package com.nori.ai.gateway.app.job.recovery;

public interface ReadinessPrecondition {
    final class PreparationFailed extends RuntimeException {
        public PreparationFailed() { super("DOCUMENT_SYNC_FAILED",null,false,false); }
    }
    int order();
    void run();
}
