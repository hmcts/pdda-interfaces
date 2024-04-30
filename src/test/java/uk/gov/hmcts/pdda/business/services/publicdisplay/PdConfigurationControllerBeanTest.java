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
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
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
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.ActiveCasesInRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayConfiguration;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayLocationComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetDdComplexValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: PdConfigurationControllerBean Test.
 * </p>
 * <p>
 * Description: Unit tests for the PdConfigurationControllerBean class
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
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.GodClass", "PMD.TooManyFields"})
class PdConfigurationControllerBeanTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 80;
    private static final Integer DISPLAY_ID = 60;
    private static final Integer ROTATION_SET_ID = 70;
    private static final Integer SCHEDULED_HEARING_ID = 80;
    private static final Integer DISPLAY_DOCUMENT_ID = 90;
    private static final Integer ROTATION_SET_DD_ID = 1;
    private static final String YES = "Y";
    private static final String COURTSITE1 = "Court Site 1";
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
    private XhbDisplayTypeRepository mockXhbDisplayTypeRepository;

    @Mock
    private ActiveCasesInRoomQuery mockActiveCasesInRoomQuery;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

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
    private final PdConfigurationControllerBean classUnderTest =
        new PdConfigurationControllerBean(mockEntityManager, mockXhbCourtRepository, mockXhbRotationSetsRepository,
            mockXhbRotationSetDdRepository, mockXhbDisplayTypeRepository, mockXhbDisplayRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
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
    void testSetDisplayDocumentsForRotationSet() {
        // Setup
        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
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

        Optional<XhbRotationSetsDao> rotationSetsDao = Optional.of(xhbRotationSetsDao);
        RotationSetComplexValue rsComplex = new RotationSetComplexValue();
        XhbRotationSetDdDao xhbRotationSetDdDao = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xhbRotationSetDdDao.setRotationSetDdId(ROTATION_SET_DD_ID);
        RotationSetDdComplexValue rsddComplex =
            new RotationSetDdComplexValue(xhbRotationSetDdDao, xhbDisplayDocumentDao);
        rsComplex.setRotationSetDao(rotationSetsDao.get());
        rsComplex.addRotationSetDdComplexValue(rsddComplex);

        boolean result = false;
        try {
            Mockito.when(mockXhbRotationSetsRepository.findById(Mockito.isA(Long.class))).thenReturn(rotationSetsDao);
            Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
                .thenReturn(Optional.of(xhbDisplayDocumentDao));

            mockXhbRotationSetDdRepository.delete(Optional.of(xrsddList.get(1)));
            Mockito.when(mockXhbRotationSetDdRepository.update(Mockito.isA(XhbRotationSetDdDao.class)))
                .thenReturn(Optional.of(rsddComplex.getRotationSetDdDao()));
            mockPublicDisplayNotifier.sendMessage(Mockito.isA(ConfigurationChangeEvent.class));

            // Run Method
            classUnderTest.setDisplayDocumentsForRotationSet(rsComplex);
            result = true;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateDisplayConfiguration() {
        // Setup
        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        xhbDisplayDao.setDisplayId(DISPLAY_ID);
        xhbDisplayDao.setRotationSetId(ROTATION_SET_ID);
        xhbDisplayDao.setShowUnassignedYn(YES);
        List<XhbCourtRoomDao> roomList = new ArrayList<>();
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());
        xhbDisplayDao.setXhbCourtRooms(roomList);

        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
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

        XhbCourtRoomDao[] roomArray = {DummyCourtUtil.getXhbCourtRoomDao(), DummyCourtUtil.getXhbCourtRoomDao()};
        DisplayConfiguration config = new DisplayConfiguration(xhbDisplayDao, xhbRotationSetsDao, roomArray);
        // Set the values again to ensure the dirty flags are set and certain logic is
        // followed.
        config.setCourtRoomDaosWithCourtRoomChanged(roomArray);
        config.setRotationSetDao(xhbRotationSetsDao);
        // Expects
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));

        boolean result = false;
        try {
            classUnderTest.updateDisplayConfiguration(config);
            result = true;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testInitialiseCourt() {
        // Setup
        mockPublicDisplayNotifier.sendMessage(Mockito.isA(ConfigurationChangeEvent.class));

        // Run method
        boolean result = false;
        try {
            classUnderTest.initialiseCourt(COURT_ID);
            result = true;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetCourtRoomsForCourtMultiSite() {
        // Setup

        // Court Site 1 + Rooms
        XhbCourtSiteDao site1 = DummyCourtUtil.getXhbCourtSiteDao();
        site1.setCourtSiteId(17);
        site1.setShortName(COURTSITE1);
        site1.setCourtSiteCode("A");
        XhbCourtRoomDao redRoom = DummyCourtUtil.getXhbCourtRoomDao();
        redRoom.setCourtSiteId(site1.getCourtSiteId());
        redRoom.setXhbCourtSite(site1);
        redRoom.setCourtRoomId(100);
        redRoom.setCourtRoomName("Red Room");
        redRoom.setDisplayName(redRoom.getCourtRoomName());
        XhbCourtRoomDao pinkRoom = DummyCourtUtil.getXhbCourtRoomDao();
        pinkRoom.setCourtSiteId(site1.getCourtSiteId());
        pinkRoom.setXhbCourtSite(site1);
        pinkRoom.setCourtRoomId(101);
        pinkRoom.setCourtRoomName("Pink Room");
        pinkRoom.setDisplayName(pinkRoom.getCourtRoomName());
        List<XhbCourtRoomDao> site1roomList = new ArrayList<>();
        site1roomList.add(redRoom);
        site1roomList.add(pinkRoom);
        site1.setXhbCourtRooms(site1roomList);

        // Court Site 2 + Rooms
        XhbCourtSiteDao site2 = DummyCourtUtil.getXhbCourtSiteDao();
        site2.setCourtSiteId(25);
        site2.setShortName("Court Site 2");
        site2.setCourtSiteCode("B");
        XhbCourtRoomDao brownRoom = DummyCourtUtil.getXhbCourtRoomDao();
        brownRoom.setCourtSiteId(site2.getCourtSiteId());
        brownRoom.setXhbCourtSite(site2);
        brownRoom.setCourtRoomId(200);
        brownRoom.setCourtRoomName("Brown Room");
        brownRoom.setDisplayName(brownRoom.getCourtRoomName());
        List<XhbCourtRoomDao> site2roomList = new ArrayList<>();
        site2roomList.add(brownRoom);
        site2.setXhbCourtRooms(site2roomList);

        List<XhbCourtSiteDao> siteList = new ArrayList<>();
        siteList.add(site1);
        siteList.add(site2);

        Mockito.when(mockXhbCourtSiteRepository.findByCourtId(COURT_ID)).thenReturn(siteList);

        // Run Method
        XhbCourtRoomDao[] roomArray = classUnderTest.getCourtRoomsForCourt(COURT_ID);

        assertEquals(3, roomArray.length, EQUALS);

        String[] expectedRoomMultiNames = {site1.getShortName() + "-" + redRoom.getCourtRoomName(),
            site1.getShortName() + "-" + pinkRoom.getCourtRoomName(),
            site2.getShortName() + "-" + brownRoom.getCourtRoomName()};
        String[] expectedRoomNames =
            {redRoom.getCourtRoomName(), pinkRoom.getCourtRoomName(), brownRoom.getCourtRoomName()};
        for (int i = 0; i < roomArray.length; i++) {
            assertEquals(expectedRoomNames[i], roomArray[i].getCourtRoomName(), EQUALS);
            assertEquals(expectedRoomMultiNames[i], roomArray[i].getMultiSiteDisplayName(), EQUALS);
        }
    }

    @Test
    void testIsPublicDisplayActive() {
        // Setup
        XhbScheduledHearingDao scheduledHearing = DummyHearingUtil.getXhbScheduledHearingDao();
        scheduledHearing.setScheduledHearingId(SCHEDULED_HEARING_ID);
        try {
            XhbScheduledHearingRepository mockRepo = Mockito.mock(XhbScheduledHearingRepository.class);
            Mockito.when(mockRepo.findById(SCHEDULED_HEARING_ID)).thenReturn(Optional.of(scheduledHearing));

            // Run Method
            classUnderTest.isPublicDisplayActive(SCHEDULED_HEARING_ID);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testActivatePublicDisplay() {
        // Setup
        Date activationDate = new Date();
        List<Integer> schedHearingIdList = new ArrayList<>();
        schedHearingIdList.add(SCHEDULED_HEARING_ID);

        try {
            // Run Method
            classUnderTest.activatePublicDisplay(SCHEDULED_HEARING_ID, activationDate);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testDeActivatePublicDisplay() {
        // Setup
        Date deactivationDate = new Date();
        XhbScheduledHearingDao scheduledHearing = DummyHearingUtil.getXhbScheduledHearingDao();
        scheduledHearing.setScheduledHearingId(SCHEDULED_HEARING_ID);
        // Clear the CrLiveDisplays to take the code down a particular path
        scheduledHearing.getXhbSitting().getXhbCourtRoom().setXhbCrLiveDisplays(new ArrayList<>());
        List<Integer> schedHearingIdList = new ArrayList<>();
        schedHearingIdList.add(SCHEDULED_HEARING_ID);

        try {
            // Run Method
            classUnderTest.deActivatePublicDisplay(SCHEDULED_HEARING_ID, deactivationDate);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    void testDisplayLocationComplexValueEquals() {
        DisplayLocationComplexValue displayLocationComplexValue1 = DummyDisplayUtil.getDisplayLocationComplexValue();
        DisplayLocationComplexValue displayLocationComplexValue2 = DummyDisplayUtil.getDisplayLocationComplexValue();
        boolean isEqual;
        displayLocationComplexValue2.setDisplayLocationDao(null);
        isEqual = displayLocationComplexValue1.equals(displayLocationComplexValue2);
        assertFalse(isEqual, FALSE);
        displayLocationComplexValue2 = DummyDisplayUtil.getDisplayLocationComplexValue();
        displayLocationComplexValue1.setDisplayLocationDao(null);
        isEqual = displayLocationComplexValue1.equals(displayLocationComplexValue2);
        assertFalse(isEqual, FALSE);
        displayLocationComplexValue1 = DummyDisplayUtil.getDisplayLocationComplexValue();
        isEqual = displayLocationComplexValue1.equals(new XhbScheduledHearingDao());
        assertFalse(isEqual, FALSE);
        isEqual = displayLocationComplexValue1.equals(displayLocationComplexValue2);
        assertFalse(isEqual, FALSE);
        displayLocationComplexValue2 = displayLocationComplexValue1;
        isEqual = displayLocationComplexValue1.equals(displayLocationComplexValue2);
        assertTrue(isEqual, EQUALS);
    }
}
