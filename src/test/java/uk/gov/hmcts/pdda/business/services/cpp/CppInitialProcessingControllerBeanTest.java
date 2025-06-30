package uk.gov.hmcts.pdda.business.services.cpp;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerException;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CPPInitialProcessingControllerBean Test.
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
 * @author Chris Vincent
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD")
class CppInitialProcessingControllerBeanTest
    extends AbstractCppInitialProcessingControllerBeanTest {

    /**
     * Test to invoke the doTask method which will call most of the other public methods in the
     * class following the Daily List route.
     */
    @Test
    void testDoTask() {
        // Setup
        XhbCppStagingInboundDao unprocessedXcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        unprocessedXcsi.setDocumentName(DAILY_LIST_DOCNAME);
        unprocessedXcsi.setDocumentType(DAILY_LIST_DOCTYPE);
        unprocessedXcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED);
        unprocessedXcsi.setProcessingStatus(null);
        List<XhbCppStagingInboundDao> unprocessedDocList = new ArrayList<>();
        unprocessedDocList.add(unprocessedXcsi);

        XhbCppStagingInboundDao validatedXcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        validatedXcsi.setDocumentName(DAILY_LIST_DOCNAME);
        validatedXcsi.setDocumentType(DAILY_LIST_DOCTYPE);
        validatedXcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS);
        validatedXcsi.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED);
        List<XhbCppStagingInboundDao> validatedDocList = new ArrayList<>();
        validatedDocList.add(validatedXcsi);

        XhbCppListDao xcl = DummyFormattingUtil.getXhbCppListDao();

        List<XhbCourtDao> xhbCourtDaoList = new ArrayList<>();
        xhbCourtDaoList.add(DummyCourtUtil.getXhbCourtDao(1, ""));

        try {
            EasyMock.expect(mockXhbCppStagingInboundRepository.getEntityManager())
                .andReturn(mockEntityManager).anyTimes();
            EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
            mockEntityManager.clear();
            EasyMock.expectLastCall().times(2);
            
            EasyMock.expect(mockCppStagingInboundControllerBean.getLatestUnprocessedDocument())
                .andReturn(unprocessedDocList);
            EasyMock.expect(mockCppStagingInboundControllerBean
                .updateStatusInProcess(unprocessedXcsi, BATCH_USERNAME))
                .andReturn(Optional.of(unprocessedXcsi));
            EasyMock.expect(mockCppStagingInboundControllerBean.validateDocument(unprocessedXcsi,
                BATCH_USERNAME)).andReturn(true);
            EasyMock.expect(mockCppStagingInboundControllerBean.getXhbCppStagingInboundRepository())
                .andReturn(mockXhbCppStagingInboundRepository);
            EasyMock
                .expect(
                    mockXhbCppStagingInboundRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(unprocessedXcsi));
            EasyMock.expect(
                mockCppStagingInboundControllerBean.getClobXmlAsString(unprocessedXcsi.getClobId()))
                .andReturn(DAILY_LIST_XML);

            EasyMock
                .expect(mockCppListControllerBean.checkForExistingCppListRecord(
                    EasyMock.isA(Integer.class), EasyMock.isA(String.class),
                    EasyMock.isA(LocalDateTime.class), EasyMock.isA(LocalDateTime.class)))
                .andReturn(null);

            mockXhbCppListRepository.save(EasyMock.isA(XhbCppListDao.class));
            EasyMock
                .expect(
                    mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andReturn(xhbCourtDaoList);
            mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));

            mockCppStagingInboundControllerBean.updateStatusProcessingSuccess(unprocessedXcsi,
                BATCH_USERNAME);
            
            mockListNodesHelper.processClobData(EasyMock.isA(String.class));
            EasyMock.expectLastCall().anyTimes();

            EasyMock.expect(mockCppStagingInboundControllerBean.getNextValidatedDocument())
                .andReturn(validatedDocList);
            EasyMock.expect(
                mockCppStagingInboundControllerBean.getClobXmlAsString(validatedXcsi.getClobId()))
                .andReturn(DAILY_LIST_XML);

            EasyMock
                .expect(mockCppListControllerBean.checkForExistingCppListRecord(
                    EasyMock.isA(Integer.class), EasyMock.isA(String.class),
                    EasyMock.isA(LocalDateTime.class), EasyMock.isA(LocalDateTime.class)))
                .andReturn(xcl);
            mockCppListControllerBean.updateCppList(xcl);

            mockCppStagingInboundControllerBean.updateStatusProcessingSuccess(validatedXcsi,
                BATCH_USERNAME);
            
        } catch (CppStagingInboundControllerException | ValidationException exception) {
            fail(exception);
        }

        replayMocks();

        // Run method
        classUnderTest.doTask();

        // Checks
        verifyMocks();
    }

    /**
     * Test to invoke the processCPPStagingInboundMessages method which will call most of the other
     * public methods in the class following the Webpage route.
     */
    @Test
    void testProcessCppStagingInboundMessages() {
        // Setup
        XhbCppStagingInboundDao unprocessedXcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        unprocessedXcsi.setDocumentName(WEBPAGE_DOCNAME);
        unprocessedXcsi.setDocumentType(WEBPAGE_DOCTYPE);
        unprocessedXcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED);
        unprocessedXcsi.setProcessingStatus(null);
        List<XhbCppStagingInboundDao> unprocessedDocList = new ArrayList<>();
        unprocessedDocList.add(unprocessedXcsi);

        XhbCppStagingInboundDao validatedXcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        validatedXcsi.setDocumentName(WEBPAGE_DOCNAME);
        validatedXcsi.setDocumentType(WEBPAGE_DOCTYPE);
        validatedXcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS);
        validatedXcsi.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED);
        List<XhbCppStagingInboundDao> validatedDocList = new ArrayList<>();
        validatedDocList.add(validatedXcsi);

        int courtId = 1;

        XhbCppFormattingDao xcf = DummyFormattingUtil.getXhbCppFormattingDao();

        try {
            EasyMock.expect(mockXhbCppStagingInboundRepository.getEntityManager())
                .andReturn(mockEntityManager).anyTimes();
            EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
            mockEntityManager.clear();
            EasyMock.expectLastCall().times(2);
            
            EasyMock.expect(mockCppStagingInboundControllerBean.getLatestUnprocessedDocument())
                .andReturn(unprocessedDocList);
            EasyMock.expect(mockCppStagingInboundControllerBean
                .updateStatusInProcess(unprocessedXcsi, BATCH_USERNAME))
                .andReturn(Optional.of(unprocessedXcsi));
            EasyMock.expect(mockCppStagingInboundControllerBean.validateDocument(unprocessedXcsi,
                BATCH_USERNAME)).andReturn(true);
            EasyMock.expect(mockCppStagingInboundControllerBean.getXhbCppStagingInboundRepository())
                .andReturn(mockXhbCppStagingInboundRepository);
            EasyMock
                .expect(
                    mockXhbCppStagingInboundRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(unprocessedXcsi));
            EasyMock.expect(
                mockCppStagingInboundControllerBean.getClobXmlAsString(unprocessedXcsi.getClobId()))
                .andReturn(INTERNET_WEBPAGE);
            EasyMock
                .expect(mockCppStagingInboundControllerBean
                    .getCourtId(Integer.valueOf(unprocessedXcsi.getCourtCode())))
                .andReturn(courtId);

            EasyMock.expect(mockXhbCppFormattingRepository.findLatestByCourtDateInDocSafe(
                EasyMock.isA(Integer.class), EasyMock.isA(String.class),
                EasyMock.isA(LocalDateTime.class))).andReturn(null);
            mockXhbCppFormattingRepository.save(EasyMock.isA(XhbCppFormattingDao.class));

            mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));
            mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));

            mockCppStagingInboundControllerBean.updateStatusProcessingSuccess(unprocessedXcsi,
                BATCH_USERNAME);
            
            mockListNodesHelper.processClobData(EasyMock.isA(String.class));
            EasyMock.expectLastCall().anyTimes();
            
            EasyMock.expect(mockCppStagingInboundControllerBean.getNextValidatedDocument())
                .andReturn(validatedDocList);
            EasyMock.expect(
                mockCppStagingInboundControllerBean.getClobXmlAsString(validatedXcsi.getClobId()))
                .andReturn(INTERNET_WEBPAGE);
            EasyMock.expect(mockCppStagingInboundControllerBean
                .getCourtId(Integer.valueOf(validatedXcsi.getCourtCode()))).andReturn(courtId);

            EasyMock.expect(mockXhbCppFormattingRepository.findLatestByCourtDateInDocSafe(
                EasyMock.isA(Integer.class), EasyMock.isA(String.class),
                EasyMock.isA(LocalDateTime.class))).andReturn(xcf);
            EasyMock.expect(mockXhbCppFormattingRepository.update(xcf)).andReturn(null);

            mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));
            mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));

            mockCppStagingInboundControllerBean.updateStatusProcessingSuccess(validatedXcsi,
                BATCH_USERNAME);

            replayMocks();

            // Run method
            classUnderTest.doTask();
        } catch (CppInitialProcessingControllerException | ValidationException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }

    /**
     * Test to invoke the handleNewDocuments method when there are no documents to be processed. The
     * method will have been tested with documents to process in other tests.
     */
    @Test
    void testHandleNewDocuments() {
        // Setup
        EasyMock.expect(mockCppStagingInboundControllerBean.getLatestUnprocessedDocument())
            .andReturn(null);

        replayMocks();

        // Run method
        try {
            classUnderTest.handleNewDocuments();
        } catch (CppStagingInboundControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }

    @Test
    void testHandleNewDocumentsFailure() {
        // Setup
        EasyMock.expect(mockCppStagingInboundControllerBean.getLatestUnprocessedDocument())
            .andThrow(new CppStagingInboundControllerException());

        replayMocks();

        // Run method
        try {
            classUnderTest.handleNewDocuments();
        } catch (CppStagingInboundControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }

    /**
     * Test to invoke the handleStuckDocuments method when there are no documents to be processed.
     * The method will have been tested with documents to process in other tests.
     */
    @Test
    void testHandleStuckDocuments() {
        // Setup
        EasyMock.expect(mockCppStagingInboundControllerBean.getNextValidatedDocument())
            .andReturn(null);

        replayMocks();

        // Run method
        try {
            classUnderTest.handleStuckDocuments();
        } catch (CppStagingInboundControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }

    @Test
    void testHandleStuckDocumentsFailure() {
        // Setup
        EasyMock.expect(mockCppStagingInboundControllerBean.getNextValidatedDocument())
            .andThrow(new CppStagingInboundControllerException());
        replayMocks();

        // Run method
        try {
            classUnderTest.handleStuckDocuments();
        } catch (CppStagingInboundControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }

    /**
     * Test to invoke the processValidatedDocument method when a XhbCppStagingInboundDao with an
     * invalid document type is processed. The method will have been tested with valid document
     * types in other tests.
     */
    @Test
    void testProcessValidatedDocument() {
        // Setup
        XhbCppStagingInboundDao invalidXcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        invalidXcsi.setDocumentName(DAILY_LIST_DOCNAME);
        invalidXcsi.setDocumentType(INVALID_DOCTYPE);
        invalidXcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED);
        invalidXcsi.setProcessingStatus(null);

        EasyMock.expect(mockXhbCppStagingInboundRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        mockEntityManager.clear();
        EasyMock.expectLastCall();
        EasyMock
            .expect(mockCppStagingInboundControllerBean.getClobXmlAsString(invalidXcsi.getClobId()))
            .andReturn(DAILY_LIST_XML);
        mockCppStagingInboundControllerBean.updateStatusProcessingFail(
            EasyMock.isA(XhbCppStagingInboundDao.class), EasyMock.isA(String.class),
            EasyMock.isA(String.class));

        replayMocks();

        // Run method
        try {
            boolean success = classUnderTest.processValidatedDocument(invalidXcsi);
            assertFalse(success, FALSE);
        } catch (CppInitialProcessingControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }

    @Test
    void testProcessValidatedDocumentFailure() {
        // Setup
        XhbCppStagingInboundDao invalidXcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        invalidXcsi.setDocumentName(DAILY_LIST_DOCNAME);
        invalidXcsi.setDocumentType(DAILY_LIST_DOCTYPE);
        invalidXcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED);
        invalidXcsi.setProcessingStatus(null);

        EasyMock
            .expect(mockCppStagingInboundControllerBean.getClobXmlAsString(invalidXcsi.getClobId()))
            .andReturn(null);

        replayMocks();

        // Run method
        try {
            boolean success = classUnderTest.processValidatedDocument(invalidXcsi);
            assertFalse(success, FALSE);
        } catch (CppInitialProcessingControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }


    /**
     * Test to invoke the createUpdateNonListRecords method when the document court id cannot be
     * found. The method will have been tested in a positive flow in other tests.
     */
    @Test
    void testCreateUpdateNonListRecords() {
        // Setup
        XhbCppStagingInboundDao xcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        xcsi.setDocumentName(WEBPAGE_DOCNAME);
        xcsi.setDocumentType(WEBPAGE_DOCTYPE);
        xcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS);
        xcsi.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED);
        int courtNotFound = 0;

        EasyMock.expect(
            mockCppStagingInboundControllerBean.getCourtId(Integer.valueOf(xcsi.getCourtCode())))
            .andReturn(courtNotFound);
        mockCppStagingInboundControllerBean.updateStatusProcessingFail(
            EasyMock.isA(XhbCppStagingInboundDao.class), EasyMock.isA(String.class),
            EasyMock.isA(String.class));

        replayMocks();

        // Run
        classUnderTest.createUpdateNonListRecords(xcsi);

        // Checks
        verifyMocks();
    }

    /**
     * Test to invoke the createUpdateListRecords method.
     */
    @Test
    void testCreateUpdateListRecords() {
        // Setup
        XhbCppStagingInboundDao xcsi = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        xcsi.setDocumentName(DAILY_LIST_DOCNAME);
        xcsi.setDocumentType(DAILY_LIST_DOCTYPE);
        xcsi.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED);
        xcsi.setProcessingStatus(null);

        List<XhbCourtDao> xhbCourtDaoList = new ArrayList<>();
        xhbCourtDaoList.add(DummyCourtUtil.getXhbCourtDao(1, ""));

        EasyMock
            .expect(mockCppListControllerBean.checkForExistingCppListRecord(
                EasyMock.isA(Integer.class), EasyMock.isA(String.class),
                EasyMock.isA(LocalDateTime.class), EasyMock.isA(LocalDateTime.class)))
            .andReturn(null);
        
        EasyMock.expect(mockXhbCppListRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        mockXhbCppListRepository.save(EasyMock.isA(XhbCppListDao.class));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andReturn(xhbCourtDaoList);
        mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));
        mockCppStagingInboundControllerBean.updateStatusProcessingSuccess(xcsi, BATCH_USERNAME);
        mockListNodesHelper.processClobData(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        
        replayMocks();

        // Run
        try {
            classUnderTest.createUpdateListRecords(xcsi, DAILY_LIST_XML);
        } catch (CppInitialProcessingControllerException exception) {
            fail(exception);
        }

        // Checks
        verifyMocks();
    }


}
