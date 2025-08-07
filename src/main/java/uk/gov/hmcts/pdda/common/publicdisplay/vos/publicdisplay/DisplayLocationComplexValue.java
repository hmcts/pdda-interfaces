package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.vos.CsAbstractValue;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;

/**
 * <p>
 * Title: Display Location Complex Value.
 * </p>
 * 
 * <p>
 * Description: VO that holds information about the location and the display within that location
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
 * @author Rakesh Lakhani
 * @version $Id: DisplayLocationComplexValue.java,v 1.1 2004/01/15 10:34:19 rz3jq5 Exp $
 */
public class DisplayLocationComplexValue extends CsAbstractValue {

    static final long serialVersionUID = -7278823187046747441L;
    
    private static final Logger LOG = LoggerFactory.getLogger(DisplayLocationComplexValue.class);

    private XhbDisplayLocationDao displayLocationDao;

    private XhbDisplayDao[] displayDao;

    /**
     * Get the list of displays in the location.
     * 
     * @return array of display basic values
     */
    public XhbDisplayDao[] getDisplayDao() {
        return displayDao.clone();
    }

    /**
     * Set the displays in the location.
     * 
     * @param displayDao array of display basic values
     */
    public void setDisplayDaos(XhbDisplayDao... displayDao) {
        this.displayDao = displayDao.clone();
    }

    /**
     * Set a location.
     * 
     * @param displayLocationDao the location basic value
     */
    public void setDisplayLocationDao(XhbDisplayLocationDao displayLocationDao) {
        this.displayLocationDao = displayLocationDao;
    }

    /**
     * Get information about the location.
     * 
     * @return XhbDisplayLocationDAO
     */
    public XhbDisplayLocationDao getDisplayLocationDao() {
        return displayLocationDao;
    }

    /**
     * Override the equals method to compare the id's of the associated basic value.
     * 
     * @param complexValue A DisplayLocationComplexValue
     * 
     * @return boolean
     */
    @Override
    public boolean equals(Object complexValue) {
        if (displayLocationDao != null && complexValue instanceof DisplayLocationComplexValue
            && ((DisplayLocationComplexValue) complexValue).displayLocationDao != null) {
            return ((DisplayLocationComplexValue) complexValue).displayLocationDao
                .equals(displayLocationDao);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
