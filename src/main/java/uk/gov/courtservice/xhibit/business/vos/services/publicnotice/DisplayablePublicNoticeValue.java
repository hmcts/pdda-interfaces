package uk.gov.courtservice.xhibit.business.vos.services.publicnotice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**

 * Title: DisplayablePublicNoticeValue.

 * Description: This VO is to contain a Public Notice that is displayable for a particular court.
 * Note : there is a set of definitive public notices but these are not . These are not the public
 * notice that actaully gets displayed on screen it is the equivalent displayable public notice for
 * this court. eg No smoking --- maps to an actual displayed message for court in Manchester --> No
 * Smoking in the Court Room .

 * Company: EDS

 * @author Pat Fox
 * @created 17 February 2003
 * @version 1.0
 */

public class DisplayablePublicNoticeValue extends CsAbstractValue implements Comparable<Object> {
    private static final long serialVersionUID = 4470489343477594601L;

    private static final Logger LOG = LoggerFactory.getLogger(DisplayablePublicNoticeValue.class);
    
    private String desc;

    private boolean active;

    private int priority;

    private Integer definitivePublicNoticeId;

    private boolean dirty;

    /**
     * Constructor for the DisplayablePublicNoticeValue object.
     */
    public DisplayablePublicNoticeValue() {
        // default constructor
        super();
    }

    /**
     * Constructor for the DisplayablePublicNoticeValue object.

     * @param configuredPnId Description of the Parameter
     * @param desc Description of the Parameter
     * @param active Description of the Parameter
     * @param versionNumber Description of the Parameter
     * @param definitivePublicNoticeId Description of the Parameter
     */
    public DisplayablePublicNoticeValue(Integer configuredPnId, String desc, boolean active,
        Integer versionNumber, Integer definitivePublicNoticeId) {
        super(configuredPnId, versionNumber);
        this.desc = desc;
        this.active = active;
        this.definitivePublicNoticeId = definitivePublicNoticeId;
        this.dirty = false;
    }

    /**
     * Constructor for the DisplayablePublicNoticeValue object.

     * @param configuredPnId Description of the Parameter
     * @param desc Description of the Parameter
     * @param isActive Description of the Parameter
     * @param versionNumber Description of the Parameter
     * @param definitivePublicNoticeId Description of the Parameter
     * @param priority Description of the Parameter
     */
    public DisplayablePublicNoticeValue(Integer configuredPnId, String desc, boolean isActive,
        Integer versionNumber, Integer definitivePublicNoticeId, int priority) {
        this(configuredPnId, desc, isActive, versionNumber, definitivePublicNoticeId);
        this.priority = priority;
    }

    /**
     * Gets the desc attribute of the DisplayablePublicNoticeValue object.

     * @return The desc value
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Gets the isActive attribute of the DisplayablePublicNoticeValue object.

     * @return The isActive value
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the priority attribute of the DisplayablePublicNoticeValue object.

     * @return The priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the desc attribute of the DisplayablePublicNoticeValue object.

     * @param desc The new desc value
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Sets the isActive attribute of the DisplayablePublicNoticeValue object.

     * @param isActive The new isActive value
     */
    public void setIsActive(boolean isActive) {
        this.active = isActive;
    }

    /**
     * Sets the priority attribute of the DisplayablePublicNoticeValue object.

     * @param priority The new priority value
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Sets the definitivePublicNotice attribute of the DisplayablePublicNoticeValue object.

     * @param definitivePublicNotice The new definitivePublicNotice value
     */
    public void setDefinitivePublicNotice(Integer definitivePublicNotice) {
        this.definitivePublicNoticeId = definitivePublicNotice;
    }

    /**
     * Gets the definitivePublicNotice attribute of the DisplayablePublicNoticeValue object.

     * @return The definitivePublicNotice value
     */
    public Integer getDefinitivePublicNotice() {
        return this.definitivePublicNoticeId;
    }

    /**
     * Gets the dirty attribute of the DisplayablePublicNoticeValue object.

     * @return The dirty value
     */
    public boolean isDirtyFlagged() {
        return dirty;
    }

    /**
     * Sets the dirty attribute of the DisplayablePublicNoticeValue object.

     * @param dirty The new dirty value
     */
    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Used for the sort of the array.

     * @param obj Description of the Parameter
     * @return Description of the Return Value
     */
    @Override
    public int compareTo(Object obj) {

        DisplayablePublicNoticeValue specifiedObj = (DisplayablePublicNoticeValue) obj;

        if (this.getPriority() < specifiedObj.getPriority()) {
            return -1;
        }
        if (this.getPriority() == specifiedObj.getPriority()) {
            return 0;
        }

        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        LOG.debug("equals()");
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
