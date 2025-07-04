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
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.services.validation.ValidationException;
import uk.gov.hmcts.pdda.business.services.validation.ValidationResult;
import uk.gov.hmcts.pdda.business.services.validation.ValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * CppStagingInboundControllerBeanValidateTest.
 */
@SuppressWarnings({"PMD"})
@ExtendWith(EasyMockExtension.class)
class CppStagingInboundControllerBeanValidateTest {

    private static final String EMPTY_STRING = "";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String USERDISPLAYNAME = "";
    private static final String VALIDFILENAME = "PublicDisplay_457_20230515154330.xml";

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
    private ValidationResult mockValidationResult;

    @Mock
    private ValidationService mockValidationService;

    @TestSubject
    private final CppStagingInboundControllerBean classUnderTest =
        new CppStagingInboundControllerBean(mockEntityManager, mockXhbConfigPropRepository,
            mockCppStagingInboundHelper, mockXhbCourtRepository, mockXhbClobRepository,
            EasyMock.createMock(XhbBlobRepository.class), mockValidationService);

    @BeforeEach
    public void setUp() {
        // Do nothing
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }


    @Test
    void testValidateDocumentEjbException() throws ValidationException {
        Assertions.assertThrows(EJBException.class, () -> {
            // Setup
            XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            dao.setDocumentName(VALIDFILENAME);

            EasyMock.expect(mockXhbConfigPropRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
            EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
            
            testingXhbConfigPropRepository();

            testingClobRepository(dao);

            EasyMock.expect(mockValidationService.validate(EasyMock.isA(String.class),
                EasyMock.isA(String.class))).andReturn(mockValidationResult);

            mockValidationResult.isValid();

            EasyMock.expectLastCall().andThrow(new EJBException());

            EasyMock.replay(mockXhbConfigPropRepository);
            EasyMock.replay(mockXhbClobRepository);
            EasyMock.replay(mockValidationService);
            EasyMock.replay(mockValidationResult);
            EasyMock.replay(mockEntityManager);
            // Run
            final boolean validDocument = classUnderTest.validateDocument(dao, USERDISPLAYNAME);
            // Checks
            EasyMock.verify(mockXhbConfigPropRepository);
            EasyMock.verify(mockXhbClobRepository);
            EasyMock.verify(mockValidationService);
            EasyMock.verify(mockValidationResult);
            EasyMock.verify(mockEntityManager);
            assertFalse(validDocument, FALSE);
        });
    }

    @Test
    void testValidateDocumentInvalidDocument() throws ValidationException {
        // Setup
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setDocumentName(VALIDFILENAME);

        EasyMock.expect(mockXhbConfigPropRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        testingXhbConfigPropRepository();

        testingClobRepository(dao);

        EasyMock.expect(
            mockValidationService.validate(EasyMock.isA(String.class), EasyMock.isA(String.class)))
            .andReturn(mockValidationResult);

        EasyMock.expect(mockValidationResult.isValid()).andReturn(false).anyTimes();

        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockValidationService);
        EasyMock.replay(mockValidationResult);
        EasyMock.replay(mockEntityManager);
        // Run
        final boolean validDocument = classUnderTest.validateDocument(dao, USERDISPLAYNAME);
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        EasyMock.verify(mockXhbClobRepository);
        EasyMock.verify(mockValidationService);
        EasyMock.verify(mockValidationResult);
        EasyMock.verify(mockEntityManager);
        assertFalse(validDocument, FALSE);
    }

    @Test
    void testValidateDocumentInvalidName() throws ValidationException {
        // Setup
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setDocumentName("INVALIDNAME.txt");

        EasyMock.expect(mockXhbConfigPropRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        testingXhbConfigPropRepository();

        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockEntityManager);
        // Run
        boolean invalidDocument = classUnderTest.validateDocument(dao, USERDISPLAYNAME);
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        EasyMock.verify(mockEntityManager);
        assertFalse(invalidDocument, FALSE);
    }

    @Test
    void testValidateDocumentInvalidType() throws ValidationException {
        // Setup
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setDocumentName(VALIDFILENAME);
        dao.setDocumentType("INVALID");

        EasyMock.expect(mockXhbConfigPropRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        testingXhbConfigPropRepository();

        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockEntityManager);
        // Run
        boolean invalidDocument = classUnderTest.validateDocument(dao, USERDISPLAYNAME);
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        EasyMock.verify(mockEntityManager);
        assertFalse(invalidDocument, FALSE);
    }

    @Test
    void testValidateDocumentSuccess() throws ValidationException {
        // Setup
        List<XhbCourtDao> courts = new ArrayList<>();
        courts.add(DummyCourtUtil.getXhbCourtDao(1, EMPTY_STRING));
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setDocumentName(VALIDFILENAME);
        dao.setDocumentType("PD");

        EasyMock.expect(mockXhbConfigPropRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        testingXhbConfigPropRepository();

        testingClobRepository(dao);

        EasyMock.expect(
            mockValidationService.validate(EasyMock.isA(String.class), EasyMock.isA(String.class)))
            .andReturn(mockValidationResult);
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andReturn(courts);
        EasyMock.expect(mockValidationResult.isValid()).andReturn(true).anyTimes();

        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockValidationService);
        EasyMock.replay(mockValidationResult);
        EasyMock.replay(mockEntityManager);
        // Run
        final boolean validDocument = classUnderTest.validateDocument(dao, USERDISPLAYNAME);
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        EasyMock.verify(mockXhbClobRepository);
        EasyMock.verify(mockValidationService);
        EasyMock.verify(mockValidationResult);
        EasyMock.verify(mockEntityManager);
        assertTrue(validDocument, TRUE);
    }

    @Test
    void testGetNextValidatedDocument() {
        // Setup
        List<XhbCppStagingInboundDao> doc = new ArrayList<>();
        EasyMock.expect(mockCppStagingInboundHelper.findNextDocumentByStatus(
            CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS,
            CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED)).andReturn(doc);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        try {
            classUnderTest.getNextValidatedDocument();
        } catch (CppStagingInboundControllerException exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
    }

    @Test
    void testGetNextValidatedDocumentFail() throws Exception {
        // Setup
        mockCppStagingInboundHelper.findNextDocumentByStatus(
            CppStagingInboundHelper.VALIDATION_STATUS_SUCCESS,
            CppStagingInboundHelper.PROCESSING_STATUS_NOTPROCESSED);
        EasyMock.expectLastCall().andThrow(new CppStagingInboundControllerException());
        EasyMock.replay(mockCppStagingInboundHelper);
        Assertions.assertThrows(CppStagingInboundControllerException.class, () -> {
            // Run
            classUnderTest.getNextValidatedDocument();
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }

    private void testingXhbConfigPropRepository() {
        String documentType = "PD";
        List<XhbConfigPropDao> returnList = new ArrayList<>();
        returnList
            .add(DummyServicesUtil.getXhbConfigPropDao("CPPX_Schema" + documentType, EMPTY_STRING));
        EasyMock
            .expect(mockXhbConfigPropRepository.findByPropertyNameSafe(EasyMock.isA(String.class)))
            .andReturn(returnList);
    }

    private void testingClobRepository(XhbCppStagingInboundDao dao) {
        XhbClobDao clobObj = new XhbClobDao();
        clobObj.setClobData("Demo Data");
        EasyMock.expect(mockXhbClobRepository.findByIdSafe(dao.getClobId()))
            .andReturn(Optional.of(clobObj));
    }
}
