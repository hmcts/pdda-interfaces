package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.ContentHandler;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.pdda.CourtelHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: Formatting Services Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
class FormattingServicesTest {

    private static final String TRUE = "Result is not True";
    private static final String COURTSITE_END_TAG = "      </courtsite>\r\n";
    private static final String COURTROOMS_END_TAG = "        </courtrooms>\r\n";
    private static final String CURRENTSTATUS_TAG = "            <currentstatus/>\r\n";
    private static final String COURTROOM_END_TAG = "          </courtroom>\r\n";
    private static final String COURTROOMNAME_COURT2 = "            <courtroomname>Court 2</courtroomname>\r\n";
    private static final String COURTROOMS_TAG = "        <courtrooms>\r\n";
    private static final String COURTROOM_TAG = "          <courtroom>\r\n";
    private static final String COURTSITE_TAG = "      <courtsite>\r\n";
    private static final String CS_CHARGE_TAG = "                                        </cs:Charge>";

    public static final String CPP_LIST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\" xmlns:"
        + "apd=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns=\"http://"
        + "www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns:p2=\"http://www.govtalk.gov.uk/people/"
        + "bs7666\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://"
        + "www.courtservice.gov.uk/schemas/courtservice DailyList-v5-9.xsd\">" + "    <cs:DocumentID>"
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
        + "                                            <apd:CitizenNameRequestedName>Sri</apd:CitizenNameRequestedName>"
        + "                                        </cs:Name>"
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

    private static final String INTERNET_WEBPAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
        + "<?xml-stylesheet type=\"text/xsl\" href=\"InternetWebPageTemplate.xsl\"?>\r\n"
        + "<currentcourtstatus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + "  <court>\r\n"
        + "    <courtname>SNARESBROOK</courtname>\r\n" + "    <courtsites>\r\n" + COURTSITE_TAG
        + "        <courtsitename>SNARESBROOK mu</courtsitename>\r\n" + COURTROOMS_TAG + COURTROOM_TAG
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
        + "            <timestatusset>10:10</timestatusset>\r\n" + COURTROOMNAME_COURT2 + COURTROOM_END_TAG
        + COURTROOM_TAG + CURRENTSTATUS_TAG + COURTROOMNAME_COURT2 + COURTROOM_END_TAG + COURTROOMS_END_TAG
        + COURTSITE_END_TAG + COURTSITE_TAG + "        <courtsitename>PRESTON3</courtsitename>\r\n" + COURTROOMS_TAG
        + COURTROOM_TAG + CURRENTSTATUS_TAG + "            <courtroomname>Court 1</courtroomname>\r\n"
        + COURTROOM_END_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG + COURTROOMNAME_COURT2 + COURTROOM_END_TAG
        + COURTROOM_TAG + CURRENTSTATUS_TAG + "            <courtroomname>Court 3</courtroomname>\r\n"
        + COURTROOM_END_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG
        + "            <courtroomname>Court 5</courtroomname>\r\n" + COURTROOM_END_TAG + COURTROOM_TAG
        + CURRENTSTATUS_TAG + "            <courtroomname>Court 10</courtroomname>\r\n" + COURTROOM_END_TAG
        + COURTROOM_TAG + CURRENTSTATUS_TAG + "            <courtroomname>Court 11</courtroomname>\r\n"
        + COURTROOM_END_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG
        + "            <courtroomname>Court 12</courtroomname>\r\n" + COURTROOM_END_TAG + COURTROOMS_END_TAG
        + COURTSITE_END_TAG + COURTSITE_TAG + "        <courtsitename>VILNIUS</courtsitename>\r\n" + COURTROOMS_TAG
        + COURTROOM_TAG + CURRENTSTATUS_TAG + "            <courtroomname>Court 9</courtroomname>\r\n"
        + COURTROOM_END_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG
        + "            <courtroomname>Court 10</courtroomname>\r\n" + COURTROOM_END_TAG + COURTROOM_TAG
        + CURRENTSTATUS_TAG + "            <courtroomname>Court 11</courtroomname>\r\n" + COURTROOM_END_TAG
        + COURTROOMS_END_TAG + COURTSITE_END_TAG + COURTSITE_TAG
        + "        <courtsitename>VILLAGE HALL</courtsitename>\r\n" + COURTROOMS_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG
        + "            <courtroomname>Court 5</courtroomname>\r\n" + COURTROOM_END_TAG + COURTROOMS_END_TAG
        + COURTSITE_END_TAG + COURTSITE_TAG + "        <courtsitename>Lewes/Brighton/Hove</courtsitename>\r\n"
        + COURTROOMS_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG + "            <courtroomname>Court 1</courtroomname>\r\n"
        + COURTROOM_END_TAG + COURTROOM_TAG + CURRENTSTATUS_TAG + COURTROOMNAME_COURT2 + COURTROOM_END_TAG
        + COURTROOM_TAG + CURRENTSTATUS_TAG + "            <courtroomname>Court 3</courtroomname>\r\n"
        + COURTROOM_END_TAG + COURTROOMS_END_TAG + COURTSITE_END_TAG + "    </courtsites>\r\n" + "  </court>\r\n"
        + "  <datetimestamp>\r\n" + "    <dayofweek>Wednesday</dayofweek>\r\n" + "    <date>06</date>\r\n"
        + "    <month>August</month>\r\n" + "    <year>2020</year>\r\n" + "    <hour>10</hour>\r\n"
        + "    <min>10</min>\r\n" + "  </datetimestamp>\r\n" + "  <pagename>snaresbrook</pagename>\r\n"
        + "</currentcourtstatus>\r\n";

