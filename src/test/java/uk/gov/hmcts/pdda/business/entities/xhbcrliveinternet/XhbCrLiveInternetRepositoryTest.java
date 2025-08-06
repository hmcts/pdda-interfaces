package uk.gov.hmcts.pdda.business.entities.xhbcrliveinternet;

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
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCrLiveInternetRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCrLiveInternetDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCrLiveInternetDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
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
        assertNotNull(crLiveInternetId, NOTNULLRESULT);
        return new XhbCrLiveInternetDao(result);
    }

}
