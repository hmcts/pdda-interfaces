package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**
 * <p>
 * Title: Add Case Event.
 * </p>
 * 
 * <p>
 * Description: This event is generated when a case is added to the todays schedule.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: AddCaseEvent.java,v 1.3 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class AddCaseEvent extends CaseCourtRoomEvent {

    static final long serialVersionUID = -5166643332732837063L;

    /**
     * Specify the case that has been added and to which court room.
     * 
     * @param courtRoomIdentifier The court the change occured in
     * @param caseChangeInformation The case effected
     */
    public AddCaseEvent(CourtRoomIdentifier courtRoomIdentifier,
        CaseChangeInformation caseChangeInformation) {
        super(courtRoomIdentifier, caseChangeInformation);
    }

    /**
     * getEventType.
     * 
     * @return EventType
     */
    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.ADD_CASE_EVENT);
    }
}
