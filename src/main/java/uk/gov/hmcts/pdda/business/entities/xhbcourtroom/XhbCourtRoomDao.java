package uk.gov.hmcts.pdda.business.entities.xhbcourtroom;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;

import java.io.Serializable;
import java.util.Collection;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_COURT_ROOM")
@NamedQuery(name = "XHB_COURT_ROOM.findByCourtSiteId",
    query = "SELECT o from XHB_COURT_ROOM o WHERE o.courtSiteId = :courtSiteId ")

@NamedQuery(name = "XHB_COURT_ROOM.findByDisplayId", query = "SELECT o FROM XHB_COURT_ROOM o "
    + "WHERE o.courtRoomId IN (SELECT dcr.courtRoomId FROM "
    + "XHB_DISPLAY_COURT_ROOM dcr WHERE dcr.displayId = :displayId)")

@NamedQuery(name = "XHB_COURT_ROOM.findVIPMultiSite", query = "SELECT o FROM XHB_COURT_ROOM o "
    + "WHERE o.courtRoomId IN (SELECT dcr.courtRoomId FROM "
    + "XHB_DISPLAY_LOCATION dl, XHB_COURT_SITE cs, " + "XHB_DISPLAY d, XHB_DISPLAY_COURT_ROOM dcr "
    + "WHERE dcr.displayId = d.displayId " + "AND d.displayLocationId = dl.displayLocationId "
    + "AND dl.courtSiteId = cs.courtSiteId AND cs.courtId = :courtId "
    + "AND d.descriptionCode = 'v_i_p') ORDER BY o.crestCourtRoomNo")

@NamedQuery(name = "XHB_COURT_ROOM.findVIPMNoSite", query = "SELECT o FROM XHB_COURT_ROOM o "
    + "WHERE o.courtRoomId IN (SELECT dcr.courtRoomId FROM "
    + "XHB_DISPLAY_LOCATION dl, XHB_COURT_SITE cs, " + "XHB_DISPLAY d, XHB_DISPLAY_COURT_ROOM dcr "
    + "WHERE dcr.displayId = d.displayId " + "AND d.displayLocationId = dl.displayLocationId "
    + "AND dl.courtSiteId = cs.courtSiteId AND cs.courtId = :courtId "
    + "AND d.descriptionCode = 'v_i_p') ORDER BY o.crestCourtRoomNo")

public class XhbCourtRoomDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 5203521612570811544L;

    @Id
    @GeneratedValue(generator = "xhb_court_room_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_court_room_seq", sequenceName = "xhb_court_room_seq",
        allocationSize = 1)
    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    @Column(name = "COURT_ROOM_NAME")
    private String courtRoomName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREST_COURT_ROOM_NO")
    private Integer crestCourtRoomNo;

    @Column(name = "COURT_SITE_ID")
    private Integer courtSiteId;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "SECURITY_IND")
    private String securityInd;

    @Column(name = "VIDEO_IND")
    private String videoInd;

    @Column(name = "OBS_IND")
    private String obsInd;

    // Non-columns
    @jakarta.persistence.Transient
    private String multiSiteDisplayName;

    @jakarta.persistence.Transient
    private Collection<XhbCrLiveDisplayDao> xhbCrLiveDisplays;

    @jakarta.persistence.Transient
    private Collection<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNotices;

    public XhbCourtRoomDao() {
        super();
    }

    public XhbCourtRoomDao(XhbCourtRoomDao otherData) {
        this();
        setCourtRoomId(otherData.getCourtRoomId());
        setCourtRoomName(otherData.getCourtRoomName());
        setDescription(otherData.getDescription());
        setCrestCourtRoomNo(otherData.getCrestCourtRoomNo());
        setCourtSiteId(otherData.getCourtSiteId());
        setDisplayName(otherData.getDisplayName());
        setSecurityInd(otherData.getSecurityInd());
        setVideoInd(otherData.getVideoInd());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getCourtRoomId();
    }

    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    public void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    public String getCourtRoomName() {
        return courtRoomName;
    }

    public void setCourtRoomName(String courtRoomName) {
        this.courtRoomName = courtRoomName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCrestCourtRoomNo() {
        return crestCourtRoomNo;
    }

    public void setCrestCourtRoomNo(Integer crestCourtRoomNo) {
        this.crestCourtRoomNo = crestCourtRoomNo;
    }

    public Integer getCourtSiteId() {
        return courtSiteId;
    }

    public void setCourtSiteId(Integer courtSiteId) {
        this.courtSiteId = courtSiteId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSecurityInd() {
        return securityInd;
    }

    public void setSecurityInd(String securityInd) {
        this.securityInd = securityInd;
    }

    public String getVideoInd() {
        return videoInd;
    }

    public void setVideoInd(String videoInd) {
        this.videoInd = videoInd;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

    public String getMultiSiteDisplayName() {
        return multiSiteDisplayName;
    }

    public void setMultiSiteDisplayName(String multiSiteDisplayName) {
        this.multiSiteDisplayName = multiSiteDisplayName;
    }

    public Collection<XhbCrLiveDisplayDao> getXhbCrLiveDisplays() {
        return xhbCrLiveDisplays;
    }

    public void setXhbCrLiveDisplays(Collection<XhbCrLiveDisplayDao> xhbCrLiveDisplays) {
        this.xhbCrLiveDisplays = xhbCrLiveDisplays;
    }

    public Collection<XhbConfiguredPublicNoticeDao> getXhbConfiguredPublicNotices() {
        return xhbConfiguredPublicNotices;
    }

    public void setXhbConfiguredPublicNotices(
        Collection<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNotices) {
        this.xhbConfiguredPublicNotices = xhbConfiguredPublicNotices;
    }


}
