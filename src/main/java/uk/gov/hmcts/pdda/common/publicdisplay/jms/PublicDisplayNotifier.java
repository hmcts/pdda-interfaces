package uk.gov.hmcts.pdda.common.publicdisplay.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStoreFactory;

public class PublicDisplayNotifier {

    private static final Logger LOG = LoggerFactory.getLogger(PublicDisplayNotifier.class);

    private final EventStore eventStore;

    // Default constructor
    public PublicDisplayNotifier() {
        this(EventStoreFactory.getEventStore());
    }

    // Testable constructor
    public PublicDisplayNotifier(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void sendMessage(PublicDisplayEvent event) {
        LOG.debug("sendMessage() - eventType={}, courtId={}", event.getEventType(),
            event.getCourtId());

        if (eventStore != null) {
            eventStore.pushEvent(event);
            LOG.debug("Event pushed to the queue");
        } else {
            LOG.error("EventStore is not available - cannot send message.");
            throw new IllegalStateException("EventStore not initialized");
        }
    }
}
