package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CaseStatusEvent;
import uk.gov.hmcts.DummyEventUtil;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**

 * Title: PublicNoticeSelectionManipulatorTest.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class PublicNoticeSelectionManipulatorTest {

    private static final String FALSE = "Result is True";
    private static final int EVENT_ID_TRIAL_PROSECUTION_CASE = 20_903;
    private static final String LOGENTRY_E20903 =
        "<event><E20903_Prosecution_Case_Options><E20903_PCO_Type>NoneExistantType"
            + "</E20903_PCO_Type></E20903_Prosecution_Case_Options></event>";

    @Test
    void testManipulateSelection() {
        // Setup
        CaseStatusEvent caseStatusEvent = DummyEventUtil.getCaseStatusEvent();
        CourtLogSubscriptionValue courtLogSubscriptionValue =
            caseStatusEvent.getCaseCourtLogInformation().getCourtLogSubscriptionValue();
        courtLogSubscriptionValue.getCourtLogViewValue()
            .setEventType(EVENT_ID_TRIAL_PROSECUTION_CASE);
        courtLogSubscriptionValue.getCourtLogViewValue().setLogEntry(LOGENTRY_E20903);
        // Run
        boolean result =
            PublicNoticeSelectionManipulator.manipulateSelection(courtLogSubscriptionValue);
        // Checks
        assertFalse(result, FALSE);
    }
}
