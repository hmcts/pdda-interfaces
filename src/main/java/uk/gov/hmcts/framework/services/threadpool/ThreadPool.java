package uk.gov.hmcts.framework.services.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Meeraj
 *
 *         Generic thread pool implementation. Original version written by R Oberg for JBoss.
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement", "PMD.LooseCoupling",
    "PMD.ConfusingTernary"})
public final class ThreadPool {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPool.class);

    /** Pool size. */
    private final int numWorkers;

    /** Timeout. * */
    private final long timeout;

    private final ExecutorService executor;

    public ThreadPool(int numWorkers) {
        this(numWorkers, 60 * 1000L); // Default timeout 1 minute, just like old code
    }

    public ThreadPool(int numWorkers, long timeout) {
        this.numWorkers = numWorkers;
        this.timeout = timeout;
        this.executor = Executors.newFixedThreadPool(numWorkers, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true); // Important to match old behaviour
            return thread;
        });
        LOG.info("ThreadPool created with {} workers and timeout {} ms", numWorkers, timeout);
    }

    public void scheduleWork(Runnable work) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("ThreadPool is shut down");
        }
        executor.submit(work);
    }

    public void shutdown() {
        LOG.info("Shutting down ThreadPool with {} workers", numWorkers);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                LOG.warn("Forcing shutdown after timeout of {} ms", timeout);
                executor.shutdownNow();
            } else {
                LOG.info("ThreadPool shutdown gracefully.");
            }
        } catch (InterruptedException e) {
            LOG.error("Shutdown interrupted", e);
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }

    public int getNumFreeWorkers() {
        // This is a rough estimate since standard Executors don't expose "free" worker counts
        // but it matches old expectations (sort of).
        if (executor.isShutdown()) {
            return 0;
        }
        return numWorkers; // You have a fixed pool, assume all workers are ready if idle
    }

}
