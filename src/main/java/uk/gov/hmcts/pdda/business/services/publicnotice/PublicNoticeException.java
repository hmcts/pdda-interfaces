package uk.gov.hmcts.pdda.business.services.publicnotice;

import uk.gov.hmcts.framework.exception.CsBusinessException;

/**

 * Title:PublicNoticeException, general Public notice Exception thrown from the Public Notice
 * Subsystem.


 * Description:see title


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @authors Pat Fox
 * @version 1.0
 */

public class PublicNoticeException extends CsBusinessException {

    static final long serialVersionUID = -2594247490023008245L;

    /**
     * PublicNoticeException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param parameters the parameters for the error message
     * @param logMessage error message for log
     * @param cause original exception caught
     */
    public PublicNoticeException(String errorKey, Object[] parameters, String logMessage,
        Throwable cause) {
        super(errorKey, parameters, logMessage, cause);
    }

    /**
     * PublicNoticeException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     */
    public PublicNoticeException(String errorKey, String logMessage) {
        super(errorKey, logMessage);
    }

    /**
     * PublicNoticeException.

     * @param errorKey key to the message for the user of application, stored in the properties file
     * @param logMessage error message for log
     * @param params parameters for message key
     */
    public PublicNoticeException(String errorKey, Object[] params, String logMessage) {
        super(errorKey, params, logMessage);
    }
}
