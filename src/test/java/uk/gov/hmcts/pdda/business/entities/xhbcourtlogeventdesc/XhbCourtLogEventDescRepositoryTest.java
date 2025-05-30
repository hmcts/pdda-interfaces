package uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc;

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
class XhbCourtLogEventDescRepositoryTest extends AbstractRepositoryTest<XhbCourtLogEventDescDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtLogEventDescRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtLogEventDescRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCourtLogEventDescRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCourtLogEventDescDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCourtLogEventDescDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected XhbCourtLogEventDescDao getDummyDao() {
        Integer eventDescId = getDummyId();
        Integer flaggedEvent = -1;
        Integer editable = -2;
        Integer sendToMercator = -3;
        Integer updateLinkedCases = -4;
        Integer publishToSubscribers = -5;
        Integer clearPublicDisplays = -6;
        Integer electronicInform = -1;
        Integer publicDisplay = -1;
        String linkedCaseText = "linkedCaseText";
        String eventDescription = "eventDescription";
        Integer eventType = -1;
        Integer publicNotice = -1;
        String shortDescription = "shortDescription";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;

        XhbCourtLogEventDescDao result = new XhbCourtLogEventDescDao();
        result.setEventDescId(eventDescId);
        result.setFlaggedEvent(flaggedEvent);
        result.setEditable(editable);
        result.setSendToMercator(sendToMercator);
        result.setUpdateLinkedCases(updateLinkedCases);
        result.setPublishToSubscribers(publishToSubscribers);
        result.setClearPublicDisplays(clearPublicDisplays);
        result.setElectronicInform(electronicInform);
        result.setPublicDisplay(publicDisplay);
        result.setLinkedCaseText(linkedCaseText);
        result.setEventDescription(eventDescription);
        result.setEventType(eventType);
        result.setPublicNotice(publicNotice);
        result.setShortDescription(shortDescription);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        eventDescId = result.getPrimaryKey();
        assertNotNull(eventDescId, NOTNULLRESULT);
        return new XhbCourtLogEventDescDao(result);
    }

}
