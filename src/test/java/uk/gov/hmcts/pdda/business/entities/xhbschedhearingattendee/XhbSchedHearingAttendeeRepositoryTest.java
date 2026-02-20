package uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
class XhbSchedHearingAttendeeRepositoryTest extends AbstractRepositoryTest<XhbSchedHearingAttendeeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbSchedHearingAttendeeRepository classUnderTest;
    
    @Mock
    private EntityTransaction mockTransaction;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbSchedHearingAttendeeRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbSchedHearingAttendeeRepository(mockEntityManager);
        
        Mockito.when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbSchedHearingAttendeeDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbSchedHearingAttendeeDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByScheduledHearingIdSafeEmpty() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Mockito.when(getEntityManager().createQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(new ArrayList<>());

            List<XhbSchedHearingAttendeeDao> result = classUnderTest.findByScheduledHearingIdSafe(-1);
            assertNotNull(result, NOTNULLRESULT);
        }
    }

    @Test
    void testDeleteByScheduledHearingIdsNoRows() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Mockito.when(getEntityManager().createQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.executeUpdate()).thenReturn(0);

            classUnderTest.deleteByScheduledHearingIds(List.of(1,2,3));

            Mockito.verify(getEntityManager()).createQuery(isA(String.class));
            Mockito.verify(mockQuery).executeUpdate();
        }
    }

    @Test
    void testDeleteByScheduledHearingIdsWithRows() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Mockito.when(getEntityManager().createQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.executeUpdate()).thenReturn(4);

            classUnderTest.deleteByScheduledHearingIds(List.of(7,8));

            Mockito.verify(mockQuery).executeUpdate();
        }
    }

    @Override
    protected XhbSchedHearingAttendeeDao getDummyDao() {
        Integer shAttendeeId = getDummyId();
        String attendeeType = "";
        Integer scheduledHearingId = -1;
        Integer shStaffId = -1;
        Integer shJusticeId = -1;
        Integer refJudgeId = -1;
        Integer refCourtReporterId = -1;
        Integer refJusticeId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbSchedHearingAttendeeDao result = new XhbSchedHearingAttendeeDao();
        result.setShAttendeeId(shAttendeeId);
        result.setAttendeeType(attendeeType);
        result.setScheduledHearingId(scheduledHearingId);
        result.setShStaffId(shStaffId);
        result.setShJusticeId(shJusticeId);
        result.setRefJudgeId(refJudgeId);
        result.setRefCourtReporterId(refCourtReporterId);
        result.setRefJusticeId(refJusticeId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        shAttendeeId = result.getPrimaryKey();
        assertNotNull(shAttendeeId, NOTNULLRESULT);
        return new XhbSchedHearingAttendeeDao(result);
    }

}