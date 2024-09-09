package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**
 * <p>
 * Title: Move Case Event.
 * </p>
 * 
 * <p>
 * Description: This event is generated when a case is moved from one court room to another
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
 * @version $Id: MoveCaseEvent.java,v 1.3 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class MoveCaseEvent extends CaseCourtRoomEvent {

    static final long serialVersionUID = 1859463624187803916L;

    private CourtRoomIdentifier movedToCourtRoomIdentifier;

    /**
     * Supply the originating court room and court room it has moved to.
     * 
     * @param fromCourtRoomIdentifier The court the case moved from
     * @param toCourtRoomIdentifier The court the case moved to
     * @param caseChangeInformation The case effected
     */
    public MoveCaseEvent(CourtRoomIdentifier fromCourtRoomIdentifier,
        CourtRoomIdentifier toCourtRoomIdentifier, CaseChangeInformation caseChangeInformation) {
        super(fromCourtRoomIdentifier, caseChangeInformation);
        setToCourtRoomIdentifier(toCourtRoomIdentifier);
    }

    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.MOVE_CASE_EVENT);
    }

    /**
     * This stores the court room in the super class. Delegates method call to
     * setCourtRoomIdentifier.
     * 
     * @param courtRoomIdentifier A new originating court room
     */
    public void setFromCourtRoomIdentifier(CourtRoomIdentifier courtRoomIdentifier) {
        setCourtRoomIdentifier(courtRoomIdentifier);
    }

    /**
     * This gets the court room in the super class. Delegates method call to getCourtRoomId.
     * 
     * @return CourtRoom information of the originating court room
     */
    public CourtRoomIdentifier getFromCourtRoomIdentifier() {
        return getCourtRoomIdentifier();
    }

    /**
     * Set a new To court room.
     * 
     * @param courtRoomIdentifier A new destination court room
     */
    public final void setToCourtRoomIdentifier(CourtRoomIdentifier courtRoomIdentifier) {
        this.movedToCourtRoomIdentifier = courtRoomIdentifier;
    }

    /**
     * Get the To court room.
     * 
     * @return information of the destination court room
     */
    public CourtRoomIdentifier getToCourtRoomIdentifier() {
        return movedToCourtRoomIdentifier;
    }
}
