package com.nori.ai.gateway.core.job;

import java.util.concurrent.Semaphore;

public final class GenerationSemaphore {
    private final Semaphore semaphore;
    public GenerationSemaphore(int permits) {
        if (permits < 1) throw new IllegalArgumentException("생성 동시성은 양수여야 합니다");
        semaphore = new Semaphore(permits,true);
    }
    public void acquire() throws InterruptedException { semaphore.acquire(); }
    public boolean acquire(java.time.Duration timeout) throws InterruptedException { return semaphore.tryAcquire(timeout.toNanos(), java.util.concurrent.TimeUnit.NANOSECONDS); }
    public void release() { semaphore.release(); }
    public int availablePermits() { return semaphore.availablePermits(); }
}
