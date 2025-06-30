package uk.gov.hmcts.framework.services.threadpool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("PMD")
class ThreadPoolTest {

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(ThreadPoolTest.class);

    private static final int NO_OF_WORKERS = 2;
    private static final long TIMEOUT_MS = 2000L;

    private ThreadPool classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ThreadPool(NO_OF_WORKERS, TIMEOUT_MS);
    }

    @AfterEach
    void tearDown() {
        if (classUnderTest != null) {
            classUnderTest.shutdown();
        }
    }

    @Test
    void testScheduleWorkSuccess() {
        assertDoesNotThrow(() -> classUnderTest.scheduleWork(() -> {
            // Simulate simple work
            log.debug("Running mock task...");
        }));
    }

    @Test
    void testScheduleWorkAfterShutdownThrowsException() {
        classUnderTest.shutdown();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            classUnderTest.scheduleWork(() -> log.debug("This should fail"));
        });
        assertEquals("ThreadPool is shut down", exception.getMessage(), "All good");
    }

    @Test
    void testShutdownGracefully() {
        assertDoesNotThrow(() -> classUnderTest.shutdown());
        // After shutdown, submitting work should throw
        assertThrows(IllegalStateException.class, () -> classUnderTest.scheduleWork(() -> {
        }));
    }

    @Test
    void testGetNumFreeWorkersBeforeAndAfterShutdown() {
        assertEquals(NO_OF_WORKERS, classUnderTest.getNumFreeWorkers(),
            "Should report full pool size before shutdown");
        classUnderTest.shutdown();
        assertEquals(0, classUnderTest.getNumFreeWorkers(), "Should report 0 after shutdown");
    }
}
