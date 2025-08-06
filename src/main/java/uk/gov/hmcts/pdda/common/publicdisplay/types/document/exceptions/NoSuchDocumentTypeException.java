package uk.gov.hmcts.pdda.common.publicdisplay.types.document.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.Fatal;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: NoSuchDocumentTypeException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
public class NoSuchDocumentTypeException extends PublicDisplayRuntimeException implements Fatal {

    static final long serialVersionUID = 6356115104060449596L;

    /**
     * Creates a new NoSuchDocumentTypeException object.

     * @param documentId the string representation of the document type for which the is no
     *        associated DisplayDocumentType.

     * @see uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType
     */
    public NoSuchDocumentTypeException(final String documentId) {
        super("There is no such document '" + documentId + "'.");
    }
}
