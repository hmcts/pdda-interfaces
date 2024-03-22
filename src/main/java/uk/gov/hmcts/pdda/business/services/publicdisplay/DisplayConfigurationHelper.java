package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.CourtRoomNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.DisplayNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.RotationSetNotFoundCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtDisplayConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

public class DisplayConfigurationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayConfigurationHelper.class);

    protected DisplayConfigurationHelper() {
        // Protected constructor
    }

    // This one is called dynamically
    public static DisplayConfiguration getDisplayConfiguration(final Integer displayId,
        final EntityManager entityManager) {
        return getDisplayConfiguration(displayId, new XhbDisplayRepository(entityManager),
            new XhbCourtRepository(entityManager));
    }

    public static DisplayConfiguration getDisplayConfiguration(final Integer displayId,
        XhbDisplayRepository xhbDisplayRepository, XhbCourtRepository xhbCourtRepository) {
        LOG.debug("getDisplayConfiguration({},{},{})", displayId, xhbDisplayRepository,
            xhbCourtRepository);
        Optional<XhbDisplayDao> display = xhbDisplayRepository.findById(displayId);
        if (!display.isPresent()) {
            throw new DisplayNotFoundException(displayId);
        }
        return new DisplayConfiguration(display.get(), display.get().getXhbRotationSet(),
            getDisplayCourtRooms(display.get(), xhbCourtRepository));
    }

    private static XhbCourtRoomDao[] getDisplayCourtRooms(final XhbDisplayDao display,
        XhbCourtRepository xhbCourtRepository) {
        LOG.debug("getDisplayCourtRooms({},{})", display, xhbCourtRepository);
        boolean isMultiSite = false;

        ArrayList<XhbCourtRoomDao> rooms = (ArrayList<XhbCourtRoomDao>) display.getXhbCourtRooms();

        if (!rooms.isEmpty()) {
            Integer courtId = rooms.iterator().next().getXhbCourtSite().getCourtId();
            isMultiSite = isCourtMultiSite(courtId, xhbCourtRepository);
        }
        if (isMultiSite) {
            return getMultiSiteCourtRoomData(display.getXhbCourtRooms());
        } else {
            return rooms.toArray(new XhbCourtRoomDao[0]);
        }
    }

    private static XhbCourtRoomDao[] getMultiSiteCourtRoomData(final Collection<?> values) {
        LOG.debug("getMultiSiteCourtRoomData({})", values);
        XhbCourtRoomDao[] returnValues = new XhbCourtRoomDao[values.size()];
        int recNo = 0;

        Iterator<?> iter = values.iterator();
        while (iter.hasNext()) {
            XhbCourtRoomDao courtRoom = (XhbCourtRoomDao) iter.next();
            XhbCourtSiteDao thisSite = courtRoom.getXhbCourtSite();
            courtRoom.setMultiSiteDisplayName(
                thisSite.getShortName() + "-" + courtRoom.getDisplayName());
            returnValues[recNo] = courtRoom;
            recNo++;
        }
        return returnValues;
    }

    private static boolean isCourtMultiSite(final Integer courtId,
        XhbCourtRepository xhbCourtRepository) {
        LOG.debug("isCourtMultiSite({},{})", courtId, xhbCourtRepository);
        Optional<XhbCourtDao> dao = xhbCourtRepository.findById(courtId);
        return dao.isPresent() && dao.get().getXhbCourtSites().size() > 1;
    }

    // This one is called dynamically
    public static void updateDisplayConfiguration(final DisplayConfiguration displayConfiguration,
        final PublicDisplayNotifier notifier, final EntityManager entityManager) {
        updateDisplayConfiguration(displayConfiguration, notifier,
            new XhbDisplayRepository(entityManager), new XhbRotationSetsRepository(entityManager),
            new XhbCourtRoomRepository(entityManager));
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
        XhbRotationSetsRepository xhbRotationSetsRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        LOG.debug("updateDisplayConfiguration({},{},{},{},{})", displayConfiguration, notifier,
            xhbDisplayRepository, xhbRotationSetsRepository, xhbCourtRoomRepository);

        // Lookup the display local reference
        Integer displayId = displayConfiguration.getDisplayId();
        Optional<XhbDisplayDao> displayLocal = xhbDisplayRepository.findById(displayId);
        if (!displayLocal.isPresent()) {
            throw new DisplayNotFoundException(displayId);
        }

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
        if (displayConfiguration.isCourtRoomsChanged()
            || displayConfiguration.isRotationSetChanged()) {
            sendNotification(displayId, displayLocal.get(), notifier);
        }
    }

    /**
     * Sets the court rooms.
     * 
     * @param displayConfiguration Display configuration
     * @param displayLocal Display local reference
     */
    private static void setCourtRooms(final DisplayConfiguration displayConfiguration,
        final XhbDisplayDao displayLocal, XhbCourtRoomRepository xhbCourtRoomRepository) {
        LOG.debug("setCourtRooms({},{},{})", displayConfiguration, displayLocal,
            xhbCourtRoomRepository);
        /**
         * if the courts have been changed: Delete the current ones and create with ones passed in.
         * Note: we are not doing optimistic lock checking because this cross reference table will
         * not have a version added
         */
        XhbCourtRoomDao[] courtRoomBasicValues = displayConfiguration.getCourtRoomDaos();

        ArrayList<XhbCourtRoomDao> courtRoomLocals =
            (ArrayList<XhbCourtRoomDao>) displayLocal.getXhbCourtRooms();

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
        LOG.debug("setRotationSet({},{},{})", displayConfiguration, displayLocal,
            xhbRotationSetsRepository);
        Integer rotationSetId = displayConfiguration.getRotationSetId();
        Optional<XhbRotationSetsDao> rotationSetLocal =
            xhbRotationSetsRepository.findById(Long.valueOf(rotationSetId));
        if (!rotationSetLocal.isPresent()) {
            throw new RotationSetNotFoundCheckedException(rotationSetId);
        }
        displayLocal.setXhbRotationSet(rotationSetLocal.get());
    }

    /**
     * Sends a JMS notification.
     * 
     * @param displayId Dipslay Id
     * @param displayLocal Display local reference
     */
    private static void sendNotification(final Integer displayId, final XhbDisplayDao displayLocal,
        PublicDisplayNotifier notifier) {
        LOG.debug("sendNotification({},{},{})", displayId, displayLocal, notifier);
        // find court Id
        Integer courtId = displayLocal.getXhbDisplayLocation().getXhbCourtSite().getCourtId();
        CourtConfigurationChange ccc =
            new CourtDisplayConfigurationChange(courtId.intValue(), displayId.intValue());
        ConfigurationChangeEvent ccEvent = new ConfigurationChangeEvent(ccc);
        notifier.sendMessage(ccEvent);
    }
}
