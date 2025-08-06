package uk.gov.hmcts.pdda.business.entities.xhbconfigprop;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbConfigPropRepositoryTest extends AbstractRepositoryTest<XhbConfigPropDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbConfigPropRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbConfigPropRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbConfigPropRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbConfigPropDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbConfigPropDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByPropertyNameSuccess() {
        boolean result = testFindByPropertyName(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testfindByPropertyNameFailure() {
        boolean result = testFindByPropertyName(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByPropertyName(XhbConfigPropDao dao) {
        List<XhbConfigPropDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbConfigPropDao> result =
            getClassUnderTest().findByPropertyName(getDummyDao().getPropertyName());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbConfigPropDao getDummyDao() {
        Integer configPropId = getDummyId();
        String propertyName = "propertyName";
        String propertyValue = "propertyValue";

        XhbConfigPropDao result = new XhbConfigPropDao(configPropId, propertyName, propertyValue);
        configPropId = result.getPrimaryKey();
        assertNotNull(configPropId, NOTNULLRESULT);
        return new XhbConfigPropDao(result);
    }

}
