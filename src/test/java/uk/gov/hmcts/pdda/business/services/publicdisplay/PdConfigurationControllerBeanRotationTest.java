package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
@SuppressWarnings({"PMD.ExcessiveImports","PMD.TooManyFields"})
class PdConfigurationControllerBeanRotationTest {

    private static final String EQUALS = "Results are not Equal";
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
    void testDisplayRotationSetDataEquals() {
        DisplayRotationSetData object1 =
            new DisplayRotationSetData(new DisplayUri("shortname", "siteCode", "location", "desc"),
                new RotationSetDisplayDocument[] {}, 0, 0, "displayType1");
        DisplayRotationSetData object2 =
            new DisplayRotationSetData(new DisplayUri("shortname2", "siteCode2", "location2", "desc"),
                new RotationSetDisplayDocument[] {}, 1, 2, "displayType2");
        assertFalse(object1.equals(object2), FALSE);
    }

    @Test
    void testGetRotationSetsDetailForCourt() {
        // Setup
        RotationSetComplexValue[] rotationSetComplexValue = {};


        Mockito.when(DisplayLocationDataHelper.getRotationSetsDetailForCourt(COURT_ID, Locale.UK, mockEntityManager))
            .thenReturn(rotationSetComplexValue);

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
        // Setup
        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        xhbDisplayDocumentDao.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        xhbDisplayDocumentDao.setDescriptionCode(DAILYLIST);

        XhbRotationSetDdDao xhbRotationSetDdDao1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        XhbRotationSetDdDao xhbRotationSetDdDao2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();

        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(xhbRotationSetDdDao1);
        xrsddList.add(xhbRotationSetDdDao2);
        XhbRotationSetsDao xhbRotationSetsDao1 = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        xhbRotationSetsDao1.setRotationSetId(1);
        xhbRotationSetsDao1.setCourtId(COURT_ID);

        XhbRotationSetsDao xhbRotationSetsDao2 = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        xhbRotationSetsDao2.setRotationSetId(2);
        xhbRotationSetsDao2.setCourtId(COURT_ID);

        List<XhbRotationSetsDao> dummyList = new ArrayList<>();
        dummyList.add(xhbRotationSetsDao1);
        dummyList.add(xhbRotationSetsDao2);

        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(COURT_ID)).thenReturn(dummyList);
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));

        // Run method
        XhbRotationSetsDao[] rotationSetsArray = classUnderTest.getRotationSetsForCourt(COURT_ID);

        // Check results
        assertArrayEquals(dummyList.toArray(), rotationSetsArray, EQUALS);
    }


    @Test
    void testRotationSetDdComplexValueEquals() {
        XhbDisplayDocumentDao displayDocumentDao1 = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        displayDocumentDao1.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        displayDocumentDao1.setDescriptionCode(DAILYLIST);
        XhbDisplayDocumentDao displayDocumentDao2 = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
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
            throw new RotationSetNotFoundCheckedException(Integer.valueOf(-1));
        });
    }

    @Test
    void testGetRotationSet() {
        // Setup
        Integer rotationSetDdId1 = 54;
        Integer rotationSetDdId2 = 76;

        XhbRotationSetDdDao xrsd1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd1.setRotationSetDdId(rotationSetDdId1);
        XhbRotationSetDdDao xrsd2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xrsd2.setRotationSetDdId(rotationSetDdId2);
        List<XhbRotationSetDdDao> xrsdList = new ArrayList<>();
        xrsdList.add(xrsd1);
        xrsdList.add(xrsd2);

        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        xhbDisplayDocumentDao.setDisplayDocumentId(DISPLAY_DOCUMENT_ID);
        xhbDisplayDocumentDao.setDescriptionCode(DAILYLIST);

        XhbRotationSetDdDao xhbRotationSetDdDao1 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xhbRotationSetDdDao1.setRotationSetDdId(rotationSetDdId1);

        XhbRotationSetDdDao xhbRotationSetDdDao2 = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        xhbRotationSetDdDao2.setRotationSetDdId(rotationSetDdId2);

        List<XhbRotationSetDdDao> xrsddList = new ArrayList<>();
        xrsddList.add(xhbRotationSetDdDao1);
        xrsddList.add(xhbRotationSetDdDao2);
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        xhbRotationSetsDao.setRotationSetId(ROTATION_SET_ID);
        xhbRotationSetsDao.setCourtId(COURT_ID);

        Optional<XhbRotationSetsDao> xrs = Optional.of(xhbRotationSetsDao);

        Mockito.when(mockXhbRotationSetsRepository.findById(Long.valueOf(ROTATION_SET_ID))).thenReturn(xrs);
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.atMostOnce();
        Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));

        // Run method
        try {
            RotationSetComplexValue complexValue = classUnderTest.getRotationSet(ROTATION_SET_ID);

            // Check results
            assertEquals(COURT_ID, complexValue.getCourtId(), EQUALS);
            assertEquals(ROTATION_SET_ID, complexValue.getRotationSetId(), EQUALS);
            assertEquals(xrsdList.size(), complexValue.getRotationSetDdComplexValues().length, EQUALS);
            assertTrue(complexValue.hasRotationSetDd(rotationSetDdId1), TRUE);
            assertTrue(complexValue.hasRotationSetDd(rotationSetDdId2), TRUE);
            assertFalse(complexValue.hasRotationSetDd(-99), FALSE);

        } catch (RotationSetNotFoundCheckedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testCreateRotationSets() {
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

        RotationSetComplexValue rsComplex = new RotationSetComplexValue();
        XhbRotationSetDdDao rotationSetDdDao = DummyPublicDisplayUtil.getXhbRotationSetDdDao();
        // rotationSetDdDao.setRotationSetDdId(1);//TODO
        XhbDisplayDocumentDao displayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        RotationSetDdComplexValue rsddComplex = new RotationSetDdComplexValue(rotationSetDdDao, displayDocumentDao);
        rsddComplex.setDisplayDocumentDao(displayDocumentDao);
        rsComplex.setRotationSetDao(xhbRotationSetsDao);
        rsComplex.addRotationSetDdComplexValue(rsddComplex);
        DisplayBasicValueSortAdapter displayBasicValueSortAdapter = DummyDisplayUtil.getDisplayBasicValueSortAdapter();
        DisplayBasicValueSortAdapter[] displayBasicValueSortAdapters = {displayBasicValueSortAdapter};
        rsComplex.setDisplayDaos(displayBasicValueSortAdapters);
        rsComplex.setRotationSetDao(rsComplex.getRotationSetsDao());

        Optional<XhbCourtDao> courtDao = Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, "Test Court"));

        Mockito.when(mockXhbCourtRepository.findById(Mockito.isA(Integer.class))).thenReturn(courtDao);

        Mockito.when(mockXhbRotationSetsRepository.update(Mockito.isA(XhbRotationSetsDao.class)))
            .thenReturn(Optional.of(xhbRotationSetsDao));
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xrsddList);
        Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));

        // Run Method
        classUnderTest.createRotationSets(rsComplex);

        XhbDisplayDao xhbDisplayDao = displayBasicValueSortAdapter.getDao();
        assertFalse(displayBasicValueSortAdapter.equals(new DisplayBasicValueSortAdapter(xhbDisplayDao, "Test2")),
            FALSE);
        assertNotNull(displayBasicValueSortAdapter.toString(), NOTNULL);
        assertEquals(rsddComplex.getDisplayDocumentBasicValue(), displayDocumentDao, EQUALS);
        assertEquals(rsddComplex.getDisplayDocumentId(), displayDocumentDao.getDisplayDocumentId(), EQUALS);
        assertEquals(rotationSetDdDao, rsComplex.getRotationSetDd(rotationSetDdDao.getRotationSetDdId()), EQUALS);
        assertNull(rsComplex.getRotationSetDd(2), NULL);
        assertNotNull(Integer.valueOf(displayBasicValueSortAdapters[0].hashCode()), NOTNULL);
        assertSame(displayBasicValueSortAdapters[0].getDao(), xhbDisplayDao, "Result is not Same");
        assertNotNull(
            Integer.valueOf(
                displayBasicValueSortAdapter.compareTo(new DisplayBasicValueSortAdapter(xhbDisplayDao, "Test2"))),
            NOTNULL);
    }

    @Test
    void testDeleteRotationSets() {
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
            new RotationSetDdComplexValue(xhbRotationSetDdDao, DummyPublicDisplayUtil.getXhbDisplayDocumentDao());
        rsComplex.setRotationSetDao(rotationSetsDao.get());
        rsComplex.addRotationSetDdComplexValue(rsddComplex);

        boolean result = false;
        try {
            Mockito.when(mockXhbRotationSetsRepository.findById(Mockito.isA(Long.class))).thenReturn(rotationSetsDao);
            Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
                .thenReturn(xrsddList);
            Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
                .thenReturn(Optional.of(xhbDisplayDocumentDao));

            mockXhbRotationSetDdRepository.delete(Optional.of(rsddComplex.getRotationSetDdDao()));
            mockXhbRotationSetsRepository.delete(rotationSetsDao);

            // Run Method
            classUnderTest.deleteRotationSets(rsComplex);
            result = true;

        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }
}
