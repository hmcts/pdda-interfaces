package uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;

import java.io.Serializable;
import java.util.Arrays;

/**

 * Title: Rotation Set data for a Display.

 * Description:

 * This class encapsulates the data about how a Display is configured with respect to a Rotation
 * Set. It is explicitly designed to provide the view of the configuration data most suitable for
 * driving the presentation tier.

 * This class is designed to be immutable and so makes certain assumptions about caching a
 * pre-calculated hash. If this class should be made mutable please remove the hash pre-calculation.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bob Boothby
 * @version 1.0
 */
public class DisplayRotationSetData implements Serializable {

    static final long serialVersionUID = 6656214779744672384L;

    private final DisplayUri displayUri;

    private RotationSetDisplayDocument[] rotationSetDisplayDocuments;

    private final int displayId;

    private final String displayType;

    private final int rotationSetId;

    private final int objectHashCode;

    /**
     * Constructor for the data element of a Display Rotation Set.

     * @param displayUri The URI of the display it is associated with.
     * @param rotationSetDisplayDocuments The URI(s) of the Display Documents.
     * @param displayId The database ID of the display.
     * @param rotationSetId The database ID of the Rotation Set.

     * @pre displayDocumentURIs != null
     * @pre forall RotationSetDisplayDocument doc in displayDocumentURIs | doc != null
     * @pre displayURI != null
     */
    public DisplayRotationSetData(DisplayUri displayUri,
        RotationSetDisplayDocument[] rotationSetDisplayDocuments, int displayId, int rotationSetId,
        String displayType) {
        this.displayUri = displayUri;
        setRotationSetDisplayDocuments(rotationSetDisplayDocuments);
        this.displayId = displayId;
        this.rotationSetId = rotationSetId;
        this.objectHashCode = calculateHashcode();
        this.displayType = displayType;
    }

    /**
     * Get the ID of the Display 'embodied' by this class.

     * @return the database ID of the Display
     */
    public int getDisplayId() {
        return displayId;
    }

    /**
     * Get the URI of the display for which this Display Rotation Set applies.

     * @return The instance of <code>DisplayURI</code> for the particular Display.
     */
    public DisplayUri getDisplayUri() {
        return displayUri;
    }

    /**
     * Gets the objects representing the Display Documents making up the rotation set as applied to
     * the Display.

     * @return an array of <code>RotationSetDisplayDocument</code> representing the Display
     *         Documents in this Display Rotation Set.
     */
    public RotationSetDisplayDocument[] getRotationSetDisplayDocuments() {
        return rotationSetDisplayDocuments.clone();
    }
    
    /**
     * Sets the objects representing the Display Documents making up the rotation set as applied to
     * the Display.
     */
    private void setRotationSetDisplayDocuments(RotationSetDisplayDocument... rotationSetDisplayDocuments) {
        this.rotationSetDisplayDocuments = rotationSetDisplayDocuments.clone();
    }

    /**
     * Get the ID of the Rotation Set 'embodied' by this class.

     * @return the database ID of the Rotation Set
     */
    public int getRotationSetId() {
        return rotationSetId;
    }

    /**
     * Get the code representing the type of the display.

     * @return the code representing the type of the display.
     */
    public String getDisplayType() {
        return displayType;
    }

    /**
     * Indicates whether some other object is "equal to" this one.

     * @param obj the reference object with which to compare.

     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DisplayRotationSetData) {
            DisplayRotationSetData testable = (DisplayRotationSetData) obj;
            return displayId == testable.displayId && rotationSetId == testable.rotationSetId
                && Arrays.equals(rotationSetDisplayDocuments, testable.rotationSetDisplayDocuments);
        }

        return false;
    }

    /**
     * Get the hashcode for this object.

     * @return This object's hashcode.

     * @see java.lang.Object.hashCode
     */
    @Override
    public int hashCode() {
        return objectHashCode;
    }

    /**
     * Calculate the hashcode for this object.

     * @return the calculated hash code.

     * @pre forall RotationSetDisplayDocument doc in _rotationSetDisplayDocuments | doc != null
     */
    private int calculateHashcode() {
        // Expect overflow here, but for the purpose of a hash, it is fine.
        long sum = (long) displayId + rotationSetId + displayUri.hashCode();

        for (int i = rotationSetDisplayDocuments.length - 1; i >= 0; i--) {
            sum += rotationSetDisplayDocuments[i].hashCode();
        }

        return (int) sum;
    }
}
