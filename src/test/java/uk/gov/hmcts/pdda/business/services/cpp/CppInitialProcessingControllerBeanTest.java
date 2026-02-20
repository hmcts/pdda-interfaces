package uk.gov.hmcts.pdda.business.services.cpp;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerException;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.pdda.data.RepositoryHelper;
import uk.gov.hmcts.pdda.business.services.validation.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Title: CPPInitialProcessingControllerBean Test.
 * .
 * Description:
 * .
 * Copyright: Copyright (c) 2022
 * .
 * Company: CGI
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
    void testDoTask() throws Exception {
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

            mockEntityManager.clear();
            EasyMock.expectLastCall().anyTimes();
            expectGetEntityManager(mockXhbCppListRepository);
            expectGetEntityManager(mockXhbCourtRepository);
            expectGetEntityManager(mockXhbFormattingRepository);
            expectGetEntityManager(mockXhbXmlDocumentRepository);
            mockXhbCppListRepository.save(EasyMock.isA(XhbCppListDao.class));
            EasyMock
                .expect(
                    mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andReturn(xhbCourtDaoList);
            EasyMock.expectLastCall().anyTimes();
            mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));
            EasyMock.expectLastCall().anyTimes();
            mockXhbXmlDocumentRepository.save(EasyMock.isA(XhbXmlDocumentDao.class));
            EasyMock.expectLastCall().anyTimes();

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

            // Prevent removeExistingHearingsForCourtAndDate from using real RepositoryHelper
            // (which would call EntityManagerUtil)
            RepositoryHelper mockRepoHelperForTest = EasyMock.createMock(RepositoryHelper.class);
            uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository mockHearingListRepoForTest =
                EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository.class);
            EasyMock.expect(mockRepoHelperForTest.getXhbHearingListRepository())
                .andReturn(mockHearingListRepoForTest).anyTimes();
            EasyMock.expect(mockHearingListRepoForTest
                .findByCourtIdAndDateSafe(EasyMock.isA(Integer.class), EasyMock.isA(LocalDateTime.class)))
            .andReturn(new ArrayList<>()).anyTimes();

            // ---- NEW: Provide mock for XhbCourtSiteRepository and expectation ----
            uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository mockCourtSiteRepoForDoTask =
                EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository.class);
            EasyMock.expect(mockRepoHelperForTest.getXhbCourtSiteRepository())
                .andReturn(mockCourtSiteRepoForDoTask).anyTimes();

            uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao courtSiteDaoDoTask =
                new uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao();
            courtSiteDaoDoTask.setCourtSiteId(123);
            courtSiteDaoDoTask.setCourtId(1);

            EasyMock.expect(mockCourtSiteRepoForDoTask.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andReturn(java.util.List.of(courtSiteDaoDoTask)).anyTimes();
            // ---- END NEW ----

            EasyMock.replay(mockRepoHelperForTest, mockHearingListRepoForTest, mockCourtSiteRepoForDoTask);
            java.lang.reflect.Field repoFieldForDoTask = classUnderTest.getClass().getDeclaredField("repositoryHelper");
            repoFieldForDoTask.setAccessible(true);
            repoFieldForDoTask.set(classUnderTest, mockRepoHelperForTest);

            replayMocks();
        } catch (CppStagingInboundControllerException | ValidationException exception) {
            fail(exception);
        }

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
            EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
            mockEntityManager.clear();
            EasyMock.expectLastCall().anyTimes();
            expectGetEntityManager(mockXhbCppFormattingRepository);
            expectGetEntityManager(mockXhbFormattingRepository);
            expectGetEntityManager(mockXhbXmlDocumentRepository);
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

            mockXhbXmlDocumentRepository.save(EasyMock.isA(XhbXmlDocumentDao.class));
            mockXhbXmlDocumentRepository.save(EasyMock.isA(XhbXmlDocumentDao.class));
            
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

        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        mockEntityManager.clear();
        EasyMock.expectLastCall().anyTimes();
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
        expectGetEntityManager(mockXhbCppListRepository);
        expectGetEntityManager(mockXhbCourtRepository);
        expectGetEntityManager(mockXhbFormattingRepository);
        expectGetEntityManager(mockXhbXmlDocumentRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        mockXhbCppListRepository.save(EasyMock.isA(XhbCppListDao.class));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andReturn(xhbCourtDaoList);
        mockXhbFormattingRepository.save(EasyMock.isA(XhbFormattingDao.class));
        mockXhbXmlDocumentRepository.save(EasyMock.isA(XhbXmlDocumentDao.class));
        mockCppStagingInboundControllerBean.updateStatusProcessingSuccess(xcsi, BATCH_USERNAME);
        mockListNodesHelper.processClobData(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        // Prevent removeExistingHearingsForCourtAndDate from using real RepositoryHelper
        RepositoryHelper mockRepoHelperForCreateTest = EasyMock.createMock(RepositoryHelper.class);
        uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository mockHearingListRepoForCreateTest =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository.class);
        EasyMock.expect(mockRepoHelperForCreateTest.getXhbHearingListRepository())
        .andReturn(mockHearingListRepoForCreateTest).anyTimes();
        EasyMock.expect(mockHearingListRepoForCreateTest
            .findByCourtIdAndDateSafe(EasyMock.isA(Integer.class), EasyMock.isA(LocalDateTime.class)))
            .andReturn(new ArrayList<>()).anyTimes();

        // ---- NEW: Provide mock for XhbCourtSiteRepository and expectation ----
        uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository mockCourtSiteRepoForCreateTest =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository.class);
        EasyMock.expect(mockRepoHelperForCreateTest.getXhbCourtSiteRepository())
            .andReturn(mockCourtSiteRepoForCreateTest).anyTimes();

        uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao courtSiteDaoCreateTest =
            new uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao();
        courtSiteDaoCreateTest.setCourtSiteId(456);
        courtSiteDaoCreateTest.setCourtId(1);

        EasyMock.expect(mockCourtSiteRepoForCreateTest.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andReturn(java.util.List.of(courtSiteDaoCreateTest)).anyTimes();
        // ---- END NEW ----

        EasyMock.replay(mockRepoHelperForCreateTest, mockHearingListRepoForCreateTest, mockCourtSiteRepoForCreateTest);
        try {
            java.lang.reflect.Field repoFieldForCreateTest = classUnderTest.getClass()
                .getDeclaredField("repositoryHelper");
            repoFieldForCreateTest.setAccessible(true);
            repoFieldForCreateTest.set(classUnderTest, mockRepoHelperForCreateTest);
        } catch (Exception e) {
            fail(e);
        }

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

    @SuppressWarnings("rawtypes")
    private void expectGetEntityManager(AbstractRepository mockRepository) {
        EasyMock.expect(mockRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
    }

    @Test
    void testRemoveExistingHearingsForCourtAndDateFlow() throws Exception {
        // Prepare mocks for repositories and repository helper
        RepositoryHelper mockRepoHelper = EasyMock.createMock(RepositoryHelper.class);
        uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository mockHearingListRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository.class);
        uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository mockSittingRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository.class);
        uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository mockScheduledRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing
                .XhbScheduledHearingRepository.class);
        uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository mockDefRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant
                .XhbSchedHearingDefendantRepository.class);
        uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeRepository mockAttRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee
                .XhbSchedHearingAttendeeRepository.class);
        uk.gov.hmcts.pdda.business.entities.xhbshjudge.XhbShJudgeRepository mockShJudgeRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbshjudge.XhbShJudgeRepository.class);
        uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository mockCrLiveRepo =
            EasyMock.createMock(uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository.class);

        // Wire repository helper getters
        EasyMock.expect(mockRepoHelper.getXhbHearingListRepository()).andReturn(mockHearingListRepo).anyTimes();
        EasyMock.expect(mockRepoHelper.getXhbSittingRepository()).andReturn(mockSittingRepo).anyTimes();
        EasyMock.expect(mockRepoHelper.getXhbScheduledHearingRepository()).andReturn(mockScheduledRepo).anyTimes();
        EasyMock.expect(mockRepoHelper.getXhbSchedHearingDefendantRepository()).andReturn(mockDefRepo).anyTimes();
        EasyMock.expect(mockRepoHelper.getXhbSchedHearingAttendeeRepository()).andReturn(mockAttRepo).anyTimes();
        EasyMock.expect(mockRepoHelper.getXhbShJudgeRepository()).andReturn(mockShJudgeRepo).anyTimes();
        EasyMock.expect(mockRepoHelper.getXhbCrLiveDisplayRepository()).andReturn(mockCrLiveRepo).anyTimes();

        // Prepare DAO objects
        uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao hearingList =
            new uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao();
        hearingList.setListId(1000);
        List<uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao> hearingLists = List.of(hearingList);

        // Expect hearing list lookup
        EasyMock.expect(mockHearingListRepo.findByCourtIdAndDateSafe(EasyMock.eq(1), EasyMock.isA(LocalDateTime.class)))
            .andReturn(hearingLists);

        // Sitting
        uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao sitting =
            new uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao();
        sitting.setSittingId(2000);
        sitting.setListId(1000);
        List<uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao> sittings = List.of(sitting);
        EasyMock.expect(mockSittingRepo.findByListIdSafe(EasyMock.eq(1000))).andReturn(sittings);

        // Scheduled hearings
        uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao sch =
            new uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao();
        sch.setScheduledHearingId(3000);
        sch.setSittingId(2000);
        List<uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao> schs = List.of(sch);
        EasyMock.expect(mockScheduledRepo.findBySittingIdsSafe(EasyMock.eq(List.of(2000)))).andReturn(schs);

        // Defendants: return one then expect delete
        uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao def =
            new uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao();
        def.setSchedHearingDefendantId(4000);
        List<uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao> defs =
            List.of(def);
        EasyMock.expect(mockDefRepo.findByScheduledHearingIdsSafe(EasyMock.eq(List.of(3000)))).andReturn(defs);
        mockDefRepo.deleteByScheduledHearingIds(EasyMock.eq(List.of(3000)));
        EasyMock.expectLastCall();

        // Attendees: return one then expect sh judge delete and bulk delete
        uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao att =
            new uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao();
        att.setShAttendeeId(5000);
        List<uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao> atts =
            List.of(att);
        EasyMock.expect(mockAttRepo.findByScheduledHearingIdSafe(EasyMock.eq(3000))).andReturn(atts);
        mockShJudgeRepo.deleteByShAttendeeId(EasyMock.eq(5000));
        EasyMock.expectLastCall();
        mockAttRepo.deleteByScheduledHearingIds(EasyMock.eq(List.of(3000)));
        EasyMock.expectLastCall();

        // Live display update
        mockCrLiveRepo.updateScheduledHearingIdToNull(EasyMock.eq(3000));
        EasyMock.expectLastCall();

        // Delete scheduled hearings by sitting ids
        mockScheduledRepo.deleteBySittingIds(EasyMock.eq(List.of(2000)));
        EasyMock.expectLastCall();

        // Delete sittings by list id
        mockSittingRepo.deleteByListId(EasyMock.eq(1000));
        EasyMock.expectLastCall();

        // Delete hearing list
        mockHearingListRepo.deleteById(EasyMock.anyInt());
        EasyMock.expectLastCall();

        // Replay all mocks
        EasyMock.replay(mockRepoHelper, mockHearingListRepo, mockSittingRepo, mockScheduledRepo,
            mockDefRepo, mockAttRepo, mockShJudgeRepo, mockCrLiveRepo);

        // Inject mockRepoHelper into classUnderTest via reflection
        java.lang.reflect.Field repoField = classUnderTest.getClass().getDeclaredField("repositoryHelper");
        repoField.setAccessible(true);
        repoField.set(classUnderTest, mockRepoHelper);

        // Call private method via reflection
        java.lang.reflect.Method m = classUnderTest.getClass()
            .getDeclaredMethod("removeExistingHearingsForCourtAndDate",
            Integer.class, LocalDate.class);
        m.setAccessible(true);
        m.invoke(classUnderTest, 1, LocalDate.of(2020, 1, 21));

        // Verify
        EasyMock.verify(mockRepoHelper, mockHearingListRepo, mockSittingRepo, mockScheduledRepo,
            mockDefRepo, mockAttRepo, mockShJudgeRepo, mockCrLiveRepo);
    }

}
