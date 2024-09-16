package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: Formatting Controller Bean Test.
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
class FormattingControllerBeanTest {

    private static final String TRUE = "Result is not True";
    private static final String XML_TAG = "<xml/>";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private FormattingServices mockFormattingServices;

    @Mock
    private XhbCppListRepository mockXhbCppListRepository;

    @TestSubject
    private final FormattingControllerBean classUnderTest = new FormattingControllerBean(mockEntityManager);

    @BeforeAll
    public static void setUp() throws Exception {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testDoTask() {
        // Setup
        final XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), XML_TAG);
        final XhbFormattingDao formattingDao = getDummyXhbFormattingDao();
        formattingDao.setXmlDocumentClobId(xhbClobDao.getClobId());
        final FormattingValue formattingValue = getDummyFormattingValue(formattingDao);
        final boolean success = true;
        EasyMock.expect(mockFormattingServices.getNextFormattingDocument()).andReturn(formattingDao);
        EasyMock.expect(mockFormattingServices.getClob(EasyMock.isA(Long.class))).andReturn(Optional.of(xhbClobDao));
        mockFormattingServices.processDocument(EasyMock.isA(FormattingValue.class), EasyMock.isA(EntityManager.class));
        mockFormattingServices.updateFormattingStatus(formattingDao.getFormattingId(), success);
        EasyMock.expect(mockFormattingServices.getFormattingValue(EasyMock.isA(XhbFormattingDao.class),
            EasyMock.isA(Reader.class), EasyMock.isA(OutputStream.class))).andReturn(formattingValue);
        EasyMock.replay(mockFormattingServices);

        // Run
        boolean result = false;
        try {
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockFormattingServices);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessFormattingDocumentNoArgs() {
        // Setup
        EasyMock.expect(mockFormattingServices.getNextFormattingDocument()).andReturn(null);
        EasyMock.replay(mockFormattingServices);

        // Run
        boolean result = false;
        try {
            classUnderTest.processFormattingDocument();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockFormattingServices);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessFormattingDocument() {
        // Setup
        final XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), XML_TAG);
        final XhbFormattingDao formattingDao = getDummyXhbFormattingDao();
        formattingDao.setXmlDocumentClobId(xhbClobDao.getClobId());
        final FormattingValue formattingValue = getDummyFormattingValue(formattingDao);
        EasyMock.expect(mockFormattingServices.getClob(EasyMock.isA(Long.class))).andReturn(Optional.of(xhbClobDao));
        mockFormattingServices.processDocument(EasyMock.isA(FormattingValue.class), EasyMock.isA(EntityManager.class));
        EasyMock.expect(mockFormattingServices.getFormattingValue(EasyMock.isA(XhbFormattingDao.class),
            EasyMock.isA(Reader.class), EasyMock.isA(OutputStream.class))).andReturn(formattingValue);
        EasyMock.replay(mockFormattingServices);

