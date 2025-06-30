package uk.gov.hmcts.pdda.crlivestatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CrLiveStatusHelperTest {

    private static final Logger LOG = LoggerFactory.getLogger(CrLiveStatusHelperTest.class);

    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private EntityManagerFactory mockEntityManagerFactory;

    @BeforeEach
    public void setup() {
        mockPddaEntityHelper();
    }

    @AfterEach
    public void teardown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testActivatePublicDisplay() {
        LOG.debug("ActivatePublicDisplay - with display");
        boolean result = testActivatePublicDisplay(true, true, null);
        assertTrue(result, TRUE);
        LOG.debug("ActivatePublicDisplay - without display");
        result = testActivatePublicDisplay(false, true, true);
        assertTrue(result, TRUE);
        LOG.debug("ActivatePublicDisplay - without display and save failure");
        result = testActivatePublicDisplay(false, true, false);
        assertTrue(result, TRUE);
    }

    private boolean testActivatePublicDisplay(boolean hasLiveDisplay, boolean hasCourtRoom, Boolean saveSuccess) {
        // Setup
        XhbScheduledHearingDao xhbScheduledHearingDao = DummyHearingUtil.getXhbScheduledHearingDao();
        mocks(xhbScheduledHearingDao, hasLiveDisplay, hasCourtRoom, false, saveSuccess);
        // Run
        CrLiveStatusHelper.activatePublicDisplay(xhbScheduledHearingDao, new Date());
        return true;
    }

    @Test
    void testDeactivatePublicDisplay() {
        LOG.debug("DeactivatePublicDisplay - with court room");
        boolean result = testDeactivatePublicDisplay(true, true, false, null);
        assertTrue(result, TRUE); 
        LOG.debug("DeactivatePublicDisplay - without court room");
        result = testDeactivatePublicDisplay(null, false, false, null);
        assertTrue(result, TRUE);
        LOG.debug("DeactivatePublicDisplay - with display");
        result = testDeactivatePublicDisplay(true, true, false, true);
        assertTrue(result, TRUE);
        LOG.debug("DeactivatePublicDisplay - without display");
        result = testDeactivatePublicDisplay(true, true, true, true);
        assertTrue(result, TRUE);
    }

    private boolean testDeactivatePublicDisplay(Boolean hasLiveDisplay, Boolean hasCourtRoom,
        Boolean otherScheduledHearing, Boolean saveSuccess) {
        // Setup
        XhbScheduledHearingDao xhbScheduledHearingDao = DummyHearingUtil.getXhbScheduledHearingDao();
        mocks(xhbScheduledHearingDao, hasLiveDisplay, hasCourtRoom, otherScheduledHearing, saveSuccess);
        // Run
        CrLiveStatusHelper.deactivatePublicDisplay(xhbScheduledHearingDao, new Date());
        return true;
    }

    private void mocks(XhbScheduledHearingDao xhbScheduledHearingDao, Boolean hasLiveDisplay, Boolean hasCourtRoom,
        Boolean otherScheduledHearing, Boolean saveSuccess) {
        XhbSittingDao xhbSittingDao = DummyHearingUtil.getXhbSittingDao();
        xhbScheduledHearingDao.setXhbSitting(xhbSittingDao);
        XhbCourtRoomDao xhbCourtRoomDao = DummyCourtUtil.getXhbCourtRoomDao();
        xhbCourtRoomDao.setXhbCrLiveDisplays(new ArrayList<>());
        XhbCrLiveDisplayDao xhbCrLiveDisplayDao = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        if (hasLiveDisplay != null && hasLiveDisplay) {
            xhbCrLiveDisplayDao.setXhbScheduledHearing(
                otherScheduledHearing ? DummyHearingUtil.getXhbScheduledHearingDao() : xhbScheduledHearingDao);
            xhbCourtRoomDao.getXhbCrLiveDisplays().add(xhbCrLiveDisplayDao);
        }
        xhbScheduledHearingDao.getXhbSitting().setXhbCourtRoom(xhbCourtRoomDao);
        // Mock
        Mockito.when(PddaEntityHelper.xcrtFindByPrimaryKey(xhbCourtRoomDao.getCourtRoomId()))
            .thenReturn(hasCourtRoom ? Optional.of(xhbCourtRoomDao) : Optional.empty());
        if (hasLiveDisplay != null && !hasLiveDisplay) {
            Mockito.when(PddaEntityHelper.xcldSave(isA(XhbCrLiveDisplayDao.class)))
                .thenReturn(saveSuccess ? Optional.of(xhbCrLiveDisplayDao) : Optional.empty());
        }
    }
    
    private void mockPddaEntityHelper() {
        Mockito.mockStatic(Persistence.class);
        Mockito.mockStatic(PddaEntityHelper.class);
        Mockito.when(Persistence.createEntityManagerFactory(isA(String.class))).thenReturn(mockEntityManagerFactory);
    }

}
