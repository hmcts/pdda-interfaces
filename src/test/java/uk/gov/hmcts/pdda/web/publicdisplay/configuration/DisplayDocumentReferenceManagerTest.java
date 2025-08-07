package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDisplayDocumentUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayDocumentReferenceManagerTest {

    private static final String TRUE = "Result is not True";
    private static final String VALID_DISPLAY_URL = "pd://display/snaresbrook/453/reception/mainscreen";
    private static final String VALID_DOCUMENT_URL = "pd://document:81/DailyList:";

    @Mock
    private PdDataControllerBean mockPdDataControllerBean;

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @InjectMocks
    private final DisplayDocumentReferenceManager classUnderTest =
        new DisplayDocumentReferenceManager(mockPdDataControllerBean, mockDisplayStoreControllerBean);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testAddDisplayDocumentReferences() {
        boolean result = false;
        try {
            classUnderTest.addDisplayDocumentReferences(
                DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL));
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testRemoveDisplayDocumentReferences() {
        boolean result = false;
        try {
            classUnderTest.removeDisplayDocumentReferences(
                DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL));
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetRenderChanges() {
        boolean result = false;
        try {
            classUnderTest.getRenderChanges(
                new DisplayDocumentType[] {DisplayDocumentTypeUtils.getDisplayDocumentType("DailyList")},
                DummyCourtUtil.getCourtRoomIdentifier());
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testFillInRenderChanges() {
        boolean result = false;
        try {
            classUnderTest.fillInRenderChanges(getDummyRenderChanges());
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    private RenderChanges getDummyRenderChanges() {
        RenderChanges result = new RenderChanges();
        DisplayDocumentUri displayDocumentUri = DummyDisplayDocumentUtil.getDisplayDocumentUri();
        result.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        result.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        result.addStartRotationSet(DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL),
            mockDisplayStoreControllerBean);
        result.addStopRotationSet(DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL),
            mockDisplayStoreControllerBean);
        return result;
    }
}
