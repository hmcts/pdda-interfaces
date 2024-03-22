package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions.RulesConfigurationException;

/**
 * <p>
 * Title: Rules Engine.
 * </p>
 * <p>
 * Description: The main interface into the Rules for established effected DisplayDocuments
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: RulesEngine.java,v 1.5 2006/06/05 12:32:37 bzjrnl Exp $
 */

public final class RulesEngine {
    private static final Logger LOG = LoggerFactory.getLogger(RulesEngine.class);

    private static RulesEngine instance;

    /**
     * Link to the mapping of rules to documents.
     */
    private RulesConfiguration ruleConfig;

    private RulesEngine() {
        try {
            ruleConfig = RulesConfiguration.newConfiguration();
        } catch (RulesConfigurationException ex) {
            LOG.error("The rules configuration failed to load", ex);
            throw ex;
        }
    }

    /**
     * Return an instance of the rule engine.
     * 
     * @return RulesEngine
     */
    public static RulesEngine getInstance() {
        synchronized (RulesEngine.class) {
            instance = new RulesEngine();
            return instance;
        }
    }

    /**
     * Get the display document types that are effected by the public display event.
     * 
     * @param publicDisplayEvent PublicDisplayEvent
     * @return DocumentsForEvent containing a collection of DisplayDocumentTypes
     * 
     * @pre publicDisplayEvent != null
     * @post return != null
     * @post return.getDisplayDocumentTypes() != null
     * 
     */
    public DocumentsForEvent getDisplayDocumentTypesForEvent(PublicDisplayEvent publicDisplayEvent) {
        DocumentsForEvent docForEvent = new DocumentsForEvent();
        LOG.debug("getDisplayDocumentTypesForEvent({})", publicDisplayEvent);
        
        // for event type look up documents
        ConditionalDocument[] conditionalDocuments =
            ruleConfig.getConditionalDocumentsForEvent(publicDisplayEvent.getEventType());

        // for each document check if it is requires refreshing
        for (ConditionalDocument conditionalDocument : conditionalDocuments) {
            // if document passes rules add to return list
            if (conditionalDocument.isDocumentValidForEvent(publicDisplayEvent)) {
                docForEvent.addDisplayDocumentTypes(conditionalDocument.getDisplayDocumentTypes());

            }
        }

        return docForEvent;
    }
}
