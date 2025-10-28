package uk.gov.hmcts.pdda.courtlog.exceptions;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * Super class of all runtime exceptions used by the court log component
 * 
 * @author pznwc5
 */
public class CourtLogRuntimeException extends CsUnrecoverableException {
    
    static final long serialVersionUID = 1375974185625217549L;
    
    /**
     * Creates a new CourtLogRuntimeException object.
     * 
     * @param message
     *            the message.
     */
    public CourtLogRuntimeException(String message) {
        super(message);
    }

    /**
     * Creates a new CourtLogRuntimeException object.
     * 
     * @param throwable
     *            the root cause.
     */
    public CourtLogRuntimeException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new CourtLogRuntimeException object.
     * 
     * @param message
     *            the message.
     * @param throwable
     *            the roor cause.
     */
    public CourtLogRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

