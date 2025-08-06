package uk.gov.hmcts.framework.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationEnum;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TranslationServicesFailureTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";

    @Mock
    private TranslationEnum mockTranslationEnum;

    @Mock
    private TranslationBundles mockTranslationBundles;

    @Mock
    private TranslationBundle mockTranslationBundle;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private InitializationService mockInitializationService;

    @Mock
    private ResourceServices mockResourceServices;

    @InjectMocks
    private final TranslationServices classUnderTest = TranslationServices.getInstance();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(InitializationService.class);
        Mockito.mockStatic(ResourceServices.class);

    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testSetTranslationBundlesMap() {
        // Setup
        Map<TranslationEnum, TranslationBundles> bundlesMap = getMockBundlesMap();
        // Run
        classUnderTest.setTranslationBundlesMap(bundlesMap);
        // Check
        assertEquals(bundlesMap, classUnderTest.getTranslationBundlesMap(),
            "Results are not Equal");
    }

    @Test
    void testGetTranslationBundlesFailureNull() {
        // Setup
        Map<TranslationEnum, TranslationBundles> emptyMap = new ConcurrentHashMap<>();
        classUnderTest.setTranslationBundlesMap(emptyMap);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getDefaultLocale()).thenReturn(Locale.UK);
        // Run
        TranslationBundles result = classUnderTest.getTranslationBundles(null);
        // Check
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetTranslationBundlesFailure() {
        // Setup
        Map<TranslationEnum, TranslationBundles> emptyMap = new ConcurrentHashMap<>();
        classUnderTest.setTranslationBundlesMap(emptyMap);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getDefaultLocale()).thenReturn(Locale.UK);
        // Run
        TranslationBundles result = classUnderTest.getTranslationBundles(mockTranslationEnum);
        // Check
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testAddTranslationBundleLanguageFailure() {
        boolean result = testAddTranslationBundleFailure(null, "Hello", "Bonjour", null, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testAddTranslationBundleKeyFailure() {
        boolean result =
            testAddTranslationBundleFailure(Locale.FRANCE, null, "Bonjour", null, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testAddTranslationBundleTranslationFailure() {
        boolean result = testAddTranslationBundleFailure(Locale.FRANCE, "Hello", null, null, false);
        assertTrue(result, TRUE);
    }

    private boolean testAddTranslationBundleFailure(Locale locale, String key, String translation,
        String context, boolean exactMatch) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TranslationBundles translationBundle = new TranslationBundles(locale);
            translationBundle.addTranslation(locale, key, translation, context, exactMatch);
        });
        return true;
    }

    private Map<TranslationEnum, TranslationBundles> getMockBundlesMap() {
        Map<TranslationEnum, TranslationBundles> result = new ConcurrentHashMap<>();
        result.put(mockTranslationEnum, mockTranslationBundles);
        return result;
    }
}
