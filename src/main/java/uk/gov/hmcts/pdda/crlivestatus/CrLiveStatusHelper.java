package uk.gov.hmcts.pdda.crlivestatus;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.framework.services.conversion.DateConverter;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.courtlog.helpers.xsl.CourtLogXslHelper;
import uk.gov.hmcts.pdda.courtlog.helpers.xsl.TranslationType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A helper class to contain all of the logic for manipulating the xhb_cr_live_status entry for a
 * court room and scheduled hearings.

 * @author pznwc5
 * @author tz0d5m
 * @version $Revision: 1.7 $
 */
@SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts", "PMD.NullAssignment"})
public final class CrLiveStatusHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CrLiveStatusHelper.class);
    private static final String REMOVE_FREE_TEXT_STYLESHEET = "config/courtlog/transformer/remove_free_text.xsl";

    private EntityManager entityManager;
    private XhbScheduledHearingRepository xhbScheduledHearingRepository;
    private XhbSittingRepository xhbSittingRepository;
    
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
    
    /**
     * Update the public display status if the passed in court log event is more
     * recent than the last displayed public display event for the case whose
     * scheduled hearing is passed in on the view value.
     * 
     * @param courtLogViewValue
     *            The <code>CourtLogViewValue</code> of the event that was
     *            created or updated.
     * @return <i>true</i> if there is a cr live status entry for the scheduled
     *         hearing on the log event passed in and the court log event passed
     *         in is more recent than the time status set of the live status
     *         entry, <i>false</i> will be returned otherwise.
     */
    public boolean updatePublicDisplayStatus(CourtLogViewValue courtLogViewValue) {
        LOG.debug("updatePublicDisplayStatus() - start");
        // 1) Get the scheduled hearing
        LOG.debug("Finding XhbScheduledHearingDao with ID: {}", courtLogViewValue.getScheduledHearingId());
        Optional<XhbScheduledHearingDao> xhbScheduledHearingDao = getXhbScheduledHearingRepository()
            .findByIdSafe(courtLogViewValue.getScheduledHearingId());
        
        if (xhbScheduledHearingDao.isPresent()) {
            LOG.debug("XhbScheduledHearingDao found with ID: {}", courtLogViewValue.getScheduledHearingId());
            
            // 2) Get the sitting from the scheduled hearing
            Optional<XhbSittingDao> xhbSittingDao = getXhbSittingRepository()
                .findByIdSafe(xhbScheduledHearingDao.get().getSittingId());
            
            if (xhbSittingDao.isPresent()) {
                LOG.debug("XhbSittingDao found with ID: {}", xhbSittingDao.get().getSittingId());
                // 3) Get the cr live display from the court room on the sitting
                Optional<XhbCrLiveDisplayDao> xhbCrLiveDisplayDao =
                    getCrLiveDisplay(xhbSittingDao.get().getXhbCourtRoom());
        
                if (xhbCrLiveDisplayDao.isPresent()) {
                    LOG.debug("XhbCrLiveDisplayDao found with ID: {}", xhbCrLiveDisplayDao.get().getCrLiveDisplayId());
                    if (!xhbCrLiveDisplayDao.get().getTimeStatusSet().isAfter(courtLogViewValue.getEntryDate())) {
                        xhbCrLiveDisplayDao.get().setTimeStatusSet(courtLogViewValue.getEntryDate());
                        xhbCrLiveDisplayDao.get().setStatus(getPublicDisplayStatus(courtLogViewValue));
                        //xcld.setStatus("<?xml version=\"1.0\" encoding=\"UTF-8\"?><event xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"30600.xsd\"><time>15:31</time><date>28/10/25</date><hearing_id>770221</hearing_id><free_text/><process_linked_cases>false</process_linked_cases><defendant_on_case_id>1</defendant_on_case_id><type>30600</type><defendant_name>BLAGGINA BLAGGER</defendant_name><scheduled_hearing_id>767898</scheduled_hearing_id><defendant_masked_name/><defendant_masked_flag>N</defendant_masked_flag></event>");
                        LOG.debug("updatePublicDisplayStatus() - returning true");
                        return true;
                    }
                }
            }
        }

        LOG.debug("updatePublicDisplayStatus() - returning false");
        return false;
    }
    
    
    /**
     * Get the public display status by transforming the passed in value object
     * by removing the free text.
     * 
     * @param viewValue
     * @return The translated xml <code>String</code>.
     */
    private static String getPublicDisplayStatus(CourtLogViewValue viewValue) {
        LOG.debug("getPublicDisplayStatus() - start and finish");
        return CourtLogXslHelper.translateEvent(viewValue, Locale.UK, TranslationType.PUBLIC_DISPLAY,
                REMOVE_FREE_TEXT_STYLESHEET);
    }

    private static Optional<XhbCrLiveDisplayDao> getCrLiveDisplay(XhbCourtRoomDao xhbCourtRoomDao) {
        List<XhbCrLiveDisplayDao> liveStatuses =
            (List<XhbCrLiveDisplayDao>) xhbCourtRoomDao.getXhbCrLiveDisplays();

        // This will always be 0 or 1 due to a unique constraint in the database
        if (!liveStatuses.isEmpty()) {
            LOG.debug("CR live Internet found");
            return Optional.of(liveStatuses.iterator().next());
        }

        // otherwise, create a new one...
        LOG.debug("Creating CR live internet");
        final XhbCrLiveDisplayDao xcldbv = new XhbCrLiveDisplayDao();
        xcldbv.setTimeStatusSet(LocalDateTime.now());
        xcldbv.setCourtRoomId(xhbCourtRoomDao.getCourtRoomId());
        
        return PddaEntityHelper.xcldSave(xcldbv);
    }

    private static void clearCrLiveDisplayStatus(XhbCrLiveDisplayDao crLiveDisplay) {
        LOG.debug("clearCRLiveDisplayStatus() - for id {}", crLiveDisplay.getCrLiveDisplayId());
        crLiveDisplay.setXhbScheduledHearing(null);
        crLiveDisplay.setStatus(null);
    }
    
    private boolean isEntityManagerActive() {
        return EntityManagerUtil.isEntityManagerActive(entityManager);
    }
    
    private EntityManager getEntityManager() {
        if (entityManager == null) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            clearRepositories();
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }
    
    private void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbScheduledHearingRepository = null;
        xhbSittingRepository = null;
    }
    
    private XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
        if (xhbScheduledHearingRepository == null || !isEntityManagerActive()) {
            xhbScheduledHearingRepository = new XhbScheduledHearingRepository(getEntityManager());
        }
        return xhbScheduledHearingRepository;
    }
    
    private XhbSittingRepository getXhbSittingRepository() {
        if (xhbSittingRepository == null || !isEntityManagerActive()) {
            xhbSittingRepository = new XhbSittingRepository(getEntityManager());
        }
        return xhbSittingRepository;
    }
}
