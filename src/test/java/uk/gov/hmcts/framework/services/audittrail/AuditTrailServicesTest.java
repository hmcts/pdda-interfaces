package uk.gov.hmcts.framework.services.audittrail;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: AuditTrailService Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class AuditTrailServicesTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private AuditTrailEvent mockAuditTrailEvent;


    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testContextInitialized() {
        // Expects
        EasyMock.expect(mockAuditTrailEvent.getTimestamp()).andReturn(new Date());
        EasyMock.expect(mockAuditTrailEvent.getWorkstationId()).andReturn("workstationId");
        EasyMock.expect(mockAuditTrailEvent.getUserId()).andReturn("userId");
        EasyMock.expect(mockAuditTrailEvent.getCourtHouseId()).andReturn("courtHouseId");
        EasyMock.expect(mockAuditTrailEvent.getEvtType()).andReturn("eventType");
        EasyMock.expect(mockAuditTrailEvent.isSuccess()).andReturn(true);
        EasyMock.expect(mockAuditTrailEvent.getCaseId()).andReturn(345_678);
        EasyMock.expect(mockAuditTrailEvent.getEventSpecificData()).andReturn("evtSpecificData");
        EasyMock.replay(mockAuditTrailEvent);
        // Run
        boolean result = false;
        try {
            AuditTrailService.getInstance().createAuditRecord(mockAuditTrailEvent);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        EasyMock.verify(mockAuditTrailEvent);
        assertTrue(result, TRUE);
    }
}
