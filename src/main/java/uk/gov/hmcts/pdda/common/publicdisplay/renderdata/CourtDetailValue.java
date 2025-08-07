package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  CourtDetailValue.
 * 
 * @author pznwc5 Value object for court detail.
 */
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
     * 
     * @param judgeName JudgeName
     */
    public void setJudgeName(JudgeName judgeName) {
        this.judgeName = judgeName;
    }

    /**
     * getJudgeName.
     * 
     * @return JudgeName
     */
    public JudgeName getJudgeName() {
        return judgeName;
    }

    /**
     * setHearingDescription.
     * 
     * @param hearingDescription String
     */
    public void setHearingDescription(String hearingDescription) {
        this.hearingDescription = hearingDescription;
    }

    /**
     * getHearingDescription.
     * 
     * @return String
     */
    public String getHearingDescription() {
        return hearingDescription;
    }

    /**
     * setPublicNotices.
     * 
     * @param publicNotices PublicNoticeValueArray
     */
    public void setPublicNotices(PublicNoticeValue... publicNotices) {
        this.publicNotices = publicNotices.clone();
    }

    /**
     * getPublicNotices.
     * 
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
        LOG.debug("equals()");
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
