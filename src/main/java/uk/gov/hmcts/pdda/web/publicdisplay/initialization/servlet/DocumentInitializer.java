package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.framework.services.threadpool.ThreadPool;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.ProcessingInstance;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.work.EventWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performs initial document rendering.

 * @author pznwc5
 */
public class DocumentInitializer {

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(DocumentInitializer.class);

    /** Courts to be initialized. */
    private int[] courtIds;

    /** Number of workers to do the initialization. */
    private int numWorkers;

    /** Delay after each initialization. */
    private long delay;

    private ThreadPool threadPool;

    /**
     * Constructs the initializer.

     * @param courtIds Courts to be initialized
     * @param numWorkers Number of workers to do the initialization
     * @param delay Delay after each initialization
     */
    public DocumentInitializer(int[] courtIds, int numWorkers, long delay) {
        this.courtIds = courtIds.clone();
        this.delay = delay;
        this.numWorkers = numWorkers;
    }

    // Junit constructor
    public DocumentInitializer(int[] courtIds, int numWorkers, long delay, ThreadPool threadPool) {
        this(courtIds, numWorkers, delay);
        this.threadPool = threadPool;
    }

    /**
     * Performs the initialization.

     */
    public void initialize() {
        log.debug("initialize() - start");
        List<EventWork> eventWorkList = getEventWorkList();
        log.debug("EventWork to schedule: {}", eventWorkList.size());
        Map<Integer, List<EventWork>> eventBatchMap = getEventBatchMap(eventWorkList);

        for (Map.Entry<Integer, List<EventWork>> entry : eventBatchMap.entrySet()) {
            Integer batch = entry.getKey();
            List<EventWork> eventBatchList = entry.getValue();
            log.debug("Batch: {}, Events: {}", batch, eventBatchList.size());
            scheduleEventWork(eventBatchList);
        }
        log.debug("initialize() - finished");
    }

    private Map<Integer, List<EventWork>> getEventBatchMap(List<EventWork> eventWorkList) {
        Map<Integer, List<EventWork>> results = new ConcurrentHashMap<>();
        Integer batchNo = 0;
        for (EventWork event : eventWorkList) {
            List<EventWork> eventBatchList = null;
            // Determine if the batch exists yet and/or is full
            if (results.containsKey(batchNo)) {
                eventBatchList = results.get(batchNo);
                if (eventBatchList.size() >= numWorkers) {
                    batchNo += 1;
                    eventBatchList = getNewEventWorkArray();
                }
            }
            // Initialise the new eventBatchList
            if (eventBatchList == null) {
                eventBatchList = getNewEventWorkArray();
            }
            // Add the event to the eventBatchList
            eventBatchList.add(event);
            // Store the eventBatchList against the batchNo
            results.put(batchNo, eventBatchList);
        }
        log.debug("Batches to schedule: {}", results.size());
        return results;
    }
    
    private List<EventWork> getNewEventWorkArray() {
        return new ArrayList<>();
    }

    private List<EventWork> getEventWorkList() {
        List<EventWork> results = new ArrayList<>();
        for (int courtId : courtIds) {
            log.debug("initialize CourtId: {}", courtId);
            // Create the court configuration change
            CourtConfigurationChange change = getCourtConfigurationChange(courtId);
            log.debug("Change: {}", change);

            // Create the event
            ConfigurationChangeEvent event = getConfigurationChangeEvent(change);
            log.debug("Event: {}", event);

            // Create the work
            EventWork work = getEventWork(event, delay, ProcessingInstance.STARTUP);
            log.debug("Work: {}", work);
            results.add(work);
        }
        return results;
    }
    
    private EventWork getEventWork(PublicDisplayEvent event, long delay, ProcessingInstance processingInstance) {
        return new EventWork(event, delay, processingInstance);
    }
    
    private CourtConfigurationChange getCourtConfigurationChange(int courtId) {
        return new CourtConfigurationChange(courtId, true);
    }
    
    private ConfigurationChangeEvent getConfigurationChangeEvent(CourtConfigurationChange change) {
        return new ConfigurationChangeEvent(change);
    }

    private void scheduleEventWork(List<EventWork> events) {
        // Create the thread pool
        ThreadPool pool = getThreadPool();
        try {
            for (EventWork work : events) {
                // Schedule the work
                pool.scheduleWork(work);
                log.debug("Work scheduled ....");
            }
        } finally {
            // Shutdown the thread pool
            pool.shutdown();
            log.info("Thread pool shutdown");
        }
    }

    private ThreadPool getThreadPool() {
        if (threadPool == null) {
            return new ThreadPool(numWorkers);
        }
        return threadPool;
    }
}
