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
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.ExcessiveImports"})
class DisplayRotationSetDataHelperTest {

    private static final String TRUE = "Result is not True";
    private static final String NOTNULL = "Result is Null";

    @Mock
    private ResourceBundle mockResourceBundle;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbRotationSetDdRepository mockXhbRotationSetDdRepository;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbDisplayTypeRepository mockXhbDisplayTypeRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

    @Mock
    private XhbDisplayDocumentRepository mockXhbDisplayDocumentRepository;

    @InjectMocks
    private final DisplayRotationSetDataHelper classUnderTest = new DisplayRotationSetDataHelper();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new DisplayRotationSetDataHelper();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataForDisplayRotationSetsNoDisplays() {
        // Setup
        List<XhbCourtSiteDao> xhbCourtSiteDaoList = new ArrayList<>();
        xhbCourtSiteDaoList.add(DummyCourtUtil.getXhbCourtSiteDao());
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        List<XhbRotationSetDdDao> xhbRotationSetDdDaoList = new ArrayList<>();
        xhbRotationSetDdDaoList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());
        XhbCourtDao court = DummyCourtUtil.getXhbCourtDao(-1, "Shortname");
        List<XhbDisplayDao> xhbDisplays = new ArrayList<>();
        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        // Expects
        Mockito
            .when(
                mockXhbRotationSetDdRepository.findByRotationSetIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(xhbRotationSetDdDaoList);
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));
        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Run
        boolean result = false;
        try {
            classUnderTest.getDataForDisplayRotationSets(court, xhbRotationSetsDao, xhbDisplays,
                mockXhbRotationSetDdRepository, mockXhbDisplayDocumentRepository, mockXhbDisplayTypeRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataForDisplayRotationSetsWithDisplays() {
        // Setup
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        XhbCourtDao court = DummyCourtUtil.getXhbCourtDao(-1, "Shortname");
        List<XhbDisplayDao> xhbDisplays = new ArrayList<>();
        xhbDisplays.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        XhbDisplayDocumentDao xhbDisplayDocumentDao = DummyPublicDisplayUtil.getXhbDisplayDocumentDao();
        List<XhbCourtRoomDao> rooms = new ArrayList<>();
        rooms.add(DummyCourtUtil.getXhbCourtRoomDao());
        // Expects
        Mockito.when(mockXhbDisplayDocumentRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbDisplayDocumentDao));
        Mockito.when(mockXhbDisplayTypeRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayTypeDao()));
        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        Mockito.when(mockXhbCourtRoomRepository.findByDisplayIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(rooms);
        // Run
        boolean result = false;
        try {
            classUnderTest.getDataForDisplayRotationSets(court, xhbRotationSetsDao, xhbDisplays,
                mockXhbRotationSetDdRepository, mockXhbDisplayDocumentRepository, mockXhbDisplayTypeRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataForCourt() {
        XhbCourtDao court = DummyCourtUtil.getXhbCourtDao(-1, "Shortname");
        // Run
        DisplayRotationSetData[] result =
            classUnderTest.getDataForCourt(court, mockXhbDisplayRepository, mockXhbRotationSetsRepository,
                mockXhbRotationSetDdRepository, mockXhbDisplayDocumentRepository, mockXhbDisplayTypeRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);
        
        assertNotNull(result, NOTNULL);
    }
}
