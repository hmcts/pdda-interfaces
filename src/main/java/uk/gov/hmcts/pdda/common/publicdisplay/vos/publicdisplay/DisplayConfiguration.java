package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;

/**

 * Title: Display Configuration.

 * Description: A display configuration defines the display (aka screen), the rotation set assigned
 * to that display, and the list of court rooms assigned to that display.

 * This object is also used for updating. Changing the rotation set and/or the court rooms and
 * calling the method updateDisplayConfiguration in the PDConfigurationController will update the
 * database and cause the relevant pages to be re-rendered.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: DisplayConfiguration.java,v 1.6 2006/06/05 12:28:26 bzjrnl Exp $
 */
public class DisplayConfiguration extends CsAbstractValue {

    static final long serialVersionUID = -912195225609058977L;

    /**
     * Use this variable for setting showUnassigned.
     */
    public static final String DB_YES = "Y";

    /**
     * Use this variable for setting showUnassigned.
     */
    public static final String DB_NO = "N";

    private final XhbDisplayDao displayDao;

    private XhbRotationSetsDao rotationSetsDao;

    private XhbCourtRoomDao[] courtRoomDaos;

    private boolean courtRoomsChanged;

    private boolean rotationSetChanged;

    /**
     * Only constructor that sets the values typically from the DB. The set methods are not used as
     * they internally set a flag that shows them as modified, which is used when updating a display
     * configuration.
     */
    public DisplayConfiguration(XhbDisplayDao displayDao, XhbRotationSetsDao rotationSetDao,
        XhbCourtRoomDao... courtRoomDaos) {
        super();
        this.displayDao = displayDao;
        this.rotationSetsDao = rotationSetDao;
        setCourtRoomDaos(courtRoomDaos);
    }

    /**
     * Sets a new collection of court rooms for the display Internally sets a flag to indicate the
     * court rooms have changed.

     * @param courtRoomDaos new list of court rooms
     */
    public void setCourtRoomDaosWithCourtRoomChanged(XhbCourtRoomDao... courtRoomDaos) {
        setCourtRoomDaos(courtRoomDaos);
        setCourtRoomsChanged(true);
    }
    
    /**
     * Get the current assignment of court rooms.

     * @return array of court rooms
     */
    public XhbCourtRoomDao[] getCourtRoomDaos() {
        return courtRoomDaos.clone();
    }

    private void setCourtRoomDaos(XhbCourtRoomDao... courtRoomDaos) {
        this.courtRoomDaos = courtRoomDaos;
    }

    
    /**
     * Query if there is a new list of court rooms.

     * @return true if new court rooms
     */
    public boolean isCourtRoomsChanged() {
        return courtRoomsChanged;
    }

    /**
     * Returns information about the display.

     * @return XhbDisplayDAO
     */
    public XhbDisplayDao getDisplayDao() {
        return displayDao;
    }

    /**
     * getDisplayId.

     * @return Integer
     */
    public Integer getDisplayId() {
        return displayDao.getDisplayId();
    }

    /**
     * Sets a new rotation set to the display. Internally sets a flag to indicate the rotation set
     * has changed

     * @param rotationSetsDao new rotation set
     */
    public void setRotationSetDao(XhbRotationSetsDao rotationSetsDao) {
        this.rotationSetsDao = rotationSetsDao;
        this.displayDao.setRotationSetId(rotationSetsDao.getPrimaryKey());
        setRotationSetChanged(true);
    }

    /**
     * Get the currently assigned rotation set.

     * @return current rotation set assigned to the display
     */
    public XhbRotationSetsDao getRotationSetDao() {
        return rotationSetsDao;
    }

    /**
     * Query if the rotation set has changed.

     * @return true if new rotation set
     */
    public boolean isRotationSetChanged() {
        return rotationSetChanged;
    }

    /**
     * getRotationSetId.

     * @return Integer
     */
    public Integer getRotationSetId() {
        return rotationSetsDao.getRotationSetId();
    }

    /**
     * Update that a new list of court rooms has been added.

     * @param courtRoomsChanged true to indicate modified
     */
    private void setCourtRoomsChanged(boolean courtRoomsChanged) {
        this.courtRoomsChanged = courtRoomsChanged;
    }

    /**
     * Update that the rotation set has been modified.

     * @param rotationSetChanged true to indicate modified
     */
    private void setRotationSetChanged(boolean rotationSetChanged) {
        this.rotationSetChanged = rotationSetChanged;
    }
}
