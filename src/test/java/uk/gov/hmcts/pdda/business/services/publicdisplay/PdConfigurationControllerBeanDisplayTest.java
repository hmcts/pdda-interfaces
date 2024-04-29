package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.ActiveCasesInRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * PdConfigurationControllerBeanDisplayTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.ExcessiveImports"})
class PdConfigurationControllerBeanDisplayTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 80;
    private static final Integer DISPLAY_ID = 60;
    private static final Integer ROTATION_SET_ID = 70;
    private static final Integer DISPLAY_DOCUMENT_ID = 90;
    private static final String YES = "Y";
    private static final String DAILYLIST = "DailyList";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbDisplayDocumentRepository mockXhbDisplayDocumentRepository;

    @Mock
    private ActiveCasesInRoomQuery mockActiveCasesInRoomQuery;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private VipDisplayDocumentQuery mockVipDisplayDocumentQuery;

    @Mock
    private VipDisplayCourtRoomQuery mockVipDisplayCourtRoomQuery;

    @Mock
    private XhbRotationSetDdRepository mockXhbRotationSetDdRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private VipCourtRoomsQuery mockVipQuery;

    @InjectMocks
    private final PdConfigurationControllerBean classUnderTest = new PdConfigurationControllerBean(mockEntityManager,
        mockXhbCourtRepository, mockXhbRotationSetsRepository, mockXhbRotationSetDdRepository, mockXhbDisplayRepository,
        mockPublicDisplayNotifier, mockVipDisplayDocumentQuery, mockVipDisplayCourtRoomQuery);

    @BeforeAll
    public static void setUp() throws Exception {
        Mockito.mockStatic(DisplayLocationDataHelper.class);
        Mockito.mockStatic(RotationSetMaintainHelper.class);
        Mockito.mockStatic(DisplayConfigurationHelper.class);
        Mockito.mockStatic(PublicDisplayActivationHelper.class);
    }

    @AfterAll
    public static void tearDown() throws Exception {
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
        // Setup
        List<XhbDisplayDocumentDao> dummyList = new ArrayList<>();
        dummyList.add(DummyPublicDisplayUtil.getXhbDisplayDocumentDao());
        dummyList.add(DummyPublicDisplayUtil.getXhbDisplayDocumentDao());

        Mockito.when(mockXhbDisplayDocumentRepository.findAll()).thenReturn(dummyList);

        // Run method
        XhbDisplayDocumentDao[] displayDocsArray = classUnderTest.getDisplayDocuments();

        // Check results
        assertArrayEquals(dummyList.toArray(), displayDocsArray, EQUALS);
    }

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
        xhbDisplayDao.setXhbDisplayLocation(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        xhbDisplayDao.setXhbCourtRooms(roomList);
        xhbDisplayDao.setXhbDisplayType(DummyPublicDisplayUtil.getXhbDisplayTypeDao());

        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        xhbDisplayDocumentDao.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        xhbDisplayDocumentDao.setDescriptionCode(DAILYLIST);

        XhbRotationSetDdDao xhbRotationSetDdDao1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xhbRotationSetDdDao1.setXhbDisplayDocument(xhbDisplayDocumentDao);

        XhbRotationSetDdDao xhbRotationSetDdDao2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xhbRotationSetDdDao2.setXhbDisplayDocument(xhbDisplayDocumentDao);

        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(xhbRotationSetDdDao1);
        xrsddList.add(xhbRotationSetDdDao2);
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        xhbRotationSetsDao.setRotationSetId(ROTATION_SET_ID);
        xhbRotationSetsDao.setCourtId(COURT_ID);

        Optional<XhbRotationSetsDao> xrs = Optional.of(xhbRotationSetsDao);
        Optional<XhbDisplayDao> xd = Optional.of(xhbDisplayDao);
        xd.get().setXhbRotationSet(xrs.get());
        String courtName = "Test Court Name";
        Optional<XhbCourtDao> court = Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, courtName));

        Mockito.when(mockXhbDisplayRepository.findById(DISPLAY_ID)).thenReturn(xd);
        Mockito.when(mockXhbCourtRepository.findById(COURT_ID)).thenReturn(court);
        Mockito.when(mockXhbRotationSetsRepository.findById(Long.valueOf(ROTATION_SET_ID))).thenReturn(xrs);
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);

        // Run Method
        DisplayRotationSetData[] result = classUnderTest.getUpdatedDisplay(COURT_ID, DISPLAY_ID);

        // Check results
        assertEquals(1, result.length, EQUALS);
        assertEquals(ROTATION_SET_ID, Integer.valueOf(result[0].getRotationSetId()), EQUALS);
        assertEquals(DISPLAY_ID, Integer.valueOf(result[0].getDisplayId()), EQUALS);
    }
}
