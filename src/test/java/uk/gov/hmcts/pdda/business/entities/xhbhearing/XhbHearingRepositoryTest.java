package uk.gov.hmcts.pdda.business.entities.xhbhearing;

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
class XhbHearingRepositoryTest extends AbstractRepositoryTest<XhbHearingDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbHearingRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbHearingRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbHearingRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByCaseIdSuccess() {
        boolean result = testFindByCaseId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCaseIdFailure() {
        boolean result = testFindByCaseId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByCaseId(XhbHearingDao dao) {
        List<XhbHearingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbHearingDao> result = getClassUnderTest().findByCaseId(getDummyDao().getCaseId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbHearingDao getDummyDao() {
        Integer hearingId = getDummyId();
        Integer caseId = -1;
        Integer refHearingTypeId = -1;
        Integer courtId = -1;
        String mpHearingType = "mpHearingType";
        Double lastCalculatedDuration = Double.valueOf(-1);
        LocalDateTime hearingStartDate = LocalDateTime.now();
        LocalDateTime hearingEndDate = LocalDateTime.now();
        Integer linkedHearingId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbHearingDao result = new XhbHearingDao();
        result.setHearingId(hearingId);
        result.setCaseId(caseId);
        result.setRefHearingTypeId(refHearingTypeId);
        result.setCourtId(courtId);
        result.setMpHearingType(mpHearingType);
        result.setLastCalculatedDuration(lastCalculatedDuration);
        result.setHearingStartDate(hearingStartDate);
        result.setHearingEndDate(hearingEndDate);
        result.setLinkedHearingId(linkedHearingId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        hearingId = result.getPrimaryKey();
        assertNotNull(hearingId, NOTNULL);
        return new XhbHearingDao(result);
    }

}
