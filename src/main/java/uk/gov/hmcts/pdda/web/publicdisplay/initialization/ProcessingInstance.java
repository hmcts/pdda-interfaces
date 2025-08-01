package uk.gov.hmcts.pdda.web.publicdisplay.initialization;

import java.io.Serializable;

/**
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments.

 * @author pznwc5
 */
public final class ProcessingInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * During start up.
     */
    public static final ProcessingInstance STARTUP = new ProcessingInstance();

    /**
     * During asynchronous JMS.
     */
    public static final ProcessingInstance ASYNCHRONOUS = new ProcessingInstance();

    /**
     * Private constructor.
     */
    private ProcessingInstance() {
        // private constructor.
    }

}
