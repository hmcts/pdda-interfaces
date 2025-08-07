package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

/**
 * PublicNoticeValue.
 * 
 * @author pznwc5 Public notices value object.
 */
public class PublicNoticeValue extends AbstractValue {

    static final long serialVersionUID = 8862116573773182259L;

    /**
     * Public notice description.
     */
    private String publicNoticeDesc;

    /**
     * Active flag.
     */
    private boolean active;

    /**
     * Priority.
     */
    private int priority;

    /**
     * setPublicNoticeDesc.
     * 
     * @param publicNoticeDesc String
     */
    public void setPublicNoticeDesc(String publicNoticeDesc) {
        this.publicNoticeDesc = publicNoticeDesc;
    }

    /**
     * getPublicNoticeDesc.
     * 
     * @return String
     */
    public String getPublicNoticeDesc() {
        return publicNoticeDesc;
    }

    /**
     * setActive.
     * 
     * @param active boolean
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * isActive.
     * 
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * setPriority.
     * 
     * @param priority int
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * getPriority.
     * 
     * @return int
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean hasInformationForDisplay() {
        return true;
    }
}
