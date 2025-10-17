package uk.gov.courtservice.xhibit.courtlog.vos;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**

 * Title: CourtLogSubscriptionValue.


 * Description: This is the instance of CourtLogSubscriptionValue using the xhibit path rather than the pdda path.


 * Copyright: Copyright (c) 2025


 * Company: CGI

 * @author Luke Gittins
 */
public class CourtLogSubscriptionValue extends CsAbstractValue {

    private static final long serialVersionUID = 7343285881258104531L;

    private CourtLogViewValue courtLogViewValue;

    private Integer hearingId;

    private Integer courtSiteId;

    private Integer pnEventType;

    private Integer courtRoomId;

    private String courtUrn;

    public CourtLogSubscriptionValue() {
        super();
    }

    public CourtLogSubscriptionValue(CourtLogViewValue courtLogViewValue) {
        this();
        this.courtLogViewValue = courtLogViewValue;
    }

    public Integer getCourtSiteId() {
        return this.courtSiteId;
    }

    public void setCourtSiteId(Integer courtSiteId) {
        this.courtSiteId = courtSiteId;
    }

    public void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    public Integer getCourtRoomId() {
        return this.courtRoomId;
    }

    public void setHearingId(Integer hearingId) {
        this.hearingId = hearingId;
    }

    public Integer getHearingId() {
        return hearingId;
    }

    public void setCourtLogViewValue(CourtLogViewValue courtLogViewValue) {
        this.courtLogViewValue = courtLogViewValue;
    }

    public CourtLogViewValue getCourtLogViewValue() {
        return this.courtLogViewValue;
    }

    public void setPnEventType(Integer pnEventType) {
        this.pnEventType = pnEventType;
    }

    public Integer getPnEventType() {
        return this.pnEventType;
    }

    public void setCourtUrn(String courtUrn) {
        this.courtUrn = courtUrn;
    }

    public String getCourtUrn() {
        return this.courtUrn;
    }

    public Integer getDefendantOnCaseId() {
        return courtLogViewValue != null ? courtLogViewValue.getDefendantOnCaseId() : null;
    }

    public Integer getDefendantOnOffenceId() {
        return courtLogViewValue != null ? courtLogViewValue.getDefendantOnOffenceId() : null;
    }

    public Integer getScheduledHearingId() {
        return courtLogViewValue != null ? courtLogViewValue.getScheduledHearingId() : null;
    }
    
    @Override
    @SuppressWarnings("PMD")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CourtLogSubscriptionValue[");
        sb.append("hearingId=").append(hearingId);
        sb.append(", courtSiteId=").append(courtSiteId);
        sb.append(", courtRoomId=").append(courtRoomId);
        sb.append(", pnEventType=").append(pnEventType);
        sb.append(", courtUrn=").append(courtUrn);
        sb.append(", courtLogViewValue=").append(courtLogViewValue);
        sb.append("]");
        return sb.toString();
    }
}
