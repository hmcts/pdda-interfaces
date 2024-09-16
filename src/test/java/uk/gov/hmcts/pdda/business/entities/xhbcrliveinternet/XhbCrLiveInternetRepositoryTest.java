package uk.gov.hmcts.pdda.business.entities.xhbcrliveinternet;

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
class XhbCrLiveInternetRepositoryTest extends AbstractRepositoryTest<XhbCrLiveInternetDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCrLiveInternetRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCrLiveInternetRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCrLiveInternetRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbCrLiveInternetDao getDummyDao() {
        Integer crLiveInternetId = getDummyId();
        Integer courtRoomId = -1;
        Integer scheduledHearingId = -1;
        LocalDateTime timeStatusSet = LocalDateTime.now();
        String status = "status";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        XhbCrLiveInternetDao result = new XhbCrLiveInternetDao();
        result.setCrLiveInternetId(crLiveInternetId);
        result.setCourtRoomId(courtRoomId);
        result.setScheduledHearingId(scheduledHearingId);
        result.setTimeStatusSet(timeStatusSet);
        result.setStatus(status);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);

        crLiveInternetId = result.getPrimaryKey();
        assertNotNull(crLiveInternetId, NOTNULL);
        return new XhbCrLiveInternetDao(result);
    }

}
