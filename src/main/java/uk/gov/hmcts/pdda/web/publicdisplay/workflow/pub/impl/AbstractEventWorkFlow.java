package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.DocumentsForEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.RulesEngine;

/**
 * <p>
 * Title: Event Work Flow.
 * </p>
 * 
 * <p>
 * Description: Retrieves effected documents and processes them
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: AbstractEventWorkFlow.java,v 1.3 2006/06/05 12:32:36 bzjrnl Exp $
 */
@SuppressWarnings("PMD.LawOfDemeter")
public abstract class AbstractEventWorkFlow extends AbstractBasicWorkFlow {
    /**
     * Creates a new AbstractEventWorkFlow object.
     * 
     * @param context WorkFlowContext
     * @param renderChanges RenderChanges
     */
    protected AbstractEventWorkFlow(WorkFlowContext context, RenderChanges renderChanges) {
        super(context, renderChanges);
    }

    /**
     * Creates a new AbstractEventWorkFlow object.
     * 
     * @param context WorkFlowContext
     */
    protected AbstractEventWorkFlow(WorkFlowContext context) {
        super(context);
    }

    /**
     * Helper method that calls the rules engine.
     * 
     * @param event PublicDisplayEvent
     * 
     * @return DocumentsForEvent
     */
    protected DocumentsForEvent processRules(PublicDisplayEvent event) {
        // remove the rules to discover which documents are effected
        // by the event
        RulesEngine rules = RulesEngine.getInstance();
        return rules.getDisplayDocumentTypesForEvent(event);
    }

    /**
     * Helper method that requests the documents that need re-rendering from the configuration and
     * processes them.
     * 
     * @param documents DocumentsForEvent
     * @param courtRoomIdentifier CourtRoomIdentifier
     */
    protected void retrieveAndProcessChanges(DocumentsForEvent documents,
        CourtRoomIdentifier courtRoomIdentifier) {
        // Pass the effected documents and the court room id to the
        // configuration to
        // establish the exact display documents to be re-rendered.
        DisplayConfigurationReader configurationReader =
            getContext().getDisplayConfigurationReader();
        RenderChanges renderChanges = configurationReader
            .getRenderChanges(documents.getDisplayDocumentTypes(), courtRoomIdentifier);
        setRenderChanges(renderChanges);

        // Render and store display documents.
        processDisplayDocumentChanges();

        // Render and store rotation sets.
        // Note: no rotation set changes are expected, this is left in in-case
        // the remove changes in configuration.
        processRotationSetChanges();
    }
}
