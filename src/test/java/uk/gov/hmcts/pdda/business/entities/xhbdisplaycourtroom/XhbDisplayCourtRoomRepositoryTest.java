package uk.gov.hmcts.pdda.business.entities.xhbdisplaycourtroom;

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
class XhbDisplayCourtRoomRepositoryTest extends AbstractRepositoryTest<XhbDisplayCourtRoomDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDisplayCourtRoomRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDisplayCourtRoomRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbDisplayCourtRoomRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbDisplayCourtRoomDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbDisplayCourtRoomDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByDisplayIdSuccess() {
        boolean result = testFindByDisplayId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByDisplayIdFailure() {
        boolean result = testFindByDisplayId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByDisplayId(XhbDisplayCourtRoomDao dao) {
        List<XhbDisplayCourtRoomDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbDisplayCourtRoomDao> result =
            getClassUnderTest().findByDisplayId(getDummyDao().getDisplayId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbDisplayCourtRoomDao getDummyDao() {
        Integer displayId = getDummyId();
        Integer courtRoomId = getDummyId();
        XhbDisplayCourtRoomDao result = new XhbDisplayCourtRoomDao(displayId, courtRoomId);
        return new XhbDisplayCourtRoomDao(result);
    }

}
