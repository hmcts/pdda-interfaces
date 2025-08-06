package uk.gov.hmcts.pdda.business.entities.xhbcrliveinternet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Commenting out the Entity annotation so that Hibernate does not think its a table that exists.

 * @author scottatwell

 */
@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CR_LIVE_INTERNET")
public class XhbCrLiveInternetDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 4680232167997663662L;

    @Id
    @GeneratedValue(generator = "xhb_cr_live_internet_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cr_live_internet_seq", sequenceName = "xhb_cr_live_internet_seq",
        allocationSize = 1)
    @Column(name = "CR_LIVE_INTERNET_ID")
    private Integer crLiveInternetId;

    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    @Column(name = "SCHEDULED_HEARING_ID")
    private Integer scheduledHearingId;

    @Column(name = "TIME_STATUS_SET")
    private LocalDateTime timeStatusSet;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "OBS_IND")
    private String obsInd;

    public XhbCrLiveInternetDao() {
        super();
    }

    public XhbCrLiveInternetDao(XhbCrLiveInternetDao otherData) {
        this();
        setCrLiveInternetId(otherData.getCrLiveInternetId());
        setCourtRoomId(otherData.getCourtRoomId());
        setScheduledHearingId(otherData.getScheduledHearingId());
        setTimeStatusSet(otherData.getTimeStatusSet());
        setStatus(otherData.getStatus());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getCrLiveInternetId();
    }

    public Integer getCrLiveInternetId() {
        return crLiveInternetId;
    }

    public void setCrLiveInternetId(Integer crLiveInternetId) {
        this.crLiveInternetId = crLiveInternetId;
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

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
