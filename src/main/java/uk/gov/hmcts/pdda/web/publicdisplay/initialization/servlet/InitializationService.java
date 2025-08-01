package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStoreFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.work.EventWorkManager;

import java.util.Locale;

/**
 * <p/>
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments.

 * @author pznwc5
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement",
    "PMD.AvoidProtectedMethodInFinalClassNotExtending"})
public final class InitializationService {
    /**
     * One second.
     */
    public static final long ONE_SECOND = 1000L;

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(InitializationService.class);

    // New setter for mocking
    private DisplayConfigurationReader displayConfigurationReader;

    /**
     * Singleton instance.
     */
    private static final InitializationService SELF = new InitializationService();

    /**
     * A flag to mark successful initialization.
     */
    private boolean initialized;

    /**
     * Default Locale.
     */
    private Locale defaultLocale;

    /**
     * Should the initialisation fail, this variable will hold the exception that caused it to fail.
     */
    private Throwable initialisationFailure;

    /**
     * Number of workers for initialization.
     */
    private int numInitializationWorkers = 5;

    /**
     * Delay after each court initialization.
     */
    private long initializationDelay = 10 * ONE_SECOND;

    /**
     * Retry period in case of initialization failure.
     */
    private long retryPeriod = 60 * ONE_SECOND;

    private EntityManagerFactory entityManagerFactory;
    
    private Environment env;
    
    /**
     * DOn't instantiate me.
     */
    private InitializationService() {
        // private constructor
    }

    /**
     * Whether the service has been initialized.

     * @return boolean
     */
    public boolean isInitialized() {
        synchronized (this) {
            return initialized;
        }
    }

    /**
     * Set the number of workers for initialization.

     * @param numInitializationWorkers int
     */
    public void setNumInitializationWorkers(int numInitializationWorkers) {
        this.numInitializationWorkers = numInitializationWorkers;
    }

    /**
     * Set the delay after each initialization.

     * @param initializationDelay long
     */
    public void setInitializationDelay(long initializationDelay) {
        this.initializationDelay = initializationDelay;
    }

    /**
     * Set the retry period.

     * @param retryPeriod long
     */
    public void setRetryPeriod(long retryPeriod) {
        this.retryPeriod = retryPeriod;
    }

    public void setDisplayConfigurationReader(DisplayConfigurationReader reader) {
        this.displayConfigurationReader = reader;
    }

    /**
     * Method to start initialization.
     */
    public void initialize() {
        Thread th = new Thread(() -> {
            try {
                if (log.isDebugEnabled()) {
                    long startTime = System.currentTimeMillis();
                    long startTotalMemory = Runtime.getRuntime().totalMemory();
                    long startFreeMemory = Runtime.getRuntime().freeMemory();
                    long startUsedMemory = startTotalMemory - startFreeMemory;
                    runNow();
                    long endTime = System.currentTimeMillis();
                    long endTotalMemory = Runtime.getRuntime().totalMemory();
                    long endFreeMemory = Runtime.getRuntime().freeMemory();
                    long endUsedMemory = endTotalMemory - endFreeMemory;
                    log.debug(
                        "Public display initialisation took {} ms and approximately {} bytes.",
                        endTime - startTime, endUsedMemory - startUsedMemory);
                } else {
                    runNow();
                }
            } catch (RuntimeException t) {
                log.error(t.getMessage(), t);
                initialisationFailure = t;
            }
        });
        th.start();
    }

    protected void runNow() {
        while (!checkMidtier()) {
            try {
                Thread.sleep(retryPeriod);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }

        doInitialize();

        synchronized (InitializationService.class) {
            initialized = true;
        }
    }


    /**
     * Method to destroy.
     */
    public void destroy() {
        log.debug("Public display uninitialized");
    }

    /**
     * Singleton accessor.

     * @return InitializationService
     */
    public static InitializationService getInstance() {
        return SELF;
    }

    public Throwable getInitialisationFailure() {
        return initialisationFailure;
    }

    /**
     * Perform initialization.
     */
    protected void doInitialize() {
        log.debug("doInitialize()");

        EventStore eventStore = EventStoreFactory.getEventStore(); // still static
        log.info("Shared EventStore instance: {}", System.identityHashCode(eventStore));

        EventWorkManager eventWorkManager =
            new EventWorkManager(eventStore, numInitializationWorkers);
        eventWorkManager.start();
        log.info("Started EventWorkManager with {} worker(s).", numInitializationWorkers);

        // Allow mock injection
        int[] courtIds = (displayConfigurationReader != null)
            ? displayConfigurationReader.getConfiguredCourtIds()
            : DisplayConfigurationReader.getInstance().getConfiguredCourtIds();

        new DocumentInitializer(courtIds, numInitializationWorkers, initializationDelay)
            .initialize();

        log.info("Initialization complete.");
    }


    /**
     * Method checks whether the publicdisplay is running.

     * @return boolean
     */
    protected boolean checkMidtier() {
        try {
            if (displayConfigurationReader != null) {
                log.info("Initialized display configuration reader (mocked)");
            } else {
                DisplayConfigurationReader.getInstance(); // still static
                log.info("Initialized display configuration reader");
            }
            return true;
        } catch (RuntimeException ne) {
            log.warn(ne.getMessage(), ne);
            log.warn("Midtier unavailable");
            return false;
        }
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    public Environment getEnvironment() {
        return env;
    }

    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
