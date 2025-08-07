package uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p>
 * Title: UnsupportedURIException.
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
public class UnsupportedUriException extends PublicDisplayRuntimeException {

    static final long serialVersionUID = -7945338424286224319L;

    /**
     * Creates a new UnsupportedURIException object.
     * 
     * @param message the message.
     */
    public UnsupportedUriException(String message) {
        super(message);
    }
}
