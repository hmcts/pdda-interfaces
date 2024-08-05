package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

import java.util.Optional;

/**
 * <p>
 * Title: Looks after the Notification coming from Public Notices susbsytem.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * 
 * @author Pat Fox,Bob Boles
 * @created 17 February 2003
 * @version 1.0 This Object is responsibe for firing the correct event to the correct Queue which
 *          the public display system is listening.
 */
public final class PublicNoticeChangeNotifier {
    private static final Logger LOG = LoggerFactory.getLogger(PublicNoticeChangeNotifier.class);

    private static final Integer REPORTING_RESTRICTIONS = Integer.valueOf(21_200);

    private static final Integer REPORTING_RESTRICTIONS_LIFTED = Integer.valueOf(21_201);

    /**
     * Constructor for the PublicNoticeChangeNotifier object.
     */
    private PublicNoticeChangeNotifier() {
        // private constructor
    }

    /**
     * This takes in a CourtRoom Id and sends an PublicNoticeSubscriptionValue to the correct JMS Queue
     * which Public Displays is listening on.
     * 
     * @param xhbCourtRoomId Description of the Parameter
     */
    public static void sendNotificationtoPublicDisplays(int xhbCourtRoomId, boolean reportingRestrictionsChanged) {
        sendNotificationToNewPublicDisplays(xhbCourtRoomId, reportingRestrictionsChanged);
    }

    /**
     * This takes in a CourtLogSubscriptionValue Id and sends an PublicNoticeSubscriptionValue to the
     * correct JMS Queue which Public Displays is listening on.
     * 
     * @param courtLogSubscriptionValue Description of the Parameter
     */
    public static void sendNotificationtoPublicDisplays(CourtLogSubscriptionValue courtLogSubscriptionValue) {
        LOG.debug("sendNotificationtoPublicDisplays()");
        Integer eventType = courtLogSubscriptionValue.getCourtLogViewValue().getEventType();

        sendNotificationToNewPublicDisplays(courtLogSubscriptionValue,
            eventType.equals(REPORTING_RESTRICTIONS) || eventType.equals(REPORTING_RESTRICTIONS_LIFTED));
    }

    private static void sendNotificationToNewPublicDisplays(int xhbCourtRoomId, boolean reportingRestrictionsChanged) {
        LOG.debug("sendNotificationToNewPublicDisplays({},{})", xhbCourtRoomId, reportingRestrictionsChanged);
        Optional<XhbCourtRoomDao> courtRoom = PddaEntityHelper.xcrtFindByPrimaryKey(xhbCourtRoomId);
        if (courtRoom.isPresent()) {
            Optional<XhbCourtSiteDao> courtSite =
                PddaEntityHelper.xcstFindByPrimaryKey(courtRoom.get().getCourtSiteId());
            if (courtSite.isPresent()) {
                CourtRoomIdentifier courtRoomId =
                    new CourtRoomIdentifier(courtSite.get().getCourtId(), courtRoom.get().getCourtRoomId());
                PublicDisplayNotifier publicDisplayNotifier = new PublicDisplayNotifier();
                publicDisplayNotifier.sendMessage(new PublicNoticeEvent(courtRoomId, reportingRestrictionsChanged));
            }
        }
    }

    private static void sendNotificationToNewPublicDisplays(CourtLogSubscriptionValue courtLogSubscriptionValue,
        boolean reportingRestrictionsChanged) {
        sendNotificationToNewPublicDisplays(courtLogSubscriptionValue.getCourtRoomId().intValue(),
            reportingRestrictionsChanged);
    }
}
