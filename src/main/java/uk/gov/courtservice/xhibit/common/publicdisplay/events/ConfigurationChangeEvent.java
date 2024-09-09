package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;

/**
 * <p>
 * Title: ConfigurationChangeEvent.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
public class ConfigurationChangeEvent implements PublicDisplayEvent {

    static final long serialVersionUID = 8303326719524067907L;

    private final CourtConfigurationChange change;

    /**
     * Creates a new ConfigurationChangeEvent object.
     * 
     * @param change CourtConfigurationChange
     */
    public ConfigurationChangeEvent(CourtConfigurationChange change) {
        this.change = change;
    }

    /**
     * getChange.
     * 
     * @return CourtConfigurationChange
     */
    public CourtConfigurationChange getChange() {
        return change;
    }

    /**
     * getEventType.
     * 
     * @return EventType
     */
    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.CONFIGURATION_EVENT);
    }

    /**
     * Get the court ID for this event.
     * 
     * @return the court Id for this event.
     */
    @Override
    public Integer getCourtId() {
        return change.getCourtId();
    }
}
