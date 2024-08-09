package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.services.validation.ValidationResult;
import uk.gov.hmcts.pdda.business.services.validation.ValidationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * CppStagingInboundControllerBeanUpdateTest.
 */
@ExtendWith(EasyMockExtension.class)
class CppStagingInboundControllerBeanUpdateTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String USERDISPLAYNAME = "";

    @Mock
    private static EntityManager mockEntityManager;

    @Mock
    private CppStagingInboundHelper mockCppStagingInboundHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbConfigPropDao mockXhbConfigPropDao;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;
    
    @Mock
    private XhbBlobRepository mockXhbBlobRepository;

    @Mock
    private ValidationResult mockValidationResult;

    @Mock
    private ValidationService mockValidationService;

    @TestSubject
    private final CppStagingInboundControllerBean classUnderTest =
        new CppStagingInboundControllerBean(mockEntityManager, mockXhbConfigPropRepository, mockCppStagingInboundHelper,
            mockXhbCourtRepository, mockXhbClobRepository, mockXhbBlobRepository, mockValidationService);

    @BeforeEach
    public void setUp() throws Exception {
        // Do nothing
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testUpdateStatusFailed() {
        // Setup
        StringBuilder oversizedStringBuilder = new StringBuilder(1024);
        for (int i = 0; i < 100; i++) {
            oversizedStringBuilder.append("TestTestTestTestTestTestTestTestTestTest");
        }
        oversizedStringBuilder.append('.');
        String reasonForFail = oversizedStringBuilder.toString();

        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_FAIL);
        dao.setValidationErrorMessage(reasonForFail);
        dao.setLastUpdatedBy(USERDISPLAYNAME);
        Optional<XhbCppStagingInboundDao> optionalOfXhbCppStagingInboundDao =
            Optional.of(new XhbCppStagingInboundDao());
        EasyMock.expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME))
            .andReturn(optionalOfXhbCppStagingInboundDao);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateStatusFailed(dao, reasonForFail, USERDISPLAYNAME);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateStatusFailedFail() {
        Assertions.assertThrows(EJBException.class, () -> {
            // Setup
            StringBuilder oversizedStringBuilder = new StringBuilder(1024);
            for (int i = 0; i < 100; i++) {
                oversizedStringBuilder.append("TestTestTestTestTestTestTestTestTestTest");
            }
            oversizedStringBuilder.append('.');
            String reasonForFail = oversizedStringBuilder.toString();

            XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            dao.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_FAIL);
            dao.setValidationErrorMessage(reasonForFail);
            dao.setLastUpdatedBy(USERDISPLAYNAME);
            mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME);
            EasyMock.expectLastCall().andThrow(new EJBException());
            EasyMock.replay(mockCppStagingInboundHelper);
            // Run
            classUnderTest.updateStatusFailed(dao, reasonForFail, USERDISPLAYNAME);
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }

    @Test
    void testUpdateStatusInProcess() {
        // Setup
        Optional<XhbCppStagingInboundDao> optionalOfXhbCppStagingInboundDao =
            Optional.of(new XhbCppStagingInboundDao());
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_INPROCESS);
        dao.setLastUpdatedBy(USERDISPLAYNAME);
        EasyMock.expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME))
            .andReturn(optionalOfXhbCppStagingInboundDao);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        optionalOfXhbCppStagingInboundDao = classUnderTest.updateStatusInProcess(dao, USERDISPLAYNAME);
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertNotNull(optionalOfXhbCppStagingInboundDao, NOTNULL);
    }

    @Test
    void testUpdateStatusInProcessFail() {
        Assertions.assertThrows(EJBException.class, () -> {
            // Setup
            XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            dao.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_INPROCESS);
            dao.setLastUpdatedBy(USERDISPLAYNAME);
            mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME);
            EasyMock.expectLastCall().andThrow(new EJBException());
            EasyMock.replay(mockCppStagingInboundHelper);
            // Run
            classUnderTest.updateStatusInProcess(dao, USERDISPLAYNAME);
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }

    @Test
    void testUpdateStatusProcessingFail() {
        // Setup
        StringBuilder oversizedStringBuilder = new StringBuilder(1024);
        for (int i = 0; i < 100; i++) {
            oversizedStringBuilder.append("TestTestTestTestTestTestTestTestTestTest");
        }
        oversizedStringBuilder.append('.');
        String reasonForFail = oversizedStringBuilder.toString();

        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_FAIL);
        dao.setValidationErrorMessage(reasonForFail);
        dao.setLastUpdatedBy(USERDISPLAYNAME);
        Optional<XhbCppStagingInboundDao> optionalOfXhbCppStagingInboundDao =
            Optional.of(new XhbCppStagingInboundDao());
        EasyMock.expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME))
            .andReturn(optionalOfXhbCppStagingInboundDao);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateStatusProcessingFail(dao, reasonForFail, USERDISPLAYNAME);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateStatusProcessingSuccess() {
        // Setup
        Optional<XhbCppStagingInboundDao> optionalOfXhbCppStagingInboundDao =
            Optional.of(new XhbCppStagingInboundDao());
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_SENT);
        dao.setLastUpdatedBy(USERDISPLAYNAME);
        EasyMock.expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME))
            .andReturn(optionalOfXhbCppStagingInboundDao);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateStatusProcessingSuccess(dao, USERDISPLAYNAME);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateStatusProcessingSuccessFail() {
        Assertions.assertThrows(EJBException.class, () -> {
            // Setup
            XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            dao.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_SENT);
            dao.setLastUpdatedBy(USERDISPLAYNAME);
            mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME);
            EasyMock.expectLastCall().andThrow(new EJBException());
            EasyMock.replay(mockCppStagingInboundHelper);
            // Run
            classUnderTest.updateStatusProcessingSuccess(dao, USERDISPLAYNAME);
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }

    @Test
    void testUpdateStatusSuccess() {
        // Setup
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED);
        dao.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS);
        dao.setLastUpdatedBy(USERDISPLAYNAME);
        Optional<XhbCppStagingInboundDao> optionalOfXhbCppStagingInboundDao =
            Optional.of(new XhbCppStagingInboundDao());
        EasyMock.expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME))
            .andReturn(optionalOfXhbCppStagingInboundDao);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateStatusSuccess(dao, USERDISPLAYNAME);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateStatusSuccessFail() {
        Assertions.assertThrows(EJBException.class, () -> {
            // Setup
            XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            dao.setProcessingStatus(CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED);
            dao.setValidationStatus(CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS);
            dao.setLastUpdatedBy(USERDISPLAYNAME);
            mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME);
            EasyMock.expectLastCall().andThrow(new EJBException());
            EasyMock.replay(mockCppStagingInboundHelper);
            // Run
            classUnderTest.updateStatusSuccess(dao, USERDISPLAYNAME);
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }
}
