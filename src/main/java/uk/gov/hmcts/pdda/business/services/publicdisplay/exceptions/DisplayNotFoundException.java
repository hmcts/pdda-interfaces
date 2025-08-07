package uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * <p>
 * Title: Display not found exception class.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Thrown when the configuration classes cannot find the display referred to.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */
public class DisplayNotFoundException extends CsUnrecoverableException {

    static final long serialVersionUID = -3601930473293280094L;

    /**
     * Basic Constructor.
     */
    public DisplayNotFoundException() {
        super();
    }

    /**
     * Complex constructor.
     * 
     * @param message A message explaining the exception.
     * @param cause The root cause of the problem.
     */
    public DisplayNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Complex constructor.
     * 
     * @param displayId Display id.
     * @param cause The root cause of the problem.
     */
    public DisplayNotFoundException(Integer displayId, Throwable cause) {
        super("Display not found with id:" + displayId, cause);
    }

    /**
     * Complex constructor.
     * 
     * @param displayId Display id.
     */
    public DisplayNotFoundException(Integer displayId) {
        super("Display not found with id:" + displayId);
    }

}
