package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD.DoNotUseThreads")
class DefaultEventStoreTest {

    private DefaultEventStore eventStore;

    @BeforeEach
    void setUp() {
        eventStore = new DefaultEventStore();
    }

    @Test
    void testPushEventAndPopEventReturnsSameEvent() {
        PublicDisplayEvent mockEvent = mock(PublicDisplayEvent.class);

        eventStore.pushEvent(mockEvent);
        PublicDisplayEvent poppedEvent = eventStore.popEvent();

        assertSame(mockEvent, poppedEvent, "The popped event should be the same as the pushed event");
    }

    @Test
    void testPopEventReturnsNullWhenNoEventWithinTimeout() throws InterruptedException {
        // Call popEvent without pushing anything
        long startTime = System.currentTimeMillis();
        PublicDisplayEvent poppedEvent = eventStore.popEvent();
        long endTime = System.currentTimeMillis();

        assertNull(poppedEvent, "Expected null when no event is pushed within timeout");
        assertTrue((endTime - startTime) >= 4900, "Expected it to wait approximately 5 seconds");
    }

    @Test
    void testPushEventNotifiesWaitingThread() throws InterruptedException {
        PublicDisplayEvent mockEvent = mock(PublicDisplayEvent.class);

        Thread popThread = new Thread(() -> {
            PublicDisplayEvent poppedEvent = eventStore.popEvent();
            assertSame(mockEvent, poppedEvent, "The popped event should be the event pushed by main thread");
        });

        popThread.start();

        // Give popThread a little time to start and block
        Thread.sleep(500);

        eventStore.pushEvent(mockEvent);

        popThread.join(3000); // Wait for popThread to finish
        assertFalse(popThread.isAlive(), "Pop thread should have completed after pushEvent");
    }

}
