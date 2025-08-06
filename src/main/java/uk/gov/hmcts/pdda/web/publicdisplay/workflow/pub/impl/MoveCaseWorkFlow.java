package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.DocumentsForEvent;

/**

 * Title: MoveCaseWorkFlow.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: MoveCaseWorkFlow.java,v 1.4 2006/06/05 12:32:37 bzjrnl Exp $
 */
public class MoveCaseWorkFlow extends AbstractEventWorkFlow {
    private DocumentsForEvent documents;

    private MoveCaseEvent moveEvent;

    /**
     * Creates a new MoveCaseWorkFlow object.

     * @param context WorkFlowContext
     */
    public MoveCaseWorkFlow(WorkFlowContext context) {
        super(context);
    }

    /**
     * Creates a new MoveCaseWorkFlow object.

     * @param context WorkFlowContext
     * @param event MoveCaseEvent
     */
    public MoveCaseWorkFlow(final WorkFlowContext context, final MoveCaseEvent event) {
        this(context);
        this.documents = super.processRules(event);
        this.moveEvent = event;
    }

    /**
     * process.
     */
    @Override
    public void process() {
        // Process the changes for the FROM court room
        retrieveAndProcessChanges(documents, moveEvent.getFromCourtRoomIdentifier());

        // Process the changes for the TO court room
        retrieveAndProcessChanges(documents, moveEvent.getToCourtRoomIdentifier());
    }
}
