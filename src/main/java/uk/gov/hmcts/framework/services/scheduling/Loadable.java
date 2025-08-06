package uk.gov.hmcts.framework.services.scheduling;

public interface Loadable {
    int TIMEOUT = 100_000;

    /**
     * This method is overloaded by subclasses to provide a callback method that performs the
     * loading task.
     */
    void load();

    /**
     * This method is overloaded by subclasses to supply a name for the task for logging purposes.
     */
    String getName();

    /**
     * A callback method for the loader.

     * @param loaded boolean
     */
    void setLoaded(boolean loaded);

    /**
     * Method used to determine whether the job has loaded successfully.

     * @return whether this Loadable has loaded successfully.
     */
    boolean isLoaded();
}
