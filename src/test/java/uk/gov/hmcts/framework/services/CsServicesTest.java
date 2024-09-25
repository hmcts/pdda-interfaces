package uk.gov.hmcts.framework.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CsServicesTest {

    private static final String NOTNULL = "Result is Null";

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetServiceLocator() {
        ServiceLocator result = CsServices.getServiceLocator();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetConfigServices() {
        ConfigServices result = CsServices.getConfigServices();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetDefaultErrorHandler() {
        ErrorHandler result = CsServices.getDefaultErrorHandler();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetLocaleServices() {
        LocaleServices result = CsServices.getLocaleServices();
        assertNotNull(result, NOTNULL);
    }
}
