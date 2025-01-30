package uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "XHB_SCHED_HEARING_DEFENDANT")
@NamedQuery(name = "XHB_SCHED_HEARING_DEFENDANT.findByScheduledHearingId",
    query = "SELECT o from XHB_SCHED_HEARING_DEFENDANT o WHERE o.scheduledHearingId = :scheduledHearingId")
@NamedQuery(name = "XHB_SCHED_HEARING_DEFENDANT.findByHearingAndDefendant",
    query = "SELECT o from XHB_SCHED_HEARING_DEFENDANT o WHERE o.scheduledHearingId = :scheduledHearingId "
        + "AND o.defendantOnCaseId = :defendantOnCaseId")
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class XhbSchedHearingDefendantDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6844793990175522946L;

    @Id
    @GeneratedValue(generator = "xhb_scheduled_hearing_def_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_scheduled_hearing_def_seq",
        sequenceName = "xhb_scheduled_hearing_def_seq", allocationSize = 1)
    @Column(name = "SCHED_HEAR_DEF_ID")
    private Integer schedHearingDefendantId;

    @Column(name = "SCHEDULED_HEARING_ID")
    private Integer scheduledHearingId;

    @Column(name = "DEFENDANT_ON_CASE_ID")
    private Integer defendantOnCaseId;

    public XhbSchedHearingDefendantDao() {
        super();
    }

    public XhbSchedHearingDefendantDao(Integer schedHearingDefendantId, Integer scheduledHearingId,
        Integer defendantOnCaseId, LocalDateTime lastUpdateDate, LocalDateTime creationDate,
        String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setSchedHearingDefendantId(schedHearingDefendantId);
        setScheduledHearingId(scheduledHearingId);
        setDefendantOnCaseId(defendantOnCaseId);
        super.setLastUpdateDate(lastUpdateDate);
        super.setCreationDate(creationDate);
        super.setLastUpdatedBy(lastUpdatedBy);
        super.setCreatedBy(createdBy);
        super.setVersion(version);
    }

    public XhbSchedHearingDefendantDao(XhbSchedHearingDefendantDao otherData) {
        this();
        setSchedHearingDefendantId(otherData.getSchedHearingDefendantId());
        setScheduledHearingId(otherData.getScheduledHearingId());
        setDefendantOnCaseId(otherData.getDefendantOnCaseId());
        super.setLastUpdateDate(otherData.getLastUpdateDate());
        super.setCreationDate(otherData.getCreationDate());
        super.setLastUpdatedBy(otherData.getLastUpdatedBy());
        super.setCreatedBy(otherData.getCreatedBy());
        super.setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getSchedHearingDefendantId();
    }

    public Integer getSchedHearingDefendantId() {
        return schedHearingDefendantId;
    }

    public void setSchedHearingDefendantId(Integer schedHearingDefendantId) {
        this.schedHearingDefendantId = schedHearingDefendantId;
    }

    public Integer getScheduledHearingId() {
        return scheduledHearingId;
    }

    public void setScheduledHearingId(Integer scheduledHearingId) {
        this.scheduledHearingId = scheduledHearingId;
    }

    public Integer getDefendantOnCaseId() {
        return defendantOnCaseId;
    }

    public void setDefendantOnCaseId(Integer defendantOnCaseId) {
        this.defendantOnCaseId = defendantOnCaseId;
    }

}
