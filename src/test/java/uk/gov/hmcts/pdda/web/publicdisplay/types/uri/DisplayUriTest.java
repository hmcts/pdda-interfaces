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
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayUriTest {

    private static final String TRUE = "Result is not True";

    private static final String COURTHOUSENAME = "snaresbrook";
    private static final String COURTSITECODE = "courtsiteCode";
    private static final String LOCATION = "location";
    private static final String DISPLAY = "display";

    @InjectMocks
    private final DisplayUri classUnderTest = new DisplayUri(COURTHOUSENAME, COURTSITECODE, LOCATION, DISPLAY);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDisplayUrlEquals() {
        DisplayUri anotherOne = new DisplayUri("anotherOne", COURTSITECODE, LOCATION, DISPLAY);
        assertNotEquals(classUnderTest, anotherOne, "Result is Equal");
        assertEquals(classUnderTest.compareTo(anotherOne), 18, "Result is not Equal");
        assertNotEquals(classUnderTest.compareTo(anotherOne) * -1, classUnderTest.hashCode(), "Results are Equal");
    }

    @Test
    void testDisplayUrlInvalidCourtHouse() {
        boolean result = testDisplayUriInvalidParams(null, COURTSITECODE, LOCATION, DISPLAY);
        assertTrue(result, TRUE);
    }

    @Test
    void testDisplayUrlInvalidCourtSiteCode() {
        boolean result = testDisplayUriInvalidParams(COURTHOUSENAME, null, LOCATION, DISPLAY);
        assertTrue(result, TRUE);
    }

    @Test
    void testDisplayUrlInvalidLocation() {
        boolean result = testDisplayUriInvalidParams(COURTHOUSENAME, COURTSITECODE, null, DISPLAY);
        assertTrue(result, TRUE);
    }

    @Test
    void testDisplayUrlInvalidMax() {
        boolean result = testDisplayUriInvalidParams(COURTHOUSENAME, COURTSITECODE, LOCATION, null);
        assertTrue(result, TRUE);
    }

    @Test
    void testDisplayUrlInvalidDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append("1234567890");
        }
        String invalidDisdplay = sb.toString();
        boolean result = testDisplayUriInvalidParams(COURTHOUSENAME, COURTSITECODE, LOCATION, invalidDisdplay);
        assertTrue(result, TRUE);
    }

    private boolean testDisplayUriInvalidParams(final String courthouseName, final String courtsiteCode,
        final String location, final String display) {
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            new DisplayUri(courthouseName, courtsiteCode, location, display);
        });
        return true;
    }
}
