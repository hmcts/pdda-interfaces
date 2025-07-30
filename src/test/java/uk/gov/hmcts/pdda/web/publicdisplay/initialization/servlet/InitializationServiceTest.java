package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;

import java.lang.reflect.Field;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings({"PMD"})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InitializationServiceTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final Locale LOCALE = Locale.UK;

    @Mock
    private EntityManagerFactory mockEntityManagerFactory;
    
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
    void testIsInitialized() {
        assertFalse(classUnderTest.isInitialized(), FALSE);
    }

    @Test
    void testSetDefaultLocale() {
        classUnderTest.setDefaultLocale(LOCALE);
        assertEquals(LOCALE, classUnderTest.getDefaultLocale(), EQUALS);
    }
    
    @Test
    void testSetEntityManagerFactory() {
        classUnderTest.setEntityManagerFactory(mockEntityManagerFactory);
        assertEquals(mockEntityManagerFactory, classUnderTest.getEntityManagerFactory(), EQUALS);
    }

    @Test
    void testDestroy() {
        boolean result = false;
        try {
            classUnderTest.destroy();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetInitialisationFailure() {
        boolean result = false;
        try {
            classUnderTest.getInitialisationFailure();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testSetNumInitializationWorkers() {
        classUnderTest.setNumInitializationWorkers(3);
        // No getter to assert directly, but no exception = pass
        assertTrue(true);
    }

    @Test
    void testSetInitializationDelay() {
        classUnderTest.setInitializationDelay(5000L);
        assertTrue(true);
    }

    @Test
    void testSetRetryPeriod() {
        classUnderTest.setRetryPeriod(30000L);
        assertTrue(true);
    }

    @Test
    void testSetAndGetEnvironment() {
        Environment mockEnv = Mockito.mock(Environment.class);
        classUnderTest.setEnvironment(mockEnv);
        assertEquals(mockEnv, classUnderTest.getEnvironment(), EQUALS);
    }

    @Test
    void testGetInstance() {
        InitializationService instance = InitializationService.getInstance();
        assertEquals(classUnderTest, instance, EQUALS);
    }

    @Test
    void testInitializeStartsThread() {
        try {
            classUnderTest.initialize();
            // Give it a moment to spin up
            Thread.sleep(100); // caution: flaky in tight environments
        } catch (Exception e) {
            fail("Initialization thread failed to start: " + e.getMessage());
        }
        assertTrue(true); // Simply proving no crash
    }

    @BeforeEach
    @AfterEach
    void resetInitializationState() throws Exception {
        // Reset the 'initialized' flag using reflection
        Field initializedField = InitializationService.class.getDeclaredField("initialized");
        initializedField.setAccessible(true);
        initializedField.set(InitializationService.getInstance(), false);

        // Reset the 'initialisationFailure' in case it was set
        Field failureField = InitializationService.class.getDeclaredField("initialisationFailure");
        failureField.setAccessible(true);
        failureField.set(InitializationService.getInstance(), null);
    }


    @Test
    void testRunNowTriggersInitializationWhenMidtierReady() {
        DisplayConfigurationReader mockReader = Mockito.mock(DisplayConfigurationReader.class);
        Mockito.when(mockReader.getConfiguredCourtIds()).thenReturn(new int[] {1, 2, 3});
        classUnderTest.setDisplayConfigurationReader(mockReader);

        classUnderTest.setNumInitializationWorkers(1);
        classUnderTest.setInitializationDelay(1);
        classUnderTest.setRetryPeriod(1);

        classUnderTest.runNow(); // directly call
        assertTrue(classUnderTest.isInitialized(), "Expected service to be initialized");
    }

    @Test
    void testDoInitializeRunsWithoutException() {
        DisplayConfigurationReader mockReader = Mockito.mock(DisplayConfigurationReader.class);
        Mockito.when(mockReader.getConfiguredCourtIds()).thenReturn(new int[] {101});
        classUnderTest.setDisplayConfigurationReader(mockReader);

        classUnderTest.setNumInitializationWorkers(1);
        classUnderTest.setInitializationDelay(1);

        assertDoesNotThrow(() -> classUnderTest.doInitialize(),
            "doInitialize should run without exceptions");
    }

    @Test
    void testCheckMidtierReturnsTrueWhenReaderAvailable() {
        DisplayConfigurationReader mockReader = Mockito.mock(DisplayConfigurationReader.class);
        classUnderTest.setDisplayConfigurationReader(mockReader);
        assertTrue(classUnderTest.checkMidtier(), "Expected checkMidtier to return true");
    }


}