    private static final String DOCTYPE_DAILY_LIST = "DL";
    private static final String DOCTYPE_FIRM_LIST = "FL";
    private static final String DOCTYPE_WARN_LIST = "WL";
    private static final String DOCTYPE_DAILY_LIST_LETTER = "DLS";
    private static final String DOCTYPE_INTERNET_WEBPAGE = "IWP";
    private static final String TRANSLATION_BUNDLE_XML = "";
    private static final String HTML = "HTM";
    private static final String XML = "XML";

    private static final String MERGE_CUT_OFF_TIME = "MERGE_CUT_OFF_TIME";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private TranslationBundles mockTranslationBundles;

    @Mock
    private FormattingConfig mockFormattingConfig;

    @Mock
    private XslServices mockXslServices;

    @Mock
    private XmlServices mockXmlServices;

    @Mock
    private TransformerFactory mockTransformerFactory;

    @Mock
    private Transformer mockTransformer;

    @Mock
    private ContentHandler mockContentHandler;

    @Mock
    private XhbBlobRepository mockXhbBlobRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private XhbCppListRepository mockXhbCppListRepository;

    @Mock
    private XhbCppFormattingRepository mockXhbCppFormattingRepository;

    @Mock
    private XhbFormattingRepository mockXhbFormattingRepository;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCppFormattingMergeRepository mockXhbCppFormattingMergeRepository;

    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;

    @Mock
    private CourtelHelper mockCourtelHelper;

    @InjectMocks
    private FormattingServices classUnderTest;

    public static class PddaSwitcher {
        static final String PDDA_SWITCH = "PDDA_SWITCHER";
        static final String PDDA_ONLY = "1";
        static final String PDDA_AND_XHIBIT = "2";
        static final String XHIBIT_ONLY = "3";
    }

    @BeforeEach
    public void setUp() throws Exception {
        Mockito.mockStatic(CsServices.class);
        Mockito.mockStatic(TransformerFactory.class);
        classUnderTest = new FormattingServices(mockEntityManager, mockCourtelHelper);
        ReflectionTestUtils.setField(classUnderTest, "xhbConfigPropRepository", mockXhbConfigPropRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppListRepository", mockXhbCppListRepository);
        ReflectionTestUtils.setField(classUnderTest, "translationBundles", mockTranslationBundles);
        ReflectionTestUtils.setField(classUnderTest, "xslServices", mockXslServices);
        ReflectionTestUtils.setField(classUnderTest, "formattingConfig", mockFormattingConfig);
        ReflectionTestUtils.setField(classUnderTest, "xhbBlobRepository", mockXhbBlobRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbClobRepository", mockXhbClobRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppListRepository", mockXhbCppListRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppFormattingRepository", mockXhbCppFormattingRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbFormattingRepository", mockXhbFormattingRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppFormattingMergeRepository",
            mockXhbCppFormattingMergeRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbXmlDocumentRepository", mockXhbXmlDocumentRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        Mockito.clearAllCaches();
    }

    @Test
    void testProcessDocumentDailyListLetter() throws IOException, TransformerConfigurationException {
        // Setup
        String xml = CPP_LIST.replace(DOCTYPE_DAILY_LIST, DOCTYPE_DAILY_LIST_LETTER);
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), xml);
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue(xhbClobDao.getClobData(),
            DOCTYPE_DAILY_LIST_LETTER, HTML, xhbCppListDao);
        formattingValue.setXmlDocumentClobId(xhbClobDao.getPrimaryKey());
        expectCreateSource(formattingValue.getDocumentType());
        expectTransformer();
        Mockito.when(mockCourtelHelper.isCourtelSendableDocument(Mockito.isA(String.class))).thenReturn(true);
        mockCourtelHelper.writeToCourtel(Mockito.isA(Long.class));

        // Run
        boolean result =
            testProcessDocuments(FormattingServices.getXmlUtils(DOCTYPE_DAILY_LIST_LETTER), formattingValue);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessDocumentFirmList() {
        // Setup
        String xml = CPP_LIST.replace(DOCTYPE_DAILY_LIST, DOCTYPE_FIRM_LIST);
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), xml);
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        FormattingValue formattingValue =
            DummyFormattingUtil.getFormattingValue(xhbClobDao.getClobData(), DOCTYPE_FIRM_LIST, XML, xhbCppListDao);
        formattingValue.setXmlDocumentClobId(xhbClobDao.getPrimaryKey());
        Mockito.when(mockCourtelHelper.isCourtelSendableDocument(Mockito.isA(String.class))).thenReturn(true);
        mockCourtelHelper.writeToCourtel(Mockito.isA(Long.class));

