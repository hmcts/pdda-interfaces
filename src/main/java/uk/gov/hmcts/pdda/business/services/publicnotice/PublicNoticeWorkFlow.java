package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeDao;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * <p>
 * Title: PublicNoticeWorkFlow - 'nerve centre' of the Public Notice Subsystem.
 * </p>
 * <p>
 * Description: see title
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Pat Fox, Bob Boles
 * @version $Id: PublicNoticeWorkFlow.java,v 1.3 2006/06/05 12:29:54 bzjrnl Exp $
 */
public class PublicNoticeWorkFlow {

    /**
     * Logger log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PublicNoticeWorkFlow.class);

    private static final Integer REPORTING_RESTRICTIONS_LIFTED = 700;
    private static final Integer REPORTING_RESTRICTIONS = 100;

    protected PublicNoticeWorkFlow() {
        // Protected constructor
    }

    /**
     * Gets the allPublicNoticesForCourtRoom attribute of the PublicNoticeWorkFlow object.
     * 
     * @param xhbCourtRoomId Description of the Parameter
     * @return The allPublicNoticesForCourtRoom value
     */
    public static DisplayablePublicNoticeValue[] getAllPublicNoticesForCourtRoom(int xhbCourtRoomId) {

        LOG.debug(" 01 Enter the getAllPublicNoticesForCourtRoom method for {}", xhbCourtRoomId);

        Optional<XhbCourtRoomDao> courtRoom = PddaEntityHelper.xcrtFindByPrimaryKey(xhbCourtRoomId);
        return getAllPublicNoticesForCourtRoom(xhbCourtRoomId, courtRoom);
    }

    public static DisplayablePublicNoticeValue[] getAllPublicNoticesForCourtRoom(int xhbCourtRoomId,
        Optional<XhbCourtRoomDao> courtRoom) {
        // construct the array given a collection of ConfigurePublicNotices

        DisplayablePublicNoticeValue[] displayablePublicNoticeValues =
            constructDisplayablePublicNoticeValuesForCourtRoom(xhbCourtRoomId, courtRoom);

        LOG.debug(" 49 Sorting the Array into Priority");

        // sort the array of displayablePublicNoticeValues
        Arrays.sort(displayablePublicNoticeValues);

        LOG.debug(" 50 Exiting the getAllPublicNoticesForCourtRoom method");

        return displayablePublicNoticeValues;
    }

    /**
     * Sets the Acivation Status on the Configured Public Notices which correspond to the Displayable
     * Public notices passed in. If an activation status is updated it sends a notification to the
     * Public Display System.
     * 
     * @param publicNotices Array of displayablePublicNotices with updated isactive status
     * @param xhbCourtRoomId the target court room ID
     */
    public static void setAllPublicNoticesForCourtRoom(DisplayablePublicNoticeValue[] publicNotices,
        int xhbCourtRoomId) {
        boolean isNotificationReqd = false;
        boolean reportingRestrictionsChanged = false;

        LOG.debug(" 01 Enter the setAllPublicNoticesForCourtRoom method");

        // need to check the PublicNoticeInvalidSelection does not encroach on
        // any rules
        PublicNoticeSelectionValidator.validateSelection(publicNotices);

        LOG.debug("02 public notices are validated");

        // iterate through the array of displayable are validated public Notices
        for (DisplayablePublicNoticeValue publicNotice : publicNotices) {

            // update if marked as dirty( IsActive/status change)
            if (publicNotice.isDirtyFlagged()) {
                updateConfiguredPublicNoticeActivationState(publicNotice);
                // if any of the ActivationStates are updated need to send a
                // notification to Public displays.
                isNotificationReqd = true;
                Integer definitiveNotice = publicNotice.getDefinitivePublicNotice();

                // Need to determine whether reporting restrictions were changed
                // or not.
                if (!reportingRestrictionsChanged && definitiveNotice.equals(REPORTING_RESTRICTIONS)
                    || definitiveNotice.equals(REPORTING_RESTRICTIONS_LIFTED)) {
                    reportingRestrictionsChanged = true;
                }
            }
        }

        // Fire a Notification to the Public Display systems to update
        // If something has been updated. Only want to send ONE notification.
        if (isNotificationReqd) {
            LOG.debug("Send the Notication to Public displays");
            sendNotification(xhbCourtRoomId, reportingRestrictionsChanged);
        }
    }

    /**
     * Based on the CourlogSubscriptions event type it decides what configured public notices Activation
     * Status is updated(If any ). When an Activation status is updated a notification is sent to the
     * public Displays subsystem.
     * 
     * @param courtLogSubscriptionValue The new publicNoticeforCourtRoom value
     * @throws PublicNoticeException Description of the Exception
     */

