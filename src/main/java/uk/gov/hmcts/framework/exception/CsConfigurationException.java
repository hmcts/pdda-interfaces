package uk.gov.hmcts.framework.exception;

/**

 * Title: CSConfigurationException.


 * Description: Runtime exception indicating a serious problem with the configuration of the
 * application


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Kevin Buckthorpe
 * @version 1.0
 */

public class CsConfigurationException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    /**
     * Extended constructor supporting exception chaining.

     * @param cause original cause of error
     */
    public CsConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Extended constructor supporting exception chaining.

     * @param logMessage error message (for logging)
     */
    public CsConfigurationException(String logMessage) {
        super(logMessage);
    }

    /**
     * Extended constructor supporting exception chaining.

     * @param logMessage error message (for logging)
     * @param cause original cause of error
     */
    public CsConfigurationException(String logMessage, Throwable cause) {
        super(logMessage, cause);
    }
}
