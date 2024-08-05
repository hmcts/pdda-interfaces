package uk.gov.courtservice.xhibit.common.publicdisplay.events.types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: Event Type.
 * </p>
 * 
 * <p>
 * Description: Identifier used to easily identify the type of Event generated
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: EventType.java,v 1.3 2006/06/05 12:28:23 bzjrnl Exp $
 */
public final class EventType {
    /**
     * Event type for configuration events. Value=ConfigurationEvent.
     */
    public static final String CONFIGURATION_EVENT = "ConfigurationEvent";

    /**
     * Event type for update case events. Value=UpdateCaseEvent.
     */
    public static final String UPDATE_CASE_EVENT = "UpdateCaseEvent";

    /**
     * Event type for move case events. Value=MoveCaseEvent.
     */
    public static final String MOVE_CASE_EVENT = "MoveCaseEvent";

    /**
     * Event type for add case events. Value=AddCaseEvent.
     */
    public static final String ADD_CASE_EVENT = "AddCaseEvent";

    /**
     * Event type for case status (ie. court log events) events. Value=CaseStatusEvent.
     */
    public static final String CASE_STATUS_EVENT = "CaseStatusEvent";

    /**
     * Event type for hearing status events. Value=HearingStatusEvent.
     */
    public static final String HEARING_STATUS_EVENT = "HearingStatusEvent";

    /**
     * Event type for public notice events. Value=PublicNoticeEvent.
     */
    public static final String PUBLIC_NOTICE_EVENT = "PublicNoticeEvent";

    /**
     * Event type for public notice events. Value=ActivateCaseEvent.
     */
    public static final String ACTIVATE_CASE_EVENT = "ActivateCaseEvent";

    /**
     * Map to store the event_types.
     */
    private static final Map<String, EventType> EVENT_TYPE_MAP = new ConcurrentHashMap<>();

    /**
     * Static initialiser to populate the hashmap with event types.
     */
    static {
        EVENT_TYPE_MAP.put(CONFIGURATION_EVENT, new EventType(CONFIGURATION_EVENT));
        EVENT_TYPE_MAP.put(UPDATE_CASE_EVENT, new EventType(UPDATE_CASE_EVENT));
        EVENT_TYPE_MAP.put(MOVE_CASE_EVENT, new EventType(MOVE_CASE_EVENT));
        EVENT_TYPE_MAP.put(ADD_CASE_EVENT, new EventType(ADD_CASE_EVENT));
        EVENT_TYPE_MAP.put(CASE_STATUS_EVENT, new EventType(CASE_STATUS_EVENT));
        EVENT_TYPE_MAP.put(HEARING_STATUS_EVENT, new EventType(HEARING_STATUS_EVENT));
        EVENT_TYPE_MAP.put(PUBLIC_NOTICE_EVENT, new EventType(PUBLIC_NOTICE_EVENT));
        EVENT_TYPE_MAP.put(ACTIVATE_CASE_EVENT, new EventType(ACTIVATE_CASE_EVENT));
    }

    // Used to store the identifier
    private final String value;

    /**
     * Creates an event type with a string identifier External objects should use the static
     * variables.
     * 
     * @param value This is one of the public static values
     */
    private EventType(String value) {
        this.value = value;
    }

    /**
     * Returns the event type. Where possible use the static strings for requesting the EventType.
     * 
     * @param eventType This is one of the public static values
     * 
     * @return the EventType identifier. Illegal Arguement thrown if invalid parameter supplied
     */
    public static EventType getEventType(String eventType) {
        if (EVENT_TYPE_MAP.containsKey(eventType)) {
            return EVENT_TYPE_MAP.get(eventType);
        } else {
            throw new IllegalArgumentException("Event type unknown:" + eventType);
        }
    }

    /**
     * Method delegated to String.
     * 
     * @param anObject Must be an EventType
     * 
     * @return checks equality with the string identifier
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof EventType) {
            return value.equals(((EventType) anObject).value);
        } else {
            return false;
        }
    }

    /**
     * Method delegated to String.
     * 
     * @return Hash code of the string identifier
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Method delegated to String.
     * 
     * @return The string identifier used in the constructor
     */
    @Override
    public String toString() {
        return value;
    }
}
