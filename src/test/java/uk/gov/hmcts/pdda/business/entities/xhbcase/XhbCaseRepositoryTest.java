package uk.gov.hmcts.pdda.business.entities.xhbcase;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCaseRepositoryTest extends AbstractRepositoryTest<XhbCaseDao> {

    @Mock
    private EntityManager mockEntityManager;

    private XhbCaseRepository classUnderTest;

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCaseRepository(mockEntityManager);
    }

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCaseRepository getClassUnderTest() {
        return classUnderTest;
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCaseDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCaseDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }


    @Test
    void testFindByNumberTypeAndCourt() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {

            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Query mockQuery = Mockito.mock(Query.class);
            XhbCaseDao dummyDao = getDummyDao(); // ← use one consistent object
            List<XhbCaseDao> list = new ArrayList<>();
            list.add(dummyDao);

            Mockito.when(mockEntityManager.createNamedQuery(Mockito.anyString()))
                .thenReturn(mockQuery);
            Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any()))
                .thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            Mockito.when(mockQuery.getSingleResult()).thenReturn(dummyDao);

            Optional<XhbCaseDao> result = classUnderTest.findByNumberTypeAndCourt(
                dummyDao.getCourtId(), dummyDao.getCaseType(), dummyDao.getCaseNumber());

            assertNotNull(result, NOTNULLRESULT);
            assertSame(dummyDao, result.get(), NOTSAMERESULT); // This will now pass
        }
    }



    @Override
    protected XhbCaseDao getDummyDao() {
        return DummyCaseUtil.getXhbCaseDao();
    }

}
