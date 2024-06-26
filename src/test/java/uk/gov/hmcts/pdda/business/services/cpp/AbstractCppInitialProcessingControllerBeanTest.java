package uk.gov.hmcts.pdda.business.services.cpp;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.exception.CsBusinessException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;
import uk.gov.hmcts.pdda.business.services.cpplist.CppListControllerBean;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * AbstractCppInitialProcessingControllerBeanTest.
 **/
class AbstractCppInitialProcessingControllerBeanTest {

    protected static final String FALSE = "Result is not False";
    protected static final String TRUE = "Result is not True";
    protected static final String EQUALS = "Results are not Equal";
    protected static final String BATCH_USERNAME = "CPPX_SCHEDULED_JOB";

    protected static final String INVALID_DOCTYPE = "ER";

    protected static final String DAILY_LIST_DOCNAME = "Test_DailyList.xml";
    protected static final String DAILY_LIST_DOCTYPE = "DL";
    private static final String COURTSITETAG = "      <courtsite>\r\n";
    private static final String COURTSITEENDTAG = "      </courtsite>\r\n";
    private static final String COURTROOMTAG = "          <courtroom>\r\n";
    private static final String COURTROOMENDTAG = "          </courtroom>\r\n";
    private static final String COURTROOMSTAG = "        <courtrooms>\r\n";
    private static final String COURTROOMSENDTAG = "        </courtrooms>\r\n";
    private static final String CURRENTSTATUSENDTAG = "            <currentstatus/>\r\n";
    private static final String COURTROOMNAMETAG = "            <courtroomname>Court 2</courtroomname>\r\n";
    private static final String CHARGETAG = "                                        </cs:Charge>";
    protected static final String DAILY_LIST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\" xmlns:apd=\"http:"
        + "//www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns=\"http://www.govtalk.gov.uk/people/"
        + "AddressAndPersonalDetails\" xmlns:p2=\"http://www.govtalk.gov.uk/people/bs7666\" xmlns:xsi=\"http://"
        + "www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.courtservice.gov.uk/schemas/"
        + "courtservice DailyList-v5-9.xsd\">" + "    <cs:DocumentID>"
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
        + CHARGETAG
        + "                                        <cs:Charge CJSoffenceCode=\"HC57001\" IndictmentCountNumber=\"0\">"
        + "                                            <cs:CRESTchargeID>453S00649408</cs:CRESTchargeID>"
        + "                                            <cs:OffenceStatement>Manslaughter on grounds of diminished "
        + "responsibility</cs:OffenceStatement>" + CHARGETAG + "                                    </cs:Charges>"
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
        + CHARGETAG
        + "                                        <cs:Charge CJSoffenceCode=\"HC57001\" IndictmentCountNumber=\"0\">"
        + "                                            <cs:CRESTchargeID>453S00649408</cs:CRESTchargeID>"
        + "                                            <cs:OffenceStatement>Manslaughter on grounds of diminished "
        + "responsibility</cs:OffenceStatement>" + CHARGETAG + "                                    </cs:Charges>"
        + "                                </cs:Defendant>" + "                            </cs:Defendants>"
        + "                        </cs:Hearing>" + "                    </cs:Hearings>"
        + "                </cs:Sitting>" + "            </cs:Sittings>" + "        </cs:CourtList>"
        + "    </cs:CourtLists>" + "</cs:DailyList>";

