package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import java.util.Objects;

/**
 * This class provides the data for daily list and jury status document.

 * @author pznwc5
 */
@SuppressWarnings("PMD")
public class JuryStatusDailyListValue extends CourtListValue {

    static final long serialVersionUID = 4966427551623410138L;

    /**
     * Judge name.
     */
    private JudgeName judgeName;

    private String floating;

    /**
     * Sets the judge name.

     * @param val Judge name
     */
    public void setJudgeName(JudgeName val) {
        judgeName = val;
    }

    /**
     * Returns the judge name.

     * @return Judge name
     */
    public JudgeName getJudgeName() {
        return judgeName;
    }

    /**
     * Returns the is floating string.

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

    /**
     * Returns the is floating string.

     * @return true if the cases is unassigned
     */
    public boolean isFloating() {
        return IS_FLOATING.equals(floating);
    }

    @Override
    public int compareTo(PublicDisplayValue other) {

        if (!this.getCourtSiteCode().equals(other.getCourtSiteCode())) {
            return this.getCourtSiteCode().compareTo(other.getCourtSiteCode());
        }
        if (!this.getFloating().equals(((JuryStatusDailyListValue) other).getFloating())) {
            // Floating cases should appear last in the list
            return this.getFloating().compareTo(((JuryStatusDailyListValue) other).getFloating());
        }
        if (!this.getCrestCourtRoomNo().equals(other.getCrestCourtRoomNo())) {
            return this.getCrestCourtRoomNo() - other.getCrestCourtRoomNo();
        }
        // Finally, compare not before time (special case to check for nulls so cannot use a equals
        // on
        // the Timestamp object)
        return compareNotBeforeTimeCheckNull(this.getNotBeforeTime(), other.getNotBeforeTime());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        JuryStatusDailyListValue other = (JuryStatusDailyListValue) object;

        if (!Objects.equals(this.getCourtSiteCode(), other.getCourtSiteCode())) {
            return false;
        }
        if (!Objects.equals(this.getCrestCourtRoomNo(), other.getCrestCourtRoomNo())) {
            return false;
        }
        if (!Objects.equals(this.getCaseNumber(), other.getCaseNumber())) {
            return false;
        }
        if (!Objects.equals(this.getFloating(), other.getFloating())) {
            return false;
        }
        return (!Objects.equals(this.getDefendantNames(), other.getDefendantNames()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getCourtSiteCode(),
            getCrestCourtRoomNo(),
            getCaseNumber(),
            getFloating(),
            getDefendantNames()
        );
    }

}
