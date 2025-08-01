package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import uk.gov.hmcts.framework.exception.CsBusinessException;

/**

 * Title: CppStagingInboundControllerException.


 * Description: Specific Exception for the CPP Staging Inbound controller.


 * Copyright: Copyright (c) 2019


 * Company: CGI

 * @author Scott Atwell
 * @version 1.0
 */
public class CppStagingInboundControllerException extends CsBusinessException {

    static final long serialVersionUID = 2264560404824644262L;

    public CppStagingInboundControllerException() {
        super();
    }

    /**
     * CppStagingInboundControllerException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CppStagingInboundControllerException(String errorKey, String logMessage,
        Throwable cause) {
        super(errorKey, logMessage, cause);
    }

    /**
     * CppStagingInboundControllerException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param parameters the parameters for the error message
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public CppStagingInboundControllerException(String errorKey, Object[] parameters,
        String logMessage, Throwable cause) {
        super(errorKey, parameters, logMessage, cause);
    }

    /**
     * CppStagingInboundControllerException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     */
    public CppStagingInboundControllerException(String errorKey, String logMessage) {
        super(errorKey, logMessage);
    }

    /**
     * CppStagingInboundControllerException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param parameters the parameters for the error message
     * @param logMessage error message for log
     */
    public CppStagingInboundControllerException(String errorKey, Object[] parameters,
        String logMessage) {
        super(errorKey, parameters, logMessage);
    }
}
