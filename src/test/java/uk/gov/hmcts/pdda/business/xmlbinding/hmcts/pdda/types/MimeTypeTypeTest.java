package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MimeTypeTypeTest {

    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String NOTEQUALS = "Result is not Equals";

    @BeforeEach
    public void setup() {
        // Do nothing
    }

    @AfterEach
    public void teardown() {
        // Do nothing
    }

    @Test
    void testInvalidType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MimeTypeType.valueOf("Invalid");
        });
    }

    @Test
    void testMimeTypeTypeHtm() {
        boolean result = testType(MimeTypeType.HTM, MimeTypeType.HTM_TYPE);
        assertTrue(result, TRUE);
    }

    @Test
    void testMimeTypeTypePdf() {
        boolean result = testType(MimeTypeType.PDF, MimeTypeType.PDF_TYPE);
        assertTrue(result, TRUE);
    }

    @Test
    void testMimeTypeTypeFop() {
        boolean result = testType(MimeTypeType.FOP, MimeTypeType.FOP_TYPE);
        assertTrue(result, TRUE);
    }

    @SuppressWarnings("static-access")
    private boolean testType(MimeTypeType classUnderTest, int type) {
        assertEquals(type, classUnderTest.getType(), NOTEQUALS);
        assertNotNull(classUnderTest.enumerate(), "Result is Null");
        assertNotNull(classUnderTest.toString(), "Result is Null");
        return true;
    }

    @Test
    void testTypeSuccess() {
        MimeTypeType htmMimeType = MimeTypeType.HTM;
        assertTrue(htmMimeType.equals(MimeTypeType.fromString("HTM")), TRUE);
    }

    @Test
    void testTypeFailure() {
        MimeTypeType htmMimeType = MimeTypeType.HTM;
        assertFalse(htmMimeType.equals(MimeTypeType.fromString("XXX")), FALSE);
    }
}