    protected static final String WEBPAGE_DOCNAME = "Test_Webpage.xml";
    protected static final String WEBPAGE_DOCTYPE = "WP";
    protected static final String INTERNET_WEBPAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
        + "<?xml-stylesheet type=\"text/xsl\" href=\"InternetWebPageTemplate.xsl\"?>\r\n"
        + "<currentcourtstatus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + "  <court>\r\n"
        + "    <courtname>SNARESBROOK</courtname>\r\n" + "    <courtsites>\r\n" + COURTSITETAG
        + "        <courtsitename>SNARESBROOK mu</courtsitename>\r\n" + COURTROOMSTAG + COURTROOMTAG
        + "            <cases>\r\n" + "              <caseDetails>\r\n"
        + "                <casenumber>20200028</casenumber>\r\n" + "                <casetype>T</casetype>\r\n"
        + "                <hearingtype>Appeal (Part Heard)</hearingtype>\r\n" + "              </caseDetails>\r\n"
        + "            </cases>\r\n" + "            <defendants>\r\n" + "              <defendant>\r\n"
        + "                <firstname>cppME</firstname>\r\n" + "                <lastname>MAcpp</lastname>\r\n"
        + "              </defendant>\r\n" + "            </defendants>\r\n" + "            <currentstatus>\r\n"
        + "              <event>\r\n" + "                <time>11:11</time>\r\n"
        + "                <date>05/08/20</date>\r\n"
        + "                <free_text>My new reporting restriction</free_text>\r\n"
        + "                <type>CPP</type>\r\n" + "              </event>\r\n" + "            </currentstatus>\r\n"
        + "            <timestatusset>10:10</timestatusset>\r\n" + COURTROOMNAMETAG + COURTROOMENDTAG + COURTROOMTAG
        + CURRENTSTATUSENDTAG + COURTROOMNAMETAG + COURTROOMENDTAG + COURTROOMSENDTAG + COURTSITEENDTAG + COURTSITETAG
        + "        <courtsitename>PRESTON3</courtsitename>\r\n" + COURTROOMSTAG + COURTROOMTAG + CURRENTSTATUSENDTAG
        + "            <courtroomname>Court 1</courtroomname>\r\n" + COURTROOMENDTAG + COURTROOMTAG
        + CURRENTSTATUSENDTAG + COURTROOMNAMETAG + COURTROOMENDTAG + COURTROOMTAG + CURRENTSTATUSENDTAG
        + "            <courtroomname>Court 3</courtroomname>\r\n" + COURTROOMENDTAG + COURTROOMTAG
        + CURRENTSTATUSENDTAG + "            <courtroomname>Court 5</courtroomname>\r\n" + COURTROOMENDTAG
        + COURTROOMTAG + CURRENTSTATUSENDTAG + "            <courtroomname>Court 10</courtroomname>\r\n"
        + COURTROOMENDTAG + COURTROOMTAG + CURRENTSTATUSENDTAG
        + "            <courtroomname>Court 11</courtroomname>\r\n" + COURTROOMENDTAG + COURTROOMTAG
        + CURRENTSTATUSENDTAG + "            <courtroomname>Court 12</courtroomname>\r\n" + COURTROOMENDTAG
        + COURTROOMSENDTAG + COURTSITEENDTAG + COURTSITETAG + "        <courtsitename>VILNIUS</courtsitename>\r\n"
        + COURTROOMSTAG + COURTROOMTAG + CURRENTSTATUSENDTAG + "            <courtroomname>Court 9</courtroomname>\r\n"
        + COURTROOMENDTAG + COURTROOMTAG + CURRENTSTATUSENDTAG
        + "            <courtroomname>Court 10</courtroomname>\r\n" + COURTROOMENDTAG + COURTROOMTAG
        + CURRENTSTATUSENDTAG + "            <courtroomname>Court 11</courtroomname>\r\n" + COURTROOMENDTAG
        + COURTROOMSENDTAG + COURTSITEENDTAG + COURTSITETAG + "        <courtsitename>VILLAGE HALL</courtsitename>\r\n"
        + COURTROOMSTAG + COURTROOMTAG + CURRENTSTATUSENDTAG + "            <courtroomname>Court 5</courtroomname>\r\n"
        + COURTROOMENDTAG + COURTROOMSENDTAG + COURTSITEENDTAG + COURTSITETAG
        + "        <courtsitename>Lewes/Brighton/Hove</courtsitename>\r\n" + COURTROOMSTAG + COURTROOMTAG
        + CURRENTSTATUSENDTAG + "            <courtroomname>Court 1</courtroomname>\r\n" + COURTROOMENDTAG
        + COURTROOMTAG + CURRENTSTATUSENDTAG + COURTROOMNAMETAG + COURTROOMENDTAG + COURTROOMTAG + CURRENTSTATUSENDTAG
        + "            <courtroomname>Court 3</courtroomname>\r\n" + COURTROOMENDTAG + COURTROOMSENDTAG
        + COURTSITEENDTAG + "    </courtsites>\r\n" + "  </court>\r\n" + "  <datetimestamp>\r\n"
        + "    <dayofweek>Wednesday</dayofweek>\r\n" + "    <date>06</date>\r\n" + "    <month>August</month>\r\n"
        + "    <year>2020</year>\r\n" + "    <hour>10</hour>\r\n" + "    <min>10</min>\r\n" + "  </datetimestamp>\r\n"
        + "  <pagename>snaresbrook</pagename>\r\n" + "</currentcourtstatus>\r\n";

