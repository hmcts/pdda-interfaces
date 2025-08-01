package uk.gov.courtservice.xhibit.common.publicdisplay.events.types;

import java.io.Serializable;

/**

 * Title: Court Room Identifier.

 * Description: This class holds the court id and court room id

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: CourtRoomIdentifier.java,v 1.4 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class CourtRoomIdentifier implements Serializable {

    static final long serialVersionUID = -7579306166442219719L;

    private Integer courtId;

    private Integer courtRoomId;

    /**
     * Create the object using the court id and court room id.

     * @param courtId identifier from XHB_COURT
     * @param courtRoomId identifier from XHB_COURT_ROOM
     */
    public CourtRoomIdentifier(Integer courtId, Integer courtRoomId) {
        setCourtId(courtId);
        setCourtRoomId(courtRoomId);
    }

    /**
     * Set a new court Id.

     * @param courtId identifier from XHB_COURT
     */
    public final void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    /**
     * Get the court Id.

     * @return court Id
     */
    public Integer getCourtId() {
        return courtId;
    }

    /**
     * Set a new court roomId.

     * @param courtRoomId identifier from XHB_COURT_ROOM
     */
    public final void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    /**
     * Get the court room Id.

     * @return court room Id
     */
    public Integer getCourtRoomId() {
        return courtRoomId;
    }
}
