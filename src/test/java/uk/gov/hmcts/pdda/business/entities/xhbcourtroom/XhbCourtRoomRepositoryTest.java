package uk.gov.hmcts.pdda.business.entities.xhbcourtroom;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCourtRoomRepositoryTest extends AbstractRepositoryTest<XhbCourtRoomDao> {

    private static final String BY_COURTSITE_ID = "ByCourtSiteId";
    private static final String BY_DISPLAY_ID = "ByDisplay";
    private static final String BY_VIPMULTISITE_ID = "ByVipMultiSite";
    private static final String BY_VIPNOSITE_ID = "ByVipNoSite";

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtRoomRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtRoomRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCourtRoomRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByCourtSiteIdSuccess() {
        boolean result = testFindBy(getDummyDao(), BY_COURTSITE_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtSiteIdFailure() {
        boolean result = testFindBy(null, BY_COURTSITE_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindVipMultiSiteSuccess() {
        boolean result = testFindBy(getDummyDao(), BY_VIPMULTISITE_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindVipMultiSiteFailure() {
        boolean result = testFindBy(null, BY_VIPMULTISITE_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindVipMNoSiteSuccess() {
        boolean result = testFindBy(getDummyDao(), BY_VIPNOSITE_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindVipMNoSiteFailure() {
        boolean result = testFindBy(null, BY_VIPNOSITE_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByDisplayIdSuccess() {
        boolean result = testFindBy(getDummyDao(), BY_DISPLAY_ID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByDisplayIdFailure() {
        boolean result = testFindBy(null, BY_DISPLAY_ID);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindBy(XhbCourtRoomDao dao, String queryBy) {
        List<XhbCourtRoomDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbCourtRoomDao> result = new ArrayList<>();
        if (BY_COURTSITE_ID.equals(queryBy)) {
            result = getClassUnderTest().findByCourtSiteId(getDummyDao().getCourtSiteId());
        } else if (BY_DISPLAY_ID.equals(queryBy)) {
            result = getClassUnderTest().findByDisplayId(getDummyDao().getCourtSiteId());
        } else if (BY_VIPMULTISITE_ID.equals(queryBy)) {
            final Integer courtId = getDummyId();
            result = getClassUnderTest().findVipMultiSite(courtId);
        } else if (BY_VIPNOSITE_ID.equals(queryBy)) {
            final Integer courtId = getDummyId();
            result = getClassUnderTest().findVipMNoSite(courtId);
        }
        assertNotNull(result, NOTNULL);
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Override
    protected XhbCourtRoomDao getDummyDao() {
        return DummyCourtUtil.getXhbCourtRoomDao();
    }

}
