package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.Rule;

/**
 * <p>
 * Title: Conditional Document.
 * </p>
 * <p>
 * Description: This class represents a document for an event and the associated rules.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: ConditionalDocument.java,v 1.4 2006/06/05 12:32:37 bzjrnl Exp $
 */

public class ConditionalDocument {

    private DisplayDocumentType[] docTypes;

    private Rule[] rules;

    private EventType eventType;

    /**
     * Create a conditional document.
     * 
     * @param docTypes The associated documents
     * @param rules The associated rules
     * @param eventType The event type
     */
    public ConditionalDocument(DisplayDocumentType[] docTypes, Rule[] rules, EventType eventType) {
        setDisplayDocumentTypes(docTypes);
        setRules(rules);
        setEventType(eventType);
    }

    /**
     * Checks that each rule associated with the document and event passes.
     * 
     * @param event PublicDisplayEvent
     * @return boolean
     */
    public boolean isDocumentValidForEvent(PublicDisplayEvent event) {
        if (!eventType.equals(event.getEventType())) {
            throw new IllegalArgumentException(
                "The event passed in (" + event.getEventType().toString()
                    + ") does not match registered event (" + eventType.toString());
        }

        // if no rules for document then true will be returned by default.
        if (rules == null) {
            return true;
        }
        boolean isValid = true;

        for (int i = 0; i < rules.length && isValid; i++) {
            Rule currentRule = rules[i];
            if (!currentRule.isValid(event)) {
                // a rule has failed, no need to remove others
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Returns the effected documents.
     * 
     * @return DisplayDocumentTypeArray
     */
    public DisplayDocumentType[] getDisplayDocumentTypes() {
        return docTypes.clone();
    }
    
    private void setDisplayDocumentTypes(DisplayDocumentType... docTypes) {
        this.docTypes = docTypes;
    } 
    
    private void setRules(Rule... rules) {
        this.rules = rules;
    }
    
    private void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
