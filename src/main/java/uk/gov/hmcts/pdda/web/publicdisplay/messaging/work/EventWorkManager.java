package uk.gov.hmcts.pdda.web.publicdisplay.messaging.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.framework.services.threadpool.ThreadPool;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;

/**
 * The class is responsible for pulling an event from the event store and assigning it to the thread
 * pool. If we need event coalesce, set the num workers to one.
 * 
 * @author pznwc5
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class EventWorkManager extends Thread {

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(EventWorkManager.class);

    /** Whether the work manager is active. */
    private boolean active = true;

    /** Event store that is managed. */
    private EventStore eventStore;

    /** Thread pool used by the event manager. */
    private ThreadPool threadPool;

    // Sleep a little if no event is available
    private static final long IDLE_SLEEP_MS = 1000L; // 1 second

    /**
     * If we need event coalesce, set the num workers to one.
     * 
     * @param eventStore Event store that is used
     * @param numWorkers Number of worker threads used by the event manager
     */
    public EventWorkManager(EventStore eventStore, int numWorkers) {
        this(eventStore, new ThreadPool(numWorkers));
        log.debug("Number of workers: {}", numWorkers);
    }

    // Used for unit tests
    public EventWorkManager(EventStore eventStore, ThreadPool threadPool) {
        super();
        this.eventStore = eventStore;
        this.threadPool = threadPool;
        log.debug("Event store: {}", eventStore);
    }

    /**
     * Shuts down the work manager.
     * 
     */
    public void shutDown() {
        active = false;
        threadPool.shutdown();
    }

    /**
     * Processes the events.
     */
    @Override
    public void run() {
        // This is not a continuously running loop as it does a wait on the event
        // store
        while (active) {
            runOnce();
        }

        log.debug("Event work manager shutdown.");
    }

    public void runOnce() {
        PublicDisplayEvent event = eventStore.popEvent();
        if (event != null) {
            log.debug("Event received: {}", event);
            threadPool.scheduleWork(new EventWork(event));
            log.debug("Event processed: {}", event);
        } else {
            // No event received, back off a bit to avoid tight loop
            try {
                log.debug("No event available. Sleeping for {} ms.", IDLE_SLEEP_MS);
                sleep(IDLE_SLEEP_MS);
            } catch (InterruptedException e) {
                log.error("EventWorkManager interrupted during idle sleep.", e);
                currentThread().interrupt(); // Preserve interrupt status
            }
        }
    }

    /*
     * public void runOnce() { PublicDisplayEvent event = eventStore.popEvent(); if (event != null)
     * { log.debug("Event received: {}", event); threadPool.scheduleWork(new EventWork(event));
     * log.debug("Event processed: {}", event); } }
     */
}
