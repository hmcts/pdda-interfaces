package uk.gov.hmcts.pdda.business.services.pdda.cath;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.scheduler.RemoteTask;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * This class is the controller bean for dealing with CaTH connections.
 */
@Stateless
@Service
public class CathConnectionServiceBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(CathConnectionServiceBean.class);

    private static final String CATH_HEALTH_ENDPOINT =
        "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management";
    private static final String CATH_MAIN_PUBLICATION_ENDPOINT =
        "https://sds-api-mgmt.staging.platform.hmcts.net/pip/data-management/publication";

    private static final String LOG_OUTPUT_SERVICE_UP = "{} is up, with expected response of {} !";
    private static final String LOG_OUTPUT_SERVICE_DOWN =
        "{} is down, with expected response of {} !";

    private static final int STATUS_CODE_401 = 401;
    private static final int STATUS_CODE_404 = 404;

    protected EntityManager entityManager;

    List<UrlPair> urls = new ArrayList<>() {
        private static final long serialVersionUID = -8822781970166180320L;
        {
            add(new UrlPair(CATH_HEALTH_ENDPOINT, STATUS_CODE_401));
            add(new UrlPair(CATH_MAIN_PUBLICATION_ENDPOINT, STATUS_CODE_404));
            add(new UrlPair(CATH_MAIN_PUBLICATION_ENDPOINT, STATUS_CODE_401));
        }
    };


    public CathConnectionServiceBean(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    public CathConnectionServiceBean() {
        super();
    }


    /**
     * Check the status of a URL given an expected http response code.
     * 
     * @param url The URL to check
     * @param expectedStatusCode The expected http response code
     * @return True if the response code matches the expected code, false
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public boolean checkUrl(String url, int expectedStatusCode, boolean useGet) {
        // May get an AssertionError if the status code is not as expected
        // and we want to catch that
        try {
            if (useGet) {
                LOG.debug("Using GET for URL: {}", url);
                given().when().get(url).then().statusCode(expectedStatusCode);
            } else {
                LOG.debug("Using POST for URL: {}", url);
                given().when().post(url).then().statusCode(expectedStatusCode);
            }
            return true;
        } catch (Throwable t) {
            LOG.error("Exception occurred while checking the URL: {}", t.getMessage());
            return false;
        }
    }

    @Override
    public void doTask() throws RemoteException {
        
        //  Check the status of the CaTH URLs
        LOG.debug("Checking the status of the CaTH URLs");

        // Check each url in the table
        Iterator<UrlPair> it = urls.iterator();
        while (it.hasNext()) {
            UrlPair entry = it.next();
            String url = entry.getUrl();
            int expectedStatusCode = entry.getExpectedStatusCode();

            // Get for health Url, Post for others
            boolean useGet = CATH_HEALTH_ENDPOINT.equals(url);
            if (checkUrl(url, expectedStatusCode, useGet)) {
                LOG.info(LOG_OUTPUT_SERVICE_UP, url, expectedStatusCode);
            } else {
                LOG.error(LOG_OUTPUT_SERVICE_DOWN, url, expectedStatusCode);
            }
        }
    }

    class UrlPair {
        String url;
        int expectedStatusCode;

        UrlPair(String url, int expectedStatusCode) {
            this.url = url;
            this.expectedStatusCode = expectedStatusCode;
        }

        public String getUrl() {
            return url;
        }

        public int getExpectedStatusCode() {
            return expectedStatusCode;
        }

    }
}
