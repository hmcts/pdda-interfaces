package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;



import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.hmcts.pdda.web.publicdisplay.messaging.jms.MessagingMode;

import java.util.Locale;

import jakarta.annotation.Resource;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Topic;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    /** Number of workers per subscription. */
    private static final String NUM_SUBSCRIPTION_WORKERS = "num.subscription.workers";

    /** Messaging mode. */
    private static final String MESSAGING_MODE = "messaging.mode";

    /** Number of workers for initialization. */
    private static final String NUM_INITIALIZATION_WORKERS = "num.initialization.workers";

    /** Delay after each initialization. */
    private static final String INITIALIZATION_DELAY = "initialization.delay";

    /** P2P messaging mode constant. */
    private static final String P2P = "P2P";

    /** Pub/Sub messaging mode constant. */
    private static final String PUB_SUB = "PubSub";

    /** JMS Topic. */
    private static final String TOPIC = "PDDATopic";

    @Resource
    private ConnectionFactory connectionFactory;

    private Topic topic;
    
    private EntityManagerFactory entityManagerFactory;
    
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

        // Set the number of workers per subscription
        setNumSubscriptionWorkers(service);

        // Set the delay after each initialization
        setInitializationDelay(service);

        // Set the number of workers for initialization
        setNumInitializationWorkers(service);

        // Set the messaging mode
        setMessagingMode(service);

        service.setConnectionFactory(connectionFactory);

        // For Spring Boot JMS
        if (topic == null) {
            log.debug("Setting topic: " + TOPIC);
            topic = new ActiveMQTopic(TOPIC);
        }

        service.setTopic(topic);

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
     * Sets the messaging mode.
     * 
     * @param service InitializationService
     */
    private void setMessagingMode(InitializationService service) {
        String msgMode = this.getInitParameter(MESSAGING_MODE);
        if (msgMode != null) {
            if (P2P.equals(msgMode)) {
                service.setMessagingMode(MessagingMode.P2P);
            } else if (PUB_SUB.equals(msgMode)) {
                service.setMessagingMode(MessagingMode.PUB_SUB);
            }
            log.debug("Messaging mode: " + msgMode);
        }
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
     * Sets the number of subscription workers.
     * 
     * @param service InitializationService
     */
    private void setNumSubscriptionWorkers(InitializationService service) {
        String numSubscriptionWorkers = getInitParameter(NUM_SUBSCRIPTION_WORKERS);
        if (numSubscriptionWorkers != null) {
            service.setNumSubscriptionWorkers(Integer.parseInt(numSubscriptionWorkers));
            log.debug("Subscription workers: " + numSubscriptionWorkers);
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
