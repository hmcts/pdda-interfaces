package uk.gov.hmcts.framework.services.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.CsServices;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



/**
 * The Asynchronous loader is used to load data in an asychnronous manner. Designed to instantiate
 * (using a no args constructor) the list of classes in asynchronousloader.properties
 * init.classes(delimited by a ;), that can on instantiation, register either themselves (if
 * loadable)or another class (that is loadable) with the AsynchronousLoader.

 * To have your data scheduled the class must extend AbstractLoadable.</p>
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
public final class AsynchronousLoader implements Runnable {
    private final List<Loadable> jobStack = new ArrayList<>();

    private static final int TIMEOUT = 100_000;

    private final Thread loaderThread;

    private static final Logger LOG = LoggerFactory.getLogger(AsynchronousLoader.class);

    private static AsynchronousLoader instance = new AsynchronousLoader();

    private boolean continueRunning = true;
    private boolean initStarted;

    private AsynchronousLoader() {
        loaderThread = new Thread(this);
        loaderThread.start();
    }

    public void init() {
        synchronized (this) {
            // Ensure init is only called once
            if (initStarted) {
                return;
            }
            initStarted = true;
            continueRunning = true;
            StringTokenizer st = new StringTokenizer(CsServices.getConfigServices()
                .getProperties("asynchronousloader").getProperty("init.classes"), ";");
            while (st.hasMoreTokens()) {
                initClass(st);
    
            }
        }
    }

    private void initClass(StringTokenizer st) {
        synchronized (this) {
            try { // Note hardened against all expected exceptions - if exception
                // unexpected then big bang.
                LOG.debug("!!!!!!!!!!!!!!!!!!!!!!!!!INITIALISING CLASS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Class<?> myClass = Class.forName(st.nextToken()); // Should do
                // static stuff
                // here.
                myClass.newInstance(); // Initiate to allow non static
                // registration with AsynchronousLoader
            } catch (ClassNotFoundException cnfe) {
                LOG.error("Could not find class.", cnfe);
            } catch (ExceptionInInitializerError eiie) {
                LOG.error("Could not initialise class.", eiie);
            } catch (LinkageError le) {
                LOG.error("Could not link class.", le);
            } catch (IllegalAccessException iae) {
                LOG.error("Could not access class.", iae);
            } catch (InstantiationException ie) {
                LOG.error("Could not instantiate class.", ie);
            } catch (SecurityException se) {
                LOG.error("Security problem in instantiating class.", se);
            } catch (RuntimeException t) {
                LOG.error("Unexpected problem.", t);
            }
        }
    }

    /**
     * Request the loader to load the job.

     * @param job the
     */
    public void addToLoader(Loadable job) {
        jobStack.add(job);
        synchronized (loaderThread) {
            loaderThread.notifyAll(); // NOSONAR
        }

    }

    /**
     * Run the current Jobs.
     */
    @Override
    public void run() {
        runCurrentJobs();

    }

    /**
     * Attempt to run all jobs, once they have run successfully, stop running them.
     */
    private void runCurrentJobs() {
        while (continueRunning) {
            try {
                synchronized (jobStack) {
                    for (Loadable referenceData : jobStack) {
                        if (!referenceData.isLoaded()) { // Only attempt load if
                            // not loaded.
                            performLoad(referenceData);
                        }
                    }
                }
                synchronized (this) {
                    wait(TIMEOUT);
                }
            } catch (InterruptedException e) {
                LOG.warn("Wait on load interrupted.");
                Thread.currentThread().interrupt();
            } catch (RuntimeException e) { // Continue running no matter what...
                // the matter might rectify itself.
                LOG.error("Error: {}", e.getMessage()); // To change body of catch statement use
                // Options | File Templates.
            }
        }
    }

    /**
     * Perform the load.
     */
    private void performLoad(Loadable referenceData) {
        synchronized (this) {
            LOG.info("Data Loader loading job: {}", referenceData.getName());
            referenceData.setLoaded(false);
            referenceData.load();
            referenceData.setLoaded(true);
            referenceData.notifyAll();
            LOG.info("Data Loader loaded job: {}", referenceData.getName());
        }
    }

    /**
     * Factory method to provide an instamce of an AsynchronousLoader.

     * @return an instamce of an AsynchronousLoader
     */
    public static AsynchronousLoader getInstance() {
        return instance;
    }

    /**
     * Finish running the Asynchronous Loader, usually to be called when exiting the application.
     */
    public void finishLoading() {
        if (!initStarted) {
            // PR6080 - finishLoading called before init() prevents subsequent
            // calls to init() doing its work
            return;
        }
        continueRunning = false;
        synchronized (this) {
            notifyAll();
        }
    }
}
