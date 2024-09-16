package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import jakarta.persistence.EntityManager;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtDisplayConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtRotationSetConfigurationChange;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: DefaultDisplayConfigurationReaderTest Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Chris Vincent
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.ExcessiveImports")
class DefaultDisplayConfigurationReaderTest {

    private static final Integer COURT_ID = 20;
    private static final Integer DISPLAY_ID = 30;
    private static final Integer ROTATION_SET_ID = 40;
    private static final String YES = "Y";
    private static final String DISPLAY_SIZE = "42in";
    private static final String NOTNULL = "Result is Null";
    private static final String DAILYLIST = "DailyList";
    private static final String TESTCOURT1 = "TestCourt1";
    private static final String TESTCOURT3 = "TestCourt3";
    private static final String TESTCOURT5 = "TestCourt5";
    private static final int[] COURTROOMIDS = {8112, 8113, 8114};
    private static final Locale DUMMYLOCALE = Locale.UK;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PdConfigurationControllerBean mockPdConfigurationController;

    @Mock
    private PdDataControllerBean mockPdDataControllerBean;

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @Mock
    private DisplayConfigurationWorker mockWorker;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbRotationSetDdRepository mockXhbRotationSetDdRepository;

    @Mock
    private CourtConfigurationChange mockCourtConfigurationChange;


