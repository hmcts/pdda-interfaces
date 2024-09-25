package uk.gov.hmcts.pdda.courtlog.vos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Title: CourtLogViewValue.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Joseph Babad / Paul Fitton
 * @version $Id: CourtLogViewValue.java,v 1.5 2011/05/25 14:11:44 atwells Exp $
 */
public class CourtLogViewValue extends CourtLogAbstractValue {

    private static final long serialVersionUID = -4292466573208039096L;
    private static final Logger LOG = LoggerFactory.getLogger(CourtLogViewValue.class);
    

    public enum EventTypes {
        BW_HISTORY_ISSUE_WARRANT_EVENT(20_100), BW_HISTORY_END_WARRANT_EVENT(20_101), CRACKED_INEFFECTIVE_EVENT(20_918);

        Integer value;

        EventTypes(Integer value) {
            this.value = value;
        }
        
        private Integer getValue() {
            return value;
        }
        
        public static EventTypes fromInteger(Integer value) {
            for (EventTypes type : values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    private String logEntry;
    private boolean dateAmended;

    public CourtLogViewValue() {
        super();
    }

    public CourtLogViewValue(Integer version) {
        super(version);
    }

    public String getLogEntry() {
        return this.logEntry;
    }

    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }

    public boolean isDateAmended() {
        return dateAmended;
    }

    public void setDateAmended(boolean isDateAmended) {
        this.dateAmended = isDateAmended;
    }

    public boolean isBwHistoryIssueWarrantEvent() {
        return EventTypes.BW_HISTORY_ISSUE_WARRANT_EVENT == EventTypes.fromInteger(getEventType());
    }

    public boolean isBwHistoryEndWarrantEvent() {
        return EventTypes.BW_HISTORY_END_WARRANT_EVENT == EventTypes.fromInteger(getEventType());
    }

    public boolean isCrackedIneffectiveEvent() {
        return EventTypes.CRACKED_INEFFECTIVE_EVENT == EventTypes.fromInteger(getEventType());
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
