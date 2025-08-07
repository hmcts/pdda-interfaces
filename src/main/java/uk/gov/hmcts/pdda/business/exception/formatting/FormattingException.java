package uk.gov.hmcts.pdda.business.exception.formatting;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * <p>
 * Title: MaintainRecipientException.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Bal Bhamra
 * @version 1.0
 */

public class FormattingException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    /**
     * Construct the ResultsValueException with the given message.
     * 
     * @param message describes the cause of the exception
     */
    public FormattingException(String message) {
        super(message);
    }

    /**
     * Construct the FormattingException with the given message and cause.
     * 
     * @param message describes the cause of the exception
     * @param cause the cause of the exception
     */
    public FormattingException(String message, Throwable cause) {
        super(message, cause);
    }
}
