package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
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
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
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
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.CourtSitePdComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayConfiguration;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.VipDisplayConfiguration;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.VipDisplayConfigurationCourtRoom;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.VipDisplayConfigurationDisplayDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;

/**
 * PdConfigurationControllerBeanGetTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.CouplingBetweenObjects",
    "PMD.TooManyMethods", "PMD.UseShortArrayInitializer"})
class PdConfigurationControllerBeanGetTest {

    private static final Logger LOG =
        LoggerFactory.getLogger(PdConfigurationControllerBeanGetTest.class);

    private static final String EQUALS = "Results are not Equal";
    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ROOM_ID = 30;
    private static final Integer COURT_SITE_ID = 40;
    private static final Integer COURT_ID = 80;
    private static final Integer DISPLAY_ID = 60;
    private static final Integer ROTATION_SET_ID = 70;
    private static final Integer DISPLAY_DOCUMENT_ID = 90;
    private static final String COURT_SITE_NAME = "SWANSEA";
    private static final String COURT_ROOM_NAME = "Court 4";
    private static final String DESC_CODE = "Test";
    private static final String YES = "Y";
    private static final String LANGUAGE_EN = "en";
    private static final String COUNTRY_GB = "GB";
    private static final String DAILYLIST = "DailyList";

    @Mock
    private EntityManager mockEntityManager;

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

    @Mock
    private DisplayRotationSetDataHelper mockDisplayRotationSetDataHelper;


    private PdConfigurationControllerBean classUnderTest;

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(DisplayLocationDataHelper.class);
        Mockito.mockStatic(RotationSetMaintainHelper.class);
        Mockito.mockStatic(DisplayConfigurationHelper.class);
        Mockito.mockStatic(PublicDisplayActivationHelper.class);

    }

    @BeforeEach
    void initTestBean() {
        classUnderTest = Mockito.spy(new PdConfigurationControllerBean(mockEntityManager,
            mockXhbCourtRepository, mockXhbRotationSetsRepository, mockXhbRotationSetDdRepository,
            mockXhbDisplayTypeRepository, mockXhbDisplayRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, Mockito.mock(PublicDisplayNotifier.class),
            mockVipDisplayDocumentQuery, mockVipDisplayCourtRoomQuery,
            mockDisplayRotationSetDataHelper));

        // Force repository method to return our mock (overriding internal creation)
        doReturn(mockXhbCourtSiteRepository).when(classUnderTest).getXhbCourtSiteRepository();
        doReturn(mockVipQuery).when(classUnderTest).getVipCourtRoomsQuery(Mockito.anyBoolean());
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testGetCourtsForPublicDisplay() {
        // Setup
        int[] expectedCourtArray = {1, 3, 5};
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        for (int courtId : expectedCourtArray) {
            dummyCourtList.add(DummyCourtUtil.getXhbCourtDao(courtId, "TestCourt" + courtId));
        }

        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
        Mockito.when(mockXhbCourtRepository.findAllSafe()).thenReturn(dummyCourtList);

        // Run method
        int[] courtArray = classUnderTest.getCourtsForPublicDisplay();

        // Check results
        assertArrayEquals(expectedCourtArray, courtArray, EQUALS);
    }

    @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
    @Test
    void testGetCourtConfiguration() {

        List<XhbRotationSetsDao> rotationSets = new ArrayList<>();
        XhbRotationSetsDao rotationSetDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        rotationSetDao.setCourtId(COURT_ID);
        rotationSetDao.setRotationSetId(ROTATION_SET_ID);
        rotationSets.add(rotationSetDao);

        XhbDisplayDao displayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        displayDao.setDisplayId(DISPLAY_ID);
        displayDao.setRotationSetId(ROTATION_SET_ID);
        List<XhbDisplayDao> xdList = List.of(displayDao);

        // Inject mocked helper logic
        DisplayUri displayUri = new DisplayUri("swansea", "sitecode", "location", "desc");
        RotationSetDisplayDocument[] rotationDocs =
            {new RotationSetDisplayDocument(new DisplayDocumentUri(new Locale("en", "GB"), COURT_ID,
                DisplayDocumentTypeUtils.getDisplayDocumentType("DailyList", "en", "GB"),
                new int[] {100}), 10L)};
        final DisplayRotationSetData[] mockDataArray =
            new DisplayRotationSetData[] {new DisplayRotationSetData(displayUri, rotationDocs,
                DISPLAY_ID, ROTATION_SET_ID, "DAILYLIST")};

        Optional<XhbCourtDao> court =
            Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, "Test Court"));

        // Mock repository responses
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
        Mockito.when(mockXhbCourtRepository.findByIdSafe(COURT_ID)).thenReturn(court);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtIdSafe(COURT_ID))
            .thenReturn(rotationSets);
        Mockito.when(mockXhbDisplayRepository.findByRotationSetIdSafe(ROTATION_SET_ID))
            .thenReturn(xdList);
        Mockito.when(mockXhbDisplayTypeRepository.findByIdSafe((Integer) Mockito.any()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayTypeDao()));
        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe((Integer) Mockito.any()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe((Integer) Mockito.any()))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));

        Mockito.when(mockDisplayRotationSetDataHelper.getDataForCourt(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(mockDataArray);

        // Run method
        DisplayRotationSetData[] result = classUnderTest.getCourtConfiguration(COURT_ID);

        // Assertions
        assertNotNull(result, NOTNULL);
        assertEquals(1, result.length, EQUALS);
        assertEquals(DISPLAY_ID, result[0].getDisplayId(), EQUALS);
        assertEquals(ROTATION_SET_ID, result[0].getRotationSetId(), EQUALS);
        assertEquals("DAILYLIST", result[0].getDisplayType(), EQUALS);
        assertNotNull(result[0].getDisplayUri(), NOTNULL);
        assertTrue(result[0].getRotationSetDisplayDocuments().length > 0, TRUE);
    }


    @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
    @Test
    void testGetUpdatedRotationSet() {
        // Setup supporting JPA entity mocks
        XhbDisplayDao displayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        displayDao.setDisplayId(DISPLAY_ID);
        displayDao.setRotationSetId(ROTATION_SET_ID);

        XhbRotationSetsDao rotationSetDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        rotationSetDao.setRotationSetId(ROTATION_SET_ID);
        rotationSetDao.setCourtId(COURT_ID);

        Optional<XhbRotationSetsDao> rotationSetOpt = Optional.of(rotationSetDao);
        Optional<XhbCourtDao> courtOpt =
            Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, "Test Court"));

        List<XhbDisplayDao> xdList = List.of(displayDao);

        // Mock the helper call
        DisplayUri displayUri = new DisplayUri("swansea", "sitecode", "location", "desc");
        RotationSetDisplayDocument[] rotationDocs =
            {new RotationSetDisplayDocument(new DisplayDocumentUri(Locale.UK, COURT_ID,
                DisplayDocumentTypeUtils.getDisplayDocumentType("DailyList", "en", "GB"),
                new int[] {100}), 10L)};
        final DisplayRotationSetData[] mockArray =
            new DisplayRotationSetData[] {new DisplayRotationSetData(displayUri, rotationDocs,
                DISPLAY_ID, ROTATION_SET_ID, "DAILYLIST")};

        // Mocks for repositories
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Long.valueOf(ROTATION_SET_ID)))
            .thenReturn(rotationSetOpt);
        Mockito.when(mockXhbCourtRepository.findByIdSafe(COURT_ID)).thenReturn(courtOpt);
        Mockito.when(mockXhbDisplayRepository.findByRotationSetId(Mockito.eq(ROTATION_SET_ID)))
            .thenReturn(xdList);

        Mockito
            .when(mockDisplayRotationSetDataHelper.getDataForDisplayRotationSets(
                Mockito.eq(courtOpt.get()), Mockito.eq(rotationSetDao), Mockito.eq(xdList),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any()))
            .thenReturn(mockArray);

        // Execute
        DisplayRotationSetData[] result =
            classUnderTest.getUpdatedRotationSet(COURT_ID, ROTATION_SET_ID);

        // Verify
        assertNotNull(result, "Result is Null");
        assertEquals(1, result.length, "Unexpected array size");
        assertEquals(DISPLAY_ID, result[0].getDisplayId(), "Display ID mismatch");
        assertEquals(ROTATION_SET_ID, result[0].getRotationSetId(), "Rotation Set ID mismatch");
    }

    @Test
    void testGetVipCourtRoomsForCourt() {
        // Setup
        XhbCourtRoomDao[] site1RoomArray =
            {DummyCourtUtil.getXhbCourtRoomDao(), DummyCourtUtil.getXhbCourtRoomDao()};
        List<XhbCourtSiteDao> siteList = new ArrayList<>();
        siteList.add(DummyCourtUtil.getXhbCourtSiteDao());

        List<XhbCourtRoomDao> xhbCourtRoomDaoList = Arrays.asList(site1RoomArray);

        try {
            Mockito.when(mockXhbCourtSiteRepository.findByCourtIdSafe(COURT_ID))
                .thenReturn(siteList);

            Mockito.when(mockXhbCourtRoomRepository.findVipMultiSite(COURT_ID))
                .thenReturn(xhbCourtRoomDaoList);

            Mockito.when(mockXhbCourtRoomRepository.findVipMNoSite(COURT_ID))
                .thenReturn(xhbCourtRoomDaoList);

            Mockito.when(mockVipQuery.getData(COURT_ID)).thenReturn(site1RoomArray);

            // Run Method
            XhbCourtRoomDao[] roomArray = classUnderTest.getVipCourtRoomsForCourt(COURT_ID);

            // Checks
            assertEquals(2, roomArray.length, EQUALS);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetVipDisplayConfiguration() {
        // Setup
        List<VipDisplayConfigurationDisplayDocument> vipDisplayDocList = new ArrayList<>();
        vipDisplayDocList.add(
            new VipDisplayConfigurationDisplayDocument(DESC_CODE, false, LANGUAGE_EN, COUNTRY_GB));
        List<VipDisplayConfigurationCourtRoom> vipCourtRoomList = new ArrayList<>();
        vipCourtRoomList.add(
            new VipDisplayConfigurationCourtRoom(COURT_ROOM_ID, COURT_SITE_NAME, COURT_ROOM_NAME));

        try {
            Mockito.when(mockVipDisplayDocumentQuery.getData(COURT_SITE_ID))
                .thenReturn(vipDisplayDocList);
            Mockito.when(mockVipDisplayCourtRoomQuery.getData(COURT_SITE_ID))
                .thenReturn(vipCourtRoomList);
            Mockito.when(mockVipDisplayCourtRoomQuery.isShowUnassignedCases()).thenReturn(true);

            // Run Method
            VipDisplayConfiguration result =
                classUnderTest.getVipDisplayConfiguration(COURT_SITE_ID);

            // Checks
            assertEquals(true, result.isUnassignedCases(), EQUALS);
            assertEquals(1, result.getVipDisplayConfigurationDisplayDocuments().length, EQUALS);
            assertEquals(1, result.getVipDisplayConfigurationCourtRooms().length, EQUALS);
            assertEquals(COURT_ROOM_ID,
                result.getVipDisplayConfigurationCourtRooms()[0].getCourtRoomId(), EQUALS);
            assertEquals(COURT_SITE_NAME,
                result.getVipDisplayConfigurationCourtRooms()[0].getCourtSiteShortName(), EQUALS);
            assertEquals(COURT_ROOM_NAME,
                result.getVipDisplayConfigurationCourtRooms()[0].getCourtRoomDisplayName(), EQUALS);
            assertEquals(DESC_CODE,
                result.getVipDisplayConfigurationDisplayDocuments()[0].getDescriptionCode(),
                EQUALS);
            assertEquals(false,
                result.getVipDisplayConfigurationDisplayDocuments()[0].isMultipleCourt(), EQUALS);
            assertEquals(LANGUAGE_EN,
                result.getVipDisplayConfigurationDisplayDocuments()[0].getLanguage(), EQUALS);
            assertEquals(COUNTRY_GB,
                result.getVipDisplayConfigurationDisplayDocuments()[0].getCountry(), EQUALS);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetCourtRoomsForCourt() {
        // Setup
        Integer courtSite1Id = 17;

        // Create court rooms
        XhbCourtRoomDao redRoom = new XhbCourtRoomDao();
        redRoom.setCourtRoomId(100);
        redRoom.setCourtRoomName("Red Room");
        redRoom.setDisplayName("Red Room");
        redRoom.setCourtSiteId(courtSite1Id);

        XhbCourtRoomDao pinkRoom = new XhbCourtRoomDao();
        pinkRoom.setCourtRoomId(101);
        pinkRoom.setCourtRoomName("Pink Room");
        pinkRoom.setDisplayName("Pink Room");
        pinkRoom.setCourtSiteId(courtSite1Id);

        List<XhbCourtRoomDao> roomList = new ArrayList<>();
        roomList.add(redRoom);
        roomList.add(pinkRoom);

        // Create and populate court site
        XhbCourtSiteDao site1 = new XhbCourtSiteDao();
        site1.setCourtSiteId(courtSite1Id);
        site1.setShortName("SITE1");
        site1.setXhbCourtRooms(roomList);

        List<XhbCourtSiteDao> siteList = new ArrayList<>();
        siteList.add(site1);

        // Print debug info before mocking
        LOG.debug("Prepared site room list: " + site1.getXhbCourtRooms());

        // Mock repository call
        Mockito.when(mockXhbCourtSiteRepository.findByCourtIdSafe(COURT_ID)).thenReturn(siteList);

        // Call method under test
        XhbCourtRoomDao[] result = classUnderTest.getCourtRoomsForCourt(COURT_ID);

        // Debug output of result
        LOG.debug("Returned room array length: " + result.length);
        for (int i = 0; i < result.length; i++) {
            LOG.debug("Room " + i + ": ID=" + result[i].getCourtRoomId() + ", Name="
                + result[i].getCourtRoomName() + ", DisplayName=" + result[i].getDisplayName()
                + ", MultiSiteDisplayName=" + result[i].getMultiSiteDisplayName());
        }

        // Assert
        assertEquals(2, result.length, "Results are not Equal");

        String[] expectedRoomNames = {"Red Room", "Pink Room"};
        for (int i = 0; i < result.length; i++) {
            assertEquals(expectedRoomNames[i], result[i].getDisplayName(), "Display name mismatch");
            assertNull(result[i].getMultiSiteDisplayName(), "Expected null MultiSiteDisplayName");
        }
    }

    @Test
    void testGetDisplayConfiguration() {
        // Setup
        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        xhbDisplayDao.setDisplayId(DISPLAY_ID);
        xhbDisplayDao.setRotationSetId(ROTATION_SET_ID);
        xhbDisplayDao.setShowUnassignedYn(YES);
        List<XhbCourtRoomDao> roomList = new ArrayList<>();
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());

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

        Optional<XhbRotationSetsDao> xrs = Optional.of(xhbRotationSetsDao);
        XhbCourtRoomDao[] roomArray =
            {DummyCourtUtil.getXhbCourtRoomDao(), DummyCourtUtil.getXhbCourtRoomDao()};
        Optional<XhbDisplayDao> displayDao = Optional.of(xhbDisplayDao);
        DisplayConfiguration displayConfiguration =
            new DisplayConfiguration(displayDao.get(), xrs.get(), roomArray);
        displayConfiguration
            .setCourtRoomDaosWithCourtRoomChanged(displayConfiguration.getCourtRoomDaos());
        displayConfiguration.setRotationSetDao(displayConfiguration.getRotationSetDao());
        // Expects
        Mockito
            .when(
                mockXhbRotationSetDdRepository.findByRotationSetIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));

        try {
            Mockito.when(
                DisplayConfigurationHelper.getDisplayConfiguration(DISPLAY_ID, mockEntityManager))
                .thenReturn(displayConfiguration);

            // Run Method
            DisplayConfiguration result = classUnderTest.getDisplayConfiguration(DISPLAY_ID);

            // Check results
            assertNotNull(result, "Result is Null");
            assertSame(displayDao.get(), result.getDisplayDao(), "Result is not Same");
            assertTrue(result.isCourtRoomsChanged(), "Result is not True");
            assertTrue(result.isRotationSetChanged(), "Result is not True");

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetDisplaysForCourt() {
        // Setup
        List<XhbCourtSiteDao> siteList = new ArrayList<>();
        siteList.add(DummyCourtUtil.getXhbCourtSiteDao());
        siteList.add(DummyCourtUtil.getXhbCourtSiteDao());

        List<CourtSitePdComplexValue> courtSitePdComplexValueList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            CourtSitePdComplexValue complexValue = DummyCourtUtil.getCourtSitePdComplexValue();
            complexValue
                .addDisplayLocationComplexValue(DummyDisplayUtil.getDisplayLocationComplexValue());
            assertSame(1, complexValue.getDisplayLocationComplexValue().length,
                "Result is not Same");
            complexValue.setCourtSiteDao(DummyCourtUtil.getXhbCourtSiteDao());
            complexValue.getCourtSiteDao().setCourtId(COURT_ID);
            courtSitePdComplexValueList.add(complexValue);
        }

        CourtSitePdComplexValue[] courtSitePdComplexValueArray =
            new CourtSitePdComplexValue[courtSitePdComplexValueList.size()];

        CourtSitePdComplexValue[] courtSitePdComplexValue =
            courtSitePdComplexValueList.toArray(courtSitePdComplexValueArray);

        Mockito.when(DisplayLocationDataHelper.getDisplaysForCourt(COURT_ID, mockEntityManager))
            .thenReturn(courtSitePdComplexValue);

        Mockito.when(mockXhbCourtSiteRepository.findByCourtIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(siteList);


        // Run Method
        CourtSitePdComplexValue[] result = classUnderTest.getDisplaysForCourt(COURT_ID);

        // Check results
        assertEquals(2, result.length, EQUALS);
        assertEquals(COURT_ID, result[0].getCourtSiteDao().getCourtId(), EQUALS);
        assertEquals(COURT_ID, result[1].getCourtSiteDao().getCourtId(), EQUALS);
    }
}
