package uk.gov.hmcts.pdda.business.entities.xhbsitting;

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
class XhbSittingRepositoryTest extends AbstractRepositoryTest<XhbSittingDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbSittingRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbSittingRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbSittingRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByNonFloatingHearingListSuccess() {
        boolean result = testFindByNonFloatingHearingList(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByNonFloatingHearingListFailure() {
        boolean result = testFindByNonFloatingHearingList(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByNonFloatingHearingList(XhbSittingDao dao) {
        List<XhbSittingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbSittingDao> result =
            getClassUnderTest().findByNonFloatingHearingList(getDummyDao().getListId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Test
    void testFindByListIdSuccess() {
        boolean result = testFindByListId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByListIdFailure() {
        boolean result = testFindByListId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByListId(XhbSittingDao dao) {
        List<XhbSittingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbSittingDao> result = getClassUnderTest().findByListId(getDummyDao().getListId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Override
    protected XhbSittingDao getDummyDao() {
        Integer sittingId = -1;
        Integer sittingSequenceNo = -1;
        String sittingJudge = "isSittingJudge";
        LocalDateTime sittingTime = LocalDateTime.now();
        String sittingNote = "sittingNote";
        Integer refJustice1Id = -1;
        Integer refJustice2Id = -1;
        Integer refJustice3Id = -1;
        Integer refJustice4Id = -1;
        String floating = "isFloating";
        Integer listId = -1;
        Integer refJudgeId = -1;
        Integer courtRoomId = -1;
        Integer courtSiteId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbSittingDao result = new XhbSittingDao();
        result.setSittingId(sittingId);
        result.setSittingSequenceNo(sittingSequenceNo);
        result.setIsSittingJudge(sittingJudge);
        result.setSittingTime(sittingTime);
        result.setSittingNote(sittingNote);
        result.setRefJustice1Id(refJustice1Id);
        result.setRefJustice2Id(refJustice2Id);
        result.setRefJustice3Id(refJustice3Id);
        result.setRefJustice4Id(refJustice4Id);
        result.setIsFloating(floating);
        result.setListId(listId);
        result.setRefJudgeId(refJudgeId);
        result.setCourtRoomId(courtRoomId);
        result.setCourtSiteId(courtSiteId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);

        sittingId = result.getPrimaryKey();
        assertNotNull(sittingId, NOTNULL);
        result.setXhbCourtSite(result.getXhbCourtSite());
        return new XhbSittingDao(result);
    }

}
