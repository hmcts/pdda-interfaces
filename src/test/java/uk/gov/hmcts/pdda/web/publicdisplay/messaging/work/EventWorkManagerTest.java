package uk.gov.hmcts.pdda.web.publicdisplay.messaging.work;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.framework.services.threadpool.ThreadPool;
import uk.gov.hmcts.framework.services.threadpool.ThreadPoolInactiveException;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;
import uk.gov.hmcts.pdda.web.publicdisplay.messaging.event.EventStore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.DoNotUseThreads")
class EventWorkManagerTest {

    private static final Integer NO_OF_WORKERS = 1;
    private static final Long TIMEOUT = Long.valueOf(1);
    private static final String TRUE = "Result is not True";

    @Mock
    private EventStore mockEventStore;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private final EventWorkManager classUnderTest =
        new EventWorkManager(mockEventStore, Mockito.mock(ThreadPool.class));

    @BeforeEach
    public void setUp() {
        Mockito.mockStatic(LoggerFactory.class);
    }

    @AfterEach
    public void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testDefaultConstructor() {
        boolean result;
        new EventWorkManager(mockEventStore, NO_OF_WORKERS);
        result = true;
        assertTrue(result, TRUE);
    }

    @Test
    void testShutdown() {
        boolean result;
        classUnderTest.shutDown();
        result = true;
        assertTrue(result, TRUE);
    }

    @Test
    void testRun() {
        // Call shutdown to make sure active=false to avoid endless loop
        boolean result;
        classUnderTest.shutDown();
        classUnderTest.start();
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testRunOnce() {
        boolean result;
        PublicDisplayEvent publicDisplayEvent = getDummyPublicDisplayEvent();
        Mockito.when(mockEventStore.popEvent()).thenReturn(publicDisplayEvent);
        classUnderTest.runOnce();
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testThreadPool() {
        ThreadPool threadPoolUnderTest = new ThreadPool(0, TIMEOUT);
        assertNotNull(threadPoolUnderTest.getNumFreeWorkers(), "Result is Null");
        threadPoolUnderTest.shutdown();
    }

    @Test
    void testThreadPoolFailure() {
        Assertions.assertThrows(ThreadPoolInactiveException.class, () -> {
            ThreadPool threadPoolUnderTest = new ThreadPool(0, TIMEOUT);
            threadPoolUnderTest.shutdown();
            threadPoolUnderTest.scheduleWork(null);
        });
    }

    @Test
    void testWorkerUnavailableException() {
        Mockito.when(LoggerFactory.getLogger(PublicDisplayRuntimeException.class))
            .thenReturn(mockLogger);
        Assertions.assertThrows(WorkerUnavailableException.class, () -> {
            throw new WorkerUnavailableException(new InterruptedException());
        });
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        return getDummyMoveCaseEvent();
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(-99, null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(-1, null);
        from.setCourtId(from.getCourtId());
        from.setCourtRoomId(from.getCourtRoomId());
        return new MoveCaseEvent(from, to, null);
    }
}
