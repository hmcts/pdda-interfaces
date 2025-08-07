package uk.gov.hmcts.framework.services.threadpool;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * ThreadPoolInactiveException.
 * @author pznwc5 The exception is thrown when the thread pool is inactive
 */
public class ThreadPoolInactiveException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    public ThreadPoolInactiveException() {
        super("Invactive thread pool");
    }
}
