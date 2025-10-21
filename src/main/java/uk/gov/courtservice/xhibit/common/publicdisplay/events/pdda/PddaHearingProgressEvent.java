package uk.gov.courtservice.xhibit.common.publicdisplay.events.pdda;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**

 * Title: Pdda Hearing Progress Event.

 * Description: The abstract class for scheduled hearing event status updates sent from xhibit to PDDA.

 * Copyright: Copyright (c) 2025

 * Company: CGI

 * @author Luke Gittins
 */

@SuppressWarnings({"PMD.LinguisticNaming", "PMD.UnnecessaryConstructor"})
public class PddaHearingProgressEvent implements PublicDisplayEvent {
    
    private static final long serialVersionUID = -8013651434648029138L;
    
    private Integer hearingProgressIndicator;
    private Integer caseNumber;
    private String caseType;
    private Integer courtId;
    private String courtName;
    private String courtRoomName;
    private String isCaseActive;

    // Used for JUnit testing
    public PddaHearingProgressEvent() {
        super();
    }
    
    public Integer getHearingProgressIndicator() {
        return hearingProgressIndicator;
    }

    public void setHearingProgressIndicator(Integer hearingProgressIndicator) {
        this.hearingProgressIndicator = hearingProgressIndicator;
    }

    public Integer getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Integer caseNumber) {
        this.caseNumber = caseNumber;
    }
    
    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
    
    @Override
    public Integer getCourtId() {
        return courtId;
    }
    
    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }
    
    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }
    
    public String getCourtRoomName() {
        return courtRoomName;
    }

    public void setCourtRoomName(String courtRoomName) {
        this.courtRoomName = courtRoomName;
    }

    public String getIsCaseActive() {
        return isCaseActive;
    }
    
    public void setIsCaseActive(String isCaseActive) {
        this.isCaseActive = isCaseActive;
    }

    @Override
    public EventType getEventType() {
        return null;
    }
}
