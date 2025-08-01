package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: DailyListXMLMergeUtils Test.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@ExtendWith(EasyMockExtension.class)
class DailyListXmlMergeUtilsTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String DATE_FORMAT_MASK = "yyyy-MM-dd";
    private static final String CS_CHARGE_TAG = "                                        </cs:Charge>";

    private static final String DAILY_LIST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\" xmlns:"
        + "apd=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns=\"http://www."
        + "govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns:p2=\"http://www.govtalk.gov.uk/"
        + "people/bs7666\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation"
        + "=\"http://www.courtservice.gov.uk/schemas/courtservice DailyList-v5-9.xsd\">" + "    <cs:DocumentID>"
        + "        <cs:DocumentName>DailyList_999_200108141220.xml</cs:DocumentName>"
        + "        <cs:UniqueID>0149051e-9db2-4b47-999e-fef76909ee73</cs:UniqueID>"
        + "        <cs:DocumentType>DL</cs:DocumentType>" + "    </cs:DocumentID>" + "    <cs:ListHeader>"
        + "        <cs:ListCategory>Criminal</cs:ListCategory>" + "        <cs:StartDate>2020-01-21</cs:StartDate>"
        + "        <cs:EndDate>2020-01-21</cs:EndDate>" + "        <cs:Version>NOT VERSIONED</cs:Version>"
        + "        <cs:PublishedTime>2020-01-07T18:45:03.000</cs:PublishedTime>"
        + "        <cs:CRESTlistID>510</cs:CRESTlistID>" + "    </cs:ListHeader>" + "    <cs:CrownCourt>"
        + "        <cs:CourtHouseType>Crown Court</cs:CourtHouseType>"
        + "        <cs:CourtHouseCode CourtHouseShortName=\"SNARE\">453</cs:CourtHouseCode>"
        + "        <cs:CourtHouseName>SNARESBROOK</cs:CourtHouseName>" + "    </cs:CrownCourt>" + "    <cs:CourtLists>"
        + "        <cs:CourtList>" + "            <cs:CourtHouse>"
        + "                <cs:CourtHouseType>Crown Court</cs:CourtHouseType>"
        + "                <cs:CourtHouseCode>453</cs:CourtHouseCode>"
        + "                <cs:CourtHouseName>SNARESBROOK</cs:CourtHouseName>" + "            </cs:CourtHouse>"
        + "            <cs:Sittings>" + "                <cs:Sitting>"
        + "                    <cs:CourtRoomNumber>235</cs:CourtRoomNumber>"
        + "                    <cs:SittingSequenceNo>1</cs:SittingSequenceNo>"
        + "                    <cs:SittingPriority>T</cs:SittingPriority>" + "                    <cs:Judiciary>"
        + "                        <cs:Judge>"
        + "                            <apd:CitizenNameSurname>Van-JUDGE</apd:CitizenNameSurname>"
        + "                            <apd:CitizenNameRequestedName>Freddy</apd:CitizenNameRequestedName>"
        + "                        </cs:Judge>" + "                    </cs:Judiciary>"
        + "                    <cs:Hearings>" + "                        <cs:Hearing>"
        + "                            <cs:HearingSequenceNumber>1</cs:HearingSequenceNumber>"
        + "                            <cs:HearingDetails HearingType=\"TRL\">"
        + "                                <cs:HearingDescription>For Trial</cs:HearingDescription>"
        + "                                <cs:HearingDate>2020-01-21</cs:HearingDate>"
        + "                            </cs:HearingDetails>"
        + "                            <cs:CaseNumber>92AD685737</cs:CaseNumber>"
        + "                            <cs:Prosecution ProsecutingAuthority=\"Crown Prosecution Service\">"
        + "                                <cs:ProsecutingReference>92AD685737</cs:ProsecutingReference>"
        + "                            </cs:Prosecution>" + "                            <cs:Defendants>"
        + "                                <cs:Defendant>" + "                                    <cs:PersonalDetails>"
        + "                                        <cs:Name>"
        + "                                            <apd:CitizenNameSurname>Van</apd:CitizenNameSurname>"
        + "                                            <apd:CitizenNameRequestedName>Sri</apd:"
        + "CitizenNameRequestedName>" + "                                        </cs:Name>"
        + "                                        <cs:IsMasked>no</cs:IsMasked>"
        + "                                    </cs:PersonalDetails>"
        + "                                    <cs:Charges>"
        + "                                        <cs:Charge CJSoffenceCode=\"TH68001A\" IndictmentCountNumber=\"0\">"
        + "                                            <cs:OffenceStatement>Attempted theft</cs:OffenceStatement>"
        + CS_CHARGE_TAG
        + "                                        <cs:Charge CJSoffenceCode=\"HC57001\" IndictmentCountNumber=\"0\">"
        + "                                            <cs:CRESTchargeID>453S00649408</cs:CRESTchargeID>"
        + "                                            <cs:OffenceStatement>Manslaughter on grounds of diminished "
        + "responsibility</cs:OffenceStatement>" + CS_CHARGE_TAG + "                                    </cs:Charges>"
        + "                                </cs:Defendant>" + "                                <cs:Defendant>"
        + "                                    <cs:PersonalDetails>"
        + "                                        <cs:Name>"
        + "                                            <apd:CitizenNameSurname>CPPSECOND</apd:CitizenNameSurname>"
        + "                                            <apd:CitizenNameRequestedName>Another</apd:"
        + "CitizenNameRequestedName>" + "                                        </cs:Name>"
        + "                                        <cs:IsMasked>no</cs:IsMasked>"
        + "                                    </cs:PersonalDetails>"
        + "                                    <cs:Charges>"
        + "                                        <cs:Charge CJSoffenceCode=\"TH68001A\" IndictmentCountNumber=\"0\">"
        + "                                            <cs:OffenceStatement>Attempted theft</cs:OffenceStatement>"
        + CS_CHARGE_TAG
        + "                                        <cs:Charge CJSoffenceCode=\"HC57001\" IndictmentCountNumber=\"0\">"
        + "                                            <cs:CRESTchargeID>453S00649408</cs:CRESTchargeID>"
        + "                                            <cs:OffenceStatement>Manslaughter on grounds of diminished "
        + "responsibility</cs:OffenceStatement>" + CS_CHARGE_TAG + "                                    </cs:Charges>"
        + "                                </cs:Defendant>" + "                            </cs:Defendants>"
        + "                        </cs:Hearing>" + "                    </cs:Hearings>"
        + "                </cs:Sitting>" + "            </cs:Sittings>" + "        </cs:CourtList>"
        + "    </cs:CourtLists>" + "</cs:DailyList>";

    @TestSubject
    private static DailyListXmlMergeUtils classUnderTest;

    @BeforeAll
    public static void setUp() {
        try {
            classUnderTest = new DailyListXmlMergeUtils();
        } catch (XPathExpressionException ex) {
            fail(ex.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetRootNodes() {
        // Setup
        final String[] expectedArray = {"DailyList/CourtLists/CourtList/Sittings"};

        // Run
        final String[] stringArr = classUnderTest.getRootNodes();

        // Checks
        assertArrayEquals(expectedArray, stringArr, EQUALS);
    }

    @Test
    void testGetNodePositionArray() {
        // Setup
        final String[] expectedArray = {"cs:CourtRoomNumber", "cs:SittingAt"};

        // Run
        final String[] stringArr = classUnderTest.getNodePositionArray();

        // Checks
        assertArrayEquals(expectedArray, stringArr, EQUALS);
    }

    @Test
    void testGetNodeMatchArray() {
        // Setup
        final String[] expectedArray = {"cs:CourtHouseCode", "cs:CourtHouseName"};

        // Run
        final String[] stringArr = classUnderTest.getNodeMatchArray();

        // Checks
        assertArrayEquals(expectedArray, stringArr, EQUALS);
    }

    @Test
    void testGetMergeType() {
        // Setup
        final String expectedString = "LISTS";

        // Run
        final String actualString = classUnderTest.getMergeType();

        // Checks
        assertEquals(expectedString, actualString, EQUALS);
    }

    @Test
    void testGetListStartDateFromDocument() {
        // Setup
        final Date expectedDate = parseDate("2020-01-21");
        final Document doc = convertStringToDocument(DAILY_LIST_XML);

        // Run
        final Date startDate = classUnderTest.getListStartDateFromDocument(doc);

        // Checks
        assertEquals(expectedDate, startDate, EQUALS);
    }

    @Test
    void testGetListEndDateFromDocument() {
        // Setup
        final Date expectedDate = parseDate("2020-01-21");
        final Document doc = convertStringToDocument(DAILY_LIST_XML);

        // Run
        final Date endDate = classUnderTest.getListEndDateFromDocument(doc);

        // Checks
        assertEquals(expectedDate, endDate, EQUALS);
    }

    @Test
    void testGetCourtHouseCodeFromDocument() {
        // Setup
        final String expectedCode = "453";
        final Document doc = convertStringToDocument(DAILY_LIST_XML);

        // Run
        final String courtHouseCode = classUnderTest.getCourtHouseCodeFromDocument(doc);

        // Checks
        assertEquals(expectedCode, courtHouseCode, EQUALS);
    }

    /**
     * Converts a String into a Document object.

     * @param xml XML String
     * @return Document
     */
    private Document convertStringToDocument(final String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception exception) {
            fail(exception);
        }
        return null;
    }

    private Date parseDate(String dateAsString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK, Locale.getDefault());
        try {
            return dateFormat.parse(dateAsString);
        } catch (ParseException e) {
            return null;
        }
    }

}
