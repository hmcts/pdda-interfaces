package uk.gov.hmcts.framework.services.threadpool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThreadPoolTest {

    private static final Integer NO_OF_WORKERS = 1;

    @Mock
    private Runnable mockRunnable;

    @InjectMocks
    private final ThreadPool classUnderTest = new ThreadPool(NO_OF_WORKERS);

    @BeforeEach
    public void setUp() {
        // Do nothing
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testScheduleWorkSuccess() {
        boolean result = false;
        try {
            classUnderTest.scheduleWork(mockRunnable);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, "Result is not True");
    }

    @Test
    void testThreadPoolInactiveException() {
        Assertions.assertThrows(ThreadPoolInactiveException.class, () -> {
            classUnderTest.shutdown();
            classUnderTest.scheduleWork(mockRunnable);
        });
    }

    @Test
    void testWorkerUnavailableException() {
        Assertions.assertThrows(WorkerUnavailableException.class, () -> {
            try {
                throw new InterruptedException();
            } catch (InterruptedException e) {
                throw new WorkerUnavailableException(e);
            }
        });
    }

    @Test
    void testShutdown() {
        boolean result = false;
        try {
            classUnderTest.shutdown();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, "Result is not True");
    }
}
