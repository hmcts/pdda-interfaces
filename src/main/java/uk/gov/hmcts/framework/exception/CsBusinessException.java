package uk.gov.hmcts.framework.exception;

/**

 * Title: CSBusinessException.


 * Description: An application exception thrown where there is an error in the business logic of the
 * application.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Kevin Buckthorpe
 * @version 1.0
 */

public class CsBusinessException extends CsRecoverableException {
    private static final long serialVersionUID = 1L;

    public CsBusinessException() {
        super();
    }

    /**
     * CSBusinessException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CsBusinessException(String errorKey, String logMessage, Throwable cause) {
        super(errorKey, logMessage, cause);
    }

    /**
     * CSBusinessException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     */
    public CsBusinessException(String errorKey, String logMessage) {
        super(errorKey, logMessage);
    }

    /**
     * CSBusinessException.

     * @param errorKey message for user of application
     * @param parameters ObjectArray
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CsBusinessException(String errorKey, Object[] parameters, String logMessage,
        Throwable cause) {
        super(errorKey, parameters, logMessage, cause);
    }

    /**
     * CSBusinessException.

     * @param errorKey message for user of application
     * @param logMessage error message for log
     */
    public CsBusinessException(String errorKey, Object[] parameters, String logMessage) {
        super(errorKey, parameters, logMessage);
    }
}
