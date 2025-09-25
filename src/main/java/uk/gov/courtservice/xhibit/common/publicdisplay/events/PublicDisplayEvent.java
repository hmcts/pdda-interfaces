package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

import java.io.Serializable;

/**

 * Title: Public Display Event.

 * Description: The super class for all events to be sent to the public display.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: PublicDisplayEvent.java,v 1.4 2006/06/05 12:28:23 bzjrnl Exp $
 */
public interface PublicDisplayEvent extends Serializable {
    /**
     * Returns an object that uniquely identifies the event.
     * @return EventType
     */
    EventType getEventType();

    /**
     * Get the court ID for this event.
     * @return the court Id for this event.
     */
    Integer getCourtId();
    
}
