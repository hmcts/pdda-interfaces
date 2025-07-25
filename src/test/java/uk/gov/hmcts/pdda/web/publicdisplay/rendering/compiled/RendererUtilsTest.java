package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.DummyPublicDisplayValueUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RendererUtilsTest {

    private static final String FALSE = "Result is True";
    private static final String TRUE = "Result is False";

    @BeforeEach
    public void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testIsHideInPublicDisplay() {
        // Setup
        DefendantName defendantName = DummyDefendantUtil.getDefendantName(true, false);
        Collection<DefendantName> defendantNames = new ArrayList<>();
        defendantNames.add(defendantName);
        boolean result = RendererUtils.isHideInPublicDisplay(defendantName.getDefendantOnCaseId(),
            defendantNames);
        assertTrue(result, TRUE);
        result = RendererUtils.isHideInPublicDisplay(defendantName.getDefendantOnCaseId() - 10,
            defendantNames);
        assertFalse(result, FALSE);
        result = RendererUtils.isHideInPublicDisplay(null, defendantNames);
        assertFalse(result, FALSE);
        result = RendererUtils.isHideInPublicDisplay(null, null);
        assertFalse(result, FALSE);
    }
    
    @Test
    void testIsReportingRestricted() {
        // Setup
        SummaryByNameValue testSummaryByNameValue =
            (SummaryByNameValue) DummyPublicDisplayValueUtil.getSummaryByNameValue(false, null);
        CourtListValue testCourtListValue =
            (CourtListValue) DummyPublicDisplayValueUtil.getCourtListValue(true, 0, null);
        final AllCourtStatusValue testAllCourtStatusValue =
            (AllCourtStatusValue) DummyPublicDisplayValueUtil.getAllCourtStatusValue(true, false,
                null);
        // Random object to make return false fire off
        final DefendantName defendantName = DummyDefendantUtil.getDefendantName(true, false);
        boolean result = RendererUtils.isReportingRestricted(testSummaryByNameValue);
        assertTrue(result, TRUE);
        result = RendererUtils.isReportingRestricted(testCourtListValue);
        assertTrue(result, TRUE);
        result = RendererUtils.isReportingRestricted(testAllCourtStatusValue);
        assertTrue(result, TRUE);
        result = RendererUtils.isReportingRestricted(defendantName);
        assertFalse(result, TRUE);
    }

}
