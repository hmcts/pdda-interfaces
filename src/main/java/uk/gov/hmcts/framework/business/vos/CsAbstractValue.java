package uk.gov.hmcts.framework.business.vos;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.conversion.BasicConverter;
import uk.gov.hmcts.framework.services.conversion.StringMap;

public abstract class CsAbstractValue implements CsValueObject {

    private static final long serialVersionUID = 1L;

    int updateCount = -1;

    Integer id;

    private boolean dirty; // set to true if the details need to be updated

    protected CsAbstractValue() {
        // protected constructor
    }

    protected CsAbstractValue(Integer version) {
        // Null check added 04/04/2003 Joseph Babad.
        // Likely to happen is you create an entity and then immediately create
        // the VO in the SAME transaction. Version will not have been set yet,
        // because
        // database insert is set to "delay until commit". So, set the the
        // updateCount to
        // 1 to mimic the fact that the "before insert" trigger does the same.
        // (Joseph Babad).
        if (version != null) {
            this.updateCount = version;
        } else {
            // Set to one - this is likely to happen wh
            this.updateCount = 1;
        }
    }

    protected CsAbstractValue(Integer id, Integer version) {
        this.id = id;
        // Null check added 24/03/2003 Ian Hannaford.
        // Likely to happen is you create an entity and then immediately create
        // the VO in the SAME transaction. Version will not have been set yet,
        // because
        // database insert is set to "delay until commit". So, set the the
        // updateCount to
        // 1 to mimic the fact that the "before insert" trigger does the same.
        // (Joseph Babad).
        if (version != null) {
            this.updateCount = version;
        } else {
            this.updateCount = 1;
        }
    }

    /**
     * Returns the version number for the entity this Value Object represents. Used to implement an
     * optimistic locking strategy.
     */
    @Override
    public Integer getVersion() {
        return updateCount;
    }

    /**
     * Sets the id.
     */
    public void setVersion(Integer val) {
        if (val != null) {
            updateCount = val;
        }
    }

    /**
     * Returns the primary key for the entity this Value Object represents.
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     */
    public void setId(Integer val) {
        id = val;
    }

    /**
     * Generate a toString value for all sub classes using reflection code in the StringMap and
     * associated converter classes.
     */
    @Override
    public String toString() {
        String str = "";
        try {
            StringMap map = new StringMap(new BasicConverter());
            map.copyValueToMap(this);
            str = map.toString();
        } catch (CsUnrecoverableException e) {
            CsServices.getDefaultErrorHandler().handleError(e, CsAbstractValue.class);
        }
        return str;
    }

    /**
     * Return whether the object is flagged as amended.
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Set the flag to signify the object has been amended or not.
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
