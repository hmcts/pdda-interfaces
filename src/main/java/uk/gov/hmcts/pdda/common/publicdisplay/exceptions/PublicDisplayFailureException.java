package uk.gov.hmcts.pdda.common.publicdisplay.exceptions;

/**

 * Title: PublicDisplayFailureException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
public class PublicDisplayFailureException extends PublicDisplayRuntimeException implements Fatal {

    static final long serialVersionUID = -6491890025059491526L;

    /**
     * Creates a new PublicDisplayFailureException object.

     * @param message the message.
     */
    public PublicDisplayFailureException(String message) {
        super(message);
    }

    /**
     * Creates a new PublicDisplayFailureException object.

     * @param throwable the root cause.
     */
    public PublicDisplayFailureException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new PublicDisplayFailureException object.

     * @param message the message.
     * @param throwable the root cause.
     */
    public PublicDisplayFailureException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
