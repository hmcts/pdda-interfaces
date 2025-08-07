package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title: Rotation Set Complex Value.
 * </p>
 * 
 * <p>
 * Description: Holds information to identify the rotation, the display pages within that rotation
 * set (with ordering and delays) and a list of screens the rotation set is assigned to
 * </p>
 * 
 * <p>
 * This VO can be used for creating a new rotation set. Simply add a rotation set basic value for
 * the name and an array of RotationSetDDComplexValue's to identify the display documents, ordering
 * and delay.<br>
 * The list of screens can be left null as these are not required or update
 * </p>
 * 
 * <p>
 * This VO can also be used for updating a rotation set. Update the rotation set basic value with
 * the new name of the rotation set and/or modify the list of RotationSetDDComplexValue's to change
 * the display documents, ordering and/or delay.
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
 * @version $Id: RotationSetComplexValue.java,v 1.5 2005/02/10 13:51:06 sz0t7n Exp $
 */
public class RotationSetComplexValue extends CsAbstractValue {

    static final long serialVersionUID = -1474521472546880003L;

    /**
     * Hashset used to store the display documents. This will prevent the display document being
     * repeated in the rotation set.
     */
    private final Set<RotationSetDdComplexValue> rotationSetDdComplexValues = new HashSet<>();

    private XhbRotationSetsDao rotationSetsDao;

    private DisplayBasicValueSortAdapter[] displayDao;

    /**
     * Delegated method that gets the court Id from the rotation set basic value.
     * 
     * @return Court Id the rotation set is defined for
     */
    public Integer getCourtId() {
        return rotationSetsDao.getCourtId();
    }

    /**
     * Set a list of displays that have this rotation set assigned This will be set by the
     * uk.gov.hmcts.pdda.business.services.publicdisplay and is not required to be set or
     * amended by any GUI components.
     * 
     * @param displayDao DisplayBasicValueSortAdapterArray
     */
    public void setDisplayDaos(DisplayBasicValueSortAdapter... displayDao) {
        this.displayDao = displayDao.clone();
    }

    /**
     * Return the list of displays that have this rotation set assigned.
     * 
     * @return DisplayBasicValueSortAdapter
     */
    public DisplayBasicValueSortAdapter[] getDisplayDaos() {
        return displayDao.clone();
    }

    /**
     * Set the rotation set. When creating a new rotation set, the basic value MUST have the court
     * id populated.
     * 
     * @param rotationSetsDao the rotation set information
     */
    public void setRotationSetDao(XhbRotationSetsDao rotationSetsDao) {
        this.rotationSetsDao = rotationSetsDao;
    }

    /**
     * Get the rotation set that this VO applies to.
     * 
     * @return XhbRotationSetsDAO
     */
    public XhbRotationSetsDao getRotationSetsDao() {
        return rotationSetsDao;
    }

    /**
     * Add an array of RotationSetDDComplexValue's in one go. This will clear the internal hashset
     * before adding this list.
     * 
     * @param newRotationSetDdComplexValues array of documents to hold
     */
    public void setRotationSetDdComplexValues(
        RotationSetDdComplexValue... newRotationSetDdComplexValues) {
        rotationSetDdComplexValues.clear();

        for (RotationSetDdComplexValue newRotationSetDdComplexValue : newRotationSetDdComplexValues) {
            addRotationSetDdComplexValue(newRotationSetDdComplexValue);
        }
    }

    /**
     * Get the current list of display documents with ordering and delay information.
     * 
     * @return array of RotationSetDDComplexValues
     */
    public RotationSetDdComplexValue[] getRotationSetDdComplexValues() {
        return rotationSetDdComplexValues
            .toArray(new RotationSetDdComplexValue[0]);
    }

    /**
     * Returns a rotation set Dd for the passed id.
     * 
     * @param rotationSetDdId Integer
     * 
     * @return XhbRotationSetDdDAO
     */
    public XhbRotationSetDdDao getRotationSetDd(Integer rotationSetDdId) {
        Iterator<RotationSetDdComplexValue> rotationSetDdIter =
            this.rotationSetDdComplexValues.iterator();

        while (rotationSetDdIter.hasNext()) {
            RotationSetDdComplexValue rotationSetDd = rotationSetDdIter.next();

            if (rotationSetDdId.equals(rotationSetDd.getRotationSetDdId())) {
                return rotationSetDd.getRotationSetDdDao();
            }
        }

        return null;
    }

    /**
     * Delegated method call that gets the rotation set id from the rotation set basic value. Note:
     * this may return null if the rotation set has not been saved.
     * 
     * @return The rotation set Id this VO belongs to
     */
    public Integer getRotationSetId() {
        return rotationSetsDao.getRotationSetId();
    }

    /**
     * Add a single item. Useful if iterating throw collections and optionally choosing which to add
     * This will append to the existing list.
     * 
     * @param rotationSetDdComplexValue Document to add
     */
    public void addRotationSetDdComplexValue(RotationSetDdComplexValue rotationSetDdComplexValue) {
        rotationSetDdComplexValues.add(rotationSetDdComplexValue);
    }

    /**
     * Checks whether the rotation set dd is there.
     * 
     * @param rotationSetDdId Integer
     * 
     * @return boolean
     */
    public boolean hasRotationSetDd(Integer rotationSetDdId) {
        Iterator<RotationSetDdComplexValue> rotationSetDdIter =
            this.rotationSetDdComplexValues.iterator();

        while (rotationSetDdIter.hasNext()) {
            RotationSetDdComplexValue rotationSetDd = rotationSetDdIter.next();

            if (rotationSetDdId.equals(rotationSetDd.getRotationSetDdId())) {
                return true;
            }
        }

        return false;
    }
}
