package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**
 * <p>
 * Title: Update Case Event.
 * </p>
 * 
 * <p>
 * Description: This event is created when case information is updated.<br>
 * Currently applies to the judge name changed and defendant maintenance.
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
 * @version $Id: UpdateCaseEvent.java,v 1.4 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class UpdateCaseEvent extends CaseCourtRoomEvent {

    static final long serialVersionUID = 1565648975540285373L;

    /**
     * Specify the case that has been modified and the court room in which it happened.
     * 
     * @param courtRoomIdentifier The court the change occured in
     * @param caseChangeInformation The case effected
     */
    public UpdateCaseEvent(CourtRoomIdentifier courtRoomIdentifier,
        CaseChangeInformation caseChangeInformation) {
        super(courtRoomIdentifier, caseChangeInformation);
    }

    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.UPDATE_CASE_EVENT);
    }
}
