package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InitializationServiceSetTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Long DELAY = Long.valueOf(1);
    private static final Integer NO_OF_WORKERS = 1;
    private static final Long RETRY = Long.valueOf(1);

    @InjectMocks
    private final InitializationService classUnderTest = InitializationService.getInstance();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(DisplayConfigurationReader.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testSetNumInitializationWorkers() {
        boolean result = false;
        try {
            classUnderTest.setNumInitializationWorkers(NO_OF_WORKERS);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testSetInitializationDelay() {
        boolean result = false;
        try {
            classUnderTest.setInitializationDelay(DELAY);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testSetRetryPeriod() {
        boolean result = false;
        try {
            classUnderTest.setRetryPeriod(RETRY);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
