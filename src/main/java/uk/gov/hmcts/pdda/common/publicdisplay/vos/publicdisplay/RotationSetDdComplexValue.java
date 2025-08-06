package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.vos.CsAbstractValue;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;

/**

 * Title: Rotation Set / Display Document complex value.

 * Description: This VO holds information from the rotation set DD basic value for ordering and
 * delay, and the display document basic value for identifying the document.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: RotationSetDDComplexValue.java,v 1.2 2004/03/29 14:09:57 pznwc5 Exp $
 */
public class RotationSetDdComplexValue extends CsAbstractValue {

    static final long serialVersionUID = -6304749725662813977L;
    
    private static final Logger LOG = LoggerFactory.getLogger(RotationSetDdComplexValue.class);

    private XhbDisplayDocumentDao displayDocumentDao;

    private XhbRotationSetDdDao rotationSetDdDao;

    /**
     * Create the complex value with the display document and the rotation set DD.

     * @param rotationSetDdDao the many-to-many relationship
     * @param displayDocumentDao the display document
     */
    public RotationSetDdComplexValue(XhbRotationSetDdDao rotationSetDdDao,
        XhbDisplayDocumentDao displayDocumentDao) {
        super();
        setRotationSetDdDao(rotationSetDdDao);
        setDisplayDocumentDao(displayDocumentDao);
    }

    /**
     * Set the display document. This should not be updated in the GUI, only by the
     * uk.gov.hmcts.pdda.business.services.publicdisplay.

     * @param displayDocumentDao display document for the XhbRotationSetDdBasicValue
     */
    public final void setDisplayDocumentDao(XhbDisplayDocumentDao displayDocumentDao) {
        this.displayDocumentDao = displayDocumentDao;
    }

    /**
     * Get the document.

     * @return the display document
     */
    public XhbDisplayDocumentDao getDisplayDocumentBasicValue() {
        return displayDocumentDao;
    }

    /**
     * Delegated method call that gets the display document id from the RotationSetDDBasicValue.

     * @return Primary key of the display document
     */
    public Integer getDisplayDocumentId() {
        return displayDocumentDao.getDisplayDocumentId();
    }

    /**
     * Set a new delay or order.

     * @param rotationSetDdDao new value
     */
    private void setRotationSetDdDao(XhbRotationSetDdDao rotationSetDdDao) {
        this.rotationSetDdDao = rotationSetDdDao;
    }

    /**
     * Get the many-to-many relationship information of rotation set to display document. This will
     * give ordering and delay.

     * @return XhbRotationSetDdBasicValue
     */
    public XhbRotationSetDdDao getRotationSetDdDao() {
        return rotationSetDdDao;
    }

    /**
     * Delegated method call that gets the rotation set Id from the RotationSetDDBasicValue.

     * @return Primary key of the rotation set
     */
    public Integer getRotationSetDdId() {
        return rotationSetDdDao.getRotationSetDdId();
    }

    /**
     * Override the equals method to compare the id's of the associated basic value only if both
     * primary keys are not null (this may not be the case if a new RotationSetDDBasicValue is being
     * added. In this case, the display document id's are compared as these can never be null.

     * @param complexValue A DisplayLocationComplexValue

     * @return true if the object with same rotation set dd primary key or same display document
     *         primary key
     */
    @Override
    public boolean equals(Object complexValue) {
        if (rotationSetDdDao != null && complexValue instanceof RotationSetDdComplexValue
            && ((RotationSetDdComplexValue) complexValue).rotationSetDdDao != null) {
            if (((RotationSetDdComplexValue) complexValue).rotationSetDdDao
                .getPrimaryKey() != null && rotationSetDdDao.getPrimaryKey() != null) {
                return ((RotationSetDdComplexValue) complexValue).rotationSetDdDao
                    .equals(rotationSetDdDao);
            } else {
                return ((RotationSetDdComplexValue) complexValue).getDisplayDocumentId()
                    .equals(rotationSetDdDao.getDisplayDocumentId());
            }
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
