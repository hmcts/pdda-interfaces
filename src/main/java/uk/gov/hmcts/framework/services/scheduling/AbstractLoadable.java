package uk.gov.hmcts.framework.services.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Sub classes must provide a concrete <b>public void load()</b> method and a <b>getName()</b>
 * method. Each method that accesses the loaded data must call the <b>waitOnLoad()</b> method before
 * accesing the data.
 * 
 * @see AsynchronousLoader
 * 
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public abstract class AbstractLoadable implements Loadable {
    private boolean loaded; // Never loaded in the first instance.

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLoadable.class);

    /**
     * A callback method for the loader.
     * 
     * @param loaded boolean
     */
    @Override
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Method used to determine whether the job has loaded successfully.
     * 
     * @return whether this AbstractLoadable has loaded successfully.
     */
    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * This method waits until either the data has loaded or <b>TIMEOUT</b> milliseconds have
     * elapsed.
     * 
     * @throws AsynchronousLoaderException if the timeout is reached.
     */
    protected void waitOnLoad() {
        try {
            synchronized (this) {
                if (!loaded) {
                    LOG.info("Waiting on load.");
                    wait(TIMEOUT);
                    if (!loaded) {
                        throw new AsynchronousLoaderException(
                            "Timeout of " + TIMEOUT + " milliseconds reached, data not loaded.");
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsynchronousLoaderException("Wait on load interrupted.", e);
        }
    }
}
