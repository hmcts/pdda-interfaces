package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

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
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtDisplayConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtRotationSetConfigurationChange;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayConfigurationWorkerTest {

    private static final String TRUE = "Result is not True";
    private static final String NOTEQUALS = "Result is not Equal";

    private static final Integer COURT_ID = 20;
    private static final Integer COURT_ROOM_ID = 205;
    private static final Integer DISPLAY_ID = 30;
    private static final Integer ROTATION_SET_ID = 50;
    private static final String COURT_NAME = "Test Court Name";

    private static final String VALID_DISPLAY_URL = "pd://display/snaresbrook/453/reception/mainscreen";
    private static final String VALID_DOCUMENT_URL = "pd://document:81/DailyList:";

    private PdConfigurationControllerBean mockPdConfigurationControllerBean;

    @InjectMocks
    private final DisplayConfigurationWorker classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    private DisplayConfigurationWorker getClassUnderTest() {
        DisplayStoreControllerBean mockDisplayStoreControllerBean;
        mockPdConfigurationControllerBean = Mockito.mock(PdConfigurationControllerBean.class);
        PdDataControllerBean mockPdDataControllerBean = Mockito.mock(PdDataControllerBean.class);
        mockDisplayStoreControllerBean = Mockito.mock(DisplayStoreControllerBean.class);
        return new DisplayConfigurationWorker(COURT_ID, mockPdConfigurationControllerBean, mockPdDataControllerBean,
            mockDisplayStoreControllerBean);
    }

    @Test
    void tesGetRenderChangesCourtConfigurationChange() {
        boolean result = tesGetRenderChanges(getDummyCourtConfigurationChange());
        assertTrue(result, TRUE);
    }

    @Test
    void tesGetRenderChangesCourtConfigurationChangeFailure() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            CourtConfigurationChange courtConfigurationChange = new CourtConfigurationChange(COURT_ID + 1, COURT_NAME);
            tesGetRenderChanges(courtConfigurationChange);
        });
    }

    @Test
    void tesGetRenderChangesCourtDisplayConfigurationChange() {
        boolean result = tesGetRenderChanges(getDummyCourtDisplayConfigurationChange());
        assertTrue(result, TRUE);
    }

    @Test
    void tesGetRenderChangesCourtRotationSetConfigurationChange() {
        boolean result = tesGetRenderChanges(getDummyCourtRotationSetConfigurationChange());
        assertTrue(result, TRUE);
    }

    private boolean tesGetRenderChanges(CourtConfigurationChange courtConfigurationChange) {
        DisplayRotationSetData displayRotationSetData =
            DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL);
        DisplayRotationSetData[] displayRotationSetDataArray = {displayRotationSetData};
        Mockito.when(mockPdConfigurationControllerBean.getCourtConfiguration(COURT_ID))
            .thenReturn(displayRotationSetDataArray);
        Mockito.when(mockPdConfigurationControllerBean.getUpdatedDisplay(COURT_ID, DISPLAY_ID))
            .thenReturn(displayRotationSetDataArray);
        Mockito.when(mockPdConfigurationControllerBean.getUpdatedRotationSet(COURT_ID, ROTATION_SET_ID))
            .thenReturn(displayRotationSetDataArray);

        RenderChanges results = classUnderTest.getRenderChanges(courtConfigurationChange);

        assertNotNull(results, "Result is Null");
        return true;
    }

    @Test
    void tesGetRenderChangesArray() {
        DisplayDocumentType[] documentTypes = {DisplayDocumentTypeUtils.getDisplayDocumentType("DailyList")};
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(COURT_ID, COURT_ROOM_ID, "Test Court", 123);

        RenderChanges results = classUnderTest.getRenderChanges(documentTypes, courtRoom);

        assertNotNull(results, "Result is Null");
    }

    @Test
    void tesGetRenderChangesArrayFailure() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            DisplayDocumentType[] documentTypes = {DisplayDocumentTypeUtils.getDisplayDocumentType("DailyList")};
            CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(COURT_ID + 1, COURT_ROOM_ID, "Test Court", 123);
            classUnderTest.getRenderChanges(documentTypes, courtRoom);
        });
    }

    @Test
    void testDisplayRotationSetDataByDisplayComparator() {
        DisplayRotationSetData oldOne =
            DummyDisplayUtil.getDisplayRotationSetData(VALID_DISPLAY_URL, VALID_DOCUMENT_URL);
        
        DisplayRotationSetDataByDisplayComparator comparator = DisplayRotationSetDataByDisplayComparator.getInstance();
        assertEquals(0, comparator.compare(oldOne, oldOne), NOTEQUALS);
        Boolean isEquals = comparator == null || false;
        assertFalse(isEquals, "Result is not False");
        isEquals = comparator.equals(comparator);
        assertTrue(isEquals, "Result is not True");
    }

    private CourtConfigurationChange getDummyCourtConfigurationChange() {
        return new CourtConfigurationChange(COURT_ID, COURT_NAME);
    }

    private CourtDisplayConfigurationChange getDummyCourtDisplayConfigurationChange() {
        return new CourtDisplayConfigurationChange(COURT_ID, COURT_NAME, DISPLAY_ID);
    }

    private CourtRotationSetConfigurationChange getDummyCourtRotationSetConfigurationChange() {
        return new CourtRotationSetConfigurationChange(COURT_ID, COURT_NAME, ROTATION_SET_ID);
    }
}
