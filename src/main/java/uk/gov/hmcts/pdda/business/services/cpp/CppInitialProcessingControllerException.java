package uk.gov.hmcts.pdda.business.services.cpp;

import uk.gov.hmcts.framework.exception.CsBusinessException;

/**

 * Title: CppInitialProcessingControllerException.


 * Description: Specific Exception for the CPP Initial Processing Controller.


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 * @version 1.0
 */
public class CppInitialProcessingControllerException extends CsBusinessException {

    static final long serialVersionUID = 2264560404824644262L;

    public CppInitialProcessingControllerException() {
        super();
    }

    /**
     * CppInitialProcessingControllerException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CppInitialProcessingControllerException(String errorKey, String logMessage,
        Throwable cause) {
        super(errorKey, logMessage, cause);
    }
}
