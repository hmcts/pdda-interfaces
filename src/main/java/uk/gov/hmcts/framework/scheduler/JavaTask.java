package uk.gov.hmcts.framework.scheduler;

/**

 * Title: The JavaTask interface.


 * Description: Interface to be implemented by a plain java class that is to be called to perform a
 * task.


 * Classes implementing this are expected to provide a no arguments constructor to allow reflective
 * instantiation by the Schedulable class.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface JavaTask {
    /**

     * This method is the one that will be called on the class in the execution of a scheduled task.


     * There should be no exception propagation from within the method implementation, all error
     * handling should be done internally.

     */
    void doTask();
}
