package uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p/>
 * Title: A general rendering error occured.
 * </p>
 * <p/>
 * <p/>
 * Description: This Exception is recoverable.
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
public class RenderingException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * A general rendering error occured.
     * 
     * @param message helpful message.
     */
    public RenderingException(final String message) {
        super(message);
    }
}
