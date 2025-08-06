package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Common superclass for public display data classes Note: This extends public display value, but
 * only uses the court site information from this value object. The row processor calls a separate
 * method on the abstractRowProcessor to acheive this.

 * @author pznwc5
 */
public class AllCourtStatusValue extends PublicDisplayValue {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(AllCourtStatusValue.class);
    
    private static final String EMPTY_STRING = "";

    /**
     * Reporting restricted.
     */
    private boolean reportingRestricted;

    /**
     * Defendant names.
     */
    private final Collection<DefendantName> defendantNames = new ArrayList<>();

    /**
     * Case title.
     */
    private String caseTitle;

    /**
     * Case number.
     */
    private String caseNumber;


    /**
     * Sets the case title.

     * @param val the Case title
     */
    public void setCaseTitle(String val) {
        caseTitle = val;
    }

    /**
     * Sets the case number.

     * @param val the Case number
     */
    public void setCaseNumber(String val) {
        caseNumber = val;
    }

    /**
     * Gets the case title.

     * @return val the Case title
     */
    public String getCaseTitle() {
        return caseTitle;
    }

    /**
     * Gets the case number.

     * @return the case number
     */
    public String getCaseNumber() {
        return caseNumber == null ? "" : caseNumber;
    }

    /**
     * Gets the defendant names.

     * @return Defendant names
     */
    public Collection<DefendantName> getDefendantNames() {
        return defendantNames;
    }

    /**
     * Adds the defendant name.

     * @param val the DefendantName name
     */
    public void addDefendantName(DefendantName val) {
        defendantNames.add(val);
    }

    @Override
    public boolean hasInformationForDisplay() {
        return caseNumber != null;
    }

    public boolean hasDefendants() {
        return !defendantNames.isEmpty();
    }

    public boolean hasCaseTitle() {
        return caseTitle != null && !EMPTY_STRING.equals(caseTitle);
    }

    /**
     * Sets reporting restriction.

     * @param val Reporting restriction
     */
    public void setReportingRestricted(boolean val) {
        reportingRestricted = val;
    }

    /**
     * Returns reporting restriction.

     * @return Reporting restriction
     */
    public boolean isReportingRestricted() {
        return reportingRestricted;
    }

    @Override
    public int compareTo(PublicDisplayValue other) {
        if (!this.getCourtSiteCode().equals(other.getCourtSiteCode())) {
            // Different court sites, alphabetic court site code comparison
            return this.getCourtSiteCode().compareTo(other.getCourtSiteCode());
        }
        if (!this.getCrestCourtRoomNo().equals(other.getCrestCourtRoomNo())) {
            // Different court room no, numeric court room number comparison
            return this.getCrestCourtRoomNo() - other.getCrestCourtRoomNo();
        }
        if (!this.getEventTime().equals(other.getEventTime())) {
            // Different event time, sort by timestamp (descending so most recent first)
            return other.getEventTime().compareTo(this.getEventTime());
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
