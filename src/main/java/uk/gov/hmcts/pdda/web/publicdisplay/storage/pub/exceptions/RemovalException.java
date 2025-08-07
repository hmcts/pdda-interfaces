package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p>
 * Title: RemovalException.
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
 * @version $Revision: 1.4 $
 */
public class RemovalException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new RemovalException object.
     * 
     * @param message the message.
     * @param throwable the root cause.
     */
    public RemovalException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
