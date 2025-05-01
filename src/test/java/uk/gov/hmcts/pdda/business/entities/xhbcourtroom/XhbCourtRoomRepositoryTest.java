package uk.gov.hmcts.pdda.business.entities.xhbcourtroom;

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
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCourtRoomRepositoryTest extends AbstractRepositoryTest<XhbCourtRoomDao> {

    private static final String BY_COURTSITE_ID = "ByCourtSiteId";
    private static final String BY_DISPLAY_ID = "ByDisplay";
    private static final String BY_VIPMULTISITE_ID = "ByVipMultiSite";
    private static final String BY_VIPNOSITE_ID = "ByVipNoSite";
    private static final String BY_COURTROOMNO = "ByCourtRoomNo";

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
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCourtRoomRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCourtRoomDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCourtRoomDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
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

        // mock the entity manager behavior
        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        try (MockedStatic<EntityManagerUtil> mockedStatic = mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            List<XhbCourtRoomDao> resultList = new ArrayList<>();
            if (BY_COURTSITE_ID.equals(queryBy)) {
                resultList =
                    getClassUnderTest().findByCourtSiteIdSafe(getDummyDao().getCourtSiteId());
            } else if (BY_DISPLAY_ID.equals(queryBy)) {
                resultList =
                    getClassUnderTest().findByDisplayIdSafe(getDummyDao().getCourtSiteId());
            } else if (BY_VIPMULTISITE_ID.equals(queryBy)) {
                final Integer courtId = getDummyId();
                resultList = getClassUnderTest().findVipMultiSite(courtId);
            } else if (BY_VIPNOSITE_ID.equals(queryBy)) {
                final Integer courtId = getDummyId();
                resultList = getClassUnderTest().findVipMNoSite(courtId);
            } else if (BY_COURTROOMNO.equals(queryBy)) {
                Mockito.when(mockQuery.getSingleResult()).thenReturn(dao);
                Optional<XhbCourtRoomDao> result = getClassUnderTest().findByCourtRoomNo(
                    getDummyDao().getCourtSiteId(), getDummyDao().getCrestCourtRoomNo());
                assertNotNull(result, "Result is Null");
                if (dao != null) {
                    assertSame(dao, result.get(), NOTSAMERESULT);
                } else {
                    assertSame(Optional.empty(), result, NOTSAMERESULT);
                }
                return true;
            }

            assertNotNull(resultList, NOTNULLRESULT);
            if (dao != null) {
                assertSame(dao, resultList.get(0), NOTSAMERESULT);
            } else {
                assertSame(0, resultList.size(), NOTSAMERESULT);
            }
            return true;
        }
    }

    @Override
    protected XhbCourtRoomDao getDummyDao() {
        return DummyCourtUtil.getXhbCourtRoomDao();
    }

}
