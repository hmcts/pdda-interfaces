package uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd;

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
class XhbRotationSetDdRepositoryTest extends AbstractRepositoryTest<XhbRotationSetDdDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbRotationSetDdRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbRotationSetDdRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbRotationSetDdRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbRotationSetDdDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbRotationSetDdDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByRotationSetIdSuccess() {
        boolean result = testFindByRotationSetId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByRotationSetIdFailure() {
        boolean result = testFindByRotationSetId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByRotationSetId(XhbRotationSetDdDao dao) {
        List<XhbRotationSetDdDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbRotationSetDdDao> result =
            getClassUnderTest().findByRotationSetId(getDummyDao().getRotationSetId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbRotationSetDdDao getDummyDao() {
        Integer rotationSetDdId = getDummyId();
        Integer rotationSetId = -1;
        Integer displayDocumentId = -1;
        Integer pageDelay = -1;
        Integer ordering = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbRotationSetDdDao result = new XhbRotationSetDdDao();
        result.setRotationSetDdId(rotationSetDdId);
        result.setRotationSetId(rotationSetId);
        result.setDisplayDocumentId(displayDocumentId);
        result.setPageDelay(pageDelay);
        result.setOrdering(ordering);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        rotationSetDdId = result.getPrimaryKey();
        assertNotNull(rotationSetDdId, NOTNULLRESULT);
        result.setDisplayDocumentId(result.getDisplayDocumentId());
        return new XhbRotationSetDdDao(result);
    }

}
