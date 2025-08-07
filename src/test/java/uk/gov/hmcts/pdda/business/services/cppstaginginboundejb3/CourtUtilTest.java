package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CourtUtilTest.
 */
@ExtendWith(EasyMockExtension.class)
class CourtUtilTest  {

    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String NOTNULL = "Result is Null";

    @Test
    void testisCppCourt() {
        // Setup
        String courtCode = "457";
        List<XhbCourtDao> data = new ArrayList<>();
        data.add(DummyCourtUtil.getXhbCourtDao(Integer.parseInt(courtCode), "Court1"));
        // Run
        boolean result = CourtUtils.isCppCourt(data);
        // Checks
        assertTrue(result, TRUE);
    }

    @Test
    void testisCppCourtFail() {
        // Setup
        List<XhbCourtDao> data = new ArrayList<>();
        // Run
        boolean result = CourtUtils.isCppCourt(data);
        // Checks
        assertFalse(result, FALSE);
    }

    @Test
    void hasCourtSites() {
        // Setup
        String clobData = "Test";
        // Run
        Boolean resultValue = CourtUtils.hasCourtSites(clobData);
        // Checks
        assertNotNull(resultValue, NOTNULL);
    }
}
