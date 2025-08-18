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

@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class CathConnectionServiceBeanTest {

    private static final Logger LOG = LoggerFactory.getLogger(CathConnectionServiceBeanTest.class);

    @Mock
    private CathConnectionServiceBean mockCathConnectionServiceBean;

    @TestSubject
    private final CathConnectionServiceBean classUnderTest = new CathConnectionServiceBean();

    @Test
    void testCheckUrls() {
        String url1 = "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management";
        int[] expectedStatusCodes1 = {401, 404};
        boolean result1 = false;
        for (int code : expectedStatusCodes1) {
            result1 = classUnderTest.checkUrl(url1, code, true);
            if (result1) {
                break;
            }
        }
        if (!result1) {
            LOG.warn("WARNING: checkUrl did not return 'true' for 401 or 404 on URL1");
        }

        String url2 = "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management/publication";
        int expectedStatusCode2 = 404;
        boolean result2 = classUnderTest.checkUrl(url2, expectedStatusCode2, true);
        if (!result2) {
            LOG.warn("WARNING: checkUrl did not return 'true' for URL2");
        }
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

             * EasyMock.replay(mockCathConnectionServiceBean);

             * // Test 1 classUnderTest.doTask();
             */
        } catch (RemoteException e) {
            LOG.error("Exception occurred while checking the URL: {}", e.getMessage());
        }

    }

}
