package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.Fatal;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;

/**
 * <p>
 * Title: DocumentNotYetRenderedException.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class DocumentNotYetRenderedException extends StoreException implements Fatal {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new DocumentNotYetRenderedException object.
     * 
     * @param doc the Storeable document.
     */
    public DocumentNotYetRenderedException(final Storeable doc) {
        super("No rendered string for document '" + doc.getUri() + "'.");
    }
}
