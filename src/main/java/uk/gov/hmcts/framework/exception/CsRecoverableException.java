package uk.gov.hmcts.framework.exception;

/**
 * Base exception class for application exceptions in the CS Hub framework. Supports exception
 * chaining, maintaining the cause and providing getCause() method. Allows additional messages to be
 * provided both for the user (use of Message class) and the developer (String errorMessage).
 * Creation date: (10/4/01 1:15:43 PM)

 * @author: Pete Raymond
 * @author: Kevin Buckthorpe
 * @author: Bal Bhamra
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class CsRecoverableException extends AbstractCsException {
    private static final long serialVersionUID = 1L;

    protected String idNum;

    public CsRecoverableException() {
        super();
        setCause(null);
    }

    /**
     * CsRecoverableException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CsRecoverableException(String errorKey, String logMessage, Throwable cause) {
        super(logMessage);
        setCause(cause);
        getCsExceptionImpl().addMessage(new Message(errorKey));
        getCsExceptionImpl().createId();
    }

    /**
     * CsRecoverableException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     */
    public CsRecoverableException(String errorKey, String logMessage) {
        super(logMessage);
        setCause(null);
        getCsExceptionImpl().addMessage(new Message(errorKey));
        getCsExceptionImpl().createId();
    }

    /**
     * CsRecoverableException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     */
    public CsRecoverableException(String errorKey, Object[] parameters, String logMessage) {
        super(logMessage);
        setCause(null);
        getCsExceptionImpl().addMessage(new Message(errorKey, parameters));
        getCsExceptionImpl().createId();
    }

    /**
     * CsRecoverableException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CsRecoverableException(String errorKey, Object[] parameters, String logMessage,
        Throwable cause) {
        super(logMessage);
        setCause(cause);
        getCsExceptionImpl().addMessage(new Message(errorKey, parameters));
        getCsExceptionImpl().createId();
    }

    /**
     * Get message.

     * @return String: the error message for logging
     */
    @Override
    public String getMessage() {
        String msg = super.getMessage();

        if (msg != null) {
            return idNum + msg;
        } else {
            return idNum;
        }
    }
}
