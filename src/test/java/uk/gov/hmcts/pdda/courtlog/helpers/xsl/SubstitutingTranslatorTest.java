package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.ErrorHandler;
import uk.gov.hmcts.pdda.courtlog.exceptions.CourtLogRuntimeException;

import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
class SubstitutingTranslatorTest {
    
    private static final int TRANSLATE_TYPE = 0;
    private static final Integer EVENT_TYPE = 101;
    private static final Locale TEST_LOCALE = Locale.ENGLISH;

    @Mock
    private Translator nextTranslator;

    @Mock
    private ConfigServices configServices;

    @Mock
    private ResourceBundle fileLookupsBundle;

    @Mock
    private ResourceBundle eventBundle;

    @Mock
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock static CsServices.getConfigServices() and getDefaultErrorHandler()
        mockStatic(CsServices.class);
        when(CsServices.getConfigServices()).thenReturn(configServices);
        when(CsServices.getDefaultErrorHandler()).thenReturn(errorHandler);
    }

    @AfterEach
    void tearDown() {
        clearAllCaches();
    }

    @Test
    void testTranslateSuccessful() throws Exception {
        // Arrange
        SubstitutingTranslator translator = new SubstitutingTranslator(TRANSLATE_TYPE, nextTranslator);

        Document xmlDoc = createXml("<root>E101_value</root>");
        String baseName = "CLSubstitution_" + SubstitutingTranslator.XSL_TYPE[TRANSLATE_TYPE] + "_" + TEST_LOCALE;

        when(configServices.getBundle(baseName)).thenReturn(fileLookupsBundle);
        when(fileLookupsBundle.getString(EVENT_TYPE.toString())).thenReturn("EventFile");
        when(configServices.getBundle("EventFile")).thenReturn(eventBundle);
        when(eventBundle.getString("E101_value")).thenReturn("SomeValue [removeMe]");

        when(nextTranslator.translate(Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn("Translated");

        // Act
        String result = translator.translate(mock(TranslationContext.class),
            TEST_LOCALE, xmlDoc, new Date(), EVENT_TYPE);

        // Assert
        assertEquals("Translated", result);
        
        verify(nextTranslator).translate(
            Mockito.any(TranslationContext.class),
            Mockito.any(Locale.class),
            Mockito.any(Document.class),
            Mockito.any(Date.class),
            Mockito.any(Integer.class)
        );

        // Verify that text node was updated
        Node textNode = xmlDoc.getDocumentElement().getFirstChild();
        assertEquals("SomeValue", textNode.getNodeValue());
    }

    @Test
    void testTranslateException() throws Exception {
        // Arrange
        SubstitutingTranslator translator = new SubstitutingTranslator(TRANSLATE_TYPE, nextTranslator);
        Document xmlDoc = createXml("<root>E101_value</root>");

        when(configServices.getBundle(Mockito.any()))
            .thenThrow(new MissingResourceException("not found", "Class", "key"));

        // Act & Assert
        assertThrows(CourtLogRuntimeException.class, () ->
                translator.translate(mock(TranslationContext.class), TEST_LOCALE, xmlDoc, new Date(), EVENT_TYPE));

        verify(errorHandler).handleError(
            Mockito.any(Exception.class),
            Mockito.eq(translator.getClass()),
            Mockito.contains("not found")
        );
    }

    @Test
    void testAllTextNodesExpected() throws Exception {
        SubstitutingTranslator translator = new SubstitutingTranslator(TRANSLATE_TYPE, nextTranslator);
        Document xmlDoc = createXml("<root><a>Alpha</a><b>Beta</b></root>");

        NodeList nodes = translator.allTextNodes(xmlDoc);
        assertEquals(2, nodes.getLength());
        assertEquals("Alpha", nodes.item(0).getNodeValue());
    }

    // Utility to create XML Document
    private static Document createXml(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));
    }

    // Helper to clear mocks
    private static void clearAllCaches() {
        Mockito.framework().clearInlineMocks();
    }
    
}
