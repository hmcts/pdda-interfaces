package uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions;

import uk.gov.hmcts.framework.exception.CsRecoverableException;

/**
 * <p>
 * Title: PublicDisplayCheckedException.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: PublicDisplayCheckedException.java,v 1.1 2004/01/15 10:38:16 pznwc5 Exp $
 */

public class PublicDisplayCheckedException extends CsRecoverableException {

    static final long serialVersionUID = 1772687658620261019L;

    public PublicDisplayCheckedException() {
        super();
    }

    public PublicDisplayCheckedException(String errorKey, String logMessage) {
        super(errorKey, logMessage);
    }

    public PublicDisplayCheckedException(String errorKey, Object[] parameters, String logMessage) {
        super(errorKey, parameters, logMessage);
    }

    public PublicDisplayCheckedException(String errorKey, Object[] parameters, String logMessage, Throwable cause) {
        super(errorKey, parameters, logMessage, cause);
    }

}
