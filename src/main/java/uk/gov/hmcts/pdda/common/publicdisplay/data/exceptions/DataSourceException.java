package uk.gov.hmcts.pdda.common.publicdisplay.data.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: DataSourceException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class DataSourceException extends PublicDisplayRuntimeException {

    static final long serialVersionUID = -6803329782083147232L;

    /**
     * Creates a new DataSourceException object.

     * @param message the message.
     */
    public DataSourceException(final String message) {
        super(message);
    }

    /**
     * Creates a new DataSourceException object.

     * @param cause the root cause.
     */
    public DataSourceException(final Throwable cause) {
        super(cause);
    }
}
