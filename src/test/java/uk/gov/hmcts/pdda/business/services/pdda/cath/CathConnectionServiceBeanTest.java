package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EasyMockExtension.class)
class CathConnectionServiceBeanTest {

    private static final Logger LOG = LoggerFactory.getLogger(CathConnectionServiceBeanTest.class);

    @Mock
    private CathConnectionServiceBean mockCathConnectionServiceBean;

    @TestSubject
    private final CathConnectionServiceBean classUnderTest = new CathConnectionServiceBean();

    @Test
    void testCheckUrls() {
        String url = "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management";
        int expectedStatusCode = 401;
        assertTrue(classUnderTest.checkUrl(url, expectedStatusCode, true),
            "checkUrl should return 'true'");

        url = "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management/publication";
        expectedStatusCode = 404;
        assertTrue(classUnderTest.checkUrl(url, expectedStatusCode, true),
            "checkUrl should return 'true'");
    }

    @Test
    void testDoTask() {

        // Test 1 - regular path through the code
        /*
         * try { classUnderTest.doTask(); } catch (RemoteException e) {
         * LOG.error("Exception occurred while checking the URL: {}", e.getMessage()); }
         */

        // Test 2 - irregular path through the code
        try {
            // EasyMock.expect(mockCathConnectionServiceBean.checkUrl(
            // "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management", 401))
            // .andReturn(true);

            EasyMock
            .expect(mockCathConnectionServiceBean.checkUrl(
                    "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management", 401,
                    true))
                .andReturn(false);
            EasyMock.replay(mockCathConnectionServiceBean);
            classUnderTest.doTask();

            /*
             * EasyMock.expect(mockCathConnectionServiceBean.checkUrl(
             * "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management/publication",
             * 404)).andReturn(false); EasyMock.expect(mockCathConnectionServiceBean.checkUrl(
             * "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management/publication",
             * 401)).andReturn(true);
             * 
             * EasyMock.replay(mockCathConnectionServiceBean);
             * 
             * // Test 1 classUnderTest.doTask();
             */
        } catch (RemoteException e) {
            LOG.error("Exception occurred while checking the URL: {}", e.getMessage());
        }

    }

}
