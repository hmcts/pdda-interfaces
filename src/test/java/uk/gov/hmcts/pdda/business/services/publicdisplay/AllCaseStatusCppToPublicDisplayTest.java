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
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AllCaseStatusCppToPublicDisplay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(EasyMockExtension.class)
class AllCaseStatusCppToPublicDisplayTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final Integer COURT_ID = 94;
    private static final String COURT_NAME = "Test Court";
    private static final Date LIST_DATE = new Date();
    private static final int[] ROOM_ARRAY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

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
    private final AllCaseStatusCppToPublicDisplay classUnderTest =
        new AllCaseStatusCppToPublicDisplay(LIST_DATE, COURT_ID, ROOM_ARRAY, mockXhbCourtRepository,
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
            new AllCaseStatusCppToPublicDisplay(LIST_DATE, COURT_ID, ROOM_ARRAY);
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
        List<XhbCourtRoomDao> courtRoomDaos = new ArrayList<>();
        courtRoomDaos.add(DummyCourtUtil.getXhbCourtRoomDao());
        XhbClobDao clobDao = DummyFormattingUtil.getXhbClobDao(xcfList.get(0).getXmlDocumentClobId(),
            AllCourtStatusCppToPublicDisplayTest.CPP_XML);

        EasyMock.expect(mockXhbCourtRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, COURT_NAME)));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtId(EasyMock.isA(Integer.class))).andReturn(courtSiteDaos);
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteId(EasyMock.isA(Integer.class)))
            .andReturn(courtRoomDaos);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockXhbCppFormattingRepository.getLatestDocumentByCourtIdAndType(EasyMock.isA(Integer.class),
            EasyMock.isA(String.class), EasyMock.isA(LocalDateTime.class))).andReturn(xcfList.get(0));

        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class))).andReturn(Optional.of(clobDao));

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
        assertEquals(COURT_ID, Integer.valueOf(classUnderTest.getCourtId()), EQUALS);
        assertEquals(LIST_DATE, classUnderTest.getDate(), EQUALS);
    }
}