    @Mock
    protected EntityManager mockEntityManager;

    @Mock
    protected CppStagingInboundControllerBean mockCppStagingInboundControllerBean;

    @Mock
    protected CppListControllerBean mockCppListControllerBean;

    @Mock
    protected XhbCppListRepository mockXhbCppListRepository;

    @Mock
    protected XhbCppFormattingRepository mockXhbCppFormattingRepository;

    @Mock
    protected XhbFormattingRepository mockXhbFormattingRepository;
    
    @Mock
    protected XhbCourtRepository mockXhbCourtRepository;

    @TestSubject
    protected final CppInitialProcessingControllerBean classUnderTest =
        new CppInitialProcessingControllerBean(mockEntityManager);

    @BeforeAll
    public static void setUp() throws Exception {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new CppInitialProcessingControllerBean();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    /**
     * Tests the getListStartDate method.
     */
    @Test
    void testGetListStartDate() throws ParserConfigurationException, IOException, SAXException {
        // Setup
        LocalDateTime startDate = LocalDateTime.of(2020, 01, 21, 0, 0);

        // Run method
        LocalDateTime ldt = classUnderTest.getListStartDate(DAILY_LIST_XML, DAILY_LIST_DOCTYPE);

        // Checks
        assertEquals(startDate, ldt, EQUALS);
    }

    /**
     * Tests the getListEndDate method.
     */
    @Test
    void testGetListEndDate() throws ParserConfigurationException, IOException, SAXException {
        // Setup
        LocalDateTime endDate = LocalDateTime.of(2020, 01, 21, 0, 0);

        // Run method
        LocalDateTime ldt = classUnderTest.getListEndDate(DAILY_LIST_XML, DAILY_LIST_DOCTYPE);

        // Checks
        assertEquals(endDate, ldt, EQUALS);
    }

    /**
     * Tests the getCourtHouseCode method.
     */
    @Test
    void testGetCourtHouseCode() throws ParserConfigurationException, IOException, SAXException {
        // Setup
        String courtHouseCode = "453";

        // Run method
        String chc = classUnderTest.getCourtHouseCode(DAILY_LIST_XML, DAILY_LIST_DOCTYPE);

        // Checks
        assertEquals(courtHouseCode, chc, EQUALS);
    }

    @Test
    void testCppInitialProcessingControllerException() {
        Assertions.assertThrows(CppInitialProcessingControllerException.class, () -> {
            throw new CppInitialProcessingControllerException();
        });
        Assertions.assertThrows(CppInitialProcessingControllerException.class, () -> {
            throw new CppInitialProcessingControllerException("", "", new CsBusinessException());
        });
    }

    /**
     * Replay the mocked objects.
     */
    protected void replayMocks() {
        EasyMock.replay(mockCppStagingInboundControllerBean);
        EasyMock.replay(mockCppListControllerBean);
        EasyMock.replay(mockXhbCppListRepository);
        EasyMock.replay(mockXhbCppFormattingRepository);
        EasyMock.replay(mockXhbFormattingRepository);
        EasyMock.replay(mockXhbCourtRepository);
    }

    /**
     * Verify the mocked objects.
     */
    protected void verifyMocks() {
        EasyMock.verify(mockCppStagingInboundControllerBean);
        EasyMock.verify(mockCppListControllerBean);
        EasyMock.verify(mockXhbCppListRepository);
        EasyMock.verify(mockXhbCppFormattingRepository);
        EasyMock.verify(mockXhbFormattingRepository);
    }

}
