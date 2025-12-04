package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 *  CourtDetailValue.

 * @author pznwc5 Value object for court detail.
 */
@SuppressWarnings("PMD")
public class CourtDetailValue extends AllCourtStatusValue {

    static final long serialVersionUID = 754560959152416644L;

    private static final Logger LOG = LoggerFactory.getLogger(CourtDetailValue.class);
    
    /**
     * Judge name.
     */
    private JudgeName judgeName;

    /**
     * Hearing description.
     */
    private String hearingDescription;

    /**
     * Public notices.
     */
    private PublicNoticeValue[] publicNotices;

    /**
     * setJudgeName.

     * @param judgeName JudgeName
     */
    public void setJudgeName(JudgeName judgeName) {
        this.judgeName = judgeName;
    }

    /**
     * getJudgeName.

     * @return JudgeName
     */
    public JudgeName getJudgeName() {
        return judgeName;
    }

    /**
     * setHearingDescription.

     * @param hearingDescription String
     */
    public void setHearingDescription(String hearingDescription) {
        this.hearingDescription = hearingDescription;
    }

    /**
     * getHearingDescription.

     * @return String
     */
    public String getHearingDescription() {
        return hearingDescription;
    }

    /**
     * setPublicNotices.

     * @param publicNotices PublicNoticeValueArray
     */
    public void setPublicNotices(PublicNoticeValue... publicNotices) {
        this.publicNotices = publicNotices.clone();
    }

    /**
     * getPublicNotices.

     * @return String
     */
    public PublicNoticeValue[] getPublicNotices() {
        return publicNotices != null ? publicNotices.clone() : new PublicNoticeValue[0];
    }

    public boolean hasPublicNotices() {
        return publicNotices != null && publicNotices.length > 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PublicDisplayValue)) {
            return false;
        }

        PublicDisplayValue other = (PublicDisplayValue) object;

        // court site code
        if (!Objects.equals(this.getCourtSiteCode(), other.getCourtSiteCode())) {
            return false;
        }

        // crest court room number
        if (!Objects.equals(this.getCrestCourtRoomNo(), other.getCrestCourtRoomNo())) {
            return false;
        }

        // prefer comparing the actual LocalDateTime if available
        if (Objects.equals(this.getEventTime(), other.getEventTime())) {
            return true;
        }

        // fallback: compare string representations (handles formatting/precision differences)
        return Objects.equals(this.getEventTimeAsString(), other.getEventTimeAsString());
    }

    @Override
    public int hashCode() {
        // include eventTimeAsString as a stable hash component (covers cases where eventTime equals may vary)
        return Objects.hash(getCourtSiteCode(), getCrestCourtRoomNo(), getEventTimeAsString());
    }


}
