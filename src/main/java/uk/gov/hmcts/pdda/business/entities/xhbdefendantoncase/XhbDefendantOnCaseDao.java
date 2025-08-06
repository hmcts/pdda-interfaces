package uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase;

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

@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessivePublicCount", "PMD.LinguisticNaming","PMD.CyclomaticComplexity",
                   "PMD.ConstructorCallsOverridableMethod", "PMD.GodClass"})
@Entity(name = "XHB_DEFENDANT_ON_CASE")
@NamedQuery(name = "XHB_DEFENDANT_ON_CASE.findByDefendantAndCase",
    query = "SELECT o from XHB_DEFENDANT_ON_CASE o WHERE o.defendantId = :defendantId AND "
        + "o.caseId = :caseId ")
public class XhbDefendantOnCaseDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6788003970955114552L;

    @Id
    @GeneratedValue(generator = "xhb_defendant_on_case_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_defendant_on_case_seq",
        sequenceName = "xhb_defendant_on_case_seq", allocationSize = 1)
    @Column(name = "DEFENDANT_ON_CASE_ID")
    private Integer defendantOnCaseId;

    @Column(name = "NO_OF_TICS")
    private Integer noOfTics;

    @Column(name = "FINAL_DRIVING_LICENCE_STATUS")
    private Integer finalDrivingLicenceStatus;

    @Column(name = "PTIURN")
    private String ptiurn;

    @Column(name = "IS_JUVENILE")
    private String isJuvenile;

    @Column(name = "IS_MASKED")
    private String isMasked;

    @Column(name = "MASKED_NAME")
    private String maskedName;

    @Column(name = "CASE_ID")
    private Integer caseId;

    @Column(name = "DEFENDANT_ID")
    private Integer defendantId;

    @Column(name = "RESULTS_VERIFIED")
    private String resultsVerified;

    @Column(name = "DEFENDANT_NUMBER")
    private Integer defendantNumber;

    @Column(name = "DATE_OF_COMMITTAL")
    private LocalDateTime dateOfCommittal;

    @Column(name = "PNC_ID")
    private String pncId;

    @Column(name = "COLLECT_MAGISTRATE_COURT_ID")
    private Integer collectMagistrateCourtId;

    @Column(name = "CURRENT_BC_STATUS")
    private String currentBcStatus;

    @Column(name = "ASN")
    private String asn;

    @Column(name = "BENCH_WARRANT_EXEC_DATE")
    private LocalDateTime benchWarrantExecDate;

    @Column(name = "COMM_BC_STATUS")
    private String commBcStatus;

    @Column(name = "BC_STATUS_BW_EXECUTED")
    private String bcStatusBwExecuted;

    @Column(name = "SPECIAL_CIR_FOUND")
    private String specialCirFound;

    @Column(name = "DATE_EXPORTED")
    private LocalDateTime dateExported;

    @Column(name = "CUSTODIAL")
    private String custodial;

    @Column(name = "SUSPENDED")
    private String suspended;

    @Column(name = "SERIOUS_DRUG_OFFENCE")
    private String seriousDrugOffence;

    @Column(name = "RECOMMENDED_DEPORTATION")
    private String recommendedDeportation;

    @Column(name = "NATIONALITY")
    private String nationality;

    @Column(name = "FIRST_FIXED_TRIAL")
    private LocalDateTime firstFixedTrial;

    @Column(name = "FIRST_HEARING_TYPE")
    private String firstHearingType;

    @Column(name = "PUBLIC_DISPLAY_HIDE")
    private String publicDisplayHide;

    @Column(name = "AMENDED_DATE_EXPORTED")
    private LocalDateTime amendedDateExported;

    @Column(name = "AMENDED_REASON")
    private String amendedReason;

    @Column(name = "HATE_IND")
    private String hateInd;

    @Column(name = "HATE_TYPE")
    private String hateType;

    @Column(name = "HATE_SENT_IND")
    private String hateSentInd;

    @Column(name = "DRIVING_DISQ_SUSPENDED_DATE")
    private LocalDateTime drivingDisqSuspendedDate;

    @Column(name = "MAG_COURT_FIRST_HEARING_DATE")
    private LocalDateTime magCourtFirstHearingDate;

    @Column(name = "MAG_COURT_FINAL_HEARING_DATE")
    private LocalDateTime magCourtFinalHearingDate;

    @Column(name = "CUSTODY_TIME_LIMIT")
    private LocalDateTime custodyTimeLimit;

    @Column(name = "FORM_NG_SENT_DATE")
    private LocalDateTime formNgSentDate;

    @Column(name = "CACD_APPEAL_RESULT_DATE")
    private LocalDateTime cacdAppealResultDate;

    @Column(name = "SECTION28_NAME1")
    private String section28Name1;

    @Column(name = "SECTION28_NAME2")
    private String section28Name2;

    @Column(name = "SECTION28_PHONE1")
    private String section28Phone1;

    @Column(name = "SECTION28_PHONE2")
    private String section28Phone2;

    @Column(name = "DATE_RCPT_NOTICE_APPEAL")
    private LocalDateTime dateRcptNoticeAppeal;

    @Column(name = "CACD_APPEAL_RESULT")
    private LocalDateTime cacdAppealResult;

    @Column(name = "COA_STATUS")
    private String coaStatus;

    @Column(name = "CTL_APPLIES")
    private String ctlApplies;

    @Column(name = "DAR_RETENTION_POLICY_ID")
    private Integer darRetentionPolicyId;

    @Column(name = "OBS_IND")
    private String obsInd;

    public XhbDefendantOnCaseDao() {
        super();
    }

    public XhbDefendantOnCaseDao(XhbDefendantOnCaseDao otherData) {
        this();
        setDefendantOnCaseId(otherData.getDefendantOnCaseId());
        setNoOfTics(otherData.getNoOfTics());
        setFinalDrivingLicenceStatus(otherData.getFinalDrivingLicenceStatus());
        setPtiurn(otherData.getPtiurn());
        setIsJuvenile(otherData.getIsJuvenile());
        setIsMasked(otherData.getIsMasked());
        setMaskedName(otherData.getMaskedName());
        setCaseId(otherData.getCaseId());
        setDefendantId(otherData.getDefendantId());
        setResultsVerified(otherData.getResultsVerified());
        setDefendantNumber(otherData.getDefendantNumber());
        setDateOfCommittal(otherData.getDateOfCommittal());
        setPncId(otherData.getPncId());
        setCollectMagistrateCourtId(otherData.getCollectMagistrateCourtId());
        setCurrentBcStatus(otherData.getCurrentBcStatus());
        setAsn(otherData.getAsn());
        setBenchWarrantExecDate(otherData.getBenchWarrantExecDate());
        setCommBcStatus(otherData.getCommBcStatus());
        setBcStatusBwExecuted(otherData.getBcStatusBwExecuted());
        setSpecialCirFound(otherData.getSpecialCirFound());
        setDateExported(otherData.getDateExported());
        setCustodial(otherData.getCustodial());
        setSuspended(otherData.getSuspended());
        setSeriousDrugOffence(otherData.getSeriousDrugOffence());
        setRecommendedDeportation(otherData.getRecommendedDeportation());
        setNationality(otherData.getNationality());
        setFirstFixedTrial(otherData.getFirstFixedTrial());
        setFirstHearingType(otherData.getFirstHearingType());
        setPublicDisplayHide(otherData.getPublicDisplayHide());
        setAmendedDateExported(otherData.getAmendedDateExported());
        setAmendedReason(otherData.getAmendedReason());
        setHateInd(otherData.getHateInd());
        setHateType(otherData.getHateType());
        setHateSentInd(otherData.getHateSentInd());
        setDrivingDisqSuspendedDate(otherData.getDrivingDisqSuspendedDate());
        setMagCourtFirstHearingDate(otherData.getMagCourtFirstHearingDate());
        setMagCourtFinalHearingDate(otherData.getMagCourtFinalHearingDate());
        setCustodyTimeLimit(otherData.getCustodyTimeLimit());
        setFormNgSentDate(otherData.getFormNgSentDate());
        setCacdAppealResultDate(otherData.getCacdAppealResultDate());
        setSection28Name1(otherData.getSection28Name1());
        setSection28Name2(otherData.getSection28Name2());
        setSection28Phone1(otherData.getSection28Phone1());
        setSection28Phone2(otherData.getSection28Phone2());
        setDateRcptNoticeAppeal(otherData.getDateRcptNoticeAppeal());
        setCacdAppealResult(otherData.getCacdAppealResult());
        setCoaStatus(otherData.getCoaStatus());
        setCtlApplies(otherData.getCtlApplies());
        setDarRetentionPolicyId(otherData.getDarRetentionPolicyId());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getDefendantOnCaseId();
    }

    public Integer getDefendantOnCaseId() {
        return defendantOnCaseId;
    }

    public void setDefendantOnCaseId(Integer defendantOnCaseId) {
        this.defendantOnCaseId = defendantOnCaseId;
    }

    public Integer getNoOfTics() {
        return noOfTics;
    }

    public void setNoOfTics(Integer noOfTics) {
        this.noOfTics = noOfTics;
    }

    public Integer getFinalDrivingLicenceStatus() {
        return finalDrivingLicenceStatus;
    }

    public void setFinalDrivingLicenceStatus(Integer finalDrivingLicenceStatus) {
        this.finalDrivingLicenceStatus = finalDrivingLicenceStatus;
    }

    public String getPtiurn() {
        return ptiurn;
    }

    public void setPtiurn(String ptiurn) {
        this.ptiurn = ptiurn;
    }

    public String getIsJuvenile() {
        return isJuvenile;
    }

    public void setIsJuvenile(String isJuvenile) {
        this.isJuvenile = isJuvenile;
    }

    public String getIsMasked() {
        return isMasked;
    }

    public void setIsMasked(String isMasked) {
        this.isMasked = isMasked;
    }

    public String getMaskedName() {
        return maskedName;
    }

    public void setMaskedName(String maskedName) {
        this.maskedName = maskedName;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Integer getDefendantId() {
        return defendantId;
    }

    public void setDefendantId(Integer defendantId) {
        this.defendantId = defendantId;
    }

    public String getResultsVerified() {
        return resultsVerified;
    }

    public void setResultsVerified(String resultsVerified) {
        this.resultsVerified = resultsVerified;
    }

    public Integer getDefendantNumber() {
        return defendantNumber;
    }

    public void setDefendantNumber(Integer defendantNumber) {
        this.defendantNumber = defendantNumber;
    }

    public LocalDateTime getDateOfCommittal() {
        return dateOfCommittal;
    }

    public void setDateOfCommittal(LocalDateTime dateOfCommittal) {
        this.dateOfCommittal = dateOfCommittal;
    }

    public String getPncId() {
        return pncId;
    }

    public void setPncId(String pncId) {
        this.pncId = pncId;
    }

    public Integer getCollectMagistrateCourtId() {
        return collectMagistrateCourtId;
    }

    public void setCollectMagistrateCourtId(Integer collectMagistrateCourtId) {
        this.collectMagistrateCourtId = collectMagistrateCourtId;
    }

    public String getCurrentBcStatus() {
        return currentBcStatus;
    }

    public void setCurrentBcStatus(String currentBcStatus) {
        this.currentBcStatus = currentBcStatus;
    }

    public String getAsn() {
        return asn;
    }

    public void setAsn(String asn) {
        this.asn = asn;
    }

    public LocalDateTime getBenchWarrantExecDate() {
        return benchWarrantExecDate;
    }

    public void setBenchWarrantExecDate(LocalDateTime benchWarrantExecDate) {
        this.benchWarrantExecDate = benchWarrantExecDate;
    }

    public String getCommBcStatus() {
        return commBcStatus;
    }

    public void setCommBcStatus(String commBcStatus) {
        this.commBcStatus = commBcStatus;
    }

    public String getBcStatusBwExecuted() {
        return bcStatusBwExecuted;
    }

    public void setBcStatusBwExecuted(String bcStatusBwExecuted) {
        this.bcStatusBwExecuted = bcStatusBwExecuted;
    }

    public String getSpecialCirFound() {
        return specialCirFound;
    }

    public void setSpecialCirFound(String specialCirFound) {
        this.specialCirFound = specialCirFound;
    }

    public LocalDateTime getDateExported() {
        return dateExported;
    }

    public void setDateExported(LocalDateTime dateExported) {
        this.dateExported = dateExported;
    }

    public String getCustodial() {
        return custodial;
    }

    public void setCustodial(String custodial) {
        this.custodial = custodial;
    }

    public String getSuspended() {
        return suspended;
    }

    public void setSuspended(String suspended) {
        this.suspended = suspended;
    }

    public String getSeriousDrugOffence() {
        return seriousDrugOffence;
    }

    public void setSeriousDrugOffence(String seriousDrugOffence) {
        this.seriousDrugOffence = seriousDrugOffence;
    }

    public String getRecommendedDeportation() {
        return recommendedDeportation;
    }

    public void setRecommendedDeportation(String recommendedDeportation) {
        this.recommendedDeportation = recommendedDeportation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDateTime getFirstFixedTrial() {
        return firstFixedTrial;
    }

    public void setFirstFixedTrial(LocalDateTime firstFixedTrial) {
        this.firstFixedTrial = firstFixedTrial;
    }

    public String getFirstHearingType() {
        return firstHearingType;
    }

    public void setFirstHearingType(String firstHearingType) {
        this.firstHearingType = firstHearingType;
    }

    public String getPublicDisplayHide() {
        return publicDisplayHide;
    }

    public void setPublicDisplayHide(String publicDisplayHide) {
        this.publicDisplayHide = publicDisplayHide;
    }

    public LocalDateTime getAmendedDateExported() {
        return amendedDateExported;
    }

    public void setAmendedDateExported(LocalDateTime amendedDateExported) {
        this.amendedDateExported = amendedDateExported;
    }

    public String getAmendedReason() {
        return amendedReason;
    }

    public void setAmendedReason(String amendedReason) {
        this.amendedReason = amendedReason;
    }

    public String getHateInd() {
        return hateInd;
    }

    public void setHateInd(String hateInd) {
        this.hateInd = hateInd;
    }

    public String getHateType() {
        return hateType;
    }

    public void setHateType(String hateType) {
        this.hateType = hateType;
    }

    public String getHateSentInd() {
        return hateSentInd;
    }

    public void setHateSentInd(String hateSentInd) {
        this.hateSentInd = hateSentInd;
    }

    public LocalDateTime getDrivingDisqSuspendedDate() {
        return drivingDisqSuspendedDate;
    }

    public void setDrivingDisqSuspendedDate(LocalDateTime drivingDisqSuspendedDate) {
        this.drivingDisqSuspendedDate = drivingDisqSuspendedDate;
    }

    public LocalDateTime getMagCourtFirstHearingDate() {
        return magCourtFirstHearingDate;
    }

    public void setMagCourtFirstHearingDate(LocalDateTime magCourtFirstHearingDate) {
        this.magCourtFirstHearingDate = magCourtFirstHearingDate;
    }

    public LocalDateTime getMagCourtFinalHearingDate() {
        return magCourtFinalHearingDate;
    }

    public void setMagCourtFinalHearingDate(LocalDateTime magCourtFinalHearingDate) {
        this.magCourtFinalHearingDate = magCourtFinalHearingDate;
    }

    public LocalDateTime getCustodyTimeLimit() {
        return custodyTimeLimit;
    }

    public void setCustodyTimeLimit(LocalDateTime custodyTimeLimit) {
        this.custodyTimeLimit = custodyTimeLimit;
    }

    public LocalDateTime getFormNgSentDate() {
        return formNgSentDate;
    }

    public void setFormNgSentDate(LocalDateTime formNgSentDate) {
        this.formNgSentDate = formNgSentDate;
    }

    public LocalDateTime getCacdAppealResultDate() {
        return cacdAppealResultDate;
    }

    public void setCacdAppealResultDate(LocalDateTime cacdAppealResultDate) {
        this.cacdAppealResultDate = cacdAppealResultDate;
    }

    public String getSection28Name1() {
        return section28Name1;
    }

    public void setSection28Name1(String section28Name1) {
        this.section28Name1 = section28Name1;
    }

    public String getSection28Name2() {
        return section28Name2;
    }

    public void setSection28Name2(String section28Name2) {
        this.section28Name2 = section28Name2;
    }

    public String getSection28Phone1() {
        return section28Phone1;
    }

    public void setSection28Phone1(String section28Phone1) {
        this.section28Phone1 = section28Phone1;
    }

    public String getSection28Phone2() {
        return section28Phone2;
    }

    public void setSection28Phone2(String section28Phone2) {
        this.section28Phone2 = section28Phone2;
    }

    public LocalDateTime getDateRcptNoticeAppeal() {
        return dateRcptNoticeAppeal;
    }

    public void setDateRcptNoticeAppeal(LocalDateTime dateRcptNoticeAppeal) {
        this.dateRcptNoticeAppeal = dateRcptNoticeAppeal;
    }

    public LocalDateTime getCacdAppealResult() {
        return cacdAppealResult;
    }

    public void setCacdAppealResult(LocalDateTime cacdAppealResult) {
        this.cacdAppealResult = cacdAppealResult;
    }

    public String getCoaStatus() {
        return coaStatus;
    }

    public void setCoaStatus(String coaStatus) {
        this.coaStatus = coaStatus;
    }

    public String getCtlApplies() {
        return ctlApplies;
    }

    public void setCtlApplies(String ctlApplies) {
        this.ctlApplies = ctlApplies;
    }

    public Integer getDarRetentionPolicyId() {
        return darRetentionPolicyId;
    }

    public void setDarRetentionPolicyId(Integer darRetentionPolicyId) {
        this.darRetentionPolicyId = darRetentionPolicyId;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
