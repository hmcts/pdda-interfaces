package uk.gov.hmcts.pdda.business.services.cppformatting;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;

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
    
    public static XhbFormattingDao createXhbFormattingRecord(Integer courtId, LocalDateTime dateIn,
        String documentType, String language) {
        XhbFormattingDao xfbv = new XhbFormattingDao();
        xfbv.setCourtId(courtId);
        xfbv.setDateIn(dateIn);
        xfbv.setDistributionType("FTP");
        xfbv.setDocumentType(documentType);
        xfbv.setFormatStatus(FORMAT_STATUS_NOT_PROCESSED);
        xfbv.setMimeType("HTM");
        xfbv.setXmlDocumentClobId(Long.valueOf(0));
        xfbv.setCountry("GB");
        xfbv.setLanguage(language);
        return xfbv;
    }
}
