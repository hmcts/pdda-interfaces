package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.DummyEventUtil;
import uk.gov.hmcts.DummyFileUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**

 * Title: PddaSerializationUtilsTest.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class PddaSerializationUtilsTest {

    @Test
    void testDecodeDeserialize() {
        byte[] decodedEvent =
            PddaSerializationUtils.decodePublicEvent(DummyFileUtil.SERIALIZED_HEARINGSTATUSEVENT);
        assertNotNull(decodedEvent, "decodePublicEvent produced null");
        PublicDisplayEvent event = PddaSerializationUtils.deserializePublicEvent(decodedEvent);
        assertNotNull(event, "deserializePublicEvent produced null");
    }

    @Test
    void testSerializeEncode() {
        PublicDisplayEvent event = DummyEventUtil.getHearingStatusEvent();
        byte[] serialized = PddaSerializationUtils.serializePublicEvent(event);
        assertNotNull(serialized, "serializePublicEvent produced null");
        String encoded = PddaSerializationUtils.encodePublicEvent(serialized);
        assertNotNull(encoded, "encodePublicEvent produced null");
    }
    
    static class EventWithFieldOnly implements PublicDisplayEvent, java.io.Serializable {
        @SuppressWarnings("unused")
        private final CourtRoomIdentifierStub courtRoomIdentifier = new CourtRoomIdentifierStub();

        @Override
        public EventType getEventType() {
            return null;
        }

        @Override
        public Integer getCourtId() {
            return null;
        }
    }

    static class CourtRoomIdentifierStub implements java.io.Serializable {
        @SuppressWarnings("unused")
        public String[] publicNotices = new String[] { "A", "B" };
    }

    @Test
    void testDeserialize_fieldOnlyCourtRoomIdentifier() {
        EventWithFieldOnly evt = new EventWithFieldOnly();
        byte[] data = PddaSerializationUtils.serializePublicEvent(evt);
        PublicDisplayEvent out = PddaSerializationUtils.deserializePublicEvent(data);
        assertNotNull(out);
    }
    
    static class EventWithNoIdentifier implements PublicDisplayEvent, java.io.Serializable {

        @Override
        public EventType getEventType() {
            return null;
        }

        @Override
        public Integer getCourtId() {
            return null;
        }
        // No fields
    }
    
    @Test
    void testDeserialize_noIdentifier() {
        EventWithNoIdentifier evt = new EventWithNoIdentifier();
        byte[] data = PddaSerializationUtils.serializePublicEvent(evt);

        PublicDisplayEvent out = PddaSerializationUtils.deserializePublicEvent(data);

        assertNotNull(out, "deserialization should still succeed");
    }
    
    static class CourtRoomIdentifierNullNotices implements java.io.Serializable {
        @SuppressWarnings("unused")
        public Object[] publicNotices = null;
    }

    static class EventWithNullNotices implements PublicDisplayEvent, java.io.Serializable {
        @SuppressWarnings("unused")
        private final CourtRoomIdentifierNullNotices courtRoomIdentifier =
            new CourtRoomIdentifierNullNotices();

        @Override
        public EventType getEventType() {
            return null;
        }

        @Override
        public Integer getCourtId() {
            return null;
        }
    }

    @Test
    void testDeserialize_publicNoticesNull() {
        EventWithNullNotices evt = new EventWithNullNotices();
        byte[] data = PddaSerializationUtils.serializePublicEvent(evt);

        PublicDisplayEvent out = PddaSerializationUtils.deserializePublicEvent(data);

        assertNotNull(out);
    }

    static class CourtRoomIdentifierNoNotices implements java.io.Serializable {
        // No publicNotices field
    }

    static class EventWithNoNoticesField implements PublicDisplayEvent, java.io.Serializable {
        @SuppressWarnings("unused")
        private final CourtRoomIdentifierNoNotices courtRoomIdentifier =
            new CourtRoomIdentifierNoNotices();

        @Override
        public EventType getEventType() {
            return null;
        }

        @Override
        public Integer getCourtId() {
            return null;
        }
    }

    @Test
    void testDeserialize_noPublicNoticesField() {
        EventWithNoNoticesField evt = new EventWithNoNoticesField();
        byte[] data = PddaSerializationUtils.serializePublicEvent(evt);

        PublicDisplayEvent out = PddaSerializationUtils.deserializePublicEvent(data);

        assertNotNull(out);
    }

    @Test
    void testDeserialize_invalidBytesReturnsNull() {
        byte[] corruptData = new byte[]{1,2,3,4,5};

        PublicDisplayEvent out = PddaSerializationUtils.deserializePublicEvent(corruptData);

        assertNull(out, "Expected null when deserialization fails");
    }


}
