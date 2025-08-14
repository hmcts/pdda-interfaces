package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CourtRoomEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.DocumentsForEvent;

/**

 * Title: DefaultEventWorkFlow.

 * Description: Workflow used to remove all events other than those specifically catered for in
 * specific work flow implementations

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: DefaultEventWorkFlow.java,v 1.4 2006/06/05 12:32:37 bzjrnl Exp $
 */
public class DefaultEventWorkFlow extends AbstractEventWorkFlow {
    
    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(DefaultEventWorkFlow.class);
    
    private final CourtRoomIdentifier courtRoomIdentifier;

    private final DocumentsForEvent documents;

    /**
     * Creates a new DefaultEventWorkFlow object.

     * @param context WorkFlowContext
     * @param event CourtRoomEvent
     */
    public DefaultEventWorkFlow(WorkFlowContext context, CourtRoomEvent event) {
        super(context);
        this.documents = super.processRules(event);
        this.courtRoomIdentifier = event.getCourtRoomIdentifier();
    }

    /**
     * Uses the rules to establish effected documents. Passes effected documents for processing.


     * @throws IllegalArgumentException Thrown if the event is not a CourtRoomEvent

     */
    @Override
    public void process() {
        log.debug("process() called:: Processing DefaultEventWorkFlow for court room: {}", courtRoomIdentifier);
        // Process the changes for the court room and documents
        retrieveAndProcessChanges(documents, courtRoomIdentifier);
    }
}
