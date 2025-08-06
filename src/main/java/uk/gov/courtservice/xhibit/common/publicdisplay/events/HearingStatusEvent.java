package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**

 * Title: Hearing Status Event.

 * Description: This event is generated when a cases status changes. For example, From 'To be heard'
 * to 'In progress'.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: HearingStatusEvent.java,v 1.3 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class HearingStatusEvent extends CaseCourtRoomEvent {

    static final long serialVersionUID = -3368435243057764616L;

    /**
     * Specify the case that whos status has change and in which court room.

     * @param courtRoomIdentifier The court the change occured in
     * @param caseChangeInformation The case effected
     */
    public HearingStatusEvent(CourtRoomIdentifier courtRoomIdentifier,
        CaseChangeInformation caseChangeInformation) {
        super(courtRoomIdentifier, caseChangeInformation);
    }

    /**
     * getEventType.

     * @return EventType
     */
    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.HEARING_STATUS_EVENT);
    }
}
