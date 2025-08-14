package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;

/**

 * Title: ConfigurationChangeWorkFlow.

 * Description: The workflow for a configuration change

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class ConfigurationChangeWorkFlow extends AbstractBasicWorkFlow {
    
    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(ConfigurationChangeWorkFlow.class);
    
    /**
     * Creates a new ConfigurationChangeWorkFlow object.

     * @param context WorkFlowContext
     * @param event ConfigurationChangeEvent
     */
    public ConfigurationChangeWorkFlow(WorkFlowContext context, ConfigurationChangeEvent event) {
        super(context);
        log.debug("ConfigurationChangeWorkFlow created with event: {}", event);

        CourtConfigurationChange change = event.getChange();
        log.debug("Configuration change: {}", change);

        // Pass the event to the configuration to establish the exact
        // display documents and rotation sets to be re-rendered.
        DisplayConfigurationReader configurationReader =
            super.getContext().getDisplayConfigurationReader();
        RenderChanges renderChanges = configurationReader.getRenderChanges(change);
        super.setRenderChanges(renderChanges);
    }

    /**
     * Configuration change events are handled by this workflow. Gets render changes from the
     * configuration, renders the document changes, then renders the rotations sets.

     * @throws IllegalArgumentException Thrown if the event is not a ConfigurationChangeEvent
     */
    @Override
    public void process() {
        log.debug("Method process() called in ConfigurationChangeWorkFlow");
        // Render and store display documents.
        processDisplayDocumentChanges();

        // Render and store rotation sets.
        processRotationSetChanges();
    }
}
