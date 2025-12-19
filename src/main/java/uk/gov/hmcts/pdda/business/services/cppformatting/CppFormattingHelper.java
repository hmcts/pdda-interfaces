package uk.gov.hmcts.pdda.business.services.cppformatting;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**

 * Title: CppFormattingHelper.


 * Description: Helper class for CppFormatting


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris
 * @version 1.0
 */
public class CppFormattingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CppFormattingHelper.class);

    private static final String DOC_TYPE_PUBLIC_DISPLAY = "PD";

    public static final String FORMAT_STATUS_SUCCESS = "MS";

    public static final String FORMAT_STATUS_FAIL = "MF";

    public static final String FORMAT_STATUS_NOT_PROCESSED = "ND";

    /**
     * Description: Returns the latest unprocessed XHB_CPP_FORMATTING record for Public Display.

     * @param courtId Integer
     * @return XhbCppFormattingDAO
     * @throws CppFormattingControllerException Exception
     */
    public XhbCppFormattingDao getLatestPublicDisplayDocument(final Integer courtId,
        final EntityManager entityManager) {
        String methodName = "getLatestPublicDisplayDocument(" + courtId + ")";
        LOG.debug(methodName + " called");

        XhbCppFormattingRepository repo = new XhbCppFormattingRepository(entityManager);

        // Make the time element midnight, as otherwise the query will not work and never find
        // anything
        return repo.getLatestDocumentByCourtIdAndTypeSafe(courtId, DOC_TYPE_PUBLIC_DISPLAY,
            LocalDateTime.now().with(LocalTime.MIDNIGHT));
    }

    public static XhbFormattingDao createXhbFormattingRecord(Integer courtId,
        XhbCppStagingInboundDao xhbCppStagingInboundDao, String documentType, String language) {
        XhbFormattingDao xfbv = new XhbFormattingDao();
        xfbv.setCourtId(courtId);
        xfbv.setDateIn(xhbCppStagingInboundDao.getTimeLoaded());
        xfbv.setDistributionType("FTP");
        xfbv.setDocumentType(documentType);
        xfbv.setFormatStatus(FORMAT_STATUS_NOT_PROCESSED);
        xfbv.setMimeType("HTM");
        xfbv.setXmlDocumentClobId(xhbCppStagingInboundDao.getClobId());
        xfbv.setCountry("GB");
        xfbv.setLanguage(language);
        return xfbv;
    }

    public static XhbXmlDocumentDao createXhbXmlDDocumentRecord(XhbFormattingDao xhbFormattingDao,
        XhbCppListDao xhbCppListDao, XhbCppStagingInboundDao xhbCppStagingInboundDao) {
        XhbXmlDocumentDao xhbXmlDocumentDao = new XhbXmlDocumentDao();
        xhbXmlDocumentDao.setDateCreated(xhbFormattingDao.getDateIn());
        xhbXmlDocumentDao.setDocumentTitle(xhbCppStagingInboundDao.getDocumentName());
        xhbXmlDocumentDao.setXmlDocumentClobId(xhbFormattingDao.getXmlDocumentClobId());
        xhbXmlDocumentDao.setStatus(xhbFormattingDao.getFormatStatus());
        xhbXmlDocumentDao.setDocumentType(xhbFormattingDao.getDocumentType());
        // This field is null on production Xhibit so setting null here too.
        xhbXmlDocumentDao.setExpiryDate(null);
        xhbXmlDocumentDao.setCourtId(xhbFormattingDao.getCourtId());
        return xhbXmlDocumentDao;
    }
    
    public static XhbXmlDocumentDao createWebPageXhbXmlDDocumentRecord(XhbCppFormattingDao xhbCppFormattingDao,
        XhbCppStagingInboundDao xhbCppStagingInboundDao, String language) {
        // Create title
        String documentTitle = xhbCppStagingInboundDao.getDocumentName().replace(".xml", "");
        // Create the XhbXmlDocument record
        XhbXmlDocumentDao xhbXmlDocumentDao = new XhbXmlDocumentDao();
        xhbXmlDocumentDao.setDateCreated(xhbCppFormattingDao.getDateIn());
        xhbXmlDocumentDao.setDocumentTitle(documentTitle + language);
        xhbXmlDocumentDao.setXmlDocumentClobId(xhbCppFormattingDao.getXmlDocumentClobId());
        xhbXmlDocumentDao.setStatus(xhbCppFormattingDao.getFormatStatus());
        xhbXmlDocumentDao.setDocumentType(xhbCppFormattingDao.getDocumentType());
        xhbXmlDocumentDao.setExpiryDate(null);
        xhbXmlDocumentDao.setCourtId(xhbCppFormattingDao.getCourtId());
        return xhbXmlDocumentDao;
    }
}
