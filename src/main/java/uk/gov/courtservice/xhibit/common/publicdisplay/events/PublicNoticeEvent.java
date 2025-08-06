package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;

/**

 * Title: Public Notice Event.

 * Description: This event is generated when the public notices are changed for a court room

 * Copyright: Copyright (c) 2002

 * Company: EDS

 * @author Rakesh Lakhani
 * @version 1.0
 */
public class PublicNoticeEvent extends CourtRoomEvent {

    static final long serialVersionUID = 5400059004972594976L;

    // Defaults to false, so do not need to do redundant assignment.
    private boolean reportingRestrictionsChanged;

    /**
     * Specify the court room for which the public notices have changed, and no reporting
     * restriction changes.

     * @param courtRoomIdentifier The court room for which the change applies.
     */
    public PublicNoticeEvent(CourtRoomIdentifier courtRoomIdentifier) {
        super(courtRoomIdentifier);
    }

    /**
     * Constructor that allows the explicit setting of whether reporting restrictions has changed.

     * @param courtRoomIdentifier The court room for which the change applies.
     * @param reportingRestrictionsChanged Whether reporting restrictions has changed.
     */
    public PublicNoticeEvent(CourtRoomIdentifier courtRoomIdentifier,
        boolean reportingRestrictionsChanged) {
        this(courtRoomIdentifier);
        this.reportingRestrictionsChanged = reportingRestrictionsChanged;
    }

    /**
     * Get event type.

     * @return eventType
     */
    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.PUBLIC_NOTICE_EVENT);
    }

    /**
     * Returns whether the reporting restrictions state has changed.

     * @return true if the reporting restrictions norice has been set or unset, false otherwise.
     */
    public boolean isReportingRestrictionsChanged() {
        return reportingRestrictionsChanged;
    }
}
