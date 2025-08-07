package uk.gov.hmcts.pdda.business.services.cppformatting;

import uk.gov.hmcts.framework.exception.CsBusinessException;

/**
 * <p>
 * Title: CppFormattingControllerException.
 * </p>
 * <p>
 * Description: Specific Exception for the CPP Formatting controller.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2019
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Chris Vincent
 * @version 1.0
 */
public class CppFormattingControllerException extends CsBusinessException {

    static final long serialVersionUID = 2264060404824644262L;

    public CppFormattingControllerException() {
        super();
    }

    /**
     * CppFormattingControllerException.
     * 
     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CppFormattingControllerException(String errorKey, String logMessage, Throwable cause) {
        super(errorKey, logMessage, cause);
    }

    /**
     * CppFormattingControllerException.
     * 
     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param parameters the parameters for the error message
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CppFormattingControllerException(String errorKey, Object[] parameters, String logMessage,
        Throwable cause) {
        super(errorKey, parameters, logMessage, cause);
    }

    /**
     * CppFormattingControllerException.
     * 
     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     */
    public CppFormattingControllerException(String errorKey, String logMessage) {
        super(errorKey, logMessage);
    }

    /**
     * CppFormattingControllerException.
     * 
     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param parameters the parameters for the error message
     * @param logMessage error message for log
     */
    public CppFormattingControllerException(String errorKey, Object[] parameters,
        String logMessage) {
        super(errorKey, parameters, logMessage);
    }
}
