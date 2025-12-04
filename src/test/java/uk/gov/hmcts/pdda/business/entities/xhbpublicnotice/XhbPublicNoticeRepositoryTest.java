package uk.gov.hmcts.pdda.business.entities.xhbpublicnotice;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class XhbPublicNoticeRepositoryTest extends AbstractRepositoryTest<XhbPublicNoticeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbPublicNoticeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbPublicNoticeRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbPublicNoticeRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbPublicNoticeDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbPublicNoticeDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected XhbPublicNoticeDao getDummyDao() {
        Integer publicNoticeId = getDummyId();
        String publicNoticeDesc = "publicNoticeDesc";
        Integer courtId = -1;
        Integer definitivePnId = getDummyId();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbPublicNoticeDao result = new XhbPublicNoticeDao(publicNoticeId, publicNoticeDesc, courtId, lastUpdateDate,
            creationDate, lastUpdatedBy, createdBy, version, definitivePnId);
        publicNoticeId = result.getPrimaryKey();
        assertNotNull(publicNoticeId, NOTNULLRESULT);
        result.setXhbDefinitivePublicNotice(result.getXhbDefinitivePublicNotice());
        return new XhbPublicNoticeDao(result);
    }
    
    
    @Test
    void testFindByCourtIdAndDefPublicNoticeIdSafeSuccess() {
        // Mock EntityManagerUtil.getEntityManager()
        try (MockedStatic<EntityManagerUtil> mockedStatic =
                 Mockito.mockStatic(EntityManagerUtil.class)) {

            EntityManager em = Mockito.mock(EntityManager.class, Mockito.RETURNS_DEEP_STUBS);
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(em);

            Query mockQuery = Mockito.mock(Query.class);

            // Mock createNamedQuery -> query
            Mockito.when(em.createNamedQuery(Mockito.anyString())).thenReturn(mockQuery);

            // Mock query.setParameter chaining
            Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any()))
                   .thenReturn(mockQuery);

            // Mock getResultList() to be non-empty so getSingleResult() is called
            Mockito.when(mockQuery.getResultList()).thenReturn(java.util.Collections.singletonList(new Object()));

            XhbPublicNoticeDao dao = getDummyDao();
            Mockito.when(mockQuery.getSingleResult()).thenReturn(dao);

            Optional<XhbPublicNoticeDao> result =
                classUnderTest.findByCourtIdAndDefPublicNoticeIdSafe(1, 2);

            assertTrue(result.isPresent(), "Expected Optional to contain value");
        }
    }

    @Test
    void testFindByCourtIdAndDefPublicNoticeIdSafeException() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
                 Mockito.mockStatic(EntityManagerUtil.class)) {

            // Force EntityManagerUtil to throw an exception
            mockedStatic.when(EntityManagerUtil::getEntityManager)
                .thenThrow(new RuntimeException("Boom"));

            Optional<XhbPublicNoticeDao> result =
                classUnderTest.findByCourtIdAndDefPublicNoticeIdSafe(1, 2);

            assertTrue(result.isEmpty(), "Expected Optional.empty()");
        }
    }
    
    @Test
    void testFindByCourtIdAndDefPublicNoticeId_emptyList() {
        // The non-safe method uses the repository's injected EntityManager, so just stub createNamedQuery
        Query mockQuery = Mockito.mock(Query.class);
        Mockito.when(mockEntityManager.createNamedQuery(Mockito.anyString())).thenReturn(mockQuery);

        // Ensure parameter chaining returns the mockQuery
        Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(mockQuery);

        // Empty result list -> should return Optional.empty() and not call getSingleResult()
        Mockito.when(mockQuery.getResultList()).thenReturn(java.util.Collections.emptyList());

        Optional<XhbPublicNoticeDao> result = classUnderTest.findByCourtIdAndDefPublicNoticeId(10, 20);

        assertTrue(result.isEmpty(), "Expected empty result when query returns empty list");
        Mockito.verify(mockQuery, Mockito.never()).getSingleResult();
    }

    @Test
    void testFindByCourtIdAndDefPublicNoticeIdSafe_emptyList() {
        // Use static mocking to supply an EM via EntityManagerUtil
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            EntityManager em = Mockito.mock(EntityManager.class, Mockito.RETURNS_DEEP_STUBS);
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(em);

            Query mockQuery = Mockito.mock(Query.class);
            Mockito.when(em.createNamedQuery(Mockito.anyString())).thenReturn(mockQuery);
            Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(mockQuery);

            // empty list
            Mockito.when(mockQuery.getResultList()).thenReturn(java.util.Collections.emptyList());

            Optional<XhbPublicNoticeDao> result =
                classUnderTest.findByCourtIdAndDefPublicNoticeIdSafe(11, 22);

            assertTrue(result.isEmpty(), "Expected Optional.empty() when underlying query returned empty");
            Mockito.verify(mockQuery, Mockito.never()).getSingleResult();
        }
    }

    @Test
    void testFindByCourtIdAndDefPublicNoticeIdSafe_nonEmptyList_callsGetSingleResult() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = Mockito.mockStatic(EntityManagerUtil.class)) {
            EntityManager em = Mockito.mock(EntityManager.class, Mockito.RETURNS_DEEP_STUBS);
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(em);

            Query mockQuery = Mockito.mock(Query.class);
            Mockito.when(em.createNamedQuery(Mockito.anyString())).thenReturn(mockQuery);
            Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(mockQuery);

            // non-empty list -> repository will call getSingleResult()
            Mockito.when(mockQuery.getResultList()).thenReturn(java.util.Collections.singletonList(new Object()));

            XhbPublicNoticeDao dao = getDummyDao();
            Mockito.when(mockQuery.getSingleResult()).thenReturn(dao);

            Optional<XhbPublicNoticeDao> result =
                classUnderTest.findByCourtIdAndDefPublicNoticeIdSafe(12, 24);

            assertTrue(result.isPresent(),
                "Expected Optional to contain dao when query.getSingleResult() returns value");
            Mockito.verify(mockQuery, Mockito.times(1)).getSingleResult();
        }
    }



}
