package org.example.dto;

import lombok.Getter;
import org.example.model.Site;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class IndexingThread {
    @Getter
    Site site;
    Thread thread;
    ForkJoinPool forkJoinPool;

    public IndexingThread(Site site, Thread thread, ForkJoinPool forkJoinPool) {
        this.site = site;
        this.thread = thread;
        this.forkJoinPool = forkJoinPool;
    }

    public void stop() {
        forkJoinPool.shutdownNow();

        try {
            forkJoinPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.interrupt();
    }
}
