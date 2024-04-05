package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    public static void setUp() throws Exception {
        Mockito.mockStatic(DisplayConfigurationReader.class);
    }

    @AfterAll
    public static void tearDown() throws Exception {
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
}
