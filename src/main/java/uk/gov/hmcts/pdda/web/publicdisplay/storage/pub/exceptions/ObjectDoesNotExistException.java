package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.Warning;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;

/**
 * <p/>
 * Title: ObjectDoesNotExistException.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.5 $
 */
public class ObjectDoesNotExistException extends PublicDisplayRuntimeException implements Warning {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ObjectDoesNotExistException object.

     * @param uri the uri of the object we could not file.
     * @param retrievalCode the reference we were using when trying to obtain the object.
     */
    public ObjectDoesNotExistException(AbstractUri uri, String retrievalCode) {
        super("Could not find the entry described by the uri '" + uri
            + "' as it does not exist. The retrieval code used was '" + retrievalCode + "'.");
    }
}
