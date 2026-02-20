package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
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
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
class XhbCrLiveDisplayRepositoryTest extends AbstractRepositoryTest<XhbCrLiveDisplayDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCrLiveDisplayRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCrLiveDisplayRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCrLiveDisplayRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCrLiveDisplayDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCrLiveDisplayDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindLiveDisplaysWhereStatusNotNull() {
        List<XhbCrLiveDisplayDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        List<XhbCrLiveDisplayDao> result = classUnderTest.findLiveDisplaysWhereStatusNotNull();
        assertNotNull(result, NOTNULLRESULT);
    }

    @Test
    void testFindByCourtRoom() {
        List<XhbCrLiveDisplayDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbCrLiveDisplayDao dao = getDummyDao();
        Optional<XhbCrLiveDisplayDao> result =
            classUnderTest.findByCourtRoom(dao.getCourtRoomId());
        assertNotNull(result, NOTNULLRESULT);
    }

    @Test
    void testUpdateScheduledHearingIdToNullNoMatches() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Mockito.when(getEntityManager().createNativeQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.executeUpdate()).thenReturn(0);

            // Should not throw
            classUnderTest.updateScheduledHearingIdToNull(123);

            Mockito.verify(getEntityManager()).createNativeQuery(isA(String.class));
            Mockito.verify(mockQuery).setParameter(isA(String.class), isA(Object.class));
            Mockito.verify(mockQuery).executeUpdate();
        }
    }

    @Test
    void testUpdateScheduledHearingIdToNullWithMatches() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Mockito.when(getEntityManager().createNativeQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.executeUpdate()).thenReturn(2);

            classUnderTest.updateScheduledHearingIdToNull(456);

            Mockito.verify(mockQuery).executeUpdate();
        }
    }

    @Override
    protected XhbCrLiveDisplayDao getDummyDao() {
        XhbCrLiveDisplayDao result = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        assertNotNull(result.getPrimaryKey(), NOTNULLRESULT);
        return new XhbCrLiveDisplayDao(result);
    }

}