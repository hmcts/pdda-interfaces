package uk.gov.hmcts.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**

 * Title: DateTimeUtilities.


 * Description: This class is used as a repositiry for common functions and utilities relating to
 * dates and time


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Ian Hannaford / Marie Holmberg
 * @version 1.0
 */
public class DateTimeUtilities {
    // The logger for this class.
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeUtilities.class);

    private static final String ORACLE_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss";

    protected DateTimeUtilities() {
        super();
    }

    /**
     * Retrieve only the "date" part of a LocalDateTime.

     * @param date the LocalDateTime without the time portion
     */
    public static LocalDateTime stripTime(LocalDateTime date) {
        LocalDateTime strippedDate = date;
        strippedDate = strippedDate.minusHours(strippedDate.getHour());
        strippedDate = strippedDate.minusMinutes(strippedDate.getMinute());
        strippedDate = strippedDate.minusSeconds(strippedDate.getSecond());
        strippedDate = strippedDate.minusNanos(strippedDate.getNano());

        return strippedDate;
    }

    /**
     * Converts a formatted String to a Calendar in the Oracle date format Expected format of Oracle
     * Date is yyyy-mm-ddTHH:mm:ss.

     * @param timeStamp String
     * @return Calendar
     */
    public static Calendar processOracleDateParameter(String timeStamp) throws ParseException {
        return processDateParameter(timeStamp, new SimpleDateFormat(ORACLE_DATE_FORMAT_STRING, Locale.getDefault()));
    }

    /**
     * Converts a formatted String to a Date in the Oracle date format.

     * @param timeStamp String
     * @return Date
     * @throws ParseException Exception
     */
    public static Date processOracleDateParameterForDate(String timeStamp) throws ParseException {
        Calendar cal =
            processDateParameter(timeStamp, new SimpleDateFormat(ORACLE_DATE_FORMAT_STRING, Locale.getDefault()));
        return calendarToDate(cal);
    }
    
    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * Converts a formatted String to a Calendar.

     * @param timeStamp String (mandatory and non-null)
     * @param format SimpleDateFormat (mandatory and non-null)
     * @return Calendar
     */
    public static Calendar processDateParameter(String timeStamp, SimpleDateFormat format)
        throws ParseException {
        LOG.debug("processDateParameter, timeStamp: {}", timeStamp);

        Calendar time = Calendar.getInstance();

        time.setTime(format.parse(timeStamp));
        return time;
    }
}
