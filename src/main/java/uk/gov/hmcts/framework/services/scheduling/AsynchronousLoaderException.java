package uk.gov.hmcts.framework.services.scheduling;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * AsynchronousLoaderException. 

 * Created by IntelliJ IDEA. User: qzd3k3 Date: 13-Mar-2003 Time: 12:42:59 To change this template
 * use Options | File Templates.
 */
public class AsynchronousLoaderException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    /**
     * AsynchronousLoaderException.

     * @param logMessage String
     */
    public AsynchronousLoaderException(String logMessage) {
        super(logMessage);
    }

    /**
     * AsynchronousLoaderException.

     * @param cause original exception caught
     */
    public AsynchronousLoaderException(Throwable cause) {
        super(cause);
    }

    /**
     * AsynchronousLoaderException.

     * @param cause original exception caught
     */
    public AsynchronousLoaderException(String logMessage, Throwable cause) {
        super(logMessage, cause);
    }

    public AsynchronousLoaderException() {
        super();
    }
}
