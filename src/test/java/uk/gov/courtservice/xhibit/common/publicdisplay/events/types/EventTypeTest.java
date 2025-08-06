package uk.gov.courtservice.xhibit.common.publicdisplay.events.types;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**

 * Title: EventTypeTest.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class EventTypeTest {

    private static final String TRUE = "Result is not true";
    private static final String FALSE = "Result is not false";

    @Test
    void testEqualsTrue() {
        EventType eventType = new EventType("AddCaseEvent");
        EventType addCaseEventType = EventType.getEventType("AddCaseEvent"); 
        assertEquals(true, eventType.equals(addCaseEventType), TRUE);
    }
    
    @Test
    void testEqualsFalse() {
        EventType eventType = new EventType("AddCaseEvent");
        EventType configurationEventType = EventType.getEventType("ConfigurationEvent");
        assertEquals(false, eventType.equals(configurationEventType), FALSE);
    }
}
