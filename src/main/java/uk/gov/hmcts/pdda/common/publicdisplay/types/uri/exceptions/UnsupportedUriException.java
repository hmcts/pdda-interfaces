package uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: UnsupportedURIException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class UnsupportedUriException extends PublicDisplayRuntimeException {

    static final long serialVersionUID = -7945338424286224319L;

    /**
     * Creates a new UnsupportedURIException object.

     * @param message the message.
     */
    public UnsupportedUriException(String message) {
        super(message);
    }
}
