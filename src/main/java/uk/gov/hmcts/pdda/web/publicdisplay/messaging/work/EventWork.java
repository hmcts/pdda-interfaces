package uk.gov.hmcts.pdda.web.publicdisplay.messaging.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.error.ErrorGatherer;
import uk.gov.hmcts.pdda.web.publicdisplay.error.ProcessingError;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.ProcessingInstance;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowManager;

/**
 * The class represents a unit of work involved in processing an event.

 * @author pznwc5
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class EventWork implements Runnable {

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(EventWork.class);

    /** Event that needs to be processed. */
    private PublicDisplayEvent event;

    /** Delay after initialization. */
    private long delay;

    /** Processing instance. */
    private ProcessingInstance processingInstance;

    /**
     * EventWork.

     * @param event Event that needs to be processed.
     */
    public EventWork(PublicDisplayEvent event) {
        this(event, 0, ProcessingInstance.ASYNCHRONOUS);
    }

    /**
     * EventWork.

     * @param event Event that needs to be processed
     * @param delay Delay after initialization
     */
    public EventWork(PublicDisplayEvent event, long delay, ProcessingInstance processingInstance) {
        this.event = event;
        this.delay = delay;
        this.processingInstance = processingInstance;

        log.debug("Event: {}", event);
        log.debug("Delay: {}", delay);

    }

    /**
     * Processes the message.
     */
    @Override
    public void run() {
        try {
            WorkFlowContext ctx = WorkFlowContext.newInstance();
            WorkFlowManager.getInstance(ctx).process(event);
            log.debug("Event processed: {}", event);

            if (delay > 0) {
                Thread.sleep(delay);
            }
        } catch (InterruptedException th) {
            log.error(th.getMessage(), th);
            Thread.currentThread().interrupt();
            processError(th);
        } catch (RuntimeException th) {
            log.error(th.getMessage(), th);
            processError(th);
        }
    }

    private void processError(Exception th) {
        ProcessingError error = new ProcessingError(event, th, processingInstance);
        ErrorGatherer.getInstance().addError(error);
    }

}
