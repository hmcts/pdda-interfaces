package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

import java.util.LinkedList;
import java.util.List;

/**
 *         A FIFO implementation of event queue.
 *         
 * @author meekun
 */
@SuppressWarnings({"PMD.AvoidSynchronizedStatement","PMD.DoNotUseThreads"})
public class DefaultEventStore implements EventStore {

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(DefaultEventStore.class);

    /** Queue of events. */
    private final List<PublicDisplayEvent> events = new LinkedList<>();

    // Let's set a 5 second timeout
    private static final long WAIT_TIMEOUT_MS = 5000L;

    /**
     * Pushes an event to the queue.
     * 
     * @param event Event to be pushed into the queue
     */
    @Override
    public void pushEvent(PublicDisplayEvent event) {
        synchronized (this) {
            ((LinkedList<PublicDisplayEvent>) events).addLast(event);
            // Notify the waiting thread when an event arrives
            notifyAll();
            log.debug("Pushed event to the queue: {}", event);
        }
    }

    /**
     * Pops an event from the queue.
     * 
     * @return Next event in the queue
     */
    @Override
    public PublicDisplayEvent popEvent() {
        synchronized (this) {
            while (events.isEmpty()) {
                try {
                    log.debug("Event queue empty, waiting up to {} ms for new event...",
                        WAIT_TIMEOUT_MS);
                    wait(WAIT_TIMEOUT_MS);
                    // Check if the queue is still empty after waiting
                    if (events.isEmpty()) {
                        log.warn("No events arrived after waiting {} ms, returning null.",
                            WAIT_TIMEOUT_MS);
                        return null; // instead of waiting forever
                    }
                } catch (InterruptedException ex) {
                    log.error("Thread interrupted while waiting for event.", ex);
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
    
            // Pop the event of the queue
            PublicDisplayEvent event = ((LinkedList<PublicDisplayEvent>) events).getFirst();
            events.remove(0);
            log.debug("Popped event into the queue: {}", event);
    
            return event;
        }
    }

}
