package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RotationSetCompiledRendererDelegateTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String NOTNULL = "Result is Null";
    private static final String VALID_DISPLAY_URL = "pd://display/snaresbrook/453/reception/mainscreen";
    private static final String VALID_DOCUMENT_URL = "pd://document:81/DailyList:";
    private static final long PAGE_DELAY = 1L;

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @TestSubject
    private final RotationSetCompiledRendererDelegate classUnderTest = new RotationSetCompiledRendererDelegate();

    @BeforeEach
    public void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testRotationSetCompiledRendererDelegate() {
        // Setup
        DisplayRotationSetData displayRotationSetData =
            getDummyDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL);
        DisplayRotationSet displayRotationSet =
            new DisplayRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        // Run
        String result = classUnderTest.getDisplayRotationSetHtml(displayRotationSet);
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testDisplayRotationSetDataCreate() {
        // Setup
        DisplayRotationSetData displayRotationSetData =
            getDummyDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL);
        DisplayRotationSet displayRotationSet =
            new DisplayRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        // Run
        displayRotationSet.create();

        StringBuilder buffer = new StringBuilder();
        displayRotationSet.appendToBuffer(buffer, "lineIndent");
        assertNotNull(displayRotationSet.toString(), NOTNULL);
    }

    private DisplayRotationSetData getDummyDisplayRotationSetData(String displayUrl, String documentUrl) {
        DisplayUri displayUri = getDummyDisplayUri(displayUrl);
        RotationSetDisplayDocument[] rotationSetDisplayDocumentArray =
            getDummyRotationSetDisplayDocumentArray(documentUrl);
        int displayId = 100;
        int rotationSetId = 200;
        String displayType = "42in";
        DisplayRotationSetData result = new DisplayRotationSetData(displayUri, rotationSetDisplayDocumentArray,
            displayId, rotationSetId, displayType);
        assertNotNull(result.toString(), NOTNULL);
        assertEquals(result, new DisplayRotationSetData(displayUri, rotationSetDisplayDocumentArray, displayId,
            rotationSetId, displayType), EQUALS);
        return result;
    }

    private DisplayUri getDummyDisplayUri(String displayUrl) {
        String courthouseName = "courthouseName";
        String courtsiteCode = "courtsiteCode";
        String location = "location";
        String display = "display";
        DisplayUri result = new DisplayUri(courthouseName, courtsiteCode, location, display);
        assertEquals(courthouseName, result.getCourthouseName(), EQUALS);
        assertEquals(courtsiteCode, result.getCourtsiteCode(), EQUALS);
        assertEquals(location, result.getLocation(), EQUALS);
        assertEquals(display, result.getDisplay(), EQUALS);
        result = new DisplayUri(displayUrl);
        assertEquals("display", result.getType(), EQUALS);
        assertEquals(Locale.getDefault(), result.getLocale(), EQUALS);
        return result;
    }

    private RotationSetDisplayDocument[] getDummyRotationSetDisplayDocumentArray(String documentUrl) {
        return new RotationSetDisplayDocument[] {getDummyRotationSetDisplayDocument(documentUrl)};
    }

    private RotationSetDisplayDocument getDummyRotationSetDisplayDocument(String documentUrl) {
        DisplayDocumentUri displayDocumentUri = getDummyDisplayDocumentUri(documentUrl);
        RotationSetDisplayDocument result = new RotationSetDisplayDocument(displayDocumentUri, PAGE_DELAY);
        assertNotNull(result.toString(), NOTNULL);
        assertEquals(result, new RotationSetDisplayDocument(displayDocumentUri, PAGE_DELAY), EQUALS);
        assertEquals(displayDocumentUri, result.getDisplayDocumentUri(), EQUALS);
        assertEquals(PAGE_DELAY, result.getPageDelay(), EQUALS);
        return result;
    }

    private DisplayDocumentUri getDummyDisplayDocumentUri(String documentUrl) {
        DisplayDocumentUri result = new DisplayDocumentUri(documentUrl);
        assertEquals("document", result.getType(), EQUALS);
        return result;
    }
}
