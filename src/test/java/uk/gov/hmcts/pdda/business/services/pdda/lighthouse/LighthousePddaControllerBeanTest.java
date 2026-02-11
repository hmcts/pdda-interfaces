package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
class LighthousePddaControllerBeanTest {

    private static final String NOTNULL = "Result is not Null";
    private static final String NULL = "Result is Null";
    private static final String TRUE = "Result is True";
    private static final String SAME = "Result is Same";
    private static final String FALSE = "Result is False";
    private static final String NOT_INSTANCE = "Result is Not An Instance of";
    private static final String MESSAGE_STATUS_PROCESSED = "VP";
    private static final String MESSAGE_STATUS_INVALID = "INV";
    private static final String UNDERSCORE = "_";
    private static final Integer PART_NO = 3;

    private static final String DAILY_LIST_EXAMPLE = "DailyList_453_20220811235559.xml";
    private static final String INVALIDNAME_EXAMPLE = "NotAFile_100_20220802030423.xml";
    private static final String INVALIDPARTS_EXAMPLE = "DailyList_453_2_20220811235559.xml";


    @Mock
    private XhbPddaMessageRepository mockXhbPddaMessageRepository;

    @Mock
    private XhbCppStagingInboundRepository mockXhbCppStagingInboundRepository;
    
    @Mock
    private XhbInternetHtmlRepository mockXhbInternetHtmlRepository;
    
    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;
    
    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private LighthousePddaControllerBean classUnderTestMock;
    
    @Captor
    private ArgumentCaptor<XhbPddaMessageDao> xhbPddaMessageDaoCapture;

    @InjectMocks
    private final LighthousePddaControllerBean classUnderTest =
        new LighthousePddaControllerBean(mockEntityManager);

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(EntityManagerUtil.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }
    
