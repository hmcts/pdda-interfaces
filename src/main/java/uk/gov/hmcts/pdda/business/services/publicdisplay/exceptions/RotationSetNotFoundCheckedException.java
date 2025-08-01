package uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions;

/**

 * Title: Rotation Set not found exception.


 * Description:


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: RotationSetNotFoundCheckedException.java,v 1.4 2004/04/07 15:34:02 sz0t7n Exp $
 */

public class RotationSetNotFoundCheckedException extends PublicDisplayCheckedException {

    static final long serialVersionUID = -2260208483685057582L;

    private static final String ERRORKEY = "publicdisplay.rotationsetnotfound";

    private static final String ERRORLOG = "Rotation set not found for id: ";

    public RotationSetNotFoundCheckedException(Integer rotationSetId) {
        super(ERRORKEY, new Object[] {rotationSetId}, ERRORLOG + rotationSetId);
    }
}
