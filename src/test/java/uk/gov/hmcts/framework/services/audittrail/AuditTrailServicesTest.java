package uk.gov.hmcts.framework.services.audittrail;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;

/**
 * <p>
 * Title: AuditTrailService Test.
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
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class AuditTrailServicesTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private AuditTrailEvent mockAuditTrailEvent;


    @BeforeAll
    public static void setUp() throws Exception {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() throws Exception {
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
        EasyMock.expect(mockAuditTrailEvent.getCaseId()).andReturn(Integer.valueOf(345678));
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
