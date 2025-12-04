package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import java.util.Objects;

/**

 * Title: AllCaseStatusValue.


 * Description:


 * Copyright: Copyright (c) 2004


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: AllCaseStatusValue.java,v 1.5 2006/06/05 12:28:24 bzjrnl Exp $
 */
@SuppressWarnings("PMD")
public class AllCaseStatusValue extends SummaryByNameValue {

    static final long serialVersionUID = -5528116868430750798L;
    
    private static final String EMPTY_STRING = "";

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
     * Hearing progress.
     */
    private Integer hearingProgress;

    /**
     * The id of the court room that the list entry is for.
     */
    private Integer listCourtRoomId;
    
    private Integer scheduledHearingId;
    private Integer defendantOnCaseId;
    private Integer hearingId;

    public String getHearingDecsription() {
        return hearingDecsription;
    }

    public void setHearingDecsription(String hearingDecsription) {
        this.hearingDecsription = hearingDecsription;
    }

    public Integer getScheduledHearingId() {
        return scheduledHearingId;
    }

    public void setScheduledHearingId(Integer scheduledHearingId) {
        this.scheduledHearingId = scheduledHearingId;
    }

    public Integer getDefendantOnCaseId() {
        return defendantOnCaseId;
    }

    public void setDefendantOnCaseId(Integer defendantOnCaseId) {
        this.defendantOnCaseId = defendantOnCaseId;
    }

    public Integer getHearingId() {
        return hearingId;
    }

    public void setHearingId(Integer hearingId) {
        this.hearingId = hearingId;
    }

    /**
     * Sets the case title.

     * @param val the Case title
     */
    public void setCaseTitle(String val) {
        caseTitle = val;
    }

    /**
     * Sets the case number.

     * @param val number
     */
    public void setCaseNumber(String val) {
        caseNumber = val;
    }

    /**
     * Gets the case title.

     * @return val the Case title
     */
    public String getCaseTitle() {
        return caseTitle == null ? "" : caseTitle;
    }

    /**
     * Returns the case number.

     * @return DCAse number
     */
    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * Sets the hearing type.

     * @param val Hearing type
     */
    public void setHearingDescription(String val) {
        hearingDecsription = val;
    }

    /**
     * Returns the hearing type.

     * @return Hearing type
     */
    public String getHearingDescription() {
        return hearingDecsription;
    }

    /**
     * Sets the hearing progress.

     * @param val Hearing progress
     */
    public void setHearingProgress(Integer val) {
        hearingProgress = val;
    }

    /**
     * Returns the hearing progress.

     * @return hearingProgress Hearing progress
     */
    public Integer getHearingProgress() {
        return hearingProgress;
    }

    /**
     * Whether this entry has defendants.

     * @return true if this entry has defendants.
     */
    public boolean hasDefendant() {
        return getDefendantName().hasValue();
    }

    public boolean hasCaseTitle() {
        return caseTitle != null && !EMPTY_STRING.equals(caseTitle);
    }

    /**
     * Get the id of the court room that the list entry is for.

     * @return The id of the court room that the list entry is for.
     */
    public Integer getListCourtRoomId() {
        return listCourtRoomId;
    }

    /**
     * Set the id of the court room that the list entry is for.

     * @param listCourtRoomId The id of the court room that the list entry is for. moved to.
     */
    public void setListCourtRoomId(Integer listCourtRoomId) {
        this.listCourtRoomId = listCourtRoomId;
    }

    /**
     * Indicates whether this entry is either in the court room being listed or whether it was
     * listed in the court room but was then moved to another.

     * @return true if the entry is currently listed in the court room.
     */
    public boolean isListedInThisCourtRoom() {
        return listCourtRoomId != null && listCourtRoomId.equals(this.getCourtRoomId());
    }

    @Override
    public int compareTo(PublicDisplayValue other) {
        if (other instanceof AllCaseStatusValue) {
            if (!this.getCourtSiteCode().equals(other.getCourtSiteCode())) {
                // Different court sites, alphabetic court site code comparison
                return this.getCourtSiteCode().compareTo(other.getCourtSiteCode());
            }
            if (!this.getFloating().equals(((AllCaseStatusValue) other).getFloating())) {
                // Floating cases should appear last in the list
                return this.getFloating().compareTo(((AllCaseStatusValue) other).getFloating());
            }
            if (!this.getCrestCourtRoomNo().equals(other.getCrestCourtRoomNo())) {
                // Different court room no, numeric court room number comparison
                return this.getCrestCourtRoomNo() - other.getCrestCourtRoomNo();
            }
            // Compare not before time (special case to check for nulls so cannot use a equals on
            // the
            // Timestamp object)
            int cmp =
                compareNotBeforeTimeCheckNull(this.getNotBeforeTime(), other.getNotBeforeTime());
            if (cmp != 0) {
                return cmp;
            }
            // Finally compare based on defendant name
            return this.getDefendantName()
                .compareTo(((AllCaseStatusValue) other).getDefendantName());
        }
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        AllCaseStatusValue other = (AllCaseStatusValue) object;

        // Base fields
        if (!Objects.equals(this.getCourtSiteCode(), other.getCourtSiteCode())) {
            return false;
        }
        if (!Objects.equals(this.getCrestCourtRoomNo(), other.getCrestCourtRoomNo())) {
            return false;
        }

        // Fields required by unit tests
        if (!Objects.equals(this.getCaseNumber(), other.getCaseNumber())) {
            return false;
        }
        if (!Objects.equals(this.getFloating(), other.getFloating())) {
            return false;
        }
        if (!Objects.equals(this.getDefendantName(), other.getDefendantName())) {
            return false;
        }
        return (!Objects.equals(this.getNotBeforeTime(), other.getNotBeforeTime()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getCourtSiteCode(),
            getCrestCourtRoomNo(),
            getCaseNumber(),
            getFloating(),
            getDefendantName(),
            getNotBeforeTime()
        );
    }

}
