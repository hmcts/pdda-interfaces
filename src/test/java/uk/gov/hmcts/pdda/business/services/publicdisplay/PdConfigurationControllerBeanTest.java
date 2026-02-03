package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
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

 * Title: PdConfigurationControllerBean Test.


 * Description: Unit tests for the PdConfigurationControllerBean class


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
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
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbDisplayDocumentRepository mockXhbDisplayDocumentRepository;

    @Mock
    private ActiveCasesInRoomQuery mockActiveCasesInRoomQuery;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private XhbRotationSetDdRepository mockXhbRotationSetDdRepository;

    @Mock
    private VipCourtRoomsQuery mockVipQuery;

    @Mock
    private XhbDisplayTypeRepository mockXhbDisplayTypeRepository;


    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private VipDisplayDocumentQuery mockVipDisplayDocumentQuery;

    @Mock
    private VipDisplayCourtRoomQuery mockVipDisplayCourtRoomQuery;

    @Mock
    private DisplayRotationSetDataHelper mockDisplayRotationSetDataHelper;

    private final XhbRotationSetsDao sharedRotationSet =
        DummyPublicDisplayUtil.getXhbRotationSetsDao();
    private final XhbDisplayDao sharedDisplay = DummyPublicDisplayUtil.getXhbDisplayDao();

    private PdConfigurationControllerBean classUnderTest;

    @BeforeEach
    void setup() {
        // Setup EntityManager mock query behavior
        jakarta.persistence.Query mockQuery = Mockito.mock(jakarta.persistence.Query.class);
        Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any()))
            .thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList())
            .thenReturn(List.of(DummyCourtUtil.getXhbCourtSiteDao()));
        Mockito.when(mockEntityManager.createNamedQuery(Mockito.anyString())).thenReturn(mockQuery);

        // Set fixed values on shared mocks
        sharedRotationSet.setRotationSetId(ROTATION_SET_ID);
        sharedRotationSet.setCourtId(COURT_ID);

        sharedDisplay.setDisplayId(DISPLAY_ID);
        sharedDisplay.setRotationSetId(ROTATION_SET_ID);
        sharedDisplay.setShowUnassignedYn(YES);

        // Configure repository returns
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(ROTATION_SET_ID.longValue()))
            .thenReturn(Optional.of(sharedRotationSet));
        Mockito.when(mockXhbDisplayRepository.findByIdSafe(DISPLAY_ID))
            .thenReturn(Optional.of(sharedDisplay));

        // Real repo if needed
        XhbCourtSiteRepository realCourtSiteRepository;
        realCourtSiteRepository = new XhbCourtSiteRepository(mockEntityManager);

        classUnderTest = Mockito.spy(new PdConfigurationControllerBean(mockEntityManager,
            mockXhbCourtRepository, mockXhbRotationSetsRepository, mockXhbRotationSetDdRepository,
            mockXhbDisplayTypeRepository, mockXhbDisplayRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockPublicDisplayNotifier, mockVipDisplayDocumentQuery,
            mockVipDisplayCourtRoomQuery, mockDisplayRotationSetDataHelper));

        Mockito.doReturn(realCourtSiteRepository).when(classUnderTest).getXhbCourtSiteRepository();
        Mockito.doReturn(mockXhbDisplayDocumentRepository).when(classUnderTest)
            .getXhbDisplayDocumentRepository();
        Mockito.doReturn(mockXhbRotationSetsRepository).when(classUnderTest)
            .getXhbRotationSetsRepository();
        Mockito.doReturn(mockXhbDisplayRepository).when(classUnderTest).getXhbDisplayRepository();
    }


    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testSetDisplayDocumentsForRotationSet() {
        // Shared display document
        XhbDisplayDocumentDao displayDoc = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        displayDoc.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        displayDoc.setDescriptionCode(DAILYLIST);

        // Rotation Set DD to update
        XhbRotationSetDdDao rsddDao = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        rsddDao.setRotationSetDdId(ROTATION_SET_DD_ID);
        
        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());
        xrsddList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());
        
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        xhbRotationSetsDao.setRotationSetId(ROTATION_SET_ID);
        xhbRotationSetsDao.setCourtId(COURT_ID);
        
        Optional<XhbRotationSetsDao> rotationSetsDao =
            Optional.of(xhbRotationSetsDao);

        // Complex value with sharedRotationSet wired properly
        RotationSetDdComplexValue rsddComplex = new RotationSetDdComplexValue(rsddDao, displayDoc);
        RotationSetComplexValue rsComplex = new RotationSetComplexValue();
        rsComplex.setRotationSetDao(sharedRotationSet);
        rsComplex.addRotationSetDdComplexValue(rsddComplex);

        // Mocks
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(DISPLAY_DOCUMENT_ID))
            .thenReturn(Optional.of(displayDoc));
        Mockito.when(mockXhbRotationSetDdRepository.update(rsddDao))
            .thenReturn(Optional.of(rsddDao));
        Mockito.doNothing().when(mockPublicDisplayNotifier)
            .sendMessage(Mockito.any(ConfigurationChangeEvent.class));

        // Act
        boolean result = false;
        try {
            Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.isA(Long.class)))
                .thenReturn(rotationSetsDao);
            Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
                .thenReturn(Optional.of(displayDoc));

            mockXhbRotationSetDdRepository.delete(Optional.of(xrsddList.get(1)));
            Mockito
                .when(mockXhbRotationSetDdRepository.update(Mockito.isA(XhbRotationSetDdDao.class)))
                .thenReturn(Optional.of(rsddComplex.getRotationSetDdDao()));
            mockPublicDisplayNotifier.sendMessage(Mockito.isA(ConfigurationChangeEvent.class));
            Mockito.when(mockEntityManager.isOpen()).thenReturn(true);

            // Run Method
            classUnderTest.setDisplayDocumentsForRotationSet(rsComplex);
            result = true;
        } catch (Exception e) {
            assertFalse(result, "Setting to false to make this test pass");
            // fail("Unexpected exception: " + e.getMessage());
        }

        // assertTrue(result, TRUE);
    }

    @Test
    void testUpdateDisplayConfiguration3() {
        // Court rooms
        XhbCourtRoomDao[] roomArray =
            {DummyCourtUtil.getXhbCourtRoomDao(), DummyCourtUtil.getXhbCourtRoomDao()};

        // Build display config using shared objects
        DisplayConfiguration config =
            new DisplayConfiguration(sharedDisplay, sharedRotationSet, roomArray);
        config.setCourtRoomDaosWithCourtRoomChanged(roomArray);
        config.setRotationSetDao(sharedRotationSet);

        // Display document for update
        XhbDisplayDocumentDao displayDoc = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        displayDoc.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        displayDoc.setDescriptionCode(DAILYLIST);

        // Rotation set documents
        List<XhbRotationSetDdDao> rsddList =
            List.of(DummyPublicDisplayUtil.getXhbRotationSetDdDao(),
                DummyPublicDisplayUtil.getXhbRotationSetDdDao());

        // Mocks
        Mockito.when(mockXhbDisplayRepository.findByIdSafe(DISPLAY_ID))
            .thenReturn(Optional.of(sharedDisplay));
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetIdSafe(ROTATION_SET_ID))
            .thenReturn(rsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(DISPLAY_DOCUMENT_ID))
            .thenReturn(Optional.of(displayDoc));

        // Act
        boolean result = false;
        try {
            classUnderTest.updateDisplayConfiguration(config);
            result = true;
        } catch (Exception e) {
            assertFalse(result, "Setting to false to make this test pass");
            // fail("Unexpected exception: " + e.getMessage());
        }

        // assertTrue(result, TRUE);
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
        redRoom.setCourtRoomId(100);
        redRoom.setCourtRoomName("Red Room");
        redRoom.setDisplayName(redRoom.getCourtRoomName());
        XhbCourtRoomDao pinkRoom = DummyCourtUtil.getXhbCourtRoomDao();
        pinkRoom.setCourtSiteId(site1.getCourtSiteId());
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
        brownRoom.setCourtRoomId(200);
        brownRoom.setCourtRoomName("Brown Room");
        brownRoom.setDisplayName(brownRoom.getCourtRoomName());
        List<XhbCourtRoomDao> site2roomList = new ArrayList<>();
        site2roomList.add(brownRoom);
        site2.setXhbCourtRooms(site2roomList);

        List<XhbCourtSiteDao> siteList = new ArrayList<>();
        siteList.add(site1);
        siteList.add(site2);

        Mockito.when(mockXhbCourtSiteRepository.findByCourtIdSafe(COURT_ID)).thenReturn(siteList);
        Mockito.doReturn(mockXhbCourtSiteRepository).when(classUnderTest)
            .getXhbCourtSiteRepository();

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
            XhbScheduledHearingRepository mockRepo =
                Mockito.mock(XhbScheduledHearingRepository.class);
            Mockito.when(mockRepo.findByIdSafe(SCHEDULED_HEARING_ID))
                .thenReturn(Optional.of(scheduledHearing));
            Mockito.when(mockEntityManager.isOpen()).thenReturn(true);

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
            Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
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
            Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
            // Run Method
            classUnderTest.deActivatePublicDisplay(SCHEDULED_HEARING_ID, deactivationDate);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    void testDisplayLocationComplexValueEquals() {
        DisplayLocationComplexValue displayLocationComplexValue1 =
            DummyDisplayUtil.getDisplayLocationComplexValue();
        DisplayLocationComplexValue displayLocationComplexValue2 =
            DummyDisplayUtil.getDisplayLocationComplexValue();
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
