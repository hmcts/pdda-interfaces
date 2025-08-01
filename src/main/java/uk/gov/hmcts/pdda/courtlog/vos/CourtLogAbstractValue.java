package uk.gov.hmcts.pdda.courtlog.vos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

import java.util.Date;

/**

 * Title: CourtLogAbstractValue.


 * Description: Value Object containing Court Log event information


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Joseph Babad / Paul Fitton
 * @version $Revision: 1.6 $
 */
public abstract class CourtLogAbstractValue extends CsAbstractValue implements Cloneable, Comparable<Object> {

    private static final long serialVersionUID = -3170263930219936328L;

    private static final Logger LOG = LoggerFactory.getLogger(CourtLogAbstractValue.class);

    /** The entry id in the log file/store. */
    private Long logEntryId;

    /** The event type. Every Court Log entry has an event type. */
    private Integer eventType;

    /** The date of the entry. */
    private Date entryDate;

    private Date lastUpdateDate;

    /** The Case ID that this entry was made against. */
    private Integer caseId;

    // added for use in CJSE Event creation
    private Integer defendantOnCaseId;

    private Integer defendantOnOffenceId;

    private Integer scheduledHearingId;

    protected CourtLogAbstractValue() {
        super();
    }

    protected CourtLogAbstractValue(Integer version) {
        super(version);
    }

    /**
     * Set the entry's case Id.

     * @param caseId Integer representing the relevant Case Id.
     */
    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    /**
     * Get the entry's case Id.

     * @return Integer
     */
    public Integer getCaseId() {
        return this.caseId;
    }

    /**
     * Get the defendant id for defendant level events.

     * @return Integer
     */
    public Integer getDefendantOnCaseId() {
        return this.defendantOnCaseId;
    }

    /**
     * Get the DefendantOnOffence id for CRN level events.

     * @return Integer
     */
    public Integer getDefendantOnOffenceId() {
        return this.defendantOnOffenceId;
    }

    /**
     * Set the DefendantOnOffence id for CRN level events.

     * @param defendantOnOffenceId Integer
     */
    public void setDefendantOnOffenceId(Integer defendantOnOffenceId) {
        this.defendantOnOffenceId = defendantOnOffenceId;
    }

    /**
     * Set the Defendant id for CRN level events.

     * @param defendantOnCaseId Integer
     */
    public void setDefendantOnCaseId(Integer defendantOnCaseId) {
        this.defendantOnCaseId = defendantOnCaseId;
    }

    /**
     * Set the scheduled hearing id.
     */
    public void setScheduledHearingId(Integer scheduledHearingId) {
        this.scheduledHearingId = scheduledHearingId;
    }

    /**
     * Get the scheduled hearing id.

     * @return Integer the scheduled hearing id
     */
    public Integer getScheduledHearingId() {
        return scheduledHearingId;
    }

    /**
     * Sets the event type for the entry.

     * @param eventType One of a set of known vos.
     */
    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    /**
     * Get the event type of the entry.

     * @return Integer
     */
    public Integer getEventType() {
        return this.eventType;
    }

    /**
     * Set the date and time.

     * @param date Date
     */
    public void setEntryDate(Date date) {
        this.entryDate = date;
    }

    /**
     * Get the entry date.

     * @return Date
     */
    public Date getEntryDate() {
        return this.entryDate;
    }

    public void setLogEntryId(Long logEntryId) {
        this.logEntryId = logEntryId;
    }

    public Long getLogEntryId() {
        return this.logEntryId;
    }

    // Override the getId
    @Override
    public Integer getId() {
        return logEntryId != null ? logEntryId.intValue() : null;
    }

    // Get the update date
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    /**
     * Implemented method of the <code>Comparable</code> interface, used to allow sorting of the court
     * value objects. By default the value objects are sorted in an ascending order by the entry date.

     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * @see #getEntryDate()
     */
    @Override
    public int compareTo(Object obj) {
        return getEntryDate().compareTo(((CourtLogAbstractValue) obj).getEntryDate());
    }

    @Override
    public boolean equals(Object obj) {
        LOG.debug("equals()");
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
