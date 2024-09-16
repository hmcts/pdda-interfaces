package uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;



@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbSchedHearingAttendeeRepositoryTest extends AbstractRepositoryTest<XhbSchedHearingAttendeeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbSchedHearingAttendeeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbSchedHearingAttendeeRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbSchedHearingAttendeeRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbSchedHearingAttendeeDao getDummyDao() {
        Integer shAttendeeId = getDummyId();
        String attendeeType = "";
        Integer scheduledHearingId = -1;
        Integer shStaffId = -1;
        Integer shJusticeId = -1;
        Integer refJudgeId = -1;
        Integer refCourtReporterId = -1;
        Integer refJusticeId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbSchedHearingAttendeeDao result = new XhbSchedHearingAttendeeDao();
        result.setShAttendeeId(shAttendeeId);
        result.setAttendeeType(attendeeType);
        result.setScheduledHearingId(scheduledHearingId);
        result.setShStaffId(shStaffId);
        result.setShJusticeId(shJusticeId);
        result.setRefJudgeId(refJudgeId);
        result.setRefCourtReporterId(refCourtReporterId);
        result.setRefJusticeId(refJusticeId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        shAttendeeId = result.getPrimaryKey();
        assertNotNull(shAttendeeId, NOTNULL);
        return new XhbSchedHearingAttendeeDao(result);
    }

}
