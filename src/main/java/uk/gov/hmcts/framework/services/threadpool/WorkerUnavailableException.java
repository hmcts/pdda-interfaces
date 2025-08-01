package uk.gov.hmcts.framework.services.threadpool;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * WorkerUnavailableException.

 * @author pznwc5 The exception is thrown when the thread pool is inactive
 */
public class WorkerUnavailableException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    /**
     * WorkerUnavailableException.

     * @param ex Interrupted exception.
     */
    public WorkerUnavailableException(InterruptedException ex) {
        super(ex.getMessage(), ex);
    }
}
