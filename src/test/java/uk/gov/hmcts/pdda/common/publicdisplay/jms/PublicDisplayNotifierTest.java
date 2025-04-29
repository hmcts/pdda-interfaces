package uk.gov.hmcts.pdda.common.publicdisplay.jms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PublicDisplayNotifierTest {

    @Mock
    private EventStore mockEventStore;

    private PublicDisplayNotifier classUnderTest;

    @BeforeEach
    void setup() {
        classUnderTest = new PublicDisplayNotifier(mockEventStore);
    }

    @Test
    void testSendMessage() {
        // Given
        PublicDisplayEvent dummyEvent = getDummyPublicDisplayEvent();

        // When
        classUnderTest.sendMessage(dummyEvent);

        // Then
        assertNotNull(dummyEvent, "PublicDisplayEvent should not be null");
        verify(mockEventStore, times(1)).pushEvent(dummyEvent);
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(-99, null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(-1, null);
        return new MoveCaseEvent(from, to, null);
    }
}
