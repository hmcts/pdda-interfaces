package uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * <p>
 * Title: Courtroom not found exception class.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Thrown when the configuration classes cannot find the courtroom referred to.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: CourtRoomNotFoundException.java,v 1.2 2004/03/29 15:59:45 pznwc5 Exp $
 */

public class CourtRoomNotFoundException extends CsUnrecoverableException {

    static final long serialVersionUID = -3758694406290786902L;

    /**
     * Basic Constructor.
     */
    public CourtRoomNotFoundException() {
        super();
    }

    /**
     * Complex constructor.
     * 
     * @param message A message explaining the exception.
     * @param cause The root cause of the problem.
     */
    public CourtRoomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Complex constructor.
     * 
     * @param courtRoomId Court room id.
     * @param cause The root cause of the problem.
     */
    public CourtRoomNotFoundException(Integer courtRoomId, Throwable cause) {
        super("Court room not found with id:" + courtRoomId, cause);
    }

    /**
     * Complex constructor.
     * 
     * @param courtRoomId Court room id.
     */
    public CourtRoomNotFoundException(Integer courtRoomId) {
        super("Court room not found with id:" + courtRoomId);
    }

}
