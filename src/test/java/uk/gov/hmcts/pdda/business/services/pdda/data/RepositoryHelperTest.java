package uk.gov.hmcts.pdda.business.services.pdda.data;

import com.pdda.hb.jpa.EntityManagerUtil;
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
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbshjudge.XhbShJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LawOfDemeter"})
class RepositoryHelperTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is False";

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private final RepositoryHelper classUnderTest = new RepositoryHelper(mockEntityManager) {
        @Override
        public EntityManager getEntityManager() {
            return super.getEntityManager();
        }
    };

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(EntityManagerUtil.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
        new RepositoryHelper();
    }

    @Test
    void testGetEntityManager() {
        boolean result = testGetEntityManager(false);
        assertTrue(result, TRUE);
        result = testGetEntityManager(true);
        assertTrue(result, TRUE);
    }

    private boolean testGetEntityManager(boolean isActive) {
        mockTheEntityManager(isActive);
        try (EntityManager entityManager = classUnderTest.getEntityManager()) {
            assertNotNull(entityManager, NOTNULL);
        }
        return true;
    }

    private void mockTheEntityManager(boolean result) {
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        Mockito.when(EntityManagerUtil.isEntityManagerActive(mockEntityManager)).thenReturn(result);
    }

    @Test
    void testGetXhbCourtSiteRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbCourtSiteRepository result = classUnderTest.getXhbCourtSiteRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbCourtSiteRepository",
            Mockito.mock(XhbCourtSiteRepository.class));
        result = classUnderTest.getXhbCourtSiteRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbCourtSiteRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbCourtRoomRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbCourtRoomRepository result = classUnderTest.getXhbCourtRoomRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbCourtSiteRepository",
            Mockito.mock(XhbCourtSiteRepository.class));
        result = classUnderTest.getXhbCourtRoomRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbCourtRoomRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbHearingListRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbHearingListRepository result = classUnderTest.getXhbHearingListRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbHearingListRepository",
            Mockito.mock(XhbHearingListRepository.class));
        result = classUnderTest.getXhbHearingListRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbHearingListRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbSittingRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbSittingRepository result = classUnderTest.getXhbSittingRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbSittingRepository",
            Mockito.mock(XhbSittingRepository.class));
        result = classUnderTest.getXhbSittingRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbSittingRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbCaseRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbCaseRepository result = classUnderTest.getXhbCaseRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbCaseRepository",
            Mockito.mock(XhbCaseRepository.class));
        result = classUnderTest.getXhbCaseRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbCaseRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbRefJudgeRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbRefJudgeRepository result = classUnderTest.getXhbRefJudgeRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbRefJudgeRepository",
            Mockito.mock(XhbRefJudgeRepository.class));
        result = classUnderTest.getXhbRefJudgeRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbRefJudgeRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbShJudgeRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbShJudgeRepository result = classUnderTest.getXhbShJudgeRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbShJudgeRepository",
            Mockito.mock(XhbShJudgeRepository.class));
        result = classUnderTest.getXhbShJudgeRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbShJudgeRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbDefendantOnCaseRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbDefendantOnCaseRepository result = classUnderTest.getXhbDefendantOnCaseRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbDefendantOnCaseRepository",
            Mockito.mock(XhbDefendantOnCaseRepository.class));
        result = classUnderTest.getXhbDefendantOnCaseRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbDefendantOnCaseRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbDefendantRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbDefendantRepository result = classUnderTest.getXhbDefendantRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbDefendantRepository",
            Mockito.mock(XhbDefendantRepository.class));
        result = classUnderTest.getXhbDefendantRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbDefendantRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbRefHearingTypeRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbRefHearingTypeRepository result = classUnderTest.getXhbRefHearingTypeRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbRefHearingTypeRepository",
            Mockito.mock(XhbRefHearingTypeRepository.class));
        result = classUnderTest.getXhbRefHearingTypeRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbRefHearingTypeRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbHearingRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbHearingRepository result = classUnderTest.getXhbHearingRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbHearingRepository",
            Mockito.mock(XhbHearingRepository.class));
        result = classUnderTest.getXhbHearingRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbHearingRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbScheduledHearingRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbScheduledHearingRepository result = classUnderTest.getXhbScheduledHearingRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbScheduledHearingRepository",
            Mockito.mock(XhbScheduledHearingRepository.class));
        result = classUnderTest.getXhbScheduledHearingRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbScheduledHearingRepository();
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetXhbSchedHearingAttendeeRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbSchedHearingAttendeeRepository result = classUnderTest.getXhbSchedHearingAttendeeRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbSchedHearingAttendeeRepository",
            Mockito.mock(XhbSchedHearingAttendeeRepository.class));
        result = classUnderTest.getXhbSchedHearingAttendeeRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbSchedHearingAttendeeRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbSchedHearingDefendantRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbSchedHearingDefendantRepository result =
            classUnderTest.getXhbSchedHearingDefendantRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbSchedHearingDefendantRepository",
            Mockito.mock(XhbSchedHearingDefendantRepository.class));
        result = classUnderTest.getXhbSchedHearingDefendantRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbSchedHearingDefendantRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetXhbCrLiveDisplayRepository() {
        // Check the null condition
        mockTheEntityManager(false);
        XhbCrLiveDisplayRepository result = classUnderTest.getXhbCrLiveDisplayRepository();
        assertNotNull(result, NOTNULL);

        // Check the inactive enitytManager
        ReflectionTestUtils.setField(classUnderTest, "xhbCrLiveDisplayRepository",
            Mockito.mock(XhbCrLiveDisplayRepository.class));
        result = classUnderTest.getXhbCrLiveDisplayRepository();
        assertNotNull(result, NOTNULL);

        // Check the active entityManager
        mockTheEntityManager(true);
        result = classUnderTest.getXhbCrLiveDisplayRepository();
        assertNotNull(result, NOTNULL);
    }

}
