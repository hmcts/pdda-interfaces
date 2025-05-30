package uk.gov.hmcts.pdda.business.entities.xhbcppformatting;

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
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCppFormattingRepositoryTest extends AbstractRepositoryTest<XhbCppFormattingDao> {

    private static final Integer BYDOCTYPE = 1;
    private static final Integer LATESTDOCBYCOURTID = 2;

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCppFormattingRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCppFormattingRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCppFormattingRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCppFormattingDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCppFormattingDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindLatestByCourtDateInDocSuccess() {
        boolean result = testFindLatestByCourtDateInDoc(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindLatestByCourtDateInDocFailure() {
        boolean result = testFindLatestByCourtDateInDoc(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindLatestByCourtDateInDoc(XhbCppFormattingDao dao) {
        List<XhbCppFormattingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        testFindLatestByCourtDateInDoc(dao, list);
        if (dao == null) {
            // Now test a null list
            testFindLatestByCourtDateInDoc(dao, null);
        }
        return true;
    }

    private boolean testFindLatestByCourtDateInDoc(XhbCppFormattingDao dao, List<XhbCppFormattingDao> list) {
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        XhbCppFormattingDao result = getClassUnderTest().findLatestByCourtDateInDoc(getDummyDao().getCourtId(),
            getDummyDao().getDocumentType(), getDummyDao().getDateIn());
        if (dao != null) {
            assertNotNull(result, NOTNULLRESULT);
            assertSame(dao, result, NOTSAMERESULT);
        } else {
            assertNull(result, "Result is not Null");
        }
        return true;
    }

    @Test
    void testFindAllNewByDocTypeSuccess() {
        boolean result = testFind(getDummyDao(), BYDOCTYPE);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindAllNewByDocTypeFailure() {
        boolean result = testFind(null, BYDOCTYPE);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetLatestDocumentByCourtIdAndTypeSuccess() {
        boolean result = testFind(getDummyDao(), LATESTDOCBYCOURTID);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetLatestDocumentByCourtIdAndTypeFailure() {
        boolean result = testFind(null, LATESTDOCBYCOURTID);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFind(XhbCppFormattingDao dao, Integer whichTest) {
        List<XhbCppFormattingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        if (BYDOCTYPE.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            List<XhbCppFormattingDao> resultList = getClassUnderTest()
                .findAllNewByDocType(getDummyDao().getDocumentType(), getDummyDao().getCreationDate());
            assertNotNull(resultList, NOTNULLRESULT);
            if (dao != null) {
                assertSame(dao, resultList.get(0), NOTSAMERESULT);
            } else {
                assertSame(0, resultList.size(), NOTSAMERESULT);
            }
        } else if (LATESTDOCBYCOURTID.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            XhbCppFormattingDao result = getClassUnderTest().getLatestDocumentByCourtIdAndType(
                getDummyDao().getCourtId(), getDummyDao().getDocumentType(), getDummyDao().getCreationDate());
            if (dao != null) {
                assertNotNull(result, NOTNULLRESULT);
                assertSame(dao, result, NOTSAMERESULT);
            } else {
                assertNull(result, NOTNULLRESULT);
            }
        }
        return true;
    }

    @Override
    protected XhbCppFormattingDao getDummyDao() {
        return DummyFormattingUtil.getXhbCppFormattingDao();
    }

}
