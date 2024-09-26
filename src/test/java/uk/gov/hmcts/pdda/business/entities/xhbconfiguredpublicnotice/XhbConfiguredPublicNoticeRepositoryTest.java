package uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class XhbConfiguredPublicNoticeRepositoryTest extends AbstractRepositoryTest<XhbConfiguredPublicNoticeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbConfiguredPublicNoticeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbConfiguredPublicNoticeRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbConfiguredPublicNoticeRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testfindByDefinitivePnCourtRoomValueSuccess() {
        boolean result = testfindByDefinitivePnCourtRoomValue(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testfindByDefinitivePnCourtRoomValueFailure() {
        boolean result = testfindByDefinitivePnCourtRoomValue(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testfindByDefinitivePnCourtRoomValue(XhbConfiguredPublicNoticeDao dao) {
        List<XhbConfiguredPublicNoticeDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbConfiguredPublicNoticeDao> result = getClassUnderTest()
            .findByDefinitivePnCourtRoomValue(getDummyDao().getCourtRoomId(), getDummyDao().getPublicNoticeId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Test
    void testFindActiveCourtRoomNoticesSuccess() {
        boolean result = testFindActiveCourtRoomNotices(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindActiveCourtRoomNoticesFailure() {
        boolean result = testFindActiveCourtRoomNotices(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindActiveCourtRoomNotices(XhbConfiguredPublicNoticeDao dao) {
        List<XhbConfiguredPublicNoticeDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbConfiguredPublicNoticeDao> result = getClassUnderTest()
            .findActiveCourtRoomNotices(getDummyDao().getCourtRoomId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Override
    protected XhbConfiguredPublicNoticeDao getDummyDao() {
        Integer configuredPublicNoticeId = getDummyId();
        String activeString = "Y";
        Integer courtRoomId = getDummyId();
        Integer publicNoticeId = getDummyId();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;

        XhbConfiguredPublicNoticeDao result = new XhbConfiguredPublicNoticeDao(configuredPublicNoticeId, activeString,
            courtRoomId, publicNoticeId, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        configuredPublicNoticeId = result.getPrimaryKey();
        assertNotNull(configuredPublicNoticeId, NOTNULL);
        result.setXhbPublicNotice(result.getXhbPublicNotice());
        return new XhbConfiguredPublicNoticeDao(result);
    }

}