        // Run
        boolean result = false;
        try {
            classUnderTest.processFormattingDocument(formattingDao);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockFormattingServices);
        assertTrue(result, TRUE);
    }

    @Test
    void testupdateFormattingDocumentStatus() {
        // Setup
        final Integer docId = 1;
        final boolean success = true;
        mockFormattingServices.updateFormattingStatus(docId, success);
        EasyMock.replay(mockFormattingServices);

        // Run
        boolean result = false;
        try {
            classUnderTest.updateFormattingDocumentStatus(docId, success);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockFormattingServices);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateCppFormatting() {
        // Setup
        final Integer cppFormattingId = 1;
        final String errorMessage = "Error Message";
        mockFormattingServices.updateCppFormatting(cppFormattingId, "MF", errorMessage);
        EasyMock.replay(mockFormattingServices);

        // Run
        boolean result = false;
        try {
            classUnderTest.updateCppFormatting(cppFormattingId, errorMessage);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockFormattingServices);
        assertTrue(result, TRUE);
    }

    @Test
    void testFormatDocument() {
        // Setup
        final String distributionTypeIn = "DA";
        final String mimeTypeIn = "PDF";
        final String documentTypeIn = "DL";
        final Integer majorVersion = 1;
        final String language = "en";
        final String country = "GB";
        final String xml = XML_TAG;
        final Integer courtId = 95;
        final LocalDateTime listStartDate = LocalDateTime.now();
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), XML_TAG);
        XhbCppListDao xhbCppListDao = getDummyCppList(xhbClobDao);
        List<XhbCppListDao> dummyList = new ArrayList<>();
        dummyList.add(xhbCppListDao);

        EasyMock.expect(mockXhbCppListRepository.findByCourtCodeAndListTypeAndListDate(EasyMock.isA(Integer.class),
            EasyMock.isA(String.class), EasyMock.isA(LocalDateTime.class))).andReturn(dummyList);
        mockFormattingServices.processDocument(EasyMock.isA(FormattingValue.class), EasyMock.isA(EntityManager.class));

        EasyMock.replay(mockXhbCppListRepository);
        EasyMock.replay(mockFormattingServices);

        // Run
        FormattingValue formattingValue = new FormattingValue(distributionTypeIn, mimeTypeIn, documentTypeIn,
            majorVersion, majorVersion, language, country, null, new ByteArrayOutputStream(1024), courtId, null);
        @SuppressWarnings("unused")
        boolean result = false;
        try {
            byte[] byteArray = classUnderTest.formatDocument(formattingValue, listStartDate, xml);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockFormattingServices);
        EasyMock.verify(mockXhbCppListRepository);
        assertTrue(result, TRUE);
    }

    private XhbCppListDao getDummyCppList(final XhbClobDao listClob) {
        Integer cppListId = 1;
        Integer courtCode = 81;
        LocalDateTime timeLoaded = null;
        LocalDateTime listStartDate = LocalDateTime.now().minusDays(5);
        LocalDateTime listEndDate = LocalDateTime.now().plusDays(1);
        Long mergedClobId = null;
        String status = null;
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        String listType = "DL";
        XhbCppListDao result = new XhbCppListDao();
        result.setCppListId(cppListId);
        result.setCourtCode(courtCode);
        result.setListType(listType);
        result.setTimeLoaded(timeLoaded);
        result.setListStartDate(listStartDate);
        result.setListEndDate(listEndDate);
        result.setListClobId(listClob.getClobId());
        result.setMergedClobId(mergedClobId);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        result = new XhbCppListDao(result);
        return result;
    }

    private XhbFormattingDao getDummyXhbFormattingDao() {
        Integer formattingId = getRandomInt();
        LocalDateTime dateIn = LocalDateTime.now();
        String formatStatus = "ND";
        String distributionType = "distributionType";
        String mimType = "pdf";
        String documentType = "IWP";
        Integer courtId = 81;
        Long formattedDocumentBlobId = null;
        Long xmlDocumentClobId = null;
        String language = "en";
        String country = "gb";
        Integer majorSchemaVersion = 1;
        Integer minorSchemaVersion = 1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;

        XhbFormattingDao result = new XhbFormattingDao();
        result.setFormattingId(formattingId);
        result.setDateIn(dateIn);
        result.setFormatStatus(formatStatus);
        result.setDistributionType(distributionType);
        result.setMimeType(mimType);
        result.setDocumentType(documentType);
        result.setCourtId(courtId);
        result.setFormattedDocumentBlobId(formattedDocumentBlobId);
        result.setXmlDocumentClobId(xmlDocumentClobId);
        result.setLanguage(language);
        result.setCountry(country);
        result.setMajorSchemaVersion(majorSchemaVersion);
        result.setMinorSchemaVersion(minorSchemaVersion);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbFormattingDao(result);
    }

    private FormattingValue getDummyFormattingValue(final XhbFormattingDao formattingDao) {
        OutputStream outputStream = new ByteArrayOutputStream();
        FormattingValue result = new FormattingValue(formattingDao.getDistributionType(), formattingDao.getMimeType(),
            formattingDao.getDocumentType(), formattingDao.getMajorSchemaVersion(),
            formattingDao.getMinorSchemaVersion(), formattingDao.getLanguage(), formattingDao.getCountry(), null,
            outputStream, formattingDao.getCourtId(), null);
        result.setXmlDocumentClobId(formattingDao.getXmlDocumentClobId());
        result.setFormattingId(formattingDao.getFormattingId());
        return result;
    }

    private Integer getRandomInt() {
        return Double.valueOf(Math.random()).intValue();
    }
}