    public static void setPublicNoticeforCourtRoom(CourtLogSubscriptionValue courtLogSubscriptionValue) {

        LOG.debug("{}{}",
            " 01 Enter the setPublicNoticeforCourtRoom xhbCourtRoomID", courtLogSubscriptionValue.getCourtRoomId());

        // updates are carried out need to send a notification
        if (updateConfiguredPublicNoticeActivationState(courtLogSubscriptionValue)) {
            LOG.debug("{}{}", " 0 updated sending Event", courtLogSubscriptionValue.getCourtRoomId());
            sendNotification(courtLogSubscriptionValue);
        }
    }

    /**
     * updateConfiguredPublicNoticeActivationState updates the Activation State based on the contents of
     * the DisplayablePublicNotice Value.
     * 
     * @param publicNotice parameter for updateConfiguredPublicNoticeActivationState
     */
    private static void updateConfiguredPublicNoticeActivationState(DisplayablePublicNoticeValue publicNotice) {
        PublicNoticeMaintainer.updateIsActive(publicNotice);
    }

    /**
     * updateConfiguredPublicNoticeActivationState.
     * 
     * @param courtLogSubscriptionValue parameter for updateConfiguredPublicNoticeActivationState
     * @return the returned boolean
     * @throws PublicNoticeException Exception
     */
    private static boolean updateConfiguredPublicNoticeActivationState(
        CourtLogSubscriptionValue courtLogSubscriptionValue) {
        return PublicNoticeSelectionManipulator.manipulateSelection(courtLogSubscriptionValue);
    }

    /**
     * sendNotification sends a notification to the public display system.
     * 
     * @param courtLogSubscriptionValue parameter for sendNotification
     */
    private static void sendNotification(CourtLogSubscriptionValue courtLogSubscriptionValue) {
        LOG.debug("sendNotification({})", courtLogSubscriptionValue);
        PublicNoticeChangeNotifier.sendNotificationtoPublicDisplays(courtLogSubscriptionValue);
    }

    /**
     * sendNotification sends a notification to the public display system.
     * 
     * @param xhbCourtRoomId parameter for sendNotification
     */
    private static void sendNotification(int xhbCourtRoomId, boolean reportingRestrictionsChanged) {
        LOG.debug("sendNotification({},{})", xhbCourtRoomId, reportingRestrictionsChanged);
        PublicNoticeChangeNotifier.sendNotificationtoPublicDisplays(xhbCourtRoomId, reportingRestrictionsChanged);
    }

    /**
     * constructs the Array of DisplayablePublicNoticeValue objects given an Xhibit Court Room Id.
     * 
     * @return Array of DisplayPublicNotices
     */

    private static DisplayablePublicNoticeValue[] constructDisplayablePublicNoticeValuesForCourtRoom(
        Integer courtRoomId, Optional<XhbCourtRoomDao> courtRoom) {
        LOG.debug("constructDisplayablePublicNoticeValuesForCourtRoom({},{})", courtRoomId, courtRoom);
        if (!courtRoom.isPresent()) {
            LOG.debug("CourtRoom Not Present");
            throw new PublicNoticeCourtRoomUnknownException(courtRoomId);
        }

        Collection<?> configuredPublicNotices = courtRoom.get().getXhbConfiguredPublicNotices();

        ArrayList<Object> displayablePublicNotices = new ArrayList<>();
        Iterator<?> iterator = configuredPublicNotices.iterator();
        while (iterator.hasNext()) {
            displayablePublicNotices.add(createDisplayablePublicNotice((XhbConfiguredPublicNoticeDao) iterator.next()));
        }

        return displayablePublicNotices.toArray(new DisplayablePublicNoticeValue[0]);
    }

    private static DisplayablePublicNoticeValue createDisplayablePublicNotice(
        XhbConfiguredPublicNoticeDao configuredPublicNotice) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}{}", " creating a displayablePublicNoticeValue for configredPN ",
                configuredPublicNotice.getConfiguredPublicNoticeId());
        }

        XhbPublicNoticeDao publicNotice = configuredPublicNotice.getXhbPublicNotice();
        XhbDefinitivePublicNoticeDao definitivePublicNotice = publicNotice.getXhbDefinitivePublicNotice();

        boolean isActive = false;
        if (configuredPublicNotice.getIsActive() != null && configuredPublicNotice.getIsActive().equals("1")) {
            isActive = true;
        }

        return new DisplayablePublicNoticeValue(configuredPublicNotice.getConfiguredPublicNoticeId(),
            publicNotice.getPublicNoticeDesc(), isActive, configuredPublicNotice.getVersion(),
            definitivePublicNotice.getDefinitivePnId(), definitivePublicNotice.getPriority());
    }

}
