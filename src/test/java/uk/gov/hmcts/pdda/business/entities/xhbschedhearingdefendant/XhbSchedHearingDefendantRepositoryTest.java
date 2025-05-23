package uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant;

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
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbSchedHearingDefendantRepositoryTest
    extends AbstractRepositoryTest<XhbSchedHearingDefendantDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbSchedHearingDefendantRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbSchedHearingDefendantRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbSchedHearingDefendantRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbSchedHearingDefendantDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbSchedHearingDefendantDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByScheduledHearingIdSuccess() {
        boolean result = testFindByScheduledHearingId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByScheduledHearingIdFailure() {
        boolean result = testFindByScheduledHearingId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByScheduledHearingId(XhbSchedHearingDefendantDao dao) {
        List<XhbSchedHearingDefendantDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbSchedHearingDefendantDao> result =
            getClassUnderTest().findByScheduledHearingId(getDummyDao().getScheduledHearingId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Test
    void testFindByHearingAndDefendant() {
        List<XhbSchedHearingDefendantDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbSchedHearingDefendantDao dao = getDummyDao();
        Optional<XhbSchedHearingDefendantDao> result = classUnderTest
            .findByHearingAndDefendant(dao.getScheduledHearingId(), dao.getDefendantOnCaseId());
        assertNotNull(result, NOTNULLRESULT);
    }

    @Override
    protected XhbSchedHearingDefendantDao getDummyDao() {
        XhbSchedHearingDefendantDao result = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        assertNotNull(result.getPrimaryKey(), NOTNULLRESULT);
        return new XhbSchedHearingDefendantDao(result);
    }

}
