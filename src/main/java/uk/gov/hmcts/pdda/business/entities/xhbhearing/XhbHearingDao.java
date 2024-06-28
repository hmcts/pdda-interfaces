package uk.gov.hmcts.pdda.business.entities.xhbhearing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_HEARING")
@NamedQuery(name = "XHB_HEARING.findByCaseId",
    query = "SELECT o from XHB_HEARING o WHERE o.caseId = :caseId")
public class XhbHearingDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6844793990175522946L;

    @Id
    @GeneratedValue(generator = "xhb_hearing_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_hearing_seq", sequenceName = "xhb_hearing_seq",
        allocationSize = 1)
    @Column(name = "HEARING_ID")
    private Integer hearingId;

    @Column(name = "CASE_ID")
    private Integer caseId;

    @Column(name = "REF_HEARING_TYPE_ID")
    private Integer refHearingTypeId;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "MP_HEARING_TYPE")
    private String mpHearingType;

    @Column(name = "LAST_CALCULATED_DURATION")
    private Double lastCalculatedDuration;

    @Column(name = "HEARING_START_DATE")
    private LocalDateTime hearingStartDate;

    @Column(name = "HEARING_END_DATE")
    private LocalDateTime hearingEndDate;

    @Column(name = "LINKED_HEARING_ID")
    private Integer linkedHearingId;

    // Non-db columns
    @jakarta.persistence.Transient
    private Collection<XhbScheduledHearingDao> scheduledHearings;

    public XhbHearingDao() {
        super();
    }

    public XhbHearingDao(XhbHearingDao otherData) {
        this();
        setHearingId(otherData.getHearingId());
        setCaseId(otherData.getCaseId());
        setRefHearingTypeId(otherData.getRefHearingTypeId());
        setCourtId(otherData.getCourtId());
        setMpHearingType(otherData.getMpHearingType());
        setLastCalculatedDuration(otherData.getLastCalculatedDuration());
        setHearingStartDate(otherData.getHearingStartDate());
        setHearingEndDate(otherData.getHearingEndDate());
        setLinkedHearingId(otherData.getLinkedHearingId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getHearingId();
    }

    public Integer getHearingId() {
        return hearingId;
    }

    public void setHearingId(Integer hearingId) {
        this.hearingId = hearingId;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Integer getRefHearingTypeId() {
        return refHearingTypeId;
    }

    public void setRefHearingTypeId(Integer refHearingTypeId) {
        this.refHearingTypeId = refHearingTypeId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getMpHearingType() {
        return mpHearingType;
    }

    public void setMpHearingType(String mpHearingType) {
        this.mpHearingType = mpHearingType;
    }

    public Double getLastCalculatedDuration() {
        return lastCalculatedDuration;
    }

    public void setLastCalculatedDuration(Double lastCalculatedDuration) {
        this.lastCalculatedDuration = lastCalculatedDuration;
    }

    public LocalDateTime getHearingStartDate() {
        return hearingStartDate;
    }

    public void setHearingStartDate(LocalDateTime hearingStartDate) {
        this.hearingStartDate = hearingStartDate;
    }

    public LocalDateTime getHearingEndDate() {
        return hearingEndDate;
    }

    public void setHearingEndDate(LocalDateTime hearingEndDate) {
        this.hearingEndDate = hearingEndDate;
    }

    public Integer getLinkedHearingId() {
        return linkedHearingId;
    }

    public void setLinkedHearingId(Integer linkedHearingId) {
        this.linkedHearingId = linkedHearingId;
    }

    public Collection<XhbScheduledHearingDao> getScheduledHearings() {
        return scheduledHearings;
    }

    public void setScheduledHearings(Collection<XhbScheduledHearingDao> scheduledHearings) {
        this.scheduledHearings = scheduledHearings;
    }

}
