package uk.gov.hmcts.pdda.crlivestatus;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(EasyMockExtension.class)
class CrLiveStatusRepositoriesTest {

    private static final String NOT_INSTANCE = "Result is not an instance of expected class";
    
    @Mock
    EntityManager mockEntityManager;
    
    @TestSubject
    CrLiveStatusRepositories classUnderTest = new CrLiveStatusRepositories(mockEntityManager);
    
    @Test
    void testGetXhbScheduledHearingRepository() {
        assertInstanceOf(XhbScheduledHearingRepository.class, classUnderTest.getXhbScheduledHearingRepository(),
            NOT_INSTANCE);
    }
    
    @Test
    void testGetXhbCourtRoomRepository() {
        assertInstanceOf(XhbCourtRoomRepository.class, classUnderTest.getXhbCourtRoomRepository(),
            NOT_INSTANCE);
    }
    
    @Test
    void testGetXhbSittingRepository() {
        assertInstanceOf(XhbSittingRepository.class, classUnderTest.getXhbSittingRepository(),
            NOT_INSTANCE);
    }
    
    @Test
    void testGetXhbCrLiveDisplayRepository() {
        assertInstanceOf(XhbCrLiveDisplayRepository.class, classUnderTest.getXhbCrLiveDisplayRepository(),
            NOT_INSTANCE);
    }
}