        // Run
        boolean result = testProcessDocuments(FormattingServices.getXmlUtils(DOCTYPE_FIRM_LIST), formattingValue);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessDocumentWarnList() {
        // Setup
        String xml = CPP_LIST.replace(DOCTYPE_DAILY_LIST, DOCTYPE_WARN_LIST);
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), xml);
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        FormattingValue formattingValue =
            DummyFormattingUtil.getFormattingValue(xhbClobDao.getClobData(), DOCTYPE_WARN_LIST, XML, xhbCppListDao);
        formattingValue.setXmlDocumentClobId(xhbClobDao.getPrimaryKey());
        Mockito.when(mockCourtelHelper.isCourtelSendableDocument(Mockito.isA(String.class))).thenReturn(true);
        mockCourtelHelper.writeToCourtel(Mockito.isA(Long.class));
        // Run
        boolean result = testProcessDocuments(FormattingServices.getXmlUtils(DOCTYPE_WARN_LIST), formattingValue);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessDocument() throws IOException, TransformerConfigurationException {
        // Setup
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), CPP_LIST);
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        FormattingValue formattingValue =
            DummyFormattingUtil.getFormattingValue(xhbClobDao.getClobData(), DOCTYPE_DAILY_LIST, XML, xhbCppListDao);
        formattingValue.setXmlDocumentClobId(xhbClobDao.getPrimaryKey());
        List<XhbCppListDao> existingList = new ArrayList<>();
        existingList.add(xhbCppListDao);
        Mockito.when(mockXhbCppListRepository.findByClobId(Mockito.isA(Long.class))).thenReturn(xhbCppListDao);
        Mockito.when(mockXhbCppListRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbCppListDao));
        Mockito.when(mockXhbCppListRepository.update(Mockito.isA(XhbCppListDao.class)))
            .thenReturn(Optional.of(existingList.get(0)));
        Mockito.when(mockXhbCppListRepository.update(Mockito.isA(XhbCppListDao.class)))
            .thenReturn(Optional.of(existingList.get(0)));
        expectTransformer();
        Mockito.when(mockCourtelHelper.isCourtelSendableDocument(Mockito.isA(String.class))).thenReturn(true);
        mockCourtelHelper.writeToCourtel(Mockito.isA(Long.class));
        // Run
        boolean result = testProcessDocuments(FormattingServices.getXmlUtils(DOCTYPE_DAILY_LIST), formattingValue);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessIwpDocument() throws IOException, TransformerConfigurationException {
        // Setup
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), INTERNET_WEBPAGE);
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        XhbFormattingDao xhbFormattingDao = DummyFormattingUtil.getXhbFormattingDao();
        xhbFormattingDao.setXmlDocumentClobId(xhbClobDao.getClobId());
        XhbCppFormattingDao xhbCppFormattingDao = DummyFormattingUtil.getXhbCppFormattingDao();
        xhbCppFormattingDao.setXmlDocumentClobId(xhbClobDao.getClobId());
        List<XhbFormattingDao> formattingDaoLatestClobList = new ArrayList<>();
        formattingDaoLatestClobList.add(xhbFormattingDao);
        List<XhbConfigPropDao> propertyList = new ArrayList<>();
        propertyList.add(DummyServicesUtil.getXhbConfigPropDao(MERGE_CUT_OFF_TIME, "23:59:59"));
        Mockito.when(mockXhbConfigPropRepository.findByPropertyName(MERGE_CUT_OFF_TIME)).thenReturn(propertyList);
        Mockito.when(mockXhbCppFormattingRepository.findLatestByCourtDateInDoc(Mockito.isA(Integer.class),
            Mockito.isA(String.class), Mockito.isA(LocalDateTime.class))).thenReturn(xhbCppFormattingDao);
        Mockito.when(mockXhbClobRepository.findById(Mockito.isA(Long.class))).thenReturn(Optional.of(xhbClobDao));
        Mockito.when(mockXhbClobRepository.update(Mockito.isA(XhbClobDao.class))).thenReturn(Optional.of(xhbClobDao));
        Mockito.when(mockXhbFormattingRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbFormattingDao));
        Mockito.when(mockXhbCppFormattingRepository.findById(Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(xhbCppFormattingDao));
        Mockito.when(mockXhbFormattingRepository.update(Mockito.isA(XhbFormattingDao.class)))
            .thenReturn(Optional.of(xhbFormattingDao));
        Mockito.when(mockXhbCppFormattingRepository.update(Mockito.isA(XhbCppFormattingDao.class)))
            .thenReturn(Optional.of(xhbCppFormattingDao));
        mockXhbCppFormattingMergeRepository.save(Mockito.isA(XhbCppFormattingMergeDao.class));
        Mockito
            .when(mockXhbFormattingRepository.findByDocumentAndClob(Mockito.isA(Integer.class),
                Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(String.class)))
            .thenReturn(formattingDaoLatestClobList);
        Mockito.when(mockXhbClobRepository.findById(Mockito.isA(Long.class))).thenReturn(Optional.of(xhbClobDao));
        expectTransformer();
        Mockito.when(mockCourtelHelper.isCourtelSendableDocument(Mockito.isA(String.class))).thenReturn(false);

        // Run
        boolean result =
            testProcessDocuments(FormattingServices.getXmlUtils(DOCTYPE_INTERNET_WEBPAGE), DummyFormattingUtil
                .getFormattingValue(xhbClobDao.getClobData(), DOCTYPE_INTERNET_WEBPAGE, XML, xhbCppListDao));
        assertTrue(result, TRUE);
    }

    private void expectCreateSource(String documentType) {
        if (!DOCTYPE_FIRM_LIST.equals(documentType) && !DOCTYPE_WARN_LIST.equals(documentType)) {
            String[] xsltNames = {};
            Mockito.when(mockFormattingConfig.getXslTransforms(Mockito.isA(FormattingValue.class)))
                .thenReturn(xsltNames);
            Templates[] templates = {};
            Mockito.when(mockXslServices.getTemplatesArray(Mockito.isA(String[].class), Mockito.isA(Locale.class),
                Mockito.isA(Map.class))).thenReturn(templates);
        }
    }

    private void expectTransformer() throws IOException, TransformerConfigurationException {
        Mockito.when(CsServices.getXslServices()).thenReturn(mockXslServices);
        Mockito.when(CsServices.getXmlServices()).thenReturn(mockXmlServices);
        Mockito.when(TransformerFactory.newInstance()).thenReturn(mockTransformerFactory);
        Mockito.when(mockTransformerFactory.newTransformer()).thenReturn(mockTransformer);
        Mockito.when(mockXslServices.getTransformer(Mockito.isA(Locale.class), Mockito.isA(Map.class)))
            .thenReturn(mockTransformer);
        Mockito.when(mockTransformer.getOutputProperties()).thenReturn(new Properties());
        Mockito.when(mockXmlServices.createXmlSerializer(Mockito.isA(OutputStream.class)))
            .thenReturn(mockContentHandler);
        Mockito.when(mockXmlServices.createXmlSerializer(Mockito.isA(StringWriter.class)))
            .thenReturn(mockContentHandler);
    }

    private boolean testProcessDocuments(AbstractXmlMergeUtils expectedXmlUtils, FormattingValue formattingValue) {
        // Setup
        List<XhbConfigPropDao> propertyList = new ArrayList<>();
        propertyList.add(DummyServicesUtil.getXhbConfigPropDao(PddaSwitcher.PDDA_SWITCH, PddaSwitcher.PDDA_ONLY));
        Mockito.when(mockXhbConfigPropRepository.findByPropertyName(PddaSwitcher.PDDA_SWITCH)).thenReturn(propertyList);
        Mockito.when(mockTranslationBundles.toXml()).thenReturn(TRANSLATION_BUNDLE_XML);
        if (expectedXmlUtils != null) {
            expectCreateSource(formattingValue.getDocumentType());
        }
        // Run
        classUnderTest.processDocument(formattingValue, mockEntityManager);
        // Checks
        if (expectedXmlUtils != null) {
            assertNotNull(classUnderTest.getXmlUtils(), "Result is Null");
            assertSame(classUnderTest.getXmlUtils().getClass(), expectedXmlUtils.getClass(), "Result is not Same");
        } else {
            assertNull(classUnderTest.getXmlUtils(), "Result is not Null");
        }
        return true;
    }
}
