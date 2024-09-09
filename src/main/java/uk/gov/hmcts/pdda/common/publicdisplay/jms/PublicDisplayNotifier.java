package uk.gov.hmcts.pdda.common.publicdisplay.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStoreFactory;

/**
 * PublicDisplayNotifier.
 * 
 * @author pznwc5
 * @version $Id: PublicDisplayNotifier.java,v 1.8 2006/06/05 12:28:24 bzjrnl Exp $
 */
public class PublicDisplayNotifier {

    private static final Logger LOG = LoggerFactory.getLogger(PublicDisplayNotifier.class);
    
    /** Event store to which the messages are pushed. */
    private EventStore eventStore;
    
    /**
     * Sends a public display event.
     * 
     * @param event Public display event
     */
    public void sendMessage(PublicDisplayEvent event) {
        LOG.debug("sendMessage()");
        
        LOG.debug(
            "Message Event: Type=" + event.getEventType() + " CourtId=" + event.getCourtId());
        
        if (eventStore == null) {
            eventStore = EventStoreFactory.getEventStore();
        }

        if (eventStore != null) {
            eventStore.pushEvent(event);
            LOG.debug("Event pushed to the event queue");
        }
    }
}
