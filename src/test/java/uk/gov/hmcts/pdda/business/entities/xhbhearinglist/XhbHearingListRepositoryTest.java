package uk.gov.hmcts.pdda.business.entities.xhbhearinglist;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbHearingListRepositoryTest extends AbstractRepositoryTest<XhbHearingListDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbHearingListRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbHearingListRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbHearingListRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByCourtIdAndDateSuccess() {
        boolean result = testFindByCourtIdAndDate(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtIdAndDateFailure() {
        boolean result = testFindByCourtIdAndDate(null);
        assertTrue(result, NOT_TRUE);

    }

    private boolean testFindByCourtIdAndDate(XhbHearingListDao dao) {
        List<XhbHearingListDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbHearingListDao> result = getClassUnderTest()
            .findByCourtIdAndDate(getDummyDao().getCourtId(), LocalDateTime.now());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Test
    void testFindByCourtIdStatusAndDate() {
        List<XhbHearingListDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbHearingListDao dao = getDummyDao();
        Optional<XhbHearingListDao> result = classUnderTest
            .findByCourtIdStatusAndDate(dao.getCourtId(), dao.getStatus(), dao.getStartDate());
        assertNotNull(result, NOTNULL);
    }

    @Override
    protected XhbHearingListDao getDummyDao() {
        XhbHearingListDao result = DummyHearingUtil.getXhbHearingListDao();
        assertNotNull(result.getPrimaryKey(), NOTNULL);
        return new XhbHearingListDao(result);
    }

}
