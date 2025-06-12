package uk.gov.hmcts.pdda.business.entities.xhbcourt;

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
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCourtRepositoryTest extends AbstractRepositoryTest<XhbCourtDao> {

    private static final String NOTNULL = "Result is Null";
    private static final Integer BYCRESTCOURTID = 1;

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCourtRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCourtDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCourtDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByCrestCourtIdValueSuccess() {
        boolean result = testFind(getDummyDao(), BYCRESTCOURTID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCrestCourtIdValueFailure() {
        boolean result = testFind(null, BYCRESTCOURTID);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFind(XhbCourtDao dao, Integer whichTest) {
        List<XhbCourtDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        List<XhbCourtDao> result = null;
        if (BYCRESTCOURTID.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            result = getClassUnderTest().findByCrestCourtIdValue(getDummyDao().getCrestCourtId());
        }
        assertNotNull(result, NOTNULL);
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Override
    protected XhbCourtDao getDummyDao() {
        return DummyCourtUtil.getXhbCourtDao(DummyServicesUtil.getDummyId(), "Court1");
    }

}
