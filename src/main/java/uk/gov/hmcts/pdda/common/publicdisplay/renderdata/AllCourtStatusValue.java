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
        int result;

        // Court site code (alphabetic)
        result = compareNullable(
            this.getCourtSiteCode(),
            other.getCourtSiteCode(),
            String::compareTo
        );
        if (result != 0) {
            return result;
        }

        // Court room number (numeric)
        result = compareNullable(
            this.getCrestCourtRoomNo(),
            other.getCrestCourtRoomNo(),
            Integer::compare
        );
        if (result != 0) {
            return result;
        }

        // Event time (descending)
        result = compareNullable(
            this.getEventTime(),
            other.getEventTime(),
            (a, b) -> b.compareTo(a) // reverse order
        );
        return result;
    }

    private static <T> int compareNullable(T first, T second, java.util.Comparator<T> comparator) {
        if (first == null || second == null) {
            return 0;
        }
        if (first.equals(second)) {
            return 0;
        }
        return comparator.compare(first, second);
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
