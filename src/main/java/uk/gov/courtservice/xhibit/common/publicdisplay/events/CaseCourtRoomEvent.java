package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;

/**
 * <p>
 * Title: Case Court Room Event.
 * </p>
 * 
 * <p>
 * Description: An event generated from a case, and therefore implicitly from a court room should
 * extend this class. It will contain the case Id and the court room Id from the parent
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
 * @version $Id: CaseCourtRoomEvent.java,v 1.4 2006/06/05 12:28:23 bzjrnl Exp $
 */
public abstract class CaseCourtRoomEvent extends CourtRoomEvent {

    static final long serialVersionUID = 669987900875089191L;

    private CaseChangeInformation caseChangeInformation;

    /**
     * Create an event that registers changes to a case within a court room.
     * 
     * @param courtRoomIdentifier CourtRoomIdentifier
     * @param caseChangeInfo CaseChangeInformation
     */
    protected CaseCourtRoomEvent(CourtRoomIdentifier courtRoomIdentifier,
        CaseChangeInformation caseChangeInfo) {
        super(courtRoomIdentifier);
        setCaseChangeInformation(caseChangeInfo);
    }

    /**
     * Set if the case is active in the specified court room.
     * 
     * @param caseActive true is case's public display is turned on in the court room
     */
    public void setCaseActive(boolean caseActive) {
        caseChangeInformation.setCaseActive(caseActive);
    }

    /**
     * Check if the case is active in the specified courtroom.
     * 
     * @return true is case's public display is turned on in the court room
     */
    public boolean isCaseActive() {
        return caseChangeInformation.isCaseActive();
    }

    /**
     * Set a new change for a new case.
     * 
     * @param caseChangeInformation CaseChangeInformation
     */
    private void setCaseChangeInformation(CaseChangeInformation caseChangeInformation) {
        this.caseChangeInformation = caseChangeInformation;
    }

    /**
     * Get the case change information.
     * 
     * @return CaseChangeInformation
     */
    public CaseChangeInformation getCaseChangeInformation() {
        return caseChangeInformation;
    }
}
