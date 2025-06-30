package uk.gov.hmcts.framework.scheduler;

/**
 * <p>
 * Title: The RemoteTask interface.
 * </p>
 * <p>
 * Description: Interface to be implemented by a Session Bean remote interface that is to be called
 * to perform a scheduled task.
 * </p>
 * <p>
 * Interface to be implemented by a Session beam that is to be called to perform a scheduled task.
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
public interface RemoteTask {
    /**
     * <p>
     * This method is the one that will be called on the session bean remote interface in the
     * execution of a scheduled task.
     * </p>
     * <p>
     * There should be no exception propagation from within the method implementation, all error
     * handling should be done internally.
     * </p>
     * 
     * @throws javax.rmi.RemoteException when there is a problem in calling the method.
     */
    void doTask() throws java.rmi.RemoteException;
}
