package uk.gov.hmcts.pdda.business.services.cppformatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: Cpp List Controller Bean Test.
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
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class CppFormattingControllerBeanTest {

    private static final String TRUE = "Result is not True";

    @Mock
    protected EntityManager mockEntityManager;
    
    @Mock
    private XhbCppFormattingRepository mockCppFormattingRepo;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private ConfigurationChangeEvent mockConfigurationChangeEvent;

    @TestSubject
    private final CppFormattingControllerBean classUnderTest =
        new CppFormattingControllerBean(EasyMock.createMock(EntityManager.class));

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
            new CppFormattingControllerBean();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testDoTask() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        List<XhbCppFormattingDao> cppFormattingList = new ArrayList<>();
        cppFormattingList.add(xhbCppFormattingDao);
        
        EasyMock.expect(mockCppFormattingRepo.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockCppFormattingRepo.findAllNewByDocType(EasyMock.isA(String.class),
            EasyMock.isA(LocalDateTime.class))).andReturn(cppFormattingList);
        for (XhbCppFormattingDao cppFormattingDao : cppFormattingList) {
            mockPublicDisplayNotifier.sendMessage(EasyMock.isA(ConfigurationChangeEvent.class));
            EasyMock.expect(mockCppFormattingRepo.update(cppFormattingDao))
                .andReturn(Optional.of(cppFormattingDao));
            EasyMock.replay(mockPublicDisplayNotifier);
            EasyMock.replay(mockCppFormattingRepo);
            EasyMock.replay(mockEntityManager);
        }
        // Run
        boolean result = false;
        try {
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppFormattingRepo);
        EasyMock.verify(mockPublicDisplayNotifier);
        EasyMock.verify(mockEntityManager);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessCppPublicDisplayDocs() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        List<XhbCppFormattingDao> cppFormattingList = new ArrayList<>();
        cppFormattingList.add(xhbCppFormattingDao);
        
        EasyMock.expect(mockCppFormattingRepo.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockCppFormattingRepo.findAllNewByDocType(EasyMock.isA(String.class),
            EasyMock.isA(LocalDateTime.class))).andReturn(cppFormattingList);
        for (XhbCppFormattingDao cppFormattingDao : cppFormattingList) {
            mockPublicDisplayNotifier.sendMessage(EasyMock.isA(ConfigurationChangeEvent.class));
            EasyMock.expect(mockCppFormattingRepo.update(cppFormattingDao))
                .andReturn(Optional.of(cppFormattingDao));
            EasyMock.replay(mockPublicDisplayNotifier);
            EasyMock.replay(mockCppFormattingRepo);
            EasyMock.replay(mockEntityManager);
        }
        // Run
        boolean result = false;
        try {
            classUnderTest.processCppPublicDisplayDocs();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppFormattingRepo);
        EasyMock.verify(mockPublicDisplayNotifier);
        EasyMock.verify(mockEntityManager);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetLatestPublicDisplayDocument() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        
        EasyMock.expect(mockCppFormattingRepo.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock
            .expect(
                mockCppFormattingRepo.getLatestDocumentByCourtIdAndType(EasyMock.isA(Integer.class),
                    EasyMock.isA(String.class), EasyMock.isA(LocalDateTime.class)))
            .andReturn(xhbCppFormattingDao);
        EasyMock.replay(mockCppFormattingRepo);
        EasyMock.replay(mockEntityManager);
        // Run
        XhbCppFormattingDao actualResult =
            classUnderTest.getLatestPublicDisplayDocument(xhbCppFormattingDao.getCourtId());
        // Checks
        EasyMock.verify(mockCppFormattingRepo);
        EasyMock.verify(mockEntityManager);
        assertNotNull(actualResult, "Result is Null");
        assertSame(xhbCppFormattingDao, actualResult, "Result is not Same");
    }

    @Test
    void testGetLatestWebPageDocument() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        
        EasyMock.expect(mockCppFormattingRepo.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock
            .expect(
                mockCppFormattingRepo.getLatestDocumentByCourtIdAndType(EasyMock.isA(Integer.class),
                    EasyMock.isA(String.class), EasyMock.isA(LocalDateTime.class)))
            .andReturn(xhbCppFormattingDao);
        EasyMock.replay(mockCppFormattingRepo);
        EasyMock.replay(mockEntityManager);
        // Run
        XhbCppFormattingDao actualResult =
            classUnderTest.getLatestWebPageDocument(xhbCppFormattingDao.getCourtId());
        // Checks
        EasyMock.verify(mockCppFormattingRepo);
        EasyMock.verify(mockEntityManager);
        assertNotNull(actualResult, "Result is Null");
        assertSame(xhbCppFormattingDao, actualResult, "Result is not Same");
    }

    @Test
    void testUpdateStatusSuccess() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        
        EasyMock.expect(mockCppFormattingRepo.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockCppFormattingRepo.update(xhbCppFormattingDao))
            .andReturn(Optional.of(xhbCppFormattingDao));
        EasyMock.replay(mockCppFormattingRepo);
        EasyMock.replay(mockEntityManager);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateStatusSuccess(xhbCppFormattingDao);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppFormattingRepo);
        EasyMock.verify(mockEntityManager);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateStatusFailed() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        
        EasyMock.expect(mockCppFormattingRepo.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockCppFormattingRepo.update(xhbCppFormattingDao))
            .andReturn(Optional.of(xhbCppFormattingDao));
        EasyMock.replay(mockCppFormattingRepo);
        EasyMock.replay(mockEntityManager);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateStatusFailed(xhbCppFormattingDao);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppFormattingRepo);
        EasyMock.verify(mockEntityManager);
        assertTrue(result, TRUE);
    }

    @Test
    void testRefreshPublicDisplaysForCourt() {
        // Setup
        XhbCppFormattingDao xhbCppFormattingDao = getDummyXhbCppFormattingDao();
        mockPublicDisplayNotifier.sendMessage(EasyMock.isA(ConfigurationChangeEvent.class));
        EasyMock.replay(mockPublicDisplayNotifier);
        // Run
        boolean result = false;
        try {
            classUnderTest.refreshPublicDisplaysForCourt(xhbCppFormattingDao.getCourtId());
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockPublicDisplayNotifier);
        assertTrue(result, TRUE);
    }

    private XhbCppFormattingDao getDummyXhbCppFormattingDao() {

        Integer cppFormattingId = Double.valueOf(Math.random()).intValue();
        Integer stagingTableId = Double.valueOf(Math.random()).intValue();
        LocalDateTime dateIn = LocalDateTime.now().minusMinutes(5);
        String formatStatus = "MS";
        String documentType = "PD";
        Integer courtId = 95;
        Long xmlDocumentClobId = (long) 1_526_408;
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";

        XhbCppFormattingDao result = new XhbCppFormattingDao();
        result.setCppFormattingId(cppFormattingId);
        result.setStagingTableId(stagingTableId);
        result.setDateIn(dateIn);
        result.setFormatStatus(formatStatus);
        result.setDocumentType(documentType);
        result.setCourtId(courtId);
        result.setXmlDocumentClobId(xmlDocumentClobId);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbCppFormattingDao(result);
    }
}
