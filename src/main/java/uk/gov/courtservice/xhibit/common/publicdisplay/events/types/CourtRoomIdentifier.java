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
@SuppressWarnings("PMD")
public class CourtRoomIdentifier implements Serializable {

    static final long serialVersionUID = -7579306166442219719L;

    private Integer courtId;

    private Integer courtRoomId;
    
    private String courtName;
    
    private Integer courtRoomNo;

    /**
     * Create the object using the court id and court room id.
     * @param courtId identifier from XHB_COURT
     * @param courtRoomId identifier from XHB_COURT_ROOM
     */
    public CourtRoomIdentifier(Integer courtId, Integer courtRoomId, String courtName, Integer courtRoomNo) {
        setCourtId(courtId);
        setCourtRoomId(courtRoomId);
        setCourtName(courtName);
        setCourtRoomNo(courtRoomNo);
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
    
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }
    
    public String getCourtName() {
        return courtName;
    }
    
    public void setCourtRoomNo(Integer courtRoomNo) {
        this.courtRoomNo = courtRoomNo;
    }
    
    public Integer getCourtRoomNo() {
        return courtRoomNo;
    }
    

    @Override
    public String toString() {
        return "CourtRoomIdentifier{"
            + "courtId=" + courtId
            + ", courtRoomId=" + courtRoomId
            + ", courtName='" + courtName + '\''
            + ", courtRoomNo=" + courtRoomNo
            + '}';
    }
    
}
