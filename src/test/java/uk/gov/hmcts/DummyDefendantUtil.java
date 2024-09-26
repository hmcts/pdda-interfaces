package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DummyDefendantUtil {

    private static final String NOTNULL = "Result is Null";
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String EQUALS = "Results are not Equal";

    private DummyDefendantUtil() {
        // Do nothing
    }

    public static XhbDefendantDao getXhbDefendantDao() {
        Integer defendantId = -1;
        String firstName = "firstName";
        String middleName = "middleName";
        String surname = "surname";
        String publicDisplayHide = "publicDisplayHide";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbDefendantDao result = new XhbDefendantDao();
        result.setDefendantId(defendantId);
        result.setFirstName(firstName);
        result.setMiddleName(middleName);
        result.setSurname(surname);
        result.setPublicDisplayHide(publicDisplayHide);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        defendantId = result.getPrimaryKey();
        assertNotNull(defendantId, NOTNULL);
        return new XhbDefendantDao(result);
    }
    
    public static XhbDefendantOnCaseDao getXhbDefendantOnCaseDao() {
        XhbDefendantOnCaseDao result = new XhbDefendantOnCaseDao();
        result.setDefendantOnCaseId(-1);
        result.setNoOfTics(-1);
        result.setFinalDrivingLicenceStatus(-1);
        result.setPtiurn("ptiurn");
        result.setIsJuvenile("juvenile");
        result.setIsMasked("masked");
        result.setMaskedName("maskedName");
        result.setCaseId(-1);
        result.setDefendantId(-1);
        result.setResultsVerified("resultsVerified");
        result.setDefendantNumber(-1);
        result.setDateOfCommittal(LocalDateTime.now());
        result.setPncId("pncId");
        result.setCollectMagistrateCourtId(-1);
        result.setCurrentBcStatus("currentBcStatus");
        result.setAsn("asn");
        result.setBenchWarrantExecDate(LocalDateTime.now());
        result.setCommBcStatus("commBcStatus");
        result.setBcStatusBwExecuted("bcStatusBwExecuted");
        result.setSpecialCirFound("specialCirFound");
        result.setDateExported(LocalDateTime.now());
        result.setCustodial("custodial");
        result.setSuspended("suspended");
        result.setSeriousDrugOffence("seriousDrugOffence");
        result.setRecommendedDeportation("recommendedDeportation");
        result.setNationality("nationality");
        result.setFirstFixedTrial(LocalDateTime.now());
        result.setFirstHearingType("firstHearingType");
        result.setPublicDisplayHide("publicDisplayHide");
        result.setAmendedDateExported(LocalDateTime.now());
        result.setAmendedReason("amendedReason");
        result.setHateInd("hateInd");
        result.setHateType("hateType");
        result.setHateSentInd("hateSentInd");
        setXhbDefendantOnCaseDaoAdditional(result);
        return new XhbDefendantOnCaseDao(result);
    }
    
    private static void setXhbDefendantOnCaseDaoAdditional(XhbDefendantOnCaseDao result) {
        result.setDrivingDisqSuspendedDate(LocalDateTime.now());
        result.setMagCourtFirstHearingDate(LocalDateTime.now());
        result.setMagCourtFinalHearingDate(LocalDateTime.now());
        result.setCustodyTimeLimit(LocalDateTime.now());
        result.setFormNgSentDate(LocalDateTime.now());
        result.setCacdAppealResultDate(LocalDateTime.now());
        result.setSection28Name1("section28Name1");
        result.setSection28Name2("section28Name2");
        result.setSection28Phone1("section28Phone1");
        result.setSection28Phone2("section28Phone2");
        result.setDateRcptNoticeAppeal(LocalDateTime.now());
        result.setCacdAppealResult(LocalDateTime.now());
        result.setCoaStatus("coaStatus");
        result.setCtlApplies("ctlApplies");
        result.setDarRetentionPolicyId(-1);
        result.setObsInd("N");
        result.setLastUpdateDate(LocalDateTime.now());
        result.setCreationDate(LocalDateTime.now().minusMinutes(1));
        result.setLastUpdatedBy(TEST2);
        result.setCreatedBy(TEST1);
        result.setVersion(3);
        Integer defendantOnCaseId = result.getPrimaryKey();
        assertNotNull(defendantOnCaseId, NOTNULL);
    }
    
    public static DefendantName getDefendantName(boolean isHidden, boolean compared) {
        DefendantName result = new DefendantName("firstName", "middleName",
            compared ? "compared" : "surname", isHidden);
        assertTrue(result.hasValue(), TRUE);
        if (isHidden) {
            assertTrue(result.isHideInPublicDisplay(), TRUE);
        } else {
            assertFalse(result.isHideInPublicDisplay(), FALSE);
        }
        assertEquals(-1, result.getDefendantOnCaseId(), EQUALS);
        assertNotNull(result.getNameWithSurnameFirst(), NOTNULL);
        assertNotNull(result.getName(), NOTNULL);

        DefendantName comparedTo = new DefendantName(null, null, null, false);
        assertNotNull(comparedTo.getName(), NOTNULL);
        assertNotNull(comparedTo.getNameWithSurnameFirst(), NOTNULL);
        Boolean isEquals = result.equals(comparedTo);
        assertFalse(isEquals, FALSE);
        return result;
    }
    
}
