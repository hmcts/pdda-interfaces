package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.util.Calendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: MergeDateUtils Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class MergeDateUtilsTest {

    private static final String NOT_EQUAL = "Result is Not Equal";
    private static final String NOT_NULL = "Result is Not Null";
    private static final String NULL = "Result is Null";
    private static final String NOT_TRUE = "Result is Not True";

    public static final String SUNDAY = "Sunday";
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";
    public static final String SATURDAY = "Saturday";

    public static final String JANUARY = "January";
    public static final String FEBRUARY = "February";
    public static final String MARCH = "March";
    public static final String APRIL = "April";
    public static final String MAY = "May";
    public static final String JUNE = "June";
    public static final String JULY = "July";
    public static final String AUGUST = "August";
    public static final String SEPTEMBER = "September";
    public static final String OCTOBER = "October";
    public static final String NOVEMBER = "November";
    public static final String DECEMBER = "December";

    @Test
    void testSetupCalendarNull() {
        assertNull(MergeDateUtils.setupCalendar(null, null), NOT_NULL);
    }

    @Test
    void testSetupCalendarNotNull() {
        assertNotNull(MergeDateUtils.setupCalendar("10:00:00", "01/01/2024"), NULL);
    }

    @Test
    void testReturnDay() {
        Calendar day1 = MergeDateUtils.setupCalendar("10:00:00", "29/02/2024");
        assertEquals(THURSDAY, MergeDateUtils.returnDay(day1), NOT_EQUAL);
        Calendar day2 = addOneDay(day1);
        assertEquals(FRIDAY, MergeDateUtils.returnDay(day2), NOT_EQUAL);
        Calendar day3 = addOneDay(day2);
        assertEquals(SATURDAY, MergeDateUtils.returnDay(day3), NOT_EQUAL);
        Calendar day4 = addOneDay(day3);
        assertEquals(SUNDAY, MergeDateUtils.returnDay(day4), NOT_EQUAL);
        Calendar day5 = addOneDay(day4);
        assertEquals(MONDAY, MergeDateUtils.returnDay(day5), NOT_EQUAL);
        Calendar day6 = addOneDay(day5);
        assertEquals(TUESDAY, MergeDateUtils.returnDay(day6), NOT_EQUAL);
        Calendar day7 = addOneDay(day6);
        assertEquals(WEDNESDAY, MergeDateUtils.returnDay(day7), NOT_EQUAL);
    }

    private Calendar addOneDay(Calendar day) {
        Calendar result = (Calendar) day.clone();
        result.add(Calendar.DAY_OF_MONTH, 1);
        return result;
    }

    @Test
    void testReturnMonth() {
        Calendar month1 = MergeDateUtils.setupCalendar("10:00:00", "01/01/2024");
        assertEquals(JANUARY, MergeDateUtils.returnMonth(month1), NOT_EQUAL);
        Calendar month2 = addOneMonth(month1);
        assertEquals(FEBRUARY, MergeDateUtils.returnMonth(month2), NOT_EQUAL);
        Calendar month3 = addOneMonth(month2);
        assertEquals(MARCH, MergeDateUtils.returnMonth(month3), NOT_EQUAL);
        Calendar month4 = addOneMonth(month3);
        assertEquals(APRIL, MergeDateUtils.returnMonth(month4), NOT_EQUAL);
        Calendar month5 = addOneMonth(month4);
        assertEquals(MAY, MergeDateUtils.returnMonth(month5), NOT_EQUAL);
        Calendar month6 = addOneMonth(month5);
        assertEquals(JUNE, MergeDateUtils.returnMonth(month6), NOT_EQUAL);
        Calendar month7 = addOneMonth(month6);
        assertEquals(JULY, MergeDateUtils.returnMonth(month7), NOT_EQUAL);
        Calendar month8 = addOneMonth(month7);
        assertEquals(AUGUST, MergeDateUtils.returnMonth(month8), NOT_EQUAL);
        Calendar month9 = addOneMonth(month8);
        assertEquals(SEPTEMBER, MergeDateUtils.returnMonth(month9), NOT_EQUAL);
        Calendar month10 = addOneMonth(month9);
        assertEquals(OCTOBER, MergeDateUtils.returnMonth(month10), NOT_EQUAL);
        Calendar month11 = addOneMonth(month10);
        assertEquals(NOVEMBER, MergeDateUtils.returnMonth(month11), NOT_EQUAL);
        Calendar month12 = addOneMonth(month11);
        assertEquals(DECEMBER, MergeDateUtils.returnMonth(month12), NOT_EQUAL);
    }

    private Calendar addOneMonth(Calendar month) {
        Calendar result = (Calendar) month.clone();
        result.add(Calendar.MONTH, 1);
        return result;
    }

    @Test
    void testReplaceDateTime()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        String xml = "<datetimestamp><dayofweek></dayofweek><date></date><month></month>"
            + "<year></year><hour></hour><min></min></datetimestamp>";
        Document testDocument = getDummyDoc(xml);
        boolean result = true;
        // Run
        MergeDateUtils.replaceDateTime(testDocument);
        // Checks
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testIsCppAfterXhibit() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        String origionalXml = "<courtroomname><cases>testCourtRoom<date>01/01/2024</date>"
            + "<time>10:00:00</time></cases></courtroomname>";
        String insertXml = "<courtroomname><cases>testCourtRoom<date>02/01/2024</date>"
            + "<time>11:00:00</time></cases></courtroomname>";

        Document origionalDocument = getDummyDoc(origionalXml);
        Node origionalNode = origionalDocument.getFirstChild().getChildNodes().item(0);
        Document insertBeforeDocument = getDummyDoc(insertXml);
        Node insertBeforeNode = insertBeforeDocument.getFirstChild().getChildNodes().item(0);
        // Run
        boolean result = MergeDateUtils.isCppAfterXhibit(origionalNode, insertBeforeNode);
        // Checks
        assertTrue(result, NOT_TRUE);
    }

    private Document getDummyDoc(String xml)
        throws SAXException, IOException, ParserConfigurationException {
        return DocumentUtils.createInputDocument(xml);
    }
}
