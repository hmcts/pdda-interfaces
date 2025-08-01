package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.DailyListCppToPublicDisplay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: PDSetupControllerBean Test.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class DailyListCppToPublicDisplayTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 94;
    private static final String COURT_NAME = "Test Court";
    private static final Date LIST_DATE = new Date();
    private static final int[] ROOM_ARRAY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final String CS_CHARGE_TAG = "                                        </cs:Charge>";

    private static final String DAILY_LIST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\" xmlns"
        + ":apd=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns=\"http://www."
        + "govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns:p2=\"http://www.govtalk.gov.uk/people/"
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

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private XhbCppFormattingRepository mockXhbCppFormattingRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private CppFormattingHelper mockCppFormattingHelper;

    @TestSubject
    private final DailyListCppToPublicDisplay classUnderTest =
        new DailyListCppToPublicDisplay(LIST_DATE, COURT_ID, ROOM_ARRAY, mockXhbCourtRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbClobRepository, mockCppFormattingHelper);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new DailyListCppToPublicDisplay(LIST_DATE, COURT_ID, ROOM_ARRAY);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetCppData() {
        // Setup
        List<XhbCppFormattingDao> xcfList = new ArrayList<>();
        XhbCppFormattingDao xhbCppFormattingDao = DummyFormattingUtil.getXhbCppFormattingDao();
        xcfList.add(xhbCppFormattingDao);
        List<XhbCourtRoomDao> courtRoomDaos = new ArrayList<>();
        courtRoomDaos.add(DummyCourtUtil.getXhbCourtRoomDao());
        List<XhbCourtSiteDao> courtSiteDaos = new ArrayList<>();
        courtSiteDaos.add(DummyCourtUtil.getXhbCourtSiteDao());

        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, COURT_NAME)));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(courtSiteDaos);
        EasyMock
            .expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(courtRoomDaos);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockXhbCppFormattingRepository.getLatestDocumentByCourtIdAndType(EasyMock.isA(Integer.class),
            EasyMock.isA(String.class), EasyMock.isA(LocalDateTime.class))).andReturn(xcfList.get(0));

        EasyMock.expect(mockXhbClobRepository.findByIdSafe(EasyMock.isA(Long.class)))
            .andReturn(Optional.of(DummyFormattingUtil.getXhbClobDao(xcfList.get(0).getXmlDocumentClobId(),
                AllCourtStatusCppToPublicDisplayTest.CPP_XML)));

        EasyMock.expect(mockCppFormattingHelper.getLatestPublicDisplayDocument(EasyMock.isA(Integer.class),
            EasyMock.isA(EntityManager.class))).andReturn(xhbCppFormattingDao);

        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);
        EasyMock.replay(mockXhbCourtRoomRepository);
        EasyMock.replay(mockXhbCppFormattingRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockCppFormattingHelper);

        /**
         * Run Method Note that due to running this with PowerMockRunner, the call to
         * AbstractXMLUtils.getDocBuilder() and subsequent call of DocumentBuilder.parse caused issues. The
         * only workaround was to use PowerMockIgnore which has a side effect of not being able to create a
         * valid XML Document that we can use or Mock.
         */
        classUnderTest.getCppData(mockEntityManager);

        // Checks
        assertArrayEquals(ROOM_ARRAY, classUnderTest.getCourtRoomIds(), EQUALS);
        assertEquals(COURT_NAME, classUnderTest.getCourtName(), EQUALS);
        assertEquals(COURT_ID, classUnderTest.getCourtId(), EQUALS);
        assertEquals(LIST_DATE, classUnderTest.getDate(), EQUALS);
    }
}
