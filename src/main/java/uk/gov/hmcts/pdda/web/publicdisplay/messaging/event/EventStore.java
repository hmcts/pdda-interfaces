package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

/**
 * The interface that should be implemented by event stores.

 * @author meekun
 */
public interface EventStore {
    /**
     * Pushed an event into the queue.

     * @param event Event that is pushed
     */
    void pushEvent(PublicDisplayEvent event);

    /**
     * Pos an event from the queue.

     * @return Event that is popped
     */
    PublicDisplayEvent popEvent();
}
