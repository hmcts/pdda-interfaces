
package uk.gov.hmcts.framework.business.exceptions;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**

 * Title: Court not found exception class.


 * Description:

 * <p/>
 * Thrown when the configuration classes cannot find the court referred to.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 * @version 1.0
 */
public class CourtNotFoundException extends CsUnrecoverableException {

    private static final long serialVersionUID = 1L;

    /**
     * Complex constructor.

     * @param courtId Court id.
     */
    public CourtNotFoundException(Integer courtId) {
        super("Court not found with id " + courtId);
    }

}
