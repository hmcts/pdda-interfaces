package uk.gov.hmcts.pdda.business.entities.xhbcourtsite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_COURT_SITE")
@NamedQuery(name = "XHB_COURT_SITE.findByCourtId",
    query = "SELECT o from XHB_COURT_SITE o WHERE o.courtId = :courtId ")
@NamedQuery(name = "XHB_COURT_SITE.findByCrestCourtIdValue",
    query = "SELECT o from XHB_COURT_SITE o WHERE o.crestCourtId = :crestCourtId AND "
        + "o.courtSiteCode='A' AND (o.obsInd IS NULL or o.obsInd='N' or o.obsInd='') ORDER BY o.courtSiteName")
@NamedQuery(name = "XHB_COURT_SITE.findByCourtIdAndCourtSiteName",
    query = "SELECT o from XHB_COURT_SITE o WHERE o.courtId = :courtId AND "
        + "o.courtSiteName = :courtSiteName AND (o.obsInd IS NULL or o.obsInd='N')")
public class XhbCourtSiteDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -1623864177985336883L;

    @Id
    @GeneratedValue(generator = "xhb_court_site_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_court_site_seq", sequenceName = "xhb_court_site_seq",
        allocationSize = 1)
    @Column(name = "COURT_SITE_ID")
    private Integer courtSiteId;

    @Column(name = "COURT_SITE_NAME")
    private String courtSiteName;

    @Column(name = "COURT_SITE_CODE")
    private String courtSiteCode;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "ADDRESS_ID")
    private Integer addressId;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "CREST_COURT_ID")
    private String crestCourtId;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "SITE_GROUP")
    private Integer siteGroup;

    @Column(name = "FLOATER_TEXT")
    private String floaterText;

    @Column(name = "LIST_NAME")
    private String listName;

    @Column(name = "TIER")
    private String tier;

    @Column(name = "OBS_IND")
    private String obsInd;

    // Non-columns
    @jakarta.persistence.Transient
    private List<XhbDisplayLocationDao> xhbDisplayLocations = new ArrayList<>();

    @jakarta.persistence.Transient
    private List<XhbCourtRoomDao> xhbCourtRooms = new ArrayList<>();

    public XhbCourtSiteDao() {
        super();
    }

    public XhbCourtSiteDao(XhbCourtSiteDao otherData) {
        this();
        setCourtSiteId(otherData.getCourtSiteId());
        setCourtSiteName(otherData.getCourtSiteName());
        setCourtSiteCode(otherData.getCourtSiteCode());
        setCourtId(otherData.getCourtId());
        setAddressId(otherData.getAddressId());
        setDisplayName(otherData.getDisplayName());
        setCrestCourtId(otherData.getCrestCourtId());
        setShortName(otherData.getShortName());
        setSiteGroup(otherData.getSiteGroup());
        setFloaterText(otherData.getFloaterText());
        setListName(otherData.getListName());
        setTier(otherData.getTier());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getCourtSiteId();
    }

    public Integer getCourtSiteId() {
        return courtSiteId;
    }

    public void setCourtSiteId(Integer courtSiteId) {
        this.courtSiteId = courtSiteId;
    }

    public String getCourtSiteName() {
        return courtSiteName;
    }

    public void setCourtSiteName(String courtSiteName) {
        this.courtSiteName = courtSiteName;
    }

    public String getCourtSiteCode() {
        return courtSiteCode;
    }

    public void setCourtSiteCode(String courtSiteCode) {
        this.courtSiteCode = courtSiteCode;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCrestCourtId() {
        return crestCourtId;
    }

    public void setCrestCourtId(String crestCourtId) {
        this.crestCourtId = crestCourtId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getSiteGroup() {
        return siteGroup;
    }

    public void setSiteGroup(Integer siteGroup) {
        this.siteGroup = siteGroup;
    }

    public String getFloaterText() {
        return floaterText;
    }

    public void setFloaterText(String floaterText) {
        this.floaterText = floaterText;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

    public List<XhbDisplayLocationDao> getXhbDisplayLocations() {
        return xhbDisplayLocations;
    }

    public void setXhbDisplayLocations(List<XhbDisplayLocationDao> xhbDisplayLocations) {
        this.xhbDisplayLocations = xhbDisplayLocations;
    }

    public List<XhbCourtRoomDao> getXhbCourtRooms() {
        return xhbCourtRooms;
    }

    public void setXhbCourtRooms(List<XhbCourtRoomDao> xhbCourtRooms) {
        this.xhbCourtRooms = xhbCourtRooms;
    }
}
