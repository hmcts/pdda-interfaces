package uk.gov.hmcts.pdda.business.entities.xhbcourt;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.TooManyFields","PMD.ExcessivePublicCount","PMD.LinguisticNaming","PMD.GodClass",
                   "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_COURT")
@Access(AccessType.FIELD)
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

    @Override
    public Integer getPrimaryKey() {
        return getCourtId();
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public String getCircuit() {
        return circuit;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCrestCourtId() {
        return crestCourtId;
    }

    public void setCrestCourtId(String crestCourtId) {
        this.crestCourtId = crestCourtId;
    }

    public String getCourtPrefix() {
        return courtPrefix;
    }

    public void setCourtPrefix(String courtPrefix) {
        this.courtPrefix = courtPrefix;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getCrestIpAddress() {
        return crestIpAddress;
    }

    public void setCrestIpAddress(String crestIpAddress) {
        this.crestIpAddress = crestIpAddress;
    }

    public String getInServiceFlag() {
        return inServiceFlag;
    }

    public void setInServiceFlag(String inServiceFlag) {
        this.inServiceFlag = inServiceFlag;
    }

    public String getProbationOfficeName() {
        return probationOfficeName;
    }

    public void setProbationOfficeName(String probationOfficeName) {
        this.probationOfficeName = probationOfficeName;
    }

    public String getInternetCourtName() {
        return internetCourtName;
    }

    public void setInternetCourtName(String internetCourtName) {
        this.internetCourtName = internetCourtName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPoliceForceCode() {
        return policeForceCode;
    }

    public void setPoliceForceCode(Integer policeForceCode) {
        this.policeForceCode = policeForceCode;
    }

    public String getFlRepSort() {
        return flRepSort;
    }

    public void setFlRepSort(String flRepSort) {
        this.flRepSort = flRepSort;
    }

    public String getCourtStartTime() {
        return courtStartTime;
    }

    public void setCourtStartTime(String courtStartTime) {
        this.courtStartTime = courtStartTime;
    }

    public String getWlRepSort() {
        return wlRepSort;
    }

    public void setWlRepSort(String wlRepSort) {
        this.wlRepSort = wlRepSort;
    }

    public Integer getWlRepPeriod() {
        return wlRepPeriod;
    }

    public void setWlRepPeriod(Integer wlRepPeriod) {
        this.wlRepPeriod = wlRepPeriod;
    }

    public String getWlRepTime() {
        return wlRepTime;
    }

    public void setWlRepTime(String wlRepTime) {
        this.wlRepTime = wlRepTime;
    }

    public String getWlFreeText() {
        return wlFreeText;
    }

    public void setWlFreeText(String wlFreeText) {
        this.wlFreeText = wlFreeText;
    }

    public String getIsPilot() {
        return isPilot;
    }

    public void setIsPilot(String isPilot) {
        this.isPilot = isPilot;
    }

    public String getDxRef() {
        return dxRef;
    }

    public void setDxRef(String dxRef) {
        this.dxRef = dxRef;
    }

    public String getCountyLocCode() {
        return countyLocCode;
    }

    public void setCountyLocCode(String countyLocCode) {
        this.countyLocCode = countyLocCode;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getCppCourt() {
        return cppCourt;
    }

    public void setCppCourt(String cppCourt) {
        this.cppCourt = cppCourt;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
