package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: RemovalException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
public class RemovalException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new RemovalException object.

     * @param message the message.
     * @param throwable the root cause.
     */
    public RemovalException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
