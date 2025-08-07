package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.common.publicdisplay.util.comparables.DigitAwareStringComparable;

/**
 * <p>
 * Title: Adapter class around XhbDisplayDAO so that they can be sorted in a more logical order.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: DisplayDAOSortAdapter.java,v 1.1 2004/01/30 16:18:18 sz0t7n Exp $
 */

public class DisplayBasicValueSortAdapter extends DigitAwareStringComparable {

    private static final long serialVersionUID = 2188624613620306735L;
    
    private static final Logger LOG = LoggerFactory.getLogger(DisplayBasicValueSortAdapter.class);

    private final XhbDisplayDao value;

    public DisplayBasicValueSortAdapter(XhbDisplayDao value, String displayText) {
        super(displayText);
        this.value = value;
    }

    public XhbDisplayDao getDao() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        LOG.debug("equals()");
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
