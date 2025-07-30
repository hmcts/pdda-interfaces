package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.RotationSetNotFoundCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayBasicValueSortAdapter;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetDdComplexValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * PdConfigurationControllerBeanRotationTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class PdConfigurationControllerBeanRotationTest {
    
    private static final Logger LOG =
        LoggerFactory.getLogger(PdConfigurationControllerBeanRotationTest.class);

    private static final String EQUALS = "Results are not Equal";
    private static final String NOTEQUALS = "Results are Equal";
    private static final String NOTNULL = "Result is Null";
    private static final String NULL = "Result is not Null";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 80;
    private static final Integer ROTATION_SET_ID = 70;
    private static final Integer DISPLAY_DOCUMENT_ID = 90;
    private static final Integer ROTATION_SET_DD_ID = 1;
    private static final String DAILYLIST = "DailyList";

    @Mock
    private EntityManager mockEntityManager;

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

    private PdConfigurationControllerBean classUnderTest;

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(DisplayLocationDataHelper.class);
        Mockito.mockStatic(RotationSetMaintainHelper.class);
        Mockito.mockStatic(DisplayConfigurationHelper.class);
        Mockito.mockStatic(PublicDisplayActivationHelper.class);
    }

    @BeforeEach
    void setup() {
        jakarta.persistence.Query mockRotationSetDdQuery =
            Mockito.mock(jakarta.persistence.Query.class);

        // RotationSetsDao returned for ROTATION_SETS query
        XhbRotationSetsDao rotationSet = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        rotationSet.setRotationSetId(ROTATION_SET_ID);
        rotationSet.setCourtId(COURT_ID);

        // RotationSetDdDao returned for ROTATION_SET_DD query
        XhbRotationSetDdDao rsdd1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao(54, 90);
        XhbRotationSetDdDao rsdd2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao(76, 91);
        Mockito.when(mockRotationSetDdQuery.getResultList()).thenReturn(List.of(rsdd1, rsdd2));

        // Stub findById directly if called via EntityManager.find
        Mockito
            .when(mockEntityManager.find(XhbRotationSetsDao.class, Long.valueOf(ROTATION_SET_ID)))
            .thenReturn(rotationSet);

        // Stub named query: ROTATION_SETS.findById
        jakarta.persistence.Query mockRotationSetQuery =
            Mockito.mock(jakarta.persistence.Query.class);
        Mockito.when(mockEntityManager.createNamedQuery("XHB_ROTATION_SETS.findById"))
            .thenReturn(mockRotationSetQuery);
        Mockito.when(mockRotationSetQuery.setParameter("rotationSetId", ROTATION_SET_ID))
            .thenReturn(mockRotationSetQuery);
        Mockito.when(mockRotationSetQuery.getResultList()).thenReturn(List.of(rotationSet)); 
        // Stub named query: ROTATION_SET_DD.findByRotationSetId
        Mockito.when(mockEntityManager.createNamedQuery("XHB_ROTATION_SET_DD.findByRotationSetId"))
            .thenReturn(mockRotationSetDdQuery);
        Mockito.when(mockRotationSetDdQuery.setParameter("rotationSetId", ROTATION_SET_ID))
            .thenReturn(mockRotationSetDdQuery);

        // Reconstruct class under test
        classUnderTest = new TestablePdConfigurationControllerBean(
            mockEntityManager,
            mockXhbCourtRepository,
            mockXhbRotationSetsRepository,
            mockXhbRotationSetDdRepository,
            Mockito.mock(XhbDisplayTypeRepository.class),
            Mockito.mock(XhbDisplayRepository.class),
            Mockito.mock(XhbDisplayLocationRepository.class),
            Mockito.mock(XhbCourtSiteRepository.class),
            Mockito.mock(XhbCourtRoomRepository.class),
            Mockito.mock(PublicDisplayNotifier.class),
            Mockito.mock(VipDisplayDocumentQuery.class),
            Mockito.mock(VipDisplayCourtRoomQuery.class),
            Mockito.mock(DisplayRotationSetDataHelper.class), mockXhbDisplayDocumentRepository
        );

        // Also mock repository lookup
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Long.valueOf(ROTATION_SET_ID)))
            .thenReturn(Optional.of(rotationSet));
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testDisplayRotationSetDataEquals() {
        DisplayRotationSetData object1 =
            new DisplayRotationSetData(new DisplayUri("shortname", "siteCode", "location", "desc"),
                new RotationSetDisplayDocument[] {}, 0, 0, "displayType1");
        DisplayRotationSetData object2 = new DisplayRotationSetData(
            new DisplayUri("shortname2", "siteCode2", "location2", "desc"),
            new RotationSetDisplayDocument[] {}, 1, 2, "displayType2");
        assertFalse(object1.equals(object2), FALSE);
    }

    @Test
    void testGetRotationSetsDetailForCourt() {
        // Setup
        RotationSetComplexValue[] rotationSetComplexValue = {};


        Mockito.when(DisplayLocationDataHelper.getRotationSetsDetailForCourt(COURT_ID, Locale.UK,
            mockEntityManager)).thenReturn(rotationSetComplexValue);
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);

        // Run Method
        boolean result = false;
        try {
            classUnderTest.getRotationSetsDetailForCourt(COURT_ID, Locale.UK);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetRotationSetsForCourt() {
        // Setup dummyList
        List<XhbRotationSetsDao> dummyList = List.of(DummyPublicDisplayUtil.getXhbRotationSetsDao(),
            DummyPublicDisplayUtil.getXhbRotationSetsDao());

        // Mock query and result
        jakarta.persistence.Query queryMock = Mockito.mock(jakarta.persistence.Query.class);
        Mockito.when(mockEntityManager.createQuery(Mockito.anyString())).thenReturn(queryMock);
        Mockito.when(queryMock.getResultList()).thenReturn(dummyList);
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(dummyList);

        // Also stub any follow-up logic, if needed
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.anyInt()))
            .thenReturn(List.of(DummyPublicDisplayUtil.getXhbRotationSetDdDao(),
                DummyPublicDisplayUtil.getXhbRotationSetDdDao()));
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayDocumentDao()));

        // Run
        XhbRotationSetsDao[] result = classUnderTest.getRotationSetsForCourt(COURT_ID);

        // Assert
        assertNotNull(result, NOTNULL);
        assertEquals(2, result.length, EQUALS);
    }


    @Test
    void testRotationSetDdComplexValueEquals() {
        XhbDisplayDocumentDao displayDocumentDao1 =
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        displayDocumentDao1.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        displayDocumentDao1.setDescriptionCode(DAILYLIST);
        XhbDisplayDocumentDao displayDocumentDao2 =
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        displayDocumentDao2.setDisplayDocumentId(DISPLAY_DOCUMENT_ID + 1);
        displayDocumentDao2.setDescriptionCode(DAILYLIST);
        RotationSetDdComplexValue rsddComplex1;
        RotationSetDdComplexValue rsddComplex2;
        boolean isEqual;

        // Test for rotationSetDdDao == null
        rsddComplex1 = new RotationSetDdComplexValue(null, displayDocumentDao1);
        rsddComplex2 = new RotationSetDdComplexValue(null, displayDocumentDao2);
        isEqual = rsddComplex1.equals(rsddComplex2);
        assertFalse(isEqual, FALSE);

        // Test for complexValue not instanceof RotationSetDdComplexValue
        XhbRotationSetDdDao xrsd1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd1.setRotationSetDdId(null);
        rsddComplex1 = new RotationSetDdComplexValue(xrsd1, displayDocumentDao1);
        isEqual = rsddComplex1.equals(new RotationSetComplexValue());
        assertFalse(isEqual, FALSE);

        // Test for rsddComplex2.rotationSetDdDao == null
        XhbRotationSetDdDao xrsd2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd2.setRotationSetDdId(null);
        rsddComplex1 = new RotationSetDdComplexValue(xrsd2, displayDocumentDao1);
        rsddComplex2 = new RotationSetDdComplexValue(null, displayDocumentDao2);
        isEqual = rsddComplex1.equals(rsddComplex2);
        assertFalse(isEqual, FALSE);

        // Test for rsddComplex2.rotationSetDdDao.rotationSetDdId == null
        XhbRotationSetDdDao xrsd3a = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd3a.setRotationSetDdId(null);
        XhbRotationSetDdDao xrsd3b = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd3b.setRotationSetDdId(null);
        rsddComplex1 = new RotationSetDdComplexValue(xrsd3a, displayDocumentDao1);
        rsddComplex2 = new RotationSetDdComplexValue(xrsd3b, displayDocumentDao2);
        isEqual = rsddComplex1.equals(rsddComplex2);
        assertFalse(isEqual, FALSE);

        // Test for rsddComplex1.rotationSetDdDao.rotationSetDdId == null
        XhbRotationSetDdDao xrsd4a = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd4a.setRotationSetDdId(null);
        XhbRotationSetDdDao xrsd4b = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd4b.setRotationSetDdId(ROTATION_SET_DD_ID + 1);
        rsddComplex1 = new RotationSetDdComplexValue(xrsd4a, displayDocumentDao1);
        rsddComplex2 = new RotationSetDdComplexValue(xrsd4b, displayDocumentDao2);
        isEqual = rsddComplex1.equals(rsddComplex2);
        assertFalse(isEqual, FALSE);

        // Test for rsddComplex1.rotationSetDdDao = rsddComplex2.rotationSetDdDao
        XhbRotationSetDdDao xrsd5a = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd5a.setRotationSetDdId(ROTATION_SET_DD_ID);
        XhbRotationSetDdDao xrsd5b = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd5b.setRotationSetDdId(ROTATION_SET_DD_ID + 1);
        rsddComplex1 = new RotationSetDdComplexValue(xrsd5a, displayDocumentDao1);
        rsddComplex2 = new RotationSetDdComplexValue(xrsd5b, displayDocumentDao2);
        isEqual = rsddComplex1.equals(rsddComplex2);
        assertFalse(isEqual, FALSE);

        // Test for equal
        XhbRotationSetDdDao xrsd6 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd6.setRotationSetDdId(ROTATION_SET_DD_ID);
        rsddComplex1 = new RotationSetDdComplexValue(xrsd6, displayDocumentDao1);
        rsddComplex2 = new RotationSetDdComplexValue(xrsd6, displayDocumentDao1);
        isEqual = rsddComplex1.equals(rsddComplex2);
        assertTrue(isEqual, TRUE);
    }

    @Test
    void testRotationSetNotFoundCheckedException() {
        Assertions.assertThrows(RotationSetNotFoundCheckedException.class, () -> {
            throw new RotationSetNotFoundCheckedException(-1);
        });
    }

    @Test
    void testGetRotationSet() {
        // Setup IDs
        Integer rotationSetDdId1 = 54;
        Integer rotationSetDdId2 = 76;
        Integer displayDocumentId1 = 90;
        Integer displayDocumentId2 = 91;

        // Create distinct RS-DD DAOs
        XhbRotationSetDdDao rsdd1 =
            DummyPublicDisplayUtil.getXhbRotationSetDdDao(rotationSetDdId1, displayDocumentId1);
        XhbRotationSetDdDao rsdd2 =
            DummyPublicDisplayUtil.getXhbRotationSetDdDao(rotationSetDdId2, displayDocumentId2);

        // Mock repository responses
        XhbRotationSetsDao rotationSet = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        rotationSet.setRotationSetId(ROTATION_SET_ID);
        rotationSet.setCourtId(COURT_ID);
        Optional<XhbRotationSetsDao> rotationSetOpt = Optional.of(rotationSet);

        List<XhbRotationSetDdDao> rsddList = List.of(rsdd1, rsdd2);
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Long.valueOf(ROTATION_SET_ID)))
            .thenReturn(rotationSetOpt);
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(ROTATION_SET_ID))
            .thenReturn(rsddList);

        // Create display documents with correct IDs
        XhbDisplayDocumentDao displayDoc1 =
            DummyPublicDisplayUtil.createDisplayDocument(displayDocumentId1, "DailyList");
        XhbDisplayDocumentDao displayDoc2 =
            DummyPublicDisplayUtil.createDisplayDocument(displayDocumentId2, "DailyList");
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.anyInt()))
            .thenAnswer(invocation -> {
                Integer id = invocation.getArgument(0);
                LOG.debug("Mock called with: " + id);
                if (id.equals(90)) {
                    return Optional.of(displayDoc1);
                }
                if (id.equals(91)) {
                    return Optional.of(displayDoc2);
                }
                return Optional.empty();
            });

        // Run method and assert
        try {
            LOG.debug("RS-DD 1: " + rsdd1.getRotationSetDdId() + " -> Doc ID = "
                + rsdd1.getDisplayDocumentId());
            LOG.debug("RS-DD 2: " + rsdd2.getRotationSetDdId() + " -> Doc ID = "
                + rsdd2.getDisplayDocumentId());

            RotationSetComplexValue result = classUnderTest.getRotationSet(ROTATION_SET_ID);

            assertEquals(COURT_ID, result.getCourtId(), EQUALS);
            assertEquals(ROTATION_SET_ID, result.getRotationSetId(), EQUALS);
            assertEquals(2, result.getRotationSetDdComplexValues().length, EQUALS);
            assertTrue(result.hasRotationSetDd(rotationSetDdId1), TRUE);
            assertTrue(result.hasRotationSetDd(rotationSetDdId2), TRUE);
            assertFalse(result.hasRotationSetDd(-1), FALSE);
        } catch (RotationSetNotFoundCheckedException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testCreateRotationSets() {
        // Setup
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

        RotationSetComplexValue rsComplex = new RotationSetComplexValue();
        XhbRotationSetDdDao rotationSetDdDao = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        XhbDisplayDocumentDao displayDocumentDao =
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        RotationSetDdComplexValue rsddComplex =
            new RotationSetDdComplexValue(rotationSetDdDao, displayDocumentDao);
        rsddComplex.setDisplayDocumentDao(displayDocumentDao);
        rsComplex.setRotationSetDao(xhbRotationSetsDao);
        rsComplex.addRotationSetDdComplexValue(rsddComplex);
        DisplayBasicValueSortAdapter displayBasicValueSortAdapter =
            DummyDisplayUtil.getDisplayBasicValueSortAdapter();
        DisplayBasicValueSortAdapter[] displayBasicValueSortAdapters =
            {displayBasicValueSortAdapter};
        rsComplex.setDisplayDaos(displayBasicValueSortAdapters);
        rsComplex.setRotationSetDao(rsComplex.getRotationSetsDao());

        Optional<XhbCourtDao> courtDao =
            Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, "Test Court"));

        Mockito.when(mockXhbCourtRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(courtDao);

        Mockito.when(mockXhbRotationSetsRepository.update(Mockito.isA(XhbRotationSetsDao.class)))
            .thenReturn(Optional.of(xhbRotationSetsDao));
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);

        // Run Method
        classUnderTest.createRotationSets(rsComplex);

        XhbDisplayDao xhbDisplayDao = displayBasicValueSortAdapter.getDao();
        assertNotEquals(displayBasicValueSortAdapter,
            new DisplayBasicValueSortAdapter(xhbDisplayDao, "Test2"), NOTEQUALS);
        assertNotNull(displayBasicValueSortAdapter.toString(), NOTNULL);
        assertEquals(rsddComplex.getDisplayDocumentBasicValue(), displayDocumentDao, EQUALS);
        assertEquals(rsddComplex.getDisplayDocumentId(), displayDocumentDao.getDisplayDocumentId(),
            EQUALS);
        assertEquals(rotationSetDdDao,
            rsComplex.getRotationSetDd(rotationSetDdDao.getRotationSetDdId()), EQUALS);
        assertNull(rsComplex.getRotationSetDd(2), NULL);
        assertSame(displayBasicValueSortAdapters[0].getDao(), xhbDisplayDao, "Result is not Same");
        DisplayBasicValueSortAdapter sort =
            new DisplayBasicValueSortAdapter(xhbDisplayDao, "Test2");
        assertEquals(displayBasicValueSortAdapter.compareTo(sort) * -1,
            sort.compareTo(displayBasicValueSortAdapter), NOTEQUALS);
        assertNotEquals(displayBasicValueSortAdapter.compareTo(sort) * -1,
            displayBasicValueSortAdapters[0].hashCode(), NOTEQUALS);
    }

    @Test
    void testDeleteRotationSets() {
        // Setup
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

        Optional<XhbRotationSetsDao> rotationSetsDao = Optional.of(xhbRotationSetsDao);
        RotationSetComplexValue rsComplex = new RotationSetComplexValue();
        XhbRotationSetDdDao xhbRotationSetDdDao = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xhbRotationSetDdDao.setRotationSetDdId(ROTATION_SET_DD_ID);
        RotationSetDdComplexValue rsddComplex = new RotationSetDdComplexValue(xhbRotationSetDdDao,
            DummyPublicDisplayUtil.getXhbDisplayDocumentDao());
        rsComplex.setRotationSetDao(rotationSetsDao.get());
        rsComplex.addRotationSetDdComplexValue(rsddComplex);

        boolean result = false;
        try {
            Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.isA(Long.class)))
                .thenReturn(rotationSetsDao);
            Mockito
                .when(
                    mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
                .thenReturn(xrsddList);
            Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
                .thenReturn(Optional.of(xhbDisplayDocumentDao));

            mockXhbRotationSetDdRepository.delete(Optional.of(rsddComplex.getRotationSetDdDao()));
            mockXhbRotationSetsRepository.delete(rotationSetsDao);
            Mockito.when(mockEntityManager.isOpen()).thenReturn(true);

            // Run Method
            classUnderTest.deleteRotationSets(rsComplex);
            result = true;

        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }
   
}
