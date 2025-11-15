package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;

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

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CR_LIVE_DISPLAY")
@NamedQuery(name = "XHB_CR_LIVE_DISPLAY.findLiveDisplaysWhereStatusNotNull",
    query = "SELECT o from XHB_CR_LIVE_DISPLAY o WHERE o.status IS NOT NULL ")
@NamedQuery(name = "XHB_CR_LIVE_DISPLAY.findByCourtRoom",
    query = "SELECT o from XHB_CR_LIVE_DISPLAY o WHERE o.courtRoomId = :courtRoomId ")
public class XhbCrLiveDisplayDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_cr_live_display_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cr_live_display_seq", sequenceName = "xhb_cr_live_display_seq",
        allocationSize = 1)
    @Column(name = "CR_LIVE_DISPLAY_ID")
    private Integer crLiveDisplayId;

    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    @Column(name = "SCHEDULED_HEARING_ID")
    private Integer scheduledHearingId;

    @Column(name = "TIME_STATUS_SET")
    private LocalDateTime timeStatusSet;

    @Column(name = "STATUS")
    private String status;


    // Non-columns
    @jakarta.persistence.Transient
    private XhbScheduledHearingDao xhbScheduledHearing;


    public XhbCrLiveDisplayDao() {
        super();
    }

    public XhbCrLiveDisplayDao(XhbCrLiveDisplayDao otherData) {
        this();
        setCrLiveDisplayId(otherData.getCrLiveDisplayId());
        setCourtRoomId(otherData.getCourtRoomId());
        setScheduledHearingId(otherData.getScheduledHearingId());
        setTimeStatusSet(otherData.getTimeStatusSet());
        setStatus(otherData.getStatus());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getCrLiveDisplayId();
    }


    public Integer getCrLiveDisplayId() {
        return crLiveDisplayId;
    }

    public void setCrLiveDisplayId(Integer crLiveDisplayId) {
        this.crLiveDisplayId = crLiveDisplayId;
    }

    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    public void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    public Integer getScheduledHearingId() {
        return scheduledHearingId;
    }

    public void setScheduledHearingId(Integer scheduledHearingId) {
        this.scheduledHearingId = scheduledHearingId;
    }

    public LocalDateTime getTimeStatusSet() {
        return timeStatusSet;
    }

    public void setTimeStatusSet(LocalDateTime timeStatusSet) {
        this.timeStatusSet = timeStatusSet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public XhbScheduledHearingDao getXhbScheduledHearing() {
        return xhbScheduledHearing;
    }

    public void setXhbScheduledHearing(XhbScheduledHearingDao xsh) {
        this.xhbScheduledHearing = xsh;
    }
}
