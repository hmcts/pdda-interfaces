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
    private static final Integer DUMMY_INTERVAL = 5;
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
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyLongId()))
            .thenReturn(dao);
        Optional<XhbCourtelListDao> result = getClassUnderTest().findById(getDummyLongId());
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
        List<XhbCourtelListDao> list = new ArrayList<>();
        // test empty list
        boolean result = testFindBy(QUERY_XMLDOCUMENTID, list);
        assertTrue(result, NOT_TRUE);
        // test populated list
        list.add(getDummyDao());
        result = testFindBy(QUERY_XMLDOCUMENTID, list);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindCourtelList() {
        List<XhbCourtelListDao> list = new ArrayList<>();
        // test empty list
        boolean result = testFindBy(QUERY_FINDCOURTELLIST, list);
        assertTrue(result, NOT_TRUE);
        // test populated list
        XhbCourtelListDao now = getDummyDao();
        now.setLastAttemptDatetime(LocalDateTime.now());
        list.add(now);
        XhbCourtelListDao sixMinutesAgo = getDummyDao();
        sixMinutesAgo.setLastAttemptDatetime(LocalDateTime.now().minusMinutes(6));
        list.add(sixMinutesAgo);
        XhbCourtelListDao never = getDummyDao();
        list.add(never);
        never.setLastAttemptDatetime(null);
        result = testFindBy(QUERY_FINDCOURTELLIST, list);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindBy(String query, List<XhbCourtelListDao> list) {
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        Optional<XhbCourtelListDao> result = Optional.empty();
        if (QUERY_XMLDOCUMENTID.equals(query)) {
            XhbCourtelListDao dao = list.isEmpty() ? null : list.get(0);
            Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyId()))
                .thenReturn(dao);
            result = getClassUnderTest().findByXmlDocumentId(getDummyId());
        } else if (QUERY_FINDCOURTELLIST.equals(query)) {
            XhbCourtelListDao dao = list.isEmpty() ? null : list.get(0);
            Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyId()))
                .thenReturn(dao);
            List<XhbCourtelListDao> resultList = getClassUnderTest().findCourtelList(
                DUMMY_COURTEL_MAX_RETRY, DUMMY_INTERVAL, DUMMY_COURTEL_LIST_AMOUNT);
            assertNotNull(resultList, "Result is Null");
            if (list.isEmpty()) {
                assertSame(0, resultList.size(), SAME);
            } else {
                // Should only return 'never' and 'sixMinutesAgo'
                assertSame(list.size() - 1, resultList.size(), SAME);
            }
            return true;
        }
        assertNotNull(result, NOTNULL);
        if (list.isEmpty()) {
            assertSame(Optional.empty(), result, SAME);
        } else {
            assertSame(list.get(0), result.get(), SAME);
        }
        return true;
    }
}
