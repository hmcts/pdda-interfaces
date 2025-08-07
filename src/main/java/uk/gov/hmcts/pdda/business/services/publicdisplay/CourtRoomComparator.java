package uk.gov.hmcts.pdda.business.services.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;

import java.util.Comparator;

/**
 * <p>
 * Title: Comparator used to sort court rooms.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Initial naive implementation that sorts purely based on the court room name. Implemented as a
 * 'factory' style singleton so that we can easily change implementation in the future.
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

public final class CourtRoomComparator implements Comparator<XhbCourtRoomDao> {
    private static final Logger LOG = LoggerFactory.getLogger(CourtRoomComparator.class);
    private static final CourtRoomComparator INSTANCE = new CourtRoomComparator();

    private CourtRoomComparator() {
    }

    /**
     * Get a comparator to sort court rooms.
     * 
     * @return A class implementing Comparator able to sort by <code>XhbCourtRoom</code>.
     */
    public static Comparator<XhbCourtRoomDao> getInstance() {
        return INSTANCE;
    }

    /**
     * Compares two instances of <code>XhbCourtRoom</code> in order to provide a natural order based
     * on their CREST court room numbers.
     * 
     * @param o1 First instance of <code>XhbCourtRoom</code> to compare.
     * @param o2 Second instance of <code>XhbCourtRoom</code> to compare
     * @return as per spec of Comparator interface.
     * @throws ClassCastException If an object not of type <code>XhbCourtRoom</code> is passed.
     */
    @Override
    public int compare(XhbCourtRoomDao o1, XhbCourtRoomDao o2) {
        return o1.getCrestCourtRoomNo().compareTo(o2.getCrestCourtRoomNo());
    }

    /**
     * Checks whether the object passed in is equivalent to this class.
     * 
     * @param obj The object check equivalence on.
     * @return true if equivalent.
     */
    @Override
    public boolean equals(Object obj) {
        LOG.debug("equals()");
        return this == obj;
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
