package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtDisplayConfigurationChange;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.CourtRoomNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.DisplayNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.RotationSetNotFoundCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayConfiguration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Title: Display Configuration Helper.
 * </p>
 * <p>
 * Description: Helper class to update a display configuration<br>
 * A display corresponds to one physical screen and the configuration includes the assigned rotation
 * set and the court rooms it covers.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: DisplayConfigurationHelper.java,v 1.6 2005/11/17 10:55:46 bzjrnl Exp $
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class DisplayConfigurationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayConfigurationHelper.class);

    protected DisplayConfigurationHelper() {
        // Protected constructor
    }

    // This one is called dynamically
    public static DisplayConfiguration getDisplayConfiguration(final Integer displayId,
        final EntityManager entityManager) {
        return getDisplayConfiguration(displayId, new XhbDisplayRepository(entityManager),
            new XhbCourtRepository(entityManager), new XhbRotationSetsRepository(entityManager),
            new XhbCourtSiteRepository(entityManager), new XhbCourtRoomRepository(entityManager));
    }

    public static DisplayConfiguration getDisplayConfiguration(final Integer displayId,
        XhbDisplayRepository xhbDisplayRepository, XhbCourtRepository xhbCourtRepository,
        XhbRotationSetsRepository xhbRotationSetsRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        LOG.debug("getDisplayConfiguration({},{},{})", displayId, xhbDisplayRepository, xhbCourtRepository);
        Optional<XhbDisplayDao> display = xhbDisplayRepository.findById(displayId);
        if (!display.isPresent()) {
            throw new DisplayNotFoundException(displayId);
        }
        Optional<XhbRotationSetsDao> xhbRotationSet =
            xhbRotationSetsRepository.findById(display.get().getRotationSetId());
        XhbRotationSetsDao xhbRotationSetDao = xhbRotationSet.isPresent() ? xhbRotationSet.get() : null;
        return new DisplayConfiguration(display.get(), xhbRotationSetDao,
            getDisplayCourtRooms(display.get(), xhbCourtRepository, xhbCourtSiteRepository, xhbCourtRoomRepository));
    }

    private static XhbCourtRoomDao[] getDisplayCourtRooms(final XhbDisplayDao display,
        XhbCourtRepository xhbCourtRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        LOG.debug("getDisplayCourtRooms({},{})", display, xhbCourtRepository);
        boolean isMultiSite = false;

        List<XhbCourtRoomDao> rooms = xhbCourtRoomRepository.findByDisplayId(display.getDisplayId());

        if (!rooms.isEmpty()) {
            Integer courtSiteId = rooms.iterator().next().getCourtSiteId();
            Optional<XhbCourtSiteDao> xhbCourtSite = xhbCourtSiteRepository.findById(courtSiteId);
            if (xhbCourtSite.isPresent()) {
                Integer courtId = xhbCourtSite.get().getCourtId();
                isMultiSite = isCourtMultiSite(courtId, xhbCourtRepository, xhbCourtSiteRepository);
            }
        }
        if (isMultiSite) {
            return getMultiSiteCourtRoomData(rooms, xhbCourtSiteRepository);
        } else {
            return rooms.toArray(new XhbCourtRoomDao[0]);
        }
    }

    private static XhbCourtRoomDao[] getMultiSiteCourtRoomData(final Collection<XhbCourtRoomDao> values,
        XhbCourtSiteRepository xhbCourtSiteRepository) {
        LOG.debug("getMultiSiteCourtRoomData({})", values);
        XhbCourtRoomDao[] returnValues = new XhbCourtRoomDao[values.size()];
        int recNo = 0;

        Iterator<?> iter = values.iterator();
        while (iter.hasNext()) {
            XhbCourtRoomDao courtRoom = (XhbCourtRoomDao) iter.next();
            Optional<XhbCourtSiteDao> thisSite = xhbCourtSiteRepository.findById(courtRoom.getCourtSiteId());
            if (thisSite.isPresent()) {
                courtRoom.setMultiSiteDisplayName(thisSite.get().getShortName() + "-" + courtRoom.getDisplayName());
                returnValues[recNo] = courtRoom;
                recNo++;
            }
        }
        return returnValues;
    }

    private static boolean isCourtMultiSite(final Integer courtId, XhbCourtRepository xhbCourtRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository) {
        LOG.debug("isCourtMultiSite({},{})", courtId, xhbCourtRepository);
        Optional<XhbCourtDao> dao = xhbCourtRepository.findById(courtId);
        boolean result = false;
        if (dao.isPresent()) {
            List<XhbCourtSiteDao> xhbCourtSites = xhbCourtSiteRepository.findByCourtId(courtId);
            result = xhbCourtSites.size() > 1;
        }
        return result;
    }

    // This one is called dynamically
    public static void updateDisplayConfiguration(final DisplayConfiguration displayConfiguration,
        final PublicDisplayNotifier notifier, final EntityManager entityManager) {
        updateDisplayConfiguration(displayConfiguration, notifier, new XhbDisplayRepository(entityManager),
            new XhbRotationSetsRepository(entityManager), new XhbCourtRoomRepository(entityManager),
            new XhbDisplayLocationRepository(entityManager), new XhbCourtSiteRepository(entityManager));
    }

    /**
     * Updates the display configuration with changes.
     * 
     * <p>Note: sends a DisplayConfigurationChanged JMS configuration message
     * 
     * @param displayConfiguration The updated display configuration to be stored
     */
    public static void updateDisplayConfiguration(final DisplayConfiguration displayConfiguration,
        final PublicDisplayNotifier notifier, XhbDisplayRepository xhbDisplayRepository,
        XhbRotationSetsRepository xhbRotationSetsRepository, XhbCourtRoomRepository xhbCourtRoomRepository,
        XhbDisplayLocationRepository xhbDisplayLocationRepository, XhbCourtSiteRepository xhbCourtSiteRepository) {
        LOG.debug("updateDisplayConfiguration({},{},{},{},{})", displayConfiguration, notifier, xhbDisplayRepository,
            xhbRotationSetsRepository, xhbCourtRoomRepository);

        // Lookup the display local reference
        Optional<XhbDisplayDao> displayLocal = xhbDisplayRepository.findById(displayConfiguration.getDisplayId());
        if (!displayLocal.isPresent()) {
            throw new DisplayNotFoundException(displayConfiguration.getDisplayId());
        }

        Integer courtId = getCourtIdFromDisplay(displayLocal, xhbDisplayLocationRepository, xhbCourtSiteRepository);
        if (courtId != null) {

            // if the rotation set has been updated write back to DB
            if (displayConfiguration.isRotationSetChanged()) {
                setRotationSet(displayConfiguration, displayLocal.get(), xhbRotationSetsRepository);
            }

            // if the court rooms have been updated write back to DB
            if (displayConfiguration.isCourtRoomsChanged()) {
                xhbDisplayRepository.update(displayConfiguration.getDisplayDao());
                setCourtRooms(displayConfiguration, displayLocal.get(), xhbCourtRoomRepository);
            }

            // if RS or courtrooms have changed send JMS message for displayId
            if (displayConfiguration.isCourtRoomsChanged() || displayConfiguration.isRotationSetChanged()) {
                sendNotification(displayConfiguration.getDisplayId(), displayLocal.get(), courtId, notifier);
            }
        }
    }

    protected static Integer getCourtIdFromDisplay(Optional<XhbDisplayDao> displayLocal,
        XhbDisplayLocationRepository xhbDisplayLocationRepository, XhbCourtSiteRepository xhbCourtSiteRepository) {
        if (displayLocal.isPresent()) {
            Optional<XhbDisplayLocationDao> xhbDisplayLocation =
                xhbDisplayLocationRepository.findById(displayLocal.get().getDisplayLocationId());
            if (xhbDisplayLocation.isPresent()) {
                Optional<XhbCourtSiteDao> xhbCourtSiteDao =
                    xhbCourtSiteRepository.findById(xhbDisplayLocation.get().getCourtSiteId());
                if (xhbCourtSiteDao.isPresent()) {
                    return xhbCourtSiteDao.get().getCourtId();
                }
            }
        }
        return null;
    }

    /**
     * Sets the court rooms.
     * 
     * @param displayConfiguration Display configuration
     * @param displayLocal Display local reference
     */
    private static void setCourtRooms(final DisplayConfiguration displayConfiguration, final XhbDisplayDao displayLocal,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        LOG.debug("setCourtRooms({},{},{})", displayConfiguration, displayLocal, xhbCourtRoomRepository);
        /**
         * if the courts have been changed: Delete the current ones and create with ones passed in. Note: we
         * are not doing optimistic lock checking because this cross reference table will not have a version
         * added
         */
        XhbCourtRoomDao[] courtRoomBasicValues = displayConfiguration.getCourtRoomDaos();

        List<XhbCourtRoomDao> courtRoomLocals = xhbCourtRoomRepository.findByDisplayId(displayLocal.getDisplayId());

        // delete existing collection
        courtRoomLocals.clear();

        // Add new ones
        for (XhbCourtRoomDao courtRoomBasicValue : courtRoomBasicValues) {
            Integer courtRoomId = courtRoomBasicValue.getPrimaryKey();
            Optional<XhbCourtRoomDao> room = xhbCourtRoomRepository.findById(courtRoomId);
            if (!room.isPresent()) {
                throw new CourtRoomNotFoundException(courtRoomId);
            }
            courtRoomLocals.add(room.get());
        }
    }

    /**
     * Set the rotation set.
     * 
     * @param displayConfiguration Display configuration
     * @param displayLocal Display local reference
     * @throws RotationSetNotFoundCheckedException If the rotation set is not found
     */
    private static void setRotationSet(final DisplayConfiguration displayConfiguration,
        final XhbDisplayDao displayLocal, XhbRotationSetsRepository xhbRotationSetsRepository) {
        LOG.debug("setRotationSet({},{},{})", displayConfiguration, displayLocal, xhbRotationSetsRepository);
        Integer rotationSetId = displayConfiguration.getRotationSetId();
        Optional<XhbRotationSetsDao> rotationSetLocal = xhbRotationSetsRepository.findById(Long.valueOf(rotationSetId));
        if (!rotationSetLocal.isPresent()) {
            throw new RotationSetNotFoundCheckedException(rotationSetId);
        }
    }

    /**
     * Sends a JMS notification.
     * 
     * @param displayId Dipslay Id
     * @param courtId court Id.
     * @param displayLocal Display local reference
     */
    private static void sendNotification(final Integer displayId, final XhbDisplayDao displayLocal,
        final Integer courtId, PublicDisplayNotifier notifier) {
        LOG.debug("sendNotification({},{},{})", displayId, displayLocal, notifier);
        CourtConfigurationChange ccc = new CourtDisplayConfigurationChange(courtId, displayId);
        ConfigurationChangeEvent ccEvent = new ConfigurationChangeEvent(ccc);
        notifier.sendMessage(ccEvent);
    }
}
