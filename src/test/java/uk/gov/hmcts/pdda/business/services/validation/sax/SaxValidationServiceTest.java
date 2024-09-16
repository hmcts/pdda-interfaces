package uk.gov.hmcts.pdda.business.services.validation.sax;

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
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import uk.gov.hmcts.framework.exception.CsBusinessException;
import uk.gov.hmcts.pdda.business.services.validation.ValidationException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SaxValidationServiceTest {

    private static final String NOTEQUALS = "Result is not Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String NULL = "Result is Null";

    private final FileEntityResolver dummyFileEntityResolver = getDummyFileEntityResolver();

    @Mock
    private SchemaFactory mockSchemaFactory;

    @Mock
    private SAXParser mockSaxParser;

    @Mock
    private SAXParserFactory mockSaxParserFactory;

    @Mock
    private XMLReader mockXmlReader;

    @Mock
    private InputSource mockInputSource;

    @Mock
    private Locator mockLocator;

    @InjectMocks
    private final SaxValidationService classUnderTest =
        new SaxValidationService(dummyFileEntityResolver, mockSchemaFactory, mockSaxParserFactory);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testValidate() throws ParserConfigurationException, SAXException, ValidationException {
        // Setup
        String xml = "<XML>";
        String schemaName = "";
        // Expects
        Mockito.when(mockSaxParserFactory.newSAXParser()).thenReturn(mockSaxParser);
        Mockito.when(mockSaxParser.getXMLReader()).thenReturn(mockXmlReader);
        // Run
        boolean result = false;
        try {
            classUnderTest.validate(xml, schemaName);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        assertTrue(result, TRUE);
    }

    @Test
    void testErrorHandlerValidationResult() throws SAXException {
        SAXParseException dummySaxParseException = new SAXParseException("", mockLocator);
        ErrorHandlerValidationResult errorHandlerValidationResult = new ErrorHandlerValidationResult();
        assertEquals(0, errorHandlerValidationResult.toString().length(), NOTEQUALS);
        errorHandlerValidationResult.error(dummySaxParseException);
        errorHandlerValidationResult.fatalError(dummySaxParseException);
        errorHandlerValidationResult.warning(dummySaxParseException);
        assertFalse(errorHandlerValidationResult.isValid(), FALSE);
        assertNotNull(errorHandlerValidationResult.toString(), NULL);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            errorHandlerValidationResult.error(null);
        });
    }

    @Test
    void testValidationException() {
        Assertions.assertThrows(ValidationException.class, () -> {
            throw new ValidationException("Test", new CsBusinessException());
        });
    }

    @Test
    void testSaxFactories() {
        SaxValidationService localClassUnderTest = new SaxValidationService(dummyFileEntityResolver, null, null);  
        try {
            assertNotNull(localClassUnderTest.getSaxParserFactory(),"SaxParserFactory IS NULL");
            assertNotNull(localClassUnderTest.getSchemaFactory(),"SchemaFactory IS NULL");
        } catch (Exception exception) {
            fail("Failed on exception" + exception.getMessage());
        }
    }

    private FileEntityResolver getDummyFileEntityResolver() {
        return new FileEntityResolver();
    }
}
