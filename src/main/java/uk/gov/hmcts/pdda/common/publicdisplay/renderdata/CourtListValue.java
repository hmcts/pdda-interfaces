package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the data for daily list and jury status document.
 * 
 * @author pznwc5
 */
public class CourtListValue extends PublicDisplayValue {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(CourtListValue.class);
    
    private static final String EMPTY_STRING = "";

    /**
     * Defendant names.
     */
    private final List<DefendantName> defendantNames = new ArrayList<>();

    /**
     * Case title.
     */
    private String caseTitle;

    /**
     * Case number.
     */
    private String caseNumber;

    /**
     * Hearing type.
     */
    private String hearingDecsription;

    /**
     * Reporting restricted.
     */
    private boolean reportingRestricted;

    /**
     * Hearing progress.
     */
    private int hearingProgress;

    /**
     * The id of the court room that the list entry is for.
     */
    private int listCourtRoomId;

    /**
     * Sets the case title.
     * 
     * @param val the Case title
     */
    public void setCaseTitle(String val) {
        caseTitle = val;
    }

    /**
     * Sets the case number.
     * 
     * @param val number
     */
    public void setCaseNumber(String val) {
        caseNumber = val;
    }

    /**
     * Gets the case title.
     * 
     * @return val the Case title
     */
    public String getCaseTitle() {
        return caseTitle;
    }

    /**
     * Returns the case number.
     * 
     * @return DCAse number
     */
    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * Returns defendant name.
     * 
     * @return Defendant name
     */
    public List<DefendantName> getDefendantNames() {
        return defendantNames;
    }

    /**
     * Sets the hearing type.
     * 
     * @param val Hearing type
     */
    public void setHearingDescription(String val) {
        hearingDecsription = val;
    }

    /**
     * Returns the hearing type.
     * 
     * @return Hearing type
     */
    public String getHearingDescription() {
        return hearingDecsription;
    }

    /**
     * Sets the hearing progress.
     * 
     * @param val Hearing progress
     */
    public void setHearingProgress(int val) {
        hearingProgress = val;
    }

    /**
     * Returns the hearing progress.
     * 
     * @return hearingProgress Hearing progress
     */
    public int getHearingProgress() {
        return hearingProgress;
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

    /**
     * Adds a defendant name.
     * 
     * @param defendantName Defendant name
     */
    public void addDefendantName(DefendantName defendantName) {
        defendantNames.add(defendantName);
    }

    /**
     * Whether this entry has defendants.
     * 
     * @return true if this entry has defendants.
     */
    public boolean hasDefendants() {
        return !defendantNames.isEmpty();
    }

    public boolean hasCaseTitle() {
        return caseTitle != null && !EMPTY_STRING.equals(caseTitle);
    }

    /**
     * Get the id of the court room that the list entry is for.
     * 
     * @return The id of the court room that the list entry is for.
     */
    public int getListCourtRoomId() {
        return listCourtRoomId;
    }

    /**
     * Set the id of the court room that the list entry is for.
     * 
     * @param listCourtRoomId The id of the court room that the list entry is for. moved to.
     */
    public void setListCourtRoomId(int listCourtRoomId) {
        this.listCourtRoomId = listCourtRoomId;
    }

    /**
     * Indicates whether this entry is either in the court room being listed or whether it was listed in
     * the court room but was then moved to another.
     * 
     * @return true if the entry is currently listed in the court room.
     */
    @SuppressWarnings("PMD.UnnecessaryBoxing")
    public boolean isListedInThisCourtRoom() {
        Integer courtRoomId = this.getCourtRoomId();
        return courtRoomId != null && courtRoomId.intValue() == listCourtRoomId;
    }

    @Override
    public int compareTo(PublicDisplayValue other) {
        // Compare not before time (special case to check for nulls so cannot use a equals on the
        // Timestamp object)
        return compareNotBeforeTimeCheckNull(this.getNotBeforeTime(), other.getNotBeforeTime());
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
