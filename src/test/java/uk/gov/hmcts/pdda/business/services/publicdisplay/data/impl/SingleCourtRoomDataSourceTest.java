package uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.PublicDisplayQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.CourtRoomNotFoundException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SingleCourtRoomDataSourceTest {

    private static final Logger LOG = LoggerFactory.getLogger(SingleCourtRoomDataSourceTest.class);

    private static final String TRUE = "Result is not True";
    private static final int COURT_ID = 81;
    private static final String COURTNAME = "Court1";
    private static final int[] COURT_ROOM_IDS = {1, 2, 3};

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PublicDisplayQuery mockPublicDisplayQuery;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    private static final DisplayDocumentType[] CLASSUNDERTESTARRAY =
        {DisplayDocumentType.COURT_DETAIL, DisplayDocumentType.COURT_LIST, DisplayDocumentType.DAILY_LIST,
            DisplayDocumentType.ALL_COURT_STATUS, DisplayDocumentType.SUMMARY_BY_NAME,
            DisplayDocumentType.ALL_CASE_STATUS, DisplayDocumentType.JURY_CURRENT_STATUS};

    private SingleCourtRoomDataSource classUnderTest;

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }


    @SuppressWarnings("PMD.CloseResource")
    @BeforeAll
    public static void mockEntityManagerFactory() {
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(emf.createEntityManager()).thenReturn(em);
        EntityManagerUtil.setEntityManagerFactory(emf);
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    private SingleCourtRoomDataSource getClassUnderTest(DisplayDocumentType displayDocumentType) {
        DisplayDocumentUri uri = new DisplayDocumentUri(Locale.UK, COURT_ID, displayDocumentType, COURT_ROOM_IDS);
        return new SingleCourtRoomDataSource(uri, mockPublicDisplayQuery, mockXhbCourtRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);
    }

    @Test
    void testRetrieveSuccess() {
        for (DisplayDocumentType displayDocumentType : CLASSUNDERTESTARRAY) {
            LOG.debug("DisplayDocumentType:{}", displayDocumentType.toString());
            classUnderTest = getClassUnderTest(displayDocumentType);
            boolean result = testRetrieve(Optional.of(DummyCourtUtil.getXhbCourtDao(1, COURTNAME)),
                Optional.of(DummyCourtUtil.getXhbCourtSiteDao()), true);
            assertTrue(result, TRUE);
        }
    }

    @Test
    void testRetrieveNoCourt() {
        classUnderTest = getClassUnderTest(DisplayDocumentType.COURT_DETAIL);
        Assertions.assertThrows(CourtNotFoundException.class, () -> {
            testRetrieve(Optional.empty(), Optional.empty(), false);
        });
    }

    @Test
    void testRetrieveNoCourtSite() {
        classUnderTest = getClassUnderTest(DisplayDocumentType.COURT_DETAIL);

        Assertions.assertThrows(CourtRoomNotFoundException.class, () -> {
            testRetrieve(Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, COURTNAME)), // court
                Optional.empty(), // missing site
                true // room expected
            );
        });
    }



    @Test
    void testRetrieveNoCourtRoom() {
        classUnderTest = getClassUnderTest(DisplayDocumentType.COURT_DETAIL);

        // Ensure courtId matches COURT_ID (81)
        XhbCourtSiteDao courtSite = DummyCourtUtil.getXhbCourtSiteDao();
        courtSite.setCourtId(COURT_ID);

        Assertions.assertThrows(CourtRoomNotFoundException.class, () -> {
            testRetrieve(Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, COURTNAME)), // court
                Optional.of(courtSite), // site
                false // no rooms
            );
        });
    }



    private boolean testRetrieve(Optional<XhbCourtDao> xhbCourtDao, Optional<XhbCourtSiteDao> xhbCourtSiteDao,
        boolean isXhbCourtRoomDaoReqd) {
        Mockito.when(mockXhbCourtRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(xhbCourtDao);
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(xhbCourtSiteDao);
        for (int courtRoomId : COURT_ROOM_IDS) {
            Optional<XhbCourtRoomDao> xhbCourtRoomDao =
                isXhbCourtRoomDaoReqd ? Optional.of(DummyCourtUtil.getXhbCourtRoomDao()) : Optional.empty();
            Mockito.when(mockXhbCourtRoomRepository.findByIdSafe(courtRoomId))
                .thenReturn(xhbCourtRoomDao);
        }
        classUnderTest.retrieve(mockEntityManager);

        return true;
    }
}
