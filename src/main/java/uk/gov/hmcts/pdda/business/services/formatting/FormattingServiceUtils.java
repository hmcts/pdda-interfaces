package uk.gov.hmcts.pdda.business.services.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * FormattingServiceUtils.
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public final class FormattingServiceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FormattingServiceUtils.class);
    private static final SimpleDateFormat JAVADATEFORMAT =
        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final String DOCTYPE_DL = "DL";
    private static final String DOCTYPE_DLL = "DLL";
    private static final String DOCTYPE_DLP = "DLP";
    private static final String DOCTYPE_DLS = "DLS";
    private static final String DOCTYPE_FL = "FL";
    private static final String DOCTYPE_FLL = "FLL";
    private static final String DOCTYPE_FLS = "FLS";
    private static final String DOCTYPE_WL = "WL";
    private static final String DOCTYPE_WLL = "WLL";
    private static final String DOCTYPE_WLS = "WLS";
    private static final String DOCTYPE_ROS = "ROS";
    private static final String PDDA_ONLY = "1";

    private FormattingServiceUtils() {

    }

    public static boolean isDailyList(String documentType) {
        return DOCTYPE_DL.equals(documentType) || DOCTYPE_DLL.equals(documentType)
            || DOCTYPE_DLP.equals(documentType);
    }

    public static boolean isFirmList(String documentType) {
        return DOCTYPE_FL.equals(documentType) || DOCTYPE_FLL.equals(documentType);
    }

    public static boolean isWarnedList(String documentType) {
        return DOCTYPE_WL.equals(documentType) || DOCTYPE_WLL.equals(documentType);
    }

    /**
     * Uses the values from the listdistribution.properties file.

     * @param docType String
     * @return boolean
     */
    public static boolean isListLetterDocType(String docType) {
        boolean returnValue = false;
        if (DOCTYPE_WLS.equals(docType) || DOCTYPE_FLS.equals(docType)
            || DOCTYPE_DLS.equals(docType) || DOCTYPE_ROS.equals(docType)) {
            returnValue = true;
        }
        return returnValue;
    }

    public static XhbBlobDao createXhbBlobDao(byte[] blobData) {
        XhbBlobDao dao = new XhbBlobDao();
        dao.setBlobData(blobData);
        dao.setCreationDate(LocalDateTime.now());
        dao.setLastUpdateDate(LocalDateTime.now());
        dao.setCreatedBy("PDDA");
        dao.setLastUpdatedBy("PDDA");
        return dao;
    }

    public static byte[] getEmptyByteArray() {
        return new byte[0];
    }

    public static ByteArrayOutputStream getByteArrayOutputStream() {
        return new ByteArrayOutputStream();
    }

    public static ByteArrayInputStream getByteArrayInputStream(String msg) {
        return new ByteArrayInputStream(msg.getBytes());
    }

    /**
     * Add amendments as part of 2787 web page redesign that the transform is not picking up (but
     * should!!).

     * @param inPage String
     * @return String
     */
    public static String amendGeneratedPage(String inPage) {
        String textToInsert =
            "\r\n<!--[if IE 7]>\r\n<link rel=\"stylesheet\" type=\"text/css\" href=\"https://"
                + "www.justice.gov.uk/css/ie7.css\" />\r\n" + "<![endif]-->\r\n<!--[if IE 6]>\r\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://www.justice.gov.uk/css/ie6.css"
                + "\" />\r\n<![endif]-->";

        StringBuilder amendedPage = new StringBuilder(inPage);
        int positionX = amendedPage.indexOf("print-listings.css");
        if (positionX >= 0) {
            char positionC = amendedPage.charAt(positionX + 19);
            char positionC1 = amendedPage.charAt(positionX + 20);
            if (positionC == '/' && positionC1 == '>') {
                amendedPage.insert(positionX + 21, textToInsert);
            }
        }

        return amendedPage.toString();
    }

    public static Map<String, String> createParameterMap() {
        // Create parameter map
        Map<String, String> parameterMap = new ConcurrentHashMap<>();
        synchronized (JAVADATEFORMAT) {
            parameterMap.put("java-date", JAVADATEFORMAT.format(new Date()));
            parameterMap.put("method", "xml");
            parameterMap.put("doctype-system", "https://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            parameterMap.put("doctype-public", "-//W3C//DTD XHTML 1.0 Strict//EN");
        }
        return parameterMap;
    }

    public static SAXParserFactory getSaxParserFactory()
        throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        spf.setNamespaceAware(true);
        return spf;
    }

    public static XMLReader createReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory spf = getSaxParserFactory();
        SAXParser parser = spf.newSAXParser();
        return parser.getXMLReader();
    }

    public static boolean isPddaOnly(List<XhbConfigPropDao> list) {
        return !list.isEmpty() && PDDA_ONLY.equals(list.get(0).getPropertyValue());
    }

    /**
     * Get the time minus any delays.

     * @return LocalDateTime Time minus any delay
     */
    public static LocalDateTime getTimeDelay(List<XhbConfigPropDao> configs) {
        LocalDateTime now = LocalDateTime.now();
        if (!configs.isEmpty()) {
            XhbConfigPropDao config = configs.get(0);
            Integer delay = Integer.valueOf(config.getPropertyValue());
            LOG.debug("getTimeDelay() - Delay seconds = " + delay);
            if (delay != null && delay != 0) {
                now = now.minusSeconds(delay);
            }
        }
        return now;
    }

    public static boolean isProcessingList(FormattingValue formattingValue) {
        return !isListLetterDocType(formattingValue.getDocumentType())
            && (isDailyList(formattingValue.getDocumentType())
                || isWarnedList(formattingValue.getDocumentType())
                || isFirmList(formattingValue.getDocumentType()))
            && formattingValue.getXmlDocumentClobId() != null
            && formattingValue.getXmlDocumentClobId() != 0;
    }
}
