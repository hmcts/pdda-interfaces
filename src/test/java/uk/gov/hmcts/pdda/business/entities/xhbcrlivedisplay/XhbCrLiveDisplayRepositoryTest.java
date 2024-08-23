package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;

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
class XhbCrLiveDisplayRepositoryTest extends AbstractRepositoryTest<XhbCrLiveDisplayDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCrLiveDisplayRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCrLiveDisplayRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCrLiveDisplayRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbCrLiveDisplayDao getDummyDao() {
        Integer crLiveDisplayId = getDummyId();
        Integer courtRoomId = Integer.valueOf(-1);
        Integer scheduledHearingId = Integer.valueOf(-1);
        LocalDateTime timeStatusSet = LocalDateTime.now();
        String status = "status";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = Integer.valueOf(3);
        XhbCrLiveDisplayDao result = new XhbCrLiveDisplayDao();
        result.setCrLiveDisplayId(crLiveDisplayId);
        result.setCourtRoomId(courtRoomId);
        result.setScheduledHearingId(scheduledHearingId);
        result.setTimeStatusSet(timeStatusSet);
        result.setStatus(status);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        crLiveDisplayId = result.getPrimaryKey();
        assertNotNull(crLiveDisplayId, NOTNULL);
        result.setXhbScheduledHearing(result.getXhbScheduledHearing());
        return new XhbCrLiveDisplayDao(result);
    }

}
