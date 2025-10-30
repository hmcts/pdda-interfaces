package uk.gov.courtservice.xhibit.courtlog.vos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CourtLogCrudValue extends CourtLogAbstractValue {

    private static final long serialVersionUID = 371289955235964063L;

    /** Standard tags used in the XML generated from the Value Object. */
    public static final String ENTRY_DATE = "date";

    public static final String ENTRY_TIME = "time";

    public static final String EVENT_TYPE = "type";

    public static final String ENTRY_FREE_TEXT = "free_text";

    /**
     * Used to hold the event-specific properties, which are converted to XML.
     */
    private Map<String, Object> propertyMap = new ConcurrentHashMap<>();

    /** Indicates whether the Court Log entry has been made in court. */
    private boolean inCourt;

    /** Free text associated with the Court Log Entry. */
    private String entryFreeText;

    public CourtLogCrudValue() {
        super();
    }

    public CourtLogCrudValue(Integer version) {
        super(version);
    }

    /**
     * Sets the entire property map.

     * @param propertyMap the map of properties to set
     */
    public void setPropertyMap(Map<String, Object> propertyMap) {
        this.propertyMap = new HashMap<>(Objects.requireNonNull(propertyMap));
    }

    /**
     * Returns the current property map.

     * @return an unmodifiable view of the property map
     */
    public Map<String, Object> getPropertyMap() {
        return Map.copyOf(propertyMap);
    }

    /**
     * Sets or updates an individual event property.

     * @param key   the property name
     * @param value the property value
     */
    public void setProperty(String key, Object value) {
        propertyMap.put(Objects.requireNonNull(key), value);
    }

    /**
     * Retrieve an event property.

     * @param key name of the property.
     * @return The value of the property.
     */
    public Object getProperty(Object key) {
        return this.propertyMap.get(key);
    }

    public void setInCourt(boolean inCourt) {
        this.inCourt = inCourt;
    }

    public boolean isInCourt() {
        return this.inCourt;
    }

    /**
     * Set the Free text for the entry.

     * @param entryFreeText text to set.
     */
    public void setEntryFreeText(String entryFreeText) {
        this.entryFreeText = entryFreeText;
    }

    /**
     * Get the entry free text.

     * @return the entry free text.
     */
    public String getEntryFreeText() {
        return this.entryFreeText;
    }
}

