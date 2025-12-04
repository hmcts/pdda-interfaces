package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.framework.services.XslServices;

import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class BasicTranslatorTest {

    private Document mockDocument;
    private TranslationContext mockContext;
    private XmlServices mockXmlServices;
    private XslServices mockXslServices;

    private static final int TYPE = 1;
    private static final Integer EVENT_TYPE = 25;
    private static final Locale LOCALE = Locale.UK;
    private static final Date ENTRY_DATE = new Date();

    @BeforeEach
    void setUp() {
        mockDocument = mock(Document.class);
        mockContext = mock(TranslationContext.class);
        mockXmlServices = mock(XmlServices.class);
        mockXslServices = mock(XslServices.class);
    }

    @Test
    void testConstructorWithoutXslInitializesSuccessfully() {
        BasicTranslator translator = new BasicTranslator(TYPE);
        assertNotNull(translator);
    }

    @Test
    void testConstructorWithXslInitializesSuccessfully() {
        BasicTranslator translator = new BasicTranslator(TYPE, "custom.xsl");
        assertNotNull(translator);
    }

    @Test
    void testTranslateUsesProvidedXslAndCallsTransform() {
        BasicTranslator translator = new BasicTranslator(TYPE, "testpath/my.xsl");

        try (MockedStatic<CsServices> csMock = mockStatic(CsServices.class)) {
            csMock.when(CsServices::getXmlServices).thenReturn(mockXmlServices);
            csMock.when(CsServices::getXslServices).thenReturn(mockXslServices);
            when(mockXslServices.transform(Mockito.any(Document.class), Mockito.any(String.class),
                Mockito.any(), Mockito.any())).thenReturn("transformed-result");

            String result =
                translator.translate(mockContext, LOCALE, mockDocument, ENTRY_DATE, EVENT_TYPE);

            assertEquals("transformed-result", result);
            verify(mockXmlServices, times(2)).addElementByTagName(Mockito.any(Document.class),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        }
    }

    @Test
    void testTranslateBuildsDefaultXslPathWhenXslIsNull() {
        BasicTranslator translator = new BasicTranslator(TYPE); // xsl = null

        try (MockedStatic<CsServices> csMock = mockStatic(CsServices.class)) {
            csMock.when(CsServices::getXmlServices).thenReturn(mockXmlServices);
            csMock.when(CsServices::getXslServices).thenReturn(mockXslServices);
            when(mockXslServices.transform(Mockito.any(Document.class), Mockito.any(String.class),
                Mockito.any(), Mockito.any())).thenReturn("default-xsl-result");

            String result =
                translator.translate(mockContext, LOCALE, mockDocument, ENTRY_DATE, EVENT_TYPE);

            assertEquals("default-xsl-result", result);
            verify(mockXmlServices, atLeastOnce()).addElementByTagName(Mockito.any(Document.class),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
            verify(mockXslServices).transform(Mockito.any(Document.class),
                Mockito.any(String.class), Mockito.any(), Mockito.any());
        }
    }

    @Test
    void testTranslateFormatsDateAndTimeForLocale() {
        BasicTranslator translator = new BasicTranslator(TYPE, "locale-check.xsl");

        try (MockedStatic<CsServices> csMock = mockStatic(CsServices.class)) {
            csMock.when(CsServices::getXmlServices).thenReturn(mockXmlServices);
            csMock.when(CsServices::getXslServices).thenReturn(mockXslServices);

            when(mockXslServices.transform(Mockito.any(Document.class), Mockito.any(String.class),
                Mockito.any(), Mockito.any())).thenReturn("formatted-result");

            String result = translator.translate(mockContext, Locale.FRANCE, mockDocument,
                ENTRY_DATE, EVENT_TYPE);
            
            assertEquals("formatted-result", result);
        }
    }
}
