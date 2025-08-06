package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseCourtLogInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**

 * Title: Case Status Event.

 * Description: This event is generated when a court log event is created, editted or deleted.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: CaseStatusEvent.java,v 1.3 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class CaseStatusEvent extends CaseCourtRoomEvent {

    static final long serialVersionUID = -1865933124293611867L;

    private CaseCourtLogInformation caseCourtLogInformation;

    /**
     * Supply the court log value that has changed.

     * @param courtRoomIdentifier The court the change occured in
     * @param caseCourtLogInformation The case effected containing the court log subscription value
     */
    public CaseStatusEvent(CourtRoomIdentifier courtRoomIdentifier,
        CaseCourtLogInformation caseCourtLogInformation) {
        super(courtRoomIdentifier, caseCourtLogInformation);
        setCaseCourtLogInformation(caseCourtLogInformation);
    }

    /**
     * Set a new court log detail for a case.

     * @param caseCourtLogInformation The case effected containing the court log subscription value
     */
    private void setCaseCourtLogInformation(CaseCourtLogInformation caseCourtLogInformation) {
        this.caseCourtLogInformation = caseCourtLogInformation;
    }

    /**
     * Get the Case court log information.

     * @return CaseCourtLogInformation
     */
    public CaseCourtLogInformation getCaseCourtLogInformation() {
        return caseCourtLogInformation;
    }

    /**
     * getEventType.
     */
    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.CASE_STATUS_EVENT);
    }
}