    @Test
    void testProcessFilesSuccess() {
        boolean result = testProcessFiles(MESSAGE_STATUS_PROCESSED);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessFilesFailure() {
        boolean result = testProcessFiles(MESSAGE_STATUS_INVALID);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetXhbPddaMessageRepository() {
        assertInstanceOf(XhbPddaMessageRepository.class,
            classUnderTest.getXhbPddaMessageRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppStagingInboundRepository() {
        assertInstanceOf(XhbCppStagingInboundRepository.class,
            classUnderTest.getXhbCppStagingInboundRepository(), NOT_INSTANCE);
    }

    @Test
    void testConstructors() {
        assertInstanceOf(LighthousePddaControllerBean.class, new LighthousePddaControllerBean(),
            NOT_INSTANCE);
        assertInstanceOf(LighthousePddaControllerBean.class,
            new LighthousePddaControllerBean(mockXhbPddaMessageRepository,
                mockXhbCppStagingInboundRepository,
                mockXhbInternetHtmlRepository, mockEntityManager),
            NOT_INSTANCE);
        assertInstanceOf(LighthousePddaControllerBean.class,
            new LighthousePddaControllerBean(mockEntityManager), NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppStagingInboundRepositoryNotNull() {
        mockXhbCppStagingInboundRepository = new XhbCppStagingInboundRepository(mockEntityManager);
        assertInstanceOf(XhbCppStagingInboundRepository.class,
            classUnderTest.getXhbCppStagingInboundRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetDocumentNameToProcess() {

        String documentName1 = DAILY_LIST_EXAMPLE;
        String result = classUnderTest.getDocumentNameToProcess(documentName1);
        assertEquals(DAILY_LIST_EXAMPLE, result, SAME);

        String documentName2 = "PDDA_34_1_453_2024101409000 list_filename = " + DAILY_LIST_EXAMPLE;
        result = classUnderTest.getDocumentNameToProcess(documentName2);
        assertEquals(DAILY_LIST_EXAMPLE, result, SAME);

        String documentName3 = "PDDA_34_1_453_2024101409000 list_filename= " + DAILY_LIST_EXAMPLE;
        result = classUnderTest.getDocumentNameToProcess(documentName3);
        assertEquals("", result, SAME);

        String documentName4 = "PDDA_34_1_453_2024101409000 list_filenam = " + DAILY_LIST_EXAMPLE;
        result = classUnderTest.getDocumentNameToProcess(documentName4);
        assertEquals("", result, SAME);

        String documentName5 =
            "PDDA_34_1_453_2024101409000 list_filename = FirmList_453_20220811235559.xml";
        result = classUnderTest.getDocumentNameToProcess(documentName5);
        assertEquals("FirmList_453_20220811235559.xml", result, SAME);

        String documentName6 =
            "PDDA_34_1_453_2024101409000 list_filename = WarnedList_453_20220811235559.xml";
        result = classUnderTest.getDocumentNameToProcess(documentName6);
        assertEquals("WarnedList_453_20220811235559.xml", result, SAME);

        String documentName7 = "PDDA_34_1_453_2024101409000";
        result = classUnderTest.getDocumentNameToProcess(documentName7);
        assertEquals("", result, SAME);

        String documentName8 = "PDDA_34_1_453_2024101409000.xml";
        result = classUnderTest.getDocumentNameToProcess(documentName8);
        assertEquals("", result, SAME);

        String documentName9 =
            "PDDA_CPD_34_1_453_2024101409000 pd_filename = PublicDisplay_453_20220811235559.xml";
        result = classUnderTest.getDocumentNameToProcess(documentName9);
        assertEquals("PublicDisplay_453_20220811235559.xml", result, SAME);

        String documentName10 =
            "PDDA_XDL_34_1_453_2024101409000 list_filename = DailyList_453_20220811235559.xml";
        result = classUnderTest.getDocumentNameToProcess(documentName10);
        assertEquals("DailyList_453_20220811235559.xml", result, SAME);

        String documentName11 = "PDDA_XPD_34_1_453_2024101409000";
        result = classUnderTest.getDocumentNameToProcess(documentName11);
        assertEquals("", result, SAME);
    }

    private boolean testProcessFiles(String expectedSavedStatus) {
        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = getDummyXhbPddaMessageDaoList();
        mockTheEntityManager();
        Mockito.when(mockXhbPddaMessageRepository.findByLighthouseSafe())
            .thenReturn(xhbPddaMessageDaoList);
        for (XhbPddaMessageDao xhbPddaMessageDao : xhbPddaMessageDaoList) {
            String[] fileParts = xhbPddaMessageDao.getCpDocumentName().split(UNDERSCORE);
            if (fileParts.length == PART_NO
                && !INVALIDNAME_EXAMPLE.equals(xhbPddaMessageDao.getCpDocumentName())
                && !INVALIDPARTS_EXAMPLE.equals(xhbPddaMessageDao.getCpDocumentName())) {
                processMessage(xhbPddaMessageDao, expectedSavedStatus);
            } else {
                processMessage(xhbPddaMessageDao, expectedSavedStatus);
                continue;
            }
        }
        
        // Mock the calls to the On Hold code to return empty for this test block
        Mockito.when(mockXhbPddaMessageRepository.findByLighthouseOnHoldSafe())
            .thenReturn(new ArrayList<>());
        List<XhbConfigPropDao> xhbConfigPropDaoList = new ArrayList<>();
        XhbConfigPropDao xhbConfigPropDao = new XhbConfigPropDao();
        xhbConfigPropDao.setPropertyValue("10");
        xhbConfigPropDaoList.add(xhbConfigPropDao);
        Mockito.when(mockXhbConfigPropRepository.findByPropertyNameSafe(Mockito.isA(String.class)))
            .thenReturn(xhbConfigPropDaoList);
        Mockito.when(mockXhbPddaMessageRepository
            .findListsExceedingOnHoldTimeframeSafe(Mockito.isA(LocalDateTime.class)))
            .thenReturn(new ArrayList<>());
        
        // Run
        classUnderTest.doTask();
        // Checks
        assertNotNull(xhbPddaMessageDaoCapture, NOTNULL);
        assertSame(expectedSavedStatus, xhbPddaMessageDaoCapture.getValue().getCpDocumentStatus(),
            SAME);

        return true;
    }

    private void processMessage(XhbPddaMessageDao xhbPddaMessageDao, String expectedSavedStatus) {
        Mockito.when(mockXhbPddaMessageRepository.findByIdSafe(xhbPddaMessageDao.getPrimaryKey()))
            .thenReturn(Optional.of(xhbPddaMessageDao));

        Optional<XhbPddaMessageDao> xhbPddaMessageDao1 =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao());

        Mockito.when(mockXhbCppStagingInboundRepository
            .findDocumentByDocumentNameSafe(Mockito.isA(String.class)))
            .thenReturn(new ArrayList<>());
        
        Mockito
            .when(mockXhbPddaMessageRepository.update(xhbPddaMessageDaoCapture.capture()))
            .thenReturn(xhbPddaMessageDao1);

        classUnderTestMock.updatePddaMessageStatus(Mockito.isA(XhbPddaMessageDao.class),
            Mockito.isA(String.class), Mockito.isA(String.class));

        if (MESSAGE_STATUS_INVALID.equals(expectedSavedStatus)) {
            // Failure
            Mockito
                .when(mockXhbCppStagingInboundRepository
                    .update(Mockito.isA(XhbCppStagingInboundDao.class)))
                .thenThrow(getRuntimeException());
        } else {
            // Success
            XhbCppStagingInboundDao stagingInboundDao =
                DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            stagingInboundDao.setDocumentName(xhbPddaMessageDao.getCpDocumentName());
            Mockito
                .when(mockXhbCppStagingInboundRepository
                    .update(Mockito.isA(XhbCppStagingInboundDao.class)))
                .thenReturn(Optional.of(stagingInboundDao));
        }
    }
    
    @Test
    void testIsDocumentNameValid() {
        assertTrue(classUnderTest.isDocumentNameValid(DAILY_LIST_EXAMPLE), TRUE);
        assertFalse(classUnderTest.isDocumentNameValid("Invalid_File.csv"), FALSE);
        assertFalse(classUnderTest.isDocumentNameValid("NotAFile_100_20220802030423.xml"), FALSE);
    }

    @Test
    void testGetDocType() {
        String result = classUnderTest.getDocType("NOTVALID");
        assertNull(result, NULL);
    }

    @Test
    void testIsGetDocumentNameValid() {
        String documentName1 = "DailyList_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName1), TRUE);

        String documentName2 = "FirmList_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName2), TRUE);

        String documentName3 = "WarnedList_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName3), TRUE);

        String documentName4 = "PublicDisplay_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName4), TRUE);

        String documentName5 = "WebPage_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName5), TRUE);

        String documentName6 =
            "PDDA_CPD_34_1_453_20220811235559 pd_filename = PublicDisplay_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName6), TRUE);

        String documentName7 =
            "PDDA_XDL_34_1_453_20220811235559 list_filename = DailyList_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName7), TRUE);

        String documentName8 = "PDDA_XPD_34_1_453_20220811235559";
        assertTrue(classUnderTest.isDocumentNameValid(documentName8), TRUE);

        String documentName9 = "PDDA_CPD_34_1_453_202208112355";
        assertFalse(classUnderTest.isDocumentNameValid(documentName9), FALSE);

        String documentName10 =
            "PDDA_XDL_34_1_453_20220811235559 list_filename = DailyList_453_20220811235559.xml";
        assertTrue(classUnderTest.isDocumentNameValid(documentName10), TRUE);

        String documentName11 = "PDDA_XDL_34_1_453_20220811235559";
        assertFalse(classUnderTest.isDocumentNameValid(documentName11), FALSE);
    }
    
    @Test
    void testProcessFileAlreadyProcessed() {
        // Setup
        LighthousePddaControllerBean controller = Mockito.spy(new LighthousePddaControllerBean());
        Mockito.doReturn(mockXhbCppStagingInboundRepository).when(controller).getXhbCppStagingInboundRepository();
        Mockito.doReturn(mockXhbPddaMessageRepository).when(controller).getXhbPddaMessageRepository();

        XhbPddaMessageDao mockXhbPddaMessageDao = Mockito.mock(XhbPddaMessageDao.class);
        Mockito.when(mockXhbPddaMessageDao.getCpDocumentName()).thenReturn("TestDoc.xml");
        Mockito.when(mockXhbPddaMessageDao.getCpDocumentStatus()).thenReturn("VN");
        
        List<XhbCppStagingInboundDao> mockXhbCppStagingInboundList = new ArrayList<>();
        XhbCppStagingInboundDao mockXhbCppStagingInboundDao = Mockito.mock(XhbCppStagingInboundDao.class);
        mockXhbCppStagingInboundList.add(mockXhbCppStagingInboundDao);
        
        // Simulate file already processed
        Mockito.when(mockXhbCppStagingInboundRepository.findDocumentByDocumentNameSafe("TestDoc.xml"))
            .thenReturn(mockXhbCppStagingInboundList);

        Mockito.when(mockXhbPddaMessageRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(mockXhbPddaMessageDao));
        
        // Spy updatePddaMessageStatus to verify it's called
        Mockito.doNothing().when(controller).updatePddaMessageStatus(Mockito.any(), Mockito.any(), Mockito.any());

        // Run
        controller.processFile(mockXhbPddaMessageDao);

        // Verify
        Mockito.verify(mockXhbCppStagingInboundRepository).findDocumentByDocumentNameSafe("TestDoc.xml");
        Mockito.verify(controller)
            .updatePddaMessageStatus(
                Mockito.eq(mockXhbPddaMessageDao),
                Mockito.eq("INV"),
                Mockito.anyString()
            );
        // Ensure no further processing occurs
        Mockito.verify(controller, Mockito.never()).isDocumentNameValid(Mockito.anyString());
    }
    
    
    @Test
    void testProcessFile_PublicDisplayEvent_Xpd_SetsProcessed() {
        // Spy controller so we can stub its repos
        LighthousePddaControllerBean controller = Mockito.spy(new LighthousePddaControllerBean());
        Mockito.doReturn(mockXhbCppStagingInboundRepository).when(controller).getXhbCppStagingInboundRepository();
        Mockito.doReturn(mockXhbPddaMessageRepository).when(controller).getXhbPddaMessageRepository();

        // Message DAO (status will be updated to VP by updatePddaMessageStatus)
        XhbPddaMessageDao dao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        dao.setCpDocumentName("PDDA_XPD_34_1_453_20241014090000"); // triggers XPD branch
        dao.setCpDocumentStatus("VN"); // current status valid-not-processed

        // No prior staging rows -> not a duplicate
        Mockito.when(mockXhbCppStagingInboundRepository.findDocumentByDocumentNameSafe(dao.getCpDocumentName()))
               .thenReturn(new ArrayList<>());

        // fetchLatest (called by updatePddaMessageStatus) returns our DAO
        Mockito.when(mockXhbPddaMessageRepository.findByIdSafe(Mockito.anyInt()))
               .thenReturn(Optional.of(dao));

        // Capture the DAO passed to update(...) so we can assert status becomes VP
        ArgumentCaptor<XhbPddaMessageDao> msgCaptor = ArgumentCaptor.forClass(XhbPddaMessageDao.class);
        Mockito.when(mockXhbPddaMessageRepository.update(msgCaptor.capture()))
            .thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        controller.processFile(dao);

        // Assert status set to PROCESSED and no staging attempted
        assertEquals("VP", msgCaptor.getValue().getCpDocumentStatus(), "XPD should set VP");
        Mockito.verify(mockXhbCppStagingInboundRepository, Mockito.never()).update(Mockito.any());
    }
    
    
    @Test
    void testProcessFile_WebPageEvent_Xwp_InsertsInternetHtmlAndSetsProcessed() {
        LighthousePddaControllerBean controller = Mockito.spy(new LighthousePddaControllerBean());
        Mockito.doReturn(mockXhbCppStagingInboundRepository).when(controller).getXhbCppStagingInboundRepository();
        Mockito.doReturn(mockXhbPddaMessageRepository).when(controller).getXhbPddaMessageRepository();
        Mockito.doReturn(mockXhbInternetHtmlRepository).when(controller).getXhbInternetHtmlRepository();
        Mockito.doReturn(mockXhbXmlDocumentRepository).when(controller).getXhbXmlDocumentRepository();

        XhbPddaMessageDao dao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        dao.setCpDocumentName("PDDA_XWP_34_1_453_20241014090000"); // triggers XWP branch
        dao.setCourtId(987);
        dao.setPddaMessageDataId(999L);
        dao.setCpDocumentStatus("VN");

        // No duplicates
        Mockito.when(mockXhbCppStagingInboundRepository.findDocumentByDocumentNameSafe(dao.getCpDocumentName()))
               .thenReturn(new ArrayList<>());

        // fetchLatest for status update
        Mockito.when(mockXhbPddaMessageRepository.findByIdSafe(Mockito.anyInt()))
               .thenReturn(Optional.of(dao));

        // Capture message update -> ensure VP
        ArgumentCaptor<XhbPddaMessageDao> msgCaptor = ArgumentCaptor.forClass(XhbPddaMessageDao.class);
        Mockito.when(mockXhbPddaMessageRepository.update(msgCaptor.capture()))
               .thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        // Internet HTML insert should be invoked with status 'C', courtId and blobId
        ArgumentCaptor<uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlDao> htmlCaptor =
            ArgumentCaptor.forClass(uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlDao.class);

        // Simulate successful insert (returning an ID)
        uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlDao returned =
            new uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlDao();
        returned.setInternetHtmlId(1234);
        Mockito.when(mockXhbInternetHtmlRepository.update(htmlCaptor.capture()))
               .thenReturn(Optional.of(returned));
        
        // Save the xhb_xml_document record
        mockXhbXmlDocumentRepository.save(Mockito.isA(XhbXmlDocumentDao.class));
        
        controller.processFile(dao);

        // Check status on message
        assertEquals("VP", msgCaptor.getValue().getCpDocumentStatus(), "XWP should set VP");

        // Check the Internet HTML row that was created
        uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlDao created = htmlCaptor.getValue();
        assertEquals("C", created.getStatus(), "XWP insert should set status C");
        assertEquals(987, created.getCourtId(), "CourtId should be passed through");
        assertEquals(999L, created.getHtmlBlobId(), "BlobId should be passed through");
    }

    
    @Test
    void testProcessFile_UnknownPddaType_SetsInvalid() {
        LighthousePddaControllerBean controller = Mockito.spy(new LighthousePddaControllerBean());
        Mockito.doReturn(mockXhbCppStagingInboundRepository).when(controller).getXhbCppStagingInboundRepository();
        Mockito.doReturn(mockXhbPddaMessageRepository).when(controller).getXhbPddaMessageRepository();

        // PDDA_* but neither XPD nor XWP and no list/pd filename => falls into "unknown" else {}
        XhbPddaMessageDao dao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        dao.setCpDocumentName("PDDA_ABC_34_1_453_2024101409000");

        Mockito.when(mockXhbCppStagingInboundRepository.findDocumentByDocumentNameSafe(dao.getCpDocumentName()))
               .thenReturn(new ArrayList<>());

        Mockito.when(mockXhbPddaMessageRepository.findByIdSafe(Mockito.anyInt()))
               .thenReturn(Optional.of(dao));

        ArgumentCaptor<XhbPddaMessageDao> msgCaptor = ArgumentCaptor.forClass(XhbPddaMessageDao.class);
        Mockito.when(mockXhbPddaMessageRepository.update(msgCaptor.capture()))
               .thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        controller.processFile(dao);

        // The last status written by the branch is INV (unknown doc type)
        assertEquals("INV", msgCaptor.getValue().getCpDocumentStatus(), "Unknown PDDA_* should set INV");
    }

    
    @Test
    void testFetchLatestThrowsWhenDaoMissing() {
        LighthousePddaControllerBean controller = Mockito.spy(new LighthousePddaControllerBean());
        Mockito.doReturn(mockXhbPddaMessageRepository).when(controller).getXhbPddaMessageRepository();

        XhbPddaMessageDao dao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        dao.setCpDocumentName("AnyDoc.xml");

        // Simulate not found -> Optional.empty() causes the else{} path to log + throw
        Mockito.when(mockXhbPddaMessageRepository.findByIdSafe(Mockito.anyInt()))
               .thenReturn(Optional.empty());

        IllegalStateException ex = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalStateException.class,
            () -> controller.updatePddaMessageStatus(dao, "IP", null),
            "Expected fetchLatest to throw when DAO not found"
        );
        assertTrue(ex.getMessage().contains("DAO not found in DB for ID:"),
            "Exception message includes helpful detail");
    }




    private List<XhbPddaMessageDao> getDummyXhbPddaMessageDaoList() {
        List<XhbPddaMessageDao> result = new ArrayList<>();
        String[] cpDocumentNames = {DAILY_LIST_EXAMPLE, "WarnedList_111_20220810010433.xml",
            "FirmList_101_20220807010423.xml", "PublicDisplay_100_20220802030423.xml",
            INVALIDNAME_EXAMPLE, INVALIDPARTS_EXAMPLE, "PDDA_303_1_453_20240411130023",
            "WebPage_122_20220804031423.xml"};
        for (String cpDocumentName : cpDocumentNames) {
            XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
            xhbPddaMessageDao.setCpDocumentName(cpDocumentName);
            result.add(xhbPddaMessageDao);
        }
        return result;
    }

    private RuntimeException getRuntimeException() {
        return new RuntimeException();
    }

    private void mockTheEntityManager() {
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        Mockito.when(EntityManagerUtil.isEntityManagerActive(mockEntityManager)).thenReturn(true);
    }

}
