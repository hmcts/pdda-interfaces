package uk.gov.hmcts.framework.services.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Meeraj
 *
 *         Generic thread pool implementation. Original version written by R Oberg for JBoss.
 */
import java.util.Stack;


@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
public final class ThreadPool {

    /** Pool size. */
    private final int numWorkers;

    /** Timeout. * */
    private final long timeout;

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPool.class);

    /** Stack of workers in the pool. */
    private final Stack<Worker> workers = new Stack<>();

    /** Whether the pool is active. */
    private boolean active = true;

    /**
     * Initialize the pool with the number of workers.
     * 
     * @param numWorkers Number of workers
     */
    public ThreadPool(int numWorkers) {
        this(numWorkers, 60 * 1000L);
        for (int i = 0; i < numWorkers; i++) {
            Worker worker = getWorker(i);
            workers.push(worker);
            worker.start();
            LOG.debug("Worker started: {}", i);
        }
    }

    /**
     * Initialize the pool with the number of workers.
     * 
     * @param numWorkers Number of workers
     */
    public ThreadPool(int numWorkers, long timeout) {
        this.numWorkers = numWorkers;
        this.timeout = timeout;
    }

    /**
     * Method called by clients to schedule a work.
     * 
     * @param work Work that needs to be done
     */
    public void scheduleWork(Runnable work) {

        // Check whether the thread pool is active
        if (!active) {
            throw new ThreadPoolInactiveException();
        }

        Worker worker = getWorker();
        LOG.debug("Worker retieved: {}", worker.getId());
        worker.scheduleWork(work);
        LOG.debug("Work scheduled: {}", work);
    }

    /**
     * Shuts down the pool.
     * 
     */
    public void shutdown() {
        synchronized (this) {
            active = false;
    
            // Wait till or the workers are returned
            waitForPoolFull();
    
            while (!workers.isEmpty()) {
                Worker worker = workers.pop();
                LOG.debug("Worker shutting down: {}", worker.getId());
                worker.shutdown();
                // Wait till the worker dies
                while (worker.isAlive()) {
                    LOG.debug("Worker still alive - worker:{}", worker.getId());
                }
                LOG.debug("Worker shutdown: {}", worker.getId());
            }
            LOG.debug("Thread pool shutdown");
        }
    }

    /**
     * Returns the number of workers.
     * 
     * @return int
     */
    public int getNumFreeWorkers() {
        synchronized (this) {
            return workers.size();
        }
    }

    private Worker getWorker(int rowNo) {
        return new Worker(this, rowNo);
    }
    
    /**
     * Gets the worker.
     * 
     * @return Worker
     */
    private Worker getWorker() {
        synchronized (this) {
            try {
                LOG.debug("Start getWorker");
                while (workers.empty()) {
                    try {
                        LOG.debug("Waiting for workers");
                        wait(timeout);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new WorkerUnavailableException(ex);
                    }
                }
                return workers.pop();
            } finally {
                LOG.debug("End getWorker");
            }
        }
    }

    /**
     * Returns the worker back to the pool.
     * 
     * @param worker Worker
     */
    private void returnWorker(Worker worker) {
        synchronized (this) {
            try {
                LOG.debug("Start returnWorker");
                workers.push(worker);
                LOG.debug("Worker returned: {}", worker.getId());
                notifyAll();
            } finally {
                LOG.debug("End returnWorker");
            }
        }
    }

    /**
     * Wait till the pool is full.
     * 
     */
    private void waitForPoolFull() {
        // Wait till all the workers are back in the pool
        synchronized (this) {
            while (workers.size() != numWorkers) {
                try {
                    LOG.debug("Waiting for workers to be returned");
                    this.wait(timeout);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private class Worker extends Thread {

        /** Whether the worker is running. */
        private boolean running = true;

        /** Work that is being processed. */
        private Runnable work;

        /** Thread pool to which the worker belongs. */
        private final ThreadPool pool;

        /** Worker id. */
        private final int number;

        /** Initialises the worker as a daemon. */
        Worker(ThreadPool pool, int number) {
            super();
            this.pool = pool;
            this.number = number;
            setDaemon(true);
        }

        /** Kills the worker. */
        void shutdown() {
            synchronized (this) {
                running = false;
                notifyAll();
                LOG.debug("Notifying for shutdown - worker: {}", number);
            }
        }

        /** Schedules a work. */
        void scheduleWork(Runnable work) {
            synchronized (this) {
                if (work != null) {
                    this.work = work;
                    notifyAll();
                    LOG.debug("Work scheduled: {}", work);
                }
            }
        }

        /**
         * Worker runs while the pool is active.
         */
        @Override
        public void run() {
            while (running) {
                // Wait for the next work
                synchronized (this) {
                    while (work == null && running) {
                        try {
                            LOG.debug("Waiting for work - worker: {}", number);
                            this.wait(); // NOSONAR
                        } catch (InterruptedException ex) {
                            LOG.error(ex.getMessage(), ex);
                            Thread.currentThread().interrupt();
                        }
                    }
                    // Do the work
                    doWork();
                }
            }
            LOG.debug("Worker killed - worker:{}", number);
        }

        /**
         * Does the work.
         * 
         */
        private void doWork() {
            if (work == null) {
                return;
            }
            // Run the work
            try {
                LOG.debug("Starting work - worker:{}", number);
                work.run();
                LOG.debug("Work done - worker: {}", number);
            } catch (RuntimeException th) {
                // We don't want the worker to bomb out in case of an exception
                LOG.error(th.getMessage(), th);
            } finally {
                // Set the work to null
                setWork(null);

                // Return the worker to the pool
                pool.returnWorker(this);
            }

        }
        
        private void setWork(Runnable work) {
            this.work = work;
        }

    }

}
