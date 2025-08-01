package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class CourtelHelperTest {

    private static final String NULL = "Result is Null";
    private static final String NOT_TRUE = "Result is not True";
    private static final String NOT_FALSE = "Result is not False";

    private XhbCourtRepository mockXhbCourtRepository;
    private XhbClobRepository mockXhbClobRepository;
    private XhbCourtelListRepository mockXhbCourtelListRepository;
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;
    private CathHelper mockCathHelper;
    private BlobHelper mockBlobHelper;
    private ConfigPropMaintainer mockConfigPropMaintainer;
    private EntityManager mockEntityManager;

    private CourtelHelper classUnderTest;

    @BeforeEach
    public void setUp() {
        mockEntityManager = EasyMock.mock(EntityManager.class);
        mockXhbClobRepository = EasyMock.mock(XhbClobRepository.class);
        mockXhbCourtRepository = EasyMock.mock(XhbCourtRepository.class);
        mockXhbCourtelListRepository = EasyMock.mock(XhbCourtelListRepository.class);
        mockXhbXmlDocumentRepository = EasyMock.mock(XhbXmlDocumentRepository.class);
        mockBlobHelper = EasyMock.mock(BlobHelper.class);
        mockCathHelper = EasyMock.mock(CathHelper.class);
        mockConfigPropMaintainer = EasyMock.mock(ConfigPropMaintainer.class);
        XhbConfigPropRepository mockXhbConfigPropRepository =
            EasyMock.mock(XhbConfigPropRepository.class);

        classUnderTest = new CourtelHelper(mockXhbClobRepository, mockXhbCourtelListRepository,
            mockXhbXmlDocumentRepository, mockBlobHelper, mockXhbConfigPropRepository,
            mockXhbCourtRepository);

        ReflectionTestUtils.setField(classUnderTest, "cathHelper", mockCathHelper);
        ReflectionTestUtils.setField(classUnderTest, "configPropMaintainer",
            mockConfigPropMaintainer);
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
        EasyMock.expect(mockXhbClobRepository.findByIdSafe(EasyMock.isA(Long.class)))
            .andReturn(Optional.empty());
        // Expect - 2nd run (No XmlDocument)
        EasyMock.expect(mockXhbClobRepository.findByIdSafe(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock
            .expect(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(EasyMock.isA(Long.class)))
            .andReturn(Optional.empty());
        // Expect - 3rd run (With CourtelList)
        EasyMock.expect(mockXhbClobRepository.findByIdSafe(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock
            .expect(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(EasyMock.isA(Long.class)))
            .andReturn(xhbXmlDocument);
        EasyMock
            .expect(mockXhbCourtelListRepository.findByXmlDocumentId(EasyMock.isA(Integer.class)))
            .andReturn(xhbCourtelList);
        // Expect - 4th run (No CourtelList - FINAL SUCCESSFUL VERSION)
        EasyMock.expect(mockXhbClobRepository.findByIdSafe(EasyMock.isA(Long.class)))
            .andReturn(xhbClobDao);
        EasyMock
            .expect(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(EasyMock.isA(Long.class)))
            .andReturn(xhbXmlDocument);
        EasyMock
            .expect(mockXhbCourtelListRepository.findByXmlDocumentId(EasyMock.isA(Integer.class)))
            .andReturn(Optional.empty());
        mockXhbCourtelListRepository.save(EasyMock.isA(XhbCourtelListDao.class));

        expectFindByCourtId();
        expectGetEntityManager(mockXhbClobRepository);
        expectGetEntityManager(mockXhbXmlDocumentRepository);
        expectGetEntityManager(mockXhbCourtelListRepository);
        expectGetEntityManager(mockXhbCourtRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();

        // Replays
        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockXhbXmlDocumentRepository);
        EasyMock.replay(mockXhbCourtelListRepository);
        EasyMock.replay(mockXhbCourtRepository);
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
    void testSendCourtelListDailyList() {
        boolean result = true;
        testSendCourtelList("DL");
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testSendCourtelListWebPage() {
        boolean result = true;
        testSendCourtelList("IWP");
        assertTrue(result, NOT_TRUE);
    }

    void testSendCourtelList(String type) {
        // Setup
        byte[] blobData = "Test Blob Data".getBytes();
        XhbBlobDao xhbBlobDao = DummyFormattingUtil.getXhbBlobDao(blobData);
        XhbCourtelListDao xhbCourtelListDao = DummyCourtelUtil.getXhbCourtelListDao();
        xhbCourtelListDao.setBlob(xhbBlobDao);
        XhbXmlDocumentDao xhbXmlDocumentDao = DummyFormattingUtil.getXhbXmlDocumentDao();
        xhbXmlDocumentDao.setDocumentType(type);
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(1L, 
              "<cs:ListHeader><cs:EndDate>2020-01-21</cs:EndDate></cs:ListHeader>");
        
        EasyMock.expect(mockXhbXmlDocumentRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbXmlDocumentDao));
        EasyMock.expect(mockXhbClobRepository.getEntityManager()).andReturn(mockEntityManager);
        EasyMock.expect(mockXhbClobRepository.findByIdSafe(xhbClobDao.getClobId()))
            .andReturn(Optional.of(xhbClobDao));
        EasyMock.expect(mockCathHelper.generateJsonString(EasyMock.isA(XhbCourtelListDao.class),
            EasyMock.isA(CourtelJson.class))).andReturn("");
        EasyMock.expect(mockBlobHelper.getBlob(EasyMock.isA(Long.class))).andReturn(xhbBlobDao);
        mockCathHelper.send(EasyMock.isA(CourtelJson.class));
        EasyMock.expectLastCall();

        expectFindByCourtId();
        expectGetEntityManager(mockXhbXmlDocumentRepository);
        expectGetEntityManager(mockXhbCourtRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();

        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbXmlDocumentRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockCathHelper);
        // Run
        classUnderTest.sendCourtelList(xhbCourtelListDao);
        // Checks
        EasyMock.verify(mockCathHelper);
    }

    @Test
    void testSendCourtelListEmptyCourtDao() {
        // Setup
        byte[] blobData = "Test Blob Data".getBytes();
        XhbBlobDao xhbBlobDao = DummyFormattingUtil.getXhbBlobDao(blobData);
        XhbCourtelListDao xhbCourtelListDao = DummyCourtelUtil.getXhbCourtelListDao();
        xhbCourtelListDao.setBlob(xhbBlobDao);
        XhbXmlDocumentDao xhbXmlDocumentDao = DummyFormattingUtil.getXhbXmlDocumentDao();
        xhbXmlDocumentDao.setDocumentType("DL");
        EasyMock.expect(mockXhbXmlDocumentRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbXmlDocumentDao));
        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.empty());
        expectGetEntityManager(mockXhbXmlDocumentRepository);
        expectGetEntityManager(mockXhbCourtRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();

        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbXmlDocumentRepository);
        EasyMock.replay(mockXhbCourtRepository);
        // Run
        classUnderTest.sendCourtelList(xhbCourtelListDao);
        // Checks
        EasyMock.verify(mockEntityManager);
        EasyMock.verify(mockXhbXmlDocumentRepository);
        EasyMock.verify(mockXhbCourtRepository);
    }

    @Test
    void testGetCourtelList() {
        // Setup
        List<XhbCourtelListDao> xhbCourtelListDaoList = new ArrayList<>();
        final Integer courtelMaxRetry = 5;
        final Integer courtelLookupDelay = 2;
        final Integer courtelLisAmount = 20;
        // Expects
        EasyMock.expect(mockXhbCourtelListRepository.getEntityManager())
            .andReturn(mockEntityManager);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.expect(mockConfigPropMaintainer.getPropertyValue(EasyMock.isA(String.class)))
            .andReturn(courtelMaxRetry.toString());
        EasyMock.expect(mockConfigPropMaintainer.getPropertyValue(EasyMock.isA(String.class)))
            .andReturn(courtelLookupDelay.toString());
        EasyMock.expect(mockConfigPropMaintainer.getPropertyValue(EasyMock.isA(String.class)))
            .andReturn(courtelLisAmount.toString());
        EasyMock
            .expect(mockXhbCourtelListRepository.findCourtelList(EasyMock.isA(Integer.class),
                EasyMock.isA(Integer.class), EasyMock.isA(Integer.class)))
            .andReturn(xhbCourtelListDaoList);
        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockConfigPropMaintainer);
        EasyMock.replay(mockXhbCourtelListRepository);
        // Run
        List<XhbCourtelListDao> results = classUnderTest.getCourtelList();
        // Checks
        assertNotNull(results, NULL);

    }

    private void expectFindByCourtId() {
        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(1, "Court1"))).anyTimes();
    }


    @SuppressWarnings("rawtypes")
    private void expectGetEntityManager(AbstractRepository mockRepository) {
        EasyMock.expect(mockRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
    }

}
