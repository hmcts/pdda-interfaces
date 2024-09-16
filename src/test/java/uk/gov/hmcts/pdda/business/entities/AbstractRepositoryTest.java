package uk.gov.hmcts.pdda.business.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

public abstract class AbstractRepositoryTest<T extends AbstractDao> {

    protected static final String NOT_TRUE = "Result is Not True";
    protected static final String SAME = "Result is not Same";
    protected static final String NOTNULL = "Result is Null";

    @Mock
    protected Query mockQuery;

    protected abstract AbstractRepository<T> getClassUnderTest();

    protected abstract EntityManager getEntityManager();

    protected abstract T getDummyDao();

    @Test
    void testfindByIdSuccess() {
        boolean result = testfindById(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testfindByIdFailure() {
        boolean result = testfindById(null);
        assertTrue(result, NOT_TRUE);
    }

    protected boolean testfindById(T dao) {
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyId()))
            .thenReturn(dao);
        Optional<T> result = getClassUnderTest().findById(getDummyId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(), SAME);
        } else {
            assertSame(Optional.empty(), result, SAME);
        }
        return true;
    }

    @Test
    void testfindAllSuccess() {
        boolean result = testfindAll(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testfindAllFailure() {
        boolean result = testfindAll(null);
        assertTrue(result, NOT_TRUE);
    }

    protected boolean testfindAll(T dao) {
        List<T> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<T> result = getClassUnderTest().findAll();
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
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
