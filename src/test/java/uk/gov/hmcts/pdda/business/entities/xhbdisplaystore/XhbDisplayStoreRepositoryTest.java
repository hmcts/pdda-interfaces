package uk.gov.hmcts.pdda.business.entities.xhbdisplaystore;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbDisplayStoreRepositoryTest extends AbstractRepositoryTest<XhbDisplayStoreDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDisplayStoreRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDisplayStoreRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbDisplayStoreRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByRetrievalCodeSuccess() {
        boolean result = testFindByRetrievalCode(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByRetrievalCodeFailure() {
        boolean result = testFindByRetrievalCode(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByRetrievalCode(XhbDisplayStoreDao dao) {
        List<XhbDisplayStoreDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        Optional<XhbDisplayStoreDao> result = getClassUnderTest().findByRetrievalCode(getDummyDao().getRetrievalCode());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(), "Result is not Same");
        } else {
            assertSame(Optional.empty(), result, "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbDisplayStoreDao getDummyDao() {
        Long displayStoreId = getDummyLongId();
        String retrievalCode = "retrievalCode";
        String content = "content";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        XhbDisplayStoreDao result = new XhbDisplayStoreDao(displayStoreId, retrievalCode, content, lastUpdateDate,
            creationDate, lastUpdatedBy, createdBy, version, obsInd);
        displayStoreId = result.getPrimaryKey();
        assertNotNull(displayStoreId, NOTNULL);
        return new XhbDisplayStoreDao(result);
    }

}
