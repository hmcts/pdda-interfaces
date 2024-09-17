package uk.gov.hmcts.framework.services.conversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



/**
 * <p>
 * Title: XDateFormat.
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
 * @author unascribed
 * @version 1.0
 */
/*
 * Ref Date Author Description
 * 
 * 53674 14-08-2003 Neil Entwistle Added new format to include seconds in the time.
 * 
 */

public final class XDateFormat {

    public static final int DATEFORMAT = 0;

    public static final int TIMEFORMAT = 1;

    public static final int DATETIMEFORMAT = 2;

    public static final int DATETIMEINSECSFORMAT = 3;

    public static final int TIMEINSECSFORMAT = 4;

    public static final int DAYOFWEEKFORMAT = 5;

    public static final int FULLMONTHFORMAT = 6;

    public static final String SIMPLEDATEFORMAT = "dd-MMM-yyyy";

    public static final String SIMPLETIMEFORMAT = "HH:mm";

    public static final String SIMPLETIMEINSECSFORMAT = "HH:mm:ss";

    public static final String SIMPLEDAYOFWEEKFORMAT = "EEEE dd MMMM yyyy";

    public static final String SIMPLEFULLMONTHFORMAT = "dd MMMM yyyy";

    private static final Logger LOG = LoggerFactory.getLogger(XDateFormat.class);

    private XDateFormat() {
    }

    public static String format(Timestamp date, int dateTimeFormat) {
        return format((Date) date, dateTimeFormat);
    }

    public static String format(Calendar date, int dateTimeFormat) {
        if (date == null) {
            return "";
        } else {
            return format(date.getTime(), dateTimeFormat);
        }
    }

    public static String format(Date date, int dateTimeFormat) {
        String strSelectedFormat;
        SimpleDateFormat formatter;

        if (date == null) {
            return "";
        } else {
            switch (dateTimeFormat) {
                case DATEFORMAT:
                    // Date only
                    strSelectedFormat = SIMPLEDATEFORMAT;
                    break;
                case TIMEFORMAT:
                    // Time only
                    strSelectedFormat = SIMPLETIMEFORMAT;
                    break;
                case DATETIMEFORMAT:
                    // Date and Time
                    strSelectedFormat = SIMPLEDATEFORMAT + " " + SIMPLETIMEFORMAT;
                    // Fall through to include seconds
                    break;
                case DATETIMEINSECSFORMAT:
                    // Date + time with seconds
                    strSelectedFormat = SIMPLEDATEFORMAT + " " + SIMPLETIMEINSECSFORMAT;
                    break;
                case TIMEINSECSFORMAT:
                    // Added format for Time including seconds to allow for Court
                    // Log sorting issues
                    // Time with seconds
                    strSelectedFormat = SIMPLETIMEINSECSFORMAT;
                    break;
                case DAYOFWEEKFORMAT:
                    strSelectedFormat = SIMPLEDAYOFWEEKFORMAT;
                    break;
                case FULLMONTHFORMAT:
                    strSelectedFormat = SIMPLEFULLMONTHFORMAT;
                    break;
                default:
                    strSelectedFormat = SIMPLEDATEFORMAT + " " + SIMPLETIMEFORMAT;
                    break;
            }
            formatter = new SimpleDateFormat(strSelectedFormat, Locale.getDefault());
            return formatter.format(date);
        }
    }

    /**
     * Supply a date as a String and this will attempt to parse it.
     * 
     * @param date String
     * @return Date
     * @throws ParseException Exception
     */
    public static Date parseAsDate(String date) throws ParseException {
        Date parsedDate;
        // Attempt to parse date using XHIBIT date format
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                SIMPLEDATEFORMAT + " " + SIMPLETIMEINSECSFORMAT, Locale.getDefault());
            parsedDate = formatter.parse(date);
            return parsedDate;
        } catch (ParseException ex) {
            doNothing();
        }
        // If that fails attempt to parse using universal formats.
        try {
            parsedDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(date);
            return parsedDate;
        } catch (ParseException ex) {
            doNothing();
        }
        try {
            parsedDate = DateFormat.getDateInstance(DateFormat.MEDIUM).parse(date);
            return parsedDate;
        } catch (ParseException ex) {
            doNothing();
        }
        try {
            parsedDate = DateFormat.getDateInstance(DateFormat.LONG).parse(date);
            return parsedDate;
        } catch (ParseException ex) {
            doNothing();
        }
        try {
            parsedDate = DateFormat.getDateInstance(DateFormat.FULL).parse(date);
            return parsedDate;
        } catch (ParseException ex) {
            LOG.error(ex.getMessage());
            throw ex;
        }
    }
    
    private static void doNothing() {
        // do nothing
    }

    public static Calendar parse(String date) throws ParseException {
        Date parsedDate = parseAsDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);
        return calendar;
    }

}
