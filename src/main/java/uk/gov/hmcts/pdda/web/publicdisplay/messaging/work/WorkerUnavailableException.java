package uk.gov.hmcts.pdda.web.publicdisplay.messaging.work;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * WorkerUnavailableException.
 * 
 * @author pznwc5 The exception is thrown when the thread pool is inactive
 */
public class WorkerUnavailableException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * WorkerUnavailableException.
     * 
     * @param ex Interrupted exception
     */
    public WorkerUnavailableException(InterruptedException ex) {
        super(ex.getMessage(), ex);
    }
}
