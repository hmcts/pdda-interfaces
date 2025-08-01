package uk.gov.hmcts.framework.scheduler;

/**

 * Title: The Stoppable interface.


 * Description: Interface to be implemented by a plain java class that is to be called to perform a
 * task and needs notification during cleanup.


 * Classes implementing this are expected to provide a no arguments constructor to allow reflective
 * instantiation by the Schedulable class.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 */
public interface Stoppable extends JavaTask {
    /**

     * This method is the one that will be called for cleaning up.


     * There should be no exception propagation from within the method implementation, all error
     * handling should be done internally.

     */
    void stop();
}
