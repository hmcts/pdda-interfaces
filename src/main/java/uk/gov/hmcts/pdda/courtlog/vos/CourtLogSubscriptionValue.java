package uk.gov.hmcts.pdda.courtlog.vos;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**

 * Title: CourtLogSubscriptionValue.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Joseph Babad
 * @version $Id: CourtLogSubscriptionValue.java,v 1.3 2004/04/22 06:51:49 tz0d5m Exp $
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
}
