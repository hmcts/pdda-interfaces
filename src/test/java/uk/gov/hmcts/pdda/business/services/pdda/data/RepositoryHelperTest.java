package uk.gov.hmcts.pdda.business.services.pdda.data;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import com.pdda.hb.jpa.EntityManagerUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TooManyMethods")
class RepositoryHelperTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is False";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private XhbHearingListRepository mockXhbHearingListRepository;

    @Mock
    private XhbSittingRepository mockXhbSittingRepository;

    @Mock
    private XhbCaseRepository mockXhbCaseRepository;

    @Mock
    private XhbDefendantOnCaseRepository mockXhbDefendantOnCaseRepository;

    @Mock
    private XhbDefendantRepository mockXhbDefendantRepository;

    @Mock
    private XhbHearingRepository mockXhbHearingRepository;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private XhbSchedHearingDefendantRepository mockXhbSchedHearingDefendantRepository;

    @Mock
    private XhbCrLiveDisplayRepository mockXhbCrLiveDisplayRepository;

    @InjectMocks
    private final RepositoryHelper classUnderTest = new RepositoryHelper(mockEntityManager,
        mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbHearingListRepository,
        mockXhbSittingRepository, mockXhbCaseRepository, mockXhbDefendantOnCaseRepository,
        mockXhbDefendantRepository, mockXhbHearingRepository, mockXhbScheduledHearingRepository,
        mockXhbSchedHearingDefendantRepository, mockXhbCrLiveDisplayRepository) {
        @Override
        public void clearRepositories() {
            super.clearRepositories();
        }
    };
    
    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(EntityManagerUtil.class);
    }
    
    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();;
    }


    @Test
    void testClearRepositories() {
        boolean result = false;
        try {
        classUnderTest.clearRepositories();
        result = true;
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetXhbCourtSiteRepository() {
        mockEntityManager();
        XhbCourtSiteRepository result = classUnderTest.getXhbCourtSiteRepository();
        assertNotNull(result, NOTNULL);
    }
    
    private void mockEntityManager() {
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        Mockito.when(EntityManagerUtil.isEntityManagerActive(mockEntityManager)).thenReturn(false);
    }
    
    @Test
    void testGetXhbCourtRoomRepository() {
        mockEntityManager();
        XhbCourtRoomRepository result = classUnderTest.getXhbCourtRoomRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbHearingListRepository() {
        mockEntityManager();
        XhbHearingListRepository result = classUnderTest.getXhbHearingListRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbSittingRepository() {
        mockEntityManager();
        XhbSittingRepository result = classUnderTest.getXhbSittingRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbCaseRepository() {
        mockEntityManager();
        XhbCaseRepository result = classUnderTest.getXhbCaseRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbDefendantOnCaseRepository() {
        mockEntityManager();
        XhbDefendantOnCaseRepository result = classUnderTest.getXhbDefendantOnCaseRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbDefendantRepository() {
        mockEntityManager();
        XhbDefendantRepository result = classUnderTest.getXhbDefendantRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbHearingRepository() {
        mockEntityManager();
        XhbHearingRepository result = classUnderTest.getXhbHearingRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbScheduledHearingRepository() {
        mockEntityManager();
        XhbScheduledHearingRepository result = classUnderTest.getXhbScheduledHearingRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbSchedHearingDefendantRepository() {
        mockEntityManager();
        XhbSchedHearingDefendantRepository result = classUnderTest.getXhbSchedHearingDefendantRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbCrLiveDisplayRepository() {
        mockEntityManager();
        XhbCrLiveDisplayRepository result = classUnderTest.getXhbCrLiveDisplayRepository();
        assertNotNull(result, NOTNULL);
    }
    
}
