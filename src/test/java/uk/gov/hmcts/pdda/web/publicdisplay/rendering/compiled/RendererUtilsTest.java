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
    
    @Test
    void hasEvent_returnsFalseForNonPublicDisplayObjects() {
        // A random object (not a PublicDisplayValue) should return false
        assertFalse(RendererUtils.hasEvent("not-a-public-display-value"),
            "Non-PublicDisplayValue should not have event");
    }

    
    @Test
    void isDefendantNamesShouldOverspill_respectsMaxDefendants() {
        // Build a small list of defendants, some hidden, some visible.
        Collection<DefendantName> names = new ArrayList<>();
        // Add 17 visible defendants -> overspill should be true
        for (int i = 0; i < 17; i++) {
            names.add(DummyDefendantUtil.getDefendantName(false, false)); // not hidden
        }
        assertTrue(RendererUtils.isDefendantNamesShouldOverspill(names), "17 visible defendants should overspill");

        // Mixed hidden: fewer visible than threshold -> no overspill
        Collection<DefendantName> names2 = new ArrayList<>();
        // 10 visible + 10 hidden -> visible count 10 <= 16 -> no overspill
        for (int i = 0; i < 10; i++) {
            names2.add(DummyDefendantUtil.getDefendantName(false, false));
        }
        for (int i = 0; i < 10; i++) {
            names2.add(DummyDefendantUtil.getDefendantName(true, false));
        }
        assertFalse(RendererUtils.isDefendantNamesShouldOverspill(names2),
            "10 visible defendants should not overspill");
    }


}
