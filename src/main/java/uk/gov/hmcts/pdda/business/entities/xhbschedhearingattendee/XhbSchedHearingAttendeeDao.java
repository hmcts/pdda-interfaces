package uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_SCHED_HEARING_ATTENDEE")
public class XhbSchedHearingAttendeeDao extends AbstractVersionedDao implements Serializable {
    private static final long serialVersionUID = -6788003970955114552L;
    @Id
    @GeneratedValue(generator = "xhb_sched_hearing_attendee_seq",
        strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_sched_hearing_attendee_seq",
        sequenceName = "xhb_sched_hearing_attendee_seq", allocationSize = 1)

    @Column(name = "SH_ATTENDEE_ID")
    private Integer shAttendeeId;

    @Column(name = "ATTENDEE_TYPE")
    private String attendeeType;

    @Column(name = "SCHEDULED_HEARING_ID")
    private Integer scheduledHearingId;

    @Column(name = "SH_STAFF_ID")
    private Integer shStaffId;

    @Column(name = "SH_JUSTICE_ID")
    private Integer shJusticeId;

    @Column(name = "REF_JUDGE_ID")
    private Integer refJudgeId;

    @Column(name = "REF_COURT_REPORTER_ID")
    private Integer refCourtReporterId;

    @Column(name = "REF_JUSTICE_ID")
    private Integer refJusticeId;

    public XhbSchedHearingAttendeeDao() {
        super();
    }

    public XhbSchedHearingAttendeeDao(XhbSchedHearingAttendeeDao otherData) {
        this();
        setShAttendeeId(otherData.getShAttendeeId());
        setAttendeeType(otherData.getAttendeeType());
        setScheduledHearingId(otherData.getScheduledHearingId());
        setShStaffId(otherData.getShStaffId());
        setShJusticeId(otherData.getShJusticeId());
        setRefJudgeId(otherData.getRefJudgeId());
        setRefCourtReporterId(otherData.getRefCourtReporterId());
        setRefJusticeId(otherData.getRefJusticeId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getShAttendeeId();
    }

    public Integer getShAttendeeId() {
        return shAttendeeId;
    }

    public void setShAttendeeId(Integer shAttendeeId) {
        this.shAttendeeId = shAttendeeId;
    }

    public String getAttendeeType() {
        return attendeeType;
    }

    public void setAttendeeType(String attendeeType) {
        this.attendeeType = attendeeType;
    }

    public Integer getScheduledHearingId() {
        return scheduledHearingId;
    }

    public void setScheduledHearingId(Integer scheduledHearingId) {
        this.scheduledHearingId = scheduledHearingId;
    }

    public Integer getShStaffId() {
        return shStaffId;
    }

    public void setShStaffId(Integer shStaffId) {
        this.shStaffId = shStaffId;
    }

    public Integer getShJusticeId() {
        return shJusticeId;
    }

    public void setShJusticeId(Integer shJusticeId) {
        this.shJusticeId = shJusticeId;
    }

    public Integer getRefJudgeId() {
        return refJudgeId;
    }

    public void setRefJudgeId(Integer refJudgeId) {
        this.refJudgeId = refJudgeId;
    }

    public Integer getRefCourtReporterId() {
        return refCourtReporterId;
    }

    public void setRefCourtReporterId(Integer refCourtReporterId) {
        this.refCourtReporterId = refCourtReporterId;
    }

    public Integer getRefJusticeId() {
        return refJusticeId;
    }

    public void setRefJusticeId(Integer refJusticeId) {
        this.refJusticeId = refJusticeId;
    }
}
