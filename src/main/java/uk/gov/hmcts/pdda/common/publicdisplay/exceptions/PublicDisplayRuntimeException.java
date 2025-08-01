package uk.gov.hmcts.pdda.common.publicdisplay.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;


/**
 * <p/>
 * Title: PublicDisplayRuntimeException.

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
 * @version $Revision: 1.4 $
 */
public class PublicDisplayRuntimeException extends CsUnrecoverableException {

    static final long serialVersionUID = 7634714536930897386L;

    private static final Logger LOG = LoggerFactory.getLogger(PublicDisplayRuntimeException.class);

    /**
     * Creates a new PublicDisplayRuntimeException object.

     * @param message the message.
     */
    public PublicDisplayRuntimeException(String message) {
        super(message);

        if (this instanceof Warning) {
            LOG.warn(message, this);
        } else {
            LOG.error(message, this);
        }

    }

    /**
     * Creates a new PublicDisplayRuntimeException object.

     * @param throwable the root cause.
     */
    public PublicDisplayRuntimeException(Throwable throwable) {
        super(throwable);

        if (this instanceof Warning) {
            LOG.warn("No message.", throwable);
        } else {
            LOG.error("No message.", throwable);
        }
    }

    /**
     * Creates a new PublicDisplayRuntimeException object.

     * @param message the message.
     * @param throwable the roor cause.
     */
    public PublicDisplayRuntimeException(String message, Throwable throwable) {
        super();
        if (this instanceof Warning) {
            LOG.warn(message, throwable);
        } else {
            LOG.error(message, throwable);
        }
    }

}
