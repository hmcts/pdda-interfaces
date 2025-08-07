package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.vos.CsAbstractValue;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Title: CourtSitePDComplexValue.
 * </p>
 * 
 * <p>
 * Description: Complex value object that builds a tree of information for a court site and the
 * locations within it (<code>DisplayLocationComplexValue</code>)
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
 * @version $Id: CourtSitePDComplexValue.java,v 1.1 2004/01/15 10:34:17 rz3jq5 Exp $
 * 
 * @see DisplayLocationComplexValue
 */
public class CourtSitePdComplexValue extends CsAbstractValue {

    private static final long serialVersionUID = -8705423954144444243L;

    private static final Logger LOG = LoggerFactory.getLogger(CourtSitePdComplexValue.class);

    /**
     * Hash set that stores all the location complex values.
     */
    private final Set<DisplayLocationComplexValue> displayLocationComplexValues = new HashSet<>();

    private XhbCourtSiteDao courtSiteDao;

    /**
     * Set a court site for the VO.
     * 
     * @param courtSiteDao The court site
     */
    public void setCourtSiteDao(XhbCourtSiteDao courtSiteDao) {
        this.courtSiteDao = courtSiteDao;
    }

    /**
     * Get the court site that this VO is for.
     * 
     * @return XhbCourtSiteDAO
     */
    public XhbCourtSiteDao getCourtSiteDao() {
        return courtSiteDao;
    }

    /**
     * Get all the locations for the court site.
     * 
     * @return An array of DisplayLocationComplexValue
     */
    public DisplayLocationComplexValue[] getDisplayLocationComplexValue() {
        return displayLocationComplexValues
            .toArray(new DisplayLocationComplexValue[0]);
    }

    /**
     * Add a display location to the hash set. If one already exists (based on the equals method in
     * DisplayLocationComplexValue) then that one will be replaced.
     * 
     * @param displayLocationComplexValue The Location to add
     */
    public void addDisplayLocationComplexValue(
        DisplayLocationComplexValue displayLocationComplexValue) {
        displayLocationComplexValues.add(displayLocationComplexValue);
        LOG.debug("Adding display location complex "
            + displayLocationComplexValue.getDisplayLocationDao().getPrimaryKey());
    }
}
