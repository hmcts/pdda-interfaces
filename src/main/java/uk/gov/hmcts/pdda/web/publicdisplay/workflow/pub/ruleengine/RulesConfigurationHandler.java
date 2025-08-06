package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.Rule;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.RuleFlyweightPool;

import java.util.ArrayList;
import java.util.List;

/**

 * Title: RulesConfigurationHandler.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Rakesh Lakhani
 * @version 1.0
 */

public class RulesConfigurationHandler extends DefaultHandler {
    // Statics for the XML tags in the Rules Configuration
    private static final String RULE_REF = "RuleRef";

    private static final String EVENT = "Event";

    private static final String CONDITIONAL_DOCUMENT = "ConditionalDocument";

    private static final String RULE = "Rule";

    // Statics for the XML attributes in the Rules Configuration
    private static final String EVENT_TYPE = "type";

    private static final String DISPLAY_DOCUMENT_ID = "id";

    private static final String RULE_ID = "id";

    private EventType currentEventType;

    private final List<Rule> rulesList = new ArrayList<>();

    private DisplayDocumentType[] currentDocuments;

    private final List<ConditionalDocument> conditionalDocumentList = new ArrayList<>();

    private final EventMappings eventMap = new EventMappings();

    /**
     * Processed when a start tag is reached. Only handles RuleRef, Event, ConditionalDocument &
     * Rule.

     * @param uri String
     * @param localName String
     * @param name String
     * @param attributes Attributes
     * @throws org.xml.sax.SAXException Exception
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
        throws SAXException {
        if (RULE_REF.equals(name)) {
            // A Rule Ref is a declaration of a reusable Rule.
            // The fly weight pool will instantiate it and hold and instance
            RuleFlyweightPool.getInstance().loadRule(attributes);
        } else if (EVENT.equals(name)) {
            // For a Event start tag, store the Event type and clear out the
            // conditional document list
            setCurrentEventType(EventType.getEventType(attributes.getValue(EVENT_TYPE)));
            conditionalDocumentList.clear();
        } else if (CONDITIONAL_DOCUMENT.equals(name)) {
            // For a conditional document start tag, get the
            // DisplayDocumentType
            // and clear out the rules list
            currentDocuments = DisplayDocumentType
                .getDisplayDocumentTypes(attributes.getValue(DISPLAY_DOCUMENT_ID));

            rulesList.clear();
        } else if (RULE.equals(name)) {
            // For a rule, get the cached rule from the pool and add to the
            // rule list
            Rule rule = RuleFlyweightPool.getInstance().getRule(attributes.getValue(RULE_ID));
            rulesList.add(rule);
        }
    }

    /**
     * Processed when a end tag is reached. Only handles Event & ConditionalDocument.

     * @param uri String
     * @param localName String
     * @param name String
     * @throws org.xml.sax.SAXException Exception
     */
    @Override
    public void endElement(String uri, String localName, String name)
        throws SAXException {
        if (EVENT.equals(name)) {
            // For an Event end tag add the conditional documents to the map
            // and reset the current event holder
            eventMap.putConditionalDocumentsForEvent(currentEventType, conditionalDocumentList
                .toArray(new ConditionalDocument[0]));
            setCurrentEventType(null);
        } else if (CONDITIONAL_DOCUMENT.equals(name)) {
            // For a ConditionalDocument end tag, remove a new conditional
            // document
            // with the associated rule set.
            if (currentEventType == null) {
                throw new SAXException(
                    "Invalid document structure. Document found before event declared");
            } else {
                ConditionalDocument cond = new ConditionalDocument(currentDocuments,
                    rulesList.toArray(new Rule[0]), currentEventType);
                // Add to the conditional document list for the current event
                conditionalDocumentList.add(cond);
            }
        }
    }

    public EventMappings getEventMappings() {
        return eventMap;
    }
    
    private void setCurrentEventType(EventType currentEventType) {
        this.currentEventType = currentEventType;
    }
}
