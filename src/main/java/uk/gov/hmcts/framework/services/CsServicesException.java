package uk.gov.hmcts.framework.services;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**

 * Title: CsServicesException.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond
 * @version 1.0
 */

public class CsServicesException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    public CsServicesException() {
        super();
    }

    public CsServicesException(String msg) {
        super(msg);
    }

    public CsServicesException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CsServicesException(Throwable cause) {
        super(cause);
    }
}
