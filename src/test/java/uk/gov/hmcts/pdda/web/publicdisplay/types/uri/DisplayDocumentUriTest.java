package uk.gov.hmcts.pdda.web.publicdisplay.types.uri;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayDocumentUriTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Integer COURTID = 10;
    private static final int[] COURTROOMIDS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    @InjectMocks
    private final DisplayDocumentUri classUnderTest =
        new DisplayDocumentUri(Locale.UK, COURTID, DisplayDocumentType.DAILY_LIST, COURTROOMIDS);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetSimpleDocumentType() {
        String results = classUnderTest.getSimpleDocumentType();
        assertNotNull(results, "Result is Null");
    }

    @Test
    void testGetCourtRoomIdsWithoutUnassigned() {
        int[] results = classUnderTest.getCourtRoomIdsWithoutUnassigned();
        assertEquals(COURTROOMIDS.length, results.length, EQUALS);
    }

    @Test
    void testGetLocale() {
        Locale results = classUnderTest.getLocale();
        assertEquals(Locale.UK, results, EQUALS);
    }

    @Test
    void testGetType() {
        String results = classUnderTest.getType();
        assertEquals("document", results, EQUALS);
    }

    @Test
    void testEqualsFailure() {
        DisplayDocumentUri another = new DisplayDocumentUri("pd://document:81/DailyList:81");
        boolean result = classUnderTest.equals(another);
        assertFalse(result, "Result is not False");
    }

    @Test
    void testInvalidOutcomes() {
        boolean result;
        result = testInvalidParams(null, COURTID, DisplayDocumentType.DAILY_LIST);
        assertTrue(result, TRUE);
        result = testInvalidParams(Locale.UK, -1, DisplayDocumentType.DAILY_LIST);
        assertTrue(result, TRUE);
        result = testInvalidParams(Locale.UK, COURTID, null);
        assertTrue(result, TRUE);
        result = testInvalidParams(Locale.UK, COURTID, DisplayDocumentType.DAILY_LIST);
        assertTrue(result, TRUE);
    }

    private boolean testInvalidParams(Locale locale, int courtId, DisplayDocumentType name) {
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            new DisplayDocumentUri(locale, courtId, name, null);
        });
        return true;
    }
}
