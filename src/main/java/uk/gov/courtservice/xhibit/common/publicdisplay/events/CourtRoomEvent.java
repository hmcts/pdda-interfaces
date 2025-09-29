package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;

/**

 * Title: Court Room Event.

 * Description: An event that has been generated from a court room should extend this class

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: CourtRoomEvent.java,v 1.4 2006/06/05 12:28:23 bzjrnl Exp $
 */
public abstract class CourtRoomEvent implements PublicDisplayEvent {

    static final long serialVersionUID = 8330096616888630605L;

    private CourtRoomIdentifier courtRoomIdentifier;

    /**
     * Create with information to identify a court room and court.

     * @param courtRoomIdentifier CourtRoomIdentifier
     */
    protected CourtRoomEvent(CourtRoomIdentifier courtRoomIdentifier) {
        setCourtRoomIdentifier(courtRoomIdentifier);
    }

    /**
     * Return the court id directly from the court room identifier.

     * @return Court Id
     */
    @Override
    public Integer getCourtId() {
        if (courtRoomIdentifier == null) {
            return null;
        }
        return courtRoomIdentifier.getCourtId();
    }

    /**
     * Return the court room id directly from the court room identifier.

     * @return Court room Id
     */
    public Integer getCourtRoomId() {
        return courtRoomIdentifier.getCourtRoomId();
    }

    /**
     * Set a new court room identifier.

     * @param courtRoomIdentifier CourtRoomIdentifier
     */
    public final void setCourtRoomIdentifier(CourtRoomIdentifier courtRoomIdentifier) {
        this.courtRoomIdentifier = courtRoomIdentifier;
    }

    /**
     * Get the court room.

     * @return CourtRoomIdentifier
     */
    public CourtRoomIdentifier getCourtRoomIdentifier() {
        return courtRoomIdentifier;
    }
}
