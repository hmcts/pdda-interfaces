package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;

import java.util.Locale;

/**
 * <p/>
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments.
 * 
 * @author pznwc5
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public final class InitializationService {
    /**
     * One second.
     */
    public static final long ONE_SECOND = 1000L;

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(InitializationService.class);

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
    private int numInitializationWorkers = 10;

    /**
     * Delay after each court initialization.
     */
    private long initializationDelay = 5 * ONE_SECOND;

    /**
     * Retry period in case of initialization failure.
     */
    private long retryPeriod = 60 * ONE_SECOND;

    private EntityManagerFactory entityManagerFactory;
    
    /**
     * DOn't instantiate me.
     */
    private InitializationService() {
        // private constructor
    }

    /**
     * Whether the service has been initialized.
     * 
     * @return boolean
     */
    public boolean isInitialized() {
        synchronized (this) {
            return initialized;
        }
    }

    /**
     * Set the number of workers for initialization.
     * 
     * @param numInitializationWorkers int
     */
    public void setNumInitializationWorkers(int numInitializationWorkers) {
        this.numInitializationWorkers = numInitializationWorkers;
    }

    /**
     * Set the delay after each initialization.
     * 
     * @param initializationDelay long
     */
    public void setInitializationDelay(long initializationDelay) {
        this.initializationDelay = initializationDelay;
    }

    /**
     * Set the retry period.
     * 
     * @param retryPeriod long
     */
    public void setRetryPeriod(long retryPeriod) {
        this.retryPeriod = retryPeriod;
    }

    /**
     * Method to start initialization.
     */
    public void initialize() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
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
            }

            public void runNow() {
                // Run until initialization is done
                while (!checkMidtier()) {
                    try {
                        Thread.sleep(retryPeriod);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage(), ex);
                        Thread.currentThread().interrupt();
                    }
                }

                // DO initialization
                doInitialize();

                // Set the initialization flag
                synchronized (InitializationService.class) {
                    initialized = true;
                }

            }
        });
        th.start();
    }

    /**
     * Method to destroy.
     */
    public void destroy() {
        log.debug("Public display uninitialized");
    }

    /**
     * Singleton accessor.
     * 
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
    private void doInitialize() {
        log.debug("doInitialize()");
        // Get the court ids
        int[] courtIds = DisplayConfigurationReader.getInstance().getConfiguredCourtIds();

        // Start initial rendering
        new DocumentInitializer(courtIds, numInitializationWorkers, initializationDelay)
            .initialize();
    }

    /**
     * Method checks whether the publicdisplay is running.
     * 
     * @return boolean
     */
    private boolean checkMidtier() {
        try {
            // Get display configuration reader
            DisplayConfigurationReader.getInstance();
            log.info("Initialized display configuration reader");
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
}
