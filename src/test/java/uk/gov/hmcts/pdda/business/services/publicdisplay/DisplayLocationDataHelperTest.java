package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.util.StringUtilities;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.CourtSitePdComplexValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayLocationDataHelperTest {

    private static final String NOT_TRUE = "Result is not True";
    private static final String NULL = "Result is Null";

    private static final Integer COURT_ID = -1;

    @Mock
    private ResourceBundle mockResourceBundle;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbRotationSetDdRepository mockXhbRotationSetDdRepository;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbDisplayDocumentRepository mockXhbDisplayDocumentRepository;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private final DisplayLocationDataHelper classUnderTest = new DisplayLocationDataHelper();

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new DisplayLocationDataHelper();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetDisplaysForCourtActual() {
        // Setup
        List<XhbCourtSiteDao> xhbCourtSiteDaoList = new ArrayList<>();
        List<XhbDisplayLocationDao> xhbDisplayLocationDaoList = new ArrayList<>();
        XhbDisplayLocationDao xhbDisplayLocationDao = DummyPublicDisplayUtil.getXhbDisplayLocationDao();
        xhbDisplayLocationDaoList.add(xhbDisplayLocationDao);
        XhbCourtSiteDao xhbCourtSiteDao = DummyCourtUtil.getXhbCourtSiteDao();
        xhbCourtSiteDao.setXhbDisplayLocations(xhbDisplayLocationDaoList);
        xhbCourtSiteDaoList.add(xhbCourtSiteDao);
        // Expects
        Mockito.when(mockXhbCourtSiteRepository.findByCourtId(Mockito.isA(Integer.class)))
            .thenReturn(xhbCourtSiteDaoList);
        // Run
        boolean result = false;
        try {
            DisplayLocationDataHelper.getDisplaysForCourt(COURT_ID, mockXhbCourtSiteRepository);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetRotationSetsDetailForCourtActual() {
        // Setup
        List<XhbRotationSetsDao> xhbRotationSetsDaoList = new ArrayList<>();
        xhbRotationSetsDaoList.add(DummyPublicDisplayUtil.getXhbRotationSetsDao());
        List<XhbDisplayDao> xhbDisplayDaoList = new ArrayList<>();
        xhbDisplayDaoList.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        Optional<XhbDisplayDocumentDao> xhbDisplayDocumentDao =
            Optional.of(DummyPublicDisplayUtil.getXhbDisplayDocumentDao());
        // Expects
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(Mockito.isA(Integer.class)))
            .thenReturn(xhbRotationSetsDaoList);
        Mockito.when(mockXhbDisplayRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xhbDisplayDaoList);
        Mockito.when(mockResourceBundle.getString(Mockito.isA(String.class))).thenReturn("TranslatedText");
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(new ArrayList<>());
        Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(xhbDisplayDocumentDao);
        // Run
        boolean result = false;
        try {
            DisplayLocationDataHelper.getRotationSetsDetailForCourt(COURT_ID, mockResourceBundle,
                mockXhbRotationSetsRepository, mockXhbRotationSetDdRepository, mockXhbDisplayRepository,
                mockXhbDisplayDocumentRepository);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetRotationSetsDetailForCourtMissingResourceException() {
        // Setup
        List<XhbRotationSetsDao> xhbRotationSetsDaoList = new ArrayList<>();
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        List<XhbRotationSetDdDao> xhbRotationSetDdDaoList = new ArrayList<>();
        xhbRotationSetDdDaoList.add(DummyPublicDisplayUtil.getXhbRotationSetDdDao());
        xhbRotationSetsDaoList.add(xhbRotationSetsDao);

        List<XhbDisplayDao> xhbDisplayDaoList = new ArrayList<>();
        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        xhbDisplayDaoList.add(xhbDisplayDao);
        Optional<XhbDisplayDocumentDao> xhbDisplayDocumentDao =
            Optional.of(DummyPublicDisplayUtil.getXhbDisplayDocumentDao());

        // Expects
        Mockito.when(mockXhbRotationSetsRepository.findByCourtId(Mockito.isA(Integer.class)))
            .thenReturn(xhbRotationSetsDaoList);
        Mockito.when(mockXhbDisplayRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(xhbDisplayDaoList);
        Mockito.when(mockResourceBundle.getString(Mockito.isA(String.class)))
            .thenThrow(new MissingResourceException(null, null, null));
        Mockito.when(mockXhbRotationSetDdRepository.findByRotationSetId(Mockito.isA(Integer.class)))
            .thenReturn(new ArrayList<>());
        Mockito.when(mockXhbDisplayDocumentRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(xhbDisplayDocumentDao);
        // Run
        boolean result = true;

        DisplayLocationDataHelper.getRotationSetsDetailForCourt(COURT_ID, mockResourceBundle,
            mockXhbRotationSetsRepository, mockXhbRotationSetDdRepository, mockXhbDisplayRepository,
            mockXhbDisplayDocumentRepository);

        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetDisplaysForCourtWrapper() {
        Integer courtId = 81;
        Mockito.when(mockEntityManager.createNamedQuery(Mockito.isA(String.class))).thenReturn(mockQuery);
        CourtSitePdComplexValue[] results = DisplayLocationDataHelper.getDisplaysForCourt(courtId, mockEntityManager);
        assertNotNull(results, NULL);
    }

    @Test
    void testGetRotationSetsDetailForCourtWrapper() {
        Integer courtId = 81;
        Mockito.when(mockEntityManager.createNamedQuery(Mockito.isA(String.class))).thenReturn(mockQuery);
        boolean result = false;
        try {
            DisplayLocationDataHelper.getRotationSetsDetailForCourt(courtId, Locale.UK, mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testToSentenceCase() {
        boolean result = false;
        try {
            StringUtilities.toSentenceCase("test This");
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }
}
