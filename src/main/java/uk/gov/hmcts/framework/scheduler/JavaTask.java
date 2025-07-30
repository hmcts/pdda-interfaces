package uk.gov.hmcts.framework.scheduler;

/**
 * <p>
 * Title: The JavaTask interface.
 * </p>
 * <p>
 * Description: Interface to be implemented by a plain java class that is to be called to perform a
 * task.
 * </p>
 * <p>
 * Classes implementing this are expected to provide a no arguments constructor to allow reflective
 * instantiation by the Schedulable class.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface JavaTask {
    /**
     * <p>
     * This method is the one that will be called on the class in the execution of a scheduled task.
     * </p>
     * <p>
     * There should be no exception propagation from within the method implementation, all error
     * handling should be done internally.
     * </p>
     */
    void doTask();
}
