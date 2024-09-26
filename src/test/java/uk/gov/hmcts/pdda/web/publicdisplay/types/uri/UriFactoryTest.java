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
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.UriFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.UnsupportedUriException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("static-access")
class UriFactoryTest {

    private static final String TRUE = "Result is not True";
    private static final String INVALID = "invalid";

    @InjectMocks
    private final UriFactory classUnderTest = new TestUriFactory();

    @BeforeAll
    public static void setUp() {
        // Nothing to do
    }

    @AfterAll
    public static void tearDown() {
        // Nothing to do
    }

    @Test
    void testCreateDocument() {
        boolean result = testCreate("pd://document:81/DailyList:81");
        assertTrue(result, TRUE);
    }

    @Test
    void testCreateDisplay() {
        boolean result = testCreate("pd://display/snaresbrook/453/reception/mainscreen");
        assertTrue(result, TRUE);
    }

    @Test
    void testCreateInvalid() {
        Assertions.assertThrows(UnsupportedUriException.class, () -> {
            classUnderTest.create(INVALID);
        });
    }

    private boolean testCreate(String uriString) {
        boolean result = false;
        try {
            classUnderTest.create(uriString);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        return result;
    }

    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    class TestUriFactory extends UriFactory {

    }
}
