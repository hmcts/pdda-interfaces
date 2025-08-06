package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.exceptions;

import uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions.RenderingException;

/**
 * <p/>
 * Title: DelegateNotFoundException.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Will Fardell
 * @version $Revision: 1.3 $
 */
public class DelegateNotFoundException extends RenderingException {
    private static final long serialVersionUID = 5714381932353117414L;

    /**
     * Could not locate delegate.

     * @param documentType the document type we are looking for
     */
    public DelegateNotFoundException(String documentType) {
        super("Could not locate renderer delegate for document '" + documentType
            + "'. This error is fundamental and unrecoverable.");
    }
}
