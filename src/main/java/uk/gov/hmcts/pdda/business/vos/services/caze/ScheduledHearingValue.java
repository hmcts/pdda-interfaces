package uk.gov.hmcts.pdda.business.vos.services.caze;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

import java.time.LocalDateTime;

/**
 * <p>
 * Title: ScheduledHearingValue.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Sarah Tong
 * @version 1.0
 */

public class ScheduledHearingValue extends CsAbstractValue {
    private Integer scheduledHearingID;

    private LocalDateTime scheduledHearingDate;
    private static final long serialVersionUID = 4378160364569859576L;

    private String caseActive;

    public LocalDateTime getScheduledHearingDate() {
        return scheduledHearingDate;
    }

    public Integer getScheduledHearingID() {
        return scheduledHearingID;
    }

    public void setScheduledHearingID(Integer scheduledHearingID) {
        this.scheduledHearingID = scheduledHearingID;
    }

    public void setScheduledHearingDate(LocalDateTime scheduledHearingDate) {
        this.scheduledHearingDate = scheduledHearingDate;
    }

    public String getCaseActive() {
        return caseActive;
    }

    public void setCaseActive(String caseActive) {
        this.caseActive = caseActive;
    }
}
