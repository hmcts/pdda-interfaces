package uk.gov.hmcts.pdda.business.services.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.Calendar;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public final class MergeDateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MergeDateUtils.class);
    private static final String EMPTY_STRING = "";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DAYOFWEEK = "dayofweek";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String HOUR = "hour";
    private static final String MIN = "min";

    private MergeDateUtils() {

    }

    /**
     * Create a calendar object with specific date and time.
     * 
     * @param time String
     * @param date String
     * @return Calendar
     */
    public static Calendar setupCalendar(String time, String date) {

        if (time != null && date != null && !EMPTY_STRING.equals(time) && !EMPTY_STRING.equals(date)) {
            String[] timeSplit = time.split(":");
            String[] dateSplit = date.split("/");

            int year = Integer.parseInt(dateSplit[2]);
            // January = 0.
            int month = Integer.parseInt(dateSplit[1]) - 1;
            int day = Integer.parseInt(dateSplit[0]);
            int hourOfDay = Integer.parseInt(timeSplit[0]);
            int minute = Integer.parseInt(timeSplit[1]);
            int second = 0;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hourOfDay, minute, second);
            return calendar;
        }
        return null;
    }

    /**
     * Returns the day in String format.
     * 
     * @param calendar Calendar
     * @return day
     */
    public static String returnDay(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "Sunday";
        }
    }

    /**
     * Returns the month in string forward.
     * 
     * @param calendar Calendar
     * @return month
     */
    public static String returnMonth(Calendar calendar) {
        int[] months = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER,
            Calendar.DECEMBER};
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
        for (int monthNo = 0; monthNo < months.length; monthNo++) {
            if (months[monthNo] == calendar.get(Calendar.MONTH)) {
                return monthNames[monthNo];
            }
        }
        return null;
    }

    /**
     * Method used in IWP that replaces the date section with the date/time that the merge is done.
     * 
     * @param baseDocument Document
     * @throws XPathExpressionException Exception
     */
    public static void replaceDateTime(Document baseDocument) throws XPathExpressionException {
        Calendar cal = Calendar.getInstance();
        int date = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int year = cal.get(Calendar.YEAR);

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate("//datetimestamp", baseDocument, XPathConstants.NODESET);

        for (int j = 0; j < nodes.getLength(); j++) {
            NodeList childNodes = nodes.item(j).getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (DAYOFWEEK.equals(childNodes.item(i).getNodeName())) {
                    childNodes.item(i).setTextContent(returnDay(cal));
                } else if (DATE.equals(childNodes.item(i).getNodeName())) {
                    childNodes.item(i).setTextContent(String.format("%02d", date));
                } else if (MONTH.equals(childNodes.item(i).getNodeName())) {
                    childNodes.item(i).setTextContent(returnMonth(cal));
                } else if (YEAR.equals(childNodes.item(i).getNodeName())) {
                    childNodes.item(i).setTextContent(Integer.toString(year));
                } else if (HOUR.equals(childNodes.item(i).getNodeName())) {
                    childNodes.item(i).setTextContent(String.format("%02d", hour));
                } else if (MIN.equals(childNodes.item(i).getNodeName())) {
                    childNodes.item(i).setTextContent(String.format("%02d", min));
                }
            }
        }
    }


    /**
     * Returns true is cpp is after xhibit in iwp and can therefore be replaced.
     * 
     * @param insertBeforeNode Node
     * @param childNodeToMerge Node
     * @return boolean
     */
    public static boolean isCppAfterXhibit(Node insertBeforeNode, Node childNodeToMerge) {

        String timeOnInsert = MergeNodeUtils.getNodeMapValues(Arrays.asList(TIME), childNodeToMerge).get(TIME);
        String dateOnInsert = MergeNodeUtils.getNodeMapValues(Arrays.asList(DATE), childNodeToMerge).get(DATE);
        Calendar cppTime = setupCalendar(timeOnInsert, dateOnInsert);

        String timeOnOriginal = MergeNodeUtils.getNodeMapValues(Arrays.asList(TIME), insertBeforeNode).get(TIME);
        String dateOnOriginal = MergeNodeUtils.getNodeMapValues(Arrays.asList(DATE), insertBeforeNode).get(DATE);
        Calendar xhibitTime = setupCalendar(timeOnOriginal, dateOnOriginal);

        if (xhibitTime != null && cppTime != null) {
            LOG.debug(xhibitTime.toString() + " after " + cppTime.toString() + " ?");
            return cppTime.after(xhibitTime);
        }
        return false;
    }
}
