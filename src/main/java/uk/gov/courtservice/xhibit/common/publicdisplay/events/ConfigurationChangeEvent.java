package uk.gov.courtservice.xhibit.common.publicdisplay.events;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;

/**

 * Title: ConfigurationChangeEvent.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
public class ConfigurationChangeEvent implements PublicDisplayEvent {

    static final long serialVersionUID = 8303326719524067907L;

    private CourtConfigurationChange change;

    /**
     * Creates a new ConfigurationChangeEvent object.

     * @param change CourtConfigurationChange
     */
    public ConfigurationChangeEvent(CourtConfigurationChange change) {
        this.change = change;
    }

    /**
     * getChange.

     * @return CourtConfigurationChange
     */
    public CourtConfigurationChange getChange() {
        return change;
    }
    
    public void setConfigurationChange(CourtConfigurationChange change) {
        this.change = change;
    }

    /**
     * getEventType.

     * @return EventType
     */
    @Override
    public EventType getEventType() {
        return EventType.getEventType(EventType.CONFIGURATION_EVENT);
    }
    
    @Override
    public Integer getCourtId() {
        return change.getCourtId();
    }
}
