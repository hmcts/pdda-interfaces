package uk.gov.hmcts.framework.xml;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.xml.sax.ForkContentHandler;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ForkContentHandlerTest {

    private static final String TRUE = "Result is not True";

    private final ContentHandler mockContentHandler = Mockito.mock(ContentHandler.class);

    @InjectMocks
    private final ForkContentHandler classUnderTest = new ForkContentHandler(mockContentHandler, mockContentHandler);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testInvalidConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ForkContentHandler(null, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ForkContentHandler(mockContentHandler, null);
        });
    }

    @Test
    void testIgnorableWhitespace() throws SAXException {
        String testString = "This is a test";
        boolean result = false;
        try {
            classUnderTest.ignorableWhitespace(testString.toCharArray(), 1, 10);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testSkippedEntity() throws SAXException {
        String testString = "This is a test";
        boolean result = false;
        try {
            classUnderTest.skippedEntity(testString);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
