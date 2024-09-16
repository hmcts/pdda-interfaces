package uk.gov.hmcts.pdda.business.entities.xhbcasereference;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        if (classUnderTest == null) {
            classUnderTest = new XhbCaseReferenceRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testfindByCaseIdSuccess() {
        boolean result = testfindByCaseId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testfindByCaseIdFailure() {
        boolean result = testfindByCaseId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testfindByCaseId(XhbCaseReferenceDao dao) {
        List<XhbCaseReferenceDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbCaseReferenceDao> result =
            getClassUnderTest().findByCaseId(getDummyDao().getCaseId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
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
        assertNotNull(result, NOTNULL);
        result = new XhbCaseReferenceDao(caseReferenceId, reportingRestrictions, caseId, lastUpdateDate, creationDate,
            lastUpdatedBy, createdBy, version);
        assertNotNull(result, NOTNULL);
        caseReferenceId = result.getPrimaryKey();
        assertNotNull(caseReferenceId, NOTNULL);
        return new XhbCaseReferenceDao(result);
    }

}
