package uk.gov.hmcts.framework.services.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsException;
import uk.gov.hmcts.framework.services.ErrorHandler;

/**

 * Title: DefaultErrorHandler.


 * Description: Default handler to handle the logging of errors and exceptions. The output is
 * configured in the log4j.properties file.


 * DefaultErrorHandler is a singleton; use getInstance() to obtain a reference.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond
 * @author Kevin Buckthorpe 20/09/2002
 * @version 1.0
 */

public final class DefaultErrorHandler implements ErrorHandler {
    private static final DefaultErrorHandler INSTANCE = new DefaultErrorHandler();

    private DefaultErrorHandler() {
    }

    /**
     * getInstance.
     * @return DefaultErrorHandler reference.
     */
    public static DefaultErrorHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Handle the logging/notification of the exception raised.

     * @param exception The exception being logged
     * @param klass The calling class name
     * @return Error message for user if available or null
     */
    @Override
    public String handleError(Throwable exception, Class<?> klass) {
        return handleError(exception, klass, "");
    }

    /**
     * Handle the logging/notification of the exception raised.

     * @param exception The exception being logged
     * @param klass The calling class name
     * @param errMsg Addition information to be logged
     * @return Error message for user if available or null
     */
    @Override
    public String handleError(Throwable exception, Class<?> klass, String errMsg) {
        boolean isLogged = isLogged(exception);
        Logger log = LoggerFactory.getLogger(klass);

        
        // make a note that the error was logged and record any
        if (isLogged) {
            // errMsg intended to be recorded
            // t.getMessage() should include the error id for CSException
            // type exceptions to enable
            // tracing of previous logging
            log.error(errMsg + ": " + exception.getMessage());  
        } else { // only log the error message if not yet logged
            log.error(errMsg, exception);
        }

        // set islogged flag on exception to true
        // return the message intended for the user of the application (or null
        // if no message)
        if (exception instanceof CsException) {
            CsException ex = (CsException) exception;
            ex.setIsLogged(true);
            return ex.getUserMessage();
        } else {
            return null;
        }
    }

    /*
     * Checks that this instance of Exception has not previously been logged. Returns true only if
     * the the exception is of type CSException AND the exception logging flag has been set to true.
     */
    private boolean isLogged(Throwable exception) {
        boolean isLogged = false;

        // if the Exception is of type CSRecoverableException or CSUnrecoverable
        // exception,
        // check to see if exception has previously been logged.
        if (exception instanceof CsException) {
            CsException ex = (CsException) exception;
            isLogged = ex.isLogged();
        }

        return isLogged;
    }
}
