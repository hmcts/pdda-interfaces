package uk.gov.hmcts.pdda.business.entities.xhbpddamessage;

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
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbPddaMessageRepositoryTest extends AbstractRepositoryTest<XhbPddaMessageDao> {

    private static final Integer BYCPDOCUMENTNAME = 3;
    private static final Integer BYLIGHTHOUSE = 4;
    private static final Integer UNRESPONDED = 5;

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbPddaMessageRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbPddaMessageRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbPddaMessageRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbPddaMessageDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbPddaMessageDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByCpDocumentNameSuccess() {
        boolean result = testFind(getDummyDao(), BYCPDOCUMENTNAME);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCpDocumentNameFailure() {
        boolean result = testFind(null, BYCPDOCUMENTNAME);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByLighthouseSuccess() {
        boolean result = testFind(getDummyDao(), BYLIGHTHOUSE);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByLighthouseFailure() {
        boolean result = testFind(null, BYLIGHTHOUSE);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindUnrespondedCpMessagesSuccess() {
        boolean result = testFind(getDummyDao(), UNRESPONDED);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindUnrespondedCpMessagesFailure() {
        boolean result = testFind(null, UNRESPONDED);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFind(XhbPddaMessageDao dao, Integer whichTest) {
        List<XhbPddaMessageDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        List<XhbPddaMessageDao> result = null;
        if (BYLIGHTHOUSE.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            result = getClassUnderTest().findByLighthouse();
        } else if (UNRESPONDED.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            result = getClassUnderTest().findUnrespondedCpMessages();
        } else if (BYCPDOCUMENTNAME.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            result =
                getClassUnderTest().findByCpDocumentName(getDummyDao().getCpDocumentName());
        }
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Override
    protected XhbPddaMessageDao getDummyDao() {
        return DummyPdNotifierUtil.getXhbPddaMessageDao();
    }

}
