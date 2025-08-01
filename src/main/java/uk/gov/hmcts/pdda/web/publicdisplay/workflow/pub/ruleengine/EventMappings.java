package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions.RulesConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**

 * Title: Event Mappings.


 * Description: This class stores arrays of ConditionalDocuments keyed by EventType


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Rakesh Lakhani
 * @version 1.0
 */

public class EventMappings {
    private final Map<EventType, ConditionalDocument[]> hashMap;

    public EventMappings() {
        hashMap = new HashMap<>();
    }

    /**
     * Returns the arrays of Conditional Documents for the given event.

     * @param eventType EventType
     * @return ConditionalDocument[]. This may be empty but never null.
     */
    public ConditionalDocument[] getConditionalDocumentsForEvent(EventType eventType) {
        if (!containsKey(eventType)) {
            throw new RulesConfigurationException("Event type not found.");
        }
        return hashMap.get(eventType);
    }

    /**
     * Add the ConditionalDocument array to the map.

     * @param eventType EventType
     * @param conditionalDocuments ConditionalDocumentArray
     */
    public void putConditionalDocumentsForEvent(EventType eventType,
        ConditionalDocument... conditionalDocuments) {
        hashMap.put(eventType, conditionalDocuments);
    }

    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

}
