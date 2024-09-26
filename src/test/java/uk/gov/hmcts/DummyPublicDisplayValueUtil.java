package uk.gov.hmcts;

import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JuryStatusDailyListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DummyPublicDisplayValueUtil {

    private static final String NOTNULL = "Result is Null";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String EQUALS = "Results are not Equal";
    private static final String NOTEQUALS = "Results are Equal";
    private static final String ONETWOTHREE = "123";
    private static final String CASENUMBER = "20230525";
    
    private DummyPublicDisplayValueUtil() {
        // Do nothing
    }

    public static PublicDisplayValue populatePublicDisplayValue(PublicDisplayValue result) {
        result.setCourtRoomId(1);
        result.setCourtSiteCode("456");
        result.setCrestCourtRoomNo(456);
        result.setCourtSiteName(result.getCourtSiteName());
        result.setCourtRoomName("CourtRoomName");
        result.setMovedFromCourtRoomId(result.getMovedFromCourtRoomId());
        result.setMovedFromCourtRoomName("CourtRoomName");
        result.setNotBeforeTime(result.getNotBeforeTime());
        assertNotNull(result.getNotBeforeTimeAsString(), NOTNULL);
        assertNotNull(result.getEventTimeAsString(), NOTNULL);
        result.setNotBeforeTime(LocalDateTime.now());
        result.setEventTime(LocalDateTime.now());
        assertNotNull(result.getNotBeforeTimeAsString(), NOTNULL);
        assertNotNull(result.getEventTimeAsString(), NOTNULL);
        // Test compareTo
        PublicDisplayValue compareTo = new PublicDisplayValue();
        compareTo.setCourtSiteShortName(compareTo.getCourtSiteShortName());
        compareTo.setCourtSiteCode("");
        compareTo.setCourtSiteCode(result.getCourtSiteCode());
        compareTo.setCrestCourtRoomNo(-999);
        compareTo.setCrestCourtRoomNo(result.getCrestCourtRoomNo());
        assertEquals(0, compareTo.compareTo(result), EQUALS);
        return result;
    }
    
    public static PublicDisplayValue getSummaryByNameValue(boolean isHidden, String event) {
        SummaryByNameValue result =
            (SummaryByNameValue) populatePublicDisplayValue(new SummaryByNameValue());
        if (event != null) {
            result.setEvent(DummyNodeUtil.getEvent(event));
        }
        result.setFloating(result.getFloating());
        result.setDefendantName(result.getDefendantName());
        result.setReportingRestricted(result.isReportingRestricted());
        result.setReportingRestricted(true);
        result.setFloating(isHidden ? "Y" : "N");
        result.setDefendantName(DummyDefendantUtil.getDefendantName(isHidden, false));

        // Test compare
        SummaryByNameValue compared = new SummaryByNameValue();
        compared.setDefendantName(DummyDefendantUtil.getDefendantName(isHidden, true));
        assertNotEquals(0, result.compareTo(compared));
        compared.setDefendantName(result.getDefendantName());

        assertEquals(0, result.compareTo(new JuryStatusDailyListValue()), EQUALS);
        return result;
    }

    public static PublicDisplayValue getAllCaseStatusValue(boolean isHidden, boolean isFloating,
        String event, String laoType) {
        AllCaseStatusValue result =
            (AllCaseStatusValue) populatePublicDisplayValue(new AllCaseStatusValue());
        if (event != null) {
            BranchEventXmlNode node = DummyNodeUtil.getEvent(event);
            if (laoType != null) {
                // HERE
                node = DummyEventUtil.eventCheck(node, event, laoType);
            }
            result.setEvent(node);
        }
        result.setFloating(result.getFloating());
        result.setDefendantName(result.getDefendantName());
        result.setReportingRestricted(result.isReportingRestricted());
        result.setListCourtRoomId(result.getListCourtRoomId());
        result.setHearingDescription(result.getHearingDescription());
        result.setCaseTitle(result.getCaseTitle());
        result.setHearingProgress(result.getHearingProgress());
        result.setHearingProgress(0);
        result.setCaseNumber(CASENUMBER);
        result.setHearingDescription("Hearing");
        result.setReportingRestricted(true);
        result.setFloating(isFloating ? "1" : "0");
        result.setDefendantName(DummyDefendantUtil.getDefendantName(isHidden, false));
        assertFalse(result.hasCaseTitle(), FALSE);
        if (isFloating) {
            result.setCaseTitle("Mr");
            assertTrue(result.hasCaseTitle(), TRUE);
        }
        assertFalse(result.isListedInThisCourtRoom(), FALSE);
        result.setListCourtRoomId(result.getCourtRoomId());
        assertTrue(result.isListedInThisCourtRoom(), TRUE);

        // Test compare
        assertEquals(0, result.compareTo(new SummaryByNameValue()), EQUALS);
        AllCaseStatusValue compared = new AllCaseStatusValue();
        compared.setCourtSiteCode(ONETWOTHREE);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCourtSiteCode(result.getCourtSiteCode());

        compared.setFloating("1".equals(result.getFloating()) ? "0" : "1");
        assertNotEquals(0, result.compareTo(compared));
        compared.setFloating(result.getFloating());

        compared.setCrestCourtRoomNo(123);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCrestCourtRoomNo(result.getCrestCourtRoomNo());

        compared.setNotBeforeTime(LocalDateTime.now().minusHours(1));
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setNotBeforeTime(result.getNotBeforeTime());

        compared.setDefendantName(DummyDefendantUtil.getDefendantName(isHidden, true));
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setDefendantName(result.getDefendantName());

        Boolean isEquals = result.equals(compared);
        assertFalse(isEquals, FALSE);

        return result;
    }
    
    public static PublicDisplayValue getJuryStatusDailyListValue(boolean isFloating,
        boolean hasDefendants) {
        JuryStatusDailyListValue result =
            (JuryStatusDailyListValue) populatePublicDisplayValue(new JuryStatusDailyListValue());
        result.setFloating(result.getFloating());
        result.setJudgeName(result.getJudgeName());
        result.setCaseNumber(CASENUMBER);
        result.setReportingRestricted(true);
        result.setFloating(isFloating ? "1" : "0");
        result.setJudgeName(DummyJudgeUtil.getJudgeName());
        if (hasDefendants) {
            result.addDefendantName(DummyDefendantUtil.getDefendantName(isFloating, false));
        }

        // Test compare
        JuryStatusDailyListValue compared = new JuryStatusDailyListValue();
        compared.setCourtSiteCode(ONETWOTHREE);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCourtSiteCode(result.getCourtSiteCode());

        compared.setFloating(isFloating ? "0" : "1");
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setFloating(result.getFloating());

        compared.setCrestCourtRoomNo(123);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCrestCourtRoomNo(result.getCrestCourtRoomNo());

        compared.setNotBeforeTime(LocalDateTime.now().minusHours(1));
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setNotBeforeTime(result.getNotBeforeTime());

        assertEquals(0, result.compareTo(compared), EQUALS);
        Boolean isEquals = result.equals(compared);
        assertFalse(isEquals, FALSE);

        return result;
    }

    public static PublicDisplayValue getAllCourtStatusValue(boolean isHidden,
        boolean defendantOverspill, String event) {
        AllCourtStatusValue result =
            (AllCourtStatusValue) populatePublicDisplayValue(new AllCourtStatusValue());
        if (event != null) {
            result.setEvent(DummyNodeUtil.getEvent(event));
        }
        assertFalse(result.hasDefendants(), FALSE);
        result.addDefendantName(DummyDefendantUtil.getDefendantName(isHidden, false));
        if (defendantOverspill) {
            for (int i = 0; i < 16; i++) {
                result.addDefendantName(DummyDefendantUtil.getDefendantName(isHidden, false));
            }
        }
        assertTrue(result.hasDefendants(), TRUE);
        assertFalse(result.hasCaseTitle(), FALSE);
        assertFalse(result.hasInformationForDisplay(), FALSE);
        result.setCaseTitle(result.getCaseTitle());
        result.setCaseNumber(result.getCaseNumber());
        result.setReportingRestricted(result.isReportingRestricted());
        result.setReportingRestricted(isHidden);
        result.setCaseNumber(CASENUMBER);
        assertTrue(result.hasInformationForDisplay(), TRUE);

        // Test compare
        AllCourtStatusValue compared = new AllCourtStatusValue();
        compared.setCourtSiteCode(ONETWOTHREE);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCourtSiteCode(result.getCourtSiteCode());

        compared.setCrestCourtRoomNo(123);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCrestCourtRoomNo(result.getCrestCourtRoomNo());

        compared.setEventTime(LocalDateTime.now().minusHours(1));
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setEventTime(result.getEventTime());
        assertEquals(0, result.compareTo(compared), EQUALS);
        Boolean isEquals = result.equals(compared);
        assertTrue(isEquals, TRUE);

        return result;
    }

    public static PublicDisplayValue getCourtListValue(boolean isHidden, int hearingProgress,
        String event) {
        CourtListValue result = (CourtListValue) populatePublicDisplayValue(new CourtListValue());
        if (event != null) {
            result.setEvent(DummyNodeUtil.getEvent(event));
        }
        assertFalse(result.hasDefendants(), FALSE);
        result.addDefendantName(DummyDefendantUtil.getDefendantName(isHidden, false));
        assertTrue(result.hasDefendants(), TRUE);
        assertFalse(result.hasCaseTitle(), FALSE);
        assertTrue(result.hasInformationForDisplay(), TRUE);
        result.setCaseTitle(result.getCaseTitle());
        result.setCaseNumber(result.getCaseNumber());
        if (isHidden) {
            assertFalse(result.isListedInThisCourtRoom(), FALSE);
            result.setListCourtRoomId(result.getCourtRoomId());
            assertTrue(result.isListedInThisCourtRoom(), TRUE);
        }
        result.setReportingRestricted(result.isReportingRestricted());
        result.setReportingRestricted(isHidden);
        result.setHearingDescription(result.getHearingDescription());
        result.setHearingProgress(result.getHearingProgress());
        result.setHearingProgress(hearingProgress);
        result.setCaseNumber(CASENUMBER);
        assertTrue(result.hasInformationForDisplay(), TRUE);

        // Test compare
        AllCourtStatusValue compared = new AllCourtStatusValue();
        compared.setNotBeforeTime(LocalDateTime.now().minusHours(1));
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setNotBeforeTime(result.getNotBeforeTime());
        assertEquals(0, result.compareTo(compared), EQUALS);
        Boolean isEquals = result.equals(compared);
        assertFalse(isEquals, FALSE);

        return result;
    }
}