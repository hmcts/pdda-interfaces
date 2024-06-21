package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CourtelHelperTest.
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
@ExtendWith(EasyMockExtension.class)
class CourtelHelperTest {

    private static final String NOT_TRUE = "Result is not True";
    private static final String NOT_FALSE = "Result is not False";
    private static final String NULL = "Result is Null";
    private static final String NOT_NULL = "Result is Not Null";

    private XhbClobRepository mockXhbClobRepository;
    private XhbBlobRepository mockXhbBlobRepository;
    private XhbCourtelListRepository mockXhbCourtelListRepository;
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;
    private BlobHelper mockBlobHelper;

    private CourtelHelper classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
        mockXhbClobRepository = EasyMock.mock(XhbClobRepository.class);
        mockXhbBlobRepository = EasyMock.mock(XhbBlobRepository.class);
        mockXhbCourtelListRepository = EasyMock.mock(XhbCourtelListRepository.class);
        mockXhbXmlDocumentRepository = EasyMock.mock(XhbXmlDocumentRepository.class);
        mockBlobHelper = EasyMock.mock(BlobHelper.class);
        XhbConfigPropRepository mockXhbConfigPropRepository =
            EasyMock.mock(XhbConfigPropRepository.class);

        classUnderTest = new CourtelHelper(mockXhbClobRepository, mockXhbCourtelListRepository,
            mockXhbXmlDocumentRepository, mockBlobHelper, mockXhbConfigPropRepository);
    }

    @Test
    void testIsCourtelSendableDocumentValid() {
        for (String type : CourtelHelper.VALID_LISTS) {
            assertTrue(classUnderTest.isCourtelSendableDocument(type), NOT_TRUE);
        }
    }

    @Test
    void testIsCourtelSendableDocumentInvalid() {
        assertFalse(classUnderTest.isCourtelSendableDocument("INVALID"), NOT_FALSE);
    }

    @Test
    void testWriteToCourtel() {
        // Setup
        Long clobId = Long.valueOf(-99);
        Optional<XhbClobDao> xhbClobDao =
            Optional.of(DummyFormattingUtil.getXhbClobDao(clobId, ""));
        Optional<XhbXmlDocumentDao> xhbXmlDocument =
            Optional.of(DummyFormattingUtil.getXhbXmlDocumentDao());
        Optional<XhbCourtelListDao> xhbCourtelList =
            Optional.of(DummyCourtelUtil.getXhbCourtelListDao());

        // Expect - 1st run (No clob)
        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(Optional.empty());
        // Expect - 2nd run (No XmlDocument)
        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock
            .expect(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(EasyMock.isA(Long.class)))
            .andReturn(Optional.empty());
        // Expect - 3rd run (With CourtelList)
        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock
            .expect(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(EasyMock.isA(Long.class)))
            .andReturn(xhbXmlDocument);
        EasyMock
            .expect(mockXhbCourtelListRepository.findByXmlDocumentId(EasyMock.isA(Integer.class)))
            .andReturn(xhbCourtelList);
        // Expect - 4th run (No CourtelList - FINAL SUCCESSFUL VERSION)
        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock
            .expect(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(EasyMock.isA(Long.class)))
            .andReturn(xhbXmlDocument);
        EasyMock
            .expect(mockXhbCourtelListRepository.findByXmlDocumentId(EasyMock.isA(Integer.class)))
            .andReturn(Optional.empty());
        mockXhbCourtelListRepository.save(EasyMock.isA(XhbCourtelListDao.class));
        // Replays
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockXhbXmlDocumentRepository);
        EasyMock.replay(mockXhbCourtelListRepository);
        boolean result;
        Long blobId = Long.valueOf(-99);
        // Run - 1st (No Clob)
        result = testWriteToCourtel(clobId, blobId);
        assertTrue(result, NOT_TRUE);
        // Run - 2nd (No XmlDocument)
        result = testWriteToCourtel(clobId, blobId);
        assertTrue(result, NOT_TRUE);
        // Run - 3rd (With CourtelList)
        result = testWriteToCourtel(clobId, blobId);
        assertTrue(result, NOT_TRUE);
        // Run - 4th (No CourtelList - FINAL SUCCESSFUL VERSION)
        result = testWriteToCourtel(clobId, blobId);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testWriteToCourtel(Long clobId, Long blobId) {
        boolean result = false;
        try {
            classUnderTest.writeToCourtel(clobId, blobId);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        assertTrue(result, NOT_TRUE);
        return true;
    }

    @Test
    void testProcessCourtelList() {
        // Setup
        Long clobId = Long.valueOf(-99);
        Optional<XhbClobDao> xhbClobDao =
            Optional.of(DummyFormattingUtil.getXhbClobDao(clobId, ""));
        Optional<XhbBlobDao> xhbBlobDao = Optional.of(new XhbBlobDao());

        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock.expect(mockXhbBlobRepository.update(EasyMock.isA(XhbBlobDao.class)))
            .andReturn(xhbBlobDao);

        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockXhbBlobRepository);
        // Run
        XhbCourtelListDao result = classUnderTest.processCourtelList(getDummyCourtelList());
        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testProcessCourtelListNull() {
        // Setup
        EasyMock.expect(mockXhbClobRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(Optional.empty());

        EasyMock.replay(mockXhbClobRepository);
        // Run
        XhbCourtelListDao result = classUnderTest.processCourtelList(getDummyCourtelList());
        // Checks
        assertNull(result, NOT_NULL);
    }

    @Test
    void testSendCourtelList() {
        // Setup
        byte[] blobData = "TestData".getBytes();
        EasyMock.expect(mockBlobHelper.getBlobData(EasyMock.isA(Long.class))).andReturn(blobData);
        EasyMock.replay(mockBlobHelper);
        boolean result;
        // Run
        classUnderTest.sendCourtelList(getDummyCourtelList());
        result = true;
        // Checks
        EasyMock.verify(mockBlobHelper);
        assertTrue(result, NOT_TRUE);
    }

    private XhbCourtelListDao getDummyCourtelList() {
        Long id = Long.valueOf(-99);
        XhbCourtelListDao xhbCourtelListDao = new XhbCourtelListDao();
        xhbCourtelListDao.setXmlDocumentClobId(id);
        xhbCourtelListDao.setBlobId(id);
        return xhbCourtelListDao;
    }

}
