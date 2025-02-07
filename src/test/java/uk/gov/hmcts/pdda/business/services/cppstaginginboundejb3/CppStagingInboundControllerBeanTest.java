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
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.services.validation.ValidationResult;
import uk.gov.hmcts.pdda.business.services.validation.ValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CppStagingInboundControllerBeanTest Test.
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
@SuppressWarnings("PMD.TooManyMethods")
class CppStagingInboundControllerBeanTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String NOT_INSTANCE = "Result is Not An Instance of";
    private static final String EMPTY_STRING = "";
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
    private ValidationResult mockValidationResult;

    @TestSubject
    private CppStagingInboundControllerBean classUnderTest = new CppStagingInboundControllerBean(
        mockEntityManager, mockXhbConfigPropRepository, mockCppStagingInboundHelper,
        mockXhbCourtRepository, EasyMock.createMock(XhbClobRepository.class),
        EasyMock.createMock(XhbBlobRepository.class),
        EasyMock.createMock(ValidationService.class));

    @BeforeEach
    public void setUp() {
        // Do nothing
    }

    @AfterEach
    public void tearDown() {
        classUnderTest = new CppStagingInboundControllerBean();
    }

    @Test
    void testGetLatestUnprocessedDocument() {
        // Setup
        List<XhbCppStagingInboundDao> toReturn = new ArrayList<>();
        EasyMock.expect(mockCppStagingInboundHelper
            .findNextDocumentByStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED, null))
            .andReturn(toReturn);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.getLatestUnprocessedDocument();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetLatestUnprocessedDocumentFail() throws Exception {
        // Setup
        mockCppStagingInboundHelper
            .findNextDocumentByStatus(CppStagingInboundHelper.VALIDATION_STATUS_NOTPROCESSED, null);
        EasyMock.expectLastCall().andThrow(new CppStagingInboundControllerException());
        EasyMock.replay(mockCppStagingInboundHelper);
        Assertions.assertThrows(CppStagingInboundControllerException.class, () -> {
            // Run
            classUnderTest.getLatestUnprocessedDocument();
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }

    @Test
    void testGetNextDocumentToValidate() {
        // Setup
        List<XhbCppStagingInboundDao> toReturn = new ArrayList<>();
        EasyMock
            .expect(mockCppStagingInboundHelper.findNextDocumentByStatus(
                CppStagingInboundHelper.VALIDATION_STATUS_INPROCESS, EMPTY_STRING))
            .andReturn(toReturn);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.getNextDocumentToValidate();
            result = true;
        } catch (CppStagingInboundControllerException exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetNextDocumentToValidateFail() throws Exception {
        // Setup
        mockCppStagingInboundHelper.findNextDocumentByStatus(
            CppStagingInboundHelper.VALIDATION_STATUS_INPROCESS, EMPTY_STRING);
        EasyMock.expectLastCall().andThrow(new CppStagingInboundControllerException());
        EasyMock.replay(mockCppStagingInboundHelper);
        Assertions.assertThrows(CppStagingInboundControllerException.class, () -> {
            // Run
            classUnderTest.getNextDocumentToValidate();
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }


    @Test
    void testResetDocumentStatus() {
        // Setup
        XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao.setDocumentName("PublicDisplay_457_20230515154330.xml");
        Optional<XhbCppStagingInboundDao> optionalOfXhbCppStagingInboundDao =
            Optional.of(new XhbCppStagingInboundDao());
        EasyMock.expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME))
            .andReturn(optionalOfXhbCppStagingInboundDao);
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.resetDocumentStatus(dao, USERDISPLAYNAME);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testResetDocumentStatusFail() {
        Assertions.assertThrows(EJBException.class, () -> {
            // Setup
            XhbCppStagingInboundDao dao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
            dao.setDocumentName("PublicDisplay_457_20230515154330.xml");
            mockCppStagingInboundHelper.updateCppStagingInbound(dao, USERDISPLAYNAME);
            EasyMock.expectLastCall().andThrow(new EJBException());
            EasyMock.replay(mockCppStagingInboundHelper);
            // Run
            classUnderTest.resetDocumentStatus(dao, USERDISPLAYNAME);
            // Checks
            EasyMock.verify(mockCppStagingInboundHelper);
        });
    }

    @Test
    void testGetSchemaName() {
        // Setup
        String documentType = "PD";
        List<XhbConfigPropDao> returnList = new ArrayList<>();
        returnList
            .add(DummyServicesUtil.getXhbConfigPropDao("CPPX_Schema" + documentType, EMPTY_STRING));
        EasyMock
            .expect(mockXhbConfigPropRepository.findByPropertyName("CPPX_Schema" + documentType))
            .andReturn(returnList);
        expectGetEntityManager(mockXhbConfigPropRepository);
        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockEntityManager);
        // Run
        String result = classUnderTest.getSchemaName(documentType);
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetCourtId() {
        // Setup
        Integer courtCode = 457;
        List<XhbCourtDao> data = new ArrayList<>();
        data.add(DummyCourtUtil.getXhbCourtDao(courtCode, "TestCourt1"));

        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(courtCode.toString()))
            .andReturn(data);
        expectGetEntityManager(mockXhbCourtRepository);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockEntityManager);
        // Run
        int returnedCourtId = classUnderTest.getCourtId(courtCode);
        // Checks
        EasyMock.verify(mockXhbCourtRepository);
        assertEquals(courtCode, returnedCourtId, EQUALS);
    }

    @Test
    void testGetCourtIdEmptyReturnValue() {
        // Setup
        Integer courtCode = 457;
        List<XhbCourtDao> data = new ArrayList<>();

        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(courtCode.toString()))
            .andReturn(data);
        expectGetEntityManager(mockXhbCourtRepository);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockEntityManager);
        // Run
        int returnedCourtId = classUnderTest.getCourtId(courtCode);
        // Checks
        EasyMock.verify(mockXhbCourtRepository);
        assertEquals(0, returnedCourtId, EQUALS);
    }
    
    @Test
    void testGetXhbCppStagingInboundRepository() {
        // Create Mock
        XhbCppStagingInboundRepository mockXhbCppStagingInboundRepository =
            EasyMock.createMock(XhbCppStagingInboundRepository.class);
        // Set Mock
        ReflectionTestUtils.setField(classUnderTest, "xhbCppStagingInboundRepository",
            mockXhbCppStagingInboundRepository);
        // Run
        assertInstanceOf(XhbCppStagingInboundRepository.class, classUnderTest.getXhbCppStagingInboundRepository(),
            NOT_INSTANCE);
    }
    
    @SuppressWarnings("rawtypes")
    private void expectGetEntityManager(AbstractRepository mockRepository) {
        EasyMock.expect(mockRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true);
    }
}