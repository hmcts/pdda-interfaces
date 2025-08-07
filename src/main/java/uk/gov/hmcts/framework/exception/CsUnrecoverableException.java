package uk.gov.hmcts.framework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Base exception class for system exceptions in the CS Hub framework. Supports exception chaining,
 * maintaining the cause and providing getCause() method. Allows additional messages to be provided
 * both for the user (use of Message class) and the developer (String errorMessage). Creation date:
 * (10/4/01 1:15:43 PM)
 * 
 * @author: Pete Raymond
 * @author: Kevin Buckthorpe
 * @author: Bal Bhamra
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class CsUnrecoverableException extends AbstractCsException {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(CsUnrecoverableException.class);

    private static final String CAUSED_BY = "Caused by:";

    /**
     * CSUnrecoverableException.
     */
    public CsUnrecoverableException() {
        super();
        getCsExceptionImpl().createId();
    }

    /**
     * Overloaded constructor, taking in a Message and a string log message as arguments.
     * 
     * @param newUserMessage a Message object
     * @param newLogMsg String
     */
    public CsUnrecoverableException(Message newUserMessage, String newLogMsg) {
        super(newLogMsg);
        getCsExceptionImpl().addMessage(newUserMessage);
        getCsExceptionImpl().createId();
    }

    /**
     * Overloaded constructor, taking in a Message, Throwable object and a string with the error
     * message as arguments.
     * 
     * @param newUserMessage a Message object
     * @param cause Throwable
     * @param newLogMsg String
     */
    public CsUnrecoverableException(Message newUserMessage, Throwable cause, String newLogMsg) {
        super(newLogMsg);
        setCause(cause);
        getCsExceptionImpl().addMessage(newUserMessage);
        getCsExceptionImpl().createId();
    }

    /**
     * CsUnrecoverableException.
     * 
     * @param logMessage String
     */
    public CsUnrecoverableException(String logMessage) {
        super(logMessage);
        getCsExceptionImpl().createId();
    }

    /**
     * CsUnrecoverableException.
     * 
     * @param cause original exception caught
     */
    public CsUnrecoverableException(Throwable cause) {
        super();
        setCause(cause);
        getCsExceptionImpl().createId();
    }

    /**
     * CsUnrecoverableException.
     * 
     * @param cause original exception caught
     */
    public CsUnrecoverableException(String logMessage, Throwable cause) {
        super(logMessage);
        setCause(cause);
        getCsExceptionImpl().createId();
    }

    @Override
    public void printStackTrace() {
        LOG.error(CAUSED_BY);
        if (getCause() != null) {
            LOG.error(getCause().toString());
        }
    }

    @Override
    public void printStackTrace(java.io.PrintStream stream) {

        LOG.debug(stream.toString());
        stream.println(CAUSED_BY);
        if (getCause() != null) {
            getCause().printStackTrace(stream);
        }
    }

    @Override
    public void printStackTrace(java.io.PrintWriter writer) {

        LOG.debug(writer.toString());
        writer.println(CAUSED_BY);
        if (getCause() != null) {
            getCause().printStackTrace(writer);
        }
    }
}
