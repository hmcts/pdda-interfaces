package uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset;

import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayRotationSetTest {

    private static final String TRUE = "Result is not True";
    private static final String VALID_DISPLAY_URL =
        "pd://display/snaresbrook/453/reception/mainscreen";
    private static final String VALID_DOCUMENT_URL = "pd://document:81/DailyList:";

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @TestSubject
    private final DisplayRotationSet classUnderTest = new DisplayRotationSet(
        DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL),
        mockDisplayStoreControllerBean);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDisplayRotationSetSuccess() {
        assertSame(Renderable.ROTATION_SET, classUnderTest.getRenderableType(),
            "Result is not Same");
    }

    @Test
    void testInvalidDisplayOutcomes() {
        String[] testValues = {"pd://invalid",
                               "pd://display/~invalid/",
                               "pd://display/snaresbrook/~invalid/",
                               "pd://display/snaresbrook/453/reception/~invalid",
                               "pd://display/snaresbrook/453/~invalid/"};
        
        for (String testValue : testValues) {
            boolean result = testDisplayRotationSetInvalidDisplayParams(testValue);
            assertTrue(result, TRUE);
        }
    }

    private boolean testDisplayRotationSetInvalidDisplayParams(String url) {
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            new DisplayRotationSet(
                DummyDisplayUtil.getDisplayRotationSetData(url, VALID_DOCUMENT_URL),
                mockDisplayStoreControllerBean);
        });
        return true;
    }

    @Test
    void testInvalidDocumentOutcomes() {
        String[] testValues = {"pd://invalid",
                               "pd://document:",
                               "pd://document:invalid/",
                               "pd://document:81/~invalid:",
                               "pd://document:81/invalid:"};

        for (String testValue : testValues) {
            boolean result = testDisplayRotationSetInvalidDocumentParams(testValue);
            assertTrue(result, TRUE);
        }
    }

    private boolean testDisplayRotationSetInvalidDocumentParams(String url) {
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            new DisplayRotationSet(
                DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, url),
                mockDisplayStoreControllerBean);
        });
        return true;
    }

    @Test
    void testDisplayUriMaxLength() {
        StringBuilder maxLengthUri = new StringBuilder();
        for (int i = 0; i < DisplayUri.MAX_DISPLAY_URI_LENGTH; i++) {
            maxLengthUri.append('X');
        }
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            DummyDisplayUtil.getDisplayUri(maxLengthUri.toString());
        });
    }
}
