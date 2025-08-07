package uk.gov.hmcts.pdda.business.services.pdda;

/**
 * PddaSftpValidationUtil.
 **/
public final class PddaSftpValidationUtil {
    
    private static final String EXTENSION = ".xml";
    
    private PddaSftpValidationUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    public static boolean validateExtension(String filename) {
        int indexOfExtension = filename.lastIndexOf('.');

        if (indexOfExtension > 0) {
            String fileExtension = filename.substring(indexOfExtension);

            if (EXTENSION.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean validateTitle(String filenamePart, String... possibilities) {
        for (String possibility : possibilities) {
            if (possibility.equalsIgnoreCase(filenamePart)) {
                return true;
            }
        }
        return false;
    }
}
