package uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier;

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
class XhbPddaDlNotifierRepositoryTest extends AbstractRepositoryTest<XhbPddaDlNotifierDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbPddaDlNotifierRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbPddaDlNotifierRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbPddaDlNotifierRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByCourtAndLastRunDateSuccess() {
        boolean result = testFindByCourtAndLastRunDate(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtAndLastRunDateFailure() {
        boolean result = testFindByCourtAndLastRunDate(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByCourtAndLastRunDate(XhbPddaDlNotifierDao dao) {
        List<XhbPddaDlNotifierDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbPddaDlNotifierDao> result = getClassUnderTest()
            .findByCourtAndLastRunDate(getDummyDao().getCourtId(), LocalDateTime.now());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbPddaDlNotifierDao getDummyDao() {
        Integer pddaDlNotifierId = getDummyId();
        Integer courtId = -1;
        LocalDateTime lastRunDate = LocalDateTime.now();
        String status = "status";
        String errorMessage = "errorMessage";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        XhbPddaDlNotifierDao result = new XhbPddaDlNotifierDao();
        result.setPddaDlNotifierId(pddaDlNotifierId);
        result.setCourtId(courtId);
        result.setLastRunDate(lastRunDate);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        pddaDlNotifierId = result.getPrimaryKey();
        assertNotNull(pddaDlNotifierId, NOTNULL);
        return new XhbPddaDlNotifierDao(result);
    }

}
