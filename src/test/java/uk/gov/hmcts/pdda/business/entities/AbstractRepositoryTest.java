package uk.gov.hmcts.pdda.business.entities;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;

public abstract class AbstractRepositoryTest<T extends AbstractDao> {

    protected static final String NOT_TRUE = "Result is Not True";
    protected static final String NOTSAMERESULT = "Result is not Same";
    protected static final String NULLRESULT = "Result is Null";
    protected static final String NOTNULLRESULT = "Result is not Null";
    protected static final String NOTNULL = "Result is not Null";
    protected static final String SAME = "Result is Same";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    protected Query mockQuery;

    protected abstract AbstractRepository<T> getClassUnderTest();

    protected abstract EntityManager getEntityManager();

    protected abstract T getDummyDao();

    protected boolean runFindByIdTest(T dao) {
        Optional<T> result = getClassUnderTest().findByIdSafe(getDummyId());
        assertNotNull(result, NULLRESULT);
        if (dao != null) {
            assertSame(dao, result.get(), NOTSAMERESULT);
        } else {
            assertSame(Optional.empty(), result, NOTSAMERESULT);
        }
        return true;
    }


    @Test
    void testFindByIdFailure() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            boolean result = testFindById(null);
            assertTrue(result, NOT_TRUE);
        }
    }

    protected boolean testFindById(T dao) {
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyId()))
            .thenReturn(dao);
        Optional<T> result = getClassUnderTest().findByIdSafe(getDummyId());
        assertNotNull(result, NULLRESULT);
        if (dao != null) {
            assertSame(dao, result.get(), NOTSAMERESULT);
        } else {
            assertSame(Optional.empty(), result, NOTSAMERESULT);
        }
        return true;
    }

    @Test
    void testFindAllSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = mockStatic(EntityManagerUtil.class)) {
            // Step 1: Mock static method to return mock EntityManager
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            // Step 2: Prepare dummy DAO and mock query
            T dummyDao = getDummyDao();
            List<T> daoList = new ArrayList<>();
            daoList.add(dummyDao);

            // Step 3: Setup mockEntityManager.createQuery to return mockQuery
            Mockito.when(mockEntityManager.createQuery(Mockito.anyString())).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(daoList);

            // Step 4: Call test logic
            boolean result = testFindAll(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }


    @Test
    void testFindAllFailure() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = mockStatic(EntityManagerUtil.class)) {
            // Step 1: Mock static method to return mock EntityManager
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            // Step 2: Setup mockEntityManager.createQuery to return mockQuery
            Mockito.when(mockEntityManager.createQuery(Mockito.anyString())).thenReturn(mockQuery);

            // Step 3: Mock empty result list
            Mockito.when(mockQuery.getResultList()).thenReturn(new ArrayList<>());

            // Step 4: Call test logic with null DAO
            boolean result = testFindAll(null);
            assertTrue(result, NOT_TRUE);
        }
    }



    protected boolean testFindAll(T dao) {
        List<T> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<T> result = getClassUnderTest().findAllSafe();
        assertNotNull(result, NULLRESULT);
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    protected Integer getDummyId() {
        return -99;
    }

    protected Long getDummyLongId() {
        return Long.valueOf(getDummyId());
    }
}
