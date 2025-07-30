package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.ActiveCasesInRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * PdConfigurationControllerBeanDisplayTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
class PdConfigurationControllerBeanDisplayTest {

    private static final Logger LOG =
        LoggerFactory.getLogger(PdConfigurationControllerBeanDisplayTest.class);

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 80;
    private static final Integer DISPLAY_ID = 60;
    private static final Integer ROTATION_SET_ID = 70;
    private static final Integer DISPLAY_DOCUMENT_ID = 90;
    private static final String YES = "Y";
    private static final String DAILYLIST = "DailyList";
    
    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbDisplayDocumentRepository mockXhbDisplayDocumentRepository;

    @Mock
    private XhbDisplayTypeRepository mockXhbDisplayTypeRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

    @Mock
    private ActiveCasesInRoomQuery mockActiveCasesInRoomQuery;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private XhbRotationSetDdRepository mockXhbRotationSetDdRepository;

    @Mock
    private DisplayRotationSetDataHelper mockDisplayRotationSetDataHelper;

    @Mock
    private VipCourtRoomsQuery mockVipQuery;
    
    @Mock
    private EntityManager mockEntityManager;

    private PdConfigurationControllerBean classUnderTest;

    @BeforeEach
    void init() {
        classUnderTest = Mockito.spy(new PdConfigurationControllerBean(mockEntityManager,
            mockXhbCourtRepository, mockXhbRotationSetsRepository, mockXhbRotationSetDdRepository,
            mockXhbDisplayTypeRepository, mockXhbDisplayRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockPublicDisplayNotifier,
            Mockito.mock(VipDisplayDocumentQuery.class),
            Mockito.mock(VipDisplayCourtRoomQuery.class), new DisplayRotationSetDataHelper() // default
        ));

        // Force override of the helper with your mock
        Mockito.doReturn(mockDisplayRotationSetDataHelper).when(classUnderTest)
            .getDisplayRotationSetDataHelper();
        Mockito.doReturn(mockXhbDisplayDocumentRepository)
            .when(classUnderTest)
            .getXhbDisplayDocumentRepository();
        Mockito.when(mockXhbDisplayDocumentRepository.getEntityManager())
            .thenReturn(mockEntityManager);
        Mockito.when(mockXhbDisplayDocumentRepository.findAll()).thenReturn(List.of(
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao(),
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao()
        ));

        
        @SuppressWarnings("unchecked")
        TypedQuery<XhbDisplayDocumentDao> mockQuery = Mockito.mock(TypedQuery.class);

        List<XhbDisplayDocumentDao> dummyList = List.of(
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao(),
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao()
        );

        Mockito.when(mockXhbDisplayDocumentRepository.getEntityManager())
               .thenReturn(mockEntityManager);
        Mockito.when(mockEntityManager.createQuery(Mockito.anyString(), Mockito.eq(XhbDisplayDocumentDao.class)))
               .thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(dummyList);
    }

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(DisplayLocationDataHelper.class);
        Mockito.mockStatic(RotationSetMaintainHelper.class);
        Mockito.mockStatic(DisplayConfigurationHelper.class);
        Mockito.mockStatic(PublicDisplayActivationHelper.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testInitialiseDisplay() {
        // Setup
        mockPublicDisplayNotifier.sendMessage(Mockito.isA(ConfigurationChangeEvent.class));

        // Run method
        boolean result = false;
        try {
            classUnderTest.initialiseDisplay(COURT_ID, DISPLAY_ID);
            result = true;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }


    @Test
    void testGetDisplayDocuments() {
        XhbDisplayDocumentDao doc = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        List<XhbDisplayDocumentDao> dummyList = List.of(doc, doc);

        Mockito.when(mockXhbDisplayDocumentRepository.findAll()).thenReturn(dummyList);

        XhbDisplayDocumentDao[] displayDocsArray = classUnderTest.getDisplayDocuments();

        System.out.println("Display documents: " + displayDocsArray.length);

        assertEquals(2, displayDocsArray.length);
        assertEquals(doc, displayDocsArray[0]);
        assertEquals(doc, displayDocsArray[1]);
    }



    @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
    @Test
    void testGetUpdatedDisplay() {
        // Setup
        List<XhbCourtRoomDao> roomList = new ArrayList<>();
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());

        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        xhbDisplayDao.setDisplayId(DISPLAY_ID);
        xhbDisplayDao.setRotationSetId(ROTATION_SET_ID);
        xhbDisplayDao.setShowUnassignedYn(YES);

        XhbDisplayDocumentDao xhbDisplayDocumentDao =
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        xhbDisplayDocumentDao.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        xhbDisplayDocumentDao.setDescriptionCode(DAILYLIST);

        XhbRotationSetDdDao xhbRotationSetDdDao1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        XhbRotationSetDdDao xhbRotationSetDdDao2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();

        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(xhbRotationSetDdDao1);
        xrsddList.add(xhbRotationSetDdDao2);
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        xhbRotationSetsDao.setRotationSetId(ROTATION_SET_ID);
        xhbRotationSetsDao.setCourtId(COURT_ID);

        expectGetEntityManager(mockXhbDisplayRepository);
        expectGetEntityManager(mockXhbCourtRepository);
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
        
        Optional<XhbRotationSetsDao> xrs = Optional.of(xhbRotationSetsDao);
        Optional<XhbDisplayDao> xd = Optional.of(xhbDisplayDao);
        String courtName = "Test Court Name";
        Optional<XhbCourtDao> court =
            Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, courtName));

        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);

        Mockito.when(mockXhbDisplayRepository.findByIdSafe(DISPLAY_ID)).thenReturn(xd);
        Mockito.when(mockXhbCourtRepository.findByIdSafe(COURT_ID)).thenReturn(court);
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Long.valueOf(ROTATION_SET_ID)))
            .thenReturn(xrs);
        Mockito
            .when(
                mockXhbRotationSetDdRepository.findByRotationSetIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));
        Mockito.when(mockXhbDisplayTypeRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayTypeDao()));
        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));

        // Setup required mock data
        DisplayUri displayUri = new DisplayUri("swansea", "sitecode", "location", "desc");
        RotationSetDisplayDocument[] rotationDocs =
            {new RotationSetDisplayDocument(new DisplayDocumentUri(Locale.UK, COURT_ID,
                DisplayDocumentTypeUtils.getDisplayDocumentType("DailyList", "en", "GB"),
                new int[] {100}), 10L)};

        DisplayRotationSetData mockData = new DisplayRotationSetData(displayUri, rotationDocs,
            DISPLAY_ID, ROTATION_SET_ID, "DAILYLIST");

        Mockito.when(mockDisplayRotationSetDataHelper.getDisplayRotationSetData(
            Mockito.any(XhbCourtDao.class), Mockito.any(XhbDisplayDao.class),
            Mockito.any(XhbRotationSetsDao.class), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(mockData);


        Mockito.when(mockXhbDisplayRepository.findByRotationSetId(ROTATION_SET_ID))
            .thenReturn(List.of(xhbDisplayDao));

        LOG.debug("Display rotationSetId: " + xhbDisplayDao.getRotationSetId());
        LOG.debug("RotationSet mocked present? " + xrs.isPresent());


        // Run Method
        DisplayRotationSetData[] result = classUnderTest.getUpdatedDisplay(COURT_ID, DISPLAY_ID);

        Mockito.verify(mockXhbRotationSetsRepository).findByIdSafe(Long.valueOf(ROTATION_SET_ID));

        LOG.debug("Result length: " + result.length);
        for (DisplayRotationSetData drsd : result) {
            LOG.debug("RotationSetId: " + drsd.getRotationSetId() + ", DisplayId: "
                + drsd.getDisplayId());
        }

        // Check results
        assertEquals(1, result.length, EQUALS);
        assertEquals(ROTATION_SET_ID, result[0].getRotationSetId(), EQUALS);
        assertEquals(DISPLAY_ID, result[0].getDisplayId(), EQUALS);
    }

    @SuppressWarnings("rawtypes")
    private void expectGetEntityManager(AbstractRepository mockRepository) {
        Mockito.when(mockRepository.getEntityManager()).thenReturn(mockEntityManager);
    }

}