    @TestSubject
    private DefaultDisplayConfigurationReader classUnderTest;

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetRenderChangesArray() {
        DisplayDocumentType displayDocumentType = DisplayDocumentTypeUtils.getDisplayDocumentType(DAILYLIST,
            DUMMYLOCALE.getLanguage(), DUMMYLOCALE.getCountry());
        DisplayRotationSetData displayRotationSetData = new DisplayRotationSetData(DummyDisplayUtil.getDisplayUri(),
            new RotationSetDisplayDocument[] {}, 100, 200, DISPLAY_SIZE);
        DisplayDocumentUri displayDocumentUri =
            new DisplayDocumentUri(DUMMYLOCALE, COURT_ID, displayDocumentType, COURTROOMIDS);
        RenderChanges renderChanges = new RenderChanges();
        renderChanges.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStartRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        renderChanges.addStopRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        assertNotNull(renderChanges.toString(), NOTNULL);
        int[] courtsForPublicDisplay = {80};
        Integer courtId = -1;
        Integer courtRoomId = -1;
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(courtId, courtRoomId);
        DisplayDocumentType[] documentTypes = {displayDocumentType};
        Mockito.when(mockPdConfigurationController.getCourtsForPublicDisplay()).thenReturn(courtsForPublicDisplay);
        Mockito.when(mockWorker.getRenderChanges(documentTypes, courtRoom)).thenReturn(renderChanges);

        classUnderTest = new DefaultDisplayConfigurationReader(mockPdConfigurationController, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockWorker);
        RenderChanges result = classUnderTest.getRenderChanges(documentTypes, courtRoom);

        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetRenderChangesCourtConfigurationChange() {
        // Setup
        List<XhbRotationSetsDao> xrsList = new ArrayList<>();
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());

        XhbCourtDao court1 = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);
        XhbCourtDao court2 = DummyCourtUtil.getXhbCourtDao(3, TESTCOURT3);
        XhbCourtDao court3 = DummyCourtUtil.getXhbCourtDao(5, TESTCOURT5);
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        dummyCourtList.add(court1);
        dummyCourtList.add(court2);
        dummyCourtList.add(court3);
        List<XhbDisplayDao> xdList = new ArrayList<>();
        xdList.add(getDummyDisplayDao());

        
        DisplayRotationSetData displayRotationSetData = new DisplayRotationSetData(DummyDisplayUtil.getDisplayUri(),
            new RotationSetDisplayDocument[] {}, 100, 200, DISPLAY_SIZE);
        DisplayDocumentType displayDocumentType = DisplayDocumentTypeUtils.getDisplayDocumentType(DAILYLIST,
            DUMMYLOCALE.getLanguage(), DUMMYLOCALE.getCountry());
        DisplayDocumentUri displayDocumentUri =
            new DisplayDocumentUri(DUMMYLOCALE, COURT_ID, displayDocumentType, COURTROOMIDS);
        RenderChanges renderChanges = new RenderChanges();
        renderChanges.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStartRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        renderChanges.addStopRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        assertNotNull(renderChanges.toString(), NOTNULL);
        XhbCourtDao xhbCourtDao = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);
        Optional<XhbCourtDao> courtDao = Optional.of(xhbCourtDao);

        Mockito.when(mockXhbCourtRepository.findAll()).thenReturn(dummyCourtList);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(xrsList);
        Mockito.when(mockXhbCourtRepository.findById(Mockito.isA(Integer.class))).thenReturn(courtDao);
        Mockito.when(mockPdConfigurationController.getCourtsForPublicDisplay()).thenReturn(new int[] {80});
        Mockito.when(mockWorker.getRenderChanges(Mockito.isA(CourtConfigurationChange.class)))
            .thenReturn(renderChanges);
        Mockito.when(mockXhbDisplayRepository.findByRotationSetId(Mockito.isA(Integer.class))).thenReturn(xdList);

        classUnderTest = new DefaultDisplayConfigurationReader(mockPdConfigurationController, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockWorker);
        RenderChanges result = classUnderTest.getRenderChanges(new CourtConfigurationChange(COURT_ID));

        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetRenderChangesCourtDisplayConfigurationChange() {
        // Setup
        List<XhbRotationSetsDao> xrsList = new ArrayList<>();
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());

        XhbCourtDao court1 = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);
        XhbCourtDao court2 = DummyCourtUtil.getXhbCourtDao(3, TESTCOURT3);
        XhbCourtDao court3 = DummyCourtUtil.getXhbCourtDao(5, TESTCOURT5);
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        dummyCourtList.add(court1);
        dummyCourtList.add(court2);
        dummyCourtList.add(court3);
        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());
        xrsddList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());

        DisplayDocumentType displayDocumentType = DisplayDocumentTypeUtils.getDisplayDocumentType(DAILYLIST,
            DUMMYLOCALE.getLanguage(), DUMMYLOCALE.getCountry());
        DisplayDocumentUri displayDocumentUri =
            new DisplayDocumentUri(DUMMYLOCALE, COURT_ID, displayDocumentType, COURTROOMIDS);
        DisplayRotationSetData displayRotationSetData = new DisplayRotationSetData(DummyDisplayUtil.getDisplayUri(),
            new RotationSetDisplayDocument[] {}, 100, 200, DISPLAY_SIZE);
        RenderChanges renderChanges = new RenderChanges();
        renderChanges.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStartRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        renderChanges.addStopRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        assertNotNull(renderChanges.toString(), NOTNULL);

        int[] courtsForPublicDisplay = {80};
        Optional<XhbRotationSetsDao> xrs = Optional.of(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        Optional<XhbDisplayDao> displayDao = Optional.of(getDummyDisplayDao());
        XhbCourtDao xhbCourtDao = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);

        Mockito.when(mockXhbCourtRepository.findAll()).thenReturn(dummyCourtList);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(xrsList);
        Mockito.when(mockXhbDisplayRepository.findById(Mockito.isA(Integer.class))).thenReturn(displayDao);
        Mockito.when(mockXhbCourtRepository.findById(Mockito.isA(Integer.class))).thenReturn(Optional.of(xhbCourtDao));
        Mockito.when(mockXhbRotationSetsRepository.findById(Long.valueOf(ROTATION_SET_ID))).thenReturn(xrs);
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockPdConfigurationController.getCourtsForPublicDisplay()).thenReturn(courtsForPublicDisplay);
        Mockito.when(mockWorker.getRenderChanges(Mockito.isA(CourtConfigurationChange.class)))
            .thenReturn(renderChanges);


        classUnderTest = new DefaultDisplayConfigurationReader(mockPdConfigurationController, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockWorker);
        RenderChanges result =
            classUnderTest.getRenderChanges(new CourtDisplayConfigurationChange(COURT_ID, DISPLAY_ID));

        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetRenderChangesCourtRotationSetConfigurationChange() {
        // Setup
        List<XhbRotationSetsDao> xrsList = new ArrayList<>();
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());

        XhbCourtDao court1 = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);
        XhbCourtDao court2 = DummyCourtUtil.getXhbCourtDao(3, TESTCOURT3);
        XhbCourtDao court3 = DummyCourtUtil.getXhbCourtDao(5, TESTCOURT5);
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        dummyCourtList.add(court1);
        dummyCourtList.add(court2);
        dummyCourtList.add(court3);
        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());
        xrsddList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());

        List<XhbDisplayDao> xdList = new ArrayList<>();
        xdList.add(getDummyDisplayDao());
        DisplayRotationSetData displayRotationSetData = new DisplayRotationSetData(DummyDisplayUtil.getDisplayUri(),
            new RotationSetDisplayDocument[] {}, 100, 200, DISPLAY_SIZE);
        DisplayDocumentType displayDocumentType = DisplayDocumentTypeUtils.getDisplayDocumentType(DAILYLIST,
            DUMMYLOCALE.getLanguage(), DUMMYLOCALE.getCountry());
        DisplayDocumentUri displayDocumentUri =
            new DisplayDocumentUri(DUMMYLOCALE, COURT_ID, displayDocumentType, COURTROOMIDS);
        RenderChanges renderChanges = new RenderChanges();
        renderChanges.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStartRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        renderChanges.addStopRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        assertNotNull(renderChanges.toString(), NOTNULL);
        int[] courtsForPublicDisplay = {80};
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        Optional<XhbRotationSetsDao> xrs = Optional.of(xhbRotationSetsDao);
        XhbCourtDao xhbCourtDao = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);

        Mockito.when(mockXhbCourtRepository.findAll()).thenReturn(dummyCourtList);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(xrsList);
        Mockito.when(mockXhbRotationSetsRepository.findById(Long.valueOf(ROTATION_SET_ID))).thenReturn(xrs);
        Mockito.when(mockXhbCourtRepository.findById(Mockito.isA(Integer.class))).thenReturn(Optional.of(xhbCourtDao));
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockPdConfigurationController.getCourtsForPublicDisplay()).thenReturn(courtsForPublicDisplay);
        Mockito.when(mockWorker.getRenderChanges(Mockito.isA(CourtConfigurationChange.class)))
            .thenReturn(renderChanges);
        Mockito.when(mockXhbDisplayRepository.findByRotationSetId(Mockito.isA(Integer.class))).thenReturn(xdList);


        classUnderTest = new DefaultDisplayConfigurationReader(mockPdConfigurationController, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockWorker);
        RenderChanges result =
            classUnderTest.getRenderChanges(new CourtRotationSetConfigurationChange(COURT_ID, ROTATION_SET_ID));

        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetRenderChanges() {
        // Setup
        List<XhbRotationSetsDao> xrsList = new ArrayList<>();
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());

        XhbCourtDao court1 = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);
        XhbCourtDao court2 = DummyCourtUtil.getXhbCourtDao(3, TESTCOURT3);
        XhbCourtDao court3 = DummyCourtUtil.getXhbCourtDao(5, TESTCOURT5);
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        dummyCourtList.add(court1);
        dummyCourtList.add(court2);
        dummyCourtList.add(court3);
        DisplayRotationSetData displayRotationSetData = new DisplayRotationSetData(DummyDisplayUtil.getDisplayUri(),
            new RotationSetDisplayDocument[] {}, 100, 200, DISPLAY_SIZE);
        DisplayDocumentType displayDocumentType = DisplayDocumentTypeUtils.getDisplayDocumentType(DAILYLIST,
            DUMMYLOCALE.getLanguage(), DUMMYLOCALE.getCountry());
        DisplayDocumentUri displayDocumentUri =
            new DisplayDocumentUri(DUMMYLOCALE, COURT_ID, displayDocumentType, COURTROOMIDS);
        RenderChanges renderChanges = new RenderChanges();
        renderChanges.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStartRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        renderChanges.addStopRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        assertNotNull(renderChanges.toString(), NOTNULL);

        int[] courtsForPublicDisplay = {80};

        Mockito.when(mockXhbCourtRepository.findAll()).thenReturn(dummyCourtList);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(xrsList);
        Mockito.when(mockPdConfigurationController.getCourtsForPublicDisplay()).thenReturn(courtsForPublicDisplay);
        Mockito.when(mockWorker.getRenderChanges(Mockito.isA(CourtConfigurationChange.class)))
            .thenReturn(renderChanges);

        classUnderTest = new DefaultDisplayConfigurationReader(mockPdConfigurationController, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockWorker);
        RenderChanges result = classUnderTest.getRenderChanges(mockCourtConfigurationChange);

        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetConfiguredCourtIds() {
        // Setup
        List<XhbRotationSetsDao> xrsList = new ArrayList<>();
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        xrsList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());

        XhbCourtDao court1 = DummyCourtUtil.getXhbCourtDao(COURT_ID, TESTCOURT1);
        XhbCourtDao court2 = DummyCourtUtil.getXhbCourtDao(3, TESTCOURT3);
        XhbCourtDao court3 = DummyCourtUtil.getXhbCourtDao(5, TESTCOURT5);
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        dummyCourtList.add(court1);
        dummyCourtList.add(court2);
        dummyCourtList.add(court3);
        DisplayRotationSetData displayRotationSetData = new DisplayRotationSetData(DummyDisplayUtil.getDisplayUri(),
            new RotationSetDisplayDocument[] {}, 100, 200, DISPLAY_SIZE);
        DisplayDocumentType displayDocumentType = DisplayDocumentTypeUtils.getDisplayDocumentType(DAILYLIST,
            DUMMYLOCALE.getLanguage(), DUMMYLOCALE.getCountry());
        DisplayDocumentUri displayDocumentUri =
            new DisplayDocumentUri(DUMMYLOCALE, COURT_ID, displayDocumentType, COURTROOMIDS);
        RenderChanges renderChanges = new RenderChanges();
        renderChanges.addStartDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStopDocument(displayDocumentUri, mockPdDataControllerBean, mockDisplayStoreControllerBean);
        renderChanges.addStartRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        renderChanges.addStopRotationSet(displayRotationSetData, mockDisplayStoreControllerBean);
        assertNotNull(renderChanges.toString(), NOTNULL);

        int[] courtsForPublicDisplay = {80};

        Mockito.when(mockXhbCourtRepository.findAll()).thenReturn(dummyCourtList);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(xrsList);
        Mockito.when(mockPdConfigurationController.getCourtsForPublicDisplay()).thenReturn(courtsForPublicDisplay);
        Mockito.when(mockWorker.getRenderChanges(Mockito.isA(CourtConfigurationChange.class)))
            .thenReturn(renderChanges);

        classUnderTest = new DefaultDisplayConfigurationReader(mockPdConfigurationController, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockWorker);
        int[] result = classUnderTest.getConfiguredCourtIds();

        assertNotNull(result, NOTNULL);
    }

    private XhbDisplayDao getDummyDisplayDao() {
        XhbDisplayDao returnObject = DummyPublicDisplayUtil.getXhbDisplayDao();
        returnObject.setShowUnassignedYn(YES);
        return returnObject;
    }
}
