package uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation;

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
class XhbDisplayLocationRepositoryTest extends AbstractRepositoryTest<XhbDisplayLocationDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDisplayLocationRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDisplayLocationRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbDisplayLocationRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbDisplayLocationDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbDisplayLocationDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByVipCourtSiteSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            boolean result = testFindByVipCourtSite(getDummyDao());
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByVipCourtSiteFailure() {
        boolean result = testFindByVipCourtSite(null);
        assertTrue(result, NOT_TRUE);
    }

    @SuppressWarnings("PMD.UseCollectionIsEmpty")
    private boolean testFindByVipCourtSite(XhbDisplayLocationDao dao) {
        List<XhbDisplayLocationDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbDisplayLocationDao> result =
            getClassUnderTest().findByVipCourtSiteSafe(getDummyDao().getCourtSiteId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertTrue(result.size() > 0, "Expected at least one result");
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertTrue(result.isEmpty(), "Expected empty result list");
        }

        return true;
    }

    @Test
    void testFindByCourtSiteSuccess() {
        boolean result = testFindByCourtSite(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtSiteFailure() {
        boolean result = testFindByCourtSite(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByCourtSite(XhbDisplayLocationDao dao) {
        List<XhbDisplayLocationDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbDisplayLocationDao> result =
            getClassUnderTest().findByCourtSite(getDummyDao().getCourtSiteId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Override
    protected XhbDisplayLocationDao getDummyDao() {
        Integer displayLocationId = getDummyId();
        String descriptionCode = "descriptionCode";
        Integer courtSiteId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbDisplayLocationDao result = new XhbDisplayLocationDao(displayLocationId, descriptionCode, courtSiteId,
            lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        displayLocationId = result.getPrimaryKey();
        assertNotNull(displayLocationId, NOTNULLRESULT);
        return new XhbDisplayLocationDao(result);
    }

}
