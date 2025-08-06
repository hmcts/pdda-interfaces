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
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AllCourtStatusCppToPublicDisplay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class AllCourtStatusCppToPublicDisplayTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 94;
    private static final String COURT_NAME = "Test Court";
    private static final Date LIST_DATE = new Date();
    private static final int[] ROOM_ARRAY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public static final String CPP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><currentcourtstatus>"
            + "<court><courtname>Swansea Crown Court</courtname><courtsites><courtsite><courtsitename>"
            + "SWANSEA</courtsitename><courtrooms><courtroom><cases><caseDetails><cppurn>85GD6772220</cppurn>"
            + "<casenumber>1</casenumber><casetype>CROWN</casetype><activecase>1</activecase><hearingtype>First "
            + "Hearing</hearingtype><defendants><defendant><firstname>Kevin</firstname><middlename>Chase</middlename>"
            + "<lastname>Franecki</lastname></defendant><defendant><firstname>Kyla</firstname><middlename>Gayle"
            + "</middlename><lastname>Macejkovic</lastname></defendant></defendants><notbeforetime>15:00"
            + "</notbeforetime><timestatusset>15:00</timestatusset><hearingprogress>0</hearingprogress>"
            + "</caseDetails></cases><courtroomname>Court Room 2</courtroomname></courtroom><courtroom><cases>"
            + "<caseDetails><cppurn>92GD2803820</cppurn><casenumber>1</casenumber><casetype>CROWN</casetype>"
            + "<activecase>0</activecase><hearingtype>First Hearing</hearingtype><defendants><defendant>"
            + "<firstname>Mckenna</firstname><middlename>Janessa</middlename><lastname>Ratke</lastname>"
            + "</defendant><defendant><firstname>Bria</firstname><middlename>Cyril</middlename><lastname>"
            + "Kuphal</lastname></defendant></defendants><currentstatus><event><time>11:33</time><free_text>"
            + "</free_text><type>10100</type></event></currentstatus><timestatusset>11:33</timestatusset>"
            + "</caseDetails><caseDetails><cppurn>60GD3522120</cppurn><casenumber>1</casenumber><casetype>CROWN"
            + "</casetype><activecase>0</activecase><hearingtype>First Hearing</hearingtype><defendants>"
            + "<defendant><firstname>Edgar</firstname><lastname>Klein</lastname></defendant><defendant>"
            + "<firstname>Duane</firstname><middlename>Neil</middlename><lastname>Luettgen</lastname>"
            + "</defendant></defendants><notbeforetime>15:00</notbeforetime><timestatusset>15:00"
            + "</timestatusset><hearingprogress>0</hearingprogress></caseDetails></cases><courtroomname>"
            + "Court 3</courtroomname></courtroom><courtroom><cases><caseDetails><cppurn>45GD8919720"
            + "</cppurn><casenumber>1</casenumber><casetype>CROWN</casetype><activecase>0</activecase>"
            + "<hearingtype>First Hearing</hearingtype><defendants><defendant><firstname>Brannon</firstname>"
            + "<middlename>Jerrell</middlename><lastname>Skiles</lastname></defendant><defendant><firstname>"
            + "George</firstname><middlename>Oswald</middlename><lastname>Dicki</lastname></defendant>"
            + "</defendants><currentstatus><event><time>11:19</time><date>11/02/20</date><free_text></free_text>"
            + "<type>30500</type></event></currentstatus><timestatusset>11:19</timestatusset></caseDetails>"
            + "<caseDetails><cppurn>39GD7229120</cppurn><casenumber>1</casenumber><casetype>CROWN</casetype>"
            + "<activecase>0</activecase><hearingtype>First Hearing</hearingtype><defendants><defendant>"
            + "<firstname>Tracy</firstname><middlename>Sigrid</middlename><lastname>Mayer</lastname>"
            + "</defendant><defendant><firstname>Roel</firstname><middlename>Terrance</middlename>"
            + "<lastname>Collins</lastname></defendant></defendants><currentstatus><event><time>11:20"
            + "</time><date>11/02/20</date><free_text></free_text><type>10500</type></event></currentstatus>"
            + "<timestatusset>11:20</timestatusset></caseDetails></cases><courtroomname>Court 1</courtroomname>"
            + "</courtroom></courtrooms><floating/></courtsite></courtsites></court><datetimestamp><dayofweek>"
            + "Tuesday</dayofweek><date>11</date><month>February</month><year>2020</year><hour>12</hour><min>1"
            + "</min></datetimestamp><pagename>swansea crown court</pagename></currentcourtstatus>\r\n";

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
    private final AllCourtStatusCppToPublicDisplay classUnderTest =
        new AllCourtStatusCppToPublicDisplay(LIST_DATE, COURT_ID, ROOM_ARRAY, mockXhbCourtRepository,
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
            new AllCourtStatusCppToPublicDisplay(LIST_DATE, COURT_ID, ROOM_ARRAY);
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
        List<XhbCourtSiteDao> courtSiteDaos = new ArrayList<>();
        courtSiteDaos.add(DummyCourtUtil.getXhbCourtSiteDao());
        courtSiteDaos.add(DummyCourtUtil.getXhbCourtSiteDao());
        List<XhbCourtRoomDao> courtRoomDaos = new ArrayList<>();
        XhbCourtRoomDao room1 = DummyCourtUtil.getXhbCourtRoomDao();
        room1.setCourtRoomId(ROOM_ARRAY[0]);
        room1.setCourtRoomName("Court Room " + ROOM_ARRAY[0]);
        courtRoomDaos.add(room1);
        XhbCourtRoomDao room2 = DummyCourtUtil.getXhbCourtRoomDao();
        room2.setCourtRoomId(ROOM_ARRAY[1]);
        room2.setCourtRoomName("Court Room " + ROOM_ARRAY[1]);
        courtRoomDaos.add(room2);

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
            .andReturn(Optional.of(DummyFormattingUtil.getXhbClobDao(xcfList.get(0).getXmlDocumentClobId(), CPP_XML)));

        EasyMock.expect(mockCppFormattingHelper.getLatestPublicDisplayDocument(EasyMock.isA(Integer.class),
            EasyMock.isA(EntityManager.class))).andReturn(xhbCppFormattingDao);

        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);
        EasyMock.replay(mockXhbCourtRoomRepository);
        EasyMock.replay(mockXhbCppFormattingRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockCppFormattingHelper);

        classUnderTest.getCppData(mockEntityManager);

        // Checks
        assertArrayEquals(ROOM_ARRAY, classUnderTest.getCourtRoomIds(), EQUALS);
        assertEquals(COURT_NAME, classUnderTest.getCourtName(), EQUALS);
        assertEquals(COURT_ID, classUnderTest.getCourtId(), EQUALS);
        assertEquals(LIST_DATE, classUnderTest.getDate(), EQUALS);
    }
}
