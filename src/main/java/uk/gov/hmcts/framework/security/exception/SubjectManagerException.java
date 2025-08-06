package uk.gov.hmcts.framework.security.exception;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**

 * Title: AccessInfoException.


 * Description: Thrown by the subject manager when an error occurs.


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: SubjectManagerException.java,v 1.1 2006/05/19 08:06:06 bzjrnl Exp $
 */
public class SubjectManagerException extends CsUnrecoverableException {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new exception with the specified cause.
     */
    public SubjectManagerException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new exception with the specified message and cause.
     */
    public SubjectManagerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Construct a new exception with the specified message and cause.
     */
    public SubjectManagerException(String msg) {
        super(msg);
    }
}
