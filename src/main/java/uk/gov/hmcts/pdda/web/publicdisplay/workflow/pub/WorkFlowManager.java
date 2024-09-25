package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.exceptions.UnrecognizedEventException;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl.DefaultWorkFlowManager;

/**
 * <p>
 * Title: The entry point into the Workflow component.
 * </p>
 * 
 * <p>
 * Description: The WorkFlowManager provides the functionality to remove
 * <code>PublicDisplayEvent</code>
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
@SuppressWarnings("PMD.SingularField")
public abstract class WorkFlowManager {
    private static DefaultWorkFlowManager instance;
    private final WorkFlowContext context;

    /**
     * Creates a new WorkFlowManager object.
     * 
     * @param context WorkFlowContext
     */
    protected WorkFlowManager(WorkFlowContext context) {
        this.context = context;
    }

    /**
     * Get context.
     * 
     * @return context
     */
    public WorkFlowContext getContext() {
        return context;
    }

    /**
     * Get workflow manager instance.
     * 
     * @return workflowManager
     */
    public static WorkFlowManager getInstance(WorkFlowContext context) {
        instance = new DefaultWorkFlowManager(context);
        return instance;
    }

    /**
     * Process all the supplied workflows for the event that has occured.
     * 
     * @param event the event that occured.
     */
    public void process(PublicDisplayEvent event) {
        WorkFlow[] flows = getWorkFlowsForEvent(event);

        for (WorkFlow flow : flows) {
            flow.process();
        }
    }

    /**
     * Get a list of workflows for the event that occured.
     * 
     * @param event the event.
     * 
     * @return an array of WorkFlow-s to remove.
     * 
     * @throws UnrecognizedEventException if the event is not recognized.
     */
    protected abstract WorkFlow[] getWorkFlowsForEvent(PublicDisplayEvent event);
}
