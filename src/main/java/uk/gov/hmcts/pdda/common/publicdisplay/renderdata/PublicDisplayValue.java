package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Common superclass for public display data classes.
 * 
 * @author pznwc5
 */
public class PublicDisplayValue extends AbstractValue implements Comparable<PublicDisplayValue> {

    static final long serialVersionUID = -1321945076202443846L;

    private static final Logger LOG = LoggerFactory.getLogger(PublicDisplayValue.class);

    /**
     * Name of the court room.
     */
    private String courtRoomName;

    /**
     * Moved from court room.
     */
    private String movedFromCourtRoomName;

    /**
     * Moved from court site short name.
     */
    private String movedFromCourtSiteShortName;

    /**
     * Id of the court room.
     */
    private Integer courtRoomId;

    /**
     * Moved from court room id.
     */
    private Integer movedFromCourtRoomId;

    /**
     * Not before time.
     */
    private LocalDateTime notBeforeTime;

    /**
     * Court Site Name.
     */
    private String courtSiteName;

    /**
     * Court Site Short Name.
     */
    private String courtSiteShortName;

    /**
     * Court Site Code.
     */
    private String courtSiteCode;

    /**
     * Crest Court Room Number.
     */
    private Integer crestCourtRoomNo;

    /**
     * The Court Log event information to be processed by the Public Display rendering.
     */
    private BranchEventXmlNode event;

    /**
     * The Court Log event information to be processed by the Public Display rendering.
     */
    private LocalDateTime eventTime;

    /**
     * Gets the Court Log Event Node.
     */
    public BranchEventXmlNode getEvent() {
        return event;
    }

    /**
     * Sets the Court Log Event Node.
     * 
     * @param val BranchEventXMLNode
     */
    public void setEvent(BranchEventXmlNode val) {
        event = val;
    }

