package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtelUtil;
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
class XhbCourtelListRepositoryTest extends AbstractRepositoryTest<XhbCourtelListDao> {

    private static final String QUERY_XMLDOCUMENTID = "xmlDocumentId";
    private static final String QUERY_FINDCOURTELLIST = "findCourtelList";
    private static final Integer DUMMY_COURTEL_LIST_AMOUNT = 5;
    private static final Integer DUMMY_INTERVAL = 0;
    private static final Integer DUMMY_COURTEL_MAX_RETRY = 5;
    
    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtelListRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtelListRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCourtelListRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected boolean testfindById(XhbCourtelListDao dao) {
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyLongId())).thenReturn(dao);
        Optional<XhbCourtelListDao> result =
            (Optional<XhbCourtelListDao>) getClassUnderTest().findById(getDummyLongId());
        assertNotNull(result, NOTNULL);
        if (dao != null) {
            assertSame(dao, result.get(), SAME);
        } else {
            assertSame(Optional.empty(), result, SAME);
        }
        return true;
    }

    @Override
    protected XhbCourtelListDao getDummyDao() {
        return DummyCourtelUtil.getXhbCourtelListDao();
    }

    @Test
    void testFindByXmlDocumentId() {
        boolean result = testFindBy(QUERY_XMLDOCUMENTID, getDummyDao());
        assertTrue(result, NOT_TRUE);
        result = testFindBy(QUERY_XMLDOCUMENTID, null);
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testFindCourtelList() {
        boolean result = testFindBy(QUERY_FINDCOURTELLIST, getDummyDao());
        assertTrue(result, NOT_TRUE);
        result = testFindBy(QUERY_FINDCOURTELLIST, null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindBy(String query, XhbCourtelListDao dao) {
        List<XhbCourtelListDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        Optional<XhbCourtelListDao> result = Optional.empty();
        if (QUERY_XMLDOCUMENTID.equals(query)) {
            Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyId())).thenReturn(dao);
            result = (Optional<XhbCourtelListDao>) getClassUnderTest().findByXmlDocumentId(getDummyId());
        } else if (QUERY_FINDCOURTELLIST.equals(query)) {
            Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyId()))
                .thenReturn(dao);
            List<XhbCourtelListDao> resultList =
                getClassUnderTest().findCourtelList(DUMMY_COURTEL_MAX_RETRY, DUMMY_INTERVAL,
                    LocalDateTime.now().plusMinutes(DUMMY_COURTEL_LIST_AMOUNT));
            assertNotNull(resultList, "Result is Null");
            if (dao != null) {
                assertSame(dao, resultList.get(0), SAME);
            } else {
                assertSame(0, resultList.size(), SAME);
            }
            return true;
        }
        assertNotNull(result, NOTNULL);
        if (dao != null) {
            assertSame(dao, result.get(), SAME);
        } else {
            assertSame(Optional.empty(), result, SAME);
        }
        return true;
    }
}
