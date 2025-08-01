package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: StoreException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class StoreException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new StoreException object.

     * @param message an associated message.
     */
    public StoreException(final String message) {
        super(message);
    }
}
