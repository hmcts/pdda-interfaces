package uk.gov.hmcts.pdda.common.publicdisplay.data.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;

/**
 * <p>
 * Title: NoSuchDataSourceException.
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
 * @version $Revision: 1.3 $
 */
public class NoSuchDataSourceException extends DataSourceException {

    static final long serialVersionUID = 39193997471870815L;

    /**
     * Creates a new NoSuchDataSourceException object.
     * 
     * @param documentType the document type for which there is no data source.
     */
    public NoSuchDataSourceException(DisplayDocumentType documentType) {
        super("There is no data source associated with the display document of type '"
            + documentType + "'.");
    }
}
