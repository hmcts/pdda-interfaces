package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ActivateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RulesTest {

    private static final String TRUE = "Result is not True";

    @BeforeAll
    public static void setUp() {
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    void testReportingRestrictionChangedRuleValid() {
        boolean result = testRule(new ReportingRestrictionChangedRule(), getDummyPublicNoticeEvent(true), true);
        assertTrue(result, TRUE);
    }

    @Test
    void testReportingRestrictionChangedRuleInvalid() {
        boolean result = testRule(new ReportingRestrictionChangedRule(), getDummyPublicNoticeEvent(false), false);
        assertTrue(result, TRUE);
    }

    @Test
    void testReportingRestrictionChangedRuleIncorrectType() {
        boolean result = testRule(new ReportingRestrictionChangedRule(), getDummyActivateCaseEvent(true), false);
        assertTrue(result, TRUE);
    }

    @Test
    void testDefendantNameChangedRuleValid() {
        boolean result = testRule(new DefendantNameChangedRule(), getDummyActivateCaseEvent(true), true);
        assertTrue(result, TRUE);
    }

    @Test
    void testCaseBeingHeardRuleValid() {
        boolean result = testRule(new CaseBeingHeardRule(), getDummyActivateCaseEvent(true), true);
        assertTrue(result, TRUE);
    }

    @Test
    void testCaseBeingHeardRuleInvalid() {
        boolean result = testRule(new CaseBeingHeardRule(), getDummyActivateCaseEvent(false), false);
        assertTrue(result, TRUE);
    }

    @Test
    void testCaseBeingHeardRuleIncorrectType() {
        boolean result = testRule(new CaseBeingHeardRule(), getDummyConfigurationChangeEvent(), false);
        assertTrue(result, TRUE);
    }

    private boolean testRule(Rule rule, PublicDisplayEvent event, boolean isValid) {
        assertNotNull(rule, "Result is Null");
        boolean result = rule.isValid(event);
        if (isValid) {
            assertTrue(result, "Result is not True");
            return result;
        } else {
            assertFalse(result, "Result is not False");
            return !result;
        }
    }

    private CourtConfigurationChange getDummyCourtConfigurationChange() {
        return new CourtConfigurationChange(81);
    }

    private CourtRoomIdentifier getDummyCourtRoomIdentifier() {
        return new CourtRoomIdentifier(-99, null);
    }

    private CaseChangeInformation getDummyCaseChangeInformation() {
        return new CaseChangeInformation(false);
    }

    private PublicNoticeEvent getDummyPublicNoticeEvent(boolean reportingRestrictionsChanged) {
        return new PublicNoticeEvent(getDummyCourtRoomIdentifier(), reportingRestrictionsChanged);
    }

    private ActivateCaseEvent getDummyActivateCaseEvent(boolean isCaseActive) {
        ActivateCaseEvent result =
            new ActivateCaseEvent(getDummyCourtRoomIdentifier(), getDummyCaseChangeInformation());
        result.setCaseActive(isCaseActive);
        return result;
    }

    private ConfigurationChangeEvent getDummyConfigurationChangeEvent() {
        return new ConfigurationChangeEvent(getDummyCourtConfigurationChange());
    }
}
