package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EasyMockExtension.class)
class CathConnectionServiceBeanTest {


    @TestSubject
    private final CathConnectionServiceBean classUnderTest = new CathConnectionServiceBean();

    @Test
    void checkUrls() {
        String url = "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management";
        int expectedStatusCode = 401;
        assertTrue(classUnderTest.checkUrl(url, expectedStatusCode),
            "checkUrl should return 'true'");

        url = "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management/publication";
        expectedStatusCode = 404;
        assertTrue(classUnderTest.checkUrl(url, expectedStatusCode),
            "checkUrl should return 'true'");
    }

}
