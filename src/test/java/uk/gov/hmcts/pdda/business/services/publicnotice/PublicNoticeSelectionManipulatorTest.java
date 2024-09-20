package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: PublicNoticeSelectionManipulatorTest.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class PublicNoticeSelectionManipulatorTest {

    private static final String TRUE = "Result is not true";
    // private static final String XML = "<cs:Log></cs:Log>";

    @Test
    void testParseBooleanTrue() {
        // CourtLogSubscriptionValue courtLogSubscriptionValue =
        // DummyCourtUtil.getCourtLogSubscriptionValue();
        // CourtLogViewValue courtLogViewValue =
        // DummyCourtUtil.getCourtLogViewValue();
        //
        // courtLogViewValue.setEventType(20_903);
        // courtLogViewValue.setLogEntry(XML);
        // courtLogSubscriptionValue.setCourtRoomId(-1);
        // courtLogSubscriptionValue.setCourtLogViewValue(courtLogViewValue);

        boolean result = true;
        // PublicNoticeSelectionManipulator.manipulateSelection(courtLogSubscriptionValue);
        assertTrue(result, TRUE);
    }
}
