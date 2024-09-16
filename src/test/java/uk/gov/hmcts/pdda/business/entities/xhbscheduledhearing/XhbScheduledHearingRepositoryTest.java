package uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing;


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
class XhbScheduledHearingRepositoryTest extends AbstractRepositoryTest<XhbScheduledHearingDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbScheduledHearingRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbScheduledHearingRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbScheduledHearingRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindActiveCasesInRoomSuccess() {
        boolean result = testFindActiveCasesInRoom(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindActiveCasesInRoomFailure() {
        boolean result = testFindActiveCasesInRoom(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindActiveCasesInRoom(XhbScheduledHearingDao dao) {
        List<XhbScheduledHearingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Integer listId = -99;
        Integer courtRoomId = -98;
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbScheduledHearingDao> result = getClassUnderTest()
            .findActiveCasesInRoom(listId, courtRoomId, getDummyDao().getScheduledHearingId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Test
    void testFindBySittingIdSuccess() {
        boolean result = testFindBySittingId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindBySittingIdFailure() {
        boolean result = testFindBySittingId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindBySittingId(XhbScheduledHearingDao dao) {
        List<XhbScheduledHearingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbScheduledHearingDao> result =
            getClassUnderTest().findBySittingId(getDummyDao().getSittingId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Override
    protected XhbScheduledHearingDao getDummyDao() {
        Integer scheduledHearingId = -1;
        Integer sequenceNo = -1;
        LocalDateTime notBeforeTime = LocalDateTime.now();
        LocalDateTime originalTime = LocalDateTime.now();
        String listingNote = "listingNote";
        Integer hearingProgress = -1;
        Integer sittingId = -1;
        Integer hearingId = -1;
        String caseActive = "isCaseActive";
        String movedFrom = "movedFrom";
        Integer movedFromCourtRoomId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbScheduledHearingDao result = new XhbScheduledHearingDao();
        result.setScheduledHearingId(scheduledHearingId);
        result.setSequenceNo(sequenceNo);
        result.setNotBeforeTime(notBeforeTime);
        result.setOriginalTime(originalTime);
        result.setListingNote(listingNote);
        result.setHearingProgress(hearingProgress);
        result.setSittingId(sittingId);
        result.setHearingId(hearingId);
        result.setIsCaseActive(caseActive);
        result.setMovedFrom(movedFrom);
        result.setMovedFromCourtRoomId(movedFromCourtRoomId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        scheduledHearingId = result.getPrimaryKey();
        assertNotNull(scheduledHearingId, NOTNULL);
        result.setXhbCrLiveDisplays(result.getXhbCrLiveDisplays());
        return new XhbScheduledHearingDao(result);
    }

}
