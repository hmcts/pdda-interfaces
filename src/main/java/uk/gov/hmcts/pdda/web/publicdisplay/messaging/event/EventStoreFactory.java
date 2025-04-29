package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.CsServices;

/**
 * Factory that returns a singleton EventStore.
 */
public final class EventStoreFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EventStoreFactory.class);

    private static final String EVENT_STORE_TYPE = "public.display.event.store.type";

    private static final EventStore EVENT_STORE = createEventStore();

    private EventStoreFactory() {
        // Prevent instantiation
    }

    /**
     * Gets the singleton EventStore instance.
     *
     * @return EventStore instance
     */
    public static EventStore getEventStore() {
        return EVENT_STORE;
    }

    private static EventStore createEventStore() {
        String eventStoreClass = CsServices.getConfigServices().getProperty(EVENT_STORE_TYPE);
        EventStore eventStore;

        if (eventStoreClass == null) {
            // Default fallback
            eventStore = new DefaultEventStore();
        } else {
            try {
                eventStore = (EventStore) Class.forName(eventStoreClass).getDeclaredConstructor()
                    .newInstance();
            } catch (ReflectiveOperationException e) {
                LOG.error("Failed to create EventStore: {}", eventStoreClass, e);
                throw new EventStoreException(eventStoreClass, e);
            }
        }

        LOG.info("Event store type created: {}", eventStore.getClass().getName());
        return eventStore;
    }
}