    /**
     * Gets the Court Log Event Time.
     */
    public LocalDateTime getEventTime() {
        // For XHIBIT events there may not be an event time, so assume the event time is midnight
        if (eventTime == null) {
            LOG.debug("Event time is null. Defaulting to 00:01.");
            return LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 1));
        }
        return eventTime;
    }

    /**
     * Gets the Court Log Event Time as String.
     */
    public String getEventTimeAsString() {
        if (eventTime == null) {
            LOG.debug("Invalid event time entered.");
            return "";
        }

        return DATEFORMAT.format(eventTime);
    }

    /**
     * Sets the Court Log Event Node.
     * 
     * @param timeIn LocalDateTime
     */
    public void setEventTime(LocalDateTime timeIn) {
        eventTime = timeIn;
    }

    /**
     * Gets the court site name.
     * 
     * @return Court Site Name
     */
    public String getCourtSiteName() {
        return courtSiteName;
    }

    /**
     * Gets the court site short name.
     * 
     * @return Court Site Short Name
     */
    public String getCourtSiteShortName() {
        return courtSiteShortName;
    }

    /**
     * Gets the court site code.
     * 
     * @return Court Site Code
     */
    public String getCourtSiteCode() {
        return courtSiteCode;
    }

    /**
     * Sets the Court Site Name.
     * 
     * @param courtSiteName String
     */
    public void setCourtSiteName(String courtSiteName) {
        this.courtSiteName = courtSiteName;
    }

    /**
     * Sets the Court Site Short Name.
     * 
     * @param courtSiteShortName String
     */
    public void setCourtSiteShortName(String courtSiteShortName) {
        this.courtSiteShortName = courtSiteShortName;
    }

    /**
     * Sets the Court Site Code.
     * 
     * @param courtSiteCode String
     */
    public void setCourtSiteCode(String courtSiteCode) {
        this.courtSiteCode = courtSiteCode;
    }

    /**
     * Gets the court room in which the case is heard.
     * 
     * @param val Court room in which the case is heard
     */
    public void setCourtRoomId(int val) {
        courtRoomId = val;
    }

    /**
     * Gets the court room in which the case is heard.
     * 
     * @return Court room in which the case is heard
     */
    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    /**
     * Gets the moved court room in which the case is heard.
     * 
     * @param val Court room in which the case is heard
     */
    public void setMovedFromCourtRoomId(Integer val) {
        movedFromCourtRoomId = val;
    }

    /**
     * Gets the moved court room in which the case is heard.
     * 
     * @return Court room in which the case is heard
     */
    public Integer getMovedFromCourtRoomId() {
        return movedFromCourtRoomId;
    }

    /**
     * Gets the court room in which the case is heard.
     * 
     * @param val Court room in which the case is heard
     */
    public void setCourtRoomName(String val) {
        courtRoomName = val;
    }

    /**
     * Gets the court room in which the case is heard.
     * 
     * @return Court room in which the case is heard
     */
    public String getCourtRoomName() {
        return courtRoomName;
    }

    /**
     * Sets the court site short name from where the case is moved.
     */
    public void setMovedFromCourtSiteShortName(String val) {
        movedFromCourtSiteShortName = val;
    }

    /**
     * Gets the court site short name from where the case is moved.
     * 
     * @return Court site short name from where the case is moved
     */
    public String getMovedFromCourtSiteShortName() {
        return movedFromCourtSiteShortName;
    }

    /**
     * Gets the court room from where the case is moved.
     * 
     * @param val Court room from where the case is moved
     */
    public void setMovedFromCourtRoomName(String val) {
        movedFromCourtRoomName = val;
    }

    /**
     * Gets the court room from where the case is moved.
     * 
     * @return Court room from where the case is moved
     */
    public String getMovedFromCourtRoomName() {
        return movedFromCourtRoomName;
    }

    /**
     * Gets the time of hearing.
     * 
     * @param val Time of hearing
     */
    public void setNotBeforeTime(LocalDateTime val) {
        notBeforeTime = val;
    }

    /**
     * Gets the time of hearing.
     * 
     * @return Time of hearing
     */
    public LocalDateTime getNotBeforeTime() {
        return notBeforeTime;
    }

    /**
     * Returns not before time as a formatted string.
     * 
     * @return String
     */
    public String getNotBeforeTimeAsString() {
        if (notBeforeTime == null) {
            LOG.debug("Invalid not before time entered.");
            return "";
        }

        return DATEFORMAT.format(notBeforeTime);
    }

    /**
     * Returns Crest Court Room Number.
     * 
     * @return Integer Crest Court Room Number
     */
    public Integer getCrestCourtRoomNo() {
        return crestCourtRoomNo;
    }

    /**
     * Sets Crest Court Room Number.
     * 
     * @param crestCourtRoomNo Integer
     */
    public void setCrestCourtRoomNo(Integer crestCourtRoomNo) {
        this.crestCourtRoomNo = crestCourtRoomNo;
    }

    @Override
    public boolean hasInformationForDisplay() {
        return true;
    }

    @Override
    public int compareTo(PublicDisplayValue other) {
        if (!this.courtSiteCode.equals(other.getCourtSiteCode())) {
            return this.courtSiteCode.compareTo(other.getCourtSiteCode());
        }
        if (!this.crestCourtRoomNo.equals(other.getCrestCourtRoomNo())) {
            return this.crestCourtRoomNo - other.getCrestCourtRoomNo();
        }
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AllCourtStatusValue
            && this.getCourtSiteCode().equals(((AllCourtStatusValue) object).getCourtSiteCode())
            && this.getCrestCourtRoomNo().equals(((AllCourtStatusValue) object).getCrestCourtRoomNo());
    }

    @Override
    public int hashCode() {
        String hash = this.getCourtSiteCode() + this.getCrestCourtRoomNo();
        return hash.hashCode();
    }

    /**
     * Method used to compare two timestamps which could potentially be null.
     * 
     * @param ts1 LocalDateTime 1
     * @param ts2 LocalDateTime 2
     * @return comparison result
     */
    protected int compareNotBeforeTimeCheckNull(LocalDateTime ts1, LocalDateTime ts2) {
        int cmp;
        if (ts1 == null) {
            // Timestamp 1 is null
            if (ts2 == null) {
                // Timestamp 2 is also null
                cmp = 0;
            } else {
                // Timestamp 2 is not null
                cmp = -1;
            }
        } else if (ts2 == null) {
            // Timestamp 1 is not null but Timestamp 2 is null
            cmp = 1;
        } else {
            // Neither Timestamp is null, so do a Timestamp compare
            cmp = ts1.compareTo(ts2);
        }
        return cmp;
    }

}
