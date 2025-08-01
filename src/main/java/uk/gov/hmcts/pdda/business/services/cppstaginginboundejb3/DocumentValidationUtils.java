package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public final class DocumentValidationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentValidationUtils.class);

    private static final String METHOD_NAME_SUFFIX = ") - ";
    private static final String ENTERED = " : entered";
    private static final Integer DOCUMENT_PARTS = 3;
    private static final String DOCUMENT_NAME = "Document name ";
    private static final Integer DOCUMENT_TIME_LENGTH = 14;
    
    private DocumentValidationUtils() {
    }
    
    public static boolean validateDocumentSecond(String docTime, String documentName) {
        final int docTimeSecond = Integer.parseInt(docTime.substring(12, 14));
        if (docTimeSecond < 0 || docTimeSecond > 59) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid - invalid second.");
            return false;
        }
        return true;
    }
    
    public static boolean validateDocumentYear(String docTime, String documentName) {
        final int docTimeYear = Integer.parseInt(docTime.substring(0, 4));
        if (docTimeYear < 2020 || docTimeYear > 2099) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid - invalid year.");
            return false;
        }
        return true;
    }
    
    public static boolean validateDocumentMonth(String docTime, String documentName) {
        final int docTimeMonth = Integer.parseInt(docTime.substring(4, 6));
        if (docTimeMonth < 1 || docTimeMonth > 12) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid - invalid month.");
            return false;
        }
        return true;
    }
    
    public static boolean validateDocumentDay(String docTime, String documentName) {
        final int docTimeDay = Integer.parseInt(docTime.substring(6, 8));
        if (docTimeDay < 1 || docTimeDay > 31) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid - invalid day.");
            return false;
        }
        return true;
    }

    public static boolean validateDocumentHour(String docTime, String documentName) {
        final int docTimeHour = Integer.parseInt(docTime.substring(8, 10));
        if (docTimeHour < 0 || docTimeHour > 23) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid - invalid hour.");
            return false;
        }
        return true;
    }
    
    public static boolean validateDocumentMinute(String docTime, String documentName) {
        final int docTimeMinute = Integer.parseInt(docTime.substring(10, 12));
        if (docTimeMinute < 0 || docTimeMinute > 59) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid - invalid minute.");
            return false;
        }
        return true;
    }
    
    public static boolean isValidDocumentTime(String docTime, String documentName) {
        if (docTime.length() != DOCUMENT_TIME_LENGTH) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Time is invalid.");
            return false;
        }

        try {
            if (validateDocumentYear(docTime, documentName) 
                && validateDocumentMonth(docTime, documentName) 
                && validateDocumentDay(docTime, documentName)
                && validateDocumentHour(docTime, documentName)
                && validateDocumentMinute(docTime, documentName)
                && validateDocumentSecond(docTime, documentName)) {
                LOG.debug("Document Time is valid");
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            LOG.error("Cannot parse the time component of " + documentName);
            return false;
        }
        return true;
    }
    
    /**
     * The incoming CPP document name must be valid according to the following rules: 1. Daily List:
     * DailyList_CourtCode_SendDate.xml 2. Firm List: FirmList_CourtCode_SendDate.xml 3. Warned List:
     * WarnedList_CourtCode_SendDate.xml 4. Web Page: WebPage_CourtCode_SendDate.xml. Where: CourtCode
     * is a 3 digit number between 400 and 500 SendDate is a date of the format YYYYMMDDHH24MISS

     * @param documentName String

     */
    public static boolean isValidDocumentName(String documentName) {
        String methodName = "isValidDocumentName(" + documentName + METHOD_NAME_SUFFIX;
        LOG.debug(methodName + ENTERED);

        if (!documentName.endsWith(".xml")) {
            return false;
        }
        String s1 = documentName.substring(0, documentName.indexOf(".xml"));

        // Break into constituent parts
        String[] parts = s1.split("_");
        if (parts.length != DOCUMENT_PARTS) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid.");
            return false;
        }
        final String docType = parts[0];

        // Check name is valid
        CppDocumentNameInIncomingDocument[] dts = CppDocumentNameInIncomingDocument.values();
        boolean validDocType = false;
        for (CppDocumentNameInIncomingDocument dt : dts) {
            if (dt.getLabel().equals(docType.toUpperCase(Locale.getDefault()))) {
                validDocType = true;
                break;
            }
        }
        if (!validDocType) {
            LOG.error(DOCUMENT_NAME + documentName + " is invalid. Unknown document type in title.");
            return false;
        }

        // Check court code
        final String courtCode = parts[1];
        try {
            int courtId = Integer.parseInt(courtCode);
            if (courtId < 400 || courtId > 499) {
                LOG.error(DOCUMENT_NAME + documentName + " is invalid. Invalid court name in title.");
                return false;
            }
        } catch (NumberFormatException nfe) {
            LOG.error("Cannot parse the time component of " + documentName);
            return false;
        }

        // Check time component
        final String docTime = parts[2];
        return isValidDocumentTime(docTime, documentName);
    }

    /**
     * The incoming CPP document type must be valid, i.e. WP, PD, DL, FL or WL

     * @param documentType String

     */
    public static boolean isValidDocumentType(String documentType) {
        String methodName = "isValidDocumentType(" + documentType + METHOD_NAME_SUFFIX;
        LOG.debug(methodName + ENTERED);

        boolean validDoc = false;
        CppDocumentTypes[] dts = CppDocumentTypes.values();
        for (CppDocumentTypes dt : dts) {
            if (dt.getLabel().equals(documentType)) {
                validDoc = true;
                break;
            }
        }

        return validDoc;
    }
}
