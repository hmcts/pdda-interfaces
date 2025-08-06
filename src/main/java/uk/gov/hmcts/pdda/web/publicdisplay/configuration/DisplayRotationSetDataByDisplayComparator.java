package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;

import java.util.Comparator;

/**

 * Title: Comparator for sorting <code>DisplayRotationSetData</code> by display ID.


 * Description:


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 * @version 1.0
 */
public final class DisplayRotationSetDataByDisplayComparator implements Comparator<Object> {
    private static final DisplayRotationSetDataByDisplayComparator INSTANCE =
        new DisplayRotationSetDataByDisplayComparator();

    /**
     * Get an instance of the comparator.

     * @return An instance of the comparator.
     */
    public static DisplayRotationSetDataByDisplayComparator getInstance() {
        return INSTANCE;
    }

    private DisplayRotationSetDataByDisplayComparator() {
    }

    /**
     * Returns an ordering for two instances of <code>DisplayRotationSetData</code> based on the
     * Display's ID.

     * @param o1 The first instance of <code>DisplayRotationSetData</code>.
     * @param o2 The second instance of <code>DisplayRotationSetData</code>.
     * @return -1 if the o1's display ID is less than o2's, 0 if they are the same and +1 if o1's
     *         display ID is greater.
     * @throws ClassCastException Exception
     */
    @Override
    public int compare(Object o1, Object o2) {
        long firstId = ((DisplayRotationSetData) o1).getDisplayId();
        long secondId = ((DisplayRotationSetData) o2).getDisplayId();
        long result = firstId - secondId;
        // Doing the division means that result/result is always going to
        // be -1, 0 or 1 and so will always be storeable in an int.
        // If we don't do this, then we could end up with an interesting
        // situation when the difference between the two longs is greater
        // than Integer.MAV_VALUE.
        if (result != 0) {
            result = result / Math.abs(result);
        }
        return (int) result;
    }

    /**
     * Checks whether the passed in object is equivalent to this one.

     * @param obj The object to check for equality.
     * @return true if obj is equivalent to this one.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 1;
    }
}
