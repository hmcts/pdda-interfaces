package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.pdda.courtlog.helpers.xml.CourtLogXmlHelper;

import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class CourtLogXslHelperTest {

    private static final String SAMPLE_XML = "<root></root>";
    private static final Locale LOCALE = Locale.UK;
    private static final Integer EVENT_TYPE = 1;
    private static final Date DATE = new Date();

    private static final String NOT_EQUAL = "Result is not equal";
    
    private static final String VIEW = "view";

    private Translator mockTranslator;
    private Document mockDocument;

    @BeforeEach
    void setUp() {
        mockTranslator = mock(Translator.class);
        mockDocument = mock(Document.class);
    }

    @Test
    void testTranslateEventValid() {
        try (MockedStatic<CourtLogXmlHelper> xmlHelperMock = mockStatic(CourtLogXmlHelper.class);
            MockedStatic<TranslatorFactory> translatorFactoryMock =
                mockStatic(TranslatorFactory.class)) {
            xmlHelperMock.when(() -> CourtLogXmlHelper.createDocument(Mockito.anyString()))
                .thenReturn(mockDocument);
            translatorFactoryMock.when(() -> TranslatorFactory
                .getTranslator(Mockito.any(TranslationType.class), Mockito.anyString()))
                .thenReturn(mockTranslator);
            when(mockTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn("translated");

            String result = CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE, LOCALE,
                TranslationType.GUI, new TranslationContext(), "path/to/xsl");

            assertEquals("translated", result, NOT_EQUAL);
        }
    }

    @Test
    void testTranslateEventCreateNew() {
        try (MockedStatic<CourtLogXmlHelper> xmlHelperMock = mockStatic(CourtLogXmlHelper.class);
            MockedStatic<TranslatorFactory> translatorFactoryMock =
                mockStatic(TranslatorFactory.class)) {
            xmlHelperMock.when(() -> CourtLogXmlHelper.createDocument(Mockito.anyString()))
                .thenReturn(mockDocument);
            translatorFactoryMock
                .when(() -> TranslatorFactory.getTranslator(Mockito.any(TranslationType.class)))
                .thenReturn(mockTranslator);
            when(mockTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn("translated-nullctx");

            String result = CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE, LOCALE,
                TranslationType.GUI, null, null);

            assertEquals("translated-nullctx", result, NOT_EQUAL);
        }
    }

    @Test
    void testTranslateEventNullXmlException() {
        Executable call = () -> CourtLogXslHelper.translateEvent(null, DATE, EVENT_TYPE, LOCALE,
            TranslationType.GUI, null, null);
        assertThrows(IllegalArgumentException.class, call);
    }

    @Test
    void testTranslateEventNullLocaleException() {
        Executable call = () -> CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE, null,
            TranslationType.GUI, null, null);
        assertThrows(IllegalArgumentException.class, call);
    }

    @Test
    void testTranslateEventNullTranslationTypeException() {
        Executable call = () -> CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE,
            LOCALE, null, null, null);
        assertThrows(IllegalArgumentException.class, call);
    }

    @Test
    void testTranslateEventContextOverload() {
        try (MockedStatic<CourtLogXmlHelper> xmlHelperMock = mockStatic(CourtLogXmlHelper.class);
            MockedStatic<TranslatorFactory> translatorFactoryMock =
                mockStatic(TranslatorFactory.class)) {
            xmlHelperMock.when(() -> CourtLogXmlHelper.createDocument(Mockito.anyString()))
                .thenReturn(mockDocument);
            translatorFactoryMock
                .when(() -> TranslatorFactory.getTranslator(Mockito.any(TranslationType.class)))
                .thenReturn(mockTranslator);
            when(mockTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn("context");

            String result = CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE, LOCALE,
                TranslationType.GUI, new TranslationContext());

            assertEquals("context", result, NOT_EQUAL);
        }
    }

    @Test
    void testTranslateEventXslOverload() {
        try (MockedStatic<CourtLogXmlHelper> xmlHelperMock = mockStatic(CourtLogXmlHelper.class);
            MockedStatic<TranslatorFactory> translatorFactoryMock =
                mockStatic(TranslatorFactory.class)) {
            xmlHelperMock.when(() -> CourtLogXmlHelper.createDocument(Mockito.anyString()))
                .thenReturn(mockDocument);
            translatorFactoryMock.when(() -> TranslatorFactory
                .getTranslator(Mockito.any(TranslationType.class), Mockito.anyString()))
                .thenReturn(mockTranslator);
            when(mockTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn("xsl");

            String result = CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE, LOCALE,
                TranslationType.GUI, "test.xsl");

            assertEquals("xsl", result, NOT_EQUAL);
        }
    }

    @Test
    void testTranslateEventBasicOverload() {
        try (MockedStatic<CourtLogXmlHelper> xmlHelperMock = mockStatic(CourtLogXmlHelper.class);
            MockedStatic<TranslatorFactory> translatorFactoryMock =
                mockStatic(TranslatorFactory.class)) {
            xmlHelperMock.when(() -> CourtLogXmlHelper.createDocument(Mockito.anyString()))
                .thenReturn(mockDocument);
            translatorFactoryMock
                .when(() -> TranslatorFactory.getTranslator(Mockito.any(TranslationType.class)))
                .thenReturn(mockTranslator);
            when(mockTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn("basic");

            String result = CourtLogXslHelper.translateEvent(SAMPLE_XML, DATE, EVENT_TYPE, LOCALE,
                TranslationType.GUI);

            assertEquals("basic", result, NOT_EQUAL);
        }
    }

    @Test
    void testTranslateEventViewValueVariants() {
        CourtLogViewValue mockViewValue = mock(CourtLogViewValue.class);
        when(mockViewValue.getLogEntry()).thenReturn(SAMPLE_XML);
        when(mockViewValue.getEntryDate()).thenReturn(DATE);
        when(mockViewValue.getEventType()).thenReturn(EVENT_TYPE);

        try (MockedStatic<CourtLogXmlHelper> xmlHelperMock = mockStatic(CourtLogXmlHelper.class);
            MockedStatic<TranslatorFactory> translatorFactoryMock =
                mockStatic(TranslatorFactory.class)) {
            xmlHelperMock.when(() -> CourtLogXmlHelper.createDocument(Mockito.anyString()))
                .thenReturn(mockDocument);
            translatorFactoryMock.when(() -> TranslatorFactory
                .getTranslator(Mockito.any(TranslationType.class), Mockito.anyString()))
                .thenReturn(mockTranslator);
            translatorFactoryMock
                .when(() -> TranslatorFactory.getTranslator(Mockito.any(TranslationType.class)))
                .thenReturn(mockTranslator);
            when(mockTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn(VIEW);

            assertEquals(VIEW,
                CourtLogXslHelper.translateEvent(mockViewValue, LOCALE, TranslationType.GUI),
                NOT_EQUAL);
            assertEquals(VIEW, CourtLogXslHelper.translateEvent(mockViewValue, LOCALE,
                TranslationType.GUI, new TranslationContext()), NOT_EQUAL);
            assertEquals(VIEW, CourtLogXslHelper.translateEvent(mockViewValue, LOCALE,
                TranslationType.GUI, "path.xsl"), NOT_EQUAL);
            assertEquals(VIEW, CourtLogXslHelper.translateEvent(mockViewValue, LOCALE,
                TranslationType.GUI, new TranslationContext(), "path.xsl"), NOT_EQUAL);
        }
    }
}
