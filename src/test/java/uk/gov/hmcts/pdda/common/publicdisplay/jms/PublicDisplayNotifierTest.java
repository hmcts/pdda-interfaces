package uk.gov.hmcts.pdda.common.publicdisplay.jms;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublicDisplayNotifierTest {
    
    @Mock
    private EventStore mockEventStore;
    
    @InjectMocks
    private final PublicDisplayNotifier classUnderTest = new PublicDisplayNotifier();

    @BeforeEach
    public void setup() throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "eventStore", mockEventStore);
    }

    @AfterEach
    public void teardown() throws Exception {
        // Do Nothing
    }

    @Test
    void testSendMessage() {
        // Setup
        PublicDisplayEvent publicDisplayEvent = getDummyPublicDisplayEvent();
        // Run
        classUnderTest.sendMessage(publicDisplayEvent);
        assertNotNull(publicDisplayEvent, "Result is Null");
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        return getDummyMoveCaseEvent();
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(Integer.valueOf(-99), null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(Integer.valueOf(-1), null);
        from.setCourtId(from.getCourtId());
        from.setCourtRoomId(from.getCourtRoomId());
        return new MoveCaseEvent(from, to, null);
    }
}
