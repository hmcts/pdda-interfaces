package uk.gov.hmcts.pdda.business.entities.xhbcourtsite;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
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
class XhbCourtSiteRepositoryTest extends AbstractRepositoryTest<XhbCourtSiteDao> {

    public static final String NULL = "Result is Null";
    
    private static final Integer BYCRESTCOURTID = 1;
    private static final Integer BYCOURTID = 2;
    private static final Integer BYCOURTSITENAME = 3;
    
    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtSiteRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtSiteRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCourtSiteRepository(getEntityManager());
        }
        return classUnderTest;
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

    @Test
    void testFindByCourtCodeAndListTypeAndListDateSuccess() {
        boolean result = testFind(getDummyDao(), BYCOURTSITENAME);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtCodeAndListTypeAndListDateFailure() {
        boolean result = testFind(null, BYCOURTSITENAME);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtIdSuccess() {
        boolean result = testFind(getDummyDao(), BYCOURTID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtIdFailure() {
        boolean result = testFind(null, BYCOURTID);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFind(XhbCourtSiteDao dao, Integer whichTest) {
        List<XhbCourtSiteDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        List<XhbCourtSiteDao> resultList = null;
        if (BYCRESTCOURTID.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            resultList =
                getClassUnderTest().findByCrestCourtIdValue(getDummyDao().getCrestCourtId());
        } else if (BYCOURTID.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            resultList = getClassUnderTest().findByCourtId(getDummyDao().getCourtId());
        } else if (BYCOURTSITENAME.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getSingleResult()).thenReturn(dao);
            Optional<XhbCourtSiteDao> result = getClassUnderTest()
                .findByCourtSiteName(getDummyDao().getCourtId(), getDummyDao().getCourtSiteName());
            assertNotNull(result, "Result is Null");
            if (dao != null) {
                assertSame(dao, result.get(), SAME);
            } else {
                assertSame(Optional.empty(), result, SAME);
            }
            return true;
        }
        assertNotNull(resultList, "Result is Null");
        if (dao != null) {
            assertSame(dao, resultList.get(0), SAME);
        } else {
            assertSame(0, resultList.size(), SAME);
        }
        return true;
    }

    @Test
    void testXhbCourtSiteDaoSecondConstructor() {
        XhbCourtSiteDao result =  new XhbCourtSiteDao(getDummyDao());
        assertNotNull(result, NULL);
    }

    @Override
    protected XhbCourtSiteDao getDummyDao() {
        return DummyCourtUtil.getXhbCourtSiteDao();
    }

}
