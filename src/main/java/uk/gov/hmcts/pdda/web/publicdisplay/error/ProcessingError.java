package uk.gov.hmcts.pdda.web.publicdisplay.error;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.ProcessingInstance;

import java.io.Serializable;
import java.util.Date;

/**
 * ProcessingError.
 * 
 * @author pznwc5
 *         <p/>
 *         To change the template for this generated type comment go to Window - Preferences - Java
 *         - Code Generation - Code and Comments
 */
public class ProcessingError implements Serializable {

    private static final long serialVersionUID = 1L;

    private final PublicDisplayEvent event;

    private final Throwable exception;

    private final ProcessingInstance processingInstance;

    private final Date time = new Date();

    public ProcessingError(PublicDisplayEvent event, Throwable exception,
        ProcessingInstance processingInstance) {
        this.event = event;
        this.exception = exception;
        this.processingInstance = processingInstance;
    }

    public PublicDisplayEvent getEvent() {
        return event;
    }

    public Throwable getException() {
        return exception;
    }

    public ProcessingInstance getProcessingInstance() {
        return processingInstance;
    }

    public Date getTime() {
        return time;
    }

}
