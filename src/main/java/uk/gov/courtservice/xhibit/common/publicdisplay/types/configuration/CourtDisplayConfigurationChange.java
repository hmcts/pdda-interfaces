package uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration;

/**
 * <p>
 * Title: Court Display configuration change.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Subtype of <code>CourtConfigurationChange</code> used to signify that a particular Display has
 * had it's configuration changed. This will usually occur as a result of either changing the
 * Rotation Set or changing the Court Rooms that the Display shows information about. The Display to
 * which this occurs will be refreshed to reflect the new configuration.
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
 * @author Bob Boothby
 * @version 1.0
 */
public class CourtDisplayConfigurationChange extends CourtConfigurationChange {

    static final long serialVersionUID = 5164433718490600709L;

    private final int displayId;

    /**
     * Creates a new CourtDisplayConfigurationChange object that will not result in the display
     * documents connected to the display display being rerendered.
     * 
     * @param courtId The court for which this change applies.
     * @param displayId The display to which this change applies.
     */
    public CourtDisplayConfigurationChange(final Integer courtId, final Integer displayId) {
        this(courtId, displayId, Boolean.FALSE);
    }

    /**
     * Creates a new CourtDisplayConfigurationChange object.
     * 
     * @param courtId The court for which this change applies.
     * @param displayId The display to which this change applies.
     * @param forceRecreate this flag is used to indicate that the display documents connected to
     *        the display need to be rerendered
     */
    public CourtDisplayConfigurationChange(final Integer courtId, final Integer displayId,
        boolean forceRecreate) {
        super(courtId, forceRecreate);
        this.displayId = displayId;
    }

    /**
     * Gets the ID for the display to which this change applies.
     */
    public int getDisplayId() {
        return displayId;
    }
}
