package uk.gov.hmcts.pdda.business.entities.xhbcase;

import jakarta.persistence.Column;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.TooManyFields","PMD.ExcessivePublicCount","PMD.GodClass",
                   "PMD.ConstructorCallsOverridableMethod"})
public class AbstractXhbCaseDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6788003970955114552L;

    @Column(name = "INDICTMENT_INFO_1")
    private String indictmentInfo1;

    @Column(name = "INDICTMENT_INFO_2")
    private String indictmentInfo2;

    @Column(name = "INDICTMENT_INFO_3")
    private String indictmentInfo3;

    @Column(name = "INDICTMENT_INFO_4")
    private String indictmentInfo4;

    @Column(name = "INDICTMENT_INFO_5")
    private String indictmentInfo5;

    @Column(name = "INDICTMENT_INFO_6")
    private String indictmentInfo6;

    @Column(name = "TELEVISED_APPLICATION_MADE")
    private String televisedApplicationMade;

    @Column(name = "TELEVISED_APP_MADE_DATE")
    private LocalDateTime televisedAppMadeDate;

    @Column(name = "TELEVISED_APP_GRANTED")
    private String televisedAppGranted;

    @Column(name = "TELEVISED_APP_REFUSED_FREETEXT")
    private String televisedAppRefusedFreetext;

    @Column(name = "TELEVISED_REMARKS_FILMED")
    private String televisedRemarksFilmed;

    @Column(name = "ORIGINAL_JPS_1")
    private String originalJps1;

    @Column(name = "ORIGINAL_JPS_2")
    private String originalJps2;

    @Column(name = "ORIGINAL_JPS_3")
    private String originalJps3;

    @Column(name = "ORIGINAL_JPS_4")
    private String originalJps4;

    @Column(name = "SECTION28_NAME1")
    private String section28Name1;

    @Column(name = "SECTION28_NAME2")
    private String section28Name2;

    @Column(name = "SECTION28_PHONE1")
    private String section28Phone1;

    @Column(name = "SECTION28_PHONE2")
    private String section28Phone2;

    @Column(name = "S28_ORDER_MADE")
    private String s28OrderMade;

    @Column(name = "S28_ELIGIBLE")
    private String s28Eligible;

    @Column(name = "MAG_CONVICTION_DATE")
    private LocalDateTime magConvictionDate;

    @Column(name = "RECEIVED_DATE")
    private LocalDateTime receivedDate;

    @Column(name = "APPEAL_LODGED_DATE")
    private LocalDateTime appealLodgedDate;

    @Column(name = "DATE_TRANS_FROM")
    private LocalDateTime dateTransFrom;

    @Column(name = "COMMITTAL_DATE")
    private LocalDateTime committalDate;

    @Column(name = "SENT_FOR_TRIAL_DATE")
    private LocalDateTime sentForTrialDate;

    @Column(name = "DATE_IND_REC")
    private LocalDateTime dateIndRec;

    @Column(name = "LC_SENT_DATE")
    private LocalDateTime lcSentDate;

    @Column(name = "PRELIMINARY_DATE_OF_HEARING")
    private LocalDateTime preliminaryDateOfHearing;

    @Column(name = "ORIG_BODY_DECISION_DATE")
    private LocalDateTime origBodyDecisionDate;

    @Column(name = "DATE_TRANS_TO")
    private LocalDateTime dateTransTo;

    @Column(name = "DATE_TRANS_RECORDED_TO")
    private LocalDateTime dateTransRecordedTo;

    @Column(name = "DATE_CTL_REMINDER_PRINTED")
    private LocalDateTime dateCtlReminderPrinted;

    protected AbstractXhbCaseDao() {
        super();
    }

    @Override
    public Object getPrimaryKey() {
        throw new UnsupportedOperationException(
            "getPrimaryKey() must be implemented by XhbCaseDao");
    }

    protected void setAdditionalData(XhbCaseDao otherData) {
        setIndictment(otherData);
        setDates(otherData);
        setSection28(otherData);
        setTelevised(otherData);
        setOriginalJps(otherData);
    }

    private void setIndictment(XhbCaseDao otherData) {
        setIndictmentInfo1(otherData.getIndictmentInfo1());
        setIndictmentInfo2(otherData.getIndictmentInfo2());
        setIndictmentInfo3(otherData.getIndictmentInfo3());
        setIndictmentInfo4(otherData.getIndictmentInfo4());
        setIndictmentInfo5(otherData.getIndictmentInfo5());
        setIndictmentInfo6(otherData.getIndictmentInfo6());
    }

    public String getIndictmentInfo1() {
        return indictmentInfo1;
    }

    public void setIndictmentInfo1(String indictmentInfo1) {
        this.indictmentInfo1 = indictmentInfo1;
    }

    public String getIndictmentInfo2() {
        return indictmentInfo2;
    }

    public void setIndictmentInfo2(String indictmentInfo2) {
        this.indictmentInfo2 = indictmentInfo2;
    }

    public String getIndictmentInfo3() {
        return indictmentInfo3;
    }

    public void setIndictmentInfo3(String indictmentInfo3) {
        this.indictmentInfo3 = indictmentInfo3;
    }

    public String getIndictmentInfo4() {
        return indictmentInfo4;
    }

    public void setIndictmentInfo4(String indictmentInfo4) {
        this.indictmentInfo4 = indictmentInfo4;
    }

    public String getIndictmentInfo5() {
        return indictmentInfo5;
    }

    public void setIndictmentInfo5(String indictmentInfo5) {
        this.indictmentInfo5 = indictmentInfo5;
    }

    public String getIndictmentInfo6() {
        return indictmentInfo6;
    }

    public void setIndictmentInfo6(String indictmentInfo6) {
        this.indictmentInfo6 = indictmentInfo6;
    }

    private void setTelevised(XhbCaseDao otherData) {
        setTelevisedApplicationMade(otherData.getTelevisedApplicationMade());
        setTelevisedAppMadeDate(otherData.getTelevisedAppMadeDate());
        setTelevisedAppGranted(otherData.getTelevisedAppGranted());
        setTelevisedAppRefusedFreetext(otherData.getTelevisedAppRefusedFreetext());
        setTelevisedRemarksFilmed(otherData.getTelevisedRemarksFilmed());
    }

    public String getTelevisedApplicationMade() {
        return televisedApplicationMade;
    }

    public void setTelevisedApplicationMade(String televisedApplicationMade) {
        this.televisedApplicationMade = televisedApplicationMade;
    }

    public LocalDateTime getTelevisedAppMadeDate() {
        return televisedAppMadeDate;
    }

    public void setTelevisedAppMadeDate(LocalDateTime televisedAppMadeDate) {
        this.televisedAppMadeDate = televisedAppMadeDate;
    }

    public String getTelevisedAppGranted() {
        return televisedAppGranted;
    }

    public void setTelevisedAppGranted(String televisedAppGranted) {
        this.televisedAppGranted = televisedAppGranted;
    }

    public String getTelevisedAppRefusedFreetext() {
        return televisedAppRefusedFreetext;
    }

    public void setTelevisedAppRefusedFreetext(String televisedAppRefusedFreetext) {
        this.televisedAppRefusedFreetext = televisedAppRefusedFreetext;
    }

    public String getTelevisedRemarksFilmed() {
        return televisedRemarksFilmed;
    }

    public void setTelevisedRemarksFilmed(String televisedRemarksFilmed) {
        this.televisedRemarksFilmed = televisedRemarksFilmed;
    }

    private void setOriginalJps(XhbCaseDao otherData) {
        setOriginalJps1(otherData.getOriginalJps1());
        setOriginalJps2(otherData.getOriginalJps2());
        setOriginalJps3(otherData.getOriginalJps3());
        setOriginalJps4(otherData.getOriginalJps4());
    }

    public String getOriginalJps1() {
        return originalJps1;
    }

    public void setOriginalJps1(String originalJps1) {
        this.originalJps1 = originalJps1;
    }

    public String getOriginalJps2() {
        return originalJps2;
    }

    public void setOriginalJps2(String originalJps2) {
        this.originalJps2 = originalJps2;
    }

    public String getOriginalJps3() {
        return originalJps3;
    }

    public void setOriginalJps3(String originalJps3) {
        this.originalJps3 = originalJps3;
    }

    public String getOriginalJps4() {
        return originalJps4;
    }

    public void setOriginalJps4(String originalJps4) {
        this.originalJps4 = originalJps4;
    }

    private void setSection28(XhbCaseDao otherData) {
        setSection28Name1(otherData.getSection28Name1());
        setSection28Name2(otherData.getSection28Name2());
        setSection28Phone1(otherData.getSection28Phone1());
        setSection28Phone2(otherData.getSection28Phone2());
        setS28OrderMade(otherData.getS28OrderMade());
        setS28Eligible(otherData.getS28Eligible());
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

    public String getS28OrderMade() {
        return s28OrderMade;
    }

    public void setS28OrderMade(String s28OrderMade) {
        this.s28OrderMade = s28OrderMade;
    }

    public String getS28Eligible() {
        return s28Eligible;
    }

    public void setS28Eligible(String s28Eligible) {
        this.s28Eligible = s28Eligible;
    }

    private void setDates(XhbCaseDao otherData) {
        setMagConvictionDate(otherData.getMagConvictionDate());
        setReceivedDate(otherData.getReceivedDate());
        setAppealLodgedDate(otherData.getAppealLodgedDate());
        setDateTransFrom(otherData.getDateTransFrom());
        setCommittalDate(otherData.getCommittalDate());
        setSentForTrialDate(otherData.getSentForTrialDate());
        setDateIndRec(otherData.getDateIndRec());
        setLcSentDate(otherData.getLcSentDate());
        setPreliminaryDateOfHearing(otherData.getPreliminaryDateOfHearing());
        setOrigBodyDecisionDate(otherData.getOrigBodyDecisionDate());
        setDateTransTo(otherData.getDateTransTo());
        setDateTransRecordedTo(otherData.getDateTransRecordedTo());
        setDateCtlReminderPrinted(otherData.getDateCtlReminderPrinted());
    }

    public LocalDateTime getMagConvictionDate() {
        return magConvictionDate;
    }

    public void setMagConvictionDate(LocalDateTime magConvictionDate) {
        this.magConvictionDate = magConvictionDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDateTime getAppealLodgedDate() {
        return appealLodgedDate;
    }

    public void setAppealLodgedDate(LocalDateTime appealLodgedDate) {
        this.appealLodgedDate = appealLodgedDate;
    }

    public LocalDateTime getDateTransFrom() {
        return dateTransFrom;
    }

    public void setDateTransFrom(LocalDateTime dateTransFrom) {
        this.dateTransFrom = dateTransFrom;
    }

    public LocalDateTime getCommittalDate() {
        return committalDate;
    }

    public void setCommittalDate(LocalDateTime committalDate) {
        this.committalDate = committalDate;
    }

    public LocalDateTime getSentForTrialDate() {
        return sentForTrialDate;
    }

    public void setSentForTrialDate(LocalDateTime sentForTrialDate) {
        this.sentForTrialDate = sentForTrialDate;
    }

    public LocalDateTime getDateIndRec() {
        return dateIndRec;
    }

    public void setDateIndRec(LocalDateTime dateIndRec) {
        this.dateIndRec = dateIndRec;
    }

    public LocalDateTime getLcSentDate() {
        return lcSentDate;
    }

    public void setLcSentDate(LocalDateTime lcSentDate) {
        this.lcSentDate = lcSentDate;
    }

    public LocalDateTime getPreliminaryDateOfHearing() {
        return preliminaryDateOfHearing;
    }

    public void setPreliminaryDateOfHearing(LocalDateTime preliminaryDateOfHearing) {
        this.preliminaryDateOfHearing = preliminaryDateOfHearing;
    }

    public LocalDateTime getOrigBodyDecisionDate() {
        return origBodyDecisionDate;
    }

    public void setOrigBodyDecisionDate(LocalDateTime origBodyDecisionDate) {
        this.origBodyDecisionDate = origBodyDecisionDate;
    }

    public LocalDateTime getDateTransTo() {
        return dateTransTo;
    }

    public void setDateTransTo(LocalDateTime dateTransTo) {
        this.dateTransTo = dateTransTo;
    }

    public LocalDateTime getDateTransRecordedTo() {
        return dateTransRecordedTo;
    }

    public void setDateTransRecordedTo(LocalDateTime dateTransRecordedTo) {
        this.dateTransRecordedTo = dateTransRecordedTo;
    }

    public LocalDateTime getDateCtlReminderPrinted() {
        return dateCtlReminderPrinted;
    }

    public void setDateCtlReminderPrinted(LocalDateTime dateCtlReminderPrinted) {
        this.dateCtlReminderPrinted = dateCtlReminderPrinted;
    }
}
