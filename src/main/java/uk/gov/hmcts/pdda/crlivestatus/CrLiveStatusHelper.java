package uk.gov.hmcts.pdda.crlivestatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.conversion.DateConverter;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A helper class to contain all of the logic for manipulating the xhb_cr_live_status entry for a
 * court room and scheduled hearings.

 * @author pznwc5
 * @author tz0d5m
 * @version $Revision: 1.7 $
 */
public final class CrLiveStatusHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CrLiveStatusHelper.class);

    private CrLiveStatusHelper() {
        // prevent external instantiation
    }


    /**
     * Method to indicate that the public display should be activated. This will clear out the
     * xhb_cr_live_display row entries for the court room that the scheduled hearing is assigned to,
     * and then assign the passed in scheduled hearing to the cr live status.

     * @param xsh XhbScheduledHearingDAO
     * @param activationDate Date
     */
    public static void activatePublicDisplay(XhbScheduledHearingDao xsh, Date activationDate) {
        LOG.debug("activatePublicDisplay() - start");

        // set xhb_cr_live_display details
        final Optional<XhbCrLiveDisplayDao> xcld =
            getCrLiveDisplay(xsh.getXhbSitting().getXhbCourtRoom());

        if (xcld.isPresent()) {
            // ensure that the current status is cleared...
            clearCrLiveDisplayStatus(xcld.get());

            // now set this scheduled hearing to the status...
            xcld.get().setXhbScheduledHearing(xsh);
            xcld.get().setTimeStatusSet(DateConverter.convertDateToLocalDateTime(activationDate));
        }

        LOG.debug("activatePublicDisplay() - end");
    }

    /**
     * Method to indicate that the public display should be deactivated if the scheduled hearing
     * passed in is not already de-activated. This will clear out the xhb_cr_live_status row entries
     * for the court room that the scheduled hearing is assigned to.

     * @param xsh XhbScheduledHearingDAO
     * @param deactivationDate Date
     */
    public static void deactivatePublicDisplay(XhbScheduledHearingDao xsh, Date deactivationDate) {
        LOG.debug("deactivatePublicDisplay() - start");

        // set xhb_cr_live_display details
        final Optional<XhbCrLiveDisplayDao> xcld =
            getCrLiveDisplay(xsh.getXhbSitting().getXhbCourtRoom());
        if (xcld.isPresent()) {
            final XhbScheduledHearingDao liveDisplayScheduledHearing =
                xcld.get().getXhbScheduledHearing();

            if (liveDisplayScheduledHearing != null && liveDisplayScheduledHearing.equals(xsh)) {
                clearCrLiveDisplayStatus(xcld.get());
                // for consistency, also set the time status set...
                xcld.get()
                    .setTimeStatusSet(DateConverter.convertDateToLocalDateTime(deactivationDate));
            }
        }

        LOG.debug("deactivatePublicDisplay() - end");
    }

    private static Optional<XhbCrLiveDisplayDao> getCrLiveDisplay(XhbCourtRoomDao courtRoom) {
        List<XhbCrLiveDisplayDao> liveStatuses =
            (List<XhbCrLiveDisplayDao>) courtRoom.getXhbCrLiveDisplays();

        // This will always be 0 or 1 due to a unique constraint in the database
        if (!liveStatuses.isEmpty()) {
            LOG.debug("CR live Internet found");
            return Optional.of(liveStatuses.iterator().next());
        }

        // otherwise, create a new one...
        LOG.debug("Creating CR live internet");
        final XhbCrLiveDisplayDao xcldbv = new XhbCrLiveDisplayDao();
        xcldbv.setTimeStatusSet(LocalDateTime.now());
        xcldbv.setCourtRoomId(courtRoom.getCourtRoomId());

        return PddaEntityHelper.xcldSave(xcldbv);
    }

    private static void clearCrLiveDisplayStatus(XhbCrLiveDisplayDao crLiveDisplay) {
        LOG.debug("clearCRLiveDisplayStatus() - for id {}", crLiveDisplay.getCrLiveDisplayId());
        crLiveDisplay.setXhbScheduledHearing(null);
        crLiveDisplay.setStatus(null);
    }
}
