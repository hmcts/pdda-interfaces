package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.persistence.EntityManager;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD.TooManyMethods")
class LighthousePddaControllerBeanTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is True";
    private static final String SAME = "Result is not Same";
    private static final String FALSE = "Result is False";
    private static final String NOT_INSTANCE = "Result is Not An Instance of";
    private static final String MESSAGE_STATUS_PROCESSED = "VP";
    private static final String MESSAGE_STATUS_INVALID = "INV";
    private static final String UNDERSCORE = "_";
    private static final Integer PART_NO = 3;

    private static final String DAILY_LIST_EXAMPLE = "DailyList_453_20220811235559.xml";


    @Mock
    private XhbPddaMessageRepository mockXhbPddaMessageRepository;

    @Mock
    private XhbCppStagingInboundRepository mockXhbCppStagingInboundRepository;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private LighthousePddaControllerBean classUnderTestMock;

    @TestSubject
    private final LighthousePddaControllerBean classUnderTest =
        new LighthousePddaControllerBean(mockEntityManager);

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
                mockXhbCppStagingInboundRepository, mockEntityManager),
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
    void testGetEntityManager() {
        assertInstanceOf(EntityManager.class, classUnderTest.getEntityManager(), NOT_INSTANCE);
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
    }

    private boolean testProcessFiles(String expectedSavedStatus) {
        // Add Captured Values
        Capture<XhbPddaMessageDao> xhbPddaMessageDaoCapture = EasyMock.newCapture();

        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = getDummyXhbPddaMessageDaoList();
        EasyMock.expect(mockXhbPddaMessageRepository.findByLighthouse())
            .andReturn(xhbPddaMessageDaoList);
        for (XhbPddaMessageDao xhbPddaMessageDao : xhbPddaMessageDaoList) {
            String[] fileParts = xhbPddaMessageDao.getCpDocumentName().split(UNDERSCORE);
            if (fileParts.length == PART_NO) {
                processMessage(xhbPddaMessageDao, xhbPddaMessageDaoCapture, expectedSavedStatus, 2);
            } else {
                processMessage(xhbPddaMessageDao, xhbPddaMessageDaoCapture, expectedSavedStatus, 1);
                continue;
            }
        }
        EasyMock.replay(classUnderTestMock);
        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbPddaMessageRepository);
        EasyMock.replay(mockXhbCppStagingInboundRepository);
        // Run
        classUnderTest.doTask();
        // Checks
        EasyMock.verify(mockEntityManager);
        EasyMock.verify(mockXhbPddaMessageRepository);
        assertNotNull(xhbPddaMessageDaoCapture, NOTNULL);
        assertSame(expectedSavedStatus, xhbPddaMessageDaoCapture.getValue().getCpDocumentStatus(),
            SAME);
        return true;
    }

    private void processMessage(XhbPddaMessageDao xhbPddaMessageDao,
        Capture<XhbPddaMessageDao> xhbPddaMessageDaoCapture, String expectedSavedStatus,
        Integer noOfExpects) {
        EasyMock.expect(mockXhbPddaMessageRepository.findById(xhbPddaMessageDao.getPrimaryKey()))
            .andReturn(Optional.of(xhbPddaMessageDao));
        EasyMock.expectLastCall().times(noOfExpects);

        Optional<XhbPddaMessageDao> xhbPddaMessageDao1 =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao());

        EasyMock
            .expect(mockXhbPddaMessageRepository.update(EasyMock.capture(xhbPddaMessageDaoCapture)))
            .andReturn(xhbPddaMessageDao1).times(noOfExpects);
        EasyMock.expectLastCall();

        classUnderTestMock.updatePddaMessageStatus(EasyMock.isA(XhbPddaMessageDao.class),
            EasyMock.isA(String.class));
        EasyMock.expectLastCall();

        if (MESSAGE_STATUS_INVALID.equals(expectedSavedStatus)) {
            // Failure
            EasyMock
                .expect(mockXhbCppStagingInboundRepository
                    .update(EasyMock.isA(XhbCppStagingInboundDao.class)))
                .andThrow(getRuntimeException());
        } else {
            // Success
            XhbCppStagingInboundDao stagingInboundDao =
                DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            stagingInboundDao.setDocumentName(xhbPddaMessageDao.getCpDocumentName());
            EasyMock
                .expect(mockXhbCppStagingInboundRepository
                    .update(EasyMock.isA(XhbCppStagingInboundDao.class)))
                .andReturn(Optional.of(stagingInboundDao));
        }
    }

    @Test
    void testIsDocumentNameValid() {
        assertTrue(classUnderTest.isDocumentNameValid(DAILY_LIST_EXAMPLE), TRUE);
        assertFalse(classUnderTest.isDocumentNameValid("Invalid_File.csv"), FALSE);
        assertFalse(classUnderTest.isDocumentNameValid("NotAFile_100_20220802030423.xml"), FALSE);
    }

    private List<XhbPddaMessageDao> getDummyXhbPddaMessageDaoList() {
        List<XhbPddaMessageDao> result = new ArrayList<>();
        String[] cpDocumentNames = {DAILY_LIST_EXAMPLE, "WarnedList_111_20220810010433.xml",
            "FirmList_101_20220807010423.xml", "PublicDisplay_100_20220802030423.xml",
            "PDDA_303_1_453_20240411130023", "WebPage_122_20220804031423.xml"};
        // {"Invalid_File.csv"};
        // "NotAFile_100_20220802030423.xml", "PDDA_303_1_453_20240411130023"};
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



}
