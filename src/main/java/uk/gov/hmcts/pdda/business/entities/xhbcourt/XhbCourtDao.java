package uk.gov.hmcts.pdda.business.entities.xhbcourt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.vos.services.court.CourtStructureValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"PMD.TooManyFields","PMD.ExcessivePublicCount","PMD.LinguisticNaming","PMD.GodClass"})
@Entity(name = "XHB_COURT")
@NamedQuery(name = "XHB_COURT.findByCrestCourtIdValue",
    query = "SELECT o from XHB_COURT o WHERE o.crestCourtId = :crestCourtId ")
@NamedQuery(name = "XHB_COURT.findNonObsoleteByCrestCourtIdValue",
    query = "SELECT o from XHB_COURT o WHERE o.crestCourtId = :crestCourtId "
        + "AND (o.obsInd IS NULL OR o.obsInd='N') ")
@NamedQuery(name = "XHB_COURT.findByShortName",
    query = "SELECT o from XHB_COURT o WHERE o.shortName = :shortName ")
@NamedQuery(name = "XHB_COURT.findNonObsoleteByShortName",
    query = "SELECT o from XHB_COURT o WHERE o.shortName = :shortName "
        + "AND (o.obsInd IS NULL OR o.obsInd='N') ")
public class XhbCourtDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 6619741714677299473L;

    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtDao.class);
    private static final Integer ONE = 1;

    @Id
    @GeneratedValue(generator = "xhb_court_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_court_seq", sequenceName = "xhb_court_seq", allocationSize = 1)
    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "COURT_TYPE")
    private String courtType;

    @Column(name = "CIRCUIT")
    private String circuit;

    @Column(name = "COURT_NAME")
    private String courtName;

    @Column(name = "CREST_COURT_ID")
    private String crestCourtId;

    @Column(name = "COURT_PREFIX")
    private String courtPrefix;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "ADDRESS_ID")
    private Integer addressId;

    @Column(name = "CREST_IP_ADDRESS")
    private String crestIpAddress;

    @Column(name = "IN_SERVICE_FLAG")
    private String inServiceFlag;

    @Column(name = "PROBATION_OFFICE_NAME")
    private String probationOfficeName;

    @Column(name = "INTERNET_COURT_NAME")
    private String internetCourtName;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "COURT_CODE")
    private String courtCode;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "POLICE_FORCE_CODE")
    private Integer policeForceCode;

    @Column(name = "FL_REP_SORT")
    private String flRepSort;

    @Column(name = "COURT_START_TIME")
    private String courtStartTime;

    @Column(name = "WL_REP_SORT")
    private String wlRepSort;

    @Column(name = "WL_REP_PERIOD")
    private Integer wlRepPeriod;

    @Column(name = "WL_REP_TIME")
    private String wlRepTime;

    @Column(name = "WL_FREE_TEXT")
    private String wlFreeText;

    @Column(name = "IS_PILOT")
    private String isPilot;

    @Column(name = "DX_REF")
    private String dxRef;

    @Column(name = "COUNTY_LOC_CODE")
    private String countyLocCode;

    @Column(name = "TIER")
    private String tier;

    @Column(name = "CPP_COURT")
    private String cppCourt;

    @Column(name = "OBS_IND")
    private String obsInd;

    @jakarta.persistence.Transient
    @OneToMany(mappedBy = "xhbCourtDAO")
    private List<XhbCourtSiteDao> xhbCourtSites = new ArrayList<>();

    public XhbCourtDao() {
        super();
    }

    public XhbCourtDao(XhbCourtDao otherData) {
        this();
        setCourtId(otherData.getCourtId());
        setCourtType(otherData.getCourtType());
        setCircuit(otherData.getCircuit());
        setCourtName(otherData.getCourtName());
        setCrestCourtId(otherData.getCrestCourtId());
        setCourtPrefix(otherData.getCourtPrefix());
        setShortName(otherData.getShortName());
        setAddressId(otherData.getAddressId());
        setCrestIpAddress(otherData.getCrestIpAddress());
        setInServiceFlag(otherData.getInServiceFlag());
        setProbationOfficeName(otherData.getProbationOfficeName());
        setInternetCourtName(otherData.getInternetCourtName());
        setDisplayName(otherData.getDisplayName());
        setCourtCode(otherData.getCourtCode());
        setCountry(otherData.getCountry());
        setLanguage(otherData.getLanguage());
        setPoliceForceCode(otherData.getPoliceForceCode());
        setFlRepSort(otherData.getFlRepSort());
        setCourtStartTime(otherData.getCourtStartTime());
        setWlRepSort(otherData.getWlRepSort());
        setWlRepPeriod(otherData.getWlRepPeriod());
        setWlRepTime(otherData.getWlRepTime());
        setWlFreeText(otherData.getWlFreeText());
        setIsPilot(otherData.getIsPilot());
        setDxRef(otherData.getDxRef());
        setCountyLocCode(otherData.getCountyLocCode());
        setTier(otherData.getTier());
        setCppCourt(otherData.getCppCourt());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());

    }

    public Integer getPrimaryKey() {
        return getCourtId();
    }

    public Integer getCourtId() {
        return courtId;
    }

    public final void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getCourtType() {
        return courtType;
    }

    public final void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public String getCircuit() {
        return circuit;
    }

    public final void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    public String getCourtName() {
        return courtName;
    }

    public final void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCrestCourtId() {
        return crestCourtId;
    }

    public final void setCrestCourtId(String crestCourtId) {
        this.crestCourtId = crestCourtId;
    }

    public String getCourtPrefix() {
        return courtPrefix;
    }

    public final void setCourtPrefix(String courtPrefix) {
        this.courtPrefix = courtPrefix;
    }

    public String getShortName() {
        return shortName;
    }

    public final void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public final void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getCrestIpAddress() {
        return crestIpAddress;
    }

    public final void setCrestIpAddress(String crestIpAddress) {
        this.crestIpAddress = crestIpAddress;
    }

    public String getInServiceFlag() {
        return inServiceFlag;
    }

    public final void setInServiceFlag(String inServiceFlag) {
        this.inServiceFlag = inServiceFlag;
    }

    public String getProbationOfficeName() {
        return probationOfficeName;
    }

    public final void setProbationOfficeName(String probationOfficeName) {
        this.probationOfficeName = probationOfficeName;
    }

    public String getInternetCourtName() {
        return internetCourtName;
    }

    public final void setInternetCourtName(String internetCourtName) {
        this.internetCourtName = internetCourtName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public final void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public String getCountry() {
        return country;
    }

    public final void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public final void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPoliceForceCode() {
        return policeForceCode;
    }

    public final void setPoliceForceCode(Integer policeForceCode) {
        this.policeForceCode = policeForceCode;
    }

    public String getFlRepSort() {
        return flRepSort;
    }

    public final void setFlRepSort(String flRepSort) {
        this.flRepSort = flRepSort;
    }

    public String getCourtStartTime() {
        return courtStartTime;
    }

    public final void setCourtStartTime(String courtStartTime) {
        this.courtStartTime = courtStartTime;
    }

    public String getWlRepSort() {
        return wlRepSort;
    }

    public final void setWlRepSort(String wlRepSort) {
        this.wlRepSort = wlRepSort;
    }

    public Integer getWlRepPeriod() {
        return wlRepPeriod;
    }

    public final void setWlRepPeriod(Integer wlRepPeriod) {
        this.wlRepPeriod = wlRepPeriod;
    }

    public String getWlRepTime() {
        return wlRepTime;
    }

    public final void setWlRepTime(String wlRepTime) {
        this.wlRepTime = wlRepTime;
    }

    public String getWlFreeText() {
        return wlFreeText;
    }

    public final void setWlFreeText(String wlFreeText) {
        this.wlFreeText = wlFreeText;
    }

    public String getIsPilot() {
        return isPilot;
    }

    public final void setIsPilot(String isPilot) {
        this.isPilot = isPilot;
    }

    public String getDxRef() {
        return dxRef;
    }

    public final void setDxRef(String dxRef) {
        this.dxRef = dxRef;
    }

    public String getCountyLocCode() {
        return countyLocCode;
    }

    public final void setCountyLocCode(String countyLocCode) {
        this.countyLocCode = countyLocCode;
    }

    public String getTier() {
        return tier;
    }

    public final void setTier(String tier) {
        this.tier = tier;
    }

    public String getCppCourt() {
        return cppCourt;
    }

    public final void setCppCourt(String cppCourt) {
        this.cppCourt = cppCourt;
    }

    public String getObsInd() {
        return obsInd;
    }

    public final void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }
    
    public List<XhbCourtSiteDao> getXhbCourtSites() {
        return xhbCourtSites;
    }

    public final void setXhbCourtSites(List<XhbCourtSiteDao> xhbCourtSites) {
        this.xhbCourtSites = xhbCourtSites;
    }

    /**
     * Returns a CourtStructureValue object representing the XhbCourtDAO object and it's children.
     * 
     * @return CourtStructureValue
     */
    public CourtStructureValue getCourtStructure() {
        CourtStructureValue courtStructureValue = new CourtStructureValue();

        courtStructureValue.setCourt(this);

        List<XhbCourtSiteDao> courtSitesList = new ArrayList<>();
        for (XhbCourtSiteDao courtSite : getXhbCourtSites()) {
            if ("Y".equals(courtSite.getObsInd())) {
                LOG.debug("Ignored Obsolete CourtSite");
            } else {
                courtSitesList.add(courtSite);

                List<XhbCourtRoomDao> courtRoomList = getCourtRoomList(courtSite);

                courtStructureValue.addCourtRooms(courtSite.getCourtSiteId(),
                    courtRoomList.toArray(getXhbCourtRoomDaoArray()));
            }
        }

        courtStructureValue.setCourtSites(courtSitesList.toArray(new XhbCourtSiteDao[0]));

        return courtStructureValue;
    }

    private XhbCourtRoomDao[] getXhbCourtRoomDaoArray() {
        return new XhbCourtRoomDao[0];
    }

    private List<XhbCourtRoomDao> getCourtRoomList(XhbCourtSiteDao courtSite) {
        List<XhbCourtRoomDao> courtRoomList = new ArrayList<>();
        for (XhbCourtRoomDao courtRoom : courtSite.getXhbCourtRooms()) {
            if ("Y".equals(courtRoom.getObsInd())) {
                LOG.debug("Ignored Obsolete CourtRoom");
            } else {
                if (getXhbCourtSites().size() > ONE) {
                    courtRoom.setMultiSiteDisplayName(
                        courtSite.getShortName() + "-" + courtRoom.getDisplayName());
                }

                courtRoomList.add(courtRoom);
            }
        }
        return courtRoomList;
    }

}
