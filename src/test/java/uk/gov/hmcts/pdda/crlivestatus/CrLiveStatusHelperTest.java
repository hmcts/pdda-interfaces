package uk.gov.hmcts.pdda.crlivestatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.courtlog.helpers.xsl.CourtLogXslHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    
    
    /**
     * Positive case.
     * - display.timeStatusSet is earlier on the same day as the court log entry date
     * - Expect update(...) to be called and method to return true
     */
    @Test
    void testUpdatePublicDisplayStatus_updatesWhenEntryDateIsAfterTimeStatusSet_sameDay_returnsTrue() {
        // Arrange
        XhbScheduledHearingDao scheduled = DummyHearingUtil.getXhbScheduledHearingDao();
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        XhbCourtRoomDao courtRoom = DummyCourtUtil.getXhbCourtRoomDao();
        
        // Wire object references
        scheduled.setXhbSitting(sitting);
        sitting.setXhbCourtRoom(courtRoom);

        // ALSO set the ID fields used by SUT repository lookups
        scheduled.setSittingId(sitting.getSittingId());
        sitting.setCourtRoomId(courtRoom.getCourtRoomId());

        // Use today's date explicitly so "isTimeFromToday" passes.
        LocalDate today = LocalDate.now();
        LocalDateTime base = today.atTime(10, 0);
        LocalDateTime displayTime = base;
        LocalDateTime entryTime = base.plusMinutes(5);

        Date entryDate = Date.from(entryTime.atZone(ZoneId.systemDefault()).toInstant());

        XhbCrLiveDisplayDao displayDao = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        displayDao.setTimeStatusSet(displayTime);
        displayDao.setCourtRoomId(courtRoom.getCourtRoomId());

        XhbScheduledHearingRepository xshRepo = mock(
            XhbScheduledHearingRepository.class);
        XhbSittingRepository xsRepo = mock(XhbSittingRepository.class);
        XhbCourtRoomRepository xcrRepo = mock(XhbCourtRoomRepository.class);
        XhbCrLiveDisplayRepository xcldRepo = mock(
            XhbCrLiveDisplayRepository.class);

        when(xshRepo.findByIdSafe(scheduled.getScheduledHearingId()))
            .thenReturn(Optional.of(scheduled));
        when(xsRepo.findByIdSafe(sitting.getSittingId()))
            .thenReturn(Optional.of(sitting));
        when(xcrRepo.findByIdSafe(courtRoom.getCourtRoomId()))
            .thenReturn(Optional.of(courtRoom));
        when(xcldRepo.findByCourtRoomSafe(courtRoom.getCourtRoomId()))
            .thenReturn(Optional.of(displayDao));

        try (MockedStatic<CourtLogXslHelper> mockedXsl =
                 mockStatic(CourtLogXslHelper.class)) {
            mockedXsl.when(() ->
                CourtLogXslHelper.translateEvent(
                    any(CourtLogViewValue.class),
                    any(Locale.class),
                    any(),
                    any(String.class)
                )
            ).thenReturn("SOME-PUBLIC-STATUS");

            CrLiveStatusHelper helper = new CrLiveStatusHelper(mockEntityManager) {
                @Override
                protected XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
                    return xshRepo;
                }

                @Override
                protected XhbSittingRepository getXhbSittingRepository() {
                    return xsRepo;
                }

                @Override
                protected XhbCourtRoomRepository getXhbCourtRoomRepository() {
                    return xcrRepo;
                }

                @Override
                protected XhbCrLiveDisplayRepository getXhbCrLiveDisplayRepository() {
                    return xcldRepo;
                }
            };

            CourtLogViewValue viewValue = createCourtLogViewValue(
                scheduled.getScheduledHearingId(), entryDate);

            // Act
            boolean result = helper.updatePublicDisplayStatus(viewValue);

            // Assert
            assertTrue(result,
                "Expected updatePublicDisplayStatus to return true when timeStatusSet <= entryDate and same day");
            verify(xcldRepo, times(1)).update(displayDao);
            assertEquals("SOME-PUBLIC-STATUS", displayDao.getStatus(),
                "Status should be set from translation");
            assertEquals(entryTime, displayDao.getTimeStatusSet());
        }
    }

    @Test
    void testUpdatePublicDisplayStatus_noUpdateWhenTimeStatusSetAfterEntry_returnsFalse() {
        // Arrange
        XhbScheduledHearingDao scheduled = DummyHearingUtil.getXhbScheduledHearingDao();
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        XhbCourtRoomDao courtRoom = DummyCourtUtil.getXhbCourtRoomDao();
        
        scheduled.setXhbSitting(sitting);
        sitting.setXhbCourtRoom(courtRoom);

        // ALSO set IDs required by SUT lookups
        scheduled.setSittingId(sitting.getSittingId());
        sitting.setCourtRoomId(courtRoom.getCourtRoomId());

        LocalDate today = LocalDate.now();
        LocalDateTime base = today.atTime(12, 0);
        LocalDateTime entryTime = base;
        LocalDateTime displayTime = base.plusMinutes(2);

        Date entryDate = Date.from(entryTime.atZone(ZoneId.systemDefault()).toInstant());

        XhbCrLiveDisplayDao displayDao = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        displayDao.setTimeStatusSet(displayTime);
        displayDao.setCourtRoomId(courtRoom.getCourtRoomId());

        XhbScheduledHearingRepository xshRepo = mock(
            XhbScheduledHearingRepository.class);
        XhbSittingRepository xsRepo = mock(XhbSittingRepository.class);
        XhbCourtRoomRepository xcrRepo = mock(XhbCourtRoomRepository.class);
        XhbCrLiveDisplayRepository xcldRepo = mock(
            XhbCrLiveDisplayRepository.class);

        when(xshRepo.findByIdSafe(scheduled.getScheduledHearingId()))
            .thenReturn(Optional.of(scheduled));
        when(xsRepo.findByIdSafe(sitting.getSittingId()))
            .thenReturn(Optional.of(sitting));
        when(xcrRepo.findByIdSafe(courtRoom.getCourtRoomId()))
            .thenReturn(Optional.of(courtRoom));
        when(xcldRepo.findByCourtRoomSafe(courtRoom.getCourtRoomId()))
            .thenReturn(Optional.of(displayDao));

        try (MockedStatic<CourtLogXslHelper> mockedXsl =
                 mockStatic(CourtLogXslHelper.class)) {
            mockedXsl.when(() ->
                CourtLogXslHelper.translateEvent(
                    any(CourtLogViewValue.class),
                    any(Locale.class),
                    any(),
                    any(String.class)
                )
            ).thenReturn("SHOULD-NOT-BE-USED");

            CrLiveStatusHelper helper = new CrLiveStatusHelper(mockEntityManager) {
                @Override
                protected XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
                    return xshRepo;
                }

                @Override
                protected XhbSittingRepository getXhbSittingRepository() {
                    return xsRepo;
                }

                @Override
                protected XhbCourtRoomRepository getXhbCourtRoomRepository() {
                    return xcrRepo;
                }

                @Override
                protected XhbCrLiveDisplayRepository getXhbCrLiveDisplayRepository() {
                    return xcldRepo;
                }
            };

            CourtLogViewValue viewValue = createCourtLogViewValue(
                scheduled.getScheduledHearingId(), entryDate);

            // Act
            boolean result = helper.updatePublicDisplayStatus(viewValue);

            // Assert
            assertFalse(result,
                "Expected updatePublicDisplayStatus to return false when timeStatusSet is after entryDate");
            verify(xcldRepo, never()).update(any(XhbCrLiveDisplayDao.class));
            assertNotEquals("SHOULD-NOT-BE-USED", displayDao.getStatus());
        }
    }

    // -------------------------
    // Helper used by tests
    // -------------------------
    private CourtLogViewValue createCourtLogViewValue(Integer scheduledHearingId, Date entryDate) {
        CourtLogViewValue v = new CourtLogViewValue();
        v.setScheduledHearingId(scheduledHearingId);
        v.setEntryDate(entryDate);
        return v;
    }


}
