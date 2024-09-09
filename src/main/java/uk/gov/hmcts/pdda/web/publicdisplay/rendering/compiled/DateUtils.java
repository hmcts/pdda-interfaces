
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    public static final DateTimeFormatter DATETIME_NOSEC_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");  
    
    private DateUtils() {
    }

    /**
     * Get the date, this is added to the velocity context in AbstractTemplateRenderer. Should be
     * called once per render.
     * 
     * @return the current date
     */
    public static Date getDate() {
        return new Date();
    }
    
    public static String getDate(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_FORMAT); 
    }
    
    public static String getTime(LocalDateTime localDateTime) {
        return localDateTime.format(TIME_FORMAT); 
    }

    public static String get2digits(int valueIn) {
        Integer value = Integer.valueOf(100 + valueIn);
        String result = value.toString().substring(1, 3);
        LOG.debug("getMinutes = {}", result);
        return result;
    }

    public static String getEventTimeAsString(Object item) {
        if (item instanceof PublicDisplayValue) {
            String eventTime = ((PublicDisplayValue) item).getEventTimeAsString();
            if (eventTime != null) {
                return eventTime;
            }
        }
        return null;
    }

    public static String getNotBeforeTimeAsString(Object item) {
        if (item instanceof PublicDisplayValue) {
            String notBeforeTimeAsString = ((PublicDisplayValue) item).getNotBeforeTimeAsString();
            if (notBeforeTimeAsString != null) {
                return notBeforeTimeAsString;
            }
        }
        return null;
    }

    public static String getMonth(Integer month) {
        String result = getMonthQ1(month);
        if (result == null) {
            result = getMonthQ2(month);
        }
        if (result == null) {
            result = getMonthQ3(month);
        }
        if (result == null) {
            result = getMonthQ4(month);
        }
        if (result == null) {
            result = "???";
        }
        return result;
    }

    private static String getMonthQ1(Integer month) {
        if (month.equals(Calendar.JANUARY)) {
            return "Jan";
        } else if (month.equals(Calendar.FEBRUARY)) {
            return "Feb";
        } else if (month.equals(Calendar.MARCH)) {
            return "Mar";
        }
        return null;
    }

    private static String getMonthQ2(Integer month) {
        if (month.equals(Calendar.APRIL)) {
            return "Apr";
        } else if (month.equals(Calendar.MAY)) {
            return "May";
        } else if (month.equals(Calendar.JUNE)) {
            return "Jun";
        }
        return null;
    }

    private static String getMonthQ3(Integer month) {
        if (month.equals(Calendar.JULY)) {
            return "Jul";
        } else if (month.equals(Calendar.AUGUST)) {
            return "Aug";
        } else if (month.equals(Calendar.SEPTEMBER)) {
            return "Sep";
        }
        return null;
    }

    private static String getMonthQ4(Integer month) {
        if (month.equals(Calendar.OCTOBER)) {
            return "Oct";
        } else if (month.equals(Calendar.NOVEMBER)) {
            return "Nov";
        } else if (month.equals(Calendar.DECEMBER)) {
            return "Dec";
        }
        return null;
    }
    
}