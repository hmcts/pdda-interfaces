package uk.gov.hmcts.framework.services.printing;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

public class PrintServiceException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    public PrintServiceException() {
        super();
    }

    public PrintServiceException(Throwable exception) {
        super(exception);
    }
}
