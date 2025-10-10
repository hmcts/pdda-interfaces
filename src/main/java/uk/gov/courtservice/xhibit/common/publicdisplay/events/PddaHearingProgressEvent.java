package uk.gov.courtservice.xhibit.common.publicdisplay.events;

/**

 * Title: Pdda Hearing Progress Event.

 * Description: The abstract class for scheduled hearing event status updates sent from xhibit to PDDA.

 * Copyright: Copyright (c) 2025

 * Company: CGI

 * @author Luke Gittins
 */

@SuppressWarnings("PMD.LinguisticNaming")
public abstract class PddaHearingProgressEvent implements PublicDisplayEvent {
    
    private static final long serialVersionUID = -5224832990492817778L;
    
    private Integer hearingProgressIndicator;
    private Integer caseNumber;
    private String caseType;
    private String courtRoomName;
    private String courtName;
    private String isCaseActive;


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

    public String getCourtRoomName() {
        return courtRoomName;
    }

    public void setCourtRoomName(String courtRoomName) {
        this.courtRoomName = courtRoomName;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }
    
    public String getIsCaseActive() {
        return isCaseActive;
    }
    
    public void setIsCaseActive(String isCaseActive) {
        this.isCaseActive = isCaseActive;
    }
}
