package uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions;

import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

/**
 * <p/>
 * Title: DocumentHasNotHadDataAddedException.
 * </p>
 * <p/>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class DocumentHasNotHadDataAddedException extends RenderingException {

    private static final long serialVersionUID = 1L;

    public DocumentHasNotHadDataAddedException(DisplayDocument doc) {
        super("The document with the URI '" + doc.getUri()
            + "' has not had the data retrieved for it.");
    }
}
