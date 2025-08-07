package uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration;

/**
 * <p>
 * Title: CourtRotationSetConfigurationChange.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Subtype of <code>CourtConfigurationChange</code> used to signify that a particular Rotation Set
 * has had it's configuration changed. This will occur when the assignment of Display Documents to
 * the Rotation Set being altered. As a result of this change all Displays using the Rotation Set
 * will be updated.
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
public class CourtRotationSetConfigurationChange extends CourtConfigurationChange {

    static final long serialVersionUID = 5393913310600248721L;

    private final int rotationSetId;

    /**
     * Creates a new CourtRotationSetConfigurationChange object. Will not force recreation of
     * associated documents.
     * 
     * @param courtId The database ID of the court involved in the change.
     * @param rotationSetId The database ID of the Rotation Set involved in the change.
     */
    public CourtRotationSetConfigurationChange(final Integer courtId, final Integer rotationSetId) {
        this(courtId, rotationSetId, Boolean.FALSE);
    }

    /**
     * Creates a new CourtRotationSetConfigurationChange object.
     * 
     * @param courtId The database ID of the court involved in the change.
     * @param rotationSetId The database ID of the Rotation Set involved in the change.
     * @param forceRecreate Whether to for the recreation of all documents associated with the
     *        rotation set.
     */
    public CourtRotationSetConfigurationChange(final Integer courtId, final Integer rotationSetId,
        boolean forceRecreate) {
        super(courtId, forceRecreate);
        this.rotationSetId = rotationSetId;
    }

    /**
     * Get the ID of the rotation set involved in the change.
     * 
     * @return The database ID of the Rotation Set involved in the change.
     */
    public int getRotationSetId() {
        return rotationSetId;
    }
}
