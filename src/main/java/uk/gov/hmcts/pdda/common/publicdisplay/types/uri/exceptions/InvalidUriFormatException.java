package uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p>
 * Title: InvalidURIFormatException.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class InvalidUriFormatException extends PublicDisplayRuntimeException {

    static final long serialVersionUID = 6723428835811946932L;

    /**
     * Creates a new InvalidURIFormatException object.
     * 
     * @param message the message.
     */
    public InvalidUriFormatException(final String message) {
        super(message);
    }

    /**
     * Creates a new InvalidURIFormatException object.
     * 
     * @param uri the offending URI.
     * @param message the message.
     */
    public InvalidUriFormatException(final String uri, final String message) {
        super("The URI '" + uri + "' is not in the correct format." + message);
    }
    
    /**
     * Creates a new InvalidURIFormatException object.
     * 
     * @param uri the offending URI.
     * @param message the message.
     * @param throwable Throwable
     */
    public InvalidUriFormatException(final String uri, final String message, final Throwable throwable) {
        super("The URI '" + uri + "' is not in the correct format." + message, throwable);
    }
}
