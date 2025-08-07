package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.Fatal;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p>
 * Title: RetrievalException.
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
public class RetrievalException extends PublicDisplayRuntimeException implements Fatal {

    private static final long serialVersionUID = 1L;

    /**
     * An object reference could not be retrieved.
     * 
     * @param message an associated message.
     */
    public RetrievalException(final String message) {
        super(message);
    }
}
