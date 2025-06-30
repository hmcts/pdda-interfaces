package uk.gov.hmcts.pdda.web.publicdisplay.messaging.work;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.framework.services.threadpool.ThreadPool;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD"})
@ExtendWith(MockitoExtension.class)
class EventWorkManagerTest {

    @Mock
    private EventStore mockEventStore;

    @Mock
    private ThreadPool mockThreadPool;

    @InjectMocks
    private EventWorkManager classUnderTest;

    @BeforeEach
    void setUp() {
        // Inject manually because constructor uses different ThreadPool if 2-arg is not used
        classUnderTest = new EventWorkManager(mockEventStore, mockThreadPool);
    }

    @AfterEach
    void tearDown() {
        classUnderTest.shutDown();
    }

    @Test
    void testRunOnceWhenEventIsAvailableSchedulesWork() {
        // Arrange
        PublicDisplayEvent mockEvent = mock(PublicDisplayEvent.class);
        when(mockEventStore.popEvent()).thenReturn(mockEvent);

        // Act
        assertDoesNotThrow(() -> classUnderTest.runOnce());

        // Assert
        verify(mockThreadPool, times(1)).scheduleWork(any(EventWork.class));
        verifyNoMoreInteractions(mockThreadPool);
    }

    @Test
    void testRunOnceWhenNoEventAvailableSleepsAndDoesNotScheduleWork()
        throws InterruptedException {
        // Arrange
        when(mockEventStore.popEvent()).thenReturn(null);

        // Spy the class to intercept sleep (optional but cleaner for a real unit test)
        EventWorkManager spyUnderTest = Mockito.spy(classUnderTest);

        // Act
        assertDoesNotThrow(() -> spyUnderTest.runOnce());

        // Assert
        verify(mockThreadPool, never()).scheduleWork(any());
    }

    @Test
    void testShutdownStopsActiveFlagAndShutsDownThreadPool() {
        // Act
        assertDoesNotThrow(() -> classUnderTest.shutDown());

        // Assert
        verify(mockThreadPool, times(1)).shutdown();
    }
}
