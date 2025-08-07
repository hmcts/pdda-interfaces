package uk.gov.hmcts.framework.services.conversion;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Abdul Rahim Hussain
 * @version 1.0
 */
import java.sql.Timestamp;
import java.util.Calendar;

public class DataTypeConverter {

    protected DataTypeConverter() {
        // Protected constructor
    }

    public static Calendar convertToCalendar(Timestamp timestamp) {
        Calendar cal = null;

        if (timestamp != null) {
            cal = Calendar.getInstance();
            cal.setTime(new java.util.Date(timestamp.getTime()));
        }

        return cal;
    }

    public static Timestamp convertToTimestamp(Calendar cal) {
        Timestamp ts = null;

        if (cal != null) {
            ts = new Timestamp(cal.getTime().getTime());
        }

        return ts;
    }

}
