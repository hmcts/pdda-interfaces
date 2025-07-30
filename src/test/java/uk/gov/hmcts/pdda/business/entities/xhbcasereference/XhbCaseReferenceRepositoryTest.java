package uk.gov.hmcts.pdda.business.entities.xhbcasereference;

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
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCaseReferenceRepositoryTest extends AbstractRepositoryTest<XhbCaseReferenceDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCaseReferenceRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCaseReferenceRepository getClassUnderTest() {
        return classUnderTest; // Let Mockito inject it
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCaseReferenceRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCaseReferenceDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCaseReferenceDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }


    @Test
    void testFindByCaseIdSuccess() {
        boolean result = testFindByCaseId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCaseIdFailure() {
        boolean result = testFindByCaseId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByCaseId(XhbCaseReferenceDao dao) {
        List<XhbCaseReferenceDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }

        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            Mockito.when(mockEntityManager.createNamedQuery(isA(String.class)))
                .thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);

            List<XhbCaseReferenceDao> result =
                getClassUnderTest().findByCaseIdSafe(dao != null ? dao.getCaseId() : -1);

            assertNotNull(result, "Result is Null");
            if (dao != null) {
                assertTrue(!result.isEmpty(), "Result list is empty");
                assertSame(dao, result.get(0), "Result is not Same");
            } else {
                assertTrue(result.isEmpty(), "Expected empty result list");
            }

            return true;
        }
    }


    @Override
    protected XhbCaseReferenceDao getDummyDao() {
        Integer caseReferenceId = getDummyId();
        Integer reportingRestrictions = -1;
        Integer caseId = getDummyId();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;

        XhbCaseReferenceDao result = new XhbCaseReferenceDao();
        assertNotNull(result, NOTNULLRESULT);
        result = new XhbCaseReferenceDao(caseReferenceId, reportingRestrictions, caseId, lastUpdateDate, creationDate,
            lastUpdatedBy, createdBy, version);
        assertNotNull(result, NOTNULLRESULT);
        caseReferenceId = result.getPrimaryKey();
        assertNotNull(caseReferenceId, NOTNULLRESULT);
        return new XhbCaseReferenceDao(result);
    }

}
