package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;


import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * InitServlet.
 * 
 * @author pznwc5 Servlet used for managing the initialization remove
 */
public class InitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(InitServlet.class);

    /** Retry period. */
    private static final String RETRY_PERIOD = "retry.period";

    /** Number of workers for initialization. */
    private static final String NUM_INITIALIZATION_WORKERS = "num.initialization.workers";

    /** Delay after each initialization. */
    private static final String INITIALIZATION_DELAY = "initialization.delay";
    
    private final EntityManagerFactory entityManagerFactory;
    
    public InitServlet(EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Override the init method to initialize services.
     * 
     * @param config the servlet config
     * 
     * @throws ServletException Exception
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        log.debug("init({})", config);
        super.init(config);

        // Get an instance of the initialization service
        InitializationService service = InitializationService.getInstance();

        // Setup the entityManagerFactory
        service.setEntityManagerFactory(this.entityManagerFactory);
        
        // Setup the default locale
        service.setDefaultLocale(getDefaultLocale());

        // Set the retry interval for initialization
        setRetryPeriod(service);

        // Set the delay after each initialization
        setInitializationDelay(service);

        // Set the number of workers for initialization
        setNumInitializationWorkers(service);

        // Start initialization
        service.initialize();

        log.debug("Initialization service scheduled");

    }

    /**
     * Override the service method to stop the servlet from serving requests.
     * 
     * @param req The HTTP request
     * @param res The HTTP response
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * Override the destroy method to cleanup resources.
     */
    @Override
    public void destroy() {
        InitializationService.getInstance().destroy();
    }

    /**
     * Sets the number of initialization workers.
     * 
     * @param service InitializationService
     */
    private void setNumInitializationWorkers(InitializationService service) {
        String numInitializationWorkers = this.getInitParameter(NUM_INITIALIZATION_WORKERS);
        if (numInitializationWorkers != null) {
            service.setNumInitializationWorkers(Integer.parseInt(numInitializationWorkers));
            log.debug("Initialization workers: " + numInitializationWorkers);
        }
    }

    /**
     * Sets initialization delay.
     * 
     * @param service InitializationService
     */
    private void setInitializationDelay(InitializationService service) {
        String initializationDelay = this.getInitParameter(INITIALIZATION_DELAY);
        if (initializationDelay != null) {
            service.setInitializationDelay(Long.parseLong(initializationDelay));
            log.debug("Initialization delay: " + initializationDelay);
        }
    }

    /**
     * Sets the retry period for initialization.
     * 
     * @param service InitializationService
     */
    private void setRetryPeriod(InitializationService service) {
        String retryPeriod = this.getInitParameter(RETRY_PERIOD);
        if (retryPeriod != null) {
            service.setRetryPeriod(Long.parseLong(retryPeriod));
            log.debug("Retry period: " + retryPeriod);
        }
    }

    private static Locale getDefaultLocale() {
        return new Locale(getDefaultLanguage(), getDefaultCountry());
    }

    private static String getDefaultLanguage() {
        return System.getProperty(
            "uk.gov.hmcts.pdda.business.vos.translation.TranslationBundlesCache.defaultLanguage",
            "en");
    }

    private static String getDefaultCountry() {
        return System.getProperty(
            "uk.gov.hmcts.pdda.business.vos.translation.TranslationBundlesCache.defaultCountry",
            "GB");
    }

}
