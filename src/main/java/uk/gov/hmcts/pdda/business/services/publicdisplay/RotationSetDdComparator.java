package uk.gov.hmcts.pdda.business.services.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;

import java.util.Comparator;

/**
 * <p>
 * Title: Comparator used to sort RotationSetDisplayDocuments.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * This comparator sorts arrays of <code>RotationSetDisplayDocuments</code> by their 'Ordering'
 * field.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */

public final class RotationSetDdComparator implements Comparator<XhbRotationSetDdDao> {
    private static final Logger LOG = LoggerFactory.getLogger(RotationSetDdComparator.class);
    private static final RotationSetDdComparator INSTANCE = new RotationSetDdComparator();

    private RotationSetDdComparator() {
        // Private constructor
    }

    /**
     * Get a comparator to sort court rooms.
     * 
     * @return A class implementing Comparator able to sort by <code>XhbCourtRoom</code>.
     */
    public static Comparator<XhbRotationSetDdDao> getInstance() {
        return INSTANCE;
    }

    /**
     * Compares two instances of <code>XhbRotationSetDd</code> in order to provide a natural order
     * based on their ordering fields.
     * 
     * @param o1 First instance of <code>XhbRotationSetDd</code> to compare.
     * @param o2 Second instance of <code>XhbRotationSetDd</code> to compare
     * @return as per spec of Comparator interface.
     * @throws ClassCastException If an object not of type <code>XhbRotationSetDd</code> is passed.
     */
    @Override
    public int compare(XhbRotationSetDdDao o1, XhbRotationSetDdDao o2) {
        return o1.getOrdering().compareTo(o2.getOrdering());
    }

    /**
     * Checks whether the object passed in is equivalent to this class.
     * 
     * @param obj The object check equivalence on.
     * @return true if equivalent.
     */
    @Override
    public boolean equals(Object obj) {
        LOG.debug("equals");
        return this == obj;
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode");
        return super.hashCode();
    }
}
