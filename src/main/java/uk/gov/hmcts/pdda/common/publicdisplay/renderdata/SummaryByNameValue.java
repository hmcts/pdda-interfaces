package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the data for summary by name document.
 * 
 * @author pznwc5
 */
public class SummaryByNameValue extends PublicDisplayValue {

    static final long serialVersionUID = -2334222330489778358L;

    private static final Logger LOG = LoggerFactory.getLogger(SummaryByNameValue.class);
    
    /**
     * Reporting restricted.
     */
    private boolean reportingRestricted;

    /**
     * Defendant name.
     */
    private DefendantName defendantName;

    /**
     * Whether the case is floating.
     */
    private String floating;

    /**
     * Returns the is floating string.
     * 
     * @return The string as it appears in the DB
     */
    public String getFloating() {
        return floating;
    }

    /**
     * Sets the is floating flag from the DB.
     */
    public void setFloating(String isFloating) {
        floating = isFloating;
    }

    public boolean isFloating() {
        return IS_FLOATING.equals(floating);
    }

    /**
     * Gets the name of the defendant.
     * 
     * @param val Name of the defendant
     */
    public void setDefendantName(DefendantName val) {
        defendantName = val;
    }

    /**
     * Gets the name of the defendant.
     * 
     * @return Name of the defendant
     */
    public DefendantName getDefendantName() {
        return defendantName;
    }

    /**
     * Sets reporting restriction.
     * 
     * @param val Reporting restriction
     */
    public void setReportingRestricted(boolean val) {
        reportingRestricted = val;
    }

    /**
     * Returns reporting restriction.
     * 
     * @return Reporting restriction
     */
    public boolean isReportingRestricted() {
        return reportingRestricted;
    }

    @Override
    public int compareTo(PublicDisplayValue other) {
        if (other instanceof SummaryByNameValue) {
            return this.getDefendantName()
                .compareTo(((SummaryByNameValue) other).getDefendantName());
        }
        return 0;
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
