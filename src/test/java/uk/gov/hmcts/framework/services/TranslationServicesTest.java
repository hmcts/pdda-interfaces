package uk.gov.hmcts.framework.services;

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
import uk.gov.hmcts.DummyServicesUtil;
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

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TranslationServicesTest {

    private static final String NOTNULL = "Result is Null";

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
        assertEquals(bundlesMap, classUnderTest.getTranslationBundlesMap(), "Results are not Equal");
    }

    @Test
    void testGetTranslationBundlesSuccess() {
        // Setup
        classUnderTest.setTranslationBundlesMap(getMockBundlesMap());
        // Run
        TranslationBundles result = classUnderTest.getTranslationBundles(mockTranslationEnum);
        // Check
        assertEquals(mockTranslationBundles, result, "Results are not Equal");
    }

    @Test
    void testGetTranslationBundleSuccess() {
        // Setup
        Mockito.when(ResourceServices.getInstance()).thenReturn(mockResourceServices);
        classUnderTest.setTranslationBundlesMap(getMockBundlesMap());
        Mockito.when(mockTranslationBundles.getTranslationBundle(Locale.UK)).thenReturn(mockTranslationBundle);
        Mockito.when(mockResourceServices.getResourceAsStream(Mockito.isA(String.class), Mockito.isA(Locale.class)))
            .thenReturn(mockInputStream);
        // Run
        TranslationBundle result = classUnderTest.getTranslationBundle(mockTranslationEnum, Locale.UK);
        // Check
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testTranslationBundle() {
        TranslationBundles translationBundle = new TranslationBundles(Locale.UK);
        assertNotNull(translationBundle.getTranslationBundle(Locale.UK), NOTNULL);
    }

    @Test
    void testAddTranslationBundle() {
        TranslationBundles translationBundle = new TranslationBundles(Locale.UK);
        translationBundle.addTranslation(Locale.FRANCE, "Hello", "Bonjour", null, false);
        assertNotNull(translationBundle.getTranslationBundle(Locale.FRANCE), NOTNULL);
        assertNotNull(translationBundle.toXml(), NOTNULL);
        assertNotNull(translationBundle.toString(), NOTNULL);
    }

    @Test
    void testGetTranslationBundleLookupTranslationBundle() {
        // Setup
        TranslationBundle translationBundle = DummyServicesUtil.getDummyLookupTranslationBundle(Locale.UK);
        classUnderTest.setTranslationBundlesMap(getMockBundlesMap());
        Mockito.when(mockTranslationBundles.getTranslationBundle(Locale.UK)).thenReturn(translationBundle);
        // Run
        TranslationBundle result = classUnderTest.getTranslationBundle(mockTranslationEnum, Locale.UK);
        // Check
        assertEquals(translationBundle, result, "Results are not Equal");
    }
    
    private Map<TranslationEnum, TranslationBundles> getMockBundlesMap() {
        Map<TranslationEnum, TranslationBundles> result = new ConcurrentHashMap<>();
        result.put(mockTranslationEnum, mockTranslationBundles);
        return result;
    }
}
