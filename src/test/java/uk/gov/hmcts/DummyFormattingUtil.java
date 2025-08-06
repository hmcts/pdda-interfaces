package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.fail;

public final class DummyFormattingUtil {
    
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";
    
    private DummyFormattingUtil() {
        // Do nothing
    }

    public static XhbCppFormattingDao getXhbCppFormattingDao() {
        Integer stagingTableId = DummyServicesUtil.getRandomNumber();
        LocalDateTime dateIn = LocalDateTime.now();
        String formatStatus = CppFormattingHelper.FORMAT_STATUS_NOT_PROCESSED;
        String documentType = "PD";
        Integer courtId = DummyServicesUtil.getDummyId();
        Long xmlDocumentClobId = Long.valueOf(DummyServicesUtil.getRandomNumber());
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 2;
        String obsInd = "N";

        XhbCppFormattingDao xcf = new XhbCppFormattingDao();
        xcf.setCppFormattingId(DummyServicesUtil.getDummyId());
        xcf.setStagingTableId(stagingTableId);
        xcf.setDateIn(dateIn);
        xcf.setFormatStatus(formatStatus);
        xcf.setDocumentType(documentType);
        xcf.setCourtId(courtId);
        xcf.setXmlDocumentClobId(xmlDocumentClobId);
        xcf.setErrorMessage(errorMessage);
        xcf.setObsInd(obsInd);
        xcf.setLastUpdateDate(lastUpdateDate);
        xcf.setCreationDate(creationDate);
        xcf.setLastUpdatedBy(lastUpdatedBy);
        xcf.setCreatedBy(createdBy);
        xcf.setVersion(version);
        return new XhbCppFormattingDao(xcf);
    }

    public static XhbFormattingDao getXhbFormattingDao() {
        Integer formattingId = DummyServicesUtil.getDummyId();
        LocalDateTime dateIn = LocalDateTime.now();
        String formatStatus = null;
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
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
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

    public static XhbClobDao getXhbClobDao(Long clobId, String clobData) {
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 2;

        XhbClobDao xc =
            new XhbClobDao(clobId, clobData, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        return new XhbClobDao(xc);
    }
    
    public static XhbBlobDao getXhbBlobDao(byte[] blobData) {
        Long blobId = Long.valueOf(4);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 2;
        XhbBlobDao result =
            new XhbBlobDao(blobId, blobData, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        return new XhbBlobDao(result);
    }
    
    public static XhbCppListDao getXhbCppListDao() {
        Integer cppListId = Double.valueOf(Math.random()).intValue();
        Integer courtCode = 453;
        String listType = "D";
        LocalDateTime timeLoaded = LocalDateTime.now();
        LocalDateTime listStartDate = LocalDateTime.now().minusMinutes(5);
        LocalDateTime listEndDate = LocalDateTime.now();
        Long listClobId = Double.valueOf(Math.random()).longValue();
        Long mergedClobId = Double.valueOf(Math.random()).longValue();
        String status = "MS";
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";

        XhbCppListDao result = new XhbCppListDao();
        result.setCppListId(cppListId);
        result.setCourtCode(courtCode);
        result.setListType(listType);
        result.setTimeLoaded(timeLoaded);
        result.setListStartDate(listStartDate);
        result.setListEndDate(listEndDate);
        result.setListClobId(listClobId);
        result.setMergedClobId(mergedClobId);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbCppListDao(result);
    }
    
    public static XhbXmlDocumentDao getXhbXmlDocumentDao() {
        Integer xmlDocumentId = Double.valueOf(Math.random()).intValue();
        LocalDateTime dateCreated = LocalDateTime.now();
        String documentTitle = "";
        Long xmlDocumentClobId = null;
        String status = "";
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);
        String documentType = "";
        Integer courtId = 81;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;

        XhbXmlDocumentDao result = new XhbXmlDocumentDao();
        result.setXmlDocumentId(xmlDocumentId);
        result.setDateCreated(dateCreated);
        result.setDocumentTitle(documentTitle);
        result.setXmlDocumentClobId(xmlDocumentClobId);
        result.setStatus(status);
        result.setExpiryDate(expiryDate);
        result.setDocumentType(documentType);
        result.setCourtId(courtId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbXmlDocumentDao(result);
    }

    public static FormattingValue getFormattingValue(String xml, String documentTypeIn, String mimeTypeIn,
        XhbCppListDao cppList) {
        String distributionTypeIn = "distributionTypeIn";
        Integer majorVersion = 1;
        Integer minorVersion = 1;
        String language = "language";
        String country = "country";
        try (Reader readerIn = new StringReader(xml)) {
            OutputStream outputStreamIn = new ByteArrayOutputStream(1024);
            Integer courtId = 81;

            FormattingValue result = new FormattingValue(distributionTypeIn, mimeTypeIn, documentTypeIn, majorVersion,
                minorVersion, language, country, readerIn, outputStreamIn, courtId, cppList);
            result.setFormattingId(DummyServicesUtil.getRandomNumber());
            return result;
        } catch (IOException exception) {
            fail(exception);
            return null;
        }
    }
}
