package uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_COURT_LOG_ENTRY")
@NamedQuery(name = "XHB_COURT_LOG_ENTRY.findByCaseId",
    query = "SELECT o from XHB_COURT_LOG_ENTRY o WHERE o.caseId = :caseId "
        + "ORDER BY o.dateTime DESC, o.creationDate DESC")
public class XhbCourtLogEntryDao extends AbstractDao implements Serializable {

    private static final long serialVersionUID = -6788003970955114552L;

    @Id
    @GeneratedValue(generator = "xhb_court_log_entry_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_court_log_entry_seq", sequenceName = "xhb_court_log_entry_seq",
        allocationSize = 1)
    @Column(name = "ENTRY_ID")
    private Integer entryId;

    @Column(name = "CASE_ID")
    private Integer caseId;

    @Column(name = "DEFENDANT_ON_CASE_ID")
    private Integer defendantOnCaseId;

    @Column(name = "DEFENDANT_ON_OFFENCE_ID")
    private Integer defendantOnOffenceId;

    @Column(name = "SCHEDULED_HEARING_ID")
    private Integer scheduledHearingId;

    @Column(name = "EVENT_DESC_ID")
    private Integer eventDescId;

    @Column(name = "LOG_ENTRY_XML")
    private String logEntryXml;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "VERSION")
    private Integer version;

    public XhbCourtLogEntryDao() {
        super();
    }

    public XhbCourtLogEntryDao(XhbCourtLogEntryDao otherData) {
        this();
        setEntryId(otherData.getEntryId());
        setCaseId(otherData.getCaseId());
        setDefendantOnCaseId(otherData.getDefendantOnCaseId());
        setDefendantOnOffenceId(otherData.getDefendantOnOffenceId());
        setScheduledHearingId(otherData.getScheduledHearingId());
        setEventDescId(otherData.getEventDescId());
        setLogEntryXml(otherData.getLogEntryXml());
        setDateTime(otherData.getDateTime());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getEntryId();
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Integer getDefendantOnCaseId() {
        return defendantOnCaseId;
    }

    public void setDefendantOnCaseId(Integer defendantOnCaseId) {
        this.defendantOnCaseId = defendantOnCaseId;
    }

    public Integer getDefendantOnOffenceId() {
        return defendantOnOffenceId;
    }

    public void setDefendantOnOffenceId(Integer defendantOnOffenceId) {
        this.defendantOnOffenceId = defendantOnOffenceId;
    }

    public Integer getScheduledHearingId() {
        return scheduledHearingId;
    }

    public void setScheduledHearingId(Integer scheduledHearingId) {
        this.scheduledHearingId = scheduledHearingId;
    }

    public Integer getEventDescId() {
        return eventDescId;
    }

    public void setEventDescId(Integer eventDescId) {
        this.eventDescId = eventDescId;
    }

    public String getLogEntryXml() {
        return logEntryXml;
    }

    public void setLogEntryXml(String logEntryXml) {
        this.logEntryXml = logEntryXml;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

}
